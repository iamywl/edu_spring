package com.edu.security.service;

import com.edu.security.dto.AuthResponse;
import com.edu.security.dto.LoginRequest;
import com.edu.security.dto.SignUpRequest;
import com.edu.security.entity.User;
import com.edu.security.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 인증 서비스
 *
 * 회원가입과 로그인 로직을 담당합니다.
 * - 회원가입: 비밀번호를 BCrypt로 암호화하여 저장
 * - 로그인: 자격 증명 검증 후 JWT 토큰 발급
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;  // BCryptPasswordEncoder
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    /**
     * 회원가입을 처리합니다.
     *
     * 1. 사용자명 중복 확인
     * 2. 비밀번호를 BCrypt로 암호화
     * 3. 사용자 엔티티 저장
     * 4. JWT 토큰 생성 후 반환
     *
     * @param request 회원가입 요청 (username, password, role)
     * @return JWT 토큰이 포함된 인증 응답
     */
    @org.springframework.transaction.annotation.Transactional
    public AuthResponse register(SignUpRequest request) {
        // 사용자명 중복 확인
        if (userRepository.existsByUsername(request.username())) {
            throw new IllegalArgumentException("이미 존재하는 사용자명입니다: " + request.username());
        }

        // 사용자 엔티티 생성 (비밀번호는 BCrypt로 암호화)
        User user = User.builder()
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))  // BCrypt 암호화
                .role(request.role())
                .build();

        // DB에 저장
        userRepository.save(user);

        // JWT 토큰 생성 후 반환
        String token = jwtService.generateToken(user);
        return new AuthResponse(token);
    }

    /**
     * 로그인을 처리합니다.
     *
     * 1. AuthenticationManager를 통해 자격 증명 검증
     *    (내부적으로 UserDetailsService + PasswordEncoder 사용)
     * 2. 검증 성공 시 JWT 토큰 발급
     *
     * @param request 로그인 요청 (username, password)
     * @return JWT 토큰이 포함된 인증 응답
     */
    public AuthResponse login(LoginRequest request) {
        // Spring Security의 AuthenticationManager를 통해 인증 수행
        // 실패 시 BadCredentialsException 발생
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username(),
                        request.password()
                )
        );

        // 인증 성공 - 사용자 조회 후 JWT 토큰 생성
        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new UsernameNotFoundException(
                        "사용자를 찾을 수 없습니다: " + request.username()));

        String token = jwtService.generateToken(user);
        return new AuthResponse(token);
    }
}
