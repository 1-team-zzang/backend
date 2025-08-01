package com.example.calpick.domain.entity;

import com.example.calpick.domain.dto.schedule.request.ScheduleRequestDto;
import com.example.calpick.domain.entity.enums.ColorTypes;
import com.example.calpick.domain.entity.enums.RepeatRule;
import com.example.calpick.domain.entity.enums.RepeatType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import static com.example.calpick.domain.util.EnumUtil.fromString;
import java.time.LocalDateTime;

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
        schedule.setColor(appointment.getColor());
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
        return schedule;
    }
}
