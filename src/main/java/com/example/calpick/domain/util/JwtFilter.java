package com.example.calpick.domain.util;

import com.example.calpick.domain.dto.user.CustomUserDetails;
import com.example.calpick.domain.entity.User;
import com.example.calpick.global.exception.CalPickException;
import com.example.calpick.global.exception.ErrorCode;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response); // OPTIONS는 필터 타지 않게
            return;
        }

        String authorization = request.getHeader("Authorization");
        // 헤더 검증
        if (authorization == null || !authorization.startsWith("Bearer ")){
            filterChain.doFilter(request, response);
            return;
        }

        String accessToken = authorization.split(" ")[1];

        // 토큰 만료 검증
        if (jwtUtil.isExpired(accessToken)){
            logger.info("Access Token expired");
            accessToken = refresh(request);
            response.setHeader("Authorization", "Bearer "+accessToken);
        }

        String name = jwtUtil.getName(accessToken);
        String email = jwtUtil.getEmail(accessToken);

        CustomUserDetails customUserDetails = new CustomUserDetails(
                User.builder().name(name).email(email).build());
        // Spring Security 인증 토큰 생성
        Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
        // 세션에 사용자 등록
        SecurityContextHolder.getContext().setAuthentication(authToken);
        filterChain.doFilter(request,response);
    }

    // Refresh Token을 통해 Access Token 재발급
    public String refresh(HttpServletRequest request) {
        String refreshToken = extractRefreshTokenFromCookie(request);

        if (refreshToken == null || jwtUtil.isExpired(refreshToken)) {
            throw new CalPickException(ErrorCode.INVALID_JWT_TOKEN); // refresh token 만료 혹은 없음
        }

        // 헤더에서 이전 access token 꺼내기
        String accessHeader = request.getHeader("Authorization");
        if (accessHeader == null || !accessHeader.startsWith("Bearer ")) {
            throw new CalPickException(ErrorCode.INVALID_JWT_TOKEN); // access token 없음
        }
        String expiredAccessToken = accessHeader.split(" ")[1];

        // accessToken과 refreshToken에서 각각 email 비교
        String accessEmail = jwtUtil.getEmail(expiredAccessToken);
        String refreshEmail = jwtUtil.getEmail(refreshToken);

        if (!accessEmail.equals(refreshEmail)) {
            throw new CalPickException(ErrorCode.INVALID_JWT_TOKEN); // 둘이 다른 사용자임
        }

        // 모든 검증 통과 시 새 accessToken 발급
        return jwtUtil.createAccessToken(
                jwtUtil.getUserId(expiredAccessToken),
                refreshEmail,
                jwtUtil.getName(refreshToken)
        );
    }

    private String extractRefreshTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() == null) return null;

        for (Cookie cookie : request.getCookies()) {
            if (cookie.getName().equals("Refresh-Token")) {
                return cookie.getValue();
            }
        }
        return null;
    }
}
