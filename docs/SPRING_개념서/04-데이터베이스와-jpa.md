# Chapter 4: 데이터베이스와 JPA

> "프로그램이 종료되면 사라지는 데이터는 데이터가 아니다. 그것은 잠깐 켜진 촛불일 뿐이다."

애플리케이션은 결국 **데이터를 다루는 일**이다. 회원을 등록하고, 주문을 저장하고, 게시글을 조회한다. 그런데 서버 프로세스의 메모리는 **휘발성**이다. 서버를 재시작하면 메모리 위의 `List<Member>`는 흔적도 없이 사라진다. 그래서 우리는 데이터를 **디스크에 영구히 남기는 장치**, 즉 데이터베이스(DB)가 필요하다.

문제는 여기서부터다. 자바 프로그램은 **객체(object)**로 세상을 표현한다. `Member`라는 객체가 있고, 그 안에 `Team`이라는 또 다른 객체가 참조로 매달려 있다. 반면 관계형 데이터베이스(RDB)는 **테이블(table)**, 즉 행(row)과 열(column)로 이루어진 납작한 표로 세상을 표현한다. 객체에는 "참조"가 있지만 테이블에는 "외래 키 값"이 있을 뿐이고, 객체에는 "상속"이 있지만 테이블에는 그런 개념이 없다.

이 챕터는 "**어떻게 하면 이 두 세계를 편하게 오갈 수 있는가**"에 대한 이야기다. 그리고 그 답으로 등장한 것이 ORM(Object-Relational Mapping)이고, 자바 진영의 표준이 JPA다. 하지만 도구를 익히기 전에, **먼저 도구가 없던 시절의 고통을 알아야 한다.** 고통을 모르면 해결책의 고마움도, 그 한계도 이해할 수 없다. 그래서 이 챕터는 "JPA 쓰는 법"이 아니라 "JPA는 왜 이렇게 생겨먹었는가"에서 출발한다.

---

## 4.1 왜 ORM이 필요한가 — JDBC 직접 작성의 고통

### JDBC만 있던 시절

자바가 DB에 접근하는 가장 밑바닥 기술은 **JDBC(Java Database Connectivity)**다. JDBC는 "자바에서 SQL을 문자열로 던지고, 그 결과를 한 줄씩 꺼내 읽는" API다. 아주 강력하지만, 아주 원시적이다. 회원 한 명을 ID로 조회하는 코드를 순수 JDBC로 써 보면 그 원시성이 드러난다.

```java
// 순수 JDBC로 회원 1명 조회하기 — "이 모든 게 고작 SELECT 한 줄을 위해서다"
public Member findById(Long id) {
    String sql = "SELECT id, name, email FROM member WHERE id = ?";
    Connection conn = null;
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    try {
        conn = dataSource.getConnection();        // 1. 커넥션 얻기
        pstmt = conn.prepareStatement(sql);       // 2. SQL 준비
        pstmt.setLong(1, id);                     // 3. 파라미터 바인딩
        rs = pstmt.executeQuery();                // 4. 실행

        if (rs.next()) {                          // 5. 결과 한 줄씩 읽기
            Member member = new Member();
            member.setId(rs.getLong("id"));       //    컬럼 → 필드 수동 복사
            member.setName(rs.getString("name")); //    (컬럼 하나 늘 때마다 여기도 한 줄)
            member.setEmail(rs.getString("email"));
            return member;
        }
        return null;
    } catch (SQLException e) {
        throw new RuntimeException(e);            // 6. 체크 예외 처리
    } finally {
        // 7. 자원 반납 — 순서 거꾸로, 각각 try-catch (안 하면 커넥션 누수!)
        try { if (rs != null) rs.close(); } catch (SQLException ignored) {}
        try { if (pstmt != null) pstmt.close(); } catch (SQLException ignored) {}
        try { if (conn != null) conn.close(); } catch (SQLException ignored) {}
    }
}
```

고작 회원 한 명을 읽는 데 30줄이 필요하다. 그리고 이 코드에는 네 가지 만성 질환이 있다.

1. **반복(boilerplate)**: 커넥션 얻고 → 문 준비하고 → 실행하고 → 결과 읽고 → 자원 닫는 이 패턴이 CRUD마다, 테이블마다 똑같이 반복된다. `Team`을 조회하는 코드도 위와 완전히 판박이다.
2. **실수하기 쉬움**: `finally`에서 `close()`를 빠뜨리면 커넥션이 반납되지 않아 **커넥션 풀이 고갈**된다(§2.7의 HikariCP를 떠올리자). 컬럼 이름 오타(`"emial"`)는 컴파일러가 못 잡는다 — 런타임에야 터진다.
3. **DB 종속**: SQL 방언(dialect)이 DB마다 다르다. 페이징만 해도 MySQL은 `LIMIT`, Oracle은 `ROWNUM`, SQL Server는 `TOP`이다. DB를 바꾸면 SQL을 전부 다시 써야 한다.
4. **객체-테이블 불일치의 수작업 봉합**: `rs.getString("name")` → `member.setName(...)`. 이 "컬럼을 필드에 옮겨 담는" 작업을 사람이 손으로 한다. 컬럼이 20개면 20줄, 연관 객체까지 있으면 지옥이 된다.

### ORM: 자동 매핑이라는 발상

ORM(Object-Relational Mapping)은 이 고통에 대한 답이다. "객체와 테이블 사이의 변환을 **사람이 하지 말고 프레임워크가 자동으로 하게 하자**"는 것이다. 같은 조회를 JPA로 쓰면 이렇게 줄어든다.

```java
// JPA로 회원 1명 조회 — 매핑도, 자원 관리도, 예외 변환도 프레임워크가 한다
Member member = entityManager.find(Member.class, id);
```

단 한 줄이다. 커넥션 관리, SQL 생성, 파라미터 바인딩, `ResultSet` → 객체 매핑, 자원 반납이 전부 프레임워크 안으로 들어갔다. 우리는 "무엇을(what)" 원하는지만 말하고, "어떻게(how)" 가져올지는 프레임워크에 맡긴다.

```
┌──────────────┐      번역(매핑)      ┌───────────────────┐
│  자바 객체    │  <───────────────>  │  RDB 테이블       │
│  Member 객체  │       ORM            │  member 테이블    │
│  .name        │  ─── 컬럼 매핑 ───>  │  name 컬럼        │
│  .team(참조)  │  ─── FK 매핑 ────>   │  team_id 컬럼     │
└──────────────┘                      └───────────────────┘
        │                                       │
   객체 지향 언어                          SQL / 집합 지향
        └──────── 서로 말이 다르다 ─────────────┘
```

### 통역사 비유

ORM은 서로 다른 언어를 쓰는 두 사람 사이의 **통역사**다. 자바 개발자는 "객체어"를 쓰고, 데이터베이스는 "SQL어(집합·테이블어)"를 쓴다. 예전에는 개발자가 두 언어를 다 배워서 직접 오가며 번역했다(=JDBC). ORM은 그 사이에 유능한 통역사를 앉힌 것이다. 개발자는 객체어로만 말하면, 통역사가 알아서 SQL로 옮겨 DB에 전달하고, DB가 돌려준 표를 다시 객체로 번역해 건네준다.

물론 통역사가 있다고 원어를 몰라도 되는 건 아니다. **통역사가 이상한 문장(비효율적 SQL)을 만들어내면 알아채야 하기 때문**이다. 이것이 뒤에 나올 N+1 문제의 씨앗이다.

### JPA는 스펙, Hibernate는 구현체

여기서 자주 헷갈리는 용어를 정리하자.

