package com.edu.javabook.ch04;

/**
 * 4.2 if 문
 *
 * if 문은 "조건이 참(true)"일 때만 코드 블록을 실행한다.
 *
 * 형식:
 *   if (조건) { ... }                     // 조건이 참이면 실행
 *   if (조건) { ... } else { ... }        // 참이면 앞, 거짓이면 else
 *   if (조건1) { } else if (조건2) { } ... // 여러 갈림길
 *   if 안에 다시 if (중첩)
 *
 * 조건식의 결과는 반드시 boolean(true/false)이어야 한다.
 */
public class IfStatement {

    public static void main(String[] args) {

        System.out.println("=== 4.2 if 문 ===");

        // [1] 단순 if : 조건이 참일 때만 실행
        System.out.println("\n[1] 단순 if");
        int score = 85;
        if (score >= 60) {
            System.out.println("합격입니다. (score=" + score + ")");
        }

        // [2] if-else : 참/거짓 두 갈래
        System.out.println("\n[2] if-else");
        int number = 7;
        if (number % 2 == 0) {
            System.out.println(number + "은(는) 짝수");
        } else {
            System.out.println(number + "은(는) 홀수");
        }

        // [3] else if : 여러 조건을 순서대로 검사 (성적 등급)
        System.out.println("\n[3] else if 사슬");
        int point = 82;
        if (point >= 90) {
            System.out.println("등급: A");
        } else if (point >= 80) {
            System.out.println("등급: B");   // 82 → 여기서 걸림
        } else if (point >= 70) {
            System.out.println("등급: C");
        } else {
            System.out.println("등급: F");
        }

        // [4] 중첩 if : if 안에 또 if
        System.out.println("\n[4] 중첩 if");
        int age = 20;
        boolean hasTicket = true;
        if (age >= 19) {
            if (hasTicket) {
                System.out.println("입장 가능 (성인 + 티켓 보유)");
            } else {
                System.out.println("티켓이 필요합니다.");
            }
        } else {
            System.out.println("성인만 입장 가능합니다.");
        }

        // [주의] 중괄호 {}를 생략하면 바로 다음 한 문장만 if에 속한다. 실수 방지를 위해 {} 권장.
        System.out.println("\n[주의] 가독성을 위해 항상 중괄호 {} 사용을 권장한다.");

        System.out.println("\n프로그램 정상 종료");
    }
}
