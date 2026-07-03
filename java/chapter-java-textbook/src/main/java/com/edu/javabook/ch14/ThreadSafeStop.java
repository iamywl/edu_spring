package com.edu.javabook.ch14;

/**
 * 14.7 스레드 안전 종료
 *
 * [stop() 을 쓰면 안 되는 이유]
 * - Thread.stop() 은 스레드를 강제로 즉시 죽인다. 이때 잡고 있던 락이 해제되고
 *   공유 객체가 "중간 상태(불완전한 상태)" 로 남아 데이터가 깨질 수 있다. → 그래서 deprecated(사용 금지).
 *
 * [안전한 종료 방법]
 * - (1) volatile 플래그 : 스레드가 반복문 조건으로 boolean 플래그를 검사하고,
 *       외부에서 그 플래그를 false 로 바꾸면 스스로 루프를 빠져나가 정상 종료한다.
 *       (volatile 은 여러 스레드 간 값의 변경이 즉시 보이도록 보장한다.)
 * - (2) interrupt()    : 스레드에 "중단 요청"을 보낸다. sleep/wait 중이면 InterruptedException 이 발생하고,
 *       실행 중이면 isInterrupted() 로 확인해 스스로 정리 후 종료할 수 있다.
 *
 * 두 방식 모두 "스레드가 스스로 마무리하고 종료" 하므로 안전하다.
 */
public class ThreadSafeStop {

    // volatile: 다른 스레드가 바꾼 값이 즉시 반영되도록 보장
    static volatile boolean running = true;

    public static void main(String[] args) throws InterruptedException {

        System.out.println("=== 14.7 스레드 안전 종료 ===");
        System.out.println("stop() 은 락/데이터를 깨뜨릴 수 있어 사용 금지(deprecated).");

        // [1] volatile 플래그로 안전 종료
        System.out.println("\n[1] volatile 플래그 방식");
        Thread worker = new Thread(() -> {
            long tick = 0;
            while (running) {          // 플래그가 false 가 되면 루프 종료
                tick++;
            }
            System.out.println("  volatile 스레드: 플래그 false 감지 → 정리 후 정상 종료 (tick=" + tick + ")");
        }, "volatile-worker");
        worker.start();
        Thread.sleep(200);             // 잠깐 일하게 둔다
        running = false;               // 종료 요청
        worker.join(2000);             // 최대 2초 대기(타임아웃으로 hang 방지)
        System.out.println("volatile 스레드 살아있음? " + worker.isAlive());

        // [2] interrupt() 로 안전 종료 (sleep 중 InterruptedException 발생)
        System.out.println("\n[2] interrupt() 방식");
        Thread sleeper = new Thread(() -> {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    System.out.println("  interrupt 스레드: 작업 중... (interrupt 대기)");
                    Thread.sleep(100);     // 인터럽트되면 여기서 예외 발생
                }
            } catch (InterruptedException e) {
                System.out.println("  interrupt 스레드: InterruptedException 감지 → 정리 후 정상 종료");
            }
        }, "interrupt-worker");
        sleeper.start();
        Thread.sleep(250);             // 몇 번 일하게 둔다
        sleeper.interrupt();           // 중단 요청
        sleeper.join(2000);            // 타임아웃 대기(hang 방지)
        System.out.println("interrupt 스레드 살아있음? " + sleeper.isAlive());

        System.out.println("\n프로그램 정상 종료");
    }
}
