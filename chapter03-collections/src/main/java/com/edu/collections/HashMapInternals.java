package com.edu.collections;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Chapter 03 (CS 심화) - HashMap은 어떻게 O(1)로 찾는가, 그리고 언제 O(n)이 되는가
 *
 * HashMap의 내부 구조(주석으로 설명):
 *   - 내부는 "버킷 배열"이다. 각 key의 hashCode()로 버킷 인덱스를 계산한다:
 *       index = (hash of key) & (capacity - 1)
 *   - 같은 버킷에 여러 key가 오면(해시 충돌) 연결 리스트로 이어 붙인다(체이닝).
 *   - 한 버킷의 충돌이 8개를 넘고 전체 용량이 충분히 크면 리스트를 '트리(레드-블랙)'로
 *     바꾼다(treeify). 이러면 최악에도 O(log n)으로 방어된다.
 *   - 저장된 개수가 (용량 × 로드팩터 0.75)를 넘으면 용량을 2배로 늘리고
 *     모든 항목을 재배치한다(resize/rehash).
 *
 * 이 데모가 증명하는 것:
 *   1) 좋은 hashCode면 조회가 사실상 O(1) — key 수를 늘려도 조회 시간이 거의 일정.
 *   2) 모든 key가 같은 버킷에 몰리는 '나쁜 hashCode'면 O(n)으로 퇴화.
 *   3) equals/hashCode 계약이 깨지면(불안정한 hashCode) 넣은 값을 다시 못 찾는다.
 */
public class HashMapInternals {

    public static void main(String[] args) {
        System.out.println("====================================");
        System.out.println(" Chapter 03 심화: HashMap 내부 동작");
        System.out.println("====================================\n");

        constantTimeLookup();
        loadFactorAndResize();
        goodVsBadHashCode();
        brokenContract();
        conclusion();
    }

    // ──────────────────────────────────────────────
    // 1. 많은 key를 넣어도 조회는 거의 일정(O(1))
    // ──────────────────────────────────────────────
    static void constantTimeLookup() {
        System.out.println("── 1. 좋은 hashCode → 조회는 사실상 O(1) ──");
        System.out.printf("  %-14s %20s%n", "key 개수", "조회 100만회 시간(ns)");
        System.out.println("  " + "-".repeat(38));

        for (int n : new int[]{10_000, 100_000, 1_000_000}) {
            Map<Integer, Integer> map = new HashMap<>();
            for (int i = 0; i < n; i++) map.put(i, i);   // Integer의 hashCode는 값 자체 → 잘 분산됨

            long start = System.nanoTime();
            long sink = 0;
            for (int r = 0; r < 1_000_000; r++) {
                sink += map.get(r % n);   // 어떤 key든 버킷 인덱스로 즉시 접근
            }
            long elapsed = System.nanoTime() - start;
            if (sink == Long.MIN_VALUE) System.out.print("");
            System.out.printf("  %-14s %20d%n", String.format("%,d", n), elapsed);
        }
        System.out.println("\n  → key 개수를 100배 늘려도 조회 시간은 거의 그대로. 이것이 O(1).\n");
    }

    // ──────────────────────────────────────────────
    // 2. 로드팩터와 resize (공개 API로는 용량을 못 본다)
    // ──────────────────────────────────────────────
    static void loadFactorAndResize() {
        System.out.println("── 2. 로드팩터 0.75와 resize ──");

        // HashMap의 기본 용량은 16, 로드팩터는 0.75.
        // 저장 개수가 16*0.75 = 12를 넘으면 용량을 32로 2배 늘립니다(그다음 임계값 24 ...).
        // 아쉽게도 현재 용량(capacity)은 public API로 읽을 수 없습니다.
        // → 그래서 여기서는 "임계값을 넘는 지점"을 계산으로 보여줍니다.
        System.out.println("  기본 용량=16, 로드팩터=0.75 → 임계값=12");
        System.out.println("  저장 개수가 임계값을 넘을 때마다 용량을 2배로 늘리고 전체 재배치(rehash):");

        int capacity = 16;
        double loadFactor = 0.75;
        System.out.printf("  %-10s %-10s %-14s%n", "삽입수", "용량", "임계값(용량*0.75)");
        System.out.println("  " + "-".repeat(36));
        for (int size = 1; size <= 100; size++) {
            int threshold = (int) (capacity * loadFactor);
            if (size > threshold) {   // 임계값 초과 → 이 시점 직전에 resize 발생
                capacity *= 2;
                System.out.printf("  %-10d %-10d %-14d  ← size %d에서 용량 2배로 resize%n",
                        size, capacity, (int) (capacity * loadFactor), size);
            }
        }
        System.out.println("  (용량을 미리 알면 new HashMap<>(예상크기/0.75)로 resize를 줄일 수 있음)\n");
    }

