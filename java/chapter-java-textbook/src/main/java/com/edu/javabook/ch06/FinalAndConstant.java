package com.edu.javabook.ch06;

/**
 * 6.11 final 필드와 상수
 *
 * - final 필드: 한 번 값이 정해지면 다시 바꿀 수 없는 필드. 반드시 초기화해야 한다.
 *     (선언 시 또는 생성자에서 초기화. 객체마다 값이 다를 수 있다.)
 * - 상수(constant): static final 로 선언한, 값이 고정된 공유 값.
 *     관례상 이름을 대문자와 밑줄(_)로 쓴다. 예: MAX_SPEED, PI
 *
 * final은 '변하면 안 되는 값'을 코드로 못 박아, 실수로 인한 변경을 막아준다.
 */
public class FinalAndConstant {

    static class Circle {
        // 상수: 모든 원이 공유하는 고정값 (static final)
        static final double PI = 3.14159;

        // final 인스턴스 필드: 객체마다 다르지만, 한 번 정하면 못 바꿈
        final int radius;

        Circle(int radius) {
            this.radius = radius;   // 생성자에서 딱 한 번 초기화
        }

        double area() {
            return PI * radius * radius;
        }
    }

    public static void main(String[] args) {

        System.out.println("=== 6.11 final 필드와 상수 ===");

        // [1] 상수(static final) 사용 - 객체 없이 접근
        System.out.println("\n[1] 상수 (static final)");
        System.out.println("Circle.PI = " + Circle.PI + " (대문자 이름, 고정값)");

        // [2] final 인스턴스 필드 - 객체마다 다른 값이지만 변경 불가
        System.out.println("\n[2] final 인스턴스 필드");
        Circle c1 = new Circle(5);
        Circle c2 = new Circle(10);
        System.out.println("c1.radius = " + c1.radius + ", 넓이 = " + c1.area());
        System.out.println("c2.radius = " + c2.radius + ", 넓이 = " + c2.area());
        System.out.println("→ radius는 생성 후 재할당 불가 (c1.radius = 7; 은 컴파일 오류)");

        // [3] 지역 final 변수도 재할당 불가
        System.out.println("\n[3] final 지역 변수");
        final int MAX = 100;
        System.out.println("final int MAX = " + MAX + " (이후 MAX = ...; 은 오류)");

        // [4] 상수의 활용: 매직 넘버 제거
        System.out.println("\n[4] 상수로 의미 부여");
        System.out.println("숫자 3.14159 대신 Circle.PI 로 쓰면 의도가 명확해짐");

        // [왜?] final은 불변을 보장하여 코드의 안정성과 가독성을 높인다.
        System.out.println("\n[왜?] 바뀌면 안 되는 값을 final로 고정해 버그를 예방한다.");

        System.out.println("\n프로그램 정상 종료");
    }
}
