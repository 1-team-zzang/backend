package com.example.calpick.global.exception;

import com.example.calpick.domain.dto.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalControllerAdvice {

    @ExceptionHandler(CalPickException.class)
    public ResponseEntity<?> applicationHandler(CalPickException e) {
        log.error("Error cause {}", e.toString());
        ErrorCode errorCode = e.getErrorCode();

        ErrorResponse errorResponse = new ErrorResponse(
                errorCode.getStatus().getReasonPhrase(),
                errorCode.getMessage(),
                errorCode.getErrorCode()
        );

        return ResponseEntity
                .status(errorCode.getStatus())
                .body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> globalHandler(Exception e) {
        log.error("Unhandled exception: {}", e.getMessage(), e);
        ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(
                        errorCode.getStatus().getReasonPhrase(),
                        e.getMessage(),
                        errorCode.getErrorCode()
                ));
    }

}
