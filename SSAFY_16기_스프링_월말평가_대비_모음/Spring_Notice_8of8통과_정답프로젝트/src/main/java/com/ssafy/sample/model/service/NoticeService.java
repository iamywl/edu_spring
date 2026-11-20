package com.ssafy.sample.model.service;

import com.ssafy.sample.model.dto.Notice;
import java.util.List;

public interface NoticeService {
    List<Notice> getAllNotices();
    Notice createNotice(Notice notice);
}