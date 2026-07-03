package com.edu.intro.aop;

import com.edu.intro.GreetingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * "AOP는 프록시로 동작한다"는 사실을 눈으로 확인시켜 주는 러너.
 *
 * 애플리케이션 기동 직후 자동 실행되어(CommandLineRunner),
 * 주입받은 GreetingService Bean의 "실제 런타임 클래스 이름"을 로그로 찍는다.
 *
 * 예상 출력:
 *  - 우리가 작성한 클래스 : com.edu.intro.KoreanGreetingService
 *  - 실제 주입된 클래스   : com.edu.intro.KoreanGreetingService$$SpringCGLIB$$0
 *
 * 이름에 $$SpringCGLIB$$가 들어 있다면, 컨테이너에 등록된 것은 원본이 아니라
 * Spring이 원본을 상속해 만든 CGLIB 프록시라는 뜻이다.
 * greet()에 붙은 @LogExecutionTime 어드바이스는 바로 이 프록시가 실행한다.
 *
 * 직접 해볼 것: KoreanGreetingService의 @LogExecutionTime을 지우고
 * ExecutionTimeAspect의 @Before 포인트컷 패턴도 안 걸리게 바꾸면
 * 프록시가 사라지고 원본 클래스 이름이 그대로 찍힌다.
 * (감쌀 부가 기능이 없으면 Spring은 프록시를 만들지 않는다)
 */
@Component
public class ProxyCheckRunner implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(ProxyCheckRunner.class);

    // 생성자 주입 (권장 방식) - 인터페이스 타입으로 주입받지만
    // 실제로 들어오는 객체는 구현체를 감싼 프록시다
    private final GreetingService greetingService;

    public ProxyCheckRunner(GreetingService greetingService) {
        this.greetingService = greetingService;
    }

    @Override
    public void run(String... args) {
        String runtimeClassName = greetingService.getClass().getName();
        // CGLIB 프록시는 클래스 이름에 $$가 들어간다
        boolean isProxy = runtimeClassName.contains("$$");

        log.info("===== AOP 프록시 확인 =====");
        log.info("[프록시 확인] 주입받은 타입(인터페이스) : {}", GreetingService.class.getName());
        log.info("[프록시 확인] 실제 런타임 클래스        : {}", runtimeClassName);
        log.info("[프록시 확인] 프록시 여부               : {}",
                isProxy ? "예 (Spring이 감싼 CGLIB 프록시)" : "아니오 (원본 객체)");

        // 프록시를 "통해서" 호출해야 어드바이스가 동작한다.
        // 아래 한 줄로 [서비스 호출 로깅]과 [실행 시간 측정] 로그가 함께 찍히는 것을 확인하자.
        log.info("[프록시 확인] greet() 호출 결과         : {}", greetingService.greet("AOP"));
        log.info("===========================");
    }
}
