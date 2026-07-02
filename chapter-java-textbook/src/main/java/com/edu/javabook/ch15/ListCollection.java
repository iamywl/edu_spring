package com.edu.javabook.ch15;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * 15.2 List 컬렉션
 *
 * List 는 "순서가 있고 중복을 허용"하는 컬렉션이다. 인덱스(0부터)로 접근한다.
 *
 * 대표 구현체 :
 *  - ArrayList  : 내부적으로 배열을 사용. 인덱스 접근(get)이 빠르다(O(1)).
 *                 중간 삽입/삭제는 뒤 원소들을 이동해야 해서 느릴 수 있다(O(n)).
 *  - LinkedList : 노드들이 앞/뒤로 연결된 구조. 앞/뒤 추가·삭제가 빠르다.
 *                 인덱스 접근은 처음부터 따라가야 해서 느리다(O(n)).
 *
 * 성능 특성 요약 :
 *  - 임의 인덱스 조회가 잦다  → ArrayList
 *  - 앞/뒤 삽입·삭제가 잦다   → LinkedList
 *
 * 이 소절에서는 add/get/remove/순회와 두 구현체의 특성을 살펴본다.
 */
public class ListCollection {

    public static void main(String[] args) {

        System.out.println("=== 15.2 List 컬렉션 ===");

        // [1] ArrayList 기본 : add / get / size
        System.out.println("\n[1] ArrayList - add / get");
        List<String> fruits = new ArrayList<>();
        fruits.add("사과");                  // 맨 뒤에 추가
        fruits.add("바나나");
        fruits.add("포도");
        fruits.add("사과");                  // List 는 중복 허용
        System.out.println("전체: " + fruits + " (크기 " + fruits.size() + ")");
        System.out.println("index 0: " + fruits.get(0));
        System.out.println("index 2: " + fruits.get(2));

        // [2] 특정 위치 삽입 / 수정 / 삭제
        System.out.println("\n[2] 삽입(add index) / 수정(set) / 삭제(remove)");
        fruits.add(1, "딸기");               // 인덱스 1에 끼워넣기
        System.out.println("1번에 딸기 삽입: " + fruits);
        fruits.set(0, "청사과");             // 0번을 교체
        System.out.println("0번 수정: " + fruits);
        fruits.remove("포도");               // 값으로 삭제
        System.out.println("'포도' 삭제: " + fruits);
        fruits.remove(0);                    // 인덱스로 삭제
        System.out.println("0번 삭제: " + fruits);

        // [3] 순회 방법 3가지
        System.out.println("\n[3] 순회 (for-index / for-each / Iterator)");
        System.out.print("for-index : ");
        for (int i = 0; i < fruits.size(); i++) System.out.print(fruits.get(i) + " ");
        System.out.println();

        System.out.print("for-each  : ");
        for (String f : fruits) System.out.print(f + " ");
        System.out.println();

        System.out.print("Iterator  : ");
        Iterator<String> it = fruits.iterator();
        while (it.hasNext()) System.out.print(it.next() + " ");
        System.out.println();

        // [4] ArrayList vs LinkedList
        System.out.println("\n[4] ArrayList vs LinkedList 성능 특성");
        List<Integer> arrayList = new ArrayList<>();
        List<Integer> linkedList = new LinkedList<>();
        for (int i = 0; i < 5; i++) { arrayList.add(i); linkedList.add(i); }
        System.out.println("ArrayList : 인덱스 조회 get(i) 가 빠름(O(1)) → " + arrayList);
        System.out.println("LinkedList: 앞/뒤 삽입·삭제가 빠름(O(1)) → " + linkedList);
        ((LinkedList<Integer>) linkedList).addFirst(-1);  // 맨 앞 추가 (LinkedList 강점)
        System.out.println("LinkedList addFirst(-1): " + linkedList);

        System.out.println("\n프로그램 정상 종료");
    }
}
