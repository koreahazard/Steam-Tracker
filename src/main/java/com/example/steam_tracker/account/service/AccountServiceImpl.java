package com.example.steam_tracker.account.service;

import com.example.steam_tracker.account.entity.Account;
import com.example.steam_tracker.account.repository.AccountRepository;
import com.example.steam_tracker.account.service.request.LoginAccountRequest;
import com.example.steam_tracker.account.service.request.SignUpAccountRequest;
import com.example.steam_tracker.account.service.response.LoginAccountResponse;
import com.example.steam_tracker.account.service.response.SignUpAccountResponse;
import com.example.steam_tracker.common.CustomException;
import com.example.steam_tracker.common.ErrorCode;
import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.regex.Pattern;

@Slf4j
@Getter
@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;
    private final String USERNAME_PATTERN = "^[a-z0-9]{6,15}$"; // 영어소문자+숫자, 6~15자
    private final String PASSWORD_PATTERN = "^[a-zA-Z0-9!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]{8,20}$"; // 영문+숫자+특수문자, 8~20자
    private final String NICKNAME_PATTERN = "^[a-zA-Z0-9가-힣]{2,10}$"; // 한글+영문+숫자, 2~10자

    @Override
    @Transactional
    public SignUpAccountResponse signUpAccount(SignUpAccountRequest request) {

        validateRequestFormat(request);
        validateDuplicateAccount(request);

        String encodedPassword = BCrypt.hashpw(request.getPassword(),BCrypt.gensalt());
        Account account = new Account(
                request.getUsername(),
                encodedPassword,
                request.getEmail(),
                request.getNickname());

        Account savedAccount = accountRepository.save(account);

        log.info("회원가입 완료: AccountId = {}, Username = {}", savedAccount.getAccountId(), savedAccount.getUsername());

        return new SignUpAccountResponse(savedAccount.getAccountId());

    }
    @Override
    @Transactional
    public LoginAccountResponse loginAccount(LoginAccountRequest request) {
            Account account = validateCredentials(request);
        log.info("로그인 완료: AccountId = {}, Username = {}", account.getAccountId(), account.getUsername());
        return new LoginAccountResponse(account.getAccountId());

    }
    private void validateDuplicateAccount(SignUpAccountRequest request) {
        if (accountRepository.existsByUsername(request.getUsername())) {
            throw new CustomException(ErrorCode.DUPLICATE_USERNAME);
        }
        if (accountRepository.existsByEmail(request.getEmail())) {
            throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
        }
        if (accountRepository.existsByNickname(request.getNickname())) {
            throw new CustomException(ErrorCode.DUPLICATE_NICKNAME);
        }
    }
    private void validateRequestFormat(SignUpAccountRequest request) {

        if (!Pattern.matches(USERNAME_PATTERN, request.getUsername())) {
            throw new CustomException(ErrorCode.INVALID_USERNAME_FORMAT);
        }

        if (!Pattern.matches(PASSWORD_PATTERN, request.getPassword())) {
            throw new CustomException(ErrorCode.INVALID_PASSWORD_FORMAT);
        }

        if (!Pattern.matches(NICKNAME_PATTERN, request.getNickname())) {
            throw new CustomException(ErrorCode.INVALID_NICKNAME_FORMAT);
        }

        if (request.getEmail() == null || !request.getEmail().contains("@")) {
            throw new CustomException(ErrorCode.INVALID_EMAIL_FORMAT);
        }
    }
    private Account validateCredentials(LoginAccountRequest request)
    {
        Optional<Account> optionalAccount = accountRepository.findByUsername(request.getUsername());

        if (optionalAccount.isEmpty()) {
            throw new CustomException(ErrorCode.INVALID_CREDENTIALS);
        }

        Account account = optionalAccount.get();

        if (!BCrypt.checkpw(request.getPassword(), account.getPassword())) {
            throw new CustomException(ErrorCode.INVALID_CREDENTIALS);
        }
        return account;

    }
}
