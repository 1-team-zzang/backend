package com.example.calpick.domain.dto.auth.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class KakaoSignupRequest {
    public String email;
    public String idToken;
    public String name;
}
