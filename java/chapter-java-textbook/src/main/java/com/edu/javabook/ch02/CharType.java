package com.edu.javabook.ch02;

/**
 * 2.3 문자 타입
 *
 * char 는 '하나의 문자'를 저장하는 2바이트 타입이다.
 * 자바는 문자를 유니코드(Unicode) 정수 코드값으로 저장한다.
 * 따라서 char 는 사실상 "정수처럼도 다룰 수 있는" 특별한 타입이다.
 * 문자 리터럴은 작은따옴표('')로 감싼다.  (큰따옴표 ""는 문자열 String)
 */
public class CharType {

    public static void main(String[] args) {

        System.out.println("=== 2.3 문자 타입 ===");

        // [1] 문자 리터럴 (작은따옴표)
        System.out.println("\n[1] 기본 문자 저장");
        char c1 = 'A';
        char c2 = '가';
        char c3 = '9';   // 문자 '9' 이지 숫자 9가 아님에 주의
        System.out.println("c1 = " + c1);
        System.out.println("c2 = " + c2);
        System.out.println("c3 = " + c3);

        // [2] 유니코드 코드값으로 저장 (문자는 내부적으로 정수다)
        System.out.println("\n[2] 유니코드 정수 코드값으로 대입");
        char c4 = 65;      // 65 → 'A'
        char c5 = 44032;   // 44032 → '가'
        System.out.println("char c4 = 65    → " + c4);
        System.out.println("char c5 = 44032 → " + c5);

        // [3] 유니코드 이스케이프 표기 (역슬래시 u + 16진수 4자리)
        System.out.println("\n[3] \\u 유니코드 이스케이프");
        char c6 = 'A';   // 0x41 = 65 = 'A'
        char c7 = '가';   // 0xAC00 = 44032 = '가'
        System.out.println("'\\u0041' → " + c6);
        System.out.println("'\\uAC00' → " + c7);

        // [4] 문자 ↔ 정수 변환
        System.out.println("\n[4] 문자와 정수 사이 변환");
        char ch = 'A';
        int code = ch;              // char → int (자동): 코드값 65
        System.out.println("'A'의 코드값 = " + code);
        char next = (char)(ch + 1); // 정수 연산 후 다시 char로 (강제 캐스팅)
        System.out.println("'A' + 1 → " + next + " (문자 A 다음 문자)");

        // [5] 활용: 코드값 차이를 이용한 계산
        System.out.println("\n[5] 코드값 산술 활용");
        char digit = '7';
        int number = digit - '0';   // 문자 '7' - 문자 '0' = 정수 7
        System.out.println("문자 '7' 을 실제 숫자 7로: " + number);

        // [6] 자주 쓰는 이스케이프 문자
        System.out.println("\n[6] 이스케이프 문자");
        System.out.println("탭 →[\t]끝, 줄바꿈은 \\n, 따옴표는 \\', 역슬래시는 \\\\ ");

        // [왜?] 문자를 숫자로 저장하기에 정렬·비교·아스키 계산이 가능하다.
        System.out.println("\n[왜?] 문자는 코드값(숫자)이라 비교/정렬/암호화 같은 연산이 가능하다.");

        System.out.println("\n프로그램 정상 종료");
    }
}
