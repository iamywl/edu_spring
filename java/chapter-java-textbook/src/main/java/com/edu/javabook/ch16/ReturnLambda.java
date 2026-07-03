package com.edu.javabook.ch16;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * 16.4 리턴값 있는 람다식
 *
 * 람다식이 값을 리턴하는 방법은 두 가지다.
 *
 *  1) 중괄호 { } 를 쓰고 그 안에서 명시적으로 return 한다.
 *       (a, b) -> { return a + b; }
 *  2) 중괄호 없이 "식(expression)" 하나만 쓴다. 이때는 그 식의 결과가
 *     자동으로 리턴되며 return 키워드를 쓰지 않는다(써서는 안 된다).
 *       (a, b) -> a + b
 *
 * 대표적인 표준 함수형 인터페이스(값을 리턴하는 것) :
 *  - Function<T, R>      : R apply(T t)            -> 입력 1개, 리턴 1개
 *  - BiFunction<T, U, R> : R apply(T t, U u)       -> 입력 2개, 리턴 1개
 *  - Predicate<T>        : boolean test(T t)       -> 입력 1개, boolean 리턴
 */
public class ReturnLambda {

    /** 두 정수를 받아 정수를 리턴하는 함수형 인터페이스 */
    @FunctionalInterface
    interface IntCalculator {
        int calc(int a, int b);
    }

    public static void main(String[] args) {

        System.out.println("=== 16.4 리턴값 있는 람다식 ===");

        // [1] 중괄호 + return 으로 명시적 리턴
        System.out.println("\n[1] 중괄호와 return 사용");
        IntCalculator add = (a, b) -> {
            int result = a + b;
            return result;
        };
        System.out.println("3 + 4 = " + add.calc(3, 4));

        // [2] 식만 쓰면 return 생략 (자동 리턴)
        System.out.println("\n[2] 식(expression) 하나 -> return 생략");
        IntCalculator multiply = (a, b) -> a * b;   // 결과가 곧 리턴값
        System.out.println("3 * 4 = " + multiply.calc(3, 4));

        // [3] Function<T, R> : 입력 1개 -> 리턴 1개
        System.out.println("\n[3] Function<T, R> (R apply(T))");
        Function<String, Integer> length = s -> s.length();
        System.out.println("\"람다식\" 의 길이: " + length.apply("람다식"));

        // [4] BiFunction<T, U, R> : 입력 2개 -> 리턴 1개
        System.out.println("\n[4] BiFunction<T, U, R> (R apply(T, U))");
        BiFunction<Integer, Integer, String> compare =
                (x, y) -> (x > y) ? (x + " 가 더 크다") : (y + " 가 더 크거나 같다");
        System.out.println(compare.apply(10, 7));

        // [5] Predicate<T> : 입력 1개 -> boolean 리턴
        System.out.println("\n[5] Predicate<T> (boolean test(T))");
        Predicate<Integer> isEven = n -> n % 2 == 0;
        System.out.println("4 는 짝수인가? " + isEven.test(4));
        System.out.println("7 은 짝수인가? " + isEven.test(7));

        System.out.println("\n프로그램 정상 종료");
    }
}
