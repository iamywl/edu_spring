# 3장. 트랜잭션과 ACID

> "Spring의 `@Transactional`이 ACID를 보장한다고 했다. 그렇다면 A·C·I·D 네 글자는 각각 정확히 무엇을 약속하는가? 그리고 그 약속이 깨지면 무슨 일이 일어나는가?"

---

## 도입: 왜 트랜잭션이 필요한가

은행 계좌 이체를 생각해보자. 김철수(계좌 A, 잔액 100,000원)가 이영희(계좌 B, 잔액 50,000원)에게 30,000원을 이체한다. 이 작업은 데이터베이스 수준에서 두 개의 `UPDATE`다.

```sql
UPDATE account SET balance = balance - 30000 WHERE id = 'A';  -- (1) 출금
UPDATE account SET balance = balance + 30000 WHERE id = 'B';  -- (2) 입금
```

만약 (1)이 실행된 직후 서버가 다운된다면? A의 잔액은 70,000원으로 줄었지만 B의 잔액은 50,000원 그대로다. 30,000원이 공중에서 사라진다. 이 상황을 **부분 실패(partial failure)**라 하고, 이것이 트랜잭션 없는 데이터베이스의 핵심 위험이다.

```
[부분 실패 시나리오]

  서버
  ────────────────────────────────────────────
  UPDATE A: 100000 → 70000  ✓ 성공
  ────────── 💥 서버 크래시 ──────────────────
  UPDATE B: 50000  → 80000  ✗ 실행 안 됨

  최종 상태: A=70000, B=50000, 총합=120000 (30,000원 증발!)
```

트랜잭션은 이 두 `UPDATE`를 **하나의 불가분 단위**로 묶어, 둘 다 성공하거나 둘 다 없었던 일이 되도록 보장한다. 그 보장의 내용을 **ACID**라는 네 가지 성질로 정의한다.

이 장은 ACID 각각의 의미와 DB가 그것을 구현하는 방법을 실패 시나리오와 함께 파헤친다. Spring 개념서 4.5절이 "`@Transactional`이 ACID를 보장한다"는 결과를 가르쳤다면, 이 장은 **그 보장의 내부 메커니즘**을 다룬다.

**실습 파일:** `chapter-cs-database/labs/02_transactions_acid.sql`

---

## 3.1 트랜잭션이란 무엇인가

**트랜잭션(transaction)**은 데이터베이스에서 하나의 논리적 작업 단위로 취급해야 하는 연산들의 묶음이다. 핵심 특성은 **분할 불가능성**이다. 묶음 안의 연산은 모두 반영되거나(commit), 모두 취소되어야(rollback) 하며, 중간 상태는 영구적으로 남아서는 안 된다.

SQL에서는 `BEGIN`과 `COMMIT`/`ROLLBACK`으로 경계를 명시한다.

```sql
BEGIN;
    UPDATE account SET balance = balance - 30000 WHERE id = 'A';
    UPDATE account SET balance = balance + 30000 WHERE id = 'B';
COMMIT;   -- 두 UPDATE가 '동시에' 영구 반영됨
```

`ROLLBACK`을 실행하거나 장애가 발생하면 `BEGIN` 이후의 모든 변경이 취소된다.

```sql
BEGIN;
    UPDATE account SET balance = balance - 50000 WHERE id = 'A';
    -- 어떤 이유로 취소 결정
ROLLBACK;   -- 위 UPDATE는 없던 일이 된다
```

PostgreSQL에서 `BEGIN`을 명시하지 않으면 각 SQL 문이 자동으로 자신만의 트랜잭션이 된다(**자동 커밋 모드, autocommit**). 두 `UPDATE`를 하나의 트랜잭션으로 묶으려면 반드시 `BEGIN`이 필요하다.

### 트랜잭션 상태 전이도

트랜잭션은 다음과 같은 상태를 거친다.

