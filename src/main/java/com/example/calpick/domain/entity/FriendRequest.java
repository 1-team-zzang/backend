package com.example.calpick.domain.entity;

import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FriendRequest {
    @Id
    private Long friendRequestId;
    private Long requesterId;
    private Long receiverId;
    private String requestStatus;
    private LocalDateTime createdAt;
}
