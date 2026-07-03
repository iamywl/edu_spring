package com.edu.algorithms;

import java.util.Random;

/**
 * CS 알고리즘 트랙 - 탐색(Search): 선형 탐색 O(n) vs 이진 탐색 O(log n)
 *
 * "값을 찾는다"는 가장 기본적인 연산이지만, 방법에 따라 성능이 하늘과 땅 차이다.
 *
 * 왜 이걸 배우는가?
 *   - 이진 탐색은 "정렬되어 있다"는 전제 하나로 O(n)을 O(log n)으로 줄인다.
 *   - 이 "절반씩 줄이기(분할)" 아이디어는 정렬(merge/quick), 트리, 그래프까지 이어진다.
 *   - Java의 Collections.binarySearch / Arrays.binarySearch / TreeMap이 모두 이 원리다.
 *
 * 이 파일은 "말"이 아니라 "비교 횟수"를 직접 세서 보여준다.
 *   - 선형 탐색: 최악의 경우 n번 비교          → O(n)
 *   - 이진 탐색: 매번 후보가 절반 → log2(n)번  → O(log n)
 *
 *   n=1,000,000 일 때:
 *     선형탐색 최악 = 1,000,000번
 *     이진탐색 최악 = log2(1,000,000) ≈ 20번
 */
public class SearchAlgorithms {

    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println(" CS 알고리즘: 탐색 (선형 O(n) vs 이진 O(log n))");
        System.out.println("=================================================\n");

