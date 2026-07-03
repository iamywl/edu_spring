package com.edu.javabook.ch03;

/**
 * Chapter 03 연산자 - 3.2 산술 연산자
 *
 * 이 소절 하나만 다룹니다: 이항 산술 연산자 +, -, *, /, %.
 *
 * 핵심 결론(먼저 봅니다):
 *   - 정수끼리의 /는 몫만 남기고 소수부를 '버립니다'(반올림 아님, 0 방향 절삭).
 *   - %(나머지)의 부호는 '피제수(왼쪽 값)'의 부호를 따릅니다.
 *   - 실수(double)가 하나라도 섞이면 실수 나눗셈이 되어 소수부가 보존됩니다.
 *   - +는 문자열이 끼면 '연결(concatenation)'로 의미가 바뀝니다.
 */
public class Arithmetic {

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println(" 3.2 산술 연산자 (Arithmetic)");
        System.out.println("==================================================\n");

        basics();
        integerDivision();
        modulo();
        floatingDivision();
        plusIsOverloaded();
    }

    private static void basics() {
        System.out.println("[1] 기본 사칙연산");
        int a = 17, b = 5;
        System.out.println("  a=17, b=5");
        System.out.println("  a + b = " + (a + b));
        System.out.println("  a - b = " + (a - b));
        System.out.println("  a * b = " + (a * b));
        System.out.println();
    }

    /** 정수 나눗셈은 소수부를 버린다(0 방향으로 절삭). */
    private static void integerDivision() {
        System.out.println("[2] 정수 나눗셈 / (소수부 버림)");
        System.out.println("  17 / 5 = " + (17 / 5) + "  (3.4가 아니라 3)");
        System.out.println("  -17 / 5 = " + (-17 / 5) + "  (0 방향 절삭이므로 -3, -4 아님)");
        System.out.println("  왜? 두 피연산자가 모두 int면 결과 타입도 int라 소수부가 존재할 수 없음.");
        System.out.println();
    }

    /** 나머지 %의 부호는 왼쪽 값을 따른다. */
    private static void modulo() {
        System.out.println("[3] 나머지 연산자 %");
        System.out.println("  17 % 5 = " + (17 % 5));
        System.out.println("  -17 % 5 = " + (-17 % 5) + "  (부호는 왼쪽(-17)을 따름)");
        System.out.println("  17 % -5 = " + (17 % -5) + "  (오른쪽 부호는 무관)");
        System.out.println("  항등식 확인: a == (a/b)*b + (a%b) => -17 == (-3)*5 + (-2) = "
                + ((-3) * 5 + (-2)));
        System.out.println();
    }

    /** 실수가 섞이면 실수 나눗셈. */
    private static void floatingDivision() {
        System.out.println("[4] 실수 나눗셈");
        System.out.println("  17 / 5.0 = " + (17 / 5.0) + "  (한쪽이 double이면 실수 연산)");
        System.out.println("  (double)17 / 5 = " + ((double) 17 / 5));
        System.out.println("  주의: (17 / 5) * 1.0 = " + ((17 / 5) * 1.0)
                + "  (먼저 정수나눗셈으로 3이 된 뒤 3.0)");
        System.out.println();
    }

    /** +는 문자열이 끼면 연결 연산자가 된다. */
    private static void plusIsOverloaded() {
        System.out.println("[5] + 는 문자열이면 '연결'로 바뀐다");
        System.out.println("  1 + 2 + \"cm\" = " + (1 + 2 + "cm") + "  (왼쪽부터: 1+2=3, 그 뒤 문자열 연결)");
        System.out.println("  \"cm\" + 1 + 2 = " + ("cm" + 1 + 2) + "  (문자열이 먼저라 1,2가 각각 연결됨)");
        System.out.println();
        System.out.println("정상 종료.");
    }
}
