package com.ssafy.sample;

import com.ssafy.sample.exception.CustomBusinessException;
import com.ssafy.sample.model.dao.NoticeDao;
import com.ssafy.sample.model.dto.Notice;
import com.ssafy.sample.model.service.NoticeService;
import com.ssafy.sample.model.service.NoticeServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Method;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;

@SpringBootTest
public class ServiceTest {

    @Autowired
    private NoticeService noticeService;

    @Autowired
    private SqlSessionTemplate sqlSessionTemplate;

    @MockitoSpyBean
    private NoticeDao noticeDao;

    @Test
    @DisplayName("[SAMPLE-SERVICE-1] NoticeService.getAllNotices() 조회 및 읽기 전용 @Transactional 검증")
    void getAllNoticesServiceTest() throws NoSuchMethodException {
        Method getAllMethod = NoticeServiceImpl.class.getMethod("getAllNotices");
        Transactional tx = getAllMethod.getAnnotation(Transactional.class);
        assertNotNull(tx, "getAllNotices() 메서드에 @Transactional(readOnly = true)가 선언되어야 합니다.");
        assertTrue(tx.readOnly(), "getAllNotices()의 @Transactional에는 readOnly = true 설정이 필요합니다.");

        List<Notice> list = noticeService.getAllNotices();
        assertNotNull(list);
        assertEquals(4, list.size());
    }

    @Test
    @DisplayName("[SAMPLE-SERVICE-2] NoticeService.createNotice() 정상 등록, 예외 검증 및 실제 DB @Transactional 롤백 검증")
    void createNoticeServiceTest() throws NoSuchMethodException {
        // 1. @Transactional 선언 검증
        Method createMethod = NoticeServiceImpl.class.getMethod("createNotice", Notice.class);
        assertNotNull(createMethod.getAnnotation(Transactional.class),
                "createNotice() 메서드에 @Transactional 선언이 필수입니다.");

        // 2. 빈 제목 예외 검증
        Notice invalid = Notice.builder()
                .catId(2)
                .mno(1)
                .title("   ")
                .content("제목 누락")
                .build();
        assertThrows(CustomBusinessException.class, () -> {
            noticeService.createNotice(invalid);
        }, "제목이 비어있을 경우 CustomBusinessException이 발생해야 합니다.");

        // 3. [핵심] 실제 DB INSERT 후 예외 발생 시 @Transactional 롤백 무결성 검증
        // noticeDao.insert() 호출 시 SqlSessionTemplate으로 실제 DB INSERT를 먼저 수행한 직후 예외를 발생시킴
        doAnswer(invocation -> {
            Notice arg = invocation.getArgument(0);
            sqlSessionTemplate.insert("com.ssafy.sample.model.dao.NoticeDao.insert", arg);
            throw new CustomBusinessException("강제 트랜잭션 롤백 테스트 예외");
        }).when(noticeDao).insert(any(Notice.class));

        Notice rollbackTarget = Notice.builder()
                .catId(2)
                .mno(1)
                .title("롤백 검증용 공지")
                .content("예외 발생 시 DB에서 롤백되어야 함")
                .build();

        assertThrows(CustomBusinessException.class, () -> {
            noticeService.createNotice(rollbackTarget);
        });

        // @Transactional이 정상 동작했다면 방금 INSERT된 데이터가 롤백되어 기존 4건이 유지되어야 함
        int countAfterRollback = sqlSessionTemplate
                .selectList("com.ssafy.sample.model.dao.NoticeDao.selectAll")
                .size();
        assertEquals(4, countAfterRollback,
                "예외 발생 시 선행 실행된 INSERT 작업이 롤백되어 기존 데이터 수(4건)가 유지되어야 합니다.");
    }
}