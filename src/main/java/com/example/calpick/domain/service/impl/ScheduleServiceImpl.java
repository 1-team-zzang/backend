package com.example.calpick.domain.service.impl;

import com.example.calpick.domain.dto.schedule.request.ScheduleRequestDto;
import com.example.calpick.domain.dto.schedule.response.ScheduleResponseDto;
import com.example.calpick.domain.dto.user.CustomUserDetails;
import com.example.calpick.domain.entity.Schedule;
import com.example.calpick.domain.entity.User;
import com.example.calpick.domain.entity.enums.ColorTypes;
import com.example.calpick.domain.entity.enums.RepeatRule;
import com.example.calpick.domain.entity.enums.RepeatType;
import com.example.calpick.domain.repository.ScheduleRepository;
import com.example.calpick.domain.repository.UserRepository;
import com.example.calpick.domain.service.ScheduleService;
import com.example.calpick.global.exception.CalPickException;
import com.example.calpick.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
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
        Schedule newSchedule = scheduleRepository.save(Schedule.of(request, user));
        return mapper.map(newSchedule, ScheduleResponseDto.class);
    }

    @Override
    public ScheduleResponseDto getSchedule(CustomUserDetails userDetails, Long scheduleId) {
        // isVisible 따라 조회 범위 다름
        if( scheduleId == null ){
            throw new CalPickException(ErrorCode.INVALID_SCHEDULE_INPUT);
        }
        Schedule schedule = scheduleRepository.findScheduleByScheduleId(scheduleId).orElseThrow(() -> new CalPickException(ErrorCode.SCHEDULE_NOT_FOUND));
        // isVisible = false고 작성자가 아닌 경우 조회 불가
        if (!schedule.getIsVisible() && !userDetails.getEmail().equals(schedule.getUser().getEmail())) throw new CalPickException(ErrorCode.NO_ACCESS_TO_SCHEDULE);
        return mapper.map(schedule,ScheduleResponseDto.class);
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
        return mapper.map(schedule, ScheduleResponseDto.class);
    }
}
