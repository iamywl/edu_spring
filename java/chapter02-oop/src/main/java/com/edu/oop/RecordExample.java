package com.edu.oop;

/**
 * [개념 8] 레코드(record)
 *
 * record란? (Java 16+)
 * - "불변(immutable) 데이터"를 아주 간결하게 표현하는 특수한 클래스.
 * - 헤더에 필드 목록만 선언하면, 컴파일러가 다음을 자동으로 만들어 준다:
 *     1. private final 필드
 *     2. 모든 필드를 받는 생성자(정규 생성자, canonical constructor)
 *     3. 필드마다 접근자 메서드 (getName()이 아니라 name() 형태)
 *     4. equals(), hashCode(), toString()
 * - record는 final 이라 상속할 수 없지만, 인터페이스는 구현할 수 있다.
 *
 * 이 예제에서 사용하는 것: PersonRecord(name, age, email)
 *   - 컴팩트 생성자에서 유효성 검증
 *   - introduce(), isAdult(), emailDomain() 커스텀 메서드
 *   - withDefaultEmail(...) 정적 팩토리 메서드
 *
 * 언제 record를 쓰나?
 *   - 데이터를 "담아서 나르는" 용도(DTO, 값 객체, 좌표, 응답 모델 등).
 *   - 값이 생성 후 바뀌지 않아야 할 때(불변).
 *   - 반대로, 가변 상태나 복잡한 동작이 핵심이면 일반 class가 낫다.
 */
public class RecordExample {

    public static void main(String[] args) {

        // ------------------------------------------------------------
        // 1) 자동 생성된 접근자(getter)
        // ------------------------------------------------------------
        printSection("1. 자동 생성된 접근자 (name(), age(), email())");

        PersonRecord person = new PersonRecord("홍길동", 25, "hong@example.com");
        // getXxx()가 아니라 필드명 그대로의 메서드가 생성된다.
        System.out.println("이름   -> " + person.name());
        System.out.println("나이   -> " + person.age());
        System.out.println("이메일 -> " + person.email());

        // ------------------------------------------------------------
        // 2) 자동 생성된 toString / equals / hashCode
        // ------------------------------------------------------------
        printSection("2. 자동 생성된 toString / equals / hashCode");

        PersonRecord same = new PersonRecord("홍길동", 25, "hong@example.com");

        // toString: 필드가 보기 좋게 출력됨
        System.out.println("toString -> " + person);
        // equals: 모든 필드 값이 같으면 true (내용 기반 동등성)
        System.out.println("person.equals(same) -> " + person.equals(same));
        // hashCode: 내용이 같으면 hashCode도 같다
        System.out.println("hashCode 동일 -> " + (person.hashCode() == same.hashCode()));

        // ------------------------------------------------------------
        // 3) 불변성(immutable)
        // ------------------------------------------------------------
        printSection("3. 불변 데이터");

        // record의 필드는 final 이라 setter가 없다.
        // 값을 바꾸고 싶으면 "새 객체"를 만들어야 한다.
        PersonRecord older = new PersonRecord(person.name(), person.age() + 1, person.email());
        System.out.println("원본     -> " + person);
        System.out.println("나이 +1 한 새 객체 -> " + older);
        System.out.println("(원본은 그대로 유지됨 -> 불변)");

        // ------------------------------------------------------------
        // 4) 커스텀 메서드 & 정적 팩토리 메서드
        // ------------------------------------------------------------
        printSection("4. 커스텀 메서드 / 팩토리 메서드");

        System.out.println("introduce()  -> " + person.introduce());
        System.out.println("isAdult()    -> " + person.isAdult());
        System.out.println("emailDomain()-> " + person.emailDomain());

        // 이메일을 생략하면 기본 이메일을 만들어 주는 팩토리 메서드
        PersonRecord auto = PersonRecord.withDefaultEmail("김철수", 30);
        System.out.println("withDefaultEmail(\"김철수\", 30) -> " + auto);

        // ------------------------------------------------------------
        // 5) 컴팩트 생성자의 유효성 검증
        // ------------------------------------------------------------
        printSection("5. 컴팩트 생성자의 유효성 검증");

        // record도 생성 시점에 규칙을 검증할 수 있다(컴팩트 생성자).
        try {
            new PersonRecord("", 25, "test@test.com");   // 빈 이름 -> 거부
        } catch (IllegalArgumentException e) {
            System.out.println("이름 검증 -> " + e.getMessage());
        }
        try {
            new PersonRecord("이몽룡", 25, "invalid-email"); // '@' 없음 -> 거부
        } catch (IllegalArgumentException e) {
            System.out.println("이메일 검증 -> " + e.getMessage());
        }

        // ------------------------------------------------------------
        // 정리
        // ------------------------------------------------------------
        printSection("정리");
        System.out.println("- record는 불변 데이터 객체를 매우 간결하게 정의한다.");
        System.out.println("- 생성자/접근자/equals/hashCode/toString이 자동 생성된다.");
        System.out.println("- 값을 바꾸려면 새 객체를 만든다(불변).");
        System.out.println("- DTO/값 객체처럼 '데이터를 나르는' 용도에 적합하다.");
    }

    private static void printSection(String title) {
        System.out.println();
        System.out.println("=".repeat(60));
        System.out.println("  " + title);
        System.out.println("=".repeat(60));
    }
}
