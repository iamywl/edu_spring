package com.edu.javabook.ch05;

import java.util.Arrays;

/**
 * 5.8 객체를 참조하는 배열
 *
 * 참조 타입(String, 클래스 등) 배열의 각 요소에는 "값"이 아니라
 * "객체의 번지(참조)"가 저장된다.
 *
 *   - new String[3] 처럼 만들면 요소는 모두 null (아직 아무 객체도 안 가리킴).
 *   - 요소에 객체를 대입하면 그 요소가 해당 객체를 가리킨다.
 *   - 두 요소가 같은 객체를 가리키면, 하나를 통해 바꾼 게 다른 쪽에도 보인다.
 */
public class ObjectReferenceArray {

    // 참조를 확인하기 위한 간단한 클래스
    static class Book {
        String title;
        Book(String title) { this.title = title; }
        @Override public String toString() { return "Book(" + title + ")"; }
    }

    public static void main(String[] args) {

        System.out.println("=== 5.8 객체를 참조하는 배열 ===");

        // [1] 참조형 배열 생성 → 요소는 모두 null
        System.out.println("\n[1] 참조형 배열의 초기 요소는 null");
        Book[] books = new Book[3];
        System.out.println("생성 직후 : " + Arrays.toString(books));

        // [2] 각 요소에 객체를 대입 (요소가 객체 번지를 가리킴)
        System.out.println("\n[2] 각 요소에 객체 대입");
        books[0] = new Book("자바");
        books[1] = new Book("스프링");
        books[2] = new Book("JPA");
        System.out.println("대입 후 : " + Arrays.toString(books));

        // [3] 요소 접근 = 그 요소가 가리키는 객체 접근
        System.out.println("\n[3] 요소를 통해 객체 필드 접근");
        System.out.println("books[1].title = " + books[1].title);

        // [4] 두 요소가 같은 객체를 가리키면 함께 바뀐다.
        System.out.println("\n[4] 같은 객체를 가리키는 두 요소");
        Book[] shelf = new Book[2];
        Book shared = new Book("공유책");
        shelf[0] = shared;   // 같은 객체
        shelf[1] = shared;   // 같은 객체
        shelf[0].title = "제목변경";   // shelf[0]으로 바꿨지만
        System.out.println("shelf[1].title = " + shelf[1].title
                + "  (같은 객체라 함께 변경됨)");

        System.out.println("\n프로그램 정상 종료");
    }
}
