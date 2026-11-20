package com.ssafy.sample;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * [스프링 부트 시작점 (메인 클래스)]
 * - 실행 방법: 이 파일에서 우클릭 -> Run As -> Spring Boot App (또는 Java Application)
 * - 서버 켜진 뒤 브라우저에서 아래 주소로 들어가면 API 테스트 화면(Swagger)이 나옵니다!
 *   http://localhost:8080/swagger-ui/index.html
 */
@SpringBootApplication
public class SpringSampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringSampleApplication.class, args);
    }
}
