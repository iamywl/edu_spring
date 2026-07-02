package com.edu.javabook.ch17;

import java.util.List;

/**
 * 17.8 루핑
 *
 * [루핑(looping)]
 * - forEach(Consumer) : 최종 연산. 각 요소에 대해 지정한 동작을 수행한다(주로 출력/처리).
 * - peek(Consumer)    : 중간 연산. 요소를 소비하지 않고 "들여다보기"만 한다(디버깅/로그에 유용).
 *
 * [주의]
 * - forEach 는 최종 연산이므로 이후 스트림을 더 쓸 수 없다.
 * - peek 는 중간 연산이라 최종 연산이 있어야 실제로 실행된다(지연 평가).
 *
 * 이 소절에서는 forEach 로 요소 처리, peek 로 파이프라인 중간 관찰을 시연한다.
 */
public class Looping {

    public static void main(String[] args) {

        System.out.println("=== 17.8 루핑 ===");

        List<String> items = List.of("연필", "지우개", "공책");

        // [1] forEach: 각 요소를 순회하며 출력 (최종 연산)
        System.out.println("\n[1] forEach (각 요소 처리)");
        items.forEach(item -> System.out.println("  구매: " + item));

        // [2] peek: 중간 단계 관찰 후 변환하여 최종 수집
        System.out.println("\n[2] peek (중간 관찰) + map + 최종 수집");
        List<Integer> lengths = items.stream()
                .peek(item -> System.out.println("  [원본] " + item))     // 중간 관찰
                .map(String::length)
                .peek(len -> System.out.println("  [길이] " + len))       // 변환 후 관찰
                .toList();
        System.out.println("길이 목록: " + lengths);
        System.out.println("→ peek 는 요소를 바꾸지 않고 들여다보기만 한다(디버깅용).");

        System.out.println("\n프로그램 정상 종료");
    }
}
