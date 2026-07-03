package com.edu.javabook.ch08;

/**
 * 8.13 봉인된 인터페이스
 *
 * sealed 인터페이스는 "이 인터페이스를 구현/상속할 수 있는 타입"을
 * permits 절로 명시적으로 제한한다(자바 17+).
 *
 *   sealed interface 이름 permits 허용타입1, 허용타입2 { ... }
 *
 * - permits 에 나열된 타입만 구현할 수 있다(무분별한 확장 차단).
 * - 허용된 구현 타입은 반드시 다음 중 하나여야 한다:
 *     final     : 더 이상 상속 불가
 *     sealed    : 다시 자신의 허용 목록을 가짐
 *     non-sealed: 봉인을 풀어 자유로운 확장 허용
 * - switch 패턴 매칭과 함께 쓰면 모든 경우가 다뤄졌음을 컴파일러가 보장한다.
 */
public class SealedInterface {

    // 봉인된 인터페이스 : Circle 과 Rectangle 만 구현할 수 있다.
    sealed interface Shape permits Circle, Rectangle {
        double area();
    }

    // 허용된 구현 1 : final 로 확장을 차단
    static final class Circle implements Shape {
        final double radius;
        Circle(double radius) { this.radius = radius; }
        @Override public double area() { return Math.PI * radius * radius; }
    }

    // 허용된 구현 2 : final 로 확장을 차단
    static final class Rectangle implements Shape {
        final double width, height;
        Rectangle(double width, double height) { this.width = width; this.height = height; }
        @Override public double area() { return width * height; }
    }

    public static void main(String[] args) {
        System.out.println("=== 8.13 봉인된 인터페이스 ===");

        Shape[] shapes = { new Circle(2), new Rectangle(3, 4) };

        // [1] permits 로 허용된 구현들만 사용
        System.out.println("\n[1] 허용된 구현 타입만 존재");
        for (Shape shape : shapes) {
            System.out.printf("%s 넓이 = %.2f%n",
                    shape.getClass().getSimpleName(), shape.area());
        }

        // [2] switch 패턴 매칭 : 봉인되어 모든 경우가 보장됨 (default 불필요)
        System.out.println("\n[2] switch 패턴 매칭 (모든 경우 처리 보장)");
        for (Shape shape : shapes) {
            String desc = switch (shape) {
                case Circle c    -> "반지름 " + c.radius + "인 원";
                case Rectangle r -> r.width + "x" + r.height + " 직사각형";
            };
            System.out.println(desc);
        }

        System.out.println("\n프로그램 정상 종료");
    }
}
