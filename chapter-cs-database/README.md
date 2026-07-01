# CS 데이터베이스 트랙 (Database Theory)

> "JPA/Spring 챕터는 데이터베이스를 **사용하는 법**을 가르친다. 이 트랙은 그 아래에 있는 **데이터베이스 자체를 하나의 CS 과목**으로 판다. 비전공자가 전공자 수준의 DB 이해에 도달하는 것이 목표다."

이 트랙은 기존 챕터(Java/Spring)와 독립적으로, **관계형 데이터베이스의 이론**을 다룬다. 개념은 **개념서**로 읽고, 각 주제를 **실제 PostgreSQL에 SQL을 돌려** 눈으로 확인한다.

---

## 학습 목표

이 트랙을 마치면 다음을 **설명하고 직접 실습**할 수 있다.

- **관계 모델**: 관계/튜플/애트리뷰트, 키(슈퍼키/후보키/기본키/외래키), 관계형이 대안보다 나은 이유
- **정규화**: 함수적 종속, 1NF→2NF→3NF→BCNF, 갱신/삽입/삭제 이상 현상을 만들고 없애기
- **트랜잭션 & ACID**: 원자성/일관성/격리성/지속성을 실패 시나리오로 이해, `BEGIN/COMMIT/ROLLBACK`
- **동시성 제어**: 격리 수준과 3대 이상 현상(더티/반복불가/팬텀), 락(공유/배타)·2PL·교착, MVCC, 낙관적 vs 비관적 락
- **인덱싱**: 전체 스캔 O(n) vs 인덱스 O(log n), B+tree, 복합/커버링 인덱스, `EXPLAIN (ANALYZE)` 읽기
- **쿼리 처리**: 논리→물리 계획, 조인 알고리즘(nested loop/hash/merge)
- **JPA 연결**: 이 모든 이론이 `@Entity`/`@Column`/`@ManyToOne`/`@Transactional` 뒤에서 어떻게 작동하는가

---

## 자료 구성

| 자료 | 위치 | 내용 |
|------|------|------|
| 개념서 | [`docs/CS_데이터베이스_개념서.md`](../docs/CS_데이터베이스_개념서.md) | 이론 전체 (7개 장, 다이어그램·"왜?"·JPA 상호참조) |
| 실습 01 | [`labs/01_normalization.sql`](labs/01_normalization.sql) | 정규화: 이상 현상 시연 → 단계별 정규화 |
| 실습 02 | [`labs/02_transactions_acid.sql`](labs/02_transactions_acid.sql) | 트랜잭션/ACID: 원자성·일관성 실험 |
| 실습 03 | [`labs/03_isolation_levels.sql`](labs/03_isolation_levels.sql) | 격리 수준: **두 세션**으로 이상 현상 관찰 |
| 실습 04 | [`labs/04_indexing_explain.sql`](labs/04_indexing_explain.sql) | 인덱싱: `EXPLAIN ANALYZE`로 Seq Scan→Index Scan |

> **추천 순서:** 개념서 각 장을 읽고 → 대응하는 실습 SQL을 돌린다.
> 2장→lab01, 3장→lab02, 4장→lab03, 5장→lab04.
> (개념서는 Spring 개념서 4장의 JPA와 4.5~4.8절을 전제로 하며, 중복 없이 이론을 더 깊게 확장한다.)

---

## 사전 준비: 실습용 PostgreSQL 띄우기

이 트랙의 SQL은 이 프로젝트의 **인프라 PostgreSQL 16**에 대고 돌린다.
(DB: `edu_spring`, 사용자: `edu`, 비밀번호: `edu1234`, 포트: `5432`)

```bash
# 프로젝트 루트에서 (docker daemon이 켜져 있어야 함)
cd docker
docker compose -f docker-compose-infra.yml up -d postgres
# adminer(웹 DB 도구)도 같이 쓰려면:
docker compose -f docker-compose-infra.yml up -d postgres adminer
```

컨테이너 이름을 확인한다 (compose 기본 프로젝트명이 디렉터리명 `docker`이므로 보통 `docker-postgres-1`):

```bash
docker ps --format '{{.Names}}\t{{.Image}}\t{{.Ports}}'
# 예) docker-postgres-1   postgres:16-alpine   0.0.0.0:5432->5432/tcp
```

