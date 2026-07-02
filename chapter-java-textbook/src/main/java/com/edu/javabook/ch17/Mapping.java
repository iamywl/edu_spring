package com.edu.javabook.ch17;

import java.util.List;

/**
 * 17.6 매핑
 *
 * [매핑(mapping)]
 * - map(Function)      : 각 요소를 다른 값/타입으로 1:1 변환한다.
 * - mapToInt(...)      : 각 요소를 int 로 변환하여 IntStream 을 만든다(집계에 유리).
 * - flatMap(Function)  : 각 요소를 스트림으로 변환한 뒤 하나의 스트림으로 평탄화(1:N)한다.
 *
 * 이 소절에서는 map, mapToInt, flatMap 을 각각 시연한다.
 */
public class Mapping {

    public static void main(String[] args) {

        System.out.println("=== 17.6 매핑 ===");

        List<String> words = List.of("apple", "banana", "cherry");

        // [1] map: 문자열 → 대문자 문자열 (1:1 변환)
        System.out.println("\n[1] map (대문자로 변환)");
        List<String> upper = words.stream()
                .map(String::toUpperCase)   // 각 요소를 변환
                .toList();
        System.out.println("결과: " + upper);

        // [2] mapToInt: 문자열 → 길이(int) → IntStream 으로 합계 집계
        System.out.println("\n[2] mapToInt (문자열 길이 합계)");
        int totalLength = words.stream()
                .mapToInt(String::length)   // 각 요소를 int(길이)로 변환 → IntStream
                .sum();                     // IntStream 은 sum() 제공
        System.out.println("전체 글자 수: " + totalLength);

        // [3] flatMap: 중첩 리스트를 하나의 스트림으로 평탄화 (1:N)
        System.out.println("\n[3] flatMap (중첩 리스트 평탄화)");
        List<List<Integer>> nested = List.of(
                List.of(1, 2),
                List.of(3, 4),
                List.of(5, 6)
        );
        List<Integer> flat = nested.stream()
                .flatMap(List::stream)      // 각 내부 리스트를 스트림으로 펼쳐 하나로 합침
                .toList();
        System.out.println("평탄화 결과: " + flat);

        System.out.println("\n프로그램 정상 종료");
    }
}
