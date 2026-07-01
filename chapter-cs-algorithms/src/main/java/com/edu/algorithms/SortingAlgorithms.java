package com.edu.algorithms;

import java.util.Arrays;
import java.util.Random;

/**
 * CS 알고리즘 트랙 - 정렬(Sorting): O(n^2) 삼형제 vs O(n log n) 형제
 *
 * 정렬은 CS의 "Hello World"다. 왜 다섯 가지나 배우는가?
 *   - 같은 문제를 푸는 여러 방법의 "복잡도 차이"를 몸으로 느끼려고.
 *   - 안정성(stable), 제자리(in-place) 같은 개념은 여기서 처음 마주친다.
 *   - Java의 Arrays.sort / Collections.sort / stream.sorted 가
 *     내부에서 이 알고리즘들(TimSort=merge계열, Dual-Pivot Quick 등)을 쓴다.
 *
 * 다섯 알고리즘:
 *   1) 버블 정렬   (Bubble)    O(n^2)  - 옆끼리 비교/교환. 가장 느리지만 이해 쉬움.
 *   2) 선택 정렬   (Selection) O(n^2)  - 최솟값을 골라 앞으로. 교환 횟수 최소.
 *   3) 삽입 정렬   (Insertion) O(n^2)  - 카드 정렬처럼 제자리에 끼워 넣음. 거의 정렬된 배열에 강함.
 *   4) 병합 정렬   (Merge)     O(n log n) - 반으로 쪼개 정렬 후 합침. 안정 정렬, 추가 메모리 필요.
 *   5) 퀵 정렬     (Quick)     O(n log n) 평균 - 피벗 기준 분할. 제자리, 평균 최고속.
 *
 * 핵심 개념:
 *   - 안정성(stable): 값이 같은 원소들의 '원래 순서'가 보존되는가?
 *                    (버블/삽입/병합 = 안정, 선택/퀵 = 불안정)
 *   - 제자리(in-place): 입력 배열 외에 추가 메모리를 거의 안 쓰는가?
 *                    (버블/선택/삽입/퀵 = 제자리, 병합 = 추가 배열 필요)
 *
 * 왜 비교 기반 정렬은 O(n log n)보다 빠를 수 없는가?
 *   - n개를 나열하는 경우의 수는 n! 가지.
 *   - 비교 한 번은 최대 2갈래(예/아니오) → k번 비교로 구분 가능한 경우는 2^k.
 *   - 2^k >= n! 이어야 모든 순서를 구별 → k >= log2(n!) ≈ n log n.
 *   - 그래서 '비교만으로' 정렬하면 Ω(n log n)이 하한(더 빠를 수 없음).
 */
public class SortingAlgorithms {

    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println(" CS 알고리즘: 정렬 (O(n^2) vs O(n log n))");
        System.out.println("=================================================\n");

