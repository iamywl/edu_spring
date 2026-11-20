package com.ssafy.sample;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.sample.controller.NoticeController;
import com.ssafy.sample.exception.CustomBusinessException;
import com.ssafy.sample.exception.GlobalRestExceptionHandler;
import com.ssafy.sample.model.dto.Notice;
import com.ssafy.sample.model.service.NoticeService;

@WebMvcTest({NoticeController.class, GlobalRestExceptionHandler.class})
public class ControllerTest {

    @Autowired
    private MockMvc mockMvc;


    private ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private NoticeService noticeService;

    @Test
    @DisplayName("[SAMPLE-CTRL-1] GET /sample/notice 전체 조회 (200 OK)")
    void getAllNoticesTest() throws Exception {
        given(noticeService.getAllNotices()).willReturn(List.of(
                Notice.builder().noticeId(1).title("5회차 월말재평가 사전 환경 설정 안내").build()
        ));

        mockMvc.perform(get("/sample/notice"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("5회차 월말재평가 사전 환경 설정 안내"));
    }

    @Test
    @DisplayName("[SAMPLE-CTRL-2] POST /sample/notice 등록 (201 Created / 400 Bad Request)")
    void createNoticeTest() throws Exception {
        Notice valid = Notice.builder().catId(1).mno(1).title("안내").content("내용").build();
        given(noticeService.createNotice(any())).willReturn(Notice.builder().noticeId(5).build());

        mockMvc.perform(post("/sample/notice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(valid)))
                .andExpect(status().isCreated())
                .andExpect(content().string("Notice Created"));

        given(noticeService.createNotice(any()))
                .willThrow(new CustomBusinessException("공지사항 제목은 필수 입력 항목입니다."));

        mockMvc.perform(post("/sample/notice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(valid)))
                .andExpect(status().isBadRequest());
    }
}