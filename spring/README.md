# Spring Boot 실전 트랙

자바 기초([../java/](../java/README.md))를 마친 뒤, Spring Boot로 백엔드 API를 만드는 트랙입니다.
개념은 [docs/SPRING_개념서/](docs/SPRING_개념서/README.md)로 읽고, 각 챕터를 Docker로 실행하며 배웁니다.

## 목차

| 챕터 | 주제 | 핵심 개념 |
|------|------|-----------|
| 04 | [Spring Boot 입문](chapter04-spring-boot-intro/) | IoC/DI, Bean, ComponentScan, Profile, **AOP(실행 시간 측정·프록시 확인)** |
| 05 | [Spring Web MVC](chapter05-spring-web/) | REST API, DTO, Validation, 예외처리, **필터 vs 인터셉터** |
| 06 | [Spring Data JPA](chapter06-spring-data-jpa/) | Entity, Repository, JPQL, 페이징, 연관관계, **@Transactional 롤백·전파·readOnly 실습** |
| 07 | [Spring Security](chapter07-spring-security/) | 인증/인가, JWT, BCrypt, RBAC |
| 08 | [테스트](chapter08-testing/) | JUnit5, Mockito, @SpringBootTest, Testcontainers |
| 09 | [종합 프로젝트](chapter09-final-project/) | 게시판 API (회원, 게시글, 댓글) |

## 빠른 시작

```bash
# 1) 공통 인프라 먼저 (PostgreSQL, Redis, Adminer)
cd spring/docker
docker compose -f docker-compose-infra.yml up -d

# Adminer(DB 관리 UI): http://localhost:8081  (PostgreSQL: edu / edu1234 / edu_spring)

# 2) 각 챕터 실행
cd ../chapter04-spring-boot-intro
docker compose up --build
```

챕터 05~07, 09에는 `requests.http` 파일이 있어 VS Code REST Client / IntelliJ HTTP Client로
curl 없이 API를 호출해 볼 수 있습니다. ([docs/추가자료_안내.md](docs/추가자료_안내.md))

## 컨테이너 이름

무엇이 떠 있는지 `docker ps`에서 바로 알 수 있도록 이름을 통일했습니다.

| 컨테이너 | 역할 |
|---|---|
| `spring-postgres` / `spring-redis` / `spring-adminer` | 공통 인프라 (spring/docker/) |
| `spring-ch04-intro` | 04장 앱 |
| `spring-ch05-web` | 05장 앱 |
| `spring-ch06-jpa` + `spring-ch06-postgres` | 06장 앱 + 전용 DB |
| `spring-ch07-security` + `spring-ch07-postgres` | 07장 앱 + 전용 DB |
| `spring-ch08-testing` + `spring-ch08-postgres` | 08장 앱 + 전용 DB |
| `spring-ch09-board` + `spring-ch09-postgres`/`-redis`/`-adminer` | 종합 프로젝트 일체 |

> 챕터별 전용 DB를 쓰는 챕터(06~09)는 인프라 없이 `docker compose up --build` 한 방으로 실행됩니다.
> 전용 DB와 공통 인프라를 동시에 띄우면 5432 포트가 충돌하니 한쪽만 사용하세요.

## 프로젝트 구조

```
spring/
├── docker/                        # 공통 인프라 (PostgreSQL, Redis, Adminer)
│   ├── docker-compose-infra.yml
│   ├── init-db.sql
│   └── Dockerfile.spring          # Spring Boot 공용 Dockerfile
├── chapter04-spring-boot-intro/
├── chapter05-spring-web/
├── chapter06-spring-data-jpa/
├── chapter07-spring-security/
├── chapter08-testing/
├── chapter09-final-project/
└── docs/
    ├── SPRING_개념서/             # 이론 개념서 (실습 챕터와 상호 연결)
    ├── SPRING_교육자료.md
    └── 추가자료_안내.md
```

## 기술 스택
- **Framework**: Spring Boot 3.4.3 (Java 21, Gradle 8.8)
- **Database**: PostgreSQL 16 / **Cache**: Redis 7
- **Security**: Spring Security + JWT
- **Testing**: JUnit 5, Mockito, Testcontainers

> 로컬에서 Gradle로 직접 빌드하려면 JDK 17~21이 필요합니다. 막히면 [../docs/트러블슈팅.md](../docs/트러블슈팅.md) 참고.
