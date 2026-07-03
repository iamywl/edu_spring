package com.edu.javabook.ch09;

/**
 * 9.2 인스턴스 멤버 클래스 (비정적 내부 클래스)
 *
 * 인스턴스 멤버 클래스는 바깥 클래스의 멤버 자리에 static 없이 선언한 클래스다.
 * 핵심 규칙: "바깥 객체가 반드시 있어야" 내부 클래스 객체를 만들 수 있다.
 * 즉, 내부 클래스의 모든 객체는 자신을 만든 바깥 객체 하나에 소속된다.
 *
 * 생성 문법 (바깥 밖에서):  바깥객체.new 내부클래스()
 *   예)  InstanceMemberClass outer = new InstanceMemberClass();
 *        InstanceMemberClass.Engine e = outer.new Engine();
 *
 * 특징:
 *   - 바깥 객체의 인스턴스 필드/메서드에 자유롭게 접근할 수 있다.
 *   - 각 바깥 객체마다 서로 다른 상태를 공유한다.
 *   - static 멤버(상수 제외)는 가질 수 없다. (바깥 객체에 묶이기 때문)
 *
 * 예제: 자동차(바깥) 안에 엔진(내부)을 두어, 엔진이 자동차의 상태를 공유한다.
 */
public class InstanceMemberClass {

    // 바깥 클래스의 인스턴스 필드
    private String carName;
    private int fuel;

    public InstanceMemberClass(String carName, int fuel) {
        this.carName = carName;
        this.fuel = fuel;
    }

    // ── 인스턴스 멤버 클래스 (static 없음) ──
    class Engine {
        // 내부 클래스는 바깥 객체(carName, fuel)에 직접 접근할 수 있다.
        void start() {
            System.out.println(carName + " 엔진 시동 (연료 " + fuel + ")");
        }

        void drive() {
            if (fuel <= 0) {
                System.out.println(carName + " 연료 없음 → 주행 불가");
                return;
            }
            fuel--;                 // 바깥 객체의 필드를 직접 수정
            System.out.println(carName + " 주행 → 남은 연료 " + fuel);
        }
    }

    public static void main(String[] args) {

        System.out.println("=== 9.2 인스턴스 멤버 클래스 ===");

        // [1] 바깥 객체를 먼저 만든다.
        System.out.println("\n[1] 바깥 객체 생성");
        InstanceMemberClass car = new InstanceMemberClass("소나타", 2);

        // [2] 바깥 객체를 통해 내부 클래스 객체를 만든다: 바깥객체.new 내부()
        System.out.println("\n[2] 바깥객체.new 내부클래스() 로 생성");
        InstanceMemberClass.Engine engine = car.new Engine();
        engine.start();
        engine.drive();
        engine.drive();
        engine.drive();             // 연료가 0이 되어 주행 불가

        // [3] 서로 다른 바깥 객체 → 서로 다른 상태를 공유
        System.out.println("\n[3] 바깥 객체마다 상태가 독립적이다");
        InstanceMemberClass car2 = new InstanceMemberClass("아반떼", 1);
        Engine engine2 = car2.new Engine();   // 같은 클래스 내부라 바로 new 가능
        engine2.start();
        engine2.drive();

        System.out.println("\n정리: 내부 객체는 항상 특정 바깥 객체에 소속된다.");
        System.out.println("프로그램 정상 종료");
    }
}
