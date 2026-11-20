# 중3 눈높이로 배우는 SQL & MyBatis Mapper 완전 초보 교과서

> **걱정 마세요!** 어려운 영어 전문 용어 다 빼고, **엑셀(Excel) 표**와 **택배 상자** 비유만으로 10분 만에 이해할 수 있게 썼습니다.  
> 지금 STS 화면에 띄워두신 **`Notice` 공지사항 연습 문제(`TODO 1 ~ TODO 4`)**까지 이 책 하나로 바로 풀 수 있습니다!

---

## [1교시] SQL이 뭔가요? (엑셀 표 다루기 명령어 4개)

### 1. 데이터베이스(DB) = 엑셀 파일, 테이블(Table) = 엑셀 시트(표)
여러분이 지금 화면에서 보고 있는 `schema.sql`은 **"엑셀 표 3장을 미리 만들어서 데이터 몇 줄 넣어두는 파일"**입니다.

- **데이터베이스(`spring_sample`)** = 엑셀 파일 1개
- **테이블(`notice`, `member`, `board_category`)** = 엑셀 시트(표) 3장
- **컬럼(`notice_id`, `title`, `content` 등)** = 표 맨 위 가로줄 제목(세로 열)
- **행(Row)** = 공지사항 글 1줄, 2줄, 3줄...

예를 들어 `notice` 표를 엑셀로 그리면 이렇게 생겼습니다:

| notice_id (번호) | cat_id (카테고리) | mno (작성자번호) | title (제목) | content (내용) | view_count (조회수) | pinned (고정) | status (상태) | created_at (작성일) |
| :---: | :---: | :---: | :--- | :--- | :---: | :---: | :---: | :--- |
| `1` | `1` | `1` | 5회차 월말재평가... | UTF-8 인코딩... | `15` | `TRUE` | `ACTIVE` | `2026-11-16 09:00:00` |
| `2` | `2` | `1` | Spring Boot & MyBatis... | 트랜잭션 롤백... | `8` | `FALSE` | `ACTIVE` | `2026-11-16 10:00:00` |

**SQL은 이 엑셀 표를 컴퓨터한테 "보여줘 / 넣어줘 / 고쳐줘 / 지워줘" 하고 시키는 딱 4가지 영어 문장입니다!**

---

### 2. 무조건 이 4개 문장만 알면 끝! (CRUD 4대장)

#### ① `SELECT` = 표에서 꺼내서 보여줘! (조회)
```sql
-- 1) notice 표의 모든(*) 칸을 다 보여줘!
SELECT * FROM notice;

-- 2) notice 표에서 번호(notice_id)가 1번인 줄만 보여줘! (WHERE = ~인 것만!)
SELECT * FROM notice WHERE notice_id = 1;

-- 3) 상태가 'ACTIVE'인 것만 번호 큰 순서(최신순, DESC)로 보여줘!
SELECT * FROM notice
WHERE status = 'ACTIVE'
ORDER BY notice_id DESC;

-- 4) 제목(title)에 '스프링' 글자가 포함된 줄만 찾아줘! (% = 앞뒤에 무슨 글자가 와도 됨)
SELECT * FROM notice
WHERE title LIKE CONCAT('%', #{keyword}, '%');
```
> **중3 핵심 암기 3개**:
> - `*` = 전부 다(모든 칸)
> - `WHERE` = 조건 필터 (~인 줄만!)
> - `ORDER BY 컬럼 DESC` = 큰 순서(최신순) 정렬 / `ASC` = 작은 순서(1, 2, 3...)

---

