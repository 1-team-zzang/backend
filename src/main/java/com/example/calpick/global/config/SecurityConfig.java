package com.example.calpick.global.config;

import com.example.calpick.domain.service.impl.UserDetailsServiceImpl;
import com.example.calpick.domain.util.CustomAuthenticationEntryPoint;
import com.example.calpick.domain.util.JwtAuthenticationFilter;
import com.example.calpick.domain.util.JwtFilter;
import com.example.calpick.domain.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.RegexRequestMatcher;

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
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/api/auth/signup","/api/auth/login","/api/auth/kakao/signup","/","/swagger-ui/**","/v3/api-docs/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/appointments/requests").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/schedules/user/**").permitAll()
                        .requestMatchers(RegexRequestMatcher.regexMatcher(HttpMethod.GET, "/api/schedules/\\d+")).permitAll()
                        .anyRequest().authenticated()
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(new CustomAuthenticationEntryPoint())
                )
                .authenticationManager(authenticationManager(http))
                .addFilterBefore(new JwtFilter(jwtUtil), JwtAuthenticationFilter.class)
                .addFilterAt(jwtAuthenticationFilter,  UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
