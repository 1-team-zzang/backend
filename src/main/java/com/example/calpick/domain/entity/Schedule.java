package com.example.calpick.domain.entity;

import com.example.calpick.domain.entity.enums.RepeatRule;
import com.example.calpick.domain.entity.enums.Visibility;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    @Enumerated(EnumType.STRING)
    private Visibility visibility;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private Boolean isAllDay;

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
        schedule.setVisibility(Visibility.PUBLIC);
        schedule.setIsAllDay(appointment.getIsAllDay());
        schedule.setUser(user);
        schedule.setAppointment(appointment);
        return schedule;
    }

}
