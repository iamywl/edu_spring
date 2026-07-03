package com.edu.concurrency;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * CS 운영체제 트랙 (9) - CompletableFuture: "기다리지 말고, 다음에 할 일을 예약하라"
 *
 * ExecutorAndFutures 의 Future 는 결과를 받으려면 get() 으로 '블록'해야 했다.
 * CompletableFuture 는 발상을 뒤집는다: 결과가 나오면 실행할 '다음 단계'를 미리 등록한다.
 *
 *   Future:              결과 나올 때까지 스레드가 서서 기다림 (blocking)
 *   CompletableFuture:   "나오면 이거 해줘" 하고 예약한 뒤 딴 일 함 (non-blocking 조합)
 *
 * 핵심 메서드 (이 데모에서 전부 등장):
 *   supplyAsync(공급자)      : 백그라운드에서 값을 만드는 비동기 작업 시작
 *   thenApply(변환)          : 결과를 '변환' (map 같은 것)          A → B
 *   thenCompose(다음비동기)  : 결과로 '또 다른 비동기 호출' 연결(flatMap) A → CF<B>
 *   thenCombine(다른CF, 합침): '독립적인 두 결과'를 만나서 합침
 *   allOf(여러 CF)           : 여러 개가 전부 끝나기를 기다림
 *   exceptionally / handle   : 예외가 나면 대체값으로 복구
 *
 * 왜 배우나? "외부 API 3개를 불러 조합"할 때 순차로 부르면 시간이 3배 든다.
 * 비동기로 동시에 던지고 조합하면 가장 느린 것 하나의 시간만 든다. 이것을 시간으로 증명한다.
 *
 * ※ 외부 네트워크 없이, Thread.sleep 으로 '느린 가짜 API'를 시뮬레이션한다.
 */
public class CompletableFutureDemo {

    // 비동기 작업을 실행할 스레드 풀 (기본 ForkJoinPool 대신 명시적 풀: 종료를 우리가 관리)
    static final ExecutorService pool = Executors.newFixedThreadPool(4);

    public static void main(String[] args) throws Exception {
        System.out.println("=================================================");
        System.out.println(" CS(9) CompletableFuture: 비동기 작업의 조합");
        System.out.println("=================================================\n");

        try {
            part1_basicPipeline();
            part2_composeAndCombine();
            part3_sequentialVsParallel();
            part4_errorHandling();
        } finally {
            pool.shutdown();                          // 풀 정리 (안 하면 JVM이 안 끝남)
            pool.awaitTermination(5, TimeUnit.SECONDS);
        }

        System.out.println("\n결론:");
        System.out.println("  - CompletableFuture 는 '결과가 나오면 할 일'을 예약하는 비동기 파이프라인이다.");
        System.out.println("  - 변환은 thenApply, 비동기 연결은 thenCompose, 독립 결과 합침은 thenCombine.");
        System.out.println("  - 독립적인 호출들은 동시에 던져라: 3 × 300ms가 900ms가 아니라 ~300ms가 된다.");
        System.out.println("  - 예외는 파이프라인을 타고 흐른다. exceptionally/handle 로 복구 지점을 만든다.");
    }

    /** 느린 가짜 API: ms 만큼 걸려서 값을 돌려준다. (실무의 DB 조회/외부 API 호출 흉내) */
    static String fakeApi(String name, String result, long ms) {
        System.out.printf("    [%s] %s 호출 시작 (%dms 걸림)...%n",
                Thread.currentThread().getName(), name, ms);
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return result;
    }

    // ─────────────────────────────────────────────────────
    // 1. supplyAsync + thenApply: 기본 파이프라인
    // ─────────────────────────────────────────────────────
    static void part1_basicPipeline() throws Exception {
        System.out.println("── 1. supplyAsync → thenApply: 만들고, 변환한다 ──");

        CompletableFuture<String> greeting =
                CompletableFuture
                        .supplyAsync(() -> fakeApi("사용자조회", "kim", 200), pool) // 비동기 시작
                        .thenApply(name -> {                                        // 결과 변환 예약
                            System.out.printf("    [%s] thenApply: '%s' → 인사말로 변환%n",
                                    Thread.currentThread().getName(), name);
                            return "안녕하세요, " + name + "님!";
                        });

        System.out.println("  (메인 스레드는 블록되지 않고 여기까지 먼저 도달했다 ← 비동기의 증거)");
        System.out.println("  최종 결과: " + greeting.get(5, TimeUnit.SECONDS)); // 여기서만 대기
        System.out.println();
    }

