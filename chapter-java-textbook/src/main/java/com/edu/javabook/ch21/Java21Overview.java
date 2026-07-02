package com.edu.javabook.ch21;

/**
 * 21.1 강화된 내용 개요
 *
 * [Java 21이란]
 * - 2023년 9월에 릴리스된 LTS(Long-Term Support) 버전이다.
 *   Java 17 이후의 LTS로서 장기간 지원을 받는 안정 버전이다.
 * - Java 17 ~ 21 사이에 프리뷰(preview)로 다듬어진 여러 기능이
 *   Java 21에서 정식(standard)으로 확정되었다.
 *
 * [Java 21에서 정식화된 주요 언어/라이브러리 변화]
 * - switch 패턴 매칭(Pattern Matching for switch): 타입 패턴, when 가드, null 처리.
 * - 레코드 패턴(Record Pattern): 레코드를 구성요소로 분해(deconstruct)한다.
 * - 가상 스레드(Virtual Thread): 경량 스레드로 대규모 동시성을 저렴하게 처리한다.
 * - 순차 컬렉션(Sequenced Collections): 순서가 있는 컬렉션에 통일된 API를 제공한다.
 * - 기본 문자셋(Default Charset): Java 18부터 UTF-8로 통일되어 Java 21에도 적용된다.
 *
 * 이 소절에서는 이후 소절에서 다룰 변화들을 목록으로 요약한다.
 */
public class Java21Overview {

    public static void main(String[] args) {

        System.out.println("=== 21.1 강화된 내용 개요 ===");

        // [1] Java 21의 위치
        System.out.println("\n[1] Java 21의 위치");
        System.out.println("  릴리스: 2023년 9월");
        System.out.println("  종류  : LTS(장기 지원) 버전");
        System.out.println("  실행 JDK: " + System.getProperty("java.version"));

        // [2] 정식화된 주요 언어 기능
        System.out.println("\n[2] 정식화된 주요 언어 기능");
        String[][] langFeatures = {
            {"switch의 null 처리",   "case null / null, default 지원"},
            {"switch 패턴 매칭",     "타입 패턴 + when 가드"},
            {"레코드 패턴",          "case Point(int x, int y) 분해"}
        };
        for (String[] f : langFeatures) {
            System.out.printf("  - %-18s : %s%n", f[0], f[1]);
        }

        // [3] 정식화된 주요 라이브러리/런타임 기능
        System.out.println("\n[3] 정식화된 주요 라이브러리/런타임 기능");
        String[][] libFeatures = {
            {"가상 스레드",     "경량 스레드로 대규모 동시성"},
            {"순차 컬렉션",     "getFirst/getLast/reversed 통일 API"},
            {"기본 문자셋",     "UTF-8로 통일(Java 18~)"}
        };
        for (String[] f : libFeatures) {
            System.out.printf("  - %-12s : %s%n", f[0], f[1]);
        }

        // [4] 이 챕터의 소절 지도
        System.out.println("\n[4] 이 챕터의 소절 지도");
        System.out.println("  21.2 지역 변수 타입 추론(var)");
        System.out.println("  21.3 switch의 null 처리");
        System.out.println("  21.4 switch 패턴 매칭");
        System.out.println("  21.5 레코드 패턴");
        System.out.println("  21.6 가상 스레드");
        System.out.println("  21.7 순차 컬렉션");
        System.out.println("  21.8 기본 문자셋 변경");

        System.out.println("\n[정리]");
        System.out.println("  Java 21은 LTS로서 그동안 프리뷰였던 패턴 매칭/레코드 패턴/가상 스레드를");
        System.out.println("  정식 기능으로 확정한 중요한 버전이다.");
    }
}
