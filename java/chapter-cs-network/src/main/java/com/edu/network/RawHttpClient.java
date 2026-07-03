package com.edu.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * CS 네트워크 트랙 - 날것의 HTTP: "HTTP는 TCP 위에 흐르는 그냥 텍스트다"
 *
 * 왜 이걸 배우는가?
 *   - Spring의 @GetMapping, RestTemplate, WebClient는 전부 이 아래에서 일어나는 일을 감춘다.
 *   - 그 감춰진 밑바닥을 직접 열어보면, "HTTP 요청"의 정체가 무엇인지 두 눈으로 확인할 수 있다.
 *   - 정체는 놀랍도록 단순하다:  TCP 소켓을 열고 →  약속된 텍스트를 쓰고 →  돌아온 텍스트를 읽는다.
 *
 * 개념서 CS_네트워크_개념서.md 와의 연결:
 *   - §3.1 소켓 : new Socket(host, 80) 이 (목적지 IP, 포트) 창구를 여는 코드
 *   - §3.3 TCP 3-way handshake : connect()가 반환되는 순간 = 악수 완료
 *   - §5.1 HTTP 요청의 해부 : "요청라인 + 헤더들 + 빈 줄(\r\n\r\n)" 을 손으로 조립
 *   - §5.3 Connection: close : 응답 후 서버가 연결 종료(§3.6 4-way close)를 시작하게 함
 *
 * 주의:
 *   - 평문(http, 80포트)이라 요청/응답이 그대로 텍스트로 보인다. (그래서 §6에서 TLS가 필요했다!)
 *   - 네트워크 접속이 필요하다. 오프라인이면 연결 실패 메시지를 출력하고 정상 종료한다.
 */
public class RawHttpClient {

    // 평문 HTTP(80포트)를 지원하는 공개 테스트 호스트.
    // example.com 은 80으로 접속하면 보통 https(443)로 301 리다이렉트를 돌려준다.
    // → "리다이렉트 응답"도 훌륭한 관찰 대상이다(상태 라인 301, Location 헤더).
    private static final String HOST = "example.com";
    private static final int    PORT = 80;
    private static final String PATH = "/";
    private static final int    TIMEOUT_MS = 5000; // 연결/읽기 타임아웃(멈춤 방지)

    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println(" CS 네트워크: 날것의 HTTP (Socket 위의 텍스트)");
        System.out.println("=================================================\n");

        System.out.println("대상:  http://" + HOST + ":" + PORT + PATH);
        System.out.println("- 이 프로그램은 브라우저/RestTemplate 없이,");
        System.out.println("  TCP 소켓에 HTTP 요청 '텍스트'를 직접 써서 보냅니다.\n");

        // try-with-resources: 블록을 벗어나면 소켓이 자동으로 close() 된다.
        // → close() 시 클라이언트 쪽 FIN이 나가며 §3.6 연결 종료가 시작된다.
        try (Socket socket = new Socket()) {

            // 1) TCP 연결 (여기서 §3.3 3-way handshake가 일어난다)
            System.out.println("── 1. TCP 연결 시도 (3-way handshake) ──");
            socket.connect(new InetSocketAddress(HOST, PORT), TIMEOUT_MS);
            socket.setSoTimeout(TIMEOUT_MS); // 응답 읽기가 무한 대기하지 않도록
            System.out.println("  연결 성공: " + socket.getLocalSocketAddress()
                    + "  →  " + socket.getRemoteSocketAddress());
            System.out.println("  (왼쪽=내 PC의 랜덤 출발포트, 오른쪽=서버 IP:80  → §3.1 4-튜플)\n");

            // 2) HTTP 요청 '텍스트' 조립  (§5.1 요청의 해부)
            //    각 줄 끝은 CRLF(\r\n), 헤더 끝은 빈 줄(\r\n\r\n) — 이 약속이 HTTP의 전부다.
            String request =
                    "GET " + PATH + " HTTP/1.1\r\n" +   // 요청 라인: [메서드] [경로] [버전]
                    "Host: " + HOST + "\r\n" +           // Host 헤더: HTTP/1.1 필수(§5.1)
                    "User-Agent: edu-RawHttpClient/1.0\r\n" +
                    "Accept: */*\r\n" +
                    "Connection: close\r\n" +            // 응답 후 연결 종료 요청(§5.3, §3.6)
                    "\r\n";                              // ★ 빈 줄 = 헤더 끝 신호

            System.out.println("── 2. 서버로 보낼 요청 원문(raw request) ──");
            printRaw(request);

            OutputStream out = socket.getOutputStream();
            out.write(request.getBytes(StandardCharsets.UTF_8));
            out.flush();

            // 3) 응답 '텍스트' 읽기  (상태 라인 + 헤더 + 빈 줄 + 바디)
            System.out.println("── 3. 서버가 돌려준 응답 원문(raw response) ──");
            InputStream in = socket.getInputStream();
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(in, StandardCharsets.UTF_8));

