package com.edu.concurrency;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * CS 운영체제 트랙 (5) - 생산자-소비자(Producer-Consumer)와 BlockingQueue
 *
 * "왜?" 데이터를 만드는 쪽(생산자)과 쓰는 쪽(소비자)의 속도가 다를 때, 어떻게 안전하게 넘길까?
 *
 *   - 공유 버퍼(큐)를 사이에 두고 넘긴다. 하지만 직접 락을 걸어 큐를 관리하면 복잡하고 버그가 잦다.
 *   - BlockingQueue 는 이 패턴을 위해 만들어진 도구다. 두 가지를 자동으로 해준다:
 *       ① 스레드 안전(thread-safe): 여러 생산자/소비자가 동시에 써도 내부 락으로 안전.
 *       ② 블로킹(blocking):
 *            - 큐가 가득 차면 put() 하는 생산자가 자리가 날 때까지 "잠들어" 기다린다.
 *            - 큐가 비면 take() 하는 소비자가 데이터가 올 때까지 "잠들어" 기다린다.
 *          바쁜 대기(busy-wait, while 루프로 CPU 태우기)가 아니라 진짜로 스레드를 재운다.
 *
 *     생산자 ──put()──▶ [ □ □ □ ] (크기 5 유한 버퍼) ──take()──▶ 소비자
 *              가득 차면 대기          ▲비면 대기
 *
 *   유한(bounded) 버퍼가 핵심이다: 생산자가 소비자보다 빠르면 버퍼가 차고,
 *   그러면 생산자가 자동으로 느려진다(back-pressure, 배압). 메모리 폭발을 막는다.
 *
 * 이 데모는:
 *   - 크기 5짜리 유한 버퍼를 쓴다.
 *   - 생산자는 빠르게, 소비자는 느리게 → 버퍼가 자주 가득 차 생산자가 블록되는 걸 관찰.
 *   - 정확히 만든 개수만큼 소비되었는지 확인(유실/중복 없음).
 *   - "독약(poison pill)" 신호로 소비자를 깔끔히 종료 → 절대 hang 하지 않음.
 */
public class ProducerConsumer {

    // 유한 버퍼: 최대 5개까지만 담긴다. 6개째 넣으려는 생산자는 블록된다.
    static final BlockingQueue<Integer> queue = new ArrayBlockingQueue<>(5);

    static final int TOTAL_ITEMS = 20;     // 총 생산할 개수
    static final int POISON_PILL = -1;      // "이제 그만" 종료 신호

    static final AtomicInteger produced = new AtomicInteger(0);
    static final AtomicInteger consumed = new AtomicInteger(0);

    public static void main(String[] args) throws Exception {
        System.out.println("=================================================");
        System.out.println(" CS(5) 생산자-소비자 & BlockingQueue (유한 버퍼)");
        System.out.println("=================================================\n");
        System.out.println("버퍼 크기 = 5,  생산할 총 개수 = " + TOTAL_ITEMS);
        System.out.println("생산자는 빠르고 소비자는 느리다 → 버퍼가 자주 가득 차 생산자가 대기하게 된다.\n");

        CountDownLatch done = new CountDownLatch(2); // 생산자 1 + 소비자 1

        // ── 생산자 ──
        Thread producer = new Thread(() -> {
            try {
                for (int item = 1; item <= TOTAL_ITEMS; item++) {
                    // 버퍼가 가득 차 있으면 put() 이 여기서 잠들어 기다린다(블로킹).
                    if (queue.remainingCapacity() == 0) {
                        System.out.println("    (버퍼 가득참! 생산자 대기 → 소비자가 하나 뺄 때까지 블록)");
                    }
                    queue.put(item);
                    produced.incrementAndGet();
                    System.out.printf("  [생산] %2d 넣음   (버퍼: %d/5)%n", item, queue.size());
                    Thread.sleep(10); // 생산은 빠르게
                }
                queue.put(POISON_PILL); // 소비자에게 "끝" 신호
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                done.countDown();
            }
        }, "producer");

        // ── 소비자 ──
        Thread consumer = new Thread(() -> {
            try {
                while (true) {
                    // 버퍼가 비어 있으면 take() 가 여기서 잠들어 데이터를 기다린다(블로킹).
                    Integer item = queue.take();
                    if (item == POISON_PILL) {
                        System.out.println("  [소비] 종료 신호(poison pill) 수신 → 소비자 종료");
                        break;
                    }
                    consumed.incrementAndGet();
                    System.out.printf("  [소비]        %2d 꺼냄   (버퍼: %d/5)%n", item, queue.size());
                    Thread.sleep(40); // 소비는 느리게 → 버퍼가 자주 참
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                done.countDown();
            }
        }, "consumer");

        producer.start();
        consumer.start();

        // 안전장치: 최대 15초 안에 끝난다. 안 끝나면 강제 인터럽트로 hang 방지.
        boolean finished = done.await(15, TimeUnit.SECONDS);
        if (!finished) {
            System.out.println("!! 시간 초과 - 스레드 강제 종료");
            producer.interrupt();
            consumer.interrupt();
        }
        producer.join(1000);
        consumer.join(1000);

        System.out.println();
        System.out.printf("생산한 개수 = %d, 소비한 개수 = %d → %s%n",
                produced.get(), consumed.get(),
                produced.get() == consumed.get() ? "일치! (유실/중복 없음)" : "불일치!");
        System.out.println("\n결론: BlockingQueue 는 락 코드를 직접 짜지 않고도 안전한 생산자-소비자를 만든다.");
        System.out.println("      유한 버퍼가 배압(back-pressure)을 주어 빠른 생산자를 자동으로 늦춘다.");
    }
}
