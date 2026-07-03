package com.edu.javabook.ch08;

/**
 * 8.10 타입 변환
 *
 * 구현 객체와 인터페이스 타입 사이의 변환을 다룬다.
 *
 * - 자동 타입 변환(업캐스팅)   : 구현 객체 → 인터페이스 타입.
 *                              항상 안전하므로 자동으로 변환된다.
 * - 강제 타입 변환(다운캐스팅) : 인터페이스 타입 → 구현 클래스 타입.
 *                              괄호로 (구현클래스) 를 명시해야 하며,
 *                              실제 객체가 그 타입일 때만 안전하다.
 */
public class InterfaceTypeCasting {

    interface Vehicle {
        void move();
    }

    static class Car implements Vehicle {
        @Override public void move() { System.out.println("자동차가 달립니다."); }

        // Car 에만 있는 고유 기능 (인터페이스에는 없음)
        public void openTrunk() { System.out.println("트렁크를 엽니다."); }
    }

    public static void main(String[] args) {
        System.out.println("=== 8.10 타입 변환 ===");

        // [1] 자동 타입 변환(업캐스팅): 구현 객체 → 인터페이스 타입
        System.out.println("\n[1] 업캐스팅 (구현객체 → 인터페이스)");
        Vehicle vehicle = new Car();   // 자동 변환
        vehicle.move();
        // vehicle.openTrunk();        // 불가: 인터페이스에는 openTrunk 가 없음

        // [2] 강제 타입 변환(다운캐스팅): 인터페이스 타입 → 구현 클래스 타입
        System.out.println("\n[2] 다운캐스팅 (인터페이스 → 구현객체)");
        Car car = (Car) vehicle;       // 명시적 캐스팅 필요
        car.move();
        car.openTrunk();               // 다운캐스팅 후 고유 기능 사용 가능

        System.out.println("\n프로그램 정상 종료");
    }
}