```
                 첫 연산 실행
    ┌──────┐  ──────────────────▶  ┌────────┐
    │ 초기 │                       │ Active │
    └──────┘                       └────────┘
                                       │
                    ┌──────────────────┼──────────────────┐
                    │ 모든 연산 성공    │                   │ 오류 발생
                    ▼                  │                   ▼
          ┌──────────────────┐         │           ┌────────────┐
          │ Partially        │         │           │   Failed   │
          │ Committed        │         │           └────────────┘
          └──────────────────┘         │                   │ ROLLBACK / 복구
                    │                  │                   ▼
     fsync 완료 ✓   │        자원 부족 / │           ┌──────────────┐
     (WAL 기록됨)   │        하드웨어 오류│           │   Aborted    │
                    ▼                  │           └──────────────┘
            ┌────────────────┐         │
            │   Committed    │ ◀───────┘ (불가)
            └────────────────┘
```

- **Active**: 트랜잭션이 연산을 수행 중
- **Partially Committed**: 마지막 연산이 끝났으나 아직 로그가 디스크에 기록되지 않음
- **Committed**: 변경이 영구 반영됨
- **Failed**: 더 이상 정상 진행이 불가능한 상태
- **Aborted**: 롤백이 완료되어 데이터베이스가 트랜잭션 이전 상태로 복원됨

이 상태 전이가 ACID 네 성질의 논리적 기반이다.

---

## 3.2 Atomicity — 원자성: 전부 아니면 전무

### 개념

**원자성(Atomicity)**은 트랜잭션 안의 연산이 **모두 성공하거나, 모두 실패하여 취소**되어야 한다는 성질이다. "원자(atom)"라는 이름처럼 더 이상 쪼갤 수 없는 단위로 다뤄진다.

부분 성공 상태는 허용되지 않는다. 계좌 이체에서 출금만 되고 입금이 안 된 상태, 주문에서 재고 차감만 되고 결제 기록이 없는 상태 — 이 모두가 원자성 위반의 결과다.

### 원리: Undo 로그

DB는 어떻게 "없었던 일"로 만드는가? **undo 로그(undo log)**가 핵심이다.

트랜잭션이 행을 변경할 때, DB는 **변경 전의 값(before-image)**을 로그에 기록해 둔다. 롤백이 필요할 때 이 로그를 역순으로 재생하여 데이터를 원래 상태로 되돌린다.

```
[Undo 로그 동작 원리]

BEGIN 시점:
  undo log: (빈 상태)
  account A: balance = 100000

UPDATE account SET balance = 70000 WHERE id = 'A' 실행:
  undo log: [A, balance, old=100000]  ← 이전 값 기록
  account A: balance = 70000 (메모리/버퍼에만)

ROLLBACK 시:
  undo log를 역순 재생:
  account A: balance = 100000 으로 복원 ✓
```

커밋이 아닌 장애(크래시)로 트랜잭션이 중단된 경우, DB는 재시작 시 Aborted 상태의 트랜잭션을 undo 로그로 롤백한다. **장애가 발생해도 부분 반영 상태는 남지 않는다.**

### SQL 예제

```sql
-- 실험 2: 명시적 ROLLBACK으로 원자성 확인
BEGIN;
    UPDATE account SET balance = balance - 50000 WHERE id = 'A';
    -- 비즈니스 로직에서 오류 감지 → 취소 결정
ROLLBACK;

-- A의 잔액은 변하지 않음
SELECT id, owner, balance FROM account ORDER BY id;
```

```sql
-- 실험 3: CHECK 제약 위반 → 자동 롤백
-- A(70000)에서 20만 원 출금 시도 → balance가 음수가 되어 제약 위반
BEGIN;
    UPDATE account SET balance = balance - 200000 WHERE id = 'A';
    -- ERROR: new row violates check constraint "account_balance_check"
    -- 이후 이 트랜잭션의 모든 명령은 무시됨:
    -- ERROR: current transaction is aborted, commands ignored until end of transaction block
    UPDATE account SET balance = balance + 200000 WHERE id = 'B';  -- 실행되지 않음
COMMIT;  -- 사실상 ROLLBACK으로 처리됨
```

PostgreSQL에서 오류가 발생한 트랜잭션은 즉시 **Aborted 상태**가 되어, 이후의 모든 명령이 무시된다. `COMMIT`을 실행해도 자동으로 `ROLLBACK`으로 전환된다. 이것이 원자성의 핵심 안전장치다.

### SQL 랩 연결

```bash
cd chapter-cs-database
docker exec -i docker-postgres-1 psql -U edu -d edu_spring < labs/02_transactions_acid.sql
```

