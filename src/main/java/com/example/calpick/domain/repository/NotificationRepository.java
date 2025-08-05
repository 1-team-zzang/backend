package com.example.calpick.domain.repository;

import com.example.calpick.domain.dto.response.notification.NotificationProjection;
import com.example.calpick.domain.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification,Long> {
    @Query(value = """
    SELECT 
        n.content AS content,
        n.event AS event,
        n.created_at AS createdAt,
        n.requester_id AS requesterId,
        n.receiver_id AS receiverId,
        requester.name AS requesterName,
        receiver.name AS receiverName,
        nt.type AS type
    FROM notification n
    JOIN notification_type nt ON nt.notification_id = n.notification_id
    JOIN users requester ON requester.user_id = n.requester_id
    JOIN users receiver ON receiver.user_id = n.receiver_id
    WHERE 
        (n.event = 'REQUEST' AND receiver.user_id = :userId)
        OR
        (n.event = 'REJECT' AND requester.user_id = :userId)
        OR
        (n.event = 'ACCEPT' AND (requester.user_id = :userId OR receiver.user_id = :userId))
    ORDER BY n.created_at DESC
    """,
            countQuery = """
    SELECT COUNT(*)
    FROM notification n
    JOIN notification_type nt ON nt.notification_id = n.notification_id
    JOIN users requester ON requester.user_id = n.requester_id
    JOIN users receiver ON receiver.user_id = n.receiver_id
    WHERE 
        (n.event = 'REQUEST' AND receiver.user_id = :userId)
        OR
        (n.event = 'REJECT' AND requester.user_id = :userId)
        OR
        (n.event = 'ACCEPT' AND (requester.user_id = :userId OR receiver.user_id = :userId))
    """,
            nativeQuery = true)
    Page<NotificationProjection> findRelevantNotifications(@Param("userId") Long userId, Pageable pageable);


    @Modifying
    @Transactional
    @Query("DELETE FROM Notification n WHERE n.expiredAt IS NOT NULL AND n.expiredAt < CURRENT_TIMESTAMP")
    int deleteExpired();



}
