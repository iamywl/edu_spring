package com.edu.javabook.ch07;

/**
 * 7.10 추상 클래스 (Abstract Class)
 *
 * 추상 클래스는 "아직 완성되지 않은, 상속 전용" 클래스이다. abstract 키워드로 선언한다.
 *
 * - 추상 메소드(abstract method) : 선언만 있고 본문 { } 이 없는 메소드.
 *                                자식이 "반드시" 재정의(구현)해야 한다.
 * - 추상 클래스(abstract class)  : 추상 메소드를 하나라도 가지면 추상 클래스가 된다.
 *                                직접 new 로 객체를 만들 수 없다(미완성이므로).
 *                                일반 메소드/필드도 함께 가질 수 있다(공통 코드 재사용).
 *
 * 쓰는 이유 : "공통 뼈대(공통 필드/메소드)는 부모가 제공하고,
 *             자식마다 달라지는 부분만 추상 메소드로 강제 구현"하게 만들기 위해서다.
 *
 * 이 소절에서는 추상 메소드를 자식이 구현하고, 부모의 공통 메소드는 재사용하는 모습을 본다.
 */
public class AbstractClass {

    // 추상 클래스 : 직접 객체 생성 불가
    static abstract class Payment {
        int amount;

        Payment(int amount) {
            this.amount = amount;
        }

        // 추상 메소드 : 결제 방식마다 다르므로 본문 없이 선언만. 자식이 반드시 구현.
        abstract void pay();

        // 일반(구현된) 메소드 : 모든 결제에 공통 → 자식이 그대로 재사용
        void printReceipt() {
            System.out.println("영수증 : " + amount + "원 결제 완료");
        }
    }

    static class CardPayment extends Payment {
        CardPayment(int amount) {
            super(amount);
        }

        @Override
        void pay() {   // 추상 메소드 구현(강제)
            System.out.println("카드로 " + amount + "원 결제합니다.");
        }
    }

    static class CashPayment extends Payment {
        CashPayment(int amount) {
            super(amount);
        }

        @Override
        void pay() {
            System.out.println("현금으로 " + amount + "원 결제합니다.");
        }
    }

    public static void main(String[] args) {

        System.out.println("=== 7.10 추상 클래스 ===");

        // [1] 추상 클래스 자체는 new 로 만들 수 없다
        System.out.println("\n[1] 추상 클래스는 직접 생성 불가");
        System.out.println("- new Payment(...) 는 컴파일 오류(미완성 클래스).");
        System.out.println("- 완성된 자식(CardPayment/CashPayment)만 객체로 만든다.");

        // [2] 자식이 추상 메소드를 각자 구현, 공통 메소드는 재사용
        System.out.println("\n[2] 자식마다 pay() 구현 + 공통 printReceipt() 재사용");
        Payment[] payments = {
                new CardPayment(10000),
                new CashPayment(5000)
        };
        for (Payment p : payments) {   // 부모 타입으로 통일해 다룸(다형성)
            p.pay();            // 자식마다 다른 구현 실행
            p.printReceipt();   // 부모의 공통 코드 재사용
        }

        System.out.println("\n프로그램 정상 종료");
    }
}
