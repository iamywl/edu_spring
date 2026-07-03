package com.edu.network;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * CS 네트워크 트랙 - 표준 HttpClient: RawHttpClient 가 손으로 하던 일을 표준 라이브러리로
 *
 * RawHttpClient 에서 우리는 소켓을 열고 "GET / HTTP/1.1\r\n..." 텍스트를 손으로 조립했다.
 * 실무에서는 그렇게 하지 않는다 — Java 11부터 표준으로 들어온 java.net.http.HttpClient 를 쓴다.
 *
 *   RawHttpClient (교육용)          HttpClient (실무용, Java 11+)
 *   ─────────────────────          ─────────────────────────────
 *   소켓 열기/닫기 직접              커넥션 관리(keep-alive/풀) 자동
 *   요청 텍스트 손으로 조립           HttpRequest 빌더로 선언
 *   응답 파싱 직접                   HttpResponse 로 상태/헤더/바디 제공
 *   HTTP/1.1 평문만                 HTTPS(TLS) + HTTP/2 협상(ALPN) 자동
 *   동기만                          동기(send) + 비동기(sendAsync → CompletableFuture)
 *
 * 이 데모가 보여주는 것:
 *   ① GET  : 타임아웃/헤더 설정, 상태 코드, 협상된 HTTP 버전(HTTP/2인지!) 확인
 *   ② POST : JSON 바디 전송 (Content-Type: application/json)
 *   ③ 비동기: sendAsync 가 돌려주는 CompletableFuture 로 논블로킹 요청
 *
 * ★ 인터넷 연결이 필요하다 ★
 *   대상: https://httpbin.org (요청을 그대로 되돌려주는 공개 테스트 서비스)
 *   오프라인이거나 서비스가 응답하지 않으면, 스택트레이스 없이 친절한 안내를 출력하고
 *   정상 종료한다. (개념 학습에는 지장 없음)
 */
public class HttpClientExample {

    private static final String BASE = "https://httpbin.org";
    private static final Duration TIMEOUT = Duration.ofSeconds(8); // 요청당 최대 8초 (hang 방지)

    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println(" CS 네트워크: 표준 HttpClient (Java 11+)");
        System.out.println("=================================================\n");
        System.out.println("대상: " + BASE + "  (인터넷 연결 필요. 오프라인이면 안내 후 정상 종료)\n");

