DROP DATABASE IF EXISTS spring_sample;
CREATE DATABASE spring_sample DEFAULT CHARACTER SET utf8mb4;
USE spring_sample;

DROP TABLE IF EXISTS notice;
DROP TABLE IF EXISTS board_category;
DROP TABLE IF EXISTS member;

-- 1. 회원 테이블 (1:1 작성자 조인 매핑 확장용)
CREATE TABLE member (
    mno INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(45) NOT NULL,
    email VARCHAR(45) NOT NULL UNIQUE,
    role VARCHAR(45) NOT NULL DEFAULT 'USER'
);

-- 2. 공지 카테고리 테이블 (카테고리별 필터링 확장용)
CREATE TABLE board_category (
    cat_id INT AUTO_INCREMENT PRIMARY KEY,
    cat_code VARCHAR(20) NOT NULL UNIQUE,
    cat_name VARCHAR(50) NOT NULL
);

-- 3. 공지사항 테이블 (기본 2개 기능 + 심화 확장 기능 모두 수용 가능한 구조)
CREATE TABLE notice (
    notice_id INT AUTO_INCREMENT PRIMARY KEY,
    cat_id INT NOT NULL,
    mno INT NOT NULL,
    title VARCHAR(100) NOT NULL,
    content VARCHAR(500) NOT NULL,
    view_count INT NOT NULL DEFAULT 0,
    pinned BOOLEAN NOT NULL DEFAULT FALSE,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_notice_category FOREIGN KEY (cat_id) REFERENCES board_category(cat_id),
    CONSTRAINT fk_notice_member FOREIGN KEY (mno) REFERENCES member(mno)
);

-- 기초 회원 데이터
INSERT INTO member (mno, name, email, role) VALUES
(1, '김싸피', 'ssafy01@ssafy.com', 'ADMIN'),
(2, '이싸피', 'ssafy02@ssafy.com', 'USER'),
(3, '박싸피', 'ssafy03@ssafy.com', 'USER');

-- 기초 카테고리 데이터
INSERT INTO board_category (cat_id, cat_code, cat_name) VALUES
(1, 'EXAM', '평가 안내'),
(2, 'CURRICULUM', '교육 과정'),
(3, 'GENERAL', '일반 공지');

-- 기초 공지사항 데이터 (기본 조회 및 심화 확장 연습용)
INSERT INTO notice (notice_id, cat_id, mno, title, content, view_count, pinned, status, created_at) VALUES
(1, 1, 1, '5회차 월말재평가 사전 환경 설정 안내', 'UTF-8 인코딩, XML DTD 설정, MySQL DB 구축을 완료해 주세요.', 15, TRUE, 'ACTIVE', '2026-11-16 09:00:00'),
(2, 2, 1, 'Spring Boot & MyBatis 심화 특강 일정', '트랜잭션 롤백 및 MyBatis 동적 쿼리 실습이 진행됩니다.', 8, FALSE, 'ACTIVE', '2026-11-16 10:00:00'),
(3, 3, 2, '강의장 퇴실 시 냉난방기 전원 점검 요청', '마지막 퇴실 인원은 전원 차단 여부를 확인해 주세요.', 3, FALSE, 'ACTIVE', '2026-11-16 11:00:00'),
(4, 1, 1, '[만료] 4회차 월말평가 안내 공지', '종료된 평가 안내 게시글입니다.', 42, FALSE, 'HIDDEN', '2026-11-01 09:00:00');

COMMIT;

select * from notice;