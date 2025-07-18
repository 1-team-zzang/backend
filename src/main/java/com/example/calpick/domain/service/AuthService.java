package com.example.calpick.domain.service;

import com.example.calpick.domain.dto.auth.request.SignupRequest;
import com.example.calpick.domain.dto.auth.response.*;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {
    void logout(String email, HttpServletResponse response);
    public SignupResponse signUp(SignupRequest request);
    void withdraw(String email, HttpServletResponse response);
}
