package com.edu.javabook.ch02;

/**
 * 2.9 연산식에서의 자동 타입 변환
 *
 * 서로 다른 타입끼리 연산하면, 자바는 두 값을 '더 큰 타입'으로 맞춘 뒤 계산한다.
 * 또한 byte/short/char 는 연산 시 무조건 int 로 승격된 뒤 계산된다.
 *
 * 규칙 요약:
 *   - 피연산자 중 하나가 double이면 → 결과 double
 *   - 아니고 하나가 float이면      → 결과 float
 *   - 아니고 하나가 long이면       → 결과 long
 *   - 그 외 (byte/short/char/int)  → 결과 int
 */
public class CastingInExpression {

    public static void main(String[] args) {

        System.out.println("=== 2.9 연산식에서의 자동 타입 변환 ===");

        // [1] int + double → double
        System.out.println("\n[1] int + double");
        int    i = 5;
        double d = 2.0;
        double r1 = i + d;   // 5가 5.0으로 변환된 뒤 계산
        System.out.println("5 + 2.0 = " + r1 + "  (결과 double)");

        // [2] byte + byte → int (작은 타입은 int로 승격)
        System.out.println("\n[2] byte + byte → int");
        byte b1 = 10, b2 = 20;
        int sum = b1 + b2;   // 결과가 int라서 byte로 못 받는다
        System.out.println("byte10 + byte20 = " + sum + "  (결과 타입은 int!)");
        // byte b3 = b1 + b2;  // ← 컴파일 에러: 결과 int를 byte에 못 담음

        // [3] 정수끼리 나눗셈은 몫만 남는다 (소수점 사라짐)
        System.out.println("\n[3] 정수 나눗셈 함정");
        int a = 7, c = 2;
        System.out.println("7 / 2        = " + (a / c) + "  (정수 나눗셈, 몫만)");
        System.out.println("7 / 2.0      = " + (a / 2.0) + "  (한쪽이 실수면 실수 결과)");
        System.out.println("(double)7/2  = " + ((double) a / c) + "  (미리 double로 캐스팅)");

        // [4] char 도 연산 시 int 로 승격된다
        System.out.println("\n[4] char 연산");
        char ch = 'A';
        int  after = ch + 1;   // 'A'(65) + 1 = 66 (int)
        System.out.println("'A' + 1 = " + after + "  (char가 int로 승격되어 계산)");
        System.out.println("다시 char로: " + (char) after);

        // [5] long 이 섞이면 결과는 long
        System.out.println("\n[5] int + long → long");
        int  x = 1;
        long y = 3_000_000_000L;
        long r2 = x + y;
        System.out.println("1 + 3_000_000_000L = " + r2 + "  (결과 long)");

        // [왜?] 자동 승격을 모르면 '정수 나눗셈으로 소수점이 사라지는' 흔한 버그에 빠진다.
        System.out.println("\n[왜?] 나눗셈 전 한쪽을 (double)로 캐스팅해야 소수점 결과를 얻는다.");

        System.out.println("\n프로그램 정상 종료");
    }
}
