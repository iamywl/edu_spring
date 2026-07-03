-- =====================================================================
-- 실습 01. 정규화(Normalization): 이상 현상(anomaly)을 직접 눈으로 보기
-- =====================================================================
--
-- 목표:
--   1) 정규화가 안 된(denormalized) 테이블 하나를 만든다.
--   2) 이 테이블에서 갱신/삽입/삭제 이상 현상(anomaly)이 어떻게 발생하는지 본다.
--   3) 1NF -> 2NF -> 3NF 로 단계별로 쪼개서, 이상 현상이 사라지는 것을 확인한다.
--
-- 실행 방법 (docker/docker-compose-infra.yml 로 postgres 를 띄운 뒤):
--   docker exec -i docker-postgres-1 psql -U edu -d edu_spring < labs/01_normalization.sql
--   (컨테이너 이름은 `docker ps` 로 확인. 기본값은 docker-postgres-1)
--
-- 개념서 참조: docs/CS_데이터베이스_개념서.md 2장(정규화)
-- =====================================================================

-- 매번 깨끗한 상태에서 시작하기 위해 기존 객체 제거 (역순으로)
DROP TABLE IF EXISTS enrollment CASCADE;
DROP TABLE IF EXISTS course CASCADE;
DROP TABLE IF EXISTS student CASCADE;
DROP TABLE IF EXISTS bad_enrollment CASCADE;

-- ---------------------------------------------------------------------
-- 0단계. 문제의 테이블: 하나에 다 때려박은 "수강신청 대장"
-- ---------------------------------------------------------------------
-- 시나리오: 학생이 강좌를 수강신청한다.
--   - 한 행 = 한 건의 수강신청
--   - 그런데 학생 정보(이름, 학과, 학과사무실)와
--     강좌 정보(강좌명, 담당교수)를 전부 이 한 테이블에 넣었다.
--
-- 이런 설계가 "정규화가 안 된(denormalized)" 상태다.
CREATE TABLE bad_enrollment (
    student_id   INT,           -- 학번
    student_name TEXT,          -- 학생 이름
    dept_name    TEXT,          -- 학과명
    dept_office  TEXT,          -- 학과 사무실 위치 (학과가 정해지면 자동으로 정해짐)
    course_id    TEXT,          -- 강좌 코드
    course_title TEXT,          -- 강좌명
    professor    TEXT,          -- 담당 교수 (강좌가 정해지면 자동으로 정해짐)
    grade        TEXT           -- 성적 (학생+강좌 조합마다 하나)
);

INSERT INTO bad_enrollment
    (student_id, student_name, dept_name, dept_office, course_id, course_title, professor, grade)
VALUES
    (1001, '김철수', '컴퓨터공학', '공학관 301', 'CS101', '자료구조',   '이교수', 'A'),
    (1001, '김철수', '컴퓨터공학', '공학관 301', 'CS102', '데이터베이스', '박교수', 'B'),
    (1002, '이영희', '컴퓨터공학', '공학관 301', 'CS101', '자료구조',   '이교수', 'A'),
    (1003, '박민수', '전자공학',   '공학관 502', 'EE201', '회로이론',   '최교수', 'C');

-- 지금 데이터를 확인해 보자. '컴퓨터공학 / 공학관 301' 이 여러 번 반복된다(중복!).
SELECT * FROM bad_enrollment ORDER BY student_id, course_id;

-- ---------------------------------------------------------------------
-- 이상 현상 #1: 갱신 이상 (Update Anomaly)
-- ---------------------------------------------------------------------
-- 컴퓨터공학과 사무실이 '공학관 301' -> '신공학관 101' 로 이사했다고 하자.
-- 같은 사실(학과 사무실)이 여러 행에 흩어져 있으므로, 전부 다 고쳐야 한다.
-- 한 행이라도 빠뜨리면 -> 같은 학과인데 사무실이 다르게 보이는 '모순'이 생긴다.
UPDATE bad_enrollment
   SET dept_office = '신공학관 101'
 WHERE student_id = 1001;   -- 일부러 김철수 행만 고쳐서 모순을 만들어 본다

-- 확인: 같은 '컴퓨터공학'인데 사무실이 두 종류로 갈라졌다. 데이터가 거짓말을 한다.
SELECT DISTINCT dept_name, dept_office
  FROM bad_enrollment
 WHERE dept_name = '컴퓨터공학';
