package com.edu.javabook.ch16;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 16.5 메소드 참조
 *
 * 람다식이 "이미 존재하는 메소드 하나를 그대로 호출"하기만 한다면,
 * 메소드 참조(Method Reference)로 더 간결하게 표현할 수 있다.
 *
 *   람다식              메소드 참조
 *   s -> Integer.parseInt(s)      ->  Integer::parseInt
 *
 * 메소드 참조의 종류(이 소절에서는 앞의 3종을 다룬다) :
 *  1) 정적 메소드 참조       : 클래스명::정적메소드    예) Integer::parseInt
 *  2) 인스턴스 메소드 참조    : 클래스명::인스턴스메소드
 *     (첫 번째 매개변수가 메소드를 호출할 대상이 된다)   예) String::length
 *  3) 특정 객체의 메소드 참조 : 객체참조::인스턴스메소드  예) out::println
 *  (4) 생성자 참조는 16.6 에서 다룬다)
 */
public class MethodReference {

    /** 정적 메소드 참조 예시를 위한 유틸 클래스 */
    static class MathUtil {
        static int square(int n) {          // 정적 메소드
            return n * n;
        }
    }

    public static void main(String[] args) {

        System.out.println("=== 16.5 메소드 참조 ===");

        // [1] 정적 메소드 참조 : 클래스명::정적메소드
        System.out.println("\n[1] 정적 메소드 참조 (클래스명::정적메소드)");
        // 람다:  n -> MathUtil.square(n)
        Function<Integer, Integer> square = MathUtil::square;
        System.out.println("square(5) = " + square.apply(5));

        // 표준 라이브러리의 정적 메소드도 동일하게 참조 가능
        // 람다:  s -> Integer.parseInt(s)
        Function<String, Integer> toInt = Integer::parseInt;
        System.out.println("parseInt(\"123\") + 1 = " + (toInt.apply("123") + 1));

        // [2] 인스턴스 메소드 참조 : 클래스명::인스턴스메소드
        //     -> 첫 번째 인자가 "메소드를 호출하는 대상"이 된다.
        System.out.println("\n[2] 인스턴스 메소드 참조 (클래스명::인스턴스메소드)");
        // 람다:  s -> s.length()
        Function<String, Integer> length = String::length;
        System.out.println("\"람다\".length() = " + length.apply("람다"));

        // 람다:  (a, b) -> a.concat(b)   (a 가 호출 대상, b 가 인자)
        BiFunction<String, String, String> concat = String::concat;
        System.out.println("\"Hello\".concat(\" World\") = " + concat.apply("Hello", " World"));

        // [3] 특정 객체의 메소드 참조 : 객체참조::인스턴스메소드
        System.out.println("\n[3] 특정 객체의 메소드 참조 (객체::인스턴스메소드)");
        String prefix = "값은 ";
        // 람다:  s -> prefix.concat(s)   (prefix 라는 특정 객체에 고정)
        Function<String, String> addPrefix = prefix::concat;
        System.out.println(addPrefix.apply("42 입니다"));

        // System.out 이라는 특정 객체의 println 참조
        // 람다:  x -> System.out.println(x)
        java.util.function.Consumer<String> printer = System.out::println;
        printer.accept("System.out::println 로 출력한 줄");

        // [4] 참고 : 메소드 참조는 결국 람다식의 축약 표현
        System.out.println("\n[4] 정리");
        Supplier<String> version = "JDK 8+"::toString;   // 특정 객체 메소드 참조 예
        System.out.println("메소드 참조는 람다식의 축약 표현이다. (" + version.get() + ")");

        System.out.println("\n프로그램 정상 종료");
    }
}
