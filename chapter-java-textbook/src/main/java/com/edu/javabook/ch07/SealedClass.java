package com.edu.javabook.ch07;

/**
 * 7.11 봉인된 클래스 (Sealed Class) — Java 17+
 *
 * sealed(봉인된) 클래스는 "누가 나를 상속할 수 있는지"를 부모가 직접 지정하는 클래스이다.
 * final(상속 완전 금지)과 일반 클래스(누구나 상속 가능)의 "중간" 단계라고 볼 수 있다.
 *
 *   sealed class Shape permits Circle, Rectangle, Triangle { ... }
 *
 * - permits : 상속을 허용할 자식 목록을 명시한다. 목록에 없는 클래스는 상속할 수 없다.
 * - sealed 의 자식은 반드시 아래 셋 중 하나여야 한다 :
 *       final       : 더 이상 상속 불가(자식 목록을 여기서 닫음)
 *       sealed      : 이 자식도 다시 자신의 permits 로 상속 대상을 제한
 *       non-sealed  : 봉인을 풀어, 이 아래로는 누구나 상속 가능
 *
 * 쓰는 이유 : 자식의 종류를 "닫힌 집합"으로 고정할 수 있어,
 *            switch 등에서 모든 경우를 안전하게 다루기 좋다(도메인 모델링에 유용).
 *
 * 이 소절에서는 permits 로 자식을 제한하고, final/non-sealed 자식을 확인한다.
 */
public class SealedClass {

    // 봉인된 부모 : Circle, Rectangle 만 상속 허용
    sealed static abstract class Shape permits Circle, Rectangle {
        abstract double area();
    }

    // 자식 1 : final → 여기서 상속을 닫는다
    static final class Circle extends Shape {
        double radius;
        Circle(double radius) { this.radius = radius; }
        @Override double area() { return Math.PI * radius * radius; }
    }

    // 자식 2 : non-sealed → 봉인 해제, 이 아래로는 누구나 상속 가능
    static non-sealed class Rectangle extends Shape {
        double w, h;
        Rectangle(double w, double h) { this.w = w; this.h = h; }
        @Override double area() { return w * h; }
    }

    // non-sealed 인 Rectangle 은 제한 없이 상속 가능(permits 없이도 OK)
    static class Square extends Rectangle {
        Square(double side) { super(side, side); }
    }

    public static void main(String[] args) {

        System.out.println("=== 7.11 봉인된 클래스 ===");

        // [1] permits 로 허용된 자식만 만들 수 있다
        System.out.println("\n[1] sealed + permits (허용된 자식만 상속 가능)");
        Shape[] shapes = {
                new Circle(2.0),
                new Rectangle(3.0, 4.0),
                new Square(5.0)     // Rectangle(non-sealed)의 자식
        };
        for (Shape s : shapes) {
            System.out.printf("%s 넓이 = %.2f%n",
                    s.getClass().getSimpleName(), s.area());
        }

        // [2] final / non-sealed 의 의미
        System.out.println("\n[2] 자식이 가질 수 있는 세 가지 형태");
        System.out.println("- Circle    : final     → 더 이상 상속 불가(집합을 닫음)");
        System.out.println("- Rectangle : non-sealed → 봉인 해제, 아래로는 자유 상속(Square 가능)");
        System.out.println("- (또는 sealed 로 다시 자식 목록을 제한할 수도 있다)");

        // [3] 닫힌 집합이라 switch 로 안전하게 분기 가능
        System.out.println("\n[3] 닫힌 집합을 활용한 타입 분기");
        for (Shape s : shapes) {
            String kind = switch (s) {
                case Circle c -> "원(반지름 " + c.radius + ")";
                case Rectangle r -> "사각형 계열(" + r.w + "x" + r.h + ")";
            };
            System.out.println(kind);
        }

        System.out.println("\n프로그램 정상 종료");
    }
}
