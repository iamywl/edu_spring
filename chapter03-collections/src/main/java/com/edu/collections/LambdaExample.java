package com.edu.collections;

import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;

/**
 * Chapter 03 - Lambda와 함수형 인터페이스 예제
 *
 * Lambda 표현식, 함수형 인터페이스, 메서드 참조, Optional을 학습합니다.
 */
public class LambdaExample {

    // ======================================================
    // 커스텀 함수형 인터페이스
    // ======================================================

    /**
     * @FunctionalInterface: 추상 메서드가 정확히 1개인 인터페이스
     * 이 어노테이션은 컴파일러가 함수형 인터페이스 규칙을 검증하도록 합니다.
     */
    @FunctionalInterface
    interface Calculator {
        int calculate(int a, int b);
    }

    /**
     * 커스텀 함수형 인터페이스 - 문자열 변환
     */
    @FunctionalInterface
    interface StringTransformer {
        String transform(String input);
    }

    // 예제용 데이터 클래스
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
        System.out.println("  Chapter 03 - Lambda & 함수형 인터페이스");
        System.out.println("========================================\n");

        demonstrateLambdaBasics();
        demonstrateFunctionalInterfaces();
        demonstrateMethodReferences();
        demonstrateOptional();
        demonstratePracticalExamples();

