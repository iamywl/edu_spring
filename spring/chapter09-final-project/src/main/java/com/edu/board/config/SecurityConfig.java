package com.edu.board.config;

import com.edu.board.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.HashMap;
import java.util.Map;

/**
 * Spring Security 설정
 *
 * JWT 기반의 Stateless 인증을 구성합니다.
 * - 세션을 사용하지 않음 (STATELESS)
 * - CSRF 비활성화 (REST API이므로)
 * - JWT 인증 필터 등록
 * - 경로별 접근 권한 설정
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserRepository userRepository;

    public SecurityConfig(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    // 주의: JwtAuthenticationFilter는 생성자로 주입하지 않는다.
    // 필터가 UserDetailsService(이 클래스의 @Bean)에 의존하고, 이 클래스는 필터에 의존하면
    // 순환 참조(BeanCurrentlyInCreationException)가 발생한다.
    // → 아래 securityFilterChain의 "메서드 파라미터"로 주입받아 순환을 끊는다.

    /**
     * Security 필터 체인 설정
     *
     * URL별 접근 권한:
     * - /api/auth/** : 누구나 접근 가능 (회원가입, 로그인)
     * - GET /api/posts, /api/posts/{id} : 누구나 접근 가능 (목록/상세 조회)
     * - GET /api/posts/{postId}/comments : 누구나 접근 가능 (댓글 조회)
     * - 그 외 API : 인증 필요
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthFilter) throws Exception {
        http
                // CSRF 비활성화 (REST API는 토큰 기반이므로 불필요)
                .csrf(AbstractHttpConfigurer::disable)

                // URL별 권한 설정
                .authorizeHttpRequests(auth -> auth
                        // 인증 API - 모두 허용
                        .requestMatchers("/api/auth/**").permitAll()
                        // 게시글/댓글 조회 - 모두 허용
                        .requestMatchers(HttpMethod.GET, "/api/posts/**").permitAll()
                        // 그 외 모든 요청 - 인증 필요
                        .anyRequest().authenticated()
                )

                // 인증/인가 예외 처리
                // - 인증 안 됨(토큰 없음/유효하지 않음) → 401 Unauthorized
                // - 인증은 됐으나 권한 부족 → 403 Forbidden
                // (기본값은 둘 다 403이라, 명시적으로 401/403을 구분해 REST API 규약을 따른다)
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(authenticationEntryPoint())
                        .accessDeniedHandler(accessDeniedHandler())
                )

                // 세션 관리 - Stateless (세션 사용 안 함)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // 인증 프로바이더 설정
                .authenticationProvider(authenticationProvider())

                // JWT 필터를 UsernamePasswordAuthenticationFilter 앞에 추가
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * UserDetailsService 빈 등록
     *
     * Spring Security가 사용자 인증 시 DB에서 사용자 정보를 로드하는 방법을 정의합니다.
     */
    @Bean
    public UserDetailsService userDetailsService() {
        return username -> userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username));
    }

    /**
     * AuthenticationProvider 빈 등록
     *
     * DaoAuthenticationProvider를 사용하여 DB 기반 인증을 수행합니다.
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * AuthenticationManager 빈 등록
     *
     * AuthService에서 로그인 인증 시 사용됩니다.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * PasswordEncoder 빈 등록
     *
     * BCrypt 해시 알고리즘을 사용하여 비밀번호를 암호화합니다.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * AuthenticationEntryPoint 빈 등록
     *
     * 인증되지 않은(=로그인하지 않은) 사용자가 보호된 자원에 접근할 때 호출됩니다.
     * 401 Unauthorized 상태 코드와 JSON 에러 본문을 반환합니다.
     */
    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return (request, response, authException) ->
                writeError(response, HttpServletResponse.SC_UNAUTHORIZED, "인증이 필요합니다.");
    }

    /**
     * AccessDeniedHandler 빈 등록
     *
     * 인증은 되었지만 권한이 부족한 사용자가 접근할 때 호출됩니다.
     * 403 Forbidden 상태 코드와 JSON 에러 본문을 반환합니다.
     */
    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return (request, response, accessDeniedException) ->
                writeError(response, HttpServletResponse.SC_FORBIDDEN, "접근 권한이 없습니다.");
    }

    /**
     * 보안 예외 응답 본문을 JSON으로 작성하는 유틸리티 메서드
     * GlobalExceptionHandler의 에러 응답 형식과 일관성을 맞춥니다.
     */
    private void writeError(HttpServletResponse response, int status, String message) throws java.io.IOException {
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        Map<String, Object> body = new HashMap<>();
        body.put("status", status);
        body.put("message", message);

        new ObjectMapper().writeValue(response.getWriter(), body);
    }
}
