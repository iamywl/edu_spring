package com.edu.javabook.ch04;

/**
 * 4.6 do-while 문
 *
 * do-while 문은 "먼저 한 번 실행한 뒤" 조건을 검사한다.
 * 따라서 조건이 처음부터 거짓이어도 "최소 1회"는 반드시 실행된다는 점이 while과 다르다.
 *
 * 형식:
 *   do { 반복할 코드 } while (조건);   // 끝에 세미콜론(;) 필수
 *
 * while  : 조건 검사 → 실행   (0번 이상 실행)
 * do-while: 실행 → 조건 검사  (1번 이상 실행)
 */
public class DoWhileStatement {

    public static void main(String[] args) {

        System.out.println("=== 4.6 do-while 문 ===");

        // [1] 기본 do-while : 일단 실행하고 조건 검사
        System.out.println("\n[1] 기본 do-while (1~5 출력)");
        int i = 1;
        do {
            System.out.println("i = " + i);
            i++;
        } while (i <= 5);

        // [2] 핵심 차이 : 조건이 처음부터 거짓이어도 본문이 "한 번"은 실행된다.
        System.out.println("\n[2] while vs do-while 차이");

        System.out.println("- while (조건 거짓): 본문 0회 실행");
        int a = 10;
        while (a < 5) {
            System.out.println("  (while) 실행됨");   // 실행되지 않음
            a++;
        }

        System.out.println("- do-while (조건 거짓): 본문 1회 실행");
        int b = 10;
        do {
            System.out.println("  (do-while) 최소 1회 실행됨 b=" + b);   // 반드시 1번 출력
            b++;
        } while (b < 5);

        // [3] 활용 예 : 최소 1회 처리 후 반복 여부 판단 (안전 상한 포함)
        System.out.println("\n[3] 최소 1회 처리 후 조건 검사");
        int count = 0;
        do {
            count++;
            System.out.println("처리 " + count + "회");
        } while (count < 3);

        System.out.println("\n프로그램 정상 종료");
    }
}
