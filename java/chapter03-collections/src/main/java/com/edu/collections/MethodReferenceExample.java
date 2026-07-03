package com.edu.collections;

import java.util.Comparator;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Chapter 03 - 개념 3: 메서드 참조(Method Reference)
 *
 * ======================================================================
 * "메서드 참조란?"
 * ======================================================================
 * 이미 존재하는 메서드를 "그대로 가리키는" 더 짧은 람다 표기법입니다.
 * 람다가 단지 기존 메서드 하나를 호출만 한다면, `::` 문법으로 줄일 수 있습니다.
 *
 * [메서드 참조 4종]
 *   1) 정적 메서드 참조        : ClassName::staticMethod
 *      대응 람다: (args) -> ClassName.staticMethod(args)
 *
 *   2) 특정 객체의 인스턴스 메서드: instance::method
 *      대응 람다: (args) -> instance.method(args)
 *
 *   3) 임의 객체의 인스턴스 메서드: ClassName::instanceMethod
 *      대응 람다: (obj, args) -> obj.instanceMethod(args)  // 첫 인자가 수신자
 *
 *   4) 생성자 참조             : ClassName::new
 *      대응 람다: (args) -> new ClassName(args)
 */
public class MethodReferenceExample {

    // ------------------------------------------------------------------
    // 예제용 데이터 클래스 (파일 자체 완결성을 위해 중첩 선언)
    // ------------------------------------------------------------------
    static class Person {
        String name;
        int age;

        Person(String name, int age) {
            this.name = name;
            this.age = age;
        }

        // 생성자 참조 예시용: 이름만 받는 생성자
        Person(String name) {
            this(name, 0);
        }

        public String getName() { return name; }
        public int getAge() { return age; }

        @Override
        public String toString() {
            return name + "(" + age + ")";
        }
    }

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("  개념 3: 메서드 참조(Method Reference)");
        System.out.println("========================================\n");

        demonstrateStaticReference();
        demonstrateBoundInstanceReference();
        demonstrateUnboundInstanceReference();
        demonstrateConstructorReference();
        demonstratePracticalUsage();

        System.out.println("========================================");
        System.out.println("  메서드 참조 예제 완료!");
        System.out.println("========================================");
    }

    // ======================================================
    // 1) 정적 메서드 참조: Class::staticMethod
    // ======================================================
    static void demonstrateStaticReference() {
        System.out.println("--- 1. 정적 메서드 참조 (Class::staticMethod) ---");

        // 람다:      s -> Integer.parseInt(s)
        // 메서드 참조: Integer::parseInt
        Function<String, Integer> parseLambda = s -> Integer.parseInt(s);
        Function<String, Integer> parseRef = Integer::parseInt;
        System.out.println("  람다      parseLambda(\"42\"): " + parseLambda.apply("42"));
        System.out.println("  메서드참조 Integer::parseInt(\"42\"): " + parseRef.apply("42"));

        List<Integer> nums = List.of("1", "2", "3").stream()
                .map(Integer::parseInt)   // 정적 메서드 참조
                .toList();
        System.out.println("  stream.map(Integer::parseInt): " + nums);

        System.out.println();
    }

    // ======================================================
    // 2) 특정 객체의 인스턴스 메서드 참조: instance::method
    // ======================================================
    static void demonstrateBoundInstanceReference() {
        System.out.println("--- 2. 특정 객체의 인스턴스 메서드 참조 (instance::method) ---");

        String prefix = "Mr. ";  // 이 "특정 객체(prefix)"에 바인딩됨

        // 람다:      s -> prefix.concat(s)
        // 메서드 참조: prefix::concat  (수신 객체가 prefix로 고정)
        Function<String, String> concatLambda = s -> prefix.concat(s);
        Function<String, String> concatRef = prefix::concat;
        System.out.println("  람다      concatLambda(\"Kim\"): " + concatLambda.apply("Kim"));
        System.out.println("  메서드참조 prefix::concat(\"Kim\"): " + concatRef.apply("Kim"));

        List<String> names = List.of("charlie", "alice", "bob");
        List<String> prefixed = names.stream().map(prefix::concat).toList();
        System.out.println("  stream.map(prefix::concat): " + prefixed);

        System.out.println();
    }

    // ======================================================
    // 3) 임의 객체의 인스턴스 메서드 참조: Class::instanceMethod
    // ======================================================
    static void demonstrateUnboundInstanceReference() {
        System.out.println("--- 3. 임의 객체의 인스턴스 메서드 참조 (Class::instanceMethod) ---");

        // 람다:      s -> s.toUpperCase()   (각 스트림 원소가 수신 객체가 됨)
        // 메서드 참조: String::toUpperCase   (첫 번째 인자가 곧 수신 객체)
        Function<String, String> upperLambda = s -> s.toUpperCase();
        Function<String, String> upperRef = String::toUpperCase;
        System.out.println("  람다      upperLambda(\"hello\"): " + upperLambda.apply("hello"));
        System.out.println("  메서드참조 String::toUpperCase(\"hello\"): " + upperRef.apply("hello"));

        List<String> names = List.of("charlie", "alice", "bob");
        List<String> uppers = names.stream().map(String::toUpperCase).toList();
        System.out.println("  stream.map(String::toUpperCase): " + uppers);

        System.out.println();
    }

    // ======================================================
    // 4) 생성자 참조: Class::new
    // ======================================================
    static void demonstrateConstructorReference() {
        System.out.println("--- 4. 생성자 참조 (Class::new) ---");

        // 인자 없는 생성자 -> Supplier
        // 람다:      () -> new java.util.ArrayList<>()
        // 메서드 참조: ArrayList::new
        Supplier<List<String>> listSupplier = java.util.ArrayList::new;
        List<String> newList = listSupplier.get();
        System.out.println("  ArrayList::new -> " + newList.getClass().getSimpleName());

        // 인자 1개 생성자 -> Function
        // 람다:      name -> new Person(name)
        // 메서드 참조: Person::new  (인자 개수로 어떤 생성자인지 추론)
        Function<String, Person> personFactory = Person::new;
        Person p = personFactory.apply("김철수");
        System.out.println("  Person::new(\"김철수\"): " + p);

        // 인자 2개 생성자 -> BiFunction
        BiFunction<String, Integer, Person> personFactory2 = Person::new;
        Person p2 = personFactory2.apply("이영희", 25);
        System.out.println("  Person::new(\"이영희\", 25): " + p2);

        System.out.println();
    }

    // ======================================================
    // 5) 실전 활용: 정렬/순회에서의 메서드 참조
    // ======================================================
    static void demonstratePracticalUsage() {
        System.out.println("--- 5. 실전 활용 (정렬, forEach) ---");

        List<Person> people = List.of(
                new Person("김철수", 30),
                new Person("이영희", 25),
                new Person("박민수", 28)
        );

        // Comparator.comparing + 메서드 참조로 정렬 키 지정
        List<Person> byName = people.stream()
                .sorted(Comparator.comparing(Person::getName))
                .toList();
        System.out.println("  이름순 정렬: " + byName);

        List<Person> byAgeDesc = people.stream()
                .sorted(Comparator.comparing(Person::getAge).reversed())
                .toList();
        System.out.println("  나이 역순 정렬: " + byAgeDesc);

        // forEach 에서 System.out::println 메서드 참조
        System.out.print("  forEach(System.out::print): ");
        List.of(1, 2, 3).forEach(System.out::print);
        System.out.println("\n");
    }
}
