package com.ssafy.sample.model.dto;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.sample.exception.CustomBusinessException;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

/**
 * ============================================================================
 * [실제 시험 출제 1순위! 명세서 4장 심화 4유형 통째로 복붙 모음]
 * - 심화 1: 1:1 작성자 조인 상세 조회 (GET /sample/notice/{noticeId}, 없으면 404)
 * - 심화 2: 조건별 동적 검색 (GET /sample/notice/search)
 * - 심화 3: 조회수 +1 증가 후 'HIDDEN' 글이면 예외 던져서 조회수 +1 롤백하기 (@Transactional)
 * - 심화 4: 게시글 숨김 상태 변경 (PATCH /sample/notice/{noticeId})
 *
 * (이 파일은 참고용 모음 파일이므로 필요한 클래스/메서드만 복사해서 원래 파일에 붙여넣으세요!)
 * ============================================================================
 */

// ----------------------------------------------------------------------------
// 1. [DTO 추가] 작성자 Member.java & 검색조건 NoticeSearchCondition.java
// ----------------------------------------------------------------------------
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
class Member {
    private int mno;
    private String name;
    private String email;
    private String role;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
class NoticeSearchCondition {
    private String title;    // 제목 포함 검색
    private Integer catId;   // ★ int 말고 Integer (입력 안 했을 때 null 체크용!)
    private Boolean pinned;  // ★ boolean 말고 Boolean (입력 안 했을 때 null 체크용!)
}

// ★ 심화 1번을 풀 때는 기존 Notice.java 에 아래 필드 1줄을 추가하면 됩니다!
// private Member member;

// ----------------------------------------------------------------------------
// 2. [DAO 추가] NoticeDao.java 에 아래 메서드 4개 추가
// ----------------------------------------------------------------------------
@Mapper
interface NoticeAdvancedDao {
    // 심화 1: 작성자(Member) 포함 1건 상세 조회
    Notice selectDetailWithMember(@Param("noticeId") int noticeId);

    // 심화 2: 조건별 동적 검색
    List<Notice> searchNotices(NoticeSearchCondition condition);

    // 심화 3: 조회수 +1 증가
    int increaseViewCount(@Param("noticeId") int noticeId);

    // 심화 4: 게시글 숨김('HIDDEN') 처리
    int hideNotice(@Param("noticeId") int noticeId);
}

// ----------------------------------------------------------------------------
// 3. [ServiceImpl 추가] NoticeServiceImpl.java 에 아래 메서드 구현
// ----------------------------------------------------------------------------
@Service
@RequiredArgsConstructor
class NoticeAdvancedServiceImpl {

    private final NoticeAdvancedDao noticeDao;

    // 심화 1 + 심화 3: 상세 조회 시 view_count +1 후 HIDDEN 이면 예외 -> 자동 롤백!
    @Transactional
    public Notice getNoticeDetail(int noticeId) {
        // 1) 먼저 조회수 +1 증가 (존재하지 않는 번호면 0 반환)
        int updated = noticeDao.increaseViewCount(noticeId);
        if (updated == 0) {
            return null; // 컨트롤러에서 404 Not Found 반환
        }

        // 2) 작성자 정보(member)까지 조인해서 1건 조회
        Notice notice = noticeDao.selectDetailWithMember(noticeId);

        // 3) 만약 숨김 처리된('HIDDEN') 글이면 예외 발생 -> 방금 올린 view_count +1 도 자동 롤백됨!
        if (notice != null && "HIDDEN".equals(notice.getStatus())) {
            throw new CustomBusinessException("숨김 처리된 공지사항은 조회할 수 없습니다.");
        }
        return notice;
    }

    // 심화 2: 조건별 동적 검색
    @Transactional(readOnly = true)
    public List<Notice> searchNotices(NoticeSearchCondition condition) {
        return noticeDao.searchNotices(condition);
    }

    // 심화 4: 게시글 숨김 상태 변경 ('HIDDEN')
    @Transactional
    public boolean hideNotice(int noticeId) {
        return noticeDao.hideNotice(noticeId) > 0;
    }
}

// ----------------------------------------------------------------------------
// 4. [Controller 추가] NoticeController.java 에 아래 메서드 3개 추가
// ----------------------------------------------------------------------------
@RestController
@RequestMapping("/sample/notice")
@RequiredArgsConstructor
class NoticeAdvancedController {

    private final NoticeAdvancedServiceImpl noticeService;

    // 심화 1 + 3: 상세 조회 (GET /sample/notice/{noticeId})
    @GetMapping("/{noticeId}")
    public ResponseEntity<?> detail(@PathVariable("noticeId") int noticeId) {
        Notice notice = noticeService.getNoticeDetail(noticeId);
        if (notice == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("공지사항을 찾을 수 없습니다.");
        }
        return ResponseEntity.ok(notice);
    }

    // 심화 2: 조건별 동적 검색 (GET /sample/notice/search?title=평가&catId=1&pinned=true)
    @GetMapping("/search")
    public ResponseEntity<List<Notice>> search(@ModelAttribute NoticeSearchCondition condition) {
        return ResponseEntity.ok(noticeService.searchNotices(condition));
    }

    // 심화 4: 숨김 상태 변경 (PATCH /sample/notice/{noticeId})
    @PatchMapping("/{noticeId}")
    public ResponseEntity<String> hide(@PathVariable("noticeId") int noticeId) {
        boolean updated = noticeService.hideNotice(noticeId);
        if (!updated) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 공지사항이 없습니다.");
        }
        return ResponseEntity.ok("Notice Hidden");
    }
}
