# Java & Spring Boot 교육 과정

## 소개
이 프로젝트는 Java 기초부터 Spring Boot 실전까지 단계별로 학습할 수 있는 교육 자료입니다.
각 챕터의 README에서 개념을 학습하고, Docker로 예제 코드를 바로 실행하며 체험할 수 있습니다.

## 사전 준비
- Docker & Docker Compose (필수 - 이것만 있으면 됩니다!)
- IDE (IntelliJ IDEA 또는 VS Code 권장, 코드를 읽고 수정할 때)
- Git

> **JDK를 따로 설치할 필요 없습니다.** Docker 컨테이너 안에 JDK 21이 포함되어 있습니다.

---

## 학습 경로 (두 가지 트랙)

이 저장소는 목적이 다른 **두 개의 Java 학습 트랙**을 함께 제공합니다. 둘 다 같은 Docker 환경/`run.sh`로 실행합니다.

| 트랙 | 무엇 | 언제 |
|------|------|------|
| **① 자바 교재 (이것이 자바다 목차)** | [docs/JAVA_교재/](docs/JAVA_교재/README.md) 개념 + [chapter-java-textbook/](chapter-java-textbook/) 소절별 실습 176개. **목차 소절 = 스크립트 1개**로 촘촘히, 처음부터 끝까지 | 자바를 **넓고 체계적으로** 훑을 때 (`./run.sh` → `j) 자바 교재`) |
| **② 개념서 + 기초 챕터** | [docs/JAVA_개념서/](docs/JAVA_개념서/README.md)("왜 그렇게 동작하는가") + [chapter01~03](chapter01-java-basics/) 실행 데모(심화: `FloatingPointBits`·`BigOTiming`·`HashMapInternals` 등) | 원리를 **깊게** 파고들 때 |

> 두 트랙은 클래스 이름이 겹치지 않아 `./run.sh <클래스명>`이 항상 유일하게 실행됩니다.
> 아래 Spring(04~09)·CS 심화 트랙은 두 경로 공통의 다음 단계입니다.

## 목차 (Spring 실전 + 종합 프로젝트)

| 챕터 | 주제 | 핵심 개념 |
|------|------|-----------|
| 01 | [Java 기초](chapter01-java-basics/) | 변수, 타입, 제어문, 배열, 메서드, 예외처리 |
| 02 | [객체지향 프로그래밍](chapter02-oop/) | 클래스, 상속, 다형성, 인터페이스, record, sealed class |
| 03 | [컬렉션과 스트림](chapter03-collections/) | 제네릭, List/Set/Map, Stream API, Lambda, Optional |
| 04 | [Spring Boot 입문](chapter04-spring-boot-intro/) | IoC/DI, Bean, ComponentScan, Profile, 자동설정 |
| 05 | [Spring Web MVC](chapter05-spring-web/) | REST API, DTO, Validation, 예외처리 |
| 06 | [Spring Data JPA](chapter06-spring-data-jpa/) | Entity, Repository, JPQL, 페이징, 연관관계 |
| 07 | [Spring Security](chapter07-spring-security/) | 인증/인가, JWT, BCrypt, RBAC |
| 08 | [테스트](chapter08-testing/) | JUnit5, Mockito, @SpringBootTest, Testcontainers |
| 09 | [종합 프로젝트](chapter09-final-project/) | 게시판 API (회원, 게시글, 댓글) |

### CS 기반 심화 트랙 (비전공자 → 전공자 수준)

프레임워크 사용법을 넘어 **그 밑단이 왜/어떻게 동작하는가**를 다룹니다. 실행 예제로 직접 측정·증명합니다.

| 트랙 | 개념서 | 실습 |
|------|--------|------|
| 자료구조·알고리즘 | [docs/CS_알고리즘_개념서/README.md](docs/CS_알고리즘_개념서/README.md) | [chapter-cs-algorithms/](chapter-cs-algorithms/) — `./run.sh SearchAlgorithms`, `SortingAlgorithms`, `RecursionAndDP`, `DataStructuresFromScratch`, `TreeAndHeap`, `GraphAlgorithms` (Big-O, 정렬, 재귀/DP, 트리·힙, 그래프) |
| 운영체제·동시성 | [docs/CS_운영체제_개념서/README.md](docs/CS_운영체제_개념서/README.md) | [chapter-cs-concurrency/](chapter-cs-concurrency/) — `./run.sh RaceConditionDemo`, `SynchronizationDemo`, `DeadlockDemo`, `ExecutorAndFutures`, `ProducerConsumer` (경쟁 상태, 동기화, 교착, 스레드풀) |
| 데이터베이스 이론 | [docs/CS_데이터베이스_개념서/README.md](docs/CS_데이터베이스_개념서/README.md) | [chapter-cs-database/](chapter-cs-database/) — PostgreSQL SQL 랩 (정규화, ACID, 격리수준, 인덱스 EXPLAIN) |
| 컴퓨터 네트워크 | [docs/CS_네트워크_개념서/README.md](docs/CS_네트워크_개념서/README.md) | [chapter-cs-network/](chapter-cs-network/) — `./run.sh RawHttpClient`(원시 HTTP 소켓), `labs/observe_http.sh`(DNS/TCP/TLS 관찰) |

> Java 개념서·Spring 개념서에도 머신 레이어 심화 섹션(바이트코드/JIT/GC, 2의 보수/IEEE-754, 타입 소거,
> @Transactional 프록시 원리, ACID/격리수준/B-tree 인덱스, HMAC/대칭·비대칭 암호)이 추가되어 있습니다.

