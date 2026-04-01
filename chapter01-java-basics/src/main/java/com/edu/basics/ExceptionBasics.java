package com.edu.basics;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

/**
 * Chapter 01 - 예외처리 기초
 *
 * try-catch-finally, 다중 catch, 커스텀 예외,
 * try-with-resources를 다룹니다.
 */
public class ExceptionBasics {

    public static void main(String[] args) {
        System.out.println("====================================");
        System.out.println(" Chapter 01: 예외처리 기초");
        System.out.println("====================================\n");

        tryCatchFinallyDemo();
        multipleCatchDemo();
        customExceptionDemo();
        tryWithResourcesDemo();
        exceptionPropagationDemo();
    }

    // ──────────────────────────────────────────────
    // 1. try-catch-finally 기본
    // ──────────────────────────────────────────────
    static void tryCatchFinallyDemo() {
        System.out.println("── 1. try-catch-finally 기본 ──");

        // 기본 구조: try 블록에서 예외 발생 시 catch 블록이 처리
        // finally 블록은 예외 발생 여부와 관계없이 항상 실행
        try {
            System.out.println("  1) try 블록 시작");
            int result = 10 / 0;  // ArithmeticException 발생!
            System.out.println("  이 줄은 실행되지 않습니다: " + result);
        } catch (ArithmeticException e) {
            // 예외 발생 시 이 블록이 실행됨
            System.out.println("  2) catch 블록: " + e.getMessage());
        } finally {
            // 예외 발생 여부와 관계없이 항상 실행
            // 리소스 정리(파일 닫기, DB 연결 해제 등)에 주로 사용
            System.out.println("  3) finally 블록: 항상 실행됩니다.");
        }

        // 예외가 발생하지 않는 경우
        System.out.println("\n  [예외 없는 경우]");
        try {
            int result = 10 / 2;
            System.out.println("  결과: " + result);
        } catch (ArithmeticException e) {
            System.out.println("  이 블록은 실행되지 않습니다.");
        } finally {
            System.out.println("  finally: 예외 없어도 실행됩니다.");
        }
        System.out.println();
    }

    // ──────────────────────────────────────────────
    // 2. 다중 catch와 예외 종류
    // ──────────────────────────────────────────────
    static void multipleCatchDemo() {
        System.out.println("── 2. 다중 catch와 예외 종류 ──");

        // 여러 종류의 예외를 각각 처리
        String[] data = {"100", "abc", null};

        for (int i = 0; i < 4; i++) {
            try {
                System.out.print("  인덱스 " + i + ": ");
                String value = data[i];               // ArrayIndexOutOfBoundsException 가능
                int number = Integer.parseInt(value);  // NumberFormatException 가능
                int result = 100 / number;             // ArithmeticException 가능
                System.out.println("결과 = " + result);

            } catch (ArrayIndexOutOfBoundsException e) {
                // 배열 인덱스 범위 초과
                System.out.println("배열 인덱스 오류: " + e.getMessage());

            } catch (NumberFormatException e) {
                // 문자열 → 숫자 변환 실패
                System.out.println("숫자 변환 오류: " + e.getMessage());

            } catch (NullPointerException e) {
                // null 참조 접근
                System.out.println("Null 참조 오류");

            } catch (Exception e) {
                // 위에서 잡지 못한 모든 예외 (가장 마지막에 위치해야 함)
                System.out.println("기타 오류: " + e.getMessage());
            }
        }

        // Java 7+ 멀티 catch: 하나의 catch 블록에서 여러 예외 타입 처리
        System.out.println("\n  [멀티 catch (Java 7+)]");
        try {
            String text = "abc";
            int num = Integer.parseInt(text);
        } catch (NumberFormatException | ArithmeticException e) {
            // 파이프(|)로 여러 예외를 묶어서 처리
            System.out.println("  숫자 변환 또는 산술 오류: " + e.getClass().getSimpleName());
        }
        System.out.println();
    }

    // ──────────────────────────────────────────────
    // 3. 커스텀 예외 (Custom Exception)
    // ──────────────────────────────────────────────
    static void customExceptionDemo() {
        System.out.println("── 3. 커스텀 예외 (Custom Exception) ──");

        // 은행 계좌 시뮬레이션
        BankAccount account = new BankAccount("홍길동", 10000);
        System.out.println("  계좌 생성: " + account);

        // 정상 출금
        try {
            account.withdraw(3000);
            System.out.println("  3000원 출금 후: " + account);
        } catch (InsufficientBalanceException e) {
            System.out.println("  출금 실패: " + e.getMessage());
        }

        // 잔액 부족 출금 시도
        try {
            account.withdraw(50000);
            System.out.println("  50000원 출금 후: " + account);
        } catch (InsufficientBalanceException e) {
            System.out.println("  출금 실패: " + e.getMessage());
            System.out.println("  현재 잔액: " + e.getCurrentBalance() + "원");
            System.out.println("  요청 금액: " + e.getRequestedAmount() + "원");
        }

        // 잘못된 금액 (RuntimeException)
        try {
            account.withdraw(-1000);
        } catch (IllegalArgumentException e) {
            System.out.println("  잘못된 요청: " + e.getMessage());
        } catch (InsufficientBalanceException e) {
            System.out.println("  출금 실패: " + e.getMessage());
        }
        System.out.println();
    }