    // ──────────────────────────────────────────────
    // 3. 좋은 hashCode vs 나쁜 hashCode: O(1) vs O(n)
    // ──────────────────────────────────────────────
    static void goodVsBadHashCode() {
        System.out.println("── 3. 나쁜 hashCode는 O(n)으로 퇴화 ──");

        // GoodKey: id를 그대로 해시 → key들이 여러 버킷에 고르게 분산
        // BadKey : 무조건 42를 반환 → 모든 key가 '같은 버킷' 하나에 몰림
        //          → 그 버킷은 긴 리스트/트리가 되어 조회가 O(n)(또는 O(log n))으로 느려짐
        int n = 20_000;
        Map<GoodKey, Integer> good = new HashMap<>();
        Map<BadKey, Integer> bad = new HashMap<>();
        for (int i = 0; i < n; i++) {
            good.put(new GoodKey(i), i);
            bad.put(new BadKey(i), i);
        }

        // 조회 시간 측정 (같은 횟수, 같은 key 분포)
        long tGood = time(() -> {
            long s = 0;
            for (int i = 0; i < n; i++) s += good.get(new GoodKey(i));
            if (s < 0) System.out.print("");
        });
        long tBad = time(() -> {
            long s = 0;
            for (int i = 0; i < n; i++) s += bad.get(new BadKey(i));
            if (s < 0) System.out.print("");
        });

        System.out.printf("  좋은 hashCode (분산됨)  : %,15d ns%n", tGood);
        System.out.printf("  나쁜 hashCode (전부 42) : %,15d ns%n", tBad);
        System.out.printf("  → 나쁜 쪽이 약 %d배 느림. 모든 key가 한 버킷에 몰렸기 때문.%n",
                tBad / Math.max(tGood, 1));
        System.out.println("    (Java 8+는 버킷이 너무 길면 트리로 바꿔 O(n)을 O(log n)으로 완화)\n");
    }

    // ──────────────────────────────────────────────
    // 4. equals/hashCode 계약이 깨지면 조회 실패
    // ──────────────────────────────────────────────
    static void brokenContract() {
        System.out.println("── 4. 불안정한 hashCode → 넣고도 못 찾는 버그 ──");

        // 규칙: "equals가 true인 두 객체는 반드시 같은 hashCode를 가져야 한다."
        // MutableKey는 필드가 바뀌면 hashCode도 바뀝니다.
        // put 이후에 필드를 바꾸면, 저장할 때 계산한 버킷과 조회할 때 계산한 버킷이 달라져
        // 같은 객체인데도 찾지 못합니다.
        Map<MutableKey, String> map = new HashMap<>();
        MutableKey key = new MutableKey(1);
        map.put(key, "값");

        System.out.println("  put 직후 조회 : " + map.get(key));   // 정상

        key.id = 999;   // 저장 후 hashCode가 바뀜 → 계약 위반
        System.out.println("  필드 변경 후 조회 : " + map.get(key) + "  ← null! (버킷이 달라짐)");
        System.out.println("  map.containsKey(key) : " + map.containsKey(key) + "  (같은 참조인데도 못 찾음)");

        System.out.println("\n  ⚠ 교훈: HashMap의 key로는 '불변(immutable)' 객체를 쓰세요.");
        System.out.println("    (String, Integer, 그리고 필드가 안 바뀌는 record가 안전한 이유)\n");
    }

    static void conclusion() {
        System.out.println("── 정리 ──");
        System.out.println("  버킷 배열 + 해시로 인덱스 계산 → 평균 O(1) 조회.");
        System.out.println("  충돌은 체이닝(리스트)으로 처리, 8개 넘으면 트리로 변환(treeify).");
        System.out.println("  로드팩터 0.75 초과 시 용량 2배 + rehash.");
        System.out.println("  나쁜/불안정한 hashCode는 O(n) 퇴화 또는 조회 실패를 부른다.");
        System.out.println();
    }

    static long time(Runnable r) {
        long start = System.nanoTime();
        r.run();
        return System.nanoTime() - start;
    }

    // ── key 클래스들 ──

    // 좋은 key: id별로 다른 hashCode → 잘 분산됨
    static final class GoodKey {
        final int id;
        GoodKey(int id) { this.id = id; }
        @Override public int hashCode() { return Integer.hashCode(id); }
        @Override public boolean equals(Object o) {
            return o instanceof GoodKey g && g.id == id;
        }
    }

    // 나쁜 key: equals는 정상이지만 hashCode가 상수 → 전부 같은 버킷으로 충돌
    static final class BadKey {
        final int id;
        BadKey(int id) { this.id = id; }
        @Override public int hashCode() { return 42; }   // 모든 key가 같은 버킷!
        @Override public boolean equals(Object o) {
            return o instanceof BadKey b && b.id == id;
        }
    }

    // 계약을 깨는 key: 필드가 가변이라 hashCode가 시간에 따라 변함
    static final class MutableKey {
        int id;   // final이 아님 → 위험
        MutableKey(int id) { this.id = id; }
        @Override public int hashCode() { return Objects.hashCode(id); }
        @Override public boolean equals(Object o) {
            return o instanceof MutableKey m && m.id == id;
        }
    }
}
