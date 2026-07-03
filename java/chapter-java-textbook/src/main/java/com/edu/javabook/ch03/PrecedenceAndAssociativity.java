package com.edu.javabook.ch03;

/**
 * Chapter 03 연산자 - 3.12 연산의 방향과 우선순위
 *
 * 이 소절 하나만 다룹니다: 연산자 우선순위(precedence)와 결합 방향(associativity).
 *
 * 핵심 결론(먼저 봅니다):
 *   - 우선순위: 한 식에 여러 연산자가 섞이면 '우선순위가 높은' 것부터 먼저 묶어 계산합니다(예: * 가 + 보다 먼저).
 *   - 결합 방향: 우선순위가 같으면 '결합 방향'대로 묶습니다. 대부분 왼쪽->오른쪽이지만,
 *     대입(=)과 삼항(?:)은 오른쪽->왼쪽입니다.
 *   - 헷갈릴 땐 괄호로 의도를 '명시'하는 것이 최선입니다. 괄호는 성능 손해가 없습니다.
 */
public class PrecedenceAndAssociativity {

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println(" 3.12 연산의 방향과 우선순위 (PrecedenceAndAssociativity)");
        System.out.println("==================================================\n");

        precedence();
        leftToRight();
        rightToLeft();
        mixedTrap();
        parenthesesAdvice();
    }

    /** 우선순위: * 가 + 보다 먼저 */
    private static void precedence() {
        System.out.println("[1] 우선순위 (* 가 + 보다 먼저)");
        System.out.println("  2 + 3 * 4 = " + (2 + 3 * 4) + "  (3*4 먼저 -> 2+12)");
        System.out.println("  (2 + 3) * 4 = " + ((2 + 3) * 4) + "  (괄호로 순서 강제)");
        System.out.println();
    }

    /** 같은 우선순위는 왼쪽->오른쪽 결합 */
    private static void leftToRight() {
        System.out.println("[2] 결합 방향: 산술은 왼쪽 -> 오른쪽");
        System.out.println("  100 - 20 - 5 = " + (100 - 20 - 5) + "  ((100-20)-5, 즉 75)");
        System.out.println("  만약 오른쪽부터였다면 100-(20-5)=85 였을 것");
        System.out.println("  8 / 4 / 2 = " + (8 / 4 / 2) + "  ((8/4)/2 = 1)");
        System.out.println();
    }

    /** 대입과 삼항은 오른쪽->왼쪽 결합 */
    private static void rightToLeft() {
        System.out.println("[3] 결합 방향: 대입(=)과 삼항(?:)은 오른쪽 -> 왼쪽");
        int a, b, c;
        a = b = c = 10;   // c=10, 그다음 b=10, 그다음 a=10
        System.out.println("  a = b = c = 10 => a=" + a + ", b=" + b + ", c=" + c + " (오른쪽부터 대입)");
        System.out.println();
    }

    /** 논리/비교 섞임의 함정 */
    private static void mixedTrap() {
        System.out.println("[4] 함정: 비교/논리 섞임");
        boolean r = 2 + 3 > 4 && 1 < 2;
        // 산술(+) > 비교(>,<) > 논리(&&) 순으로 우선순위
        System.out.println("  2 + 3 > 4 && 1 < 2 => " + r);
        System.out.println("  풀이: (2+3) 먼저 -> (5>4)=true, (1<2)=true -> true && true");
        System.out.println();
    }

    private static void parenthesesAdvice() {
        System.out.println("[5] 조언: 괄호로 의도를 드러내라");
        System.out.println("  - 우선순위 표를 외우기보다 괄호로 '읽는 사람'에게 의도를 보여준다.");
        System.out.println("  - 괄호는 런타임 비용이 없다. 애매하면 무조건 괄호.");
        System.out.println();
        System.out.println("정상 종료.");
    }
}
