# Chapter 06: Spring Data JPA with PostgreSQL Docker

## 학습 목표
- JPA와 ORM 개념을 이해한다
- Spring Data JPA를 활용하여 데이터베이스 CRUD를 구현한다
- 쿼리 메서드, JPQL, Native Query를 사용할 수 있다
- 페이징과 정렬을 구현한다
- 엔티티 간 연관관계를 매핑하고 **N+1 문제를 `@EntityGraph`/`JOIN FETCH`로 해결**한다
- `@Transactional`과 변경 감지(dirty checking), 전역 예외 처리를 이해한다
- Docker로 PostgreSQL을 실행하고 연동한다

---

## 1. JPA란? ORM 개념

### ORM (Object-Relational Mapping)
ORM은 객체(Object)와 관계형 데이터베이스(Relational Database)를 자동으로 매핑해주는 기술입니다.

```
Java 객체 (Entity)  <──ORM──>  데이터베이스 테이블
   필드(field)       <──────>   컬럼(column)
   인스턴스           <──────>   행(row)
```

### JPA (Java Persistence API)
- Java에서 ORM을 사용하기 위한 **표준 인터페이스(스펙)**입니다
- JPA는 인터페이스일 뿐, 실제 구현체는 **Hibernate**, EclipseLink 등이 있습니다
- Spring Data JPA는 JPA를 더 편리하게 사용할 수 있도록 감싸놓은 Spring의 추상화 계층입니다

```
Spring Data JPA  →  JPA (표준 스펙)  →  Hibernate (구현체)  →  JDBC  →  DB
```

### JPA를 사용하는 이유
1. **SQL을 직접 작성하지 않아도 된다** - 메서드 호출만으로 CRUD 가능
2. **데이터베이스 종류에 독립적** - Dialect만 바꾸면 DB 변경 가능
3. **객체 중심 개발** - 테이블이 아닌 객체를 중심으로 설계
4. **생산성 향상** - 반복적인 SQL 작성 불필요

---

## 2. Entity 매핑

### 주요 어노테이션

| 어노테이션 | 설명 |
|-----------|------|
| `@Entity` | 이 클래스가 JPA 엔티티임을 선언 |
| `@Table` | 매핑할 테이블명 지정 (생략 시 클래스명 사용) |
| `@Id` | 기본 키(Primary Key) 필드 지정 |
| `@GeneratedValue` | 기본 키 생성 전략 지정 |
| `@Column` | 컬럼 매핑 (이름, 제약조건 등) |
| `@CreatedDate` | 엔티티 생성 시각 자동 기록 |
| `@EntityListeners` | 엔티티 이벤트 리스너 등록 |

### @GeneratedValue 전략

| 전략 | 설명 |
|------|------|
| `IDENTITY` | 키 생성을 DB에 위임 (PostgreSQL의 SERIAL/IDENTITY 컬럼). 이 챕터의 코드가 사용하는 전략 |
| `SEQUENCE` | DB 시퀀스 오브젝트 사용. PostgreSQL/Oracle처럼 시퀀스를 지원하는 DB에서 권장 |
| `TABLE` | 키 생성 전용 테이블 사용 (모든 DB에서 동작하나 성능은 낮음) |
| `AUTO` | DB 방언(Dialect)에 따라 자동 선택. Hibernate 6 + PostgreSQL에서는 `SEQUENCE`가 선택됨 |

> 참고: `SEQUENCE`는 "PostgreSQL의 기본값"이 아니라 **JPA가 선택하는 키 생성 전략** 중 하나다.
> `AUTO`로 두면 Hibernate 6가 PostgreSQL에 대해 `SEQUENCE`를 고르지만, 이 챕터는 명시적으로 `IDENTITY`를 쓴다.

### 엔티티 예시

```java
@Entity
@Table(name = "member")
@EntityListeners(AuditingEntityListener.class)
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;
}
```

---

## 3. JpaRepository 인터페이스

Spring Data JPA는 `JpaRepository`를 상속하는 것만으로 기본 CRUD 메서드를 제공합니다.

```java
public interface MemberRepository extends JpaRepository<Member, Long> {
    // 기본 제공 메서드: save(), findById(), findAll(), delete(), count() 등
}
```

### JpaRepository 계층 구조

```
Repository (마커 인터페이스)
  └── CrudRepository (기본 CRUD)
        └── ListCrudRepository
              └── PagingAndSortingRepository (페이징 + 정렬)
                    └── JpaRepository (JPA 특화: flush, batch 등)
```

### 기본 제공 메서드

| 메서드 | 설명 |
|--------|------|
| `save(entity)` | 저장 (insert 또는 update) |
| `findById(id)` | ID로 조회 (Optional 반환) |
| `findAll()` | 전체 조회 |
| `findAll(Pageable)` | 페이징 조회 |
| `deleteById(id)` | ID로 삭제 |
| `count()` | 전체 개수 조회 |
| `existsById(id)` | 존재 여부 확인 |

