# Chapter 06: Spring Data JPA with PostgreSQL Docker

> **🐳 실습 환경 — 이 장은 `spring-ch06-jpa` 컨테이너(+ 전용 DB `spring-ch06-postgres`)로 실습한다**
> ```bash
> cd spring/chapter06-spring-data-jpa && docker compose up --build
> ```
> 위 명령 한 방으로 앱+DB가 모두 뜬다 (공통 인프라 `spring-postgres` 불필요 — 동시에 띄우면 5432 포트 충돌 주의).
> API 호출은 `requests.http` 파일로 (VS Code REST Client). 컨테이너 상태 확인: `docker ps`

## 학습 목표
- JPA와 ORM 개념을 이해한다
- Spring Data JPA를 활용하여 데이터베이스 CRUD를 구현한다
- 쿼리 메서드, JPQL, Native Query를 사용할 수 있다
- 페이징과 정렬을 구현한다
- 엔티티 간 연관관계를 매핑하고 **N+1 문제를 `@EntityGraph`/`JOIN FETCH`로 해결**한다
- `@Transactional`과 변경 감지(dirty checking), 전역 예외 처리를 이해한다
- `@Transactional`의 **롤백 규칙·전파(REQUIRED/REQUIRES_NEW)·readOnly**를 데모 API로 직접 확인한다
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

## 9. @Transactional 깊게 보기 — 롤백·전파·readOnly

8장에서 `@Transactional`이 "트랜잭션 경계를 긋는다"는 것을 배웠다. 이 절에서는 그 애노테이션의
**옵션들이 실제로 무엇을 바꾸는지**를 실행 가능한 데모(`TransactionDemoService` / `TransactionDemoController`)로
직접 확인한다.

### 9.1 프록시가 트랜잭션을 처리하는 흐름

`@Transactional`은 마법이 아니라 **프록시**다(`ProxyRevealRunner`가 시작 로그로 폭로한다).
컨트롤러가 서비스 메서드를 호출하면, 사실은 Spring이 만든 프록시가 먼저 받는다.

```
 [컨트롤러]
     │  memberService.createMember(...) 호출
     ▼
 [프록시]  ← Spring이 만든 가짜 MemberService (클래스명에 $$SpringCGLIB$$)
     │ ① 트랜잭션 시작 (커넥션 풀에서 커넥션 획득, autocommit 해제)
     ▼
 [진짜 서비스 메서드 실행]
     │
     ├─ 정상 종료          → ② 프록시가 commit  (변경 감지 flush 포함)
     ├─ RuntimeException   → ② 프록시가 rollback (기본 규칙)
     └─ checked Exception  → ② 프록시가 commit(!) (기본 규칙 — 아래 표)
     ▼
 [커넥션 반납]
```

핵심: **트랜잭션을 열고/커밋하고/롤백하는 코드는 내 메서드가 아니라 프록시에 있다.**
그래서 "프록시를 거치지 않는 호출"에는 트랜잭션이 적용되지 않는다(→ 9.4 self-invocation).

### 9.2 롤백 규칙 — "예외가 나면 무조건 롤백"이 아니다

| 메서드에서 빠져나온 것 | 기본 동작 | 바꾸는 방법 |
|---|---|---|
| 정상 리턴 | **commit** | - |
| `RuntimeException` (unchecked) | **rollback** | `noRollbackFor = ...`로 커밋 가능 |
| `Error` | **rollback** | - |
| checked `Exception` | **commit** (!) | `rollbackFor = 예외.class`로 롤백 지정 |

checked 예외는 "호출자가 잡아서 복구할 수 있는 예외"로 간주해 커밋한다는 것이 스프링의 철학이다.
하지만 실무에서는 이 기본값이 함정이 되기 쉬워, `rollbackFor = Exception.class`를 명시하는 팀도 많다.

