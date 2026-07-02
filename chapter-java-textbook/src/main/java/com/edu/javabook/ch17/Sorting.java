package com.edu.javabook.ch17;

import java.util.Comparator;
import java.util.List;

/**
 * 17.7 정렬
 *
 * [정렬(sorting)]
 * - sorted()                : 요소의 자연 순서(Comparable)로 정렬한다.
 * - sorted(Comparator)      : 지정한 비교 기준으로 정렬한다.
 * - Comparator.comparing    : 정렬 키를 지정한다.
 * - reversed()              : 정렬 순서를 뒤집는다(내림차순).
 * - thenComparing           : 1차 기준이 같을 때 2차 기준으로 추가 정렬한다.
 *
 * 이 소절에서는 자연 정렬, 비교자 정렬, reversed, thenComparing 을 시연한다.
 */
public class Sorting {

    // 정렬 시연용 간단한 데이터 클래스(이름, 나이)
    record Person(String name, int age) {}

    public static void main(String[] args) {

        System.out.println("=== 17.7 정렬 ===");

        // [1] 자연 순서 정렬: sorted()
        System.out.println("\n[1] sorted() (자연 순서 오름차순)");
        List<Integer> nums = List.of(5, 3, 8, 1, 9, 2);
        System.out.println("정렬 결과: " + nums.stream().sorted().toList());

        // [2] 내림차순: Comparator.reverseOrder() 또는 reversed()
        System.out.println("\n[2] 내림차순 정렬 (reversed)");
        System.out.println("정렬 결과: " +
                nums.stream().sorted(Comparator.reverseOrder()).toList());

        List<Person> people = List.of(
                new Person("kim", 30),
                new Person("lee", 25),
                new Person("park", 30),
                new Person("choi", 25)
        );

        // [3] Comparator.comparing: 나이 오름차순 정렬
        System.out.println("\n[3] Comparator.comparing (나이 오름차순)");
        people.stream()
                .sorted(Comparator.comparingInt(Person::age))
                .forEach(p -> System.out.println("  " + p));

        // [4] reversed + thenComparing: 나이 내림차순, 같으면 이름 오름차순
        System.out.println("\n[4] reversed + thenComparing (나이 내림차순, 동률이면 이름 오름차순)");
        people.stream()
                .sorted(Comparator.comparingInt(Person::age).reversed()
                        .thenComparing(Person::name))
                .forEach(p -> System.out.println("  " + p));

        System.out.println("\n프로그램 정상 종료");
    }
}
