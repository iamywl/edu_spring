package com.edu.javabook.ch02;

/**
 * 2.4 실수 타입
 *
 * 소수점이 있는 수(실수)를 저장하는 타입은 2가지: float, double
 *   float  : 4바이트, 유효자릿수 약 7자리,  리터럴 접미사 f 필요
 *   double : 8바이트, 유효자릿수 약 15자리, 실수의 '기본' 타입
 *
 * 컴퓨터는 실수를 2진 부동소수점(IEEE 754)으로 저장하기 때문에
 * 0.1 같은 값을 '정확히' 표현하지 못한다 → 미세한 오차가 생긴다.
 */
public class FloatType {

    public static void main(String[] args) {

        System.out.println("=== 2.4 실수 타입 ===");

        // [1] float 과 double
        System.out.println("\n[1] float / double 선언");
        float  f = 3.14f;   // f 접미사가 없으면 컴파일 에러 (기본 실수 리터럴은 double)
        double d = 3.14;
        System.out.println("float  f = " + f);
        System.out.println("double d = " + d);

        // [2] 정밀도(유효자릿수) 차이
        System.out.println("\n[2] 정밀도 비교 (1/3)");
        float  f2 = 1.0f / 3.0f;
        double d2 = 1.0  / 3.0;
        System.out.println("float  1/3 = " + f2 + "  (약 7자리에서 끊김)");
        System.out.println("double 1/3 = " + d2 + "  (약 15자리로 더 정밀)");

        // [3] 지수 표기법 (e)
        System.out.println("\n[3] 지수 표기법");
        double big   = 3.5e3;    // 3.5 x 10^3 = 3500.0
        double small = 3.5e-3;   // 3.5 x 10^-3 = 0.0035
        System.out.println("3.5e3  = " + big);
        System.out.println("3.5e-3 = " + small);

        // [4] 부동소수점 오차 맛보기 (가장 유명한 예)
        System.out.println("\n[4] 부동소수점 오차 시연");
        double result = 0.1 + 0.2;
        System.out.println("0.1 + 0.2 = " + result + "  (0.3 이 아니다!)");
        System.out.println("(0.1+0.2 == 0.3) ? " + (result == 0.3));

        // [5] 오차를 감안한 안전한 비교 방법
        System.out.println("\n[5] 실수 비교는 '차이가 아주 작은지'로 판단");
        double epsilon = 1e-9;   // 허용 오차
        boolean nearlyEqual = Math.abs(result - 0.3) < epsilon;
        System.out.println("|(0.1+0.2) - 0.3| < 1e-9 ? " + nearlyEqual);

        // [6] 특수 값: 무한대, NaN
        System.out.println("\n[6] 특수 값");
        System.out.println("1.0/0.0 = " + (1.0 / 0.0) + "  (무한대, Infinity)");
        System.out.println("0.0/0.0 = " + (0.0 / 0.0) + "  (수 아님, NaN)");

        // [왜?] 돈 계산처럼 오차가 치명적이면 double 대신 BigDecimal 을 쓴다.
        System.out.println("\n[왜?] 정밀한 소수 계산이 필요하면 double 대신 BigDecimal을 사용한다.");

        System.out.println("\n프로그램 정상 종료");
    }
}
