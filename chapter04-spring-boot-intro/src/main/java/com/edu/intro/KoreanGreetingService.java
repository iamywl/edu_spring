package com.edu.intro;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

// Profile에 따라 다른 Bean이 등록되는 예제
@Service
@Profile("!english")  // english 프로필이 아닐 때 활성화
public class KoreanGreetingService implements GreetingService {

    @Override
    public String greet(String name) {
        return "안녕하세요, " + name + "님! (Korean Greeting Service)";
    }
}
