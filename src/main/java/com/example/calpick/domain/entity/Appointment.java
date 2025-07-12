package com.example.calpick.domain.entity;

import com.example.calpick.domain.entity.enums.AppointmentStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long appointmentId;

    private String title;
    private String content;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private String requesterName;
    private String requesterEmail;
    @Enumerated(EnumType.STRING)
    private AppointmentStatus appointmentStatus;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private Boolean isAllDay;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_id")
    private User requester;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id")
    private User receiver;

}
