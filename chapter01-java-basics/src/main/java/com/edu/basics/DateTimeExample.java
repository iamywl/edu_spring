package com.edu.basics;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;

/**
 * Chapter 01 - 날짜와 시간 API (java.time)
 *
 * Java 8부터 도입된 java.time 패키지는 불변(immutable)이며 스레드 안전한
 * 날짜/시간 API를 제공합니다. (구식 Date, Calendar를 대체)
 *
 * ★ Spring 엔티티에서 생성일시/수정일시 필드는 보통 LocalDateTime을 사용합니다.
 *   예) @Column private LocalDateTime createdAt;
 *   따라서 이 API에 익숙해지는 것이 실무에서 매우 중요합니다.
 */
public class DateTimeExample {

    public static void main(String[] args) {
        System.out.println("====================================");
        System.out.println(" Chapter 01: 날짜와 시간 API (java.time)");
        System.out.println("====================================\n");

        localDateAndTime();
        plusMinusOperations();
        compareDates();
        durationAndPeriod();
        formattingAndParsing();
    }

    // ──────────────────────────────────────────────
    // 1. LocalDate / LocalTime / LocalDateTime
    // ──────────────────────────────────────────────
    static void localDateAndTime() {
        System.out.println("── 1. LocalDate / LocalTime / LocalDateTime ──");

        // LocalDate: 날짜만 (년-월-일)
        LocalDate today = LocalDate.now();
        LocalDate specificDate = LocalDate.of(2026, 6, 30);
        System.out.println("  오늘 날짜 (now)      : " + today);
        System.out.println("  지정 날짜 (of)       : " + specificDate);
        System.out.println("    연도   : " + specificDate.getYear());
        System.out.println("    월     : " + specificDate.getMonthValue());
        System.out.println("    일     : " + specificDate.getDayOfMonth());
        System.out.println("    요일   : " + specificDate.getDayOfWeek());

        // LocalTime: 시간만 (시:분:초)
        LocalTime time = LocalTime.of(14, 30, 0);
        System.out.println("  지정 시간 (of)       : " + time);

        // LocalDateTime: 날짜 + 시간 (Spring 엔티티에서 가장 자주 사용)
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime meeting = LocalDateTime.of(2026, 6, 30, 14, 30, 0);
        System.out.println("  현재 일시 (now)      : " + now);
        System.out.println("  지정 일시 (of)       : " + meeting);
        System.out.println();
    }

    // ──────────────────────────────────────────────
    // 2. 날짜 더하기/빼기 (plus/minus)
    // ──────────────────────────────────────────────
    static void plusMinusOperations() {
        System.out.println("── 2. 날짜 더하기/빼기 (plus/minus) ──");

        LocalDate base = LocalDate.of(2026, 6, 30);
        System.out.println("  기준 날짜             : " + base);

        // java.time 객체는 불변이므로 plus/minus는 "새 객체"를 반환합니다.
        // 원본은 절대 변하지 않습니다.
        System.out.println("  plusDays(7)           : " + base.plusDays(7));
        System.out.println("  minusDays(10)         : " + base.minusDays(10));
        System.out.println("  plusMonths(2)         : " + base.plusMonths(2));
        System.out.println("  plusYears(1)          : " + base.plusYears(1));
        System.out.println("  plusWeeks(2)          : " + base.plusWeeks(2));

        // 원본이 그대로임을 확인
        System.out.println("  (원본은 불변)         : " + base);

        // LocalDateTime도 동일하게 동작
        LocalDateTime dt = LocalDateTime.of(2026, 6, 30, 9, 0);
        System.out.println("  dt.plusHours(3)       : " + dt.plusHours(3));
        System.out.println("  dt.minusMinutes(30)   : " + dt.minusMinutes(30));
        System.out.println();
    }

