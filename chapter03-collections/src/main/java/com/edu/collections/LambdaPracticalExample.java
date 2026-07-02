package com.edu.collections;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Chapter 03 - 개념 5: 람다 실전 예제
 *
 * ======================================================================
 * 앞서 배운 람다/함수형 인터페이스/메서드 참조를 "실전 상황"에 종합 적용합니다.
 * ======================================================================
 * 다루는 실전 기법:
 *   1) Comparator 람다로 정렬 (comparing / thenComparing / reversed)
 *   2) 컬렉션 처리 (filter -> map -> collect)
 *   3) 함수 조합 (Function.andThen / compose)
 *   4) Consumer 조합으로 파이프라인 로깅
 *   5) Supplier 로 기본값(폴백) 전략
 */
public class LambdaPracticalExample {

    // ------------------------------------------------------------------
    // 예제용 데이터 클래스 (파일 자체 완결성을 위해 중첩 선언)
    // ------------------------------------------------------------------
    static class Person {
        String name;
        int age;
        String email;

        Person(String name, int age, String email) {
            this.name = name;
            this.age = age;
            this.email = email;
        }

        public String getName() { return name; }
        public int getAge() { return age; }
        public String getEmail() { return email; }

        @Override
        public String toString() {
            return name + "(" + age + ")";
        }
    }

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("  개념 5: 람다 실전 종합 예제");
        System.out.println("========================================\n");

        demonstrateComparatorSort();
        demonstrateCollectionPipeline();
        demonstrateFunctionComposition();
        demonstrateConsumerPipeline();
        demonstrateSupplierFallback();

        System.out.println("========================================");
        System.out.println("  람다 실전 예제 완료!");
        System.out.println("========================================");
    }

    private static List<Person> samplePeople() {
        return List.of(
                new Person("김철수", 30, "kim@company.com"),
                new Person("이영희", 25, "lee@school.com"),
                new Person("박민수", 35, "park@company.com"),
                new Person("정수진", 28, "jung@school.com"),
                new Person("한지민", 22, "han@company.com")
        );
    }

    // ======================================================
    // 1) Comparator 람다로 정렬
    // ======================================================
    static void demonstrateComparatorSort() {
        System.out.println("--- 1. Comparator 람다 정렬 ---");

        List<Person> people = new ArrayList<>(samplePeople());

        // 기본: 나이 오름차순 (comparingInt + 메서드 참조)
        people.sort(Comparator.comparingInt(Person::getAge));
        System.out.println("  나이 오름차순: " + people);

        // reversed: 나이 내림차순
        people.sort(Comparator.comparingInt(Person::getAge).reversed());
        System.out.println("  나이 내림차순: " + people);

        // thenComparing: 1차 키가 같을 때 2차 키로 정렬
        //   (여기서는 나이가 같은 경우 이름으로 - 데이터엔 동점이 없지만 문법 시연)
        people.sort(Comparator.comparingInt(Person::getAge)
                .thenComparing(Person::getName));
        System.out.println("  나이 -> 이름 정렬: " + people);

        // 순수 람다 Comparator: 이름 길이 기준
        people.sort((a, b) -> a.getName().length() - b.getName().length());
        System.out.println("  이름 길이 정렬: " + people);

        System.out.println();
    }

    // ======================================================
    // 2) 컬렉션 처리 파이프라인 (filter -> map -> collect)
    // ======================================================
    static void demonstrateCollectionPipeline() {
        System.out.println("--- 2. 컬렉션 처리 (filter -> map -> collect) ---");

        List<Person> people = samplePeople();

        // 조건을 Predicate 변수로 분리하면 재사용/가독성이 좋아진다.
        Predicate<Person> isOver30 = p -> p.getAge() >= 30;
        Function<Person, String> toName = Person::getName;

        List<String> over30Names = people.stream()
                .filter(isOver30)
                .map(toName)
                .toList();
        System.out.println("  30세 이상 이름: " + over30Names);

        // 도메인별 인원 집계 (grouping 대신 map+set 으로 단순 시연)
        Set<String> domains = people.stream()
                .map(p -> p.getEmail().substring(p.getEmail().indexOf("@") + 1))
                .collect(Collectors.toSet());
        System.out.println("  이메일 도메인 집합: " + domains);

        System.out.println();
    }

    // ======================================================
    // 3) 함수 조합 (andThen / compose)
    // ======================================================
    static void demonstrateFunctionComposition() {
        System.out.println("--- 3. 함수 조합 (andThen / compose) ---");

        List<Person> people = samplePeople();

        // 작은 함수들을 조합해 "Person -> 이메일 도메인" 파이프라인 구성
        Function<Person, String> getEmail = Person::getEmail;
        Function<String, String> getDomain = email -> email.substring(email.indexOf("@") + 1);

        // andThen: getEmail 실행 후 그 결과를 getDomain 에 전달 (왼 -> 오른)
        Function<Person, String> personToDomain = getEmail.andThen(getDomain);
        System.out.println("  andThen 파이프라인 (첫 사람 도메인): "
                + personToDomain.apply(people.get(0)));

        // compose: 반대 방향 (오른 -> 왼). n+10 을 먼저, 그 뒤 라벨링.
        Function<Integer, String> label = n -> "숫자:" + n;
        Function<Integer, String> addTenThenLabel = label.compose(n -> n + 10);
        System.out.println("  compose (5 -> +10 -> 라벨): " + addTenThenLabel.apply(5));

        // 여러 변환을 체이닝
        Function<Person, String> summary = ((Function<Person, String>) Person::getName)
                .andThen(name -> "이름=" + name)
                .andThen(String::toUpperCase);
        System.out.println("  체이닝 요약: " + summary.apply(people.get(1)));

        System.out.println();
    }

    // ======================================================
    // 4) Consumer 조합으로 파이프라인 로깅
    // ======================================================
    static void demonstrateConsumerPipeline() {
        System.out.println("--- 4. Consumer 조합 (andThen 로깅) ---");

        List<Person> people = samplePeople();

        Consumer<Person> logStart = p -> System.out.println("    처리 시작: " + p.getName());
        Consumer<Person> doWork = p -> System.out.println("    완료: " + p.getName() + " -> " + p.getEmail());

        // 회사 이메일 사용자만 골라, 두 소비 동작을 순서대로 실행
        people.stream()
                .filter(p -> p.getEmail().endsWith("@company.com"))
                .forEach(logStart.andThen(doWork));

        System.out.println();
    }

    // ======================================================
    // 5) Supplier 로 기본값(폴백) 전략
    // ======================================================
    static void demonstrateSupplierFallback() {
        System.out.println("--- 5. Supplier 기본값 전략 ---");

        List<Person> people = samplePeople();

        // 검색 실패 시 지연 생성될 기본 게스트 (Supplier -> orElseGet)
        Supplier<Person> defaultGuest = () -> new Person("게스트", 0, "guest@default.com");

        Person found = people.stream()
                .filter(p -> p.getName().equals("없는사람"))
                .findFirst()
                .orElseGet(defaultGuest);  // 값이 없을 때만 Supplier 실행
        System.out.println("  검색 결과: " + found + " (" + found.getEmail() + ")");

        System.out.println();
    }
}
