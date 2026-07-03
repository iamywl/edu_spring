package com.edu.concurrency;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * CS 운영체제 트랙 (8) - ReentrantLock: synchronized 가 못 하는 것들
 *
 * synchronized 는 단순하고 좋다. 그런데 다음이 필요해지면 한계에 부딪힌다:
 *   - "락을 못 잡으면 기다리지 말고 포기하고 싶다"        → tryLock()
 *   - "정해진 시간만 기다리고 싶다(교착 회피!)"           → tryLock(시간)
 *   - "먼저 기다린 스레드가 먼저 잡게 하고 싶다(기아 방지)" → new ReentrantLock(true) 공정 모드
 *   - "대기 조건을 여러 개로 나누고 싶다"                 → newCondition() (wait/notify는 1개뿐)
 *
 * 이 데모가 보여주는 것 네 가지:
 *   ① 재진입(reentrancy): 같은 스레드가 이미 쥔 락을 또 잡아도 통과한다 (홀드 카운트 관찰)
 *   ② tryLock(타임아웃)으로 교착 회피: 반대 순서로 락 2개를 잡는 고전 교착 상황에서,
 *      "못 잡으면 잡은 것을 내려놓고 재시도" 전략으로 교착 없이 둘 다 완주한다
 *   ③ 공정성(fairness): 공정 락은 대기 순서대로 락을 준다 (비공정은 새치기 허용 = 더 빠름)
 *   ④ Condition 으로 만든 bounded buffer: notFull/notEmpty 두 조건으로 await/signal
 *      (BlockingQueue 가 내부에서 하는 일을 직접 구현해 보는 것)
 *
 * 모든 부분에 타임아웃/래치가 걸려 있어 절대 hang 하지 않는다.
 */
public class ReentrantLockDemo {

    public static void main(String[] args) throws Exception {
        System.out.println("=================================================");
        System.out.println(" CS(8) ReentrantLock: tryLock·공정성·Condition·재진입");
        System.out.println("=================================================\n");

        part1_reentrancy();
        part2_tryLockAvoidsDeadlock();
        part3_fairness();
        part4_conditionBoundedBuffer();

        System.out.println("\n결론:");
        System.out.println("  - 재진입: 같은 스레드는 쥔 락을 또 잡을 수 있다(홀드 카운트). synchronized 도 동일.");
        System.out.println("  - tryLock(시간): '무한 대기' 대신 '포기하고 재시도' → 교착을 구조적으로 회피.");
        System.out.println("  - 공정 모드: 대기 순서 보장(기아 방지) 대신 속도를 조금 내준다.");
        System.out.println("  - Condition: 대기 조건을 여러 개로 나눠 정확한 상대만 깨운다(BlockingQueue 의 속).");
    }

    // ─────────────────────────────────────────────────────
    // 1. 재진입: 이미 쥔 락을 다시 lock() 하면? → 카운트만 올라가고 통과
    // ─────────────────────────────────────────────────────
    static void part1_reentrancy() {
        System.out.println("── 1. 재진입(reentrant): 내가 쥔 락은 나를 막지 않는다 ──");
        ReentrantLock lock = new ReentrantLock();

        lock.lock();   // 1번째 획득
        try {
            System.out.println("  outer(): 락 획득. 홀드 카운트 = " + lock.getHoldCount());
            lock.lock();   // 같은 스레드가 2번째 획득 → 막히지 않고 카운트만 +1
            try {
                System.out.println("  inner(): 같은 락 또 획득! 홀드 카운트 = " + lock.getHoldCount());
                System.out.println("  ▶ 만약 재진입이 안 된다면? 자기가 쥔 락을 자기가 기다리는 '자기 교착'이 된다.");
            } finally {
                lock.unlock(); // 카운트 -1 (아직 안 풀림)
            }
            System.out.println("  inner 반환 후 홀드 카운트 = " + lock.getHoldCount() + " (아직 내가 쥐고 있음)");
        } finally {
            lock.unlock(); // 카운트 0 → 이제 완전히 해제
        }
        System.out.println("  최종 홀드 카운트 = " + lock.getHoldCount() + " (완전 해제. lock 횟수만큼 unlock!)\n");
    }

