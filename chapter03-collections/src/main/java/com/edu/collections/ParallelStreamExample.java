package com.edu.collections;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.*;

/**
 * Chapter 03 - Parallel Stream 예제 (ParallelStreamExample)
 *
 * ┌─────────────────────────────────────────────────────────────┐
 * │ 이 파일은 "병렬 스트림(Parallel Stream)"에만 집중한다.       │
 * └─────────────────────────────────────────────────────────────┘
 *
 * ▶ 병렬 스트림이란?
 *   - 스트림 요소를 여러 조각으로 나눠 여러 스레드에서 동시에 처리한 뒤 결과를 합친다.
 *   - collection.parallelStream() 또는 stream().parallel() 로 만든다.
 *   - 내부적으로 공용 ForkJoinPool(commonPool)을 사용한다.
 *
 * ▶ 언제 병렬이 "이득"인가?
 *   - 데이터가 충분히 크고(수십만~수백만 이상), 요소별 연산이 무거우며,
 *   - 각 연산이 서로 "독립적"이고(공유 상태 없음), 소스가 쉽게 쪼개질 때(ArrayList, 배열).
 *
 * ▶ 언제 병렬이 "위험/손해"인가?
 *   - 데이터가 작으면 스레드 분할/병합 오버헤드 때문에 오히려 순차보다 느리다.
 *   - 공유 가변 상태(mutable shared state)를 건드리면 경쟁 조건(race condition)으로
 *     결과가 틀리거나 예측 불가해진다. (아래 5번에서 실제로 재현)
 *   - 순서가 중요한데 forEach를 쓰면 순서가 보장되지 않는다.
 *   - LinkedList처럼 쪼개기 어려운 소스는 분할 비용이 커서 부적합.
 *
 * 참고: .toList()는 Java 16+ 방식이며 수정 불가 리스트를 반환한다.
 */
public class ParallelStreamExample {

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("  Chapter 03 - Parallel Stream");
        System.out.println("========================================\n");

        // 병렬에 사용할 수 있는 코어 수 (commonPool 크기의 근거)
        System.out.println("--- 0. 실행 환경 ---");
        System.out.println("  가용 프로세서(코어) 수 : " + Runtime.getRuntime().availableProcessors());
        System.out.println();

        List<Integer> bigList = IntStream.rangeClosed(1, 10_000_000)
                .boxed()
                .toList();

        // --------------------------------------------------
        // 1. 큰 데이터 - 순차 vs 병렬 성능 측정 (병렬이 이득인 경우)
        // --------------------------------------------------
        System.out.println("--- 1. 큰 데이터 성능 측정 (1천만 개 짝수 합계) ---");

        long startSeq = System.nanoTime();
        long sumSeq = bigList.stream()
                .filter(n -> n % 2 == 0)
                .mapToLong(Integer::longValue)
                .sum();
        long endSeq = System.nanoTime();

        long startPar = System.nanoTime();
        long sumPar = bigList.parallelStream()
                .filter(n -> n % 2 == 0)
                .mapToLong(Integer::longValue)
                .sum();
        long endPar = System.nanoTime();

        System.out.println("  순차 : " + sumSeq + " (소요 " + (endSeq - startSeq) / 1_000_000 + "ms)");
        System.out.println("  병렬 : " + sumPar + " (소요 " + (endPar - startPar) / 1_000_000 + "ms)");
        System.out.println("  => 결과 값은 동일. 큰 데이터에서는 병렬이 보통 더 빠르다.");
        System.out.println();

        // --------------------------------------------------
        // 2. 작은 데이터 - 병렬이 오히려 손해인 경우
        // --------------------------------------------------
        System.out.println("--- 2. 작은 데이터 (병렬 오버헤드) ---");
        List<Integer> smallList = IntStream.rangeClosed(1, 10).boxed().toList();

        long s1 = System.nanoTime();
        int seqSmall = smallList.stream().mapToInt(Integer::intValue).sum();
        long e1 = System.nanoTime();

        long s2 = System.nanoTime();
        int parSmall = smallList.parallelStream().mapToInt(Integer::intValue).sum();
        long e2 = System.nanoTime();