        correctnessCheck();
        stabilityDemo();
        benchmarkTable();
        conclusion();
    }

    // ──────────────────────────────────────────────
    // 0. 먼저 다섯 알고리즘이 "정확히 정렬하는지" 검증
    // ──────────────────────────────────────────────
    static void correctnessCheck() {
        System.out.println("── 0. 정확성 검증 (같은 입력을 다섯 방법으로 정렬) ──");
        int[] sample = {5, 2, 9, 1, 5, 6, 3, 8, 7, 4};
        System.out.println("  입력    : " + Arrays.toString(sample));

        System.out.println("  버블    : " + Arrays.toString(bubbleSort(sample.clone())));
        System.out.println("  선택    : " + Arrays.toString(selectionSort(sample.clone())));
        System.out.println("  삽입    : " + Arrays.toString(insertionSort(sample.clone())));
        System.out.println("  병합    : " + Arrays.toString(mergeSort(sample.clone())));
        int[] q = sample.clone();
        quickSort(q, 0, q.length - 1);
        System.out.println("  퀵      : " + Arrays.toString(q));
        System.out.println();
    }

    // ──────────────────────────────────────────────
    // 1. 버블 정렬 O(n^2), 안정(stable), 제자리
    //    인접한 두 원소를 비교해 크면 뒤로 보낸다.
    //    한 바퀴 돌면 가장 큰 값이 맨 뒤로 "떠오른다(bubble up)".
    // ──────────────────────────────────────────────
    static int[] bubbleSort(int[] a) {
        int n = a.length;
        for (int i = 0; i < n - 1; i++) {           // 바깥 루프 n번
            boolean swapped = false;
            for (int j = 0; j < n - 1 - i; j++) {   // 안쪽 루프 → 이중 반복 = O(n^2)
                if (a[j] > a[j + 1]) {              // '>'이므로 같으면 교환 안 함 → 안정
                    swap(a, j, j + 1);
                    swapped = true;
                }
            }
            if (!swapped) break; // 이미 정렬됐으면 조기 종료(거의 정렬된 입력에 유리)
        }
        return a;
    }

    // ──────────────────────────────────────────────
    // 2. 선택 정렬 O(n^2), 불안정(unstable), 제자리
    //    남은 부분에서 최솟값을 골라 맨 앞과 교환한다.
    //    비교는 O(n^2)이지만 교환은 최대 n-1번(교환 비용이 큰 경우 유리).
    // ──────────────────────────────────────────────
    static int[] selectionSort(int[] a) {
        int n = a.length;
        for (int i = 0; i < n - 1; i++) {
            int min = i;
            for (int j = i + 1; j < n; j++) {       // 최솟값 탐색 = O(n) → 전체 O(n^2)
                if (a[j] < a[min]) min = j;
            }
            swap(a, i, min); // 멀리 있는 원소를 끌어오므로 같은 값의 순서가 뒤바뀔 수 있음 → 불안정
        }
        return a;
    }

    // ──────────────────────────────────────────────
    // 3. 삽입 정렬 O(n^2), 안정(stable), 제자리
    //    손패 정렬처럼, 앞쪽 '정렬된 부분'에 현재 원소를 제자리에 끼워 넣는다.
    //    거의 정렬된 배열에서는 거의 O(n)에 가깝다(각 원소가 조금만 이동).
    // ──────────────────────────────────────────────
    static int[] insertionSort(int[] a) {
        for (int i = 1; i < a.length; i++) {
            int key = a[i];
            int j = i - 1;
            // key보다 큰 원소들을 오른쪽으로 밀며 자리를 만든다.
            // 'a[j] > key'이므로 같은 값은 밀지 않음 → 안정
            while (j >= 0 && a[j] > key) {
                a[j + 1] = a[j];
                j--;
            }
            a[j + 1] = key;
        }
        return a;
    }

    // ──────────────────────────────────────────────
    // 4. 병합 정렬 O(n log n), 안정(stable), 제자리 아님(추가 배열)
    //    분할정복: 반으로 쪼개 각각 정렬 → 두 정렬된 배열을 하나로 병합.
    //    쪼개는 깊이 = log n, 각 깊이에서 병합 비용 = O(n) → O(n log n).
    // ──────────────────────────────────────────────
    static int[] mergeSort(int[] a) {
        if (a.length <= 1) return a;                 // 기저 조건: 원소 1개는 이미 정렬됨
        int mid = a.length / 2;
        int[] left = mergeSort(Arrays.copyOfRange(a, 0, mid));   // 왼쪽 절반 정렬
        int[] right = mergeSort(Arrays.copyOfRange(a, mid, a.length)); // 오른쪽 절반 정렬
        return merge(left, right);                   // 두 정렬된 배열 병합
    }

    // 두 개의 '정렬된' 배열을 하나의 정렬된 배열로 합친다. O(n).
    static int[] merge(int[] left, int[] right) {
        int[] result = new int[left.length + right.length];
        int i = 0, j = 0, k = 0;
        while (i < left.length && j < right.length) {
            // '<=' 이므로 같은 값이면 왼쪽(원래 앞쪽) 것을 먼저 넣음 → 안정
            if (left[i] <= right[j]) result[k++] = left[i++];
            else result[k++] = right[j++];
        }
        while (i < left.length) result[k++] = left[i++];   // 남은 것 이어붙임
        while (j < right.length) result[k++] = right[j++];
        return result;
    }

    // ──────────────────────────────────────────────
    // 5. 퀵 정렬 O(n log n) 평균 / O(n^2) 최악, 불안정, 제자리
    //    피벗(pivot)을 하나 골라 "작은 것 왼쪽 / 큰 것 오른쪽"으로 분할(partition).
    //    피벗은 제자리를 찾고, 좌우를 재귀로 정렬한다.
    //    피벗이 매번 한쪽으로 치우치면(이미 정렬된 입력 등) 최악 O(n^2).
    // ──────────────────────────────────────────────
    static void quickSort(int[] a, int lo, int hi) {
        if (lo >= hi) return;                 // 기저 조건
        int p = partition(a, lo, hi);         // 피벗을 제자리로, 인덱스 반환
        quickSort(a, lo, p - 1);              // 피벗 왼쪽(작은 값들) 정렬
        quickSort(a, p + 1, hi);              // 피벗 오른쪽(큰 값들) 정렬
    }

    // Lomuto 분할: 맨 끝을 피벗으로. 피벗보다 작은 원소를 앞쪽으로 모은다.
    static int partition(int[] a, int lo, int hi) {
        int pivot = a[hi];
        int i = lo - 1;                       // '작은 값 영역'의 경계
        for (int j = lo; j < hi; j++) {
            if (a[j] < pivot) {               // 멀리 있는 원소를 끌어와 교환 → 불안정
                i++;
                swap(a, i, j);
            }
        }
        swap(a, i + 1, hi);                   // 피벗을 경계 바로 뒤(제자리)로
        return i + 1;
    }

    // ──────────────────────────────────────────────
    // 안정성(stability) 시연: (값, 원래순번) 쌍을 값 기준으로 정렬한 뒤
    // 같은 값 안에서 원래 순번이 유지되는지 확인
    // ──────────────────────────────────────────────
    static void stabilityDemo() {
        System.out.println("── 안정성(stable) 시연 ──");
        System.out.println("  같은 '값'을 가진 원소가 정렬 후에도 원래 순서를 지키면 '안정'.");
        System.out.println("  (값, 입력순번) 쌍을 값 기준으로만 정렬한다.\n");

        // 값은 같지만 입력 순번(a,b,c...)이 다른 원소들
        Item[] input = {
                new Item(3, 'a'), new Item(1, 'b'), new Item(3, 'c'),
                new Item(2, 'd'), new Item(1, 'e'), new Item(3, 'f')
        };
        System.out.println("  입력      : " + Arrays.toString(input));

        // 안정 정렬(삽입): 같은 값이면 원래 순서 유지 → 3은 a,c,f 순
        Item[] stable = input.clone();
        insertionSortItems(stable);
        System.out.println("  삽입(안정): " + Arrays.toString(stable) + "  ← 값이 같은 3은 a,c,f 순서 유지");

        // 불안정 정렬(선택): 같은 값의 순서가 뒤바뀔 수 있음
        Item[] unstable = input.clone();
        selectionSortItems(unstable);
        System.out.println("  선택(불안정): " + Arrays.toString(unstable) + "  ← 값이 같은 3의 순서가 뒤바뀔 수 있음");
        System.out.println("\n  → 실무 예: '이름'으로 정렬된 목록을 다시 '부서'로 정렬할 때,");
        System.out.println("    안정 정렬이면 같은 부서 안에서 이름 순서가 그대로 유지된다.\n");
    }

    // 안정성 시연용 (값, 순번표시) 쌍
    static class Item {
        int value; char tag;
        Item(int value, char tag) { this.value = value; this.tag = tag; }
        @Override public String toString() { return value + "" + tag; }
    }

    // Item 배열용 삽입 정렬(안정)
    static void insertionSortItems(Item[] a) {
        for (int i = 1; i < a.length; i++) {
            Item key = a[i];
            int j = i - 1;
            while (j >= 0 && a[j].value > key.value) { // '>' → 같으면 안 밀어 안정
                a[j + 1] = a[j];
                j--;
            }
            a[j + 1] = key;
        }
    }

    // Item 배열용 선택 정렬(불안정)
    static void selectionSortItems(Item[] a) {
        for (int i = 0; i < a.length - 1; i++) {
            int min = i;
            for (int j = i + 1; j < a.length; j++) {
                if (a[j].value < a[min].value) min = j;
            }
            Item t = a[i]; a[i] = a[min]; a[min] = t;
        }
    }

    // ──────────────────────────────────────────────
    // 벤치마크: 같은 입력을 크기별로 정렬하고 시간을 재서 표로 출력
    // ──────────────────────────────────────────────
    static void benchmarkTable() {
        System.out.println("── 벤치마크: 같은 랜덤 배열을 크기별로 정렬 (단위: ms) ──");
        System.out.println("   O(n^2)은 n이 커지면 급격히 느려지고, O(n log n)은 완만하다.\n");

        System.out.printf("  %-9s %10s %10s %10s %12s %10s%n",
                "N", "버블", "선택", "삽입", "병합", "퀵");
        System.out.println("  " + "-".repeat(66));

        for (int n : new int[]{1_000, 5_000, 20_000, 50_000}) {
            int[] base = randomArray(n, 12345);

            // O(n^2) 계열은 n이 크면 매우 느리므로 상한을 둔다.
            String bubble = (n <= 20_000) ? ms(() -> bubbleSort(base.clone())) : "(생략)";
            String selection = (n <= 20_000) ? ms(() -> selectionSort(base.clone())) : "(생략)";
            String insertion = (n <= 20_000) ? ms(() -> insertionSort(base.clone())) : "(생략)";
            String merge = ms(() -> mergeSort(base.clone()));
            String quick = ms(() -> { int[] c = base.clone(); quickSort(c, 0, c.length - 1); });

            System.out.printf("  %-9s %10s %10s %10s %12s %10s%n",
                    fmt(n), bubble, selection, insertion, merge, quick);
        }
        System.out.println("\n  → N을 2~4배 키우면 O(n^2)(버블/선택/삽입)은 시간이 4~16배로 폭증한다.");
        System.out.println("    병합/퀵(O(n log n))은 거의 비례(살짝 더)로만 늘어난다.");
        System.out.println("    Java의 Arrays.sort는 기본형은 Dual-Pivot 퀵, 객체는 TimSort(병합계열)를 쓴다.\n");
    }

    // ── 보조 메서드들 ──
    static void swap(int[] a, int i, int j) { int t = a[i]; a[i] = a[j]; a[j] = t; }

    static int[] randomArray(int n, long seed) {
        Random r = new Random(seed);
        int[] a = new int[n];
        for (int i = 0; i < n; i++) a[i] = r.nextInt(n * 10);
        return a;
    }

    // Runnable 실행 시간을 밀리초 문자열로 (여러 번 돌려 중앙값 성격의 안정치)
    static String ms(Runnable r) {
        long best = Long.MAX_VALUE;
        for (int rep = 0; rep < 3; rep++) {           // 3회 중 최소값(잡음 완화)
            long s = System.nanoTime();
            r.run();
            best = Math.min(best, System.nanoTime() - s);
        }
        return String.format("%.1f", best / 1_000_000.0);
    }

    static String fmt(long n) { return String.format("%,d", n); }

    static void conclusion() {
        System.out.println("── 정리 ──");
        System.out.println("  O(n^2)  : 버블/선택/삽입 — 이해 쉬움, 작은/거의정렬된 데이터엔 삽입이 실용적.");
        System.out.println("  O(nlogn): 병합(안정, 추가메모리) / 퀵(제자리, 평균 최고속, 최악 O(n^2)).");
        System.out.println("  안정성이 필요하면 병합, 메모리가 빠듯하고 평균속도가 중요하면 퀵.");
        System.out.println("  비교 기반 정렬의 하한은 Ω(n log n) — 비교만으로는 이보다 빠를 수 없다.");
        System.out.println();
    }
}
