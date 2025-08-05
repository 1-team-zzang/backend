package com.example.calpick.domain.scheduler;

import com.example.calpick.domain.repository.NotificationRepository;
import com.example.calpick.domain.repository.NotificationTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class NotificationScheduler {

    private final NotificationRepository notificationRepository;
    private final NotificationTypeRepository notificationTypeRepository;


    @Scheduled(cron = "0 0 3 * * *") //매일 새벽 3시
    @Transactional
    public void deleteExpiredNotifications() {
        int deletedTypes = notificationTypeRepository.deleteByExpiredNotification();
        int deletedNotifications = notificationRepository.deleteExpired();

        System.out.println("삭제된 NotificationType: " + deletedTypes);
        System.out.println("삭제된 Notification: " + deletedNotifications);
    }


}
