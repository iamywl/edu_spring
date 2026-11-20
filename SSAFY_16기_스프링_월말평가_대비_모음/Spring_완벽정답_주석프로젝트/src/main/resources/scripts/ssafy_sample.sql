-- 1. 데이터베이스 생성 및 선택
DROP DATABASE IF EXISTS ssafy_sample;
CREATE DATABASE IF NOT EXISTS ssafy_sample;
USE ssafy_sample;

-- 2. 테이블 생성 (Schema)

-- [사용자 테이블]: 회원 정보 및 자산 관리
CREATE TABLE users (
    user_id VARCHAR(20) PRIMARY KEY,     -- 사용자 아이디 (로그인 ID)
    user_name VARCHAR(30) NOT NULL,      -- 사용자 실명
    point INT DEFAULT 10000              -- 보유 포인트 
);

-- [상품 테이블]: 판매 리소스 관리 
CREATE TABLE products (
    product_id INT PRIMARY KEY AUTO_INCREMENT, -- 상품 고유 번호 (자동 증가)
    name VARCHAR(50) NOT NULL,                 -- 상품명
    price INT NOT NULL,                        -- 상품 단가
    stock INT NOT NULL                         -- 현재 재고 수량 
);

-- [주문 테이블]: 구매 이력 관리 
CREATE TABLE orders (
    order_id INT PRIMARY KEY AUTO_INCREMENT,   -- 주문 고유 번호 (자동 증가)
    product_id INT,                            -- 외래키: 구매한 상품 번호
    user_id VARCHAR(20),                       -- 외래키: 주문한 사용자 아이디
    order_date DATETIME DEFAULT CURRENT_TIMESTAMP, -- 주문 일시
    quantity INT NOT NULL,                     -- 주문 수량 (재고 차감 계산 시 사용)
    status VARCHAR(20) DEFAULT 'COMPLETED',    -- 주문 상태 (COMPLETED: 완료, CANCELLED: 취소)
    FOREIGN KEY (product_id) REFERENCES products(product_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- 3. 더미 데이터 삽입 (Data)

-- 사용자 데이터
INSERT INTO users (user_id, user_name, point) VALUES 
('ssafy01', '김싸피', 50000),
('ssafy02', '이싸피', 10000),
('ssafy03', '박싸피', 500),
('ssafy04', '최싸피', 30000),
('ssafy05', '정싸피', 15000);

-- 상품 데이터
INSERT INTO products (name, price, stock) VALUES 
('기계식 키보드', 120000, 5),   -- 고가 상품 
('무선 마우스', 35000, 20),     -- 넉넉한 재고 
('장패드', 15000, 2),          -- 품절 임박 
('C타입 케이블', 5000, 50),     -- 저가 다량 상품
('사무용 키보드', 45000, 15),   -- '키보드' 검색 시 기계식과 함께 나와야 함
('게이밍 마우스', 89000, 7),    -- '마우스' 검색 시 무선 마우스와 함께 나와야 함
('독서대', 25000, 10),          -- 검색 조건에 걸리지 않을 일반 상품
('모니터 암', 65000, 3);

-- 초기 주문 데이터
INSERT INTO orders (product_id, user_id, quantity, status) VALUES 
(1, 'ssafy01', 1, 'COMPLETED'), -- 기계식 키보드: 최근 구매자 '김싸피'가 조회되어야 함
(2, 'ssafy02', 2, 'COMPLETED'), -- 무선 마우스: 최근 구매자 '이싸피'가 조회되어야 함
(4, 'ssafy03', 1, 'CANCELLED'), -- 취소된 주문: 검색 시 최근 구매자에서 제외되어야 함
(5, 'ssafy04', 1, 'COMPLETED'),
(6, 'ssafy05', 1, 'COMPLETED');

COMMIT;
