package com.edu.javabook.ch12;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;

/**
 * 12.12 어노테이션 (Annotation)
 *
 * 어노테이션은 코드에 붙이는 "메타데이터(추가 정보)"다. @ 로 시작하며,
 * 컴파일러에게 지시하거나, 프레임워크가 실행 중 읽어 동작을 바꾸는 데 쓴다.
 *
 * 내장(표준) 어노테이션 예 :
 *   - @Override    : 부모 메서드를 재정의함을 표시 (오타 방지)
 *   - @Deprecated  : 더 이상 쓰지 말 것을 표시
 *   - @SuppressWarnings : 특정 경고를 숨김
 *
 * 커스텀 어노테이션 만들기 :
 *   - @interface 로 정의한다.
 *   - @Retention(RUNTIME) : 실행 중에도 정보를 유지 → 리플렉션으로 읽을 수 있음
 *       (SOURCE = 컴파일 후 사라짐, CLASS = 클래스파일까지만, RUNTIME = 실행까지)
 *   - @Target : 어디에 붙일 수 있는지 지정 (METHOD, TYPE 등)
 *   - 요소(element) : 괄호 안에 값을 담을 수 있다. (default 로 기본값 지정)
 *
 * 이 소절에서는 커스텀 어노테이션을 만들어 메서드에 붙이고,
 * 리플렉션으로 그 정보를 실행 중에 읽어 온다.
 */
public class AnnotationClass {

    // [커스텀 어노테이션] 실행 중 읽으려면 RUNTIME 유지 필수
    @Retention(RetentionPolicy.RUNTIME)   // 실행 시점까지 정보 유지 → 리플렉션 가능
    @Target(ElementType.METHOD)           // 메서드에만 붙일 수 있음
    @interface TestCase {
        String name();                    // 필수 요소
        int order() default 0;            // 기본값이 있는 요소
    }

    // 어노테이션을 붙인 대상 클래스
    static class Calculator {

        @TestCase(name = "덧셈 테스트", order = 1)
        public int add(int a, int b) { return a + b; }

        @TestCase(name = "곱셈 테스트", order = 2)
        public int multiply(int a, int b) { return a * b; }

        // 어노테이션이 없는 메서드 (비교용)
        public int subtract(int a, int b) { return a - b; }
    }

    // 내장 어노테이션 사용 예 : @Deprecated
    @Deprecated
    static void oldMethod() {
        System.out.println("  (구식 메서드 실행 - @Deprecated 표시됨)");
    }

    public static void main(String[] args) {

        System.out.println("=== 12.12 어노테이션 ===");

        // [1] 내장 어노테이션
        System.out.println("\n[1] 내장 어노테이션");
        System.out.println("  @Override    : 재정의 검증 (예: toString 등)");
        System.out.println("  @Deprecated  : 사용 자제 표시");
        oldMethod();  // @Deprecated 붙은 메서드 호출 (컴파일 경고 대상)

        // [2] 커스텀 어노테이션을 리플렉션으로 읽기
        System.out.println("\n[2] 커스텀 @TestCase 를 리플렉션으로 읽기");
        Method[] methods = Calculator.class.getDeclaredMethods();
        for (Method m : methods) {
            // 해당 메서드에 @TestCase 가 붙어 있는지 확인
            if (m.isAnnotationPresent(TestCase.class)) {
                TestCase tc = m.getAnnotation(TestCase.class);   // 어노테이션 정보 획득
                System.out.println("  [발견] 메서드=" + m.getName()
                        + ", name=\"" + tc.name() + "\""
                        + ", order=" + tc.order());
            } else {
                System.out.println("  [스킵] 메서드=" + m.getName() + " (어노테이션 없음)");
            }
        }

        // [3] 어노테이션 정보로 실제 메서드 동적 호출 (간이 테스트 실행기 흉내)
        System.out.println("\n[3] 어노테이션 기반 동적 실행 (간이 테스트 러너)");
        Calculator calc = new Calculator();
        try {
            for (Method m : methods) {
                if (m.isAnnotationPresent(TestCase.class)) {
                    TestCase tc = m.getAnnotation(TestCase.class);
                    Object result = m.invoke(calc, 6, 4);  // 6, 4 로 호출
                    System.out.println("  [" + tc.order() + "] " + tc.name()
                            + " → 결과 = " + result);
                }
            }
        } catch (Exception e) {
            System.out.println("  실행 중 오류: " + e.getMessage());
        }

        System.out.println("\n프로그램 정상 종료");
    }
}