---

## 4. 쿼리 메서드 (Query Methods)

메서드 이름 규칙에 따라 JPA가 자동으로 쿼리를 생성합니다.

### 기본 문법

```java
// findBy + 필드명
Optional<Member> findByEmail(String email);

// findBy + 필드명 + Containing (LIKE '%keyword%')
List<Member> findByNameContaining(String name);

// findBy + 연관엔티티 + 필드명
List<Member> findByTeamName(String teamName);
```

### 주요 키워드

| 키워드 | 예시 | 생성 SQL |
|--------|------|----------|
| `And` | `findByNameAndEmail(...)` | `WHERE name = ? AND email = ?` |
| `Or` | `findByNameOrEmail(...)` | `WHERE name = ? OR email = ?` |
| `Between` | `findByCreatedAtBetween(...)` | `WHERE created_at BETWEEN ? AND ?` |
| `LessThan` | `findByIdLessThan(...)` | `WHERE id < ?` |
| `GreaterThan` | `findByIdGreaterThan(...)` | `WHERE id > ?` |
| `Like` | `findByNameLike(...)` | `WHERE name LIKE ?` |
| `Containing` | `findByNameContaining(...)` | `WHERE name LIKE '%?%'` |
| `StartingWith` | `findByNameStartingWith(...)` | `WHERE name LIKE '?%'` |
| `EndingWith` | `findByNameEndingWith(...)` | `WHERE name LIKE '%?'` |
| `OrderBy` | `findByTeamOrderByNameAsc(...)` | `ORDER BY name ASC` |
| `IsNull` | `findByTeamIsNull()` | `WHERE team IS NULL` |
| `IsNotNull` | `findByTeamIsNotNull()` | `WHERE team IS NOT NULL` |
| `In` | `findByNameIn(List)` | `WHERE name IN (?, ?, ...)` |
| `countBy` | `countByTeamName(...)` | `SELECT COUNT(*) WHERE ...` |

---

## 5. @Query (JPQL, Native Query)

### JPQL (Java Persistence Query Language)
엔티티 객체를 대상으로 하는 쿼리 언어입니다. SQL과 유사하지만 **테이블이 아닌 엔티티**를 대상으로 합니다.

```java
// JPQL - 엔티티명(Member)과 필드명(name)을 사용
@Query("SELECT m FROM Member m WHERE m.name LIKE %:keyword%")
List<Member> searchByName(@Param("keyword") String keyword);
```

### Native Query
데이터베이스에 직접 SQL을 실행합니다. DB 특화 기능이 필요할 때 사용합니다.

```java
// Native Query - 실제 테이블명(member)과 컬럼명을 사용
@Query(value = "SELECT * FROM member WHERE email LIKE %:domain", nativeQuery = true)
List<Member> findByEmailDomain(@Param("domain") String domain);
```

### JPQL vs Native Query

| 구분 | JPQL | Native Query |
|------|------|-------------|
| 대상 | 엔티티 객체 | 데이터베이스 테이블 |
| 문법 | 엔티티명, 필드명 사용 | 테이블명, 컬럼명 사용 |
| DB 독립성 | O (DB 변경 시 수정 불필요) | X (DB 종속적) |
| 사용 시기 | 일반적인 경우 | DB 특화 기능 필요 시 |

---

## 6. 페이징과 정렬

### Pageable 사용

```java
// Repository
Page<Member> findByNameContaining(String name, Pageable pageable);

// Service에서 호출
Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());
Page<Member> page = memberRepository.findByNameContaining("홍", pageable);
```

### Page 객체 주요 메서드

| 메서드 | 설명 |
|--------|------|
| `getContent()` | 현재 페이지 데이터 목록 |
| `getTotalElements()` | 전체 데이터 개수 |
| `getTotalPages()` | 전체 페이지 수 |
| `getNumber()` | 현재 페이지 번호 (0부터) |
| `getSize()` | 페이지 크기 |
| `hasNext()` | 다음 페이지 존재 여부 |
| `hasPrevious()` | 이전 페이지 존재 여부 |

### REST API에서 페이징

```
GET /api/members?page=0&size=10&sort=createdAt,desc
```

---

## 7. 연관관계 매핑

### @ManyToOne / @OneToMany

```
Member (다)  ──────>  Team (일)
 team_id (FK)         id (PK)
```

```java
// Member.java - 연관관계의 주인 (FK를 가진 쪽)
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "team_id")
private Team team;

// Team.java - 양방향 매핑
@OneToMany(mappedBy = "team", cascade = CascadeType.ALL)
private List<Member> members = new ArrayList<>();
```

