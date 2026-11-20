package com.ssafy.sample;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

import com.ssafy.sample.model.dao.NoticeDao;
import com.ssafy.sample.model.dto.Notice;

@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class DaoTest {

    @Autowired
    private NoticeDao noticeDao;

    @Test
    @DisplayName("[SAMPLE-DAO-1] NoticeDao.selectAll() 전체 공지 목록(4건) 조회 검증")
    void selectAllNoticesTest() {
        List<Notice> list = noticeDao.selectAll();
        assertNotNull(list);
        assertEquals(4, list.size());
    }

    @Test
    @DisplayName("[SAMPLE-DAO-2] NoticeDao.insert() 신규 공지 삽입 및 PK 자동 채번 검증")
    void insertNoticeTest() {
        Notice notice = Notice.builder()
                .catId(1)
                .mno(1)
                .title("DAO 테스트 공지")
                .content("PK 자동 채번 확인")
                .build();

        int rows = noticeDao.insert(notice);
        assertEquals(1, rows);
        assertTrue(notice.getNoticeId() > 4);
    }
}