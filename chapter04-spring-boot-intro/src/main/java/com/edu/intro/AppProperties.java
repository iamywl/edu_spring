package com.edu.intro;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @ConfigurationProperties 예제.
 *
 * application.yml에 정의한 "app." 으로 시작하는 프로퍼티들을
 * 이 객체의 필드에 자동으로 묶어(binding) 준다.
 *
 * 예) application.yml
 *   app:
 *     name: 교육용 Spring Boot 앱
 *     version: 1.0.0
 *     max-users: 100      <- 케밥케이스(max-users)가 카멜케이스(maxUsers) 필드에 매핑됨
 *
 * @Value와의 차이:
 *   - @Value("${app.name}")  : 프로퍼티를 하나씩 개별적으로 주입 (HelloController 참고)
 *   - @ConfigurationProperties: 관련 프로퍼티를 객체 하나로 묶어 타입 안전하게 관리 (권장)
 *
 * record로 정의하면 불변 객체가 되고, Spring이 생성자를 통해 값을 바인딩한다.
 */
@ConfigurationProperties(prefix = "app")
public record AppProperties(
        String name,
        String version,
        int maxUsers
) {
}
