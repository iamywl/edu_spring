package com.edu.javabook.ch04;

/**
 * 4.5 while 문
 *
 * while 문은 "조건이 참인 동안" 코드를 반복한다.
 * 반복 횟수가 미리 정해져 있지 않고, 어떤 조건이 만족될 때까지 돌릴 때 적합하다.
 *
 * 형식:
 *   while (조건) { 반복할 코드 }
 *     - 조건을 먼저 검사한다 → 처음부터 거짓이면 한 번도 실행되지 않는다.
 *     - 반복 안에서 조건을 언젠가 거짓으로 만들지 않으면 무한 루프가 되므로 주의.
 */
public class WhileStatement {

    public static void main(String[] args) {

        System.out.println("=== 4.5 while 문 ===");

        // [1] 기본 while : 조건이 참인 동안 반복
        System.out.println("\n[1] 기본 while (1~5 출력)");
        int i = 1;
        while (i <= 5) {
            System.out.println("i = " + i);
            i++;                    // 이 증가가 없으면 무한 루프! 조건을 거짓으로 만드는 역할
        }

        // [2] 처음부터 조건이 거짓이면 본문이 한 번도 실행되지 않는다.
        System.out.println("\n[2] 조건이 처음부터 거짓");
        int n = 10;
        while (n < 5) {
            System.out.println("이 문장은 실행되지 않는다.");
            n++;
        }
        System.out.println("n(" + n + ") < 5 가 거짓이라 본문 미실행");

        // [3] 누적 계산 : 1부터 10까지 합
        System.out.println("\n[3] 누적 합 (1~10)");
        int sum = 0;
        int k = 1;
        while (k <= 10) {
            sum += k;
            k++;
        }
        System.out.println("1~10 합 = " + sum);

        // [4] 조건 플래그로 제어 : 안전을 위해 반복 횟수 상한도 둔다.
        System.out.println("\n[4] 플래그로 반복 종료");
        boolean running = true;
        int step = 0;
        while (running) {
            step++;
            System.out.println("step = " + step);
            if (step >= 3) {
                running = false;    // 플래그를 false로 만들어 반복 종료
            }
        }

        System.out.println("\n프로그램 정상 종료");
    }
}
