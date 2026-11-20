package com.ssafy.sample.model.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ssafy.sample.exception.CustomBusinessException;
import com.ssafy.sample.model.dao.NoticeDao;
import com.ssafy.sample.model.dto.Notice;

import lombok.RequiredArgsConstructor;

/**
 * [TODO 3 정답] NoticeServiceImpl.java
 * ★★★ JUnit 채점(ServiceTest) 통과 필수 조건 ★★★
 * 1. @Service + @RequiredArgsConstructor + private final NoticeDao noticeDao;
 * 2. getAllNotices() 위에 @Transactional(readOnly = true) 필수!
 * 3. createNotice() 위에 @Transactional 필수!
 * 4. title이 null 이거나 trim().isEmpty() 면 CustomBusinessException("공지사항 제목은 필수 입력 항목입니다.") 던지기!
 */
@Service
@RequiredArgsConstructor
public class NoticeServiceImpl implements NoticeService {

    private final NoticeDao noticeDao;

    @Override
    @Transactional(readOnly = true)
    public List<Notice> getAllNotices() {
        return noticeDao.selectAll();
    }

    @Override
    @Transactional
    public Notice createNotice(Notice notice) {
        if (notice == null || notice.getTitle() == null || notice.getTitle().trim().isEmpty()) {
            throw new CustomBusinessException("공지사항 제목은 필수 입력 항목입니다.");
        }
        noticeDao.insert(notice);
        return notice;
    }
}
