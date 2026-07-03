package com.edu.oop;

/**
 * 개 클래스 - Animal을 상속받은 구체 클래스
 * - extends: 부모 클래스를 상속
 * - super: 부모 생성자/메서드 호출
 * - @Override: 부모 메서드를 재정의 (다형성)
 */
public class Dog extends Animal {

    // 하위 클래스 고유 필드
    private String breed;

    // === 생성자: super()로 부모 생성자 호출 ===
    public Dog(String name, int age, String breed) {
        super(name, age);   // 부모(Animal)의 생성자 호출
        this.breed = breed;
    }

    // Getter
    public String getBreed() {
        return breed;
    }

    // === 추상 메서드 구현 (반드시 구현해야 컴파일 가능) ===
    @Override
    public String speak() {
        return getName() + "이(가) 멍멍! 하고 짖습니다.";
    }

    // === 메서드 오버라이딩: 부모의 info()를 재정의하여 품종 정보 추가 ===
    @Override
    public String info() {
        return super.info() + " | 품종: " + breed;
    }

    /** 개만의 고유 행동 */
    public String fetch() {
        return getName() + "이(가) 공을 물어옵니다!";
    }

    @Override
    public String toString() {
        return "Dog{name='" + getName() + "', age=" + getAge() + ", breed='" + breed + "'}";
    }
}
