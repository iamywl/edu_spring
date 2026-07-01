package com.edu.algorithms;

import java.util.ArrayList;
import java.util.List;

/**
 * CS 알고리즘 트랙 - 이진 탐색 트리(BST)와 최소 힙(MinHeap) 직접 구현
 *
 * 우리는 TreeMap, TreeSet, PriorityQueue를 "써봤다". 그 안에 뭐가 있을까?
 *   - TreeMap/TreeSet 의 바닥 = 균형 이진 탐색 트리(레드-블랙 트리).
 *   - PriorityQueue 의 바닥 = 이진 힙(binary heap).
 * 여기서는 그 원형을 직접 만들어, 왜 O(log n)이 나오고 언제 무너지는지 본다.
 *
 * 1) 이진 탐색 트리(BST):
 *    "왼쪽 < 부모 < 오른쪽" 규칙으로 값을 정리한 트리.
 *    - 탐색/삽입: O(트리 높이). 균형 잡히면 높이 ≈ log n → O(log n).
 *    - 함정(퇴화, degeneration): 정렬된 값을 순서대로 넣으면 한쪽으로만 자라
 *      '기울어진 사슬(=연결 리스트)'이 되어 높이 = n → O(n)으로 퇴화한다.
 *      (그래서 실무의 TreeMap은 스스로 균형을 맞추는 레드-블랙 트리를 쓴다.)
 *    - 중위 순회(inorder)를 하면 값이 '정렬된 순서'로 나온다.
 *
 * 2) 최소 힙(MinHeap) = 우선순위 큐(PriorityQueue):
 *    "부모 <= 자식" 규칙만 지키는 '완전 이진 트리'. 배열 하나로 표현한다.
 *    - 최솟값(루트) 조회: O(1)
 *    - 삽입/삭제(최솟값): O(log n) (한 층씩 올리거나 내리는 sift 연산)
 *    - 힙으로 정렬하면 힙 정렬(heap sort) O(n log n).
 */
public class TreeAndHeap {

    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println(" CS 알고리즘: 이진 탐색 트리(BST) & 최소 힙");
        System.out.println("=================================================\n");

