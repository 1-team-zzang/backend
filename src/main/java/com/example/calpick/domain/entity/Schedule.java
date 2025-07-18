package com.example.calpick.domain.entity;

import com.example.calpick.domain.entity.enums.RepeatRule;
import com.example.calpick.domain.entity.enums.Visibility;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appointment_id")
    private Appointment appointment;

}
