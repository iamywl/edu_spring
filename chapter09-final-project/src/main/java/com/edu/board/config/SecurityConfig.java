package com.edu.board.config;

import com.edu.board.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final UserRepository userRepository;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter, UserRepository userRepository) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.userRepository = userRepository;
    }

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
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
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
}
