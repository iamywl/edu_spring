package com.edu.oop;

/**
 * 원 클래스 - Shape의 sealed 하위 클래스
 * - final: 더 이상 상속할 수 없음
 */
public final class Circle extends Shape {

    private final double radius;  // 반지름

    public Circle(String color, double radius) {
        super(color);
        if (radius <= 0) {
            throw new IllegalArgumentException("반지름은 양수여야 합니다.");
        }
        this.radius = radius;
    }

    public double getRadius() {
        return radius;
    }

    /** 원의 넓이: π × r² */
    @Override
    public double area() {
        return Math.PI * radius * radius;
    }

    /** 원의 둘레: 2 × π × r */
    public double circumference() {
        return 2 * Math.PI * radius;
    }

    @Override
    public String toString() {
        return "Circle{color='" + getColor() + "', radius=" + radius + "}";
    }
}
