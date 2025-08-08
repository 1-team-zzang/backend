package com.example.calpick.domain.service.impl;

import com.example.calpick.domain.dto.schedule.request.ScheduleRequestDto;
import com.example.calpick.domain.dto.schedule.response.CalenderResponseDto;
import com.example.calpick.domain.dto.schedule.response.ScheduleDetailResponseDto;
import com.example.calpick.domain.dto.schedule.response.ScheduleResponseDto;
import com.example.calpick.domain.dto.schedule.response.ScheduleShareDto;
import com.example.calpick.domain.dto.user.CustomUserDetails;
import com.example.calpick.domain.entity.Schedule;
import com.example.calpick.domain.entity.User;
import com.example.calpick.domain.entity.enums.ColorTypes;
import com.example.calpick.domain.entity.enums.RepeatRule;
import com.example.calpick.domain.entity.enums.RepeatType;
import com.example.calpick.domain.entity.enums.UserStatus;
import com.example.calpick.domain.repository.ScheduleRepository;
import com.example.calpick.domain.repository.UserRepository;
import com.example.calpick.domain.service.ScheduleService;
import com.example.calpick.global.exception.CalPickException;
import com.example.calpick.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import static com.example.calpick.domain.util.EnumUtil.fromString;

@Service
@RequiredArgsConstructor
public class ScheduleServiceImpl implements ScheduleService {
    private final ScheduleRepository scheduleRepository;
    private final UserRepository userRepository;
    private final ModelMapper mapper;

    @Override
    @Transactional
    public ScheduleResponseDto createSchedule(CustomUserDetails userDetails, ScheduleRequestDto request) {
        User user= userRepository.findByEmail(userDetails.getEmail()).orElseThrow(() -> new CalPickException(ErrorCode.INVALID_EMAIL));
        Schedule schedule = Schedule.of(request, user);
        if (schedule.getIsRepeated()) {
            if (schedule.getRepeatType() == RepeatType.COUNT)
                schedule.setRepeatEndAt(calculateRepeatEndAt(schedule.getRepeatRule(), schedule.getEndAt(), schedule.getRepeatCount()));
            else schedule.setRepeatCount(calculateRepeatCount(schedule.getRepeatRule(), schedule.getEndAt(), schedule.getRepeatEndAt()));
        }
        Schedule newSchedule = scheduleRepository.save(schedule);
        return mapper.map(newSchedule, ScheduleResponseDto.class);
    }

    @Override
    public ScheduleDetailResponseDto getSchedule(CustomUserDetails userDetails, Long scheduleId) {
        if( scheduleId == null ){
            throw new CalPickException(ErrorCode.INVALID_SCHEDULE_INPUT);
        }
        Schedule schedule = scheduleRepository.findScheduleByScheduleId(scheduleId).orElseThrow(() -> new CalPickException(ErrorCode.SCHEDULE_NOT_FOUND));
        ScheduleDetailResponseDto responseDto = mapper.map(schedule,ScheduleDetailResponseDto.class);
        responseDto.setOwner( userDetails ==null? false : userDetails.getUserId().equals(schedule.getUser().getUserId()));
        return responseDto;
    }

    private List<ScheduleResponseDto> getScheduleList(Long userId, LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = startDate.atTime(0, 0);
        LocalDateTime endDateTime = endDate.atTime(23, 59);
        List<Schedule> scheduleList = scheduleRepository.getSchedulesByDateRange(
                userId, startDateTime, endDateTime);

        List<ScheduleResponseDto> dtoList = new ArrayList<>();

        for (Schedule schedule : scheduleList) {
            if (schedule.getIsRepeated()) {
                dtoList.addAll(generateRepeatedDtos(schedule, startDateTime, endDateTime));
            } else {
                dtoList.add(mapper.map(schedule, ScheduleResponseDto.class));
            }
        }

        return dtoList;
    }

