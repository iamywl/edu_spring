package com.edu.collections;

import java.util.*;
import java.util.stream.*;

/**
 * Chapter 03 - Stream 생성 예제 (StreamCreationExample)
 *
 * ┌─────────────────────────────────────────────────────────────┐
 * │ 이 파일은 "스트림을 어떻게 만드는가"에만 집중한다.            │
 * └─────────────────────────────────────────────────────────────┘
 *
 * ▶ 스트림(Stream)이란?
 *   - 데이터의 "흐름(연속된 요소의 시퀀스)"을 표현하는 API이다.
 *   - 컬렉션(List/Set 등)이 "데이터를 저장하는 것"이라면,
 *     스트림은 그 데이터를 "어떻게 처리할지"를 표현한다.
 *   - 스트림 자체는 데이터를 저장하지 않는다. 원본(소스)에서 요소를 끌어와 흘려보낸다.
 *
 * ▶ 왜 "선언적(declarative)"인가?
 *   - 명령형(imperative) 코드는 "어떻게(how)" 반복하고 조건을 검사할지 일일이 지시한다.
 *       for (int i = 0; i < list.size(); i++) { if (...) { ... } }
 *   - 선언형(declarative) 스트림은 "무엇(what)"을 원하는지만 기술한다.
 *       list.stream().filter(...).map(...).toList();
 *   - 루프 인덱스, 임시 변수, 반복 제어 로직이 사라져 의도가 그대로 드러난다.
 *
 * 참고: .toList()는 Java 16+의 간결한 방식이며 수정 불가(unmodifiable) 리스트를 반환한다.
 *      결과를 나중에 수정(add/remove)해야 한다면 collect(Collectors.toList())를 쓴다.
 */
public class StreamCreationExample {

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("  Chapter 03 - Stream 생성 (Creation)");
        System.out.println("========================================\n");

        // --------------------------------------------------
        // 0. 명령형 vs 선언형 비교 (왜 스트림을 쓰는가)
        // --------------------------------------------------
        System.out.println("--- 0. 명령형 vs 선언형 ---");
        List<String> fruits = List.of("apple", "banana", "avocado", "cherry");

        // 명령형: 'a'로 시작하는 과일을 직접 골라 담는다 (어떻게 할지 일일이 지시)
        List<String> imperative = new ArrayList<>();
        for (String f : fruits) {
            if (f.startsWith("a")) {
                imperative.add(f);
            }
        }
        System.out.println("  명령형(for+if)  : " + imperative);

        // 선언형: "a로 시작하는 것만" 이라는 의도만 기술
        List<String> declarative = fruits.stream()
                .filter(f -> f.startsWith("a"))
                .toList();
        System.out.println("  선언형(stream)  : " + declarative);
        System.out.println();

        // --------------------------------------------------
        // 1. 컬렉션에서 스트림 생성 - 가장 흔한 방식
        // --------------------------------------------------
        System.out.println("--- 1. 컬렉션에서 생성 (collection.stream()) ---");
        List<String> list = List.of("a", "b", "c");
        Stream<String> streamFromList = list.stream();
        System.out.println("  list.stream()         : " + streamFromList.toList());

        Set<Integer> set = Set.of(10, 20, 30);
        System.out.println("  set.stream()          : " + set.stream().sorted().toList());
        System.out.println();

        // --------------------------------------------------
        // 2. Stream.of() - 값들을 직접 나열해 생성
        // --------------------------------------------------
        System.out.println("--- 2. Stream.of() ---");
        Stream<String> streamOf = Stream.of("x", "y", "z");
        System.out.println("  Stream.of(\"x\",\"y\",\"z\"): " + streamOf.toList());
        // 단일 배열을 넘기면 원소들의 스트림이 된다
        System.out.println("  Stream.of(1,2,3)      : " + Stream.of(1, 2, 3).toList());
        System.out.println();

        // --------------------------------------------------
        // 3. 배열에서 생성 - Arrays.stream()
        // --------------------------------------------------
        System.out.println("--- 3. 배열에서 생성 (Arrays.stream()) ---");
        int[] intArray = {1, 2, 3, 4, 5};
        // int[] 는 IntStream(기본형 특화 스트림)이 되어 sum() 같은 편의 메서드를 쓸 수 있다.
        int sum = Arrays.stream(intArray).sum();
        System.out.println("  Arrays.stream(intArray).sum() : " + sum);

        String[] strArray = {"Java", "Stream", "API"};
        System.out.println("  Arrays.stream(strArray)       : " + Arrays.stream(strArray).toList());
        System.out.println();

        // --------------------------------------------------
        // 4. IntStream.range / rangeClosed - 숫자 범위 스트림
        // --------------------------------------------------
        System.out.println("--- 4. IntStream 범위 스트림 ---");
        // range(1, 5)      -> 1,2,3,4      (끝 미포함)
        // rangeClosed(1,5) -> 1,2,3,4,5    (끝 포함)
        // boxed()는 int -> Integer 로 박싱하여 List<Integer> 로 모을 수 있게 한다.
        List<Integer> rangeExclusive = IntStream.range(1, 5).boxed().toList();
        List<Integer> rangeClosed = IntStream.rangeClosed(1, 5).boxed().toList();
        System.out.println("  IntStream.range(1, 5)         : " + rangeExclusive);
        System.out.println("  IntStream.rangeClosed(1, 5)   : " + rangeClosed);
        System.out.println();

        // --------------------------------------------------
        // 5. Stream.iterate() - 시드값 + 함수로 무한 스트림
        // --------------------------------------------------
        System.out.println("--- 5. Stream.iterate() ---");
        // 무한 스트림이므로 반드시 limit 등으로 잘라야 한다.
        List<Integer> iterateResult = Stream.iterate(0, n -> n + 2)
                .limit(5)
                .toList();
        System.out.println("  iterate(0, n->n+2).limit(5)   : " + iterateResult);

        // Java 9+ : 종료 조건(predicate)을 가진 iterate (for문처럼 동작)
        List<Integer> iterateWithPredicate = Stream.iterate(1, n -> n <= 100, n -> n * 2)
                .toList();
        System.out.println("  iterate(1, n<=100, n*2)       : " + iterateWithPredicate);
        System.out.println();

        // --------------------------------------------------
        // 6. Stream.generate() - Supplier로 무한 스트림
        // --------------------------------------------------
        System.out.println("--- 6. Stream.generate() ---");
        // 이전 값과 무관하게 매번 새 값을 공급한다. 역시 limit 필수.
        List<Double> randomNumbers = Stream.generate(Math::random)
                .limit(3)
                .toList();
        System.out.println("  generate(Math::random).limit(3): " + randomNumbers);
        System.out.println();

        // --------------------------------------------------
        // 7. 문자열에서 스트림 생성 - String.chars()
        // --------------------------------------------------
        System.out.println("--- 7. 문자열에서 생성 (String.chars()) ---");
        // chars()는 각 문자를 int 코드값으로 흘려보내는 IntStream을 만든다.
        long charCount = "Hello World".chars()
                .filter(c -> c != ' ')
                .count();
        System.out.println("  \"Hello World\" 공백 제외 문자 수 : " + charCount);
        System.out.println();

        System.out.println("========================================");
        System.out.println("  Stream 생성 예제 완료!");
        System.out.println("========================================");
    }
}
