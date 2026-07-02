package com.edu.javabook.ch04;

/**
 * 4.8 continue 문
 *
 * continue 문은 현재 반복의 "남은 부분을 건너뛰고" 다음 반복으로 넘어간다.
 * break가 반복을 완전히 끝낸다면, continue는 이번 회차만 건너뛰고 반복은 계속된다.
 *
 * 기본 continue   : 가장 안쪽 반복문의 다음 회차로 이동
 *                   (for에서는 증감식으로, while에서는 조건 검사로 이동)
 * 레이블 continue : 지정한 바깥 반복문의 다음 회차로 이동
 */
public class ContinueStatement {

    public static void main(String[] args) {

        System.out.println("=== 4.8 continue 문 ===");

        // [1] 기본 continue : 짝수만 건너뛰고 홀수만 출력
        System.out.println("\n[1] 기본 continue (홀수만 출력)");
        for (int i = 1; i <= 10; i++) {
            if (i % 2 == 0) {
                continue;          // 짝수면 아래 출력을 건너뛰고 다음 i로
            }
            System.out.println("홀수 i = " + i);
        }

        // [2] while에서 continue : 조건 검사로 되돌아간다.
        //     주의 - 증감을 continue 앞에서 처리하지 않으면 무한 루프가 될 수 있다.
        System.out.println("\n[2] while + continue (3의 배수 건너뛰기)");
        int n = 0;
        while (n < 10) {
            n++;                    // 먼저 증가시켜 무한 루프 방지
            if (n % 3 == 0) {
                continue;          // 3의 배수면 출력 건너뜀
            }
            System.out.println("n = " + n);
        }

        // [3] 레이블 continue : 안쪽에서 바깥 반복의 다음 회차로 점프
        System.out.println("\n[3] 레이블 continue (바깥 반복 다음 회차로)");
        outer:
        for (int row = 1; row <= 3; row++) {
            for (int col = 1; col <= 3; col++) {
                if (col == 2) {
                    continue outer;   // 바깥 for(row)의 다음 회차로 → 이 row의 col=3은 건너뜀
                }
                System.out.println("row=" + row + ", col=" + col);
            }
        }

        System.out.println("\n프로그램 정상 종료");
    }
}
