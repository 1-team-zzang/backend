package com.example.calpick.domain.entity;

import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Friendship {
    @Id
    private Long friendshipId;
    private Long userId;
    private Long friendId;
    private LocalDateTime created_at;
}
