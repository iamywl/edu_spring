# Chapter 02: 객체지향 프로그래밍 (OOP) - 실습 가이드

> 이 문서는 **실습 가이드**입니다. OOP 개념의 상세한 설명은 아래 문서를 참고하세요.
> - [JAVA_개념서](../docs/JAVA_개념서.md) - Chapter 4: 객체지향 프로그래밍 (왜 OOP인가, 4대 원칙, SOLID)
> - [JAVA_교육자료](../docs/JAVA_교육자료.md) - Part 2: OOP 코드 예제 (2.1~2.8)

---

## 실행 방법

```bash
# Docker 컨테이너 내에서 실행 - 개념당 스크립트 1개
./run.sh InheritanceExample     # 상속
./run.sh PolymorphismExample    # 다형성
# 또는 대화형 메뉴: ./run.sh 실행 후 카테고리(Chapter 02) 선택 → 개념 선택
```

각 개념은 이제 독립 실행 스크립트로 분리되어 있습니다(**개념당 스크립트 1개**). `./run.sh <ClassName>`으로 원하는 개념만 바로 실행하거나, 인자 없이 `./run.sh`를 실행해 대화형 메뉴에서 카테고리(Chapter 02)를 고른 뒤 개념을 선택할 수 있습니다.

**실행 가능한 개념 스크립트(10개)**: `InheritanceExample`, `PolymorphismExample`, `EncapsulationExample`, `ObjectMethodsExample`, `PatternMatchingExample`, `InterfaceExample`, `EnumExample`, `RecordExample`, `SealedClassExample`, `NestedClassExample`

---

## 프로젝트 구조

```
src/main/java/com/edu/oop/
  InheritanceExample.java     # 상속 데모
  PolymorphismExample.java    # 다형성 데모
  EncapsulationExample.java   # 캡슐화 데모
  ObjectMethodsExample.java   # toString/equals/hashCode 데모
  PatternMatchingExample.java # instanceof 패턴 매칭 데모
  InterfaceExample.java       # 인터페이스 데모
  EnumExample.java            # enum 데모
  RecordExample.java          # record 데모
  SealedClassExample.java     # sealed class 데모
  NestedClassExample.java     # 중첩 클래스 4종 (정적 중첩/내부/지역/익명)
  Animal.java          # 추상 클래스 (공통 필드, 추상 메서드, equals/hashCode)
  Dog.java             # Animal 상속 - 개
  Cat.java             # Animal 상속 - 고양이
  Bird.java            # Animal 상속 + Flyable 구현 - 새
  Flyable.java         # 인터페이스 (추상/default/static 메서드)
  Season.java          # enum (필드, 생성자, 커스텀 메서드)
  PersonRecord.java    # record (컴팩트 생성자, 팩토리 메서드)
  Shape.java           # sealed class
  Circle.java          # Shape의 final 하위 클래스
  Rectangle.java       # Shape의 non-sealed 하위 클래스
```

---

## 세션별 실습

### 세션 1: 클래스와 객체, 상속, 다형성

**개념 학습**
- [JAVA_개념서](../docs/JAVA_개념서.md) - Chapter 4: "절차적 vs 객체지향", "캡슐화", "상속", "다형성" 읽기
- [JAVA_교육자료](../docs/JAVA_교육자료.md) - Part 2.1~2.4: "클래스와 객체", "상속", "다형성" 읽기

**예제 코드 분석**
- 파일: `Animal.java`, `Dog.java`, `Cat.java`, `Bird.java`, `InheritanceExample.java`, `PolymorphismExample.java`
- 실행 스크립트: `InheritanceExample`(상속), `PolymorphismExample`(다형성)
- 주목할 포인트:
  - `Animal` 타입 변수로 `Dog`, `Cat`, `Bird` 객체를 참조 (다형성)
  - `Animal[]` 배열을 순회하며 `speak()` 호출 시 실제 타입에 따라 다른 결과 출력
  - `super(name, age)` 호출로 부모 생성자에 값 전달

**예제 실행**
- `./run.sh InheritanceExample`, `./run.sh PolymorphismExample`
- 출력에서 확인할 것:
  - 각 동물의 `info()`가 하위 클래스마다 다른 정보를 포함하는지
  - `speak()`가 동물별로 다른 울음소리를 반환하는지
  - `breathe()`는 모든 동물이 동일한 동작을 하는지

