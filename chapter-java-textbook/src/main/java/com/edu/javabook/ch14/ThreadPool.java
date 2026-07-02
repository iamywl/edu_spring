package com.edu.javabook.ch14;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * 14.9 스레드풀
 *
 * [스레드풀이 필요한 이유]
 * - 작업마다 매번 스레드를 새로 만들고 없애면 비용이 크고, 스레드가 폭증할 위험이 있다.
 * - 스레드풀은 미리 만들어 둔 소수의 스레드를 "재사용" 하며 작업(Task)만 계속 받아 처리한다.
 *
 * [ExecutorService 사용 흐름]
 *   1) Executors.newFixedThreadPool(n) 으로 n개 스레드 풀 생성
 *   2) submit(작업) 으로 작업을 제출 → 결과를 담은 Future 를 돌려받음
 *   3) Future.get() 으로 작업 결과를 (완료될 때까지 기다렸다가) 받음
 *   4) shutdown() 으로 새 작업 접수를 막고,
 *      awaitTermination(timeout) 으로 진행 중 작업이 끝날 때까지 "제한 시간 안에서" 기다림
 *
 * - shutdown + awaitTermination 을 반드시 호출해야 풀의 스레드가 정리되어 프로그램이 정상 종료된다.
 *   (호출하지 않으면 풀 스레드가 남아 hang 될 수 있다.)
 */
public class ThreadPool {

    public static void main(String[] args) throws InterruptedException {

        System.out.println("=== 14.9 스레드풀 ===");

        // [1] 고정 크기 3짜리 스레드풀 생성
        System.out.println("\n[1] newFixedThreadPool(3) 생성");
        ExecutorService pool = Executors.newFixedThreadPool(3);

        // [2] submit + Future : 1~5의 제곱을 계산하는 작업 5개 제출
        System.out.println("\n[2] 작업 5개 submit, Future 로 결과 수집");
        List<Future<Integer>> futures = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            final int n = i;
            Future<Integer> f = pool.submit(() -> {
                int result = n * n;
                System.out.println("  " + Thread.currentThread().getName()
                        + " 가 " + n + "의 제곱 계산 = " + result);
                return result;
            });
            futures.add(f);
        }

        // [3] Future.get() 으로 결과 회수 (각 작업이 끝날 때까지 대기)
        System.out.println("\n[3] Future.get() 으로 결과 회수");
        int sum = 0;
        try {
            for (Future<Integer> f : futures) {
                sum += f.get();   // 결과가 준비될 때까지 대기 후 반환
            }
        } catch (Exception e) {
            System.out.println("작업 처리 중 예외: " + e.getMessage());
        }
        System.out.println("제곱들의 합(1+4+9+16+25): " + sum);

        // [4] shutdown + awaitTermination 으로 안전 종료
        System.out.println("\n[4] shutdown / awaitTermination 으로 풀 정리");
        pool.shutdown();   // 새 작업 접수 중단
        boolean finished = pool.awaitTermination(5, TimeUnit.SECONDS);  // 최대 5초 대기(hang 방지)
        System.out.println("제한 시간 내 모든 작업 종료? " + finished);
        if (!finished) {
            pool.shutdownNow();   // 혹시 남았다면 강제 종료 요청
        }

        System.out.println("\n프로그램 정상 종료");
    }
}
