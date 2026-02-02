package com.example.steam_tracker.account.controller.requestForm;

import com.example.steam_tracker.account.service.request.SignUpAccountRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class SignUpAccountRequestForm {
    private final String username;
    private final String password;
    private final String email;
    private final String nickname;

    public SignUpAccountRequest toSignUpAccountRequest() {
        return new SignUpAccountRequest(
                this.username,
                this.password,
                this.email,
                this.nickname
        );
    }
}
