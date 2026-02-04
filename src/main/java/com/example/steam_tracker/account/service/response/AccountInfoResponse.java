package com.example.steam_tracker.account.service.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AccountInfoResponse {
    private final Long accountId;
    private final String username;
    private final String email;
    private final String nickname;
}
