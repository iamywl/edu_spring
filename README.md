# Java & Spring Boot 교육 과정

[![CI](https://github.com/iamywl/edu_spring/actions/workflows/ci.yml/badge.svg)](https://github.com/iamywl/edu_spring/actions/workflows/ci.yml)

Java 기초부터 Spring Boot 실전까지 단계별로 학습하는 교육 자료입니다.
**두 개의 독립 프로젝트**로 나뉘어 있고, 각자 자기 Docker 환경을 가집니다.

```
edu_spring/
├── java/     ← ① 자바 샌드박스: 개념당 스크립트 1개, 컨테이너에서 바로 실행
├── spring/   ← ② Spring Boot 실전: 챕터별 앱 + 공통 인프라(PostgreSQL/Redis)
└── docs/     ← 공용 문서 (트러블슈팅, 학습계획, 맥북 가이드)
```

## 어디서 시작하나요?

| 단계 | 프로젝트 | 시작 방법 |
|------|---------|----------|
| **1. 자바를 배운다** | [java/](java/README.md) | `cd java && docker compose up -d` → 컨테이너 `java-sandbox`에 붙어 `./run.sh` |
| **2. Spring을 배운다** | [spring/](spring/README.md) | `cd spring/docker && docker compose -f docker-compose-infra.yml up -d` → 챕터별 실행 |

두 프로젝트는 완전히 분리되어 있습니다 — 자바만 배울 때는 `spring/`을 몰라도 되고,
`java/`는 폴더 전체가 컨테이너 `/app`에 통마운트되는 단순한 샌드박스입니다.
(마운트 경로 상세는 [java/README.md](java/README.md)의 "마운트 경로" 절 참고)

## 사전 준비
- Docker & Docker Compose (이것만 있으면 됩니다 — JDK 설치 불필요)
- IDE (VS Code 권장) + Git

## 공용 문서
- [docs/트러블슈팅.md](docs/트러블슈팅.md) — 자주 겪는 오류 해결
- [docs/맥북_Docker_실습가이드.md](docs/맥북_Docker_실습가이드.md)
- [docs/학습계획.md](docs/학습계획.md)
