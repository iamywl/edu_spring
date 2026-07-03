package com.edu.javabook.ch04;

/**
 * 4.1 코드 실행 흐름 제어
 *
 * 프로그램은 기본적으로 위에서 아래로 "순차(sequential)" 실행된다.
 * 하지만 상황에 따라 실행 순서를 바꿀 필요가 있는데, 이를 "제어 흐름(control flow)"이라 한다.
 *
 * 제어 흐름은 크게 세 가지로 나뉜다.
 *   1) 순차(sequence)   : 작성한 순서대로 한 문장씩 실행
 *   2) 조건(selection)  : 조건에 따라 실행할 코드를 고름   (if, switch)
 *   3) 반복(iteration)  : 같은 코드를 여러 번 실행         (for, while, do-while)
 */
public class ExecutionFlow {

    public static void main(String[] args) {

        System.out.println("=== 4.1 코드 실행 흐름 제어 ===");

        // [1] 순차 실행: 특별한 제어문이 없으면 위에서 아래로 순서대로 실행된다.
        System.out.println("\n[1] 순차 실행");
        System.out.println("첫 번째 문장");
        System.out.println("두 번째 문장");
        System.out.println("세 번째 문장");

        // [2] 조건 실행: 조건이 참일 때만 특정 코드를 실행한다.
        System.out.println("\n[2] 조건 실행 (개요)");
        int temperature = 30;
        if (temperature >= 28) {
            System.out.println("덥습니다. (조건이 참이라 이 문장 실행)");
        } else {
            System.out.println("시원합니다. (이 문장은 실행되지 않음)");
        }

        // [3] 반복 실행: 같은 작업을 정해진 횟수만큼 되풀이한다.
        System.out.println("\n[3] 반복 실행 (개요)");
        for (int i = 1; i <= 3; i++) {
            System.out.println("반복 " + i + "회차");
        }

        // [4] 흐름 개념 정리
        System.out.println("\n[4] 흐름 개념 정리");
        System.out.println("순차 → 위에서 아래로");
        System.out.println("조건 → 갈림길에서 하나의 길 선택 (if / switch)");
        System.out.println("반복 → 같은 길을 여러 번 (for / while / do-while)");

        System.out.println("\n프로그램 정상 종료");
    }
}
