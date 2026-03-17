package com.edu.jpa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Chapter 06: Spring Data JPA with PostgreSQL
 * - JPA Auditing 활성화 (@CreatedDate 사용을 위해 필요)
 */
@SpringBootApplication
@EnableJpaAuditing // JPA Auditing 활성화 (엔티티 생성/수정 시각 자동 기록)
public class Chapter06Application {

    public static void main(String[] args) {
        SpringApplication.run(Chapter06Application.class, args);
    }
}