        System.out.println("  순차 합계 : " + seqSmall + " (" + (e1 - s1) + "ns)");
        System.out.println("  병렬 합계 : " + parSmall + " (" + (e2 - s2) + "ns)");
        System.out.println("  => 데이터가 작으면 분할/병합 비용 때문에 병렬이 손해일 수 있다.");
        System.out.println();

        // --------------------------------------------------
        // 3. 실제로 몇 개의 스레드가 동원되는지 확인
        // --------------------------------------------------
        System.out.println("--- 3. 사용된 스레드 확인 ---");
        Set<String> threads = Collections.synchronizedSet(new HashSet<>());
        IntStream.rangeClosed(1, 1000)
                .parallel()
                .forEach(n -> threads.add(Thread.currentThread().getName()));
        System.out.println("  동원된 스레드 : " + threads);
        System.out.println();

        // --------------------------------------------------
        // 4. 순서 - forEach(순서 X) vs forEachOrdered(순서 O)
        // --------------------------------------------------
        System.out.println("--- 4. 순서 보장 ---");
        System.out.print("  forEach        (순서 보장 안 됨) : ");
        List.of(1, 2, 3, 4, 5, 6, 7, 8).parallelStream()
                .forEach(n -> System.out.print(n + " "));
        System.out.println();

        System.out.print("  forEachOrdered (순서 보장)       : ");
        List.of(1, 2, 3, 4, 5, 6, 7, 8).parallelStream()
                .forEachOrdered(n -> System.out.print(n + " "));
        System.out.println();
        System.out.println();

        // --------------------------------------------------
        // 5. 공유 가변 상태의 위험 - 병렬에서 절대 하면 안 되는 것
        // --------------------------------------------------
        System.out.println("--- 5. 공유 가변 상태의 위험 (race condition) ---");
        System.out.println("  [나쁜 예: 병렬 스트림에서 일반 변수/컬렉션을 직접 변경]");

        // 잘못된 방식: 여러 스레드가 하나의 ArrayList에 동시에 add -> 결과 개수가 틀릴 수 있음.
        // 실제로 ArrayList는 스레드 안전하지 않아 개수가 어긋나거나 예외(예: 배열 인덱스 오류)가 난다.
        // 데모가 중단되지 않도록 try/catch로 감싸서 "위험하다"는 점만 보여준다.
        List<Integer> unsafe = new ArrayList<>();
        try {
            IntStream.rangeClosed(1, 100_000)
                    .parallel()
                    .forEach(unsafe::add); // ArrayList는 스레드 안전하지 않음!
            System.out.println("  기대 개수 : 100000, 실제(unsafe) : " + unsafe.size()
                    + "  <- 실행마다 달라짐 (100000이 아닐 수 있음)");
        } catch (Exception ex) {
            System.out.println("  기대 개수 : 100000, 실제(unsafe) : 예외 발생 -> "
                    + ex.getClass().getSimpleName()
                    + "  <- 경쟁 조건으로 인해 아예 깨질 수도 있음");
        }

        System.out.println("  [올바른 예: 부수효과 없이 collect / reduce 로 합치기]");
        List<Integer> safe = IntStream.rangeClosed(1, 100_000)
                .parallel()
                .boxed()
                .toList(); // 각 스레드 결과를 안전하게 병합
        System.out.println("  실제(safe collect) : " + safe.size());

        // 카운팅이 필요하면 원자적(atomic) 타입이나 reduce/sum을 사용
        AtomicLong atomicCount = new AtomicLong();
        IntStream.rangeClosed(1, 100_000)
                .parallel()
                .forEach(n -> atomicCount.incrementAndGet());
        System.out.println("  실제(AtomicLong)   : " + atomicCount.get());
        System.out.println("  => 병렬에서는 공유 가변 상태를 피하고, 수집은 collect/reduce에 맡겨라.");
        System.out.println();

        System.out.println("========================================");
        System.out.println("  Parallel Stream 예제 완료!");
        System.out.println("========================================");
    }
}