**데모로 확인** (예외를 던지기 *전에* 회원을 INSERT하므로, 회원 수 변화가 곧 커밋/롤백의 증거다):

| 엔드포인트 | 서비스 메서드 | 예상 결과 |
|---|---|---|
| `POST /api/tx-demo/rollback-runtime` | `saveThenThrowRuntime` | 회원 수 그대로 (롤백) |
| `POST /api/tx-demo/commit-checked` | `saveThenThrowChecked` | 회원 수 **+1** (예외가 났는데 커밋!) |
| `POST /api/tx-demo/rollback-for` | `saveThenThrowCheckedWithRollbackFor` | 회원 수 그대로 (rollbackFor로 롤백) |

> 참고: `Member`의 PK 전략이 IDENTITY라서 `save()` 시점에 INSERT가 **즉시 실행**된다.
> 콘솔에 insert 문이 찍혔는데도 결과적으로 저장되지 않는 것 — 그것이 롤백이다.

### 9.3 전파(Propagation) — REQUIRED vs REQUIRES_NEW

트랜잭션이 있는 메서드가 **또 다른 트랜잭션 메서드를 호출**하면 어떻게 될까?
그 규칙이 전파(propagation) 속성이다.

```
 REQUIRED (기본값): "있으면 합류, 없으면 새로 시작"
 ┌──────────────────── 물리 트랜잭션 1개 ────────────────────┐
 │ 바깥: INSERT A ──> 내부(REQUIRED): INSERT B, 예외!         │
 │                     └─> 공유 트랜잭션에 rollback-only 마크 │
 │ 바깥이 예외 catch → 정상 종료 → 커밋 시도                  │
 │   → rollback-only 발견 → 전체 롤백 + UnexpectedRollback-  │
 │     Exception!  (A도 B도 저장 안 됨)                       │
 └───────────────────────────────────────────────────────────┘

 REQUIRES_NEW: "무조건 새 트랜잭션 (바깥은 잠시 보류)"
 ┌──── 트랜잭션 A ────┐   ┌──── 트랜잭션 B (새로) ────┐
 │ 바깥: INSERT A     │   │ 내부: INSERT B, 예외!      │
 │ (보류되었다 재개)   │   │  → B만 롤백               │
 │ 예외 catch → 커밋  │   └───────────────────────────┘
 │ → A는 저장됨!      │
 └────────────────────┘
```

REQUIRED에서 기억할 것: **바깥에서 내부 예외를 catch해도 소용없다.** 내부의 `@Transactional`
프록시가 예외를 통과시키는 순간 이미 공유 트랜잭션에 rollback-only 마크를 찍었기 때문이다.
바깥은 "복구했다"고 착각하지만, 커밋 순간 `UnexpectedRollbackException`으로 배신당한다.

**데모로 확인:**

| 엔드포인트 | 예상 결과 |
|---|---|
| `POST /api/tx-demo/propagation-required` | 회원 수 그대로 + `UnexpectedRollbackException` (전체 롤백) |
| `POST /api/tx-demo/propagation-requires-new` | 회원 수 **+1** — 바깥 회원만 저장 (내부만 롤백) |

**예상 로그 해석** — 두 데모 모두 `[TX-DEMO]` 로그에 현재 트랜잭션 이름을 찍는다
(`TransactionSynchronizationManager.getCurrentTransactionName()`):

```
# propagation-required: 바깥/내부의 트랜잭션 이름이 "같다" = 같은 트랜잭션에 합류
[TX-DEMO] 바깥(outer) - REQUIRED 실습 | 트랜잭션 이름=...TransactionDemoService.outerWithRequiredInner | 활성=true ...
[TX-DEMO] 내부(REQUIRED)             | 트랜잭션 이름=...TransactionDemoService.outerWithRequiredInner | 활성=true ...

# propagation-requires-new: 내부의 트랜잭션 이름이 "다르다" = 새 트랜잭션이 시작됨
[TX-DEMO] 바깥(outer) - REQUIRES_NEW 실습 | 트랜잭션 이름=...TransactionDemoService.outerWithRequiresNewInner ...
[TX-DEMO] 내부(REQUIRES_NEW)             | 트랜잭션 이름=...TransactionInnerService.saveInnerThenFailRequiresNew ...
```