- **JPA(Jakarta Persistence API)**: **표준 명세(스펙)**다. "ORM은 이런 인터페이스와 애너테이션을 제공해야 한다"고 규정한 **약속 문서**일 뿐, 그 자체로는 동작하는 코드가 아니다. `@Entity`, `@Id`, `EntityManager` 같은 인터페이스와 애너테이션의 규격을 정의한다.
- **Hibernate**: 그 스펙을 실제로 **구현한 라이브러리(구현체)**다. 우리가 `entityManager.find()`를 호출하면 실제로 SQL을 만들고 DB에 던지는 일은 Hibernate가 한다. Spring Boot는 기본 JPA 구현체로 Hibernate를 쓴다.

```
   JPA (스펙 = 콘센트 규격)          Hibernate / EclipseLink (구현 = 실제 발전소)
   ┌───────────────────┐            ┌────────────────────────────┐
   │ @Entity, @Id      │  <──구현──  │ 실제 SQL 생성, 캐시,        │
   │ EntityManager     │            │ 지연 로딩, 방언 처리 …      │
   │ (인터페이스만)     │            └────────────────────────────┘
   └───────────────────┘
```

이 구조 덕분에 **우리 코드는 JPA(스펙)에만 의존**하고, 구현체를 Hibernate에서 다른 것으로 바꿔도 코드는 그대로 둘 수 있다. §1의 "인터페이스에 의존하라(DIP)"가 프레임워크 차원에서 실현된 예다. 이 챕터에서 `EntityManager`나 `@Entity`라고 하면 JPA(스펙)를, "실제로 이렇게 동작한다"고 하면 Hibernate(구현)를 가리킨다고 보면 된다.

---

## 4.2 Entity — 객체와 테이블을 잇는 지도

### @Entity: "이 클래스는 테이블이다"

ORM이 자동으로 매핑하려면 "어느 클래스가 어느 테이블에 대응하는지"를 알아야 한다. 그 지도를 그리는 것이 **애너테이션**이다. `chapter06-spring-data-jpa`의 `Member` 엔티티를 그대로 보자.

```java
@Entity                                        // 이 클래스는 JPA가 관리하는 엔티티다
@Table(name = "member")                        // member 테이블에 매핑
public class Member {

    @Id                                        // 기본 키(PK)
    @GeneratedValue(strategy = GenerationType.IDENTITY) // PK 자동 생성 전략
    private Long id;

    @Column(nullable = false, length = 50)     // name 컬럼: NOT NULL, VARCHAR(50)
    private String name;

    @Column(nullable = false, unique = true)   // email 컬럼: NOT NULL + UNIQUE
    private String email;

    @ManyToOne(fetch = FetchType.LAZY)         // 여러 회원이 하나의 팀에 (N:1)
    @JoinColumn(name = "team_id")              // 외래 키 컬럼명
    private Team team;

    protected Member() {}                      // JPA용 기본 생성자 (뒤에서 설명)

    public Member(String name, String email) {
        this.name = name;
        this.email = email;
    }
    // getter / setter 생략
}
```

각 애너테이션의 뜻:

| 애너테이션 | 의미 | 없으면? |
|---|---|---|
| `@Entity` | 이 클래스는 JPA 관리 대상 | JPA가 무시. 테이블 매핑 안 됨 |
| `@Table(name=...)` | 매핑할 테이블명 지정 | 클래스명(소문자화)이 테이블명이 됨 |
| `@Id` | 기본 키 필드 | 엔티티마다 반드시 하나 필요(없으면 에러) |
| `@GeneratedValue` | PK 자동 생성 전략 | PK를 직접 지정해야 함 |
| `@Column` | 컬럼 세부 제약(길이/NULL/유니크) | 필드명=컬럼명, 기본 제약으로 매핑 |
| `@ManyToOne`/`@JoinColumn` | 연관관계 + FK 컬럼 | 참조를 테이블로 못 옮김 |

이 매핑을 그림으로 보면:

```
   자바 객체 (Member)                        RDB 테이블 (member)
 ┌────────────────────────┐              ┌──────┬────────┬──────────────┬─────────┐
 │ id      : Long   ───────┼──@Id────────>│ id   │ name   │ email        │ team_id │
 │ name    : String ───────┼──@Column───> ├──────┼────────┼──────────────┼─────────┤
 │ email   : String ───────┼──@Column───> │ 1    │ 김철수 │ a@ex.com     │ 10      │
 │ team    : Team(참조) ────┼─@JoinColumn─>│ 2    │ 이영희 │ b@ex.com     │ 10      │
 └────────────────────────┘   (team_id)   └──────┴────────┴──────────────┴─────────┘
       "참조"로 팀을 가리킴                        "외래 키 값(10)"으로 팀을 가리킴
```

핵심 통찰 하나: 객체는 `team`이라는 **참조**로 팀을 붙들지만, 테이블은 `team_id`라는 **정수 값**으로 팀을 가리킨다. `@JoinColumn(name = "team_id")`이 바로 이 "참조 ↔ FK 값" 번역을 담당한다.

### 왜 기본 생성자가 필요한가 — 리플렉션

`Member`에는 `protected Member() {}` 라는 **인자 없는 기본 생성자**가 있다. JPA 스펙은 이걸 **필수**로 요구한다. 왜일까?

DB에서 데이터를 읽어와 객체로 만들 때, JPA(Hibernate)는 우리가 만든 `new Member(name, email)` 생성자를 호출하지 않는다. 그러면 어떤 생성자를 써야 할지, 어떤 값을 넣어야 할지 알 수 없기 때문이다. 대신 **리플렉션(Reflection)**을 쓴다.

> **리플렉션**이란 프로그램이 실행 중에 클래스의 구조(필드·메서드·생성자)를 들여다보고 조작하는 자바 기능이다. "일단 빈 객체를 하나 만든 뒤, 필드에 값을 하나씩 강제로 꽂아 넣는" 방식이다.

즉 Hibernate는 이렇게 한다.

```
1. new Member()   ← 기본 생성자로 "빈 껍데기" 객체 생성
2. 리플렉션으로 member.id, member.name, member.email 필드에 DB 값을 직접 주입
```

1단계를 하려면 **인자 없는 생성자**가 반드시 있어야 한다. 그래서 JPA는 기본 생성자를 요구한다. `public`이 아니라 `protected`인 이유는, 개발자가 실수로 `new Member()`로 빈 회원을 만들지 못하게 막으면서도 JPA(같은 패키지/프록시)는 접근할 수 있게 하는 절충이다.

### PK 생성 전략: IDENTITY vs SEQUENCE

`@GeneratedValue(strategy = ...)`는 PK를 누가 생성할지를 정한다. 대표 두 전략:

- **IDENTITY**: **DB가** PK를 만든다. MySQL의 `AUTO_INCREMENT`, PostgreSQL의 `SERIAL`이 이것이다. `INSERT`가 실제로 실행되어야 그 결과로 PK 값을 알 수 있다. `Member`가 이 전략을 쓴다(PostgreSQL 기준).
- **SEQUENCE**: **DB의 시퀀스 오브젝트**에서 미리 번호를 발급받는다(Oracle, PostgreSQL 지원). `INSERT` 전에 `nextval`로 값을 먼저 얻을 수 있다.

```
IDENTITY:  save() → 즉시 INSERT 실행 → DB가 채번한 PK 회수
           (INSERT를 뒤로 미룰 수 없음 → 쓰기 지연 최적화 제약)

SEQUENCE:  save() → 시퀀스에서 번호 먼저 받음 → INSERT는 나중에 모아서
           (여러 INSERT를 배치로 묶기 유리)
```

교육 프로젝트는 PostgreSQL과 `IDENTITY`를 쓴다. 대량 삽입 성능이 중요하다면 `SEQUENCE`가 유리하지만, 여기서는 단순함을 택했다.

### 영속성 컨텍스트 — 1차 캐시와 변경 감지

