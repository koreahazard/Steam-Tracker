package com.example.steam_tracker.common;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtProvider {

    @Value("${JWT_SECRET}")
    private String jwtSecret;

    // 30분 = 30 * 60 * 1000 ms
    @Value("${ACCESS_TOKEN_EXPIRATION_MS}")
    private int accessTokenExpirationMs;

    // 7일 = 7 * 24 * 60 * 60 * 1000 ms
    @Value("${REFRESH_TOKEN_EXPIRATION_MS}")
    private int refreshTokenExpirationMs;

    // ===== Getter 추가 =====
    public String getJwtSecret() {
        return jwtSecret;
    }
    public int getAccessTokenExpirationMs() {
        return accessTokenExpirationMs;
    }

    public int getRefreshTokenExpirationMs() {
        return refreshTokenExpirationMs;
    }

    // 액세스 토큰 생성
    public String generateAccessToken(Long accountId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + accessTokenExpirationMs);

        return Jwts.builder()
                .setSubject(String.valueOf(accountId))
                .claim("type", "access") // 액세스 토큰임을 명시
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(Keys.hmacShaKeyFor(jwtSecret.getBytes()), SignatureAlgorithm.HS256)
                .compact();
    }

    // 리프레시 토큰 생성
    public String generateRefreshToken(Long accountId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + refreshTokenExpirationMs);

        return Jwts.builder()
                .setSubject(String.valueOf(accountId))
                .claim("type", "refresh") // 리프레시 토큰임을 명시
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(Keys.hmacShaKeyFor(jwtSecret.getBytes()), SignatureAlgorithm.HS256)
                .compact();
    }

    // 토큰에서 계정ID 추출 및 타입 체크
    public Long getAccountIdFromToken(String token, String expectedType) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(Keys.hmacShaKeyFor(jwtSecret.getBytes()))
                    .parseClaimsJws(token)
                    .getBody();

            String type = claims.get("type", String.class);

            if (!expectedType.equalsIgnoreCase(type)) {
                throw new CustomException(ErrorCode.INVALID_TOKEN);
            }

            return Long.parseLong(claims.getSubject());

        } catch (ExpiredJwtException e) {
            throw new CustomException(ErrorCode.ACCESS_TOKEN_EXPIRED);

        } catch (JwtException | IllegalArgumentException e) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }
    }

    // 토큰 검증 (유효 & 타입)
    public boolean validateToken(String token, String expectedType) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(Keys.hmacShaKeyFor(jwtSecret.getBytes()))
                    .parseClaimsJws(token)
                    .getBody();

            String type = claims.get("type", String.class);
            return expectedType.equalsIgnoreCase(type);
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