**실습 과제**
1. `Fish` 클래스를 만들어 `Animal`을 상속받아보세요. `speak()`에서 `"뻐끔뻐끔"`을 반환하게 하세요.
2. `PolymorphismExample`의 `animals` 배열에 `Fish` 객체를 추가하고 다시 실행해보세요.
3. `Fish`만의 고유 메서드 `swim()`을 추가해보세요.

---

### 세션 2: 캡슐화

**개념 학습**
- [JAVA_개념서](../docs/JAVA_개념서.md) - Chapter 4: "캡슐화" (자동차 비유) 읽기
- [JAVA_교육자료](../docs/JAVA_교육자료.md) - Part 2.2: "캡슐화" 읽기

**예제 코드 분석**
- 파일: `Animal.java` (private 필드 + getter/setter), `Dog.java`, `EncapsulationExample.java`
- 실행 스크립트: `EncapsulationExample`
- 주목할 포인트:
  - `Animal`의 필드가 `private`으로 선언되어 외부에서 직접 접근 불가
  - `setName("")` 호출 시 유효성 검증이 작동하여 예외 발생
  - getter/setter를 통해서만 필드 값에 접근 가능

**예제 실행**
- `./run.sh EncapsulationExample`
- 출력에서 확인할 것:
  - getter로 이름, 나이, 품종 조회 결과
  - setter로 이름/나이 변경 후 `info()` 출력 변화
  - 빈 이름 설정 시 `IllegalArgumentException` 메시지

**실습 과제**
1. `Animal.setAge(-1)`을 호출해서 음수 나이 유효성 검증이 작동하는지 확인하세요.
2. `Dog`에 `setBreed()` 메서드를 추가하고, 빈 문자열이 들어오면 예외를 던지도록 만들어보세요.

---

### 세션 3: toString, equals, hashCode

**개념 학습**
- [JAVA_교육자료](../docs/JAVA_교육자료.md) - Part 2.1: "toString, equals, hashCode" 읽기

**예제 코드 분석**
- 파일: `Animal.java` (equals/hashCode 오버라이드), `Dog.java` (toString 오버라이드), `ObjectMethodsExample.java`
- 실행 스크립트: `ObjectMethodsExample`
- 주목할 포인트:
  - `==`는 참조(주소) 비교, `equals()`는 내용 비교
  - `Animal.equals()`가 `name`과 `age`만 비교 (breed는 비교하지 않음)
  - `equals()`가 true이면 `hashCode()`도 동일해야 하는 규칙

**예제 실행**
- `./run.sh ObjectMethodsExample`
- 출력에서 확인할 것:
  - `dog1 == dog2`가 `false`인 이유 (서로 다른 객체)
  - `dog1.equals(dog2)`가 `true`인 이유 (같은 이름, 같은 나이)
  - `hashCode`가 동일한지

**실습 과제**
1. `Dog`의 `equals()`를 오버라이드하여 `breed`까지 비교하도록 수정해보세요. `hashCode()`도 함께 수정하세요.
2. 같은 이름/나이지만 다른 품종의 `Dog` 두 개를 만들어서 `equals()` 결과가 달라지는지 확인하세요.

---

### 세션 4: instanceof 패턴 매칭

**개념 학습**
- [JAVA_교육자료](../docs/JAVA_교육자료.md) - Part 2.4: "다형성", "instanceof 패턴 매칭" 읽기

**예제 코드 분석**
- 파일: `PatternMatchingExample.java`
- 실행 스크립트: `PatternMatchingExample`
- 주목할 포인트:
  - Java 16+ 패턴 매칭: `if (animal instanceof Dog d)` 에서 타입 검사와 캐스팅을 동시에 수행
  - 각 타입별 고유 메서드 호출 (`fetch()`, `purr()`, `fly()`)
  - 기존 방식 `((Dog) animal).fetch()` 대비 간결함

**예제 실행**
- `./run.sh PatternMatchingExample`
- 출력에서 확인할 것:
  - 각 동물이 자기 타입에 맞는 고유 행동을 출력하는지

