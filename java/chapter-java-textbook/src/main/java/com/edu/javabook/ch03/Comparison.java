package com.edu.javabook.ch03;

/**
 * Chapter 03 연산자 - 3.6 비교 연산자
 *
 * 이 소절 하나만 다룹니다: 비교 연산자 ==, !=, <, >, <=, >= 와
 * 두 가지 함정(실수 비교, 문자열 == vs equals).
 *
 * 핵심 결론(먼저 봅니다):
 *   - 비교 연산자의 결과는 항상 boolean(true/false)입니다.
 *   - 실수는 오차 때문에 ==로 같은지 비교하면 안 됩니다. '차이의 절댓값 < 허용오차'로 판단하세요.
 *   - 문자열의 ==는 '같은 객체인지'를 봅니다. '내용이 같은지'는 반드시 equals()를 쓰세요.
 */
public class Comparison {

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println(" 3.6 비교 연산자 (Comparison)");
        System.out.println("==================================================\n");

        basicComparisons();
        floatingComparisonTrap();
        floatingComparisonFix();
        stringEqualsVsEqualsEquals();
    }

    private static void basicComparisons() {
        System.out.println("[1] 기본 비교 (결과는 boolean)");
        int a = 5, b = 8;
        System.out.println("  5 == 8 => " + (a == b));
        System.out.println("  5 != 8 => " + (a != b));
        System.out.println("  5 <  8 => " + (a < b));
        System.out.println("  5 >  8 => " + (a > b));
        System.out.println("  5 <= 5 => " + (a <= 5));
        System.out.println();
    }

    /** 실수 == 의 함정 */
    private static void floatingComparisonTrap() {
        System.out.println("[2] 실수 비교의 함정");
        double x = 0.1 + 0.2;
        System.out.println("  (0.1 + 0.2 == 0.3) => " + (x == 0.3) + "  (오차 때문에 false!)");
        System.out.println("  실제 값: 0.1 + 0.2 = " + x);
        System.out.println();
    }

    /** 실수 비교의 올바른 방법 */
    private static void floatingComparisonFix() {
        System.out.println("[3] 실수 비교의 올바른 방법 (허용오차 사용)");
        double x = 0.1 + 0.2;
        double epsilon = 1e-9;   // 허용 오차
        boolean nearlyEqual = Math.abs(x - 0.3) < epsilon;
        System.out.println("  Math.abs((0.1+0.2) - 0.3) < 1e-9 => " + nearlyEqual);
        System.out.println("  결론: 실수는 '거의 같은가'로 비교한다.");
        System.out.println();
    }

    /** 문자열 == vs equals */
    private static void stringEqualsVsEqualsEquals() {
        System.out.println("[4] 문자열 == vs equals()");
        String a = "hello";
        String b = "hello";                 // 문자열 리터럴은 풀(pool)을 공유
        String c = new String("hello");     // new는 새 객체를 강제로 만든다
        System.out.println("  a == b        => " + (a == b) + "  (리터럴은 같은 객체를 공유하기도 함)");
        System.out.println("  a == c        => " + (a == c) + "  (new로 만든 c는 다른 객체 -> false)");
        System.out.println("  a.equals(c)   => " + a.equals(c) + "  (내용 비교는 항상 true)");
        System.out.println("  결론: 문자열 '내용' 비교는 반드시 equals()!");
        System.out.println();
        System.out.println("정상 종료.");
    }
}
