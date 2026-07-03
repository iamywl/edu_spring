package com.edu.javabook.ch17;

import java.util.List;

/**
 * 17.9 매칭
 *
 * [매칭(matching)]
 * - anyMatch(Predicate)  : 하나라도 조건을 만족하면 true.
 * - allMatch(Predicate)  : 모든 요소가 조건을 만족하면 true.
 * - noneMatch(Predicate) : 아무 요소도 조건을 만족하지 않으면 true.
 *
 * 세 연산 모두 boolean 을 반환하는 최종 연산이며,
 * 결과가 확정되면 나머지 요소를 검사하지 않고 멈출 수 있다(short-circuit).
 *
 * 이 소절에서는 anyMatch/allMatch/noneMatch 를 각각 시연한다.
 */
public class Matching {

    public static void main(String[] args) {

        System.out.println("=== 17.9 매칭 ===");

        List<Integer> numbers = List.of(2, 4, 6, 8, 10);

        // [1] anyMatch: 하나라도 조건을 만족하는가?
        System.out.println("\n[1] anyMatch (하나라도 6보다 큰 값이 있는가?)");
        boolean anyOver6 = numbers.stream().anyMatch(n -> n > 6);
        System.out.println("결과: " + anyOver6);

        // [2] allMatch: 모두 조건을 만족하는가?
        System.out.println("\n[2] allMatch (모두 짝수인가?)");
        boolean allEven = numbers.stream().allMatch(n -> n % 2 == 0);
        System.out.println("결과: " + allEven);

        // [3] noneMatch: 아무도 조건을 만족하지 않는가?
        System.out.println("\n[3] noneMatch (홀수가 하나도 없는가?)");
        boolean noOdd = numbers.stream().noneMatch(n -> n % 2 == 1);
        System.out.println("결과: " + noOdd);

        System.out.println("\n프로그램 정상 종료");
    }
}
