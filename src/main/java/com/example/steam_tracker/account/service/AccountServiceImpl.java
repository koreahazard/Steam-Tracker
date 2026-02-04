package com.example.steam_tracker.account.service;

import com.example.steam_tracker.account.entity.Account;
import com.example.steam_tracker.account.repository.AccountRepository;
import com.example.steam_tracker.account.service.request.LoginAccountRequest;
import com.example.steam_tracker.account.service.request.SignUpAccountRequest;
import com.example.steam_tracker.account.service.response.AccountInfoResponse;
import com.example.steam_tracker.account.service.response.AccountRefreshResponse;
import com.example.steam_tracker.account.service.response.LoginAccountResponse;
import com.example.steam_tracker.account.service.response.SignUpAccountResponse;
import com.example.steam_tracker.common.CustomException;
import com.example.steam_tracker.common.ErrorCode;
import com.example.steam_tracker.common.JwtProvider;
import com.example.steam_tracker.redis.RedisService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;
import java.util.regex.Pattern;

@Slf4j
@Getter
@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;
    private final RedisService redisService;
    private final JwtProvider jwtProvider;
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

        String accessToken = jwtProvider.generateAccessToken(account.getAccountId());
        String refreshToken = jwtProvider.generateRefreshToken(account.getAccountId());
        redisService.save(
                "refresh:" + account.getAccountId(),
                refreshToken,
                jwtProvider.getRefreshTokenExpirationMs() // ms 단위
        );

        return new LoginAccountResponse(account.getAccountId(),accessToken,refreshToken);

    }
    @Override
    @Transactional
    public AccountInfoResponse getAccountInfo(Long accountId) {
        Optional<Account> optionalAccount = accountRepository.findById(accountId);

        if (optionalAccount.isEmpty()) {
            throw new CustomException(ErrorCode.ACCOUNT_NOT_FOUND);
        }

        Account account = optionalAccount.get();

        return new AccountInfoResponse(
                account.getAccountId(),
                account.getUsername(),
                account.getEmail(),
                account.getNickname()
        );

    }
    @Override
    @Transactional
    public Long getAccountIdFromToken(String token, String expectedType) {
        try {
            Long accountId = jwtProvider.getAccountIdFromToken(token, "access");
            log.info("getAccountIdFromToken 호출: accountId = {}, tokenType = {}", accountId, expectedType);

            if ("access".equalsIgnoreCase(expectedType)) {
                Date now = new Date();
                Claims claims = Jwts.parser()
                        .setSigningKey(Keys.hmacShaKeyFor(jwtProvider.getJwtSecret().getBytes()))
                        .parseClaimsJws(token)
                        .getBody();

                if (claims.getExpiration().before(now)) {
                    throw new CustomException(ErrorCode.ACCESS_TOKEN_EXPIRED);
                }
            }

            return accountId;
        } catch (JwtException | IllegalArgumentException e) {
            log.error("토큰 검증 실패: {}", e.getMessage());
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }
    }
    @Override
    @Transactional
    public AccountRefreshResponse refreshAccessToken(String refreshToken) {

        // 1. refresh 토큰 검증 + accountId 추출
        Long accountId = jwtProvider.getAccountIdFromToken(refreshToken, "refresh");

        log.info("[Refresh] accountId={}", accountId);

        // 2. Redis 저장된 refresh 조회
        String savedRefresh = redisService.get("refresh:" + accountId);

        if (savedRefresh == null) {
            throw new CustomException(ErrorCode.REFRESH_TOKEN_EXPIRED);
        }

        // 3. 토큰 불일치 → 위조 or 다른 기기 로그인
        if (!savedRefresh.equals(refreshToken)) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }

        // 4. 새 access 발급
        String newAccess = jwtProvider.generateAccessToken(accountId);

        return new AccountRefreshResponse(newAccess);
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
