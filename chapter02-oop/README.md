# Chapter 02: 객체지향 프로그래밍 (OOP)

Java의 핵심인 객체지향 프로그래밍의 주요 개념을 학습합니다.

---

## 1. 클래스와 객체

### 클래스란?
클래스는 객체를 생성하기 위한 **설계도(blueprint)** 입니다.
필드(상태)와 메서드(행위)로 구성됩니다.

```java
public class Animal {
    // 필드 (상태)
    private String name;
    private int age;

    // 생성자
    public Animal(String name, int age) {
        this.name = name;
        this.age = age;
    }

    // 메서드 (행위)
    public void speak() {
        System.out.println(name + "이(가) 소리를 냅니다.");
    }
}
```

### 객체란?
클래스를 기반으로 생성된 **실체(instance)** 입니다.

```java
Animal dog = new Animal("멍멍이", 3);  // 객체 생성
dog.speak();  // 메서드 호출
```

### toString, equals, hashCode
`Object` 클래스의 메서드를 오버라이드하여 객체의 동등성과 문자열 표현을 정의합니다.

```java
@Override
public String toString() {
    return "Animal{name='" + name + "', age=" + age + "}";
}

@Override
public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Animal animal)) return false;
    return age == animal.age && Objects.equals(name, animal.name);
}

@Override
public int hashCode() {
    return Objects.hash(name, age);
}
```

---

## 2. 캡슐화 (Encapsulation)

### 접근 제어자

| 접근 제어자 | 같은 클래스 | 같은 패키지 | 하위 클래스 | 전체 |
|------------|:---------:|:---------:|:---------:|:---:|
| `private`   | O | X | X | X |
| `(default)` | O | O | X | X |
| `protected` | O | O | O | X |
| `public`    | O | O | O | O |

### Getter / Setter
필드를 `private`으로 선언하고, 공개 메서드를 통해 접근합니다.

```java
public class Animal {
    private String name;

    // Getter
    public String getName() {
        return name;
    }

    // Setter (유효성 검증 가능)
    public void setName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("이름은 비어있을 수 없습니다.");
        }
        this.name = name;
    }
}
```

---

## 3. 상속과 다형성

### 상속 (Inheritance)
`extends` 키워드로 부모 클래스의 필드와 메서드를 물려받습니다.

```java
public class Dog extends Animal {
    private String breed;

    public Dog(String name, int age, String breed) {
        super(name, age);  // 부모 생성자 호출
        this.breed = breed;
    }

    @Override
    public String speak() {
        return getName() + "이(가) 멍멍! 하고 짖습니다.";
    }
}
```

### 다형성 (Polymorphism)
부모 타입의 참조 변수로 자식 객체를 다룰 수 있습니다.

```java
Animal animal1 = new Dog("바둑이", 3, "진돗개");
Animal animal2 = new Cat("나비", 2, true);

// 같은 메서드 호출이지만 실제 타입에 따라 다른 동작
animal1.speak();  // "바둑이이(가) 멍멍! 하고 짖습니다."
animal2.speak();  // "나비이(가) 야옹~ 하고 웁니다."
```

### instanceof와 패턴 매칭 (Java 16+)

```java
if (animal instanceof Dog dog) {
    System.out.println("품종: " + dog.getBreed());
}
```

---

## 4. 추상 클래스와 인터페이스

### 추상 클래스 (Abstract Class)
- `abstract` 키워드로 선언
- 직접 인스턴스 생성 불가
- 추상 메서드와 일반 메서드를 모두 가질 수 있음

```java
public abstract class Animal {
    public abstract String speak();  // 하위 클래스에서 반드시 구현

    public String breathe() {        // 일반 메서드도 가능
        return "숨을 쉽니다.";
    }
}
```

### 인터페이스 (Interface)
- 다중 구현 가능 (`implements`)
- Java 8+: `default` 메서드, `static` 메서드
- Java 9+: `private` 메서드

```java
public interface Flyable {
    // 추상 메서드
    String fly();

    // default 메서드 (기본 구현 제공)
    default String land() {
        return "착륙합니다.";
    }

    // static 메서드
    static String description() {
        return "날 수 있는 능력을 나타내는 인터페이스입니다.";
    }
}
```

