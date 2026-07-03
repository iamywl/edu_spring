package com.edu.javabook.ch08;

/**
 * 8.11 다형성
 *
 * 다형성(polymorphism)은 "같은 타입, 같은 호출, 다른 실행 결과"를 말한다.
 * 인터페이스 타입 하나로 여러 구현 객체를 담아, 동일한 메소드를 호출하면
 * 실제 담긴 객체에 따라 서로 다른 동작이 실행된다.
 *
 * - 매개변수 다형성 : 메소드가 인터페이스 타입을 받으면 어떤 구현이든 처리 가능
 * - 배열 다형성     : 인터페이스 타입 배열에 여러 구현 객체를 함께 담을 수 있다
 */
public class InterfacePolymorphism {

    interface Speaker {
        void speak();
    }

    static class Korean implements Speaker {
        @Override public void speak() { System.out.println("안녕하세요"); }
    }
    static class English implements Speaker {
        @Override public void speak() { System.out.println("Hello"); }
    }
    static class Japanese implements Speaker {
        @Override public void speak() { System.out.println("こんにちは"); }
    }

    public static void main(String[] args) {
        System.out.println("=== 8.11 다형성 ===");

        // [1] 인터페이스 타입 배열에 서로 다른 구현을 담는다
        System.out.println("\n[1] 배열 다형성 (한 배열, 여러 구현)");
        Speaker[] speakers = { new Korean(), new English(), new Japanese() };
        for (Speaker speaker : speakers) {
            speaker.speak();   // 같은 호출이지만 실제 객체마다 다른 결과
        }

        // [2] 인터페이스 타입 매개변수 → 어떤 구현이 와도 처리
        System.out.println("\n[2] 매개변수 다형성");
        greet(new Korean());
        greet(new English());

        System.out.println("\n프로그램 정상 종료");
    }

    // 어떤 Speaker 구현이든 받아서 동일하게 처리
    static void greet(Speaker speaker) {
        System.out.print("인사말: ");
        speaker.speak();
    }
}
