package com.edu.javabook.ch07;

/**
 * 7.9 객체 타입 확인 (instanceof)
 *
 * 부모 타입 변수가 실제로 어떤 자식 객체를 가리키는지 알아야 할 때 instanceof 를 쓴다.
 *
 *   객체 instanceof 타입   → 객체가 그 타입(또는 그 자식)이면 true, 아니면 false
 *
 * - 다운캐스팅(7.7) 전에 instanceof 로 확인하면 ClassCastException 을 예방할 수 있다.
 * - 패턴 매칭 instanceof (Java 16+) :
 *       if (obj instanceof Dog dog) { ... dog 사용 ... }
 *   확인과 동시에 캐스팅된 변수(dog)를 바로 만들어 준다 → 별도 캐스트 코드가 필요 없다.
 *
 * 이 소절에서는 전통적 instanceof 와 패턴 매칭 instanceof 를 비교한다.
 */
public class InstanceofCheck {

    static class Animal {
        String name;
        Animal(String name) { this.name = name; }
    }

    static class Dog extends Animal {
        Dog(String name) { super(name); }
        void bark() { System.out.println(name + " 멍멍"); }
    }

    static class Cat extends Animal {
        Cat(String name) { super(name); }
        void meow() { System.out.println(name + " 야옹"); }
    }

    public static void main(String[] args) {

        System.out.println("=== 7.9 객체 타입 확인 ===");

        Animal[] animals = {
                new Dog("바둑이"),
                new Cat("나비"),
                new Animal("이름없는동물")
        };

        // [1] 전통적 instanceof + 명시적 캐스트
        System.out.println("\n[1] 전통적 instanceof (확인 후 직접 캐스트)");
        for (Animal a : animals) {
            if (a instanceof Dog) {
                Dog d = (Dog) a;   // 확인했으니 안전하게 다운캐스팅
                d.bark();
            } else if (a instanceof Cat) {
                Cat c = (Cat) a;
                c.meow();
            } else {
                System.out.println(a.name + " 은(는) 특정 소리가 없다.");
            }
        }

        // [2] 패턴 매칭 instanceof (Java 16+) : 확인 + 캐스팅을 한 번에
        System.out.println("\n[2] 패턴 매칭 instanceof (확인과 동시에 변수 생성)");
        for (Animal a : animals) {
            if (a instanceof Dog d) {        // 참이면 d 에 캐스팅된 값이 바로 들어감
                d.bark();
            } else if (a instanceof Cat c) {
                c.meow();
            } else {
                System.out.println(a.name + " 은(는) 특정 소리가 없다.");
            }
        }

        System.out.println("\n프로그램 정상 종료");
    }
}
