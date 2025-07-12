package com.example.calpick.domain.entity;

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
    private Long userId;
    private String shareToken;
    private LocalDateTime visible_start_date;
    private LocalDateTime visible_end_date;
    private LocalDateTime expires_at;
    private LocalDateTime created_at;
    private String link_status;
}