---

## 빠른 시작

### Chapter 01~03: Java 기초 (통합 Docker + VS Code 실습)

하나의 Docker 컨테이너에서 Chapter 01~03의 모든 예제를 실행합니다.
VS Code에 붙여서 개념서를 읽으면서 코드를 수정하고 바로 실행할 수 있습니다.

```bash
# 1. 컨테이너 띄우기 (최초 1회)
docker compose up -d

# 2. VS Code에서 접속 (추천!)
#    F1 → "Dev Containers: Attach to Running Container" → java-edu 선택
#    터미널 열기(Ctrl+`) 후:
./compile.sh                          # 컴파일
./run.sh                              # 대화형 계층 메뉴 (카테고리 선택 → 개념 선택)
./run.sh VariablesAndTypes            # Ch01 - 변수와 타입
./run.sh PolymorphismExample          # Ch02 - 다형성 (개념당 스크립트 1개)
./run.sh StreamCreationExample        # Ch03 - Stream API
./run.sh FloatingPointBits            # [심화] 부동소수점 비트 (IEEE-754)
./run.sh SortingAlgorithms            # CS 알고리즘 - 정렬 5종 측정
./run.sh RaceConditionDemo            # CS 동시성 - 경쟁 상태(레이스 컨디션)

# 인자 없이 실행하면 대화형 계층 메뉴(카테고리 선택 → 개념 선택)가 뜨고,
# ./run.sh <ClassName>으로 클래스 이름을 지정하면 해당 개념이 바로 실행됩니다.

# 3. 또는 터미널에서 직접 실행
docker exec -it java-edu ./compile.sh
docker exec -it java-edu ./run.sh                    # 대화형 계층 메뉴
docker exec -it java-edu ./run.sh VariablesAndTypes  # 클래스 이름으로 직접 실행
docker exec -it java-edu ./run.sh all                # 전체 실행

# 4. 종료
docker compose down
```

### 학습 흐름

```
README로 개념 학습  →  Docker + VSC로 예제 실행  →  코드 수정/실험  →  compile.sh → run.sh
```

1. 각 챕터의 `README.md`를 읽으며 **왜(Why)** 이 개념이 필요한지 이해합니다
2. VS Code에서 예제를 실행하여 **결과를 눈으로 확인**합니다
3. 소스 코드를 직접 수정하고 `./compile.sh` → `./run.sh`로 변화를 확인합니다
4. 다음 챕터로 넘어갑니다

> 볼륨 마운트 덕분에 VS Code에서 파일 저장하면 컨테이너에 바로 반영됩니다.
> `docker build`를 다시 할 필요 없이 `./compile.sh`만 실행하면 됩니다.

---

### Chapter 04~08: Spring Boot

Spring Boot 챕터는 각 챕터 디렉토리에서 개별적으로 실행합니다.

```bash
# 공통 인프라 먼저 실행 (PostgreSQL, Redis, Adminer)
cd docker
docker compose -f docker-compose-infra.yml up -d

# Adminer(DB 관리도구) 접속: http://localhost:8081
# PostgreSQL 접속정보: edu / edu1234 / edu_spring

# 각 챕터 실행
cd ../chapter04-spring-boot-intro
docker compose up --build
```

### Chapter 09: 종합 프로젝트

```bash
cd chapter09-final-project
docker compose up --build

# API 테스트
curl -X POST http://localhost:8080/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"password123"}'

curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"password123"}'
```

---

## 프로젝트 구조

```
edu_spring/
├── Dockerfile                     # Java 챕터 통합 실행 (Ch01~03)
├── run.sh                         # 대화형 예제 실행 스크립트
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
├── chapter09-final-project/       # 종합 프로젝트 (게시판)
├── chapter-cs-algorithms/         # [CS] 자료구조와 알고리즘 (실행 예제)
├── chapter-cs-concurrency/        # [CS] 운영체제와 동시성 (실행 예제)
├── chapter-cs-database/           # [CS] 데이터베이스 이론 (SQL 랩)
├── chapter-cs-network/            # [CS] 컴퓨터 네트워크 (관찰 데모)
├── compile.sh                     # Ch01~03 Java 컴파일 스크립트
├── docker-compose.yml             # Ch01~03 Java 통합 실행
├── .devcontainer/                 # VS Code Dev Container 설정
└── docs/                          # 참고 문서
    ├── JAVA_개념서/README.md
    ├── JAVA_교육자료.md
    ├── JAVA_코딩표준.md
    ├── SPRING_개념서/README.md
    ├── SPRING_교육자료.md
    ├── 학습계획.md / 학습계획.csv
    ├── 트러블슈팅.md              # 자주 겪는 오류 해결 가이드
    ├── 추가자료_안내.md           # .http 파일 등 보조 자료 안내
    └── 맥북_Docker_실습가이드.md
```

> **API 테스트**: 각 Spring 챕터(05~07, 09)에는 `requests.http` 파일이 있어 VS Code REST Client
> 또는 IntelliJ HTTP Client로 curl 없이 API를 호출/테스트할 수 있습니다. (`docs/추가자료_안내.md` 참고)
> 막히는 부분이 있으면 `docs/트러블슈팅.md`를 먼저 확인하세요.

## 기술 스택
- **Language**: Java 21
- **Framework**: Spring Boot 3.4.3
- **Database**: PostgreSQL 16
- **Cache**: Redis 7
- **Security**: Spring Security + JWT
- **Testing**: JUnit 5, Mockito, Testcontainers
- **Container**: Docker, Docker Compose
- **Build**: Gradle 8.8
