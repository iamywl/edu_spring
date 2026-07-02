package com.edu.basics;

/**
 * 호출 스택(Call Stack) 데모 — 메서드를 호출할 때마다 스택에 한 칸(스택 프레임)이 쌓이고,
 * 메서드가 끝나면 그 칸이 빠진다(LIFO). 지역 변수는 그 칸에 함께 저장된다.
 *
 * JAVA_개념서 1장 "컴퓨터는 어떻게 프로그램을 실행하는가"의 Stack 절과 연결된다.
 * (개념: main → method1 → method2 순으로 쌓이고, method2 → method1 → main 순으로 빠진다)
 */
public class StackDemo {

    public static void main(String[] args) {
        System.out.println("=== 호출 스택(Call Stack) 데모 ===\n");
        System.out.println("메서드 호출 = 스택에 프레임 push, 메서드 종료 = pop (LIFO)\n");

        int a = 10; // main의 지역 변수 (main 프레임에 저장)
        System.out.println("[main] 시작 (지역변수 a=" + a + ")");
        printDepth("main");
        method1();
        System.out.println("[main] 끝 → main 프레임 pop, 프로그램 종료");
    }

    static void method1() {
        int b = 20; // method1의 지역 변수
        System.out.println("  [method1] 호출됨 → 프레임 push (지역변수 b=" + b + ")");
        printDepth("method1");
        method2();
        System.out.println("  [method1] 끝 → method1 프레임 pop");
    }

    static void method2() {
        int c = 30; // method2의 지역 변수
        System.out.println("    [method2] 호출됨 → 프레임 push (지역변수 c=" + c + ")");
        printDepth("method2");
        System.out.println("    [method2] 끝 → method2 프레임 먼저 pop (가장 마지막에 쌓였으므로)");
    }

    /** 현재 호출 스택의 깊이와 프레임(메서드) 목록을 출력 — 스택이 실제로 쌓임을 눈으로 확인 */
    private static void printDepth(String where) {
        StackTraceElement[] frames = Thread.currentThread().getStackTrace();
        // frames[0]=getStackTrace, frames[1]=printDepth 는 제외하고 실제 호출 체인만 센다
        int depth = frames.length - 2;
        StringBuilder chain = new StringBuilder();
        for (int i = frames.length - 1; i >= 2; i--) { // 아래(main)부터 위로
            chain.append(frames[i].getMethodName());
            if (i > 2) chain.append(" → ");
        }
        System.out.println("      └ 현재 스택 깊이 " + depth + ": [" + chain + "]");
    }
}
