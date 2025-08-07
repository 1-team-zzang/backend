package com.example.calpick.domain.repository;

import com.example.calpick.domain.entity.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface NotificationTypeRepository extends JpaRepository<NotificationType,Long> {

    @Modifying
    @Transactional
    @Query("""
        DELETE FROM NotificationType nt 
        WHERE nt.notification.notificationId IN (
            SELECT n.notificationId FROM Notification n WHERE n.expiredAt IS NOT NULL AND n.expiredAt < CURRENT_TIMESTAMP
        )
    """)
    int deleteByExpiredNotification();
}