    // ──────────────────────────────────────────────
    // 3. 날짜 비교 (isBefore / isAfter / isEqual)
    // ──────────────────────────────────────────────
    static void compareDates() {
        System.out.println("── 3. 날짜 비교 (isBefore / isAfter) ──");

        LocalDate start = LocalDate.of(2026, 1, 1);
        LocalDate end = LocalDate.of(2026, 12, 31);

        System.out.println("  시작일: " + start + ", 종료일: " + end);
        System.out.println("  start.isBefore(end)   : " + start.isBefore(end));  // true
        System.out.println("  start.isAfter(end)    : " + start.isAfter(end));   // false
        System.out.println("  start.isEqual(start)  : " + start.isEqual(start)); // true

        // 실전 사례: 마감일이 지났는지 검사
        LocalDate deadline = LocalDate.of(2026, 6, 1);
        LocalDate todayLike = LocalDate.of(2026, 6, 30);
        boolean overdue = todayLike.isAfter(deadline);
        System.out.println("  마감일(" + deadline + ") 초과 여부: " + overdue);
        System.out.println();
    }

    // ──────────────────────────────────────────────
    // 4. Duration(시간 간격) / Period(날짜 간격)
    // ──────────────────────────────────────────────
    static void durationAndPeriod() {
        System.out.println("── 4. Duration / Period ──");

        // Period: 날짜 기반 간격 (년, 월, 일)
        LocalDate birth = LocalDate.of(2000, 3, 15);
        LocalDate now = LocalDate.of(2026, 6, 30);
        Period age = Period.between(birth, now);
        System.out.println("  [Period - 날짜 간격]");
        System.out.println("  " + birth + " ~ " + now);
        System.out.println("  → " + age.getYears() + "년 "
                + age.getMonths() + "개월 " + age.getDays() + "일");

        // Duration: 시간 기반 간격 (시, 분, 초)
        LocalDateTime checkIn = LocalDateTime.of(2026, 6, 30, 9, 0);
        LocalDateTime checkOut = LocalDateTime.of(2026, 6, 30, 18, 30);
        Duration worked = Duration.between(checkIn, checkOut);
        System.out.println("\n  [Duration - 시간 간격]");
        System.out.println("  출근 " + checkIn.toLocalTime() + " ~ 퇴근 " + checkOut.toLocalTime());
        System.out.println("  → 총 " + worked.toHours() + "시간 "
                + (worked.toMinutes() % 60) + "분 근무");
        System.out.println("  toMinutes(): " + worked.toMinutes() + "분");
        System.out.println();
    }

    // ──────────────────────────────────────────────
    // 5. 포맷팅과 파싱 (DateTimeFormatter)
    // ──────────────────────────────────────────────
    static void formattingAndParsing() {
        System.out.println("── 5. 포맷팅과 파싱 (DateTimeFormatter) ──");

        LocalDateTime now = LocalDateTime.of(2026, 6, 30, 14, 30, 45);

        // 포맷팅(format): 날짜/시간 객체 → 원하는 형식의 문자열
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        DateTimeFormatter korFormatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시 mm분");

        System.out.println("  [포맷팅: 객체 → 문자열]");
        System.out.println("  yyyy-MM-dd HH:mm:ss : " + now.format(formatter));
        System.out.println("  한국어 형식         : " + now.format(korFormatter));

        // 파싱(parse): 문자열 → 날짜/시간 객체
        String input = "2026-12-25 10:00:00";
        LocalDateTime parsed = LocalDateTime.parse(input, formatter);
        System.out.println("\n  [파싱: 문자열 → 객체]");
        System.out.println("  입력 문자열: \"" + input + "\"");
        System.out.println("  파싱 결과  : " + parsed);
        System.out.println("  (요일: " + parsed.getDayOfWeek() + ")");

        // 잘못된 형식은 예외(DateTimeParseException)가 발생합니다.
        try {
            LocalDateTime.parse("2026/12/25", formatter);
        } catch (Exception e) {
            System.out.println("\n  형식이 맞지 않으면 파싱 예외 발생: "
                    + e.getClass().getSimpleName());
        }
        System.out.println();
    }
}
