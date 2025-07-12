package com.example.calpick.domain.entity;

import com.example.calpick.domain.entity.enums.LinkStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "share_link")
public class ShareLink {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long shareLinkId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String shareToken;
    private LocalDateTime visibleStartDate;
    private LocalDateTime visibleEndDate;
    private LocalDateTime expiresAt;
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    private LinkStatus linkStatus;
}