    // ─────────────────────────────────────────────────────
    // 2. tryLock(타임아웃)으로 교착 회피
    // ─────────────────────────────────────────────────────
    static void part2_tryLockAvoidsDeadlock() throws InterruptedException {
        System.out.println("── 2. tryLock(타임아웃): 교착이 '생길 수 없는' 구조 만들기 ──");
        System.out.println("  상황: 스레드A는 [락1→락2], 스레드B는 [락2→락1] 순서로 잡는다.");
        System.out.println("        lock()이었다면 서로 상대 락을 영원히 기다리는 교착(DeadlockDemo)이 가능.");
        System.out.println("  전략: tryLock(50ms)로 시도 → 못 잡으면 '쥔 것을 내려놓고' 잠깐 쉬었다 재시도.\n");

        ReentrantLock lock1 = new ReentrantLock();
        ReentrantLock lock2 = new ReentrantLock();
        CountDownLatch done = new CountDownLatch(2);

        Runnable taskA = () -> acquireBothWithTryLock("A", lock1, lock2, done); // 1 → 2 순서
        Runnable taskB = () -> acquireBothWithTryLock("B", lock2, lock1, done); // 2 → 1 순서(반대!)

        new Thread(taskA, "thread-A").start();
        new Thread(taskB, "thread-B").start();

        boolean finished = done.await(10, TimeUnit.SECONDS); // 안전장치
        System.out.println(finished
                ? "  ▶ 두 스레드 모두 완주! 반대 순서로 잡았는데도 교착이 없다 — 포기·재시도 덕분."
                : "  ?! 10초 내 미완료 (이례적 상황)");
        System.out.println();
    }

