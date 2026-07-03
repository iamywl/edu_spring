package com.edu.security.config;

import com.edu.security.service.JwtService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT 인증 필터
 *
 * OncePerRequestFilter를 상속하여 모든 HTTP 요청마다 한 번씩 실행됩니다.
 * 요청 헤더에서 JWT 토큰을 추출하고 유효성을 검증하여
 * SecurityContext에 인증 정보를 설정합니다.
 *
 * 처리 흐름:
 * 1. Authorization 헤더에서 "Bearer " 토큰 추출
 * 2. 토큰에서 사용자명 추출
 * 3. DB에서 사용자 정보 로드
 * 4. 토큰 유효성 검증
 * 5. SecurityContext에 인증 정보 설정
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        // 1. Authorization 헤더에서 JWT 토큰 추출
        final String authHeader = request.getHeader("Authorization");

        // Bearer 토큰이 없으면 다음 필터로 넘김 (인증 없이 진행)
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // "Bearer " 이후의 토큰 문자열 추출
        final String jwt = authHeader.substring(7);

        try {
            // 2. 토큰에서 사용자명 추출
            final String username = jwtService.extractUsername(jwt);

            // 3. 사용자명이 존재하고, 아직 인증되지 않은 경우에만 처리
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                // DB에서 사용자 정보 로드
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                // 4. 토큰 유효성 검증 (사용자명 일치 + 만료 여부)
                if (jwtService.isTokenValid(jwt, userDetails)) {

                    // 5. 인증 토큰 생성 및 SecurityContext에 설정
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,  // 비밀번호는 null (이미 JWT로 인증됨)
                                    userDetails.getAuthorities()  // 권한 정보
                            );

                    // 요청의 상세 정보 설정 (IP 주소, 세션 ID 등)
                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );

                    // SecurityContext에 인증 정보 저장
                    // -> 이후 컨트롤러에서 @AuthenticationPrincipal로 접근 가능
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (ExpiredJwtException e) {
            // 만료된 토큰: 정상적으로 발급됐으나 유효 기간이 지난 경우.
            // 재로그인이 필요한 흔한 상황이므로 INFO 수준으로 기록한다.
            logger.info("만료된 JWT 토큰: " + e.getMessage());
        } catch (JwtException e) {
            // 서명 불일치·구조 손상 등 위조 가능성이 있는 토큰.
            // (JwtException은 SignatureException·MalformedJwtException 등의 상위 예외)
            //
            // WHY: 위조·만료 토큰을 조용히 삼키면(catch(Exception)) 공격을 탐지할 수 없으므로
            //      구체적 예외로 구분해 로깅한다. 위조 시도는 보안 이벤트이므로 WARN 수준으로 남긴다.
            logger.warn("유효하지 않은(위조 의심) JWT 토큰: " + e.getMessage());
        }
        // 어느 경우든 예외를 필터 밖으로 던지지 않고 인증 없이 다음 필터로 진행한다.
        // -> SecurityContext에 인증 정보가 없으므로 보호된 자원 접근 시
        //    AuthenticationEntryPoint가 401을 반환한다.

        // 다음 필터로 요청 전달
        filterChain.doFilter(request, response);
    }
}
