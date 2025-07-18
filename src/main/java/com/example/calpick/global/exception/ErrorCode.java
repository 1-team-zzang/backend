package com.example.calpick.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ErrorCode {


    //APPOINTMENT
    INVALID_TIME_RANGE(HttpStatus.BAD_REQUEST,"시작시간이 종료시간보다 빨라야 합니다","APT-01"),
    DUPLICATE_APPOINTMENT_TIME(HttpStatus.CONFLICT,"신청한 시간에 이미 일정이 존재합니다","APT-05"),
    APPOINTMENT_NOT_FOUND(HttpStatus.NOT_FOUND,"해당 약속 정보를 찾을 수 없습니다","APT-04"),
    INVALID_APPOINTMENT_TIME(HttpStatus.BAD_REQUEST, "신청은 현재 시각 이후의 시간으로만 가능합니다", "APT-06"),


    NOTIFICATION_NOT_FOUND(HttpStatus.NOT_FOUND,"해당 알림 정보를 찾을 수 없습니다","NOTI-01"),

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
