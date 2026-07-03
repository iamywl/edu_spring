package com.edu.collections;

import java.util.ArrayList;
import java.util.List;

/**
 * Chapter 03 - 와일드카드 (Wildcard) + PECS 예제
 *
 * [이 파일 한 가지 주제]
 *   "와일드카드 ? / ? extends / ? super  와  PECS 원칙"
 *
 * 와일드카드(?)란?
 *   - 제네릭에서 "타입을 특정하지 않겠다"는 의미의 물음표(?) 기호입니다.
 *   - 왜 필요한가? List<Object> 와 List<Integer> 는 상속 관계가 아닙니다!
 *     (Integer 는 Object 의 하위 타입이지만, List<Integer> 는 List<Object> 의
 *      하위 타입이 아님 -> 제네릭은 "불공변(invariant)")
 *     그래서 "여러 타입의 리스트를 유연하게 받기 위해" 와일드카드를 씁니다.
 *
 * 세 가지 형태:
 *   1) <?>              : 비한정 와일드카드. "모든 타입". 주로 읽기 전용.
 *   2) <? extends T>    : 상한 와일드카드. "T 또는 그 하위 타입". 값을 꺼내는(생산) 용도.
 *   3) <? super T>      : 하한 와일드카드. "T 또는 그 상위 타입". 값을 넣는(소비) 용도.
 *
 * PECS 원칙 (반드시 기억!):
 *   Producer  - Extends,  Consumer - Super
 *   = 데이터를 "생산(꺼내 읽기)"하면 extends, "소비(넣어 쓰기)"하면 super.
 *   - <? extends T> 는 꺼내 읽기는 되지만 add 는 (null 외) 불가.
 *       왜? 실제 원소 타입이 T 의 어떤 하위 타입인지 모르므로 넣으면 위험.
 *   - <? super T>   는 T(및 하위)를 넣는 것은 되지만, 꺼내면 Object 로만 받음.
 */
public class WildcardExample {

    // ------------------------------------------------------
    // 1) 비한정 와일드카드 <?>  - 모든 리스트를 읽기 전용으로 출력
    // ------------------------------------------------------
    public static void printList(List<?> list) {
        System.out.print("  리스트 내용: [");
        for (int i = 0; i < list.size(); i++) {
            System.out.print(list.get(i));   // 꺼낸 원소는 Object 로 취급
            if (i < list.size() - 1) System.out.print(", ");
        }
        System.out.println("]");
        // list.add("x");  // 컴파일 에러! 어떤 타입인지 모르므로 넣을 수 없음
    }

    // ------------------------------------------------------
    // 2) 상한 와일드카드 <? extends Number>  - Producer(생산: 꺼내 읽기)
    //    Number 또는 그 하위 타입의 리스트에서 합계를 구한다.
    // ------------------------------------------------------
    public static double sumWithWildcard(List<? extends Number> list) {
        double total = 0.0;
        for (Number num : list) {         // 꺼낼 때 최소한 Number 로 보장됨 -> 읽기 OK
            total += num.doubleValue();
        }
        // list.add(1);  // 컴파일 에러! 하위 타입이 무엇인지 몰라 넣을 수 없음
        return total;
    }

    // ------------------------------------------------------
    // 3) 하한 와일드카드 <? super Integer>  - Consumer(소비: 넣어 쓰기)
    //    Integer 또는 그 상위 타입(Number, Object)의 리스트에 값을 추가한다.
    // ------------------------------------------------------
    public static void addNumbers(List<? super Integer> list) {
        list.add(1);   // Integer(및 하위)는 안전하게 넣을 수 있음 -> 쓰기 OK
        list.add(2);
        list.add(3);
        // Integer x = list.get(0);  // 컴파일 에러! 꺼내면 Object 로만 받을 수 있음
    }

    // ------------------------------------------------------
    // 4) PECS 종합 - copy(src, dest)
    //    src 에서 읽으니 Producer -> extends
    //    dest 에 쓰니   Consumer -> super
    // ------------------------------------------------------
    public static <T> void copy(List<? extends T> src, List<? super T> dest) {
        for (T item : src) {   // src: 생산자 -> extends
            dest.add(item);    // dest: 소비자 -> super
        }
    }

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("  와일드카드 ? / ? extends / ? super + PECS");
        System.out.println("========================================\n");

        // --- 1. 비한정 와일드카드 <?> ---
        System.out.println("--- 1. 비한정 와일드카드 <?> (읽기 전용) ---");
        printList(List.of("a", "b", "c"));   // List<String>
        printList(List.of(1, 2, 3));         // List<Integer>
        System.out.println("  => 타입에 상관없이 어떤 리스트든 받을 수 있음 (넣기는 불가)");
        System.out.println();

        // --- 2. 상한 와일드카드 <? extends Number> (Producer) ---
        System.out.println("--- 2. 상한 와일드카드 <? extends Number> (Producer-Extends) ---");
        List<Integer> intList = List.of(1, 2, 3);
        List<Double> doubleList = List.of(1.5, 2.5, 3.5);
        System.out.println("  Integer 리스트 합계: " + sumWithWildcard(intList));
        System.out.println("  Double  리스트 합계: " + sumWithWildcard(doubleList));
        System.out.println("  => 꺼내 읽기(생산)에 적합. add 는 불가!");
        System.out.println();

        // --- 3. 하한 와일드카드 <? super Integer> (Consumer) ---
        System.out.println("--- 3. 하한 와일드카드 <? super Integer> (Consumer-Super) ---");
        List<Number> numberList = new ArrayList<>();
        addNumbers(numberList);   // Number 리스트에 Integer 추가 OK
        System.out.println("  Number 리스트에 Integer 추가: " + numberList);
        List<Object> objectList = new ArrayList<>();
        addNumbers(objectList);   // Object 리스트에도 가능(Object 는 Integer 의 상위)
        System.out.println("  Object 리스트에 Integer 추가: " + objectList);
        System.out.println("  => 넣어 쓰기(소비)에 적합. 꺼내면 Object 로만 받음!");
        System.out.println();

        // --- 4. PECS 종합 copy ---
        System.out.println("--- 4. PECS 원칙 종합 (copy: src=extends, dest=super) ---");
        List<Integer> source = List.of(10, 20, 30);
        List<Number> destination = new ArrayList<>();
        copy(source, destination);   // Integer -> Number 복사
        System.out.println("  source(Integer, 생산자): " + source);
        System.out.println("  destination(Number, 소비자): " + destination);
        System.out.println("  => Producer Extends, Consumer Super. 기억하세요: PECS!");

        System.out.println("\n========================================");
        System.out.println("  와일드카드 예제 완료!");
        System.out.println("========================================");
    }
}
