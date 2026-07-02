package com.edu.collections;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;

/**
 * Chapter 03 - Set 예제
 *
 * Set은 "중복을 허용하지 않는" 컬렉션입니다.
 * 같은 값을 여러 번 추가해도 하나만 저장됩니다.
 *
 * 대표 구현체 3가지 (순서 특성이 다릅니다):
 * - HashSet       : 해시 테이블 기반. 가장 빠르지만 "순서를 보장하지 않음".
 * - LinkedHashSet : 해시 테이블 + 연결 리스트. "삽입한 순서"를 유지함.
 * - TreeSet       : 레드-블랙 트리 기반. 요소를 "자동으로 정렬"해서 보관함.
 *
 * 활용: 중복 제거, 존재 여부(포함) 확인, 그리고 집합 연산(합집합/교집합/차집합).
 */
public class SetExample {

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("  Chapter 03 - Set 예제");
        System.out.println("========================================\n");

        // ======================================================
        // 1. HashSet - 순서 보장 안 됨, 중복 제거
        // ======================================================
        System.out.println("--- 1. HashSet (순서 없음) ---");

        Set<String> hashSet = new HashSet<>();
        hashSet.add("바나나");
        hashSet.add("사과");
        hashSet.add("체리");
        hashSet.add("사과");   // 이미 존재하는 값이므로 무시됨 (중복 제거)
        System.out.println("  HashSet: " + hashSet);
        System.out.println("  '사과' 포함 여부 contains: " + hashSet.contains("사과"));
        System.out.println("  크기 size(): " + hashSet.size() + " (사과 중복 추가는 무시됨)");

        // ======================================================
        // 2. LinkedHashSet - 삽입 순서 유지
        // ======================================================
        System.out.println("\n--- 2. LinkedHashSet (삽입 순서 유지) ---");

        Set<String> linkedHashSet = new LinkedHashSet<>();
        linkedHashSet.add("바나나");
        linkedHashSet.add("사과");
        linkedHashSet.add("체리");
        linkedHashSet.add("귤");
        System.out.println("  LinkedHashSet: " + linkedHashSet + " (넣은 순서 그대로 유지)");

        // ======================================================
        // 3. TreeSet - 자동 정렬
        // ======================================================
        System.out.println("\n--- 3. TreeSet (자동 정렬) ---");

        Set<String> treeSet = new TreeSet<>();
        treeSet.add("바나나");
        treeSet.add("사과");
        treeSet.add("체리");
        treeSet.add("귤");
        System.out.println("  TreeSet: " + treeSet + " (사전 순으로 자동 정렬)");

        // ======================================================
        // 4. 집합 연산 (합집합 / 교집합 / 차집합)
        // ======================================================
        System.out.println("\n--- 4. 집합 연산 ---");

        Set<Integer> setA = new HashSet<>(Set.of(1, 2, 3, 4, 5));
        Set<Integer> setB = new HashSet<>(Set.of(3, 4, 5, 6, 7));
        System.out.println("  집합 A: " + setA);
        System.out.println("  집합 B: " + setB);

        // 합집합: A 또는 B에 있는 모든 요소 -> addAll
        Set<Integer> union = new HashSet<>(setA);
        union.addAll(setB);
        System.out.println("  합집합 (A ∪ B): " + union);

        // 교집합: A 와 B 둘 다에 있는 요소만 남김 -> retainAll
        Set<Integer> intersection = new HashSet<>(setA);
        intersection.retainAll(setB);
        System.out.println("  교집합 (A ∩ B): " + intersection);

        // 차집합: A 에서 B 에 있는 요소를 제거 -> removeAll
        Set<Integer> difference = new HashSet<>(setA);
        difference.removeAll(setB);
        System.out.println("  차집합 (A - B): " + difference);

        System.out.println("\n  [순서 특성 요약]");
        System.out.println("    HashSet       - 순서 없음 (가장 빠름)");
        System.out.println("    LinkedHashSet - 삽입 순서 유지");
        System.out.println("    TreeSet       - 정렬 순서 유지 (자동 정렬)");

        System.out.println("\n========================================");
        System.out.println("  Set 예제 완료!");
        System.out.println("========================================");
    }
}
