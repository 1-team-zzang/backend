package com.example.calpick.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ErrorCode {
    INVALID_EMAIL(HttpStatus.BAD_REQUEST, "이메일 형식이 올바르지 않습니다.", "AUTH-01"),

    //약속
    INVALID_TIME_RANGE(HttpStatus.BAD_REQUEST,"시작시간이 종료시간보다 빨라야 합니다","APT-01"),
    DUPLICATE_APPOINTMENT_TIME(HttpStatus.CONFLICT,"신청한 시간에 이미 일정이 존재합니다","APT-05"),
    APPOINTMENT_NOT_FOUND(HttpStatus.NOT_FOUND,"해당 약속 정보를 찾을 수 없습니다","APT-04"),
    INVALID_APPOINTMENT_TIME(HttpStatus.BAD_REQUEST, "신청은 현재 시각 이후의 시간으로만 가능합니다", "APT-06"),


    NOTIFICATION_NOT_FOUND(HttpStatus.NOT_FOUND,"해당 알림 정보를 찾을 수 없습니다","NOTI-01"),


    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "internal server error", "COMMON-500");


    private final HttpStatus status;
    private final String message;
    private final String errorCode;

}
