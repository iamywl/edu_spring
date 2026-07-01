-- =====================================================================
-- 실습 02. 트랜잭션과 ACID: 원자성(Atomicity)을 직접 실험
-- =====================================================================
--
-- 목표:
--   1) BEGIN / COMMIT / ROLLBACK 이 무엇을 하는지 눈으로 본다.
--   2) 원자성(Atomicity): "전부 성공 아니면 전부 실패" 를 계좌 이체로 확인.
--   3) 일관성(Consistency): 제약조건 위반이 트랜잭션 전체를 롤백시키는 것을 확인.
--
-- 실행 방법:
--   docker exec -i docker-postgres-1 psql -U edu -d edu_spring < labs/02_transactions_acid.sql
--
-- 참고: 이 스크립트는 psql 한 세션에서 순서대로 실행되도록 작성됐다.
--       PostgreSQL 에서 트랜잭션 도중 에러가 나면 그 트랜잭션은
--       "aborted(중단됨)" 상태가 되어 COMMIT 해도 자동으로 ROLLBACK 된다.
--       (이 성질 자체가 원자성의 핵심 안전장치다.)
--
-- 개념서 참조: docs/CS_데이터베이스_개념서.md 3장(트랜잭션과 ACID)
--             docs/SPRING_개념서.md 4.5절(@Transactional 과 ACID)
-- =====================================================================

DROP TABLE IF EXISTS account CASCADE;

-- 계좌 테이블. balance 는 음수가 될 수 없다는 제약(CHECK)을 건다.
-- 이 CHECK 제약이 곧 '일관성(Consistency)'의 규칙이다.
CREATE TABLE account (
    id      TEXT PRIMARY KEY,
    owner   TEXT NOT NULL,
    balance INT  NOT NULL CHECK (balance >= 0)   -- 잔액은 0 이상이어야 함
);

INSERT INTO account (id, owner, balance) VALUES
    ('A', '김철수', 100000),
    ('B', '이영희',  50000);

SELECT * FROM account ORDER BY id;   -- A: 100000, B: 50000


-- ---------------------------------------------------------------------
-- 실험 1. 정상 이체: A -> B 로 30,000원. COMMIT 으로 확정.
-- ---------------------------------------------------------------------
-- 이 두 UPDATE 는 '하나의 작업'이어야 한다.
-- 하나만 반영되면 돈이 사라지거나 복제된다. 그래서 트랜잭션으로 묶는다.
BEGIN;

    UPDATE account SET balance = balance - 30000 WHERE id = 'A';  -- 출금
    UPDATE account SET balance = balance + 30000 WHERE id = 'B';  -- 입금

COMMIT;   -- 여기서 두 변경이 '동시에' 확정된다.

-- 확인: A 70000, B 80000. 합계는 그대로 150000 (돈이 새거나 늘지 않았다).
SELECT id, owner, balance FROM account ORDER BY id;


-- ---------------------------------------------------------------------
-- 실험 2. 원자성: 중간에 명시적으로 ROLLBACK 하면 전부 취소된다.
-- ---------------------------------------------------------------------
BEGIN;

    UPDATE account SET balance = balance - 50000 WHERE id = 'A';  -- A 에서 5만 출금
    -- 여기서 "아, 취소해야겠다" 라고 판단했다고 하자.

ROLLBACK;   -- 위 UPDATE 는 없던 일이 된다.

-- 확인: A 는 여전히 70000. 롤백된 출금은 반영되지 않았다.
SELECT id, owner, balance FROM account ORDER BY id;


-- ---------------------------------------------------------------------
-- 실험 3. 일관성 위반으로 인한 자동 롤백:
--         잔액보다 많이 출금 -> CHECK(balance >= 0) 위반 -> 트랜잭션 전체 실패
-- ---------------------------------------------------------------------
-- 시나리오: A(70000) 에서 B 로 200,000원을 이체하려 한다.
--   1번째 UPDATE(출금)에서 balance 가 -130000 이 되어 CHECK 제약을 위반한다.
--   PostgreSQL 은 그 순간 에러를 내고 트랜잭션을 aborted 상태로 만든다.
--   따라서 2번째 UPDATE(입금)는 실행되지 못하고, COMMIT 해도 롤백된다.
--   => "출금은 됐는데 입금은 안 된" 어중간한 상태가 원천적으로 불가능하다 (원자성).
BEGIN;

    UPDATE account SET balance = balance - 200000 WHERE id = 'A';
    -- ↑ 이 줄에서 다음과 같은 에러가 발생한다:
    --   ERROR:  new row for relation "account" violates check constraint "account_balance_check"
    --   이후 이 트랜잭션 안의 모든 명령은 아래 에러로 무시된다:
    --   ERROR:  current transaction is aborted, commands ignored until end of transaction block

    UPDATE account SET balance = balance + 200000 WHERE id = 'B';  -- 실행되지 못함

COMMIT;   -- 트랜잭션이 이미 aborted 이므로 COMMIT 은 사실상 ROLLBACK 으로 처리된다.

-- 확인: A 70000, B 80000. 실험 3은 통째로 없던 일이 됐다.
SELECT id, owner, balance FROM account ORDER BY id;


-- ---------------------------------------------------------------------
-- 실험 4. SAVEPOINT: 트랜잭션 안에서 부분 롤백 (부분 취소)
-- ---------------------------------------------------------------------
-- 큰 트랜잭션 안에서 일부만 되돌리고 싶을 때 SAVEPOINT 를 쓴다.
-- (Spring 의 중첩 트랜잭션 PROPAGATION.NESTED 가 내부적으로 이걸 사용한다.)
BEGIN;

    UPDATE account SET balance = balance - 10000 WHERE id = 'A';   -- A: 70000 -> 60000
    SAVEPOINT sp1;   -- 되돌아올 지점 표시

    UPDATE account SET balance = balance - 99999999 WHERE id = 'A';  -- 이건 취소하고 싶다
    -- CHECK 위반이 나면 sp1 이후만 롤백하면 된다. 아래처럼 되돌린다.
    ROLLBACK TO SAVEPOINT sp1;   -- sp1 이후 변경만 취소 (첫 UPDATE 는 유지)

COMMIT;

-- 확인: A 60000 (첫 출금 1만원만 반영, 문제의 UPDATE 는 부분 롤백됨), B 80000.
SELECT id, owner, balance FROM account ORDER BY id;


-- ---------------------------------------------------------------------
-- 요약
-- ---------------------------------------------------------------------
--   Atomicity(원자성)    : BEGIN..COMMIT 으로 묶인 작업은 전부 or 전무. (실험 2, 3)
--   Consistency(일관성)  : CHECK/NOT NULL/FK 같은 제약을 어기는 커밋은 거부된다. (실험 3)
--   Isolation(격리성)    : 동시 트랜잭션 간 간섭 -> 03_isolation_levels.sql 에서 다룬다.
--   Durability(지속성)   : COMMIT 이 성공하면 정전에도 살아남는다 (WAL 로그 덕분).
--
-- Spring 에서는 이 BEGIN/COMMIT/ROLLBACK 을 @Transactional 이 자동으로 감싸준다.
-- 즉 이 실습의 원자성이 곧 @Transactional 메서드가 예외 시 전체 롤백하는 원리다.
--
-- 실습 끝. 다음: 03_isolation_levels.sql
