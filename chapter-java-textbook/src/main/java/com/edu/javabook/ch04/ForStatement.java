package com.edu.javabook.ch04;

/**
 * 4.4 for 문
 *
 * for 문은 "반복 횟수가 정해져 있을 때" 가장 흔히 쓰는 반복문이다.
 *
 * 형식:
 *   for (초기화; 조건; 증감) { 반복할 코드 }
 *     - 초기화 : 처음 한 번만 실행 (보통 카운터 변수 선언)
 *     - 조건   : 매 반복 전에 검사, 참이면 계속
 *     - 증감   : 반복 본문 실행 후 매번 실행
 *
 * 향상된 for(for-each) : 배열/컬렉션의 요소를 처음부터 끝까지 순회할 때 편리하다.
 */
public class ForStatement {

    public static void main(String[] args) {

        System.out.println("=== 4.4 for 문 ===");

        // [1] 기본 for : 1부터 5까지 출력
        System.out.println("\n[1] 기본 for");
        for (int i = 1; i <= 5; i++) {
            System.out.println("i = " + i);
        }

        // [2] 다중 변수 for : 초기화/증감에 콤마로 변수 여러 개
        System.out.println("\n[2] 다중 변수 for");
        for (int a = 0, b = 10; a < b; a++, b--) {
            System.out.println("a=" + a + ", b=" + b);
        }

        // [3] 무한 for : 조건을 비우면 무한 반복이 된다. 반드시 break로 탈출해야 한다.
        //     (여기서는 카운터로 3회만 돌고 안전하게 break)
        System.out.println("\n[3] 무한 for + break (안전 탈출)");
        int count = 0;
        for (;;) {                 // for(;;) 는 조건이 항상 참 → 무한 반복
            count++;
            System.out.println("count = " + count);
            if (count >= 3) {
                break;             // 3회 후 반드시 탈출
            }
        }

        // [4] 향상된 for (for-each) : 배열 요소를 순서대로 꺼낸다.
        System.out.println("\n[4] 향상된 for (for-each)");
        String[] fruits = {"사과", "바나나", "체리"};
        for (String fruit : fruits) {
            System.out.println("과일: " + fruit);
        }

        // [5] 중첩 for : 구구단 2단 예시
        System.out.println("\n[5] 중첩 for (2단)");
        for (int dan = 2; dan <= 2; dan++) {
            for (int j = 1; j <= 9; j++) {
                System.out.println(dan + " x " + j + " = " + (dan * j));
            }
        }

        System.out.println("\n프로그램 정상 종료");
    }
}
