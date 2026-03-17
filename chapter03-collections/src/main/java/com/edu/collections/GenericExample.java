package com.edu.collections;

import java.util.ArrayList;
import java.util.List;

/**
 * Chapter 03 - 제네릭 (Generics) 예제
 *
 * 제네릭을 사용하면 컴파일 시점에 타입 안전성을 보장하고,
 * 불필요한 캐스팅을 제거할 수 있습니다.
 */
public class GenericExample {

    // ======================================================
    // 1. 제네릭 클래스 - Box<T>
    // ======================================================
    /**
     * 어떤 타입이든 담을 수 있는 상자 클래스
     * T는 타입 파라미터로, 사용 시점에 구체적인 타입으로 대체됩니다.
     */
    static class Box<T> {
        private T value;

        public Box() {}

        public Box(T value) {
            this.value = value;
        }

        public T get() {
            return value;
        }

        public void set(T value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return "Box{value=" + value + "}";
        }
    }

    // ======================================================
    // 2. 다중 타입 파라미터 클래스 - Pair<K, V>
    // ======================================================
    /**
     * 두 개의 타입 파라미터를 가지는 쌍(Pair) 클래스
     */
    static class Pair<K, V> {
        private final K key;
        private final V value;

        public Pair(K key, V value) {
            this.key = key;
            this.value = value;
        }

        public K getKey() { return key; }
        public V getValue() { return value; }

        @Override
        public String toString() {
            return "Pair{" + key + " = " + value + "}";
        }
    }

    // ======================================================
    // 3. 바운디드 타입 파라미터 (Bounded Type Parameter)
    // ======================================================
    /**
     * T는 반드시 Number를 상속한 타입이어야 합니다.
     * 이를 통해 숫자 타입만 허용하도록 제한할 수 있습니다.
     */
    static class NumberBox<T extends Number> {
        private T value;

        public NumberBox(T value) {
            this.value = value;
        }

        // Number의 메서드를 안전하게 사용할 수 있음
        public double doubleValue() {
            return value.doubleValue();
        }

        @Override
        public String toString() {
            return "NumberBox{value=" + value + ", doubleValue=" + doubleValue() + "}";
        }
    }

    // ======================================================
    // 4. 제네릭 메서드
    // ======================================================

    /**
     * 리스트의 첫 번째 요소를 반환하는 제네릭 메서드
     * 메서드 레벨에서 타입 파라미터를 선언합니다.
     */
    public static <T> T getFirst(List<T> list) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    /**
     * 바운디드 타입 파라미터를 사용한 제네릭 메서드
     * Number의 하위 타입 리스트의 합계를 구합니다.
     */
    public static <T extends Number> double sum(List<T> list) {
        double total = 0.0;
        for (T element : list) {
            total += element.doubleValue();
        }
        return total;
    }

    /**
     * 두 개의 타입 파라미터를 가지는 제네릭 메서드
     */
    public static <K, V> Pair<K, V> makePair(K key, V value) {
        return new Pair<>(key, value);
    }

    // ======================================================
    // 5. 와일드카드 (Wildcard)
    // ======================================================

    /**
     * 비한정 와일드카드 (Unbounded Wildcard) - <?>
     * 모든 타입의 리스트를 읽기 전용으로 처리할 때 사용
     */
    public static void printList(List<?> list) {
        System.out.print("  리스트 내용: [");
        for (int i = 0; i < list.size(); i++) {
            System.out.print(list.get(i));
            if (i < list.size() - 1) System.out.print(", ");
        }
        System.out.println("]");
    }

    /**
     * 상한 와일드카드 (Upper Bounded Wildcard) - <? extends Number>
     * Producer-Extends: 데이터를 꺼내는(읽는) 용도
     * Number 또는 그 하위 타입의 리스트에서 합계를 구합니다.
     */
    public static double sumWithWildcard(List<? extends Number> list) {
        double total = 0.0;
        for (Number num : list) {
            total += num.doubleValue();
        }
        return total;
    }

    /**
     * 하한 와일드카드 (Lower Bounded Wildcard) - <? super Integer>
     * Consumer-Super: 데이터를 넣는(쓰는) 용도
     * Integer 또는 그 상위 타입의 리스트에 값을 추가합니다.
     */
    public static void addNumbers(List<? super Integer> list) {
        list.add(1);
        list.add(2);
        list.add(3);
    }

