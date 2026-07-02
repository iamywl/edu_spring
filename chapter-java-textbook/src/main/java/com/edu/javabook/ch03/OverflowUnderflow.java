package com.edu.javabook.ch03;

/**
 * Chapter 03 연산자 - 3.3 오버플로우와 언더플로우
 *
 * 이 소절 하나만 다룹니다: 정수 타입이 표현 범위를 넘어설 때 벌어지는 순환(wraparound).
 *
 * 핵심 결론(먼저 봅니다):
 *   - int/long은 고정된 비트 수만 씁니다(int=32비트). 범위를 넘으면 예외가 아니라 '조용히' 값이 순환합니다.
 *   - MAX_VALUE + 1 은 MIN_VALUE가 됩니다(2의 보수 표현에서 최상위 비트가 넘치기 때문).
 *   - 오버플로우는 에러를 던지지 않으므로 가장 위험합니다. 큰 수는 long/BigInteger로.
 *   - (참고) 부동소수 언더플로우: 아주 작은 double은 0.0으로 가라앉습니다.
 */
public class OverflowUnderflow {

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println(" 3.3 오버플로우와 언더플로우 (OverflowUnderflow)");
        System.out.println("==================================================\n");

        intRange();
        overflowWraparound();
        twosComplementView();
        multiplicationOverflow();
        floatingUnderflow();
    }

    private static void intRange() {
        System.out.println("[1] int의 표현 범위");
        System.out.println("  Integer.MAX_VALUE = " + Integer.MAX_VALUE);
        System.out.println("  Integer.MIN_VALUE = " + Integer.MIN_VALUE);
        System.out.println("  int는 32비트 고정 -> 약 -21억 ~ +21억");
        System.out.println();
    }

    /** MAX + 1 이 MIN으로 순환한다. */
    private static void overflowWraparound() {
        System.out.println("[2] 오버플로우: MAX + 1 = MIN (순환)");
        int max = Integer.MAX_VALUE;
        int wrapped = max + 1;   // 예외 없이 조용히 순환
        System.out.println("  MAX_VALUE + 1 = " + wrapped + "  (=" + Integer.MIN_VALUE + ")");
        System.out.println("  MIN_VALUE - 1 = " + (Integer.MIN_VALUE - 1) + "  (반대로 MAX로 순환)");
        System.out.println("  주의: 예외가 안 나므로 눈치채기 어렵다!");
        System.out.println();
    }

    /** 2의 보수 관점: 비트 패턴으로 이해 */
    private static void twosComplementView() {
        System.out.println("[3] 2의 보수 관점 (비트로 이해)");
        int max = Integer.MAX_VALUE;
        System.out.println("  MAX_VALUE 비트: " + Integer.toBinaryString(max));
        System.out.println("  MAX+1    비트: " + Integer.toBinaryString(max + 1));
        System.out.println("  0111...1 에 1을 더하면 1000...0 이 되고,");
        System.out.println("  2의 보수에서 최상위 1은 '음수(가장 작은 값)'를 뜻함 -> MIN_VALUE");
        System.out.println();
    }

    /** 곱셈에서의 조용한 오버플로우와 해결 */
    private static void multiplicationOverflow() {
        System.out.println("[4] 곱셈 오버플로우와 해결책");
        int big = 100_000;
        System.out.println("  int  100000 * 100000 = " + (big * big) + "  (오버플로우로 엉뚱한 값)");
        long safe = (long) big * big;   // 한쪽을 long으로 승격시켜야 안전
        System.out.println("  long (long)100000 * 100000 = " + safe + "  (long으로 승격하면 정상)");
        try {
            Math.multiplyExact(big, big);   // 오버플로우를 예외로 잡고 싶을 때
        } catch (ArithmeticException e) {
            System.out.println("  Math.multiplyExact -> 오버플로우 시 예외: " + e.getMessage());
        }
        System.out.println();
    }

    /** 부동소수 언더플로우 참고 */
    private static void floatingUnderflow() {
        System.out.println("[5] (참고) 부동소수 언더플로우");
        double tiny = Double.MIN_VALUE;   // 표현 가능한 가장 작은 양수
        System.out.println("  Double.MIN_VALUE = " + tiny);
        System.out.println("  MIN_VALUE / 2 = " + (tiny / 2) + "  (더 작아지면 0.0으로 가라앉음)");
        System.out.println();
        System.out.println("정상 종료.");
    }
}
