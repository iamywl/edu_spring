package com.edu.javabook.ch02;

/**
 * 2.5 논리 타입
 *
 * boolean 은 참(true) 또는 거짓(false) 두 가지 값만 가지는 타입이다.
 * 주로 '조건'을 표현하는 데 쓰이며, if/while 같은 제어문의 핵심이 된다.
 * (자바에서는 C언어처럼 0/1을 boolean으로 쓸 수 없다. 오직 true/false만 가능.)
 */
public class BooleanType {

    public static void main(String[] args) {

        System.out.println("=== 2.5 논리 타입 ===");

        // [1] boolean 기본
        System.out.println("\n[1] true / false");
        boolean isJavaFun = true;
        boolean isFinished = false;
        System.out.println("isJavaFun  = " + isJavaFun);
        System.out.println("isFinished = " + isFinished);

        // [2] 비교 연산의 결과는 boolean 이다
        System.out.println("\n[2] 비교 연산 결과");
        int a = 10, b = 20;
        System.out.println("a > b  → " + (a > b));
        System.out.println("a < b  → " + (a < b));
        System.out.println("a == b → " + (a == b));
        System.out.println("a != b → " + (a != b));

        // [3] 논리 연산자: && (AND), || (OR), ! (NOT)
        System.out.println("\n[3] 논리 연산자");
        boolean t = true, f = false;
        System.out.println("t && f → " + (t && f) + "  (둘 다 참이어야 참)");
        System.out.println("t || f → " + (t || f) + "  (하나라도 참이면 참)");
        System.out.println("!t     → " + (!t) + "  (참을 뒤집으면 거짓)");

        // [4] 조건문에서의 활용
        System.out.println("\n[4] if 문에서 활용");
        int score = 85;
        boolean isPass = score >= 60;
        if (isPass) {
            System.out.println("score " + score + " → 합격");
        } else {
            System.out.println("score " + score + " → 불합격");
        }

        // [5] 단락 평가(short-circuit): && 는 앞이 false면 뒤를 아예 안 본다
        System.out.println("\n[5] 단락 평가");
        int x = 0;
        // x != 0 가 false이므로 뒤의 (10/x) 는 실행되지 않아 나눗셈 오류가 안 난다
        boolean safe = (x != 0) && (10 / x > 1);
        System.out.println("(x!=0) && (10/x>1) → " + safe + "  (0으로 나누기 회피됨)");

        // [왜?] boolean은 프로그램의 '흐름'을 결정하는 스위치 역할을 한다.
        System.out.println("\n[왜?] boolean은 조건 분기의 스위치이며, 상태 플래그로도 널리 쓰인다.");

        System.out.println("\n프로그램 정상 종료");
    }
}
