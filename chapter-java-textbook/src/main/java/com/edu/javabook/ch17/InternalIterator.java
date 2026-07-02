package com.edu.javabook.ch17;

import java.util.List;

/**
 * 17.2 내부 반복자
 *
 * [외부 반복자(external iterator) vs 내부 반복자(internal iterator)]
 * - 외부 반복자 : 개발자가 for/while, Iterator로 요소를 "직접 꺼내며" 반복한다.
 *                반복의 제어권이 코드(개발자)에게 있다.
 * - 내부 반복자 : 스트림이 요소 반복을 "대신" 수행한다.
 *                개발자는 각 요소에 무엇을 할지(함수)만 넘기고, 반복은 스트림에게 위임한다.
 *
 * [위임(delegation)의 장점]
 * - 반복 방식(순서/병렬 여부 등)을 라이브러리가 최적화하여 결정할 수 있다.
 * - 개발자는 "처리 로직"에만 집중하면 된다.
 *
 * 이 소절에서는 같은 작업을 외부 반복과 내부 반복으로 각각 구현해 비교한다.
 */
public class InternalIterator {

    public static void main(String[] args) {

        System.out.println("=== 17.2 내부 반복자 ===");

        List<Integer> numbers = List.of(1, 2, 3, 4, 5, 6);

        // [1] 외부 반복자: for-each 로 개발자가 직접 요소를 꺼내 반복 제어
        System.out.println("\n[1] 외부 반복자 (for-each, 제어권은 개발자)");
        int externalSum = 0;
        for (int n : numbers) {          // 반복을 개발자가 직접 수행
            externalSum += n;
        }
        System.out.println("합계: " + externalSum);

        // [2] 외부 반복자: Iterator 로 명시적으로 hasNext/next 호출
        System.out.println("\n[2] 외부 반복자 (Iterator, 더 명시적)");
        var it = numbers.iterator();
        int itSum = 0;
        while (it.hasNext()) {            // 다음 요소가 있는지 개발자가 확인
            itSum += it.next();          // 다음 요소를 개발자가 꺼냄
        }
        System.out.println("합계: " + itSum);

        // [3] 내부 반복자: 반복을 스트림에 위임, 개발자는 처리 로직(람다)만 제공
        System.out.println("\n[3] 내부 반복자 (스트림에 반복을 위임)");
        int internalSum = numbers.stream()  // 반복 제어를 스트림에게 위임
                .mapToInt(Integer::intValue)
                .sum();                     // 요소를 어떻게 꺼내 도는지는 스트림이 담당
        System.out.println("합계: " + internalSum);
        System.out.println("→ 개발자는 '무엇을 할지'만 넘기고, '어떻게 반복할지'는 스트림이 처리한다.");

        System.out.println("\n프로그램 정상 종료");
    }
}
