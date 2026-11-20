package com.ssafy.sample.model.dao;

import com.ssafy.sample.model.dto.Notice;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

//TODO 2: 구현 요구사항 명세서를 참조하여 NoticeDao.xml 매퍼를 완성하세요
@Mapper
public interface NoticeDao {
    List<Notice> selectAll();
    int insert(Notice notice);
    
}

// my batis 