**실습 과제**
1. 세션 1에서 만든 `Fish`에 대한 `instanceof` 분기를 추가하고, `swim()`을 호출하도록 수정해보세요.
2. `switch` 패턴 매칭(Java 21+)으로 `if-else` 체인을 변환해보세요:
   ```java
   String result = switch (animal) {
       case Dog d -> d.fetch();
       case Cat c -> c.purr();
       case Bird b -> b.fly();
       default -> "알 수 없는 동물";
   };
   ```

---

### 세션 5: 인터페이스

**개념 학습**
- [JAVA_개념서](../docs/JAVA_개념서.md) - Chapter 4: "추상 클래스 vs 인터페이스" 읽기
- [JAVA_교육자료](../docs/JAVA_교육자료.md) - Part 2.5: "추상 클래스와 인터페이스" 읽기

**예제 코드 분석**
- 파일: `Flyable.java`, `Bird.java`, `InterfaceExample.java`
- 실행 스크립트: `InterfaceExample`
- 주목할 포인트:
  - `Flyable` 인터페이스에 추상 메서드(`fly()`, `getMaxAltitude()`), default 메서드(`land()`, `flightStatus()`), static 메서드(`description()`, `isSafeAltitude()`) 세 종류가 있음
  - `Bird`가 `Animal extends` + `Flyable implements` 동시에 사용
  - `Bird`가 `land()`를 오버라이드하여 기본 구현을 대체

**예제 실행**
- `./run.sh InterfaceExample`
- 출력에서 확인할 것:
  - `Flyable.description()` static 메서드 호출 결과
  - `flightStatus()` default 메서드의 기본 동작
  - `land()` 오버라이드된 결과와 기본 구현의 차이
  - `isSafeAltitude()` 유틸리티 메서드 결과

**실습 과제**
1. `Swimmable` 인터페이스를 만들어보세요 (추상 메서드: `swim()`, `getMaxDepth()`, default 메서드: `dive()`).
2. 세션 1에서 만든 `Fish`에 `Swimmable`을 구현해보세요.
3. `Bird`의 `land()` 오버라이드를 삭제하고, default 구현이 사용되는지 확인해보세요.

---

### 세션 6: enum

**개념 학습**
- [JAVA_교육자료](../docs/JAVA_교육자료.md) - Part 2.6: "enum" 읽기

**예제 코드 분석**
- 파일: `Season.java`, `EnumExample.java`
- 실행 스크립트: `EnumExample`
- 주목할 포인트:
  - 각 enum 상수가 필드 값(`koreanName`, `period`, `avgTemperature`)을 가짐
  - `values()`, `valueOf()`, `name()`, `ordinal()` 기본 메서드 활용
  - `fromKoreanName()` 커스텀 검색 메서드와 switch 표현식 활용

**예제 실행**
- `./run.sh EnumExample`
- 출력에서 확인할 것:
  - 모든 계절 순회 시 ordinal 값 순서
  - `isHot()`, `isCold()` 판별 결과
  - `valueOf("AUTUMN")`, `fromKoreanName("봄")` 조회 결과

**실습 과제**
1. `Season`에 `isWarm()` 메서드를 추가해보세요 (평균 기온 10도 이상일 때 true).
2. `getNextSeason()` 메서드를 추가해보세요 (봄 -> 여름 -> 가을 -> 겨울 -> 봄). 힌트: `values()`와 `ordinal()` 활용.
3. `fromKoreanName("장마")` 처럼 없는 이름으로 호출하면 어떤 예외가 발생하는지 확인해보세요.

---

### 세션 7: record

**개념 학습**
- [JAVA_교육자료](../docs/JAVA_교육자료.md) - Part 2.7: "record" 읽기

**예제 코드 분석**
- 파일: `PersonRecord.java`, `RecordExample.java`
- 실행 스크립트: `RecordExample`
- 주목할 포인트:
  - `record` 한 줄로 `private final` 필드, 생성자, getter, `equals()`, `hashCode()`, `toString()` 자동 생성
  - 컴팩트 생성자에서 유효성 검증 (빈 이름, 잘못된 이메일)
  - 정적 팩토리 메서드 `withDefaultEmail()` 패턴

**예제 실행**
- `./run.sh RecordExample`
- 출력에서 확인할 것:
  - record의 getter가 `getName()`이 아니라 `name()`인 점
  - 자동 생성된 `toString()` 형식
  - 두 record 객체의 `equals()`, `hashCode()` 동일성
  - 빈 이름으로 생성 시 유효성 검증 예외

