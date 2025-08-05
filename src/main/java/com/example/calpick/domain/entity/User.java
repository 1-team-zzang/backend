package com.example.calpick.domain.entity;
import com.example.calpick.domain.entity.enums.LoginType;
import com.example.calpick.domain.entity.enums.UserStatus;
import com.example.calpick.domain.util.LoginTypeSetConverter;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;
    private String email;
    private String password; // 일반 로그인 유저용
    private String name;
    private String profileUrl;
    private String shareToken;
    private String idToken; // 카카오 로그인에서 받은 ID 토큰

    @Enumerated(EnumType.STRING)
    private UserStatus userStatus;

    @Column(columnDefinition = "SET('NORMAL','KAKAO','GOOGLE')")
    @Convert(converter = LoginTypeSetConverter.class)
    @Builder.Default
    private Set<LoginType> loginTypes = new HashSet<>();

    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private LocalDateTime deletedAt;
}
