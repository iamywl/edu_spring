package com.edu.collections;

import java.util.ArrayList;
import java.util.List;

/**
 * Chapter 03 - 개념 1: 람다식(Lambda Expression) 기초
 *
 * ======================================================================
 * "람다식이란 무엇인가?"
 * ======================================================================
 * 람다식은 "이름 없는 함수(익명 함수)"를 간결하게 표현한 것입니다.
 * 즉, 메서드 하나를 값처럼 변수에 담거나 인자로 전달할 수 있게 해줍니다.
 *
 * 자바에서 람다식은 "함수형 인터페이스"(추상 메서드가 1개인 인터페이스)의
 * 구현체를 짧게 작성하는 문법적 설탕(syntactic sugar)입니다.
 *
 * [기본 문법]
 *   (매개변수) -> 표현식
 *   (매개변수) -> { 문장들; return 값; }
 *
 * 이 파일은 "왜 람다인가?"를 익명 클래스와 비교하며 단계적으로 보여줍니다.
 */
public class LambdaBasicsExample {

    // ------------------------------------------------------------------
    // 이 예제에서만 쓰는 작은 함수형 인터페이스 (파일 자체 완결성을 위해 중첩 선언)
    // ------------------------------------------------------------------

    /**
     * @FunctionalInterface: 추상 메서드가 정확히 1개인 인터페이스를 보장.
     * 두 정수를 받아 하나의 정수를 반환하는 "계산" 개념.
     */
    @FunctionalInterface
    interface Calculator {
        int calculate(int a, int b);
    }

    /**
     * 문자열 하나를 받아 다른 문자열로 변환하는 함수형 인터페이스.
     */
    @FunctionalInterface
    interface StringTransformer {
        String transform(String input);
    }

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("  개념 1: 람다식(Lambda) 기초");
        System.out.println("========================================\n");

        demonstrateAnonymousToLambda();
        demonstrateSyntaxForms();
        demonstrateCustomFunctionalInterfaces();
        demonstrateVariableCapture();

