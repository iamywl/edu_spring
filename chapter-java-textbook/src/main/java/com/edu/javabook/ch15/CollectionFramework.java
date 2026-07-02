package com.edu.javabook.ch15;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 15.1 컬렉션 프레임워크
 *
 * 컬렉션 프레임워크(Collection Framework)는 여러 데이터를 담고 다루기 위한
 * "표준화된 자료구조 라이브러리"이다. (java.util 패키지)
 *
 * 왜 배열 대신 컬렉션인가?
 *  - 배열은 생성 시 크기가 고정된다. 원소를 추가/삭제하려면 직접 복사/이동해야 한다.
 *  - 컬렉션은 크기가 동적으로 늘어나며, add/remove/search 같은 편의 메소드를 제공한다.
 *  - 중복 제거(Set), 키-값 매핑(Map), 정렬/검색 등 용도별 구현체가 준비되어 있다.
 *
 * 큰 두 계층(인터페이스) :
 *  1) Collection : 원소들의 묶음
 *       - List : 순서 있음, 중복 허용 (ArrayList, LinkedList ...)
 *       - Set  : 중복 불허 (HashSet, LinkedHashSet, TreeSet ...)
 *       - Queue: 대기열 (ArrayDeque, PriorityQueue ...)
 *  2) Map : 키(Key) → 값(Value) 매핑, 키 중복 불허 (HashMap, TreeMap ...)
 *     * Map 은 Collection 을 상속하지 않는 별도 계층이다.
 *
 * 이 소절에서는 배열의 한계와 컬렉션의 편리함을 비교한다.
 */
public class CollectionFramework {

    public static void main(String[] args) {

        System.out.println("=== 15.1 컬렉션 프레임워크 ===");

        // [1] 배열의 한계 : 크기가 고정된다
        System.out.println("\n[1] 배열은 크기가 고정된다");
        int[] arr = new int[3];             // 크기 3으로 고정
        arr[0] = 10; arr[1] = 20; arr[2] = 30;
        System.out.println("배열 크기: " + arr.length + " (더 추가하려면 새 배열로 복사해야 함)");

        // [2] 컬렉션(List)은 크기가 동적으로 늘어난다
        System.out.println("\n[2] List 는 동적으로 커진다");
        List<Integer> list = new ArrayList<>();
        list.add(10);
        list.add(20);
        list.add(30);
        list.add(40);                       // 배열과 달리 자유롭게 추가
        System.out.println("현재 원소 개수: " + list.size() + ", 내용: " + list);

        // [3] Collection 계층 요약
        System.out.println("\n[3] Collection 계층 개요");
        System.out.println("List : 순서 O, 중복 O  → ArrayList, LinkedList");
        System.out.println("Set  : 순서 X(구현체별), 중복 X → HashSet, TreeSet");
        System.out.println("Queue: 대기열(FIFO/우선순위) → ArrayDeque, PriorityQueue");

        // [4] Map 계층 요약
        System.out.println("\n[4] Map 계층 개요 (Collection 과 별개)");
        Map<String, Integer> scores = new HashMap<>();
        scores.put("국어", 90);
        scores.put("수학", 85);
        System.out.println("키-값 매핑 저장: " + scores);
        System.out.println("키 '수학' 의 값: " + scores.get("수학"));

        System.out.println("\n→ 배열은 고정, 컬렉션은 동적 + 용도별 자료구조 제공");
        System.out.println("\n프로그램 정상 종료");
    }
}
