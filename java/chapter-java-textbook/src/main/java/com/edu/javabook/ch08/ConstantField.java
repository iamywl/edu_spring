package com.edu.javabook.ch08;

/**
 * 8.3 상수 필드
 *
 * 인터페이스에 선언한 필드는 모두 "public static final" 로 취급된다.
 * 즉, 이 세 가지 제한자를 생략해도 자동으로 붙는다(암묵적).
 *
 * - public  : 어디서든 접근 가능
 * - static  : 인터페이스이름.상수 로 접근 (객체 없이 사용)
 * - final   : 값을 바꿀 수 없는 상수 → 반드시 선언과 동시에 초기화
 */
public class ConstantField {

    interface Calculator {
        // 아래 두 줄은 사실상 public static final 이다.
        double PI = 3.14159;              // 암묵적으로 public static final
        int    MAX_INPUT = 1_000_000;     // 마찬가지

        double area(double radius);       // 상수를 사용하는 추상 메소드
    }

    static class CircleCalculator implements Calculator {
        @Override
        public double area(double radius) {
            // 인터페이스 상수는 구현 클래스 안에서 이름만으로 참조 가능
            return PI * radius * radius;
        }
    }

    public static void main(String[] args) {
        System.out.println("=== 8.3 상수 필드 ===");

        // [1] 객체 없이 인터페이스이름.상수 로 접근 (static)
        System.out.println("\n[1] 인터페이스 상수 직접 접근");
        System.out.println("Calculator.PI        = " + Calculator.PI);
        System.out.println("Calculator.MAX_INPUT = " + Calculator.MAX_INPUT);

        // [2] 상수를 사용하는 구현 메소드 호출
        System.out.println("\n[2] 상수를 사용한 계산");
        Calculator calc = new CircleCalculator();
        System.out.println("반지름 2인 원의 넓이 = " + calc.area(2));

        // [3] final 이므로 값 변경 불가 (아래는 주석 처리: 컴파일 오류가 남)
        System.out.println("\n[3] final 이라 재할당 불가");
        // Calculator.PI = 3.14;   // 컴파일 오류: cannot assign a value to final variable
        System.out.println("Calculator.PI = 3.14; 는 컴파일 오류가 발생한다.");

        System.out.println("\n프로그램 정상 종료");
    }
}
