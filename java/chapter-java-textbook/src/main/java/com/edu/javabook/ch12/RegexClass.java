package com.edu.javabook.ch12;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 12.10 정규 표현식 클래스
 *
 * 정규 표현식(Regular Expression)은 "문자열의 패턴"을 표현하는 규칙 문자열이다.
 * 이메일, 전화번호처럼 일정한 형식을 검사하거나 원하는 부분을 찾을 때 쓴다.
 *
 * 대표 기호 :
 *   \\d 숫자, \\w 단어문자, . 임의문자, * 0회이상, + 1회이상, ? 0또는1회
 *   {n} n회, [ ] 문자집합, ( ) 그룹, ^ 시작, $ 끝, | 또는
 *
 * 핵심 클래스 :
 *   - Pattern : 정규식을 컴파일한 객체 (Pattern.compile("..."))
 *   - Matcher : 대상 문자열에 패턴을 적용한 객체 (pattern.matcher(text))
 *
 * 주요 동작 :
 *   - matches() : 문자열 "전체"가 패턴과 일치하는가
 *   - find()    : 패턴과 일치하는 부분을 "찾아가며" 탐색
 *   - group()   : find/matches 로 찾은 부분(또는 그룹)을 꺼냄
 *   - replaceAll: 패턴에 맞는 부분을 모두 치환
 *   - split()   : 패턴을 기준으로 분리
 *
 * 이 소절에서는 위 동작들을 코드로 확인한다.
 */
public class RegexClass {

    public static void main(String[] args) {

        System.out.println("=== 12.10 정규 표현식 클래스 ===");

        // [1] matches : 전체 일치 검사 (이메일 형식)
        System.out.println("\n[1] matches (전체 일치 - 이메일 검증)");
        String emailPattern = "\\w+@\\w+\\.\\w+";
        System.out.println("  \"user@test.com\" : " + Pattern.matches(emailPattern, "user@test.com"));
        System.out.println("  \"bad-email\"     : " + Pattern.matches(emailPattern, "bad-email"));
        // String.matches 로도 가능 (내부적으로 동일)
        System.out.println("  \"a@b.c\".matches : " + "a@b.c".matches(emailPattern));

        // [2] Pattern / Matcher + find + group : 부분 탐색
        System.out.println("\n[2] find / group (부분 탐색 - 전화번호 추출)");
        String text = "연락처는 010-1234-5678 또는 02-9876-5432 입니다.";
        Pattern phone = Pattern.compile("(\\d{2,3})-(\\d{3,4})-(\\d{4})");
        Matcher m = phone.matcher(text);
        while (m.find()) {
            System.out.println("  찾음 : " + m.group()      // 전체 매칭
                    + "  (국번=" + m.group(1)              // 그룹 1
                    + ", 중간=" + m.group(2) + ")");        // 그룹 2
        }

        // [3] replaceAll : 패턴 치환 (전화번호 마스킹)
        System.out.println("\n[3] replaceAll (치환)");
        String masked = text.replaceAll("\\d{4}(?=$|[^\\d])", "****");
        System.out.println("  숫자 4자리 마스킹 → " + masked);
        String noSpace = "a b  c   d".replaceAll("\\s+", "_");
        System.out.println("  공백을 _ 로       → " + noSpace);

        // [4] split : 패턴 기준 분리
        System.out.println("\n[4] split (분리)");
        String data = "사과, 바나나,포도 ,  귤";
        String[] items = data.split("\\s*,\\s*");   // 콤마 앞뒤 공백 포함 분리
        System.out.println("  분리 결과 (" + items.length + "개):");
        for (String item : items) {
            System.out.println("    - [" + item + "]");
        }

        System.out.println("\n프로그램 정상 종료");
    }
}
