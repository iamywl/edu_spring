package com.edu.javabook.ch19;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * 19.2 IP 주소 얻기
 *
 * [InetAddress 클래스]
 * - java.net.InetAddress 는 IP 주소를 표현하는 클래스다.
 * - 생성자가 없고 정적 팩터리 메서드로 객체를 얻는다.
 *   getLocalHost()      : 현재 실행 중인 컴퓨터(로컬 호스트)의 IP
 *   getByName(호스트명)  : 호스트 이름을 IP로 변환(이름 해석, DNS lookup)
 *   getAllByName(호스트명): 여러 IP를 배열로 반환
 *
 * [예외 처리]
 * - 이름 해석에 실패하면 UnknownHostException(검사 예외)이 발생하므로
 *   반드시 try-catch 또는 throws 로 처리해야 한다.
 *
 * 이 소절은 인터넷 없이 loopback(localhost)만으로 자기완결적으로 동작한다.
 */
public class GetIpAddress {

    public static void main(String[] args) {

        System.out.println("=== 19.2 IP 주소 얻기 ===");

        // [1] 로컬 호스트의 IP 얻기
        System.out.println("\n[1] getLocalHost()");
        try {
            InetAddress local = InetAddress.getLocalHost();
            System.out.println("  호스트 이름 : " + local.getHostName());
            System.out.println("  IP 주소     : " + local.getHostAddress());
        } catch (UnknownHostException e) {
            System.out.println("  로컬 호스트 이름 해석 실패: " + e.getMessage());
        }

        // [2] 이름으로 IP 얻기 (localhost 는 인터넷 없이도 해석됨)
        System.out.println("\n[2] getByName(\"localhost\")");
        try {
            InetAddress loopback = InetAddress.getByName("localhost");
            System.out.println("  호스트 이름 : " + loopback.getHostName());
            System.out.println("  IP 주소     : " + loopback.getHostAddress());
            System.out.println("  loopback 여부: " + loopback.isLoopbackAddress());
        } catch (UnknownHostException e) {
            System.out.println("  이름 해석 실패: " + e.getMessage());
        }

        // [3] IP 문자열로 직접 얻기
        System.out.println("\n[3] getByName(\"127.0.0.1\")");
        try {
            InetAddress ip = InetAddress.getByName("127.0.0.1");
            System.out.println("  IP 주소     : " + ip.getHostAddress());
            System.out.println("  loopback 여부: " + ip.isLoopbackAddress());
        } catch (UnknownHostException e) {
            System.out.println("  이름 해석 실패: " + e.getMessage());
        }

        // [4] 존재하지 않는 이름 → 예외 발생 시연
        System.out.println("\n[4] 잘못된 호스트 이름 예외 처리");
        try {
            InetAddress.getByName("this.host.does.not.exist.invalid");
            System.out.println("  (예외가 발생하지 않았음)");
        } catch (UnknownHostException e) {
            System.out.println("  예상대로 UnknownHostException 발생: 이름 해석 불가");
        }

        System.out.println("\n프로그램 정상 종료");
    }
}
