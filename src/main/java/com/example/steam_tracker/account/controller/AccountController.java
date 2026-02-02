package com.example.steam_tracker.account.controller;

import com.example.steam_tracker.account.controller.requestForm.SignUpAccountRequestForm;
import com.example.steam_tracker.account.service.AccountService;
import com.example.steam_tracker.account.service.request.SignUpAccountRequest;
import com.example.steam_tracker.account.service.response.SignUpAccountResponse;
import com.example.steam_tracker.common.ResponseForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/account")
public class AccountController {

    private final AccountService accountService;

    @PostMapping("/signup")
    public ResponseEntity<ResponseForm<Object>> signup(
            @RequestBody SignUpAccountRequestForm form
    ) {
        log.info("회원가입 API 호출 - Username: {}", form.getUsername());
        SignUpAccountRequest request = form.toSignUpAccountRequest();
        SignUpAccountResponse response = accountService.signUpAccount(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ResponseForm.success(
                        HttpStatus.OK,
                        "SUCCESS",
                        "회원가입에 성공했습니다.",
                        response
                ));
    }
}
