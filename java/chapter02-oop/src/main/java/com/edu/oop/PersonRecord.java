package com.edu.oop;

/**
 * 사람 레코드 (Java 16+)
 * - record: 불변(immutable) 데이터 객체를 간결하게 정의
 * - 자동 생성되는 것들:
 *   1. private final 필드
 *   2. 모든 필드를 받는 생성자 (canonical constructor)
 *   3. getter 메서드 (필드명과 동일: name(), age(), email())
 *   4. equals(), hashCode(), toString()
 * - record는 final 클래스 (상속 불가)
 * - 인터페이스 구현은 가능
 */
public record PersonRecord(String name, int age, String email) {

    // === 컴팩트 생성자: 유효성 검증에 사용 ===
    // (매개변수 목록을 생략하고, 자동으로 필드에 할당됨)
    public PersonRecord {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("이름은 필수입니다.");
        }
        if (age < 0 || age > 200) {
            throw new IllegalArgumentException("나이는 0~200 사이여야 합니다.");
        }
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("유효한 이메일 주소가 필요합니다.");
        }
    }

    // === 커스텀 메서드 추가 가능 ===

    /** 자기소개 */
    public String introduce() {
        return name + " (" + age + "세, " + email + ")";
    }

    /** 성인 여부 확인 */
    public boolean isAdult() {
        return age >= 19;   // 한국 기준 만 19세
    }

    /** 이메일 도메인 추출 */
    public String emailDomain() {
        return email.substring(email.indexOf("@") + 1);
    }

    // === 정적 팩토리 메서드 ===

    /** 이메일 없이 생성하는 팩토리 메서드 */
    public static PersonRecord withDefaultEmail(String name, int age) {
        return new PersonRecord(name, age, name.toLowerCase() + "@example.com");
    }
}
