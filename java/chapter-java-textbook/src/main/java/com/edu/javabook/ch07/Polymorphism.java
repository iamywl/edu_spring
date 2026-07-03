package com.edu.javabook.ch07;

/**
 * 7.8 다형성 (Polymorphism)
 *
 * 다형성이란 "하나의 부모 타입 참조로 여러 자식 객체를 다룰 수 있고,
 * 실제 실행되는 메소드는 객체의 실제 타입에 따라 결정되는" 성질이다.
 *
 * 핵심 두 가지 :
 *  - 부모 타입 참조 : 부모 타입 변수 하나로 어떤 자식 객체든 가리킬 수 있다(업캐스팅).
 *                    덕분에 배열/매개변수를 부모 타입 하나로 통일해 다룰 수 있다.
 *  - 동적 바인딩(dynamic binding) :
 *                    부모 타입 변수로 재정의된 메소드를 호출하면,
 *                    "변수의 타입"이 아니라 "실제 객체의 타입"에 정의된 메소드가 실행된다.
 *                    (컴파일 시점이 아니라 실행 시점에 어떤 메소드가 불릴지 결정)
 *
 * 이 소절에서는 부모 타입 배열로 여러 자식을 순회하며, 동적 바인딩을 눈으로 확인한다.
 */
public class Polymorphism {

    static class Shape {
        double area() {
            return 0;
        }

        String shapeName() {
            return "도형";
        }
    }

    static class Circle extends Shape {
        double radius;

        Circle(double radius) {
            this.radius = radius;
        }

        @Override
        double area() {
            return Math.PI * radius * radius;
        }

        @Override
        String shapeName() {
            return "원";
        }
    }

    static class Rectangle extends Shape {
        double w, h;

        Rectangle(double w, double h) {
            this.w = w;
            this.h = h;
        }

        @Override
        double area() {
            return w * h;
        }

        @Override
        String shapeName() {
            return "사각형";
        }
    }

    public static void main(String[] args) {

        System.out.println("=== 7.8 다형성 ===");

        // [1] 부모 타입(Shape) 배열 하나로 여러 자식 객체를 담는다
        System.out.println("\n[1] 부모 타입 참조로 여러 자식을 하나로 다루기");
        Shape[] shapes = {
                new Circle(2.0),
                new Rectangle(3.0, 4.0),
                new Circle(1.0)
        };

        // [2] 동적 바인딩 : 같은 area() 호출이지만 실제 객체에 맞는 버전이 실행됨
        System.out.println("\n[2] 동적 바인딩 (실제 객체 타입의 메소드가 실행됨)");
        double total = 0;
        for (Shape s : shapes) {                     // 변수 타입은 모두 Shape
            double a = s.area();                     // 실제로는 Circle/Rectangle 의 area() 실행
            System.out.printf("%s 넓이 = %.2f%n", s.shapeName(), a);
            total += a;
        }
        System.out.printf("전체 넓이 합 = %.2f%n", total);

        System.out.println("\n프로그램 정상 종료");
    }
}
