package com.example.calpick.domain.util;

import com.example.calpick.domain.dto.auth.request.LoginRequest;
import com.example.calpick.domain.dto.response.Response;
import com.example.calpick.domain.dto.user.CustomUserDetails;
import com.example.calpick.domain.dto.user.UserDto;
import com.example.calpick.global.exception.CalPickException;
import com.example.calpick.global.exception.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

@Slf4j(topic = "로그인 및 JWT 생성")
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;
    private final ObjectMapper objectMapper;
    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager,
                                   ObjectMapper objectMapper,
                                   JwtUtil jwtUtil){
        this.authenticationManager = authenticationManager;
        this.objectMapper = objectMapper;
        this.jwtUtil = jwtUtil;
        setFilterProcessesUrl("/api/auth/login");
    }

    // 로그인 시, email & password 기반으로 토큰 발급
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try{
            LoginRequest loginRequest = objectMapper.readValue(request.getInputStream(), LoginRequest.class);
            // token으로
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    loginRequest.getEmail(), loginRequest.getPassword(), null
            );

            //authManager에서 검증 진행
            return authenticationManager.authenticate(authToken);
        }catch (IOException e){
            // 로그인 요청 파싱 실패
            throw new CalPickException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    // 로그인 성공시 실행되는 메소드 - JWT 발급
    @Override
    protected void successfulAuthentication(HttpServletRequest request,  HttpServletResponse response, FilterChain chain, Authentication authentication){
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        Long id = customUserDetails.getUserId();;
        String email = customUserDetails.getEmail();
        String name = customUserDetails.getUsername();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String role = auth.getAuthority();

        String accessToken = jwtUtil.createAccessToken(id, email, name);
        String refreshToken = jwtUtil.createRefreshToken(role, email);
        ResponseCookie cookie = ResponseCookie.from("Refresh-Token", refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/") // 모든 경로에 쿠키 전송
                .sameSite("None") // CORS 허용
                .maxAge(60 * 60 * 24 * 7) // 7일
                .build();


        response.addHeader("Authorization", "Bearer "+accessToken);
        response.setHeader("Set-Cookie", cookie.toString());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Response<UserDto> successResponse = Response.success(new UserDto(id, name, email));

        try{
            objectMapper.writeValue(response.getWriter(), successResponse);
        }catch (IOException e){ throw new CalPickException(ErrorCode.INTERNAL_SERVER_ERROR);}
    }

    // 로그인 실패 시 실행되는 메소드
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed){
        throw new CalPickException(ErrorCode.INVALID_PASSWORD);
    }

}
