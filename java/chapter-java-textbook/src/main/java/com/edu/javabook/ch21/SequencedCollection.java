package com.edu.javabook.ch21;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * 21.7 순차 컬렉션 (Sequenced Collections)
 *
 * [순차 컬렉션이란]
 * - Java 21에서 추가된 컬렉션 인터페이스 계층이다.
 * - 원소에 "정해진 순서(첫 번째~마지막)"가 있는 컬렉션들에게
 *   통일된 API를 제공한다.
 *
 * [새 인터페이스]
 * - SequencedCollection : 순서가 있는 컬렉션 (예: List)
 *     getFirst(), getLast(), addFirst(), addLast(), removeFirst(), removeLast(), reversed()
 * - SequencedSet        : 순서가 있는 집합 (예: LinkedHashSet)
 * - SequencedMap        : 순서가 있는 맵 (예: LinkedHashMap)
 *     firstEntry(), lastEntry(), putFirst(), putLast(), reversed()
 *
 * [의의]
 * - 예전에는 첫/마지막 원소를 얻는 방법이 컬렉션마다 제각각이었다.
 *   (list.get(0), list.get(size-1) 등) 이제 getFirst/getLast로 통일된다.
 */
public class SequencedCollection {

    public static void main(String[] args) {

        System.out.println("=== 21.7 순차 컬렉션 ===");

        // [1] SequencedCollection: List
        System.out.println("\n[1] SequencedCollection (List)");
        // List는 Java 21에서 SequencedCollection을 구현한다
        java.util.SequencedCollection<String> seq = new ArrayList<>(List.of("B", "C", "D"));
        seq.addFirst("A");   // 맨 앞에 추가
        seq.addLast("E");    // 맨 뒤에 추가
        System.out.println("  전체        = " + seq);
        System.out.println("  getFirst()  = " + seq.getFirst());
        System.out.println("  getLast()   = " + seq.getLast());
        System.out.println("  reversed()  = " + seq.reversed());

        // [2] SequencedSet: LinkedHashSet (삽입 순서를 유지하는 집합)
        System.out.println("\n[2] SequencedSet (LinkedHashSet)");
        java.util.SequencedSet<Integer> set = new LinkedHashSet<>();
        set.addLast(10);
        set.addLast(20);
        set.addLast(30);
        set.addFirst(5);
        System.out.println("  전체        = " + set);
        System.out.println("  getFirst()  = " + set.getFirst());
        System.out.println("  getLast()   = " + set.getLast());
        System.out.println("  reversed()  = " + set.reversed());

        // [3] SequencedMap: LinkedHashMap (삽입 순서를 유지하는 맵)
        System.out.println("\n[3] SequencedMap (LinkedHashMap)");
        java.util.SequencedMap<String, Integer> map = new LinkedHashMap<>();
        map.putLast("two", 2);
        map.putLast("three", 3);
        map.putFirst("one", 1);   // 맨 앞에 삽입
        System.out.println("  전체         = " + map);
        System.out.println("  firstEntry() = " + map.firstEntry());
        System.out.println("  lastEntry()  = " + map.lastEntry());
        System.out.println("  reversed()   = " + map.reversed());

        // [4] 통일된 API의 이점
        System.out.println("\n[4] 통일된 접근");
        System.out.println("  예전: list.get(0), list.get(list.size()-1) 처럼 제각각");
        System.out.println("  지금: 어떤 순차 컬렉션이든 getFirst()/getLast()로 동일하게 접근");

        System.out.println("\n[정리]");
        System.out.println("  순차 컬렉션은 첫/마지막 원소 접근과 역순 뷰를");
        System.out.println("  getFirst/getLast/reversed로 통일해 제공한다.");
    }
}
