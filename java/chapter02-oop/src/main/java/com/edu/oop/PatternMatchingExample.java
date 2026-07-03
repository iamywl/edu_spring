package com.edu.oop;

/**
 * [개념 5] 패턴 매칭(Pattern Matching)
 *
 * 패턴 매칭이란?
 * - 값의 "타입/구조를 검사"하면서 동시에 "변수로 바인딩"하는 기능.
 * - 예전에는 instanceof 로 타입 검사 후, 다시 명시적 형변환(cast)을 해야 했다.
 *   패턴 매칭은 이 두 단계를 하나로 합쳐 코드가 짧고 안전해진다.
 *
 * 이 예제에서 다루는 것:
 *   (1) instanceof 패턴 매칭 (Java 16+): if (x instanceof Dog d) { ... d 사용 ... }
 *   (2) switch 패턴 매칭 (Java 21+): switch (x) { case Dog d -> ... }
 *
 * Animal 계층(Dog/Cat/Bird)에 적용하여 각 타입 고유 동작을 호출한다.
 */
public class PatternMatchingExample {

    public static void main(String[] args) {

        // 부모 타입(Animal) 배열에 서로 다른 자식들을 담는다.
        Animal[] animals = {
                new Dog("바둑이", 3, "진돗개"),
                new Cat("나비", 2, true),
                new Bird("짹짹이", 1, "참새", 500)
        };

        // ------------------------------------------------------------
        // 0) 옛날 방식(참고): instanceof + 명시적 형변환
        // ------------------------------------------------------------
        printSection("0. (참고) 패턴 매칭 이전의 옛날 방식");

        for (Animal animal : animals) {
            if (animal instanceof Dog) {
                // 타입 검사 후, 다시 (Dog)로 캐스팅해야 했다 -> 중복/장황
                Dog d = (Dog) animal;
                System.out.println("[옛날] " + d.getName() + " -> " + d.fetch());
            }
        }

        // ------------------------------------------------------------
        // 1) instanceof 패턴 매칭 (Java 16+)
        // ------------------------------------------------------------
        printSection("1. instanceof 패턴 매칭");

        for (Animal animal : animals) {
            // 'animal instanceof Dog d' 가 true면 d(=Dog로 캐스팅된 값)를 바로 사용 가능.
            if (animal instanceof Dog d) {
                System.out.println(d.getName() + " (개) 고유 행동 -> " + d.fetch());
            } else if (animal instanceof Cat c) {
                System.out.println(c.getName() + " (고양이) 고유 행동 -> " + c.purr());
            } else if (animal instanceof Bird b) {
                System.out.println(b.getName() + " (새) 고유 행동 -> " + b.fly());
            }
        }

        // ------------------------------------------------------------
        // 2) switch 패턴 매칭 (Java 21+)
        // ------------------------------------------------------------
        printSection("2. switch 패턴 매칭");

        for (Animal animal : animals) {
            // case 라벨 자체가 타입 패턴이며, 매칭되면 변수(d/c/b)로 바인딩된다.
            String action = switch (animal) {
                case Dog d  -> d.getName() + " -> " + d.fetch();
                case Cat c  -> c.getName() + " -> " + c.purr();
                case Bird b -> b.getName() + " -> " + b.fly();
                default     -> animal.getName() + " -> (고유 행동 없음)";
            };
            System.out.println(action);
        }

        // ------------------------------------------------------------
        // 3) switch 패턴 + when 가드(guard)
        // ------------------------------------------------------------
        printSection("3. switch 패턴 매칭 + when 조건");

        for (Animal animal : animals) {
            // 'when' 을 붙이면 타입뿐 아니라 추가 조건까지 함께 검사할 수 있다.
            String desc = switch (animal) {
                case Bird b when b.getMaxAltitude() >= 1000 -> b.getName() + ": 고공 비행 새";
                case Bird b                                 -> b.getName() + ": 저공 비행 새";
                case Cat c when c.isIndoor()                -> c.getName() + ": 실내 고양이";
                case Cat c                                  -> c.getName() + ": 실외 고양이";
                default                                     -> animal.getName() + ": 그 외 동물";
            };
            System.out.println(desc);
        }

        // ------------------------------------------------------------
        // 정리
        // ------------------------------------------------------------
        printSection("정리");
        System.out.println("- instanceof 패턴: 타입 검사 + 변수 바인딩을 한 번에 (Java 16+).");
        System.out.println("- switch 패턴: case 라벨에 타입을 써서 분기 (Java 21+).");
        System.out.println("- when 가드로 타입 + 추가 조건을 동시에 검사할 수 있다.");
        System.out.println("- 명시적 캐스팅이 사라져 코드가 짧고 안전해진다.");
    }

    private static void printSection(String title) {
        System.out.println();
        System.out.println("=".repeat(60));
        System.out.println("  " + title);
        System.out.println("=".repeat(60));
    }
}
