package com.example.calpick.domain.dto.response.notification;

import java.time.LocalDateTime;

public interface NotificationProjection {
        String getContent();
        String getEvent();
        LocalDateTime getCreatedAt();
        Long getRequesterId();
        Long getReceiverId();
        String getRequesterName();
        String getReceiverName();
        String getType();
}
