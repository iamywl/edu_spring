package com.edu.javabook.ch11;

/**
 * 11.6 사용자 정의 예외
 *
 * 자바가 미리 제공하는 표준 예외만으로는 "우리 프로그램만의 의미 있는 오류"를
 * 정확히 표현하기 어렵다. 이럴 때 직접 예외 클래스를 만든다.
 *
 * 만드는 방법 :
 *   - checked 예외로 만들려면   → Exception 을 상속        (처리 강제)
 *   - unchecked 예외로 만들려면 → RuntimeException 을 상속 (처리 강제 X)
 *
 * 관례 :
 *   - 클래스 이름은 "...Exception" 으로 끝맺는다.
 *   - 메시지를 받는 생성자(super(message)) 를 제공한다.
 *   - 필요하면 원인/코드 같은 추가 필드를 넣어 더 풍부한 정보를 담는다.
 *
 * 이 소절에서는 checked/unchecked 사용자 정의 예외를 각각 만들어,
 * "무엇이 잘못되었는지"를 의미 있게 표현하고 처리하는 방법을 확인한다.
 */
public class CustomException {

    // [사용자 정의 예외 1] checked : 잔액 부족 → 반드시 처리하게 만들고 싶다 → Exception 상속
    static class InsufficientBalanceException extends Exception {
        private final int shortage;   // 추가 정보 : 부족한 금액

        InsufficientBalanceException(String message, int shortage) {
            super(message);
            this.shortage = shortage;
        }

        int getShortage() {
            return shortage;
        }
    }

    // [사용자 정의 예외 2] unchecked : 잘못된 입력(음수) → 프로그래머 실수 성격 → RuntimeException 상속
    static class InvalidAmountException extends RuntimeException {
        InvalidAmountException(String message) {
            super(message);
        }
    }

    // 간단한 계좌 : 표준 예외 대신 "의미 있는" 사용자 정의 예외를 던진다
    static class Account {
        private int balance;

        Account(int balance) {
            this.balance = balance;
        }

        // 출금 : 금액 검증(unchecked) + 잔액 검증(checked)
        void withdraw(int amount) throws InsufficientBalanceException {
            if (amount <= 0) {
                // 잘못된 사용 → 실행 예외로 즉시 알림
                throw new InvalidAmountException("출금액은 0보다 커야 합니다: " + amount);
            }
            if (amount > balance) {
                // 업무 규칙 위반 → 호출자가 반드시 처리하도록 checked 예외
                throw new InsufficientBalanceException(
                        "잔액 부족: 현재 " + balance + "원, 요청 " + amount + "원",
                        amount - balance);
            }
            balance -= amount;
            System.out.println("  출금 성공: " + amount + "원, 남은 잔액 " + balance + "원");
        }
    }

    public static void main(String[] args) {

        System.out.println("=== 11.6 사용자 정의 예외 ===");

        Account account = new Account(10_000);

        // [1] 정상 출금
        System.out.println("\n[1] 정상 출금 (잔액 10000원 → 3000원 출금)");
        try {
            account.withdraw(3_000);
        } catch (InsufficientBalanceException e) {
            System.out.println("  " + e.getMessage());
        }

        // [2] checked 사용자 정의 예외 : 잔액 부족 → 반드시 처리해야 함
        System.out.println("\n[2] 잔액 부족 (checked: InsufficientBalanceException)");
        try {
            account.withdraw(50_000);
        } catch (InsufficientBalanceException e) {
            System.out.println("  처리됨: " + e.getMessage());
            System.out.println("  추가 정보 - 부족한 금액: " + e.getShortage() + "원");
        }

        // [3] unchecked 사용자 정의 예외 : 잘못된 입력(음수)
        System.out.println("\n[3] 잘못된 입력 (unchecked: InvalidAmountException)");
        try {
            account.withdraw(-100);
        } catch (InvalidAmountException e) {
            System.out.println("  처리됨: " + e.getMessage());
        } catch (InsufficientBalanceException e) {
            System.out.println("  " + e.getMessage());
        }

        System.out.println("\n프로그램 정상 종료");
    }
}
