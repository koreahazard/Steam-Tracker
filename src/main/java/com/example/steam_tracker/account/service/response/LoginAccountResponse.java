package com.example.steam_tracker.account.service.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class LoginAccountResponse {
	private final Long accountId;
	private final String accessToken;
	private final String refreshToken;
}
