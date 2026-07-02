package com.edu.javabook.ch12;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * 12.9 형식 클래스
 *
 * 숫자나 문자열을 "보기 좋은 형식"으로 출력하는 방법을 다룬다.
 *
 * 서식 문자열(format string) :
 *   - String.format(...) / System.out.printf(...) 에서 사용한다.
 *   - %d(정수), %f(실수), %s(문자열), %x(16진수) 등 "변환 문자"로 값을 끼워 넣는다.
 *   - 폭/정밀도 지정 : %5d(5칸), %.2f(소수 2자리), %,d(천 단위 콤마) 등
 *
 * DecimalFormat :
 *   - 숫자를 원하는 패턴으로 문자열화한다. (#,###.## 처럼 패턴 지정)
 *   - 0 은 자리 채움, # 은 필요할 때만 표시.
 *
 * NumberFormat :
 *   - 통화/백분율 등 "지역(Locale)에 맞는" 숫자 형식을 제공한다.
 *
 * 이 소절에서는 printf/format 서식, DecimalFormat, NumberFormat 을 확인한다.
 */
public class FormatClass {

    public static void main(String[] args) {

        System.out.println("=== 12.9 형식 클래스 ===");

        // [1] printf / String.format 서식
        System.out.println("\n[1] printf / String.format 서식");
        System.out.printf("  정수 %%d       : [%d]%n", 42);
        System.out.printf("  폭 지정 %%5d   : [%5d]%n", 42);        // 오른쪽 정렬 5칸
        System.out.printf("  실수 %%.2f     : [%.2f]%n", 3.14159);   // 소수 2자리
        System.out.printf("  천단위 %%,d    : [%,d]%n", 1234567);    // 콤마
        System.out.printf("  문자열 %%-10s  : [%-10s]끝%n", "hi");    // 왼쪽 정렬 10칸
        String s = String.format("  format 반환값 : 이름=%s, 점수=%d", "홍길동", 95);
        System.out.println(s);

        // [2] DecimalFormat : 패턴으로 숫자 형식화
        System.out.println("\n[2] DecimalFormat");
        DecimalFormat df1 = new DecimalFormat("#,###");
        DecimalFormat df2 = new DecimalFormat("#,##0.00");
        DecimalFormat df3 = new DecimalFormat("000");
        System.out.println("  \"#,###\"   → " + df1.format(1234567));
        System.out.println("  \"#,##0.00\"→ " + df2.format(1234.5));
        System.out.println("  \"000\"     → " + df3.format(7) + " (0 은 자리 채움)");

        // [3] NumberFormat : 통화 / 백분율 (Locale 지정)
        System.out.println("\n[3] NumberFormat (통화 / 백분율)");
        NumberFormat currency = NumberFormat.getCurrencyInstance(Locale.KOREA);
        NumberFormat percent = NumberFormat.getPercentInstance();
        NumberFormat number = NumberFormat.getNumberInstance();
        System.out.println("  통화(한국)  = " + currency.format(50000));
        System.out.println("  백분율      = " + percent.format(0.875) + " (0.875 → 반올림 표시)");
        System.out.println("  일반 숫자   = " + number.format(1234567.89));

        System.out.println("\n프로그램 정상 종료");
    }
}
