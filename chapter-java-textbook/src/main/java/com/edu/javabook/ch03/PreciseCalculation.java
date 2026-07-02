package com.edu.javabook.ch03;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Chapter 03 연산자 - 3.4 정확한 계산은 정수 연산으로
 *
 * 이 소절 하나만 다룹니다: 부동소수(double) 오차의 실체와, 정수/BigDecimal로 정확히 계산하는 법.
 *
 * 핵심 결론(먼저 봅니다):
 *   - double은 10진 소수를 2진 분수로 근사할 뿐이라 0.1 + 0.2 != 0.3 이 됩니다.
 *   - 돈 계산은 절대 double로 하지 마세요. '최소 단위(원/센트)'의 정수로 다루거나 BigDecimal을 쓰세요.
 *   - BigDecimal도 new BigDecimal(0.1)처럼 double을 넘기면 오차가 그대로 들어옵니다 -> 반드시 문자열로.
 */
public class PreciseCalculation {

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println(" 3.4 정확한 계산은 정수 연산으로 (PreciseCalculation)");
        System.out.println("==================================================\n");

        theBug();
        integerApproach();
        bigDecimalRight();
        bigDecimalTrap();
    }

    /** 부동소수 오차의 실체 */
    private static void theBug() {
        System.out.println("[1] 부동소수 오차의 실체");
        double sum = 0.1 + 0.2;
        System.out.println("  0.1 + 0.2 = " + sum + "  (0.3이 아님!)");
        System.out.println("  (0.1 + 0.2 == 0.3) => " + (sum == 0.3));
        System.out.println("  왜? 0.1, 0.2는 2진수로 무한소수라 반올림 오차가 섞임.");
        System.out.println();
    }

    /** 정수 단위로 계산하기: 돈은 '원' 단위 정수로 */
    private static void integerApproach() {
        System.out.println("[2] 정수 단위로 계산하기 (권장)");
        // 1200.50원, 350.25원을 '전(1/100원)' 단위 정수로 바꿔 계산
        long priceCents = 120050;   // 1200.50원
        long taxCents = 35025;      // 350.25원
        long totalCents = priceCents + taxCents;
        System.out.println("  1200.50 + 350.25 을 '전' 단위 정수로: " + totalCents + "전");
        System.out.println("  다시 원 단위로: " + (totalCents / 100) + "." + String.format("%02d", totalCents % 100) + "원");
        System.out.println("  오차 없이 정확! 화폐/수량은 최소 단위 정수로 다룬다.");
        System.out.println();
    }

    /** BigDecimal로 정확히: 문자열 생성자 사용 */
    private static void bigDecimalRight() {
        System.out.println("[3] BigDecimal로 정확히 (문자열 생성자)");
        BigDecimal a = new BigDecimal("0.1");
        BigDecimal b = new BigDecimal("0.2");
        System.out.println("  new BigDecimal(\"0.1\") + new BigDecimal(\"0.2\") = " + a.add(b));
        BigDecimal price = new BigDecimal("100.00");
        BigDecimal divided = price.divide(new BigDecimal("3"), 2, RoundingMode.HALF_UP);
        System.out.println("  100.00 / 3 (소수 2자리 반올림) = " + divided);
        System.out.println();
    }

    /** BigDecimal의 함정: double 생성자 */
    private static void bigDecimalTrap() {
        System.out.println("[4] BigDecimal의 함정 (double 생성자 금지)");
        System.out.println("  new BigDecimal(0.1)   = " + new BigDecimal(0.1) + "  <- double 오차가 그대로!");
        System.out.println("  new BigDecimal(\"0.1\") = " + new BigDecimal("0.1") + "  <- 문자열이면 정확");
        System.out.println("  결론: BigDecimal은 반드시 문자열로 생성하라.");
        System.out.println();
        System.out.println("정상 종료.");
    }
}
