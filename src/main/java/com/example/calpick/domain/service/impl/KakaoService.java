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
import java.util.Set;

@RequiredArgsConstructor
@Service
public class KakaoService{
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @Transactional
    public LoginResponse kakaoSignIn(KakaoSignupRequest signupRequest) {
        // 이메일로 유저를 찾음 -> 없으면 입력받은 것으로 생성
        User user = userRepository.findByEmail(signupRequest.getEmail())
                .orElseGet(()->createNewUser(signupRequest));
        Set<LoginType> loginTypes = user.getLoginTypes();
        if (!loginTypes.contains(LoginType.KAKAO)){ // KAKAO 없는 경우 회원가입/정보추가, 있는 경우 로그인
            if(user.getLoginTypes().contains(LoginType.NORMAL)) {
                user.setIdToken(signupRequest.getIdToken());
            }
            user.getLoginTypes().add(LoginType.KAKAO);
            user.setModifiedAt(LocalDateTime.now());
            user = userRepository.save(user);
        }

        Long id = user.getUserId();
        String email = user.getEmail();
        String name = user.getName();
        String accessToken = jwtUtil.createAccessToken(id, email, name);
        String refreshToken = jwtUtil.createRefreshToken("ROLE_USER", email);
        loginTypes = user.getLoginTypes();
        String profileUrl = user.getProfileUrl();

        return new LoginResponse(
                accessToken,
                refreshToken,
                new UserDto(id, name, email, loginTypes, profileUrl)
        );
    }

    public User createNewUser(KakaoSignupRequest request){
        LocalDateTime now = LocalDateTime.now();
        return User.builder()
                .email(request.getEmail())
                .idToken(request.getIdToken())
                .name(request.getName())
                .userStatus(UserStatus.ACTIVE)
                .createdAt(now)
                .modifiedAt(now)
                .deletedAt(null)
                .build();
    }
}
