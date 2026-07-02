package com.edu.collections;

import java.util.*;
import java.util.stream.*;

/**
 * Chapter 03 - Collectors 예제 (CollectorsExample)
 *
 * ┌─────────────────────────────────────────────────────────────┐
 * │ 이 파일은 종단 연산 collect()와 Collectors 유틸에만 집중한다.│
 * └─────────────────────────────────────────────────────────────┘
 *
 * ▶ collect(Collector) 란?
 *   - 스트림의 요소들을 "누적/수집"하여 컬렉션, 맵, 문자열, 통계값 등으로 만드는 종단 연산.
 *   - java.util.stream.Collectors 클래스가 자주 쓰는 수집기(Collector)를 미리 제공한다.
 *
 * ▶ 대표 Collectors
 *   - toList / toSet / toMap  : 컬렉션으로 수집
 *   - joining                 : 문자열로 이어붙이기
 *   - groupingBy              : 키 기준 그룹핑 (SQL의 GROUP BY 느낌)
 *   - partitioningBy          : true/false 두 그룹으로 분할
 *   - counting / summingInt / averagingInt : 그룹 내 집계(다운스트림 수집기)
 *
 * 참고: 단순히 리스트로 모을 때는 Java 16+의 .toList()가 더 간결하다.
 *      단, .toList()는 수정 불가 리스트이고, Collectors.toList()는 수정 가능 리스트를 반환한다.
 *      여기서는 "Collectors 자체를 학습"하는 목적이므로 collect(...)를 명시적으로 보여준다.
 */
public class CollectorsExample {

    // 예제용 데이터 클래스 (이 파일 안에 self-contained)
    static class Student {
        String name;
        String city;
        int age;
        int score;

        Student(String name, String city, int age, int score) {
            this.name = name;
            this.city = city;
            this.age = age;
            this.score = score;
        }

        public String getName() { return name; }
        public String getCity() { return city; }
        public int getAge() { return age; }
        public int getScore() { return score; }

        @Override
        public String toString() {
            return name + "(" + city + ", " + age + "세, " + score + "점)";
        }
    }

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("  Chapter 03 - Collectors");
        System.out.println("========================================\n");

        List<Student> students = List.of(
                new Student("김철수", "서울", 20, 85),
                new Student("이영희", "부산", 22, 92),
                new Student("박민수", "서울", 21, 78),
                new Student("정수진", "대전", 23, 95),
                new Student("한지민", "부산", 20, 88),
                new Student("최영호", "서울", 22, 91)
        );

        // --------------------------------------------------
        // 1. toList - 리스트로 수집
        // --------------------------------------------------
        System.out.println("--- 1. Collectors.toList ---");
        List<String> names = students.stream()
                .map(Student::getName)
                .collect(Collectors.toList());
        System.out.println("  이름 목록 : " + names);
        System.out.println();

        // --------------------------------------------------
        // 2. toSet - 셋으로 수집 (중복 자동 제거)
        // --------------------------------------------------
        System.out.println("--- 2. Collectors.toSet ---");
        Set<String> cities = students.stream()
                .map(Student::getCity)
                .collect(Collectors.toSet());
        System.out.println("  도시 집합 : " + cities);
        System.out.println();

        // --------------------------------------------------
        // 3. toMap - 맵으로 수집 (키 -> 값)
        // --------------------------------------------------
        System.out.println("--- 3. Collectors.toMap ---");
        // 주의: 키가 중복되면 예외 발생. 필요 시 병합 함수(3번째 인자)를 넘긴다.
        Map<String, Integer> nameScoreMap = students.stream()
                .collect(Collectors.toMap(Student::getName, Student::getScore));
        System.out.println("  이름 -> 점수 : " + nameScoreMap);
        System.out.println();

        // --------------------------------------------------
        // 4. joining - 문자열 이어붙이기
        // --------------------------------------------------
        System.out.println("--- 4. Collectors.joining ---");
        String plain = students.stream().map(Student::getName)
                .collect(Collectors.joining());
        String withDelim = students.stream().map(Student::getName)
                .collect(Collectors.joining(", ", "[", "]")); // 구분자, 접두, 접미
        System.out.println("  구분자 없이 : " + plain);
        System.out.println("  구분자+대괄호: " + withDelim);
        System.out.println();

        // --------------------------------------------------
        // 5. groupingBy - 키 기준 그룹핑
        // --------------------------------------------------
        System.out.println("--- 5. Collectors.groupingBy ---");
        Map<String, List<Student>> byCity = students.stream()
                .collect(Collectors.groupingBy(Student::getCity));
        System.out.println("  도시별 그룹 :");
        byCity.forEach((city, studs) ->
                System.out.println("    " + city + " : " + studs));
        System.out.println();

        // --------------------------------------------------
        // 6. groupingBy + counting - 그룹별 개수
        // --------------------------------------------------
        System.out.println("--- 6. groupingBy + counting ---");
        // groupingBy의 2번째 인자로 "다운스트림 수집기"를 주면 그룹 내부를 추가 집계한다.
        Map<String, Long> countByCity = students.stream()
                .collect(Collectors.groupingBy(Student::getCity, Collectors.counting()));
        System.out.println("  도시별 인원수 : " + countByCity);
        System.out.println();

        // --------------------------------------------------
        // 7. groupingBy + summingInt / averagingInt - 그룹별 합/평균
        // --------------------------------------------------
        System.out.println("--- 7. groupingBy + summing / averaging ---");
        Map<String, Integer> sumScoreByCity = students.stream()
                .collect(Collectors.groupingBy(
                        Student::getCity,
                        Collectors.summingInt(Student::getScore)));
        System.out.println("  도시별 점수 합계 : " + sumScoreByCity);

        Map<String, Double> avgScoreByCity = students.stream()
                .collect(Collectors.groupingBy(
                        Student::getCity,
                        Collectors.averagingInt(Student::getScore)));
        System.out.println("  도시별 점수 평균 : " + avgScoreByCity);
        System.out.println();

        // --------------------------------------------------
        // 8. partitioningBy - true/false 두 그룹으로 분할
        // --------------------------------------------------
        System.out.println("--- 8. Collectors.partitioningBy ---");
        // groupingBy와 달리 키가 항상 Boolean(true/false) 두 개로 고정된다.
        Map<Boolean, List<Student>> partition = students.stream()
                .collect(Collectors.partitioningBy(s -> s.getScore() >= 90));
        System.out.println("  90점 이상 : " + partition.get(true));
        System.out.println("  90점 미만 : " + partition.get(false));
        System.out.println();

        // --------------------------------------------------
        // 9. summarizingInt - 개수/합/평균/최소/최대를 한 번에
        // --------------------------------------------------
        System.out.println("--- 9. Collectors.summarizingInt ---");
        IntSummaryStatistics stats = students.stream()
                .collect(Collectors.summarizingInt(Student::getScore));
        System.out.println("  개수 : " + stats.getCount());
        System.out.println("  합계 : " + stats.getSum());
        System.out.println("  평균 : " + String.format("%.2f", stats.getAverage()));
        System.out.println("  최소 : " + stats.getMin());
        System.out.println("  최대 : " + stats.getMax());
        System.out.println();

        System.out.println("========================================");
        System.out.println("  Collectors 예제 완료!");
        System.out.println("========================================");
    }
}
