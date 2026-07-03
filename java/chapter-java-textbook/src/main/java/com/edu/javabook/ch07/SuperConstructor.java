package com.edu.javabook.ch07;

/**
 * 7.3 부모 생성자 호출
 *
 * 자식 객체를 만들 때는 "부모 부분"이 먼저 초기화되어야 한다.
 * 그래서 자식 생성자의 첫 줄에서는 반드시 부모 생성자가 호출된다.
 *
 * - super(...) : 부모 생성자를 명시적으로 호출한다. 반드시 자식 생성자의 "첫 문장"이어야 한다.
 * - super(...) 를 생략하면, 컴파일러가 자동으로 super() (매개변수 없는 부모 생성자)를 넣어준다.
 *   → 따라서 부모에 기본 생성자가 없고 매개변수 생성자만 있으면, 자식은 super(...) 를 직접 써야 한다.
 *
 * 호출 순서 : 부모 생성자가 먼저 끝난 뒤 자식 생성자 본문이 실행된다(위 → 아래).
 *
 * 이 소절에서는 생성자 실행 순서를 출력으로 눈으로 확인한다.
 */
public class SuperConstructor {

    static class Person {
        String name;

        Person(String name) {
            this.name = name;
            System.out.println("  1) Person(부모) 생성자 실행 : name = " + name);
        }
    }

    static class Student extends Person {
        int studentId;

        Student(String name, int studentId) {
            super(name);   // (필수) 부모 생성자 먼저 호출. 첫 문장이어야 한다.
            this.studentId = studentId;
            System.out.println("  2) Student(자식) 생성자 실행 : studentId = " + studentId);
        }
    }

    public static void main(String[] args) {

        System.out.println("=== 7.3 부모 생성자 호출 ===");

        // [1] super(...) 로 부모 생성자를 호출하는 순서 확인
        System.out.println("\n[1] new Student(...) 실행 시 생성자 호출 순서");
        System.out.println("객체 생성 시작 →");
        Student s = new Student("홍길동", 20240101);
        System.out.println("객체 생성 완료 → name=" + s.name + ", studentId=" + s.studentId);

        // [2] 핵심 정리
        System.out.println("\n[2] 정리");
        System.out.println("- 부모(Person) 생성자가 먼저 끝난 뒤 자식(Student) 본문이 실행된다.");
        System.out.println("- super(...) 는 자식 생성자의 첫 문장이어야 한다.");
        System.out.println("- 부모에 매개변수 생성자만 있으면 super(...) 호출을 생략할 수 없다.");

        System.out.println("\n프로그램 정상 종료");
    }
}
