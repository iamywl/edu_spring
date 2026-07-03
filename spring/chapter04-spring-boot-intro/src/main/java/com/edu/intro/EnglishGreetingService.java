package com.edu.intro;

import com.edu.intro.aop.LogExecutionTime;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("english")
public class EnglishGreetingService implements GreetingService {

    // 한국어 구현체와 동일하게, 애너테이션만 붙이면 실행 시간 측정이 적용된다
    @LogExecutionTime
    @Override
    public String greet(String name) {
        return "Hello, " + name + "! (English Greeting Service)";
    }
}
