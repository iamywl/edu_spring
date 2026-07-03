package com.edu.javabook.ch05;

/**
 * 5.1 데이터 타입 분류
 *
 * 자바의 데이터 타입은 크게 두 종류로 나뉜다.
 *
 * [기본 타입(primitive type)]
 *   - 정수(byte, short, int, long), 실수(float, double), 문자(char), 논리(boolean)
 *   - 변수에 "값 자체"가 저장된다.
 *
 * [참조 타입(reference type)]
 *   - 배열, 열거(enum), 클래스, 인터페이스
 *   - 변수에 "객체의 번지(주소)"가 저장된다. (값이 아니라 위치를 가리킨다)
 *
 * 핵심 차이: 기본형은 값을 직접 담고, 참조형은 값이 있는 곳을 가리킨다.
 */
public class DataTypeCategory {

    public static void main(String[] args) {

        System.out.println("=== 5.1 데이터 타입 분류 ===");

        // [1] 기본 타입 : 변수 안에 값 자체가 들어간다.
        System.out.println("\n[1] 기본 타입 (값 자체 저장)");
        int age = 25;          // 변수 age 안에 정수 25가 그대로 저장
        double pi = 3.14;      // 변수 pi 안에 실수 3.14가 그대로 저장
        char grade = 'A';      // 변수 grade 안에 문자 'A'가 그대로 저장
        boolean pass = true;   // 변수 pass 안에 참/거짓이 그대로 저장
        System.out.println("age(int)     = " + age);
        System.out.println("pi(double)   = " + pi);
        System.out.println("grade(char)  = " + grade);
        System.out.println("pass(boolean)= " + pass);

        // [2] 참조 타입 : 변수에는 "객체가 있는 번지"가 들어간다.
        System.out.println("\n[2] 참조 타입 (객체의 번지 저장)");
        int[] scores = { 90, 80, 70 };   // 배열 객체는 힙에 생성, scores는 그 번지를 가리킴
        String name = "홍길동";           // 문자열 객체를 가리킴
        System.out.println("scores 변수는 배열 객체를 '가리킨다' → " + java.util.Arrays.toString(scores));
        System.out.println("name 변수는 문자열 객체를 '가리킨다'  → " + name);

        // [3] 값 저장 방식 차이 시연
        System.out.println("\n[3] 저장 방식 차이 (복사할 때 나타난다)");
        int a = 10;
        int b = a;   // 기본형 : 값 10이 '복사'되어 b에 저장 (서로 독립)
        b = 99;
        System.out.println("기본형 복사 후 a=" + a + ", b=" + b + " (a는 그대로 → 값 복사)");

        int[] arr1 = { 1, 2, 3 };
        int[] arr2 = arr1;   // 참조형 : 번지가 '복사'되어 둘이 같은 객체를 가리킴
        arr2[0] = 99;
        System.out.println("참조형 복사 후 arr1[0]=" + arr1[0] + " (같이 바뀜 → 번지 복사)");

        System.out.println("\n프로그램 정상 종료");
    }
}
