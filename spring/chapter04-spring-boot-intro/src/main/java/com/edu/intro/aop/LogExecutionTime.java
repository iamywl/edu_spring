package com.edu.intro.aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * "이 메서드의 실행 시간을 측정하라"는 표식(marker) 역할의 커스텀 애너테이션.
 *
 * 중요한 사실: 이 애너테이션 자체는 아무 일도 하지 않는다!
 *  - 애너테이션은 그저 코드에 붙이는 "팻말"일 뿐이다.
 *  - 실제 측정 로직은 ExecutionTimeAspect가 이 팻말이 붙은 메서드를 찾아
 *    프록시를 통해 앞뒤에 끼워 넣는다.
 *  - @Transactional, @Cacheable 같은 Spring의 애너테이션들도
 *    전부 이와 동일한 "애너테이션(표식) + 프록시(실행)" 구조로 동작한다.
 */
@Target(ElementType.METHOD)         // 메서드에만 붙일 수 있다
@Retention(RetentionPolicy.RUNTIME) // 런타임까지 유지되어야 Spring이 리플렉션으로 읽을 수 있다
public @interface LogExecutionTime {
}
