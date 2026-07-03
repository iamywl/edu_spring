package com.edu.javabook.ch15;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * 15.4 Map 컬렉션
 *
 * Map 은 "키(Key) → 값(Value)" 쌍으로 저장하는 컬렉션이다.
 *  - 키는 중복될 수 없다(같은 키에 put 하면 값이 덮어써진다).
 *  - 값은 중복될 수 있다.
 *  - 인덱스가 아니라 "키"로 값을 꺼낸다.
 *
 * 대표 구현체와 순서 :
 *  - HashMap       : 순서 보장 X (가장 빠름).
 *  - LinkedHashMap : 삽입 순서 유지.
 *  - TreeMap       : 키를 기준으로 정렬(오름차순) 유지.
 *
 * 이 소절에서는 put/get/getOrDefault 와 순회 방법을 살펴본다.
 */
public class MapCollection {

    public static void main(String[] args) {

        System.out.println("=== 15.4 Map 컬렉션 ===");

        // [1] put / get : 키로 저장하고 키로 꺼낸다
        System.out.println("\n[1] put / get");
        Map<String, Integer> scores = new HashMap<>();
        scores.put("국어", 90);
        scores.put("수학", 85);
        scores.put("영어", 95);
        System.out.println("전체: " + scores);
        System.out.println("get('수학'): " + scores.get("수학"));

        // [2] 키 중복 : 덮어쓰기
        System.out.println("\n[2] 같은 키에 put → 값 덮어쓰기");
        scores.put("수학", 100);            // 기존 85 를 100 으로 교체
        System.out.println("수학 다시 put(100): " + scores.get("수학"));

        // [3] getOrDefault : 없는 키에 대한 기본값
        System.out.println("\n[3] getOrDefault - 없는 키의 기본값");
        System.out.println("과학 점수(없음): " + scores.get("과학"));          // null
        System.out.println("getOrDefault('과학', 0): " + scores.getOrDefault("과학", 0));

        // [4] 존재 확인 / 삭제
        System.out.println("\n[4] containsKey / remove");
        System.out.println("containsKey('영어'): " + scores.containsKey("영어"));
        scores.remove("영어");
        System.out.println("영어 삭제 후: " + scores);

        // [5] 순회 (keySet / values / entrySet)
        System.out.println("\n[5] 순회");
        System.out.print("keySet  : ");
        for (String key : scores.keySet()) System.out.print(key + " ");
        System.out.println();

        System.out.print("values  : ");
        for (Integer v : scores.values()) System.out.print(v + " ");
        System.out.println();

        System.out.println("entrySet:");
        for (Map.Entry<String, Integer> e : scores.entrySet()) {
            System.out.println("  " + e.getKey() + " = " + e.getValue());
        }

        // [6] 구현체별 순서 비교
        System.out.println("\n[6] 구현체별 키 순서");
        String[] keys = {"c", "a", "b"};
        Map<String, Integer> hash = new HashMap<>();
        Map<String, Integer> linked = new LinkedHashMap<>();
        Map<String, Integer> tree = new TreeMap<>();
        for (int i = 0; i < keys.length; i++) {
            hash.put(keys[i], i); linked.put(keys[i], i); tree.put(keys[i], i);
        }
        System.out.println("HashMap       : " + hash + " (순서 무의미)");
        System.out.println("LinkedHashMap : " + linked + " (삽입 순서)");
        System.out.println("TreeMap       : " + tree + " (키 정렬)");

        System.out.println("\n프로그램 정상 종료");
    }
}
