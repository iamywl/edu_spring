package com.edu.collections;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

/**
 * Chapter 03 - 개념 2: 함수형 인터페이스(Functional Interface)
 *
 * ======================================================================
 * "함수형 인터페이스란?"
 * ======================================================================
 * 추상 메서드가 "정확히 하나"인 인터페이스를 함수형 인터페이스라고 부릅니다.
 * (default/static 메서드는 여러 개 있어도 됨)
 * 람다식은 바로 이 함수형 인터페이스의 구현체입니다.
 *
 * @FunctionalInterface 어노테이션을 붙이면 컴파일러가
 * "추상 메서드가 1개인지" 검증해 줍니다 (실수 방지용, 필수는 아님).
 *
 * 자바는 자주 쓰는 함수형 인터페이스를 java.util.function 패키지에
 * 미리 정의해 두었습니다:
 *   - Predicate<T>       : T -> boolean        (조건 검사)
 *   - Function<T,R>      : T -> R               (변환)
 *   - Consumer<T>        : T -> void            (소비, 부수효과)
 *   - Supplier<T>        : () -> T              (공급, 생성)
 *   - BiFunction<T,U,R>  : (T,U) -> R           (인자 2개 변환)
 *   - UnaryOperator<T>   : T -> T               (같은 타입 변환)
 *   - BinaryOperator<T>  : (T,T) -> T           (같은 타입 이항 연산)
 */
public class FunctionalInterfaceExample {

    // ------------------------------------------------------------------
    // @FunctionalInterface 검증을 보여주기 위한 커스텀 인터페이스
    // ------------------------------------------------------------------

    /**
     * 추상 메서드가 1개이므로 함수형 인터페이스 조건 충족.
     * (default 메서드는 여러 개 있어도 규칙 위반이 아님)
     */
    @FunctionalInterface
    interface Greeting {
        String greet(String name);          // 유일한 추상 메서드

        default String politeGreet(String name) {   // default 는 여러 개 가능
            return greet(name) + " (정중히)";
        }
    }

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("  개념 2: 함수형 인터페이스");
        System.out.println("========================================\n");

        demonstrateCustomAndAnnotation();
        demonstratePredicate();
        demonstrateFunction();
        demonstrateConsumer();
        demonstrateSupplier();
        demonstrateBiFunction();
        demonstrateOperators();

