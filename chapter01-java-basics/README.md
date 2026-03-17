# Chapter 01: Java Basics (자바 기초)

Java 프로그래밍의 기초를 다루는 챕터입니다. 변수, 연산자, 제어문, 배열, 메서드, 예외처리까지 핵심 개념을 학습합니다.

---

## 목차

1. [변수와 데이터 타입](#1-변수와-데이터-타입)
2. [연산자](#2-연산자)
3. [제어문](#3-제어문)
4. [배열](#4-배열)
5. [메서드](#5-메서드)
6. [예외처리 기초](#6-예외처리-기초)

---

## 1. 변수와 데이터 타입

> POC 코드: [`VariablesAndTypes.java`](src/main/java/com/edu/basics/VariablesAndTypes.java)

### 1.1 기본형 (Primitive Types)

Java는 8가지 기본형 데이터 타입을 제공합니다.

| 타입      | 크기    | 범위 / 설명                          |
|-----------|---------|--------------------------------------|
| `byte`    | 1 byte  | -128 ~ 127                           |
| `short`   | 2 bytes | -32,768 ~ 32,767                     |
| `int`     | 4 bytes | -2^31 ~ 2^31-1 (약 21억)            |
| `long`    | 8 bytes | -2^63 ~ 2^63-1                       |
| `float`   | 4 bytes | 단정밀도 부동소수점                  |
| `double`  | 8 bytes | 배정밀도 부동소수점                  |
| `char`    | 2 bytes | 유니코드 문자 하나                   |
| `boolean` | 1 bit*  | `true` 또는 `false`                  |

### 1.2 참조형 (Reference Types)

기본형을 제외한 모든 타입은 참조형입니다. 객체의 메모리 주소를 저장합니다.

- `String` - 문자열 (불변 객체)
- 배열 (`int[]`, `String[]` 등)
- 클래스, 인터페이스, 열거형

### 1.3 형변환 (Type Casting)

- **자동 형변환 (Widening)**: 작은 타입 -> 큰 타입 (데이터 손실 없음)
  ```java
  int num = 100;
  long bigNum = num; // 자동 형변환
  ```
- **강제 형변환 (Narrowing)**: 큰 타입 -> 작은 타입 (데이터 손실 가능)
  ```java
  double pi = 3.14;
  int intPi = (int) pi; // 3 (소수점 손실)
  ```

### 1.4 var 키워드 (Java 10+)

지역 변수의 타입을 컴파일러가 추론합니다.

```java
var message = "Hello";    // String으로 추론
var numbers = List.of(1, 2, 3); // List<Integer>로 추론
```

---

## 2. 연산자

> POC 코드: [`VariablesAndTypes.java`](src/main/java/com/edu/basics/VariablesAndTypes.java) 내 연산자 섹션

### 2.1 산술 연산자

| 연산자 | 설명   | 예시        |
|--------|--------|-------------|
| `+`    | 덧셈   | `5 + 3 = 8` |
| `-`    | 뺄셈   | `5 - 3 = 2` |
| `*`    | 곱셈   | `5 * 3 = 15`|
| `/`    | 나눗셈 | `5 / 3 = 1` |
| `%`    | 나머지 | `5 % 3 = 2` |

### 2.2 비교 연산자

`==`, `!=`, `>`, `<`, `>=`, `<=`

### 2.3 논리 연산자

- `&&` (AND), `||` (OR), `!` (NOT)
- 단축 평가(Short-circuit evaluation): 왼쪽 조건만으로 결과가 확정되면 오른쪽은 평가하지 않음

### 2.4 비트 연산자

`&`, `|`, `^`, `~`, `<<`, `>>`, `>>>`

---

## 3. 제어문

> POC 코드: [`ControlFlow.java`](src/main/java/com/edu/basics/ControlFlow.java)

### 3.1 조건문

#### if / else if / else

```java
if (score >= 90) {
    grade = "A";
} else if (score >= 80) {
    grade = "B";
} else {
    grade = "C";
}
```

#### switch 문

기존 switch 문과 Java 14+ switch 표현식을 모두 지원합니다.

```java
// Java 14+ switch 표현식 (화살표 구문)
String result = switch (day) {
    case MONDAY, FRIDAY -> "근무일";
    case SATURDAY, SUNDAY -> "휴일";
    default -> "평일";
};
```

### 3.2 반복문

| 종류           | 용도                              |
|----------------|-----------------------------------|
| `for`          | 반복 횟수가 정해진 경우           |
| `enhanced for` | 배열/컬렉션 순회                  |
| `while`        | 조건이 참인 동안 반복             |
| `do-while`     | 최소 1번 실행 후 조건 검사        |

### 3.3 분기문

- `break` - 반복문 즉시 종료
- `continue` - 현재 반복 건너뛰기
- **레이블(Label)** - 중첩 반복문에서 외부 루프 제어

```java
outer:
for (int i = 0; i < 3; i++) {
    for (int j = 0; j < 3; j++) {
        if (j == 1) continue outer; // 외부 루프의 다음 반복으로
    }
}
```

---

## 4. 배열

> POC 코드: [`ArraysAndMethods.java`](src/main/java/com/edu/basics/ArraysAndMethods.java)

### 4.1 1차원 배열

```java
int[] numbers = {1, 2, 3, 4, 5};
int[] scores = new int[10]; // 크기 10, 기본값 0
```

### 4.2 2차원 배열

```java
int[][] matrix = {
    {1, 2, 3},
    {4, 5, 6},
    {7, 8, 9}
};
```

배열은 **고정 크기**이며 생성 후 크기를 변경할 수 없습니다. 가변 크기가 필요하면 `ArrayList`를 사용합니다.

---

## 5. 메서드

> POC 코드: [`ArraysAndMethods.java`](src/main/java/com/edu/basics/ArraysAndMethods.java)

### 5.1 메서드 구조

```java
접근제어자 반환타입 메서드이름(매개변수) {
    // 실행 코드
    return 반환값;
}
```

### 5.2 가변인자 (Varargs)

매개변수 개수가 정해지지 않은 경우 사용합니다.

```java
public static int sum(int... numbers) {
    int total = 0;
    for (int n : numbers) total += n;
    return total;
}
```

### 5.3 메서드 오버로딩 (Overloading)

같은 이름의 메서드를 매개변수의 타입이나 개수를 달리하여 여러 개 정의할 수 있습니다.

### 5.4 재귀 (Recursion)

메서드가 자기 자신을 호출하는 기법입니다. 반드시 **종료 조건(base case)**이 필요합니다.

---

## 6. 예외처리 기초

> POC 코드: [`ExceptionBasics.java`](src/main/java/com/edu/basics/ExceptionBasics.java)

### 6.1 예외 계층 구조

```
Throwable
├── Error (시스템 오류, 처리 불필요)
└── Exception
    ├── RuntimeException (Unchecked - 컴파일러 검사 X)
    │   ├── NullPointerException
    │   ├── ArrayIndexOutOfBoundsException
    │   └── ArithmeticException
    └── IOException 등 (Checked - 반드시 처리 필요)
```

### 6.2 try-catch-finally

```java
try {
    // 예외 발생 가능 코드
} catch (예외타입 변수) {
    // 예외 처리
} finally {
    // 항상 실행 (리소스 정리)
}
```

### 6.3 try-with-resources (Java 7+)

`AutoCloseable`을 구현한 리소스를 자동으로 닫아줍니다.

```java
try (var reader = new BufferedReader(new FileReader("file.txt"))) {
    // reader 사용
} // 자동으로 reader.close() 호출
```

### 6.4 커스텀 예외

비즈니스 로직에 맞는 예외를 직접 정의할 수 있습니다.

```java
public class InsufficientBalanceException extends Exception {
    public InsufficientBalanceException(String message) {
        super(message);
    }
}
```

---

## 실행 방법

### 로컬 실행 (JDK 21 필요)

```bash
# 컴파일
mkdir -p out
javac -d out src/main/java/com/edu/basics/*.java

# 각 파일 실행
java -cp out com.edu.basics.VariablesAndTypes
java -cp out com.edu.basics.ControlFlow
java -cp out com.edu.basics.ArraysAndMethods
java -cp out com.edu.basics.ExceptionBasics
```

### Docker로 실행

```bash
# 기본 실행 (VariablesAndTypes)
docker compose up --build

# 특정 클래스 실행
docker compose run java-basics java -cp out com.edu.basics.ControlFlow
docker compose run java-basics java -cp out com.edu.basics.ArraysAndMethods
docker compose run java-basics java -cp out com.edu.basics.ExceptionBasics
```
