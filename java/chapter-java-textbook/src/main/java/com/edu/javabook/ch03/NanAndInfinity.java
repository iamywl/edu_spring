package com.edu.javabook.ch03;

/**
 * Chapter 03 연산자 - 3.5 나눗셈 후 NaN과 Infinity
 *
 * 이 소절 하나만 다룹니다: 실수 나눗셈이 만들어내는 특수값 NaN/Infinity와, 정수 0 나눗셈 예외.
 *
 * 핵심 결론(먼저 봅니다):
 *   - 정수 나눗셈에서 0으로 나누면 ArithmeticException(예외)이 발생합니다.
 *   - 실수 나눗셈에서 0.0으로 나누면 예외 없이 Infinity(무한대) 또는 NaN(비수)이 나옵니다.
 *   - 1.0/0.0 = Infinity, 0.0/0.0 = NaN.
 *   - NaN은 자기 자신과도 == 가 false입니다. 반드시 Double.isNaN()으로 검사하세요.
 */
public class NanAndInfinity {

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println(" 3.5 나눗셈 후 NaN과 Infinity (NanAndInfinity)");
        System.out.println("==================================================\n");

        integerDivByZero();
        floatingInfinity();
        nan();
        nanIsWeird();
        safeChecks();
    }

    /** 정수 0으로 나누기: 예외 발생 */
    private static void integerDivByZero() {
        System.out.println("[1] 정수 0으로 나누기 -> 예외");
        try {
            int bad = 10 / 0;   // 여기서 예외 발생
            System.out.println("  도달 못 함: " + bad);
        } catch (ArithmeticException e) {
            System.out.println("  10 / 0 -> ArithmeticException: " + e.getMessage());
        }
        System.out.println("  정수 세계에는 무한대가 없어서 예외로 처리한다.");
        System.out.println();
    }

    /** 실수 1.0/0.0 = Infinity */
    private static void floatingInfinity() {
        System.out.println("[2] 실수 0.0으로 나누기 -> Infinity (예외 아님)");
        System.out.println("  1.0 / 0.0 = " + (1.0 / 0.0) + "  (양의 무한대)");
        System.out.println("  -1.0 / 0.0 = " + (-1.0 / 0.0) + "  (음의 무한대)");
        System.out.println("  Double.POSITIVE_INFINITY = " + Double.POSITIVE_INFINITY);
        System.out.println();
    }

    /** 0.0/0.0 = NaN */
    private static void nan() {
        System.out.println("[3] 0.0 / 0.0 -> NaN (Not a Number)");
        double nan = 0.0 / 0.0;
        System.out.println("  0.0 / 0.0 = " + nan);
        System.out.println("  무한대 - 무한대 = " + (Double.POSITIVE_INFINITY - Double.POSITIVE_INFINITY));
        System.out.println();
    }

    /** NaN의 이상한 성질 */
    private static void nanIsWeird() {
        System.out.println("[4] NaN의 이상한 성질 (== 로 못 잡는다)");
        double nan = 0.0 / 0.0;
        System.out.println("  (nan == nan) => " + (nan == nan) + "  (자기 자신과도 false!)");
        System.out.println("  (nan != nan) => " + (nan != nan) + "  (역설적으로 이걸로 판별 가능)");
        System.out.println();
    }

    /** 안전한 검사 방법 */
    private static void safeChecks() {
        System.out.println("[5] 안전한 검사: isNaN / isInfinite");
        double a = 0.0 / 0.0;
        double b = 1.0 / 0.0;
        System.out.println("  Double.isNaN(0.0/0.0)      => " + Double.isNaN(a));
        System.out.println("  Double.isInfinite(1.0/0.0) => " + Double.isInfinite(b));
        System.out.println("  결론: 실수 나눗셈 결과는 isNaN/isInfinite로 방어하라.");
        System.out.println();
        System.out.println("정상 종료.");
    }
}
