package com.edu.javabook.ch15;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 15.8 수정할 수 없는 컬렉션
 *
 * "수정할 수 없는(불변, unmodifiable/immutable) 컬렉션"은 생성 후 내용을 바꿀 수 없다.
 * add / remove / set / put 을 호출하면 UnsupportedOperationException 이 발생한다.
 *
 * 왜 쓰는가?
 *  - 실수로(또는 외부에서) 데이터가 변경되는 것을 막아 "안전"하게 공유할 수 있다.
 *  - 상수 목록, 설정값처럼 변하면 안 되는 데이터에 적합하다.
 *
 * 만드는 방법 :
 *  1) List.of(...) / Set.of(...) / Map.of(...)  : 처음부터 불변으로 생성 (JDK 9+).
 *  2) Collections.unmodifiableList/Set/Map(원본) : 기존 컬렉션의 "읽기 전용 뷰".
 *
 * 이 소절에서는 불변 컬렉션과 수정 시도 시의 예외를 확인한다.
 */
public class UnmodifiableCollection {

    public static void main(String[] args) {

        System.out.println("=== 15.8 수정할 수 없는 컬렉션 ===");

        // [1] List.of - 처음부터 불변
        System.out.println("\n[1] List.of / Set.of / Map.of (불변 생성)");
        List<String> days = List.of("월", "화", "수");
        Set<Integer> nums = Set.of(1, 2, 3);
        Map<String, Integer> ages = Map.of("철수", 20, "영희", 21);
        System.out.println("List.of: " + days);
        System.out.println("Set.of : " + nums);
        System.out.println("Map.of : " + ages);
        System.out.println("읽기는 자유롭다 → days.get(0) = " + days.get(0));

        // [2] 수정 시도 → UnsupportedOperationException
        System.out.println("\n[2] 수정 시도하면 예외 발생");
        try {
            days.add("목");                 // 불변 리스트에 추가 시도
        } catch (UnsupportedOperationException e) {
            System.out.println("days.add(\"목\") → UnsupportedOperationException 발생!");
        }

        // [3] Collections.unmodifiableList - 읽기 전용 뷰
        System.out.println("\n[3] Collections.unmodifiableList (읽기 전용 뷰)");
        List<String> origin = new ArrayList<>();
        origin.add("A"); origin.add("B");
        List<String> readOnly = Collections.unmodifiableList(origin);
        System.out.println("읽기 전용 뷰: " + readOnly);
        try {
            readOnly.set(0, "Z");           // 뷰를 통한 수정 시도
        } catch (UnsupportedOperationException e) {
            System.out.println("readOnly.set(...) → UnsupportedOperationException 발생!");
        }

        // [4] 주의 : unmodifiable 뷰는 원본이 바뀌면 함께 바뀐다
        System.out.println("\n[4] 주의 - 뷰는 원본 변경이 반영된다");
        origin.add("C");                    // 원본을 수정
        System.out.println("원본에 'C' 추가 후 readOnly: " + readOnly + " (뷰에도 반영됨)");
        System.out.println("→ 완전한 불변이 필요하면 List.of 나 List.copyOf 를 사용한다.");

        System.out.println("\n프로그램 정상 종료");
    }
}
