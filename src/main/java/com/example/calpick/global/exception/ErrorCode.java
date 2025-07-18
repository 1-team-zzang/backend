package com.example.calpick.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ErrorCode {
    // AUTH
    DUPLICATED_EMAIL(HttpStatus.BAD_REQUEST, "중복된 이메일입니다.", "AUTH-01"),
    INVALID_EMAIL(HttpStatus.BAD_REQUEST, "이메일 형식이 아닙니다.", "AUTH-02"),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "올바른 형식의 비밀번호가 아닙니다.","AUTH-03"),
    INVALID_INPUT(HttpStatus.BAD_REQUEST, "필수 입력값이 미충족되었습니다.", "AUTH-04"),
    INVALID_JWT_TOKEN(HttpStatus.BAD_REQUEST, "토큰이 만료되었습니다. 다시 로그인해주세요.", "AUTH-05"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "internal server error", "COMMON-500");

    private final HttpStatus status;
    private final String message;
    private final String errorCode;

}
