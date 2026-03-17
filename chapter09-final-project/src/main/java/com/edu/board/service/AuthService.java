package com.edu.board.service;

import com.edu.board.dto.AuthResponse;
import com.edu.board.dto.LoginRequest;
import com.edu.board.dto.SignUpRequest;
import com.edu.board.entity.User;
import com.edu.board.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 인증 서비스
 *
 * 회원가입과 로그인 비즈니스 로직을 처리합니다.
 */
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    /**
     * 회원가입 처리
     *
     * 1. 사용자명 중복 체크
     * 2. 비밀번호 BCrypt 암호화
     * 3. 사용자 저장
     * 4. JWT 토큰 발급
     *
     * @param request 회원가입 요청 DTO
     * @return 토큰과 사용자명을 포함한 인증 응답
     */
    @Transactional
    public AuthResponse register(SignUpRequest request) {
        // 사용자명 중복 체크
        if (userRepository.existsByUsername(request.username())) {
            throw new IllegalArgumentException("이미 사용 중인 사용자명입니다: " + request.username());
        }

        // 새 사용자 생성 (비밀번호는 BCrypt로 암호화)
        User user = new User(
                request.username(),
                passwordEncoder.encode(request.password())
        );
        userRepository.save(user);

        // JWT 토큰 생성 후 응답 반환
        String token = jwtService.generateToken(user);
        return new AuthResponse(token, user.getUsername());
    }

    /**
     * 로그인 처리
     *
     * 1. Spring Security의 AuthenticationManager로 인증
     * 2. 인증 성공 시 JWT 토큰 발급
     *
     * @param request 로그인 요청 DTO
     * @return 토큰과 사용자명을 포함한 인증 응답
     */
    public AuthResponse login(LoginRequest request) {
        // Spring Security를 통한 인증 (실패 시 BadCredentialsException 발생)
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username(),
                        request.password()
                )
        );

        // 인증 성공 - 사용자 조회 후 토큰 발급
        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        String token = jwtService.generateToken(user);
        return new AuthResponse(token, user.getUsername());
    }
}
