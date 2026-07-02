package com.edu.javabook.ch08;

/**
 * 8.4 추상 메소드
 *
 * 인터페이스의 일반 메소드는 몸통(구현) 없이 선언만 하며,
 * 모두 "public abstract" 로 취급된다(암묵적).
 *
 * - 몸통이 없다(선언 뒤에 세미콜론).
 * - 구현 클래스는 이 추상 메소드를 "반드시" 재정의해야 한다(구현 강제).
 * - 재정의할 때는 접근 제한을 public 으로 해야 한다(인터페이스가 public이므로).
 */
public class AbstractMethod {

    interface Payment {
        // 아래 두 메소드는 사실상 public abstract 이다. 몸통이 없다.
        void pay(int amount);         // public abstract void pay(int amount);
        boolean isPaid();
    }

    // 추상 메소드를 모두 구현하지 않으면 컴파일 자체가 안 된다(구현 강제).
    static class CardPayment implements Payment {
        private boolean paid = false;

        @Override
        public void pay(int amount) {
            System.out.println("카드로 " + amount + "원 결제했습니다.");
            paid = true;
        }

        @Override
        public boolean isPaid() {
            return paid;
        }
    }

    public static void main(String[] args) {
        System.out.println("=== 8.4 추상 메소드 ===");

        // [1] 구현 강제 : 인터페이스 타입으로 사용
        System.out.println("\n[1] 추상 메소드 구현 후 호출");
        Payment payment = new CardPayment();
        payment.pay(15000);

        // [2] 다른 추상 메소드도 반드시 구현되어 있음
        System.out.println("\n[2] 결제 여부 확인");
        System.out.println("결제 완료? " + payment.isPaid());

        System.out.println("\n프로그램 정상 종료");
    }
}
