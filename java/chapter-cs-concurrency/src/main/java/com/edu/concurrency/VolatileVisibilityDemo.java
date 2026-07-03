package com.edu.concurrency;

import java.util.concurrent.TimeUnit;

/**
 * CS 운영체제 트랙 (6) - 메모리 가시성(Visibility): "분명히 false로 바꿨는데 왜 안 멈추지?"
 *
 * SynchronizationDemo 가 "원자성"을 다뤘다면, 이 데모는 "가시성"을 다룬다.
 *
 *   - 가시성 문제: 한 스레드가 변수에 쓴 값을 다른 스레드가 '영영' 못 볼 수 있다.
 *     이유 ① 각 CPU 코어는 자기 캐시에 값을 두고 메인 메모리 반영을 미룬다.
 *     이유 ② JIT 컴파일러가 "이 변수는 이 스레드 안에서 안 바뀐다"고 판단하면
 *            while(flag) 를 while(true) 로 최적화(호이스팅)해 버릴 수 있다.
 *
 *   - 해결: volatile. "이 변수는 항상 메인 메모리에서 읽고, 쓰면 즉시 반영하라"는 표시.
 *     volatile 쓰기 → 이후의 volatile 읽기 사이에는 happens-before 관계가 성립하여,
 *     쓰기 이전의 모든 메모리 변경이 읽는 스레드에게 반드시 보인다.
 *
 * ★ 재현에 관한 중요한 주의 ★
 *   가시성 버그는 "반드시 터지는" 버그가 아니라 "터질 수도 있는" 버그다.
 *   JIT 워밍업 정도, CPU 아키텍처, 코어 수, OS 스케줄링에 따라
 *   volatile 없이도 우연히 잘 멈추는 경우가 있다. (특히 ARM/x86 차이, -Xint 실행 여부 등)
 *   → 1부에서 "멈추지 않음"이 재현되지 않아도 실패가 아니다. 출력의 해석 안내를 읽을 것.
 *   → 반대로 volatile 을 붙인 2부는 '어떤 환경에서든 항상' 즉시 멈추는 것이 보장된다.
 *     "우연히 되는 것"과 "보장되는 것"의 차이가 이 데모의 핵심이다.
 *
 * 안전장치: 1부의 워커는 데몬 스레드 + 타임아웃 관찰이므로 이 프로그램은 절대 hang 하지 않는다.
 */
public class VolatileVisibilityDemo {

    // ── 1부: volatile 없는 평범한 플래그 (가시성 보장 없음) ──
    static boolean plainFlag = true;          // ← volatile 없음! 워커가 낡은 true만 볼 수 있다

    // ── 2부: volatile 플래그 (가시성 보장) ──
    static volatile boolean volatileFlag = true;

    // ── 3부: happens-before 를 이용한 안전 발행(safe publication) ──
    static int payload = 0;                   // 일반 변수 (volatile 아님!)
    static volatile boolean published = false; // volatile 경계 역할

    public static void main(String[] args) throws Exception {
        System.out.println("=================================================");
        System.out.println(" CS(6) 메모리 가시성: volatile 과 happens-before");
        System.out.println("=================================================\n");

        part1_plainFlagMayNeverStop();
        part2_volatileFlagAlwaysStops();
        part3_happensBeforePublication();

        System.out.println("\n결론:");
        System.out.println("  - volatile 없는 플래그: 멈출 수도, 영영 못 멈출 수도 있다 (보장 없음).");
        System.out.println("  - volatile 플래그: 어떤 환경에서든 즉시 멈춘다 (JMM이 보장).");
        System.out.println("  - happens-before: volatile 쓰기 '이전'의 일반 쓰기까지 통째로 보이게 해 준다.");
        System.out.println("  - 단, volatile 은 가시성만! count++ 같은 복합 연산엔 여전히 무력하다");
        System.out.println("    (그건 SynchronizationDemo / AtomicCasDemo 의 영역).");
    }

