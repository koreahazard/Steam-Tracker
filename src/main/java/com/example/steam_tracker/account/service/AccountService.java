package com.example.steam_tracker.account.service;

import com.example.steam_tracker.account.service.request.LoginAccountRequest;
import com.example.steam_tracker.account.service.request.SignUpAccountRequest;
import com.example.steam_tracker.account.service.response.AccountInfoResponse;
import com.example.steam_tracker.account.service.response.AccountRefreshResponse;
import com.example.steam_tracker.account.service.response.LoginAccountResponse;
import com.example.steam_tracker.account.service.response.SignUpAccountResponse;

public interface AccountService {
    SignUpAccountResponse signUpAccount(SignUpAccountRequest request);
    LoginAccountResponse loginAccount(LoginAccountRequest request);
    AccountInfoResponse getAccountInfo(Long accountId);
    Long getAccountIdFromToken(String token,String tokenType);
    AccountRefreshResponse refreshAccessToken(String refreshToken);
}
