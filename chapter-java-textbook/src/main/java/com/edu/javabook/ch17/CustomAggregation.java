package com.edu.javabook.ch17;

import java.util.List;
import java.util.Optional;

/**
 * 17.11 커스텀 집계
 *
 * [reduce(리듀스)]
 * - count/sum/max 같은 기본 집계로 표현하기 어려운 "직접 정의한 집계"를 만들 때 사용한다.
 * - 요소들을 하나의 값으로 누적(reduce)한다.
 *
 * [reduce 의 세 가지 형태]
 * - reduce(BinaryOperator)              : 초기값 없이 누적 → Optional 반환.
 * - reduce(identity, BinaryOperator)    : 초기값(identity) 과 함께 누적 → 값 직접 반환.
 * - reduce(identity, accumulator, combiner) : 병렬 처리 시 부분 결과 결합까지 지정.
 *
 * 이 소절에서는 reduce 로 합계/최대/곱셈(커스텀 집계)을 시연한다.
 */
public class CustomAggregation {

    public static void main(String[] args) {

        System.out.println("=== 17.11 커스텀 집계 ===");

        List<Integer> numbers = List.of(1, 2, 3, 4, 5);

        // [1] reduce (초기값 있음): 합계
        System.out.println("\n[1] reduce (초기값 0으로 합계)");
        int sum = numbers.stream().reduce(0, (a, b) -> a + b); // 0에서 시작해 계속 더함
        System.out.println("합계: " + sum);

        // [2] reduce (초기값 없음): 최댓값 → Optional 반환
        System.out.println("\n[2] reduce (초기값 없이 최댓값, Optional 반환)");
        Optional<Integer> max = numbers.stream().reduce(Integer::max);
        System.out.println("최댓값: " + max.orElse(0));

        // [3] reduce (커스텀 연산): 모든 요소의 곱
        System.out.println("\n[3] reduce (커스텀: 모든 요소의 곱)");
        int product = numbers.stream().reduce(1, (a, b) -> a * b); // 1에서 시작해 계속 곱함
        System.out.println("곱: " + product);

        // [4] reduce (커스텀 연산): 문자열 이어붙이기
        System.out.println("\n[4] reduce (문자열 누적)");
        List<String> words = List.of("Java", "Stream", "Reduce");
        String joined = words.stream().reduce("", (a, b) -> a.isEmpty() ? b : a + "-" + b);
        System.out.println("연결 결과: " + joined);

        System.out.println("\n프로그램 정상 종료");
    }
}
