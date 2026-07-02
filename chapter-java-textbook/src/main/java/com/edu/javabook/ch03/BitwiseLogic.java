package com.edu.javabook.ch03;

/**
 * Chapter 03 연산자 - 3.8 비트 논리 연산자
 *
 * 이 소절 하나만 다룹니다: 비트 단위 AND(&), OR(|), XOR(^), NOT(~).
 *
 * 핵심 결론(먼저 봅니다):
 *   - 비트 연산은 정수를 2진수로 펼쳐서 '같은 자리끼리' 논리 연산을 합니다.
 *   - & : 둘 다 1일 때만 1 (마스킹/특정 비트 확인에 사용)
 *   - | : 하나라도 1이면 1 (특정 비트 켜기)
 *   - ^ : 서로 다르면 1 (토글, 같은 값 두 번 XOR하면 원복)
 *   - ~ : 모든 비트 반전 (~x == -x - 1, 2의 보수 때문)
 */
public class BitwiseLogic {

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println(" 3.8 비트 논리 연산자 (BitwiseLogic)");
        System.out.println("==================================================\n");

        and();
        or();
        xor();
        not();
        practicalMasking();
    }

    /** 8비트 폭으로 이진 표기를 보기 좋게 출력 */
    private static String bin8(int v) {
        String s = Integer.toBinaryString(v & 0xFF);
        return "0".repeat(8 - s.length()) + s;
    }

    private static void and() {
        System.out.println("[1] & (비트 AND: 둘 다 1일 때만 1)");
        int a = 0b1100;  // 12
        int b = 0b1010;  // 10
        System.out.println("  a = " + bin8(a) + " (12)");
        System.out.println("  b = " + bin8(b) + " (10)");
        System.out.println("  a & b = " + bin8(a & b) + " (" + (a & b) + ")");
        System.out.println();
    }

    private static void or() {
        System.out.println("[2] | (비트 OR: 하나라도 1이면 1)");
        int a = 0b1100, b = 0b1010;
        System.out.println("  a | b = " + bin8(a | b) + " (" + (a | b) + ")");
        System.out.println();
    }

    private static void xor() {
        System.out.println("[3] ^ (비트 XOR: 서로 다르면 1)");
        int a = 0b1100, b = 0b1010;
        System.out.println("  a ^ b = " + bin8(a ^ b) + " (" + (a ^ b) + ")");
        int twice = (a ^ b) ^ b;   // 같은 값으로 두 번 XOR -> 원복
        System.out.println("  (a ^ b) ^ b = " + bin8(twice) + " (" + twice + ") <- a로 원복됨");
        System.out.println();
    }

    private static void not() {
        System.out.println("[4] ~ (비트 NOT: 모든 비트 반전)");
        int x = 5;
        System.out.println("  x  = 5   비트(32): " + Integer.toBinaryString(x));
        System.out.println("  ~x = " + (~x) + "   (2의 보수라 ~x == -x - 1 = " + (-x - 1) + ")");
        System.out.println();
    }

    /** 실전: 마스킹으로 특정 비트 확인/설정 */
    private static void practicalMasking() {
        System.out.println("[5] 실전: 마스킹 (권한 플래그 예)");
        int READ = 0b100, WRITE = 0b010, EXEC = 0b001;
        int perm = READ | WRITE;   // 읽기+쓰기 권한 부여
        System.out.println("  perm = READ|WRITE = " + bin8(perm) + " (" + perm + ")");
        System.out.println("  쓰기 권한 있나? (perm & WRITE) != 0 => " + ((perm & WRITE) != 0));
        System.out.println("  실행 권한 있나? (perm & EXEC)  != 0 => " + ((perm & EXEC) != 0));
        System.out.println();
        System.out.println("정상 종료.");
    }
}