실험 2(ROLLBACK), 실험 3(CHECK 위반 자동 롤백)을 관찰한다. 각 실험 후 `SELECT`로 잔액이 변하지 않음을 확인하라.

---

## 3.3 Consistency — 일관성: 제약은 항상 참이어야 한다

### 개념

**일관성(Consistency)**은 트랜잭션 실행 전후로 데이터베이스가 미리 정의된 **무결성 규칙(integrity constraints)**을 항상 만족해야 한다는 성질이다.

무결성 규칙의 예:
- `balance CHECK (balance >= 0)`: 잔액은 음수가 될 수 없다
- `UNIQUE (email)`: 이메일은 중복될 수 없다
- `FOREIGN KEY (dept_id) REFERENCES department(id)`: 존재하지 않는 부서를 참조할 수 없다
- `NOT NULL`: 필수 컬럼은 null일 수 없다

트랜잭션 도중에는 일시적으로 제약을 위반한 상태가 될 수 있다(예: 출금 직후 입금 직전). 그러나 **커밋 시점에는 반드시 모든 제약이 참이어야** 한다.

### 원리: 원자성과의 협력

일관성은 홀로 구현되지 않는다. 제약 위반이 발생하면 DB가 **오류를 발생시켜** 트랜잭션을 Aborted 상태로 만들고, 원자성이 그 트랜잭션 전체를 롤백시킨다. 일관성은 "어떤 상태가 올바른가"를 정의하고, 원자성은 "올바르지 않은 상태가 영구 반영되지 않도록" 보장한다.

```
[일관성 + 원자성의 협력]

트랜잭션 시작: balance_A=70000, balance_B=80000
  → 제약 CHECK(balance>=0) 참 ✓

UPDATE balance_A: 70000 - 200000 = -130000
  → CHECK 위반 감지! ← 일관성이 감시
  → 트랜잭션 Aborted    ← DB가 오류 발생
  → 전체 ROLLBACK       ← 원자성이 취소

트랜잭션 종료: balance_A=70000, balance_B=80000
  → 제약 CHECK(balance>=0) 참 ✓ (변화 없음)
```

### SQL 예제

```sql
CREATE TABLE account (
    id      TEXT PRIMARY KEY,
    owner   TEXT NOT NULL,
    balance INT  NOT NULL CHECK (balance >= 0)
);

-- 잔액 부족 이체 시도
BEGIN;
    UPDATE account SET balance = balance - 200000 WHERE id = 'A';
    -- ERROR:  new row for relation "account" violates check constraint "account_balance_check"
COMMIT;  -- ROLLBACK으로 처리됨

-- 잔액 확인: 변화 없음
SELECT id, owner, balance FROM account ORDER BY id;
```

### DB가 마지막 방어선이다

애플리케이션 레벨에서 "잔액 >= 출금액"을 체크해도, 동시에 두 요청이 들어오거나 애플리케이션 버그가 있을 경우 그 체크는 우회될 수 있다. DB의 `CHECK` 제약은 **SQL 레벨의 마지막 방어선**이다. Spring에서 `@Transactional` 메서드가 예외를 던지면 롤백되는 것도, 결국 이 메커니즘을 Spring이 감싸는 것이다.

> **왜 비즈니스 규칙을 DB에 넣어야 하는가?**
> 애플리케이션 코드는 언제든 버그가 생기고, 마이그레이션 스크립트가 직접 INSERT할 때, DBA가 psql로 직접 쿼리할 때 모두 애플리케이션 로직을 우회한다. DB 제약은 어떤 경로로 접근해도 적용된다.

---

## 3.4 Isolation — 격리성: 혼자 도는 것처럼 보이게

### 개념

**격리성(Isolation)**은 동시에 실행되는 여러 트랜잭션이 서로의 **중간 상태를 볼 수 없도록** 해야 한다는 성질이다. 각 트랜잭션은 마치 데이터베이스를 독점하여 혼자 실행되는 것처럼 동작해야 한다.

예를 들어 이체 트랜잭션의 중간 상태(A 출금 완료, B 입금 대기)를 다른 트랜잭션이 읽으면, 그 트랜잭션은 실제로 존재하지 않아야 할 데이터를 근거로 의사결정을 내리게 된다.

### 완벽한 격리 vs 현실

