package com.edu.javabook.ch05;

/**
 * 5.5 문자열(String) 타입
 *
 * String 은 참조 타입이며, 문자열을 다루는 가장 기본 클래스다.
 *
 * [문자열 리터럴과 String Pool]
 *   - "abc" 같은 리터럴은 힙 안의 "문자열 상수 풀"에 보관되어 재사용된다.
 *   - 같은 리터럴은 풀에서 같은 객체를 공유한다.
 *
 * [new String("abc")]
 *   - 풀과 별개로 힙에 새 객체를 만든다.
 *
 * [intern()]
 *   - new 로 만든 문자열도 풀의 객체로 맞춰준다.
 *
 * String 은 불변(immutable)이라, 내용을 바꾸면 새 객체가 만들어진다.
 */
public class StringTypeRef {

    public static void main(String[] args) {

        System.out.println("=== 5.5 문자열(String) 타입 ===");

        // [1] 리터럴은 String Pool을 공유한다.
        System.out.println("\n[1] 리터럴은 풀 공유 (== true)");
        String a = "java";
        String b = "java";
        System.out.println("a = \"" + a + "\", b = \"" + b + "\"");
        System.out.println("a == b : " + (a == b) + "  (같은 풀 객체)");

        // [2] new String 은 별도 객체를 만든다.
        System.out.println("\n[2] new String 은 별도 객체 (== false)");
        String c = new String("java");
        System.out.println("a == c        : " + (a == c) + "  (new → 다른 객체)");
        System.out.println("a.equals(c)   : " + a.equals(c) + "  (내용은 같음)");

        // [3] intern() 으로 풀 객체로 맞춘다.
        System.out.println("\n[3] intern() 으로 풀 객체 확보");
        String d = c.intern();   // 풀에 있는 "java"를 반환
        System.out.println("a == c.intern() : " + (a == d) + "  (풀 객체로 통일됨)");

        // [4] String 은 불변 : 연산하면 새 객체가 생긴다.
        System.out.println("\n[4] 불변성 (내용 변경 시 새 객체)");
        String s = "Hello";
        String upper = s.concat(" World");  // s 자체는 안 바뀌고 새 문자열 생성
        System.out.println("원본 s   = " + s + "  (변하지 않음)");
        System.out.println("결과     = " + upper);
        System.out.println("길이     = " + upper.length());

        System.out.println("\n프로그램 정상 종료");
    }
}
