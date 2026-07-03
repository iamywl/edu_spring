package com.edu.javabook.ch19;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * 19.4 UDP 네트워킹
 *
 * [UDP 통신의 특징]
 * - 연결(connect) 과정 없이 데이터그램(datagram) 이라는 패킷 단위로 주고받는다.
 * - DatagramSocket : 송수신에 사용하는 소켓.
 * - DatagramPacket : 실제 데이터 + 목적지(또는 출발지) 주소/포트를 담는 패킷.
 * - 신뢰성을 보장하지 않으므로(순서/도착 미보장) 빠르지만 유실될 수 있다.
 *
 * [hang 방지]
 * - receive() 는 패킷이 올 때까지 무한정 대기하므로,
 *   setSoTimeout(밀리초) 로 타임아웃을 설정해 반드시 수초 내 반환되게 한다.
 *
 * 여기서는 loopback(127.0.0.1)에서 수신 소켓과 송신 소켓이 한 프로세스 안에서 통신한다.
 */
public class UdpNetworking {

    public static void main(String[] args) throws Exception {

        System.out.println("=== 19.4 UDP 네트워킹 ===");

        InetAddress loopback = InetAddress.getByName("127.0.0.1");

        // [1] 수신용 소켓: 포트 0 → OS가 임의 포트 자동 할당
        DatagramSocket receiver = new DatagramSocket(0, loopback);
        receiver.setSoTimeout(5000); // 5초 내 미수신 시 예외 → hang 방지
        int recvPort = receiver.getLocalPort();
        System.out.println("\n[1] 수신 소켓 대기 (127.0.0.1:" + recvPort + ")");

        // [2] 송신용 소켓: 수신 소켓으로 데이터그램 전송
        System.out.println("\n[2] 송신");
        try (DatagramSocket sender = new DatagramSocket()) {
            byte[] data = "hello udp".getBytes();
            DatagramPacket sendPacket =
                    new DatagramPacket(data, data.length, loopback, recvPort);
            sender.send(sendPacket);
            System.out.println("  [송신] \"hello udp\" → 127.0.0.1:" + recvPort);
        }

        // [3] 수신
        System.out.println("\n[3] 수신");
        byte[] buffer = new byte[1024];
        DatagramPacket recvPacket = new DatagramPacket(buffer, buffer.length);
        try {
            receiver.receive(recvPacket); // 타임아웃 내 수신
            String received = new String(recvPacket.getData(), 0, recvPacket.getLength());
            System.out.println("  [수신] 데이터: " + received);
            System.out.println("  [수신] 보낸 곳: "
                    + recvPacket.getAddress().getHostAddress() + ":" + recvPacket.getPort());
        } catch (java.net.SocketTimeoutException e) {
            System.out.println("  [수신] 타임아웃(패킷 미도착)");
        } finally {
            receiver.close();
        }

        System.out.println("\n프로그램 정상 종료");
    }
}
