package com.example.calpick.domain.util;

import com.example.calpick.global.exception.CalPickException;
import com.example.calpick.global.exception.ErrorCode;

public class EnumUtil {
    public static <T extends Enum<T>> T fromString(Class<T> enumClass, String value){
        try {
            return (value == null || value.isBlank())? null : Enum.valueOf(enumClass, value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new CalPickException(ErrorCode.INVALID_SCHEDULE_INPUT, "유효하지 않은 enum 값(" + value + ") for " + enumClass.getName());
        }
    }
}
