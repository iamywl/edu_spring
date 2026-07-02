package com.edu.javabook.ch13;

import java.util.ArrayList;
import java.util.List;

/**
 * 13.5 와일드카드 (Wildcard, ?)
 *
 * 제네릭은 기본적으로 "불공변(invariant)"이다.
 * 즉, List<Integer> 는 List<Number> 의 하위 타입이 아니다.
 * 그래서 "여러 타입의 제네릭 객체"를 한 메소드로 받으려면 와일드카드 ? 가 필요하다.
 *
 * 세 가지 형태 :
 *   1) <?>            : 비한정 와일드카드. 모든 타입을 받되, 원소를 읽어 Object 로만 다룬다.
 *   2) <? extends T>  : 상한 경계. T 또는 그 자손. "읽기(생산)"에 적합. (꺼내 쓰기 좋음)
 *   3) <? super T>    : 하한 경계. T 또는 그 조상. "쓰기(소비)"에 적합. (넣기 좋음)
 *
 * PECS 원칙 :
 *   Producer-Extends, Consumer-Super
 *   - 데이터를 생산(꺼내 읽기)하는 곳 → ? extends T
 *   - 데이터를 소비(집어넣기)하는 곳 → ? super T
 *
 * 이 소절에서는 세 형태와 PECS 를 확인한다.
 */
public class WildcardType {

    /** <?> : 어떤 타입의 리스트든 받아 크기만 확인 (원소는 Object 로만 취급) */
    static void printSize(List<?> list) {
        System.out.println("  크기: " + list.size() + ", 내용: " + list);
        // list.add("x"); // <- 불가능! 어떤 타입인지 모르므로 넣을 수 없다(null 만 가능)
    }

    /** <? extends Number> : 생산자. Number 계열 리스트에서 값을 "읽어" 합계를 낸다 (PE) */
    static double sumOf(List<? extends Number> list) {
        double total = 0.0;
        for (Number n : list) {             // 꺼내면 최소한 Number 임이 보장된다
            total += n.doubleValue();
        }
        return total;
    }

    /** <? super Integer> : 소비자. Integer 를 받을 수 있는 리스트에 값을 "넣는다" (CS) */
    static void addNumbers(List<? super Integer> list) {
        for (int i = 1; i <= 3; i++) {
            list.add(i);                    // Integer 는 반드시 넣을 수 있다
        }
    }

    public static void main(String[] args) {

        System.out.println("=== 13.5 와일드카드 ===");

        // [1] 비한정 와일드카드 <?>
        System.out.println("\n[1] 비한정 와일드카드 <?>");
        List<String>  strList = List.of("A", "B");
        List<Integer> intList = List.of(1, 2, 3);
        System.out.println("String 리스트:");
        printSize(strList);                 // List<?> 이므로 어떤 타입이든 받는다
        System.out.println("Integer 리스트:");
        printSize(intList);

        // [2] 상한 경계 <? extends T> : 생산자(Producer) - 읽기
        System.out.println("\n[2] 상한 경계 <? extends Number> (Producer-Extends)");
        List<Integer> ints    = List.of(10, 20, 30);
        List<Double>  doubles = List.of(1.5, 2.5);
        System.out.println("Integer 리스트 합계: " + sumOf(ints));
        System.out.println("Double  리스트 합계: " + sumOf(doubles));
        System.out.println("→ List<Integer>, List<Double> 를 모두 하나의 메소드로 처리.");

        // [3] 하한 경계 <? super T> : 소비자(Consumer) - 쓰기
        System.out.println("\n[3] 하한 경계 <? super Integer> (Consumer-Super)");
        List<Integer> target1 = new ArrayList<>();
        List<Number>  target2 = new ArrayList<>();   // Number 는 Integer 의 조상
        List<Object>  target3 = new ArrayList<>();   // Object 도 조상
        addNumbers(target1);
        addNumbers(target2);
        addNumbers(target3);
        System.out.println("List<Integer> 에 추가 후: " + target1);
        System.out.println("List<Number>  에 추가 후: " + target2);
        System.out.println("List<Object>  에 추가 후: " + target3);

        // [4] PECS 요약
        System.out.println("\n[4] PECS 원칙 정리");
        System.out.println("  Producer-Extends : 꺼내 읽기만 → <? extends T>");
        System.out.println("  Consumer-Super   : 집어넣기만   → <? super T>");
        System.out.println("  → 읽기와 쓰기를 모두 해야 하면 정확한 타입(와일드카드 X)을 쓴다.");

        System.out.println("\n프로그램 정상 종료");
    }
}
