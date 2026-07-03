package com.edu.javabook.ch06;

/**
 * 6.2 객체와 클래스
 *
 * - 클래스(class): 객체를 만들기 위한 '설계도'. 어떤 필드와 메서드를 가질지 정의한다.
 * - 객체(object) : 설계도(클래스)로 실제로 만들어진 '실체'. 인스턴스(instance)라고도 한다.
 *
 * 비유: 클래스는 '붕어빵 틀', 객체는 그 틀로 찍어낸 '붕어빵' 하나하나이다.
 * 하나의 클래스로 여러 객체를 만들 수 있으며, 각 객체는 서로 독립된 상태를 가진다.
 */
public class ObjectAndClass {

    // [설계도] 자동차라는 개념을 정의한 클래스
    static class Car {
        String name;   // 필드: 객체가 가질 데이터
        int speed;

        void accelerate(int delta) {   // 메서드: 객체의 기능
            speed += delta;
        }
    }

    public static void main(String[] args) {

        System.out.println("=== 6.2 객체와 클래스 ===");

        // [1] 클래스(설계도)로부터 객체(인스턴스) 생성
        System.out.println("\n[1] 클래스 → 객체(인스턴스) 생성");
        Car sonata = new Car();        // 붕어빵 1개
        sonata.name = "소나타";
        Car avante = new Car();        // 붕어빵 또 1개
        avante.name = "아반떼";
        System.out.println("sonata.name = " + sonata.name);
        System.out.println("avante.name = " + avante.name);

        // [2] 같은 클래스라도 객체는 서로 독립적인 상태를 가진다
        System.out.println("\n[2] 객체마다 독립된 상태");
        sonata.accelerate(50);
        avante.accelerate(30);
        System.out.println("sonata.speed = " + sonata.speed + " (아반떼와 무관)");
        System.out.println("avante.speed = " + avante.speed + " (소나타와 무관)");

        // [3] 두 참조가 같은 객체를 가리키면 상태를 공유한다
        System.out.println("\n[3] 참조 공유");
        Car ref = sonata;              // 새 객체가 아니라 같은 객체를 가리킴
        ref.accelerate(10);
        System.out.println("ref는 sonata와 같은 객체 → sonata.speed = " + sonata.speed);
        System.out.println("(sonata == ref) → " + (sonata == ref));
        System.out.println("(sonata == avante) → " + (sonata == avante));

        // [왜?] 클래스는 '타입'을 정의하고, 객체는 그 타입의 '값(실체)'이다.
        System.out.println("\n[왜?] 하나의 설계도(클래스)로 서로 다른 실체(객체)를 여러 개 만든다.");

        System.out.println("\n프로그램 정상 종료");
    }
}
