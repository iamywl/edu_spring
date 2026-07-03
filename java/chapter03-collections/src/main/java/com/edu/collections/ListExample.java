package com.edu.collections;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Chapter 03 - List 예제
 *
 * List는 "순서가 있는" 컬렉션이며 "중복을 허용"합니다.
 * 대표 구현체로 ArrayList와 LinkedList가 있습니다.
 *
 * - ArrayList : 내부적으로 배열(가변 배열)을 사용합니다.
 *               인덱스로 값을 꺼내는 접근(get)이 매우 빠릅니다. -> O(1)
 *               반대로 리스트 "중간"에 삽입/삭제하면 뒤의 요소들을 밀어야 해서 느립니다. -> O(n)
 *
 * - LinkedList : 이중 연결 리스트(각 노드가 앞/뒤 노드를 가리킴)를 사용합니다.
 *                iterator로 순회하며 삽입/삭제하는 경우 O(1)로 빠릅니다.
 *                하지만 특정 인덱스에 접근하려면 처음부터 순차 탐색을 해야 해서 느립니다. -> O(n)
 *
 * [언제 무엇을 쓰나?]
 *   대부분의 경우 ArrayList가 더 효율적이고 메모리도 적게 씁니다.
 *   빈번한 앞/뒤 삽입·삭제가 필요하다면 LinkedList(또는 ArrayDeque)를 고려합니다.
 */
public class ListExample {

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("  Chapter 03 - List 예제");
        System.out.println("========================================\n");

        // ======================================================
        // 1. ArrayList - 배열 기반, 인덱스 접근이 빠름
        // ======================================================
        System.out.println("--- 1. ArrayList ---");

        List<String> arrayList = new ArrayList<>();
        arrayList.add("사과");      // 리스트 끝에 추가
        arrayList.add("바나나");
        arrayList.add("체리");
        arrayList.add("바나나");    // List는 중복을 허용하므로 그대로 추가됨
        System.out.println("  추가 후 ArrayList: " + arrayList);

        // 인덱스로 조회 (0부터 시작). ArrayList에서는 O(1)로 매우 빠름
        System.out.println("  인덱스 접근 get(1): " + arrayList.get(1));
        System.out.println("  크기 size(): " + arrayList.size());
        System.out.println("  '바나나' 포함 여부 contains: " + arrayList.contains("바나나"));
        System.out.println("  '바나나'의 첫 위치 indexOf: " + arrayList.indexOf("바나나"));

        // 특정 인덱스 값 수정
        arrayList.set(0, "딸기");
        System.out.println("  set(0, \"딸기\") 후: " + arrayList);

        // 값으로 삭제 / 인덱스로 삭제
        arrayList.remove("바나나");        // 값이 일치하는 "첫 번째" 요소만 삭제됨
        System.out.println("  remove(\"바나나\") 후: " + arrayList);
        arrayList.remove(0);                // 인덱스 0 요소 삭제
        System.out.println("  remove(0) 후: " + arrayList);

        // ======================================================
        // 2. LinkedList - 연결 리스트 기반, 삽입/삭제에 유리
        // ======================================================
        System.out.println("\n--- 2. LinkedList ---");

        List<String> linkedList = new LinkedList<>();
        linkedList.add("하나");
        linkedList.add("둘");
        linkedList.add("셋");
        linkedList.add(1, "하나반");   // 인덱스 1 위치에 "삽입" (뒤 요소는 한 칸씩 밀림)
        System.out.println("  add(1, \"하나반\") 후: " + linkedList);

        // ======================================================
        // 3. 순회(반복) 방법들
        // ======================================================
        System.out.println("\n--- 3. 순회(iteration) ---");

        // (1) 향상된 for문 (가장 흔한 방식)
        System.out.print("  for-each: ");
        for (String item : linkedList) {
            System.out.print(item + " ");
        }
        System.out.println();

        // (2) 인덱스 기반 for문 (ArrayList에 적합, LinkedList에서는 비효율적)
        System.out.print("  index for: ");
        for (int i = 0; i < linkedList.size(); i++) {
            System.out.print(i + "->" + linkedList.get(i) + " ");
        }
        System.out.println();

        // (3) forEach + 람다 (Java 8+)
        System.out.print("  forEach 람다: ");
        linkedList.forEach(item -> System.out.print(item + " "));
        System.out.println();

        // ======================================================
        // 4. 불변 리스트 (Java 9+) 와 성능 정리
        // ======================================================
        System.out.println("\n--- 4. 불변 리스트 & 성능 정리 ---");

        // List.of 로 만든 리스트는 수정할 수 없습니다(추가/삭제 시 예외 발생).
        List<String> immutableList = List.of("X", "Y", "Z");
        System.out.println("  불변 리스트 (List.of): " + immutableList);
        try {
            immutableList.add("W");   // UnsupportedOperationException 발생
        } catch (UnsupportedOperationException e) {
            System.out.println("  불변 리스트 수정 시도 -> UnsupportedOperationException!");
        }

        System.out.println("\n  [성능 특성 요약]");
        System.out.println("    ArrayList  - 인덱스 접근 O(1), 중간 삽입/삭제 O(n)");
        System.out.println("    LinkedList - 인덱스 접근 O(n), (iterator) 삽입/삭제 O(1)");
        System.out.println("    보통은 ArrayList가 무난하며, 앞/뒤 잦은 삽입·삭제엔 LinkedList/ArrayDeque 고려");

        System.out.println("\n========================================");
        System.out.println("  List 예제 완료!");
        System.out.println("========================================");
    }
}
