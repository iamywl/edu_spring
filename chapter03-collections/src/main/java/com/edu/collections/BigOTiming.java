package com.edu.collections;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Chapter 03 (CS 심화) - Big-O를 "측정"해서 눈으로 보기
 *
 * 시간복잡도(Big-O)는 추상적인 이론처럼 보이지만, 실제로 시간을 재보면
 * O(n)과 O(log n)의 곡선이 갈라지는 것을 직접 관찰할 수 있습니다.
 *
 * Big-O를 구하는 법(주석에서 반복 설명):
 *   - "입력 n이 커질 때 지배적인 연산이 몇 번 실행되는가"를 센다.
 *   - 상수와 낮은 차수 항은 버린다. (예: 3n + 5 → O(n))
 *   - 반복문이 n번 돌면 O(n), 매번 절반씩 줄면 O(log n),
 *     이중 반복문이면 O(n^2).
 *
 * 주의: JIT 워밍업/GC 때문에 미세한 시간은 흔들립니다. 절대값이 아니라
 *      "n이 10배 커질 때 시간이 어떻게 늘어나는가"의 경향을 보세요.
 */
public class BigOTiming {

    public static void main(String[] args) {
        System.out.println("====================================");
        System.out.println(" Chapter 03 심화: Big-O 측정하기");
        System.out.println("====================================\n");

        warmUp();
        linearVsBinarySearch();
        arrayListVsLinkedListGet();
        addFrontVsAddEnd();
        conclusion();
    }

    // ──────────────────────────────────────────────
    // 워밍업: JIT 컴파일러가 코드를 최적화하도록 미리 몇 번 실행
    // ──────────────────────────────────────────────
    static void warmUp() {
        long sink = 0;
        int[] arr = new int[10_000];
        for (int i = 0; i < arr.length; i++) arr[i] = i;
        for (int r = 0; r < 5_000; r++) {
            sink += linearSearch(arr, -1);            // 항상 못 찾음(최악 케이스)
            sink += Arrays.binarySearch(arr, r % 10000);
        }
        // sink를 사용해 최적화로 코드가 통째로 제거되는 것 방지
        if (sink == Long.MIN_VALUE) System.out.println("(unreachable)");
    }

    // ──────────────────────────────────────────────
    // 1. 선형 탐색 O(n) vs 이진 탐색 O(log n)
    // ──────────────────────────────────────────────
    static void linearVsBinarySearch() {
        System.out.println("── 1. 선형 탐색 O(n) vs 이진 탐색 O(log n) ──");
        System.out.println("  (정렬된 배열에서 '없는 값'을 찾음 = 최악 케이스)\n");

        System.out.printf("  %-12s %18s %18s %12s%n", "N", "선형탐색(ns)", "이진탐색(ns)", "배율(선형/이진)");
        System.out.println("  " + "-".repeat(64));

        for (int n : new int[]{10_000, 100_000, 1_000_000}) {
            int[] arr = new int[n];
            for (int i = 0; i < n; i++) arr[i] = i;   // 0,1,2,... 정렬 상태
            int target = -1;                           // 없는 값 → 끝까지 탐색

            // 선형 탐색: 최악의 경우 n번 비교 → O(n)
            long t1 = time(() -> { linearSearch(arr, target); });
            // 이진 탐색: 매 단계 후보를 절반으로 → log2(n)번 → O(log n)
            long t2 = time(() -> { Arrays.binarySearch(arr, target); });

            System.out.printf("  %-12s %18d %18d %12s%n",
                    fmt(n), t1, t2, (t2 == 0 ? "-" : (t1 / t2) + "x"));
        }
        System.out.println("\n  → N이 10배 커지면 선형탐색 시간도 약 10배. 이진탐색은 거의 일정.");
        System.out.println("    N=100만이면 선형은 최대 100만번, 이진은 약 20번(log2(1e6)≈20) 비교.\n");
    }

