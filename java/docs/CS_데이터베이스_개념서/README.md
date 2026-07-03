# CS 데이터베이스 개념서 — 전공 교재판

> "JPA는 데이터베이스를 '가려주는' 기술이다. 그런데 가려진 그 뒤에 무엇이 있는지 모르면, JPA가 만들어내는 SQL이 왜 느린지, 왜 데이터가 꼬이는지 영영 알 수 없다. 이 책은 그 **가려진 데이터베이스 자체**를 하나의 CS 과목으로 파고든다."

> **🐳 실습 환경 — 이 책의 SQL 랩은 `db-lab-postgres` 컨테이너에서 실행한다**
> ```bash
> cd java/chapter-cs-database && docker compose up -d   # DB + Adminer(8082) 켜기
> docker exec -i db-lab-postgres psql -U edu -d edu_spring < labs/01_normalization.sql
> ```
> 상세한 실행 방법(대화형 psql, Adminer 접속, 세션 2개짜리 격리 수준 랩 등)은 아래 "SQL 랩 실행 안내" 절 참고.

---

## 이 책은 무엇인가

이 책은 관계형 데이터베이스를 **하나의 전공 CS 과목**으로 다루는 교재다. 비전공자가 전공자 수준의 DB 이해에 도달하는 것을 목표로, 관계 모델의 수학적 토대부터 함수 종속과 정규형의 증명, 트랜잭션의 실패 시나리오, 락과 MVCC의 내부 메커니즘, B+tree 인덱스와 실행계획, 조인 알고리즘의 비용 모델까지를 **왜 그런가**의 관점에서 파고든다.

이 프로젝트의 Spring 파트(`docs/SPRING_개념서/README.md` 4장)는 **JPA/ORM의 사용법과 메커니즘**을 가르친다. Entity, Repository, 연관관계, 그리고 4.5~4.8절에서 트랜잭션/격리/인덱스를 **JPA 관점에서** 다룬다. 이 책은 그 아래, 데이터베이스를 CS 과목으로서 다룬다. Spring 개념서가 "`@Transactional`이 ACID를 보장한다"고 했다면, 이 책은 "**ACID란 정확히 무엇이고, DB가 그것을 어떻게 구현하는가**"를 판다. 중복은 피하고, 각 장 끝에서 Spring 개념서로 **상호 참조**를 건다.

---

## 학습 목표

이 책을 마치면 다음을 **설명하고, 직접 SQL로 실습하고, 화이트보드에 증명**할 수 있다.

- **관계 모델**: 관계/튜플/애트리뷰트의 수학적 정의, 키(슈퍼키/후보키/기본키/외래키), 관계 대수, 관계형이 대안보다 나은 이유
- **정규화**: 함수 종속과 암스트롱 공리, 폐포(closure)로 후보키 찾기, 1NF→2NF→3NF→BCNF의 정의와 증명, 무손실 분해와 종속성 보존
- **트랜잭션 & ACID**: 원자성/일관성/격리성/지속성을 실패 시나리오와 WAL/undo·redo 로그로 이해
- **동시성 제어**: 스케줄과 직렬가능성(충돌/뷰), 3대 이상 현상, 락(공유/배타)·2PL·교착·타임스탬프 순서화·MVCC 스냅샷, 낙관적 vs 비관적 락
- **인덱싱**: 전체 스캔 O(n) vs 인덱스 O(log n), B-tree vs B+tree, 복합/커버링/부분 인덱스, 선택도와 옵티마이저, `EXPLAIN (ANALYZE)` 읽기
- **쿼리 처리**: 논리→물리 계획, 조인 알고리즘(nested loop/hash/merge)의 비용 모델, 통계와 옵티마이저
- **JPA 연결**: 이 모든 이론이 `@Entity`/`@Column`/`@ManyToOne`/`@Transactional` 뒤에서 어떻게 작동하는가

---

## 목차

| 장 | 제목 | 한 줄 설명 |
|----|------|-----------|
| [1장](01-관계-모델.md) | 관계 모델 (The Relational Model) | 테이블이 왜 '행과 열'인가 — 커드의 수학 이론, 키, 관계 대수 |
| [2장](02-정규화.md) | 정규화 (Normalization) | 함수 종속과 정규형으로 이상 현상을 체계적으로 제거하기 |
| [3장](03-트랜잭션과-ACID.md) | 트랜잭션과 ACID | 전부 성공 or 전부 실패 — ACID를 실패 시나리오와 로그로 파헤치기 |
| [4장](04-동시성-제어.md) | 동시성 제어 (Concurrency Control) | 격리 수준·락·2PL·타임스탬프·MVCC — 동시에 돌 때 데이터를 지키는 법 |
| [5장](05-인덱싱.md) | 인덱싱 (Indexing) | B+tree와 실행계획 — WHERE가 왜 빠른지 눈으로 확인하기 |
| [6장](06-쿼리-처리.md) | 쿼리 처리 기초 (Query Processing) | 선언형 SQL이 물리 계획으로 번역되는 과정과 조인 비용 모델 |
| [7장](07-JPA로-돌아오기.md) | JPA로 돌아오기 — 이론과 ORM의 연결 | 애노테이션 한 줄이 이 책의 어떤 이론을 부르는가 |

---

## 추천 학습 순서