이제 JPA의 심장을 볼 차례다. JPA는 엔티티를 다룰 때 **영속성 컨텍스트(Persistence Context)**라는 "1차 캐시 겸 작업 공간"을 둔다. `EntityManager` 뒤에 숨어 있는, 트랜잭션 동안 살아 있는 임시 저장소다.

**1차 캐시**: 한 트랜잭션 안에서 같은 엔티티를 두 번 조회하면, 두 번째는 DB에 가지 않고 캐시에서 돌려준다.

```java
Member m1 = em.find(Member.class, 1L);  // SELECT 실행 → 1차 캐시에 저장
Member m2 = em.find(Member.class, 1L);  // 캐시에서 반환 → SELECT 없음!
System.out.println(m1 == m2);           // true (같은 인스턴스 보장)
```

**변경 감지(Dirty Checking)**: 여기가 초심자가 가장 놀라는 지점이다. 영속 상태의 엔티티 값을 바꾸면, `save()`나 `update()`를 호출하지 않아도 트랜잭션이 끝날 때 **자동으로 UPDATE 쿼리가 나간다.**

```java
@Transactional
public void changeName(Long id, String newName) {
    Member member = memberRepository.findById(id).orElseThrow();
    member.setName(newName);   // 값만 바꿈. save() 호출 안 함!
}   // ← 트랜잭션 커밋 시점에 JPA가 알아서 UPDATE member SET name=? 실행
```

어떻게 이게 가능할까? 영속성 컨텍스트는 엔티티를 처음 읽을 때 그 **원본 스냅샷(snapshot)**을 따로 저장해 둔다. 트랜잭션이 커밋될 때(=플러시 시점) 현재 엔티티와 스냅샷을 비교해서, 달라진 필드가 있으면 자동으로 UPDATE 문을 만든다.

```
  [읽기 시점]                         [커밋 시점 = flush]
  member(name="김철수")               member(name="김영수")  ← 사용자가 바꿈
       │                                    │
   스냅샷 저장                          스냅샷과 비교
  snapshot(name="김철수")  ────────>  다르다! → UPDATE member SET name='김영수'
```

**워드(Word) 변경 추적 비유**: 마이크로소프트 워드에서 "변경 내용 추적" 기능을 켜면, 원본 대비 어디가 바뀌었는지 자동으로 표시된다. 우리는 문서를 그냥 고칠 뿐인데 워드가 알아서 diff를 잡는다. 영속성 컨텍스트의 Dirty Checking이 정확히 이것이다. "원본(스냅샷) 대비 지금 무엇이 달라졌는가"를 프레임워크가 추적하고, 그 diff만큼 UPDATE로 반영한다.

> 이 "1차 캐시 + 변경 감지"가 왜 트랜잭션 단위인지, 그 트랜잭션 동안 DB 커넥션을 계속 붙잡고 있다는 사실은 §4.5와 §4.8에서 다시 만난다.

**실행 연결**: `chapter06-spring-data-jpa`의 `MemberService`는 클래스에 `@Transactional(readOnly=true)`를, 쓰기 메서드에는 `@Transactional`을 붙여서 이 영속성 컨텍스트의 경계를 명시적으로 그어 둔다.

---

## 4.3 JpaRepository — 인터페이스만으로 구현체가 생긴다

### 구현 없는 인터페이스가 어떻게 동작하나

`chapter06`의 `MemberRepository`를 다시 보자.

```java
public interface MemberRepository extends JpaRepository<Member, Long> {
    // ... 메서드 선언만 있고 구현이 하나도 없다
}
```

**인터페이스**다. `implements MemberRepository`를 하는 클래스가 프로젝트 어디에도 없다. 그런데 컨트롤러에 주입해서 `memberRepository.save(member)`를 호출하면 멀쩡히 동작한다. 어떻게?

Spring Data JPA가 애플리케이션 시작 시점에 이 인터페이스를 스캔해서, **런타임에 구현체(프록시 객체)를 동적으로 만들어** 스프링 빈으로 등록해 주기 때문이다. `JpaRepository<Member, Long>`를 상속하는 것만으로 `save()`, `findById()`, `findAll()`, `delete()`, `count()` 같은 기본 CRUD가 전부 딸려 온다.

```
  개발자가 쓴 것            Spring Data JPA가 실행 시 만드는 것
 ┌──────────────────┐     ┌───────────────────────────────────┐
 │ interface        │     │ class $Proxy123 implements ...     │
 │ MemberRepository │ ──> │  save()   → em.persist/merge       │
 │ extends          │     │  findById → em.find                │
 │ JpaRepository    │     │  findByEmail → SQL 자동 생성        │
 └──────────────────┘     └───────────────────────────────────┘
     (구현 없음)                 (프록시 = 자동 생성 구현체)
```

**실행 연결**: `chapter06`의 `ProxyRevealRunner`는 시작 시 `memberService.getClass().getName()`을 찍어서, 우리가 주입받은 객체가 원본이 아니라 스프링이 감싼 프록시(클래스명에 `$$`가 들어감)임을 눈으로 확인시켜 준다. §3의 AOP 프록시와 같은 원리다.

### 쿼리 메서드 — 메서드 이름이 곧 쿼리

Spring Data JPA의 가장 마법 같은 기능이다. **메서드 이름을 규칙에 맞게 지으면, 그 이름을 파싱해서 SQL을 자동으로 만든다.** `MemberRepository`의 실제 예:

```java
// 이메일로 조회 → SELECT ... WHERE email = ?
Optional<Member> findByEmail(String email);

// 이름에 문자열 포함 → SELECT ... WHERE name LIKE '%?%'
List<Member> findByNameContaining(String name);

// 연관 엔티티(team)의 name으로 조회 → JOIN team WHERE team.name = ?
List<Member> findByTeamName(String teamName);
```

이름의 각 조각이 쿼리 문법으로 번역된다.

```
 find  ByEmail        → SELECT ... WHERE email = ?
 find  ByName Containing → WHERE name LIKE %?%
 find  ByTeam Name     → JOIN team ... WHERE team.name = ?
  │      │      │
 동작   조건    연산자/중첩 프로퍼티
```

주요 키워드: `And`, `Or`, `Between`, `LessThan`, `GreaterThan`, `Like`, `Containing`, `StartingWith`, `In`, `OrderBy...Desc`, `IgnoreCase` 등. 반환 타입도 의미가 있다 — 결과가 없을 수 있으면 `Optional<Member>`, 여러 건이면 `List<Member>`.

### @Query — 직접 쓰는 JPQL과 Native SQL

이름 규칙이 감당 못 하는 복잡한 쿼리는 직접 쓴다. 두 종류가 있다.

```java
// JPQL: "테이블"이 아니라 "엔티티"를 대상으로 하는 객체 지향 쿼리
// FROM Member(엔티티명), m.name(필드명) — DB 테이블/컬럼명이 아님에 주목!
@Query("SELECT m FROM Member m WHERE m.name LIKE %:keyword%")
List<Member> searchByName(@Param("keyword") String keyword);

// Native Query: 진짜 SQL. 실제 테이블명 member, 실제 컬럼명 email 사용
@Query(value = "SELECT * FROM member WHERE email LIKE %:domain", nativeQuery = true)
List<Member> findByEmailDomain(@Param("domain") String domain);
```

- **JPQL(Java Persistence Query Language)**: SQL과 비슷하게 생겼지만 **엔티티와 필드**를 대상으로 한다(`FROM Member`, `m.name`). Hibernate가 이를 각 DB 방언의 실제 SQL로 번역해 준다. → DB 독립적.
- **Native Query**: 데이터베이스의 **진짜 SQL** 그대로다(`FROM member`, `email`). 특정 DB 전용 기능을 써야 할 때 쓰지만, DB 종속성이 생긴다.

