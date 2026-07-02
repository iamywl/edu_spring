package com.edu.javabook.ch16;

import java.util.function.Supplier;

/**
 * 16.2 매개변수 없는 람다식
 *
 * 함수형 인터페이스의 추상 메소드가 "매개변수를 받지 않을" 때는
 * 람다식의 매개변수 자리를 빈 괄호 () 로 표현한다.
 *
 *   () -> 실행할 코드
 *
 * 대표적인 표준 함수형 인터페이스 :
 *  - Runnable   : void run()      -> 입력도 없고 리턴도 없다(동작만 수행).
 *  - Supplier<T>: T    get()      -> 입력은 없지만 값 하나를 리턴(공급)한다.
 *
 * 이 소절에서는 () -> ... 형태를 여러 방식으로 살펴본다.
 */
public class NoParamLambda {

    /** 매개변수 없이 문자열을 만들어 주는 함수형 인터페이스 */
    @FunctionalInterface
    interface MessageMaker {
        String make();                      // 매개변수 없음, 리턴 있음
    }

    public static void main(String[] args) {

        System.out.println("=== 16.2 매개변수 없는 람다식 ===");

        // [1] 실행문이 하나 : 중괄호/return 생략
        System.out.println("\n[1] 빈 괄호 () 로 시작하는 람다");
        MessageMaker m1 = () -> "매개변수 없는 람다식입니다.";
        System.out.println(m1.make());

        // [2] 실행문이 여러 줄 : 중괄호 { } 와 return 사용
        System.out.println("\n[2] 여러 줄 실행문(중괄호 + return)");
        MessageMaker m2 = () -> {
            String part1 = "여러 줄";
            String part2 = "실행 가능";
            return part1 + " " + part2;
        };
        System.out.println(m2.make());

        // [3] Runnable : 입력도 리턴도 없이 "동작"만 수행
        System.out.println("\n[3] Runnable (void run())");
        Runnable task = () -> System.out.println("Runnable 이 실행하는 동작입니다.");
        task.run();

        // [4] Supplier : 매개변수 없이 값을 공급
        System.out.println("\n[4] Supplier<T> (T get())");
        Supplier<Integer> diceLike = () -> 6;     // 항상 6을 공급하는 예
        System.out.println("공급된 값: " + diceLike.get());

        Supplier<String> greeting = () -> "Supplier 가 만든 인사말";
        System.out.println(greeting.get());

        System.out.println("\n프로그램 정상 종료");
    }
}
