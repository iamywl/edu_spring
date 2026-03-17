package com.edu.board;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Chapter 09: Final Project - 게시판 REST API
 *
 * 이전 챕터에서 배운 모든 내용을 종합한 최종 프로젝트입니다.
 * - Spring Security + JWT 인증
 * - Spring Data JPA + PostgreSQL
 * - RESTful API 설계
 * - Docker Compose 배포
 */
@SpringBootApplication
public class Chapter09Application {

    public static void main(String[] args) {
        SpringApplication.run(Chapter09Application.class, args);
    }
}
