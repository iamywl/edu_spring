package com.edu.algorithms;

/**
 * CS 알고리즘 트랙 - 자료구조 직접 구현하기 (라이브러리 없이)
 *
 * 우리는 이미 ArrayList, LinkedList, Stack, ArrayDeque를 "써봤다".
 * 하지만 그 안이 어떻게 생겼는지 모르면, Big-O가 왜 그렇게 나오는지도 알 수 없다.
 * 그래서 여기서는 표준 라이브러리를 쓰지 않고 '바닥부터' 직접 만든다.
 *
 * 만들 자료구조 (모두 정수 int 기준, 이해를 위해 최소 구현):
 *   1) 동적 배열(DynamicArray)  ← java.util.ArrayList 의 뼈대
 *   2) 단일 연결 리스트(LinkedList) ← java.util.LinkedList 의 뼈대
 *   3) 스택(Stack)  ← LIFO, java.util.Deque(스택 용도)
 *   4) 큐(Queue)    ← FIFO, java.util.Queue/ArrayDeque
 *
 * 각 연산 옆에 Big-O를 주석으로 달았다. "왜 그 비용인가"를 코드로 확인하라.
 */
public class DataStructuresFromScratch {

    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println(" CS 알고리즘: 자료구조 직접 구현 (bare-metal)");
        System.out.println("=================================================\n");