    /**
     * PECS 원칙 적용 예시
     * Producer-Extends, Consumer-Super
     * src에서 읽고(extends), dest에 쓴다(super)
     */
    public static <T> void copy(List<? extends T> src, List<? super T> dest) {
        for (T item : src) {
            dest.add(item);
        }
    }

    // ======================================================
    // main 메서드 - 모든 예제 실행
    // ======================================================
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("  Chapter 03 - 제네릭 (Generics) 예제");
        System.out.println("========================================\n");

        // --- 1. 제네릭 클래스 ---
        System.out.println("--- 1. 제네릭 클래스 (Box<T>) ---");

        // String 타입의 Box
        Box<String> stringBox = new Box<>("Hello Generics");
        System.out.println("  String Box: " + stringBox);
        System.out.println("  값 꺼내기: " + stringBox.get());

        // Integer 타입의 Box
        Box<Integer> intBox = new Box<>(42);
        System.out.println("  Integer Box: " + intBox);

        // 다이아몬드 연산자 (<>) - 타입 추론
        Box<Double> doubleBox = new Box<>();
        doubleBox.set(3.14);
        System.out.println("  Double Box: " + doubleBox);
        System.out.println();

        // --- 2. 다중 타입 파라미터 ---
        System.out.println("--- 2. 다중 타입 파라미터 (Pair<K, V>) ---");
        Pair<String, Integer> pair1 = new Pair<>("나이", 25);
        Pair<Integer, String> pair2 = new Pair<>(1, "첫 번째");
        System.out.println("  " + pair1);
        System.out.println("  " + pair2);
        System.out.println();

        // --- 3. 바운디드 타입 파라미터 ---
        System.out.println("--- 3. 바운디드 타입 파라미터 (NumberBox) ---");
        NumberBox<Integer> intNumBox = new NumberBox<>(100);
        NumberBox<Double> doubleNumBox = new NumberBox<>(3.14);
        System.out.println("  " + intNumBox);
        System.out.println("  " + doubleNumBox);
        // NumberBox<String> strNumBox = new NumberBox<>("error"); // 컴파일 에러! String은 Number가 아님
        System.out.println("  NumberBox<String>은 컴파일 에러 (String은 Number 하위 타입이 아님)");
        System.out.println();

        // --- 4. 제네릭 메서드 ---
        System.out.println("--- 4. 제네릭 메서드 ---");
        List<String> names = List.of("김철수", "이영희", "박민수");
        String firstName = getFirst(names);
        System.out.println("  첫 번째 이름: " + firstName);

        List<Integer> numbers = List.of(10, 20, 30, 40, 50);
        double total = sum(numbers);
        System.out.println("  정수 리스트 합계: " + total);

        List<Double> doubles = List.of(1.1, 2.2, 3.3);
        double doubleTotal = sum(doubles);
        System.out.println("  실수 리스트 합계: " + doubleTotal);

        Pair<String, Integer> autoPair = makePair("점수", 95);
        System.out.println("  자동 타입 추론 Pair: " + autoPair);
        System.out.println();

        // --- 5. 와일드카드 ---
        System.out.println("--- 5. 와일드카드 ---");

        // 비한정 와일드카드 <?>
        System.out.println("  [비한정 와일드카드 <?>]");
        printList(List.of("a", "b", "c"));
        printList(List.of(1, 2, 3));

        // 상한 와일드카드 <? extends Number>
        System.out.println("  [상한 와일드카드 <? extends Number>] (Producer-Extends)");
        List<Integer> intList = List.of(1, 2, 3);
        List<Double> doubleList = List.of(1.5, 2.5, 3.5);
        System.out.println("  Integer 리스트 합계: " + sumWithWildcard(intList));
        System.out.println("  Double 리스트 합계: " + sumWithWildcard(doubleList));

        // 하한 와일드카드 <? super Integer>
        System.out.println("  [하한 와일드카드 <? super Integer>] (Consumer-Super)");
        List<Number> numberList = new ArrayList<>();
        addNumbers(numberList);  // Integer를 Number 리스트에 추가 가능
        System.out.println("  Number 리스트에 Integer 추가: " + numberList);

        // PECS 원칙 적용
        System.out.println("  [PECS 원칙 - copy 메서드]");
        List<Integer> source = List.of(10, 20, 30);
        List<Number> destination = new ArrayList<>();
        copy(source, destination);  // Integer -> Number 복사
        System.out.println("  source(Integer): " + source);
        System.out.println("  destination(Number): " + destination);

        System.out.println("\n========================================");
        System.out.println("  제네릭 예제 완료!");
        System.out.println("========================================");
    }
}