        explainWithSmallExample();
        countComparisonsAcrossSizes();
        showSortedPrecondition();
        conclusion();
    }

    // ──────────────────────────────────────────────
    // 1. 작은 예제로 "어떻게" 동작하는지 눈으로 본다
    // ──────────────────────────────────────────────
    static void explainWithSmallExample() {
        System.out.println("── 1. 작은 배열에서 동작 관찰 ──");
        int[] arr = {2, 5, 8, 12, 16, 23, 38, 56, 72, 91}; // 정렬된 상태(이진 탐색의 전제)
        int target = 23;

        System.out.println("  배열: [2, 5, 8, 12, 16, 23, 38, 56, 72, 91]  (정렬됨)");
        System.out.println("  찾는 값(target): " + target + "\n");

        System.out.println("  [선형 탐색] 앞에서부터 하나씩 비교:");
        int[] linear = linearSearchVerbose(arr, target);
        System.out.println("    → 인덱스 " + linear[0] + "에서 발견, 비교 " + linear[1] + "번\n");

        System.out.println("  [이진 탐색] 매번 가운데와 비교하고 절반을 버림:");
        int[] binary = binarySearchVerbose(arr, target);
        System.out.println("    → 인덱스 " + binary[0] + "에서 발견, 비교 " + binary[1] + "번\n");
    }

    // 선형 탐색: 인덱스 0부터 끝까지 순서대로 비교한다.
    // 지배 연산 = 비교. 최악(맨 끝 or 없음)에는 n번 → O(n).
    // 반환: {찾은 인덱스(-1이면 없음), 비교 횟수}
    static int[] linearSearchVerbose(int[] arr, int target) {
        int comparisons = 0;
        for (int i = 0; i < arr.length; i++) {
            comparisons++;
            System.out.printf("      비교 %d: arr[%d]=%d %s %d%n",
                    comparisons, i, arr[i],
                    (arr[i] == target ? "==" : "!="), target);
            if (arr[i] == target) return new int[]{i, comparisons};
        }
        return new int[]{-1, comparisons};
    }

    // 이진 탐색: [lo, hi] 범위의 가운데(mid)를 target과 비교.
    //   - 같으면 끝.
    //   - target이 더 크면 오른쪽 절반만 남긴다(lo = mid+1).
    //   - target이 더 작으면 왼쪽 절반만 남긴다(hi = mid-1).
    // 매 단계 후보가 절반 → 최대 log2(n)번 → O(log n).
    // 전제: 배열이 반드시 정렬되어 있어야 한다!
    static int[] binarySearchVerbose(int[] arr, int target) {
        int lo = 0, hi = arr.length - 1, comparisons = 0;
        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2; // (lo+hi)/2 대신 오버플로 방지형
            comparisons++;
            System.out.printf("      비교 %d: 범위[%d..%d], 가운데 arr[%d]=%d, target=%d → ",
                    comparisons, lo, hi, mid, arr[mid], target);
            if (arr[mid] == target) {
                System.out.println("일치!");
                return new int[]{mid, comparisons};
            } else if (arr[mid] < target) {
                System.out.println("작다 → 오른쪽 절반으로");
                lo = mid + 1;
            } else {
                System.out.println("크다 → 왼쪽 절반으로");
                hi = mid - 1;
            }
        }
        return new int[]{-1, comparisons};
    }

    // ──────────────────────────────────────────────
    // 2. n을 키우며 최악 비교 횟수를 세서 표로 출력
    //    (없는 값을 찾음 = 최악 케이스)
    // ──────────────────────────────────────────────
    static void countComparisonsAcrossSizes() {
        System.out.println("── 2. n을 10배씩 키우며 '최악' 비교 횟수 측정 ──");
        System.out.println("   (배열에 없는 값을 찾아 끝까지 탐색하게 만든다)\n");

        System.out.printf("  %-14s %18s %18s%n", "N", "선형탐색(비교)", "이진탐색(비교)");
        System.out.println("  " + "-".repeat(52));

        for (int n : new int[]{100, 1_000, 10_000, 100_000, 1_000_000}) {
            int[] arr = new int[n];
            for (int i = 0; i < n; i++) arr[i] = i * 2; // 0,2,4,... 정렬 & 홀수는 존재 안 함
            int target = -1;                            // 없는 값 → 최악

            long linearCmp = linearSearchCount(arr, target);
            long binaryCmp = binarySearchCount(arr, target);

            System.out.printf("  %-14s %18s %18s%n", fmt(n), fmt(linearCmp), fmt(binaryCmp));
        }
        System.out.println("\n  → N을 10배 키우면 선형은 비교도 약 10배(비례). ");
        System.out.println("    이진은 N이 10배가 되어도 비교가 겨우 +3~4번씩만 늘어난다(log 스케일).");
        System.out.println("    log2(100만) ≈ 20 이므로 이진은 20번대에서 멈춘다.\n");
    }

    // 비교 횟수만 세는 선형 탐색 (출력 없음)
    static long linearSearchCount(int[] arr, int target) {
        long c = 0;
        for (int i = 0; i < arr.length; i++) {
            c++;
            if (arr[i] == target) return c;
        }
        return c;
    }

    // 비교 횟수만 세는 이진 탐색 (출력 없음)
    static long binarySearchCount(int[] arr, int target) {
        long c = 0;
        int lo = 0, hi = arr.length - 1;
        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;
            c++;
            if (arr[mid] == target) return c;
            else if (arr[mid] < target) lo = mid + 1;
            else hi = mid - 1;
        }
        return c;
    }

    // ──────────────────────────────────────────────
    // 3. "정렬 전제"를 깨면 이진 탐색이 왜 틀리는가
    // ──────────────────────────────────────────────
    static void showSortedPrecondition() {
        System.out.println("── 3. 이진 탐색의 대전제: '정렬되어 있어야 한다' ──");
        int[] unsorted = {38, 2, 91, 16, 5, 72, 8, 56, 23, 12};
        Random rnd = new Random(42);
        // 실제로 존재하는 값을 하나 골라 정렬 안 된 배열에 이진 탐색을 시도해 본다.
        int target = unsorted[rnd.nextInt(unsorted.length)];

        System.out.println("  정렬 안 된 배열: [38, 2, 91, 16, 5, 72, 8, 56, 23, 12]");
        System.out.println("  이 배열에 '분명히 존재하는' 값 " + target + " 을 이진 탐색해 본다.");

        int[] result = binarySearchVerboseQuiet(unsorted, target);
        boolean found = result[0] != -1;
        System.out.println("  → 이진 탐색 결과: " + (found ? "찾음(운 좋게)" : "못 찾음(FAIL)"));
        System.out.println("  → 정렬 안 된 배열에서는 '절반 버리기' 판단이 근거를 잃어 결과가 틀릴 수 있다.");
        System.out.println("    그래서 이진 탐색 전에는 반드시 정렬(O(n log n))이 선행되어야 한다.");
        System.out.println("    한 번 찾을 거면 선형탐색(O(n))이 낫고, 여러 번 찾을 거면 정렬 후 이진탐색이 이득.\n");
    }

    static int[] binarySearchVerboseQuiet(int[] arr, int target) {
        int lo = 0, hi = arr.length - 1, c = 0;
        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;
            c++;
            if (arr[mid] == target) return new int[]{mid, c};
            else if (arr[mid] < target) lo = mid + 1;
            else hi = mid - 1;
        }
        return new int[]{-1, c};
    }

    static void conclusion() {
        System.out.println("── 정리 ──");
        System.out.println("  선형 탐색 O(n)   : 아무 배열에서나 가능, 하지만 크기에 비례해 느려짐.");
        System.out.println("  이진 탐색 O(log n): '정렬'이라는 전제가 필요하지만 엄청나게 빠름.");
        System.out.println("  Java 연결고리: Arrays.binarySearch, Collections.binarySearch,");
        System.out.println("               TreeMap/TreeSet(정렬 트리)이 모두 이 '분할' 원리를 쓴다.");
        System.out.println();
    }

    // 숫자를 1,000,000 형태로
    static String fmt(long n) {
        return String.format("%,d", n);
    }
}