#### ② `INSERT` = 표 맨 아래에 새 줄 추가해줘! (등록)
```sql
INSERT INTO notice (cat_id, mno, title, content)
VALUES (#{catId}, #{mno}, #{title}, #{content});
```
- **해석**: "`notice` 표의 `(cat_id, mno, title, content)` 칸에, 값 `(#{catId}, #{mno}, #{title}, #{content})`을 새로 한 줄 넣어줘!"
- **왜 `notice_id`, `view_count`, `pinned`, `status`, `created_at`은 안 넣나요?**
  - `schema.sql`을 보면 얘들은 `AUTO_INCREMENT`(자동 1,2,3 증가) 또는 `DEFAULT`(기본값 `0`, `FALSE`, `'ACTIVE'`, 현재시간)가 붙어 있어서 **안 넣어도 DB가 알아서 자동으로 채워주기 때문**입니다! (물론 넣고 싶으면 칸 이름과 `VALUES` 짝을 맞춰서 넣어도 됩니다.)

---

#### ③ `UPDATE` = 표에 이미 있는 줄의 내용을 고쳐줘! (수정)
```sql
UPDATE notice
SET title = #{title}, content = #{content}
WHERE notice_id = #{noticeId};
```
- **해석**: "`notice` 표에서 **번호(`notice_id`)가 `#{noticeId}`인 줄만 찾아서**, `title`과 `content`를 새 값으로 바꿔(`SET`)줘!"
- **★ 절대 주의**: `WHERE`를 빼먹으면 표에 있는 공지사항 **전부 다** 똑같은 제목으로 바뀌어 버립니다! `UPDATE`엔 무조건 `WHERE`!

---

#### ④ `DELETE` = 표에서 특정 줄을 지워줘! (삭제)
```sql
DELETE FROM notice
WHERE notice_id = #{noticeId};
```
- **해석**: "`notice` 표에서 **번호(`notice_id`)가 `#{noticeId}`인 줄을 삭제**해줘!"

---

#### ⑤ (보너스) `JOIN` = 표 두 개를 짝지어서 이름까지 같이 꺼내오기!
`notice` 표에는 작성자 번호(`mno = 1`)만 적혀 있고, 작성자 이름(`김싸피`)은 `member` 표에 적혀 있습니다. 두 표를 합쳐서 보고 싶으면 **같은 번호(`mno`)끼리 풀칠(`JOIN`)**하면 됩니다!

```sql
SELECT
    n.*,                      -- notice 표(별명 n)의 모든 칸 +
    m.name AS writer_name,    -- member 표(별명 m)의 name 칸을 writer_name 이름으로 가져오기!
    c.cat_name AS cat_name    -- board_category 표(별명 c)의 카테고리 이름도 가져오기!
FROM notice n
JOIN member m ON n.mno = m.mno
JOIN board_category c ON n.cat_id = c.cat_id;
```
> **해석**: "`notice`(별명 `n`)랑 `member`(별명 `m`)를 `n.mno`와 `m.mno`가 같은 줄끼리 붙여(`JOIN`)서 꺼내줘!"

---

## [2교시] Mapper(`.xml`)가 뭔가요? (방금 화면 빨간 X 에러 해결!)

방금 회원님이 보내주신 STS 캡쳐 화면을 보면 **`NoticeMapper.xml` 6번째 줄에 `<select>` 상자 없이 바로 `SLECTD`라고 적어서 빨간 X가 떴습니다!**

### 1. Mapper 파일은 "SQL을 담아두는 택배 상자 모음집"입니다
- 맨 위 `<mapper namespace="...">` 는 **거대한 큰 택배 박스**입니다.
- **중요 규칙**: 큰 박스(`<mapper>`) 바로 안에 SQL 글자를 그냥 적으면 XML 문법 에러가 납니다!
- 반드시 아래 **4가지 작은 상자 중 하나를 먼저 열고**, 그 **안에** SQL을 적어야 합니다!
  - 조회할 때: **`<select id="함수이름" resultType="담을도시락통"> ... </select>`**
  - 추가할 때: **`<insert id="함수이름"> ... </insert>`**
  - 수정할 때: **`<update id="함수이름"> ... </update>`**
  - 삭제할 때: **`<delete id="함수이름"> ... </delete>`**

