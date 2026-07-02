package com.edu.javabook.ch08;

/**
 * 8.1 인터페이스의 역할
 *
 * 인터페이스는 "무엇을 할 수 있는가(규격)"만 정의하고,
 * "어떻게 하는가(구현)"는 구현 클래스에 맡긴다.
 *
 * - 규격과 구현의 분리 : 사용하는 쪽은 인터페이스 타입만 알면 되고,
 *                        구현이 바뀌어도 코드를 고칠 필요가 없다.
 * - 다형성의 기반       : 같은 인터페이스 타입 변수로 서로 다른 구현 객체를
 *                        똑같은 방식으로 다룰 수 있다.
 */
public class InterfaceRole {

    // 규격(인터페이스) : 리모컨은 "켜고/끄는" 기능이 있다는 약속만 한다.
    interface RemoteControl {
        void turnOn();   // 어떻게 켜는지는 정의하지 않는다.
        void turnOff();
    }

    // 구현 A : 텔레비전
    static class Television implements RemoteControl {
        @Override public void turnOn()  { System.out.println("TV 화면이 켜졌습니다."); }
        @Override public void turnOff() { System.out.println("TV 화면이 꺼졌습니다."); }
    }

    // 구현 B : 오디오
    static class Audio implements RemoteControl {
        @Override public void turnOn()  { System.out.println("오디오 전원이 켜졌습니다."); }
        @Override public void turnOff() { System.out.println("오디오 전원이 꺼졌습니다."); }
    }

    public static void main(String[] args) {
        System.out.println("=== 8.1 인터페이스의 역할 ===");

        // [1] 규격과 구현의 분리
        // 사용하는 쪽(operate 메소드)은 RemoteControl 규격만 알면 된다.
        System.out.println("\n[1] 규격과 구현의 분리");
        operate(new Television());
        operate(new Audio());

        // [2] 다형성 : 같은 타입 변수로 다른 구현을 담아 동일하게 호출
        System.out.println("\n[2] 다형성의 기반 (같은 타입, 다른 동작)");
        RemoteControl rc = new Television();
        rc.turnOn();
        rc = new Audio();      // 같은 변수에 다른 구현을 담아도 됨
        rc.turnOn();

        System.out.println("\n프로그램 정상 종료");
    }

    // 인터페이스 타입만 받으므로, 어떤 구현이 오든 동작한다.
    static void operate(RemoteControl rc) {
        rc.turnOn();
        rc.turnOff();
    }
}
