# 참고 답안: 게시글 좋아요(Like) 기능

> 이 폴더(`solutions/like-feature/`)는 **도전 과제(Day 37~38)의 참고 답안**입니다.
> 직접 구현해 본 뒤 막히거나 결과를 확인하고 싶을 때 비교용으로 읽으세요.
>
> **중요:** 이 파일들은 `src/main` 밖에 있어서 빌드/실행에 포함되지 않습니다.
> 실제로 동작시키려면 아래 "적용 방법"대로 `src/main/java/com/edu/board/...`의
> 알맞은 패키지 폴더로 복사해야 합니다.

## 포함 파일

| 파일 | 실제 위치 (적용 시) | 역할 |
|------|---------------------|------|
| `Like.java` | `entity/Like.java` | 좋아요 엔티티 |
| `LikeRepository.java` | `repository/LikeRepository.java` | 좋아요 저장소 |
| `LikeService.java` | `service/LikeService.java` | 좋아요 비즈니스 로직 |
| `LikeController.java` | `controller/LikeController.java` | 좋아요 REST API |

각 파일의 `package` 선언은 이미 실제 패키지(`com.edu.board.*`)로 맞춰져 있으므로,
해당 폴더로 복사하면 그대로 컴파일됩니다.

## 설계 설명

### 1. 엔티티 모델 — 별도 Like 엔티티
좋아요는 "어떤 User가 어떤 Post를 좋아한다"는 **다대다(N:M) 관계**입니다.
이를 다음과 같이 두 개의 다대일 관계로 풀어내는 연결 엔티티(`Like`)로 표현했습니다.

```
User (1) ─── (N) Like (N) ─── (1) Post
```

- `Comment`처럼 `@ManyToOne(fetch = LAZY)`로 `User`와 `Post`를 참조합니다.
- 기존 엔티티(`User`, `Post`)를 수정하지 않아도 동작합니다.
  (선택적으로 `Post`에 `@OneToMany(mappedBy = "post") List<Like> likes` 를 추가할 수 있지만 필수는 아닙니다.)

### 2. 중복 좋아요 방지 — 복합 유니크 제약 (핵심)
한 사용자가 같은 게시글에 좋아요를 여러 번 누르면 안 됩니다. 이를 **2단계**로 막습니다.

1. **애플리케이션 레벨** — `existsByUserIdAndPostId(...)`로 이미 눌렀는지 확인 후 저장
2. **DB 레벨** — `(user_id, post_id)` 복합 **유니크 제약**

```java
@Table(name = "post_like",
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "post_id"}))
```

> 왜 DB 제약도 필요한가? 두 요청이 거의 동시에 들어오면 둘 다 "아직 안 눌렀다"고
> 판단해 둘 다 INSERT를 시도하는 **경쟁 조건(race condition)**이 생길 수 있습니다.
> 유니크 제약은 이런 경우의 최후 방어선이며, 위반 시 `DataIntegrityViolationException`이
> 발생하므로 서비스에서 이를 잡아 멱등하게 처리합니다.

> 참고: `like`는 SQL 예약어라 테이블명을 `post_like`로 지정했습니다.

### 3. 토글 로직
좋아요/취소를 표현하는 두 가지 방식을 모두 제공합니다.

- **분리형**: `POST .../likes`(추가) + `DELETE .../likes`(취소) — REST 의미가 명확
- **토글형**: `toggle()` 한 메서드로 "있으면 삭제, 없으면 추가" (참고용 대안)

두 방식 모두 **멱등(idempotent)**하도록 만들었습니다.
(이미 누른 상태에서 또 추가해도, 안 누른 상태에서 취소해도 오류 없이 현재 개수를 반환)

### 4. 좋아요 개수 — count 쿼리
좋아요 행을 모두 메모리로 불러와 세지 않고, DB에서 바로 집계합니다.

```java
long countByPostId(Long postId);  // SELECT COUNT(*) FROM post_like WHERE post_id = ?
```

### 5. 인증 처리 — 기존 패턴 재사용
`SecurityConfig`는 `GET /api/posts/**`만 `permitAll`이고 나머지는 `authenticated()`입니다.
따라서 `POST`/`DELETE /api/posts/{id}/likes`는 **설정 변경 없이 자동으로 인증이 필요**하며,
토큰 없이 호출하면 (개선 후) **401 Unauthorized**를 받습니다.
컨트롤러는 다른 컨트롤러와 동일하게 `@AuthenticationPrincipal User user`로 로그인 사용자를 주입받습니다.

## 적용 방법

```bash
cd chapter09-final-project
cp solutions/like-feature/Like.java           src/main/java/com/edu/board/entity/
cp solutions/like-feature/LikeRepository.java  src/main/java/com/edu/board/repository/
cp solutions/like-feature/LikeService.java     src/main/java/com/edu/board/service/
cp solutions/like-feature/LikeController.java  src/main/java/com/edu/board/controller/
./gradlew build
```

## 동작 확인 (curl)

```bash
TOKEN="로그인으로_받은_토큰"

# 좋아요 추가  → {"likeCount":1}
curl -X POST http://localhost:8080/api/posts/1/likes -H "Authorization: Bearer $TOKEN"

# 같은 사용자가 또 추가해도 중복 없이 → {"likeCount":1}
curl -X POST http://localhost:8080/api/posts/1/likes -H "Authorization: Bearer $TOKEN"

# 좋아요 취소  → {"likeCount":0}
curl -X DELETE http://localhost:8080/api/posts/1/likes -H "Authorization: Bearer $TOKEN"

# 토큰 없이 호출 → 401 Unauthorized
curl -i -X POST http://localhost:8080/api/posts/1/likes
```

## 더 해보기
- `PostResponse`에 `likeCount` 필드를 추가해 게시글 상세 응답에 좋아요 수 포함하기
- 현재 사용자가 이 글에 좋아요를 눌렀는지(`liked: true/false`) 응답에 추가하기
- 좋아요 기능 통합 테스트 작성 (Day 38) — `PostApiIntegrationTest`를 참고
