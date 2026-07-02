package com.edu.javabook.ch02;

/**
 * 2.6 문자열 타입
 *
 * String 은 여러 문자를 이어붙인 '문자열'을 저장한다.
 * 기본 타입(int, char 등)과 달리 String 은 '클래스(참조 타입)'이다.
 * 문자열 리터럴은 큰따옴표("")로 감싼다.
 * String 은 불변(immutable): 한 번 만든 문자열의 내용은 바꿀 수 없다.
 */
public class StringType {

    public static void main(String[] args) {

        System.out.println("=== 2.6 문자열 타입 ===");

        // [1] 문자열 선언
        System.out.println("\n[1] 문자열 리터럴");
        String name = "홍길동";
        String greeting = "안녕하세요";
        System.out.println("name = " + name);
        System.out.println("greeting = " + greeting);

        // [2] + 연산으로 문자열 연결(concatenation)
        System.out.println("\n[2] + 로 문자열 연결");
        String message = greeting + ", " + name + "님!";
        System.out.println(message);

        // [3] 문자열 + 숫자 → 숫자가 문자열로 바뀌어 이어붙는다
        System.out.println("\n[3] 문자열 + 숫자");
        int age = 25;
        System.out.println("나이: " + age + "세");
        System.out.println("주의: \"결과=\" + 1 + 2 → " + ("결과=" + 1 + 2) + " (왼→오, 문자열 연결)");
        System.out.println("주의: 1 + 2 + \"=결과\" → " + (1 + 2 + "=결과") + " (숫자끼리 먼저 더함)");

        // [4] 자주 쓰는 String 메서드
        System.out.println("\n[4] 주요 메서드");
        String s = "Hello, Java World";
        System.out.println("원본        : " + s);
        System.out.println("length()    : " + s.length());          // 길이
        System.out.println("charAt(1)   : " + s.charAt(1));          // 특정 위치 문자
        System.out.println("substring(7): " + s.substring(7));       // 부분 문자열
        System.out.println("indexOf(J)  : " + s.indexOf("Java"));    // 위치 찾기
        System.out.println("toUpperCase : " + s.toUpperCase());      // 대문자
        System.out.println("toLowerCase : " + s.toLowerCase());      // 소문자
        System.out.println("replace     : " + s.replace("Java", "Spring")); // 치환
        System.out.println("contains    : " + s.contains("World"));  // 포함 여부
        System.out.println("  trim  '  hi  ' → [" + "  hi  ".trim() + "]"); // 앞뒤 공백 제거

        // [5] 문자열 비교는 == 가 아니라 equals() 로 한다
        System.out.println("\n[5] 문자열 비교");
        String a = new String("java");
        String b = new String("java");
        System.out.println("a == b        → " + (a == b) + "  (주소 비교, 다른 객체)");
        System.out.println("a.equals(b)   → " + a.equals(b) + "  (내용 비교, 올바른 방법)");

        // [왜?] String은 불변이라 안전하게 공유되지만, 반복 연결이 많으면 StringBuilder가 유리하다.
        System.out.println("\n[왜?] 문자열 내용 비교는 반드시 equals()! 대량 연결은 StringBuilder 사용.");

        System.out.println("\n프로그램 정상 종료");
    }
}
