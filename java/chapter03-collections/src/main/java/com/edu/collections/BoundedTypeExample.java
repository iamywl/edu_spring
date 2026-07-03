package com.edu.collections;

import java.util.List;

/**
 * Chapter 03 - 바운디드 타입 파라미터 (Bounded Type Parameter) 예제
 *
 * [이 파일 한 가지 주제]
 *   "상한 경계 <T extends ...>  =  타입 파라미터에 제한 걸기"
 *
 * 바운디드 타입 파라미터란?
 *   - <T> 는 "아무 타입이나" 허용합니다.
 *   - <T extends Number> 처럼 쓰면 "Number 또는 그 하위 타입"만 허용합니다.
 *     (여기서 extends 는 클래스 상속과 인터페이스 구현을 모두 포괄)
 *
 * 왜 경계가 필요한가? (핵심!)
 *   - 그냥 <T> 이면 T 는 Object 로만 취급됩니다.
 *     -> T 값으로는 Object 의 메서드(toString, equals ...)밖에 못 씁니다.
 *   - <T extends Number> 로 경계를 주면,
 *     T 는 최소한 Number 이므로 doubleValue(), intValue() 같은
 *     Number 의 메서드를 "안전하게" 호출할 수 있습니다.
 *   - <T extends Comparable<T>> 로 주면 compareTo() 를 쓸 수 있어
 *     대소 비교/정렬 로직을 제네릭하게 작성할 수 있습니다.
 *
 * 정리: "경계 = 그 타입이 가진 기능(메서드)을 T 에게 보장해 주는 약속"
 */
public class BoundedTypeExample {

    // ======================================================
    // 1. 상한 경계 클래스 - NumberBox<T extends Number>
    // ======================================================
    /**
     * T 는 반드시 Number 를 상속한 타입이어야 합니다.
     * 덕분에 value.doubleValue() 를 마음 놓고 호출할 수 있습니다.
     */
    static class NumberBox<T extends Number> {
        private final T value;

        public NumberBox(T value) {
            this.value = value;
        }

        // T 가 Number 임이 보장되므로 Number 의 메서드 사용 가능
        public double doubleValue() {
            return value.doubleValue();
        }

        @Override
        public String toString() {
            return "NumberBox{value=" + value + ", doubleValue=" + doubleValue() + "}";
        }
    }

    // ======================================================
    // 2. 상한 경계 메서드 - <T extends Number>
    // ======================================================
    /**
     * Number 의 하위 타입 리스트의 합계를 구합니다.
     * 경계가 없으면 element.doubleValue() 를 호출할 수 없습니다.
     */
    public static <T extends Number> double sum(List<T> list) {
        double total = 0.0;
        for (T element : list) {
            total += element.doubleValue();   // 경계 덕분에 가능
        }
        return total;
    }

    // ======================================================
    // 3. Comparable 경계 - <T extends Comparable<T>>
    // ======================================================
    /**
     * 리스트에서 최댓값을 찾는 제네릭 메서드.
     * T 가 Comparable<T> 를 구현해야 compareTo 로 비교할 수 있습니다.
     * -> String, Integer, Double 등 Comparable 을 구현한 타입 모두 사용 가능.
     */
    public static <T extends Comparable<T>> T max(List<T> list) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        T best = list.get(0);
        for (T item : list) {
            // compareTo 결과가 양수면 item 이 더 큼
            if (item.compareTo(best) > 0) {
                best = item;
            }
        }
        return best;
    }

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("  바운디드 타입 파라미터 <T extends ...>");
        System.out.println("========================================\n");

        // --- 1. 상한 경계 클래스 NumberBox ---
        System.out.println("--- 1. 상한 경계 클래스 (NumberBox<T extends Number>) ---");
        NumberBox<Integer> intNumBox = new NumberBox<>(100);
        NumberBox<Double> doubleNumBox = new NumberBox<>(3.14);
        System.out.println("  " + intNumBox);
        System.out.println("  " + doubleNumBox);
        // NumberBox<String> err = new NumberBox<>("error"); // 컴파일 에러!
        System.out.println("  NumberBox<String> 은 컴파일 에러! (String 은 Number 하위 타입이 아님)");
        System.out.println("  => 경계 덕분에 '숫자만' 허용하도록 제한 + doubleValue() 사용 보장");
        System.out.println();

        // --- 2. 상한 경계 메서드 sum ---
        System.out.println("--- 2. 상한 경계 메서드 (<T extends Number> sum) ---");
        List<Integer> ints = List.of(10, 20, 30, 40, 50);
        List<Double> doubles = List.of(1.1, 2.2, 3.3);
        System.out.println("  정수 리스트 합계: " + sum(ints));
        System.out.println("  실수 리스트 합계: " + sum(doubles));
        System.out.println("  (경계가 없다면 element.doubleValue() 호출 자체가 불가능)");
        System.out.println();

        // --- 3. Comparable 경계 메서드 max ---
        System.out.println("--- 3. Comparable 경계 (<T extends Comparable<T>> max) ---");
        List<Integer> numbers = List.of(3, 9, 1, 7, 5);
        List<String> words = List.of("banana", "apple", "cherry");
        System.out.println("  숫자 최댓값: " + max(numbers));    // 9
        System.out.println("  문자열 최댓값(사전순): " + max(words)); // cherry
        System.out.println("  => Comparable 을 구현한 타입이면 무엇이든 비교/정렬 가능");

        System.out.println("\n========================================");
        System.out.println("  바운디드 타입 파라미터 예제 완료!");
        System.out.println("========================================");
    }
}
