package com.ssafy.sample.aop;

import java.util.Arrays;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * [AOP = 공통 기능(로깅, 실행시간 측정)을 핵심 코드 밖에서 자동으로 끼워 넣기]
 * - pom.xml에 spring-boot-starter-aop 가 이미 들어있어서 바로 사용 가능합니다!
 * - 시험에서 "Service 메서드 실행 전후로 로그/시간을 찍는 AOP를 작성하라"고 나올 때 그대로 쓰세요.
 */
@Slf4j      // log.info(...) 사용 가능
@Aspect     // AOP 클래스 선언
@Component  // 스프링 Bean 등록
public class LoggingAspect {

    // 어디에 적용할지 지정: com.ssafy.sample.model.service 패키지 안의 모든 클래스, 모든 메서드
    @Pointcut("execution(* com.ssafy.sample.model.service.*.*(..))")
    public void serviceLayer() {}

    // 1. 메서드 실행 직전
    @Before("serviceLayer()")
    public void beforeMethod(JoinPoint jp) {
        log.info("[AOP-BEFORE] 메서드={} 파라미터={}", jp.getSignature().toShortString(), Arrays.toString(jp.getArgs()));
    }

    // 2. 메서드 정상 종료 후 (반환값 확인)
    @AfterReturning(pointcut = "serviceLayer()", returning = "result")
    public void afterReturning(JoinPoint jp, Object result) {
        log.info("[AOP-RETURN] 메서드={} 결과={}", jp.getSignature().toShortString(), result);
    }

    // 3. 메서드 실행 중 예외 발생 시
    @AfterThrowing(pointcut = "serviceLayer()", throwing = "ex")
    public void afterThrowing(JoinPoint jp, Throwable ex) {
        log.error("[AOP-ERROR] 메서드={} 예외={}", jp.getSignature().toShortString(), ex.getMessage());
    }

    // 4. 메서드 실행 전 + 후 + 시간 측정 (@Around가 시험 단골!)
    @Around("serviceLayer()")
    public Object measureExecutionTime(ProceedingJoinPoint pjp) throws Throwable {
        long start = System.currentTimeMillis();
        try {
            // ★ pjp.proceed() 를 호출해야 실제 Service 메서드가 실행됩니다! (빼먹으면 동작 안 함)
            return pjp.proceed();
        } finally {
            long end = System.currentTimeMillis();
            log.info("[AOP-TIME] {} 실행시간: {}ms", pjp.getSignature().toShortString(), (end - start));
        }
    }
}
