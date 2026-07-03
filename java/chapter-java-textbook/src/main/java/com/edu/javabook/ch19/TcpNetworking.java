package com.edu.javabook.ch19;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 19.3 TCP 네트워킹
 *
 * [TCP 통신의 흐름]
 * - 서버: ServerSocket 을 특정 포트에 열고 accept() 로 클라이언트 연결을 기다린다.
 * - 클라이언트: Socket 으로 서버의 IP:포트에 연결(connect)한다.
 * - 연결이 맺어지면 양쪽 모두 Socket 을 통해 입출력 스트림으로 데이터를 주고받는다.
 *
 * [4-튜플(4-tuple)]
 * - 하나의 TCP 연결은 (출발지 IP, 출발지 포트, 목적지 IP, 목적지 포트) 로 유일하게 식별된다.
 *
 * [자기완결 구성]
 * - ServerSocket(0) : 포트 0 → OS가 사용 가능한 임의 포트를 자동 할당(포트 충돌 방지).
 * - 서버는 별도 스레드에서 accept 후 echo(받은 문자열을 그대로 돌려줌) 한 뒤 종료.
 * - setSoTimeout / join 으로 절대 hang 되지 않고 수초 내 종료한다.
 *
 * 여기서는 loopback(127.0.0.1)에서 서버-클라이언트가 한 프로세스 안에서 통신한다.
 */
public class TcpNetworking {

    public static void main(String[] args) throws Exception {

        System.out.println("=== 19.3 TCP 네트워킹 ===");

        // [1] 서버 소켓을 포트 0(자동 할당)으로 loopback 에 바인딩
        ServerSocket serverSocket = new ServerSocket();
        serverSocket.bind(new InetSocketAddress("127.0.0.1", 0));
        serverSocket.setSoTimeout(5000); // accept 가 5초 넘게 막히면 예외 → hang 방지
        int port = serverSocket.getLocalPort();
        System.out.println("\n[1] 서버 시작 (127.0.0.1:" + port + ")");

        // [2] 서버 스레드: 클라이언트 한 명을 받아 echo 후 종료
        Thread serverThread = new Thread(() -> {
            try (Socket client = serverSocket.accept()) {
                client.setSoTimeout(5000);
                // 서버 관점의 4-튜플 출력
                System.out.println("  [서버] 연결 수락");
                System.out.println("  [서버] 원격(클라이언트): " + client.getRemoteSocketAddress());
                System.out.println("  [서버] 로컬(서버)      : " + client.getLocalSocketAddress());

                BufferedReader in = new BufferedReader(
                        new InputStreamReader(client.getInputStream()));
                PrintWriter out = new PrintWriter(client.getOutputStream(), true);

                String line = in.readLine();      // 클라이언트가 보낸 한 줄 수신
                System.out.println("  [서버] 수신: " + line);
                out.println("echo:" + line);       // 그대로 되돌려줌
            } catch (Exception e) {
                System.out.println("  [서버] 오류: " + e.getMessage());
            } finally {
                try { serverSocket.close(); } catch (Exception ignore) {}
            }
        });
        serverThread.start();

        // [3] 클라이언트: 서버에 연결하여 메시지 송신 후 응답 수신
        System.out.println("\n[2] 클라이언트 연결 및 송수신");
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress("127.0.0.1", port), 5000);
            socket.setSoTimeout(5000);

            System.out.println("  [클라] 로컬(클라이언트): " + socket.getLocalSocketAddress());
            System.out.println("  [클라] 원격(서버)      : " + socket.getRemoteSocketAddress());

            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));

            out.println("hello tcp");             // 서버로 송신
            String reply = in.readLine();          // 서버 응답 수신
            System.out.println("  [클라] 서버 응답: " + reply);
        }

        // [4] 서버 스레드가 반드시 끝나도록 join (타임아웃 포함)
        serverThread.join(6000);
        System.out.println("\n[3] 서버 스레드 종료 여부: " + !serverThread.isAlive());

        System.out.println("\n프로그램 정상 종료");
    }
}