    private List<ScheduleResponseDto> generateRepeatedDtos(Schedule schedule, LocalDateTime rangeStart, LocalDateTime rangeEnd) {
        List<ScheduleResponseDto> dtos = new ArrayList<>();

        LocalDateTime currentStart = schedule.getStartAt();
        LocalDateTime currentEnd = schedule.getEndAt();
        Long repeatCount = schedule.getRepeatCount();
        long count = 0;

        while (currentStart.isBefore(rangeEnd.plusMinutes(1)) && repeatCount != null && count < repeatCount) {
            // 현재 인스턴스가 범위 내에 있는 경우만 추가
            if (currentStart.isAfter(rangeStart.minusMinutes(1))) {
                ScheduleResponseDto dto = mapper.map(schedule, ScheduleResponseDto.class);
                dto.setStartAt(currentStart);
                dto.setEndAt(currentEnd);
                dtos.add(dto);
            }

            // 다음 반복 날짜 계산
            currentStart = switch (schedule.getRepeatRule()) {
                case DAILY -> currentStart.plusDays(1);
                case WEEKLY -> currentStart.plusWeeks(1);
                case MONTHLY -> currentStart.plusMonths(1);
                case YEARLY -> currentStart.plusYears(1);
            };
            currentEnd = switch (schedule.getRepeatRule()) {
                case DAILY -> currentEnd.plusDays(1);
                case WEEKLY -> currentEnd.plusWeeks(1);
                case MONTHLY -> currentEnd.plusMonths(1);
                case YEARLY -> currentEnd.plusYears(1);
            };
            count++;
        }

        return dtos;
    }

    @Override
    public CalenderResponseDto getOwnCalendar(CustomUserDetails userDetails, LocalDate startDate, LocalDate endDate){
        if (userDetails.getEmail() == null) throw new CalPickException(ErrorCode.UNAUTHORIZED_USER);
        User owner = userRepository.findByEmail(userDetails.getEmail()).orElseThrow(()->new CalPickException(ErrorCode.INVALID_EMAIL));
        CalenderResponseDto responseDto = new CalenderResponseDto();
        responseDto.setOwner(true);
        responseDto.setScheduleResponseList(getScheduleList(owner.getUserId(), startDate, endDate));
        return responseDto;
    }

    @Override
    public CalenderResponseDto getOtherCalendar(CustomUserDetails userDetails, Long calendarUserId, LocalDate startDate, LocalDate endDate) {
        User owner = userRepository.findById(calendarUserId).orElseThrow(()->new CalPickException(ErrorCode.SCHEDULE_NOT_FOUND));
        if (owner.getUserStatus() != UserStatus.ACTIVE) throw new CalPickException(ErrorCode.SCHEDULE_NOT_FOUND);

        CalenderResponseDto responseDto = new CalenderResponseDto();
        if (userDetails == null || !userDetails.getEmail().equals(owner.getEmail())) responseDto.setOwner(false);
        else responseDto.setOwner(true);
        responseDto.setScheduleResponseList(getScheduleList(owner.getUserId(), startDate, endDate));
        return responseDto;
    }

