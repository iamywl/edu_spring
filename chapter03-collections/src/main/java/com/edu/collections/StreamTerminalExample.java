package com.edu.collections;

import java.util.*;
import java.util.stream.*;

/**
 * Chapter 03 - Stream 종단(최종) 연산 예제 (StreamTerminalExample)
 *
 * ┌─────────────────────────────────────────────────────────────┐
 * │ 이 파일은 "종단 연산(Terminal Operations)"에만 집중한다.     │
 * └─────────────────────────────────────────────────────────────┘
 *
 * ▶ 종단 연산이란?
 *   - 스트림 파이프라인을 "끝내며" 실제 결과(값/컬렉션/부수효과)를 만들어내는 연산이다.
 *   - 종단 연산이 호출되어야 비로소 앞의 모든 중간 연산이 실제로 실행된다(지연 평가 종료).
 *   - 반환 타입이 스트림이 아니다. (예: long, Optional, boolean, void, 컬렉션 등)
 *   - 대표: forEach / count / reduce / min / max / anyMatch / allMatch / findFirst.
 *
 * ▶ 스트림은 "한 번만 소비된다" (single-use)
 *   - 하나의 스트림에 종단 연산을 두 번 호출하면 IllegalStateException이 발생한다.
 *   - 물이 한 번 흘러가면 다시 못 쓰듯, 스트림도 재사용 불가.
 *   - 같은 데이터를 또 처리하려면 소스(컬렉션)에서 stream()을 "다시" 만들어야 한다.
 *
 * 참고: .toList()는 Java 16+ 방식이며 수정 불가 리스트를 반환한다.
 */
public class StreamTerminalExample {

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("  Chapter 03 - 종단 연산 (Terminal)");
        System.out.println("========================================\n");

        List<Integer> numbers = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        // --------------------------------------------------
        // 0. 스트림은 한 번만 소비된다 (재사용 시 예외)
        // --------------------------------------------------
        System.out.println("--- 0. 스트림 1회 소비 규칙 ---");
        Stream<Integer> once = numbers.stream();
        System.out.println("  첫 번째 종단 연산 count() : " + once.count());
        try {
            once.count(); // 이미 소비된 스트림 재사용 -> 예외
        } catch (IllegalStateException e) {
            System.out.println("  두 번째 종단 연산 시도 -> 예외 발생!");
            System.out.println("    " + e.getMessage());
        }
        System.out.println("  => 다시 쓰려면 numbers.stream()으로 새 스트림을 만들어야 함");
        System.out.println();

        // --------------------------------------------------
        // 1. forEach - 각 요소에 대해 동작 수행 (반환값 없음, 부수효과 목적)
        // --------------------------------------------------
        System.out.println("--- 1. forEach ---");
        System.out.print("  5 이하 출력 : ");
        numbers.stream()
                .filter(n -> n <= 5)
                .forEach(n -> System.out.print(n + " "));
        System.out.println("\n");

        // --------------------------------------------------
        // 2. count - 요소 개수 (long 반환)
        // --------------------------------------------------
        System.out.println("--- 2. count ---");
        long count = numbers.stream()
                .filter(n -> n > 5)
                .count();
        System.out.println("  5보다 큰 수의 개수 : " + count);
        System.out.println();

        // --------------------------------------------------
        // 3. reduce - 요소들을 하나의 값으로 누적(fold)
        // --------------------------------------------------
        System.out.println("--- 3. reduce (누적) ---");
        // (1) 초기값(identity)이 있는 형태 -> 항상 값 반환
        int sum = numbers.stream().reduce(0, Integer::sum);
        System.out.println("  합계 (초기값 0)     : " + sum);

        // (2) 초기값이 없는 형태 -> Optional 반환 (비어있을 수 있으므로)
        Optional<Integer> product = numbers.stream().reduce((a, b) -> a * b);
        System.out.println("  전체 곱 (Optional)  : " + product.orElse(0));

        // (3) 문자열 결합에도 활용
        String joined = Stream.of("Java", "Stream", "API")
                .reduce("", (a, b) -> a.isEmpty() ? b : a + " " + b);
        System.out.println("  문자열 결합         : " + joined);
        System.out.println();

        // --------------------------------------------------
        // 4. min / max - 최솟값 / 최댓값 (Optional 반환)
        // --------------------------------------------------
        System.out.println("--- 4. min / max ---");
        Optional<Integer> minVal = numbers.stream().min(Integer::compareTo);
        Optional<Integer> maxVal = numbers.stream().max(Integer::compareTo);
        System.out.println("  min : " + minVal.orElse(0) + ", max : " + maxVal.orElse(0));
        System.out.println();

        // --------------------------------------------------
        // 5. anyMatch / allMatch / noneMatch - 조건 검사 (boolean, short-circuit)
        // --------------------------------------------------
        System.out.println("--- 5. anyMatch / allMatch / noneMatch ---");
        // 이 연산들은 결과가 확정되면 즉시 멈춘다(단락 평가). 전부 순회하지 않을 수 있음.
        boolean anyEven = numbers.stream().anyMatch(n -> n % 2 == 0);
        boolean allPositive = numbers.stream().allMatch(n -> n > 0);
        boolean noneNegative = numbers.stream().noneMatch(n -> n < 0);
        System.out.println("  anyMatch (짝수 존재?)  : " + anyEven);
        System.out.println("  allMatch (모두 양수?)  : " + allPositive);
        System.out.println("  noneMatch (음수 없음?) : " + noneNegative);
        System.out.println();

        // --------------------------------------------------
        // 6. findFirst / findAny - 요소 하나 찾기 (Optional, short-circuit)
        // --------------------------------------------------
        System.out.println("--- 6. findFirst / findAny ---");
        Optional<Integer> first = numbers.stream()
                .filter(n -> n > 5)
                .findFirst();
        System.out.println("  findFirst (5보다 큰 첫 번째) : " + first.orElse(-1));

        Optional<Integer> any = numbers.stream()
                .filter(n -> n % 3 == 0)
                .findAny();
        System.out.println("  findAny (3의 배수 아무거나)   : " + any.orElse(-1));
        System.out.println();

        // --------------------------------------------------
        // 7. toArray - 배열로 변환
        // --------------------------------------------------
        System.out.println("--- 7. toArray ---");
        Integer[] oddArray = numbers.stream()
                .filter(n -> n % 2 != 0)
                .toArray(Integer[]::new);
        System.out.println("  홀수 배열 : " + Arrays.toString(oddArray));
        System.out.println();

        System.out.println("========================================");
        System.out.println("  종단 연산 예제 완료!");
        System.out.println("========================================");
    }
}
