package com.edu.javabook.ch12;

/**
 * 12.7 수학 클래스
 *
 * Math 클래스는 수학 계산에 필요한 상수와 정적 메서드를 모아 둔 유틸리티 클래스다.
 * 모든 멤버가 static 이라 객체 없이 Math.메서드() 형태로 바로 쓴다.
 *
 * 주요 메서드 :
 *   - Math.abs(x)      : 절댓값
 *   - Math.max/min     : 큰 값 / 작은 값
 *   - Math.pow(a, b)   : a의 b제곱
 *   - Math.sqrt(x)     : 제곱근
 *   - Math.round(x)    : 반올림 (가장 가까운 정수)
 *   - Math.ceil/floor  : 올림 / 내림
 *   - Math.random()    : 0.0 이상 1.0 미만 난수
 *   - Math.addExact(a,b): 오버플로가 나면 예외를 던지는 "안전한 덧셈"
 *   - 상수 Math.PI, Math.E
 *
 * 이 소절에서는 위 메서드들을 호출해 계산 결과를 확인한다.
 * (Math.random 은 비대화형/재현성을 위해 고정 시드의 Random 대신 "범위만" 검증한다)
 */
public class MathClass {

    public static void main(String[] args) {

        System.out.println("=== 12.7 수학 클래스 ===");

        // [1] abs / max / min
        System.out.println("\n[1] abs / max / min");
        System.out.println("  Math.abs(-7)     = " + Math.abs(-7));
        System.out.println("  Math.max(3, 9)   = " + Math.max(3, 9));
        System.out.println("  Math.min(3, 9)   = " + Math.min(3, 9));

        // [2] pow / sqrt
        System.out.println("\n[2] pow / sqrt");
        System.out.println("  Math.pow(2, 10)  = " + Math.pow(2, 10) + " (2의 10제곱)");
        System.out.println("  Math.sqrt(144)   = " + Math.sqrt(144));

        // [3] round / ceil / floor
        System.out.println("\n[3] round / ceil / floor");
        System.out.println("  Math.round(3.4)  = " + Math.round(3.4) + " (반올림 내림)");
        System.out.println("  Math.round(3.6)  = " + Math.round(3.6) + " (반올림 올림)");
        System.out.println("  Math.ceil(3.1)   = " + Math.ceil(3.1) + " (올림)");
        System.out.println("  Math.floor(3.9)  = " + Math.floor(3.9) + " (내림)");

        // [4] 상수와 random
        System.out.println("\n[4] 상수 / random");
        System.out.println("  Math.PI          = " + Math.PI);
        double rand = Math.random();  // 0.0 이상 1.0 미만
        System.out.println("  Math.random()    = " + rand);
        System.out.println("  0.0 <= random < 1.0 ? " + (rand >= 0.0 && rand < 1.0));
        // 1~6 주사위 예시 (범위만 검증)
        int dice = (int) (Math.random() * 6) + 1;
        System.out.println("  주사위(1~6)      = " + dice + " (1~6 범위 ? " + (dice >= 1 && dice <= 6) + ")");

        // [5] addExact : 오버플로 안전 덧셈
        System.out.println("\n[5] addExact (오버플로 안전 덧셈)");
        int safe = Math.addExact(100, 200);
        System.out.println("  Math.addExact(100, 200) = " + safe);
        try {
            // MAX_VALUE + 1 은 오버플로 → ArithmeticException 발생
            Math.addExact(Integer.MAX_VALUE, 1);
        } catch (ArithmeticException e) {
            System.out.println("  addExact(MAX_VALUE, 1) → 예외: " + e.getMessage() + " (오버플로 감지)");
        }

        System.out.println("\n프로그램 정상 종료");
    }
}