완벽한 격리(직렬화, Serializable)는 동시에 실행되는 트랜잭션을 하나씩 순서대로 실행한 것과 동일한 결과를 보장한다. 그러나 동시성이 줄어들어 **성능 비용**이 크다.

실무에서는 완벽한 격리를 포기하는 대신 **격리 수준(isolation level)**을 낮춰 성능을 얻는다. 그 트레이드오프에서 발생하는 문제들을 **이상 현상(anomaly)**이라 하며, 4장에서 상세히 다룬다.

---

## 3.5 Durability — 지속성: 커밋은 정전에도 살아남는다

### 개념

**지속성(Durability)**은 일단 커밋된 트랜잭션의 결과는 이후 어떤 장애(정전, 크래시, 재시작)가 발생해도 **영구적으로 보존**되어야 한다는 성질이다.

"COMMIT가 성공했다면, 그 변경은 영원히 반영되어 있다"는 약속이다.

### 원리: WAL(Write-Ahead Logging)

지속성을 구현하는 핵심 기법이 **WAL(Write-Ahead Logging, 선행 기록 로깅)**이다.

**핵심 규칙**: 실제 데이터 파일을 수정하기 전에, "무엇을 어떻게 바꿀 것인지"를 **로그 파일에 먼저 기록**하고 디스크에 안전하게 저장(`fsync`)한다. 커밋은 로그가 디스크에 안착하면 성공으로 선언된다.

```
[WAL 파이프라인]

트랜잭션 수행 중:
  ┌──────────────────────────────────────────────────────┐
  │  메모리(shared buffer)                               │
  │  ┌──────────────────┐    ┌──────────────────────┐   │
  │  │ 데이터 페이지     │    │   WAL 버퍼           │   │
  │  │ account A: 70000 │    │ LSN#42: UPDATE A     │   │
  │  │ (변경됨, dirty)   │    │         100000→70000 │   │
  │  └──────────────────┘    └──────────────────────┘   │
  └──────────────────────────────────────────────────────┘
           │ (나중에 체크포인트 시)              │ COMMIT 시 즉시
           ▼                                   ▼ fsync
  ┌──────────────────┐              ┌─────────────────────┐
  │  데이터 파일      │              │   WAL 파일 (pg_wal) │
  │  (디스크)        │              │   (디스크, 안전)    │
  └──────────────────┘              └─────────────────────┘

COMMIT 성공 선언: WAL이 디스크에 기록된 순간
  → 데이터 파일은 아직 수정 안 됐어도 OK
  → 정전 시 재시작 후 WAL을 재생하여 복구 가능
```

### 왜 로그를 먼저 쓰는가?

**순차 쓰기(sequential write)가 랜덤 쓰기(random write)보다 훨씬 빠르기** 때문이다.

데이터 파일의 여러 페이지를 수정하면 디스크의 여기저기 랜덤 위치에 써야 한다. 반면 WAL은 단조 증가하는 로그 파일에 **순서대로 append**하기만 한다. HDD에서 순차 쓰기는 랜덤 쓰기보다 수십 배 빠르다. SSD에서도 순차 쓰기가 유리하다.

```
[랜덤 쓰기 vs 순차 쓰기 비교]

랜덤 쓰기 (데이터 파일 직접 수정):
  디스크: [페이지7] ... [페이지23] ... [페이지2] ... [페이지91]
           ↑ 여기       ↑ 여기        ↑ 여기       ↑ 여기
  → 헤드가 이리저리 이동 → 느림

순차 쓰기 (WAL 로그):
  디스크: [WAL: rec1][WAL: rec2][WAL: rec3][WAL: rec4] ...
                                                         ↑ 여기에 append
  → 항상 끝에만 추가 → 빠름
```

### Redo 로그와 복구

WAL 레코드는 **변경 후의 값(after-image)**을 포함한다. 이를 **redo 로그**라 한다. 정전 후 재시작 시:

1. WAL 레코드를 처음부터 재생하여 커밋된 트랜잭션의 변경을 데이터 파일에 반영
2. 완료되지 않은(Aborted/Active) 트랜잭션은 undo 로그로 롤백

```
[Undo vs Redo 로그]

          역할            포함 정보         사용 시점
  ─────────────────────────────────────────────────────
  Undo  원자성/롤백용    변경 전 값(before-image)  ROLLBACK, 크래시 후 미완료 TX 취소
  Redo  지속성/복구용    변경 후 값(after-image)   크래시 후 커밋된 TX 재적용
```