### 페이징 — 왜 전체 조회는 안 되나 (도서관 비유)

회원이 100만 명인데 `findAll()`로 전부 메모리에 올리면 어떻게 될까? OutOfMemoryError로 서버가 죽는다. 목록은 항상 **잘라서(paging)** 가져와야 한다.

```java
// Pageable을 파라미터로 받으면 자동으로 LIMIT/OFFSET이 붙는다
@EntityGraph(attributePaths = "team")   // team까지 함께(N+1 방지, §4.4)
Page<Member> findByNameContaining(String name, Pageable pageable);
```

호출 측:

```java
Pageable pageable = PageRequest.of(0, 20, Sort.by("createdAt").descending());
Page<Member> page = memberRepository.findByNameContaining("김", pageable);
// → SELECT ... WHERE name LIKE '%김%' ORDER BY created_at DESC LIMIT 20 OFFSET 0
page.getTotalElements();  // 전체 개수(별도 count 쿼리)
page.getContent();        // 이번 페이지 20건
```

**도서관 비유**: 도서관에서 "역사책 전부 주세요"라고 하면 사서가 수레 100대를 끌고 올 수 없다. "역사책을 최신순으로 앞에서 20권만 주세요"라고 해야 한다. `Pageable`은 바로 이 "몇 번째부터(offset), 몇 권(limit), 어떤 순서(sort)로"를 담은 주문서다. `Page` 객체는 그 20권과 함께 "전체 몇 권 중 이거예요(totalElements)"라는 정보까지 돌려준다.

---

## 4.4 연관관계 — 참조를 외래 키로 옮기기

### 누가 연관관계의 주인인가 — 외래 키를 가진 쪽

객체 세계에서 `Member`는 `team` 참조를 갖고, `Team`은 `List<Member>` 컬렉션을 갖는다. 양쪽이 서로를 가리킨다(양방향). 그런데 테이블 세계에는 `member.team_id`라는 외래 키가 **한 곳에만** 있다. 이 불일치를 어떻게 다룰까?

JPA는 "**연관관계의 주인(owner)**"이라는 개념으로 해결한다. **외래 키를 실제로 가진(=관리하는) 쪽이 주인**이다. 주인만 외래 키 값을 INSERT/UPDATE할 수 있고, 반대쪽은 "읽기 전용 거울"이다.

```java
// Member: 외래 키 team_id를 가진 쪽 = 주인
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "team_id")
private Team team;
```

```java
// Team: 외래 키가 없는 쪽 = 주인이 아님. mappedBy로 "저쪽이 주인"이라 선언
@OneToMany(mappedBy = "team", cascade = CascadeType.ALL, orphanRemoval = true)
private List<Member> members = new ArrayList<>();
```

`mappedBy = "team"`은 "이 컬렉션은 `Member` 엔티티의 `team` 필드에 의해 매핑된다(=저쪽이 주인이다)"는 뜻이다.

```
   객체 세계 (양방향 참조)              테이블 세계 (FK는 한 곳뿐)
 ┌──────────┐        ┌────────┐      ┌───────────┐      ┌────────┐
 │ Member   │──team─>│ Team   │      │ member    │      │ team   │
 │          │<members│        │      │  team_id ─┼─FK──>│  id    │
 └──────────┘        └────────┘      └───────────┘      └────────┘
   양쪽이 서로 참조                    FK는 member 쪽에만 존재
                                       → member(=@ManyToOne)가 주인
```

**아파트 열쇠 비유**: 부부가 아파트 하나를 공유한다. 하지만 정문 열쇠(=외래 키)는 실물로 **한 개**뿐이다. "이 열쇠는 남편이 관리한다"고 정하면, 문을 잠그고 여는(=DB에 FK를 쓰는) 권한은 남편에게만 있다. 아내는 "우리 집 열쇠는 남편이 갖고 있다"고 알기만 할 뿐(=`mappedBy`), 자기가 열쇠 정보를 바꿔도 실제 자물쇠(DB)에는 반영되지 않는다. 주인이 아닌 쪽에서 아무리 값을 바꿔도 DB에 안 나가는 이유가 이것이다.

### FetchType — LAZY vs EAGER, 왜 LAZY가 기본인가

연관 엔티티를 **언제** 불러올지를 정하는 것이 `FetchType`이다.

- **EAGER(즉시 로딩)**: 회원을 조회하는 순간 팀도 **무조건 함께** 가져온다.
- **LAZY(지연 로딩)**: 회원만 먼저 가져오고, 팀은 **실제로 `member.getTeam()`을 호출하는 순간** 그때 별도 쿼리로 가져온다.

```java
@ManyToOne(fetch = FetchType.LAZY)   // Member는 LAZY를 명시
private Team team;
```

LAZY는 어떻게 "나중에" 가져올까? Hibernate는 `member.getTeam()`이 반환하는 자리에 **진짜 Team이 아니라 프록시(가짜 Team)**를 꽂아 둔다. 이 프록시의 메서드(`getName()` 등)를 처음 호출하는 순간 DB 쿼리가 나가서 진짜 데이터로 채워진다.

```
 LAZY:  Member 조회 ──> team 자리에 [프록시] 꽂아둠
                         │
                    member.getTeam().getName() 호출
                         │
                    이때! SELECT * FROM team WHERE id=?
```

**`@ManyToOne`의 기본이 EAGER인데도 코드에서 LAZY로 명시하는 이유**, 그리고 실무에서 항상 LAZY를 권하는 이유: EAGER는 "필요 없을 때도 무조건 조인/추가 쿼리"를 하기 때문이다. 회원 이름만 필요한 화면에서도 팀을 매번 끌고 오면 낭비다. 게다가 EAGER는 다음에 볼 N+1 문제를 **예측 불가능하게** 터뜨린다. 그래서 **모든 연관관계는 LAZY로 두고, 필요할 때만 명시적으로 함께 조회**하는 것이 원칙이다.

### N+1 문제 — 지연 로딩의 함정

LAZY는 좋지만, 잘못 쓰면 유명한 **N+1 문제**를 만든다. 시나리오는 게시글-작성자 예로 보자(Post @ManyToOne User).

```java
// Post 엔티티 (작성자 User를 LAZY로 참조)
@ManyToOne(fetch = FetchType.LAZY)
private User author;
```

```java
// 게시글 100건을 조회하고, 각 게시글의 작성자 이름을 화면에 뿌린다
List<Post> posts = postRepository.findAll();       // ① SELECT * FROM post (1번)
for (Post post : posts) {
    System.out.println(post.getAuthor().getName()); // ② 작성자 접근 → 여기서 매번 쿼리!
}
```

무슨 일이 일어날까?

```
 ① SELECT * FROM post;                        ← 쿼리 1번 (post 100건 반환)
 ② post 1번의 author 접근 → SELECT * FROM user WHERE id=1
    post 2번의 author 접근 → SELECT * FROM user WHERE id=2
    post 3번의 author 접근 → SELECT * FROM user WHERE id=3
       ... (100번 반복) ...
    post 100번의 author 접근 → SELECT * FROM user WHERE id=100
 ────────────────────────────────────────────────────────
 총 쿼리 = 1 (post 목록) + N (작성자 각각) = 1 + 100 = 101번!
```

목록 조회 1번 + 각 요소마다 연관 조회 N번 = **1 + N번**. 이것이 N+1이다. **원인은 LAZY 자체가 아니라, "목록을 한 방에 가져와 놓고, 연관 데이터는 요소마다 하나씩 뒤늦게 가져오기" 때문**이다. 100건이면 101번, 10000건이면 10001번 — 트래픽이 조금만 늘어도 DB가 비명을 지른다.

### JOIN FETCH — 한 번에 가져오기 (편의점 비유)