        System.out.println("========================================");
        System.out.println("  Lambda & 함수형 인터페이스 예제 완료!");
        System.out.println("========================================");
    }

    // ======================================================
    // 1. Lambda 표현식 기초
    // ======================================================
    static void demonstrateLambdaBasics() {
        System.out.println("--- 1. Lambda 표현식 기초 ---");

        // 기본 문법: (매개변수) -> 표현식
        // 또는: (매개변수) -> { 문장들; }

        // 커스텀 함수형 인터페이스로 Lambda 사용
        Calculator add = (a, b) -> a + b;
        Calculator subtract = (a, b) -> a - b;
        Calculator multiply = (a, b) -> a * b;
        Calculator divide = (a, b) -> b != 0 ? a / b : 0;

        System.out.println("  더하기: 10 + 3 = " + add.calculate(10, 3));
        System.out.println("  빼기:   10 - 3 = " + subtract.calculate(10, 3));
        System.out.println("  곱하기: 10 * 3 = " + multiply.calculate(10, 3));
        System.out.println("  나누기: 10 / 3 = " + divide.calculate(10, 3));

        // 여러 줄 Lambda
        Calculator modWithLog = (a, b) -> {
            System.out.println("  [로그] 나머지 연산: " + a + " % " + b);
            return a % b;
        };
        System.out.println("  나머지: 10 % 3 = " + modWithLog.calculate(10, 3));

        // 문자열 변환 Lambda
        StringTransformer toUpper = input -> input.toUpperCase();
        StringTransformer addBrackets = input -> "[" + input + "]";
        System.out.println("  toUpper: " + toUpper.transform("hello"));
        System.out.println("  addBrackets: " + addBrackets.transform("hello"));

        // Runnable을 Lambda로
        Runnable task = () -> System.out.println("  Runnable Lambda 실행!");
        task.run();

        // Comparator를 Lambda로
        List<String> names = new ArrayList<>(List.of("Charlie", "Alice", "Bob"));
        names.sort((a, b) -> a.compareTo(b));  // Lambda 비교자
        System.out.println("  정렬된 이름: " + names);
        System.out.println();
    }

    // ======================================================
    // 2. 주요 함수형 인터페이스
    // ======================================================
    static void demonstrateFunctionalInterfaces() {
        System.out.println("--- 2. 함수형 인터페이스 (Predicate, Function, Consumer, Supplier) ---");

        // ---- Predicate<T>: T -> boolean (조건 검사) ----
        System.out.println("  [Predicate - 조건 검사]");
        Predicate<Integer> isEven = n -> n % 2 == 0;
        Predicate<Integer> isPositive = n -> n > 0;
        Predicate<String> isNotEmpty = s -> !s.isEmpty();

        System.out.println("  isEven(4): " + isEven.test(4));
        System.out.println("  isEven(7): " + isEven.test(7));

        // Predicate 조합: and, or, negate
        Predicate<Integer> isEvenAndPositive = isEven.and(isPositive);
        Predicate<Integer> isOdd = isEven.negate();
        System.out.println("  isEven AND isPositive (-4): " + isEvenAndPositive.test(-4));
        System.out.println("  isOdd(3): " + isOdd.test(3));

        // 리스트 필터링에 활용
        List<Integer> numbers = List.of(-3, -2, -1, 0, 1, 2, 3);
        List<Integer> evenPositive = numbers.stream()
                .filter(isEvenAndPositive)
                .toList();
        System.out.println("  짝수이면서 양수: " + evenPositive);

        // ---- Function<T, R>: T -> R (변환) ----
        System.out.println("  [Function - 변환]");
        Function<String, Integer> strLength = String::length;
        Function<Integer, String> intToStr = n -> "숫자:" + n;
        Function<String, String> toUpperCase = String::toUpperCase;

        System.out.println("  strLength(\"안녕하세요\"): " + strLength.apply("안녕하세요"));

        // Function 조합: andThen, compose
        Function<String, String> upperThenAddLength = toUpperCase
                .andThen(s -> s + " (길이: " + s.length() + ")");
        System.out.println("  andThen: " + upperThenAddLength.apply("hello"));

        // compose: 먼저 실행할 함수 지정 (반대 순서)
        Function<Integer, String> addTenThenToStr = intToStr.compose(n -> n + 10);
        System.out.println("  compose (10 더하고 문자열 변환): " + addTenThenToStr.apply(5));

        // ---- Consumer<T>: T -> void (소비) ----
        System.out.println("  [Consumer - 소비]");
        Consumer<String> printer = s -> System.out.println("    출력: " + s);
        Consumer<String> logger = s -> System.out.println("    로그: [INFO] " + s);

        printer.accept("Hello Consumer");

        // Consumer 조합: andThen
        Consumer<String> printAndLog = printer.andThen(logger);
        printAndLog.accept("둘 다 실행");

        // ---- Supplier<T>: () -> T (생성) ----
        System.out.println("  [Supplier - 생성]");
        Supplier<String> greeting = () -> "안녕하세요!";
        Supplier<List<String>> listFactory = ArrayList::new;
        Supplier<Double> randomSupplier = Math::random;

        System.out.println("  greeting: " + greeting.get());
        System.out.println("  random: " + randomSupplier.get());

        List<String> newList = listFactory.get();
        newList.add("Supplier로 생성된 리스트");
        System.out.println("  listFactory: " + newList);

        // ---- UnaryOperator<T>: T -> T ----
        System.out.println("  [UnaryOperator - 같은 타입 변환]");
        UnaryOperator<String> exclaim = s -> s + "!";
        UnaryOperator<Integer> doubleIt = n -> n * 2;
        System.out.println("  exclaim: " + exclaim.apply("와우"));
        System.out.println("  doubleIt: " + doubleIt.apply(21));

        // ---- BinaryOperator<T>: (T, T) -> T ----
        System.out.println("  [BinaryOperator - 이항 연산]");
        BinaryOperator<Integer> maxOp = Integer::max;
        BinaryOperator<String> concat = String::concat;
        System.out.println("  max(3, 7): " + maxOp.apply(3, 7));
        System.out.println("  concat: " + concat.apply("Hello ", "World"));
        System.out.println();
    }

    // ======================================================
    // 3. 메서드 참조 (Method Reference)
    // ======================================================
    static void demonstrateMethodReferences() {
        System.out.println("--- 3. 메서드 참조 (Method Reference) ---");

        List<String> names = List.of("charlie", "alice", "bob", "diana");

        // 1) 정적 메서드 참조: Class::staticMethod
        System.out.println("  [정적 메서드 참조]");
        // Lambda: s -> Integer.parseInt(s)
        // 메서드 참조: Integer::parseInt
        List<Integer> nums = List.of("1", "2", "3").stream()
                .map(Integer::parseInt)   // 정적 메서드 참조
                .toList();
        System.out.println("  Integer::parseInt: " + nums);

        // 2) 특정 객체의 인스턴스 메서드 참조: instance::method
        System.out.println("  [인스턴스 메서드 참조]");
        String prefix = "Mr. ";
        // Lambda: s -> prefix.concat(s)
        // 메서드 참조: prefix::concat
        List<String> prefixed = names.stream()
                .map(prefix::concat)   // 특정 객체의 인스턴스 메서드
                .toList();
        System.out.println("  prefix::concat: " + prefixed);

        // 3) 임의 객체의 인스턴스 메서드 참조: Class::instanceMethod
        System.out.println("  [임의 객체 인스턴스 메서드 참조]");
        // Lambda: s -> s.toUpperCase()
        // 메서드 참조: String::toUpperCase
        List<String> uppers = names.stream()
                .map(String::toUpperCase)   // 각 문자열 객체의 메서드
                .toList();
        System.out.println("  String::toUpperCase: " + uppers);

        // 4) 생성자 참조: Class::new
        System.out.println("  [생성자 참조]");
        // Lambda: () -> new ArrayList<>()
        // 메서드 참조: ArrayList::new
        Supplier<List<String>> listSupplier = ArrayList::new;
        List<String> newList = listSupplier.get();
        System.out.println("  ArrayList::new: " + newList.getClass().getSimpleName());

        // Function을 이용한 생성자 참조
        Function<String, StringBuilder> sbFactory = StringBuilder::new;
        StringBuilder sb = sbFactory.apply("초기값");
        System.out.println("  StringBuilder::new: " + sb);

        // 실전 활용: 정렬에서 메서드 참조
        System.out.println("  [실전 활용]");
        List<Person> people = List.of(
                new Person("김철수", 30, "kim@test.com"),
                new Person("이영희", 25, "lee@test.com"),
                new Person("박민수", 28, "park@test.com")
        );

        // 이름 기준 정렬 - Comparator.comparing + 메서드 참조
        List<Person> sortedByName = people.stream()
                .sorted(Comparator.comparing(Person::getName))
                .toList();
        System.out.println("  이름순 정렬: " + sortedByName);

        // 나이 기준 역순 정렬
        List<Person> sortedByAgeDesc = people.stream()
                .sorted(Comparator.comparing(Person::getAge).reversed())
                .toList();
        System.out.println("  나이 역순 정렬: " + sortedByAgeDesc);

        // forEach에서 메서드 참조
        System.out.print("  forEach 메서드 참조: ");
        List.of(1, 2, 3).forEach(System.out::print);
        System.out.println("\n");
    }

    // ======================================================
    // 4. Optional - null 안전 처리
    // ======================================================
    static void demonstrateOptional() {
        System.out.println("--- 4. Optional ---");

        // Optional 생성
        Optional<String> present = Optional.of("값이 있음");
        Optional<String> empty = Optional.empty();
        Optional<String> nullable = Optional.ofNullable(null);  // null이면 empty

        System.out.println("  [Optional 생성]");
        System.out.println("  of(\"값이 있음\"): " + present);
        System.out.println("  empty(): " + empty);
        System.out.println("  ofNullable(null): " + nullable);

        // 값 확인 및 접근
        System.out.println("  [값 확인 및 접근]");
        System.out.println("  isPresent: " + present.isPresent());  // true
        System.out.println("  isEmpty: " + empty.isEmpty());        // true (Java 11+)

        // ifPresent: 값이 있을 때만 실행
        present.ifPresent(v -> System.out.println("  ifPresent: " + v));
        empty.ifPresent(v -> System.out.println("  이 줄은 출력되지 않음"));

        // ifPresentOrElse: 값이 있으면 첫 번째, 없으면 두 번째 실행 (Java 9+)
        empty.ifPresentOrElse(
                v -> System.out.println("  값: " + v),
                () -> System.out.println("  ifPresentOrElse: 값이 없습니다!")
        );

        // 기본값 제공
        System.out.println("  [기본값 제공]");
        String value1 = empty.orElse("기본값");
        System.out.println("  orElse: " + value1);

        // orElseGet: Supplier로 기본값 생성 (지연 평가)
        String value2 = empty.orElseGet(() -> "계산된 기본값");
        System.out.println("  orElseGet: " + value2);

        // orElseThrow: 값이 없으면 예외 발생
        try {
            empty.orElseThrow(() -> new IllegalArgumentException("값이 없음!"));
        } catch (IllegalArgumentException e) {
            System.out.println("  orElseThrow: " + e.getMessage());
        }

        // or: 값이 없으면 다른 Optional 반환 (Java 9+)
        Optional<String> result = empty.or(() -> Optional.of("대체 Optional"));
        System.out.println("  or: " + result);

        // 변환 (map, flatMap, filter)
        System.out.println("  [변환 - map, flatMap, filter]");

        // map: 값이 있으면 변환
        Optional<Integer> length = present.map(String::length);
        System.out.println("  map(String::length): " + length);

        // filter: 조건에 맞으면 유지, 아니면 empty
        Optional<String> filtered = present.filter(s -> s.length() > 3);
        System.out.println("  filter(length > 3): " + filtered);

        Optional<String> filteredOut = present.filter(s -> s.length() > 100);
        System.out.println("  filter(length > 100): " + filteredOut);

        // flatMap: 중첩 Optional 방지
        Optional<Optional<String>> nested = present.map(s -> Optional.of(s.toUpperCase()));
        Optional<String> flat = present.flatMap(s -> Optional.of(s.toUpperCase()));
        System.out.println("  map (중첩됨): " + nested);
        System.out.println("  flatMap (평탄화): " + flat);

        // 실전 활용: 체이닝
        System.out.println("  [실전 활용 - 체이닝]");
        Map<String, Person> personMap = new HashMap<>();
        personMap.put("user1", new Person("김철수", 30, "kim@test.com"));
        personMap.put("user2", new Person("이영희", 25, null));  // 이메일 없음

        // null 안전 체이닝
        String email = Optional.ofNullable(personMap.get("user1"))
                .map(Person::getEmail)
                .map(String::toUpperCase)
                .orElse("이메일 없음");
        System.out.println("  user1 이메일: " + email);

        String noEmail = Optional.ofNullable(personMap.get("user2"))
                .map(Person::getEmail)
                .map(String::toUpperCase)
                .orElse("이메일 없음");
        System.out.println("  user2 이메일: " + noEmail);

        String notFound = Optional.ofNullable(personMap.get("user999"))
                .map(Person::getEmail)
                .orElse("사용자 없음");
        System.out.println("  user999 이메일: " + notFound);

        // stream()으로 변환 (Java 9+)
        List<String> optionalToList = Optional.of("값")
                .stream()
                .toList();
        System.out.println("  Optional.stream(): " + optionalToList);
        System.out.println();
    }

    // ======================================================
    // 5. 실전 종합 예제
    // ======================================================
    static void demonstratePracticalExamples() {
        System.out.println("--- 5. 실전 종합 예제 ---");

        List<Person> people = List.of(
                new Person("김철수", 30, "kim@company.com"),
                new Person("이영희", 25, "lee@school.com"),
                new Person("박민수", 35, "park@company.com"),
                new Person("정수진", 28, "jung@school.com"),
                new Person("한지민", 22, "han@company.com")
        );

        // 예제 1: 조건 필터 + 변환 + 수집
        System.out.println("  [예제 1] 30세 이상의 이름 목록");
        Predicate<Person> isOver30 = p -> p.getAge() >= 30;
        Function<Person, String> toName = Person::getName;

        List<String> over30Names = people.stream()
                .filter(isOver30)
                .map(toName)
                .toList();
        System.out.println("  결과: " + over30Names);

        // 예제 2: 함수 조합으로 복잡한 변환
        System.out.println("  [예제 2] 이메일 도메인 추출");
        Function<Person, String> getEmail = Person::getEmail;
        Function<String, String> getDomain = email -> email.substring(email.indexOf("@") + 1);
        Function<Person, String> personToDomain = getEmail.andThen(getDomain);

        Set<String> domains = people.stream()
                .map(personToDomain)
                .collect(Collectors.toSet());
        System.out.println("  이메일 도메인: " + domains);

        // 예제 3: Consumer로 로깅 처리
        System.out.println("  [예제 3] Consumer 체이닝으로 로그 처리");
        Consumer<Person> logPerson = p ->
                System.out.println("    처리 중: " + p.getName());
        Consumer<Person> processPerson = p ->
                System.out.println("    완료: " + p.getName() + " -> " + p.getEmail());

        people.stream()
                .filter(p -> p.getEmail().endsWith("@company.com"))
                .forEach(logPerson.andThen(processPerson));

        // 예제 4: Supplier를 이용한 기본값 전략
        System.out.println("  [예제 4] Supplier로 기본값 전략");
        Supplier<Person> defaultPerson = () -> new Person("게스트", 0, "guest@default.com");

        Person found = people.stream()
                .filter(p -> p.getName().equals("없는사람"))
                .findFirst()
                .orElseGet(defaultPerson);
        System.out.println("  검색 결과: " + found + " (" + found.getEmail() + ")");
        System.out.println();
    }
}