    // ─────────────────────────────────────────────────────
    // 2. thenCompose(의존 호출 연결) + thenCombine(독립 호출 합침)
    // ─────────────────────────────────────────────────────
    static void part2_composeAndCombine() throws Exception {
        System.out.println("── 2. thenCompose vs thenCombine ──");

        // thenCompose: 앞 결과가 있어야 다음 호출이 가능할 때 (의존 관계: 사용자ID → 주문조회)
        System.out.println("  (a) thenCompose — '사용자 조회' 결과로 '주문 조회'를 이어 부른다 (의존적):");
        CompletableFuture<String> orders =
                CompletableFuture
                        .supplyAsync(() -> fakeApi("사용자조회", "user-7", 150), pool)
                        .thenCompose(userId ->                          // 결과로 '또 다른 비동기' 시작
                                CompletableFuture.supplyAsync(
                                        () -> fakeApi("주문조회(" + userId + ")", userId + "의 주문 3건", 150),
                                        pool));
        System.out.println("      결과: " + orders.get(5, TimeUnit.SECONDS));

        // thenCombine: 서로 무관한 두 호출을 '동시에' 던지고 결과를 합칠 때
        System.out.println("  (b) thenCombine — '날씨'와 '환율'을 동시에 부르고 합친다 (독립적):");
        long start = System.nanoTime();
        CompletableFuture<String> weather =
                CompletableFuture.supplyAsync(() -> fakeApi("날씨API", "맑음", 200), pool);
        CompletableFuture<String> rate =
                CompletableFuture.supplyAsync(() -> fakeApi("환율API", "1$=1,350원", 200), pool);

        String combined = weather
                .thenCombine(rate, (w, r) -> "오늘: " + w + ", " + r)  // 둘 다 끝나면 합침
                .get(5, TimeUnit.SECONDS);
        long ms = (System.nanoTime() - start) / 1_000_000;
        System.out.printf("      결과: %s  (소요 %dms — 200+200=400ms가 아니라 ~200ms!)%n%n", combined, ms);
    }

    // ─────────────────────────────────────────────────────
    // 3. 순차 vs 병렬: allOf 로 시간 차이를 증명
    // ─────────────────────────────────────────────────────
    static void part3_sequentialVsParallel() throws Exception {
        System.out.println("── 3. 순차 호출 vs 병렬 호출: 시간으로 증명 ──");
        final long COST = 300; // 가짜 API 하나당 300ms

        // (a) 순차: 하나 끝나야 다음 시작 → 300 × 3 = 약 900ms
        System.out.println("  (a) 순차 호출 (하나씩 기다림):");
        long t1 = System.nanoTime();
        String a = fakeApi("API-A", "a", COST);
        String b = fakeApi("API-B", "b", COST);
        String c = fakeApi("API-C", "c", COST);
        long seqMs = (System.nanoTime() - t1) / 1_000_000;
        System.out.printf("      순차 결과 [%s,%s,%s]  소요 = %dms (≈ 300×3)%n", a, b, c, seqMs);

        // (b) 병렬: 셋을 동시에 던지고 allOf 로 완료 대기 → 약 300ms
        System.out.println("  (b) 병렬 호출 (동시에 던지고 allOf 로 대기):");
        long t2 = System.nanoTime();
        CompletableFuture<String> fa = CompletableFuture.supplyAsync(() -> fakeApi("API-A", "a", COST), pool);
        CompletableFuture<String> fb = CompletableFuture.supplyAsync(() -> fakeApi("API-B", "b", COST), pool);
        CompletableFuture<String> fc = CompletableFuture.supplyAsync(() -> fakeApi("API-C", "c", COST), pool);

        CompletableFuture.allOf(fa, fb, fc).get(5, TimeUnit.SECONDS); // 셋 다 끝날 때까지
        long parMs = (System.nanoTime() - t2) / 1_000_000;
        System.out.printf("      병렬 결과 [%s,%s,%s]  소요 = %dms (≈ 가장 느린 하나!)%n",
                fa.join(), fb.join(), fc.join(), parMs);
        System.out.printf("  ▶ 순차 %dms vs 병렬 %dms — 독립적인 I/O는 동시에 던지는 것이 정답.%n%n",
                seqMs, parMs);
    }

    // ─────────────────────────────────────────────────────
    // 4. 예외 처리: exceptionally / handle
    // ─────────────────────────────────────────────────────
    static void part4_errorHandling() throws Exception {
        System.out.println("── 4. 예외 처리: 파이프라인 중간에서 터지면? ──");

        // (a) exceptionally: 예외가 났을 때만 호출되어 '대체값'으로 복구
        String recovered = CompletableFuture
                .supplyAsync(() -> {
                    System.out.println("    외부 API 호출... (이번엔 실패한다고 가정)");
                    if (true) {
                        throw new IllegalStateException("외부 서비스 점검 중");
                    }
                    return "정상 응답";
                }, pool)
                .thenApply(s -> s + " (가공됨)")        // 예외 발생 시 이 단계는 건너뛴다!
                .exceptionally(ex -> {
                    System.out.println("    exceptionally: 예외 잡음 → " + ex.getCause().getMessage());
                    return "기본값(캐시된 응답)";        // 대체값으로 파이프라인 복구
                })
                .get(5, TimeUnit.SECONDS);
        System.out.println("  (a) exceptionally 복구 결과: " + recovered);

        // (b) handle: 성공/실패 '양쪽 모두' 지나는 합류 지점 (result 또는 ex 중 하나만 non-null)
        String handled = CompletableFuture
                .supplyAsync(() -> fakeApi("정상API", "성공 데이터", 100), pool)
                .handle((result, ex) -> ex == null
                        ? "handle: 성공 → " + result
                        : "handle: 실패 → 기본값 사용")
                .get(5, TimeUnit.SECONDS);
        System.out.println("  (b) handle 결과 (이번엔 성공 경로): " + handled);
        System.out.println("  ▶ exceptionally = catch 처럼 '실패 시에만', handle = finally 처럼 '항상' 지나는 문.");
    }
}
