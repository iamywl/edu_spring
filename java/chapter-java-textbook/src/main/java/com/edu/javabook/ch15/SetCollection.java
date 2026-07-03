package com.edu.javabook.ch15;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;

/**
 * 15.3 Set 컬렉션
 *
 * Set 은 "중복을 허용하지 않는" 컬렉션이다. 같은 값을 여러 번 add 해도 하나만 저장된다.
 * (인덱스 개념이 없다 → get(i) 같은 위치 접근은 제공하지 않는다.)
 *
 * 대표 구현체와 순서 :
 *  - HashSet       : 순서를 보장하지 않는다(해시 기반, 가장 빠름).
 *  - LinkedHashSet : "입력한 순서(삽입 순서)"를 유지한다.
 *  - TreeSet       : "정렬된 순서(오름차순)"를 유지한다(이진 트리 기반).
 *
 * 이 소절에서는 중복 제거와 구현체별 순서 차이를 비교한다.
 */
public class SetCollection {

    public static void main(String[] args) {

        System.out.println("=== 15.3 Set 컬렉션 ===");

        // [1] 중복 자동 제거
        System.out.println("\n[1] Set 은 중복을 자동 제거한다");
        Set<String> set = new HashSet<>();
        set.add("사과");
        set.add("바나나");
        set.add("사과");                    // 중복 → 무시됨
        set.add("포도");
        System.out.println("add 를 4번 했지만 크기: " + set.size());
        System.out.println("포함 여부 contains('사과'): " + set.contains("사과"));

        // [2] HashSet : 순서 보장 안 함
        System.out.println("\n[2] HashSet - 순서 보장 X");
        Set<Integer> hash = new HashSet<>();
        for (int n : new int[]{50, 10, 40, 20, 30}) hash.add(n);
        System.out.println("입력: 50 10 40 20 30 → 출력: " + hash + " (순서 무의미)");

        // [3] LinkedHashSet : 삽입 순서 유지
        System.out.println("\n[3] LinkedHashSet - 삽입 순서 유지");
        Set<Integer> linked = new LinkedHashSet<>();
        for (int n : new int[]{50, 10, 40, 20, 30}) linked.add(n);
        System.out.println("입력: 50 10 40 20 30 → 출력: " + linked + " (넣은 순서 그대로)");

        // [4] TreeSet : 정렬 순서 유지
        System.out.println("\n[4] TreeSet - 오름차순 정렬 유지");
        Set<Integer> tree = new TreeSet<>();
        for (int n : new int[]{50, 10, 40, 20, 30}) tree.add(n);
        System.out.println("입력: 50 10 40 20 30 → 출력: " + tree + " (자동 정렬)");

        // [5] 활용 : 리스트의 중복 제거
        System.out.println("\n[5] 활용 - 중복 제거하기");
        String[] words = {"a", "b", "a", "c", "b", "a"};
        Set<String> unique = new LinkedHashSet<>();
        for (String w : words) unique.add(w);
        System.out.println("원본 6개 → 유니크: " + unique);

        System.out.println("\n프로그램 정상 종료");
    }
}
