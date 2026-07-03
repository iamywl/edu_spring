package com.edu.javabook.ch07;

/**
 * 7.2 클래스 상속
 *
 * 자바에서 상속은 extends 키워드로 표현한다.
 *
 *   class 자식 extends 부모 { ... }
 *
 * - 자식은 부모의 필드/메소드를 물려받는다(단, private 은 직접 접근 불가).
 * - 단일 상속(single inheritance) : 자바 클래스는 부모를 "오직 하나"만 가질 수 있다.
 *   (여러 부모를 동시에 extends 할 수 없다 → 다이아몬드 문제 방지)
 * - 여러 단계로 이어지는 상속(계층 상속)은 가능하다. 예) A → B → C
 *
 * 이 소절에서는 extends 로 필드/메소드를 물려받는 모습과, 다단계 상속을 확인한다.
 */
public class ClassInheritance {

    // 1단계 : 최상위 부모
    static class Animal {
        String name;

        Animal(String name) {
            this.name = name;
        }

        void breathe() {
            System.out.println(name + " 숨을 쉰다.");
        }
    }

    // 2단계 : Animal 을 상속 (extends 하나만 가능 → 단일 상속)
    static class Dog extends Animal {

        Dog(String name) {
            super(name);
        }

        void bark() {
            System.out.println(name + " 멍멍 짖는다.");
        }
    }

    // 3단계 : Dog 를 상속 → Animal 의 기능까지 연쇄적으로 물려받는다 (다단계 상속)
    static class Puppy extends Dog {

        Puppy(String name) {
            super(name);
        }

        void whine() {
            System.out.println(name + " 낑낑거린다.");
        }
    }

    public static void main(String[] args) {

        System.out.println("=== 7.2 클래스 상속 ===");

        // [1] extends 로 물려받은 필드/메소드 사용
        System.out.println("\n[1] extends 로 필드와 메소드 물려받기");
        Dog dog = new Dog("바둑이");
        dog.breathe();   // Animal 에서 물려받음
        dog.bark();      // Dog 자신의 메소드
        System.out.println("물려받은 필드 name = " + dog.name);

        // [2] 다단계 상속 : Puppy 는 Dog + Animal 의 기능을 모두 가진다
        System.out.println("\n[2] 다단계 상속 (Animal → Dog → Puppy)");
        Puppy puppy = new Puppy("초코");
        puppy.breathe(); // Animal 에서
        puppy.bark();    // Dog 에서
        puppy.whine();   // Puppy 자신

        // [3] 단일 상속 설명
        System.out.println("\n[3] 단일 상속 (부모는 오직 하나)");
        System.out.println("자바 클래스는 extends 뒤에 부모를 하나만 쓸 수 있다.");
        System.out.println("여러 부모가 필요하면 인터페이스(implements)를 사용한다.");

        System.out.println("\n프로그램 정상 종료");
    }
}
