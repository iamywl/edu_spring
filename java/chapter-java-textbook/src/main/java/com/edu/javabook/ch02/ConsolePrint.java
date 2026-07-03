package com.edu.javabook.ch02;

/**
 * 2.12 콘솔로 변수값 출력
 *
 * 콘솔 출력에는 세 가지 방법을 쓴다.
 *   System.out.println(x) : 출력 후 줄바꿈
 *   System.out.print(x)   : 출력만 (줄바꿈 없음)
 *   System.out.printf(...) : 형식(포맷)을 지정해 출력
 */
public class ConsolePrint {

    public static void main(String[] args) {

        System.out.println("=== 2.12 콘솔로 변수값 출력 ===");

        // [1] println : 출력하고 줄을 바꾼다
        System.out.println("\n[1] println (줄바꿈 O)");
        System.out.println("첫 줄");
        System.out.println("둘째 줄");

        // [2] print : 줄바꿈 없이 이어서 출력
        System.out.println("\n[2] print (줄바꿈 X)");
        System.out.print("A");
        System.out.print("B");
        System.out.print("C");
        System.out.println();   // 마지막에 줄바꿈 한 번
        System.out.println("(위 ABC는 한 줄에 이어짐)");

        // [3] + 로 변수와 문자열을 섞어 출력
        System.out.println("\n[3] 문자열 + 변수");
        String name = "홍길동";
        int age = 25;
        System.out.println("이름: " + name + ", 나이: " + age);

        // [4] printf : 형식 지정자로 정돈된 출력
        System.out.println("\n[4] printf 형식 출력");
        //   %d 정수, %f 실수, %s 문자열, %c 문자, %b 논리, %n 줄바꿈
        System.out.printf("정수 %d, 실수 %f, 문자열 %s%n", 10, 3.14, "자바");
        System.out.printf("문자 %c, 논리 %b%n", 'A', true);

        // [5] printf 폭·소수점 자리 지정
        System.out.println("\n[5] 폭 / 소수점 자리 맞추기");
        System.out.printf("[%5d]  (폭 5, 오른쪽 정렬)%n", 42);
        System.out.printf("[%-5d] (폭 5, 왼쪽 정렬)%n", 42);
        System.out.printf("[%.2f]  (소수점 2자리)%n", 3.14159);
        System.out.printf("[%08.2f] (폭8, 0채움, 소수2자리)%n", 3.14159);

        // [6] 천단위 콤마, 16진수 등
        System.out.println("\n[6] 기타 형식");
        System.out.printf("천단위 콤마: %,d%n", 1234567);
        System.out.printf("16진수    : %x%n", 255);

        // [왜?] println은 빠른 확인용, printf는 표·정렬처럼 형식이 중요할 때 쓴다.
        System.out.println("\n[왜?] 값 확인은 println, 정렬·자릿수 등 형식이 중요하면 printf.");

        System.out.println("\n프로그램 정상 종료");
    }
}