    /** first→second 순서로 두 락을 tryLock 으로 잡는다. 못 잡으면 내려놓고 재시도. */
    static void acquireBothWithTryLock(String name, ReentrantLock first, ReentrantLock second,
                                       CountDownLatch done) {
        int attempts = 0;
        try {
            while (true) {
                attempts++;
                if (first.tryLock(50, TimeUnit.MILLISECONDS)) {        // 1단계: 첫 락 시도
                    try {
                        if (second.tryLock(50, TimeUnit.MILLISECONDS)) { // 2단계: 둘째 락 시도
                            try {
                                System.out.printf("  [%s] 시도 %d번 만에 두 락 모두 획득! 작업 수행 중...%n",
                                        name, attempts);
                                Thread.sleep(100); // 두 자원을 쓰는 작업 흉내
                                return;            // 완료 (finally 들이 역순으로 락을 풀어준다)
                            } finally {
                                second.unlock();
                            }
                        }
                        // 둘째 락 실패 → 첫 락을 '내려놓는' 것이 핵심! (쥔 채 버티면 교착 위험)
                        System.out.printf("  [%s] 둘째 락 실패 → 쥔 락을 내려놓고 재시도 (교착 예방)%n", name);
                    } finally {
                        if (first.isHeldByCurrentThread()) {
                            first.unlock();
                        }
                    }
                }
                // 잠깐 무작위로 쉬어 '서로 계속 부딪히는' 라이브락(livelock)도 피한다.
                Thread.sleep(ThreadLocalRandom.current().nextInt(10, 30));
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            done.countDown();
        }
    }

    // ─────────────────────────────────────────────────────
    // 3. 공정성: 공정 락은 '줄 선 순서대로' 준다
    // ─────────────────────────────────────────────────────
    static void part3_fairness() throws InterruptedException {
        System.out.println("── 3. 공정성(fairness): new ReentrantLock(true) ──");
        System.out.println("  공정 락: 가장 오래 기다린 스레드부터. 기아(starvation) 방지, 대신 느림.");
        System.out.println("  비공정(기본): 락이 풀리는 순간 도착한 스레드가 새치기 가능. 더 빠름.\n");

        ReentrantLock fairLock = new ReentrantLock(true); // true = 공정 모드
        CountDownLatch ready = new CountDownLatch(4);
        CountDownLatch go = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(4);

        // 메인이 락을 쥔 동안 워커 4개가 순서대로 줄을 서게 만든다.
        fairLock.lock();
        try {
            for (int i = 1; i <= 4; i++) {
                final int id = i;
                new Thread(() -> {
                    ready.countDown();
                    try {
                        go.await();               // 출발 신호 대기
                        fairLock.lock();          // 여기서 줄을 선다 (공정: 선착순 보장)
                        try {
                            System.out.printf("  워커%d 락 획득 (isFair=%s, 대기자 %d명 남음)%n",
                                    id, fairLock.isFair(), fairLock.getQueueLength());
                            Thread.sleep(30);
                        } finally {
                            fairLock.unlock();
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    } finally {
                        done.countDown();
                    }
                }, "fair-worker-" + i).start();
                // 각 워커가 '순서대로' 대기열에 들어가도록 시차를 두고 출발시킨다.
            }
            ready.await();
            go.countDown();
            Thread.sleep(200); // 4개가 모두 대기열에 줄을 설 시간
            System.out.println("  메인이 락을 놓는다 → 공정 모드라 '줄 선 순서대로' 가져간다:");
        } finally {
            fairLock.unlock();
        }

        done.await(10, TimeUnit.SECONDS);
        System.out.println("  ▶ 공정 락은 대기열(FIFO) 순서 보장. 단, 매번 줄을 확인하는 비용 때문에");
        System.out.println("    처리량은 비공정보다 낮다. 그래서 기본값은 비공정(false)이다.\n");
    }

    // ─────────────────────────────────────────────────────
    // 4. Condition: await/signal 로 만드는 bounded buffer (크기 3)
    // ─────────────────────────────────────────────────────
    static void part4_conditionBoundedBuffer() throws InterruptedException {
        System.out.println("── 4. Condition: 손으로 만드는 bounded buffer (크기 3) ──");
        System.out.println("  notFull  조건: 버퍼가 차면 생산자가 여기서 await, 소비자가 꺼낸 뒤 signal");
        System.out.println("  notEmpty 조건: 버퍼가 비면 소비자가 여기서 await, 생산자가 넣은 뒤 signal");
        System.out.println("  (BlockingQueue 내부가 정확히 이 구조다. wait/notify 는 조건이 1개뿐이라 불편)\n");

        BoundedBuffer buffer = new BoundedBuffer(3);
        final int TOTAL = 8;
        CountDownLatch done = new CountDownLatch(2);

        Thread producer = new Thread(() -> {
            try {
                for (int i = 1; i <= TOTAL; i++) {
                    buffer.put(i);        // 가득 차면 notFull.await() 로 잠든다
                    Thread.sleep(20);     // 빠른 생산자
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                done.countDown();
            }
        }, "producer");

        Thread consumer = new Thread(() -> {
            try {
                for (int i = 1; i <= TOTAL; i++) {
                    int item = buffer.take(); // 비면 notEmpty.await() 로 잠든다
                    Thread.sleep(80);         // 느린 소비자 → 버퍼가 자주 가득 찬다
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                done.countDown();
            }
        }, "consumer");

        producer.start();
        consumer.start();
        done.await(15, TimeUnit.SECONDS);
        System.out.println("  ▶ '버퍼 가득! 생산자 await' 후에 소비자의 take → signal 로 깨어난 것을 볼 수 있다.");
    }

    /** ReentrantLock + Condition 두 개로 만든 고전 bounded buffer. */
    static class BoundedBuffer {
        private final Deque<Integer> items = new ArrayDeque<>();
        private final int capacity;
        private final ReentrantLock lock = new ReentrantLock();
        private final Condition notFull = lock.newCondition();   // "자리가 났다"
        private final Condition notEmpty = lock.newCondition();  // "물건이 생겼다"

        BoundedBuffer(int capacity) {
            this.capacity = capacity;
        }

        void put(int item) throws InterruptedException {
            lock.lock();
            try {
                while (items.size() == capacity) {  // if 가 아니라 while! (깨어나면 조건 재확인)
                    System.out.printf("  [생산자] 버퍼 가득(%d/%d) → notFull.await() 로 잠듦%n",
                            items.size(), capacity);
                    notFull.await();                // 락을 '반납하고' 잠든다 (핵심!)
                }
                items.addLast(item);
                System.out.printf("  [생산자] %d 넣음  (버퍼 %d/%d)%n", item, items.size(), capacity);
                notEmpty.signal();                  // 잠들어 있을지 모를 소비자를 깨운다
            } finally {
                lock.unlock();
            }
        }

        int take() throws InterruptedException {
            lock.lock();
            try {
                while (items.isEmpty()) {
                    System.out.println("  [소비자] 버퍼 빔 → notEmpty.await() 로 잠듦");
                    notEmpty.await();
                }
                int item = items.removeFirst();
                System.out.printf("  [소비자] %d 꺼냄  (버퍼 %d/%d)%n", item, items.size(), capacity);
                notFull.signal();                   // 잠들어 있을지 모를 생산자를 깨운다
                return item;
            } finally {
                lock.unlock();
            }
        }
    }
}
