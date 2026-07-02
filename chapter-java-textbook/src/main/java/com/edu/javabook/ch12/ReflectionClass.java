package com.edu.javabook.ch12;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 12.11 리플렉션 (Reflection)
 *
 * 리플렉션은 실행 중(runtime)에 클래스의 구조(필드/메서드/생성자)를 "들여다보고",
 * 심지어 객체를 동적으로 생성하거나 메서드를 호출하는 기능이다.
 * (컴파일 시점이 아니라 실행 시점에 클래스 정보를 다룬다.)
 *
 * 활용 예 : 프레임워크(Spring 등)의 의존성 주입, JSON 라이브러리, 테스트 도구 등.
 *
 * Class 객체 얻는 3가지 방법 :
 *   1) 클래스.class            (Person.class)
 *   2) 객체.getClass()          (person.getClass())
 *   3) Class.forName("완전이름") (동적 로딩)
 *
 * 주요 조회/조작 :
 *   - getDeclaredFields()  : 선언된 필드 목록
 *   - getDeclaredMethods() : 선언된 메서드 목록
 *   - getConstructor(...)  : 생성자 얻기 → newInstance 로 동적 생성
 *   - method.invoke(obj..) : 메서드 동적 호출
 *
 * 이 소절에서는 Person 클래스를 리플렉션으로 조회하고,
 * 동적으로 객체를 만들어 메서드를 호출한다.
 */
public class ReflectionClass {

    // 리플렉션 대상 클래스
    public static class Person {
        private String name;
        private int age;

        public Person() { this("이름없음", 0); }
        public Person(String name, int age) {
            this.name = name;
            this.age = age;
        }

        public String greet() {
            return name + "(" + age + "세) 님, 안녕하세요!";
        }
    }

    public static void main(String[] args) throws Exception {

        System.out.println("=== 12.11 리플렉션 ===");

        // [1] Class 객체 얻는 3가지 방법
        System.out.println("\n[1] Class 객체 얻기 (3가지)");
        Class<?> c1 = Person.class;                // (1) .class
        Person p = new Person("홍길동", 30);
        Class<?> c2 = p.getClass();                // (2) getClass()
        Class<?> c3 = Class.forName(
                "com.edu.javabook.ch12.ReflectionClass$Person"); // (3) forName
        System.out.println("  (1) Person.class        = " + c1.getSimpleName());
        System.out.println("  (2) p.getClass()        = " + c2.getSimpleName());
        System.out.println("  (3) Class.forName(...)  = " + c3.getSimpleName());
        System.out.println("  세 방법 모두 동일한 Class ? " + (c1 == c2 && c2 == c3));

        // [2] 필드 조회
        System.out.println("\n[2] 필드 조회 (getDeclaredFields)");
        for (Field f : c1.getDeclaredFields()) {
            System.out.println("  필드 : " + f.getType().getSimpleName() + " " + f.getName());
        }

        // [3] 메서드 조회
        System.out.println("\n[3] 메서드 조회 (getDeclaredMethods)");
        for (Method mth : c1.getDeclaredMethods()) {
            System.out.println("  메서드 : " + mth.getReturnType().getSimpleName()
                    + " " + mth.getName() + "()");
        }

        // [4] 동적 생성 + 동적 호출
        System.out.println("\n[4] 동적 객체 생성 + 메서드 호출");
        Constructor<?> cons = c1.getConstructor(String.class, int.class);
        Object obj = cons.newInstance("김철수", 25);   // 동적 생성
        Method greet = c1.getMethod("greet");
        Object result = greet.invoke(obj);             // 동적 호출
        System.out.println("  생성한 객체의 greet() = " + result);

        System.out.println("\n프로그램 정상 종료");
    }
}