```xml
<!-- ❌ 잘못된 예 (방금 화면에 뜬 에러!) -->
<mapper namespace="com.ssafy.sample.model.dao.NoticeDao">
    SELECT * FROM notice   <!-- 작은 상자(<select>) 없이 그냥 쓰면 빨간 X! -->
</mapper>

<!-- ⭕ 올바른 예 (반드시 <select> 상자 안에 넣기!) -->
<mapper namespace="com.ssafy.sample.model.dao.NoticeDao">
    <select id="selectAll" resultType="Notice">
        SELECT * FROM notice
    </select>
</mapper>
```

---

### 2. Mapper가 자바(Dao)랑 연결되는 4가지 비밀 규칙 (이것만 알면 마스터!)

자바 파일(`NoticeDao.java`)과 SQL 파일(`NoticeMapper.xml`)은 서로 다른 파일인데 어떻게 연결될까요? **딱 4가지만 이름 맞추면 자동으로 연결됩니다!**

1. **`namespace` = `Dao` 파일의 주소(패키지명.인터페이스명)**
   - `NoticeDao.java`의 패키지가 `com.ssafy.sample.model.dao`이고 이름이 `NoticeDao`라면:
   - XML 맨 위에 `<mapper namespace="com.ssafy.sample.model.dao.NoticeDao">` 라고 적습니다. (스켈레톤 파일엔 이미 적혀 있음!)
2. **`id="..."` = `Dao` 파일 안의 함수(메서드) 이름**
   - `NoticeDao.java`에 `List<Notice> selectAll();` 이라고 되어 있으면,
   - XML에 `<select id="selectAll" ...>` 로 적어야 합니다. **대소문자 1글자라도 다르면 에러!**
3. **`resultType="..."` = SELECT 결과를 담을 DTO(도시락통) 이름**
   - `<select>`로 DB에서 한 줄을 꺼내왔을 때, 자바의 어떤 클래스에 담을지 알려주는 것입니다.
   - 예: `Notice` 클래스에 담을 거면 `resultType="Notice"`!
   - *(단, `application.properties`에 `mybatis.configuration.map-underscore-to-camel-case=true` 가 적혀 있어야 DB의 `notice_id`가 자바의 `noticeId`에 자동으로 쏙 들어갑니다!)*
4. **`#{변수명}` = 자바에서 넘어온 값을 SQL 빈칸에 꽂는 구멍!**
   - 자바에서 `notice.getTitle()` 값을 SQL에 넣고 싶으면 SQL 안에 **`#{title}`** 이라고 적습니다.
   - MyBatis가 알아서 `notice` 도시락통에서 `title` 값을 꺼내 따옴표(`'...'`)까지 붙여 안전하게 넣어줍니다!

---

### 3. Mapper에서 꼭 알아야 할 문법 3가지

1. **자동 생성된 번호(PK) 받아오기**:
   `<insert id="insert" parameterType="Notice" useGeneratedKeys="true" keyProperty="noticeId">`
   → INSERT 하자마자 DB가 자동으로 매겨준 번호(`1, 2, 3, 4, 5...`)를 `notice.noticeId` 변수에 다시 채워줍니다!
2. **XML 부등호 규칙 (`<` 금지!)**:
   XML에서는 `<` 기호를 태그 시작 괄호로 착각해서 에러가 납니다!
   - `>=` (이상) ➔ **`&gt;=`**
   - `<=` (이하) ➔ **`&lt;=`**
   - `>` (초과) ➔ **`&gt;`**
   - `<` (미만) ➔ **`&lt;`**
3. **동적 검색 (`<where>` + `<if>`)**:
   사용자가 제목을 입력했을 때만 `WHERE` 조건을 붙이고 싶을 때 씁니다:
   ```xml
   <select id="search" resultType="SearchResponse">
       SELECT * FROM notice
       <where>
           <if test="keyword != null and keyword != ''">
               AND title LIKE CONCAT('%', #{keyword}, '%')
           </if>
           <if test="minView != null">
               AND view_count &gt;= #{minView}
           </if>
       </where>
   </select>
   ```