-- => '컴퓨터공학'이 '신공학관 101'과 '공학관 301' 둘 다 나온다 = 갱신 이상.

-- ---------------------------------------------------------------------
-- 이상 현상 #2: 삽입 이상 (Insert Anomaly)
-- ---------------------------------------------------------------------
-- 아직 아무도 수강신청하지 않은 새 강좌 'CS201 운영체제(정교수)'를 등록하고 싶다.
-- 그런데 이 테이블은 '수강신청 한 건'이 있어야만 행을 넣을 수 있는 구조다.
-- 학생 정보 없이 강좌만 넣으려면 학생 컬럼을 전부 NULL 로 비워야 한다.
INSERT INTO bad_enrollment
    (student_id, student_name, dept_name, dept_office, course_id, course_title, professor, grade)
VALUES
    (NULL, NULL, NULL, NULL, 'CS201', '운영체제', '정교수', NULL);
-- => "강좌를 등록하려면 억지로 NULL 투성이 행을 만들어야 한다" = 삽입 이상.

-- ---------------------------------------------------------------------
-- 이상 현상 #3: 삭제 이상 (Delete Anomaly)
-- ---------------------------------------------------------------------
-- 박민수(1003)가 자퇴해서 그의 수강신청 기록을 지운다.
DELETE FROM bad_enrollment WHERE student_id = 1003;
-- 그런데 박민수는 'EE201 회로이론(최교수)'를 듣던 유일한 학생이었다.
-- 그 행을 지우는 순간, '회로이론'과 '최교수'라는 강좌 정보까지 통째로 사라진다.
SELECT DISTINCT course_id, course_title, professor
  FROM bad_enrollment
 WHERE course_id = 'EE201';
-- => 결과가 0행. 학생을 지웠을 뿐인데 강좌 정보가 증발했다 = 삭제 이상.

-- 정리:
--   한 테이블에 서로 다른 종류의 사실(학생 / 강좌 / 수강)을 섞어 넣었기 때문에
--   위 세 가지 이상 현상이 생긴다. 해결책은 "사실의 종류별로 테이블을 나누는 것" = 정규화.


-- =====================================================================
-- 정규화 시작: 각 단계가 무엇을 보장하는지 주석으로 확인
-- =====================================================================
--
-- 이 데이터의 함수적 종속(functional dependency, FD):
--   student_id            -> student_name, dept_name        (학번이 정해지면 이름/학과가 정해짐)
--   dept_name             -> dept_office                    (학과가 정해지면 사무실이 정해짐)
--   course_id             -> course_title, professor        (강좌가 정해지면 제목/교수가 정해짐)
--   (student_id, course_id) -> grade                        (학생+강좌 조합이 정해지면 성적이 정해짐)
--
-- 이 테이블의 후보키(candidate key)는 (student_id, course_id) 복합키다.
--
-- 1NF: 모든 컬럼이 '더 쪼갤 수 없는 원자값'을 가져야 한다.
--      위 bad_enrollment 는 이미 각 칸에 값이 하나뿐이라 1NF는 만족한다.
--      (만약 course_id 칸에 'CS101, CS102' 처럼 여러 값을 넣었다면 1NF 위반이었다.)
--
-- 2NF: 1NF + "부분 종속 제거".
--      복합키 (student_id, course_id) 의 '일부'에만 종속되는 컬럼을 분리한다.
--        - student_name, dept_name 은 student_id 에만 종속 (course_id 와 무관) -> 분리
--        - course_title, professor 는 course_id 에만 종속 (student_id 와 무관) -> 분리
--        - grade 만이 (student_id, course_id) 전체에 종속되므로 남긴다.
--
-- 3NF: 2NF + "이행적 종속 제거".
--      키가 아닌 컬럼이 다른 비(非)키 컬럼을 통해 종속되는 경우를 분리한다.
--        - student -> dept_name -> dept_office (이행적 종속)
--          즉 dept_office 는 키(student_id)에 직접 종속이 아니라 dept_name 을 거쳐 종속.
--          => 학과(department)를 별도 테이블로 분리해야 하지만,
--             이 실습에서는 단순화를 위해 학생 테이블에 dept 정보를 두되
--             아래 BCNF 설명에서 완전한 분리를 다룬다.
--
-- BCNF: 모든 결정자(->의 왼쪽)가 후보키여야 한다.
--       dept_name -> dept_office 에서 dept_name 은 student 테이블의 후보키가 아니다.
--       따라서 엄밀히 하려면 department 테이블을 따로 두는 것이 BCNF 다. (아래 반영)
-- ---------------------------------------------------------------------

