# Chapter 07: Spring Security - JWT 기반 인증/인가

> **🐳 실습 환경 — 이 장은 `spring-ch07-security` 컨테이너(+ 전용 DB `spring-ch07-postgres`)로 실습한다**
> ```bash
> cd spring/chapter07-spring-security && docker compose up --build
> ```
> 위 명령 한 방으로 앱+DB가 모두 뜬다 (공통 인프라 `spring-postgres`와 동시에 띄우면 5432 포트 충돌 주의).
> API 호출은 `requests.http` 파일로 (VS Code REST Client). 컨테이너 상태 확인: `docker ps`

## 목차
1. [Spring Security 아키텍처](#1-spring-security-아키텍처)
2. [인증(Authentication) vs 인가(Authorization)](#2-인증authentication-vs-인가authorization)
3. [Password Encoding (BCrypt)](#3-password-encoding-bcrypt)
4. [JWT 기반 인증](#4-jwt-기반-인증)
5. [Role 기반 접근 제어](#5-role-기반-접근-제어)
6. [CORS 설정](#6-cors-설정)
7. [Docker로 실행하기](#7-docker로-실행하기)

---

## 1. Spring Security 아키텍처

### SecurityFilterChain 이해하기

Spring Security는 서블릿 필터(Servlet Filter) 기반으로 동작합니다.
HTTP 요청이 들어오면 여러 개의 보안 필터를 순서대로 거치게 됩니다.

```
HTTP 요청 → DelegatingFilterProxy → FilterChainProxy → SecurityFilterChain
                                                          ├── CorsFilter
                                                          ├── CsrfFilter
                                                          ├── UsernamePasswordAuthenticationFilter
                                                          ├── BearerTokenAuthenticationFilter
                                                          ├── AuthorizationFilter
                                                          └── ...
```

### 핵심 구성 요소

| 구성 요소 | 설명 |
|-----------|------|
| `SecurityFilterChain` | 보안 필터들의 체인. 요청에 대해 어떤 필터를 적용할지 결정 |
| `AuthenticationManager` | 인증 처리를 담당하는 매니저 |
| `AuthenticationProvider` | 실제 인증 로직을 수행 (DB 조회, 비밀번호 검증 등) |
| `UserDetailsService` | 사용자 정보를 로드하는 서비스 |
| `SecurityContext` | 인증된 사용자 정보를 보관하는 컨텍스트 |

### SecurityFilterChain 설정 예시

```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    return http
        .csrf(csrf -> csrf.disable())                    // CSRF 비활성화 (JWT 사용 시)
        .sessionManagement(session ->
            session.sessionCreationPolicy(STATELESS))    // 세션 사용 안 함
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/api/auth/**").permitAll()  // 인증 없이 접근 가능
            .requestMatchers("/api/admin/**").hasRole("ADMIN")  // ADMIN만 접근
            .anyRequest().authenticated()                 // 나머지는 인증 필요
        )
        .build();
}
```

> **참고**: Spring Security 6.x부터는 `WebSecurityConfigurerAdapter`가 제거되었습니다.
> 대신 `SecurityFilterChain` Bean을 직접 등록하는 방식을 사용합니다.

---

## 2. 인증(Authentication) vs 인가(Authorization)

### 인증 (Authentication) - "누구인가?"

사용자가 **본인이 맞는지** 확인하는 과정입니다.

- 아이디/비밀번호 로그인
- JWT 토큰 검증
- OAuth2 소셜 로그인

```
사용자 → 아이디/비밀번호 제출 → AuthenticationManager → 인증 성공/실패
```

### 인가 (Authorization) - "무엇을 할 수 있는가?"

인증된 사용자가 **특정 리소스에 접근할 권한**이 있는지 확인하는 과정입니다.

- URL 기반 접근 제어
- 메서드 레벨 보안 (`@PreAuthorize`)
- Role 기반 접근 제어

```java
// URL 기반 인가
.requestMatchers("/api/admin/**").hasRole("ADMIN")

// 메서드 레벨 인가
@PreAuthorize("hasRole('ADMIN')")
public List<User> getAllUsers() { ... }
```

### 인증 vs 인가 비교

| 구분 | 인증 (Authentication) | 인가 (Authorization) |
|------|----------------------|---------------------|
| 목적 | 사용자 신원 확인 | 접근 권한 확인 |
| 시점 | 인가보다 먼저 수행 | 인증 이후 수행 |
| 실패 시 | 401 Unauthorized | 403 Forbidden |
| 예시 | 로그인 | 관리자 페이지 접근 |

---

## 3. Password Encoding (BCrypt)

### 왜 비밀번호를 암호화해야 하나?

비밀번호를 평문으로 저장하면 데이터베이스가 유출될 경우 모든 사용자의 비밀번호가 노출됩니다.
**BCrypt**는 단방향 해시 함수로, 원본 비밀번호를 복원할 수 없습니다.

### BCrypt 특징

- **솔트(Salt) 자동 생성**: 같은 비밀번호라도 매번 다른 해시값 생성
- **적응형 해싱**: 연산 비용(strength)을 조절하여 무차별 대입 공격 방어
- **기본 strength**: 10 (2^10 = 1,024번 해싱)

```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();  // 기본 strength: 10
}

// 사용 예시
String encoded = passwordEncoder.encode("myPassword123");
// $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy

boolean matches = passwordEncoder.matches("myPassword123", encoded);
// true
```

### BCrypt 해시 구조

```
$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy
 │  │  │                      │
 │  │  │                      └── 해시값 (31자)
 │  │  └── 솔트 (22자)
 │  └── 비용 인자 (10 = 2^10번 반복)
 └── 알고리즘 버전
```

---

## 4. JWT 기반 인증

### JWT (JSON Web Token)란?

JWT는 당사자 간에 정보를 안전하게 전송하기 위한 토큰 표준(RFC 7519)입니다.
서버가 세션을 유지할 필요 없이 토큰만으로 인증 상태를 확인할 수 있습니다.

### JWT 구조

```
xxxxx.yyyyy.zzzzz
  │      │     │
  │      │     └── Signature (서명)
  │      └── Payload (페이로드 - 클레임 데이터)
  └── Header (헤더 - 알고리즘, 토큰 타입)
```

**Header:**
```json
{
  "alg": "HS256",
  "typ": "JWT"
}
```

**Payload:**
```json
{
  "sub": "user@example.com",
  "iat": 1700000000,
  "exp": 1700003600,
  "role": "USER"
}
```

**Signature:**
```
HMACSHA256(
  base64UrlEncode(header) + "." + base64UrlEncode(payload),
  secret
)
```

### JWT 인증 흐름

```
1. 로그인 요청
   Client → POST /api/auth/login (username, password) → Server

2. JWT 발급
   Server → JWT 토큰 생성 → Client에게 반환

3. 인증된 요청
   Client → GET /api/users/me (Authorization: Bearer <JWT>) → Server

4. 토큰 검증
   Server → JWT 서명 검증 → 유효하면 요청 처리
```

### JwtService 핵심 메서드

```java
// 토큰 생성
public String generateToken(UserDetails userDetails) {
    return Jwts.builder()
        .subject(userDetails.getUsername())
        .issuedAt(new Date())
        .expiration(new Date(System.currentTimeMillis() + expiration))
        .signWith(getSigningKey())
        .compact();
}

// 토큰에서 사용자명 추출
public String extractUsername(String token) {
    return extractClaim(token, Claims::getSubject);
}

// 토큰 유효성 검증
public boolean isTokenValid(String token, UserDetails userDetails) {
    String username = extractUsername(token);
    return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
}
```

---

## 5. Role 기반 접근 제어

### Role (역할) 설계

```java
public enum Role {
    USER,   // 일반 사용자
    ADMIN   // 관리자
}
```

### URL 기반 접근 제어

```java
.authorizeHttpRequests(auth -> auth
    .requestMatchers("/api/auth/**").permitAll()        // 누구나 접근 가능
    .requestMatchers("/api/admin/**").hasRole("ADMIN")  // ADMIN만 접근
    .requestMatchers("/api/users/**").hasAnyRole("USER", "ADMIN")  // USER, ADMIN 접근
    .anyRequest().authenticated()                        // 인증된 사용자만
)
```

### 메서드 레벨 보안

```java
@EnableMethodSecurity  // SecurityConfig에 추가

// 컨트롤러에서 사용
@PreAuthorize("hasRole('ADMIN')")
@GetMapping("/api/admin/users")
public List<User> getAllUsers() { ... }

@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
@GetMapping("/api/users/me")
public User getCurrentUser() { ... }
```

### Spring Security의 Role 규칙

> Spring Security에서 `hasRole("ADMIN")`은 내부적으로 `ROLE_ADMIN` 권한을 확인합니다.
> `UserDetails.getAuthorities()`에서 `ROLE_` 접두어를 붙여 반환해야 합니다.

```java
@Override
public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
}
```

---

## 6. CORS 설정

### CORS (Cross-Origin Resource Sharing)란?

브라우저가 다른 출처(Origin)의 리소스에 접근할 때 적용되는 보안 정책입니다.
프론트엔드(React 등)와 백엔드(Spring Boot)가 다른 포트에서 실행될 때 필요합니다.

```
프론트엔드: http://localhost:3000  →  백엔드: http://localhost:8080
                                      ↑ 다른 출처이므로 CORS 필요
```

### Spring Security에서 CORS 설정

```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    return http
        .cors(cors -> cors.configurationSource(corsConfigurationSource()))
        // ... 기타 설정
        .build();
}

@Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration config = new CorsConfiguration();
    config.setAllowedOrigins(List.of("http://localhost:3000"));
    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
    config.setAllowedHeaders(List.of("*"));
    config.setAllowCredentials(true);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config);
    return source;
}
```

---

## 7. Docker로 실행하기

### 사전 준비

- Docker Desktop 설치
- 프로젝트 빌드: `./gradlew bootJar`

### Docker Compose로 실행

```bash
# 빌드 및 실행
docker-compose up --build

# 백그라운드 실행
docker-compose up -d --build

# 로그 확인
docker-compose logs -f app

# 중지
docker-compose down

# 볼륨 포함 중지 (DB 데이터 삭제)
docker-compose down -v
```

### API 테스트

```bash
# 1. 회원가입
curl -X POST http://localhost:8080/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{"username": "testuser", "password": "password123", "role": "USER"}'

# 2. 로그인 (JWT 토큰 발급)
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "testuser", "password": "password123"}'

# 응답: {"token": "eyJhbGciOiJIUzI1NiJ9..."}

# 3. 내 정보 조회 (JWT 토큰 필요)
curl http://localhost:8080/api/users/me \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..."

# 4. 관리자 전용 - 전체 사용자 조회
curl http://localhost:8080/api/admin/users \
  -H "Authorization: Bearer <ADMIN_TOKEN>"
```

---

## 프로젝트 구조

```
chapter07-spring-security/
├── build.gradle
├── settings.gradle
├── Dockerfile
├── docker-compose.yml
└── src/main/
    ├── java/com/edu/security/
    │   ├── Chapter07Application.java       # 메인 클래스
    │   ├── entity/
    │   │   ├── User.java                   # 사용자 엔티티 (UserDetails 구현)
    │   │   └── Role.java                   # 역할 열거형
    │   ├── repository/
    │   │   └── UserRepository.java         # 사용자 리포지토리
    │   ├── service/
    │   │   ├── JwtService.java             # JWT 토큰 생성/검증
    │   │   └── AuthService.java            # 인증 서비스 (회원가입/로그인)
    │   ├── config/
    │   │   ├── SecurityConfig.java         # Spring Security 설정
    │   │   └── JwtAuthenticationFilter.java # JWT 인증 필터
    │   ├── dto/
    │   │   ├── SignUpRequest.java           # 회원가입 요청
    │   │   ├── LoginRequest.java           # 로그인 요청
    │   │   └── AuthResponse.java           # 인증 응답 (JWT 토큰)
    │   └── controller/
    │       ├── AuthController.java         # 인증 API
    │       └── UserController.java         # 사용자 API
    └── resources/
        └── application.yml                 # 애플리케이션 설정
```

---

## 핵심 정리

| 개념 | 설명 |
|------|------|
| SecurityFilterChain | HTTP 요청에 대한 보안 필터 체인 구성 |
| BCryptPasswordEncoder | 비밀번호를 안전하게 해싱 (단방향) |
| JWT | 서버 세션 없이 토큰 기반 인증 수행 |
| Role | 사용자 역할에 따른 접근 권한 제어 |
| @PreAuthorize | 메서드 단위 권한 제어 (`@EnableMethodSecurity` 필요) |
| 401 vs 403 | 인증 실패=401(토큰 없음/무효), 인가 실패=403(권한 부족) |
| CORS | 다른 출처의 요청을 허용하기 위한 설정 |
| STATELESS | JWT 사용 시 서버 세션을 사용하지 않는 정책 |

---

## 401 vs 403 — 인증 실패와 인가 실패의 구분

REST API에서는 두 상황을 반드시 구분해야 한다.

| 상태 코드 | 의미 | 발생 상황 |
|-----------|------|-----------|
| **401 Unauthorized** | 인증(Authentication) 실패 | 토큰이 없거나 유효하지 않음 → "당신이 누구인지 모른다" |
| **403 Forbidden** | 인가(Authorization) 실패 | 인증은 됐지만 권한이 부족함 → "누구인지는 알지만 접근 권한이 없다" |

Spring Security는 기본적으로 두 경우 모두 403을 반환하므로, `SecurityConfig`에서
`AuthenticationEntryPoint`(→401)와 `AccessDeniedHandler`(→403)를 직접 등록해 구분했다.

```java
.exceptionHandling(ex -> ex
        .authenticationEntryPoint(authenticationEntryPoint())  // 미인증 → 401
        .accessDeniedHandler(accessDeniedHandler()))           // 권한부족 → 403
```

`src/test`의 `AuthIntegrationTest`가 이 동작(토큰 없음→401, USER의 ADMIN API 접근→403)을 검증한다.

---

## ⚠️ 보안 주의사항 (학습용 단순화)

이 챕터는 학습 목적상 일부를 단순화했다. **실무에서는 반드시 다음을 지켜야 한다.**

1. **비밀키(secret)를 코드/설정 파일에 평문으로 두지 말 것.**
   `application.yml`은 학습 편의를 위해 기본값을 담고 있지만 `${JWT_SECRET:...}` 형태로
   환경변수 주입을 우선하도록 해두었다. 운영에서는 환경변수·시크릿 매니저로 외부화한다.
   (참고: HS256 서명키는 최소 32바이트여야 하며, 코드에서는 secret 문자열의 UTF-8 바이트를 키로 사용한다.)

2. **CSRF를 비활성화한 이유.** CSRF 공격은 브라우저가 쿠키를 자동으로 실어 보내는 점을 악용한다.
   이 프로젝트는 인증 정보를 쿠키가 아니라 `Authorization: Bearer` **헤더**로 전송하고
   세션을 STATELESS로 두므로 CSRF에 취약하지 않다. 그래서 `csrf.disable()`이 안전하다.
   반대로 **쿠키/세션 기반 인증**을 쓴다면 CSRF 보호를 *끄면 안 된다*.

3. **회원가입 시 클라이언트가 역할(role)을 직접 고르게 하지 말 것.**
   현재 `SignUpRequest`에는 `role` 필드가 있어 누구나 `ADMIN`으로 가입할 수 있다(학습용 노출).
   실무에서는 가입 시 서버가 무조건 `USER`로 고정하고, 권한 상승은 관리자만 수행해야 한다.
   (Chapter 09에서는 이 점을 개선한다.)

4. **JWT 만료와 갱신.** stateless JWT는 만료 전 강제 무효화가 어렵다.
   실무에서는 짧은 액세스 토큰 + 리프레시 토큰(서버 저장/회전)으로 재발급하거나 블랙리스트로 무효화한다.

## 참고 자료

- [Spring Security 공식 문서](https://docs.spring.io/spring-security/reference/)
- [JWT 공식 사이트](https://jwt.io/)
- [JJWT 라이브러리](https://github.com/jwtk/jjwt)
- [BCrypt 위키피디아](https://en.wikipedia.org/wiki/Bcrypt)
