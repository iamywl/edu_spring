package com.ssafy.sample;

import com.ssafy.sample.model.dto.Notice;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DtoTest {

    @Test
    @DisplayName("[SAMPLE-DTO-1] Notice DTO 기본 9개 필드 선언 및 타입 일치 검증")
    void noticeFieldsTest() {
        List<Field> declaredFields = Arrays.stream(Notice.class.getDeclaredFields())
                .filter(f -> !f.isSynthetic())
                .toList();
        assertEquals(9, declaredFields.size(), "Notice 클래스에는 명세서에 정의된 9개 필드가 선언되어야 합니다.");

        assertDoesNotThrow(() -> {
            assertEquals(int.class, Notice.class.getDeclaredField("noticeId").getType());
            assertEquals(int.class, Notice.class.getDeclaredField("catId").getType());
            assertEquals(int.class, Notice.class.getDeclaredField("mno").getType());
            assertEquals(String.class, Notice.class.getDeclaredField("title").getType());
            assertEquals(String.class, Notice.class.getDeclaredField("content").getType());
            assertEquals(int.class, Notice.class.getDeclaredField("viewCount").getType());
            assertEquals(boolean.class, Notice.class.getDeclaredField("pinned").getType());
            assertEquals(String.class, Notice.class.getDeclaredField("status").getType());
            assertEquals(String.class, Notice.class.getDeclaredField("createdAt").getType());
        });
    }

    @Test
    @DisplayName("[SAMPLE-DTO-2] Lombok 기본 생성자, @Builder, Getter/Setter 정상 동작 검증")
    void lombokTest() {
        Notice empty = new Notice();
        empty.setNoticeId(1);
        empty.setTitle("테스트");
        empty.setPinned(true);
        assertEquals(1, empty.getNoticeId());
        assertEquals("테스트", empty.getTitle());
        assertTrue(empty.isPinned());

        Notice notice = Notice.builder()
                .noticeId(10)
                .catId(1)
                .mno(1)
                .title("빌더 공지")
                .content("내용")
                .pinned(true)
                .status("ACTIVE")
                .build();

        assertEquals(10, notice.getNoticeId());
        assertTrue(notice.isPinned());
    }
}