package com.edu.javabook.ch06;

/**
 * 6.3 클래스 선언
 *
 * 클래스는 'class 키워드 + 클래스이름 + { 멤버들 }' 형태로 선언한다.
 *   [접근제한자] class 클래스이름 { ... }
 *
 * 규칙과 관례:
 *  - 클래스 이름은 대문자로 시작하는 파스칼 표기법(PascalCase)을 쓴다.
 *  - 하나의 .java 파일에는 public 클래스가 최대 1개이며, 파일명과 같아야 한다.
 *  - 여러 클래스를 한 파일에 선언할 수 있으나, public이 아닌 것만 여러 개 가능하다.
 */
public class ClassDeclaration {

    // [선언 예1] 가장 단순한 클래스 (멤버가 없어도 클래스는 성립한다)
    static class Empty {
    }

    // [선언 예2] 필드와 메서드를 가진 클래스
    static class Student {
        String name;
        int grade;

        String intro() {
            return name + "(" + grade + "학년)";
        }
    }

    public static void main(String[] args) {

        System.out.println("=== 6.3 클래스 선언 ===");

        // [1] 클래스 선언의 기본 형식 설명
        System.out.println("\n[1] 선언 형식");
        System.out.println("[접근제한자] class 클래스이름 { 필드; 생성자; 메서드; }");
        System.out.println("- 이름은 PascalCase (예: ClassDeclaration, Student)");

        // [2] 빈 클래스도 객체로 만들 수 있다
        System.out.println("\n[2] 빈 클래스");
        Empty e = new Empty();
        System.out.println("new Empty() → " + e.getClass().getSimpleName() + " 객체 생성됨");

        // [3] 멤버를 가진 클래스 사용
        System.out.println("\n[3] 멤버를 가진 클래스");
        Student s = new Student();
        s.name = "홍길동";
        s.grade = 2;
        System.out.println("s.intro() → " + s.intro());

        // [4] 클래스 이름은 곧 '타입'이 된다
        System.out.println("\n[4] 클래스는 타입");
        System.out.println("변수 s의 타입 이름 → " + s.getClass().getSimpleName());

        // [왜?] 클래스 선언은 새로운 데이터 타입을 프로그램에 정의하는 일이다.
        System.out.println("\n[왜?] 클래스를 선언하면 원하는 형태의 사용자 정의 타입이 만들어진다.");

        System.out.println("\n프로그램 정상 종료");
    }
}
