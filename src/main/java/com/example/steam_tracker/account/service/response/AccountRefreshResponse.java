package com.example.steam_tracker.account.service.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AccountRefreshResponse {

    private final String accessToken;
}
