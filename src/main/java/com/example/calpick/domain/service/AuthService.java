package com.example.calpick.domain.service;

import com.example.calpick.domain.dto.auth.request.SignupRequest;
import com.example.calpick.domain.dto.auth.response.*;

public interface AuthService {
    public String logout();
    public SignupResponse signUp(SignupRequest request);

    public String withdraw();
}