> REQUIRES_NEW는 바깥 커넥션을 보류한 채 **커넥션을 하나 더** 쓴다. 남용하면 커넥션 풀이
> 빨리 고갈되므로, "본 작업이 실패해도 반드시 남겨야 하는 이력/감사 로그" 같은 곳에 선별적으로 쓴다.

### 9.4 self-invocation 함정 — 왜 내부 서비스를 별도 빈으로 분리했나

전파 데모의 "내부 작업"은 `TransactionDemoService` 안의 메서드가 아니라
**별도의 빈 `TransactionInnerService`**에 있다. 이유는 9.1의 프록시 구조 때문이다.

```java
// ❌ 잘못된 예: 같은 클래스 안에서 자기 메서드 호출 (self-invocation)
@Service
public class BadService {
    @Transactional
    public void outer() {
        this.inner();   // ← this(원본 객체)를 직접 호출: 프록시를 안 거친다!
    }
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void inner() { ... }   // REQUIRES_NEW가 조용히 무시됨 (에러도 안 남!)
}
```

`this.inner()`는 프록시가 아니라 원본 객체의 메서드를 바로 부른다. 트랜잭션 코드는 프록시에
있으므로 `REQUIRES_NEW`는 적용되지 않고, 그냥 바깥 트랜잭션 안에서 실행된다.
**해결책: 트랜잭션 속성이 달라야 하는 메서드는 별도 빈으로 분리해 주입받아 호출한다.**
(`TransactionInnerService`의 클래스 주석에 상세 설명이 있다.)

### 9.5 readOnly=true — 변경 감지가 flush되지 않는다

8장에서 배운 변경 감지(dirty checking)는 "커밋 시점의 flush"에서 일어난다.
`readOnly = true`면 Hibernate가 세션의 FlushMode를 MANUAL로 바꾸고 스냅샷 비교/flush를
생략한다. 그래서 **영속 엔티티의 값을 바꿔도 UPDATE가 나가지 않는다.**

| 엔드포인트 | 예상 결과 |
|---|---|
| `POST /api/tx-demo/read-only` | `setName()` 호출에도 UPDATE 없음, 재조회 시 이름 "원본이름" 그대로 |

콘솔에서 확인할 것: `tryUpdateInReadOnly` 실행 구간에 **UPDATE 문이 없다**
(`MemberService.updateMember`를 호출했을 때와 비교해 보라). `[TX-DEMO]` 로그에는 `readOnly=true`가 찍힌다.

### 9.6 데모 실행 방법

```bash
# 1. PostgreSQL 실행
docker compose up postgres -d

# 2. 애플리케이션 실행
./gradlew bootRun

# 3. 시나리오 하나씩 호출 (requests.http 14~19번, 또는 curl)
curl -X POST http://localhost:8080/api/tx-demo/rollback-runtime
curl -X POST http://localhost:8080/api/tx-demo/commit-checked
curl -X POST http://localhost:8080/api/tx-demo/rollback-for
curl -X POST http://localhost:8080/api/tx-demo/propagation-required
curl -X POST http://localhost:8080/api/tx-demo/propagation-requires-new
curl -X POST http://localhost:8080/api/tx-demo/read-only
```

각 응답의 `details`(단계별 설명)와 `memberCountBefore/After`를 읽고,
**서버 콘솔의 `[TX-DEMO]` 로그 + Hibernate SQL 로그**와 대조하는 것이 핵심 학습 활동이다.

### 확인문제

**1. `@Transactional` 메서드가 회원을 저장한 뒤 `IOException`(checked)을 던졌다. 회원은 저장될까? 이유와 함께, 저장되지 않게 만드는 방법을 쓰라.**

