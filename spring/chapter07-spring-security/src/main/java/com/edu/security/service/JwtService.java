package com.edu.security.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.Function;

/**
 * JWT 토큰 생성 및 검증 서비스
 *
 * io.jsonwebtoken (JJWT) 라이브러리를 사용하여
 * JWT 토큰의 생성, 파싱, 검증을 담당합니다.
 */
@Service
public class JwtService {

    /** application.yml에서 주입받는 JWT 비밀키 */
    @Value("${jwt.secret}")
    private String secretKey;

    /** 토큰 만료 시간 (밀리초) */
    @Value("${jwt.expiration}")
    private long expiration;

    /**
     * JWT 토큰을 생성합니다.
     *
     * @param userDetails 인증된 사용자 정보
     * @return 생성된 JWT 토큰 문자열
     */
    public String generateToken(UserDetails userDetails) {
        return Jwts.builder()
                .subject(userDetails.getUsername())       // 토큰 주체 (사용자명)
                .issuedAt(new Date())                     // 토큰 발행 시간
                .expiration(new Date(System.currentTimeMillis() + expiration))  // 만료 시간
                .signWith(getSigningKey())                // 서명 키로 서명
                .compact();                               // 토큰 문자열 생성
    }

    /**
     * 토큰에서 사용자명(subject)을 추출합니다.
     *
     * @param token JWT 토큰
     * @return 사용자명
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * 토큰의 유효성을 검증합니다.
     * - 토큰의 사용자명과 UserDetails의 사용자명이 일치하는지 확인
     * - 토큰이 만료되지 않았는지 확인
     *
     * @param token JWT 토큰
     * @param userDetails 사용자 정보
     * @return 유효 여부
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    /**
     * 토큰에서 특정 클레임을 추출합니다.
     *
     * @param token JWT 토큰
     * @param claimsResolver 클레임 추출 함수
     * @return 추출된 클레임 값
     */
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * 토큰에서 모든 클레임을 추출합니다.
     * 서명 키로 토큰의 무결성을 검증한 후 페이로드를 파싱합니다.
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())    // 서명 검증에 사용할 키 설정
                .build()
                .parseSignedClaims(token)       // 토큰 파싱 및 서명 검증
                .getPayload();                  // 페이로드(클레임) 반환
    }

    /**
     * 토큰 만료 여부를 확인합니다.
     */
    private boolean isTokenExpired(String token) {
        Date expirationDate = extractClaim(token, Claims::getExpiration);
        return expirationDate.before(new Date());
    }

    /**
     * JWT 서명에 사용할 SecretKey를 생성합니다.
     *
     * 비밀키 문자열의 UTF-8 바이트를 그대로 HMAC-SHA 키로 사용합니다.
     * (Base64 디코딩이 아니라 평문 문자열의 바이트를 사용)
     *
     * 주의: HS256 알고리즘은 최소 32바이트(256비트) 길이의 키를 요구합니다.
     * 따라서 application.yml의 jwt.secret 값은 최소 32자 이상의
     * ASCII 문자열이어야 합니다. (ASCII 1글자 = 1바이트)
     * 32자보다 짧으면 서명 시 WeakKeyException이 발생합니다.
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
