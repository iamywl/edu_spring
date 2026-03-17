package com.edu.oop;

/**
 * OOP 개념 종합 데모
 * - 클래스와 객체, 캡슐화, 상속, 다형성, 추상 클래스, 인터페이스
 * - enum, record, sealed class
 */
public class OopMain {

    public static void main(String[] args) {

        // ============================================================
        // 1. 클래스와 객체 / 상속 / 다형성
        // ============================================================
        printSection("1. 클래스, 상속, 다형성");

        // 다형성: 부모 타입(Animal)으로 자식 객체를 참조
        Animal dog = new Dog("바둑이", 3, "진돗개");
        Animal cat = new Cat("나비", 2, true);
        Animal bird = new Bird("짹짹이", 1, "참새", 500);

        // 다형성: 같은 speak() 호출이지만 실제 타입에 따라 다른 결과
        Animal[] animals = {dog, cat, bird};
        for (Animal animal : animals) {
            System.out.println(animal.info());
            System.out.println("  -> " + animal.speak());
            System.out.println("  -> " + animal.breathe());
        }

        // ============================================================
        // 2. 캡슐화 (Getter / Setter)
        // ============================================================
        printSection("2. 캡슐화");

        Dog myDog = new Dog("초코", 5, "푸들");
        System.out.println("이름: " + myDog.getName());        // getter
        System.out.println("나이: " + myDog.getAge() + "살");   // getter
        System.out.println("품종: " + myDog.getBreed());        // getter

        myDog.setName("초코초코");   // setter
        myDog.setAge(6);             // setter
        System.out.println("변경 후: " + myDog.info());

        // 유효성 검증 시연
        try {
            myDog.setName("");  // 빈 이름 -> 예외 발생
        } catch (IllegalArgumentException e) {
            System.out.println("유효성 검증: " + e.getMessage());
        }

        // ============================================================
        // 3. toString, equals, hashCode
        // ============================================================
        printSection("3. toString, equals, hashCode");

        Dog dog1 = new Dog("바둑이", 3, "진돗개");
        Dog dog2 = new Dog("바둑이", 3, "진돗개");

        System.out.println("toString: " + dog1);
        System.out.println("== (참조 비교): " + (dog1 == dog2));           // false (다른 객체)
        System.out.println("equals (내용 비교): " + dog1.equals(dog2));     // true (같은 이름, 나이)
        System.out.println("hashCode 동일: " + (dog1.hashCode() == dog2.hashCode()));  // true

        // ============================================================
        // 4. instanceof와 패턴 매칭
        // ============================================================
        printSection("4. instanceof 패턴 매칭");

        for (Animal animal : animals) {
            // Java 16+ 패턴 매칭: instanceof와 동시에 변수 선언
            if (animal instanceof Dog d) {
                System.out.println(d.getName() + " -> 개 고유 행동: " + d.fetch());
            } else if (animal instanceof Cat c) {
                System.out.println(c.getName() + " -> 고양이 고유 행동: " + c.purr());
            } else if (animal instanceof Bird b) {
                System.out.println(b.getName() + " -> 새 고유 행동: " + b.fly());
            }
        }

        // ============================================================
        // 5. 인터페이스
        // ============================================================
        printSection("5. 인터페이스 (Flyable)");

        Bird sparrow = new Bird("참새", 1, "참새", 500);
        Bird eagle = new Bird("독수리", 5, "독수리", 3000);

        // 인터페이스 타입으로 참조 가능
        Flyable[] flyers = {sparrow, eagle};

        // static 메서드: 인터페이스에서 직접 호출
        System.out.println(Flyable.description());
        System.out.println();

        for (Flyable flyer : flyers) {
            System.out.println(flyer.fly());                // 추상 메서드
            System.out.println("  " + flyer.flightStatus()); // default 메서드
            System.out.println("  " + flyer.land());         // 오버라이드된 default 메서드
            System.out.println("  안전 고도? " + Flyable.isSafeAltitude(flyer.getMaxAltitude()));
        }

        // ============================================================
        // 6. enum
        // ============================================================
        printSection("6. enum (Season)");

        // 모든 계절 순회
        System.out.println("--- 모든 계절 ---");
        for (Season season : Season.values()) {
            System.out.println("  " + season.name()
                    + " (ordinal: " + season.ordinal() + ")"
                    + " -> " + season.describe());
        }

        // 특정 계절 사용
        System.out.println();
        Season summer = Season.SUMMER;
        System.out.println("여름이 더운가요? " + summer.isHot());
        Season winter = Season.WINTER;
        System.out.println("겨울이 추운가요? " + winter.isCold());

        // 이름으로 조회
        Season fromName = Season.valueOf("AUTUMN");
        System.out.println("valueOf(\"AUTUMN\"): " + fromName.describe());

        // 한국어 이름으로 조회 (커스텀 메서드)
        Season fromKorean = Season.fromKoreanName("봄");
        System.out.println("fromKoreanName(\"봄\"): " + fromKorean.describe());

        // switch 표현식과 함께 사용
        String advice = switch (summer) {
            case SPRING -> "꽃구경 가기 좋은 계절입니다.";
            case SUMMER -> "수분을 충분히 섭취하세요!";
            case AUTUMN -> "단풍 구경을 추천합니다.";
            case WINTER -> "따뜻하게 입고 다니세요.";
        };
        System.out.println("여름 조언: " + advice);

        // ============================================================
        // 7. record (Java 16+)
        // ============================================================
        printSection("7. record (PersonRecord)");

        PersonRecord person1 = new PersonRecord("홍길동", 25, "hong@example.com");
        PersonRecord person2 = new PersonRecord("홍길동", 25, "hong@example.com");
        PersonRecord person3 = PersonRecord.withDefaultEmail("김철수", 30);

        // 자동 생성된 getter (필드명과 동일)
        System.out.println("이름: " + person1.name());
        System.out.println("나이: " + person1.age());
        System.out.println("이메일: " + person1.email());

        // 자동 생성된 toString
        System.out.println("toString: " + person1);

        // 자동 생성된 equals, hashCode
        System.out.println("equals: " + person1.equals(person2));                     // true
        System.out.println("hashCode 동일: " + (person1.hashCode() == person2.hashCode()));  // true

        // 커스텀 메서드
        System.out.println("자기소개: " + person1.introduce());
        System.out.println("성인 여부: " + person1.isAdult());
        System.out.println("이메일 도메인: " + person1.emailDomain());

        // 팩토리 메서드
        System.out.println("팩토리 메서드: " + person3);

        // 유효성 검증 시연
        try {
            new PersonRecord("", 25, "test@test.com");
        } catch (IllegalArgumentException e) {
            System.out.println("유효성 검증: " + e.getMessage());
        }

        // ============================================================
        // 8. sealed class (Java 17+)
        // ============================================================
        printSection("8. sealed class (Shape)");

        Shape circle = new Circle("빨강", 5.0);
        Shape rectangle = new Rectangle("파랑", 4.0, 6.0);

        Shape[] shapes = {circle, rectangle};
        for (Shape shape : shapes) {
            System.out.println(shape.describe());
        }

        // instanceof 패턴 매칭으로 sealed class 활용
        System.out.println();
        for (Shape shape : shapes) {
            String detail = "";
            if (shape instanceof Circle c) {
                detail = "반지름: " + c.getRadius()
                        + ", 둘레: " + String.format("%.2f", c.circumference());
            } else if (shape instanceof Rectangle r) {
                detail = "가로: " + r.getWidth() + ", 세로: " + r.getHeight()
                        + ", 둘레: " + String.format("%.2f", r.perimeter())
                        + ", 정사각형? " + r.isSquare();
            }
            System.out.println(shape.getClass().getSimpleName() + " 상세: " + detail);
        }

        // ============================================================
        // 마무리
        // ============================================================
        printSection("학습 완료!");
        System.out.println("Chapter 02 OOP의 모든 개념을 실습했습니다.");
        System.out.println("핵심: 캡슐화, 상속, 다형성, 추상화 + enum, record, sealed class");
    }

    /** 섹션 구분선 출력 헬퍼 */
    private static void printSection(String title) {
        System.out.println();
        System.out.println("=".repeat(60));
        System.out.println("  " + title);
        System.out.println("=".repeat(60));
    }
}
