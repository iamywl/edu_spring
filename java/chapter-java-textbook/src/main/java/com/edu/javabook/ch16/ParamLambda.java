package com.edu.javabook.ch16;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * 16.3 매개변수 있는 람다식
 *
 * 함수형 인터페이스의 추상 메소드가 매개변수를 받을 때는 괄호 안에 매개변수를 쓴다.
 *
 *   (x)      -> ...      매개변수 1개
 *   (x, y)   -> ...      매개변수 2개
 *
 * 축약 규칙 :
 *  - 매개변수 타입은 대부분 생략할 수 있다(컴파일러가 문맥으로 추론).
 *  - 매개변수가 정확히 1개이면 괄호도 생략할 수 있다.  예) x -> ...
 *  - 매개변수가 0개이거나 2개 이상이면 괄호는 반드시 필요하다.
 *
 * 대표적인 표준 함수형 인터페이스(값을 "소비"만 하고 리턴하지 않는 것) :
 *  - Consumer<T>       : void accept(T t)          -> 입력 1개, 리턴 없음
 *  - BiConsumer<T, U>  : void accept(T t, U u)     -> 입력 2개, 리턴 없음
 */
public class ParamLambda {

    /** 정수 하나를 받아 처리하는 함수형 인터페이스(리턴 없음) */
    @FunctionalInterface
    interface IntPrinter {
        void print(int value);
    }

    /** 정수 두 개를 받아 처리하는 함수형 인터페이스(리턴 없음) */
    @FunctionalInterface
    interface IntPairPrinter {
        void print(int a, int b);
    }

    public static void main(String[] args) {

        System.out.println("=== 16.3 매개변수 있는 람다식 ===");

        // [1] 매개변수 1개 : 타입을 명시한 형태
        System.out.println("\n[1] 매개변수 1개 (타입 명시)");
        IntPrinter p1 = (int value) -> System.out.println("받은 값: " + value);
        p1.print(10);

        // [2] 타입 생략 + 괄호 생략 (매개변수가 1개일 때만 괄호 생략 가능)
        System.out.println("\n[2] 타입/괄호 생략");
        IntPrinter p2 = value -> System.out.println("제곱: " + (value * value));
        p2.print(7);

        // [3] 매개변수 2개 : (x, y) 형태 (괄호 필수)
        System.out.println("\n[3] 매개변수 2개");
        IntPairPrinter p3 = (a, b) -> System.out.println(a + " + " + b + " = " + (a + b));
        p3.print(3, 5);

        // [4] Consumer<T> : 입력 1개를 소비(리턴 없음)
        System.out.println("\n[4] Consumer<T> (void accept(T))");
        Consumer<String> shout = text -> System.out.println(text.toUpperCase() + "!!!");
        shout.accept("hello");

        // [5] BiConsumer<T, U> : 입력 2개를 소비(리턴 없음)
        System.out.println("\n[5] BiConsumer<T, U> (void accept(T, U))");
        BiConsumer<String, Integer> repeat =
                (word, count) -> {
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < count; i++) sb.append(word);
                    System.out.println(word + " x " + count + " = " + sb);
                };
        repeat.accept("냥", 3);

        System.out.println("\n프로그램 정상 종료");
    }
}
