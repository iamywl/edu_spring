package com.edu.javabook.ch15;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 * 15.6 LIFO와 FIFO
 *
 * 데이터를 넣고 빼는 "순서 규칙"에 따라 두 가지 대표 자료구조가 있다.
 *
 *  - Stack (LIFO, Last-In-First-Out) : 나중에 넣은 것이 먼저 나온다. (접시 쌓기)
 *  - Queue (FIFO, First-In-First-Out): 먼저 넣은 것이 먼저 나온다. (줄 서기)
 *
 * 구현 권장 :
 *  - 스택은 java.util.Stack 대신 Deque(ArrayDeque) 사용을 권장한다.
 *      push(맨 위 삽입) / pop(맨 위 제거) / peek(맨 위 확인)
 *  - 큐는 Queue(ArrayDeque) 사용.
 *      offer(뒤에 삽입) / poll(앞에서 제거) / peek(앞 확인)
 *  - PriorityQueue : 넣은 순서와 무관하게 "우선순위(기본: 작은 값)"가 먼저 나온다.
 *
 * 이 소절에서는 Deque 기반 Stack/Queue 와 PriorityQueue 를 실습한다.
 */
public class StackQueueCollection {

    public static void main(String[] args) {

        System.out.println("=== 15.6 LIFO와 FIFO ===");

        // [1] Stack (LIFO) - Deque 를 스택으로 사용
        System.out.println("\n[1] Stack (LIFO) - Deque.push/pop/peek");
        Deque<String> stack = new ArrayDeque<>();
        stack.push("A");                    // 아래
        stack.push("B");
        stack.push("C");                    // 맨 위
        System.out.println("쌓기 후 상태(위→아래): " + stack);
        System.out.println("peek(맨 위 확인): " + stack.peek());
        System.out.print("pop 순서: ");
        while (!stack.isEmpty()) System.out.print(stack.pop() + " ");
        System.out.println("  (나중에 넣은 C 부터)");

        // [2] Queue (FIFO) - ArrayDeque 를 큐로 사용
        System.out.println("\n[2] Queue (FIFO) - offer/poll/peek");
        Queue<String> queue = new ArrayDeque<>();
        queue.offer("1번");                 // 먼저 들어옴
        queue.offer("2번");
        queue.offer("3번");
        System.out.println("대기열: " + queue);
        System.out.println("peek(맨 앞 확인): " + queue.peek());
        System.out.print("poll 순서: ");
        while (!queue.isEmpty()) System.out.print(queue.poll() + " ");
        System.out.println("  (먼저 넣은 1번 부터)");

        // [3] PriorityQueue - 우선순위 순으로 꺼내짐
        System.out.println("\n[3] PriorityQueue - 우선순위(기본 오름차순)");
        PriorityQueue<Integer> pq = new PriorityQueue<>();
        for (int n : new int[]{40, 10, 30, 20, 50}) pq.offer(n);
        System.out.println("넣은 순서: 40 10 30 20 50");
        System.out.print("poll 순서: ");
        while (!pq.isEmpty()) System.out.print(pq.poll() + " ");
        System.out.println("  (작은 값부터 나옴)");

        System.out.println("\n→ LIFO=스택, FIFO=큐, 우선순위=PriorityQueue");
        System.out.println("\n프로그램 정상 종료");
    }
}
