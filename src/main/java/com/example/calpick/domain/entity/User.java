package com.example.calpick.domain.entity;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {
    @Id
    private Long userId;
    private String email;
    private String password;
    private String name;
    private String profile_url;
    private String user_status;
    private String login_type;
    private LocalDateTime created_at;
    private LocalDateTime modified_at;
    private LocalDateTime deleted_at;
}
