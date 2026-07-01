package com.edu.algorithms;

import java.util.Arrays;

/**
 * CS 알고리즘 트랙 - 재귀(Recursion)와 동적 계획법(DP)
 *
 * 재귀란 "함수가 자기 자신을 호출하는 것"이다. 왜 위험하고 왜 강력한가?
 *
 *   - 강력함: 큰 문제를 '같은 모양의 작은 문제'로 쪼갤 수 있으면 코드가 놀랍도록 간결해진다.
 *   - 위험함: 호출마다 스택 프레임이 쌓인다. 기저 조건(base case)이 없거나 너무 깊으면
 *            StackOverflowError로 프로그램이 죽는다.
 *
 * 재귀 → DP로 가는 길:
 *   피보나치의 '순진한 재귀'는 같은 값을 지수적으로 여러 번 다시 계산한다(O(2^n)).
 *   이 '겹치는 부분 문제(overlapping subproblems)'를 저장(메모)해서 재사용하면
 *   O(n)으로 줄어든다. 이것이 동적 계획법(Dynamic Programming, DP)이다.
 *     - 메모이제이션(memoization): 위→아래(top-down), 재귀 + 캐시.
 *     - 타뷸레이션(tabulation)   : 아래→위(bottom-up), 반복문 + 표 채우기.
 *
 * 이 파일은 "호출 횟수"와 "시간"을 직접 세서 O(2^n)과 O(n)의 차이를 보여준다.
 * 마지막으로 0/1 배낭(knapsack) DP로 "그리디로는 안 되는" 최적화 문제를 푼다.
 */
public class RecursionAndDP {

    // 순진한 피보나치의 호출 횟수를 세기 위한 카운터
    static long naiveCalls = 0;

    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println(" CS 알고리즘: 재귀와 동적 계획법(DP)");
        System.out.println("=================================================\n");