해결책은 "**연관 데이터를 처음부터 조인해서 한 방에 가져오기**"다. JPQL의 `JOIN FETCH`가 그 역할을 한다.

```java
// N+1 해결: 게시글과 작성자를 조인해서 한 번의 쿼리로 로딩
@Query("SELECT p FROM Post p JOIN FETCH p.author")
List<Post> findAllWithAuthor();
// → SELECT p.*, u.* FROM post p INNER JOIN user u ON p.author_id = u.id  (쿼리 1번!)
```

`chapter06`의 `MemberRepository`도 똑같은 두 가지 표준 해법을 모두 보여준다.

```java
// 방법 1) @EntityGraph — team을 LEFT JOIN으로 함께 로딩
@Override
@EntityGraph(attributePaths = "team")
Page<Member> findAll(Pageable pageable);

// 방법 2) JOIN FETCH(JPQL) — 위와 동일 효과를 직접 작성
@Query("SELECT m FROM Member m JOIN FETCH m.team")
List<Member> findAllWithTeam();
```

**편의점 비유**: 라면 100개를 사는데, 라면을 하나 집을 때마다 "이 라면에 맞는 스프 주세요" 하고 계산대에 100번 왕복하면 하루가 다 간다(N+1). 대신 "라면 100개랑 거기 딸린 스프 전부 한 번에 주세요"라고 주문하면 한 번의 왕복으로 끝난다(JOIN FETCH). DB 왕복(round-trip) 자체가 비용이므로, **왕복 횟수를 1번으로 줄이는 것**이 핵심이다.

### 양방향 매핑의 주의점

양방향으로 매핑하면 편리하지만 함정이 있다. **연관관계의 주인 쪽(FK를 가진 쪽)에 값을 넣어야만 DB에 반영된다.** `Team`의 편의 메서드가 이 실수를 방지한다.

```java
// Team의 연관관계 편의 메서드: 양쪽을 동시에 세팅
public void addMember(Member member) {
    members.add(member);       // 주인 아닌 쪽(거울) 세팅
    member.setTeam(this);      // ★ 주인 쪽 세팅 → 이게 있어야 DB의 team_id가 채워짐
}
```

`members.add(member)`만 하고 `member.setTeam(this)`를 빠뜨리면, 객체 상으로는 팀에 회원이 들어간 것처럼 보이지만 **DB에는 저장되지 않는다**(주인이 아니므로). 그래서 양방향에서는 항상 이런 편의 메서드로 양쪽을 함께 세팅하는 것이 관례다.

---

## 4.5 트랜잭션과 ACID

### @Transactional이 보장하는 것

계좌 이체를 생각하자. A의 잔고에서 1만 원을 빼고(UPDATE 1) B의 잔고에 1만 원을 더한다(UPDATE 2). 만약 UPDATE 1만 성공하고 UPDATE 2 직전에 서버가 죽으면? A의 돈은 사라졌는데 B는 못 받았다. **1만 원이 증발한다.**

이 둘은 "전부 되거나, 전부 안 되거나" 해야 한다. 이 "하나의 논리적 작업 단위"가 **트랜잭션(transaction)**이고, 스프링에서는 `@Transactional`로 경계를 긋는다.

```java
@Transactional  // 이 메서드는 하나의 트랜잭션. 예외가 나면 전부 롤백
public void transfer(Long fromId, Long toId, long amount) {
    Account from = accountRepository.findById(fromId).orElseThrow();
    Account to   = accountRepository.findById(toId).orElseThrow();
    from.withdraw(amount);   // UPDATE 1 (dirty checking으로 반영 예정)
    to.deposit(amount);      // UPDATE 2
}   // 메서드 정상 종료 → commit / 도중 예외 → rollback (한 푼도 안 움직임)
```

`@Transactional`은 §3의 AOP 프록시로 동작한다. 메서드 진입 시 커넥션을 얻어 트랜잭션을 시작하고, 정상 종료면 `commit`, 런타임 예외가 터지면 `rollback`한다. 이 한 줄이 뒤에 설명할 ACID의 상당 부분을 애플리케이션 레벨에서 보장한다.

### ACID — 트랜잭션의 4대 보증

트랜잭션이 지켜야 할 4가지 성질, 그 머리글자가 ACID다.

```
 A  Atomicity   (원자성)   : 전부 성공 아니면 전부 실패. 중간은 없다.
 C  Consistency (일관성)   : 트랜잭션 전후로 DB 규칙(제약)이 항상 지켜진다.
 I  Isolation   (격리성)   : 동시에 도는 트랜잭션들이 서로 간섭하지 않는다.
 D  Durability  (지속성)   : 커밋된 데이터는 장애가 나도 사라지지 않는다.
```

각각을 우리가 배운 것과 연결해 보자.

**A — 원자성(Atomicity)**: "전부 아니면 전무." 이체에서 두 UPDATE 중 하나만 반영되는 일은 없다. → **`@Transactional`의 rollback**이 이를 책임진다. 예외가 나면 이미 실행한 UPDATE도 없던 일이 된다.

**C — 일관성(Consistency)**: 트랜잭션이 끝난 뒤에도 데이터가 정해진 규칙을 위반하지 않는다. 예: `email UNIQUE`, `balance >= 0`, FK 무결성. → **DB의 제약조건(constraint)**과 애플리케이션 검증이 함께 지킨다. `Member`의 `@Column(unique = true)`가 만든 UNIQUE 제약이 대표적이다. 이체 후에도 "잔고 합계 불변" 같은 규칙이 유지되어야 한다.

**I — 격리성(Isolation)**: 동시에 여러 트랜잭션이 돌아도 서로의 중간 상태를 보지 않는다. 두 사람이 동시에 같은 계좌에서 이체해도 잔고가 꼬이지 않아야 한다. → 이걸 **얼마나 강하게** 보장할지가 §4.6의 **격리 수준**이다.

**D — 지속성(Durability)**: 커밋된 순간 그 데이터는 정전이 나도 살아남는다. → DB는 이를 위해 **WAL(Write-Ahead Log, 선행 기록 로그)**을 쓴다. 데이터 파일을 실제로 고치기 **전에**, 변경 내용을 먼저 로그 파일에 안전하게 적어 둔다. 커밋은 "로그에 기록 완료"를 뜻하므로, 도중에 서버가 죽어도 재기동 시 로그를 재생(replay)해서 커밋된 변경을 복구한다.

```
 WAL로 보장하는 지속성:
   ① 변경을 로그에 먼저 기록(디스크 fsync) ──> "커밋 성공" 응답
   ② 실제 데이터 파일은 나중에 천천히 반영
   ③ 만약 ②를 하다 정전 → 재기동 시 로그(①) 재생 → 커밋분 완전 복구
```

정리하면, `@Transactional`(A·격리 경계)과 DB 제약조건(C)과 DB 엔진의 WAL(D), 격리 수준 설정(I)이 손잡고 ACID를 만든다.

---

## 4.6 격리 수준과 이상 현상

### 동시성이 만드는 세 가지 이상 현상

여러 트랜잭션이 **동시에** 같은 데이터를 건드리면, 격리성이 약할수록 이상한 일이 생긴다. 대표 세 가지:

**① Dirty Read(더티 리드)**: 아직 **커밋되지 않은** 다른 트랜잭션의 변경을 읽는다.

```
 T1: UPDATE balance = 0   (아직 commit 안 함)
 T2:                      SELECT balance → 0 을 읽음 (더티!)
 T1: ROLLBACK             (없던 일로)
     → T2는 존재한 적 없는 값 0을 읽어버렸다
```

**② Non-repeatable Read(반복 불가능한 읽기)**: 같은 행을 두 번 읽었는데 값이 달라진다(사이에 다른 트랜잭션이 UPDATE+커밋).

