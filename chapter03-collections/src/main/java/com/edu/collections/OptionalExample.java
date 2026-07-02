package com.edu.collections;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Chapter 03 - 개념 4: Optional (null 안전 처리)
 *
 * ======================================================================
 * "Optional 이란?"
 * ======================================================================
 * Optional<T> 는 "값이 있을 수도, 없을 수도 있음"을 타입으로 표현하는 상자입니다.
 * null 을 직접 다루다 발생하는 NullPointerException(NPE)을 예방하고,
 * "값이 없을 때 어떻게 할지"를 코드에 명시적으로 강제합니다.
 *
 * [생성]  of(값) / ofNullable(값) / empty()
 * [확인]  isPresent() / isEmpty() / ifPresent() / ifPresentOrElse()
 * [기본값] orElse() / orElseGet() / orElseThrow() / or()
 * [변환]  map() / flatMap() / filter()
 *
 * ⚠ 안티패턴 경고: get() 남용 금지! (아래 5번 섹션 참고)
 */
public class OptionalExample {

    // ------------------------------------------------------------------
    // 예제용 데이터 클래스 (파일 자체 완결성을 위해 중첩 선언)
    // ------------------------------------------------------------------
    static class Person {
        String name;
        String email;  // null 일 수 있음 (Optional 활용 대상)

        Person(String name, String email) {
            this.name = name;
            this.email = email;
        }

        public String getName() { return name; }
        public String getEmail() { return email; }

        @Override
        public String toString() {
            return name;
        }
    }

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("  개념 4: Optional (null 안전 처리)");
        System.out.println("========================================\n");

        demonstrateCreation();
        demonstratePresenceCheck();
        demonstrateDefaults();
        demonstrateTransform();
        demonstrateAntiPattern();
        demonstrateChaining();

