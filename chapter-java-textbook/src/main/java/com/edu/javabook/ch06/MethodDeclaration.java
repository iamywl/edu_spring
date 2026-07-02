package com.edu.javabook.ch06;

import java.util.Arrays;

/**
 * 6.8 메소드 선언과 호출
 *
 * 메서드는 객체의 기능(행위)을 정의한 코드 블록이다.
 *   [접근제한자] 리턴타입 메서드이름(매개변수목록) { 실행문; return 값; }
 *
 * - 매개변수(parameter): 메서드에 전달되는 입력값.
 * - 리턴값(return): 메서드가 돌려주는 결과. 리턴 타입이 void면 반환값이 없다.
 * - 오버로딩(overloading): 이름이 같고 매개변수(개수/타입)가 다른 메서드를 여러 개.
 * - 가변인자(varargs): 타입... 이름 형태로, 개수가 정해지지 않은 인자를 배열처럼 받는다.
 */
public class MethodDeclaration {

    static class Calculator {

        // [리턴값 O] 두 정수의 합
        int add(int a, int b) {
            return a + b;
        }

        // [오버로딩] 매개변수 타입이 다른 동일 이름 메서드
        double add(double a, double b) {
            return a + b;
        }

        // [오버로딩] 매개변수 개수가 다른 동일 이름 메서드
        int add(int a, int b, int c) {
            return a + b + c;
        }

        // [가변인자] 인자를 몇 개든 받아서 합산 (내부적으로 배열)
        int sum(int... nums) {
            int total = 0;
            for (int n : nums) total += n;
            return total;
        }

        // [리턴값 X] void: 결과를 돌려주지 않고 출력만
        void printLine(String msg) {
            System.out.println("  >> " + msg);
        }
    }

    public static void main(String[] args) {

        System.out.println("=== 6.8 메소드 선언과 호출 ===");
        Calculator calc = new Calculator();

        // [1] 매개변수와 리턴값
        System.out.println("\n[1] 매개변수와 리턴값");
        int r1 = calc.add(3, 4);
        System.out.println("add(3, 4) → " + r1);

        // [2] 메서드 오버로딩: 타입/개수에 따라 알맞은 메서드가 선택됨
        System.out.println("\n[2] 오버로딩");
        System.out.println("add(1.5, 2.5)   → " + calc.add(1.5, 2.5) + " (double 버전)");
        System.out.println("add(1, 2, 3)    → " + calc.add(1, 2, 3) + " (3개 버전)");

        // [3] 가변인자: 인자 개수가 유연함
        System.out.println("\n[3] 가변인자(varargs)");
        System.out.println("sum()          → " + calc.sum());
        System.out.println("sum(10)        → " + calc.sum(10));
        System.out.println("sum(1,2,3,4,5) → " + calc.sum(1, 2, 3, 4, 5));
        System.out.println("가변인자는 내부적으로 배열: " + Arrays.toString(new int[]{1, 2, 3}));

        // [4] void 메서드 호출
        System.out.println("\n[4] void 메서드");
        calc.printLine("반환값 없는 메서드");

        // [왜?] 메서드로 로직을 이름 붙여 재사용하고, 오버로딩/가변인자로 유연성을 얻는다.
        System.out.println("\n[왜?] 메서드는 재사용 가능한 기능 단위이며, 이름 하나로 다양한 형태를 제공한다.");

        System.out.println("\n프로그램 정상 종료");
    }
}
