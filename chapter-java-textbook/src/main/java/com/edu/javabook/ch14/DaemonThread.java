package com.edu.javabook.ch14;

/**
 * 14.8 데몬 스레드
 *
 * - 데몬(daemon) 스레드는 "다른 일반 스레드를 돕는 보조 스레드" 이다.
 *   예: 가비지 컬렉터, 자동 저장, 로그 flush 같은 배경 작업.
 * - 핵심 특징: 데몬 스레드는 "일반(사용자) 스레드가 모두 끝나면 함께 강제 종료" 된다.
 *   즉, 메인 스레드가 끝나면 살아 있던 데몬 스레드도 즉시 멈춘다.
 * - setDaemon(true) 로 지정하며, 반드시 start() "이전" 에 호출해야 한다.
 *
 * 이 소절에서는 무한 반복하는 데몬 스레드를 만들어도, 메인 스레드가 끝나면
 * 프로그램이 정상 종료되는 것을 확인한다(그래서 hang 이 발생하지 않는다).
 */
public class DaemonThread {

    public static void main(String[] args) throws InterruptedException {

        System.out.println("=== 14.8 데몬 스레드 ===");

        // 무한 반복하는 배경 작업(데몬으로 지정할 예정)
        Thread daemon = new Thread(() -> {
            int count = 0;
            while (true) {                 // 일반 스레드였다면 프로그램이 끝나지 않겠지만...
                count++;
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    break;
                }
                if (count <= 3) {
                    System.out.println("  데몬 스레드 배경 작업 " + count + "회 수행");
                }
            }
        }, "배경-데몬");

        // [1] 데몬으로 지정 (start 이전에!)
        System.out.println("\n[1] setDaemon(true) 지정");
        daemon.setDaemon(true);
        System.out.println("데몬 스레드인가? " + daemon.isDaemon());
        daemon.start();

        // [2] 메인 스레드가 잠깐 일한 뒤 종료
        System.out.println("\n[2] 메인 스레드 작업 (약 0.5초)");
        Thread.sleep(500);

        // [3] 메인 스레드가 끝나면 데몬 스레드도 함께 종료됨
        System.out.println("\n[3] 메인 스레드 종료 예정");
        System.out.println("메인이 끝나면 무한 반복하던 데몬 스레드도 자동으로 함께 종료된다.");

        System.out.println("\n프로그램 정상 종료 (데몬 스레드도 같이 종료)");
    }
}
