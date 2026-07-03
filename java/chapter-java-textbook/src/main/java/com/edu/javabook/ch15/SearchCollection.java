package com.edu.javabook.ch15;

import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * 15.5 검색 강화 컬렉션
 *
 * TreeSet / TreeMap 은 정렬 상태를 유지하므로 "범위/근사 검색"에 강하다.
 * 이들은 NavigableSet / NavigableMap 인터페이스를 구현하며 다음 메소드를 제공한다.
 *
 * [값 하나 찾기]
 *  - first() / last()      : 가장 작은 / 가장 큰 원소
 *  - floor(x)  : x 이하(<=) 중 가장 큰 값     (x 자신 포함)
 *  - ceiling(x): x 이상(>=) 중 가장 작은 값   (x 자신 포함)
 *  - lower(x)  : x 미만(<)  중 가장 큰 값     (x 자신 제외)
 *  - higher(x) : x 초과(>)  중 가장 작은 값   (x 자신 제외)
 *
 * [범위(부분) 뷰]
 *  - headSet/headMap(x) : x 보다 작은 부분
 *  - tailSet/tailMap(x) : x 이상인 부분
 *  - subSet/subMap(a,b) : a 이상 b 미만 부분
 *
 * 이 소절에서는 정렬 기반 검색 메소드를 실습한다.
 */
public class SearchCollection {

    public static void main(String[] args) {

        System.out.println("=== 15.5 검색 강화 컬렉션 ===");

        // [1] TreeSet 준비 (자동 정렬)
        System.out.println("\n[1] TreeSet(NavigableSet) 준비");
        NavigableSet<Integer> set = new TreeSet<>();
        for (int n : new int[]{10, 20, 30, 40, 50}) set.add(n);
        System.out.println("집합: " + set);
        System.out.println("first(최소): " + set.first() + ", last(최대): " + set.last());

        // [2] floor / ceiling / lower / higher
        System.out.println("\n[2] 근사 검색 (기준값 25, 30)");
        System.out.println("floor(25)   = " + set.floor(25)   + "  (25 이하 중 최대)");
        System.out.println("ceiling(25) = " + set.ceiling(25) + "  (25 이상 중 최소)");
        System.out.println("floor(30)   = " + set.floor(30)   + "  (30 포함)");
        System.out.println("lower(30)   = " + set.lower(30)   + "  (30 제외, 미만)");
        System.out.println("higher(30)  = " + set.higher(30)  + "  (30 제외, 초과)");

        // [3] headSet / tailSet / subSet
        System.out.println("\n[3] 범위 뷰");
        System.out.println("headSet(30)      = " + set.headSet(30)       + "  (30 미만)");
        System.out.println("tailSet(30)      = " + set.tailSet(30)       + "  (30 이상)");
        System.out.println("subSet(20, 50)   = " + set.subSet(20, 50)    + "  (20 이상 50 미만)");

        // [4] TreeMap 도 동일하게 동작
        System.out.println("\n[4] TreeMap(NavigableMap) 근사/범위 검색");
        NavigableMap<Integer, String> map = new TreeMap<>();
        map.put(10, "십"); map.put(20, "이십"); map.put(30, "삼십"); map.put(40, "사십");
        System.out.println("맵: " + map);
        System.out.println("firstKey: " + map.firstKey() + ", lastKey: " + map.lastKey());
        System.out.println("floorKey(25)   = " + map.floorKey(25)   + "  (25 이하 최대 키)");
        System.out.println("ceilingKey(25) = " + map.ceilingKey(25) + "  (25 이상 최소 키)");
        System.out.println("headMap(30)     = " + map.headMap(30)   + "  (키 30 미만)");
        System.out.println("tailMap(30)     = " + map.tailMap(30)   + "  (키 30 이상)");

        System.out.println("\n→ 정렬 유지 덕분에 범위/근사 검색이 간단하다");
        System.out.println("\n프로그램 정상 종료");
    }
}
