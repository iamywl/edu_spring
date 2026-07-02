package com.edu.javabook.ch14;

/**
 * 14.1 멀티 스레드 개념
 *
 * [프로세스 vs 스레드]
 * - 프로세스(Process) : 실행 중인 하나의 프로그램. 운영체제로부터 독립된 메모리(코드/힙/스택)를 할당받는다.
 * - 스레드(Thread)    : 프로세스 내부에서 실제로 코드를 실행하는 흐름의 단위.
 *                      하나의 프로세스는 여러 스레드를 가질 수 있다(멀티 스레드).
 *
 * [동시성(Concurrency) 개요]
 * - 하나의 CPU 코어가 여러 스레드를 아주 빠르게 번갈아 실행하면 마치 동시에 실행되는 것처럼 보인다.
 * - 코어가 여러 개면 실제로 병렬(Parallelism)로 실행되기도 한다.
 * - 멀티 스레드를 쓰면 대기 시간이 긴 작업(네트워크/파일)을 기다리는 동안 다른 일을 할 수 있어 효율적이다.
 *
 * [공유 힙 / 개별 스택]
 * - 같은 프로세스 안의 스레드들은 힙(heap) 영역과 메서드 영역을 "공유"한다.
 *   → 그래서 스레드끼리 같은 객체/변수에 접근할 수 있고, 이 때문에 동기화 문제가 생긴다(14.6).
 * - 반면 각 스레드는 자신만의 "스택(stack)"을 가진다.
 *   → 지역 변수, 메서드 호출 정보는 스레드마다 독립적이다.
 *
 * 이 소절에서는 개념을 출력으로 확인하고, 힙 공유/스택 독립을 코드로 간단히 시연한다.
 */
public class ThreadConcept {

    // 힙에 존재하는 공유 객체(모든 스레드가 접근 가능)
    static int sharedHeapValue = 0;

    public static void main(String[] args) throws InterruptedException {

        System.out.println("=== 14.1 멀티 스레드 개념 ===");

        // [1] 프로세스 vs 스레드 개념 출력
        System.out.println("\n[1] 프로세스 vs 스레드");
        System.out.println("프로세스: 실행 중인 프로그램(독립 메모리 공간)");
        System.out.println("스레드  : 프로세스 안에서 코드를 실행하는 흐름(하나의 프로세스에 여러 개 가능)");

        // [2] 현재 실행 환경의 사용 가능한 CPU 코어 수(동시성/병렬성의 기반)
        System.out.println("\n[2] 동시성 개요");
        int cores = Runtime.getRuntime().availableProcessors();
        System.out.println("사용 가능한 CPU 코어 수: " + cores);
        System.out.println("코어보다 스레드가 많으면 빠르게 번갈아 실행되어 동시에 도는 것처럼 보인다.");

        // [3] 공유 힙: 두 스레드가 같은 static 변수(힙 영역 객체)에 접근
        System.out.println("\n[3] 공유 힙 시연 (static 변수는 모든 스레드가 공유)");
        Thread t1 = new Thread(() -> sharedHeapValue += 100);
        Thread t2 = new Thread(() -> sharedHeapValue += 200);
        t1.start();
        t2.start();
        t1.join();   // t1 종료 대기
        t2.join();   // t2 종료 대기
        System.out.println("두 스레드가 공유 변수에 더한 결과: " + sharedHeapValue + " (기대값 300)");

        // [4] 개별 스택: 각 스레드의 지역 변수는 독립적
        System.out.println("\n[4] 개별 스택 시연 (지역 변수는 스레드마다 독립)");
        Runnable localWork = () -> {
            int localVar = 0;          // 이 변수는 각 스레드의 스택에 따로 존재
            for (int i = 0; i < 5; i++) {
                localVar += i;
            }
            System.out.println("  " + Thread.currentThread().getName()
                    + " 의 지역 변수 합계: " + localVar);
        };
        Thread s1 = new Thread(localWork, "스레드-A");
        Thread s2 = new Thread(localWork, "스레드-B");
        s1.start();
        s2.start();
        s1.join();
        s2.join();
        System.out.println("→ 두 스레드가 같은 코드를 실행해도 지역 변수는 서로 간섭하지 않는다.");

        System.out.println("\n프로그램 정상 종료");
    }
}
