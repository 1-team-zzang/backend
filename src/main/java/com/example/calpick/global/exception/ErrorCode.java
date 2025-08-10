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
    NO_ACCESS_TO_APPOINTMENT_REQUEST(HttpStatus.FORBIDDEN, "해당 약속 신청에 대한 권한이 없습니다.", "APT-07"),
    SELF_APPOINTMENT_REQUEST_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "본인에게 약속을 신청할 수 없습니다.", "APT-08"),



    // SCHEDULE
    SCHEDULE_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 일정입니다.", "SCH-01"),
    NO_ACCESS_TO_SCHEDULE(HttpStatus.UNAUTHORIZED, "접근 권한이 없습니다.", "SCH-02"),
    INVALID_SCHEDULE_INPUT(HttpStatus.BAD_REQUEST, "필수 입력값이 미충족되었습니다.", "SCH-03"),
    INVALID_SCHEDULE_TIME_RANGE(HttpStatus.NOT_FOUND, "시작시간이 종료시간보다 늦을 수 없습니다.", "SCH-04"),


    NOTIFICATION_NOT_FOUND(HttpStatus.NOT_FOUND,"해당 알림 정보를 찾을 수 없습니다","NOTI-01"),

    // AUTH
    DUPLICATED_EMAIL(HttpStatus.BAD_REQUEST, "중복된 이메일입니다.", "AUTH-01"),
    INVALID_EMAIL(HttpStatus.BAD_REQUEST, "이메일 형식이 아닙니다.", "AUTH-02"),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다.","AUTH-03"),
    INVALID_AUTH_INPUT(HttpStatus.BAD_REQUEST, "필수 입력값이 미충족되었습니다.", "AUTH-04"),
    INVALID_JWT_TOKEN(HttpStatus.BAD_REQUEST, "토큰이 만료되었습니다. 다시 로그인해주세요.", "AUTH-05"),

    UNAUTHORIZED_USER(HttpStatus.UNAUTHORIZED, "인증이 필요합니다. 로그인 후 다시 시도해주세요.", "AUTH-06"),
    KAKAO_TOKEN_REQUEST_FAILED(HttpStatus.BAD_REQUEST, "카카오 토큰 인증이 실패했습니다. 다시 시도해주세요.", "AUTH-07"),

    //친구
    NO_ACCESS_TO_FRIEND_REQUEST(HttpStatus.FORBIDDEN,"해당 친구 요청에 대한 권한이 없습니다.","FRD_01"),
    FRIEND_NOT_FOUND(HttpStatus.NOT_FOUND,"요청한 친구 정보를 찾을 수 없습니다.","FRD_02"),
    ALREADY_FRIENDS(HttpStatus.CONFLICT,"선택하신 사용자와는 이미 친구 상태입니다.","FRD_03"),
    FRIEND_REQUEST_NOT_FOUND(HttpStatus.NOT_FOUND,"친구 요청 정보를 찾을 수 없습니다.","FRD_04"),
    DUPLICATE_FRIEND_REQUEST(HttpStatus.CONFLICT,"이미 선택하신 사용자에게 친구 요청을 한 상태입니다.","FRD_05"),
    FRIEND_REQUEST_ALREADY_RECEIVED(HttpStatus.CONFLICT,"선택한 사용자가 친구 요청을 한 상태입니다.","FRD_06"),





    INVALID_INPUT_ERROR(HttpStatus.BAD_REQUEST, "필수 입력값이 미충족되었습니다.", "COMMON-400"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "internal server error", "COMMON-500");


    private final HttpStatus status;
    private final String message;
    private final String errorCode;

}
