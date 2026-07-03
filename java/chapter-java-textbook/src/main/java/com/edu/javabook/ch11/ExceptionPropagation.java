package com.edu.javabook.ch11;

/**
 * 11.5 예외 떠넘기기 (throws)
 *
 * 메소드 안에서 발생한 예외를 그 메소드가 직접 처리하지 않고,
 * "나를 호출한 쪽에서 처리하라"고 넘길 수 있다. 이것이 예외 떠넘기기(throws)이다.
 *
 *   리턴타입 메소드명(...) throws 예외타입 {
 *       // 이 안에서 발생한 checked 예외를 처리하지 않고 호출자에게 넘긴다
 *   }
 *
 * 호출 스택 전파 :
 *   예외를 처리(catch)하는 곳이 나올 때까지, 호출한 메소드를 거꾸로 거슬러 올라가며 전파된다.
 *   main → A → B → C 순으로 호출했고 C 에서 예외가 나면,
 *   C 가 안 잡으면 B 로, B 가 안 잡으면 A 로, A 가 안 잡으면 main 으로 올라간다.
 *   끝까지 아무도 안 잡으면 프로그램이 비정상 종료된다.
 *
 * 이 소절에서는 throws 로 예외를 위임하고, 호출 스택을 따라 전파되어
 * 최종적으로 main 에서 처리되는 과정을 확인한다.
 */
public class ExceptionPropagation {

    // 보조 예외 클래스 (checked 예외) : 처리를 강제하기 위해 Exception 상속
    static class TaskException extends Exception {
        TaskException(String message) {
            super(message);
        }
    }

    // [3단계] 가장 안쪽 : 여기서 예외 발생. 처리하지 않고 throws 로 떠넘김
    static void levelC() throws TaskException {
        System.out.println("  levelC 실행 → 예외 발생, 처리하지 않고 떠넘김(throws)");
        throw new TaskException("levelC 에서 발생한 작업 오류");
    }

    // [2단계] levelC 호출. 잡지 않고 그대로 위로 떠넘김(throws)
    static void levelB() throws TaskException {
        System.out.println("  levelB 실행 → levelC 호출");
        levelC();   // 예외를 잡지 않으므로 levelA 로 전파된다
    }

    // [1단계] levelB 호출. 역시 떠넘김(throws)
    static void levelA() throws TaskException {
        System.out.println("  levelA 실행 → levelB 호출");
        levelB();   // 예외를 잡지 않으므로 main 으로 전파된다
    }

    public static void main(String[] args) {

        System.out.println("=== 11.5 예외 떠넘기기 ===");

        // [1] main 이 최종 호출자 : 아래 호출 사슬에서 전파된 예외를 여기서 잡는다
        System.out.println("\n[1] 호출 사슬: main → levelA → levelB → levelC");
        System.out.println("    (levelC 에서 발생 → A/B/C 모두 throws → main 에서 처리)\n");
        try {
            levelA();   // levelC 의 예외가 스택을 따라 여기까지 전파됨
        } catch (TaskException e) {
            System.out.println("\n  [main:catch] 전파된 예외를 최종 처리: " + e.getMessage());
        }

        // [2] 스택 트레이스로 전파 경로 확인
        System.out.println("\n[2] 예외가 거쳐 온 호출 경로(스택 트레이스)");
        try {
            levelA();
        } catch (TaskException e) {
            StackTraceElement[] trace = e.getStackTrace();
            System.out.println("  발생 지점부터 거슬러 온 경로:");
            for (StackTraceElement el : trace) {
                if (el.getClassName().startsWith("com.edu.javabook.ch11")) {
                    System.out.println("    at " + el.getMethodName() + "()");
                }
            }
        }

        System.out.println("\n프로그램 정상 종료");
    }
}
