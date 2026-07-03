package com.edu.javabook.ch19;

/**
 * 19.1 네트워크 기초
 *
 * [네트워크란]
 * - 두 대 이상의 컴퓨터가 케이블/무선으로 연결되어 데이터를 주고받는 환경이다.
 * - 자바는 java.net 패키지로 네트워크 프로그래밍을 표준 지원한다.
 *
 * [IP 주소(IP Address)]
 * - 네트워크에 연결된 컴퓨터를 식별하는 주소다. (예: 192.168.0.10)
 * - IPv4: 32비트, 점(.)으로 구분된 4개의 숫자. (예: 127.0.0.1)
 * - IPv6: 128비트, 콜론(:)으로 구분. (예: ::1)
 * - loopback 주소 127.0.0.1(localhost)은 "자기 자신"을 가리킨다.
 *
 * [포트(Port)]
 * - 하나의 컴퓨터 안에서 실행 중인 여러 프로그램을 구분하는 번호(0~65535)다.
 * - 0~1023은 well-known 포트(예: HTTP 80, HTTPS 443), 1024 이상은 일반적으로 사용자용.
 * - "IP 주소 + 포트"가 통신의 종착지(endpoint)를 결정한다.
 *
 * [프로토콜(Protocol)]
 * - 데이터를 주고받기 위한 약속(규칙)이다.
 * - TCP: 연결 지향, 신뢰성 보장(순서/재전송/오류검출). 전화 통화에 비유.
 * - UDP: 비연결, 빠르지만 신뢰성 보장 안 함. 편지/방송에 비유.
 *
 * 이 소절은 개념 정리가 목적이므로 코드는 최소로만 시연한다.
 */
public class NetworkBasics {

    public static void main(String[] args) {

        System.out.println("=== 19.1 네트워크 기초 ===");

        // [1] IP 주소 개념
        System.out.println("\n[1] IP 주소");
        System.out.println("  IPv4 loopback : 127.0.0.1 (자기 자신)");
        System.out.println("  IPv6 loopback : ::1");
        System.out.println("  도메인 이름   : localhost → 127.0.0.1 로 변환됨");

        // [2] 포트 개념
        System.out.println("\n[2] 포트(0~65535)");
        System.out.println("  well-known : 80(HTTP), 443(HTTPS), 22(SSH)");
        System.out.println("  endpoint = IP + Port (예: 127.0.0.1:8080)");

        // [3] TCP vs UDP 개요
        System.out.println("\n[3] TCP vs UDP");
        System.out.println("  구분      | TCP              | UDP");
        System.out.println("  ---------+------------------+------------------");
        System.out.println("  연결여부  | 연결지향         | 비연결");
        System.out.println("  신뢰성    | 보장(순서/재전송) | 미보장");
        System.out.println("  속도      | 상대적으로 느림   | 빠름");
        System.out.println("  용도      | 파일전송/웹/채팅  | 스트리밍/게임/DNS");

        System.out.println("\n프로그램 정상 종료");
    }
}