---

## [3교시] 지금 내 STS 화면(`Notice` 연습문제 TODO 1~4) 그대로 따라 풀기!

지금 STS에 열려 있는 프로젝트(`spring_sample` 공지사항 프로젝트)의 **`TODO 1` ~ `TODO 4`**를 한 단계씩 같이 완성해 봅시다!

### `TODO 1`: `Notice.java` 채우기 (도시락통 만들기)
`schema.sql`의 `notice` 테이블 컬럼 **9개**를 자바 변수 **9개**로 그대로 옮겨 적으면 됩니다. (밑줄 `_` 지우고 뒤 글자 대문자!)

| DB 컬럼명 (`schema.sql`) | 자바 타입 | 자바 변수명 (`Notice.java`) | 설명 |
|---|---|---|---|
| `notice_id` (INT) | `int` | `noticeId` | 공지 번호 (자동 증가) |
| `cat_id` (INT) | `int` | `catId` | 카테고리 번호 (1, 2, 3) |
| `mno` (INT) | `int` | `mno` | 작성자 회원 번호 |
| `title` (VARCHAR) | `String` | `title` | 제목 |
| `content` (VARCHAR) | `String` | `content` | 내용 |
| `view_count` (INT) | `int` | `viewCount` | 조회수 |
| `pinned` (BOOLEAN) | `boolean` | `pinned` | 상단 고정 여부 (`true`/`false`) |
| `status` (VARCHAR) | `String` | `status` | 상태 (`'ACTIVE'`/`'HIDDEN'`) |
| `created_at` (TIMESTAMP) | `String` | `createdAt` | 작성일시 (문자열로 편하게 받기) |

```java
package com.ssafy.sample.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notice {
    private int noticeId;
    private int catId;
    private int mno;
    private String title;
    private String content;
    private int viewCount;
    private boolean pinned;
    private String status;
    private String createdAt;
}
```

---

### `TODO 2`: `NoticeMapper.xml` 채우기 (방금 빨간 X 뜬 파일!)
`NoticeDao.java`를 열어보면 메서드가 딱 2개 있습니다:
- `List<Notice> selectAll();`
- `int insert(Notice notice);`

