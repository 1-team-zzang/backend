package com.example.calpick.domain.entity;

import com.example.calpick.domain.entity.enums.NotificationEvent;
import com.example.calpick.domain.entity.enums.NotificationStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table
@Getter
@Setter
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long notificationId;

    private String content;

    private String requesterName;
    private String requesterEmail;
    private String receiverName;
    private String receiverEmail;
    @Enumerated(EnumType.STRING)
    private NotificationEvent event;
    @Enumerated(EnumType.STRING)
    private NotificationStatus notificationStatus;
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_id")
    private User requester;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id")
    private User receiver;

    private LocalDateTime expiredAt;

    public static Notification of(Appointment appointment, NotificationEvent event,String content) {
        Notification notification = new Notification();

        if (appointment.getRequester() != null) {
            notification.setRequester(appointment.getRequester());
        } else {
            notification.setRequesterEmail(appointment.getRequesterEmail());
        }
        notification.setContent(content);
        notification.setRequesterName(appointment.getRequesterName());
        notification.setReceiver(appointment.getReceiver());
        notification.setEvent(event);
        notification.setCreatedAt(LocalDateTime.now());
        notification.setNotificationStatus(NotificationStatus.PENDING);
        notification.setExpiredAt(LocalDateTime.now().plusDays(30));

        return notification;
    }

    public static Notification of(User user,User friend, NotificationEvent event,String content) {
        Notification notification = new Notification();

        notification.setRequester(user);
        notification.setContent(content);
        notification.setRequesterName(user.getName());
        notification.setReceiver(friend);
        notification.setEvent(event);
        notification.setCreatedAt(LocalDateTime.now());
        notification.setNotificationStatus(NotificationStatus.PENDING);
        notification.setExpiredAt(LocalDateTime.now().plusDays(30));

        return notification;
    }
}
