package com.edu.javabook.ch14;

/**
 * 14.6 스레드 동기화
 *
 * [경쟁 상태(Race Condition)]
 * - 여러 스레드가 하나의 공유 자원(변수)을 동시에 수정하면 결과가 꼬일 수 있다.
 * - count++ 은 "읽기 → 1 증가 → 쓰기" 3단계라 원자적(atomic)이지 않다.
 *   두 스레드가 동시에 읽으면 한쪽의 증가가 사라지는 "갱신 손실"이 발생한다.
 *
 * [해결: synchronized]
 * - synchronized 로 임계 영역(critical section)을 보호하면,
 *   한 번에 하나의 스레드만 그 코드를 실행할 수 있어(락 획득) 갱신 손실이 사라진다.
 *
 * 이 소절에서는 동기화 없이 → 값이 틀리는 현상, 동기화 후 → 정확한 값을 짧게 비교한다.
 */
public class ThreadSync {

    // 동기화 없는 카운터 (경쟁 상태 발생 가능)
    static class UnsafeCounter {
        int count = 0;
        void increment() { count++; }           // 원자적이지 않음
    }

    // synchronized 로 보호한 카운터
    static class SafeCounter {
        int count = 0;
        synchronized void increment() { count++; }   // 한 번에 한 스레드만 실행
    }

    static final int THREADS = 4;
    static final int LOOPS = 50_000;   // 4 * 50000 = 200000 이 정답

    public static void main(String[] args) throws InterruptedException {

        System.out.println("=== 14.6 스레드 동기화 ===");
        int expected = THREADS * LOOPS;
        System.out.println("기대값(정답): " + expected);

        // [1] 동기화 없음 → 경쟁 상태로 값이 틀릴 수 있음
        System.out.println("\n[1] 동기화 없음 (경쟁 상태)");
        UnsafeCounter unsafe = new UnsafeCounter();
        runAll(() -> {
            for (int i = 0; i < LOOPS; i++) unsafe.increment();
        });
        System.out.println("결과: " + unsafe.count
                + (unsafe.count == expected ? " (우연히 맞음)" : " → 정답보다 작음(갱신 손실 발생)"));

        // [2] synchronized 적용 → 항상 정확
        System.out.println("\n[2] synchronized 적용 (동기화)");
        SafeCounter safe = new SafeCounter();
        runAll(() -> {
            for (int i = 0; i < LOOPS; i++) safe.increment();
        });
        System.out.println("결과: " + safe.count
                + (safe.count == expected ? " (정답과 일치)" : " (불일치)"));

        System.out.println("\n프로그램 정상 종료");
    }

    // 동일 작업을 THREADS 개의 스레드로 실행하고 모두 join 으로 기다린다(hang 방지)
    static void runAll(Runnable task) throws InterruptedException {
        Thread[] threads = new Thread[THREADS];
        for (int i = 0; i < THREADS; i++) {
            threads[i] = new Thread(task);
            threads[i].start();
        }
        for (Thread t : threads) {
            t.join();   // 모든 스레드 종료 대기
        }
    }
}
