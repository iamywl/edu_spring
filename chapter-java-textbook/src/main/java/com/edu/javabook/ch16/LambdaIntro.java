package com.edu.javabook.ch16;

import java.util.function.Supplier;

/**
 * 16.1 람다식이란?
 *
 * 람다식(Lambda Expression)은 "메소드(함수) 하나를 식(expression)처럼 간결하게
 * 표현"하는 방법이다. (JDK 8부터 도입)
 *
 * 왜 필요한가?
 *  - 자바에서 "동작(코드 조각)"을 넘기려면 예전에는 익명 클래스를 써야 했다.
 *  - 익명 클래스는 실제로 중요한 코드(한 줄)에 비해 껍데기(new, 클래스명, 메소드
 *    선언, 중괄호)가 너무 길어 가독성이 떨어졌다.
 *  - 람다식은 이 껍데기를 걷어내고 "매개변수 -> 실행할 코드" 형태로 축약한다.
 *
 * 함수형 인터페이스(Functional Interface) :
 *  - "추상 메소드가 딱 1개"인 인터페이스.
 *  - 람다식은 바로 이 함수형 인터페이스의 유일한 추상 메소드 구현을 대신한다.
 *  - @FunctionalInterface 어노테이션을 붙이면 컴파일러가 규칙(추상 메소드 1개)을
 *    검사해 준다(필수는 아니지만 권장).
 *
 * 이 소절에서는 익명 클래스 -> 람다식으로 축약되는 과정을 비교한다.
 */
public class LambdaIntro {

    /** 인사말을 만들어 주는 함수형 인터페이스(추상 메소드 1개) */
    @FunctionalInterface
    interface Greeting {
        String say(String name);            // 유일한 추상 메소드
    }

    public static void main(String[] args) {

        System.out.println("=== 16.1 람다식이란? ===");

        // [1] 익명 클래스 방식 : 껍데기가 길다
        System.out.println("\n[1] 익명 클래스로 구현");
        Greeting g1 = new Greeting() {
            @Override
            public String say(String name) {
                return "안녕하세요, " + name + "님!";
            }
        };
        System.out.println(g1.say("홍길동"));

        // [2] 람다식 방식 : 핵심 코드만 남긴다
        System.out.println("\n[2] 람다식으로 축약");
        // (매개변수) -> { 실행문 }  의 형태
        Greeting g2 = (String name) -> {
            return "반갑습니다, " + name + "님!";
        };
        System.out.println(g2.say("김철수"));

        // [3] 람다식 축약 규칙
        System.out.println("\n[3] 람다식 더 줄이기");
        // 타입 생략 + 실행문이 하나면 중괄호/return 생략 가능
        Greeting g3 = name -> "환영합니다, " + name + "님!";
        System.out.println(g3.say("이영희"));

        // [4] 축약 과정 비교 정리
        System.out.println("\n[4] 축약 과정 한눈에 보기");
        System.out.println("익명클래스 : new Greeting(){ public String say(String n){ return ...; } }");
        System.out.println("람다(기본) : (String name) -> { return ...; }");
        System.out.println("람다(축약) : name -> ...");

        // [5] 표준 함수형 인터페이스 Supplier 로도 같은 개념 확인
        System.out.println("\n[5] 표준 함수형 인터페이스(Supplier) 예");
        // Supplier<T> : 매개변수 없이 T 값을 "공급"한다
        Supplier<String> today = () -> "오늘도 좋은 하루!";
        System.out.println(today.get());

        System.out.println("\n프로그램 정상 종료");
    }
}
