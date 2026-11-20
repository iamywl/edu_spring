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

/**
 * [TODO 4 정답 - JUnit ControllerTest 100% 통과 버전]
 * ★★★ JUnit 채점 감점 방지 포인트 ★★★
 * 1. 클래스 주소는 정확히 @RequestMapping("/sample/notice") !
 * 2. 등록(POST) 성공 시 상태코드 201 Created, 본문은 정확히 "Notice Created" 반환!
 * 3. 제목 누락 시 컨트롤러에서 try-catch 로 예외를 잡지 마세요!
 *    (GlobalRestExceptionHandler가 알아서 400 Bad Request로 바꿔줍니다!)
 */
@Tag(name = "Notice", description = "공지사항 관리 API (샘플)")
@RestController
@RequestMapping("/sample/notice")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    @Operation(summary = "전체 공지사항 목록 조회")
    @GetMapping
    public ResponseEntity<List<Notice>> list() {
        return ResponseEntity.ok(noticeService.getAllNotices());
    }

    @Operation(summary = "신규 공지사항 등록")
    @PostMapping
    public ResponseEntity<String> create(@RequestBody Notice notice) {
        noticeService.createNotice(notice);
        return ResponseEntity.status(HttpStatus.CREATED).body("Notice Created");
    }
}
