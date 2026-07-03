package com.edu.javabook.ch04;

/**
 * 4.7 break 문
 *
 * break 문은 반복문(또는 switch)을 "즉시 빠져나온다".
 * 조건을 만족하는 순간 더 이상 반복할 필요가 없을 때 사용한다.
 *
 * 기본 break     : 자신을 감싸는 "가장 안쪽" 반복문 하나만 탈출한다.
 * 레이블 break   : 바깥 반복문에 이름(레이블)을 붙여, 중첩 반복을 한 번에 탈출한다.
 *                  형식:  이름: for(...) { for(...) { break 이름; } }
 */
public class BreakStatement {

    public static void main(String[] args) {

        System.out.println("=== 4.7 break 문 ===");

        // [1] 기본 break : 특정 값을 찾으면 반복 종료
        System.out.println("\n[1] 기본 break (5를 찾으면 중단)");
        for (int i = 1; i <= 10; i++) {
            if (i == 5) {
                System.out.println(i + " 발견 → 반복 중단");
                break;             // for 문을 즉시 탈출 (6~10은 실행 안 됨)
            }
            System.out.println("i = " + i);
        }

        // [2] 중첩 반복에서 기본 break : 안쪽 반복만 빠져나온다.
        System.out.println("\n[2] 중첩 반복에서 기본 break (안쪽만 탈출)");
        for (int row = 1; row <= 3; row++) {
            for (int col = 1; col <= 3; col++) {
                if (col == 2) {
                    break;         // 안쪽 for만 탈출 → 바깥 for는 계속
                }
                System.out.println("row=" + row + ", col=" + col);
            }
        }

        // [3] 레이블 break : 바깥 반복까지 한 번에 탈출
        System.out.println("\n[3] 레이블 break (바깥까지 한 번에 탈출)");
        outer:
        for (int row = 1; row <= 3; row++) {
            for (int col = 1; col <= 3; col++) {
                if (row == 2 && col == 2) {
                    System.out.println("row=2,col=2 도달 → 바깥 반복까지 탈출");
                    break outer;   // 'outer' 레이블이 붙은 바깥 for 전체를 탈출
                }
                System.out.println("row=" + row + ", col=" + col);
            }
        }

        System.out.println("\n프로그램 정상 종료");
    }
}
