package com.edu.javabook.ch19;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 19.5 서버의 동시 요청 처리
 *
 * [문제 상황]
 * - 단일 스레드 서버는 accept() → 요청 처리 → 다음 accept() 순서로만 동작하므로,
 *   한 클라이언트를 처리하는 동안 다른 클라이언트는 대기해야 한다.
 *
 * [해결: 스레드 풀(ExecutorService)]
 * - accept() 로 연결을 받을 때마다 처리 작업을 스레드 풀에 제출(submit)한다.
 * - 여러 클라이언트를 동시에(병렬로) 처리할 수 있다.
 * - Executors.newFixedThreadPool(n) 으로 스레드 개수를 제한해 자원을 관리한다.
 *
 * [자기완결 & hang 방지]
 * - ServerSocket(0) 자동 포트, loopback 통신.
 * - 서버는 정해진 요청 수(3개)를 채우면 스스로 종료한다.
 * - setSoTimeout, join, awaitTermination 으로 반드시 수초 내 종료한다.
 */
public class ConcurrentServer {

    static final int CLIENT_COUNT = 3; // 처리할 클라이언트 수

    public static void main(String[] args) throws Exception {

        System.out.println("=== 19.5 서버의 동시 요청 처리 ===");

        // [1] 서버 소켓 열기 (자동 포트, loopback)
        ServerSocket serverSocket = new ServerSocket();
        serverSocket.bind(new InetSocketAddress("127.0.0.1", 0));
        serverSocket.setSoTimeout(5000);
        int port = serverSocket.getLocalPort();
        System.out.println("\n[1] 서버 시작 (127.0.0.1:" + port + "), 요청 " + CLIENT_COUNT + "개 처리 후 종료");

        // [2] 서버 스레드: accept 후 스레드 풀에서 각 클라이언트 병렬 처리
        ExecutorService workerPool = Executors.newFixedThreadPool(CLIENT_COUNT);
        Thread serverThread = new Thread(() -> {
            try {
                for (int i = 0; i < CLIENT_COUNT; i++) {
                    Socket client = serverSocket.accept();
                    workerPool.submit(() -> handleClient(client));
                }
            } catch (Exception e) {
                System.out.println("  [서버] 오류: " + e.getMessage());
            } finally {
                try { serverSocket.close(); } catch (Exception ignore) {}
            }
        });
        serverThread.start();

        // [3] 클라이언트 3개를 각각 스레드로 동시에 접속
        System.out.println("\n[2] 클라이언트 3개 동시 접속");
        Thread[] clients = new Thread[CLIENT_COUNT];
        for (int i = 0; i < CLIENT_COUNT; i++) {
            final int id = i + 1;
            clients[i] = new Thread(() -> connectAndTalk(port, id));
            clients[i].start();
        }

        // [4] 모든 스레드가 반드시 종료되도록 join / 종료 처리
        for (Thread c : clients) c.join(6000);
        serverThread.join(6000);
        workerPool.shutdown();
        boolean done = workerPool.awaitTermination(6, TimeUnit.SECONDS);

        System.out.println("\n[3] 워커 풀 정상 종료: " + done);
        System.out.println("\n프로그램 정상 종료");
    }

    // 서버 측: 한 클라이언트를 처리(요청 받아 응답)
    static void handleClient(Socket client) {
        try (Socket c = client) {
            c.setSoTimeout(5000);
            BufferedReader in = new BufferedReader(new InputStreamReader(c.getInputStream()));
            PrintWriter out = new PrintWriter(c.getOutputStream(), true);
            String req = in.readLine();
            System.out.println("  [서버] " + Thread.currentThread().getName() + " 처리 요청: " + req);
            out.println("응답:" + req);
        } catch (Exception e) {
            System.out.println("  [서버] 처리 오류: " + e.getMessage());
        }
    }

    // 클라이언트 측: 접속하여 요청 보내고 응답 수신
    static void connectAndTalk(int port, int id) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress("127.0.0.1", port), 5000);
            socket.setSoTimeout(5000);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out.println("클라이언트#" + id + " 요청");
            String reply = in.readLine();
            System.out.println("  [클라#" + id + "] 서버 응답: " + reply);
        } catch (Exception e) {
            System.out.println("  [클라#" + id + "] 오류: " + e.getMessage());
        }
    }
}
