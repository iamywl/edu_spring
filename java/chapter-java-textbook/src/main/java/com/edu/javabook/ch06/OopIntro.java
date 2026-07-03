package com.edu.javabook.ch06;

/**
 * 6.1 객체지향 프로그래밍(OOP)
 *
 * 객체지향 프로그래밍은 현실 세계의 사물(객체)을 소프트웨어로 모델링하는 방식이다.
 * 데이터(필드)와 그 데이터를 다루는 기능(메서드)을 하나의 '객체'로 묶어서 관리한다.
 *
 * [절차지향 vs 객체지향]
 *  - 절차지향: 데이터와 함수가 분리되어 있고, 함수를 순서대로 호출해 처리한다.
 *  - 객체지향: 데이터와 함수를 객체 안에 함께 넣어, 객체가 스스로 일을 처리한다.
 *
 * OOP의 4대 특징: 캡슐화, 상속, 다형성, 추상화.
 */
public class OopIntro {

    // [절차지향 스타일] 데이터(배열)와 처리 함수(static)가 따로 논다.
    static int calcTotalProcedural(int[] prices) {
        int sum = 0;
        for (int p : prices) sum += p;
        return sum;
    }

    // [객체지향 스타일] 데이터와 기능을 하나의 객체(Cart)로 묶는다.
    static class Cart {
        private int[] prices;             // 데이터(상태)

        Cart(int[] prices) {              // 객체가 자기 데이터를 가진다
            this.prices = prices;
        }

        int calcTotal() {                 // 기능(행위)도 객체 안에 있다
            int sum = 0;
            for (int p : prices) sum += p;
            return sum;
        }
    }

    public static void main(String[] args) {

        System.out.println("=== 6.1 객체지향 프로그래밍 ===");

        int[] prices = {1000, 2000, 3000};

        // [1] 절차지향: 데이터를 함수에 '넘겨서' 처리
        System.out.println("\n[1] 절차지향 방식");
        int total1 = calcTotalProcedural(prices);
        System.out.println("calcTotalProcedural(prices) → " + total1);
        System.out.println("→ 데이터(prices)와 함수가 분리되어 있음");

        // [2] 객체지향: 객체가 데이터를 품고 '스스로' 처리
        System.out.println("\n[2] 객체지향 방식");
        Cart cart = new Cart(prices);
        int total2 = cart.calcTotal();
        System.out.println("cart.calcTotal() → " + total2);
        System.out.println("→ 데이터와 기능이 Cart 객체 안에 함께 있음");

        // [3] OOP 4대 특징 개념 요약
        System.out.println("\n[3] 객체지향 4대 특징");
        System.out.println("- 캡슐화(Encapsulation): 데이터와 기능을 묶고 내부를 숨긴다");
        System.out.println("- 상속(Inheritance)   : 기존 클래스를 확장해 재사용한다");
        System.out.println("- 다형성(Polymorphism): 같은 호출이 객체에 따라 다르게 동작한다");
        System.out.println("- 추상화(Abstraction) : 핵심만 남기고 복잡함을 감춘다");

        // [왜?] OOP는 복잡한 프로그램을 '객체' 단위로 나눠 이해와 재사용을 쉽게 한다.
        System.out.println("\n[왜?] 현실을 객체로 모델링하면 유지보수와 확장이 쉬워진다.");

        System.out.println("\n프로그램 정상 종료");
    }
}
