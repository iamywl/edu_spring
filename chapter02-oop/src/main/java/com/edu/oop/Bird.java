package com.edu.oop;

/**
 * 새 클래스 - Animal을 상속받고 Flyable 인터페이스를 구현
 * - 클래스 상속(extends)과 인터페이스 구현(implements)을 동시에 사용하는 예제
 * - Java에서 클래스는 단일 상속만 가능하지만, 인터페이스는 다중 구현 가능
 */
public class Bird extends Animal implements Flyable {

    // 새 고유 필드
    private String species;       // 종류 (예: 독수리, 참새)
    private int maxAltitude;      // 최대 비행 고도 (미터)

    // === 생성자 ===
    public Bird(String name, int age, String species, int maxAltitude) {
        super(name, age);              // 부모(Animal) 생성자 호출
        this.species = species;
        this.maxAltitude = maxAltitude;
    }

    // Getter
    public String getSpecies() {
        return species;
    }

    // === Animal의 추상 메서드 구현 ===
    @Override
    public String speak() {
        return getName() + "이(가) 짹짹! 하고 노래합니다.";
    }

    @Override
    public String info() {
        return super.info() + " | 종류: " + species + " | 최대 고도: " + maxAltitude + "m";
    }

    // === Flyable 인터페이스의 추상 메서드 구현 ===

    @Override
    public String fly() {
        return getName() + "(" + species + ")이(가) 하늘을 날고 있습니다!";
    }

    @Override
    public int getMaxAltitude() {
        return maxAltitude;
    }

    // === default 메서드 오버라이드 (선택적) ===
    @Override
    public String land() {
        return getName() + "이(가) 나뭇가지에 사뿐히 내려앉습니다.";
    }

    @Override
    public String toString() {
        return "Bird{name='" + getName() + "', age=" + getAge()
                + ", species='" + species + "', maxAltitude=" + maxAltitude + "}";
    }
}
