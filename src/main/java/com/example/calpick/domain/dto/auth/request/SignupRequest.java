package com.example.calpick.domain.dto.auth.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class SignupRequest {
    @Email
    private String email;
    @NotBlank
    private String name;
    @NotBlank
    private String password;
    private String profileUrl;
}