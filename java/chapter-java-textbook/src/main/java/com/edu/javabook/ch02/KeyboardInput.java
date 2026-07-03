package com.edu.javabook.ch02;

import java.util.Scanner;

/**
 * 2.13 키보드 입력 데이터를 변수에 저장
 *
 * 사용자가 키보드로 입력한 값은 Scanner 클래스로 읽어 변수에 저장한다.
 *
 * ※ 이 예제는 '비대화형'으로 동작하도록, 실제 키보드(System.in) 대신
 *   문자열을 입력원으로 갖는 Scanner 를 사용해 시연한다.
 *   (그래야 자동 실행 시 입력 대기로 멈추지 않는다.)
 *
 *   실제 키보드 입력을 받으려면 아래처럼 System.in 을 쓴다:
 *
 *       Scanner sc = new Scanner(System.in);
 *       System.out.print("나이: ");
 *       int age = sc.nextInt();   // ← 여기서 사용자의 입력을 기다린다(블로킹)
 *       sc.close();
 */
public class KeyboardInput {

    public static void main(String[] args) {

        System.out.println("=== 2.13 키보드 입력 데이터를 변수에 저장 ===");

        // [1] 비대화형 시연: 실제 키보드 대신 미리 준비한 문자열을 Scanner에 넣는다.
        //     실제로는 new Scanner(System.in) 이라고만 바꾸면 키보드 입력이 된다.
        System.out.println("\n[1] Scanner 준비 (여기선 문자열로 시연)");
        String simulatedInput = "42 3.14 홍길동";
        Scanner sc = new Scanner(simulatedInput);
        System.out.println("입력원(가짜 키보드) : \"" + simulatedInput + "\"");

        // [2] 타입별 읽기 메서드
        //     nextInt()    : 정수 하나
        //     nextDouble() : 실수 하나
        //     next()       : 공백 전까지 문자열 하나
        //     nextLine()   : 줄 전체
        System.out.println("\n[2] 타입별로 읽어 변수에 저장");
        int    age    = sc.nextInt();
        double height = sc.nextDouble();
        String name   = sc.next();
        System.out.println("읽은 정수(age)    = " + age);
        System.out.println("읽은 실수(height) = " + height);
        System.out.println("읽은 문자열(name) = " + name);

        // [3] 읽어온 값으로 계산도 그대로 가능
        System.out.println("\n[3] 입력값 활용");
        System.out.println(name + "님은 " + age + "세, 키 " + height + "cm 입니다.");
        System.out.println("10년 후 나이: " + (age + 10) + "세");

        // [4] Scanner 는 다 쓰면 close() 로 닫아 자원을 반환한다.
        sc.close();
        System.out.println("\n[4] Scanner close() 로 자원 반환 완료");

        // [실제 키보드 입력 코드] — 자동 실행에서 멈추지 않도록 주석 처리해 둠.
        //   Scanner keyboard = new Scanner(System.in);
        //   System.out.print("이름을 입력하세요: ");
        //   String input = keyboard.nextLine();  // 사용자가 엔터 칠 때까지 대기
        //   System.out.println("입력한 이름: " + input);
        //   keyboard.close();

        // [왜?] 입력 로직을 System.in 대신 문자열 Scanner로 두면 테스트·시연을 자동화할 수 있다.
        System.out.println("\n[왜?] Scanner의 입력원만 바꾸면(System.in↔문자열) 대화형/자동 시연을 오갈 수 있다.");

        System.out.println("\n프로그램 정상 종료");
    }
}
