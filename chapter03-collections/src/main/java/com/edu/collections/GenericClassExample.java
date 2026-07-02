package com.edu.collections;

/**
 * Chapter 03 - 제네릭 클래스 (Generic Class) 예제
 *
 * [이 파일 한 가지 주제]
 *   "제네릭 클래스 / 타입 파라미터 <T>"
 *
 * 제네릭(Generic)이란?
 *   - 클래스나 메서드를 정의할 때 사용할 타입을 "확정하지 않고"
 *     사용하는 시점에 결정하도록 하는 기능입니다.
 *   - <T> 처럼 꺾쇠 안에 "타입 파라미터"를 선언합니다.
 *   - T 는 Type 의 약자로 관례적으로 쓰는 이름입니다.
 *     (E=Element, K=Key, V=Value, N=Number 등도 관례)
 *
 * 왜 쓰는가? (제네릭의 핵심 이점 = 타입 안전성 + 캐스팅 제거)
 *   - 제네릭이 없던 시절에는 Object 로 값을 담고,
 *     꺼낼 때마다 (String) 처럼 강제 캐스팅을 해야 했습니다.
 *   - 캐스팅은 런타임에 ClassCastException 을 일으킬 위험이 있습니다.
 *   - 제네릭을 쓰면 컴파일 시점에 타입을 검사하므로
 *     잘못된 타입을 넣으면 아예 컴파일이 안 됩니다. (안전!)
 *   - 꺼낼 때 캐스팅이 필요 없습니다. (편리!)
 */
public class GenericClassExample {

    // ======================================================
    // 1. 제네릭 클래스 - Box<T>
    // ======================================================
    /**
     * 어떤 타입이든 담을 수 있는 상자 클래스.
     * T 는 타입 파라미터로, 사용 시점에 구체적인 타입으로 대체됩니다.
     *   - Box<String> 이면 T -> String
     *   - Box<Integer> 이면 T -> Integer
     */
    static class Box<T> {
        private T value;

        public Box() {}

        public Box(T value) {
            this.value = value;
        }

        public T get() {
            return value;   // 꺼낼 때 캐스팅 불필요 (이미 T 타입임이 보장됨)
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
     * 두 개의 타입 파라미터를 가지는 쌍(Pair) 클래스.
     * 타입 파라미터는 여러 개를 쉼표로 나열할 수 있습니다: <K, V>
     *   - K: Key(키) 타입
     *   - V: Value(값) 타입
     * 예) Map 의 한 항목(entry)처럼 "키-값" 묶음을 표현할 때 유용합니다.
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
    // 3. (비교용) 제네릭이 없다면? - Object 를 쓰는 낡은 방식
    // ======================================================
    /**
     * 제네릭 이전에는 이렇게 Object 로 담았습니다.
     * -> 꺼낼 때 매번 캐스팅이 필요하고, 잘못된 캐스팅은 런타임 예외를 냅니다.
     */
    static class ObjectBox {
        private Object value;
        public ObjectBox(Object value) { this.value = value; }
        public Object get() { return value; }
    }

    // ======================================================
    // main 메서드
    // ======================================================
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("  제네릭 클래스 / 타입 파라미터 <T>");
        System.out.println("========================================\n");

        // --- 1. 제네릭 클래스 Box<T> ---
        System.out.println("--- 1. 제네릭 클래스 (Box<T>) ---");

        // String 타입의 Box (T -> String)
        Box<String> stringBox = new Box<>("Hello Generics");
        System.out.println("  String Box: " + stringBox);
        System.out.println("  값 꺼내기(캐스팅 불필요): " + stringBox.get());

        // Integer 타입의 Box (T -> Integer)
        Box<Integer> intBox = new Box<>(42);
        System.out.println("  Integer Box: " + intBox);

        // 다이아몬드 연산자 <> - 우변의 타입 파라미터를 생략하면
        // 좌변을 보고 컴파일러가 타입을 추론합니다. (Java 7+)
        Box<Double> doubleBox = new Box<>();
        doubleBox.set(3.14);
        System.out.println("  Double Box(다이아몬드 <>): " + doubleBox);
        System.out.println();

        // --- 2. 다중 타입 파라미터 Pair<K, V> ---
        System.out.println("--- 2. 다중 타입 파라미터 (Pair<K, V>) ---");
        Pair<String, Integer> pair1 = new Pair<>("나이", 25);   // K=String, V=Integer
        Pair<Integer, String> pair2 = new Pair<>(1, "첫 번째");  // K=Integer, V=String
        System.out.println("  " + pair1 + "  -> key 타입=String, value 타입=Integer");
        System.out.println("  " + pair2 + "  -> key 타입=Integer, value 타입=String");
        System.out.println();

        // --- 3. 타입 안전성이란? (제네릭 vs Object) ---
        System.out.println("--- 3. 타입 안전성이란? (제네릭 vs Object) ---");

        // (가) 제네릭 방식: 잘못된 타입은 아예 컴파일 불가 -> 안전
        Box<String> safe = new Box<>("문자열만");
        String s = safe.get();   // 캐스팅 없이 바로 String
        System.out.println("  제네릭 Box: 꺼낼 때 캐스팅 불필요 -> \"" + s + "\"");
        System.out.println("  (참고) safe.set(123) 은 컴파일 에러! -> String 자리에 int 불가");

        // (나) Object 방식: 컴파일은 되지만 잘못된 캐스팅은 런타임 폭탄
        ObjectBox objBox = new ObjectBox("사실은 문자열");
        String ok = (String) objBox.get();   // 올바른 캐스팅
        System.out.println("  ObjectBox: 꺼낼 때 강제 캐스팅 필요 -> \"" + ok + "\"");
        try {
            // 실제로는 String 인데 Integer 로 캐스팅 시도 -> 런타임 예외!
            Integer wrong = (Integer) objBox.get();
            System.out.println("  " + wrong);
        } catch (ClassCastException e) {
            System.out.println("  잘못된 캐스팅 -> ClassCastException 발생! (" + e.getMessage() + ")");
            System.out.println("  => 제네릭을 썼다면 이런 실수는 컴파일 단계에서 막힙니다.");
        }

        System.out.println("\n========================================");
        System.out.println("  제네릭 클래스 예제 완료!");
        System.out.println("========================================");
    }
}
