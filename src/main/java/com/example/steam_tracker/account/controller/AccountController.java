package com.example.steam_tracker.account.controller;

import com.example.steam_tracker.account.controller.requestForm.LoginAccountRequestForm;
import com.example.steam_tracker.account.controller.requestForm.SignUpAccountRequestForm;
import com.example.steam_tracker.account.service.AccountService;
import com.example.steam_tracker.account.service.request.LoginAccountRequest;
import com.example.steam_tracker.account.service.request.SignUpAccountRequest;
import com.example.steam_tracker.account.service.response.AccountInfoResponse;
import com.example.steam_tracker.account.service.response.AccountRefreshResponse;
import com.example.steam_tracker.account.service.response.LoginAccountResponse;
import com.example.steam_tracker.account.service.response.SignUpAccountResponse;
import com.example.steam_tracker.common.ResponseForm;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/account")
public class AccountController {

    private final AccountService accountService;

    @PostMapping("/signup")
    public ResponseEntity<ResponseForm<SignUpAccountResponse>> signup(
            @RequestBody SignUpAccountRequestForm form
    ) {
        log.info("[회원가입] API 호출 - Username: {}", form.getUsername());
        SignUpAccountRequest request = form.toSignUpAccountRequest();
        SignUpAccountResponse response = accountService.signUpAccount(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ResponseForm.success(
                        "SUCCESS",
                        "회원가입이 완료됐습니다",
                        response
                ));
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseForm<LoginAccountResponse>> login(
            @RequestBody LoginAccountRequestForm form
    ) {
        log.info("[로그인] API 호출 - Username: {}", form.getUsername());
        LoginAccountRequest request = form.toLoginAccountRequest();
        LoginAccountResponse response = accountService.loginAccount(request);
        log.info("[로그인] 완료 accountId={}, \n-accessToken={}, \n-refreshToken={}",
                response.getAccountId(),
                response.getAccessToken(),
                response.getRefreshToken()
        );
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseForm.success(
                        "SUCCESS",
                        "로그인이 완료됐습니다",
                        response
                ));

    }
    @GetMapping("/info")
    public ResponseEntity<ResponseForm<AccountInfoResponse>> info(
            @RequestHeader("Authorization") String authHeader
    ) {
        // "Bearer " 접두어 제거
        String token = authHeader.replace("Bearer ", "");

        // 액세스 토큰으로만 인증 (유효하지 않으면 CustomException 발생)
        Long accountId = accountService.getAccountIdFromToken(token, "access");
        log.info("[회원정보 조회] JWT에서 추출한 accountId={}", accountId);

        // DB에서 계정 정보 조회 (없으면 CustomException 발생)
        AccountInfoResponse response = accountService.getAccountInfo(accountId);
        log.info("[회원정보 조회] 완료 - accountId={}", accountId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseForm.success(
                        "SUCCESS",
                        "회원 정보 조회 완료",
                        response
                ));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ResponseForm<AccountRefreshResponse>> refresh(
            @RequestHeader("Authorization") String authHeader
    ) {
        String refreshToken = authHeader.replace("Bearer ", "");

        log.info("[토큰 재발급] refresh 요청");

        AccountRefreshResponse response =
                accountService.refreshAccessToken(refreshToken);

        log.info("[토큰 재발급] 새 access 발급 완료");

        return ResponseEntity.ok(
                ResponseForm.success(
                        "SUCCESS",
                        "Access 토큰 재발급 완료",
                        response
                )
        );
    }
}
