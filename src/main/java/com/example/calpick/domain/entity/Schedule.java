package com.example.calpick.domain.entity;

import com.example.calpick.domain.dto.schedule.request.ScheduleRequestDto;
import com.example.calpick.domain.entity.enums.ColorTypes;
import com.example.calpick.domain.entity.enums.RepeatRule;
import com.example.calpick.domain.entity.enums.RepeatType;
import com.example.calpick.global.exception.CalPickException;
import com.example.calpick.global.exception.ErrorCode;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import static com.example.calpick.domain.util.EnumUtil.fromString;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Entity
@Table
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long scheduleId;

    private String title;
    private String content;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private Boolean isRepeated;
    @Enumerated(EnumType.STRING)
    private RepeatRule repeatRule;
    private Boolean isVisible;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private Boolean isAllDay;
    @Enumerated(EnumType.STRING)
    private RepeatType repeatType;
    private Long repeatCount;
    private LocalDateTime repeatEndAt;
    @Enumerated(EnumType.STRING)
    private ColorTypes color;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appointment_id")
    private Appointment appointment;

    public static Schedule of(Appointment appointment, User user) {
        Schedule schedule = new Schedule();
        schedule.setTitle(appointment.getTitle());
        schedule.setStartAt(appointment.getStartAt());
        schedule.setEndAt(appointment.getEndAt());
        schedule.setCreatedAt(LocalDateTime.now());
        schedule.setIsRepeated(false);
        schedule.setIsVisible(true);
        schedule.setIsAllDay(appointment.getIsAllDay());
        schedule.setUser(user);
        schedule.setAppointment(appointment);
        schedule.setColor(ColorTypes.RED);
        return schedule;
    }

    public static Schedule of(ScheduleRequestDto request, User user) {
        Schedule schedule = new Schedule();
        schedule.setTitle(request.getTitle());
        schedule.setContent(request.getContent());
        schedule.setStartAt(request.getStartAt());
        schedule.setEndAt(request.getEndAt());
        schedule.setCreatedAt(LocalDateTime.now());
        schedule.setRepeatRule(fromString(RepeatRule.class, request.getRepeatRule()));
        schedule.setRepeatType(fromString(RepeatType.class, request.getRepeatType()));
        schedule.setIsRepeated(request.getIsRepeated());
        schedule.setIsVisible(request.getIsVisible());
        schedule.setIsAllDay(request.getIsAllDay());
        schedule.setUser(user);
        schedule.setAppointment(null);
        schedule.setRepeatCount(request.getRepeatCount());
        schedule.setRepeatEndAt(request.getRepeatEndAt());
        schedule.setColor(fromString(ColorTypes.class, request.getColor()));
        schedule.setModifiedAt(LocalDateTime.now());

        if (schedule.getIsRepeated()){
            if (schedule.getRepeatType()==RepeatType.COUNT){
                Long cnt = request.getRepeatCount()-1;
                LocalDateTime repeatEndAt = switch (schedule.getRepeatRule()) {
                    case DAILY -> request.getEndAt().plusDays(cnt);
                    case WEEKLY -> request.getEndAt().plusWeeks(cnt);
                    case MONTHLY -> request.getEndAt().plusMonths(cnt);
                    case YEARLY -> request.getEndAt().plusYears(cnt);
                    default -> throw new CalPickException(ErrorCode.INVALID_SCHEDULE_INPUT);
                };
                schedule.setRepeatEndAt(repeatEndAt);
            }else{
                long count =  switch (schedule.getRepeatRule()) {
                    case DAILY -> ChronoUnit.DAYS.between(request.getEndAt().toLocalDate(), request.getRepeatEndAt().toLocalDate())+1;
                    case WEEKLY -> ChronoUnit.WEEKS.between(request.getEndAt().toLocalDate(), request.getRepeatEndAt().toLocalDate())+1;
                    case MONTHLY -> ChronoUnit.MONTHS.between(request.getEndAt().toLocalDate().withDayOfMonth(1),
                            request.getRepeatEndAt().toLocalDate().withDayOfMonth(1)) + 1;
                    case YEARLY -> ChronoUnit.YEARS.between(request.getEndAt().toLocalDate().withDayOfYear(1),
                                request.getRepeatEndAt().toLocalDate().withDayOfYear(1)) + 1;
                    default -> throw new CalPickException(ErrorCode.INVALID_SCHEDULE_INPUT);
                };
                schedule.setRepeatCount(count);
            }
        }
        return schedule;
    }
}
