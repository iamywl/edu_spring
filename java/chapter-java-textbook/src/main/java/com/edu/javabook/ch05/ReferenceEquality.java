package com.edu.javabook.ch05;

/**
 * 5.3 참조 타입의 ==, != 연산
 *
 * 기본 타입에서 ==, != 는 "값"을 비교하지만,
 * 참조 타입에서 ==, != 는 "번지(주소)"를 비교한다.
 *
 * 동일성(identity)  : == → 두 변수가 '같은 객체'를 가리키는가?
 * 동등성(equality)  : equals() → 두 객체의 '내용'이 같은가?
 *
 * 그래서 문자열 내용 비교에는 반드시 equals() 를 써야 한다.
 */
public class ReferenceEquality {

    public static void main(String[] args) {

        System.out.println("=== 5.3 참조 타입의 ==, != 연산 ===");

        // [1] == 는 번지(같은 객체인지)를 비교한다.
        System.out.println("\n[1] == 는 '같은 객체인가'를 비교");
        int[] a = { 1, 2, 3 };
        int[] b = { 1, 2, 3 };   // 내용은 같지만 별개의 객체
        int[] c = a;             // a와 같은 객체를 가리킴
        System.out.println("a == b : " + (a == b) + "  (내용 같아도 다른 객체 → false)");
        System.out.println("a == c : " + (a == c) + "  (같은 객체를 가리킴 → true)");

        // [2] 문자열 == 의 함정 : 리터럴은 풀을 공유, new 는 새 객체
        System.out.println("\n[2] String의 == vs equals");
        String s1 = "hello";          // 문자열 풀의 객체를 가리킴
        String s2 = "hello";          // 같은 풀 객체를 가리킴
        String s3 = new String("hello"); // new → 힙에 별도 객체 생성
        System.out.println("s1 == s2       : " + (s1 == s2) + "  (풀 공유 → true)");
        System.out.println("s1 == s3       : " + (s1 == s3) + "  (new 별도 객체 → false)");

        // [3] 내용 비교는 equals() 로 해야 한다.
        System.out.println("\n[3] 내용 비교는 equals() 사용");
        System.out.println("s1.equals(s3)  : " + s1.equals(s3) + "  (내용 같음 → true)");

        // [4] != 도 마찬가지로 번지 비교
        System.out.println("\n[4] != 는 '다른 객체인가'를 비교");
        System.out.println("s1 != s3       : " + (s1 != s3) + "  (다른 객체 → true)");

        System.out.println("\n프로그램 정상 종료");
    }
}
