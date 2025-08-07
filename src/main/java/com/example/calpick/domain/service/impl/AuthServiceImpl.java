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
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Set;

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
    @Transactional
    public SignupResponse signUp(SignupRequest dto) {
        String email = dto.getEmail();
        User user = userRepository.findByEmail(email).orElseGet(
                ()->createNewUser(dto));
        Set<LoginType> loginTypes = user.getLoginTypes();
        if (loginTypes.contains(LoginType.NORMAL)){
            throw new CalPickException(ErrorCode.DUPLICATED_EMAIL);
        }
        if(loginTypes.contains(LoginType.KAKAO)){
            user.setPassword(bCryptPasswordEncoder.encode(dto.getPassword()));
        }
        user.getLoginTypes().add(LoginType.NORMAL);
        User savedUser = userRepository.save(user);
        return mapper.map(savedUser, SignupResponse.class);
    }

    @Transactional
    public User createNewUser(SignupRequest dto){
        LocalDateTime now = LocalDateTime.now();
        return User.builder()
                .email(dto.getEmail())
                .password(bCryptPasswordEncoder.encode(dto.getPassword()))
                .name(dto.getName())
                .profileUrl(dto.getProfileUrl())
                .userStatus(UserStatus.ACTIVE)
                .createdAt(now)
                .modifiedAt(now)
                .deletedAt(null)
                .build();
    }

    @Override
    @Transactional
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