    @Override
    @Transactional
    public ScheduleResponseDto updateSchedule(CustomUserDetails userDetails, Long scheduleId, ScheduleRequestDto request) {
        if( scheduleId == null ) throw new CalPickException(ErrorCode.INVALID_SCHEDULE_INPUT);
        Schedule schedule = scheduleRepository.findScheduleByScheduleId(scheduleId).orElseThrow(() -> new CalPickException(ErrorCode.SCHEDULE_NOT_FOUND));
        // 작성자만 수정 가능
        if (!userDetails.getEmail().equals(schedule.getUser().getEmail())) throw new CalPickException(ErrorCode.NO_ACCESS_TO_SCHEDULE);
        schedule.setTitle(request.getTitle());
        schedule.setContent(request.getContent());
        schedule.setStartAt(request.getStartAt());
        schedule.setEndAt(request.getEndAt());
        schedule.setIsRepeated(request.getIsRepeated());
        schedule.setRepeatRule(fromString(RepeatRule.class, request.getRepeatRule()));
        schedule.setIsVisible(request.getIsVisible());
        schedule.setModifiedAt(LocalDateTime.now()); // 현재 시간으로 수정
        schedule.setIsAllDay(request.getIsAllDay());
        schedule.setRepeatType(fromString(RepeatType.class, request.getRepeatType()));
        schedule.setRepeatCount(request.getRepeatCount());
        schedule.setRepeatEndAt(request.getRepeatEndAt());
        schedule.setColor(fromString(ColorTypes.class, request.getColor()));

        if (schedule.getIsRepeated()) {
            if (schedule.getRepeatType() == RepeatType.COUNT)
                schedule.setRepeatEndAt(calculateRepeatEndAt(schedule.getRepeatRule(), schedule.getEndAt(), schedule.getRepeatCount()));
            else schedule.setRepeatCount(calculateRepeatCount(schedule.getRepeatRule(), schedule.getEndAt(), schedule.getRepeatEndAt()));
        }

        return mapper.map(schedule, ScheduleResponseDto.class);
    }

    @Override
    @Transactional
    public void deleteSchedule(CustomUserDetails userDetails, Long scheduleId) {
        if( scheduleId == null ) throw new CalPickException(ErrorCode.INVALID_SCHEDULE_INPUT);
        Schedule schedule = scheduleRepository.findScheduleByScheduleId(scheduleId).orElseThrow(() -> new CalPickException(ErrorCode.SCHEDULE_NOT_FOUND));
        // 작성자만 삭제 가능
        if (!userDetails.getEmail().equals(schedule.getUser().getEmail())) throw new CalPickException(ErrorCode.NO_ACCESS_TO_SCHEDULE);
        scheduleRepository.delete(schedule);
    }

    @Override
    public ScheduleShareDto shareMyCalendar(CustomUserDetails userDetails) {
        if (userDetails.getEmail() == null) throw new CalPickException(ErrorCode.UNAUTHORIZED_USER);
        User user = userRepository.findByEmail(userDetails.getEmail()).orElseThrow(()->new CalPickException(ErrorCode.INVALID_EMAIL));
        return new ScheduleShareDto(user.getUserId());
    }

    // 반복 계산
    private LocalDateTime calculateRepeatEndAt(RepeatRule repeatRule,LocalDateTime endAt, Long repeatCount){
        Long cnt = repeatCount-1;
        LocalDateTime repeatEndAt = switch (repeatRule) {
            case DAILY -> endAt.plusDays(cnt);
            case WEEKLY -> endAt.plusWeeks(cnt);
            case MONTHLY -> endAt.plusMonths(cnt);
            case YEARLY -> endAt.plusYears(cnt);
            default -> throw new CalPickException(ErrorCode.INVALID_SCHEDULE_INPUT);
        };
        return repeatEndAt;
    }
    private Long calculateRepeatCount(RepeatRule repeatRule, LocalDateTime endAt, LocalDateTime repeatEndAt){
        Long count =  switch (repeatRule) {
            case DAILY -> ChronoUnit.DAYS.between(endAt.toLocalDate(), repeatEndAt.toLocalDate())+1;
            case WEEKLY -> ChronoUnit.WEEKS.between(endAt.toLocalDate(), repeatEndAt.toLocalDate())+1;
            case MONTHLY -> ChronoUnit.MONTHS.between(endAt.toLocalDate().withDayOfMonth(1),
                    repeatEndAt.toLocalDate().withDayOfMonth(1)) + 1;
            case YEARLY -> ChronoUnit.YEARS.between(endAt.toLocalDate().withDayOfYear(1),
                    repeatEndAt.toLocalDate().withDayOfYear(1)) + 1;
            default -> throw new CalPickException(ErrorCode.INVALID_SCHEDULE_INPUT);
        };
        return count;
    }
}
