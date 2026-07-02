package com.edu.javabook.ch08;

/**
 * 8.6 정적 메소드
 *
 * 인터페이스에도 static 메소드를 정의할 수 있으며, 몸통(구현)을 가진다.
 *
 * - 호출 방법 : 인터페이스이름.메소드() 로 직접 호출한다(객체 필요 없음).
 * - 구현 클래스나 객체를 통해서는 호출할 수 없다(인터페이스 이름 전용).
 * - 주로 인터페이스와 관련된 "도우미(유틸리티)" 기능을 담는다.
 *   예) 팩토리 메소드, 검증 메소드
 */
public class StaticMethodInterface {

    interface Shape {
        double area();   // 추상 메소드

        // 정적 메소드 : 인터페이스와 관련된 유틸리티 (팩토리 역할)
        static Shape createSquare(double side) {
            return () -> side * side;   // 함수형 인터페이스이므로 람다로 구현
        }

        // 정적 메소드 : 넓이가 유효한지 검증하는 도우미
        static boolean isValidArea(double area) {
            return area > 0;
        }
    }

    public static void main(String[] args) {
        System.out.println("=== 8.6 정적 메소드 ===");

        // [1] 인터페이스이름.정적메소드() 로 객체 생성 (팩토리)
        System.out.println("\n[1] 정적 팩토리 메소드로 객체 생성");
        Shape square = Shape.createSquare(4);
        System.out.println("한 변이 4인 정사각형 넓이 = " + square.area());

        // [2] 인터페이스이름.정적메소드() 로 검증 도우미 사용
        System.out.println("\n[2] 정적 유틸리티 메소드 사용");
        System.out.println("넓이 16 유효? " + Shape.isValidArea(square.area()));
        System.out.println("넓이 -1 유효? " + Shape.isValidArea(-1));

        System.out.println("\n프로그램 정상 종료");
    }
}
