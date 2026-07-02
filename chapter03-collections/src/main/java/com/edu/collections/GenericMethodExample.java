package com.edu.collections;

import java.util.List;

/**
 * Chapter 03 - 제네릭 메서드 (Generic Method) 예제
 *
 * [이 파일 한 가지 주제]
 *   "제네릭 메서드 + 타입 추론(Type Inference)"
 *
 * 제네릭 메서드란?
 *   - 클래스가 제네릭이 아니어도, "메서드 하나"만 제네릭으로 만들 수 있습니다.
 *   - 타입 파라미터를 "반환 타입 앞"에 선언합니다.
 *        public static <T> T getFirst(List<T> list)
 *                       ^^^  <- 여기! 이 메서드만의 타입 파라미터 선언
 *   - static 메서드도 자유롭게 제네릭으로 만들 수 있습니다.
 *
 * 타입 추론(Type Inference)이란?
 *   - 호출할 때 <타입>을 명시하지 않아도, 컴파일러가 "인자를 보고"
 *     타입 파라미터를 자동으로 알아냅니다.
 *   - 예) getFirst(names)  <- names 가 List<String> 이므로 T=String 으로 추론
 *   - 필요하면 명시할 수도 있습니다: GenericMethodExample.<String>getFirst(names)
 */
public class GenericMethodExample {

    // ------------------------------------------------------
    // 제네릭 메서드 1: 리스트의 첫 번째 요소 반환
    // <T> 를 반환 타입(T) 앞에 선언한다.
    // ------------------------------------------------------
    public static <T> T getFirst(List<T> list) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        return list.get(0);   // 반환 타입이 T 이므로 캐스팅 불필요
    }

    // ------------------------------------------------------
    // 제네릭 메서드 2: 두 값을 교환한 배열을 만들어 반환
    //  - 타입 파라미터가 인자 여러 곳에 함께 쓰이는 예
    // ------------------------------------------------------
    public static <T> T[] swapFirstTwo(T[] arr) {
        if (arr != null && arr.length >= 2) {
            T tmp = arr[0];
            arr[0] = arr[1];
            arr[1] = tmp;
        }
        return arr;
    }

    // ------------------------------------------------------
    // 제네릭 메서드 3: 여러 타입 파라미터 <A, B>
    //  - 서로 다른 타입 두 개를 받아 문자열로 묶어 반환
    // ------------------------------------------------------
    public static <A, B> String join(A first, B second) {
        return "(" + first + ", " + second + ")";
    }

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("  제네릭 메서드 + 타입 추론");
        System.out.println("========================================\n");

        // --- 1. 기본 제네릭 메서드 + 타입 추론 ---
        System.out.println("--- 1. 제네릭 메서드 getFirst (타입 추론) ---");

        // names 가 List<String> -> 컴파일러가 T=String 으로 추론
        List<String> names = List.of("김철수", "이영희", "박민수");
        String firstName = getFirst(names);   // 반환값도 String
        System.out.println("  첫 번째 이름(String): " + firstName);

        // numbers 가 List<Integer> -> T=Integer 로 추론
        List<Integer> numbers = List.of(10, 20, 30);
        Integer firstNum = getFirst(numbers);
        System.out.println("  첫 번째 숫자(Integer): " + firstNum);

        // 타입을 명시적으로 지정할 수도 있음 (보통은 생략)
        String explicit = GenericMethodExample.<String>getFirst(names);
        System.out.println("  타입 명시 호출 결과: " + explicit);
        System.out.println();

        // --- 2. 배열을 다루는 제네릭 메서드 ---
        System.out.println("--- 2. 제네릭 메서드 swapFirstTwo ---");
        String[] fruits = { "사과", "바나나", "체리" };
        swapFirstTwo(fruits);   // T=String 추론, 앞의 두 요소 교환
        System.out.print("  교환 후 배열: [");
        for (int i = 0; i < fruits.length; i++) {
            System.out.print(fruits[i]);
            if (i < fruits.length - 1) System.out.print(", ");
        }
        System.out.println("]");
        System.out.println();

        // --- 3. 여러 타입 파라미터를 가진 제네릭 메서드 ---
        System.out.println("--- 3. 여러 타입 파라미터 <A, B> join ---");
        System.out.println("  join(\"점수\", 95)    -> " + join("점수", 95));      // A=String, B=Integer
        System.out.println("  join(1, true)        -> " + join(1, true));         // A=Integer, B=Boolean
        System.out.println("  join(3.14, \"파이\")  -> " + join(3.14, "파이"));    // A=Double, B=String

        System.out.println("\n========================================");
        System.out.println("  제네릭 메서드 예제 완료!");
        System.out.println("========================================");
    }
}
