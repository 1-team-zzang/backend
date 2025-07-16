package com.example.calpick.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.LinkedHashMap;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Response<T> {
    private Integer code;
    private String message;
    private T data;

    public static Response<Void> error(String errorCode, String message) {
        return new Response<>(Integer.valueOf(errorCode), message, null);
    }

    public static <T> Response<T> success(T data) {
        return new Response<>(200, "success", data);
    }

    public static Response<Object> success() {
        return new Response<>(200, "success",  new LinkedHashMap<>());
    }
}
