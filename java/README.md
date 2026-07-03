# Java 실습 샌드박스

자바 코드를 **설치 없이 Docker 컨테이너 안에서 바로 실행**하며 배우는 트랙입니다.
JDK를 로컬에 깔 필요 없이, 컨테이너(`java-sandbox`) 하나면 됩니다.

## 빠른 시작

```bash
cd java                     # 이 디렉토리에서
docker compose up -d        # 샌드박스 컨테이너(java-sandbox) 띄우기 (최초 1회)

# VS Code로 접속 (추천)
#   F1 → "Dev Containers: Attach to Running Container" → java-sandbox 선택 → 폴더 /app 열기
#   또는 이 java/ 폴더를 VS Code로 열고 "Reopen in Container"

# 컨테이너 터미널에서:
./compile.sh                # 전체 컴파일
./run.sh                    # 대화형 메뉴 (카테고리 → 개념 선택)
./run.sh VariablesAndTypes  # 클래스 이름으로 바로 실행
./run.sh all                # 전체 실행

# 터미널에서 직접 실행할 수도 있습니다
docker exec -it java-sandbox ./run.sh SortingAlgorithms

docker compose down         # 종료
```

## 마운트 경로 (호스트 ↔ 컨테이너)

`java/` 디렉토리 **전체가 통마운트**됩니다. 부분 마운트가 아니므로 헷갈릴 것이 없습니다 —
호스트에서 보이는 파일 = 컨테이너에서 보이는 파일입니다.

| 호스트 (내 컴퓨터) | 컨테이너 (java-sandbox) | 설명 |
|---|---|---|
| `java/` | `/app` | 전체 통마운트 (수정 즉시 반영) |
| `java/chapter01-java-basics/src/...` | `/app/chapter01-java-basics/src/...` | 소스 코드 |
| `java/docs/` | `/app/docs/` | 개념서·교재 |
| `java/run.sh`, `java/compile.sh` | `/app/run.sh`, `/app/compile.sh` | 실행/컴파일 스크립트 |
| (컴파일 결과) | `/app/out/` | `.class` 출력 — 호스트 `java/out/`에 생기며 git에는 올라가지 않음 |

> VS Code에서 파일을 저장하면 컨테이너에 바로 반영됩니다. `docker build`를 다시 할 필요 없이
> `./compile.sh`만 다시 실행하면 됩니다. 상세한 실행/디버깅 방법: [docs/VSCode_실행_디버깅_가이드.md](docs/VSCode_실행_디버깅_가이드.md)

## 학습 경로 (두 가지 트랙)

| 트랙 | 무엇 | 언제 |
|------|------|------|
| **① 자바 교재 (이것이 자바다 목차)** | [docs/JAVA_교재/](docs/JAVA_교재/README.md) 개념 + [chapter-java-textbook/](chapter-java-textbook/) 소절별 실습 176개. **목차 소절 = 스크립트 1개** | 자바를 **넓고 체계적으로** 훑을 때 (`./run.sh` → `j) 자바 교재`) |
| **② 개념서 + 기초 챕터** | [docs/JAVA_교재/심화/](docs/JAVA_교재/심화/README.md)("왜 그렇게 동작하는가") + [chapter01~03](chapter01-java-basics/) 실행 데모 | 원리를 **깊게** 파고들 때 |

| 챕터 | 주제 | 핵심 개념 |
|------|------|-----------|
| 01 | [Java 기초](chapter01-java-basics/) | 변수, 타입, 제어문, 배열, 메서드, 예외처리 |
| 02 | [객체지향 프로그래밍](chapter02-oop/) | 클래스, 상속, 다형성, 인터페이스, record, sealed class |
| 03 | [컬렉션과 스트림](chapter03-collections/) | 제네릭, List/Set/Map, Stream API, Lambda, Optional |

### CS 기반 심화 트랙

| 트랙 | 개념서 | 실습 |
|------|--------|------|
| 자료구조·알고리즘 | [docs/CS_알고리즘_개념서/](docs/CS_알고리즘_개념서/README.md) | [chapter-cs-algorithms/](chapter-cs-algorithms/) — `./run.sh SortingAlgorithms` 등 6종 |
| 운영체제·동시성 | [docs/CS_운영체제_개념서/](docs/CS_운영체제_개념서/README.md) | [chapter-cs-concurrency/](chapter-cs-concurrency/) — 경쟁 상태·동기화·교착 + **심화: `VolatileVisibilityDemo`(가시성), `AtomicCasDemo`(CAS), `ReentrantLockDemo`(Lock/Condition), `CompletableFutureDemo`(비동기)** |
| 데이터베이스 이론 | [docs/CS_데이터베이스_개념서/](docs/CS_데이터베이스_개념서/README.md) | [chapter-cs-database/](chapter-cs-database/) — 전용 PostgreSQL SQL 랩 (`cd chapter-cs-database && docker compose up -d`, 컨테이너 `db-lab-postgres`) |
| 컴퓨터 네트워크 | [docs/CS_네트워크_개념서/](docs/CS_네트워크_개념서/README.md) | [chapter-cs-network/](chapter-cs-network/) — `./run.sh RawHttpClient`(원시 소켓), **`HttpClientExample`(표준 HttpClient)** |

## 프로젝트 구조

```
java/
├── Dockerfile                  # 샌드박스 이미지 (JDK 21)
├── docker-compose.yml          # java/ 전체를 /app에 통마운트
├── compile.sh / run.sh         # 컴파일·실행 스크립트 (개념당 스크립트 1개)
├── .devcontainer/              # VS Code Dev Container 설정
├── chapter01-java-basics/      # Java 기초 (README = 개념서)
├── chapter02-oop/              # 객체지향
├── chapter03-collections/      # 컬렉션·제네릭·함수형·스트림
├── chapter-java-textbook/      # 자바 교재 실습 (Ch02~21, 소절별)
├── chapter-cs-algorithms/      # [CS] 자료구조·알고리즘
├── chapter-cs-concurrency/     # [CS] 운영체제·동시성
├── chapter-cs-database/        # [CS] DB 이론 SQL 랩 (전용 PostgreSQL 포함)
├── chapter-cs-network/         # [CS] 네트워크
├── docs/                       # 자바 교재·심화 개념서·CS 개념서
└── out/                        # 컴파일 결과 (자동 생성, git 제외)
```

## 학습 흐름

1. 챕터 `README.md` / `docs/` 개념서를 읽으며 **왜(Why)** 필요한지 이해
2. `./run.sh <클래스명>`으로 예제를 실행해 **결과를 눈으로 확인**
3. 소스를 직접 수정하고 `./compile.sh` → `./run.sh`로 변화 확인
4. 자바가 끝나면 [../spring/](../spring/README.md) 트랙으로

> 막히면 [../docs/트러블슈팅.md](../docs/트러블슈팅.md)를 먼저 확인하세요.

## 기술 스택
- **Language**: Java 21 (컨테이너에 JDK 포함 — 로컬 설치 불필요)
- **Container**: Docker, Docker Compose
