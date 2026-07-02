package com.edu.javabook.ch19;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 19.7 TCP 채팅 프로그램
 *
 * [간이 채팅 원리]
 * - TCP 연결이 맺어지면 양쪽은 하나의 소켓을 통해 서로 여러 줄의 메시지를 주고받는다.
 * - 서버와 클라이언트가 번갈아 가며 읽고(readLine) 쓰기(println) 를 반복한다.
 *
 * [자기완결 & hang 방지]
 * - ServerSocket(0) 자동 포트 + loopback(127.0.0.1) 통신.
 * - 서버 스레드와 클라이언트 스레드가 한 프로세스 안에서 대화한다.
 * - 정해진 메시지 수(3왕복) 를 주고받은 뒤 스스로 종료한다.
 * - setSoTimeout / join 으로 반드시 수초 내 정상 종료한다.
 *
 * [프로토콜(약속)]
 * - 클라이언트가 먼저 한 줄 보내면 서버가 한 줄 답한다. 이를 ROUNDS 번 반복한다.
 */
public class TcpChat {

    static final int ROUNDS = 3; // 메시지 왕복 횟수

    public static void main(String[] args) throws Exception {

        System.out.println("=== 19.7 TCP 채팅 프로그램 ===");

        // [1] 서버 소켓 열기 (자동 포트, loopback)
        ServerSocket serverSocket = new ServerSocket();
        serverSocket.bind(new InetSocketAddress("127.0.0.1", 0));
        serverSocket.setSoTimeout(5000);
        int port = serverSocket.getLocalPort();
        System.out.println("\n[1] 채팅 서버 시작 (127.0.0.1:" + port + "), " + ROUNDS + "회 대화");

        // [2] 서버 스레드: 클라이언트 메시지를 받고 응답을 ROUNDS 번 반복
        Thread serverThread = new Thread(() -> {
            try (Socket client = serverSocket.accept()) {
                client.setSoTimeout(5000);
                BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                PrintWriter out = new PrintWriter(client.getOutputStream(), true);
                for (int i = 1; i <= ROUNDS; i++) {
                    String msg = in.readLine();               // 클라이언트 메시지 수신
                    System.out.println("  [서버] 받음: " + msg);
                    out.println("[" + i + "] 서버가 인사드립니다");  // 서버 응답 송신
                }
            } catch (Exception e) {
                System.out.println("  [서버] 오류: " + e.getMessage());
            } finally {
                try { serverSocket.close(); } catch (Exception ignore) {}
            }
        });
        serverThread.start();

        // [3] 클라이언트 스레드: 메시지를 보내고 응답을 ROUNDS 번 반복
        System.out.println("\n[2] 클라이언트 접속 및 대화");
        Thread clientThread = new Thread(() -> {
            try (Socket socket = new Socket()) {
                socket.connect(new InetSocketAddress("127.0.0.1", port), 5000);
                socket.setSoTimeout(5000);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                for (int i = 1; i <= ROUNDS; i++) {
                    out.println("[" + i + "] 안녕하세요 서버님");   // 클라이언트 메시지 송신
                    String reply = in.readLine();                // 서버 응답 수신
                    System.out.println("  [클라] 받음: " + reply);
                }
            } catch (Exception e) {
                System.out.println("  [클라] 오류: " + e.getMessage());
            }
        });
        clientThread.start();

        // [4] 두 스레드가 반드시 종료되도록 join
        clientThread.join(6000);
        serverThread.join(6000);
        System.out.println("\n[3] 대화 종료 (서버 alive=" + serverThread.isAlive()
                + ", 클라 alive=" + clientThread.isAlive() + ")");

        System.out.println("\n프로그램 정상 종료");
    }
}
