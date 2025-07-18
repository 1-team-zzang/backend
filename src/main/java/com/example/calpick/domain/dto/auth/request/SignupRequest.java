package com.example.calpick.domain.dto.auth.request;

import lombok.Getter;

@Getter
public class SignupRequest {
    private String email;
    private String name;
    private String password;
}