> 아래 명령의 `docker-postgres-1`은 위에서 **실제로 확인한 이름**으로 바꿔 쓴다.
> (환경에 따라 `docker_postgres_1`, `edu_spring-postgres-1` 등일 수 있다.)

---

## 실습 실행 방법

### 방법 A) SQL 파일을 통째로 실행 (실습 01, 02, 04)

`psql`에 파일을 리다이렉트로 흘려 넣는다. `-i`(stdin 연결)에 주의.

```bash
# 프로젝트 루트 기준. chapter-cs-database 로 이동해서 실행하면 상대경로가 깔끔하다.
cd chapter-cs-database

docker exec -i docker-postgres-1 psql -U edu -d edu_spring < labs/01_normalization.sql
docker exec -i docker-postgres-1 psql -U edu -d edu_spring < labs/02_transactions_acid.sql
docker exec -i docker-postgres-1 psql -U edu -d edu_spring < labs/04_indexing_explain.sql
```

출력에서 각 `SELECT` 결과와 (실습 04의) `EXPLAIN ANALYZE` 계획을 읽으며 개념서와 대조한다.

### 방법 B) 대화형 psql 세션에서 한 줄씩 (권장 — 결과를 천천히 관찰)

```bash
docker exec -it docker-postgres-1 psql -U edu -d edu_spring
```
접속 후 파일 내용을 복사해 붙여 넣거나, `\i` 메타명령으로 실행할 수 있다.
(단, `\i`는 컨테이너 내부 경로 기준이라 파일을 컨테이너로 복사해야 하므로, 붙여넣기가 더 간단하다.)

세션 종료는 `\q`.

### 방법 C) Adminer(웹 UI)로 실행

브라우저에서 **http://localhost:8081** 접속 후:
```
시스템(System):   PostgreSQL
서버(Server):     postgres          (compose 네트워크 내부 서비스명)
사용자(Username): edu
비밀번호:         edu1234
데이터베이스:     edu_spring
```
로그인 후 **SQL 명령(SQL command)** 메뉴에 SQL을 붙여 넣고 실행. 결과가 표로 보인다.
(단, 실습 03의 두 세션 시나리오는 Adminer로는 어려우니 아래 psql 방식을 쓴다.)

---

## 실습 03만 특별: **두 개의 psql 세션**이 필요하다

`labs/03_isolation_levels.sql`은 "한 트랜잭션이 열려 있는 동안 다른 트랜잭션이 무엇을 보는가"를 관찰하는 실습이라, **파일을 통째로 실행하면 안 된다.** 터미널 창을 **두 개** 열어야 한다.

```bash
# 터미널 1 (세션 A)
docker exec -it docker-postgres-1 psql -U edu -d edu_spring

# 터미널 2 (세션 B)  ← 새 터미널 창에서
docker exec -it docker-postgres-1 psql -U edu -d edu_spring
```

그다음 `labs/03_isolation_levels.sql` 파일의 주석에 적힌 순서대로,
**[세션 A] 표시가 붙은 명령은 터미널 1에**, **[세션 B]는 터미널 2에** 손으로 번갈아 입력한다.
(예: A가 `BEGIN; UPDATE ...`로 트랜잭션을 열어 둔 상태에서, B가 `SELECT`로 무엇이 보이는지 확인하는 식.)

이 실습으로 다음을 직접 눈으로 본다:
- READ COMMITTED(PostgreSQL 기본)에서 **Dirty Read가 발생하지 않음**
- 같은 설정에서 **Non-repeatable Read는 발생함**
- `SET TRANSACTION ISOLATION LEVEL REPEATABLE READ`로 올리면 **Non-repeatable Read가 사라짐**

---

## 정리 (실습 후)

각 실습 SQL은 시작할 때 `DROP TABLE IF EXISTS ...`로 자기 테이블을 정리하므로, 여러 번 반복 실행해도 안전하다. 인프라 전체를 내리려면:

```bash
cd docker
docker compose -f docker-compose-infra.yml down          # 컨테이너만 정지/삭제 (데이터 볼륨 유지)
# 데이터까지 완전 초기화하려면:
docker compose -f docker-compose-infra.yml down -v        # 볼륨(postgres_data)까지 삭제
```

> 이 트랙은 기존 `run.sh` / `docker-compose.yml` / 다른 챕터를 건드리지 않는다.
> 실습은 오직 `docker/docker-compose-infra.yml`의 PostgreSQL에 대고 돈다.
