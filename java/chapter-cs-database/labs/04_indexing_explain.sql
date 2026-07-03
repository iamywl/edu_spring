-- =====================================================================
-- 실습 04. 인덱스와 EXPLAIN ANALYZE: Seq Scan -> Index Scan 을 눈으로 보기
-- =====================================================================
--
-- 목표:
--   1) 인덱스 없이 조회하면 Seq Scan(전체 훑기, O(n))이 나온다.
--   2) 인덱스를 만들면 Index Scan(O(log n))으로 바뀌고 실행 시간이 확 줄어든다.
--   3) 복합 인덱스의 '왼쪽 접두사(leftmost-prefix)' 규칙을 확인한다.
--   4) EXPLAIN (ANALYZE) 를 읽는 법을 익힌다.
--
-- 실행 방법:
--   docker exec -i docker-postgres-1 psql -U edu -d edu_spring < labs/04_indexing_explain.sql
--
-- EXPLAIN 읽는 법 요약:
--   - "Seq Scan"    = 테이블을 처음부터 끝까지 다 읽음 (인덱스 없음)
--   - "Index Scan"  = 인덱스로 바로 찾아감
--   - "cost=시작..총합"  = 옵티마이저의 추정 비용 (작을수록 좋음, 단위는 상대적)
--   - "actual time=...", "rows=..."  = ANALYZE 로 실제 측정한 시간/행 수
--   - "Execution Time" = 실제 총 실행 시간(ms)
--
-- 개념서 참조: docs/CS_데이터베이스_개념서.md 5장(인덱싱), 6장(쿼리 처리)
--             docs/SPRING_개념서.md 4.7절(인덱스와 B-tree)
-- =====================================================================

DROP TABLE IF EXISTS big_user CASCADE;

CREATE TABLE big_user (
    id       BIGSERIAL PRIMARY KEY,
    email    TEXT NOT NULL,
    country  TEXT NOT NULL,
    age      INT  NOT NULL
);

-- ---------------------------------------------------------------------
-- 100만 행 생성: generate_series 로 대량 데이터를 한 번에 만든다.
-- ---------------------------------------------------------------------
-- generate_series(1, 1000000) 은 1부터 100만까지의 숫자를 행으로 뿜어낸다.
-- 그 숫자(g)를 이용해 이메일/국가/나이를 만들어 넣는다.
INSERT INTO big_user (email, country, age)
SELECT
    'user' || g || '@example.com',                       -- user1@..., user2@...
    (ARRAY['KR','US','JP','DE','FR'])[1 + (g % 5)],       -- 5개 국가를 순환
    20 + (g % 50)                                         -- 나이 20~69
FROM generate_series(1, 1000000) AS g;

-- 통계정보 갱신 (옵티마이저가 올바른 계획을 세우도록). 대량 INSERT 후 권장.
ANALYZE big_user;

SELECT count(*) AS 총행수 FROM big_user;   -- 1000000


-- ---------------------------------------------------------------------
-- [1] 인덱스가 없을 때: email 로 조회 -> Seq Scan (전체 훑기)
-- ---------------------------------------------------------------------
-- BUFFERS 옵션까지 켜면 몇 개의 데이터 블록을 읽었는지도 볼 수 있다.
EXPLAIN (ANALYZE, BUFFERS)
SELECT * FROM big_user WHERE email = 'user777777@example.com';
-- 예상 결과 계획:
--   Seq Scan on big_user  (... rows=1 ...)
--     Filter: (email = 'user777777@example.com')
--     Rows Removed by Filter: 999999   <- 999,999개를 헛되이 읽고 버렸다!
--   Execution Time: (수십 ms)          <- 100만 행을 다 훑었기 때문
--
-- 이것이 O(n) 이다: 데이터가 2배면 시간도 대략 2배.


-- ---------------------------------------------------------------------
-- [2] 인덱스를 만든다: email 컬럼에 B-tree 인덱스
-- ---------------------------------------------------------------------
CREATE INDEX idx_big_user_email ON big_user (email);
ANALYZE big_user;   -- 인덱스 만든 뒤에도 통계 갱신


-- ---------------------------------------------------------------------
-- [3] 같은 조회를 다시: 이제 Index Scan (인덱스로 바로 찾기)
-- ---------------------------------------------------------------------
EXPLAIN (ANALYZE, BUFFERS)
SELECT * FROM big_user WHERE email = 'user777777@example.com';
-- 예상 결과 계획:
--   Index Scan using idx_big_user_email on big_user  (... rows=1 ...)
--     Index Cond: (email = 'user777777@example.com')
--   Execution Time: (0.0x ms)          <- Seq Scan 대비 수백~수천 배 빠르다
--
-- 이것이 O(log n) 이다: 100만 행이라도 B-tree 높이는 3~4단계라 몇 번만에 도달.
-- [1] 의 Execution Time 과 [3] 의 Execution Time 을 직접 비교해 보라.


