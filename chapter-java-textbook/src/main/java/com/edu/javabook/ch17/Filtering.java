package com.edu.javabook.ch17;

import java.util.List;

/**
 * 17.5 필터링
 *
 * [필터링(filtering)]
 * - filter(Predicate)  : 조건을 만족하는(true) 요소만 남긴다.
 * - distinct()         : 중복 요소를 제거한다(equals/hashCode 기준).
 *
 * 두 연산 모두 중간 연산으로, 결과 스트림을 반환한다.
 *
 * 이 소절에서는 filter 로 조건 필터링, distinct 로 중복 제거를 시연한다.
 */
public class Filtering {

    public static void main(String[] args) {

        System.out.println("=== 17.5 필터링 ===");

        List<Integer> numbers = List.of(1, 2, 2, 3, 4, 4, 5, 6, 6);

        // [1] filter: 짝수만 남기기
        System.out.println("\n[1] filter (짝수만 남기기)");
        List<Integer> evens = numbers.stream()
                .filter(n -> n % 2 == 0)   // 조건이 true 인 요소만 통과
                .toList();                 // Java 16+ : 불변 리스트로 수집
        System.out.println("짝수: " + evens);

        // [2] distinct: 중복 제거
        System.out.println("\n[2] distinct (중복 제거)");
        List<Integer> distinct = numbers.stream()
                .distinct()                // 중복 요소 제거
                .toList();
        System.out.println("중복 제거: " + distinct);

        // [3] filter + distinct 조합
        System.out.println("\n[3] filter + distinct 조합 (중복 없는 짝수)");
        List<Integer> distinctEvens = numbers.stream()
                .filter(n -> n % 2 == 0)
                .distinct()
                .toList();
        System.out.println("결과: " + distinctEvens);

        System.out.println("\n프로그램 정상 종료");
    }
}