**실습 과제**
1. `AddressRecord(String city, String street, String zipCode)` record를 만들어보세요. zipCode 유효성 검증을 추가하세요.
2. `PersonRecord`에 `AddressRecord`를 포함하는 새 record `EmployeeRecord(String name, int age, AddressRecord address)`를 만들어보세요.
3. record는 상속이 불가능합니다. 시도해보고 컴파일 에러 메시지를 확인하세요.

---

### 세션 8: sealed class

**개념 학습**
- [JAVA_교육자료](../docs/JAVA_교육자료.md) - Part 2.8: "sealed class" 읽기

**예제 코드 분석**
- 파일: `Shape.java`, `Circle.java`, `Rectangle.java`, `SealedClassExample.java`
- 실행 스크립트: `SealedClassExample`
- 주목할 포인트:
  - `sealed class Shape permits Circle, Rectangle` - 상속 가능 클래스를 명시적으로 제한
  - `Circle`은 `final` (더 이상 상속 불가), `Rectangle`은 `non-sealed` (자유롭게 상속 가능)
  - `instanceof` 패턴 매칭으로 sealed class의 하위 타입별 처리

**예제 실행**
- `./run.sh SealedClassExample`
- 출력에서 확인할 것:
  - 각 도형의 `describe()` 출력 (색상, 넓이)
  - `instanceof` 패턴 매칭으로 타입별 상세 정보 (반지름/둘레, 가로/세로/정사각형 여부)

**실습 과제**
1. `Triangle` 클래스를 만들고 `Shape`의 `permits`에 추가해보세요. `final`로 선언하고 `area()`를 구현하세요.
2. `Circle`을 상속하는 클래스를 만들려고 시도해보세요. `final` 때문에 컴파일 에러가 나는지 확인하세요.
3. `Rectangle`은 `non-sealed`이므로 `Square extends Rectangle`을 만들 수 있습니다. 시도해보세요.
4. `permits`에 없는 클래스가 `Shape`를 상속하면 어떤 에러가 나는지 확인해보세요.

---

### 세션 9: 중첩 클래스

**개념 학습**
- [JAVA_교육자료](../docs/JAVA_교육자료.md) - "중첩 클래스, 익명 클래스" 참고

**예제 코드 분석**
- 파일: `NestedClassExample.java` (독립 실행)
- 주목할 포인트:
  - **정적 중첩 클래스(static nested)**: 바깥 인스턴스 없이 생성
  - **내부 클래스(inner)**: `바깥인스턴스.new Inner()` 문법, 바깥 클래스의 private 필드 접근 가능
  - **지역 클래스(local)**: 메서드 안에 선언, 사실상 final 지역 변수 캡처
  - **익명 클래스(anonymous)**: 인터페이스를 즉석에서 구현, 함수형 인터페이스는 람다로 대체 가능

**예제 실행**
- `./run.sh NestedClassExample`
- 출력에서 확인할 것:
  - 내부 클래스가 바깥 필드(`outerField`)에 접근하는 것
  - 익명 클래스 구현과 동일한 일을 하는 람다 표현식의 간결함

**실습 과제**
1. `Greeting`을 구현하는 익명 클래스를 하나 더 만들고, 같은 동작을 람다로도 작성해 비교해보세요.
2. 정적 중첩 클래스와 내부 클래스의 생성 방법 차이를 코드로 확인해보세요.

---

## 핵심 정리

| 개념 | 핵심 키워드 | 예제 파일 |
|---|---|---|
| 상속/다형성 | `extends`, `@Override`, 부모 타입 참조 | Animal, Dog, Cat, Bird |
| 캡슐화 | `private` 필드, getter/setter, 유효성 검증 | Animal |
| 인터페이스 | `implements`, `default`, `static` 메서드 | Flyable, Bird |
| enum | 상수 + 필드 + 메서드, `values()`, `valueOf()` | Season |
| record | 불변 데이터, 자동 생성, 컴팩트 생성자 | PersonRecord |
| sealed class | `sealed`, `permits`, `final`/`non-sealed` | Shape, Circle, Rectangle |
| 중첩 클래스 | static nested / inner / local / anonymous, 람다 | NestedClassExample |
