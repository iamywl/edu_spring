package com.edu.javabook.ch14;

/**
 * 14.5 스레드 상태
 *
 * 스레드는 생성부터 소멸까지 여러 상태를 거치며, Thread.getState() 로 확인할 수 있다.
 * (java.lang.Thread.State 열거형)
 *
 *   NEW            : 스레드 객체를 생성했지만 아직 start() 하지 않은 상태
 *   RUNNABLE       : start() 호출 후 실행 중이거나 실행 대기 중인 상태
 *   TIMED_WAITING  : sleep(ms), join(ms), wait(ms) 처럼 "시간 제한이 있는" 대기 상태
 *   WAITING        : 시간 제한 없이 대기(join(), wait())
 *   BLOCKED        : synchronized 락을 얻으려고 기다리는 상태
 *   TERMINATED     : run() 이 끝나 완전히 종료된 상태
 *
 * 이 소절에서는 NEW → RUNNABLE → TIMED_WAITING → TERMINATED 흐름을 관찰한다.
 */
public class ThreadState {

    public static void main(String[] args) throws InterruptedException {

        System.out.println("=== 14.5 스레드 상태 ===");

        // 잠깐 sleep 하여 TIMED_WAITING 상태를 관찰할 수 있게 만든 스레드
        Thread t = new Thread(() -> {
            try {
                Thread.sleep(300);   // 이 동안 TIMED_WAITING
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "상태관찰-스레드");

        // [1] NEW : 아직 start() 하지 않음
        System.out.println("\n[1] start() 전 상태: " + t.getState() + " (기대: NEW)");

        // [2] RUNNABLE : start() 직후
        t.start();
        System.out.println("[2] start() 직후 상태: " + t.getState() + " (기대: RUNNABLE)");

        // [3] TIMED_WAITING : sleep 중인 것을 잡아내기 위해 잠깐 여유를 준 뒤 확인
        Thread.sleep(100);   // 대상 스레드가 sleep(300) 에 진입할 시간을 준다
        System.out.println("[3] sleep 중 상태  : " + t.getState() + " (기대: TIMED_WAITING)");

        // [4] TERMINATED : 스레드가 끝날 때까지 기다린 뒤 확인
        t.join();
        System.out.println("[4] 종료 후 상태   : " + t.getState() + " (기대: TERMINATED)");

        System.out.println("\n프로그램 정상 종료");
    }
}
