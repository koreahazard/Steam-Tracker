package com.example.steam_tracker.account.service.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class LoginAccountRequest {
    private final String username;
    private final String password;
}
