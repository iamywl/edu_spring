package com.edu.javabook.ch13;

/**
 * 13.2 제네릭 타입
 *
 * 제네릭 타입이란 타입을 파라미터로 가지는 클래스나 인터페이스를 말한다.
 *
 *   class 클래스명<T> { ... }          // 타입 파라미터 T 를 하나 가진다
 *   interface 인터페이스명<T> { ... }
 *
 * - T 는 "타입 파라미터"로, 관례상 다음 이름을 자주 쓴다.
 *     T (Type), E (Element), K (Key), V (Value), N (Number)
 * - 타입 파라미터는 여러 개 지정할 수 있다 :  <K, V> 처럼.
 *
 * 이 소절에서는
 *  (1) 타입 파라미터 하나짜리 제네릭 클래스/인터페이스,
 *  (2) 여러 타입 파라미터 <K, V> 제네릭 클래스를 다룬다.
 */
public class GenericType {

    /** 제네릭 인터페이스 : 담는 동작을 T 로 일반화 */
    interface Container<T> {
        void put(T item);
        T take();
    }

    /** 제네릭 클래스 (인터페이스 구현) : 타입 파라미터 하나 <T> */
    static class Basket<T> implements Container<T> {
        private T item;
        @Override public void put(T item) { this.item = item; }
        @Override public T take() { return item; }
    }

    /** 여러 타입 파라미터를 가진 제네릭 클래스 : 키-값 쌍 <K, V> */
    static class Pair<K, V> {
        private final K key;
        private final V value;
        public Pair(K key, V value) { this.key = key; this.value = value; }
        public K getKey()   { return key; }
        public V getValue() { return value; }
        @Override public String toString() { return "(" + key + " = " + value + ")"; }
    }

    public static void main(String[] args) {

        System.out.println("=== 13.2 제네릭 타입 ===");

        // [1] 타입 파라미터 하나짜리 제네릭 클래스
        System.out.println("\n[1] 제네릭 클래스 <T> - String 으로 지정");
        Basket<String> strBasket = new Basket<>();
        strBasket.put("사과");
        String fruit = strBasket.take();     // 캐스팅 불필요
        System.out.println("바구니에서 꺼낸 값: " + fruit);

        System.out.println("\n    같은 클래스를 Integer 로 재사용");
        Basket<Integer> intBasket = new Basket<>();
        intBasket.put(2025);
        int year = intBasket.take();
        System.out.println("바구니에서 꺼낸 값: " + year);

        // [2] 제네릭 인터페이스 타입으로 다루기
        System.out.println("\n[2] 제네릭 인터페이스 <T> 타입으로 참조");
        Container<Double> box = new Basket<>();   // 인터페이스 타입 변수
        box.put(3.14);
        double pi = box.take();
        System.out.println("Container<Double> 에서 꺼낸 값: " + pi);

        // [3] 여러 타입 파라미터 <K, V>
        System.out.println("\n[3] 여러 타입 파라미터 <K, V>");
        Pair<String, Integer> age = new Pair<>("나이", 20);
        System.out.println("문자열-정수 쌍: " + age);
        System.out.println("  key   타입: " + age.getKey().getClass().getSimpleName());
        System.out.println("  value 타입: " + age.getValue().getClass().getSimpleName());

        Pair<Integer, String> rank = new Pair<>(1, "금메달");
        System.out.println("정수-문자열 쌍: " + rank);

        // [4] 제네릭 타입은 서로 중첩(조합)할 수 있다
        System.out.println("\n[4] 제네릭 타입의 조합");
        Pair<String, Pair<String, Integer>> nested =
                new Pair<>("학생", new Pair<>("점수", 95));
        System.out.println("중첩된 쌍: " + nested);
        System.out.println("안쪽 값의 점수: " + nested.getValue().getValue());

        System.out.println("\n프로그램 정상 종료");
    }
}
