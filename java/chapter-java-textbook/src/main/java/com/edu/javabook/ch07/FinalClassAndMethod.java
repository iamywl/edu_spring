package com.edu.javabook.ch07;

/**
 * 7.5 final 클래스와 final 메소드
 *
 * final 키워드는 "더 이상 바꿀 수 없음"을 뜻한다. 상속과 관련해서는 두 가지로 쓰인다.
 *
 * - final 클래스  : 상속을 금지한다. 이 클래스를 extends 할 수 없다.
 *                  예) java.lang.String 은 final 클래스라 상속이 불가능하다.
 *                  → 클래스의 동작이 바뀌면 안 되는(보안/불변) 경우에 사용한다.
 *
 * - final 메소드  : 재정의(overriding)를 금지한다. 자식이 이 메소드를 다시 정의할 수 없다.
 *                  → 반드시 부모가 정한 동작 그대로 사용해야 하는 경우에 사용한다.
 *
 * (참고) final 변수는 값을 한 번만 대입할 수 있는 상수를 뜻한다 — 이 소절의 주제는 상속 금지다.
 *
 * 아래 주석의 잘못된 예시는 "왜 금지되는지"를 설명하기 위한 것이며 실제로는 컴파일되지 않는다.
 */
public class FinalClassAndMethod {

    // [1] final 클래스 : 상속 불가
    static final class Password {
        private final String value;

        Password(String value) {
            this.value = value;
        }

        String masked() {
            return "*".repeat(value.length());
        }
    }
    // 아래처럼 쓰면 컴파일 오류 (final 클래스는 상속 금지)
    // static class WeakPassword extends Password { }

    // [2] final 메소드 : 재정의 불가
    static class Account {
        private int balance = 1000;

        // 이 계산 규칙은 자식이 함부로 바꾸지 못하도록 final
        final int getBalance() {
            return balance;
        }

        // 이 메소드는 자식이 자유롭게 재정의 가능(final 아님)
        String describe() {
            return "일반 계좌";
        }
    }

    static class SavingsAccount extends Account {
        // final 메소드 getBalance() 는 재정의할 수 없다.
        // @Override int getBalance() { return 0; }  // 컴파일 오류!

        @Override
        String describe() {   // final 이 아니므로 재정의 허용
            return "적금 계좌";
        }
    }

    public static void main(String[] args) {

        System.out.println("=== 7.5 final 클래스와 final 메소드 ===");

        // [1] final 클래스는 그대로 사용은 가능하다(상속만 불가)
        System.out.println("\n[1] final 클래스 (상속 금지, 사용은 가능)");
        Password pw = new Password("secret");
        System.out.println("마스킹된 비밀번호 : " + pw.masked());
        System.out.println("→ Password 를 extends 하는 것은 컴파일 단계에서 막힌다.");

        // [2] final 메소드는 재정의만 금지, 상속받아 호출은 가능
        System.out.println("\n[2] final 메소드 (재정의 금지, 호출은 가능)");
        SavingsAccount sa = new SavingsAccount();
        System.out.println("잔액(final 메소드) : " + sa.getBalance());
        System.out.println("설명(재정의 가능) : " + sa.describe());

        System.out.println("\n프로그램 정상 종료");
    }
}
