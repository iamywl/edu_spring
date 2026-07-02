package com.edu.javabook.ch17;

import java.util.List;
import java.util.stream.Stream;

/**
 * 17.3 중간 처리와 최종 처리
 *
 * [중간 연산(intermediate operation)]
 * - 스트림을 입력받아 "또 다른 스트림"을 반환한다. (filter, map, sorted, distinct, peek ...)
 * - 연결(체이닝)이 가능하며, 즉시 실행되지 않고 "지연 평가(lazy evaluation)"된다.
 *
 * [최종 연산(terminal operation)]
 * - 스트림을 소비하여 결과(값/컬렉션/void)를 만든다. (forEach, count, collect, reduce, sum ...)
 * - 최종 연산이 호출되는 순간 비로소 파이프라인 전체가 실행된다.
 * - 최종 연산 이후 그 스트림은 재사용할 수 없다.
 *
 * 이 소절에서는 최종 연산이 없으면 중간 연산이 실행되지 않음(지연 평가)을 출력으로 확인한다.
 */
public class IntermediateTerminal {

    public static void main(String[] args) {

        System.out.println("=== 17.3 중간 처리와 최종 처리 ===");

        List<Integer> numbers = List.of(1, 2, 3, 4, 5);

        // [1] 중간 연산만 구성 (최종 연산 없음) → 지연 평가로 실행되지 않음
        System.out.println("\n[1] 중간 연산만 구성 (최종 연산 없음)");
        Stream<Integer> lazyStream = numbers.stream()
                .filter(n -> {
                    System.out.println("  filter 실행: " + n); // 최종 연산 전에는 찍히지 않음
                    return n % 2 == 0;
                });
        System.out.println("아직 최종 연산이 없으므로 위 filter 로그는 출력되지 않는다.");

        // [2] 최종 연산(forEach) 호출 → 이때 파이프라인 전체가 실행됨
        System.out.println("\n[2] 최종 연산 호출 (지금 실행됨)");
        lazyStream.forEach(n -> System.out.println("  최종 결과(짝수): " + n));

        // [3] 최종 연산이 스트림을 소비한다는 점 강조
        System.out.println("\n[3] 최종 연산의 결과 반환");
        long evenCount = numbers.stream()
                .filter(n -> n % 2 == 0)  // 중간 연산
                .count();                 // 최종 연산: 개수(long) 반환
        System.out.println("짝수 개수: " + evenCount);
        System.out.println("→ 최종 연산이 호출되어야 중간 연산이 실제로 수행된다(지연 평가).");

        System.out.println("\n프로그램 정상 종료");
    }
}
