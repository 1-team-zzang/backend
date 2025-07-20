package com.example.calpick.domain.util;

import com.example.calpick.global.exception.ErrorCode;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        ErrorCode ex = ErrorCode.UNAUTHORIZED_USER;

        response.setStatus(ex.getStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        String json = String.format("{\"error\": \"%s\",\n\"message\":\"%s\", \"errorCode\":\"%s\"}",
                ex.getStatus().getReasonPhrase(),
                ex.getMessage(),
                ex.getErrorCode());

        response.getWriter().write(json);
    }
}
