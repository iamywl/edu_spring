package com.edu.oop;

/**
 * [개념 1] 상속(Inheritance)
 *
 * 상속이란?
 * - 이미 존재하는 클래스(부모/상위 클래스)의 필드와 메서드를 물려받아
 *   새로운 클래스(자식/하위 클래스)를 정의하는 것.
 * - 공통 코드를 부모에 모아두고, 자식은 차이점만 추가/변경 -> 코드 재사용.
 *
 * 이 예제에서 사용하는 계층:
 *   Animal (추상 부모)  <--  Dog / Cat (구체 자식)
 *
 * 핵심 키워드:
 *   - extends  : 부모 클래스를 상속받는다.
 *   - super()  : 자식 생성자에서 부모 생성자를 호출한다.
 *   - super.메서드() : 자식에서 부모의 원래 메서드를 호출한다.
 *   - @Override : 부모 메서드를 자식이 재정의(오버라이딩)함을 명시.
 *
 * 이 클래스 하나만 실행해도 상속의 모든 요소를 볼 수 있도록 구성되어 있습니다.
 */
public class InheritanceExample {

    public static void main(String[] args) {

        // ------------------------------------------------------------
        // 1) 필드/메서드 상속: 자식이 부모의 것을 물려받는다
        // ------------------------------------------------------------
        printSection("1. 필드와 메서드 상속");

        // Dog는 Animal을 extends 했으므로,
        // Animal에 정의된 name/age 필드와 getName()/getAge()/breathe() 를 그대로 사용한다.
        Dog dog = new Dog("바둑이", 3, "진돗개");

        // getName(), getAge() 는 Animal에 정의된 상속받은 메서드
        System.out.println("이름(상속받은 getName): " + dog.getName());
        System.out.println("나이(상속받은 getAge): " + dog.getAge() + "살");
        // breed는 Dog 고유 필드 -> 자식만의 추가 요소
        System.out.println("품종(Dog 고유 getBreed): " + dog.getBreed());
        // breathe() 는 Animal에 있는 일반 메서드 -> 자식이 그대로 물려받아 사용
        System.out.println("숨쉬기(상속받은 breathe): " + dog.breathe());

        // ------------------------------------------------------------
        // 2) super: 자식 생성자에서 부모 생성자 호출
        // ------------------------------------------------------------
        printSection("2. super() - 부모 생성자 호출");

        // Dog 생성자 내부는 super(name, age) 로 Animal의 생성자를 먼저 호출한다.
        // 즉, 부모가 자신의 필드(name, age)를 초기화한 뒤, 자식이 breed를 초기화한다.
        Cat cat = new Cat("나비", 2, true);
        System.out.println("Cat 생성 완료 -> " + cat.info());
        System.out.println("설명: Cat 생성자는 내부에서 super(name, age)로");
        System.out.println("      부모 Animal의 name/age를 먼저 초기화한 뒤 indoor를 설정합니다.");

        // ------------------------------------------------------------
        // 3) 오버라이딩(@Override): 부모 메서드를 자식이 재정의
        // ------------------------------------------------------------
        printSection("3. 메서드 오버라이딩 (@Override)");

        // speak()는 Animal의 추상 메서드 -> 각 자식이 반드시 구현(오버라이딩)해야 한다.
        System.out.println("Dog.speak()  -> " + dog.speak());
        System.out.println("Cat.speak()  -> " + cat.speak());

        // info()는 Animal에 기본 구현이 있지만, 자식이 super.info()를 활용해 확장한다.
        // Dog.info() 내부: super.info() + " | 품종: " + breed
        System.out.println();
        System.out.println("Animal.info() 를 자식이 super.info()로 확장한 결과:");
        System.out.println("  Dog.info() -> " + dog.info());
        System.out.println("  Cat.info() -> " + cat.info());

        // ------------------------------------------------------------
        // 4) 정리
        // ------------------------------------------------------------
        printSection("정리");
        System.out.println("- extends 로 부모의 필드/메서드를 물려받는다.");
        System.out.println("- super()로 부모 생성자를, super.m()로 부모 메서드를 호출한다.");
        System.out.println("- @Override 로 부모 메서드를 자식에 맞게 재정의한다.");
        System.out.println("- 공통은 부모에, 차이점만 자식에 -> 코드 재사용 & 유지보수 용이.");
    }

    /** 섹션 구분선 출력 헬퍼 */
    private static void printSection(String title) {
        System.out.println();
        System.out.println("=".repeat(60));
        System.out.println("  " + title);
        System.out.println("=".repeat(60));
    }
}
