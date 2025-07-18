package com.example.calpick.global.exception;

import lombok.Getter;

@Getter
public class CalPickException extends RuntimeException{
    private final ErrorCode errorCode;

    public CalPickException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public CalPickException(ErrorCode errorCode, String detailMessage) {
        super(detailMessage != null ? String.format("%s: %s", errorCode.getMessage(), detailMessage)
                : errorCode.getMessage());
        this.errorCode = errorCode;
    }

}
