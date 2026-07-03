package com.edu.javabook.ch07;

/**
 * 7.1 상속 개념
 *
 * 상속(inheritance)은 이미 만들어진 클래스(부모, super class)의 필드와 메소드를
 * 새로 만드는 클래스(자식, sub class)가 "물려받아" 재사용하는 문법이다.
 *
 * - 코드 재사용 : 공통 기능을 부모에 한 번만 작성하고, 여러 자식이 공유한다.
 *                중복 코드가 사라져 유지보수가 쉬워진다.
 * - is-a 관계   : 상속은 "자식 is-a 부모" 가 성립할 때 쓴다.
 *                예) SportsCar is-a Car (스포츠카는 자동차의 한 종류다) → OK
 *                    Engine is-a Car (엔진은 자동차의 한 종류다) → 성립 안 됨(이때는 상속 대신 포함/has-a)
 *
 * 이 소절에서는 "부모의 코드를 자식이 그대로 재사용한다"는 감각만 확인한다.
 */
public class InheritanceConcept {

    // [부모 클래스] 모든 자동차가 공통으로 가지는 기능
    static class Car {
        String name;

        Car(String name) {
            this.name = name;
        }

        // 공통 기능 : 시동
        void start() {
            System.out.println(name + " 시동을 켠다.");
        }

        // 공통 기능 : 정지
        void stop() {
            System.out.println(name + " 정지한다.");
        }
    }

    // [자식 클래스] Car를 상속 → start/stop 을 "다시 작성하지 않아도" 사용 가능
    // "SportsCar is-a Car" 가 성립하므로 상속이 자연스럽다.
    static class SportsCar extends Car {

        SportsCar(String name) {
            super(name);   // 부모 생성자 호출(자세한 내용은 7.3)
        }

        // 자식만의 추가 기능
        void boost() {
            System.out.println(name + " 부스트! 최고 속도로 달린다.");
        }
    }

    public static void main(String[] args) {

        System.out.println("=== 7.1 상속 개념 ===");

        // [1] 코드 재사용 : SportsCar는 start/stop 코드를 한 줄도 안 썼지만 사용 가능
        System.out.println("\n[1] 코드 재사용 (부모의 기능을 물려받아 사용)");
        SportsCar sc = new SportsCar("페라리");
        sc.start();     // 부모 Car 에서 물려받은 메소드
        sc.boost();     // 자식 SportsCar 자신의 메소드
        sc.stop();      // 부모 Car 에서 물려받은 메소드

        // [2] is-a 관계 확인 : 스포츠카는 자동차의 한 종류이다
        System.out.println("\n[2] is-a 관계 (SportsCar is-a Car)");
        System.out.println("sc 는 SportsCar 인가? " + (sc instanceof SportsCar));
        System.out.println("sc 는 Car 이기도 한가? " + (sc instanceof Car));

        System.out.println("\n프로그램 정상 종료");
    }
}