        demoDynamicArray();
        demoLinkedList();
        demoStack();
        demoQueue();
        conclusion();
    }

    // ══════════════════════════════════════════════
    // 1. 동적 배열 (Dynamic Array) — ArrayList의 뼈대
    //    내부는 고정 크기 배열. 꽉 차면 2배 큰 배열로 '복사 이사'한다.
    //    - get/set(index): O(1)   (배열은 인덱스로 즉시 접근)
    //    - add(맨뒤)      : 평균 O(1) (가끔 확장 O(n)이 있지만 분할상환하면 O(1))
    //    - add(중간)/remove(중간): O(n) (뒤 원소들을 밀거나 당김)
    // ══════════════════════════════════════════════
    static class DynamicArray {
        private int[] data;   // 실제 저장 공간
        private int size;     // 현재 원소 개수 (data.length는 '용량')

        DynamicArray() {
            data = new int[2]; // 작은 초기 용량에서 시작
            size = 0;
        }

        int size() { return size; }

        int get(int index) {                       // O(1)
            checkIndex(index);
            return data[index];
        }

        void set(int index, int value) {           // O(1)
            checkIndex(index);
            data[index] = value;
        }

        void add(int value) {                      // 평균(분할상환) O(1)
            if (size == data.length) grow();       // 꽉 차면 용량 2배로
            data[size++] = value;
        }

        void insert(int index, int value) {        // O(n): 뒤 원소들을 한 칸씩 민다
            if (index < 0 || index > size) throw new IndexOutOfBoundsException();
            if (size == data.length) grow();
            for (int i = size; i > index; i--) data[i] = data[i - 1];
            data[index] = value;
            size++;
        }

        int removeAt(int index) {                  // O(n): 뒤 원소들을 한 칸씩 당긴다
            checkIndex(index);
            int removed = data[index];
            for (int i = index; i < size - 1; i++) data[i] = data[i + 1];
            size--;
            return removed;
        }

        // 용량 2배 확장 = 새 배열을 만들어 전부 복사 → 이 순간만 O(n)
        private void grow() {
            int[] bigger = new int[data.length * 2];
            for (int i = 0; i < size; i++) bigger[i] = data[i]; // 복사 이사
            data = bigger;
        }

        private void checkIndex(int index) {
            if (index < 0 || index >= size) throw new IndexOutOfBoundsException("index=" + index);
        }

        int capacity() { return data.length; }

        @Override public String toString() {
            StringBuilder sb = new StringBuilder("[");
            for (int i = 0; i < size; i++) { if (i > 0) sb.append(", "); sb.append(data[i]); }
            return sb.append("]").toString();
        }
    }

    static void demoDynamicArray() {
        System.out.println("── 1. 동적 배열 (ArrayList의 뼈대) ──");
        DynamicArray arr = new DynamicArray();
        System.out.println("  초기 용량=" + arr.capacity() + ", 크기=" + arr.size());
        System.out.println("  add로 8개 넣으며 '용량 확장(복사 이사)'을 관찰:");
        for (int i = 1; i <= 8; i++) {
            int before = arr.capacity();
            arr.add(i * 10);
            if (arr.capacity() != before) {
                System.out.printf("    용량 확장! %d → %d  (원소 %d개일 때)%n",
                        before, arr.capacity(), arr.size());
            }
        }
        System.out.println("  내용: " + arr + "  (용량=" + arr.capacity() + ")");
        System.out.println("  get(3) = " + arr.get(3) + "   ← O(1) 즉시 접근");
        arr.insert(0, 999);
        System.out.println("  insert(0, 999) 후: " + arr + "   ← 앞 삽입은 O(n)(뒤로 밀기)");
        System.out.println("  removeAt(0) = " + arr.removeAt(0) + " 후: " + arr);
        System.out.println("  → 확장은 원소가 늘 때마다가 아니라 '꽉 찰 때만' 일어나므로 평균 O(1).\n");
    }

    // ══════════════════════════════════════════════
    // 2. 단일 연결 리스트 (Singly Linked List) — LinkedList의 뼈대
    //    노드가 '값 + 다음 노드 참조'로 이어진 사슬. 배열과 달리 연속 메모리가 아니다.
    //    - addFirst/removeFirst: O(1) (head만 바꿈)
    //    - addLast            : O(1) (tail 포인터 유지 시)
    //    - get(index)         : O(n) (head부터 따라가야 함 → 랜덤 접근 불리)
    // ══════════════════════════════════════════════
    static class LinkedList {
        // 노드: 값 하나 + 다음 노드로 가는 화살표(참조)
        private static class Node {
            int value;
            Node next;
            Node(int value) { this.value = value; }
        }

        private Node head;  // 첫 노드
        private Node tail;  // 마지막 노드(끝 삽입 O(1)을 위해 유지)
        private int size;

        int size() { return size; }

        void addFirst(int value) {                 // O(1)
            Node n = new Node(value);
            n.next = head;
            head = n;
            if (tail == null) tail = n;
            size++;
        }

        void addLast(int value) {                  // O(1) (tail 덕분)
            Node n = new Node(value);
            if (tail == null) { head = tail = n; }
            else { tail.next = n; tail = n; }
            size++;
        }

        int removeFirst() {                        // O(1)
            if (head == null) throw new IllegalStateException("비어 있음");
            int v = head.value;
            head = head.next;
            if (head == null) tail = null;
            size--;
            return v;
        }

        int get(int index) {                       // O(n): head부터 index만큼 이동
            if (index < 0 || index >= size) throw new IndexOutOfBoundsException();
            Node cur = head;
            for (int i = 0; i < index; i++) cur = cur.next; // 화살표를 따라 걷는다
            return cur.value;
        }

        @Override public String toString() {
            StringBuilder sb = new StringBuilder();
            Node cur = head;
            while (cur != null) { sb.append(cur.value).append(cur.next != null ? " → " : ""); cur = cur.next; }
            return sb.length() == 0 ? "(빈 리스트)" : sb.toString();
        }
    }

    static void demoLinkedList() {
        System.out.println("── 2. 단일 연결 리스트 (LinkedList의 뼈대) ──");
        LinkedList list = new LinkedList();
        list.addLast(10); list.addLast(20); list.addLast(30);
        list.addFirst(5);
        System.out.println("  addLast 10,20,30 후 addFirst 5:  " + list);
        System.out.println("  구조: head → [5] → [10] → [20] → [30] → null");
        System.out.println("  get(2) = " + list.get(2) + "   ← head부터 2칸 걸어감(O(n))");
        System.out.println("  removeFirst() = " + list.removeFirst() + " 후: " + list + "  ← head 교체만(O(1))");
        System.out.println("  → 인덱스 랜덤 접근은 배열이 유리(O(1)), 앞/끝 삽입·삭제는 연결 리스트가 유리(O(1)).\n");
    }

    // ══════════════════════════════════════════════
    // 3. 스택 (Stack) — LIFO(Last In, First Out)
    //    "접시 쌓기". 위에서만 넣고(push) 위에서만 뺀다(pop).
    //    연결 리스트의 앞쪽(head)을 top으로 쓰면 push/pop 모두 O(1).
    //    쓰임: 함수 콜 스택, 실행 취소(undo), 괄호 짝 검사, DFS.
    // ══════════════════════════════════════════════
    static class Stack {
        private static class Node { int v; Node next; Node(int v){this.v=v;} }
        private Node top;
        private int size;

        void push(int v) {                         // O(1): 맨 위에 얹기
            Node n = new Node(v);
            n.next = top;
            top = n;
            size++;
        }

        int pop() {                                // O(1): 맨 위 걷어내기
            if (top == null) throw new IllegalStateException("스택이 비어 있음");
            int v = top.v;
            top = top.next;
            size--;
            return v;
        }

        int peek() {                               // O(1): 위 값만 엿보기(안 뺌)
            if (top == null) throw new IllegalStateException("스택이 비어 있음");
            return top.v;
        }

        boolean isEmpty() { return size == 0; }
        int size() { return size; }
    }

    static void demoStack() {
        System.out.println("── 3. 스택 (LIFO) ──");
        Stack st = new Stack();
        System.out.println("  push 1, 2, 3  (위로 쌓임)");
        st.push(1); st.push(2); st.push(3);
        System.out.println("  peek() = " + st.peek() + "  (맨 위 = 마지막에 넣은 3)");
        System.out.print("  pop 순서: ");
        while (!st.isEmpty()) System.out.print(st.pop() + " ");
        System.out.println("  ← 넣은 반대 순서(3,2,1)로 나온다 = LIFO");

        // 응용: 괄호 짝 맞추기
        System.out.println("  응용) 괄호 검사 \"(()())\" → " + balanced("(()())"));
        System.out.println("  응용) 괄호 검사 \"(()\"    → " + balanced("(()"));
        System.out.println();
    }

    // 스택 활용 예: 여는 괄호는 push, 닫는 괄호는 pop해서 짝을 맞춘다.
    static boolean balanced(String s) {
        Stack st = new Stack();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '(') st.push(1);
            else if (c == ')') { if (st.isEmpty()) return false; st.pop(); }
        }
        return st.isEmpty();
    }

    // ══════════════════════════════════════════════
    // 4. 큐 (Queue) — FIFO(First In, First Out)
    //    "매표소 줄서기". 뒤로 들어와(enqueue) 앞에서 나간다(dequeue).
    //    head/tail 포인터를 둔 연결 리스트로 둘 다 O(1).
    //    쓰임: 작업 대기열, BFS(너비 우선 탐색), 프린터 스풀.
    // ══════════════════════════════════════════════
    static class Queue {
        private static class Node { int v; Node next; Node(int v){this.v=v;} }
        private Node head; // 나가는 쪽(앞)
        private Node tail; // 들어오는 쪽(뒤)
        private int size;

        void enqueue(int v) {                      // O(1): 뒤에 붙이기
            Node n = new Node(v);
            if (tail == null) head = tail = n;
            else { tail.next = n; tail = n; }
            size++;
        }

        int dequeue() {                            // O(1): 앞에서 빼기
            if (head == null) throw new IllegalStateException("큐가 비어 있음");
            int v = head.v;
            head = head.next;
            if (head == null) tail = null;
            size--;
            return v;
        }

        boolean isEmpty() { return size == 0; }
        int size() { return size; }
    }

    static void demoQueue() {
        System.out.println("── 4. 큐 (FIFO) ──");
        Queue q = new Queue();
        System.out.println("  enqueue 1, 2, 3  (뒤로 줄 섬)");
        q.enqueue(1); q.enqueue(2); q.enqueue(3);
        System.out.print("  dequeue 순서: ");
        while (!q.isEmpty()) System.out.print(q.dequeue() + " ");
        System.out.println("  ← 넣은 순서(1,2,3) 그대로 나온다 = FIFO");
        System.out.println("  → 스택(LIFO)과 큐(FIFO)는 '어느 쪽에서 빼느냐'만 다르다.");
        System.out.println("    큐는 그래프 BFS의 핵심 도구다(GraphAlgorithms.java 참고).\n");
    }

    static void conclusion() {
        System.out.println("── 정리 ──");
        System.out.println("  동적 배열 : 인덱스 접근 O(1), 끝 추가 평균 O(1), 중간 삽입/삭제 O(n).  (=ArrayList)");
        System.out.println("  연결 리스트: 앞/끝 삽입·삭제 O(1), 인덱스 접근 O(n).                (=LinkedList)");
        System.out.println("  스택(LIFO)/큐(FIFO): push·pop / enqueue·dequeue 모두 O(1).");
        System.out.println("  → 트리(BST)와 힙은 TreeAndHeap.java 에서 이어서 만든다.");
        System.out.println();
    }
}