```
 T1: SELECT balance → 100
 T2:                  UPDATE balance = 50; COMMIT
 T1: SELECT balance → 50   (같은 조회인데 값이 바뀜!)
```

**③ Phantom Read(팬텀 리드)**: 같은 조건으로 두 번 조회했는데 **행의 개수**가 달라진다(사이에 INSERT/DELETE+커밋).

```
 T1: SELECT count(*) WHERE age > 20 → 10건
 T2:                  INSERT (age=30); COMMIT
 T1: SELECT count(*) WHERE age > 20 → 11건  (유령처럼 새 행 등장!)
```

### 격리 수준 4단계

이 이상 현상들을 얼마나 막을지를 정하는 것이 **격리 수준(Isolation Level)**이다. 높일수록 안전하지만 동시성(성능)은 떨어진다 — 전형적인 트레이드오프다.

| 격리 수준 | Dirty Read | Non-repeatable Read | Phantom Read | 특징 |
|---|:---:|:---:|:---:|---|
| **READ UNCOMMITTED** | 발생 | 발생 | 발생 | 가장 느슨. 커밋 전 데이터도 읽음 |
| **READ COMMITTED** | 방지 | 발생 | 발생 | 커밋된 것만 읽음. PostgreSQL 기본 |
| **REPEATABLE READ** | 방지 | 방지 | 발생(*) | 같은 행 반복 읽기 보장. MySQL(InnoDB) 기본 |
| **SERIALIZABLE** | 방지 | 방지 | 방지 | 완전 직렬화. 가장 안전, 가장 느림 |

(*) 표준상 REPEATABLE READ에서 Phantom이 허용되지만, MySQL InnoDB는 갭 락으로 상당 부분 막는 등 구현마다 차이가 있다.

```
  느슨 ◄──────────────────────────────────────────► 엄격
  READ          READ           REPEATABLE      SERIALIZABLE
  UNCOMMITTED   COMMITTED      READ
  동시성 높음                                  동시성 낮음
  안전성 낮음                                  안전성 높음
     ▲                ▲                             ▲
  거의 안 씀      PostgreSQL 기본                완전 격리 필요할 때만
```

**PostgreSQL의 기본은 READ COMMITTED**다. Dirty Read는 막되, Non-repeatable/Phantom은 허용한다. 대부분의 웹 애플리케이션에는 이 수준이 성능과 안전의 균형점이라 기본값으로 채택되었다. 정말 엄격한 정합성이 필요한 특정 트랜잭션에서만 `@Transactional(isolation = Isolation.SERIALIZABLE)` 등으로 국소적으로 올린다.

### MVCC — 어떻게 읽기와 쓰기가 안 싸우나

전통적 방식이라면 "읽는 동안 쓰기 못 함, 쓰는 동안 읽기 못 함"처럼 잠금(lock)으로 격리를 구현할 것이다. 그러면 읽기가 쓰기를 막아 성능이 급락한다. PostgreSQL과 MySQL(InnoDB)은 대신 **MVCC(Multi-Version Concurrency Control, 다중 버전 동시성 제어)**를 쓴다.

핵심 아이디어: **데이터를 덮어쓰지 않고, 변경할 때마다 새로운 "버전"을 만든다.** 각 트랜잭션은 자기가 시작한 시점에 맞는 **스냅샷(버전)**을 읽는다.

```
                         행의 버전들 (시간순)
  balance:  [v1=100] ──> [v2=50] ──> [v3=30]
                │            │           │
  T_A(먼저 시작): v1(100)을 계속 봄 ← 다른 트랜잭션이 고쳐도 내 버전은 불변
  T_B(나중 시작): v3(30)을 봄
```

이 덕분에 **"읽기는 쓰기를 막지 않고, 쓰기는 읽기를 막지 않는다."** 읽는 쪽은 자기 스냅샷(과거 버전)을 보고, 쓰는 쪽은 새 버전을 만들 뿐이라 서로 기다릴 필요가 없다. §4.2에서 본 JPA의 Dirty Checking 스냅샷과 이름이 같지만 층위가 다르다 — 저건 애플리케이션 캐시의 스냅샷, 이건 DB 엔진의 버전 스냅샷이다.

---

## 4.7 인덱스와 B-tree

### Full Scan O(n) vs 인덱스 O(log n)

`SELECT * FROM member WHERE email = 'a@example.com'`을 실행한다고 하자. 인덱스가 없으면 DB는 **처음부터 끝까지 모든 행을 하나씩 비교**한다. 이것이 **풀 스캔(Full Scan)**이고, 행이 N개면 시간이 O(n)이다. 100만 건이면 최악의 경우 100만 번 비교한다.

**책 찾아보기 비유**: 500쪽짜리 전공책에서 "트랜잭션"이란 단어가 나온 곳을 찾는다. 방법 1은 1쪽부터 500쪽까지 다 넘기며 눈으로 훑기(풀 스캔). 방법 2는 책 맨 뒤 **찾아보기(색인, index)**에서 "트랜잭션 … 217, 384쪽"을 보고 곧장 그 쪽으로 가기. 색인은 이미 **가나다순으로 정렬**되어 있어서, 원하는 단어를 몇 번의 점프로 찾는다. DB 인덱스가 바로 이 "찾아보기"다.

### B-tree — 인덱스의 자료구조

DB 인덱스는 대부분 **B-tree(정확히는 B+tree)**라는 균형 트리로 만든다. 정렬된 값을 트리로 쌓아, 루트에서 시작해 몇 단계만 내려가면 원하는 값에 도달한다. 각 단계에서 탐색 범위가 확 줄어들기 때문에 시간이 **O(log n)**이다.

```
                         [ 50 ]                    ← 루트 노드
                        /      \
                 [20 | 35]      [70 | 90]          ← 내부 노드
                /    |    \      /    |    \
           [..12] [21..] [36..][51..][71..][91..]  ← 리프 노드(실제 데이터/포인터)

  email = 'a@example.com'(정렬상 값=63)을 찾는다면:
    ① 루트: 63 > 50 → 오른쪽으로
    ② 내부: 63 < 70 → 51.. 리프로
    ③ 리프: 63 발견!  (3번 비교로 끝. 풀 스캔이면 최악 N번)
```

100만 건이라도 트리 높이는 3~4단계 정도라, **몇 번의 비교로 원하는 행을 찾는다.** O(n)과 O(log n)의 차이가 100만 건에서 100만 번 vs 20번 수준으로 벌어진다.

### 트레이드오프 — 인덱스는 공짜가 아니다

그렇다면 모든 컬럼에 인덱스를 걸면 될까? 아니다. **인덱스는 읽기를 빠르게 하는 대신 쓰기를 느리게 한다.**

- 행을 INSERT/UPDATE/DELETE할 때마다, 관련된 **모든 인덱스의 B-tree도 정렬 상태를 유지하도록 갱신**해야 한다.
- 인덱스는 별도의 디스크 공간도 잡아먹는다.

즉 인덱스는 "읽기 성능 ↑ / 쓰기 성능 ↓ / 저장 공간 ↑"의 거래다. **자주 검색·조인·정렬의 조건이 되는 컬럼에만 선별적으로** 건다. 거의 조회 안 하는 컬럼에 인덱스를 걸면 쓰기만 느려지는 손해다.

```
       인덱스 없음                  인덱스 있음
 읽기:  느림 O(n)      ────────>    빠름 O(log n)   ← 이득
 쓰기:  빠름            ────────>    느림(인덱스 갱신) ← 비용
 공간:  작음            ────────>    큼               ← 비용
```

### JPA에서 인덱스 만들기

`Member`의 `@Column(unique = true)`는 단순한 제약이 아니다. **UNIQUE 제약을 걸면 DB가 자동으로 유니크 인덱스를 생성**한다.

