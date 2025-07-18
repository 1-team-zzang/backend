package com.example.calpick.domain.service.impl;

import com.example.calpick.domain.dto.auth.request.SignupRequest;
import com.example.calpick.domain.dto.auth.response.SignupResponse;
import com.example.calpick.domain.entity.User;
import com.example.calpick.domain.entity.enums.LoginType;
import com.example.calpick.domain.entity.enums.UserStatus;
import com.example.calpick.domain.repository.UserRepository;
import com.example.calpick.domain.service.AuthService;
import com.example.calpick.global.exception.CalPickException;
import com.example.calpick.global.exception.ErrorCode;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ModelMapper mapper;

    @Override
    public void logout(String email, HttpServletResponse response) {
        ResponseCookie deleteCookie = ResponseCookie.from("Refresh-Token", "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0) // 삭제
                .sameSite("None")
                .build();

        response.setHeader("Set-Cookie", deleteCookie.toString());
    }

    @Override
    public SignupResponse signUp(SignupRequest dto) {
        String email = dto.getEmail();
        if (userRepository.existsByEmail(email)==1){
            throw new CalPickException(ErrorCode.DUPLICATED_EMAIL);
        }
        // 이메일 형식, 비밀번호, 필수 입력값 검증 Exception 추가

        LocalDateTime now = LocalDateTime.now();
        User user = User.builder()
                .email(dto.getEmail())
                .password(bCryptPasswordEncoder.encode(dto.getPassword()))
                .name(dto.getName())
                .userStatus(UserStatus.ACTIVE)
                .loginType(LoginType.NORMAL)
                .createdAt(now)
                .modifiedAt(now)
                .deletedAt(null)
                .build();
        User newUser = userRepository.save(user);
        return mapper.map(newUser, SignupResponse.class);
    }


    @Override
    public void withdraw(String email, HttpServletResponse response) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CalPickException(ErrorCode.INVALID_EMAIL));

        // SOFT DELETE
        user.setDeletedAt(LocalDateTime.now());
        user.setUserStatus(UserStatus.DELETED);
        userRepository.save(user);

        ResponseCookie deleteCookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .sameSite("None")
                .build();

        response.setHeader("Set-Cookie", deleteCookie.toString());
    }
}
