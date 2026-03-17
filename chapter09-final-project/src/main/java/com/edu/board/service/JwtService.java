package com.edu.board.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * JWT 토큰 서비스
 *
 * JWT(JSON Web Token)의 생성, 파싱, 검증을 담당합니다.
 * - 토큰 생성: 사용자 정보를 기반으로 JWT 생성
 * - 토큰 파싱: JWT에서 사용자명 등 클레임(Claims) 추출
 * - 토큰 검증: 토큰의 유효성(만료, 변조) 확인
 */
@Service
public class JwtService {

    /** JWT 서명에 사용할 비밀키 (application.yml에서 주입) */
    @Value("${jwt.secret}")
    private String secretKey;

    /** 토큰 만료 시간 (밀리초, 기본 24시간) */
    @Value("${jwt.expiration}")
    private long expiration;

    /**
     * JWT에서 사용자명(subject)을 추출합니다.
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * JWT에서 특정 클레임을 추출합니다.
     *
     * @param token          JWT 토큰
     * @param claimsResolver 클레임에서 원하는 값을 추출하는 함수
     * @param <T>            반환 타입
     * @return 추출된 클레임 값
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * UserDetails 정보를 기반으로 JWT를 생성합니다.
     *
     * @param userDetails 인증된 사용자 정보
     * @return 생성된 JWT 문자열
     */
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    /**
     * 추가 클레임과 UserDetails를 기반으로 JWT를 생성합니다.
     *
     * @param extraClaims 추가로 포함할 클레임 맵
     * @param userDetails 인증된 사용자 정보
     * @return 생성된 JWT 문자열
     */
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts.builder()
                .claims(extraClaims)                                           // 추가 클레임 설정
                .subject(userDetails.getUsername())                             // subject = 사용자명
                .issuedAt(new Date(System.currentTimeMillis()))                // 발급 시각
                .expiration(new Date(System.currentTimeMillis() + expiration)) // 만료 시각
                .signWith(getSigningKey())                                      // 서명
                .compact();                                                     // 최종 JWT 문자열 생성
    }

    /**
     * 토큰이 유효한지 검증합니다.
     * - 토큰의 사용자명과 전달된 UserDetails의 사용자명이 일치하는지
     * - 토큰이 만료되지 않았는지
     *
     * @param token       JWT 토큰
     * @param userDetails 비교할 사용자 정보
     * @return 유효하면 true
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    /**
     * 토큰이 만료되었는지 확인합니다.
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * 토큰에서 만료 시각을 추출합니다.
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * 토큰에서 모든 클레임을 파싱합니다.
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())   // 서명 검증 키 설정
                .build()
                .parseSignedClaims(token)       // 토큰 파싱 및 서명 검증
                .getPayload();                  // 클레임(페이로드) 반환
    }

    /**
     * 비밀키 문자열을 HMAC-SHA 키 객체로 변환합니다.
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
