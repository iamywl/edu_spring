package com.edu.concurrency;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;

/**
 * CS 운영체제 트랙 (7) - 원자적 연산: CAS 를 손으로 돌려 보고, LongAdder 까지 비교한다
 *
 * SynchronizationDemo 에서 "AtomicLong 을 쓰면 정답이 나온다"까지 봤다.
 * 이 데모는 그 속을 연다: incrementAndGet() 내부의 CAS 재시도 루프를 '직접' 구현해 본다.
 *
 *   CAS(Compare-And-Swap): CPU가 제공하는 원자 명령.
 *     "메모리 값이 아직 기대값 V면 새값으로 바꿔라. 그 사이 누가 바꿨으면 실패를 알려라."
 *     실패하면? 최신 값을 다시 읽고 재시도한다(락 없이! = lock-free).
 *
 * 보여주는 것 네 가지:
 *   ① int++ 경쟁: 보호 없는 증가가 값을 잃는다 (문제 재확인)
 *   ② AtomicInteger: 같은 조건에서 항상 정답 (라이브러리의 CAS)
 *   ③ 손으로 만든 CAS 루프: compareAndSet 으로 직접 구현 + '재시도가 몇 번 있었는지' 관찰
 *   ④ AtomicLong vs LongAdder 처리량 비교: 경합이 심하면 LongAdder 가 빠른 이유
 *
 * LongAdder 는 왜 빠른가?
 *   AtomicLong 은 변수 '하나'를 모두가 CAS 하므로 경합이 심하면 재시도가 폭증한다.
 *   LongAdder 는 내부에 셀(cell)을 여러 개 두고 스레드를 분산시킨 뒤, sum() 때 합산한다.
 *   → "쓰기는 잦고 읽기는 가끔"인 통계 카운터(요청 수 집계 등)에 최적.
 *
 * ※ 소요 시간 비교는 엄밀한 벤치마크(JMH)가 아니라 '감을 잡는' 간이 측정이다.
 *   JIT 워밍업/머신 상태에 따라 수치는 흔들리지만, 경향(LongAdder 우세)은 대개 재현된다.
 */
public class AtomicCasDemo {

    static final int THREADS = 8;
    static final int INCREMENTS = 100_000;

    static int plainCounter = 0;                                   // ① 보호 없음
    static final AtomicInteger atomicCounter = new AtomicInteger(); // ② 라이브러리 CAS
    static final AtomicInteger casCounter = new AtomicInteger();    // ③ 손으로 CAS
    static final AtomicLong casRetries = new AtomicLong();          //    재시도 횟수 관찰용

    public static void main(String[] args) throws Exception {
        System.out.println("=================================================");
        System.out.println(" CS(7) 원자적 연산: CAS 직접 구현과 LongAdder");
        System.out.println("=================================================\n");

        long expected = (long) THREADS * INCREMENTS;
        System.out.printf("설정: 스레드 %d개 × 각 %,d회 증가.  정답 = %,d%n%n", THREADS, INCREMENTS, expected);

        // ① 보호 없는 int++ : 값이 샌다 (read-modify-write 3단계가 쪼개지므로)
        runConcurrently(() -> plainCounter++);
        System.out.printf("── 1. 보호 없는 int++          결과 = %,d  %s%n",
                plainCounter,
                plainCounter == expected ? "(운 좋게 맞음 - 다시 실행해 보라)" : "← 틀림! " + (expected - plainCounter) + " 증발");

        // ② AtomicInteger.incrementAndGet(): 내부가 CAS 재시도 루프
        runConcurrently(atomicCounter::incrementAndGet);
        System.out.printf("── 2. AtomicInteger            결과 = %,d  %s%n",
                atomicCounter.get(), atomicCounter.get() == expected ? "← 정답! (라이브러리 CAS)" : "?!");

        // ③ CAS 루프를 손으로 구현: incrementAndGet() 이 속에서 하는 일 그대로
        runConcurrently(AtomicCasDemo::casIncrement);
        System.out.printf("── 3. 손으로 만든 CAS 루프      결과 = %,d  %s%n",
                casCounter.get(), casCounter.get() == expected ? "← 정답!" : "?!");
        System.out.printf("     CAS 실패→재시도 횟수 = %,d회  (총 시도 %,d회 중)%n",
                casRetries.get(), expected + casRetries.get());
        System.out.println("     ▶ 재시도가 0이 아니다 = 실제로 스레드끼리 '부딪혀서' CAS가 실패했다는 증거.");
        System.out.println("       실패해도 값을 잃지 않고 최신 값으로 다시 시도하므로 최종 결과는 정확하다.\n");

        // ④ AtomicLong vs LongAdder 처리량 간이 비교
        compareThroughput();

        System.out.println("\n결론:");
        System.out.println("  - CAS = \"아직 그 값이면 바꿔라\" 하드웨어 원자 명령. 실패하면 재시도(lock-free).");
        System.out.println("  - AtomicInteger.incrementAndGet() 은 방금 손으로 짠 CAS 루프와 같은 원리다.");
        System.out.println("  - 경합이 심한 집계 카운터라면 LongAdder(셀 분산)가 AtomicLong 보다 유리하다.");
    }

