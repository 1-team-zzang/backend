package com.example.calpick.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
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

    public static NotificationType of(com.example.calpick.domain.entity.enums.NotificationType type, Long id , Notification notification) {
        NotificationType notificationType = new NotificationType();
        notificationType.setType(type);
        notificationType.setReferenceId(id);
        notificationType.setNotification(notification);
        return notificationType;
    }
}