            String line;
            int lineNo = 0;
            boolean headerEnded = false;
            int bodyBytesShown = 0;
            final int BODY_PREVIEW_LIMIT = 800; // 바디는 앞부분만 미리보기(터미널 도배 방지)

            while ((line = reader.readLine()) != null) {
                if (!headerEnded) {
                    if (lineNo == 0) {
                        // 첫 줄 = 상태 라인 (예: "HTTP/1.1 301 Moved Permanently")
                        System.out.println("  [상태 라인] " + line);
                    } else if (line.isEmpty()) {
                        // 빈 줄 = 헤더의 끝(§5.1의 그 빈 줄)
                        System.out.println("  [헤더 끝(빈 줄)] ──── 이 아래는 바디(body) ────");
                        headerEnded = true;
                    } else {
                        System.out.println("  [헤더] " + line);
                    }
                } else {
                    // 바디: 너무 길면 앞부분만 보여준다
                    if (bodyBytesShown < BODY_PREVIEW_LIMIT) {
                        System.out.println("  " + line);
                        bodyBytesShown += line.length() + 1;
                    } else {
                        System.out.println("  ... (바디 이하 생략) ...");
                        break;
                    }
                }
                lineNo++;
            }

            System.out.println("\n── 4. 정리 ──");
            System.out.println("  방금 본 것이 HTTP의 전부입니다:");
            System.out.println("   1) TCP 소켓을 열고");
            System.out.println("   2) 약속된 텍스트(요청 라인+헤더+빈 줄)를 쓰고");
            System.out.println("   3) 돌아온 텍스트(상태 라인+헤더+바디)를 읽는다.");
            System.out.println("  Spring의 @GetMapping 은 이 과정을 대신 처리해 줄 뿐입니다.");
            System.out.println("  (평문이라 다 보이죠? 그래서 §6의 TLS/HTTPS가 필요했습니다.)");

        } catch (java.net.UnknownHostException e) {
            System.out.println("\n[네트워크] 호스트를 찾을 수 없습니다(DNS 실패 또는 오프라인): " + e.getMessage());
            System.out.println("  → 인터넷 연결을 확인하세요. (개념 학습에는 지장 없습니다.)");
        } catch (java.net.SocketTimeoutException e) {
            System.out.println("\n[네트워크] 타임아웃: 서버 응답이 " + TIMEOUT_MS + "ms 안에 오지 않았습니다.");
        } catch (IOException e) {
            System.out.println("\n[네트워크] 연결/입출력 오류: " + e.getMessage());
            System.out.println("  → 방화벽/프록시/오프라인 여부를 확인하세요.");
        }
    }

    /** 요청 원문을 눈에 보이게 출력한다(\r\n을 표시하여 CRLF 구조를 드러냄). */
    private static void printRaw(String raw) {
        // 요청은 "...Connection: close\r\n\r\n" 으로 끝난다.
        // 마지막 "\r\n\r\n"(헤더 끝 빈 줄)은 한 번만 표시하도록, 끝의 빈 조각들을 정리한다.
        String[] lines = raw.split("\r\n", -1);
        int end = lines.length;
        while (end > 0 && lines[end - 1].isEmpty()) {
            end--; // 뒤쪽 빈 조각 제거
        }
        for (int i = 0; i < end; i++) {
            System.out.println("  | " + lines[i] + "\\r\\n");
        }
        System.out.println("  |(빈 줄=\\r\\n\\r\\n → 헤더 끝)");
        System.out.println();
    }
}
