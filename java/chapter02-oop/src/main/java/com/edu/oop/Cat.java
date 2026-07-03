package com.edu.oop;

/**
 * 고양이 클래스 - Animal을 상속받은 구체 클래스
 * - 상속과 다형성 예제
 * - 부모 메서드 오버라이딩
 */
public class Cat extends Animal {

    // 하위 클래스 고유 필드
    private boolean indoor;

    // === 생성자 ===
    public Cat(String name, int age, boolean indoor) {
        super(name, age);   // 부모 생성자 호출
        this.indoor = indoor;
    }

    // Getter
    public boolean isIndoor() {
        return indoor;
    }

    // === 추상 메서드 구현 ===
    @Override
    public String speak() {
        return getName() + "이(가) 야옹~ 하고 웁니다.";
    }

    // === 메서드 오버라이딩 ===
    @Override
    public String info() {
        String type = indoor ? "실내 고양이" : "실외 고양이";
        return super.info() + " | " + type;
    }

    /** 고양이만의 고유 행동 */
    public String purr() {
        return getName() + "이(가) 그르릉~ 하고 목을 울립니다.";
    }

    @Override
    public String toString() {
        return "Cat{name='" + getName() + "', age=" + getAge() + ", indoor=" + indoor + "}";
    }
}
