package com.edu.javabook.ch21;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 21.6 가상 스레드 (Virtual Thread)
 *
 * [가상 스레드란]
 * - Java 21에서 정식화된 경량 스레드다.
 * - 기존 플랫폼 스레드(운영체제 스레드에 1:1로 매핑)와 달리,
 *   가상 스레드는 JVM이 관리하는 아주 가벼운 스레드다.
 * - 수천~수백만 개를 만들어도 부담이 적어 대규모 동시성(특히 I/O 대기가 많은
 *   작업)에 적합하다.
 *
 * [생성 방법]
 * - Thread.ofVirtual().start(Runnable) : 가상 스레드 하나를 직접 만들어 실행.
 * - Executors.newVirtualThreadPerTaskExecutor() : 작업마다 가상 스레드를 하나씩
 *   할당하는 ExecutorService. 대량 작업 제출에 편리하다.
 *
 * [정상 종료]
 * - ExecutorService를 try-with-resources로 닫거나 shutdown 후
 *   awaitTermination으로 모든 작업이 끝날 때까지 기다린 뒤 종료한다.
 *   (그래야 프로그램이 hang 없이 정상적으로 exit 0로 끝난다.)
 */
public class VirtualThread {

    public static void main(String[] args) throws InterruptedException {

        System.out.println("=== 21.6 가상 스레드 ===");

        // [1] 가상 스레드 하나를 직접 생성/실행하고 join으로 대기
        System.out.println("\n[1] 가상 스레드 직접 생성");
        Thread vt = Thread.ofVirtual().name("demo-vt").start(() -> {
            System.out.println("  가상 스레드 실행 중, isVirtual=" + Thread.currentThread().isVirtual());
        });
        vt.join();   // 해당 가상 스레드가 끝날 때까지 대기(정상 종료 보장)
        System.out.println("  가상 스레드 완료");

        // [2] 작업마다 가상 스레드를 할당하는 Executor로 다수 생성
        System.out.println("\n[2] 다수의 가상 스레드로 작업 처리");
        int taskCount = 1000;
        AtomicInteger done = new AtomicInteger(0);

        // try-with-resources: 블록 종료 시 close()가 shutdown+awaitTermination을 수행
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < taskCount; i++) {
                final int id = i;
                executor.submit(() -> {
                    // 짧은 I/O 대기를 흉내 (가상 스레드는 대기 중 캐리어 스레드를 점유하지 않음)
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    done.incrementAndGet();
                    if (id == 0) {
                        System.out.println("  첫 작업 실행 스레드 isVirtual="
                                + Thread.currentThread().isVirtual());
                    }
                });
            }
        } // <- 여기서 모든 작업이 끝날 때까지 대기 후 executor 종료

        System.out.println("  제출한 작업 수 = " + taskCount);
        System.out.println("  완료한 작업 수 = " + done.get());

        // [3] 명시적 shutdown + awaitTermination 패턴 (참고용)
        System.out.println("\n[3] 명시적 종료 패턴");
        ExecutorService ex = Executors.newVirtualThreadPerTaskExecutor();
        ex.submit(() -> System.out.println("  추가 작업 1 완료"));
        ex.submit(() -> System.out.println("  추가 작업 2 완료"));
        ex.shutdown();  // 새 작업 접수 중단
        boolean finished = ex.awaitTermination(10, TimeUnit.SECONDS);  // 완료 대기
        System.out.println("  모든 작업 정상 종료 = " + finished);

        System.out.println("\n[정리]");
        System.out.println("  가상 스레드는 경량이라 대규모로 만들 수 있고,");
        System.out.println("  awaitTermination(또는 try-with-resources)으로 hang 없이 정상 종료한다.");
    }
}