        System.out.println("========================================");
        System.out.println("  Optional 예제 완료!");
        System.out.println("========================================");
    }

    // ======================================================
    // 1) 생성: of / ofNullable / empty
    // ======================================================
    static void demonstrateCreation() {
        System.out.println("--- 1. Optional 생성 ---");

        Optional<String> present = Optional.of("값이 있음");       // null 이면 즉시 NPE
        Optional<String> empty = Optional.empty();                 // 빈 Optional
        Optional<String> nullable = Optional.ofNullable(null);     // null 이면 empty 로

        System.out.println("  of(\"값이 있음\"): " + present);
        System.out.println("  empty(): " + empty);
        System.out.println("  ofNullable(null): " + nullable);
        System.out.println("  주의: of(null) 은 NPE 발생 -> 값이 null 가능하면 ofNullable 사용");

        System.out.println();
    }

    // ======================================================
    // 2) 값 확인: isPresent / isEmpty / ifPresent / ifPresentOrElse
    // ======================================================
    static void demonstratePresenceCheck() {
        System.out.println("--- 2. 값 확인 및 조건 실행 ---");

        Optional<String> present = Optional.of("데이터");
        Optional<String> empty = Optional.empty();

        System.out.println("  present.isPresent(): " + present.isPresent());  // true
        System.out.println("  empty.isEmpty(): " + empty.isEmpty());          // true (Java 11+)

        // ifPresent: 값이 있을 때만 실행 (없으면 아무 일도 안 함)
        present.ifPresent(v -> System.out.println("  ifPresent: " + v));
        empty.ifPresent(v -> System.out.println("  (이 줄은 출력되지 않음)"));

        // ifPresentOrElse: 있으면 첫 번째, 없으면 두 번째 실행 (Java 9+)
        empty.ifPresentOrElse(
                v -> System.out.println("  값: " + v),
                () -> System.out.println("  ifPresentOrElse: 값이 없습니다!")
        );

        System.out.println();
    }

    // ======================================================
    // 3) 기본값 제공: orElse / orElseGet / orElseThrow / or
    // ======================================================
    static void demonstrateDefaults() {
        System.out.println("--- 3. 기본값 제공 ---");

        Optional<String> empty = Optional.empty();

        // orElse: 값이 없으면 지정한 값 반환 (인자는 항상 평가됨)
        System.out.println("  orElse: " + empty.orElse("기본값"));

        // orElseGet: 값이 없을 때만 Supplier 실행 (지연 평가 -> 비용 큰 기본값에 유리)
        System.out.println("  orElseGet: " + empty.orElseGet(() -> "계산된 기본값"));

        // 팁: orElse 는 값이 있어도 인자를 미리 계산하므로,
        //     기본값 생성 비용이 크면 orElseGet 을 쓰는 것이 좋다.

        // orElseThrow: 값이 없으면 예외 발생
        try {
            empty.orElseThrow(() -> new IllegalArgumentException("값이 없음!"));
        } catch (IllegalArgumentException e) {
            System.out.println("  orElseThrow: " + e.getMessage());
        }

        // or: 값이 없으면 "다른 Optional" 을 반환 (Java 9+)
        Optional<String> fallback = empty.or(() -> Optional.of("대체 Optional"));
        System.out.println("  or: " + fallback);

        System.out.println();
    }

    // ======================================================
    // 4) 변환: map / flatMap / filter
    // ======================================================
    static void demonstrateTransform() {
        System.out.println("--- 4. 변환 (map, flatMap, filter) ---");

        Optional<String> present = Optional.of("hello world");

        // map: 값이 있으면 변환, 없으면 그대로 empty
        Optional<Integer> length = present.map(String::length);
        System.out.println("  map(String::length): " + length);

        // filter: 조건 만족하면 유지, 아니면 empty 로
        System.out.println("  filter(length > 3): " + present.filter(s -> s.length() > 3));
        System.out.println("  filter(length > 100): " + present.filter(s -> s.length() > 100));

        // flatMap: 변환 결과 자체가 Optional 일 때 이중 상자를 평탄화
        Optional<Optional<String>> nested = present.map(s -> Optional.of(s.toUpperCase()));
        Optional<String> flat = present.flatMap(s -> Optional.of(s.toUpperCase()));
        System.out.println("  map (중첩 Optional<Optional>): " + nested);
        System.out.println("  flatMap (평탄화 Optional): " + flat);

        System.out.println();
    }

    // ======================================================
    // 5) 안티패턴 경고: get() 남용
    // ======================================================
    static void demonstrateAntiPattern() {
        System.out.println("--- 5. ⚠ 안티패턴 경고: get() 남용 ---");

        Optional<String> empty = Optional.empty();

        // ❌ 나쁜 예: isPresent() 로 검사한 뒤 get() 하는 것은
        //    결국 null 체크와 다를 게 없어 Optional 의 이점을 살리지 못한다.
        //    게다가 검사를 빼먹고 get() 만 부르면 NoSuchElementException 발생!
        System.out.println("  [나쁜 예] empty.get() 를 그냥 호출하면 예외 발생:");
        try {
            String value = empty.get();  // 값이 없으므로 예외
            System.out.println("  " + value);
        } catch (java.util.NoSuchElementException e) {
            System.out.println("    -> NoSuchElementException: " + e.getMessage());
        }

        // ✅ 좋은 예: get() 대신 orElse / orElseGet / map / ifPresent 등으로
        //    "값이 없을 때의 처리"를 함께 명시한다.
        String safe = empty.orElse("안전한 기본값");
        System.out.println("  [좋은 예] empty.orElse(...) => " + safe);

        System.out.println();
    }

    // ======================================================
    // 6) 실전 활용: null 안전 체이닝
    // ======================================================
    static void demonstrateChaining() {
        System.out.println("--- 6. 실전 활용 (null 안전 체이닝) ---");

        Map<String, Person> personMap = new HashMap<>();
        personMap.put("user1", new Person("김철수", "kim@test.com"));
        personMap.put("user2", new Person("이영희", null));  // 이메일이 null

        // Optional 체이닝: get 결과 null, email null, 미존재 키 를 모두 안전 처리
        String email1 = Optional.ofNullable(personMap.get("user1"))
                .map(Person::getEmail)
                .map(String::toUpperCase)
                .orElse("이메일 없음");
        System.out.println("  user1 이메일: " + email1);

        String email2 = Optional.ofNullable(personMap.get("user2"))
                .map(Person::getEmail)      // null -> empty 로 자연스럽게 전파
                .map(String::toUpperCase)
                .orElse("이메일 없음");
        System.out.println("  user2 이메일: " + email2);

        String email3 = Optional.ofNullable(personMap.get("user999"))  // 없는 키
                .map(Person::getEmail)
                .orElse("사용자 없음");
        System.out.println("  user999 이메일: " + email3);

        // stream() 으로 변환 후 컬렉션 연산에 합류 (Java 9+)
        List<String> asList = Optional.of("값").stream().toList();
        System.out.println("  Optional.stream(): " + asList);

        System.out.println();
    }
}
