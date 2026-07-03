package com.edu.collections;

import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.Deque;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 * Chapter 03 - Queue / Deque 예제
 *
 * Queue(큐)는 데이터를 "한 줄로 세워" 처리하는 자료구조입니다.
 *
 * - Queue      : 기본적으로 FIFO (First In, First Out / 먼저 들어온 것이 먼저 나감).
 *                offer(삽입), poll(맨 앞 제거), peek(맨 앞 조회) 를 사용합니다.
 * - Deque      : "양쪽 끝" 모두에서 삽입/삭제가 가능한 큐(Double Ended Queue).
 *                ArrayDeque 구현체가 대표적이며, 스택(LIFO)으로도 사용할 수 있습니다.
 * - PriorityQueue : "우선순위"가 높은(기본은 값이 작은) 요소가 먼저 나오는 큐.
 *
 * 참고: add/remove/element 대신 offer/poll/peek 를 쓰면
 *      실패 시 예외 대신 false/null 을 돌려주어 다루기 편합니다.
 */
public class QueueExample {

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("  Chapter 03 - Queue / Deque 예제");
        System.out.println("========================================\n");

        // ======================================================
        // 1. Queue - FIFO (먼저 들어온 것이 먼저 나감)
        // ======================================================
        System.out.println("--- 1. Queue (FIFO) ---");

        Queue<String> queue = new LinkedList<>();
        queue.offer("첫 번째");   // 큐의 뒤쪽에 삽입
        queue.offer("두 번째");
        queue.offer("세 번째");
        System.out.println("  Queue: " + queue);
        System.out.println("  peek (맨 앞 조회, 제거X): " + queue.peek());
        System.out.println("  poll (맨 앞 꺼내고 제거): " + queue.poll());
        System.out.println("  poll 후 Queue: " + queue);

        // ======================================================
        // 2. PriorityQueue - 우선순위 큐 (기본: 최소 힙)
        // ======================================================
        System.out.println("\n--- 2. PriorityQueue (우선순위 큐) ---");

        // 기본은 오름차순 = 값이 작은 것이 먼저 나옴 (최소 힙)
        PriorityQueue<Integer> minHeap = new PriorityQueue<>();
        minHeap.offer(30);
        minHeap.offer(10);
        minHeap.offer(20);
        System.out.print("  최소 힙 추출 순서: ");
        while (!minHeap.isEmpty()) {
            System.out.print(minHeap.poll() + " ");   // 10 20 30 순으로 나옴
        }
        System.out.println();

        // Comparator.reverseOrder() 를 주면 값이 큰 것이 먼저 나옴 (최대 힙)
        PriorityQueue<Integer> maxHeap = new PriorityQueue<>(Comparator.reverseOrder());
        maxHeap.offer(30);
        maxHeap.offer(10);
        maxHeap.offer(20);
        System.out.print("  최대 힙 추출 순서: ");
        while (!maxHeap.isEmpty()) {
            System.out.print(maxHeap.poll() + " ");    // 30 20 10 순으로 나옴
        }
        System.out.println();

        // ======================================================
        // 3. ArrayDeque - 큐로 사용 (FIFO)
        // ======================================================
        System.out.println("\n--- 3. Deque (ArrayDeque) 를 큐로 사용 ---");

        Deque<String> deque = new ArrayDeque<>();
        deque.offerLast("A");   // 뒤쪽에 삽입
        deque.offerLast("B");
        deque.offerLast("C");
        System.out.println("  Deque(큐): " + deque);
        System.out.println("  pollFirst (앞에서 제거): " + deque.pollFirst());
        System.out.println("  결과: " + deque);

        // ======================================================
        // 4. ArrayDeque - 스택으로 사용 (LIFO)
        // ======================================================
        System.out.println("\n--- 4. Deque 를 스택으로 사용 (LIFO) ---");

        // 나중에 넣은 것이 먼저 나오는 스택. push / pop / peek 사용.
        Deque<String> stack = new ArrayDeque<>();
        stack.push("바닥");     // push == offerFirst (앞쪽에 쌓음)
        stack.push("중간");
        stack.push("꼭대기");
        System.out.println("  Deque(스택): " + stack);
        System.out.println("  peek (맨 위 조회): " + stack.peek());
        System.out.println("  pop (맨 위 꺼내기): " + stack.pop());   // pop == pollFirst
        System.out.println("  pop 후: " + stack);

        // ======================================================
        // 5. 양방향 삽입/삭제
        // ======================================================
        System.out.println("\n--- 5. 양방향(both ends) 삽입/삭제 ---");

        deque.offerFirst("맨 앞");   // 앞쪽에 삽입
        deque.offerLast("맨 뒤");    // 뒤쪽에 삽입
        System.out.println("  양방향 삽입 후: " + deque);
        System.out.println("  pollFirst: " + deque.pollFirst());
        System.out.println("  pollLast: " + deque.pollLast());
        System.out.println("  결과: " + deque);

        System.out.println("\n========================================");
        System.out.println("  Queue / Deque 예제 완료!");
        System.out.println("========================================");
    }
}
