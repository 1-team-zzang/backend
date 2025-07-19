package com.example.calpick.domain.repository;

import com.example.calpick.domain.entity.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationTypeRepository extends JpaRepository<NotificationType,Long> {
}
