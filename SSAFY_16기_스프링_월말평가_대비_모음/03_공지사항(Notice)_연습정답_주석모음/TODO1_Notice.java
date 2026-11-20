package com.ssafy.sample.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * [TODO 1 정답 - JUnit DtoTest 100% 통과 버전]
 * ★ 주의: 명세서에 있는 9개 필드만 정확히 넣어야 합니다! (다른 필드 넣으면 DtoTest 감점!)
 * ★ @Data, @NoArgsConstructor, @AllArgsConstructor, @Builder 4개 전부 필수!
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notice {
    private int noticeId;               // 1. 공지 번호 (PK)
    private int catId;                  // 2. 카테고리 번호 (FK)
    private int mno;                    // 3. 작성자 회원 번호 (FK)
    private String title;               // 4. 제목
    private String content;             // 5. 내용
    @Builder.Default
    private int viewCount = 0;          // 6. 조회수 (기본값 0)
    @Builder.Default
    private boolean pinned = false;     // 7. 상단 고정 여부 (기본값 false)
    @Builder.Default
    private String status = "ACTIVE";   // 8. 상태 (기본값 'ACTIVE')
    private String createdAt;           // 9. 등록 일시
}