```java
@Column(nullable = false, unique = true)
private String email;
// → CREATE UNIQUE INDEX ... ON member(email)  이 자동 생성됨
// → 그래서 findByEmail(email) 조회가 빠르고, 중복 이메일 INSERT는 DB가 차단
```

유니크가 아닌 일반 검색용 인덱스는 `@Table(indexes = ...)`로 명시한다.

```java
@Entity
@Table(name = "member", indexes = {
    @Index(name = "idx_member_name", columnList = "name")  // name 검색용 인덱스
})
public class Member { ... }
```

정리하면 `Member`는 `email`에 (UNIQUE 덕분에) 인덱스를 갖고, 이름 검색이 잦다면 `name`에 인덱스를 추가로 걸어 `findByNameContaining` 같은 조회를 빠르게 할 수 있다. (단, `LIKE '%...%'`처럼 앞에 와일드카드가 붙으면 인덱스를 못 타는 경우도 있음을 유의.)

---

## 4.8 커넥션 풀과 ORM 임피던스 불일치

### @Transactional은 커넥션을 붙잡고 있다

§2.7에서 **HikariCP 커넥션 풀**을 배웠다. DB 커넥션은 만드는 비용이 커서 미리 몇 개 만들어 두고 빌려 쓰고 반납한다고 했다. 여기 JPA의 트랜잭션이 어떻게 얽히는지 다시 짚자.

`@Transactional` 메서드에 진입하면 **풀에서 커넥션을 하나 빌려와, 메서드가 끝날 때까지 계속 붙잡고 있다.** 그 사이 영속성 컨텍스트가 살아 있고, Dirty Checking·LAZY 로딩이 모두 이 커넥션 위에서 일어난다.

```
 @Transactional 진입 ─┐
                      │  ← 이 구간 내내 커넥션 1개를 점유
   findById() ...     │     (풀에서 빌린 상태로 반납 안 됨)
   member.setName()   │
   getTeam() (LAZY)   │
 메서드 종료(commit) ─┘  ← 여기서 비로소 커넥션 반납
```

**그래서 트랜잭션을 짧게 유지하는 것이 중요하다.** 트랜잭션 안에서 외부 API 호출 같은 느린 작업을 하면 그동안 커넥션을 붙잡아, 풀의 커넥션이 금방 동나고 다른 요청들이 커넥션을 못 얻어 대기하다 타임아웃 난다. `MemberService`가 읽기 메서드에 `@Transactional(readOnly = true)`를 붙이는 것도 이 맥락이다 — 읽기 전용임을 알려 불필요한 스냅샷/플러시를 줄이고 트랜잭션을 가볍게 한다.

### 객체 그래프 vs 평평한 테이블 — 임피던스 불일치

이 챕터의 처음으로 돌아가자. JPA가 왜 이렇게 복잡한 장치들(영속성 컨텍스트, 프록시, 연관관계 주인, LAZY…)을 갖고 있는가? 근본 원인은 **객체 세계와 관계형 세계가 태생적으로 다르게 생겼기 때문**이다. 이 구조적 차이를 **객체-관계 임피던스 불일치(Object-Relational Impedance Mismatch)**라 부른다.

```
   객체 세계 (Java)                        관계형 세계 (RDB)
 ┌────────────────────────┐            ┌───────────────────────────┐
 │ Member ─참조(reference)→ Team        │ member.team_id ─FK 값→ team.id │
 │        (객체를 직접 가리킴)            │        (정수 값을 가짐)          │
 │                                      │                             │
 │ 상속: Employee extends Person        │ 상속 개념 없음(테이블은 평면)   │
 │                                      │                             │
 │ 컬렉션: List<Member> members         │ JOIN으로 여러 행을 엮음         │
 │        (객체 그래프로 순회)           │ (집합 연산으로 표현)            │
 └────────────────────────┘            └───────────────────────────┘
```

세 가지 대표 불일치:

1. **참조(reference) vs 외래 키 값**: 객체는 `member.getTeam().getName()`처럼 참조를 따라 **그래프를 자유롭게 순회**한다. 테이블은 `team_id`라는 값을 갖고 JOIN으로 이어 붙일 뿐이다. → `@JoinColumn`, 연관관계 주인, LAZY 프록시가 이 간극을 메운다.
2. **상속(inheritance) vs 없음**: 객체는 `Employee extends Person`처럼 상속이 자연스럽지만, 테이블에는 상속이 없다. → JPA는 `@Inheritance` 전략(단일 테이블/조인/구현별 테이블)으로 흉내 낸다.
3. **컬렉션(collection) vs JOIN**: 객체는 `Team.members`라는 컬렉션을 그냥 for문으로 돈다. 테이블은 두 테이블을 JOIN한 결과 집합으로 표현한다. → `@OneToMany`와 페치 전략이 컬렉션을 채운다.

**JPA의 존재 이유가 바로 이 불일치를 메우는 것이다.** 개발자가 객체 그래프로 자연스럽게 코딩하면(`member.getTeam()`), JPA가 뒤에서 그것을 테이블·외래 키·JOIN의 언어로 번역한다. §4.1의 "통역사"가 이 임피던스 불일치를 넘나드는 통역사였던 것이다.

하지만 통역에는 새는 부분이 있다(leaky abstraction). 객체처럼 편하게 짰는데 뒤에서 N+1 같은 비효율적 SQL이 나갈 수 있다. 그래서 **JPA를 잘 쓰려면 "지금 이 객체 코드가 어떤 SQL로 번역되는가"를 항상 의식**해야 한다. 이것이 이 챕터 전체를 관통하는 태도다 — 편의를 누리되, 그 아래 흐르는 SQL과 트랜잭션과 인덱스를 이해할 것.

---

## ⚠️ 흔한 오해와 함정

**오해 1: "LAZY로 설정하면 N+1이 자동으로 해결된다."**
정반대다. LAZY는 오히려 N+1을 **일으키는** 설정이다. 목록을 가져온 뒤 요소마다 연관 데이터를 뒤늦게 조회하기 때문이다. N+1을 막는 것은 LAZY가 아니라 `JOIN FETCH` 또는 `@EntityGraph`다. LAZY는 "불필요한 로딩을 안 하게" 해주는 기본 정책이고, "필요한 로딩을 한 방에" 하는 건 별개의 명시적 조치가 필요하다.

**오해 2: "save()를 호출하지 않으면 저장(수정)이 안 된다."**
조회한 **영속 상태** 엔티티는 값만 바꿔도 트랜잭션 커밋 시 **Dirty Checking**으로 UPDATE가 자동으로 나간다(§4.2). `member.setName("새이름")`만 하고 `save()`를 안 불러도 이름이 바뀐다. 반대로 이 사실을 모르면 "왜 안 바꿨는데 값이 바뀌었지?"라며 당황한다. 단, **새 엔티티를 처음 저장(INSERT)**할 때는 `save()`(=`persist`)가 필요하다 — 아직 영속 상태가 아니기 때문이다.

**오해 3: OSIV를 켜두면 다 편하다.**
OSIV(Open Session In View)는 영속성 컨텍스트를 뷰(응답 렌더링)까지 열어 둬서, 컨트롤러/뷰에서도 LAZY 로딩이 되게 해준다. Spring Boot는 기본이 `true`다. 편하지만 함정이 있다 — **DB 커넥션을 요청이 끝날 때까지(뷰 렌더링까지) 붙잡고 있어** 트래픽이 몰리면 커넥션 풀이 고갈된다(§4.8). 성능이 중요한 서비스는 `spring.jpa.open-in-view=false`로 끄고, 필요한 연관은 서비스 계층에서 `JOIN FETCH`로 미리 로딩한다.

