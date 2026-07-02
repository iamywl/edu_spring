package com.edu.javabook.ch07;

/**
 * 7.4 메소드 재정의 (Overriding)
 *
 * 부모에게 물려받은 메소드가 자식에게 맞지 않으면, 자식이 "같은 메소드를 다시 정의"할 수 있다.
 * 이것을 메소드 재정의(overriding)라고 한다.
 *
 * 재정의 규칙 :
 *   1) 메소드 이름 / 매개변수 목록 / 리턴 타입이 부모와 같아야 한다.
 *   2) 접근 제한자는 부모와 같거나 더 넓어야 한다(예: protected → public 가능, protected → private 불가).
 *   3) @Override 애노테이션을 붙이면, 규칙을 어겼을 때 컴파일러가 오류로 잡아준다(강력 권장).
 *
 * super.메소드() 를 쓰면 재정의한 자식 안에서 "부모의 원래 메소드"도 호출할 수 있다.
 *
 * 이 소절에서는 부모 메소드를 자식이 어떻게 바꿔 부르는지 확인한다.
 */
public class MethodOverriding {

    static class Animal {
        void sound() {
            System.out.println("동물이 소리를 낸다.");
        }
    }

    static class Cat extends Animal {
        // 부모의 sound() 를 자식에 맞게 재정의
        @Override
        void sound() {
            System.out.println("야옹");
        }
    }

    static class Dog extends Animal {
        @Override
        void sound() {
            // super 로 부모의 원래 동작도 함께 실행할 수 있다.
            super.sound();
            System.out.println("(그리고) 멍멍");
        }
    }

    public static void main(String[] args) {

        System.out.println("=== 7.4 메소드 재정의 ===");

        // [1] 자식마다 같은 메소드가 다르게 동작
        System.out.println("\n[1] 같은 sound() 이지만 자식마다 다르게 동작");
        Animal a = new Animal();
        Cat cat = new Cat();
        Dog dog = new Dog();

        a.sound();     // 부모 원본
        cat.sound();   // Cat 이 재정의한 버전
        dog.sound();   // Dog 이 재정의한 버전(super 포함)

        // [2] @Override 의 역할
        System.out.println("\n[2] @Override 의 역할");
        System.out.println("- 재정의 규칙을 어기면 컴파일러가 오류로 알려준다.");
        System.out.println("- 예: 이름 오타(soundd) 시 재정의가 아니라 '새 메소드'가 되는 실수를 막아준다.");

        System.out.println("\n프로그램 정상 종료");
    }
}
