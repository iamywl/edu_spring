package com.edu.javabook.ch02;

/**
 * 2.2 정수 타입
 *
 * 정수를 저장하는 기본 타입은 4가지: byte, short, int, long
 * 크기(바이트)가 클수록 표현 범위가 넓어진다.
 *   byte  : 1바이트  (-128 ~ 127)
 *   short : 2바이트  (-32,768 ~ 32,767)
 *   int   : 4바이트  (약 -21억 ~ 21억)   ← 정수의 '기본' 타입
 *   long  : 8바이트  (약 -922경 ~ 922경)
 */
public class IntegerType {

    public static void main(String[] args) {

        System.out.println("=== 2.2 정수 타입 ===");

        // [1] 각 타입 선언
        System.out.println("\n[1] 네 가지 정수 타입");
        byte  b = 100;
        short s = 30000;
        int   i = 2000000000;
        long  l = 9000000000L;   // long 리터럴에는 접미사 L(또는 l) 필요
        System.out.println("byte  b = " + b);
        System.out.println("short s = " + s);
        System.out.println("int   i = " + i);
        System.out.println("long  l = " + l);

        // [2] 각 타입의 표현 범위 (Wrapper 클래스 상수 활용)
        System.out.println("\n[2] 타입별 표현 범위");
        System.out.println("byte  : " + Byte.MIN_VALUE + " ~ " + Byte.MAX_VALUE);
        System.out.println("short : " + Short.MIN_VALUE + " ~ " + Short.MAX_VALUE);
        System.out.println("int   : " + Integer.MIN_VALUE + " ~ " + Integer.MAX_VALUE);
        System.out.println("long  : " + Long.MIN_VALUE + " ~ " + Long.MAX_VALUE);

        // [3] 정수 리터럴 표기법 (같은 값 10진/2진/8진/16진)
        System.out.println("\n[3] 진법별 리터럴 (모두 값은 10)");
        int dec = 10;        // 10진수
        int bin = 0b1010;    // 2진수 (0b 접두사)
        int oct = 012;       // 8진수 (0 접두사)
        int hex = 0xA;       // 16진수 (0x 접두사)
        System.out.println("10진 10   = " + dec);
        System.out.println("2진 0b1010 = " + bin);
        System.out.println("8진 012    = " + oct);
        System.out.println("16진 0xA   = " + hex);

        // [4] 언더스코어(_)로 자릿수 구분 가능 (가독성용, 값에는 영향 없음)
        System.out.println("\n[4] 언더스코어 구분자");
        long big = 1_000_000_000L;
        System.out.println("1_000_000_000L = " + big);

        // [5] 오버플로우: 범위를 넘으면 '한 바퀴 돌아' 최솟값으로 넘어간다.
        System.out.println("\n[5] 오버플로우 시연");
        int max = Integer.MAX_VALUE;
        System.out.println("int 최댓값        : " + max);
        System.out.println("최댓값 + 1        : " + (max + 1) + "  (최솟값으로 순환!)");

        // [왜?] 큰 수를 다룰 땐 long을, 메모리가 극도로 중요할 땐 byte/short를 쓴다.
        //       평소엔 그냥 int 를 쓰면 된다(연산의 기본 단위가 int라 가장 효율적).
        System.out.println("\n[왜?] 기본은 int, 21억을 넘을 값(주민번호·타임스탬프 등)은 long 사용.");

        System.out.println("\n프로그램 정상 종료");
    }
}