### 주요 개념
- **연관관계의 주인**: 외래 키(FK)를 관리하는 쪽 (보통 @ManyToOne 쪽)
- **mappedBy**: 주인이 아닌 쪽에서 주인 필드를 지정
- **FetchType.LAZY**: 지연 로딩 (연관 엔티티를 실제 사용할 때 쿼리 실행)
- **FetchType.EAGER**: 즉시 로딩 (연관 엔티티를 함께 조회) - 성능 문제 주의
- **cascade**: 영속성 전이 (부모 저장/삭제 시 자식도 함께 처리)

### 연관관계 편의 메서드

```java
// Team.java
public void addMember(Member member) {
    members.add(member);
    member.setTeam(this);
}
```

### N+1 문제와 해결 (★ 실무 핵심)

연관관계에서 가장 자주 만나는 성능 함정이 **N+1 문제**다.

- `Member.team`은 `LAZY`로 설정되어 있다. 회원 목록(N건)을 조회한 뒤 각 회원의 팀 이름
  (`member.getTeam().getName()`)에 접근하면, 팀을 가져오는 쿼리가 **회원 수만큼 추가**로 나간다.
- 즉, 목록 1번 + 각 행마다 1번 = **1 + N 쿼리**. 데이터가 많을수록 치명적이다.

`application.yml`에 `show-sql: true`가 켜져 있으니, **콘솔 로그에서 쿼리가 몇 번 나가는지 직접 확인**해 보자.

**해결책 1 — `@EntityGraph` (이 챕터의 `MemberRepository.findAll(Pageable)`):**

```java
@Override
@EntityGraph(attributePaths = "team")   // team을 LEFT JOIN으로 함께 로딩
Page<Member> findAll(Pageable pageable);
```

**해결책 2 — JPQL `JOIN FETCH` (`MemberRepository.findAllWithTeam`, `TeamRepository.findAllWithMembers`):**

```java
@Query("SELECT m FROM Member m JOIN FETCH m.team")
List<Member> findAllWithTeam();

// 컬렉션(1:N)을 FETCH JOIN할 때는 중복 행이 생기므로 DISTINCT를 붙인다
@Query("SELECT DISTINCT t FROM Team t LEFT JOIN FETCH t.members")
List<Team> findAllWithMembers();
```

> 팁: `member→team`처럼 **다대일(ToOne)** 관계는 `@EntityGraph` + 페이징을 함께 써도 안전하다.
> 반면 **일대다(컬렉션) FETCH JOIN + 페이징**은 메모리에서 페이징하게 되어(`HHH000104` 경고) 위험하므로 주의한다.

---

## 8. 트랜잭션과 예외 처리

### @Transactional

`MemberService`는 클래스 레벨에 `@Transactional(readOnly = true)`를, 쓰기 메서드(`create`/`update`/`delete`)에는
`@Transactional`을 붙였다.

- **읽기 전용 트랜잭션**(`readOnly = true`)은 변경 감지(dirty checking)를 생략해 약간의 성능 이점이 있다.
- **변경 감지(Dirty Checking)**: `updateMember`는 `save()`를 호출하지 않는다. 트랜잭션 안에서 조회한
  엔티티(영속 상태)의 필드를 바꾸면, 커밋 시점에 JPA가 변경을 감지해 자동으로 UPDATE를 실행한다.
- **영속성 컨텍스트 상태**: 트랜잭션 내에서 조회한 엔티티는 *영속(managed)* 상태이고, 트랜잭션이 끝나면
  *준영속(detached)* 상태가 된다. LAZY 연관을 트랜잭션 밖에서 접근하면 `LazyInitializationException`이 난다.

### 일관된 예외 처리와 HTTP 상태 코드

이 챕터는 컨트롤러마다 `@ExceptionHandler`를 두는 대신, **전역 예외 처리기**
(`exception/GlobalExceptionHandler`, `@RestControllerAdvice`)로 모든 예외를 한 곳에서 처리한다 (Chapter 05와 동일 패턴).
핵심은 **상황에 맞는 정확한 HTTP 상태 코드**를 돌려주는 것이다.

| 상황 | 예외 | HTTP 상태 |
|------|------|-----------|
| 없는 회원/팀 조회·수정·삭제 | `ResourceNotFoundException` | **404 Not Found** |
| 이메일/팀 이름 중복 | `DuplicateResourceException` | **409 Conflict** |
| `@Valid` 검증 실패 | `MethodArgumentNotValidException` | **400 Bad Request** |
| 그 외 예기치 못한 오류 | `Exception` | **500 Internal Server Error** |