        // HttpClient 는 한 번 만들어 재사용한다 (내부에 커넥션 풀이 있다 — 매번 만들면 손해).
        HttpClient client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)          // HTTP/2 를 '희망' (안 되면 1.1로 자동 강등)
                .connectTimeout(Duration.ofSeconds(5))       // TCP 연결 수립 타임아웃
                .followRedirects(HttpClient.Redirect.NORMAL) // 3xx 리다이렉트 자동 추적
                .build();

        boolean ok = part1_get(client);
        if (ok) {                       // 첫 요청이 실패했다면(오프라인) 나머지는 시도하지 않는다
            part2_postJson(client);
            part3_async(client);
        } else {
            System.out.println("첫 요청이 실패하여 나머지 단계(POST/비동기)는 건너뜁니다.");
            System.out.println("네트워크가 되는 환경에서 다시 실행해 보세요. 프로그램은 정상 종료합니다.");
            return;
        }

        System.out.println("\n── 정리 ──");
        System.out.println("  - RawHttpClient 가 손으로 한 일(소켓/요청 조립/파싱)을 HttpClient 가 대신한다.");
        System.out.println("  - 심지어 TLS 암호화와 HTTP/2 협상(ALPN)까지 자동이다.");
        System.out.println("  - sendAsync 는 CompletableFuture 를 돌려준다 → CS(9) 데모의 조합 기법이 그대로 통한다.");
        System.out.println("  - Spring 의 RestClient/WebClient 도 결국 이런 HTTP 클라이언트를 감싼 것이다.");
    }

    // ─────────────────────────────────────────────────────
    // 1. GET: 헤더 붙이기, 상태 코드, HTTP/2 협상 확인
    // ─────────────────────────────────────────────────────
    static boolean part1_get(HttpClient client) {
        System.out.println("── 1. GET 요청: 상태 코드와 'HTTP/2 협상' 확인 ──");
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE + "/get?course=network"))
                    .timeout(TIMEOUT)                              // 이 요청의 응답 대기 한도
                    .header("Accept", "application/json")          // 헤더는 이렇게 선언적으로
                    .header("User-Agent", "edu-HttpClientExample/1.0")
                    .GET()
                    .build();

            // send() = 동기: 응답이 올 때까지 여기서 기다린다 (RawHttpClient 와 같은 방식)
            HttpResponse<String> response =
                    client.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("  상태 코드   : " + response.statusCode() + " (200이면 성공)");
            System.out.println("  협상된 버전 : " + response.version()
                    + (response.version() == HttpClient.Version.HTTP_2
                       ? "  ← TLS 핸드셰이크의 ALPN 으로 HTTP/2 가 선택됐다! (개념서 §5.2)"
                       : "  ← 서버/경로 사정으로 HTTP/1.1 로 통신했다 (그것도 정상)"));
            response.headers().firstValue("content-type")
                    .ifPresent(ct -> System.out.println("  Content-Type: " + ct));
            System.out.println("  바디 미리보기:");
            System.out.println(indent(preview(response.body(), 300)));
            System.out.println("  ▶ 우리가 보낸 쿼리(course=network)와 헤더가 'args/headers'로 메아리쳐 온다.\n");
            return true;
        } catch (Exception e) {
            printFriendlyNetworkError("GET " + BASE + "/get", e);
            return false;
        }
    }

    // ─────────────────────────────────────────────────────
    // 2. POST: JSON 바디 보내기
    // ─────────────────────────────────────────────────────
    static void part2_postJson(HttpClient client) {
        System.out.println("── 2. POST 요청: JSON 바디 전송 ──");
        // 실무라면 Jackson 등으로 객체→JSON 직렬화하지만, 여기선 원리를 위해 문자열로 직접.
        String json = "{\"name\": \"kim\", \"course\": \"cs-network\", \"week\": 5}";
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE + "/post"))
                    .timeout(TIMEOUT)
                    .header("Content-Type", "application/json")   // "바디가 JSON"이라는 선언 (중요!)
                    .POST(HttpRequest.BodyPublishers.ofString(json)) // 바디를 싣는다
                    .build();

            HttpResponse<String> response =
                    client.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("  보낸 JSON  : " + json);
            System.out.println("  상태 코드  : " + response.statusCode());
            System.out.println("  응답 미리보기 (httpbin 은 받은 것을 'json' 필드로 되돌려준다):");
            System.out.println(indent(preview(response.body(), 400)));
            System.out.println("  ▶ GET과의 차이는 '바디의 유무'와 Content-Type 선언뿐이다.\n");
        } catch (Exception e) {
            printFriendlyNetworkError("POST " + BASE + "/post", e);
        }
    }

    // ─────────────────────────────────────────────────────
    // 3. 비동기: sendAsync → CompletableFuture
    // ─────────────────────────────────────────────────────
    static void part3_async(HttpClient client) {
        System.out.println("── 3. 비동기 요청: sendAsync + CompletableFuture ──");
        // 이 단계는 응답 속도가 안정적인 example.com 을 쓴다 (상태 코드만 보면 되므로 충분).
        try {
            HttpRequest req1 = HttpRequest.newBuilder()
                    .uri(URI.create("https://example.com/")).timeout(TIMEOUT).GET().build();
            HttpRequest req2 = HttpRequest.newBuilder()
                    .uri(URI.create("https://example.org/")).timeout(TIMEOUT).GET().build();

            long start = System.nanoTime();

            // sendAsync 는 '즉시' CompletableFuture 를 돌려주고 요청은 백그라운드에서 진행된다.
            CompletableFuture<String> f1 = client
                    .sendAsync(req1, HttpResponse.BodyHandlers.ofString())
                    .thenApply(res -> "example.com → 상태 " + res.statusCode()
                            + " (" + res.version() + ")");                 // 응답 오면 실행할 변환 예약
            CompletableFuture<String> f2 = client
                    .sendAsync(req2, HttpResponse.BodyHandlers.ofString())
                    .thenApply(res -> "example.org → 상태 " + res.statusCode()
                            + " (" + res.version() + ")");

            System.out.println("  (두 요청을 동시에 던졌다. 메인 스레드는 블록되지 않고 여기 먼저 도달!)");

            // 두 요청이 모두 끝나기를 (최대 15초) 기다린다.
            CompletableFuture.allOf(f1, f2).get(15, TimeUnit.SECONDS);
            long ms = (System.nanoTime() - start) / 1_000_000;

            System.out.println("  " + f1.join());
            System.out.println("  " + f2.join());
            System.out.printf("  두 요청 총 소요: %dms — 순차(2배)가 아니라 동시에 진행된 시간이다.%n", ms);
            System.out.println("  ▶ CS(9) CompletableFutureDemo 의 thenApply/allOf 가 실전 HTTP에 그대로 쓰인다.");
        } catch (Exception e) {
            printFriendlyNetworkError("비동기 GET ×2", e);
        }
    }

    // ─────────────────────────────────────────────────────
    // 도우미들
    // ─────────────────────────────────────────────────────

    /** 스택트레이스 없이, 원인별로 친절한 안내만 출력한다. (교육용: 겁주지 않기) */
    static void printFriendlyNetworkError(String what, Exception outer) {
        System.out.println("  [연결 실패] " + what + " 요청이 실패했습니다.");
        // 비동기(CompletableFuture) 경로의 예외는 ExecutionException 으로 포장돼 오므로 알맹이를 꺼낸다.
        Throwable e = outer;
        while ((e instanceof java.util.concurrent.ExecutionException
                || e instanceof java.util.concurrent.CompletionException)
                && e.getCause() != null) {
            e = e.getCause();
        }
        String reason;
        if (e instanceof java.net.UnknownHostException) {
            reason = "호스트 이름을 IP로 바꾸지 못했습니다 (DNS 실패 — 오프라인일 가능성이 큼).";
        } else if (e instanceof java.net.http.HttpTimeoutException) {
            reason = "제한 시간(" + TIMEOUT.toSeconds() + "초) 안에 응답이 오지 않았습니다 (서버 느림/차단).";
        } else if (e instanceof java.net.ConnectException) {
            reason = "TCP 연결 자체가 거부/실패했습니다 (방화벽/프록시/오프라인 확인).";
        } else if (e instanceof InterruptedException) {
            Thread.currentThread().interrupt();
            reason = "대기 중 인터럽트가 걸렸습니다.";
        } else if (e instanceof java.util.concurrent.TimeoutException) {
            reason = "전체 대기 한도 안에 응답이 오지 않았습니다 (서버 느림/차단).";
        } else {
            reason = "네트워크 오류: " + e.getClass().getSimpleName()
                    + (e.getMessage() != null ? " (" + e.getMessage() + ")" : "");
        }
        System.out.println("  원인 추정: " + reason);
        System.out.println("  → 인터넷 연결을 확인한 뒤 다시 실행해 보세요. (개념 학습에는 지장 없습니다)\n");
    }

    /** 긴 바디는 앞부분만 잘라 보여준다 (터미널 도배 방지). */
    static String preview(String body, int limit) {
        String trimmed = body.strip();
        return trimmed.length() <= limit ? trimmed
                : trimmed.substring(0, limit) + "\n... (이하 " + (trimmed.length() - limit) + "자 생략)";
    }

    /** 여러 줄 문자열 앞에 들여쓰기를 붙인다. */
    static String indent(String text) {
        return "    | " + text.replace("\n", "\n    | ");
    }
}