-- 학과 테이블: dept_name -> dept_office 종속을 독립시킨다 (3NF/BCNF)
DROP TABLE IF EXISTS department CASCADE;
CREATE TABLE department (
    dept_name   TEXT PRIMARY KEY,   -- 학과명이 곧 키
    dept_office TEXT NOT NULL
);

-- 학생 테이블: student_id 가 결정하는 것들만 (이름 + 소속 학과)
CREATE TABLE student (
    student_id   INT PRIMARY KEY,
    student_name TEXT NOT NULL,
    dept_name    TEXT NOT NULL REFERENCES department(dept_name)  -- 학과로의 FK
);

-- 강좌 테이블: course_id 가 결정하는 것들만 (강좌명 + 교수)
CREATE TABLE course (
    course_id    TEXT PRIMARY KEY,
    course_title TEXT NOT NULL,
    professor    TEXT NOT NULL
);

-- 수강신청 테이블: (학생, 강좌) 조합과 그 조합의 성적만
-- 복합 기본키 + 두 개의 외래키로 무결성을 강제한다.
CREATE TABLE enrollment (
    student_id INT  NOT NULL REFERENCES student(student_id),
    course_id  TEXT NOT NULL REFERENCES course(course_id),
    grade      TEXT,
    PRIMARY KEY (student_id, course_id)   -- 같은 학생이 같은 강좌를 중복 신청 못 함
);

-- 정규화된 스키마에 데이터를 채운다 (각 사실을 '한 곳에만' 저장)
INSERT INTO department (dept_name, dept_office) VALUES
    ('컴퓨터공학', '공학관 301'),
    ('전자공학',   '공학관 502');

INSERT INTO student (student_id, student_name, dept_name) VALUES
    (1001, '김철수', '컴퓨터공학'),
    (1002, '이영희', '컴퓨터공학'),
    (1003, '박민수', '전자공학');

INSERT INTO course (course_id, course_title, professor) VALUES
    ('CS101', '자료구조',     '이교수'),
    ('CS102', '데이터베이스', '박교수'),
    ('EE201', '회로이론',     '최교수'),
    ('CS201', '운영체제',     '정교수');   -- 수강생이 없어도 강좌만 등록 가능 (삽입 이상 해결!)

INSERT INTO enrollment (student_id, course_id, grade) VALUES
    (1001, 'CS101', 'A'),
    (1001, 'CS102', 'B'),
    (1002, 'CS101', 'A'),
    (1003, 'EE201', 'C');

-- ---------------------------------------------------------------------
-- 이상 현상이 사라졌는지 확인
-- ---------------------------------------------------------------------

-- (갱신 이상 해결) 학과 사무실 이전: 이제 딱 한 행만 고치면 끝. 모순 불가능.
UPDATE department SET dept_office = '신공학관 101' WHERE dept_name = '컴퓨터공학';
SELECT * FROM department;   -- '컴퓨터공학' 사무실은 항상 한 값

-- (삭제 이상 해결) 박민수(1003) 수강 기록만 삭제해도 강좌 정보는 course 에 남는다.
DELETE FROM enrollment WHERE student_id = 1003;
SELECT * FROM course WHERE course_id = 'EE201';   -- 'EE201 회로이론'은 여전히 살아있다!

-- (원래 형태로 다시 보고 싶다면) JOIN 으로 언제든 합쳐서 볼 수 있다.
-- 정규화는 "저장은 나눠서, 조회는 JOIN 으로 합쳐서" 라는 원칙이다.
SELECT s.student_id, s.student_name, d.dept_name, d.dept_office,
       c.course_id, c.course_title, c.professor, e.grade
  FROM enrollment e
  JOIN student    s ON s.student_id = e.student_id
  JOIN department d ON d.dept_name  = s.dept_name
  JOIN course     c ON c.course_id  = e.course_id
 ORDER BY s.student_id, c.course_id;

-- 실습 끝. 다음: 02_transactions_acid.sql
