package com.edu.javabook.ch06;

/**
 * 6.7 생성자 선언과 호출
 *
 * 생성자(constructor)는 new 로 객체를 만들 때 호출되어 필드를 초기화하는 특별한 멤버다.
 *  - 이름이 클래스 이름과 같고, 리턴 타입이 없다(void도 아님).
 *  - 생성자를 하나도 선언하지 않으면, 컴파일러가 '기본 생성자'를 자동으로 넣어준다.
 *  - 매개변수를 달리하여 여러 개 만들 수 있다(생성자 오버로딩).
 *  - this() : 같은 클래스의 다른 생성자를 호출(생성자 첫 줄에서만).
 *  - this   : 현재 객체 자신의 참조. 필드와 매개변수 이름이 겹칠 때 구분에 쓴다.
 */
public class ConstructorDeclaration {

    static class Book {
        String title;
        int price;

        // [기본 생성자] 매개변수 없는 생성자
        Book() {
            this("제목없음", 0);   // this() 로 다른 생성자를 재사용
            System.out.println("  (기본 생성자 호출)");
        }

        // [오버로딩] 제목만 받는 생성자
        Book(String title) {
            this(title, 10000);    // this() 로 위임
            System.out.println("  (제목 생성자 호출)");
        }

        // [오버로딩] 제목+가격을 받는 생성자
        Book(String title, int price) {
            this.title = title;    // this.title(필드) = title(매개변수)
            this.price = price;
            System.out.println("  (제목+가격 생성자 호출)");
        }

        String info() {
            return title + " / " + price + "원";
        }
    }

    public static void main(String[] args) {

        System.out.println("=== 6.7 생성자 선언과 호출 ===");

        // [1] 기본 생성자 호출 (this()로 다른 생성자 연쇄 호출됨)
        System.out.println("\n[1] 기본 생성자");
        Book b1 = new Book();
        System.out.println("b1 → " + b1.info());

        // [2] 오버로딩된 생성자 호출 (제목만)
        System.out.println("\n[2] 오버로딩(제목만)");
        Book b2 = new Book("자바의 정석");
        System.out.println("b2 → " + b2.info());

        // [3] 오버로딩된 생성자 호출 (제목+가격)
        System.out.println("\n[3] 오버로딩(제목+가격)");
        Book b3 = new Book("이펙티브 자바", 36000);
        System.out.println("b3 → " + b3.info());

        // [4] this 키워드: 필드와 매개변수 이름 충돌 해결
        System.out.println("\n[4] this 키워드");
        System.out.println("this.title = title 에서 앞은 필드, 뒤는 매개변수를 가리킴");

        // [왜?] 생성자는 '완전히 초기화된 객체'만 세상에 나오도록 보장한다.
        System.out.println("\n[왜?] 생성자로 객체가 유효한 초기 상태를 갖도록 강제할 수 있다.");

        System.out.println("\n프로그램 정상 종료");
    }
}
