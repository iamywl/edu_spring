package com.edu.javabook.ch08;

/**
 * 8.2 인터페이스와 구현 클래스 선언
 *
 * 인터페이스 선언 : interface 키워드로 선언한다.
 * 구현 클래스     : class 선언 뒤에 implements 인터페이스이름 을 붙이고,
 *                  인터페이스의 추상 메소드를 "모두" 재정의(구현)해야 한다.
 *
 *   interface 이름 { ... }
 *   class 이름 implements 인터페이스이름 { ... }
 */
public class InterfaceDeclaration {

    // 인터페이스 선언
    interface Animal {
        void sound();   // 구현 클래스가 반드시 채워야 하는 추상 메소드
    }

    // implements 로 구현 클래스 선언 (추상 메소드를 모두 구현)
    static class Dog implements Animal {
        @Override
        public void sound() {
            System.out.println("멍멍");
        }
    }

    static class Cat implements Animal {
        @Override
        public void sound() {
            System.out.println("야옹");
        }
    }

    public static void main(String[] args) {
        System.out.println("=== 8.2 인터페이스와 구현 클래스 선언 ===");

        // [1] 구현 객체를 그 클래스 타입으로 사용
        System.out.println("\n[1] 구현 클래스 타입으로 사용");
        Dog dog = new Dog();
        dog.sound();

        // [2] 인터페이스 타입 변수에 구현 객체를 대입
        System.out.println("\n[2] 인터페이스 타입 변수로 사용");
        Animal animal = new Cat();   // implements 관계이므로 대입 가능
        animal.sound();

        System.out.println("\n프로그램 정상 종료");
    }
}
