package com.edu.javabook.ch07;

/**
 * 7.7 타입 변환 (업캐스팅 / 다운캐스팅)
 *
 * 상속 관계에서는 부모 타입과 자식 타입 사이에 변환이 일어난다.
 *
 * - 업캐스팅(upcasting)   : 자식 타입 → 부모 타입.
 *                          "자식 is-a 부모" 이므로 항상 안전하다. 자동(암시적)으로 된다.
 *                          단, 부모 타입 변수로는 "부모가 가진 멤버"만 호출할 수 있다.
 *
 * - 다운캐스팅(downcasting): 부모 타입 → 자식 타입.
 *                          강제(명시적) 캐스트 (자식) 가 필요하다.
 *                          실제 객체가 그 자식이 아닐 경우 실행 중 ClassCastException 이 난다.
 *                          → 다운캐스팅 전에는 instanceof 로 확인하는 것이 안전하다(7.9 참고).
 *
 * 이 소절에서는 업캐스팅과 다운캐스팅의 차이, 그리고 잘못된 다운캐스팅을 확인한다.
 */
public class TypeCasting {

    static class Animal {
        void breathe() {
            System.out.println("숨을 쉰다.");
        }
    }

    static class Bird extends Animal {
        void fly() {
            System.out.println("난다.");
        }
    }

    public static void main(String[] args) {

        System.out.println("=== 7.7 타입 변환 ===");

        // [1] 업캐스팅 : 자식 → 부모 (자동, 항상 안전)
        System.out.println("\n[1] 업캐스팅 (자식 → 부모, 자동)");
        Bird bird = new Bird();
        Animal animal = bird;   // 업캐스팅: 캐스트 연산자 없이 자동
        animal.breathe();       // 부모 멤버는 호출 가능
        // animal.fly();        // 부모 타입 변수라 fly() 는 보이지 않음(컴파일 오류)
        System.out.println("→ 부모 타입 변수로는 부모의 멤버만 사용 가능하다.");

        // [2] 다운캐스팅 : 부모 → 자식 (강제 캐스트 필요)
        System.out.println("\n[2] 다운캐스팅 (부모 → 자식, 강제 캐스트)");
        // animal 이 실제로는 Bird 객체이므로 안전하게 되돌릴 수 있다.
        Bird backToBird = (Bird) animal;   // 명시적 캐스트
        backToBird.fly();                  // 이제 자식 멤버 호출 가능

        // [3] 잘못된 다운캐스팅 : 실제 객체가 그 자식이 아니면 예외 발생
        System.out.println("\n[3] 잘못된 다운캐스팅 (ClassCastException 발생 예)");
        Animal plain = new Animal();       // 실제 객체는 그냥 Animal
        try {
            Bird wrong = (Bird) plain;     // Animal 을 Bird 로 강제 → 실행 중 예외
            wrong.fly();
        } catch (ClassCastException e) {
            System.out.println("예외 발생 : " + e.getClass().getSimpleName());
            System.out.println("→ 그래서 다운캐스팅 전 instanceof 확인이 안전하다(7.9).");
        }

        System.out.println("\n프로그램 정상 종료");
    }
}
