package com.edu.javabook.ch17;

import java.util.stream.IntStream;
import java.util.stream.LongStream;

/**
 * 17.13 병렬 처리
 *
 * [병렬 스트림(parallel stream)]
 * - parallel() / parallelStream() 으로 여러 스레드가 요소를 나눠 동시에 처리한다.
 * - 내부적으로 ForkJoinPool 을 사용해 작업을 분할/병합한다.
 *
 * [언제 이득인가]
 * - 데이터가 매우 많고, 각 요소 처리 비용이 클 때.
 * - 요소 처리 순서가 결과에 영향을 주지 않을 때(예: 합계).
 *
 * [언제 위험한가]
 * - 데이터가 적으면 스레드 분할/병합 비용이 더 커서 오히려 느리다.
 * - 공유 상태를 수정하면 스레드 경합/오류가 발생한다(가변 상태 공유 금지).
 * - 순서가 중요한 작업(정렬 출력 등)에서는 결과 순서가 뒤섞일 수 있다.
 *
 * 이 소절에서는 순차/병렬 합계를 간단히 측정 비교하고 주의점을 출력한다.
 */
public class ParallelStream {

    public static void main(String[] args) {

        System.out.println("=== 17.13 병렬 처리 ===");

        long n = 50_000_000L; // 충분히 큰 데이터 (병렬 이득 관찰용)

        // [1] 순차 스트림 합계 측정
        System.out.println("\n[1] 순차 스트림 합계");
        long startSeq = System.nanoTime();
        long seqSum = LongStream.rangeClosed(1, n).sum();
        long seqMs = (System.nanoTime() - startSeq) / 1_000_000;
        System.out.println("합계: " + seqSum + " (소요 " + seqMs + "ms)");

        // [2] 병렬 스트림 합계 측정
        System.out.println("\n[2] 병렬 스트림 합계 (parallel)");
        long startPar = System.nanoTime();
        long parSum = LongStream.rangeClosed(1, n).parallel().sum();
        long parMs = (System.nanoTime() - startPar) / 1_000_000;
        System.out.println("합계: " + parSum + " (소요 " + parMs + "ms)");
        System.out.println("두 합계 동일 여부: " + (seqSum == parSum));

        // [3] 병렬이 항상 빠른 것은 아님: 작은 데이터에서는 오히려 손해
        System.out.println("\n[3] 작은 데이터에서는 병렬이 손해일 수 있음");
        int smallSum = IntStream.rangeClosed(1, 10).parallel().sum();
        System.out.println("작은 데이터(1~10) 병렬 합계: " + smallSum + " (분할/병합 비용이 더 큼)");

        // [4] 주의점 요약
        System.out.println("\n[4] 병렬 처리 주의점");
        System.out.println("- 이득: 데이터가 크고 요소 처리 비용이 크며 순서 무관할 때");
        System.out.println("- 위험: 데이터가 적을 때, 가변 공유 상태를 수정할 때, 순서가 중요할 때");
        System.out.println("(측정값은 실행 환경/코어 수에 따라 달라질 수 있다.)");

        System.out.println("\n프로그램 정상 종료");
    }
}
