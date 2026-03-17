# Java & Spring Boot 교육 과정

## 소개
이 프로젝트는 Java 기초부터 Spring Boot 실전까지 단계별로 학습할 수 있는 교육 자료이다.
모든 예제는 Docker를 활용하여 실행 환경을 통일하고, 인프라 구성을 코드로 관리한다.

## 사전 준비
- JDK 21 (Eclipse Temurin 권장)
- Docker & Docker Compose
- IDE (IntelliJ IDEA 권장)
- Git

## 목차

| 챕터 | 주제 | 핵심 개념 | Docker 활용 |
|------|------|-----------|-------------|
| 01 | Java 기초 | 변수, 타입, 제어문, 배열, 메서드, 예외처리 | Java 컴파일/실행 컨테이너 |
| 02 | 객체지향 프로그래밍 | 클래스, 상속, 다형성, 인터페이스, record, sealed class | Java 컴파일/실행 컨테이너 |
| 03 | 컬렉션과 스트림 | 제네릭, List/Set/Map, Stream API, Lambda, Optional | Java 컴파일/실행 컨테이너 |
| 04 | Spring Boot 입문 | IoC/DI, Bean, ComponentScan, Profile, 자동설정 | Spring Boot 앱 컨테이너 |
| 05 | Spring Web MVC | REST API, DTO, Validation, 예외처리 | Spring Boot 앱 컨테이너 |
| 06 | Spring Data JPA | Entity, Repository, JPQL, 페이징, 연관관계 | PostgreSQL + 앱 컨테이너 |
| 07 | Spring Security | 인증/인가, JWT, BCrypt, RBAC | PostgreSQL + 앱 컨테이너 |
| 08 | 테스트 | JUnit5, Mockito, @SpringBootTest, Testcontainers | Testcontainers (Docker 기반 테스트) |
| 09 | 종합 프로젝트 | 게시판 API (회원, 게시글, 댓글) | Docker Compose 풀스택 |

## 빠른 시작

### Chapter 01~03 실행 (순수 Java)
```bash
cd chapter01-java-basics
docker build -t java-basics .
docker run --rm java-basics

# 특정 클래스 실행
docker run --rm java-basics java -cp out com.edu.basics.ControlFlow
```

### Chapter 04~07 실행 (Spring Boot)
```bash
cd chapter04-spring-boot-intro
docker compose up --build
```

### Chapter 09 종합 프로젝트 실행
```bash
cd chapter09-final-project
docker compose up --build

# API 테스트
# 회원가입
curl -X POST http://localhost:8080/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"password123"}'

# 로그인
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"password123"}'

# 게시글 작성 (토큰 필요)
curl -X POST http://localhost:8080/api/posts \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {TOKEN}" \
  -d '{"title":"첫 게시글","content":"안녕하세요!"}'

# 게시글 목록 조회
curl "http://localhost:8080/api/posts?page=0&size=10"

# 게시글 상세 조회
curl http://localhost:8080/api/posts/1
```

## 인프라 Docker Compose
공통 인프라(PostgreSQL, Redis, Adminer)를 한번에 실행:
```bash
cd docker
docker compose -f docker-compose-infra.yml up -d

# Adminer(DB 관리도구) 접속: http://localhost:8081
# PostgreSQL 접속정보: edu / edu1234 / edu_spring
```

## 프로젝트 구조
```
edu_spring/
├── README.md                      # 이 파일
├── docker/                        # 공통 Docker 설정
│   ├── docker-compose-infra.yml   # 인프라 (PostgreSQL, Redis, Adminer)
│   ├── init-db.sql               # DB 초기화 스크립트
│   └── Dockerfile.spring         # Spring Boot 공용 Dockerfile
├── chapter01-java-basics/         # Java 기초
├── chapter02-oop/                 # 객체지향 프로그래밍
├── chapter03-collections/         # 컬렉션과 스트림
├── chapter04-spring-boot-intro/   # Spring Boot 입문
├── chapter05-spring-web/          # Spring Web MVC
├── chapter06-spring-data-jpa/     # Spring Data JPA
├── chapter07-spring-security/     # Spring Security
├── chapter08-testing/             # 테스트
└── chapter09-final-project/       # 종합 프로젝트 (게시판)
```

## 학습 방법
1. 각 챕터의 `README.md`를 먼저 읽고 개념을 이해한다
2. POC 코드를 Docker로 실행하며 결과를 확인한다
3. 코드를 수정하고 다시 실행하며 실험한다
4. Chapter 09에서 전체 개념을 종합하여 실전 프로젝트를 완성한다

## 기술 스택
- **Language**: Java 21
- **Framework**: Spring Boot 3.3.x ~ 3.4.x
- **Database**: PostgreSQL 16
- **Cache**: Redis 7
- **Security**: Spring Security + JWT
- **Testing**: JUnit 5, Mockito, Testcontainers
- **Container**: Docker, Docker Compose
- **Build**: Gradle 8.8
