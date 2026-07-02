package com.edu.collections;

import java.util.*;
import java.util.stream.*;

/**
 * Chapter 03 - Stream 중간 연산 예제 (StreamIntermediateExample)
 *
 * ┌─────────────────────────────────────────────────────────────┐
 * │ 이 파일은 "중간 연산(Intermediate Operations)"에만 집중한다. │
 * └─────────────────────────────────────────────────────────────┘
 *
 * ▶ 중간 연산이란?
 *   - 스트림을 입력받아 또 다른 스트림을 반환하는 연산이다.
 *   - 따라서 여러 개를 점(.)으로 연결(체이닝)할 수 있다.
 *   - 대표적으로 filter / map / flatMap / distinct / sorted / limit / skip / peek 가 있다.
 *
 * ▶ 지연 평가(Lazy Evaluation) - 매우 중요한 개념
 *   - 중간 연산은 "당장 실행되지 않는다". 그저 "무엇을 할지"를 예약해 둘 뿐이다.
 *   - 최종 연산(terminal operation, 예: toList(), forEach(), count())이 호출되는
 *     순간에야 비로소 모든 중간 연산이 한꺼번에 흐르며 실행된다.
 *   - 덕분에 요소 하나가 filter -> map -> ... 파이프라인을 통째로 통과하는
 *     "요소 단위(element-wise)" 처리가 가능해 불필요한 계산을 피할 수 있다.
 *     (예: limit(1)이 있으면 필요한 만큼만 계산하고 멈춘다 = short-circuit)
 *
 * 참고: .toList()는 Java 16+ 방식이며 수정 불가 리스트를 반환한다.
 */
public class StreamIntermediateExample {

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("  Chapter 03 - 중간 연산 (Intermediate)");
        System.out.println("========================================\n");

        // --------------------------------------------------
        // 0. 지연 평가(Lazy) 증명 - 중간 연산만 있으면 아무 일도 안 일어난다
        // --------------------------------------------------
        System.out.println("--- 0. 지연 평가(Lazy) 증명 ---");
        System.out.println("  [중간 연산만 걸고 최종 연산은 호출하지 않음]");
        Stream<Integer> lazyStream = Stream.of(1, 2, 3)
                .peek(n -> System.out.println("    peek 실행됨: " + n)); // 최종 연산 없으면 출력 X
        System.out.println("  -> 위에 peek 로그가 하나도 안 보이면 정상 (아직 실행 전)");

        System.out.println("  [이제 최종 연산 toList() 호출]");
        lazyStream.toList(); // 이 순간 비로소 peek가 실행된다
        System.out.println("  -> toList() 호출 후에야 peek 로그가 출력됨");
        System.out.println();

        List<String> names = List.of("김철수", "이영희", "박민수", "김영희", "이철수", "김민수");

        // --------------------------------------------------
        // 1. filter - 조건에 맞는 요소만 통과시킴
        // --------------------------------------------------
        System.out.println("--- 1. filter (조건 통과) ---");
        List<String> kims = names.stream()
                .filter(name -> name.startsWith("김"))
                .toList();
        System.out.println("  김씨만 : " + kims);
        System.out.println();

        // --------------------------------------------------
        // 2. map - 각 요소를 다른 값으로 "변환"
        // --------------------------------------------------
        System.out.println("--- 2. map (변환) ---");
        // 요소 하나 -> 결과 하나 (1:1 변환)
        List<Integer> nameLengths = names.stream()
                .map(String::length)
                .toList();
        System.out.println("  이름 길이 : " + nameLengths);

        List<String> upper = Stream.of("java", "stream")
                .map(String::toUpperCase)
                .toList();
        System.out.println("  대문자화 : " + upper);
        System.out.println();

        // --------------------------------------------------
        // 3. flatMap - 중첩 구조를 "평탄화"
        // --------------------------------------------------
        System.out.println("--- 3. flatMap (평탄화) ---");
        // map은 1:1, flatMap은 "요소 하나 -> 여러 개(스트림)"를 펼쳐 하나의 스트림으로 합친다.
        List<List<Integer>> nested = List.of(
                List.of(1, 2, 3),
                List.of(4, 5),
                List.of(6, 7, 8, 9)
        );
        List<Integer> flattened = nested.stream()
                .flatMap(Collection::stream) // 각 내부 리스트를 스트림으로 펼쳐 합침
                .toList();
        System.out.println("  중첩 리스트 평탄화 : " + flattened);

        // 문장 -> 단어로 분해하는 대표적 예시
        List<String> sentences = List.of("Hello World", "Java Stream");
        List<String> words = sentences.stream()
                .flatMap(sentence -> Arrays.stream(sentence.split(" ")))
                .toList();
        System.out.println("  문장 -> 단어       : " + words);
        System.out.println();

        // --------------------------------------------------
        // 4. distinct - 중복 제거 (equals/hashCode 기준)
        // --------------------------------------------------
        System.out.println("--- 4. distinct (중복 제거) ---");
        List<Integer> withDups = List.of(1, 2, 2, 3, 3, 3, 4);
        List<Integer> distinct = withDups.stream()
                .distinct()
                .toList();
        System.out.println("  " + withDups + " -> " + distinct);
        System.out.println();

        // --------------------------------------------------
        // 5. sorted - 정렬
        // --------------------------------------------------
        System.out.println("--- 5. sorted (정렬) ---");
        List<Integer> numbers = List.of(5, 3, 8, 1, 9, 2);
        System.out.println("  오름차순 : " + numbers.stream().sorted().toList());
        System.out.println("  내림차순 : " + numbers.stream().sorted(Comparator.reverseOrder()).toList());
        System.out.println();

        // --------------------------------------------------
        // 6. limit / skip - 개수 제한 및 앞부분 건너뛰기
        // --------------------------------------------------
        System.out.println("--- 6. limit / skip ---");
        List<Integer> range = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        System.out.println("  limit(3)          : " + range.stream().limit(3).toList());
        System.out.println("  skip(7)           : " + range.stream().skip(7).toList());
        System.out.println("  skip(3).limit(4)  : " + range.stream().skip(3).limit(4).toList());
        System.out.println();

        // --------------------------------------------------
        // 7. peek - 파이프라인 중간을 "엿보기"(디버깅용)
        // --------------------------------------------------
        System.out.println("--- 7. peek (디버깅) ---");
        // peek은 요소를 소비하지 않고 그대로 흘려보내며, 지나가는 값을 관찰만 한다.
        System.out.print("  통과 순서 관찰 : ");
        List<Integer> peekResult = Stream.of(1, 2, 3, 4, 5)
                .peek(n -> System.out.print("[in:" + n + "] "))
                .filter(n -> n % 2 == 0)
                .peek(n -> System.out.print("[통과:" + n + "] "))
                .toList();
        System.out.println("-> 결과: " + peekResult);
        System.out.println();

        // --------------------------------------------------
        // 8. 연산 체이닝 - 중간 연산을 이어붙여 파이프라인 구성
        // --------------------------------------------------
        System.out.println("--- 8. 연산 체이닝 ---");
        System.out.println("  [김씨 이름을 정렬하고 '님'을 붙이기]");
        List<String> chained = names.stream()
                .filter(name -> name.startsWith("김"))
                .sorted()
                .map(name -> name + " 님")
                .toList();
        System.out.println("  결과 : " + chained);
        System.out.println();

        System.out.println("========================================");
        System.out.println("  중간 연산 예제 완료!");
        System.out.println("========================================");
    }
}
