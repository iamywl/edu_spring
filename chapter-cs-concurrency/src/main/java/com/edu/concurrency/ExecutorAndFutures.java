package com.edu.concurrency;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * CS 운영체제 트랙 (4) - 스레드 풀(ExecutorService)과 Future
 *
 * "왜?" 요청마다 new Thread() 하지 않고 스레드 풀을 쓰는가?
 *
 *   - 스레드 생성은 공짜가 아니다. OS 자원(스택 메모리 ~1MB, 커널 스케줄링 등록)이 든다.
 *     요청이 초당 수천 개면 스레드를 매번 만들다 서버가 죽는다.
 *   - 스레드 풀은 스레드를 "미리 몇 개 만들어 두고 재사용"한다.
 *     작업(task)만 큐에 넣으면, 놀고 있는 워커 스레드가 꺼내서 처리한다.
 *   - 이것이 바로 톰캣이 HTTP 요청을 처리하는 방식이다(thread-per-request).
 *     → SPRING_개념서 §2.7 "서버는 멀티스레드다"의 그 스레드 풀이 이것이다.
 *
 *     작업들 ─▶ [작업 큐] ─▶ ┌─ 워커1 ─┐
 *                            ├─ 워커2 ─┤ (3개를 계속 재사용)
 *                            └─ 워커3 ─┘
 *
 *   - Future<T>: "지금은 아직 없지만 나중에 나올 결과"를 담는 영수증.
 *     submit() 하면 즉시 Future 를 받고, future.get() 하면 결과가 준비될 때까지 기다린다.
 *
 * 두 가지를 증명한다:
 *   (1) 풀의 스레드 수는 고정(3개)인데 작업은 9개 → 스레드가 재사용된다(이름이 반복 등장).
 *   (2) Future 로 각 작업의 결과(제곱값)를 순서대로 회수한다.
 */
public class ExecutorAndFutures {

    public static void main(String[] args) throws Exception {
        System.out.println("=================================================");
        System.out.println(" CS(4) 스레드 풀(ExecutorService)과 Future");
        System.out.println("=================================================\n");

        poolReuseAndFutures();
        System.out.println();
        rawThreadContrast();

        System.out.println("\n결론: 풀은 스레드를 재사용해 생성 비용을 없애고, Future 로 결과를 받아온다.");
        System.out.println("      프레임워크(톰캣/스프링)가 요청 처리에 스레드 풀을 쓰는 이유가 이것이다.");
    }

    static void poolReuseAndFutures() throws InterruptedException, ExecutionException {
        System.out.println("── 1. 스레드 3개 풀로 작업 9개 처리 (+ Future 로 결과 회수) ──");

        // 워커 스레드 3개짜리 고정 풀.
        ExecutorService pool = Executors.newFixedThreadPool(3);

        List<Future<Integer>> futures = new ArrayList<>();
        for (int n = 1; n <= 9; n++) {
            final int value = n;
            // submit 은 Callable<Integer> 를 받아 Future<Integer> 를 즉시 돌려준다.
            Future<Integer> future = pool.submit(() -> {
                String worker = Thread.currentThread().getName();
                // 어떤 워커가 이 작업을 집었는지 출력 → 이름이 3종류로 반복되면 재사용 증거.
                System.out.printf("  [%s] 작업 %d 처리 중...%n", worker, value);
                Thread.sleep(50); // 일하는 척(짧게)
                return value * value; // 결과 = 제곱
            });
            futures.add(future);
        }

        // Future.get() 으로 결과를 순서대로 회수한다(준비될 때까지 블록).
        System.out.println("\n  결과 회수(Future.get):");
        int sum = 0;
        for (int i = 0; i < futures.size(); i++) {
            int squared = futures.get(i).get(); // 결과가 나올 때까지 대기
            sum += squared;
            System.out.printf("    작업 %d 의 제곱 = %d%n", i + 1, squared);
        }
        System.out.println("  제곱들의 합 = " + sum);

        // 풀은 반드시 종료시킨다. 안 그러면 워커 스레드가 살아있어 프로그램이 안 끝난다.
        pool.shutdown(); // 새 작업 거부, 진행 중 작업은 마저 처리
        boolean done = pool.awaitTermination(5, TimeUnit.SECONDS);
        System.out.println("  풀 종료 완료? " + done + "  (shutdown → awaitTermination)");
        System.out.println("  ▶ 워커 이름이 3종류만 반복 등장했다면, 스레드가 재사용된 것이다.");
    }

    static void rawThreadContrast() throws InterruptedException {
        System.out.println("── 2. 대조: raw new Thread() 는 매번 새 스레드 (재사용 없음) ──");
        for (int i = 1; i <= 3; i++) {
            final int id = i;
            Thread th = new Thread(() ->
                    System.out.printf("  new Thread() → %s (매번 새로 생성/소멸, 비쌈)%n",
                            Thread.currentThread().getName()), "raw-thread-" + id);
            th.start();
            th.join();
        }
        System.out.println("  ▶ 이름이 매번 다르다 = 매번 새 스레드. 대량 트래픽에선 이 방식이 감당 불가.");
    }
}
