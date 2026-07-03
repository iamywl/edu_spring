package com.edu.collections;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Chapter 03 - Collections 유틸리티 예제
 *
 * java.util.Collections 는 컬렉션을 다루는 "정적(static) 유틸리티 메서드" 모음입니다.
 * (Collection 인터페이스와는 이름만 비슷할 뿐 다른 것입니다. 끝에 s 가 붙습니다.)
 *
 * 자주 쓰는 기능:
 *   - 정렬        : sort
 *   - 뒤집기      : reverse
 *   - 섞기        : shuffle
 *   - 최소/최대   : min, max
 *   - 빈도수      : frequency
 *   - 불변 뷰      : unmodifiableList (수정 불가)
 *   - 동기화 뷰    : synchronizedList (멀티스레드 안전)
 *   - 빈/단일 컬렉션 : emptyList, singletonList 등
 */
public class CollectionsUtilExample {

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("  Chapter 03 - Collections 유틸리티 예제");
        System.out.println("========================================\n");

        // ======================================================
        // 1. 정렬 / 역순 / 섞기
        // ======================================================
        System.out.println("--- 1. 정렬 / reverse / shuffle ---");

        // List.of 로 만든 리스트는 불변이므로, 수정 가능한 ArrayList로 감싸서 사용
        List<Integer> numbers = new ArrayList<>(List.of(5, 3, 8, 1, 9, 2, 7));
        System.out.println("  원본: " + numbers);

        Collections.sort(numbers);   // 오름차순 정렬
        System.out.println("  오름차순 sort: " + numbers);

        Collections.sort(numbers, Comparator.reverseOrder());   // Comparator로 내림차순 정렬
        System.out.println("  내림차순 sort: " + numbers);

        Collections.reverse(numbers);   // 현재 순서를 뒤집음
        System.out.println("  reverse: " + numbers);

        Collections.shuffle(numbers);   // 무작위로 섞음 (실행마다 결과가 달라짐)
        System.out.println("  shuffle: " + numbers);

        // ======================================================
        // 2. 최솟값 / 최댓값 / 빈도수
        // ======================================================
        System.out.println("\n--- 2. min / max / frequency ---");

        System.out.println("  min: " + Collections.min(numbers));
        System.out.println("  max: " + Collections.max(numbers));

        List<String> fruits = List.of("사과", "바나나", "사과", "체리", "사과");
        System.out.println("  과일 리스트: " + fruits);
        System.out.println("  '사과' 빈도 frequency: " + Collections.frequency(fruits, "사과"));

        // ======================================================
        // 3. 불변 리스트 (unmodifiableList)
        // ======================================================
        System.out.println("\n--- 3. unmodifiableList (불변 뷰) ---");

        List<String> unmodifiable = Collections.unmodifiableList(new ArrayList<>(fruits));
        System.out.println("  불변 리스트: " + unmodifiable);
        try {
            unmodifiable.add("포도");   // 수정 시도 -> 예외 발생
        } catch (UnsupportedOperationException e) {
            System.out.println("  수정 시도 -> UnsupportedOperationException 발생!");
        }

        // ======================================================
        // 4. 동기화 리스트 (synchronizedList)
        // ======================================================
        System.out.println("\n--- 4. synchronizedList (멀티스레드 안전) ---");

        List<String> syncList = Collections.synchronizedList(new ArrayList<>());
        syncList.add("스레드 안전");
        System.out.println("  동기화 리스트: " + syncList);
        System.out.println("  멀티스레드 환경에서는 synchronizedList 또는 CopyOnWriteArrayList 사용");

        // ======================================================
        // 5. 빈 컬렉션 / 단일 요소 컬렉션
        // ======================================================
        System.out.println("\n--- 5. 빈 / 단일 요소 컬렉션 ---");

        List<String> emptyList = Collections.emptyList();
        Map<String, Integer> emptyMap = Collections.emptyMap();
        System.out.println("  빈 리스트 emptyList: " + emptyList);
        System.out.println("  빈 맵 emptyMap: " + emptyMap);

        List<String> singletonList = Collections.singletonList("유일한 요소");
        System.out.println("  싱글턴 리스트 singletonList: " + singletonList);

        System.out.println("\n========================================");
        System.out.println("  Collections 유틸리티 예제 완료!");
        System.out.println("========================================");
    }
}
