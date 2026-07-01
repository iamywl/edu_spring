package com.edu.concurrency;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

/**
 * CS 운영체제 트랙 (2) - 동기화(Synchronization): 경쟁 상태를 고치는 세 가지 방법
 *
 * RaceConditionDemo 에서 본 "잃어버린 갱신"을 고친다. 같은 카운터를 세 방법으로 보호한다:
 *
 *   ① synchronized  : 자바가 내장한 "모니터 락". 임계 구역(critical section)을 잠근다.
 *                      한 번에 한 스레드만 read-modify-write 3단계를 끝까지 마칠 수 있다.
 *   ② AtomicLong    : CAS(Compare-And-Swap)라는 하드웨어 원자 명령을 사용.
 *                      락 없이(lock-free) "값이 그대로면 바꾸고, 아니면 다시 시도".
 *   ③ ReentrantLock : java.util.concurrent 의 명시적 락. lock()/unlock() 을 직접 호출.
 *                      synchronized 와 같은 효과지만 tryLock/타임아웃/공정성 등 기능이 많다.
 *
 * 추가로 아주 중요한 반례를 보여준다:
 *   ④ volatile 만으로는 count++ 를 고치지 못한다!
 *      volatile 은 "가시성(visibility)"을 보장할 뿐 "원자성(atomicity)"을 보장하지 않는다.
 *      즉 최신 값을 "보게"는 해주지만, read-modify-write 3단계가 쪼개지는 것은 못 막는다.
 *
 * 세 방법 모두 항상 정확한 값(= 스레드수 × 반복수)을 낸다는 것을 출력으로 증명한다.
 */
public class SynchronizationDemo {

    static final int THREADS = 8;
    static final int INCREMENTS = 100_000;

    // ── 각 방법이 쓰는 공유 상태들 ──
    static long syncCounter = 0;                          // ① synchronized 로 보호
    static final Object lock = new Object();              //    그때 쓸 모니터 객체
    static final AtomicLong atomicCounter = new AtomicLong(0); // ② CAS 기반
    static long reentrantCounter = 0;                    // ③ ReentrantLock 으로 보호
    static final ReentrantLock reentrantLock = new ReentrantLock();
    static volatile long volatileCounter = 0;            // ④ volatile "만" (일부러 실패시킬 것)

    public static void main(String[] args) throws Exception {
        System.out.println("=================================================");
        System.out.println(" CS(2) 동기화: 경쟁 상태를 고치는 세 가지 방법");
        System.out.println("=================================================\n");

        long expected = (long) THREADS * INCREMENTS;
        System.out.printf("설정: 스레드 %d개 × 각 %,d회.  정답 = %,d%n%n", THREADS, INCREMENTS, expected);

        // ① synchronized
        syncCounter = 0;
        runConcurrently(() -> {
            // synchronized 블록 = 모니터 락 획득/해제. 한 번에 한 스레드만 이 안에 들어온다.
            synchronized (lock) {
                syncCounter++;
            }
        });
        report("① synchronized (모니터 락)", syncCounter, expected);

        // ② AtomicLong (CAS)
        atomicCounter.set(0);
        runConcurrently(() -> {
            // incrementAndGet() 내부는 CAS 루프다:
            //   "현재값이 아직 v면 v+1로 바꿔라. 그 사이 누가 바꿨으면 실패 → 새 값 읽고 재시도."
            atomicCounter.incrementAndGet();
        });
        report("② AtomicLong (CAS, lock-free)", atomicCounter.get(), expected);

        // ③ ReentrantLock
        reentrantCounter = 0;
        runConcurrently(() -> {
            reentrantLock.lock();      // 락 획득
            try {
                reentrantCounter++;    // 임계 구역
            } finally {
                reentrantLock.unlock(); // 반드시 finally 에서 해제 (예외 나도 풀리도록)
            }
        });
        report("③ ReentrantLock (명시적 락)", reentrantCounter, expected);

        // ④ 반례: volatile 만으로는 실패한다
        System.out.println("\n── 반례: volatile 만으로는 못 고친다 (가시성 ≠ 원자성) ──");
        volatileCounter = 0;
        runConcurrently(() -> {
            // volatile 은 "항상 최신 값을 읽게" 해주지만,
            // ++(읽기→더하기→쓰기) 3단계가 통째로 원자적이 되진 않는다. 그래서 여전히 샌다.
            volatileCounter++;
        });
        long lost = expected - volatileCounter;
        System.out.printf("  volatile 결과 = %,d  (정답 %,d, %,d 증발)  %s%n",
                volatileCounter, expected, lost,
                volatileCounter == expected ? "?! (운 좋게 맞음)" : "← 여전히 틀림!");
        System.out.println("  교훈: volatile 은 '보이게' 할 뿐, '쪼개지지 않게' 하지는 못한다.");
        System.out.println("        원자성이 필요하면 synchronized / Atomic / Lock 을 써야 한다.");

        System.out.println("\n결론:");
        System.out.println("  - synchronized / Atomic / ReentrantLock → 셋 다 항상 정답.");
        System.out.println("  - 각자 트레이드오프: synchronized(가장 단순), Atomic(단일 변수엔 가장 빠름),");
        System.out.println("    ReentrantLock(기능 풍부: tryLock/타임아웃/공정성).");
    }

    /** 주어진 작업을 각 스레드가 INCREMENTS 번씩, THREADS개 스레드로 동시에 실행한다. */
    static void runConcurrently(Runnable increment) throws InterruptedException {
        CountDownLatch startGun = new CountDownLatch(1);
        CountDownLatch finishLine = new CountDownLatch(THREADS);
        ExecutorService pool = Executors.newFixedThreadPool(THREADS);

        for (int t = 0; t < THREADS; t++) {
            pool.submit(() -> {
                try {
                    startGun.await();
                    for (int i = 0; i < INCREMENTS; i++) {
                        increment.run();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    finishLine.countDown();
                }
            });
        }

        startGun.countDown();
        finishLine.await(10, TimeUnit.SECONDS);
        pool.shutdown();
        pool.awaitTermination(5, TimeUnit.SECONDS);
    }

    static void report(String name, long got, long expected) {
        System.out.printf("  %-32s 결과 = %,d  → %s%n",
                name, got, got == expected ? "정답! ✔" : "틀림(" + (expected - got) + " 증발)");
    }
}
