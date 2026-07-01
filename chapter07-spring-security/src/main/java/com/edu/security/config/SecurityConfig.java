package com.edu.security.config;

import com.edu.security.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Spring Security 설정 클래스
 *
 * Spring Security 6.x에서는 SecurityFilterChain Bean을 직접 등록하는 방식을 사용합니다.
 * (기존의 WebSecurityConfigurerAdapter 상속 방식은 deprecated)
 *
 * 주요 설정:
 * - CSRF 비활성화 (JWT 기반이므로 CSRF 토큰 불필요)
 * - 세션 STATELESS 설정 (JWT로 인증하므로 서버 세션 불필요)
 * - URL 기반 접근 제어
 * - JWT 인증 필터 등록
 * - CORS 설정
 */
@Configuration
@EnableWebSecurity      // Spring Security 활성화
@EnableMethodSecurity   // @PreAuthorize 등 메서드 레벨 보안 활성화
public class SecurityConfig {

    /**
     * SecurityFilterChain 구성
     *
     * HTTP 요청에 대한 보안 규칙을 정의합니다.
     *
     * @param http HttpSecurity 설정 객체
     * @param jwtFilter JWT 인증 필터
     * @return 구성된 SecurityFilterChain
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtAuthenticationFilter jwtFilter) throws Exception {
        return http
                // CORS 설정 적용
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                // CSRF 비활성화 - JWT 기반 인증에서는 CSRF 토큰이 불필요
                .csrf(csrf -> csrf.disable())
                // 세션 관리 - STATELESS: 서버에서 세션을 생성하지 않음 (JWT 사용)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 인증/인가 실패 시 응답 처리
                // - 기본값은 둘 다 403을 반환하지만, REST API에서는 의미를 구분해야 한다.
                //   · 인증 실패(토큰 없음/유효하지 않음) → 401 Unauthorized
                //   · 인가 실패(인증은 됐으나 권한 부족, 예: USER가 ADMIN API 접근) → 403 Forbidden
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(authenticationEntryPoint())
                        .accessDeniedHandler(accessDeniedHandler()))
                // URL 기반 접근 제어 규칙
                .authorizeHttpRequests(auth -> auth
                        // 인증 API는 누구나 접근 가능 (회원가입, 로그인)
                        .requestMatchers("/api/auth/**").permitAll()
                        // 관리자 API는 ADMIN 역할만 접근 가능
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        // 그 외 모든 요청은 인증 필요
                        .anyRequest().authenticated()
                )
                // JWT 필터를 UsernamePasswordAuthenticationFilter 앞에 추가
                // -> 모든 요청에서 JWT 토큰을 먼저 확인
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    /**
     * 비밀번호 인코더 Bean 등록
     *
     * BCryptPasswordEncoder를 사용하여 비밀번호를 안전하게 해싱합니다.
     * - 솔트(Salt)를 자동으로 생성하여 같은 비밀번호도 매번 다른 해시값 생성
     * - 기본 strength: 10 (2^10 = 1,024번 해싱)
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * AuthenticationManager Bean 등록
     *
     * 인증 처리를 담당하는 매니저입니다.
     * AuthService에서 로그인 시 자격 증명 검증에 사용됩니다.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * UserDetailsService Bean 등록
     *
     * Spring Security가 사용자 인증 시 DB에서 사용자 정보를 로드하는 데 사용합니다.
     * JwtAuthenticationFilter에서도 토큰의 사용자명으로 사용자를 조회할 때 사용됩니다.
     */
    @Bean
    public UserDetailsService userDetailsService(UserRepository userRepository) {
        return username -> userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "사용자를 찾을 수 없습니다: " + username));
    }

    /**
     * 인증 실패(미인증) 처리기 → 401 Unauthorized
     *
     * 토큰이 없거나 유효하지 않아 SecurityContext에 인증 정보가 없는 상태에서
     * 보호된 자원에 접근하면 호출된다.
     */
    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return (request, response, authException) ->
                writeError(response, HttpStatus.UNAUTHORIZED, "인증이 필요합니다. 유효한 토큰을 포함해 주세요.");
    }

    /**
     * 인가 실패(권한 부족) 처리기 → 403 Forbidden
     *
     * 인증은 되었지만 해당 자원에 접근할 권한이 없을 때(예: USER가 ADMIN 전용 API 호출) 호출된다.
     */
    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return (request, response, accessDeniedException) ->
                writeError(response, HttpStatus.FORBIDDEN, "접근 권한이 없습니다.");
    }

    /**
     * 에러 응답을 JSON 형태로 직접 작성하는 헬퍼.
     */
    private void writeError(jakarta.servlet.http.HttpServletResponse response,
                            HttpStatus status, String message) throws java.io.IOException {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        String body = String.format("{\"status\":%d,\"error\":\"%s\",\"message\":\"%s\"}",
                status.value(), status.getReasonPhrase(), message);
        response.getWriter().write(body);
    }

    /**
     * CORS (Cross-Origin Resource Sharing) 설정
     *
     * 프론트엔드(React 등)와 백엔드가 다른 포트에서 실행될 때 필요합니다.
     * 예: 프론트엔드 http://localhost:3000 → 백엔드 http://localhost:8080
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        // 허용할 출처 (프론트엔드 주소)
        config.setAllowedOrigins(List.of("http://localhost:3000"));
        // 허용할 HTTP 메서드
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        // 허용할 헤더 (Authorization 헤더 포함)
        config.setAllowedHeaders(List.of("*"));
        // 자격 증명(쿠키 등) 포함 허용
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // 모든 경로에 CORS 설정 적용
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
