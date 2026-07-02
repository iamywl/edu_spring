package com.edu.javabook.ch03;

/**
 * Chapter 03 연산자 - 3.1 부호/증감 연산자
 *
 * 이 소절 하나만 다룹니다: 단항 부호 연산자(+, -)와 증감 연산자(++, --),
 * 그리고 전위(prefix)/후위(postfix)의 결정적 차이.
 *
 * 핵심 결론(먼저 봅니다):
 *   - 단항 +는 사실상 아무 일도 하지 않습니다. -는 부호를 뒤집습니다.
 *   - ++x(전위): "먼저 1 증가시킨 뒤" 그 값을 식의 결과로 사용합니다.
 *   - x++(후위): "증가 전 값을 식의 결과로 먼저 사용하고" 그 다음 1 증가합니다.
 *   - 변수 단독으로 쓰면 전위/후위 결과는 같지만, 다른 식과 섞이면 완전히 달라집니다.
 */
public class SignAndIncrement {

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println(" 3.1 부호/증감 연산자 (SignAndIncrement)");
        System.out.println("==================================================\n");

        unarySign();
        prefixVsPostfix();
        whyItMatters();
        commonPitfall();
    }

    /** 단항 부호 연산자 +, - */
    private static void unarySign() {
        System.out.println("[1] 단항 부호 연산자 (+, -)");
        int a = 10;
        int b = -10;
        System.out.println("  +a = " + (+a) + "  (단항 +는 값을 바꾸지 않음)");
        System.out.println("  -a = " + (-a) + "  (부호 반전)");
        System.out.println("  -b = " + (-b) + "  (음수의 부호 반전 -> 양수)");
        // 왜? 단항 -는 '0 - a'와 같은 의미로 부호를 뒤집습니다.
        System.out.println();
    }

    /** 전위/후위 증감의 차이를 값의 흐름으로 보여줍니다. */
    private static void prefixVsPostfix() {
        System.out.println("[2] 전위(++x) vs 후위(x++)");

        int x = 5;
        int r1 = ++x;   // 먼저 6으로 증가 -> r1 = 6, x = 6
        System.out.println("  int x=5; r1 = ++x;  => r1=" + r1 + ", x=" + x + " (전위: 증가 후 사용)");

        int y = 5;
        int r2 = y++;   // 먼저 5를 사용 -> r2 = 5, 그 뒤 y = 6
        System.out.println("  int y=5; r2 = y++;  => r2=" + r2 + ", y=" + y + " (후위: 사용 후 증가)");

        System.out.println("  => 변수에 대입하며 섞으면 결과가 달라진다!");
        System.out.println();
    }

    /** 왜 중요한가: 식 안에 섞였을 때 */
    private static void whyItMatters() {
        System.out.println("[3] 왜 중요한가 (식 안에 섞였을 때)");
        int i = 3;
        int result = i++ + i++;   // (3) + (4) = 7, 이후 i=5
        System.out.println("  int i=3; i++ + i++  => 결과=" + result + ", 최종 i=" + i);
        System.out.println("  풀이: 첫 i++는 3을 내놓고 i=4, 둘째 i++는 4를 내놓고 i=5 -> 3+4=7");

        int j = 3;
        int result2 = ++j + ++j;  // (4) + (5) = 9, 이후 j=5
        System.out.println("  int j=3; ++j + ++j  => 결과=" + result2 + ", 최종 j=" + j);
        System.out.println("  풀이: 첫 ++j는 j=4로 만들고 4, 둘째 ++j는 j=5로 만들고 5 -> 4+5=9");
        System.out.println();
    }

    /** 흔한 함정: 가독성을 해치는 복잡한 증감 */
    private static void commonPitfall() {
        System.out.println("[4] 흔한 함정 / 조언");
        System.out.println("  - 한 식에서 같은 변수를 여러 번 증감하면 읽기 어렵고 버그의 원인.");
        System.out.println("  - 반복문 카운터처럼 '단독'으로 쓸 때는 전위/후위 결과가 같음.");
        int k = 0;
        k++;        // 단독 사용
        ++k;        // 단독 사용
        System.out.println("  k++; ++k; 두 번 실행 후 k=" + k + " (단독이면 순서 무관)");
        System.out.println();
        System.out.println("정상 종료.");
    }
}