    // ─────────────────────────────────────────────────────
    // 1부. volatile 없는 플래그: 워커가 못 멈출 수 있다
    // ─────────────────────────────────────────────────────
    static void part1_plainFlagMayNeverStop() throws Exception {
        System.out.println("── 1. volatile 없는 플래그: 멈추라고 했는데 멈출까? ──");
        System.out.println("  워커: while (plainFlag) { 카운트++ }  ← 순수 하드 루프(그래야 JIT이 최적화함)");

        // 데몬 스레드: 만에 하나 영영 안 멈춰도 JVM 종료를 막지 않는다 (hang 방지 안전장치).
        Thread worker = new Thread(() -> {
            long spins = 0;
            while (plainFlag) {   // volatile 이 아니므로, JIT이 이 검사를 루프 밖으로 빼버릴 수 있다
                spins++;          // (동기화 동작이 전혀 없는 순수 루프여야 최적화 대상이 된다)
            }
            System.out.printf("  [워커] 루프 탈출! (약 %,d 회 돈 후)%n", spins);
        }, "plain-worker");
        worker.setDaemon(true);
        worker.start();

        // JIT이 루프를 컴파일할 시간을 준다 (수십만~수백만 회 돌면 핫스팟으로 컴파일됨).
        TimeUnit.MILLISECONDS.sleep(300);

        System.out.println("  [메인] plainFlag = false 로 변경! 워커야 이제 멈춰라...");
        long stopAt = System.nanoTime();
        plainFlag = false;        // 메인 스레드는 분명히 썼다. 그러나 워커에게 보일 보장이 없다!

        worker.join(1500);        // 최대 1.5초만 기다린다 (안전장치)
        long waitedMs = (System.nanoTime() - stopAt) / 1_000_000;

        if (worker.isAlive()) {
            System.out.printf("  [메인] %d ms를 기다려도 워커가 안 멈춤 → 가시성 문제 재현 성공!%n", waitedMs);
            System.out.println("  ▶ 해석: 워커는 자기 캐시(또는 JIT이 굳혀버린 코드)의 낡은 true만 보고 있다.");
            System.out.println("          이 워커는 데몬이라 프로그램 종료와 함께 사라진다.");
        } else {
            System.out.printf("  [메인] 워커가 %d ms 만에 멈췄다 → 이번 실행에선 우연히 값이 전파됐다.%n", waitedMs);
            System.out.println("  ▶ 해석: 재현 실패도 정상! 가시성 버그는 '가끔만' 터진다 — 그게 무서운 점.");
            System.out.println("          JMM은 이 코드가 멈춘다고 '보장'하지 않는다. 보장이 필요하면 volatile.");
        }
        System.out.println();
    }

    // ─────────────────────────────────────────────────────
    // 2부. volatile 플래그: 반드시, 즉시 멈춘다
    // ─────────────────────────────────────────────────────
    static void part2_volatileFlagAlwaysStops() throws Exception {
        System.out.println("── 2. volatile 플래그: 같은 코드, 키워드 하나 추가 ──");
        System.out.println("  워커: while (volatileFlag) { 카운트++ }  ← volatile 읽기라 항상 최신 값을 본다");

        Thread worker = new Thread(() -> {
            long spins = 0;
            while (volatileFlag) { // volatile 읽기: 캐시가 아닌 '최신 값'을 읽도록 JMM이 강제
                spins++;
            }
            System.out.printf("  [워커] 루프 탈출! (약 %,d 회 돈 후)%n", spins);
        }, "volatile-worker");
        worker.start();

        TimeUnit.MILLISECONDS.sleep(300); // 1부와 같은 조건으로 JIT 워밍업

        System.out.println("  [메인] volatileFlag = false 로 변경!");
        long stopAt = System.nanoTime();
        volatileFlag = false;      // volatile 쓰기 → 워커의 다음 volatile 읽기에 반드시 보인다

        worker.join(5000);         // 실제로는 즉시 끝난다
        long waitedMs = (System.nanoTime() - stopAt) / 1_000_000;
        System.out.printf("  [메인] 워커 종료까지 %d ms → volatile 덕분에 즉시 멈췄다. (항상 이렇다)%n%n", waitedMs);
    }

    // ─────────────────────────────────────────────────────
    // 3부. happens-before: volatile 이 '다른 변수'까지 보이게 한다
    // ─────────────────────────────────────────────────────
    static void part3_happensBeforePublication() throws Exception {
        System.out.println("── 3. happens-before: 안전 발행(safe publication) ──");
        System.out.println("  쓰는 쪽:  payload = 42;        // ① 일반 쓰기 (volatile 아님)");
        System.out.println("            published = true;    // ② volatile 쓰기 ← 경계!");
        System.out.println("  읽는 쪽:  if (published)       // ③ volatile 읽기");
        System.out.println("                payload 읽기      // ④ ①의 42가 반드시 보임을 JMM이 보장");

        Thread reader = new Thread(() -> {
            while (!published) {       // ③ volatile 읽기 (true가 될 때까지 대기)
                Thread.onSpinWait();   // "스핀 대기 중"이라고 CPU에 힌트 (Java 9+)
            }
            // ②(volatile 쓰기)가 ③(volatile 읽기)보다 happens-before 이고,
            // ①은 프로그램 순서상 ② 앞이므로, 전이성에 의해 ①의 결과가 여기서 반드시 보인다.
            System.out.println("  [읽기 스레드] published=true 확인 → payload = " + payload
                    + (payload == 42 ? "  ← ①의 값이 정확히 보인다!" : "  ?! (JMM 위반 - 있을 수 없음)"));
        }, "reader");
        reader.start();

        TimeUnit.MILLISECONDS.sleep(100);
        payload = 42;         // ① 일반 쓰기: 이것 자체는 아무 가시성 보장이 없지만...
        published = true;     // ② volatile 쓰기: 이 경계가 ①까지 함께 '밀어내' 준다

        reader.join(5000);
        System.out.println("  ▶ 핵심: volatile 변수 하나가 '경계'가 되어, 그 이전의 일반 쓰기(payload=42)까지");
        System.out.println("          읽는 스레드에게 통째로 보이게 한다. 이것이 happens-before 의 힘이다.");
    }
}
