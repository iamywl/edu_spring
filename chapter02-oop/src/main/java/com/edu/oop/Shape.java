package com.edu.oop;

/**
 * 도형 sealed 클래스 (Java 17+)
 * - sealed: 상속할 수 있는 클래스를 permits로 명시적으로 제한
 * - 허용된 하위 클래스만 상속 가능
 * - 하위 클래스는 반드시 final, sealed, non-sealed 중 하나로 선언
 * - switch 패턴 매칭과 함께 사용하면 모든 경우를 컴파일 타임에 검증 가능
 */
public abstract sealed class Shape permits Circle, Rectangle {

    // 공통 필드
    private final String color;

    // 생성자
    protected Shape(String color) {
        this.color = color;
    }

    // Getter
    public String getColor() {
        return color;
    }

    /**
     * 넓이 계산 - 추상 메서드
     * 도형(Shape) 자체는 넓이를 정의할 수 없으므로(넓이 0인 도형은 의미가 없음)
     * 하위 클래스(Circle, Rectangle)가 반드시 구현해야 합니다.
     */
    public abstract double area();

    /** 도형 정보 출력 */
    public String describe() {
        return getClass().getSimpleName() + " (색상: " + color + ", 넓이: "
                + String.format("%.2f", area()) + ")";
    }
}
