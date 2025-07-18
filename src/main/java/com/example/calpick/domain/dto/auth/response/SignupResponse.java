package com.example.calpick.domain.dto.auth.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class SignupResponse {
    private String email;
    private String name;
}
