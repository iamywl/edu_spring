package com.edu.javabook.ch08;

/**
 * 8.8 다중 인터페이스 구현
 *
 * 클래스는 하나만 상속(extends)할 수 있지만,
 * 인터페이스는 여러 개를 동시에 구현(implements)할 수 있다.
 *
 *   class 이름 implements 인터페이스A, 인터페이스B { ... }
 *
 * → 각 인터페이스의 추상 메소드를 모두 구현하면,
 *   그 클래스의 객체는 여러 규격을 동시에 만족하는 객체가 된다.
 */
public class MultipleImplementation {

    // 규격 1 : 날 수 있다
    interface Flyable {
        void fly();
    }

    // 규격 2 : 헤엄칠 수 있다
    interface Swimmable {
        void swim();
    }

    // 오리는 두 규격을 동시에 구현한다.
    static class Duck implements Flyable, Swimmable {
        @Override public void fly()  { System.out.println("오리가 날개를 펴고 납니다."); }
        @Override public void swim() { System.out.println("오리가 물 위를 헤엄칩니다."); }
    }

    public static void main(String[] args) {
        System.out.println("=== 8.8 다중 인터페이스 구현 ===");

        Duck duck = new Duck();

        // [1] 두 인터페이스 기능을 모두 사용
        System.out.println("\n[1] 여러 규격을 동시에 만족");
        duck.fly();
        duck.swim();

        // [2] 각 인터페이스 타입으로 나누어 다룰 수 있다
        System.out.println("\n[2] 필요한 규격 타입으로 취급");
        Flyable f = duck;    // 날 수 있는 것으로 취급
        Swimmable s = duck;  // 헤엄칠 수 있는 것으로 취급
        f.fly();
        s.swim();

        System.out.println("\n프로그램 정상 종료");
    }
}
