-- =====================================================================
-- 실습 03. 격리 수준(Isolation Level): 두 세션으로 이상 현상 관찰하기
-- =====================================================================
--
-- 이 실습은 "한 방에 실행"되지 않는다.
-- 반드시 psql 세션을 **두 개** 열고, 아래 [세션 A] / [세션 B] 순서대로
-- 손으로 번갈아 실행해야 한다. (한쪽이 트랜잭션을 열어 둔 상태에서
--  다른 쪽이 무엇을 보는지가 이 실습의 핵심이다.)
--
-- 두 세션 여는 법 (터미널 창 2개):
--   터미널1:  docker exec -it docker-postgres-1 psql -U edu -d edu_spring
--   터미널2:  docker exec -it docker-postgres-1 psql -U edu -d edu_spring
--   (컨테이너 이름은 `docker ps` 로 확인. 기본값 docker-postgres-1)
--
-- PostgreSQL 기본 격리 수준 = READ COMMITTED.
--   -> Dirty Read(더티 리드)는 기본적으로 '발생하지 않는다'.
--   -> 하지만 Non-repeatable Read(반복 불가능한 읽기)는 여전히 발생한다.
--
-- 개념서 참조: docs/CS_데이터베이스_개념서.md 4장(동시성 제어)
--             docs/SPRING_개념서.md 4.6절(격리 수준과 이상 현상)
-- =====================================================================


-- =====================================================================
-- [준비] 아무 세션에서나 한 번만 실행해 테이블을 만든다.
-- =====================================================================
DROP TABLE IF EXISTS account CASCADE;
CREATE TABLE account (
    id      TEXT PRIMARY KEY,
    balance INT NOT NULL
);
INSERT INTO account (id, balance) VALUES ('A', 100000);
-- 준비 끝. 이제 아래 시나리오대로 두 세션을 번갈아 실행한다.



-- #####################################################################
-- 시나리오 1) Dirty Read 가 READ COMMITTED 에서는 '발생하지 않음'을 확인
-- #####################################################################
-- 더티 리드 = 다른 트랜잭션이 아직 COMMIT 하지 않은 값을 읽어버리는 것.
-- READ COMMITTED 는 "커밋된 값만 읽는다"를 보장하므로 이걸 막는다.
--
--  실행 순서 (위에서 아래로, 세션을 번갈아):
--
--  (1) [세션 A]
--        BEGIN;
--        UPDATE account SET balance = 999999 WHERE id = 'A';   -- 아직 COMMIT 안 함!
--        -- A 는 자기 트랜잭션 안에서는 999999 를 본다.
--        SELECT balance FROM account WHERE id = 'A';           -- => 999999 (본인만 보임)
--
--  (2) [세션 B]   (A 가 아직 COMMIT 하지 않은 상태에서)
--        SELECT balance FROM account WHERE id = 'A';
--        -- 결과: 100000  <-- A 가 바꾼 999999 가 '보이지 않는다'.
--        --       READ COMMITTED 이므로 커밋 안 된 값(더티 데이터)을 읽지 않는다.
--        --       => Dirty Read 가 발생하지 않았다! (이것이 정상)
--
--  (3) [세션 A]
--        ROLLBACK;   -- A 가 변경을 취소한다. 999999 는 존재한 적 없는 값이 된다.
--        -- 만약 (2)에서 B 가 999999 를 읽었다면, 존재하지도 않을 값을
--        -- 읽은 셈이 되어 큰일이었을 것이다. READ COMMITTED 가 그걸 막아줬다.
--
--  (4) [세션 B]
--        SELECT balance FROM account WHERE id = 'A';   -- => 100000 (변함없이 안전)
--
-- 결론: PostgreSQL 기본값(READ COMMITTED)에서 Dirty Read 는 원천 차단된다.



