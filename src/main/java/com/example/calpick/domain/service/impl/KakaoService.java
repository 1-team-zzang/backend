package com.example.calpick.domain.service.impl;

import com.example.calpick.domain.dto.auth.request.KakaoCodeRequest;
import com.example.calpick.domain.dto.auth.response.KakaoTokenResponse;
import com.example.calpick.domain.dto.auth.response.KakaoUserInfoResponse;
import com.example.calpick.domain.dto.auth.response.LoginResponse;
import com.example.calpick.domain.dto.user.UserDto;
import com.example.calpick.domain.entity.User;
import com.example.calpick.domain.entity.enums.LoginType;
import com.example.calpick.domain.entity.enums.UserStatus;
import com.example.calpick.domain.repository.UserRepository;
import com.example.calpick.domain.util.JwtUtil;
import com.example.calpick.global.exception.CalPickException;
import com.example.calpick.global.exception.ErrorCode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class KakaoService{
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final WebClient webClient;

    @Value("${kakao.client.id}")
    private String kakaoClientId;
    @Value("${kakao.client.secret}")
    private String kakaoClientSecret;
    @Value("${kakao.redirect.develop}")
    private String redirectUrl;

    // 토큰 -> 사용자정보 -> 저장 및 로그인 반환
    public Mono<LoginResponse> kakaoAuthorize(KakaoCodeRequest request) {
        return getKakaoToken(request.getCode()) // 1. 인증 코드로 토큰 발급
                .flatMap(tokenResp -> {
                    String accessToken = tokenResp.getAccessToken();
                    if (accessToken==null){
                        return Mono.error(new CalPickException(ErrorCode.KAKAO_TOKEN_REQUEST_FAILED, "Kakao AccessToken이 없습니다."));
                    }
                    // 2. accessToken으로 사용자 정보 요청
                    return getKakaoUserInfo(accessToken)
                        .flatMap(userInfo->
                            Mono.fromCallable(()->kakaoSignIn(userInfo))
                        );
                });
    }

    // 인증 코드로 토큰 요청 받기
    private Mono<KakaoTokenResponse> getKakaoToken(String code) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "authorization_code");
        formData.add("client_id", kakaoClientId);
        formData.add("redirect_uri", redirectUrl);
        formData.add("code", code);
        formData.add("client_secret", kakaoClientSecret);

        return webClient.post()
                .uri("https://kauth.kakao.com/oauth/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue(formData)
                .retrieve()
                .bodyToMono(KakaoTokenResponse.class)
                .onErrorMap(e ->
                        new CalPickException(ErrorCode.KAKAO_TOKEN_REQUEST_FAILED, "카카오 토큰 요청이 실패하였습니다."+e)
                );
    }

    // 토큰으로 사용자 정보 요청받기
    private Mono<KakaoUserInfoResponse> getKakaoUserInfo(String accessToken) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        // 요청할 property_keys를 JSON 배열 문자열로 추가합니다.
        formData.add("property_keys", "[\"kakao_account.email\", \"kakao_account.profile\"]");
        return webClient.post()
                .uri("https://kapi.kakao.com/v2/user/me")
                .headers(h -> h.setBearerAuth(accessToken))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue(formData) // property_keys
                .retrieve()
                .bodyToMono(KakaoUserInfoResponse.class)
                .onErrorMap(e ->
                        new CalPickException(ErrorCode.KAKAO_TOKEN_REQUEST_FAILED, "카카오 사용자 정보 조회에 실패하였습니다."+e)
                );
    }

    public LoginResponse kakaoLogin(User user){
        Long id = user.getUserId();
        String email = user.getEmail();
        String name = user.getName();
        String accessToken = jwtUtil.createAccessToken(id, email, name);
        String refreshToken = jwtUtil.createRefreshToken("ROLE_USER", email);

        return new LoginResponse(
                accessToken,
                refreshToken,
                new UserDto(id, name, email, user.getLoginTypes(), user.getProfileUrl())
        );
    }

    @Transactional
    public LoginResponse kakaoSignIn(KakaoUserInfoResponse infoResponse) {
        // 이메일로 유저를 찾음 -> 없으면 입력받은 것으로 생성
        User user = userRepository.findByEmail(infoResponse.getEmail())
                .orElseGet(()->createNewUser(infoResponse));
        if (user.getKakaoId() == null){
            // 카카오 로그인 한 적 없는 사람
            user.setKakaoId(infoResponse.getKakaoId());
            user.getLoginTypes().add(LoginType.KAKAO);
            user.setModifiedAt(LocalDateTime.now());
            user = userRepository.save(user);
        }
        return kakaoLogin(user);
    }

    public User createNewUser(KakaoUserInfoResponse request){
        LocalDateTime now = LocalDateTime.now();
        return User.builder()
                .email(request.getEmail())
                .name(request.getKakaoAccount().getProfile().getName())
                .userStatus(UserStatus.ACTIVE)
                .createdAt(now)
                .modifiedAt(now)
                .deletedAt(null)
                .build();
    }
}