1. **Spring 개념서 4장**으로 JPA가 뭘 하는지 먼저 감을 잡는다.
2. **1~2장**(관계 모델, 정규화)으로 "테이블을 어떻게 설계하는가"를 배운다.
3. **3~4장**(트랜잭션, 동시성)으로 Spring 4.5~4.6절의 이론적 토대를 다진다.
4. **5~6장**(인덱싱, 쿼리 처리)으로 Spring 4.7절을 깊게 확장한다.
5. **7장**으로 이론과 JPA 애노테이션을 한 장의 지도로 잇는다.
6. 각 장을 읽을 때마다 대응하는 **SQL 랩**을 실제 PostgreSQL에 돌려 본다.

각 장은 독립적으로도 읽히지만, 정규화(2장)는 관계 모델(1장)을, 동시성(4장)은 트랜잭션(3장)을, 쿼리 처리(6장)는 인덱싱(5장)을 전제로 한다.

---

## SQL 랩 실행 안내

이 책의 이론은 **손으로 돌려봐야 진짜 내 것이 된다.** 각 장은 `chapter-cs-database/labs/`의 SQL 실습과 연결된다.

| 장 | SQL 랩 | 실행 방식 |
|----|--------|-----------|
| 2장 (정규화) | `labs/01_normalization.sql` | 파일 통째 실행 |
| 3장 (ACID) | `labs/02_transactions_acid.sql` | 파일 통째 실행 |
| 4장 (격리) | `labs/03_isolation_levels.sql` | **psql 세션 2개** 번갈아 입력 |
| 5장 (인덱스) | `labs/04_indexing_explain.sql` | 파일 통째 실행 (`EXPLAIN ANALYZE` 관찰) |

**대상 DB:** PostgreSQL 16 (DB 랩 전용 `java/chapter-cs-database/docker-compose.yml`).
DB `edu_spring`, 사용자 `edu`, 비밀번호 `edu1234`, 호스트 포트 `5433` (spring 인프라의 5432와 충돌하지 않는다).

### 1) 실습용 PostgreSQL 띄우기

```bash
# 리포 루트에서 (docker daemon이 켜져 있어야 함)
cd java/chapter-cs-database
docker compose up -d
```

컨테이너 이름은 compose 파일에 **고정**되어 있다: PostgreSQL은 `db-lab-postgres`, Adminer(웹 DB 도구, http://localhost:8082)는 `db-lab-adminer`. 아래 명령들을 그대로 복사해 쓰면 된다.

### 2) SQL 파일 통째로 실행 (랩 01, 02, 04)

```bash
cd java/chapter-cs-database
docker exec -i db-lab-postgres psql -U edu -d edu_spring < labs/01_normalization.sql
docker exec -i db-lab-postgres psql -U edu -d edu_spring < labs/02_transactions_acid.sql
docker exec -i db-lab-postgres psql -U edu -d edu_spring < labs/04_indexing_explain.sql
```

### 3) 대화형 psql 세션 (권장 — 결과를 천천히 관찰)

```bash
docker exec -it db-lab-postgres psql -U edu -d edu_spring
```

### 4) 랩 03만 특별: **두 개의 psql 세션**이 필요하다

`labs/03_isolation_levels.sql`은 "한 트랜잭션이 열려 있는 동안 다른 트랜잭션이 무엇을 보는가"를 관찰하므로 **파일을 통째로 실행하면 안 된다.** 터미널 창을 두 개 열어야 한다.

```bash
# 터미널 1 (세션 A)
docker exec -it db-lab-postgres psql -U edu -d edu_spring
# 터미널 2 (세션 B)  ← 새 터미널 창에서
docker exec -it db-lab-postgres psql -U edu -d edu_spring
```

파일 주석의 순서대로 `[세션 A]`는 터미널 1에, `[세션 B]`는 터미널 2에 번갈아 입력한다.

### 5) 정리 (실습 후)

각 랩 SQL은 시작할 때 `DROP TABLE IF EXISTS ...`로 자기 테이블을 정리하므로 반복 실행해도 안전하다. 랩 DB를 내리려면:

```bash
cd java/chapter-cs-database
docker compose down          # 컨테이너만 정지/삭제
docker compose down -v       # 데이터까지 완전 초기화
```

> 이 트랙은 기존 `run.sh` / `java/docker-compose.yml` / 다른 챕터를 건드리지 않는다. 실습은 오직 DB 랩 전용 `java/chapter-cs-database/docker-compose.yml`의 PostgreSQL(`db-lab-postgres`)에 대고 돈다. 랩 실행 방법의 상세 버전은 [`chapter-cs-database/README.md`](../../chapter-cs-database/README.md)에도 있다.

---

## 관련 문서

- [`docs/SPRING_개념서/README.md`](../../../spring/docs/SPRING_개념서/README.md) 4장 — JPA/ORM의 사용법 (이 책의 이론이 노출되는 표면)
- [`docs/CS_알고리즘_개념서/README.md`](../CS_알고리즘_개념서/README.md) — B+tree의 배경이 되는 트리/탐색 이론
- [`docs/CS_운영체제_개념서/README.md`](../CS_운영체제_개념서/README.md) — WAL/디스크 I/O·락·교착의 OS 관점
- [`chapter-cs-database/`](../../chapter-cs-database/) — SQL 실습 랩

---

**첫 장으로 →** [1장: 관계 모델](01-관계-모델.md)
