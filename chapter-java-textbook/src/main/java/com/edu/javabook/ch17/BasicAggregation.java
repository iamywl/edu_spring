package com.edu.javabook.ch17;

import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.OptionalInt;

/**
 * 17.10 기본 집계
 *
 * [기본 집계(aggregation)]
 * - count()   : 요소 개수(long).
 * - sum()     : 합계 (IntStream/LongStream/DoubleStream 에서 제공).
 * - average() : 평균 (OptionalDouble 반환).
 * - max()/min(): 최대/최소 (Optional 반환).
 * - summaryStatistics() : 개수/합/평균/최대/최소를 한 번에 계산.
 *
 * sum/average/max/min 은 숫자 스트림(IntStream 등)에서 바로 사용할 수 있다.
 *
 * 이 소절에서는 개수/합/평균/최대/최소와 통계 객체를 시연한다.
 */
public class BasicAggregation {

    public static void main(String[] args) {

        System.out.println("=== 17.10 기본 집계 ===");

        List<Integer> scores = List.of(90, 75, 88, 62, 100, 55);

        // [1] count: 개수
        System.out.println("\n[1] count (개수)");
        System.out.println("요소 개수: " + scores.stream().count());

        // [2] sum: 합계 (IntStream 으로 변환 후)
        System.out.println("\n[2] sum (합계)");
        int sum = scores.stream().mapToInt(Integer::intValue).sum();
        System.out.println("합계: " + sum);

        // [3] average: 평균 (OptionalDouble)
        System.out.println("\n[3] average (평균)");
        double avg = scores.stream().mapToInt(Integer::intValue).average().orElse(0);
        System.out.println("평균: " + avg);

        // [4] max / min: 최대/최소 (OptionalInt)
        System.out.println("\n[4] max / min (최대 / 최소)");
        OptionalInt max = scores.stream().mapToInt(Integer::intValue).max();
        OptionalInt min = scores.stream().mapToInt(Integer::intValue).min();
        System.out.println("최대: " + max.orElse(0) + ", 최소: " + min.orElse(0));

        // [5] summaryStatistics: 한 번에 통계 계산
        System.out.println("\n[5] IntStream 통계 (summaryStatistics)");
        IntSummaryStatistics stats = scores.stream()
                .mapToInt(Integer::intValue)
                .summaryStatistics();
        System.out.println("개수: " + stats.getCount());
        System.out.println("합계: " + stats.getSum());
        System.out.println("평균: " + stats.getAverage());
        System.out.println("최대: " + stats.getMax());
        System.out.println("최소: " + stats.getMin());

        System.out.println("\n프로그램 정상 종료");
    }
}
