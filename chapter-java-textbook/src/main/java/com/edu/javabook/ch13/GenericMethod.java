package com.edu.javabook.ch13;

import java.util.List;

/**
 * 13.3 제네릭 메소드
 *
 * 제네릭 메소드는 "메소드 하나"에 대해 타입 파라미터를 선언한 메소드다.
 * 클래스가 제네릭이 아니어도, 메소드 단위로 제네릭을 쓸 수 있다.
 *
 *   [제어자] <타입파라미터...> 반환타입 메소드명(파라미터...) { ... }
 *   예)     public static  <T>          T      pick(T a, T b) { ... }
 *
 * 핵심 : 반환타입 앞에 <T> 처럼 타입 파라미터를 먼저 선언한다.
 *
 * 타입 추론(Type Inference) :
 *   호출할 때 인자의 타입을 보고 컴파일러가 T 를 자동으로 알아낸다.
 *   그래서 대부분 <타입> 을 명시하지 않고 그냥 호출하면 된다.
 *   (필요하면 obj.<Integer>method(...) 처럼 명시적으로 지정할 수도 있다.)
 *
 * 이 소절에서는 제네릭 메소드 선언과 타입 추론을 확인한다.
 */
public class GenericMethod {

    /** 배열의 모든 원소를 출력하는 제네릭 메소드 */
    static <T> void printAll(T[] arr) {
        System.out.print("  [ ");
        for (T item : arr) System.out.print(item + " ");
        System.out.println("]");
    }

    /** 두 값 중 하나를 그대로 돌려주는 제네릭 메소드 (반환 타입도 T) */
    static <T> T firstOf(T a, T b) {
        return a;
    }

    /** 두 타입 파라미터를 쓰는 제네릭 메소드 : 배열에서 특정 값의 존재 여부 */
    static <T> boolean contains(T[] arr, T target) {
        for (T item : arr) {
            if (item.equals(target)) return true;
        }
        return false;
    }

    /** 서로 다른 두 타입 파라미터 <K, V> 를 쓰는 제네릭 메소드 */
    static <K, V> void printEntry(K key, V value) {
        System.out.println("  " + key + " -> " + value
                + "  (" + key.getClass().getSimpleName()
                + ", " + value.getClass().getSimpleName() + ")");
    }

    public static void main(String[] args) {

        System.out.println("=== 13.3 제네릭 메소드 ===");

        // [1] 제네릭 메소드 호출 + 타입 추론
        System.out.println("\n[1] 제네릭 메소드와 타입 추론");
        Integer[] nums = { 10, 20, 30 };
        String[]  strs = { "가", "나", "다" };
        System.out.println("정수 배열 출력 (T = Integer 로 추론):");
        printAll(nums);                     // T 를 명시하지 않아도 Integer 로 추론
        System.out.println("문자열 배열 출력 (T = String 으로 추론):");
        printAll(strs);

        // [2] 반환 타입이 T 인 제네릭 메소드
        System.out.println("\n[2] 반환 타입이 T 인 제네릭 메소드");
        String picked = firstOf("첫째", "둘째");   // T = String
        Integer pickedNum = firstOf(100, 200);       // T = Integer
        System.out.println("firstOf(문자열): " + picked);
        System.out.println("firstOf(정수)  : " + pickedNum);

        // [3] 명시적 타입 지정(보통 생략하지만 가능하다)
        System.out.println("\n[3] 타입 명시적 지정");
        String explicit = GenericMethod.<String>firstOf("명시", "지정");
        System.out.println("<String>firstOf(...) 결과: " + explicit);

        // [4] 존재 여부 확인 제네릭 메소드
        System.out.println("\n[4] contains 제네릭 메소드");
        System.out.println("정수 배열에 20 이 있는가? " + contains(nums, 20));
        System.out.println("문자열 배열에 '라' 가 있는가? " + contains(strs, "라"));

        // [5] 두 타입 파라미터 <K, V> 제네릭 메소드
        System.out.println("\n[5] 서로 다른 타입 파라미터 <K, V>");
        printEntry("이름", "홍길동");
        printEntry("점수", 95);

        // [6] JDK 표준 라이브러리의 제네릭 메소드 예시
        System.out.println("\n[6] JDK 표준의 제네릭 메소드 (List.of)");
        List<String> list = List.of("A", "B", "C");   // <T> List<T> of(...) 로 추론
        System.out.println("List.of 결과: " + list + " (크기: " + list.size() + ")");

        System.out.println("\n프로그램 정상 종료");
    }
}