<details>
<summary>정답</summary>

**저장된다(커밋).** 스프링의 기본 롤백 규칙은 `RuntimeException`과 `Error`에만 롤백하고, checked 예외는 "호출자가 복구할 수 있는 예외"로 보아 커밋한다. 롤백시키려면 `@Transactional(rollbackFor = IOException.class)`처럼 `rollbackFor`를 지정한다. (데모: `commit-checked` vs `rollback-for`)
</details>

**2. REQUIRED로 합류한 내부 메서드가 예외를 던졌고, 바깥 메서드가 그 예외를 try-catch로 잡은 뒤 정상 종료했다. 바깥의 INSERT는 커밋될까?**

<details>
<summary>정답</summary>

**커밋되지 않는다.** 내부 프록시가 예외를 통과시키는 순간 공유 트랜잭션에 **rollback-only 마크**를 찍는다. 바깥이 예외를 잡아도 마크는 지워지지 않으므로, 바깥이 커밋을 시도하는 순간 전체가 롤백되고 `UnexpectedRollbackException`이 발생한다. 내부 실패에도 바깥을 살리려면 내부를 `REQUIRES_NEW`(별도 트랜잭션)로 분리해야 한다. (데모: `propagation-required` vs `propagation-requires-new`)
</details>

**3. 다음 코드에서 `inner()`의 `REQUIRES_NEW`는 적용될까? 이유를 프록시 관점에서 설명하라.**

```java
@Service
public class MyService {
    @Transactional
    public void outer() { this.inner(); }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void inner() { ... }
}
```

<details>
<summary>정답</summary>

**적용되지 않는다.** 트랜잭션 시작/커밋/롤백 코드는 원본 객체가 아니라 **프록시**에 있다. `this.inner()`는 프록시를 거치지 않고 원본 객체의 메서드를 직접 호출(self-invocation)하므로 `REQUIRES_NEW`가 조용히 무시되고 바깥 트랜잭션에 그대로 합류한다. 해결책은 `inner()`를 **별도 빈으로 분리**해 주입받아 호출하는 것이다. (이 챕터의 `TransactionInnerService`가 그 예다.)
</details>

---

## 10. Docker로 PostgreSQL 실행하기

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
│   │   ├── MemberController.java
│   │   └── TransactionDemoController.java  # @Transactional 실습 API (/api/tx-demo/**)
│   ├── dto/
│   │   ├── MemberRequest.java
│   │   ├── MemberResponse.java
│   │   ├── TeamRequest.java
│   │   ├── TeamResponse.java
│   │   ├── ErrorResponse.java        # 공통 에러 응답 DTO
│   │   └── TxDemoResponse.java       # 트랜잭션 데모 응답 (시나리오 설명 + 회원 수 변화)
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
│       ├── MemberService.java
│       ├── TransactionDemoService.java    # 롤백 규칙·전파·readOnly 실습
│       └── TransactionInnerService.java   # 전파 실습용 내부 빈 (self-invocation 함정 설명)
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
| 롤백 규칙 | 기본은 RuntimeException/Error만 롤백, checked 예외는 커밋 → `rollbackFor`로 변경 |
| 전파(Propagation) | REQUIRED(합류, 내부 실패=전체 롤백) vs REQUIRES_NEW(새 트랜잭션, 내부만 롤백) |
| self-invocation | `this.method()`는 프록시를 우회 → 트랜잭션 속성 무시. 별도 빈으로 분리해 해결 |
| readOnly=true | flush 생략 → 변경 감지 UPDATE가 나가지 않음, 읽기 전용 메서드 최적화 |
| 예외 처리 | `@RestControllerAdvice`로 전역 처리, 의미에 맞는 HTTP 상태(404/409/400) 반환 |
| Docker Compose | PostgreSQL + Spring 앱을 함께 실행 |