### ARIES 복구 알고리즘

현대 DB(Oracle, SQL Server, PostgreSQL 등)의 복구는 **ARIES(Algorithm for Recovery and Isolation Exploiting Semantics)**를 따른다. 세 단계로 구성된다.

```
[ARIES 복구 3단계]

1. Analysis(분석 단계)
   → WAL을 스캔하여 크래시 시점의 활성 트랜잭션과
     dirty(수정됐으나 아직 디스크에 기록 안 된) 페이지 목록을 파악

2. Redo(재실행 단계)
   → WAL을 처음부터 재생하여 모든 변경을 데이터 파일에 반영
     (커밋됐든 아니든 일단 다 적용 — 이것이 ARIES의 특징)

3. Undo(취소 단계)
   → 크래시 시 진행 중이던(커밋 안 된) 트랜잭션을 역순으로 롤백
     → 원자성 보장
```

### 체크포인트(Checkpoint)

WAL 파일이 무한히 쌓이면 복구 시 처음부터 재생해야 한다. **체크포인트**는 주기적으로 메모리의 dirty 페이지를 디스크에 플러시하고 "여기까지는 안전하다"는 표시를 WAL에 남기는 작업이다. 다음 재시작 시 마지막 체크포인트 이후의 WAL만 재생하면 된다.

```
[체크포인트와 WAL 관계]

WAL: ──[chkpt#1]──[LSN100]──[LSN200]──[chkpt#2]──[LSN300]──[크래시]
                                                    ↑
                                               재시작 시 여기서부터 재생
```

### PostgreSQL의 WAL 구현

```sql
-- WAL 파일 위치 확인 (관리자 권한)
SELECT pg_current_wal_lsn();          -- 현재 WAL 위치(LSN)
SELECT pg_walfile_name(pg_current_wal_lsn());  -- WAL 파일명

-- 동기화 커밋 설정 확인 (지속성 vs 성능 트레이드오프)
SHOW synchronous_commit;
-- on       : 커밋 시 WAL이 디스크에 fsync됨 (기본값, 지속성 보장)
-- off      : WAL 버퍼에 기록 후 바로 리턴 (성능↑, 장애 시 최대 수십ms치 손실 가능)
-- remote_write, remote_apply : 복제 슬레이브와 관련
```

PostgreSQL의 WAL 파일은 `$PGDATA/pg_wal/` 디렉터리에 위치하며, 기본 크기는 16MB 세그먼트다.

**그룹 커밋(Group Commit)**: 여러 트랜잭션이 동시에 커밋 대기 중일 때, PostgreSQL은 이들을 그룹으로 묶어 한 번의 `fsync`로 처리한다. 개별 `fsync` 비용을 여러 트랜잭션이 나눠 부담하므로 처리량(throughput)이 크게 향상된다.

---

## 3.6 SAVEPOINT — 부분 롤백

트랜잭션 전체를 롤백하지 않고 일부만 취소하고 싶을 때 **SAVEPOINT**를 사용한다.

### 개념

`SAVEPOINT 이름`으로 현재 지점을 표시하고, `ROLLBACK TO SAVEPOINT 이름`으로 그 지점 이후의 변경만 취소한다. `COMMIT`하면 롤백되지 않은 모든 변경이 영구 반영된다.

```
[SAVEPOINT 상태 전이]

BEGIN
  │
  ▼ UPDATE A: 10만→9만
  │
  ├─── SAVEPOINT sp1 ──────────────────────┐
  │                                        │ (복귀 지점)
  ▼ UPDATE A: 9만 → 음수 시도             │
  │                                        │
  ├─── ROLLBACK TO SAVEPOINT sp1 ──────────┘
  │    (sp1 이후 변경만 취소, 9만→10만 복원)
  │    (sp1 이전 변경 유지: 10만→9만)
  ▼
COMMIT (A: 9만이 영구 반영)
```

### SQL 예제

