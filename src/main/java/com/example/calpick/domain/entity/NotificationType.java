package com.example.calpick.domain.entity;

import jakarta.persistence.*;

@Entity
@Table
public class NotificationType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long notificationTypeId;

    @Enumerated(EnumType.STRING)
    private com.example.calpick.domain.entity.enums.NotificationType type;
    private Long referenceId;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notification_id")
    private Notification notification;
}
