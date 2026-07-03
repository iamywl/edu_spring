package com.edu.javabook.ch03;

/**
 * Chapter 03 연산자 - 3.11 삼항(조건) 연산자
 *
 * 이 소절 하나만 다룹니다: 조건 ? 값1 : 값2 형태의 삼항 연산자와 중첩 사용.
 *
 * 핵심 결론(먼저 봅니다):
 *   - 조건 ? A : B 는 '조건이 true면 A, 아니면 B'를 '값으로' 돌려주는 유일한 삼항 연산자입니다.
 *   - if문은 '문장(statement)'이라 값을 못 내지만, 삼항은 '식(expression)'이라 대입/반환에 바로 쓸 수 있습니다.
 *   - 중첩(nested)도 되지만 2~3단계를 넘으면 가독성이 급격히 나빠집니다.
 */
public class Ternary {

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println(" 3.11 삼항(조건) 연산자 (Ternary)");
        System.out.println("==================================================\n");

        basic();
        asExpression();
        nested();
        readabilityNote();
    }

    private static void basic() {
        System.out.println("[1] 기본 형태: 조건 ? A : B");
        int score = 75;
        String result = (score >= 60) ? "합격" : "불합격";
        System.out.println("  score=75, (score>=60) ? \"합격\" : \"불합격\" => " + result);
        System.out.println();
    }

    /** 식이라서 대입/반환에 바로 쓸 수 있다 */
    private static void asExpression() {
        System.out.println("[2] 삼항은 '식'이라 값으로 쓴다");
        int a = 7, b = 12;
        int max = (a > b) ? a : b;   // 한 줄로 최댓값
        System.out.println("  int max = (a>b)?a:b; => " + max);
        System.out.println("  절댓값도: (n<0)?-n:n => " + absViaTernary(-9));
        System.out.println();
    }

    private static int absViaTernary(int n) {
        return (n < 0) ? -n : n;
    }

    /** 중첩 삼항: 등급 매기기 */
    private static void nested() {
        System.out.println("[3] 중첩 삼항 (등급 매기기)");
        int[] scores = {95, 82, 71, 55};
        for (int s : scores) {
            // 위에서부터 차례로 걸러진다: 90+ A, 80+ B, 70+ C, 나머지 F
            String grade = (s >= 90) ? "A"
                          : (s >= 80) ? "B"
                          : (s >= 70) ? "C"
                          : "F";
            System.out.println("  점수 " + s + " => 등급 " + grade);
        }
        System.out.println();
    }

    private static void readabilityNote() {
        System.out.println("[4] 가독성 주의");
        System.out.println("  - 중첩이 깊어지면 if-else나 switch가 더 읽기 쉽다.");
        System.out.println("  - 삼항은 '한 가지 값을 두 갈래 중에 고를 때' 가장 빛난다.");
        System.out.println();
        System.out.println("정상 종료.");
    }
}
