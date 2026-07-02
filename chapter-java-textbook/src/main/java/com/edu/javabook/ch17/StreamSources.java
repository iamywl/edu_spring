package com.edu.javabook.ch17;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * 17.4 리소스로부터 스트림 얻기
 *
 * [스트림을 얻는 대표적인 방법]
 * - 컬렉션         : collection.stream()
 * - 배열(객체 배열) : Arrays.stream(array)
 * - 배열(기본형)   : Arrays.stream(int[]) → IntStream
 * - 나열된 값      : Stream.of(v1, v2, ...)
 * - 정수 범위      : IntStream.range(a, b) / IntStream.rangeClosed(a, b)
 *
 * 이 소절에서는 다양한 리소스로부터 스트림을 얻는 방법을 각각 시연한다.
 */
public class StreamSources {

    public static void main(String[] args) {

        System.out.println("=== 17.4 리소스로부터 스트림 얻기 ===");

        // [1] 컬렉션으로부터: List.stream()
        System.out.println("\n[1] 컬렉션 → stream()");
        List<String> list = List.of("사과", "바나나", "포도");
        list.stream().forEach(s -> System.out.println("  " + s));

        // [2] 객체 배열로부터: Arrays.stream()
        System.out.println("\n[2] 객체 배열 → Arrays.stream()");
        String[] fruits = {"수박", "참외", "딸기"};
        Arrays.stream(fruits).forEach(s -> System.out.println("  " + s));

        // [3] 기본형 배열로부터: Arrays.stream(int[]) → IntStream
        System.out.println("\n[3] int 배열 → Arrays.stream() (IntStream)");
        int[] scores = {90, 80, 70};
        int total = Arrays.stream(scores).sum();
        System.out.println("  점수 합계: " + total);

        // [4] 나열된 값으로부터: Stream.of()
        System.out.println("\n[4] 나열된 값 → Stream.of()");
        Stream.of("빨강", "초록", "파랑").forEach(s -> System.out.println("  " + s));

        // [5] 정수 범위로부터: IntStream.range / rangeClosed
        System.out.println("\n[5] 정수 범위 → IntStream.range / rangeClosed");
        System.out.print("  range(1,5)      : ");
        IntStream.range(1, 5).forEach(n -> System.out.print(n + " "));   // 1 2 3 4
        System.out.println("(끝값 미포함)");
        System.out.print("  rangeClosed(1,5): ");
        IntStream.rangeClosed(1, 5).forEach(n -> System.out.print(n + " ")); // 1 2 3 4 5
        System.out.println("(끝값 포함)");

        System.out.println("\n프로그램 정상 종료");
    }
}