    // 선형 탐색: 앞에서부터 하나씩 비교 (지배 연산 = 비교, 최대 n번 → O(n))
    static long linearSearch(int[] arr, int target) {
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == target) return i;
        }
        return -1;
    }

    // ──────────────────────────────────────────────
    // 2. ArrayList.get O(1) vs LinkedList.get O(n)
    // ──────────────────────────────────────────────
    static void arrayListVsLinkedListGet() {
        System.out.println("── 2. ArrayList.get(mid) O(1) vs LinkedList.get(mid) O(n) ──");
        System.out.println("  (가운데 인덱스를 반복 조회)\n");

        System.out.printf("  %-12s %18s %18s %12s%n", "N", "ArrayList(ns)", "LinkedList(ns)", "배율");
        System.out.println("  " + "-".repeat(64));

        for (int n : new int[]{10_000, 50_000, 200_000}) {
            List<Integer> arrayList = new ArrayList<>();
            List<Integer> linkedList = new LinkedList<>();
            for (int i = 0; i < n; i++) { arrayList.add(i); linkedList.add(i); }
            final int mid = n / 2;
            final int reps = 2_000;

            // ArrayList: 내부가 배열이라 인덱스로 즉시 접근 → O(1)
            long t1 = time(() -> {
                long s = 0;
                for (int r = 0; r < reps; r++) s += arrayList.get(mid);
                if (s < 0) System.out.print("");
            });
            // LinkedList: head부터 mid까지 노드를 따라가야 함 → O(n)
            long t2 = time(() -> {
                long s = 0;
                for (int r = 0; r < reps; r++) s += linkedList.get(mid);
                if (s < 0) System.out.print("");
            });

            System.out.printf("  %-12s %18d %18d %12s%n",
                    fmt(n), t1, t2, (t1 == 0 ? "-" : (t2 / Math.max(t1, 1)) + "x"));
        }
        System.out.println("\n  → ArrayList는 N과 무관하게 일정. LinkedList는 N에 비례해 느려짐.");
        System.out.println("    LinkedList는 '순차 순회'엔 좋지만 '인덱스 랜덤 접근'엔 최악.\n");
    }

    // ──────────────────────────────────────────────
    // 3. ArrayList 맨 앞 삽입 O(n) vs 맨 뒤 삽입 O(1) 평균
    // ──────────────────────────────────────────────
    static void addFrontVsAddEnd() {
        System.out.println("── 3. ArrayList 맨 앞 삽입 O(n) vs 맨 뒤 삽입 O(1)* ──");
        System.out.println("  (* 뒤 삽입은 가끔 배열 확장 비용이 있지만 평균 O(1) = 분할상환)\n");

        System.out.printf("  %-12s %18s %18s %12s%n", "삽입 횟수", "맨앞 add(ns)", "맨뒤 add(ns)", "배율");
        System.out.println("  " + "-".repeat(64));

        for (int n : new int[]{10_000, 20_000, 40_000}) {
            final int count = n;
            // 맨 앞 삽입: 매번 뒤 원소를 전부 한 칸씩 밀어야 함 → i번째 삽입에 O(i) → 전체 O(n^2)
            long t1 = time(() -> {
                List<Integer> list = new ArrayList<>();
                for (int i = 0; i < count; i++) list.add(0, i);
            });
            // 맨 뒤 삽입: 그냥 끝에 붙임 → 평균 O(1), 전체 O(n)
            long t2 = time(() -> {
                List<Integer> list = new ArrayList<>();
                for (int i = 0; i < count; i++) list.add(i);
            });

            System.out.printf("  %-12s %18d %18d %12s%n",
                    fmt(n), t1, t2, (t2 == 0 ? "-" : (t1 / Math.max(t2, 1)) + "x"));
        }
        System.out.println("\n  → 삽입 횟수를 2배로 늘리면 '맨 앞'은 약 4배(n^2), '맨 뒤'는 약 2배(n).");
        System.out.println("    맨 앞에 자주 넣어야 하면 ArrayDeque나 LinkedList를 고려하세요.\n");
    }

    static void conclusion() {
        System.out.println("── 정리 ──");
        System.out.println("  Big-O는 'N이 커질 때 시간이 어떻게 증가하는가'를 나타냄.");
        System.out.println("  O(1): 그대로 / O(log n): 아주 천천히 / O(n): 비례 / O(n^2): 폭발적 증가.");
        System.out.println("  자료구조 선택 = 어떤 연산을 자주 하느냐에 따라 Big-O가 갈린다.");
        System.out.println();
    }

    // ── 시간 측정 보조: 실행 시간을 나노초로 반환 ──
    static long time(Runnable r) {
        long start = System.nanoTime();
        r.run();
        return System.nanoTime() - start;
    }

    // 숫자를 1,000,000 형태로 (자리마다 콤마)
    static String fmt(int n) {
        return String.format("%,d", n);
    }
}
