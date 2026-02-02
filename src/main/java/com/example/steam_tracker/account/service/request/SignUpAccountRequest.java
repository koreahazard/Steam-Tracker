package com.example.steam_tracker.account.service.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class SignUpAccountRequest {
    private final String username;
    private final String password;
    private final String email;
    private final String nickname;

}
