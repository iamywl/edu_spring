package com.edu.javabook.ch14;

/**
 * 14.4 스레드 이름
 *
 * - 모든 스레드에는 이름이 있다. 디버깅/로그에서 어느 스레드가 실행했는지 구분할 때 유용하다.
 * - getName() : 스레드 이름을 얻는다.
 * - setName() : 스레드 이름을 바꾼다.
 * - 이름을 지정하지 않으면 JVM 이 자동으로 "Thread-0", "Thread-1" ... 형태의 기본 이름을 붙인다.
 * - 생성자 Thread(Runnable, String) 로 만들 때 이름을 직접 지정할 수도 있다.
 * - 메인 스레드의 기본 이름은 "main" 이다.
 */
public class ThreadName {

    public static void main(String[] args) throws InterruptedException {

        System.out.println("=== 14.4 스레드 이름 ===");

        // [1] 메인 스레드의 기본 이름
        System.out.println("\n[1] 메인 스레드 이름: " + Thread.currentThread().getName());

        // [2] 이름을 지정하지 않은 스레드의 기본 이름 (Thread-0, Thread-1 ...)
        System.out.println("\n[2] 기본 이름 (지정 안 함)");
        Thread d1 = new Thread(() ->
                System.out.println("  실행: " + Thread.currentThread().getName()));
        Thread d2 = new Thread(() ->
                System.out.println("  실행: " + Thread.currentThread().getName()));
        System.out.println("d1 기본 이름: " + d1.getName());
        System.out.println("d2 기본 이름: " + d2.getName());
        d1.start(); d1.join();
        d2.start(); d2.join();

        // [3] 생성자로 이름 지정
        System.out.println("\n[3] 생성자로 이름 지정");
        Thread named = new Thread(() ->
                System.out.println("  실행: " + Thread.currentThread().getName()), "결제-처리-스레드");
        System.out.println("지정한 이름: " + named.getName());
        named.start();
        named.join();

        // [4] setName() 으로 이름 변경
        System.out.println("\n[4] setName() 으로 이름 변경");
        Thread renamed = new Thread(() ->
                System.out.println("  실행: " + Thread.currentThread().getName()));
        System.out.println("변경 전 이름: " + renamed.getName());
        renamed.setName("주문-처리-스레드");
        System.out.println("변경 후 이름: " + renamed.getName());
        renamed.start();
        renamed.join();

        System.out.println("\n프로그램 정상 종료");
    }
}