        System.out.println("========================================");
        System.out.println("  함수형 인터페이스 예제 완료!");
        System.out.println("========================================");
    }

    // ======================================================
    // 0) @FunctionalInterface 와 커스텀 인터페이스
    // ======================================================
    static void demonstrateCustomAndAnnotation() {
        System.out.println("--- 0. @FunctionalInterface 커스텀 예제 ---");

        // 추상 메서드가 1개이므로 람다로 구현 가능
        Greeting hello = name -> "안녕하세요, " + name + "님!";
        System.out.println("  greet:       " + hello.greet("철수"));
        // default 메서드는 추상 메서드를 재사용해 동작
        System.out.println("  politeGreet: " + hello.politeGreet("철수"));

        System.out.println();
    }

    // ======================================================
    // 1) Predicate<T>: T -> boolean (조건 검사)
    // ======================================================
    static void demonstratePredicate() {
        System.out.println("--- 1. Predicate<T> : T -> boolean (조건 검사) ---");

        Predicate<Integer> isEven = n -> n % 2 == 0;
        Predicate<Integer> isPositive = n -> n > 0;
        Predicate<String> isNotEmpty = s -> !s.isEmpty();

        System.out.println("  isEven(4): " + isEven.test(4));
        System.out.println("  isEven(7): " + isEven.test(7));
        System.out.println("  isNotEmpty(\"\"): " + isNotEmpty.test(""));

        // 조합: and / or / negate
        Predicate<Integer> isEvenAndPositive = isEven.and(isPositive);
        Predicate<Integer> isOdd = isEven.negate();
        System.out.println("  isEven AND isPositive (-4): " + isEvenAndPositive.test(-4));
        System.out.println("  isEven OR  isPositive (3) : " + isEven.or(isPositive).test(3));
        System.out.println("  negate -> isOdd(3): " + isOdd.test(3));

        // 실전: 리스트 필터링
        List<Integer> numbers = List.of(-3, -2, -1, 0, 1, 2, 3);
        List<Integer> evenPositive = numbers.stream().filter(isEvenAndPositive).toList();
        System.out.println("  짝수이면서 양수: " + evenPositive);

        System.out.println();
    }

    // ======================================================
    // 2) Function<T,R>: T -> R (변환)
    // ======================================================
    static void demonstrateFunction() {
        System.out.println("--- 2. Function<T,R> : T -> R (변환) ---");

        Function<String, Integer> strLength = String::length;
        Function<Integer, String> intToStr = n -> "숫자:" + n;
        Function<String, String> toUpperCase = String::toUpperCase;

        System.out.println("  strLength(\"안녕하세요\"): " + strLength.apply("안녕하세요"));
        System.out.println("  intToStr(42): " + intToStr.apply(42));

        // andThen: 이 함수 실행 후, 결과를 다음 함수에 전달 (왼 -> 오른)
        Function<String, String> upperThenAddLength =
                toUpperCase.andThen(s -> s + " (길이: " + s.length() + ")");
        System.out.println("  andThen: " + upperThenAddLength.apply("hello"));

        // compose: 인자를 먼저 다른 함수에 넣고, 그 결과를 이 함수에 전달 (오른 -> 왼)
        Function<Integer, String> addTenThenToStr = intToStr.compose(n -> n + 10);
        System.out.println("  compose (10 더한 뒤 문자열화): " + addTenThenToStr.apply(5));

        System.out.println();
    }

    // ======================================================
    // 3) Consumer<T>: T -> void (소비 / 부수효과)
    // ======================================================
    static void demonstrateConsumer() {
        System.out.println("--- 3. Consumer<T> : T -> void (소비) ---");

        Consumer<String> printer = s -> System.out.println("    출력: " + s);
        Consumer<String> logger = s -> System.out.println("    로그: [INFO] " + s);

        printer.accept("Hello Consumer");

        // andThen: 두 소비 동작을 순서대로 실행
        Consumer<String> printAndLog = printer.andThen(logger);
        System.out.println("  printer.andThen(logger):");
        printAndLog.accept("둘 다 실행");

        System.out.println();
    }

    // ======================================================
    // 4) Supplier<T>: () -> T (공급 / 생성)
    // ======================================================
    static void demonstrateSupplier() {
        System.out.println("--- 4. Supplier<T> : () -> T (공급) ---");

        Supplier<String> greeting = () -> "안녕하세요!";
        Supplier<List<String>> listFactory = ArrayList::new;  // 생성자 참조
        Supplier<Double> randomSupplier = Math::random;       // 정적 메서드 참조

        System.out.println("  greeting.get(): " + greeting.get());
        System.out.println("  randomSupplier.get(): " + randomSupplier.get());

        List<String> newList = listFactory.get();
        newList.add("Supplier로 만든 새 리스트 원소");
        System.out.println("  listFactory.get(): " + newList);

        // Supplier 의 핵심 가치: "필요할 때 지연 생성" (값을 미리 만들지 않음)
        System.out.println();
    }

    // ======================================================
    // 5) BiFunction<T,U,R>: (T,U) -> R (인자 2개 변환)
    // ======================================================
    static void demonstrateBiFunction() {
        System.out.println("--- 5. BiFunction<T,U,R> : (T,U) -> R ---");

        BiFunction<Integer, Integer, Integer> add = (a, b) -> a + b;
        BiFunction<String, Integer, String> repeat = (s, n) -> s.repeat(n);

        System.out.println("  add(3, 4): " + add.apply(3, 4));
        System.out.println("  repeat(\"ab\", 3): " + repeat.apply("ab", 3));

        // BiFunction 도 andThen 으로 결과를 후처리할 수 있다.
        BiFunction<Integer, Integer, String> addThenLabel =
                add.andThen(sum -> "합계=" + sum);
        System.out.println("  add.andThen(라벨): " + addThenLabel.apply(10, 20));

        System.out.println();
    }

    // ======================================================
    // 6) UnaryOperator<T> / BinaryOperator<T> (같은 타입 특화)
    // ======================================================
    static void demonstrateOperators() {
        System.out.println("--- 6. UnaryOperator / BinaryOperator (같은 타입 특화) ---");

        // UnaryOperator<T> 는 Function<T,T> 의 특수형 (입력=출력 타입)
        UnaryOperator<String> exclaim = s -> s + "!";
        UnaryOperator<Integer> doubleIt = n -> n * 2;
        System.out.println("  UnaryOperator exclaim: " + exclaim.apply("와우"));
        System.out.println("  UnaryOperator doubleIt: " + doubleIt.apply(21));

        // BinaryOperator<T> 는 BiFunction<T,T,T> 의 특수형
        BinaryOperator<Integer> maxOp = Integer::max;
        BinaryOperator<String> concat = String::concat;
        System.out.println("  BinaryOperator max(3,7): " + maxOp.apply(3, 7));
        System.out.println("  BinaryOperator concat: " + concat.apply("Hello ", "World"));

        System.out.println();
    }
}
