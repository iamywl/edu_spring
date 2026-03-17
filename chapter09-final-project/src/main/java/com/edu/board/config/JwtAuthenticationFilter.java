package com.edu.board.config;

import com.edu.board.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
 * 모든 HTTP 요청에서 Authorization 헤더를 확인하고,
 * 유효한 JWT 토큰이 있으면 SecurityContext에 인증 정보를 설정합니다.
 *
 * OncePerRequestFilter를 상속하여 요청당 한 번만 실행됩니다.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    /**
     * 필터 핵심 로직
     *
     * 1. Authorization 헤더에서 "Bearer " 토큰 추출
     * 2. 토큰에서 사용자명 추출
     * 3. 사용자 정보 로드 및 토큰 유효성 검증
     * 4. 인증 성공 시 SecurityContext에 인증 정보 설정
     */
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        // Authorization 헤더 확인
        final String authHeader = request.getHeader("Authorization");

        // Bearer 토큰이 아니면 다음 필터로 넘김
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // "Bearer " 이후의 토큰 문자열 추출
        final String jwt = authHeader.substring(7);

        try {
            // 토큰에서 사용자명 추출
            final String username = jwtService.extractUsername(jwt);

            // 사용자명이 있고, 아직 인증되지 않은 상태인 경우
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // DB에서 사용자 정보 로드
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                // 토큰 유효성 검증
                if (jwtService.isTokenValid(jwt, userDetails)) {
                    // 인증 토큰 생성
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );
                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );

                    // SecurityContext에 인증 정보 설정
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            // 토큰 파싱/검증 실패 시 인증 없이 계속 진행
            // (공개 API는 인증 없이도 접근 가능)
            logger.debug("JWT 인증 실패: " + e.getMessage());
        }

        // 다음 필터로 요청 전달
        filterChain.doFilter(request, response);
    }
}