-- #####################################################################
-- 시나리오 2) Non-repeatable Read 가 READ COMMITTED 에서는 '발생함'을 확인
-- #####################################################################
-- 반복 불가능한 읽기 = 한 트랜잭션 안에서 같은 행을 두 번 읽었는데,
-- 그 사이 다른 트랜잭션이 값을 바꾸고 COMMIT 해서 두 결과가 달라지는 것.
-- READ COMMITTED 는 '매 SELECT 마다 그 순간 커밋된 최신값'을 보므로 이게 생긴다.
--
--  먼저 값을 초기화 (아무 세션에서):
--        UPDATE account SET balance = 100000 WHERE id = 'A';
--
--  실행 순서:
--
--  (1) [세션 A]
--        BEGIN;   -- 기본 격리 수준(READ COMMITTED)으로 시작
--        SELECT balance FROM account WHERE id = 'A';   -- 1번째 읽기 => 100000
--        -- (아직 A 의 트랜잭션은 열려 있다. 커밋하지 않고 잠깐 멈춘다.)
--
--  (2) [세션 B]
--        BEGIN;
--        UPDATE account SET balance = 55555 WHERE id = 'A';
--        COMMIT;   -- B 는 값을 바꾸고 '커밋까지' 완료했다.
--
--  (3) [세션 A]   (같은 트랜잭션을 아직 안 끝낸 상태에서 다시 읽는다)
--        SELECT balance FROM account WHERE id = 'A';   -- 2번째 읽기 => 55555 (!)
--        -- 같은 트랜잭션 안에서 같은 행을 두 번 읽었는데 100000 -> 55555 로 달라졌다.
--        -- => Non-repeatable Read 발생! (READ COMMITTED 에서는 정상적인 동작)
--        COMMIT;
--
-- 왜 이게 문제인가? A 가 "잔액을 확인 -> 계산 -> 다시 확인" 하는 로직이라면,
-- 중간에 값이 바뀌어 계산이 어긋날 수 있다. 이런 게 곤란한 업무는
-- 격리 수준을 REPEATABLE READ 로 올려야 한다 (시나리오 3).



-- #####################################################################
-- 시나리오 3) REPEATABLE READ 로 올리면 Non-repeatable Read 가 사라진다
-- #####################################################################
-- REPEATABLE READ 는 "트랜잭션 시작 시점의 스냅샷"을 트랜잭션 내내 고정해서 본다.
-- 그래서 도중에 남이 바꿔 커밋해도, 이 트랜잭션은 계속 처음 본 값을 본다.
-- (PostgreSQL 은 MVCC 로 각 트랜잭션에게 자기 버전 스냅샷을 준다.)
--
--  값 초기화 (아무 세션에서):
--        UPDATE account SET balance = 100000 WHERE id = 'A';
--
--  실행 순서:
--
--  (1) [세션 A]
--        BEGIN;
--        SET TRANSACTION ISOLATION LEVEL REPEATABLE READ;   -- 격리 수준을 올린다
--        SELECT balance FROM account WHERE id = 'A';   -- 1번째 읽기 => 100000
--        -- (커밋하지 않고 멈춘다. 이 시점의 스냅샷이 고정된다.)
--
--  (2) [세션 B]
--        BEGIN;
--        UPDATE account SET balance = 77777 WHERE id = 'A';
--        COMMIT;   -- B 가 바꾸고 커밋
--
--  (3) [세션 A]   (같은 트랜잭션에서 다시 읽는다)
--        SELECT balance FROM account WHERE id = 'A';   -- 2번째 읽기 => 100000 (!)
--        -- B 가 77777 로 바꿔 커밋했지만, A 는 여전히 처음 본 100000 을 본다.
--        -- => Non-repeatable Read 가 발생하지 않는다! (스냅샷 고정)
--        COMMIT;
--
--  (4) [세션 A]   (트랜잭션을 끝낸 뒤 새 트랜잭션/조회)
--        SELECT balance FROM account WHERE id = 'A';   -- => 77777 (이제 최신값)
--
-- 정리 (개념서 4장의 매트릭스와 일치):
--   READ COMMITTED  : Dirty X, Non-repeatable O, Phantom O   <- PostgreSQL 기본
--   REPEATABLE READ : Dirty X, Non-repeatable X, Phantom X*  (*PG는 MVCC로 팬텀도 대부분 차단)
--   SERIALIZABLE    : 모두 X (직렬 실행처럼 보장, 대신 동시성 비용 최대)
--
-- 참고: 이 실습을 마치면 두 세션 모두 트랜잭션을 COMMIT/ROLLBACK 으로 닫아
--       열린 트랜잭션이 남지 않게 하자 (열린 트랜잭션은 다른 작업을 막을 수 있다).
--
-- 실습 끝. 다음: 04_indexing_explain.sql