### 추상 클래스 vs 인터페이스

| 구분 | 추상 클래스 | 인터페이스 |
|------|-----------|-----------|
| 다중 상속 | 불가 (단일 상속) | 가능 (다중 구현) |
| 필드 | 인스턴스 변수 가능 | 상수만 가능 (`public static final`) |
| 생성자 | 있음 | 없음 |
| 용도 | IS-A 관계 | CAN-DO / HAS-A 능력 |

---

## 5. enum (열거형)

상수의 집합을 타입 안전하게 정의합니다.

```java
public enum Season {
    SPRING("봄", "3월~5월"),
    SUMMER("여름", "6월~8월"),
    AUTUMN("가을", "9월~11월"),
    WINTER("겨울", "12월~2월");

    private final String koreanName;
    private final String period;

    Season(String koreanName, String period) {
        this.koreanName = koreanName;
        this.period = period;
    }

    public String describe() {
        return koreanName + " (" + period + ")";
    }
}
```

### enum 활용
- `values()` : 모든 상수 배열 반환
- `valueOf("SPRING")` : 이름으로 상수 조회
- `name()`, `ordinal()` : 이름, 순번 조회
- `switch` 문에서 활용 가능

---

## 6. record (Java 16+)

불변 데이터 객체를 간결하게 정의합니다.
`equals()`, `hashCode()`, `toString()`, getter가 자동 생성됩니다.

```java
public record PersonRecord(String name, int age, String email) {

    // 컴팩트 생성자 (유효성 검증)
    public PersonRecord {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("이름은 필수입니다.");
        }
        if (age < 0) {
            throw new IllegalArgumentException("나이는 0 이상이어야 합니다.");
        }
    }

    // 커스텀 메서드
    public String introduce() {
        return name + " (" + age + "세, " + email + ")";
    }
}
```

### record 특징
- 모든 필드는 `private final` (불변)
- `getter`는 필드명과 동일 (`name()`, `age()`)
- 상속 불가 (`final` 클래스)
- 인터페이스 구현 가능

---

## 7. sealed class (Java 17+)

상속할 수 있는 클래스를 **명시적으로 제한**합니다.

```java
// Shape를 상속할 수 있는 클래스를 permits로 지정
public sealed class Shape permits Circle, Rectangle {
    // ...
}

// final: 더 이상 상속 불가
public final class Circle extends Shape { }

// non-sealed: 자유롭게 상속 가능
public non-sealed class Rectangle extends Shape { }
```

### sealed class 허용되는 하위 클래스 키워드
- `final` : 더 이상 상속 불가
- `sealed` : 다시 제한된 상속 허용
- `non-sealed` : 자유로운 상속 허용

### switch 패턴 매칭과 함께 사용 (Java 21+)

```java
String info = switch (shape) {
    case Circle c    -> "반지름: " + c.getRadius();
    case Rectangle r -> "가로: " + r.getWidth() + ", 세로: " + r.getHeight();
};
```

---

## 실행 방법

### Docker로 실행
```bash
docker build -t chapter02-oop .
docker run --rm chapter02-oop
```

### 직접 컴파일 및 실행
```bash
mkdir -p out
javac -d out src/main/java/com/edu/oop/*.java
java -cp out com.edu.oop.OopMain
```

---

## 핵심 정리

| 개념 | 핵심 키워드 | 설명 |
|------|-----------|------|
| 클래스/객체 | `class`, `new` | 설계도와 실체 |
| 캡슐화 | `private`, getter/setter | 데이터 보호 |
| 상속 | `extends`, `super` | 코드 재사용 |
| 다형성 | 오버라이딩, 업캐스팅 | 유연한 설계 |
| 추상 클래스 | `abstract` | 공통 뼈대 정의 |
| 인터페이스 | `interface`, `implements` | 능력/계약 정의 |
| enum | `enum` | 타입 안전한 상수 |
| record | `record` | 불변 데이터 객체 |
| sealed class | `sealed`, `permits` | 제한된 상속 |