> 모든 상황에 `IllegalArgumentException`(→400)을 던지면 "없는 데이터(404)"와 "잘못된 입력(400)"을 구분할 수 없다.
> 의미에 맞는 예외 타입을 만들어 상태 코드를 분리하는 것이 REST API 설계의 기본이다.

---

## 9. Docker로 PostgreSQL 실행하기

### Docker Compose로 실행

```bash
# PostgreSQL만 실행
docker compose up postgres -d

# 전체 실행 (PostgreSQL + Spring 앱)
docker compose up -d

# 로그 확인
docker compose logs -f

# 종료
docker compose down

# 볼륨 포함 종료 (데이터 삭제)
docker compose down -v
```

### PostgreSQL 접속 확인

```bash
# Docker 컨테이너 내부에서 psql 실행
docker compose exec postgres psql -U edu -d edu_spring

# 테이블 목록 확인
\dt

# 데이터 조회
SELECT * FROM member;

# 종료
\q
```

---

## 실행 방법

### 1. PostgreSQL 실행
```bash
docker compose up postgres -d
```

### 2. 애플리케이션 실행
```bash
./gradlew bootRun
```

### 3. API 테스트

```bash
# 팀 생성
curl -X POST http://localhost:8080/api/teams \
  -H "Content-Type: application/json" \
  -d '{"name": "개발팀"}'

# 회원 등록
curl -X POST http://localhost:8080/api/members \
  -H "Content-Type: application/json" \
  -d '{"name": "홍길동", "email": "hong@example.com", "teamId": 1}'

# 회원 전체 조회 (페이징)
curl "http://localhost:8080/api/members?page=0&size=10&sort=createdAt,desc"

# 이름으로 검색
curl "http://localhost:8080/api/members/search?keyword=홍&page=0&size=10"

# 회원 단건 조회
curl http://localhost:8080/api/members/1

# 회원 수정
curl -X PUT http://localhost:8080/api/members/1 \
  -H "Content-Type: application/json" \
  -d '{"name": "홍길동2", "email": "hong2@example.com", "teamId": 1}'

# 회원 삭제
curl -X DELETE http://localhost:8080/api/members/1

# 팀별 회원 조회
curl http://localhost:8080/api/teams/1/members

# 이메일 도메인으로 검색
curl "http://localhost:8080/api/members/search/email?domain=example.com"
```

---

## 프로젝트 구조

```
chapter06-spring-data-jpa/
├── build.gradle
├── settings.gradle
├── docker-compose.yml
├── Dockerfile
├── gradle/wrapper/
│   └── gradle-wrapper.properties
├── src/main/java/com/edu/jpa/
│   ├── Chapter06Application.java
│   ├── controller/
│   │   └── MemberController.java
│   ├── dto/
│   │   ├── MemberRequest.java
│   │   ├── MemberResponse.java
│   │   ├── TeamRequest.java
│   │   ├── TeamResponse.java
│   │   └── ErrorResponse.java        # 공통 에러 응답 DTO
│   ├── entity/
│   │   ├── Member.java
│   │   └── Team.java
│   ├── exception/                    # 예외 + 전역 처리기
│   │   ├── ResourceNotFoundException.java   # → 404
│   │   ├── DuplicateResourceException.java  # → 409
│   │   └── GlobalExceptionHandler.java      # @RestControllerAdvice
│   ├── repository/
│   │   ├── MemberRepository.java     # @EntityGraph / JOIN FETCH (N+1 방지)
│   │   └── TeamRepository.java       # JOIN FETCH (N+1 방지)
│   └── service/
│       └── MemberService.java
└── src/main/resources/
    └── application.yml
```

---

## 핵심 정리

| 개념 | 설명 |
|------|------|
| JPA | Java 표준 ORM 스펙, Hibernate가 대표 구현체 |
| Entity | DB 테이블과 매핑되는 Java 클래스 |
| JpaRepository | 기본 CRUD + 페이징 + 정렬을 제공하는 인터페이스 |
| 쿼리 메서드 | 메서드 이름으로 쿼리 자동 생성 |
| JPQL | 엔티티 기반 쿼리 언어 |
| Native Query | DB에 직접 SQL 실행 |
| 페이징 | `Pageable`과 `Page`로 구현 |
| 연관관계 | `@ManyToOne`, `@OneToMany`로 엔티티 간 관계 매핑 |
| N+1 문제 | LAZY 연관 반복 접근 시 1+N 쿼리 발생 → `@EntityGraph` / `JOIN FETCH`로 해결 |
| @Transactional | 트랜잭션 경계 설정, 변경 감지(dirty checking)로 UPDATE 자동 반영 |
| 예외 처리 | `@RestControllerAdvice`로 전역 처리, 의미에 맞는 HTTP 상태(404/409/400) 반환 |
| Docker Compose | PostgreSQL + Spring 앱을 함께 실행 |
