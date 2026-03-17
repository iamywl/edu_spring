# Chapter 09: Final Project - 게시판 REST API

## 프로젝트 개요

이 프로젝트는 Chapter 01~08에서 배운 모든 내용을 종합하여 만드는 **게시판(Board) REST API** 입니다.
Spring Boot, Spring Security(JWT), Spring Data JPA, PostgreSQL, Docker Compose를 활용하여
실무 수준의 백엔드 애플리케이션을 구축합니다.

## 사용 기술

| 기술 | 버전 | 용도 |
|------|------|------|
| Java | 21 | 언어 |
| Spring Boot | 3.3.0 | 프레임워크 |
| Spring Security | - | 인증/인가 (JWT) |
| Spring Data JPA | - | ORM |
| PostgreSQL | 16 | 데이터베이스 |
| Docker Compose | - | 컨테이너 오케스트레이션 |
| Testcontainers | 1.19.8 | 통합 테스트 |

## 기능 목록

### 1. 회원 (Auth)
- 회원가입 (POST /api/auth/signup)
- 로그인 (POST /api/auth/login) - JWT 토큰 발급

### 2. 게시글 (Post)
- 게시글 목록 조회 (GET /api/posts) - 페이징, 검색 지원
- 게시글 상세 조회 (GET /api/posts/{id}) - 조회수 증가
- 게시글 작성 (POST /api/posts) - 인증 필요
- 게시글 수정 (PUT /api/posts/{id}) - 작성자만 가능
- 게시글 삭제 (DELETE /api/posts/{id}) - 작성자 또는 관리자

### 3. 댓글 (Comment)
- 댓글 목록 조회 (GET /api/posts/{postId}/comments)
- 댓글 작성 (POST /api/posts/{postId}/comments) - 인증 필요
- 댓글 삭제 (DELETE /api/posts/{postId}/comments/{commentId}) - 작성자 또는 관리자

---

## API 명세표

### Auth API

| Method | URL | 설명 | 인증 |
|--------|-----|------|------|
| POST | `/api/auth/signup` | 회원가입 | X |
| POST | `/api/auth/login` | 로그인 | X |

#### 회원가입 요청
```json
{
  "username": "testuser",
  "password": "password123"
}
```

#### 로그인 응답
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "username": "testuser"
}
```

### Post API

| Method | URL | 설명 | 인증 |
|--------|-----|------|------|
| GET | `/api/posts?page=0&size=10&keyword=검색어` | 게시글 목록 (페이징/검색) | X |
| GET | `/api/posts/{id}` | 게시글 상세 조회 | X |
| POST | `/api/posts` | 게시글 작성 | O |
| PUT | `/api/posts/{id}` | 게시글 수정 | O (작성자) |
| DELETE | `/api/posts/{id}` | 게시글 삭제 | O (작성자/관리자) |

#### 게시글 작성 요청
```json
{
  "title": "게시글 제목",
  "content": "게시글 내용입니다."
}
```

#### 게시글 상세 응답
```json
{
  "id": 1,
  "title": "게시글 제목",
  "content": "게시글 내용입니다.",
  "author": "testuser",
  "viewCount": 5,
  "commentCount": 3,
  "createdAt": "2024-01-01T12:00:00"
}
```

### Comment API

| Method | URL | 설명 | 인증 |
|--------|-----|------|------|
| GET | `/api/posts/{postId}/comments` | 댓글 목록 조회 | X |
| POST | `/api/posts/{postId}/comments` | 댓글 작성 | O |
| DELETE | `/api/posts/{postId}/comments/{commentId}` | 댓글 삭제 | O (작성자/관리자) |

#### 댓글 작성 요청
```json
{
  "content": "댓글 내용입니다."
}
```

---

## Docker Compose로 전체 실행하기

### 사전 준비
- Docker Desktop 설치
- Java 21 (로컬 빌드 시)

### 실행 방법

```bash
# 1. 프로젝트 디렉토리로 이동
cd chapter09-final-project

# 2. Docker Compose로 전체 실행 (빌드 포함)
docker compose up --build

# 3. 백그라운드 실행
docker compose up --build -d

# 4. 로그 확인
docker compose logs -f app

# 5. 종료
docker compose down