```sql
-- 실험 4: SAVEPOINT로 부분 롤백
BEGIN;
    UPDATE account SET balance = balance - 10000 WHERE id = 'A';  -- A: 70000 → 60000
    SAVEPOINT sp1;  -- 되돌아올 지점 표시

    UPDATE account SET balance = balance - 99999999 WHERE id = 'A';  -- 잘못된 업데이트
    -- CHECK 위반 발생 시:
    ROLLBACK TO SAVEPOINT sp1;  -- sp1 이후만 취소, 첫 UPDATE는 유지

COMMIT;  -- A: 60000 (첫 출금 1만 원만 반영)

SELECT id, owner, balance FROM account ORDER BY id;
-- 결과: A=60000, B=80000
```

### Spring과의 연결

Spring의 `@Transactional(propagation = Propagation.NESTED)`은 내부적으로 `SAVEPOINT`를 사용한다. 중첩 트랜잭션(nested transaction)이 실패하면 그 지점까지만 롤백되고, 외부 트랜잭션은 계속 진행할 수 있다.

```java
// Spring: PROPAGATION.NESTED가 내부적으로 SAVEPOINT 사용
@Transactional
public void outerService() {
    innerService();  // 이 메서드가 실패해도 outerService는 계속 가능
    // (SAVEPOINT로 내부만 롤백)
}

@Transactional(propagation = Propagation.NESTED)
public void innerService() {
    // 실패 시 ROLLBACK TO SAVEPOINT로 이 메서드 범위만 취소
}
```

Spring의 트랜잭션 전파(propagation) 전체 목록은 `../SPRING_개념서/README.md` 4.5절을 참조하라. 여기서는 SAVEPOINT와 가장 직접적으로 연결되는 `NESTED`만 다룬다.

### SQL 랩 연결

```bash
cd chapter-cs-database
docker exec -i docker-postgres-1 psql -U edu -d edu_spring < labs/02_transactions_acid.sql
```

실험 4(SAVEPOINT)를 관찰한다. `ROLLBACK TO SAVEPOINT sp1` 후 `COMMIT`하면 첫 번째 `UPDATE`만 반영되고 두 번째는 취소된 상태로 확정된다.

---

## 3.7 ACID 전체 조망

```
[ACID 요약]

┌─────────────────┬──────────────────────────────────────────────────────────────┐
│ 성질            │ 약속                                                          │
├─────────────────┼──────────────────────────────────────────────────────────────┤
│ Atomicity       │ 트랜잭션 안의 모든 연산은 전부 반영되거나 전부 취소된다.      │
│ (원자성)        │ 구현: Undo 로그, ROLLBACK, 크래시 복구                       │
├─────────────────┼──────────────────────────────────────────────────────────────┤
│ Consistency     │ 트랜잭션 전후로 무결성 제약이 항상 참이어야 한다.            │
│ (일관성)        │ 구현: CHECK/NOT NULL/FK/UNIQUE 제약 + 원자성                 │
├─────────────────┼──────────────────────────────────────────────────────────────┤
│ Isolation       │ 동시 트랜잭션은 서로의 중간 상태를 볼 수 없어야 한다.        │
│ (격리성)        │ 구현: 락(Lock), MVCC → 4장에서 상세히 다룸                  │
├─────────────────┼──────────────────────────────────────────────────────────────┤
│ Durability      │ 커밋된 데이터는 정전에도 보존된다.                           │
│ (지속성)        │ 구현: WAL(Write-Ahead Logging), Redo 로그, fsync             │
└─────────────────┴──────────────────────────────────────────────────────────────┘
```

---

## ⚠️ 흔한 오해와 함정

**오해 1: "COMMIT을 호출하면 즉시 데이터가 디스크에 저장된다"**

엄밀히 말하면, COMMIT 시 **WAL 레코드**가 디스크에 저장되는 것이다. 실제 데이터 파일(shared buffer의 dirty 페이지)은 나중에 체크포인트 시 기록된다. 그러나 WAL이 있으면 크래시 후 복구가 가능하므로 지속성은 보장된다.

**오해 2: "Consistency(일관성)는 DB가 자동으로 다 챙긴다"**

DB가 체크하는 것은 **선언된 제약**(CHECK, NOT NULL, FK 등)뿐이다. "주문 금액이 재고 수량 × 단가를 초과하지 않는다"처럼 복잡한 비즈니스 규칙은 애플리케이션 또는 트리거로 직접 구현해야 한다. ACID의 Consistency는 애플리케이션이 올바른 트랜잭션을 작성한다는 전제 하에만 동작한다.