    /**
     * incrementAndGet() 을 손으로 구현한 것.
     * compareAndSet(기대값, 새값): 현재값 == 기대값이면 바꾸고 true, 아니면 false(재시도).
     */
    static void casIncrement() {
        while (true) {
            int current = casCounter.get();                 // 1) 현재값 읽기
            int next = current + 1;                         // 2) 새값 계산
            if (casCounter.compareAndSet(current, next)) {  // 3) "아직 current면 next로" 원자 시도
                return;                                     //    성공 → 끝
            }
            casRetries.incrementAndGet();                   //    실패 → 그 사이 누가 바꿈. 재시도!
        }
    }

    /** AtomicLong 과 LongAdder 로 같은 양(스레드 8 × 500,000회)을 증가시키고 소요 시간을 잰다. */
    static void compareThroughput() throws InterruptedException {
        final int perThread = 500_000;
        long expected = (long) THREADS * perThread;
        System.out.printf("── 4. 처리량 간이 비교 (스레드 %d개 × %,d회 = 총 %,d회 증가) ──%n",
                THREADS, perThread, expected);

        // (a) AtomicLong: 모두가 '한 변수'를 CAS → 경합 시 재시도 폭증
        AtomicLong atomicLong = new AtomicLong();
        long atomicMs = timeConcurrently(perThread, atomicLong::incrementAndGet);
        System.out.printf("     AtomicLong : %,4d ms  (결과 %,d %s)%n",
                atomicMs, atomicLong.get(), atomicLong.get() == expected ? "정확" : "?!");

        // (b) LongAdder: 셀 여러 개에 분산해서 더하고, sum() 때 합산 → 경합 완화
        LongAdder adder = new LongAdder();
        long adderMs = timeConcurrently(perThread, adder::increment);
        System.out.printf("     LongAdder  : %,4d ms  (결과 %,d %s)%n",
                adderMs, adder.sum(), adder.sum() == expected ? "정확" : "?!");

        System.out.println("     ▶ 해석: 둘 다 '항상 정확'하다. 차이는 속도뿐.");
        System.out.println("       LongAdder가 비슷하거나 빠르게 나오는 것이 보통이다(경합 분산 효과).");
        System.out.println("       간이 측정이라 역전될 수도 있다 — 수치보다 '경합 분산'이라는 원리가 중요.");
    }

    /** 주어진 작업을 THREADS개 스레드가 perThread번씩 동시에 실행하고 소요 시간(ms)을 반환. */
    static long timeConcurrently(int perThread, Runnable increment) throws InterruptedException {
        CountDownLatch startGun = new CountDownLatch(1);
        CountDownLatch finishLine = new CountDownLatch(THREADS);
        ExecutorService pool = Executors.newFixedThreadPool(THREADS);
        for (int t = 0; t < THREADS; t++) {
            pool.submit(() -> {
                try {
                    startGun.await();
                    for (int i = 0; i < perThread; i++) {
                        increment.run();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    finishLine.countDown();
                }
            });
        }
        long start = System.nanoTime();
        startGun.countDown();                       // 출발 총성: 모두 동시에 시작 (경합 극대화)
        finishLine.await(20, TimeUnit.SECONDS);
        long elapsedMs = (System.nanoTime() - start) / 1_000_000;
        pool.shutdown();
        pool.awaitTermination(5, TimeUnit.SECONDS);
        return elapsedMs;
    }

    /** ①~③에서 쓰는 공용 실행기: THREADS개 스레드 × INCREMENTS회. */
    static void runConcurrently(Runnable increment) throws InterruptedException {
        timeConcurrently(INCREMENTS, increment);
    }
}
