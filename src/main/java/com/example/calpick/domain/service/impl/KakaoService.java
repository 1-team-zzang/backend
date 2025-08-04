package com.example.calpick.domain.service.impl;

import com.example.calpick.domain.dto.auth.request.KakaoSignupRequest;
import com.example.calpick.domain.dto.auth.response.LoginResponse;
import com.example.calpick.domain.dto.user.UserDto;
import com.example.calpick.domain.entity.User;
import com.example.calpick.domain.entity.enums.LoginType;
import com.example.calpick.domain.entity.enums.UserStatus;
import com.example.calpick.domain.repository.UserRepository;
import com.example.calpick.domain.util.JwtUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class KakaoService{
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    public LoginResponse kakaoSignIn(KakaoSignupRequest signupRequest) {
        User kakaoUser = userRepository.findByUid(signupRequest.getIdToken())
                .orElseGet(()->kakaoSignUp(signupRequest));
        Long id = kakaoUser.getUserId();
        String email = kakaoUser.getEmail();
        String name = kakaoUser.getName();
        String accessToken = jwtUtil.createAccessToken(id, email, name);
        String refreshToken = jwtUtil.createRefreshToken("ROLE_USER", email);
        LoginType loginType = kakaoUser.getLoginType();
        String profileUrl = kakaoUser.getProfileUrl();

        return new LoginResponse(
                accessToken,
                refreshToken,
                new UserDto(id, name, email, loginType,profileUrl)
        );
    }

    @Transactional
    public User kakaoSignUp(KakaoSignupRequest request) {
        LocalDateTime now = LocalDateTime.now();
        User user = User.builder()
                .email(request.getEmail())
                .password(request.getIdToken())
                .name(request.getName())
                .userStatus(UserStatus.ACTIVE)
                .loginType(LoginType.KAKAO)
                .createdAt(now)
                .modifiedAt(now)
                .build();
        User newUser = userRepository.save(user);
        return newUser;
    }
}
