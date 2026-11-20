package com.ssafy.sample.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * [TODO 1 정답] Notice.java (공지사항 도시락통)
 * ★★★ JUnit 채점(DtoTest) 통과 필수 조건 ★★★
 * 1. 반드시 아래 9개 필드만 정확히 선언해야 합니다! (임의 필드 추가 시 DtoTest 실패!)
 * 2. @Data, @NoArgsConstructor, @AllArgsConstructor, @Builder 4개 전부 필수!
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notice {
    private int noticeId;       // 공지사항 고유 번호 (PK)
    private int catId;          // 카테고리 번호 (FK)
    private int mno;            // 작성자 회원 번호 (FK)
    private String title;       // 공지사항 제목
    private String content;     // 공지사항 본문 내용
    @Builder.Default
    private int viewCount = 0;  // 조회수 (기본값 0)
    @Builder.Default
    private boolean pinned = false; // 상단 고정 여부 (기본값 false)
    @Builder.Default
    private String status = "ACTIVE"; // 상태 (기본값 'ACTIVE')
    private String createdAt;   // 등록 일시
}
