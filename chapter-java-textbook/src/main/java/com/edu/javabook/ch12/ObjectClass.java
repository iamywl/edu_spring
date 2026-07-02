package com.edu.javabook.ch12;

import java.util.Objects;

/**
 * 12.3 Object 클래스
 *
 * Object 는 모든 클래스의 최상위 부모다. 우리가 만든 모든 클래스는
 * (명시하지 않아도) 자동으로 Object 를 상속한다. 따라서 Object 가 제공하는
 * 메서드는 어떤 객체에서든 사용할 수 있다.
 *
 * 자주 재정의(override)하는 대표 메서드 :
 *   - getClass()  : 객체의 실제 클래스 정보(Class 객체)를 반환. (재정의 대상 아님)
 *   - equals(o)   : 두 객체가 "논리적으로 같은가"를 정의. 기본은 == (주소 비교)
 *   - hashCode()  : 객체를 대표하는 정수. equals 를 재정의하면 함께 재정의해야 한다.
 *   - toString()  : 객체를 문자열로 표현. 기본은 "클래스명@16진수해시".
 *
 * equals/hashCode 규칙 :
 *   - equals 가 true 인 두 객체는 hashCode 도 반드시 같아야 한다.
 *   - 그래야 HashMap, HashSet 등에서 올바르게 동작한다.
 *
 * 이 소절에서는 Student 클래스에서 위 메서드들을 재정의하여
 * "같은 학번이면 같은 학생"으로 취급되도록 만든다.
 */
public class ObjectClass {

    // 학번(id)이 같으면 같은 학생으로 보고 싶은 클래스
    static class Student {
        private final int id;
        private final String name;

        Student(int id, String name) {
            this.id = id;
            this.name = name;
        }

        // equals 재정의 : 학번(id)이 같으면 동일 객체로 취급
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;                 // 주소가 같으면 당연히 같음
            if (o == null || getClass() != o.getClass()) return false; // 타입 확인
            Student other = (Student) o;
            return id == other.id;                      // 핵심 : id 만 비교
        }

        // hashCode 재정의 : equals 에서 쓴 id 를 기준으로 해시 생성
        @Override
        public int hashCode() {
            return Objects.hash(id);
        }

        // toString 재정의 : 사람이 읽기 좋은 형태로 표현
        @Override
        public String toString() {
            return "Student{id=" + id + ", name='" + name + "'}";
        }
    }

    public static void main(String[] args) {

        System.out.println("=== 12.3 Object 클래스 ===");

        Student s1 = new Student(1, "홍길동");
        Student s2 = new Student(1, "홍길순");  // 이름은 다르지만 학번은 같음
        Student s3 = new Student(2, "김철수");

        // [1] getClass() : 객체의 실제 클래스 정보
        System.out.println("\n[1] getClass()");
        System.out.println("  s1.getClass()          = " + s1.getClass());
        System.out.println("  s1.getClass().getName()= " + s1.getClass().getName());
        System.out.println("  s1.getClass().getSimpleName()= " + s1.getClass().getSimpleName());

        // [2] toString() : 재정의 결과 (기본 형식 대신 읽기 좋은 형태)
        System.out.println("\n[2] toString()");
        System.out.println("  s1 = " + s1);   // println 이 자동으로 toString() 호출
        System.out.println("  s3 = " + s3);

        // [3] equals() : 학번이 같으면 true
        System.out.println("\n[3] equals()");
        System.out.println("  s1.equals(s2) = " + s1.equals(s2) + "  (학번 같음 → true)");
        System.out.println("  s1.equals(s3) = " + s1.equals(s3) + " (학번 다름 → false)");
        System.out.println("  s1 == s2      = " + (s1 == s2) + " (주소는 다름)");

        // [4] hashCode() : equals 가 true 인 s1, s2 는 hashCode 도 같아야 함
        System.out.println("\n[4] hashCode()");
        System.out.println("  s1.hashCode() = " + s1.hashCode());
        System.out.println("  s2.hashCode() = " + s2.hashCode() + "  (s1 과 같음)");
        System.out.println("  s3.hashCode() = " + s3.hashCode());

        System.out.println("\n프로그램 정상 종료");
    }
}
