package com.edu.oop;

/**
 * [개념 2] 다형성(Polymorphism)
 *
 * 다형성이란?
 * - "하나의 타입(부모)으로 여러 형태(자식)를 다룰 수 있는" 능력.
 * - 부모 타입 변수에 자식 객체를 담고(업캐스팅),
 *   같은 메서드를 호출해도 실제 객체의 타입에 따라 다른 동작이 실행된다.
 *
 * 핵심 개념:
 *   - 업캐스팅(Upcasting) : 자식 객체를 부모 타입 변수에 대입 (자동, 안전)
 *     예) Animal a = new Dog(...);
 *   - 동적 디스패치(Dynamic Dispatch / 동적 바인딩) :
 *     a.speak() 호출 시, 컴파일 타임의 타입(Animal)이 아니라
 *     런타임의 실제 타입(Dog)에 구현된 speak()가 실행된다.
 *
 * 왜 유용한가?
 *   - 새로운 자식 타입이 추가돼도 호출하는 코드는 바뀌지 않는다(확장에 열려 있음).
 *   - Animal[] 하나로 Dog/Cat/Bird 를 모두 통일된 방식으로 처리할 수 있다.
 */
public class PolymorphismExample {

    public static void main(String[] args) {

        // ------------------------------------------------------------
        // 1) 업캐스팅: 자식 객체를 부모 타입으로 참조
        // ------------------------------------------------------------
        printSection("1. 업캐스팅 (부모 타입 참조)");

        // 우변은 각각 Dog/Cat/Bird 객체지만, 좌변 변수 타입은 모두 Animal.
        // 자식 -> 부모 방향의 대입은 자동으로 이루어진다(형변환 표기 불필요).
        Animal dog = new Dog("바둑이", 3, "진돗개");
        Animal cat = new Cat("나비", 2, true);
        Animal bird = new Bird("짹짹이", 1, "참새", 500);

        System.out.println("변수 타입은 모두 Animal 이지만 실제 객체는 각각 다릅니다:");
        System.out.println("  dog 의 실제 타입  -> " + dog.getClass().getSimpleName());
        System.out.println("  cat 의 실제 타입  -> " + cat.getClass().getSimpleName());
        System.out.println("  bird 의 실제 타입 -> " + bird.getClass().getSimpleName());

        // ------------------------------------------------------------
        // 2) 동적 디스패치: 같은 호출, 다른 결과
        // ------------------------------------------------------------
        printSection("2. 동적 디스패치 (같은 speak() 호출, 다른 동작)");

        // 부모 타입 배열로 자식들을 한 번에 담는다 -> 다형성의 핵심 활용.
        Animal[] animals = {dog, cat, bird};

        for (Animal animal : animals) {
            // animal 의 컴파일 타임 타입은 Animal 이지만,
            // 실제 실행되는 speak()는 런타임 객체(Dog/Cat/Bird)의 것이다.
            System.out.println(animal.info());
            System.out.println("  speak() -> " + animal.speak());
        }

        // ------------------------------------------------------------
        // 3) 왜 유용한가: 통일된 처리 + 확장성
        // ------------------------------------------------------------
        printSection("3. 다형성이 유용한 이유");

        // 아래 makeEveryoneSpeak 메서드는 Animal 타입만 알면 된다.
        // 새로운 동물(예: 새로운 Animal 자식)이 생겨도 이 메서드는 수정할 필요가 없다.
        makeEveryoneSpeak(animals);

        System.out.println();
        System.out.println("포인트: makeEveryoneSpeak()는 구체 타입을 몰라도 동작한다.");
        System.out.println("       -> 코드 재사용 & 확장에 열려 있음(OCP).");

        // ------------------------------------------------------------
        // 4) 정리
        // ------------------------------------------------------------
        printSection("정리");
        System.out.println("- 업캐스팅: Animal a = new Dog(...); (자동, 안전)");
        System.out.println("- 동적 디스패치: a.speak() 는 실제 타입의 메서드를 실행.");
        System.out.println("- 하나의 부모 타입으로 여러 자식을 통일되게 다룰 수 있다.");
    }

    /**
     * 다형성 활용 메서드: 파라미터가 Animal 배열이므로
     * 어떤 종류의 동물이 들어와도 동일한 코드로 처리한다.
     */
    private static void makeEveryoneSpeak(Animal[] animals) {
        for (Animal animal : animals) {
            System.out.println(animal.getName() + " 말한다 -> " + animal.speak());
        }
    }

    private static void printSection(String title) {
        System.out.println();
        System.out.println("=".repeat(60));
        System.out.println("  " + title);
        System.out.println("=".repeat(60));
    }
}