**오해 4: "인덱스는 많을수록 좋다."**
인덱스는 읽기를 빠르게 하지만 쓰기마다 인덱스도 갱신해야 하므로 쓰기가 느려지고 공간도 더 쓴다(§4.7). 모든 컬럼에 인덱스를 거는 것은 INSERT/UPDATE가 잦은 테이블에서 성능을 오히려 떨어뜨린다. **조회·정렬·조인 조건으로 자주 쓰이는 컬럼에만 선별적으로** 걸어야 한다.

**오해 5: "@Transactional을 붙이면 무조건 롤백된다."**
스프링은 기본적으로 **런타임 예외(unchecked)와 Error**에만 롤백하고, **체크 예외(checked)에는 롤백하지 않는다.** 체크 예외에도 롤백하려면 `@Transactional(rollbackFor = Exception.class)`처럼 지정해야 한다. 또한 `@Transactional`은 AOP 프록시로 동작하므로(§3), 같은 클래스 내부에서 자기 메서드를 직접 호출하면(self-invocation) 프록시를 거치지 않아 트랜잭션이 적용되지 않는다.

**오해 6: "격리 수준은 높을수록 무조건 좋다."**
SERIALIZABLE은 가장 안전하지만 동시성이 가장 낮아, 웹 서비스 전체에 걸면 처리량이 급락한다(§4.6). 대부분은 READ COMMITTED로 충분하고, 정말 엄격한 정합성이 필요한 소수 트랜잭션만 국소적으로 격리 수준을 올린다.

---

## 연습문제

**1. JDBC의 네 가지 만성 질환을 쓰고, ORM이 각각을 어떻게 해결하는지 설명하라.**
<details><summary>힌트/해설</summary>
반복(boilerplate), 실수 유발(자원 누수·오타), DB 종속(방언), 객체-테이블 수작업 매핑. ORM은 커넥션/자원 관리 자동화, SQL 자동 생성, 방언 추상화(JPQL), 컬럼↔필드 자동 매핑으로 각각을 해결한다.
</details>

**2. 다음 코드는 `save()`를 호출하지 않는데도 이름이 변경된다. 그 이유를 JPA의 어떤 메커니즘으로 설명할 수 있는가?**
```java
@Transactional
public void rename(Long id, String name) {
    Member m = memberRepository.findById(id).orElseThrow();
    m.setName(name);   // save() 없음
}
```
<details><summary>힌트/해설</summary>
영속성 컨텍스트의 **Dirty Checking(변경 감지)**. 조회 시 저장해 둔 스냅샷과 커밋 시점의 엔티티를 비교해, 달라진 필드에 대해 자동으로 UPDATE를 생성한다(§4.2). `m`이 영속 상태이기 때문에 가능하다.
</details>

**3. 게시글 목록을 조회하고 각 작성자 이름을 출력하는 코드에서 쿼리가 101번 나갔다. 이름과 원인을 설명하고, 코드로 해결책을 제시하라.**
<details><summary>힌트/해설</summary>
**N+1 문제**. 목록 1번 + 요소마다 작성자 조회 N(=100)번. LAZY 참조를 요소별로 뒤늦게 로딩해서 생긴다. 해결: `@Query("SELECT p FROM Post p JOIN FETCH p.author")` 또는 `@EntityGraph(attributePaths = "author")`로 한 번에 조인 로딩(§4.4).
</details>

**4. 계좌 이체(출금+입금)에서 ACID의 A(원자성)와 D(지속성)가 각각 어떤 장치로 보장되는지 설명하라.**
<details><summary>힌트/해설</summary>
A(원자성): `@Transactional`의 rollback — 도중 예외 시 이미 실행한 출금 UPDATE도 취소되어 전부 아니면 전무가 된다. D(지속성): DB 엔진의 **WAL(선행 기록 로그)** — 커밋된 변경을 로그에 먼저 안전히 기록하므로 정전 후 재기동 시 재생으로 복구된다(§4.5).
</details>

**5. Non-repeatable Read와 Phantom Read의 차이를 설명하고, 각각을 방지하는 최소 격리 수준을 표에서 찾아라.**
<details><summary>힌트/해설</summary>
Non-repeatable Read는 **같은 행**을 두 번 읽을 때 값이 달라지는 것(사이의 UPDATE+커밋), Phantom Read는 **같은 조건 조회의 행 개수**가 달라지는 것(사이의 INSERT/DELETE+커밋). Non-repeatable Read는 **REPEATABLE READ** 이상, Phantom Read는 표준상 **SERIALIZABLE**에서 확실히 방지된다(§4.6).
</details>

---

## 요약

- **왜 ORM인가**: 순수 JDBC는 반복·실수 유발·DB 종속·객체↔테이블 수작업 매핑이라는 고통이 있었다. ORM은 이를 자동화하는 "통역사"이며, **JPA는 스펙, Hibernate는 그 구현체**다.
- **Entity**: `@Entity`/`@Table`/`@Id`/`@GeneratedValue`/`@Column`으로 객체를 테이블에 매핑한다. **기본 생성자**는 Hibernate가 리플렉션으로 빈 객체를 만들기 위해 필수다. PK 전략은 IDENTITY(DB 채번) vs SEQUENCE(시퀀스 발급). **영속성 컨텍스트**는 1차 캐시와 **변경 감지(Dirty Checking, 워드 변경추적 비유)**를 제공한다.
- **JpaRepository**: 인터페이스만 선언하면 Spring Data JPA가 **프록시 구현체를 자동 생성**한다. **쿼리 메서드**(메서드 이름=쿼리), `@Query`(JPQL/Native), `Pageable`(도서관 비유로 잘라 가져오기)을 제공한다.
- **연관관계**: **외래 키를 가진 쪽이 주인**(아파트 열쇠 비유), 반대쪽은 `mappedBy`로 거울. FetchType은 **LAZY가 원칙**(EAGER는 낭비·예측불가). **N+1 문제**는 목록+요소별 로딩으로 1+N 쿼리가 나가는 현상이며, **JOIN FETCH / @EntityGraph**로 한 방에 조인해 해결한다(편의점 비유).
- **트랜잭션과 ACID**: `@Transactional`이 원자성·격리 경계를, 제약조건이 일관성을, WAL이 지속성을 보장한다(계좌 이체 예).
- **격리 수준**: Dirty/Non-repeatable/Phantom Read를 어디까지 막느냐로 4단계. PostgreSQL 기본은 **READ COMMITTED**. **MVCC**로 "읽기와 쓰기가 서로 막지 않게" 구현한다.
- **인덱스와 B-tree**: 풀 스캔 O(n) vs B-tree O(log n)(책 찾아보기 비유). 인덱스는 읽기를 빠르게 하되 **쓰기·공간 비용**이 있는 트레이드오프. `@Column(unique=true)`는 유니크 인덱스를 자동 생성, `@Table(indexes=...)`로 일반 인덱스 지정.
- **임피던스 불일치**: 객체(참조·상속·컬렉션)와 테이블(FK 값·평면·JOIN)의 구조적 차이가 **JPA의 존재 이유**다. `@Transactional`은 그 구간 내내 커넥션을 점유하므로 트랜잭션은 짧게 유지해야 한다(HikariCP 고갈 주의).

실행 코드로는 `chapter06-spring-data-jpa`의 `Member`(@ManyToOne Team)·`Team`(@OneToMany mappedBy) 엔티티, `MemberRepository`(쿼리 메서드·@Query·@EntityGraph·JOIN FETCH), `MemberService`(@Transactional), `ProxyRevealRunner`(프록시 폭로)를 열어 이 챕터의 개념들이 실제로 동작하는 모습을 확인하라.

---

[← 이전: 웹 애플리케이션의 구조](03-웹-애플리케이션-구조.md) | [목차](README.md) | [다음: 보안 - Spring Security →](05-보안-spring-security.md)
