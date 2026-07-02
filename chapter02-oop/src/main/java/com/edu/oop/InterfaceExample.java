package com.edu.oop;

/**
 * [개념 6] 인터페이스(Interface)
 *
 * 인터페이스란?
 * - 클래스가 "무엇을 할 수 있어야 하는지"를 정의한 계약(contract).
 * - 메서드의 "선언(시그니처)"만 두고 구현은 구현 클래스에 맡긴다.
 * - 클래스는 단일 상속만 가능하지만, 인터페이스는 여러 개를 동시에 구현(implements)할 수 있다.
 *
 * 이 예제에서 사용하는 것: Flyable 인터페이스 <- Bird 가 구현
 *
 * Flyable 이 가진 것:
 *   - 추상 메서드: fly(), getMaxAltitude()  (구현 클래스가 반드시 구현)
 *   - default 메서드: land(), flightStatus()  (기본 구현 제공, 필요 시 오버라이드)
 *   - static 메서드: description(), isSafeAltitude(...)  (인터페이스 이름으로 직접 호출)
 *
 * 또한 Bird 는 'extends Animal implements Flyable' 로,
 * 클래스 상속과 인터페이스 구현을 동시에 하는 예도 함께 보여준다.
 */
public class InterfaceExample {

    public static void main(String[] args) {

        // ------------------------------------------------------------
        // 1) 인터페이스 타입으로 참조 (구현체를 인터페이스로 다루기)
        // ------------------------------------------------------------
        printSection("1. 인터페이스 타입으로 참조");

        // Bird는 Flyable을 구현했으므로 Flyable 타입 변수에 담을 수 있다.
        Flyable sparrow = new Bird("참새", 1, "참새", 500);
        Flyable eagle = new Bird("독수리", 5, "독수리", 3000);

        // 인터페이스 배열로 통일된 처리 (다형성)
        Flyable[] flyers = {sparrow, eagle};
        System.out.println("Flyable 타입으로 참새/독수리를 통일되게 다룬다.");

        // ------------------------------------------------------------
        // 2) static 메서드: 인터페이스 이름으로 직접 호출
        // ------------------------------------------------------------
        printSection("2. static 메서드 (인터페이스에서 직접 호출)");

        // 구현 객체 없이 Flyable.메서드() 형태로 호출한다.
        System.out.println(Flyable.description());

        // ------------------------------------------------------------
        // 3) 추상 메서드 + default 메서드
        // ------------------------------------------------------------
        printSection("3. 추상 메서드 vs default 메서드");

        for (Flyable flyer : flyers) {
            System.out.println("fly() [추상, 구현 필수]      -> " + flyer.fly());
            System.out.println("flightStatus() [default]     -> " + flyer.flightStatus());
            // Bird는 land() default 메서드를 오버라이드했다.
            System.out.println("land() [default를 오버라이드]  -> " + flyer.land());
            System.out.println("isSafeAltitude() [static] -> 안전 고도? "
                    + Flyable.isSafeAltitude(flyer.getMaxAltitude()));
            System.out.println();
        }

        // ------------------------------------------------------------
        // 4) 클래스 상속 + 인터페이스 구현 동시에
        // ------------------------------------------------------------
        printSection("4. Bird = Animal 상속 + Flyable 구현");

        // 같은 Bird 객체를 두 가지 시각으로 볼 수 있다:
        //  - Animal 로 보면 speak()/info() 같은 동물의 능력
        //  - Flyable 로 보면 fly()/land() 같은 나는 능력
        Bird bird = new Bird("매", 4, "매", 2000);
        Animal asAnimal = bird;    // Animal 관점
        Flyable asFlyable = bird;  // Flyable 관점

        System.out.println("Animal 관점  -> " + asAnimal.speak());
        System.out.println("Animal 관점  -> " + asAnimal.info());
        System.out.println("Flyable 관점 -> " + asFlyable.fly());
        System.out.println("(둘 다 동일한 Bird 객체를 서로 다른 계약으로 바라본 것)");

        // ------------------------------------------------------------
        // 5) 다중 구현 개념 설명
        // ------------------------------------------------------------
        printSection("5. 다중 구현 개념");
        System.out.println("- 클래스는 하나만 extends 할 수 있다(단일 상속).");
        System.out.println("- 하지만 인터페이스는 여러 개를 implements 할 수 있다.");
        System.out.println("  예) class Duck extends Animal implements Flyable, Swimmable { ... }");
        System.out.println("- 즉, '~할 수 있다(can-do)' 능력을 여러 개 조합할 수 있다.");

        // ------------------------------------------------------------
        // 정리
        // ------------------------------------------------------------
        printSection("정리");
        System.out.println("- 인터페이스 = 구현해야 할 계약(무엇을 할 수 있는가).");
        System.out.println("- 추상 메서드는 구현 필수, default 메서드는 기본 구현 제공.");
        System.out.println("- static 메서드는 인터페이스 이름으로 직접 호출.");
        System.out.println("- 클래스는 상속 1개 + 인터페이스 여러 개를 조합할 수 있다.");
    }

    private static void printSection(String title) {
        System.out.println();
        System.out.println("=".repeat(60));
        System.out.println("  " + title);
        System.out.println("=".repeat(60));
    }
}
