package com.edu.javabook.ch14;

/**
 * 14.3 작업 스레드 생성과 실행
 *
 * 작업 스레드를 만드는 대표적인 방법 3가지:
 *   (1) Thread 클래스를 상속받아 run() 재정의
 *   (2) Runnable 인터페이스를 구현하여 Thread 에 전달
 *   (3) 람다식으로 Runnable 을 간단히 전달 (자바 8 이상)
 *
 * [start() vs run()]
 * - run()   : 그냥 메서드 호출. "현재 스레드"에서 순차 실행된다(새 스레드 안 생김!).
 * - start() : JVM 에게 새 스레드 생성을 요청 → 새 스레드가 run() 을 실행한다.
 * → 반드시 start() 를 호출해야 병렬 실행이 된다.
 */
public class ThreadCreation {

    // (1) Thread 상속 방식
    static class MyThread extends Thread {
        @Override
        public void run() {
            System.out.println("  [상속] 실행 스레드: " + Thread.currentThread().getName());
        }
    }

    // (2) Runnable 구현 방식
    static class MyTask implements Runnable {
        @Override
        public void run() {
            System.out.println("  [Runnable] 실행 스레드: " + Thread.currentThread().getName());
        }
    }

    public static void main(String[] args) throws InterruptedException {

        System.out.println("=== 14.3 작업 스레드 생성과 실행 ===");

        // [1] Thread 상속
        System.out.println("\n[1] Thread 클래스 상속");
        Thread t1 = new MyThread();
        t1.start();
        t1.join();

        // [2] Runnable 구현
        System.out.println("\n[2] Runnable 인터페이스 구현");
        Thread t2 = new Thread(new MyTask());
        t2.start();
        t2.join();

        // [3] 람다식
        System.out.println("\n[3] 람다식으로 Runnable 전달");
        Thread t3 = new Thread(() ->
                System.out.println("  [람다] 실행 스레드: " + Thread.currentThread().getName()));
        t3.start();
        t3.join();

        // [4] start() vs run() 차이
        System.out.println("\n[4] start() vs run() 차이");
        Runnable who = () ->
                System.out.println("  실행 스레드 이름: " + Thread.currentThread().getName());

        System.out.println("run() 직접 호출 (새 스레드 안 생김, main 에서 실행):");
        new Thread(who, "새스레드-run").run();     // main 스레드에서 실행됨

        System.out.println("start() 호출 (새 스레드 생성):");
        Thread t4 = new Thread(who, "새스레드-start");
        t4.start();
        t4.join();
        System.out.println("→ run() 은 'main', start() 는 '새스레드-start' 로 찍히는 점에 주목.");

        System.out.println("\n프로그램 정상 종료");
    }
}
