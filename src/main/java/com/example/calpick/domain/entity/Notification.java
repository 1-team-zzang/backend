package com.example.calpick.domain.entity;

import com.example.calpick.domain.entity.enums.NotificationEvent;
import com.example.calpick.domain.entity.enums.NotificationStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long notificationId;

    private String requesterName;
    private String requesterEmail;
    private String receiverName;
    private String receiverEmail;
    @Enumerated(EnumType.STRING)
    private NotificationEvent event;
    @Enumerated(EnumType.STRING)
    private NotificationStatus notificationStatus;
    private LocalDateTime createdAt;




}
