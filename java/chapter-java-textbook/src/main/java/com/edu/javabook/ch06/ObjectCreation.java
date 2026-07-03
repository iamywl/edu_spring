package com.edu.javabook.ch06;

/**
 * 6.4 객체 생성과 클래스 변수(참조 변수)
 *
 * 객체는 'new 연산자'로 생성한다.
 *   클래스이름 변수 = new 클래스이름();
 *
 * - new 는 힙(heap) 메모리에 객체를 만들고, 그 객체의 '주소(참조)'를 돌려준다.
 * - 왼쪽의 변수(참조 변수)는 값이 아니라 객체의 '주소'를 담는다.
 * - 참조 변수에 아무 객체도 없으면 null 이다. null 상태에서 멤버 접근 시 NPE 발생.
 */
public class ObjectCreation {

    static class Point {
        int x, y;
    }

    public static void main(String[] args) {

        System.out.println("=== 6.4 객체 생성과 클래스 변수 ===");

        // [1] new 로 객체 생성, 참조 변수에 대입
        System.out.println("\n[1] new 로 객체 생성");
        Point p1 = new Point();     // 힙에 Point 객체 생성 → 주소를 p1에 저장
        p1.x = 10;
        p1.y = 20;
        System.out.println("p1 = (" + p1.x + ", " + p1.y + ")");

        // [2] 참조 변수는 '주소'를 담는다 → 대입하면 같은 객체를 가리킨다
        System.out.println("\n[2] 참조 변수는 주소를 담는다");
        Point p2 = p1;             // 객체 복사가 아니라 '주소' 복사
        p2.x = 99;                 // p2로 바꿔도 p1이 보는 객체가 같아 함께 바뀜
        System.out.println("p2.x=99 후 p1.x = " + p1.x + " (같은 객체를 가리킴)");
        System.out.println("(p1 == p2) → " + (p1 == p2));

        // [3] new 를 두 번 하면 서로 다른 객체
        System.out.println("\n[3] new 는 매번 새 객체");
        Point p3 = new Point();
        System.out.println("(p1 == p3) → " + (p1 == p3) + " (다른 객체)");

        // [4] null 참조: 아직 객체를 가리키지 않는 상태
        System.out.println("\n[4] null 참조");
        Point p4 = null;
        System.out.println("p4 == null → " + (p4 == null));
        System.out.println("→ null 상태에서 p4.x 접근 시 NullPointerException 발생");

        // [왜?] 자바에서 객체는 항상 참조를 통해 다뤄진다(값이 아니라 주소).
        System.out.println("\n[왜?] 참조 변수의 대입은 객체 복사가 아니라 '주소' 복사다.");

        System.out.println("\n프로그램 정상 종료");
    }
}
