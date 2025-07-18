package com.example.calpick.domain.config;

import com.example.calpick.domain.service.impl.UserDetailsServiceImpl;
import com.example.calpick.domain.util.JwtAuthenticationFilter;
import com.example.calpick.domain.util.JwtFilter;
import com.example.calpick.domain.util.JwtUtil;
import com.example.calpick.global.exception.CalPickException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserDetailsServiceImpl userDetailsServiceImpl;
    private final ObjectMapper objectMapper;
    private final JwtUtil jwtUtil;

    @Bean
    public BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }


    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder builder = http.getSharedObject(AuthenticationManagerBuilder.class);
        builder.userDetailsService(userDetailsServiceImpl).passwordEncoder(passwordEncoder());
        return builder.build();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{

        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(
                authenticationManager(http), objectMapper, jwtUtil
        );
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/signup","/api/auth/login","/","/swagger-ui/**","/v3/api-docs/**").permitAll()
                        .anyRequest().authenticated()
                )
                .authenticationManager(authenticationManager(http))
                .addFilterBefore(new JwtFilter(jwtUtil), JwtAuthenticationFilter.class)
                .addFilterAt(jwtAuthenticationFilter,  UsernamePasswordAuthenticationFilter.class)
                // ----------------------- 예외 처리 -----------------------
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) -> {
                            Throwable cause = authException.getCause();
                            if (cause instanceof CalPickException) {
                                CalPickException cpe = (CalPickException) cause;
                                // ErrorCode에서 HTTP 상태, 메시지 가져오기
                                String message = cpe.getErrorCode().getMessage();
                                response.setStatus(cpe.getErrorCode().getStatus().value());
                                response.setContentType("application/json");
                                response.getWriter().write(
                                        String.format("{\"errorCode\":\"%s\",\"message\":\"%s\"}",
                                                cpe.getErrorCode().name(), message)
                                );
                            } else {
                                // 기본 인증 에러
                                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, authException.getMessage());
                            }
                        })
                );
        return http.build();
    }
}
