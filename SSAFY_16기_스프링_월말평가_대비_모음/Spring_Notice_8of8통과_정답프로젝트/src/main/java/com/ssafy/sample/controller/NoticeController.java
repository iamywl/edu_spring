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
 * [TODO 4 정답] NoticeController.java
 * ★★★ JUnit 채점(ControllerTest) 통과 필수 조건 ★★★
 * 1. @RestController + @RequestMapping("/sample/notice") + @RequiredArgsConstructor
 * 2. GET /sample/notice  -> 200 OK + List<Notice>
 * 3. POST /sample/notice -> @RequestBody Notice notice -> 201 Created + "Notice Created"
 * 4. 주의: try-catch로 예외를 잡지 마세요! (GlobalRestExceptionHandler가 자동으로 400 반환함)
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