**오해 3: "autocommit 모드에서는 ROLLBACK이 불가능하다"**

PostgreSQL의 autocommit은 각 SQL 문이 암묵적 트랜잭션으로 실행되는 것이다. `BEGIN`으로 트랜잭션을 명시적으로 시작하면 autocommit은 해당 트랜잭션이 끝날 때까지 일시 중단된다. JDBC, Spring에서 `@Transactional`이 하는 일도 사실상 `BEGIN`을 발행하고 종료 시 `COMMIT`/`ROLLBACK`을 호출하는 것이다.

**오해 4: "SAVEPOINT가 있으면 트랜잭션을 세분화해서 부분 커밋할 수 있다"**

`ROLLBACK TO SAVEPOINT`는 **부분 롤백**이지, **부분 커밋**이 아니다. SAVEPOINT로 나뉜 부분이 독립적으로 커밋되지는 않는다. 최종 `COMMIT`이나 `ROLLBACK`은 트랜잭션 전체에 적용된다. 진정한 부분 커밋을 원하면 트랜잭션을 별도로 분리해야 한다.

**오해 5: "`synchronous_commit = off`로 설정하면 지속성이 파괴된다"**

`synchronous_commit = off`는 WAL의 `fsync`를 지연시켜 **최대 수십 ms 동안의 데이터 손실** 가능성을 허용한다. 이는 ACID의 완전한 지속성을 희생하는 것이다. 단, 데이터 손실 시에도 **DB 자체는 일관된 상태**로 남는다(일부 커밋된 트랜잭션만 사라질 뿐 반쪽짜리 커밋은 발생하지 않는다). 로그나 세션 데이터처럼 손실이 허용되는 데이터에만 사용해야 한다.

---

## 연습문제

**문제 1.** 아래 시나리오에서 어떤 ACID 성질이 어떻게 작동하는지 설명하라.

> 쇼핑몰에서 상품 A를 5개 주문하는 트랜잭션이 다음을 수행한다:
> 1. `orders` 테이블에 주문 레코드 INSERT
> 2. `inventory` 테이블에서 상품 A의 재고를 5 감소
>
> (2)에서 재고가 3개밖에 없어 `CHECK (stock >= 0)` 제약이 위반된다. 최종 상태는 어떻게 되는가?

<details>
<summary>힌트 및 해설</summary>

- **Atomicity**: (1)과 (2)가 하나의 트랜잭션으로 묶여 있다면, (2)에서 제약 위반이 발생하면 (1)도 함께 취소된다. 주문 레코드는 남지 않는다.
- **Consistency**: `CHECK (stock >= 0)` 제약이 (2)의 UPDATE를 거부한다. DB가 무결성을 강제한다.
- 최종 상태: 주문도 없고 재고도 변하지 않는다. 트랜잭션은 Aborted.

만약 (1)과 (2)가 `BEGIN`으로 묶이지 않은 별도의 SQL 문이라면, (1)이 커밋된 후 (2)가 실패하여 주문은 있으나 재고는 줄지 않는 불일치가 발생한다. 이것이 트랜잭션으로 묶어야 하는 이유다.
</details>

---

**문제 2.** WAL에서 "로그를 먼저 쓴다"는 원칙의 이유를 다음 두 관점에서 설명하라: (a) 정확성(correctness) 관점, (b) 성능(performance) 관점.

<details>
<summary>힌트 및 해설</summary>

(a) **정확성**: 데이터 파일에 변경을 먼저 쓰다가 크래시가 나면, 어떤 변경이 완전히 적용됐는지 알 방법이 없다. WAL이 먼저 기록되어 있으면, 재시작 후 WAL을 재생하여 일관된 상태로 복구할 수 있다.

(b) **성능**: 데이터 파일의 여러 페이지를 수정하려면 디스크의 다양한 위치에 랜덤 쓰기가 필요하다. WAL은 단조 증가하는 파일에 순차적으로 append하므로 훨씬 빠르다. 커밋은 WAL fsync만으로 완료되고 데이터 파일 기록은 나중에 체크포인트 시 처리된다.
</details>

---

**문제 3.** 다음 코드는 Spring에서 계좌 이체를 구현한다. 어떤 문제가 있으며, 어떻게 수정해야 하는가?