        factorialDemo();
        stackOverflowNote();
        fibonacciComparison();
        knapsackDemo();
        greedyVsDpNote();
        conclusion();
    }

    // ──────────────────────────────────────────────
    // 1. 팩토리얼: 가장 단순한 재귀 (n! = n * (n-1)!)
    // ──────────────────────────────────────────────
    static void factorialDemo() {
        System.out.println("── 1. 팩토리얼: 재귀의 기본 구조 ──");
        System.out.println("  정의: n! = n * (n-1)!,  0! = 1 (기저 조건)");
        System.out.println("  5! 계산 과정(호출이 쌓였다 풀린다):");
        System.out.println("    factorial(5)");
        System.out.println("     = 5 * factorial(4)");
        System.out.println("     = 5 * (4 * factorial(3))");
        System.out.println("     = ... = 5*4*3*2*1 = " + factorial(5));
        System.out.println("  → 기저 조건(0!)에 닿으면 되돌아오며 곱이 완성된다.\n");
    }

    // 재귀 함수는 반드시 '기저 조건'(더 이상 자기 호출을 안 하는 경우)이 있어야 한다.
    static long factorial(int n) {
        if (n <= 1) return 1;         // 기저 조건: 재귀의 바닥
        return n * factorial(n - 1);  // 재귀 호출: 자기보다 작은 문제로
    }

    // ──────────────────────────────────────────────
    // 2. 재귀의 위험: 콜 스택과 StackOverflowError
    // ──────────────────────────────────────────────
    static void stackOverflowNote() {
        System.out.println("── 2. 재귀의 함정: 콜 스택 깊이 ──");
        System.out.println("  호출마다 스택 프레임이 쌓인다. 너무 깊으면 StackOverflowError.");
        int depth = measureStackDepth();
        System.out.println("  이 JVM에서 (단순 재귀 기준) 대략 " + fmt(depth) + " 깊이 근처에서 스택이 넘쳤다.");
        System.out.println("  → 아주 깊은 재귀는 반복문/명시적 스택으로 바꾸거나, 꼬리재귀를 반복문화한다.\n");
    }

    // 실제로 재귀를 계속 깊게 들어가 StackOverflowError가 날 때까지의 깊이를 잰다.
    // 필드에 현재 깊이를 기록해 두므로, 예외로 풀려나온 뒤에도 '최대 도달 깊이'를 알 수 있다.
    static int reachedDepth = 0;
    static int measureStackDepth() {
        reachedDepth = 0;
        try {
            recurseDeep(1);
        } catch (StackOverflowError e) {
            // 스택이 넘치면 여기로 온다. reachedDepth에 마지막 깊이가 남아 있다.
        }
        return reachedDepth;
    }

    static void recurseDeep(int d) {
        reachedDepth = d;           // 넘치기 직전까지 도달한 깊이를 계속 갱신
        recurseDeep(d + 1);         // 기저 조건이 없으므로 StackOverflowError로 멈춘다
    }

    // ──────────────────────────────────────────────
    // 3. 피보나치: 순진한 재귀 O(2^n) vs 메모이제이션 O(n) vs 타뷸레이션 O(n)
    // ──────────────────────────────────────────────
    static void fibonacciComparison() {
        System.out.println("── 3. 피보나치: O(2^n) vs O(n) ──");
        System.out.println("  정의: fib(n) = fib(n-1) + fib(n-2),  fib(0)=0, fib(1)=1\n");

        System.out.println("  순진한 재귀는 같은 값을 지수적으로 다시 계산한다:");
        System.out.println("           fib(5)");
        System.out.println("          /      \\");
        System.out.println("       fib(4)    fib(3)     ← fib(3)이 벌써 두 번 등장");
        System.out.println("       /   \\      /   \\");
        System.out.println("    fib(3) fib(2) ...        ← 아래로 갈수록 중복 폭발\n");

        System.out.printf("  %-6s %18s %14s %14s %16s%n",
                "n", "순진한재귀(호출수)", "메모(호출수)", "값", "순진재귀시간(ns)");
        System.out.println("  " + "-".repeat(72));

        for (int n : new int[]{10, 20, 30, 35, 40}) {
            // 순진한 재귀: 호출 수를 세고 시간을 잰다.
            naiveCalls = 0;
            long s = System.nanoTime();
            long v1 = fibNaive(n);
            long naiveNs = System.nanoTime() - s;
            long naiveCount = naiveCalls;

            // 메모이제이션: 호출 수를 별도로 센다.
            long[] memo = new long[n + 1];
            Arrays.fill(memo, -1);
            int[] memoCalls = {0};
            long v2 = fibMemo(n, memo, memoCalls);

            // 타뷸레이션으로 값 교차검증
            long v3 = fibTab(n);
            if (v1 != v2 || v2 != v3) System.out.println("  (경고: 값 불일치!)");

            System.out.printf("  %-6d %18s %14s %14s %16s%n",
                    n, fmt(naiveCount), fmt(memoCalls[0]), fmt(v1), fmt(naiveNs));
        }
        System.out.println("\n  → 순진한 재귀 호출 수는 n이 5 커질 때마다 약 10배(≈지수). n=40이면 수억 번.");
        System.out.println("    메모이제이션은 호출이 n에 비례(약 2n)로만 늘어난다 → O(n).");
        System.out.println("    같은 문제인데 O(2^n) → O(n)! 이것이 DP의 위력이다.\n");
    }

    // 순진한 재귀 피보나치: 겹치는 부분문제를 매번 다시 계산 → O(2^n)
    static long fibNaive(int n) {
        naiveCalls++;
        if (n < 2) return n;                       // 기저: fib(0)=0, fib(1)=1
        return fibNaive(n - 1) + fibNaive(n - 2);  // 같은 값 반복 계산!
    }

    // 메모이제이션(top-down): 한 번 계산한 값을 배열에 저장해 재사용 → O(n)
    static long fibMemo(int n, long[] memo, int[] calls) {
        calls[0]++;
        if (n < 2) return n;
        if (memo[n] != -1) return memo[n];         // 캐시 적중 → 재계산 회피
        return memo[n] = fibMemo(n - 1, memo, calls) + fibMemo(n - 2, memo, calls);
    }

    // 타뷸레이션(bottom-up): 표를 작은 것부터 채워 올라감 → O(n), 재귀 없음(스택 안전)
    static long fibTab(int n) {
        if (n < 2) return n;
        long[] dp = new long[n + 1];
        dp[0] = 0; dp[1] = 1;
        for (int i = 2; i <= n; i++) dp[i] = dp[i - 1] + dp[i - 2];
        return dp[n];
    }

    // ──────────────────────────────────────────────
    // 4. 0/1 배낭 문제(0/1 Knapsack): DP의 대표 최적화 문제
    //    "무게 한도 W 안에서 가치 합을 최대로." 각 물건은 넣거나(1) 안 넣거나(0).
    //    dp[i][w] = 앞 i개 물건, 용량 w일 때의 최대 가치.
    //    점화식: 물건 i를 안 넣음 vs 넣음 중 더 큰 값.
    // ──────────────────────────────────────────────
    static void knapsackDemo() {
        System.out.println("── 4. 0/1 배낭 문제 (DP 최적화) ──");
        // 그리디가 '실패'하도록 설계된 고전 예시.
        // 비율(가치/무게)은 A(6) > B(5) > C(4) 순이지만,
        // 그리디가 A,B를 담으면 무게 30을 써 C(무게30)를 못 담아 손해를 본다.
        int[] weight = {10, 20, 30};
        int[] value  = {60, 100, 120};
        String[] name = {"물건A", "물건B", "물건C"};
        int capacity = 50;

        System.out.println("  배낭 용량 W = " + capacity);
        System.out.println("  물건 목록 (무게, 가치, 비율):");
        for (int i = 0; i < weight.length; i++) {
            System.out.printf("    %s: 무게 %d, 가치 %d, 비율(가치/무게) %.1f%n",
                    name[i], weight[i], value[i], (double) value[i] / weight[i]);
        }

        int best = knapsack(weight, value, capacity);
        System.out.println("  → DP로 구한 최대 가치: " + best);
        System.out.println("    (용량 50의 최적: 물건B(20)+물건C(30) = 가치 100+120 = 220)");

        // 그리디(가치/무게 비율 큰 것부터)로 풀면 왜 틀리는지 보인다.
        int greedy = knapsackGreedyByRatio(weight, value, capacity);
        System.out.println("  → 참고) '가치/무게 비율' 그리디의 결과: " + greedy
                + (greedy < best ? "  ← DP보다 작다(그리디 실패!)" : "  (이 예시선 우연히 일치)"));
        System.out.println("    그리디는 비율 최고 A(6.0)를 먼저 담고 B(5.0)까지 담아 무게 30/가치 160.");
        System.out.println("    하지만 남은 용량 20으로는 C(무게30)를 못 담는다. 반면 B+C를 담으면 220!");
        System.out.println("    '지금 당장 최선(A)'이 전체 최선을 망친 것 — 그리디가 실패하는 전형이다.");
        System.out.println();
    }

    // 0/1 배낭 DP: 시간/공간 O(n * W). n=물건수, W=용량.
    static int knapsack(int[] w, int[] v, int W) {
        int n = w.length;
        int[][] dp = new int[n + 1][W + 1];        // dp[i][cap]
        for (int i = 1; i <= n; i++) {
            for (int cap = 0; cap <= W; cap++) {
                // 경우 1: 물건 i(0-index i-1)를 안 넣음 → 이전 행 그대로
                dp[i][cap] = dp[i - 1][cap];
                // 경우 2: 넣을 수 있으면(무게가 용량 이하) 넣은 경우와 비교해 큰 값
                if (w[i - 1] <= cap) {
                    dp[i][cap] = Math.max(dp[i][cap],
                            dp[i - 1][cap - w[i - 1]] + v[i - 1]);
                }
            }
        }
        return dp[n][W];
    }

    // 그리디(비율 큰 순서로 통째 담기): 0/1 배낭에서는 최적을 보장하지 못한다.
    static int knapsackGreedyByRatio(int[] w, int[] v, int W) {
        int n = w.length;
        Integer[] idx = new Integer[n];
        for (int i = 0; i < n; i++) idx[i] = i;
        // 가치/무게 비율 내림차순 정렬
        Arrays.sort(idx, (x, y) -> Double.compare((double) v[y] / w[y], (double) v[x] / w[x]));
        int cap = W, total = 0;
        for (int i : idx) {
            if (w[i] <= cap) { cap -= w[i]; total += v[i]; } // 통째로만 담을 수 있음(0/1)
        }
        return total;
    }

    static void greedyVsDpNote() {
        System.out.println("── 그리디 vs DP 직관 ──");
        System.out.println("  그리디: '지금 당장 최선'을 매 단계 고른다. 빠르지만 항상 최적은 아니다.");
        System.out.println("         (동전 거스름돈, 최소 신장트리 등 '탐욕 선택 속성'이 성립할 때만 정답)");
        System.out.println("  DP    : 모든 부분문제의 답을 저장/조합해 '전역 최적'을 보장한다.");
        System.out.println("         겹치는 부분문제 + 최적 부분구조가 있으면 DP가 정답을 낸다.");
        System.out.println("  → 위 0/1 배낭에서 그리디가 DP보다 작은 값을 내면, 그것이 '그리디 실패'의 증거다.\n");
    }

    static void conclusion() {
        System.out.println("── 정리 ──");
        System.out.println("  재귀: 큰 문제를 같은 모양의 작은 문제로. 반드시 기저 조건 필요(안 그럼 StackOverflow).");
        System.out.println("  DP  : 겹치는 부분문제를 저장(메모/표)해 지수시간을 다항시간으로 낮춘다.");
        System.out.println("       - 메모이제이션(top-down, 재귀+캐시) / 타뷸레이션(bottom-up, 반복문).");
        System.out.println("  그리디는 빠르지만 최적 보장이 조건부, DP는 최적 보장(대신 비용 O(n*W) 등).");
        System.out.println();
    }

    static String fmt(long n) { return String.format("%,d", n); }
}
