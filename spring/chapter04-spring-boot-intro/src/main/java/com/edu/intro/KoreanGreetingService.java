package com.edu.intro;

import com.edu.intro.aop.LogExecutionTime;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

// Profile에 따라 다른 Bean이 등록되는 예제
@Service
@Profile("!english")  // english 프로필이 아닐 때 활성화
public class KoreanGreetingService implements GreetingService {

    // 커스텀 애너테이션 하나로 실행 시간 측정이 적용된다.
    // 측정 코드는 이 클래스에 한 줄도 없다! (ExecutionTimeAspect가 프록시로 끼워 넣음)
    @LogExecutionTime
    @Override
    public String greet(String name) {
        return "안녕하세요, " + name + "님! (Korean Greeting Service)";
    }
}