```java
public void transfer(String fromId, String toId, int amount) {
    // @Transactional 없음
    accountRepository.debit(fromId, amount);   // UPDATE 출금
    if (someCondition) throw new RuntimeException("오류!");
    accountRepository.credit(toId, amount);    // UPDATE 입금
}
```

<details>
<summary>힌트 및 해설</summary>

**문제**: `@Transactional`이 없으므로 각 `save/update` 호출이 별도의 트랜잭션으로 autocommit된다. 예외가 발생하면 `debit`(출금)은 이미 커밋된 상태이고, `credit`(입금)은 실행되지 않아 돈이 사라진다. 원자성 위반.

**수정**:
```java
@Transactional
public void transfer(String fromId, String toId, int amount) {
    accountRepository.debit(fromId, amount);
    if (someCondition) throw new RuntimeException("오류!");  // 예외 시 전체 롤백
    accountRepository.credit(toId, amount);
}
```

`@Transactional`이 `BEGIN`을 발행하고, 예외 발생 시 `ROLLBACK`을 실행하여 출금까지 취소한다. Spring 개념서 4.5절 참조.
</details>

---

**문제 4.** ARIES의 Redo 단계는 커밋되지 않은 트랜잭션의 변경도 일단 재적용한다. 그런데도 커밋되지 않은 변경이 영구 반영되지 않는 이유는 무엇인가?

<details>
<summary>힌트 및 해설</summary>

ARIES의 Redo 단계는 "손실된 변경을 없애지 않기 위해" 커밋 여부와 무관하게 WAL을 재생한다. 이후 **Undo 단계**에서 커밋되지 않은(크래시 시 진행 중이던) 트랜잭션을 undo 로그로 역순 롤백한다. 따라서 최종적으로 커밋된 트랜잭션의 변경만 남는다. Redo와 Undo의 두 단계가 협력하여 원자성과 지속성을 동시에 보장한다.
</details>

---

**문제 5.** `SAVEPOINT`를 사용하면 "부분 커밋"이 가능하다는 설명이 있다. 이 설명이 틀린 이유를 설명하고, `SAVEPOINT`의 정확한 동작을 서술하라.

<details>
<summary>힌트 및 해설</summary>

`SAVEPOINT`는 **부분 롤백**을 지원하지, 부분 커밋을 지원하지 않는다. `ROLLBACK TO SAVEPOINT sp1`은 sp1 이후의 변경만 취소할 뿐이며, 최종 `COMMIT`은 취소되지 않은 모든 변경을 한 번에 영구 반영한다. sp1 이전의 변경이 독립적으로 커밋되는 것이 아니다. 트랜잭션의 원자성은 전체 `BEGIN~COMMIT` 단위로 적용된다.
</details>

---

## 요약

- **트랜잭션**은 전부 성공하거나 전부 실패해야 하는 논리적 작업 단위다.
- **Atomicity(원자성)**: undo 로그와 ROLLBACK으로 부분 실패를 원천 차단한다.
- **Consistency(일관성)**: DB 제약(CHECK, FK 등)이 트랜잭션 전후에 항상 참임을 보장하며, 원자성과 협력한다.
- **Isolation(격리성)**: 동시 트랜잭션이 서로의 중간 상태를 볼 수 없게 한다. 락과 MVCC로 구현하며 4장에서 상세히 다룬다.
- **Durability(지속성)**: WAL(Write-Ahead Logging)이 커밋된 변경을 정전에도 보존한다. 로그 우선 기록 원칙과 fsync가 핵심이다.
- **Undo 로그**는 원자성(롤백)을, **Redo 로그(WAL)**는 지속성(복구)을 각각 담당한다.
- **ARIES**: Analysis → Redo → Undo 세 단계로 크래시 후 완벽한 복구를 수행한다.
- **SAVEPOINT**: 트랜잭션 안의 부분 롤백을 지원하며, Spring의 `PROPAGATION.NESTED`가 내부적으로 사용한다.
- Spring의 `@Transactional`은 이 모든 메커니즘의 `BEGIN`/`COMMIT`/`ROLLBACK` 흐름을 자동으로 감싼다. 상세한 Spring 연결은 `../SPRING_개념서/README.md` 4.5절을 참조하라.

---

[← 이전 장: 2장 정규화](02-정규화.md) | [다음 장: 4장 동시성 제어 →](04-동시성-제어.md)
