package com.example.steam_tracker.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ResponseForm<Void>> handleCustomException(CustomException e) {
        ErrorCode errorCode = e.getErrorCode();
        log.error("비즈니스 예외 발생: [{}] {}", errorCode.name(), errorCode.getMessage());
        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ResponseForm.fail(errorCode));

    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseForm<Void>> handleException(Exception e) {
        // 사고 내용은 로그에만 자세히 남기고, 사용자에게는 보안상 500 에러 코드를 보냅니다.
        log.error("예상치 못한 서버 내부 에러 발생: ", e);

        return ResponseEntity
                .status(ErrorCode.INTERNAL_SERVER_ERROR.getStatus())
                .body(ResponseForm.fail(ErrorCode.INTERNAL_SERVER_ERROR));
    }

}