        System.out.println("========================================");
        System.out.println("  람다식 기초 예제 완료!");
        System.out.println("========================================");
    }

    // ======================================================
    // 1) 익명 클래스 -> 람다로 축약되는 과정
    // ======================================================
    static void demonstrateAnonymousToLambda() {
        System.out.println("--- 1. 익명 클래스에서 람다로 축약 ---");

        // (A) 예전 방식: 익명 클래스로 인터페이스 구현
        //     -> new 인터페이스() { ...메서드 본문... } 형태로 장황함
        Calculator addOldStyle = new Calculator() {
            @Override
            public int calculate(int a, int b) {
                return a + b;
            }
        };
        System.out.println("  익명 클래스 방식 add(2,3): " + addOldStyle.calculate(2, 3));

        // (B) 람다 방식: 어차피 메서드가 하나뿐이므로,
        //     "매개변수 -> 반환값" 만 남기고 나머지는 컴파일러가 추론.
        //     new Calculator(){...} 껍데기, 메서드 이름, 반환 타입이 모두 생략됨.
        Calculator addLambda = (a, b) -> a + b;
        System.out.println("  람다 방식      add(2,3): " + addLambda.calculate(2, 3));

        // 정리: 아래 두 줄은 완전히 같은 동작을 한다.
        //   new Calculator(){ public int calculate(int a,int b){ return a+b; } }
        //   (a, b) -> a + b
        System.out.println();
    }

    // ======================================================
    // 2) 람다의 여러 문법 형태
    // ======================================================
    static void demonstrateSyntaxForms() {
        System.out.println("--- 2. 람다 문법의 여러 형태 ---");

        // 형태 1: 매개변수 2개, 단일 표현식 (return/중괄호 생략)
        Calculator multiply = (a, b) -> a * b;
        System.out.println("  단일 표현식 곱하기 4*5 = " + multiply.calculate(4, 5));

        // 형태 2: 매개변수 타입을 명시할 수도 있음 (보통은 생략하고 추론에 맡김)
        Calculator subtract = (int a, int b) -> a - b;
        System.out.println("  타입 명시 빼기   9-4 = " + subtract.calculate(9, 4));

        // 형태 3: 여러 문장이 필요하면 { } 블록 + 명시적 return
        Calculator divideWithLog = (a, b) -> {
            System.out.println("    [로그] 나눗셈 수행: " + a + " / " + b);
            if (b == 0) {
                return 0; // 0으로 나누기 방어
            }
            return a / b;
        };
        System.out.println("  블록 람다 나누기 10/3 = " + divideWithLog.calculate(10, 3));

        // 형태 4: 매개변수 1개면 괄호 생략 가능
        StringTransformer toUpper = input -> input.toUpperCase();     // 괄호 없음
        StringTransformer addBrackets = (input) -> "[" + input + "]"; // 괄호 있어도 OK
        System.out.println("  괄호 생략 대문자 : " + toUpper.transform("hello"));
        System.out.println("  괄호 포함 감싸기 : " + addBrackets.transform("hello"));

        System.out.println();
    }

    // ======================================================
    // 3) 커스텀 함수형 인터페이스로 함수를 값처럼 다루기
    // ======================================================
    static void demonstrateCustomFunctionalInterfaces() {
        System.out.println("--- 3. 함수를 값처럼 다루기 (왜 람다인가?) ---");

        // 람다 덕분에 "동작(behavior) 그 자체"를 변수에 담을 수 있다.
        // 즉, 데이터뿐 아니라 "연산"도 값처럼 저장/전달 가능해진다.
        Calculator add = (a, b) -> a + b;
        Calculator subtract = (a, b) -> a - b;
        Calculator multiply = (a, b) -> a * b;

        // 여러 연산을 리스트에 담아두고 순회 실행 (함수를 데이터처럼 취급)
        List<Calculator> operations = List.of(add, subtract, multiply);
        String[] labels = {"더하기", "빼기", "곱하기"};
        for (int i = 0; i < operations.size(); i++) {
            System.out.println("  " + labels[i] + " 10, 3 => " + operations.get(i).calculate(10, 3));
        }

        // 람다를 메서드의 인자로 전달하기 (전략을 주입하는 패턴)
        System.out.println("  applyOperation(add):      " + applyOperation(7, 8, add));
        System.out.println("  applyOperation(즉석 람다): " + applyOperation(7, 8, (a, b) -> a * a + b * b));

        System.out.println();
    }

    /** 람다(동작)를 인자로 받아 실행하는 헬퍼 - "함수를 값으로 전달" 시연용 */
    static int applyOperation(int a, int b, Calculator op) {
        return op.calculate(a, b);
    }

    // ======================================================
    // 4) 변수 캡처와 effectively final
    // ======================================================
    static void demonstrateVariableCapture() {
        System.out.println("--- 4. 변수 캡처 (effectively final) ---");

        // 람다는 자신을 감싸는 스코프의 지역 변수를 "캡처"해서 사용할 수 있다.
        int base = 100;                 // 지역 변수 캡처
        String tag = "결과";            // 이 역시 캡처됨

        // 캡처되는 지역 변수는 반드시 final 또는 "effectively final"(값이 변하지 않음)이어야 한다.
        //   -> 만약 아래 주석을 풀어 base 값을 바꾸면 컴파일 에러가 난다.
        //   base = 200; // (컴파일 에러: 캡처된 변수는 수정 불가)
        Calculator addBase = (a, b) -> {
            // base, tag 는 읽기만 가능. 값을 바꾸려 하면 컴파일 에러.
            System.out.println("    [" + tag + "] base(" + base + ") + a + b");
            return base + a + b;
        };
        System.out.println("  캡처된 base로 계산: " + addBase.calculate(1, 2));

        // "왜 final이어야 하나?"
        //   람다는 나중에(다른 스레드에서도) 실행될 수 있어, 원본 지역 변수가 사라진 뒤에도
        //   값을 안전하게 쓰려면 그 값이 변하지 않는다는 보장이 필요하기 때문.

        // 참고: 인스턴스/정적 필드는 이 제약을 받지 않지만,
        //       여기서는 지역 변수 캡처 개념에 집중한다.

        // 캡처를 이용해 "설정값이 고정된 함수"를 만들어 반환하는 활용 예:
        List<String> results = new ArrayList<>();
        for (String name : List.of("Alice", "Bob")) {
            // 반복 변수 name 도 각 반복마다 effectively final 로 캡처된다.
            StringTransformer greeter = input -> input + " -> 안녕하세요, " + name + "님";
            results.add(greeter.transform("인사"));
        }
        results.forEach(r -> System.out.println("  " + r));

        System.out.println();
    }
}