    // ──────────────────────────────────────────────
    // 4. try-with-resources (Java 7+)
    // ──────────────────────────────────────────────
    static void tryWithResourcesDemo() {
        System.out.println("── 4. try-with-resources (Java 7+) ──");

        // AutoCloseable을 구현한 리소스는 try 블록이 끝나면 자동으로 close() 호출
        // finally에서 수동으로 닫을 필요 없음

        // 예시: StringReader를 BufferedReader로 감싸서 읽기
        String data = "첫 번째 줄\n두 번째 줄\n세 번째 줄";

        // try-with-resources 사용
        System.out.println("  [try-with-resources로 읽기]");
        try (BufferedReader reader = new BufferedReader(new StringReader(data))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("  읽은 줄: " + line);
            }
            // try 블록이 끝나면 reader.close()가 자동 호출됨
        } catch (IOException e) {
            System.out.println("  읽기 오류: " + e.getMessage());
        }
        // finally { reader.close(); } 가 필요 없음!

        // 커스텀 AutoCloseable 리소스
        System.out.println("\n  [커스텀 AutoCloseable 리소스]");
        try (MyResource resource = new MyResource("DB 연결")) {
            resource.doWork();
            // 블록 종료 시 close() 자동 호출
        } catch (Exception e) {
            System.out.println("  오류: " + e.getMessage());
        }

        // 여러 리소스를 동시에 관리 (세미콜론으로 구분)
        System.out.println("\n  [여러 리소스 동시 관리]");
        try (
            MyResource res1 = new MyResource("리소스1");
            MyResource res2 = new MyResource("리소스2")
        ) {
            res1.doWork();
            res2.doWork();
            // 닫히는 순서: res2 → res1 (선언의 역순)
        } catch (Exception e) {
            System.out.println("  오류: " + e.getMessage());
        }
        System.out.println();
    }

    // ──────────────────────────────────────────────
    // 5. 예외 전파 (Exception Propagation)
    // ──────────────────────────────────────────────
    static void exceptionPropagationDemo() {
        System.out.println("── 5. 예외 전파 (Exception Propagation) ──");

        // 예외를 처리하지 않으면 호출한 메서드로 전파됨
        // method1 → method2 → method3 에서 예외 발생
        try {
            method1();
        } catch (Exception e) {
            System.out.println("  main에서 예외 처리: " + e.getMessage());

            // 스택 트레이스 출력 (디버깅에 유용)
            System.out.println("  스택 트레이스:");
            for (StackTraceElement element : e.getStackTrace()) {
                if (element.getClassName().contains("edu.basics")) {
                    System.out.println("    → " + element);
                }
            }
        }

        // throws 키워드로 예외 선언
        try {
            riskyMethod();
        } catch (IOException e) {
            System.out.println("\n  checked 예외 처리: " + e.getMessage());
        }
        System.out.println();
    }

    // 예외 전파 체인: method1 → method2 → method3
    static void method1() {
        System.out.println("  method1 호출");
        method2();
    }

    static void method2() {
        System.out.println("  method2 호출");
        method3();
    }

    static void method3() {
        System.out.println("  method3 호출 → 예외 발생!");
        // RuntimeException(Unchecked)은 throws 선언 없이 전파 가능
        throw new RuntimeException("method3에서 발생한 오류");
    }

    // Checked Exception은 throws로 선언하거나 try-catch로 처리해야 함
    static void riskyMethod() throws IOException {
        throw new IOException("파일을 찾을 수 없습니다");
    }

    // ══════════════════════════════════════════════
    //  내부 클래스 정의
    // ══════════════════════════════════════════════

    /**
     * 커스텀 예외 클래스 - 잔액 부족 예외
     *
     * Exception을 상속하면 Checked Exception (반드시 처리해야 함)
     * RuntimeException을 상속하면 Unchecked Exception
     */
    static class InsufficientBalanceException extends Exception {
        private final int currentBalance;   // 현재 잔액
        private final int requestedAmount;  // 요청 금액

        public InsufficientBalanceException(String message, int currentBalance, int requestedAmount) {
            super(message);  // 부모 클래스의 생성자 호출
            this.currentBalance = currentBalance;
            this.requestedAmount = requestedAmount;
        }

        // 추가 정보를 제공하는 getter
        public int getCurrentBalance() {
            return currentBalance;
        }

        public int getRequestedAmount() {
            return requestedAmount;
        }
    }

    /**
     * 간단한 은행 계좌 클래스
     */
    static class BankAccount {
        private String owner;
        private int balance;

        public BankAccount(String owner, int initialBalance) {
            this.owner = owner;
            this.balance = initialBalance;
        }

        /**
         * 출금 메서드
         * @throws InsufficientBalanceException 잔액 부족 시
         * @throws IllegalArgumentException 금액이 0 이하일 때
         */
        public void withdraw(int amount) throws InsufficientBalanceException {
            if (amount <= 0) {
                // Unchecked Exception: 프로그래밍 오류를 나타냄
                throw new IllegalArgumentException("출금 금액은 0보다 커야 합니다: " + amount);
            }
            if (amount > balance) {
                // Checked Exception: 비즈니스 로직 예외
                throw new InsufficientBalanceException(
                    "잔액이 부족합니다. (잔액: " + balance + "원, 요청: " + amount + "원)",
                    balance, amount
                );
            }
            balance -= amount;
        }

        @Override
        public String toString() {
            return owner + "의 계좌 [잔액: " + balance + "원]";
        }
    }

    /**
     * AutoCloseable을 구현한 커스텀 리소스
     * try-with-resources에서 자동으로 close() 호출됨
     */
    static class MyResource implements AutoCloseable {
        private final String name;

        public MyResource(String name) {
            this.name = name;
            System.out.println("    " + name + " 열림 (생성)");
        }

        public void doWork() {
            System.out.println("    " + name + " 작업 수행 중...");
        }

        @Override
        public void close() throws Exception {
            // 리소스 정리 로직 (파일 닫기, 연결 해제 등)
            System.out.println("    " + name + " 닫힘 (자동 close)");
        }
    }
}
