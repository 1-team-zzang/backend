package com.example.calpick.domain.entity;

import com.example.calpick.domain.entity.enums.AppointmentStatus;
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
