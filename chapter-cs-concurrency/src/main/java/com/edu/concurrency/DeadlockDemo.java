package com.edu.concurrency;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * CS 운영체제 트랙 (3) - 교착 상태(Deadlock): 서로가 서로를 기다리다 영원히 멈추기
 *
 * "왜?" 두 스레드가 락을 반대 순서로 잡으면 프로그램이 통째로 얼어붙는가?
 *
 * 시나리오:
 *   - 락 A, 락 B 두 개가 있다.
 *   - Thread-1: A를 먼저 잡고 → B를 잡으려 한다.
 *   - Thread-2: B를 먼저 잡고 → A를 잡으려 한다.
 *
 *     Thread-1 ── 잡음 ──▶ [락 A] ── 원함 ──▶ [락 B] ◀── 잡음 ── Thread-2
 *          ▲                                                       │
 *          └──────────────── 원함 ◀────────────────────────────────┘
 *     서로가 상대가 쥔 락을 기다린다 → 순환 대기(circular wait) → 아무도 못 나아감.
 *
 * 코프만(Coffman)의 네 가지 조건이 동시에 성립하면 교착이 발생한다:
 *   ① 상호 배제(Mutual Exclusion)   : 락은 한 번에 한 스레드만.
 *   ② 점유 대기(Hold and Wait)      : 락 하나 쥔 채 다른 락을 기다림.
 *   ③ 비선점(No Preemption)         : 쥔 락을 강제로 뺏을 수 없음.
 *   ④ 순환 대기(Circular Wait)      : 대기 관계가 원을 이룸.
 * → 넷 중 하나만 깨도 교착은 예방된다. 여기서는 ④를 깬다("락 순서 통일").
 *
 * 주의: 진짜 synchronized 로 교착을 만들면 프로그램이 영원히 멈춰 교육용으로 위험하다.
 *       그래서 tryLock(타임아웃) 기반의 "감시견(watchdog)"으로 교착을 감지하고,
 *       "교착이 발생했다"고 출력한 뒤 안전하게 빠져나온다. 절대 hang 하지 않는다.
 */
public class DeadlockDemo {

    static final ReentrantLock lockA = new ReentrantLock();
    static final ReentrantLock lockB = new ReentrantLock();

    public static void main(String[] args) throws Exception {
        System.out.println("=================================================");
        System.out.println(" CS(3) 교착 상태(Deadlock): 반대 순서 락의 함정");
        System.out.println("=================================================\n");

        demonstrateDeadlock();
        System.out.println();
        demonstrateFix();

        System.out.println("\n결론: 여러 락을 잡아야 한다면 '항상 같은 순서'로 잡아라(락 순서 정하기).");
        System.out.println("      그것만으로 순환 대기(코프만 ④)가 깨져 교착이 원천 봉쇄된다.");
    }

    // ──────────────────────────────────────────────
    // 1. 교착을 일부러 유발 (감시견이 감지 후 탈출)
    // ──────────────────────────────────────────────
    static void demonstrateDeadlock() throws InterruptedException {
        System.out.println("── 1. 교착 유발: 두 스레드가 락을 반대 순서로 잡는다 ──");

        // 두 스레드가 각자 첫 락을 확실히 쥔 뒤에 두 번째 락을 노리도록 동기화.
        // 이래야 교착이 100% 재현된다(비결정성 제거).
        CountDownLatch bothHoldFirst = new CountDownLatch(2);
        // 교착에 실제로 빠졌는지(둘 다 두 번째 락 획득 실패) 표시.
        final boolean[] deadlockHit = { false };

        Thread t1 = new Thread(() -> {
            lockA.lock();
            try {
                System.out.println("  [Thread-1] 락 A 획득. 이제 락 B를 원함...");
                bothHoldFirst.countDown();
                await(bothHoldFirst); // 상대도 자기 첫 락을 쥘 때까지 대기 → 교착 확정
                // tryLock: 무한 대기 대신 3초만 기다린다. 이 안전장치가 hang을 막는다.
                boolean gotB = lockB.tryLock(3, TimeUnit.SECONDS);
                if (gotB) {
                    try { System.out.println("  [Thread-1] 락 B 획득(교착 아님)"); }
                    finally { lockB.unlock(); }
                } else {
                    synchronized (deadlockHit) { deadlockHit[0] = true; }
                    System.out.println("  [Thread-1] 3초 동안 락 B를 못 얻음 → 교착 감지! 포기하고 물러남.");
                }
            } catch (InterruptedException ignored) {
                Thread.currentThread().interrupt();
            } finally {
                lockA.unlock();
            }
        }, "Thread-1");

        Thread t2 = new Thread(() -> {
            lockB.lock();
            try {
                System.out.println("  [Thread-2] 락 B 획득. 이제 락 A를 원함...");
                bothHoldFirst.countDown();
                await(bothHoldFirst);
                boolean gotA = lockA.tryLock(3, TimeUnit.SECONDS);
                if (gotA) {
                    try { System.out.println("  [Thread-2] 락 A 획득(교착 아님)"); }
                    finally { lockA.unlock(); }
                } else {
                    synchronized (deadlockHit) { deadlockHit[0] = true; }
                    System.out.println("  [Thread-2] 3초 동안 락 A를 못 얻음 → 교착 감지! 포기하고 물러남.");
                }
            } catch (InterruptedException ignored) {
                Thread.currentThread().interrupt();
            } finally {
                lockB.unlock();
            }
        }, "Thread-2");

        t1.start();
        t2.start();
        t1.join();
        t2.join();

        System.out.println(deadlockHit[0]
                ? "  ▶ 결과: 교착 상태(DEADLOCK)가 실제로 발생했다. (tryLock 타임아웃으로 탈출)"
                : "  ▶ 결과: 이번엔 운 좋게 교착이 안 났다(드묾). 다시 실행하면 대개 교착이 난다.");
    }

    // ──────────────────────────────────────────────
    // 2. 해결: 락 순서를 통일한다 (항상 A → B)
    // ──────────────────────────────────────────────
    static void demonstrateFix() throws InterruptedException {
        System.out.println("── 2. 해결책: 두 스레드 모두 '락 A → 락 B' 순서로 잡는다 ──");

        Runnable orderedWork = () -> {
            // 순서를 통일하면, 한 스레드가 A를 잡은 순간 다른 스레드는 A에서 막힌다.
            // A를 못 잡았으니 B도 아직 안 건드린다 → 순환 대기가 생길 수 없다.
            lockA.lock();
            try {
                lockB.lock();
                try {
                    System.out.println("  [" + Thread.currentThread().getName()
                            + "] 락 A → 락 B 순서로 둘 다 획득. 작업 후 반납.");
                } finally {
                    lockB.unlock();
                }
            } finally {
                lockA.unlock();
            }
        };

        Thread t1 = new Thread(orderedWork, "Thread-1");
        Thread t2 = new Thread(orderedWork, "Thread-2");
        t1.start();
        t2.start();
        // 안전장치: 5초 안에 안 끝나면 뭔가 잘못된 것.
        t1.join(5000);
        t2.join(5000);
        System.out.println("  ▶ 결과: 교착 없이 두 스레드 모두 정상 완료. (순환 대기 조건이 깨졌다)");
    }

    static void await(CountDownLatch latch) {
        try {
            latch.await(3, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
