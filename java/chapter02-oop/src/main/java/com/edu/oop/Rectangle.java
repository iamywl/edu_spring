package com.edu.oop;

/**
 * 직사각형 클래스 - Shape의 sealed 하위 클래스
 * - non-sealed: 자유롭게 상속 가능 (sealed 제한 해제)
 */
public non-sealed class Rectangle extends Shape {

    private final double width;   // 가로
    private final double height;  // 세로

    public Rectangle(String color, double width, double height) {
        super(color);
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("가로와 세로는 양수여야 합니다.");
        }
        this.width = width;
        this.height = height;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    /** 직사각형 넓이: 가로 × 세로 */
    @Override
    public double area() {
        return width * height;
    }

    /** 직사각형 둘레: 2 × (가로 + 세로) */
    public double perimeter() {
        return 2 * (width + height);
    }

    /** 정사각형 여부 */
    public boolean isSquare() {
        return Double.compare(width, height) == 0;
    }

    @Override
    public String toString() {
        return "Rectangle{color='" + getColor() + "', width=" + width + ", height=" + height + "}";
    }
}
