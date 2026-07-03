package com.edu.oop;

import java.util.Objects;

/**
 * 동물 추상 클래스
 * - 추상 클래스: 직접 인스턴스를 생성할 수 없고, 하위 클래스에서 상속받아 사용
 * - 공통 필드와 메서드를 정의하고, 추상 메서드로 하위 클래스에 구현을 강제
 */
public abstract class Animal {

    // === 필드 (캡슐화: private으로 외부 직접 접근 차단) ===
    private String name;
    private int age;

    // === 생성자 ===

    /** 기본 생성자 */
    protected Animal() {
        this("이름없음", 0);
    }

    /** 매개변수 생성자 */
    protected Animal(String name, int age) {
        this.name = name;
        this.age = age;
    }

    // === Getter / Setter (캡슐화: 공개 메서드를 통한 접근) ===

    public String getName() {
        return name;
    }

    public void setName(String name) {
        // 유효성 검증 가능
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("이름은 비어있을 수 없습니다.");
        }
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        if (age < 0) {
            throw new IllegalArgumentException("나이는 0 이상이어야 합니다.");
        }
        this.age = age;
    }

    // === 추상 메서드: 하위 클래스에서 반드시 구현해야 함 ===

    /** 동물의 울음소리 - 각 동물마다 다르므로 추상 메서드로 선언 */
    public abstract String speak();

    // === 일반 메서드: 모든 하위 클래스가 공유 ===

    /** 숨쉬기 - 모든 동물이 공통으로 수행 */
    public String breathe() {
        return name + "이(가) 숨을 쉽니다.";
    }

    /** 정보 출력 */
    public String info() {
        return "[" + getClass().getSimpleName() + "] " + name + " (나이: " + age + "살)";
    }

    // === Object 메서드 오버라이드 ===

    /**
     * toString: 객체의 문자열 표현
     * System.out.println() 등에서 자동 호출됨
     */
    @Override
    public String toString() {
        return getClass().getSimpleName() + "{name='" + name + "', age=" + age + "}";
    }

    /**
     * equals: 객체의 동등성 비교
     * == 은 참조(주소) 비교, equals는 내용 비교
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;                          // 같은 참조면 동일
        // instanceof는 하위 클래스 인스턴스도 true가 될 수 있어 대칭성이 깨질 수 있다.
        // 상속 계층에서 엄격한 동등성이 필요하면 getClass() != o.getClass() 비교를 쓴다.
        // (Dog/Cat은 equals를 오버라이드하지 않으므로 Animal의 name/age 필드로만 비교된다.)
        if (!(o instanceof Animal animal)) return false;     // 타입이 다르면 불일치
        return age == animal.age && Objects.equals(name, animal.name);
    }

    /**
     * hashCode: equals와 반드시 함께 오버라이드
     * equals가 true인 두 객체는 같은 hashCode를 가져야 함
     */
    @Override
    public int hashCode() {
        return Objects.hash(name, age);
    }
}