-- =====================================================================
-- [4] 복합 인덱스와 왼쪽 접두사(leftmost-prefix) 규칙
-- =====================================================================
-- (country, age) 순서로 된 복합 인덱스를 만든다.
-- B-tree 복합 인덱스는 '앞 컬럼부터 순서대로'만 활용할 수 있다.
--   비유: 전화번호부는 (성, 이름) 순 정렬이라
--         '성=김' 은 빠르게 찾지만, '이름=철수'(성 무관)는 인덱스가 무용지물이다.
CREATE INDEX idx_big_user_country_age ON big_user (country, age);
ANALYZE big_user;

-- (4-a) 앞 컬럼(country)부터 사용 -> 인덱스 사용됨 (Index Scan / Bitmap Index Scan)
EXPLAIN (ANALYZE, BUFFERS)
SELECT * FROM big_user WHERE country = 'KR' AND age = 30;
-- 예상: idx_big_user_country_age 를 사용. (country, age) 둘 다 왼쪽부터 매칭.

-- (4-b) 앞 컬럼만 사용 (country) -> 인덱스 사용됨 (접두사이므로 OK)
EXPLAIN (ANALYZE, BUFFERS)
SELECT * FROM big_user WHERE country = 'KR';
-- 예상: idx_big_user_country_age 사용 (왼쪽 접두사 country 만으로도 활용 가능).

-- (4-c) 뒤 컬럼만 사용 (age) -> 왼쪽 접두사(country)를 건너뜀 -> 이 인덱스 못 씀!
EXPLAIN (ANALYZE, BUFFERS)
SELECT * FROM big_user WHERE age = 30;
-- 예상: Seq Scan (또는 다른 방식). idx_big_user_country_age 는 사용되지 않는다.
--       왜? B-tree 는 country 로 먼저 정렬되어 있어, country 없이 age 만으로는
--       원하는 값이 인덱스 전체에 흩어져 있기 때문. => "왼쪽 접두사 규칙" 위반.
--   교훈: 복합 인덱스는 자주 함께 쓰는 조건의 '앞 컬럼 순서'를 신중히 정해야 한다.


-- =====================================================================
-- [5] 커버링 인덱스(covering index): 인덱스만으로 결과를 끝내기
-- =====================================================================
-- 조회에 필요한 컬럼이 전부 인덱스 안에 있으면, 테이블 본체(heap)를 안 봐도 된다.
-- 이를 Index Only Scan 이라 하며, 더 빠르다.
CREATE INDEX idx_big_user_email_country ON big_user (email) INCLUDE (country);
ANALYZE big_user;

EXPLAIN (ANALYZE, BUFFERS)
SELECT email, country FROM big_user WHERE email = 'user777777@example.com';
-- 예상: "Index Only Scan" 이 나올 수 있다.
--       필요한 email, country 가 모두 인덱스에 있어 테이블을 안 읽어도 되기 때문.
--       (Heap Fetches 가 0 에 가까우면 커버링이 잘 작동한 것.)


-- =====================================================================
-- [6] 인덱스의 대가: 쓰기 비용 & 공간
-- =====================================================================
-- 인덱스는 '정렬된 색인'이라 INSERT/UPDATE/DELETE 마다 색인도 갱신해야 한다.
-- => 읽기는 빨라지지만 쓰기는 느려지고, 디스크 공간도 더 쓴다.
-- 방금 만든 인덱스들이 차지하는 크기를 확인해 보자.
SELECT
    indexrelname AS 인덱스,
    pg_size_pretty(pg_relation_size(indexrelid)) AS 크기
FROM pg_stat_user_indexes
WHERE relname = 'big_user'
ORDER BY pg_relation_size(indexrelid) DESC;

-- 결론:
--   - WHERE / JOIN / ORDER BY 에 자주 쓰는 컬럼 -> 인덱스 이득이 크다.
--   - 값 종류가 거의 없거나(예: 성별), 쓰기가 매우 빈번한 컬럼 -> 인덱스가 손해일 수 있다.
--   - "일단 다 걸자"가 아니라 EXPLAIN 으로 확인하며 필요한 곳에만 건다.
--
-- JPA 연결: @Column(unique=true) 는 자동으로 유니크 인덱스를 만든다.
--   그래서 existsByEmail / findByEmail 이 빠른 것이다 (이 실습의 [3] 과 같은 원리).
--   명시적으로는 @Table(indexes = @Index(columnList = "...")) 로도 선언한다.
--
-- 실습 끝.
