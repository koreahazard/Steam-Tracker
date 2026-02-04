package com.example.steam_tracker.common;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

    /* =========================
     * COMMON
     * ========================= */
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다"),

    /* =========================
     * 회원가입 예외처리
     * ========================= */
    INVALID_USERNAME_FORMAT(HttpStatus.BAD_REQUEST, "아이디는 6~15자의 영문 소문자 또는 숫자여야 합니다."),
    INVALID_PASSWORD_FORMAT(HttpStatus.BAD_REQUEST, "비밀번호는 8~20자의영문, 숫자, 특수문자여야 합니다."),
    INVALID_NICKNAME_FORMAT(HttpStatus.BAD_REQUEST, "닉네임은 2~10자의 한글, 영문, 숫자여야 합니다."),
    INVALID_EMAIL_FORMAT(HttpStatus.BAD_REQUEST, "올바른 이메일 형식이 아닙니다."),
    DUPLICATE_USERNAME(HttpStatus.CONFLICT, "이미 존재하는 아이디입니다"),
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "이미 존재하는 이메일입니다"),
    DUPLICATE_NICKNAME(HttpStatus.CONFLICT, "이미 존재하는 닉네임입니다"),
    /* =========================
     * 로그인 예외처리
     * ========================= */
    INVALID_CREDENTIALS(HttpStatus.CONFLICT,"아이디 혹은 비밀번호가 일치하지 않습니다."),
    /* =========================
     * 인증 예외처리
     * ========================= */
    ACCOUNT_NOT_FOUND(HttpStatus.NOT_FOUND,"계정 정보가 존재하지 않습니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED,"권한이 없습니다."),
    ACCESS_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED,"액세스 토큰이 만료되었습니다. 다시 로그인 해주세요."),
    REFRESH_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED,"리프레쉬 토큰이 만료되었습니다. 다시 로그인 해주세요.");


    private final HttpStatus status;
    private final String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
