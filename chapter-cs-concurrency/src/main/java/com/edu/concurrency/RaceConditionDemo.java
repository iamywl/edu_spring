package com.edu.concurrency;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * CS 운영체제 트랙 (1) - 경쟁 상태(Race Condition)와 "잃어버린 갱신(Lost Update)"
 *
 * "왜?" count++ 는 한 줄인데, 여러 스레드가 동시에 돌리면 왜 숫자가 모자라는가?
 *
 * 핵심 주장:
 *   - N개의 스레드가 각각 공유 변수를 M번씩 증가시키면,
 *     기대값은 N * M 이다. 그런데 실제 결과는 그보다 "작다".
 *   - 왜냐하면 count++ 는 원자적(atomic)이지 않기 때문이다.
 *     바이트코드 수준에서 count++ 는 세 단계다:
 *         ① 읽기 (read)   : 필드 값을 operand stack 으로 (getfield / iload)
 *         ② 더하기 (modify): 스택 위에서 +1        (iadd)
 *         ③ 쓰기 (write)  : 결과를 다시 필드로     (putfield / istore)
 *     (JAVA_개념서 §1.5 "operand stack: 계산은 스택 위에서 벌어진다" 와 같은 그림이다.)
 *
 *   이 세 단계 "사이"에 다른 스레드가 끼어들면 갱신이 사라진다.
 *
 *     Thread A: read count(=100) ─────────────┐          write 101
 *     Thread B:        read count(=100) ─ +1 ─ write 101  (A의 결과를 덮어씀!)
 *                                        └ 두 번 더했는데 결과는 +1. → 하나가 증발했다.
 *
 * 실행할 때마다 결과가 "다르게" 나오는 것도 관찰 포인트다(비결정성).
 * OS 스케줄러가 스레드를 언제 멈추고 다른 스레드로 바꿀지(문맥 교환) 매번 다르기 때문이다.
 */
public class RaceConditionDemo {

    // 여러 스레드가 "공유"하는 상태. 이 필드 하나를 모두가 건드린다.
    // (참고: 지역 변수였다면 각 스레드의 스택에 따로 있어서 절대 안 섞인다.
    //  섞이는 것은 오직 공유되는 필드/힙의 데이터뿐이다. → SPRING_개념서 §2.7 과 동일한 이야기)
    static long unsafeCounter = 0;

    // 대조군: 이 필드는 딱 하나의 스레드만 건드린다(경쟁 없음).
    static long singleThreadCounter = 0;

    static final int THREADS = 8;          // 동시에 달리는 스레드 수
    static final int INCREMENTS = 100_000; // 각 스레드가 증가시키는 횟수

    public static void main(String[] args) throws Exception {
        System.out.println("=================================================");
        System.out.println(" CS(1) 경쟁 상태: 잃어버린 갱신을 눈으로 보기");
        System.out.println("=================================================\n");

        explainBytecode();

        long expected = (long) THREADS * INCREMENTS;
        System.out.println("설정: 스레드 " + THREADS + "개 × 각 " + INCREMENTS + "회 증가");
        System.out.println("기대값(정답) = " + THREADS + " × " + INCREMENTS + " = " + expected);
        System.out.println();

        // 같은 실험을 5번 반복 → 매번 다른 (그리고 대부분 기대값보다 작은) 값이 나온다.
        System.out.println("── 위험한 카운터(동기화 없음)를 5번 실행 ──");
        for (int run = 1; run <= 5; run++) {
            long result = runUnsafe();
            long lost = expected - result;
            System.out.printf("  실행 %d: 결과 = %,d  (정답보다 %,d 만큼 증발!)  %s%n",
                    run, result, lost, lost == 0 ? "" : "← 잃어버린 갱신");
        }

        System.out.println("\n관찰:");
        System.out.println("  - 결과가 기대값보다 작다 → 갱신이 사라졌다(lost update).");
        System.out.println("  - 매번 값이 다르다 → 비결정적(nondeterministic).");
        System.out.println("    OS 스케줄러가 문맥 교환(context switch) 타이밍을 매번 다르게 잡기 때문.");

        // 대조군: 한 스레드만 돌리면 경쟁이 없으므로 항상 정확하다.
        System.out.println("\n── 대조군: 단일 스레드로 같은 총량 증가 ──");
        singleThreadCounter = 0;
        for (long i = 0; i < expected; i++) {
            singleThreadCounter++;
        }
        System.out.printf("  단일 스레드 결과 = %,d  (항상 정답, 경쟁 자체가 없음)%n", singleThreadCounter);

        System.out.println("\n결론: count++ 는 원자적이지 않다. 공유 상태 + 동시성 = 경쟁 상태.");
        System.out.println("      해결책은 SynchronizationDemo 에서 세 가지 방법으로 본다.");
    }

    /** 위험한 카운터를 THREADS개 스레드로 동시에 증가시키고 최종값을 돌려준다. */
    static long runUnsafe() throws InterruptedException {
        unsafeCounter = 0;

        // CountDownLatch: 모든 스레드를 "동시에" 출발시키기 위한 출발 신호총.
        // 이래야 경쟁이 최대로 벌어져 현상이 잘 보인다.
        CountDownLatch startGun = new CountDownLatch(1);
        CountDownLatch finishLine = new CountDownLatch(THREADS);

        ExecutorService pool = Executors.newFixedThreadPool(THREADS);
        for (int t = 0; t < THREADS; t++) {
            pool.submit(() -> {
                try {
                    startGun.await(); // 출발 신호를 기다린다
                    for (int i = 0; i < INCREMENTS; i++) {
                        // 바로 이 한 줄이 read-modify-write 3단계라서 위험하다.
                        unsafeCounter++;
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    finishLine.countDown();
                }
            });
        }

        startGun.countDown();               // 탕! 모두 동시 출발
        finishLine.await(10, TimeUnit.SECONDS); // 다 끝날 때까지 대기(최대 10초 안전장치)
        pool.shutdown();
        pool.awaitTermination(5, TimeUnit.SECONDS);
        return unsafeCounter;
    }

    static void explainBytecode() {
        System.out.println("── count++ 의 정체(바이트코드 3단계) ──");
        System.out.println("   count++ 는 한 줄처럼 보이지만 실제로는:");
        System.out.println("     ① read   : 현재 값을 읽어온다");
        System.out.println("     ② modify : +1 한다");
        System.out.println("     ③ write  : 다시 저장한다");
        System.out.println("   ①과 ③ 사이에 다른 스레드가 끼어들면 → 갱신이 덮어써진다.\n");
    }
}
