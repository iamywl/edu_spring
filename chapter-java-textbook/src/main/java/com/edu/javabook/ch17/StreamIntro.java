package com.edu.javabook.ch17;

import java.util.List;

/**
 * 17.1 스트림이란
 *
 * [스트림(Stream)이란]
 * - 컬렉션, 배열 등의 데이터 요소를 하나씩 흘려보내며(stream) 처리하는 반복자다.
 * - "무엇을 할 것인가"를 기술하는 선언적(declarative) 처리 방식이다.
 *   (전통적인 for 문은 "어떻게 반복할 것인가"를 명령적으로 기술한다.)
 *
 * [파이프라인(pipeline) 개념]
 * - 스트림은 여러 연산을 연결(체이닝)하여 하나의 처리 흐름을 만든다.
 *   소스(source) → 중간 연산(filter/map/sorted ...) → 최종 연산(forEach/collect ...)
 * - 데이터가 파이프를 통과하듯 한 단계씩 흘러가며 가공된다.
 *
 * 이 소절에서는 명령형 처리와 스트림(선언형) 처리를 비교하고 파이프라인을 시연한다.
 */
public class StreamIntro {

    public static void main(String[] args) {

        System.out.println("=== 17.1 스트림이란 ===");

        List<String> names = List.of("kim", "lee", "park", "choi", "an");

        // [1] 전통적인 명령형(imperative) 처리: 어떻게 반복할지 직접 기술
        System.out.println("\n[1] 명령형(for 문) 처리");
        int count = 0;
        for (String name : names) {
            if (name.length() >= 3) {           // 조건 검사
                System.out.println("  " + name.toUpperCase());
                count++;
            }
        }
        System.out.println("길이 3 이상 개수: " + count);

        // [2] 선언형(스트림) 처리: 무엇을 할지 파이프라인으로 기술
        System.out.println("\n[2] 선언형(스트림) 처리");
        long streamCount = names.stream()          // 소스: 컬렉션으로부터 스트림 생성
                .filter(name -> name.length() >= 3) // 중간 연산: 필터링
                .map(String::toUpperCase)           // 중간 연산: 대문자 매핑
                .peek(name -> System.out.println("  " + name)) // 흐름 확인용
                .count();                           // 최종 연산: 개수 집계
        System.out.println("길이 3 이상 개수: " + streamCount);

        // [3] 파이프라인 구조 설명
        System.out.println("\n[3] 파이프라인 구조");
        System.out.println("소스(stream) → 중간연산(filter, map) → 최종연산(count)");
        System.out.println("→ 코드가 데이터 처리 의도를 그대로 드러내어 읽기 쉽다.");

        System.out.println("\n프로그램 정상 종료");
    }
}
