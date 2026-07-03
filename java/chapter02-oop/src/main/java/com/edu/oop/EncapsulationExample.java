package com.edu.oop;

/**
 * [개념 3] 캡슐화(Encapsulation)
 *
 * 캡슐화란?
 * - 객체의 데이터(필드)를 외부에서 직접 건드리지 못하게 감추고(private),
 *   공개된 메서드(getter/setter)를 통해서만 접근하게 하는 것.
 * - 이를 통해 "불변식(invariant)" - 즉 객체가 항상 지켜야 할 규칙 - 을 보호한다.
 *
 * 왜 필요한가?
 *   - 잘못된 값이 필드에 직접 들어가는 것을 막을 수 있다(유효성 검증).
 *   - 내부 구현을 바꿔도 외부 코드는 영향을 받지 않는다.
 *   - 객체가 스스로 자신의 상태를 책임지게 되어 버그가 줄어든다.
 *
 * 이 예제는 두 가지로 캡슐화를 보여준다:
 *   (A) 기존 도메인 클래스 Animal/Dog 의 private 필드 + setter 유효성 검증
 *   (B) 내부에 작은 Account(계좌) 클래스를 두어 "잔액이 음수가 될 수 없다"는
 *       불변식을 어떻게 지키는지 시연
 */
public class EncapsulationExample {

    public static void main(String[] args) {

        // ------------------------------------------------------------
        // (A) 도메인 클래스의 캡슐화: private 필드 + getter/setter
        // ------------------------------------------------------------
        printSection("A. 도메인 클래스(Animal/Dog)의 캡슐화");

        // Dog의 name/age는 Animal에서 private으로 선언되어 있어
        // 외부에서 dog.name 처럼 직접 접근할 수 없다. -> getter/setter만 사용.
        Dog dog = new Dog("초코", 5, "푸들");

        System.out.println("getter로 읽기:");
        System.out.println("  이름: " + dog.getName());
        System.out.println("  나이: " + dog.getAge() + "살");

        // setter로 안전하게 변경
        dog.setName("초코초코");
        dog.setAge(6);
        System.out.println("setter로 변경 후: " + dog.info());

        // ------------------------------------------------------------
        // (A-2) 불변식 보호: 잘못된 값은 setter가 거부한다
        // ------------------------------------------------------------
        printSection("A-2. setter의 유효성 검증 (불변식 보호)");

        // 빈 이름은 허용되지 않는다 -> setName이 예외를 던져 잘못된 상태를 막는다.
        try {
            dog.setName("");   // 규칙 위반
        } catch (IllegalArgumentException e) {
            System.out.println("이름 검증 실패 메시지: " + e.getMessage());
        }

        // 음수 나이도 허용되지 않는다.
        try {
            dog.setAge(-1);    // 규칙 위반
        } catch (IllegalArgumentException e) {
            System.out.println("나이 검증 실패 메시지: " + e.getMessage());
        }

        System.out.println("검증 실패 후에도 객체 상태는 안전하게 유지됨 -> " + dog.info());

        // ------------------------------------------------------------
        // (B) 작은 예제: Account (계좌)로 불변식 "잔액 >= 0" 보호
        // ------------------------------------------------------------
        printSection("B. Account 예제 - '잔액은 음수가 될 수 없다'");

        Account account = new Account("1002-333-4444", 10_000);
        System.out.println("초기 잔액: " + account.getBalance() + "원");

        account.deposit(5_000);
        System.out.println("5,000원 입금 후 잔액: " + account.getBalance() + "원");

        account.withdraw(3_000);
        System.out.println("3,000원 출금 후 잔액: " + account.getBalance() + "원");

        // 잔액보다 큰 금액 출금 시도 -> 불변식 위반 -> 거부
        try {
            account.withdraw(999_999);
        } catch (IllegalArgumentException e) {
            System.out.println("출금 실패 메시지: " + e.getMessage());
        }
        System.out.println("실패 후에도 잔액은 그대로 -> " + account.getBalance() + "원");

        // 참고: balance 필드는 private final 이 아니라 private 이므로 내부에서만 변경된다.
        //       외부에서는 오직 deposit()/withdraw()를 통해서만 잔액을 바꿀 수 있다.

        // ------------------------------------------------------------
        // 정리
        // ------------------------------------------------------------
        printSection("정리");
        System.out.println("- 필드는 private 으로 숨기고, 메서드로만 접근하게 한다.");
        System.out.println("- setter/메서드 안에서 유효성 검증 -> 불변식을 보호한다.");
        System.out.println("- 내부 구현을 바꿔도 외부 코드는 영향을 받지 않는다.");
    }

    /**
     * 캡슐화 시연용 내부(static nested) 클래스: 은행 계좌.
     * - balance 는 private 이라 외부에서 직접 수정 불가.
     * - deposit/withdraw 메서드만이 잔액을 변경할 수 있고,
     *   그 안에서 "잔액은 0 이상"이라는 불변식을 강제한다.
     */
    static class Account {
        private final String number;  // 계좌번호(생성 후 변경 불가)
        private long balance;         // 잔액(오직 메서드로만 변경 가능)

        Account(String number, long initialBalance) {
            if (initialBalance < 0) {
                throw new IllegalArgumentException("초기 잔액은 0 이상이어야 합니다.");
            }
            this.number = number;
            this.balance = initialBalance;
        }

        public String getNumber() {
            return number;
        }

        public long getBalance() {
            return balance;
        }

        /** 입금: 금액은 양수여야 한다 */
        public void deposit(long amount) {
            if (amount <= 0) {
                throw new IllegalArgumentException("입금액은 양수여야 합니다.");
            }
            balance += amount;
        }

        /** 출금: 금액은 양수이고, 잔액을 초과할 수 없다(불변식 보호) */
        public void withdraw(long amount) {
            if (amount <= 0) {
                throw new IllegalArgumentException("출금액은 양수여야 합니다.");
            }
            if (amount > balance) {
                throw new IllegalArgumentException(
                        "잔액이 부족합니다. (잔액: " + balance + ", 요청: " + amount + ")");
            }
            balance -= amount;
        }
    }

    private static void printSection(String title) {
        System.out.println();
        System.out.println("=".repeat(60));
        System.out.println("  " + title);
        System.out.println("=".repeat(60));
    }
}
