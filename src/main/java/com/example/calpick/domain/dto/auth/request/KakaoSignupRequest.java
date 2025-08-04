package com.example.calpick.domain.dto.auth.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class KakaoSignupRequest {
    @Email
    public String email;
    @NotBlank
    public String idToken;
    @NotBlank
    public String name;
}
