package com.example.calpick.domain.dto.auth.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class KakaoCodeRequest {
    @NotBlank
    String code;
}
