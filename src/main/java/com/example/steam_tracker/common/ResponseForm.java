package com.example.steam_tracker.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public class ResponseForm<T> {

    private final boolean success;
    private final String code;
    private final String message;
    private final T data;

    public static <T> ResponseForm<T> success( String code, String message, T data) {
        return new ResponseForm<> (
                true,
                code,
                message,
                data
        );
    }

    public static <T> ResponseForm<T> fail(ErrorCode errorCode) {
        return new ResponseForm<>(
                false,
                errorCode.name(),
                errorCode.getMessage(),
                null
        );
    }

}
