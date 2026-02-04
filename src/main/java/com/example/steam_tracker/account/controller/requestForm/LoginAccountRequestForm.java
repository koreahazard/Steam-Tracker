package com.example.steam_tracker.account.controller.requestForm;

import com.example.steam_tracker.account.service.request.LoginAccountRequest;
import com.example.steam_tracker.account.service.request.SignUpAccountRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class LoginAccountRequestForm {
    private final String username;
    private final String password;

    public LoginAccountRequest toLoginAccountRequest() {
        return new LoginAccountRequest(
                this.username,
                this.password
        );
    }
}
