package com.edu.collections;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Chapter 03 - Map 예제
 *
 * Map은 "키(key) - 값(value) 쌍"으로 데이터를 저장하는 컬렉션입니다.
 * 키는 중복될 수 없고(같은 키로 put하면 값이 덮어써짐), 값은 중복될 수 있습니다.
 *
 * 대표 구현체 3가지 (순서 특성이 다릅니다):
 * - HashMap       : 해시 테이블 기반. 가장 빠르지만 "순서를 보장하지 않음".
 * - LinkedHashMap : "삽입한 순서"를 유지함.
 * - TreeMap       : 레드-블랙 트리 기반. "키 기준으로 자동 정렬"됨.
 */
public class MapExample {

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("  Chapter 03 - Map 예제");
        System.out.println("========================================\n");

        // ======================================================
        // 1. HashMap - put / get, 키 중복 시 값 갱신
        // ======================================================
        System.out.println("--- 1. HashMap (순서 없음) ---");

        Map<String, Integer> hashMap = new HashMap<>();
        hashMap.put("김철수", 90);   // 키-값 저장
        hashMap.put("이영희", 85);
        hashMap.put("박민수", 95);
        hashMap.put("김철수", 92);   // 이미 있는 키 -> 기존 값(90)이 92로 "갱신"됨
        System.out.println("  HashMap: " + hashMap);
        System.out.println("  get(\"김철수\"): " + hashMap.get("김철수"));
        System.out.println("  containsKey(\"이영희\"): " + hashMap.containsKey("이영희"));

        // ======================================================
        // 2. LinkedHashMap - 삽입 순서 유지
        // ======================================================
        System.out.println("\n--- 2. LinkedHashMap (삽입 순서 유지) ---");

        Map<String, Integer> linkedHashMap = new LinkedHashMap<>();
        linkedHashMap.put("세 번째", 3);
        linkedHashMap.put("첫 번째", 1);
        linkedHashMap.put("두 번째", 2);
        System.out.println("  LinkedHashMap: " + linkedHashMap + " (넣은 순서 그대로)");

        // ======================================================
        // 3. TreeMap - 키 기준 자동 정렬
        // ======================================================
        System.out.println("\n--- 3. TreeMap (키 정렬) ---");

        // 기존 맵을 넘겨 생성하면 그 내용을 키 기준으로 정렬해 담습니다.
        Map<String, Integer> treeMap = new TreeMap<>(hashMap);
        System.out.println("  TreeMap: " + treeMap + " (키가 사전 순으로 정렬됨)");

        // ======================================================
        // 4. 유용한 메서드들 (Java 8+)
        // ======================================================
        System.out.println("\n--- 4. Map 유용한 메서드 ---");

        Map<String, Integer> scores = new HashMap<>();
        scores.put("국어", 80);
        scores.put("영어", 90);

        // getOrDefault: 키가 없으면 지정한 기본값을 반환 (원본은 바뀌지 않음)
        int mathScore = scores.getOrDefault("수학", 0);
        System.out.println("  getOrDefault(\"수학\", 0): " + mathScore);

        // putIfAbsent: 키가 "없을 때만" 추가 (있으면 무시)
        scores.putIfAbsent("영어", 100);  // 이미 있으므로 무시됨
        scores.putIfAbsent("수학", 75);   // 없으므로 새로 추가됨
        System.out.println("  putIfAbsent 후: " + scores);

        // computeIfAbsent: 키가 없을 때, 값을 "계산해서" 넣음
        scores.computeIfAbsent("과학", key -> key.length() * 10);
        System.out.println("  computeIfAbsent 후: " + scores);

        // merge: 기존 값과 새 값을 병합 (여기서는 더하기)
        scores.merge("국어", 10, Integer::sum);  // 국어 80 + 10 = 90
        System.out.println("  merge(\"국어\", 10, sum) 후: " + scores);

        // ======================================================
        // 5. 순회(iteration) 방법들
        // ======================================================
        System.out.println("\n--- 5. Map 순회 ---");

        // (1) entrySet: 키와 값을 함께 꺼낼 때 가장 효율적
        System.out.print("  entrySet: ");
        for (Map.Entry<String, Integer> entry : scores.entrySet()) {
            System.out.print(entry.getKey() + "=" + entry.getValue() + " ");
        }
        System.out.println();

        // (2) keySet: 키만 순회 (필요하면 get으로 값을 꺼냄)
        System.out.print("  keySet: ");
        for (String key : scores.keySet()) {
            System.out.print(key + "(" + scores.get(key) + ") ");
        }
        System.out.println();

        // (3) values: 값만 순회
        System.out.print("  values: ");
        for (Integer value : scores.values()) {
            System.out.print(value + " ");
        }
        System.out.println();

        // (4) forEach + 람다 (Java 8+)
        System.out.print("  forEach 람다: ");
        scores.forEach((key, value) -> System.out.print(key + ":" + value + " "));
        System.out.println();

        System.out.println("\n  [순서 특성 요약]");
        System.out.println("    HashMap       - 순서 없음 (가장 빠름)");
        System.out.println("    LinkedHashMap - 삽입 순서 유지");
        System.out.println("    TreeMap       - 키 기준 자동 정렬");

        System.out.println("\n========================================");
        System.out.println("  Map 예제 완료!");
        System.out.println("========================================");
    }
}