        bstDemo();
        bstDegenerationDemo();
        heapDemo();
        heapSortDemo();
        conclusion();
    }

    // ══════════════════════════════════════════════
    // 1. 이진 탐색 트리 (Binary Search Tree)
    // ══════════════════════════════════════════════
    static class BST {
        // 노드: 값 + 왼쪽/오른쪽 자식
        private static class Node {
            int value;
            Node left, right;
            Node(int value) { this.value = value; }
        }

        private Node root;

        // 삽입: 규칙(왼 < 부모 < 오)에 따라 내려갈 곳을 정한다. O(높이)
        void insert(int value) { root = insert(root, value); }
        private Node insert(Node node, int value) {
            if (node == null) return new Node(value);      // 빈 자리 = 여기에 새 노드
            if (value < node.value) node.left = insert(node.left, value);   // 작으면 왼쪽
            else if (value > node.value) node.right = insert(node.right, value); // 크면 오른쪽
            // 같으면 중복 → 무시(집합 의미). TreeSet도 중복을 허용하지 않는다.
            return node;
        }

        // 탐색: 값과 비교해 왼/오 한쪽으로만 내려간다. 매 단계 후보 절반 → O(높이)
        boolean contains(int value) {
            Node cur = root;
            while (cur != null) {
                if (value == cur.value) return true;
                cur = (value < cur.value) ? cur.left : cur.right; // 이진탐색과 똑같은 원리!
            }
            return false;
        }

        // 중위 순회(inorder): 왼쪽 → 자기 → 오른쪽. BST를 이렇게 돌면 '정렬 순서'가 나온다.
        List<Integer> inorder() {
            List<Integer> out = new ArrayList<>();
            inorder(root, out);
            return out;
        }
        private void inorder(Node node, List<Integer> out) {
            if (node == null) return;
            inorder(node.left, out);   // 먼저 왼쪽(더 작은 값들)
            out.add(node.value);       // 그 다음 자기 자신
            inorder(node.right, out);  // 마지막 오른쪽(더 큰 값들)
        }

        // 트리 높이 = 가장 깊은 경로의 노드 수. 성능을 좌우하는 값.
        int height() { return height(root); }
        private int height(Node node) {
            if (node == null) return 0;
            return 1 + Math.max(height(node.left), height(node.right));
        }
    }

    static void bstDemo() {
        System.out.println("── 1. 이진 탐색 트리 (균형 잡힌 경우) ──");
        BST tree = new BST();
        int[] values = {50, 30, 70, 20, 40, 60, 80};   // 골고루 섞여 균형에 가깝게 들어감
        for (int v : values) tree.insert(v);

        System.out.println("  삽입 순서: 50, 30, 70, 20, 40, 60, 80");
        System.out.println("  트리 모양:");
        System.out.println("            50");
        System.out.println("          /    \\");
        System.out.println("        30      70");
        System.out.println("       /  \\    /  \\");
        System.out.println("      20  40  60  80");
        System.out.println("  높이 = " + tree.height() + " (원소 7개, log2(7)≈2.8 → 균형이면 이 정도)");
        System.out.println("  contains(60) = " + tree.contains(60) + "  (50→70→60, 3번만에 도달)");
        System.out.println("  contains(45) = " + tree.contains(45));
        System.out.println("  중위 순회(inorder) = " + tree.inorder() + "  ← 자동으로 정렬됨!");
        System.out.println();
    }

    // BST 퇴화: 정렬된 값을 넣으면 높이가 n이 되어 O(n)으로 망가진다.
    static void bstDegenerationDemo() {
        System.out.println("── 2. BST의 함정: '퇴화(degeneration)' ──");
        System.out.println("  정렬된 값 1,2,3,...을 순서대로 넣으면 한쪽으로만 자란다:");
        System.out.println("      1");
        System.out.println("       \\");
        System.out.println("        2");
        System.out.println("         \\");
        System.out.println("          3   ...  (사실상 연결 리스트)");

        System.out.printf("%n  %-10s %14s %18s%n", "N", "균형BST 높이", "퇴화BST 높이(정렬입력)");
        System.out.println("  " + "-".repeat(46));
        for (int n : new int[]{7, 15, 31, 63}) {
            // 퇴화: 1..n 오름차순 삽입 → 높이 = n
            BST bad = new BST();
            for (int i = 1; i <= n; i++) bad.insert(i);
            // 균형에 가깝게: 값의 '가운데'부터 넣으면 높이 ≈ log n
            BST good = new BST();
            insertBalanced(good, 1, n);
            System.out.printf("  %-10d %14d %18d%n", n, good.height(), bad.height());
        }
        System.out.println("\n  → 퇴화하면 높이=N이라 탐색이 O(N)! 균형이면 높이≈log2(N).");
        System.out.println("    이래서 java.util.TreeMap/TreeSet은 스스로 균형을 맞추는");
        System.out.println("    '레드-블랙 트리'를 써서 항상 O(log n)을 보장한다.\n");
    }

    // [lo..hi]의 가운데 값부터 넣어 균형 트리를 만든다(시연용).
    static void insertBalanced(BST tree, int lo, int hi) {
        if (lo > hi) return;
        int mid = (lo + hi) / 2;
        tree.insert(mid);
        insertBalanced(tree, lo, mid - 1);
        insertBalanced(tree, mid + 1, hi);
    }

    // ══════════════════════════════════════════════
    // 3. 최소 힙 (MinHeap) = 우선순위 큐
    //    완전 이진 트리를 '배열'로 표현한다(0-index 기준):
    //      부모(i)의 자식 = 2i+1, 2i+2 / 자식(i)의 부모 = (i-1)/2
    //    규칙: 부모 <= 두 자식. 따라서 루트(arr[0])가 항상 최솟값.
    // ══════════════════════════════════════════════
    static class MinHeap {
        private int[] heap;
        private int size;

        MinHeap() { heap = new int[4]; size = 0; }

        int size() { return size; }
        int peekMin() {                            // O(1): 루트가 최솟값
            if (size == 0) throw new IllegalStateException("힙이 비어 있음");
            return heap[0];
        }

        // 삽입: 맨 끝에 넣고, 부모보다 작으면 위로 끌어올린다(sift-up). O(log n)
        void add(int value) {
            if (size == heap.length) grow();
            heap[size] = value;
            siftUp(size);
            size++;
        }

        private void siftUp(int i) {
            while (i > 0) {
                int parent = (i - 1) / 2;
                if (heap[i] >= heap[parent]) break; // 부모보다 크거나 같으면 제자리
                swap(i, parent);                    // 부모보다 작으면 위로 교환
                i = parent;
            }
        }

        // 최솟값 추출: 루트를 꺼내고, 맨 끝 원소를 루트로 옮긴 뒤 아래로 내린다(sift-down). O(log n)
        int pollMin() {
            if (size == 0) throw new IllegalStateException("힙이 비어 있음");
            int min = heap[0];
            heap[0] = heap[size - 1];   // 마지막 원소를 루트로
            size--;
            siftDown(0);
            return min;
        }

        private void siftDown(int i) {
            while (true) {
                int left = 2 * i + 1, right = 2 * i + 2, smallest = i;
                if (left < size && heap[left] < heap[smallest]) smallest = left;
                if (right < size && heap[right] < heap[smallest]) smallest = right;
                if (smallest == i) break;           // 두 자식보다 작으면 제자리
                swap(i, smallest);                  // 더 작은 자식과 교환하고 계속 내려감
                i = smallest;
            }
        }

        private void grow() {
            int[] bigger = new int[heap.length * 2];
            for (int i = 0; i < size; i++) bigger[i] = heap[i];
            heap = bigger;
        }

        private void swap(int a, int b) { int t = heap[a]; heap[a] = heap[b]; heap[b] = t; }
    }

    static void heapDemo() {
        System.out.println("── 3. 최소 힙 (PriorityQueue의 뼈대) ──");
        MinHeap heap = new MinHeap();
        int[] vals = {5, 3, 8, 1, 9, 2, 7};
        System.out.println("  add 순서: 5, 3, 8, 1, 9, 2, 7 (뒤죽박죽)");
        for (int v : vals) heap.add(v);
        System.out.println("  peekMin() = " + heap.peekMin() + "  ← 넣은 순서와 무관하게 항상 최솟값");
        System.out.print("  pollMin 반복 → ");
        StringBuilder sb = new StringBuilder();
        while (heap.size() > 0) sb.append(heap.pollMin()).append(" ");
        System.out.println(sb.toString().trim() + "   ← 오름차순으로 쏟아진다");
        System.out.println("  → 매 pollMin은 O(log n). 최솟값만 빠르게 꺼내야 할 때 최적(작업 스케줄러, 다익스트라).\n");
    }

    // 힙 정렬: n개를 힙에 넣고(add) 하나씩 최솟값을 빼면 정렬된다. O(n log n).
    static void heapSortDemo() {
        System.out.println("── 4. 힙 정렬 (heap sort) ──");
        int[] input = {9, 4, 7, 1, 8, 3, 6, 2, 5};
        System.out.println("  입력: " + java.util.Arrays.toString(input));
        MinHeap heap = new MinHeap();
        for (int v : input) heap.add(v);            // n번 add → O(n log n)
        int[] sorted = new int[input.length];
        for (int i = 0; i < sorted.length; i++) sorted[i] = heap.pollMin(); // n번 poll → O(n log n)
        System.out.println("  힙정렬: " + java.util.Arrays.toString(sorted));
        System.out.println("  → add n번 + pollMin n번 = O(n log n). 안정 정렬은 아니지만 제자리 변형도 가능.\n");
    }

    static void conclusion() {
        System.out.println("── 정리 ──");
        System.out.println("  BST : 왼<부모<오. 균형이면 탐색/삽입 O(log n), 퇴화하면 O(n).");
        System.out.println("        중위 순회하면 정렬 순서. → TreeMap/TreeSet(균형 = 레드블랙트리)의 원리.");
        System.out.println("  MinHeap: 완전이진트리(배열). 최솟값 O(1) 조회, 삽입/삭제 O(log n).");
        System.out.println("        → PriorityQueue의 원리, 힙정렬 O(n log n)의 기반.");
        System.out.println();
    }
}
