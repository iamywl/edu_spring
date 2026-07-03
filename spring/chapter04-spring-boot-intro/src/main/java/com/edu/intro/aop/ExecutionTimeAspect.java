package com.edu.intro.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * 공통 관심사(실행 시간 측정, 호출 로깅)를 한곳에 모아둔 Aspect.
 *
 * @Aspect    : "이 클래스는 다른 Bean의 메서드 앞뒤에 끼어드는 부가 기능 모음"이라고 선언
 * @Component : Aspect도 Bean이어야 Spring이 인식한다
 *
 * 동작 원리 (프록시):
 *  - Spring은 포인트컷에 걸리는 Bean을 발견하면, 원본 대신
 *    원본을 상속한 가짜 객체(CGLIB 프록시)를 만들어 컨테이너에 등록한다.
 *  - 우리가 greetingService.greet()를 호출하면 실제로는 프록시의 greet()가
 *    먼저 실행되고, 프록시가 [어드바이스 → 원본 메서드 → 어드바이스] 순서로 감싸서 실행한다.
 *  - 프록시가 진짜 등록됐는지는 ProxyCheckRunner가 클래스 이름으로 보여준다.
 *
 * 용어 정리:
 *  - 포인트컷(Pointcut) : "어디에" 끼어들지 정하는 조건식
 *  - 어드바이스(Advice) : "무엇을" 끼워 넣을지 정하는 코드 (@Around, @Before 등)
 */
@Aspect
@Component
public class ExecutionTimeAspect {

    // System.out 대신 SLF4J Logger를 사용한다.
    // 로깅 프레임워크를 쓰면 로그 레벨(INFO/DEBUG...), 시각, 스레드, 출력 위치를
    // 코드 수정 없이 설정(application.yml)만으로 제어할 수 있다.
    private static final Logger log = LoggerFactory.getLogger(ExecutionTimeAspect.class);

    /**
     * [포인트컷 1] 애너테이션 기반: @LogExecutionTime이 붙은 메서드에만 적용
     *
     * @Around 어드바이스는 대상 메서드를 "감싸는" 가장 강력한 어드바이스다.
     *  - joinPoint.proceed() 호출 전이 "메서드 실행 전", 호출 후가 "메서드 실행 후"
     *  - proceed()를 호출하지 않으면 원본 메서드가 아예 실행되지 않는다 (차단도 가능!)
     *  - 반환값을 가로채거나 바꿔치기할 수도 있다
     */
    @Around("@annotation(com.edu.intro.aop.LogExecutionTime)")
    public Object measureExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.nanoTime();  // 원본 메서드 실행 "전"
        try {
            // 원본 메서드 실행 (이 한 줄이 실제 비즈니스 로직 호출)
            return joinPoint.proceed();
        } finally {
            // 원본 메서드 실행 "후" - 예외가 나도 finally라서 반드시 측정된다
            long elapsedMillis = (System.nanoTime() - start) / 1_000_000;
            log.info("[실행 시간 측정] {} 실행에 {}ms 걸렸습니다 (@LogExecutionTime 어드바이스)",
                    joinPoint.getSignature().toShortString(), elapsedMillis);
        }
    }

    /**
     * [포인트컷 2] execution 패턴 기반: com.edu.intro 하위의 이름이 *Service로 끝나는
     * 클래스의 모든 public 메서드에 적용 (애너테이션을 붙일 필요가 없다!)
     *
     * execution 표현식 읽는 법:
     *   execution( *  com.edu.intro..  *Service  .  *  (..) )
     *              │        │             │         │   └ 인자: 개수/타입 무관
     *              │        │             │         └ 메서드 이름: 전부
     *              │        │             └ 클래스 이름: Service로 끝나는 것
     *              │        └ 패키지: com.edu.intro와 그 하위 전부 (..)
     *              └ 반환 타입: 무엇이든
     *
     * @Before 어드바이스는 대상 메서드 실행 "직전"에만 실행된다.
     * (반환값을 바꾸거나 실행을 막을 수는 없다 - 그건 @Around의 몫)
     */
    @Before("execution(* com.edu.intro..*Service.*(..))")
    public void logServiceCall(JoinPoint joinPoint) {
        log.info("[서비스 호출 로깅] {} 호출됨 - 인자: {} (execution 포인트컷 어드바이스)",
                joinPoint.getSignature().toShortString(),
                Arrays.toString(joinPoint.getArgs()));
    }
}
