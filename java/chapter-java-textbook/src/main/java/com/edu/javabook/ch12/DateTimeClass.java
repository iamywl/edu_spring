package com.edu.javabook.ch12;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;

/**
 * 12.8 날짜와 시간 클래스
 *
 * 자바 8부터 java.time 패키지가 도입되어 날짜/시간을 훨씬 안전하고 편하게 다룬다.
 * (예전 Date/Calendar 의 불편함을 개선한 "불변" 클래스들이다.)
 *
 * 핵심 클래스 :
 *   - LocalDate     : 날짜만 (년-월-일)
 *   - LocalTime     : 시간만 (시:분:초)
 *   - LocalDateTime : 날짜 + 시간
 *   - Period        : 두 "날짜" 사이의 간격 (년/월/일 단위)
 *   - Duration      : 두 "시간" 사이의 간격 (시/분/초 단위)
 *   - DateTimeFormatter : 날짜/시간을 원하는 문자열 형식으로 변환
 *
 * 이 소절에서는 고정된 날짜/시간 값을 사용해(비대화형, 재현 가능)
 * 생성, 계산, 간격(Period/Duration), 형식화를 확인한다.
 */
public class DateTimeClass {

    public static void main(String[] args) {

        System.out.println("=== 12.8 날짜와 시간 클래스 ===");

        // [1] LocalDate / LocalDateTime 생성
        System.out.println("\n[1] 날짜/시간 생성");
        LocalDate date = LocalDate.of(2026, 6, 30);
        LocalDateTime dateTime = LocalDateTime.of(2026, 6, 30, 14, 30, 0);
        System.out.println("  LocalDate     = " + date);
        System.out.println("  LocalDateTime = " + dateTime);
        System.out.println("  연/월/일       = " + date.getYear() + "/" +
                date.getMonthValue() + "/" + date.getDayOfMonth());
        System.out.println("  요일           = " + date.getDayOfWeek());

        // [2] 날짜 계산 (불변 → 새 객체 반환)
        System.out.println("\n[2] 날짜 계산 (plus/minus)");
        LocalDate nextWeek = date.plusDays(7);
        LocalDate lastMonth = date.minusMonths(1);
        System.out.println("  기준일          = " + date);
        System.out.println("  plusDays(7)     = " + nextWeek);
        System.out.println("  minusMonths(1)  = " + lastMonth);

        // [3] Period : 두 날짜 사이 간격 (년/월/일)
        System.out.println("\n[3] Period (날짜 간격)");
        LocalDate start = LocalDate.of(2026, 1, 1);
        LocalDate end = LocalDate.of(2026, 6, 30);
        Period period = Period.between(start, end);
        System.out.println("  " + start + " ~ " + end);
        System.out.println("  → " + period.getMonths() + "개월 " + period.getDays() + "일");

        // [4] Duration : 두 시간 사이 간격 (시/분/초)
        System.out.println("\n[4] Duration (시간 간격)");
        LocalTime t1 = LocalTime.of(9, 0, 0);
        LocalTime t2 = LocalTime.of(17, 30, 0);
        Duration duration = Duration.between(t1, t2);
        System.out.println("  " + t1 + " ~ " + t2);
        System.out.println("  → 총 " + duration.toMinutes() + "분 (" +
                duration.toHours() + "시간 " + (duration.toMinutes() % 60) + "분)");

        // [5] DateTimeFormatter : 원하는 형식으로 변환
        System.out.println("\n[5] DateTimeFormatter (형식화)");
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH:mm");
        System.out.println("  형식화 결과 = " + dateTime.format(fmt));
        // 문자열 → 날짜 파싱
        LocalDate parsed = LocalDate.parse("2026-12-25");
        System.out.println("  parse(\"2026-12-25\") = " + parsed);

        System.out.println("\n프로그램 정상 종료");
    }
}
