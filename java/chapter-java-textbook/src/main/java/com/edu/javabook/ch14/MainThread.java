package com.edu.javabook.ch14;

/**
 * 14.2 메인 스레드
 *
 * - 자바 프로그램이 시작되면 JVM 은 main() 메서드를 실행하기 위해 "메인 스레드"를 만든다.
 * - 즉 main() 도 하나의 스레드 위에서 동작한다. 우리가 늘 쓰던 코드가 이미 멀티 스레드의 일부였던 셈.
 * - 메인 스레드가 작업 스레드를 만들면 스레드가 여러 개가 된다.
 *
 * [종료 조건]
 * - 프로그램(프로세스)은 "실행 중인 모든 (데몬이 아닌) 스레드"가 끝나야 종료된다.
 * - 따라서 메인 스레드가 먼저 끝나도, 사용자가 만든 일반 스레드가 살아 있으면 프로그램은 끝나지 않는다.
 *
 * Thread.currentThread() 로 현재 실행 중인 스레드 객체를 얻어 이름/우선순위 등을 확인한다.
 */
public class MainThread {

    public static void main(String[] args) throws InterruptedException {

        System.out.println("=== 14.2 메인 스레드 ===");

        // [1] 현재 스레드 = 메인 스레드
        System.out.println("\n[1] main() 을 실행하는 스레드 확인");
        Thread main = Thread.currentThread();
        System.out.println("현재 스레드 이름     : " + main.getName());       // 보통 "main"
        System.out.println("현재 스레드 우선순위 : " + main.getPriority());   // 기본 5
        System.out.println("현재 스레드 ID       : " + main.getId());
        System.out.println("살아 있는가          : " + main.isAlive());
        System.out.println("데몬 스레드인가      : " + main.isDaemon());

        // [2] 메인 스레드가 작업 스레드를 만든다 → 스레드가 2개가 됨
        System.out.println("\n[2] 메인 스레드가 작업 스레드 생성");
        Thread worker = new Thread(() -> {
            System.out.println("  작업 스레드 실행 중: " + Thread.currentThread().getName());
        }, "작업-스레드");
        worker.start();
        worker.join();   // 작업 스레드가 끝날 때까지 메인 스레드 대기

        // [3] 종료 조건 설명
        System.out.println("\n[3] 종료 조건");
        System.out.println("프로그램은 데몬이 아닌 모든 스레드가 끝나야 종료된다.");
        System.out.println("여기서는 작업 스레드를 join() 으로 기다렸으므로 안전하게 종료된다.");

        System.out.println("\n메인 스레드 종료 → 프로그램 정상 종료");
    }
}
