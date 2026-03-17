package com.edu.collections;

import java.util.*;

/**
 * Chapter 03 - 컬렉션 프레임워크 예제
 *
 * List, Set, Map, Queue, Deque 및 Collections 유틸리티를 학습합니다.
 */
public class CollectionExample {

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("  Chapter 03 - 컬렉션 프레임워크 예제");
        System.out.println("========================================\n");

        demonstrateList();
        demonstrateSet();
        demonstrateMap();
        demonstrateQueue();
        demonstrateCollectionsUtility();

        System.out.println("========================================");
        System.out.println("  컬렉션 예제 완료!");
        System.out.println("========================================");
    }

    // ======================================================
    // 1. List - 순서가 있는 컬렉션 (중복 허용)
    // ======================================================
    static void demonstrateList() {
        System.out.println("--- 1. List (ArrayList vs LinkedList) ---");

        // ArrayList: 내부적으로 배열 사용 - 인덱스 접근이 빠름 O(1)
        List<String> arrayList = new ArrayList<>();
        arrayList.add("사과");
        arrayList.add("바나나");
        arrayList.add("체리");
        arrayList.add("바나나");  // 중복 허용
        System.out.println("  ArrayList: " + arrayList);
        System.out.println("  인덱스 접근 (get(1)): " + arrayList.get(1));

        // LinkedList: 이중 연결 리스트 - iterator를 통한 삽입/삭제 O(1), 인덱스 접근 O(n)
        List<String> linkedList = new LinkedList<>();
        linkedList.add("하나");
        linkedList.add("둘");
        linkedList.add("셋");
        linkedList.add(1, "하나반");  // 인덱스 1에 삽입
        System.out.println("  LinkedList: " + linkedList);

        // 성능 비교 개념 설명
        System.out.println("  [성능 비교]");
        System.out.println("    ArrayList  - 인덱스 접근: O(1), 중간 삽입/삭제: O(n)");
        System.out.println("    LinkedList - 인덱스 접근: O(n), 삽입/삭제: O(1) (iterator 사용 시)");
        System.out.println("    일반적으로 ArrayList를 사용하는 것이 더 효율적입니다.");

        // 불변 리스트 생성 (Java 9+)
        List<String> immutableList = List.of("X", "Y", "Z");
        System.out.println("  불변 리스트 (List.of): " + immutableList);
        // immutableList.add("W"); // UnsupportedOperationException!
        System.out.println();
    }

    // ======================================================
    // 2. Set - 중복을 허용하지 않는 컬렉션
    // ======================================================
    static void demonstrateSet() {
        System.out.println("--- 2. Set (HashSet, TreeSet, LinkedHashSet) ---");

        // HashSet: 해시 테이블 기반 - 가장 빠르지만 순서 보장 안 됨
        Set<String> hashSet = new HashSet<>();
        hashSet.add("바나나");
        hashSet.add("사과");
        hashSet.add("체리");
        hashSet.add("사과");  // 중복 추가 시도 -> 무시됨
        System.out.println("  HashSet (순서 없음): " + hashSet);

        // TreeSet: 레드-블랙 트리 기반 - 자동 정렬
        Set<String> treeSet = new TreeSet<>();
        treeSet.add("바나나");
        treeSet.add("사과");
        treeSet.add("체리");
        treeSet.add("귤");
        System.out.println("  TreeSet (정렬됨): " + treeSet);

        // LinkedHashSet: 해시 테이블 + 연결 리스트 - 삽입 순서 유지
        Set<String> linkedHashSet = new LinkedHashSet<>();
        linkedHashSet.add("바나나");
        linkedHashSet.add("사과");
        linkedHashSet.add("체리");
        linkedHashSet.add("귤");
        System.out.println("  LinkedHashSet (삽입 순서): " + linkedHashSet);

        // Set 연산
        Set<Integer> setA = new HashSet<>(Set.of(1, 2, 3, 4, 5));
        Set<Integer> setB = new HashSet<>(Set.of(3, 4, 5, 6, 7));

        // 합집합
        Set<Integer> union = new HashSet<>(setA);
        union.addAll(setB);
        System.out.println("  합집합 (A ∪ B): " + union);

        // 교집합
        Set<Integer> intersection = new HashSet<>(setA);
        intersection.retainAll(setB);
        System.out.println("  교집합 (A ∩ B): " + intersection);

        // 차집합
        Set<Integer> difference = new HashSet<>(setA);
        difference.removeAll(setB);
        System.out.println("  차집합 (A - B): " + difference);
        System.out.println();
    }

    // ======================================================
    // 3. Map - 키-값 쌍으로 데이터 저장
    // ======================================================
    static void demonstrateMap() {
        System.out.println("--- 3. Map (HashMap, TreeMap, LinkedHashMap) ---");

        // HashMap: 해시 테이블 기반 - 가장 빠르지만 순서 보장 안 됨
        Map<String, Integer> hashMap = new HashMap<>();
        hashMap.put("김철수", 90);
        hashMap.put("이영희", 85);
        hashMap.put("박민수", 95);
        hashMap.put("김철수", 92);  // 같은 키로 put하면 값이 갱신됨
        System.out.println("  HashMap: " + hashMap);
        System.out.println("  김철수 점수: " + hashMap.get("김철수"));

        // TreeMap: 레드-블랙 트리 기반 - 키 기준 자동 정렬
        Map<String, Integer> treeMap = new TreeMap<>(hashMap);
        System.out.println("  TreeMap (키 정렬): " + treeMap);

        // LinkedHashMap: 삽입 순서 유지
        Map<String, Integer> linkedHashMap = new LinkedHashMap<>();
        linkedHashMap.put("세 번째", 3);
        linkedHashMap.put("첫 번째", 1);
        linkedHashMap.put("두 번째", 2);
        System.out.println("  LinkedHashMap (삽입 순서): " + linkedHashMap);

        // Map의 유용한 메서드들 (Java 8+)
        System.out.println("  [Map 유용한 메서드]");

        Map<String, Integer> scores = new HashMap<>();
        scores.put("국어", 80);
        scores.put("영어", 90);

        // getOrDefault: 키가 없으면 기본값 반환
        int mathScore = scores.getOrDefault("수학", 0);
        System.out.println("  getOrDefault(\"수학\", 0): " + mathScore);

        // putIfAbsent: 키가 없을 때만 추가
        scores.putIfAbsent("영어", 100);  // 이미 있으므로 무시
        scores.putIfAbsent("수학", 75);   // 없으므로 추가
        System.out.println("  putIfAbsent 후: " + scores);

        // computeIfAbsent: 키가 없을 때 값을 계산하여 추가
        scores.computeIfAbsent("과학", key -> key.length() * 10);
        System.out.println("  computeIfAbsent 후: " + scores);

        // merge: 기존 값과 새 값을 병합
        scores.merge("국어", 10, Integer::sum);  // 국어 점수에 10 더하기
        System.out.println("  merge(\"국어\", 10, sum) 후: " + scores);

        // 순회 방법들
        System.out.println("  [Map 순회]");
        System.out.print("    entrySet: ");
        for (Map.Entry<String, Integer> entry : scores.entrySet()) {
            System.out.print(entry.getKey() + "=" + entry.getValue() + " ");
        }
        System.out.println();

        System.out.print("    forEach: ");
        scores.forEach((key, value) -> System.out.print(key + ":" + value + " "));
        System.out.println("\n");
    }

    // ======================================================
    // 4. Queue와 Deque
    // ======================================================
    static void demonstrateQueue() {
        System.out.println("--- 4. Queue, PriorityQueue, ArrayDeque ---");

        // Queue - FIFO (First In, First Out)
        Queue<String> queue = new LinkedList<>();
        queue.offer("첫 번째");   // 큐에 삽입
        queue.offer("두 번째");
        queue.offer("세 번째");
        System.out.println("  Queue: " + queue);
        System.out.println("  peek (맨 앞 조회): " + queue.peek());
        System.out.println("  poll (맨 앞 제거): " + queue.poll());
        System.out.println("  poll 후 Queue: " + queue);

        // PriorityQueue - 우선순위에 따라 정렬
        System.out.println("  [PriorityQueue - 우선순위 큐]");
        PriorityQueue<Integer> minHeap = new PriorityQueue<>();  // 기본: 오름차순 (최소 힙)
        minHeap.offer(30);
        minHeap.offer(10);
        minHeap.offer(20);
        System.out.print("  최소 힙 순서대로 추출: ");
        while (!minHeap.isEmpty()) {
            System.out.print(minHeap.poll() + " ");
        }
        System.out.println();

        // 내림차순 PriorityQueue (최대 힙)
        PriorityQueue<Integer> maxHeap = new PriorityQueue<>(Comparator.reverseOrder());
        maxHeap.offer(30);
        maxHeap.offer(10);
        maxHeap.offer(20);
        System.out.print("  최대 힙 순서대로 추출: ");
        while (!maxHeap.isEmpty()) {
            System.out.print(maxHeap.poll() + " ");
        }
        System.out.println();

        // ArrayDeque - 양방향 큐 (스택/큐 모두 가능)
        System.out.println("  [ArrayDeque - 양방향 큐]");
        Deque<String> deque = new ArrayDeque<>();

        // 큐처럼 사용 (FIFO)
        deque.offerLast("A");
        deque.offerLast("B");
        deque.offerLast("C");
        System.out.println("  Deque (큐): " + deque);

        // 스택처럼 사용 (LIFO)
        Deque<String> stack = new ArrayDeque<>();
        stack.push("바닥");     // offerFirst와 동일
        stack.push("중간");
        stack.push("꼭대기");
        System.out.println("  Deque (스택): " + stack);
        System.out.println("  pop: " + stack.pop());     // pollFirst와 동일
        System.out.println("  pop 후: " + stack);

        // 양방향 삽입/삭제
        deque.offerFirst("맨 앞");
        deque.offerLast("맨 뒤");
        System.out.println("  양방향 삽입 후: " + deque);
        System.out.println("  pollFirst: " + deque.pollFirst());
        System.out.println("  pollLast: " + deque.pollLast());
        System.out.println("  결과: " + deque);
        System.out.println();
    }

    // ======================================================
    // 5. Collections 유틸리티
    // ======================================================
    static void demonstrateCollectionsUtility() {
        System.out.println("--- 5. Collections 유틸리티 ---");

        List<Integer> numbers = new ArrayList<>(List.of(5, 3, 8, 1, 9, 2, 7));

        // 정렬
        Collections.sort(numbers);
        System.out.println("  오름차순 정렬: " + numbers);

        Collections.sort(numbers, Comparator.reverseOrder());
        System.out.println("  내림차순 정렬: " + numbers);

        // 역순
        Collections.reverse(numbers);
        System.out.println("  reverse: " + numbers);

        // 셔플
        Collections.shuffle(numbers);
        System.out.println("  shuffle: " + numbers);

        // 최솟값, 최댓값
        System.out.println("  min: " + Collections.min(numbers));
        System.out.println("  max: " + Collections.max(numbers));

        // 빈도 수
        List<String> fruits = List.of("사과", "바나나", "사과", "체리", "사과");
        System.out.println("  사과 빈도: " + Collections.frequency(fruits, "사과"));

        // 불변 리스트 - 수정 시 UnsupportedOperationException 발생
        List<String> unmodifiable = Collections.unmodifiableList(new ArrayList<>(fruits));
        System.out.println("  불변 리스트: " + unmodifiable);
        try {
            unmodifiable.add("포도");
        } catch (UnsupportedOperationException e) {
            System.out.println("  불변 리스트 수정 시도 -> UnsupportedOperationException 발생!");
        }

        // 동기화 리스트 - 멀티스레드 환경에서 안전
        List<String> syncList = Collections.synchronizedList(new ArrayList<>());
        syncList.add("스레드 안전");
        System.out.println("  동기화 리스트: " + syncList);
        System.out.println("  멀티스레드 환경에서는 synchronizedList 또는 CopyOnWriteArrayList 사용");

        // 빈 컬렉션
        List<String> emptyList = Collections.emptyList();
        Map<String, Integer> emptyMap = Collections.emptyMap();
        System.out.println("  빈 리스트: " + emptyList);
        System.out.println("  빈 맵: " + emptyMap);

        // 단일 요소 컬렉션
        List<String> singletonList = Collections.singletonList("유일한 요소");
        System.out.println("  싱글턴 리스트: " + singletonList);
        System.out.println();
    }
}
