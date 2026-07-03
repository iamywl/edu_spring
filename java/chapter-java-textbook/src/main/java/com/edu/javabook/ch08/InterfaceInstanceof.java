package com.edu.javabook.ch08;

/**
 * 8.12 객체 타입 확인
 *
 * instanceof 연산자로 어떤 객체가 특정 인터페이스/클래스 타입인지 확인한다.
 * 다운캐스팅 전에 확인하면 ClassCastException 을 예방할 수 있다.
 *
 *   객체 instanceof 타입          → true/false
 *   객체 instanceof 타입 변수명    → 자바 16+ 패턴 매칭:
 *                                   true 이면 변수에 자동으로 캐스팅되어 담긴다
 */
public class InterfaceInstanceof {

    interface Payment {
        void pay();
    }

    static class CardPayment implements Payment {
        @Override public void pay() { System.out.println("카드 결제"); }
        public void checkLimit()    { System.out.println("한도 확인"); }   // 고유 기능
    }

    static class CashPayment implements Payment {
        @Override public void pay() { System.out.println("현금 결제"); }
    }

    public static void main(String[] args) {
        System.out.println("=== 8.12 객체 타입 확인 ===");

        Payment[] payments = { new CardPayment(), new CashPayment() };

        // [1] instanceof 로 인터페이스 구현 여부 확인
        System.out.println("\n[1] instanceof 로 타입 확인");
        for (Payment p : payments) {
            System.out.println(p.getClass().getSimpleName()
                    + " 는 Payment 인가? " + (p instanceof Payment));
        }

        // [2] instanceof 패턴 매칭 : 확인 + 자동 캐스팅을 한 번에
        System.out.println("\n[2] instanceof 패턴 매칭 후 안전한 다운캐스팅");
        for (Payment p : payments) {
            p.pay();
            if (p instanceof CardPayment card) {   // true면 card 에 캐스팅되어 담김
                card.checkLimit();                 // CardPayment 고유 기능 호출
            }
        }

        System.out.println("\n프로그램 정상 종료");
    }
}