그러니 `NoticeMapper.xml` 안에 `<select id="selectAll">` 과 `<insert id="insert">` 상자 2개를 만들면 됩니다!

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.ssafy.sample.model.dao.NoticeDao">

    <!-- 1. 전체 목록 조회: NoticeDao의 selectAll()과 연결! -->
    <!-- resultType="Notice": 꺼낸 1줄마다 Notice 객체에 자동 저장 -->
    <select id="selectAll" resultType="Notice">
        SELECT *
        FROM notice
        ORDER BY pinned DESC, notice_id DESC
    </select>

    <!-- 2. 공지 등록: NoticeDao의 insert(Notice notice)와 연결! -->
    <!-- useGeneratedKeys="true" keyProperty="noticeId": DB가 만들어준 새 번호를 notice.noticeId에 저장! -->
    <insert id="insert" parameterType="Notice" useGeneratedKeys="true" keyProperty="noticeId">
        INSERT INTO notice (cat_id, mno, title, content, pinned)
        VALUES (#{catId}, #{mno}, #{title}, #{content}, #{pinned})
    </insert>

</mapper>
```

---

### `TODO 3`: `NoticeServiceImpl.java` 채우기 (주방장 + 유효성 검사)
이 프로젝트에는 `CustomBusinessException`과 `GlobalRestExceptionHandler`가 이미 들어있습니다!  
제목이나 내용이 비어 있을 때 `throw new CustomBusinessException("...");`을 던지면 알아서 `400 Bad Request` 에러 응답이 날아갑니다.

```java
package com.ssafy.sample.model.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ssafy.sample.exception.CustomBusinessException;
import com.ssafy.sample.model.dao.NoticeDao;
import com.ssafy.sample.model.dto.Notice;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NoticeServiceImpl implements NoticeService {

    private final NoticeDao dao;

    @Override
    public List<Notice> getAllNotices() {
        return dao.selectAll();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Notice createNotice(Notice notice) {
        // 간단한 유효성 검증: 제목이 비어있으면 예외 던지기!
        if (notice.getTitle() == null || notice.getTitle().trim().isEmpty()) {
            throw new CustomBusinessException("공지사항 제목은 필수입니다.");
        }
        if (notice.getContent() == null || notice.getContent().trim().isEmpty()) {
            throw new CustomBusinessException("공지사항 내용은 필수입니다.");
        }
        dao.insert(notice);
        return notice; // useGeneratedKeys 덕분에 noticeId가 채워져서 반환됨!
    }
}
```

---

### `TODO 4`: `NoticeController.java` 채우기 (웨이터)

```java
package com.ssafy.sample.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.sample.model.dto.Notice;
import com.ssafy.sample.model.service.NoticeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Notice", description = "공지사항 관리 API (샘플)")
@RestController
@RequestMapping("/api/notices")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService service;

    @Operation(summary = "전체 공지사항 목록 조회")
    @GetMapping
    public ResponseEntity<List<Notice>> list() {
        List<Notice> list = service.getAllNotices();
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @Operation(summary = "신규 공지사항 등록")
    @PostMapping
    public ResponseEntity<String> create(@RequestBody Notice notice) {
        Notice created = service.createNotice(notice);
        return new ResponseEntity<>("등록 완료! 공지번호=" + created.getNoticeId(), HttpStatus.CREATED);
    }
}
```

---

## [4교시] 중3 눈높이 스프링 단어장 12개 (이것만 알면 헷갈릴 게 없음!)

| 단어 | 한 줄 뜻 (쉬운 비유) |
|---|---|
| **DTO** (`Notice.java`) | DB 데이터를 담아서 계층끼리 옮겨주는 **도시락통** |
| **DAO** (`NoticeDao.java`) | 자바가 SQL을 부를 수 있게 연결해 주는 **전화기 번호부 (`@Mapper`)** |
| **Mapper** (`NoticeMapper.xml`) | 실제 SQL 문장(`SELECT`, `INSERT`...)이 적혀있는 **레시피북** |
| **Service** (`NoticeServiceImpl.java`) | 컨트롤러 지시를 받아서 검사하고 DAO를 호출하는 **주방장 (`@Service`)** |
| **Controller** (`NoticeController.java`) | 손님(브라우저/Swagger)이 보내는 URL을 맨 앞에서 받는 **웨이터 (`@RestController`)** |
| **Bean (빈)** | 스프링이 미리 만들어서 창고에 보관하고 있는 부품 객체 (`@Service`, `@RestController` 붙인 애들) |
| **DI (의존성 주입)** | 내가 `new NoticeDao()` 하지 않아도 `@RequiredArgsConstructor` + `private final`로 스프링이 알아서 꽂아주는 것 |
| **`@Transactional`** | 작업 여러 개를 한 묶음으로 묶어서, 중간에 1개라도 실패하면 DB를 원래대로 싹 되돌리는 안전장치 |
| **`@PathVariable("id")`** | 주소 중간에 낀 값 꺼내기 (예: `/api/notices/3` 에서 `3` 꺼내기) |
| **`@RequestParam("name")`** | 주소 뒤 물음표 값 꺼내기 (예: `/api/notices?name=평가` 에서 `평가` 꺼내기) |
| **`@RequestBody`** | 손님이 보낸 JSON 박스(`{"title":"안녕"}`) 통째로 DTO에 담아 받기 |
| **`ResponseEntity`** | 손님한테 돌려줄 **[데이터 + HTTP 상태코드(200, 400, 404)]** 포장 박스 |
