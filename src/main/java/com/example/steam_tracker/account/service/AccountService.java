package com.example.steam_tracker.account.service;

import com.example.steam_tracker.account.service.request.LoginAccountRequest;
import com.example.steam_tracker.account.service.request.SignUpAccountRequest;
import com.example.steam_tracker.account.service.response.LoginAccountResponse;
import com.example.steam_tracker.account.service.response.SignUpAccountResponse;

public interface AccountService {
    SignUpAccountResponse signUpAccount(SignUpAccountRequest request);
    LoginAccountResponse loginAccount(LoginAccountRequest request);
}
