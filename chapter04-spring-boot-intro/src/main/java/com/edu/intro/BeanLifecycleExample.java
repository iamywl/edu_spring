package com.edu.intro;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Component;

// Bean 생명주기 콜백 예제
@Component
public class BeanLifecycleExample {

    public BeanLifecycleExample() {
        System.out.println("1. [생성자] BeanLifecycleExample 생성");
    }

    @PostConstruct
    public void init() {
        System.out.println("2. [@PostConstruct] 초기화 메서드 실행");
    }

    @PreDestroy
    public void destroy() {
        System.out.println("3. [@PreDestroy] 소멸 메서드 실행");
    }
}
