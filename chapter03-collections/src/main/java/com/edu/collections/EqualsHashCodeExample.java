package com.edu.collections;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Chapter 03 - equals와 hashCode 계약(contract) 예제
 *
 * HashSet, HashMap 같은 해시 기반 컬렉션은 equals()와 hashCode()를 함께 사용합니다.
 * equals()만 재정의하고 hashCode()를 재정의하지 않으면 컬렉션이 오작동합니다.
 *
 * [equals/hashCode 계약]
 *  1. equals()가 true인 두 객체는 hashCode()도 반드시 같아야 한다.
 *  2. hashCode()가 같다고 해서 equals()가 반드시 true일 필요는 없다(해시 충돌 허용).
 *  3. equals()는 반사성/대칭성/추이성/일관성을 만족해야 한다.
 */
public class EqualsHashCodeExample {

    // ======================================================
    // (나쁜 예) equals만 재정의, hashCode는 재정의 안 함
    // ======================================================
    static class BadProduct {
        String name;
        int price;

        BadProduct(String name, int price) {
            this.name = name;
            this.price = price;
        }

        // equals만 재정의 → 내용이 같으면 동등하다고 판단
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof BadProduct)) return false;
            BadProduct other = (BadProduct) o;
            return price == other.price && Objects.equals(name, other.name);
        }
        // ⚠ hashCode()를 재정의하지 않음! → Object의 기본 hashCode(주소 기반) 사용

        @Override
        public String toString() {
            return name + "(" + price + ")";
        }
    }

    // ======================================================
    // (좋은 예) equals와 hashCode를 함께 재정의
    // ======================================================
    static class GoodProduct {
        String name;
        int price;

        GoodProduct(String name, int price) {
            this.name = name;
            this.price = price;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof GoodProduct)) return false;
            GoodProduct other = (GoodProduct) o;
            return price == other.price && Objects.equals(name, other.name);
        }

        // equals에서 사용한 필드와 "동일한 필드"로 hashCode를 계산해야 계약을 지킵니다.
        @Override
        public int hashCode() {
            return Objects.hash(name, price);
        }

        @Override
        public String toString() {
            return name + "(" + price + ")";
        }
    }

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("  Chapter 03 - equals & hashCode 계약");
        System.out.println("========================================\n");

        demonstrateBrokenContract();
        demonstrateFixedContract();
        explainContract();

        System.out.println("========================================");
        System.out.println("  equals & hashCode 예제 완료!");
        System.out.println("========================================");
    }

    // ======================================================
    // 1. hashCode 미재정의 → HashSet에서 요소 유실
    // ======================================================
    static void demonstrateBrokenContract() {
        System.out.println("--- 1. (나쁜 예) hashCode 미재정의 → 버그 발생 ---");

        BadProduct p1 = new BadProduct("노트북", 1000);
        BadProduct p2 = new BadProduct("노트북", 1000);  // 내용 동일

        // equals는 true지만...
        System.out.println("  p1.equals(p2)      : " + p1.equals(p2));      // true
        // hashCode는 서로 다름 (주소 기반 기본 구현)
        System.out.println("  p1.hashCode()      : " + p1.hashCode());
        System.out.println("  p2.hashCode()      : " + p2.hashCode());
        System.out.println("  hashCode 동일?     : " + (p1.hashCode() == p2.hashCode()));

        // HashSet은 먼저 hashCode로 버킷을 찾고 그 안에서 equals로 비교합니다.
        // hashCode가 다르면 다른 버킷으로 가버려서 "중복인데도" 둘 다 저장됩니다!
        Set<BadProduct> set = new HashSet<>();
        set.add(p1);
        set.add(p2);
        System.out.println("  HashSet 크기 (기대 1) : " + set.size() + " ← 중복 제거 실패! ★버그★");
        System.out.println("  set.contains(new 동일 객체): "
                + set.contains(new BadProduct("노트북", 1000)) + " ← 못 찾음!");
        System.out.println();
    }

    // ======================================================
    // 2. equals + hashCode 함께 재정의 → 정상 동작
    // ======================================================
    static void demonstrateFixedContract() {
        System.out.println("--- 2. (좋은 예) equals + hashCode 재정의 → 정상 ---");

        GoodProduct p1 = new GoodProduct("노트북", 1000);
        GoodProduct p2 = new GoodProduct("노트북", 1000);

        System.out.println("  p1.equals(p2)      : " + p1.equals(p2));      // true
        System.out.println("  hashCode 동일?     : " + (p1.hashCode() == p2.hashCode())); // true

        Set<GoodProduct> set = new HashSet<>();
        set.add(p1);
        set.add(p2);
        System.out.println("  HashSet 크기 (기대 1) : " + set.size() + " ← 중복 정상 제거!");
        System.out.println("  set.contains(new 동일 객체): "
                + set.contains(new GoodProduct("노트북", 1000)) + " ← 잘 찾음!");
        System.out.println();
    }

    // ======================================================
    // 3. 계약 규칙 정리
    // ======================================================
    static void explainContract() {
        System.out.println("--- 3. equals/hashCode 계약 정리 ---");
        System.out.println("  1) equals가 true면 hashCode도 반드시 같아야 한다. (가장 중요)");
        System.out.println("  2) hashCode가 같아도 equals가 true가 아닐 수 있다. (해시 충돌 허용)");
        System.out.println("  3) equals 5원칙: 반사성, 대칭성, 추이성, 일관성, null이면 false");
        System.out.println("  4) equals에 사용한 필드로 hashCode도 계산해야 한다.");
        System.out.println("  💡 IDE/Lombok(@EqualsAndHashCode)이나 record로 자동 생성하면 안전합니다.");
        System.out.println();
    }
}
