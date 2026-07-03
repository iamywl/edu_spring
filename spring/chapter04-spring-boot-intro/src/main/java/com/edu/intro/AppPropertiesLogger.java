package com.edu.intro;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * 애플리케이션 기동 시 설정값을 출력하는 예제.
 *
 * 두 가지 설정 주입 방식을 한 곳에서 비교한다.
 *  1) @ConfigurationProperties로 묶은 AppProperties 객체 (생성자 주입)
 *  2) @Value로 프로퍼티를 하나씩 개별 주입
 */
@Component
public class AppPropertiesLogger implements CommandLineRunner {

    // 1) @ConfigurationProperties 객체를 통째로 주입받는다 (권장 방식)
    private final AppProperties appProperties;

    // 2) @Value로 프로퍼티를 개별 주입한다 (대조용)
    //    문법: ${프로퍼티키:기본값}  - 키가 없으면 콜론 뒤의 기본값이 사용된다
    @Value("${app.name:이름 없음}")
    private String appNameByValue;

    public AppPropertiesLogger(AppProperties appProperties) {
        this.appProperties = appProperties;
    }

    @Override
    public void run(String... args) {
        System.out.println("===== @ConfigurationProperties 데모 =====");
        System.out.println("[묶어서 주입] app.name     = " + appProperties.name());
        System.out.println("[묶어서 주입] app.version  = " + appProperties.version());
        System.out.println("[묶어서 주입] app.maxUsers = " + appProperties.maxUsers());
        System.out.println("[@Value 주입] app.name     = " + appNameByValue);
        System.out.println("=========================================");
    }
}
