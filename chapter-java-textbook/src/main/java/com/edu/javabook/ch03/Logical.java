package com.edu.javabook.ch03;

/**
 * Chapter 03 연산자 - 3.7 논리 연산자
 *
 * 이 소절 하나만 다룹니다: 논리곱 &&, 논리합 ||, 부정 !, 그리고 단축평가(short-circuit).
 *
 * 핵심 결론(먼저 봅니다):
 *   - &&는 왼쪽이 false면 오른쪽을 '아예 계산하지 않고' false로 확정합니다.
 *   - ||는 왼쪽이 true면 오른쪽을 '아예 계산하지 않고' true로 확정합니다.
 *   - 이 '단축평가' 덕분에 null 검사 후 메서드 호출 같은 안전 패턴이 가능합니다.
 *   - (참고) &, | 는 논리형에도 쓸 수 있지만 단축평가를 하지 않습니다(항상 양쪽 계산).
 */
public class Logical {

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println(" 3.7 논리 연산자 (Logical)");
        System.out.println("==================================================\n");

        truthTables();
        shortCircuitAnd();
        shortCircuitOr();
        safePattern();
        nonShortCircuit();
    }

    private static void truthTables() {
        System.out.println("[1] 진리표 (&&, ||, !)");
        System.out.println("  true  && false => " + (true && false));
        System.out.println("  true  || false => " + (true || false));
        System.out.println("  !true          => " + (!true));
        System.out.println();
    }

    /** && 단축평가: 왼쪽이 false면 오른쪽은 실행 안 됨 */
    private static void shortCircuitAnd() {
        System.out.println("[2] && 단축평가 (왼쪽 false면 오른쪽 생략)");
        boolean result = (false) && sideEffect("&&의 오른쪽");
        System.out.println("  false && sideEffect() => " + result);
        System.out.println("  위에서 'sideEffect 호출됨' 이 안 찍혔다면 단축평가가 일어난 것.");
        System.out.println();
    }

    /** || 단축평가: 왼쪽이 true면 오른쪽은 실행 안 됨 */
    private static void shortCircuitOr() {
        System.out.println("[3] || 단축평가 (왼쪽 true면 오른쪽 생략)");
        boolean result = (true) || sideEffect("||의 오른쪽");
        System.out.println("  true || sideEffect() => " + result);
        System.out.println("  마찬가지로 오른쪽 호출이 생략된다.");
        System.out.println();
    }

    /** 단축평가를 활용한 안전 패턴 */
    private static void safePattern() {
        System.out.println("[4] 단축평가 안전 패턴 (null 방어)");
        String s = null;
        // s가 null이면 왼쪽에서 멈추므로 s.length() 호출로 인한 NPE가 없다.
        boolean ok = (s != null) && (s.length() > 0);
        System.out.println("  (s != null) && (s.length() > 0) => " + ok + "  (NPE 없이 안전)");
        System.out.println();
    }

    /** 참고: 비단축 &, | (항상 양쪽 계산) */
    private static void nonShortCircuit() {
        System.out.println("[5] (참고) 비단축 & (양쪽 항상 계산)");
        boolean result = (false) & sideEffect("&의 오른쪽 - 항상 실행됨");
        System.out.println("  false & sideEffect() => " + result + "  (오른쪽도 실행됨)");
        System.out.println();
        System.out.println("정상 종료.");
    }

    /** 호출되면 흔적을 남기는 도우미: 실행 여부를 눈으로 확인용 */
    private static boolean sideEffect(String who) {
        System.out.println("    >> sideEffect 호출됨: " + who);
        return true;
    }
}