# 6. 볼륨까지 삭제 (DB 데이터 초기화)
docker compose down -v
```

### 실행되는 서비스

| 서비스 | 포트 | 설명 |
|--------|------|------|
| app | 8080 | Spring Boot 애플리케이션 |
| postgres | 5432 | PostgreSQL 데이터베이스 |
| redis | 6379 | Redis (추후 캐시 확장용) |
| adminer | 8081 | DB 관리 웹 UI |

---

## 테스트 방법 (curl 예제)

### 1. 회원가입
```bash
curl -X POST http://localhost:8080/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{"username": "testuser", "password": "password123"}'
```

### 2. 로그인
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "testuser", "password": "password123"}'
```

응답에서 받은 토큰을 환경변수에 저장:
```bash
TOKEN="여기에_토큰_붙여넣기"
```

### 3. 게시글 작성
```bash
curl -X POST http://localhost:8080/api/posts \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"title": "첫 번째 게시글", "content": "안녕하세요! 첫 게시글입니다."}'
```

### 4. 게시글 목록 조회 (페이징)
```bash
# 기본 조회 (page=0, size=10)
curl http://localhost:8080/api/posts

# 페이지 지정
curl "http://localhost:8080/api/posts?page=0&size=5"

# 키워드 검색
curl "http://localhost:8080/api/posts?keyword=첫+번째"
```

### 5. 게시글 상세 조회
```bash
curl http://localhost:8080/api/posts/1
```

### 6. 게시글 수정
```bash
curl -X PUT http://localhost:8080/api/posts/1 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"title": "수정된 제목", "content": "수정된 내용입니다."}'
```

### 7. 게시글 삭제
```bash
curl -X DELETE http://localhost:8080/api/posts/1 \
  -H "Authorization: Bearer $TOKEN"
```

### 8. 댓글 작성
```bash
curl -X POST http://localhost:8080/api/posts/1/comments \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"content": "좋은 글이네요!"}'
```

### 9. 댓글 목록 조회
```bash
curl http://localhost:8080/api/posts/1/comments
```

### 10. 댓글 삭제
```bash
curl -X DELETE http://localhost:8080/api/posts/1/comments/1 \
  -H "Authorization: Bearer $TOKEN"
```

---

## 프로젝트 구조

```
chapter09-final-project/
├── build.gradle
├── settings.gradle
├── Dockerfile
├── docker-compose.yml
└── src/
    ├── main/
    │   ├── java/com/edu/board/
    │   │   ├── Chapter09Application.java
    │   │   ├── entity/
    │   │   │   ├── User.java
    │   │   │   ├── Role.java
    │   │   │   ├── Post.java
    │   │   │   └── Comment.java
    │   │   ├── repository/
    │   │   │   ├── UserRepository.java
    │   │   │   ├── PostRepository.java
    │   │   │   └── CommentRepository.java
    │   │   ├── dto/
    │   │   │   ├── SignUpRequest.java
    │   │   │   ├── LoginRequest.java
    │   │   │   ├── AuthResponse.java
    │   │   │   ├── PostRequest.java
    │   │   │   ├── PostResponse.java
    │   │   │   ├── PostListResponse.java
    │   │   │   ├── CommentRequest.java
    │   │   │   ├── CommentResponse.java
    │   │   │   └── PageResponse.java
    │   │   ├── service/
    │   │   │   ├── JwtService.java
    │   │   │   ├── AuthService.java
    │   │   │   ├── PostService.java
    │   │   │   └── CommentService.java
    │   │   ├── config/
    │   │   │   ├── SecurityConfig.java
    │   │   │   └── JwtAuthenticationFilter.java
    │   │   ├── controller/
    │   │   │   ├── AuthController.java
    │   │   │   ├── PostController.java
    │   │   │   └── CommentController.java
    │   │   └── exception/
    │   │       ├── GlobalExceptionHandler.java
    │   │       ├── ResourceNotFoundException.java
    │   │       └── UnauthorizedException.java
    │   └── resources/
    │       └── application.yml
    └── test/
        └── java/com/edu/board/
            └── PostApiIntegrationTest.java
```

## 학습 포인트

1. **Spring Security + JWT** - 토큰 기반 인증/인가 구현
2. **JPA 연관관계** - @ManyToOne, @OneToMany 매핑
3. **DTO 패턴** - Record를 활용한 요청/응답 분리
4. **페이징 & 검색** - Spring Data JPA의 Pageable 활용
5. **예외 처리** - @RestControllerAdvice를 통한 전역 예외 처리
6. **Docker Compose** - 멀티 컨테이너 환경 구성
7. **Testcontainers** - 실제 DB를 사용한 통합 테스트
