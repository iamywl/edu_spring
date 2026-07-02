package com.edu.javabook.ch03;

/**
 * Chapter 03 연산자 - 3.9 비트 이동 연산자
 *
 * 이 소절 하나만 다룹니다: 왼쪽 시프트 <<, 산술 오른쪽 시프트 >>, 논리 오른쪽 시프트 >>>.
 *
 * 핵심 결론(먼저 봅니다):
 *   - x << n : 왼쪽으로 n칸 이동 = x * 2^n (오른쪽은 0으로 채움)
 *   - x >> n : 산술 오른쪽 이동 = x / 2^n, '부호 비트'로 채움(음수는 여전히 음수 유지)
 *   - x >>> n: 논리 오른쪽 이동, 항상 '0'으로 채움 -> 음수를 넣으면 큰 양수가 됨
 *   - 음수를 다룰 때 >> 와 >>> 의 차이가 극명하게 드러납니다.
 */
public class BitShift {

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println(" 3.9 비트 이동 연산자 (BitShift)");
        System.out.println("==================================================\n");

        leftShift();
        rightShiftArithmetic();
        rightShiftLogical();
        negativeDifference();
    }

    /** << : 곱하기 2^n */
    private static void leftShift() {
        System.out.println("[1] << 왼쪽 시프트 (x * 2^n)");
        int x = 3;
        System.out.println("  3 << 1 = " + (x << 1) + "  (3 * 2)");
        System.out.println("  3 << 2 = " + (x << 2) + "  (3 * 4)");
        System.out.println("  3 << 4 = " + (x << 4) + "  (3 * 16)");
        System.out.println("  비트: " + Integer.toBinaryString(x) + " -> " + Integer.toBinaryString(x << 4));
        System.out.println();
    }

    /** >> : 나누기 2^n (부호 유지) */
    private static void rightShiftArithmetic() {
        System.out.println("[2] >> 산술 오른쪽 시프트 (x / 2^n, 부호 유지)");
        int x = 64;
        System.out.println("  64 >> 1 = " + (x >> 1) + "  (64 / 2)");
        System.out.println("  64 >> 3 = " + (x >> 3) + "  (64 / 8)");
        int neg = -64;
        System.out.println("  -64 >> 1 = " + (neg >> 1) + "  (음수도 부호 유지하며 절반)");
        System.out.println();
    }

    /** >>> : 항상 0으로 채움 */
    private static void rightShiftLogical() {
        System.out.println("[3] >>> 논리 오른쪽 시프트 (항상 0 채움)");
        int x = 64;
        System.out.println("  64 >>> 1 = " + (x >>> 1) + "  (양수는 >> 와 동일)");
        System.out.println();
    }

    /** 음수에서 >> 와 >>> 의 결정적 차이 */
    private static void negativeDifference() {
        System.out.println("[4] 음수에서 >> vs >>> 의 차이 (핵심)");
        int neg = -8;
        System.out.println("  -8 비트: " + Integer.toBinaryString(neg));
        System.out.println("  -8 >> 1  = " + (neg >> 1) + "  (부호 비트 1로 채움 -> 여전히 음수 -4)");
        System.out.println("  -8 >>> 1 = " + (neg >>> 1) + "  (0으로 채움 -> 거대한 양수!)");
        System.out.println("  -8 >>> 1 비트: " + Integer.toBinaryString(neg >>> 1));
        System.out.println("  결론: 음수를 오른쪽으로 밀 땐 >> 와 >>> 를 반드시 구분하라.");
        System.out.println();
        System.out.println("정상 종료.");
    }
}
