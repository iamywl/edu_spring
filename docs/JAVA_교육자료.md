# Java 프로그래밍 교육자료

> 본 문서는 Java 프로그래밍의 기초부터 컬렉션, 함수형 프로그래밍까지를 다루는 종합 교육자료이다.
> Chapter 01~03 및 실습 환경 안내를 하나의 문서로 통합한 자료이다.

---

## Part 1: Java 기초

---

### 1.1 Java 소개

#### Java 언어의 특징

Java는 1995년 Sun Microsystems의 제임스 고슬링(James Gosling)이 개발한 범용 프로그래밍 언어이다.
다음과 같은 핵심 특징을 가진다.

| 특징 | 설명 |
|------|------|
| 플랫폼 독립성 | "Write Once, Run Anywhere" - 바이트코드로 컴파일되어 JVM 위에서 실행 |
| 객체지향 | 캡슐화, 상속, 다형성, 추상화를 지원하는 객체지향 언어 |
| 자동 메모리 관리 | Garbage Collector(GC)가 사용하지 않는 객체를 자동으로 회수 |
| 강타입 언어 | 컴파일 시점에 타입 검사를 수행하여 안정성 확보 |
| 멀티스레드 지원 | 언어 차원에서 스레드 생성과 동기화를 지원 |
| 풍부한 표준 라이브러리 | java.lang, java.util, java.io 등 방대한 API 제공 |

#### JDK, JRE, JVM 관계

```
+--------------------------------------------------+
|                    JDK (Java Development Kit)      |
|  +----------------------------------------------+ |
|  |              JRE (Java Runtime Environment)   | |
|  |  +------------------------------------------+| |
|  |  |            JVM (Java Virtual Machine)     || |
|  |  |  - 클래스 로더                              || |
|  |  |  - 바이트코드 검증기                         || |
|  |  |  - JIT 컴파일러                             || |
|  |  |  - Garbage Collector                      || |
|  |  +------------------------------------------+| |
|  |  + 핵심 라이브러리 (java.base 모듈 등)            | |
|  +----------------------------------------------+ |
|  + 개발 도구 (javac, javadoc, jdb, jar 등)         |
+--------------------------------------------------+
```

- **JVM**: 바이트코드를 해당 OS의 기계어로 변환하여 실행하는 가상 머신
- **JRE**: JVM + 표준 클래스 라이브러리. 프로그램 실행에 필요한 최소 환경
  > **참고**: Java 11부터 Oracle은 별도의 JRE를 배포하지 않는다. JDK가 곧 배포 단위이며, `jlink`로 커스텀 런타임을 생성할 수 있다.
- **JDK**: JRE + 컴파일러(javac), 디버거(jdb) 등 개발 도구 포함

#### Java 버전 변천사

| 버전 | 출시 | 주요 기능 |
|------|------|----------|
| Java 8 (LTS) | 2014.03 | Lambda, Stream API, Optional, default method, LocalDateTime |
| Java 9 | 2017.09 | 모듈 시스템(Jigsaw), JShell, private interface method |
| Java 10 | 2018.03 | var (지역 변수 타입 추론) |
| Java 11 (LTS) | 2018.09 | HTTP Client API, String 메서드 추가, var in lambda |
| Java 14 | 2020.03 | switch expressions(정식), record(preview), NullPointerException 개선 |
| Java 16 | 2021.03 | record(정식), instanceof 패턴 매칭(정식) |
| Java 17 (LTS) | 2021.09 | sealed class(정식), switch 패턴 매칭(preview) |
| Java 21 (LTS) | 2023.09 | Virtual Thread, 패턴 매칭 switch(정식), 순차 컬렉션 |

#### 개발환경 설정 (Docker 기반)

Docker를 활용하면 로컬 환경에 JDK를 직접 설치하지 않고도 Java 개발환경을 구성할 수 있다.

```dockerfile
# Dockerfile
FROM eclipse-temurin:21-jdk

WORKDIR /app
COPY . .

RUN javac Main.java
CMD ["java", "Main"]
```

```bash
# 빌드 및 실행
docker build -t java-edu .
docker run --rm java-edu
```

---

### 1.2 변수와 데이터 타입

#### Primitive Types (기본형) 8가지

Java의 기본 데이터 타입은 총 8가지이며, 스택 메모리에 직접 값이 저장된다.

| 타입 | 크기 | 기본값 | 최소값 | 최대값 | 설명 |
|------|------|--------|--------|--------|------|
| `byte` | 1 byte | 0 | -128 | 127 | 작은 정수 |
| `short` | 2 bytes | 0 | -32,768 | 32,767 | 짧은 정수 |
| `int` | 4 bytes | 0 | -2^31 | 2^31 - 1 | 정수 (기본) |
| `long` | 8 bytes | 0L | -2^63 | 2^63 - 1 | 큰 정수 |
| `float` | 4 bytes | 0.0f | ~1.4E-45 | ~3.4E+38 | 단정밀도 실수 |
| `double` | 8 bytes | 0.0d | ~4.9E-324 | ~1.8E+308 | 배정밀도 실수 (기본) |
| `char` | 2 bytes | '\u0000' | 0 | 65,535 | 유니코드 문자 |
| `boolean` | 1 bit* | false | false | true | 논리값 |

> *boolean의 실제 메모리 크기는 JVM 구현에 따라 다르다 (보통 1 byte).

```java
public class PrimitiveTypeExample {
    public static void main(String[] args) {
        // 정수형
        byte b = 127;
        short s = 32767;
        int i = 2_147_483_647;     // 언더스코어로 가독성 향상 (Java 7+)
        long l = 9_223_372_036_854_775_807L;  // L 접미사 필수

        // 실수형
        float f = 3.14F;           // F 접미사 필수
        double d = 3.141592653589793;

        // 문자형과 논리형
        char c = '가';             // 유니코드 지원
        boolean flag = true;

        System.out.println("int 최대값: " + Integer.MAX_VALUE);
        System.out.println("long 최대값: " + Long.MAX_VALUE);
    }
}
```

#### Reference Types (참조형)

참조형 변수는 힙(Heap) 메모리에 있는 객체의 주소를 저장한다.

```java
// String
String name = "홍길동";                // 문자열 리터럴 (String Pool에 저장)
String name2 = new String("홍길동");   // 새로운 객체 생성 (권장하지 않음)

// 배열
int[] numbers = {1, 2, 3, 4, 5};
String[] names = new String[3];

// 클래스 (사용자 정의 타입)
Student student = new Student("김철수", 20);
```

#### 형변환 (Type Casting)

```java
public class TypeCastingExample {
    public static void main(String[] args) {
        // === Widening (자동 형변환, 암시적) ===
        // byte → short → int → long → float → double
        int intVal = 100;
        long longVal = intVal;        // int → long (자동)
        double doubleVal = longVal;   // long → double (자동)
        System.out.println("자동 형변환: " + doubleVal);  // 100.0

        // === Narrowing (명시적 형변환, 캐스팅) ===
        double pi = 3.14159;
        int intPi = (int) pi;        // double → int (소수점 버림)
        System.out.println("명시적 형변환: " + intPi);    // 3

        // 오버플로우 주의
        int bigNumber = 130;
        byte byteVal = (byte) bigNumber;
        System.out.println("오버플로우: " + byteVal);     // -126

        // 문자열 ↔ 숫자 변환
        String str = "123";
        int num = Integer.parseInt(str);
        String back = String.valueOf(num);
    }
}
```

#### var 키워드 (Java 10+)

컴파일러가 초기화 값으로부터 타입을 추론한다. 지역 변수에만 사용 가능하다.

```java
public class VarExample {
    public static void main(String[] args) {
        var message = "안녕하세요";          // String으로 추론
        var numbers = List.of(1, 2, 3);     // List<Integer>로 추론
        var map = new HashMap<String, Integer>(); // HashMap<String, Integer>

        // 사용 불가한 경우
        // var x;                // 초기화 없이 사용 불가
        // var y = null;         // null로는 추론 불가
        // var z = {1, 2, 3};    // 배열 초기화에서 사용 불가
    }

    // var result = "hello";    // 필드(멤버 변수)에는 사용 불가
}
```

#### 리터럴 표현법

```java
// 정수 리터럴
int decimal = 42;           // 10진수
int binary = 0b101010;      // 2진수 (0b 접두사)
int octal = 052;            // 8진수 (0 접두사)
int hex = 0x2A;             // 16진수 (0x 접두사)
long bigNum = 42L;          // long 리터럴 (L 접미사)
int readable = 1_000_000;   // 언더스코어 (Java 7+)

// 실수 리터럴
float f = 3.14F;            // float (F 접미사)
double d = 3.14;            // double (기본)
double sci = 1.5e3;         // 과학적 표기법 (1500.0)

// 텍스트 블록 (Java 15+)
String json = """
        {
            "name": "홍길동",
            "age": 30
        }
        """;
```

---

### 1.3 연산자

#### 산술 연산자

```java
int a = 10, b = 3;
System.out.println(a + b);   // 13 (덧셈)
System.out.println(a - b);   // 7  (뺄셈)
System.out.println(a * b);   // 30 (곱셈)
System.out.println(a / b);   // 3  (정수 나눗셈 - 소수점 버림)
System.out.println(a % b);   // 1  (나머지)

// 주의: 정수 나눗셈
System.out.println(10 / 3);      // 3 (정수끼리 연산)
System.out.println(10.0 / 3);    // 3.3333... (하나라도 실수면 실수 결과)
```

#### 비교 연산자

```java
int x = 5, y = 10;
System.out.println(x == y);   // false
System.out.println(x != y);   // true
System.out.println(x > y);    // false
System.out.println(x < y);    // true
System.out.println(x >= y);   // false
System.out.println(x <= y);   // true

// 참조형 비교 주의
String s1 = new String("hello");
String s2 = new String("hello");
System.out.println(s1 == s2);       // false (주소 비교)
System.out.println(s1.equals(s2));  // true  (값 비교)
```

#### 논리 연산자와 단축 평가 (Short-circuit Evaluation)

```java
boolean a = true, b = false;
System.out.println(a && b);   // false (AND)
System.out.println(a || b);   // true  (OR)
System.out.println(!a);       // false (NOT)

// 단축 평가 (Short-circuit evaluation)
// && : 왼쪽이 false이면 오른쪽을 평가하지 않음
// || : 왼쪽이 true이면 오른쪽을 평가하지 않음
String str = null;
if (str != null && str.length() > 0) {
    // str이 null이면 str.length()를 호출하지 않음 → NullPointerException 방지
    System.out.println("문자열 길이: " + str.length());
}
```

#### 비트 연산자

```java
int a = 0b1010;  // 10
int b = 0b1100;  // 12
System.out.println(a & b);   // 0b1000 = 8  (AND)
System.out.println(a | b);   // 0b1110 = 14 (OR)
System.out.println(a ^ b);   // 0b0110 = 6  (XOR)
System.out.println(~a);      // 비트 반전
System.out.println(a << 1);  // 0b10100 = 20 (왼쪽 시프트, *2 효과)
System.out.println(a >> 1);  // 0b0101 = 5   (오른쪽 시프트, /2 효과)
System.out.println(a >>> 1); // 부호 없는 오른쪽 시프트
```

#### 삼항 연산자

```java
int score = 85;
String grade = (score >= 90) ? "A" : (score >= 80) ? "B" : "C";
System.out.println("등급: " + grade);  // B
```

#### instanceof 연산자

```java
Object obj = "Hello";
if (obj instanceof String) {
    String str = (String) obj;
    System.out.println(str.toUpperCase());
}

// Java 16+ 패턴 매칭
if (obj instanceof String str) {
    System.out.println(str.toUpperCase());  // 캐스팅 불필요
}
```

#### 연산자 우선순위 표

| 우선순위 | 연산자 | 결합방향 |
|---------|--------|---------|
| 1 (높음) | `()`, `[]`, `.` | → |
| 2 | `++`, `--` (후위) | → |
| 3 | `++`, `--` (전위), `+`, `-` (부호), `~`, `!` | ← |
| 4 | `(type)` (캐스팅), `new` | ← |
| 5 | `*`, `/`, `%` | → |
| 6 | `+`, `-` | → |
| 7 | `<<`, `>>`, `>>>` | → |
| 8 | `<`, `<=`, `>`, `>=`, `instanceof` | → |
| 9 | `==`, `!=` | → |
| 10 | `&` | → |
| 11 | `^` | → |
| 12 | `\|` | → |
| 13 | `&&` | → |
| 14 | `\|\|` | → |
| 15 | `? :` | ← |
| 16 (낮음) | `=`, `+=`, `-=` 등 | ← |

---

### 1.4 제어문

#### if / else if / else

```java
int score = 85;

if (score >= 90) {
    System.out.println("A학점");
} else if (score >= 80) {
    System.out.println("B학점");
} else if (score >= 70) {
    System.out.println("C학점");
} else {
    System.out.println("F학점");
}
```

#### switch 문 (전통 방식)

```java
int day = 3;
String dayName;

switch (day) {
    case 1:
        dayName = "월요일";
        break;
    case 2:
        dayName = "화요일";
        break;
    case 3:
        dayName = "수요일";
        break;
    default:
        dayName = "기타";
        break;
}
System.out.println(dayName);  // 수요일
```

#### switch expressions (Java 14+)

```java
// 화살표(arrow) 문법 - break 불필요, fall-through 없음
int day = 3;
String dayName = switch (day) {
    case 1 -> "월요일";
    case 2 -> "화요일";
    case 3 -> "수요일";
    case 4 -> "목요일";
    case 5 -> "금요일";
    case 6, 7 -> "주말";           // 여러 값 매칭
    default -> "잘못된 입력";
};

// yield 키워드 (블록 내에서 값 반환)
String category = switch (day) {
    case 1, 2, 3, 4, 5 -> {
        System.out.println("평일입니다");
        yield "평일";
    }
    case 6, 7 -> {
        System.out.println("주말입니다");
        yield "주말";
    }
    default -> throw new IllegalArgumentException("잘못된 요일: " + day);
};
```

#### for 문

```java
// 기본 for
for (int i = 0; i < 5; i++) {
    System.out.print(i + " ");  // 0 1 2 3 4
}

// 향상된 for (enhanced for / for-each)
String[] fruits = {"사과", "바나나", "포도"};
for (String fruit : fruits) {
    System.out.println(fruit);
}

// 무한 루프
for (;;) {
    // 조건 없이 무한 반복
    break;  // 탈출 조건 필수
}
```

#### while, do-while

```java
// while
int count = 0;
while (count < 5) {
    System.out.print(count + " ");
    count++;
}
// 출력: 0 1 2 3 4

// do-while: 최소 1번은 반드시 실행
int num = 10;
do {
    System.out.println("num = " + num);
    num++;
} while (num < 5);
// num이 10이지만 한 번은 실행됨 → "num = 10" 출력
```

#### break, continue, labeled loops

```java
// break와 continue
for (int i = 0; i < 10; i++) {
    if (i == 3) continue;  // 3을 건너뛰고 다음 반복으로
    if (i == 7) break;     // 7에서 루프 종료
    System.out.print(i + " ");
}
// 출력: 0 1 2 4 5 6

// 레이블이 있는 루프 (중첩 루프 탈출)
outer:
for (int i = 0; i < 5; i++) {
    for (int j = 0; j < 5; j++) {
        if (i * j > 6) {
            System.out.println("탈출 지점: i=" + i + ", j=" + j);
            break outer;  // 바깥 루프까지 한번에 탈출
        }
    }
}
```

---

### 1.5 배열

#### 1차원 배열

```java
// 선언과 초기화
int[] arr1 = new int[5];                    // 크기만 지정 (기본값 0)
int[] arr2 = {10, 20, 30, 40, 50};          // 리터럴 초기화
int[] arr3 = new int[]{10, 20, 30};         // new와 함께 초기화

// 배열 접근
System.out.println(arr2[0]);     // 10 (첫 번째 요소)
System.out.println(arr2.length); // 5  (배열 길이)

// 배열 순회
for (int i = 0; i < arr2.length; i++) {
    System.out.print(arr2[i] + " ");
}
```

#### 2차원 배열과 가변 배열 (Jagged Array)

```java
// 2차원 배열
int[][] matrix = {
    {1, 2, 3},
    {4, 5, 6},
    {7, 8, 9}
};

// 2차원 배열 순회
for (int i = 0; i < matrix.length; i++) {
    for (int j = 0; j < matrix[i].length; j++) {
        System.out.printf("%d ", matrix[i][j]);
    }
    System.out.println();
}

// 가변 배열 (각 행의 열 수가 다름)
int[][] jagged = new int[3][];
jagged[0] = new int[]{1, 2};
jagged[1] = new int[]{3, 4, 5};
jagged[2] = new int[]{6};
```

#### Arrays 유틸리티 클래스

```java
import java.util.Arrays;

int[] arr = {5, 3, 1, 4, 2};

// 정렬
Arrays.sort(arr);
System.out.println(Arrays.toString(arr));  // [1, 2, 3, 4, 5]

// 복사
int[] copied = Arrays.copyOf(arr, 3);      // [1, 2, 3]
int[] rangeCopy = Arrays.copyOfRange(arr, 1, 4);  // [2, 3, 4]

// 채우기
int[] filled = new int[5];
Arrays.fill(filled, 7);                    // [7, 7, 7, 7, 7]

// 이진 탐색 (정렬된 배열에서만 사용)
int index = Arrays.binarySearch(arr, 3);   // 2

// 비교
int[] a = {1, 2, 3};
int[] b = {1, 2, 3};
System.out.println(Arrays.equals(a, b));   // true
```

#### 배열 vs ArrayList 비교

| 특성 | 배열 (`int[]`) | ArrayList |
|------|--------------|-----------|
| 크기 | 고정 (생성 후 변경 불가) | 동적 (자동 확장) |
| 타입 | 기본형 + 참조형 모두 가능 | 참조형만 가능 (Wrapper 필요) |
| 성능 | 빠름 (연속 메모리) | 약간 느림 (오토박싱 오버헤드) |
| 기능 | 제한적 (length만 제공) | 풍부한 메서드 (add, remove, contains 등) |
| 다차원 | 다차원 배열 지원 | 리스트의 리스트로 구현 |
| Null | 기본형은 null 불가 | null 요소 저장 가능 |

---

### 1.6 메서드

#### 메서드 선언과 호출

```java
public class MethodExample {

    // 메서드 선언
    // [접근제어자] [static] 반환타입 메서드명(매개변수) { 본문 }
    public static int add(int a, int b) {
        return a + b;
    }

    // 반환값이 없는 메서드
    public static void greet(String name) {
        System.out.println("안녕하세요, " + name + "님!");
    }

    public static void main(String[] args) {
        int result = add(3, 5);
        System.out.println("3 + 5 = " + result);  // 8

        greet("홍길동");  // 안녕하세요, 홍길동님!
    }
}
```

#### 가변인자 (Varargs)

```java
// 가변인자: 매개변수 개수를 유연하게 처리
public static int sum(int... numbers) {
    int total = 0;
    for (int n : numbers) {
        total += n;
    }
    return total;
}

// 호출
sum(1, 2);           // 3
sum(1, 2, 3, 4, 5);  // 15
sum();               // 0

// 주의: 가변인자는 매개변수 목록의 마지막에만 위치할 수 있음
public static void print(String prefix, int... values) { /* OK */ }
// public static void print(int... values, String suffix) { /* 컴파일 에러 */ }
```

#### 메서드 오버로딩 (Overloading)

같은 이름의 메서드를 매개변수의 타입이나 개수를 달리하여 여러 개 정의하는 것이다.

```java
public class Calculator {
    public int add(int a, int b) {
        return a + b;
    }

    public double add(double a, double b) {
        return a + b;
    }

    public int add(int a, int b, int c) {
        return a + b + c;
    }

    public String add(String a, String b) {
        return a + b;  // 문자열 연결
    }
}
```

#### 재귀 (Recursion)

```java
// 팩토리얼: n! = n * (n-1)!
public static long factorial(int n) {
    if (n <= 1) return 1;           // 기저 조건 (base case)
    return n * factorial(n - 1);    // 재귀 호출
}

// 피보나치 수열: F(n) = F(n-1) + F(n-2)
public static long fibonacci(int n) {
    if (n <= 0) return 0;
    if (n == 1) return 1;
    return fibonacci(n - 1) + fibonacci(n - 2);
}

// 메모이제이션을 적용한 피보나치 (성능 개선)
public static long fibMemo(int n, long[] memo) {
    if (n <= 0) return 0;
    if (n == 1) return 1;
    if (memo[n] != 0) return memo[n];
    memo[n] = fibMemo(n - 1, memo) + fibMemo(n - 2, memo);
    return memo[n];
}
```

---

### 1.7 예외처리

#### 예외 계층 구조

```
                    Throwable
                   /         \
                Error       Exception
                 |            /        \
           OutOfMemory   IOException   RuntimeException
           StackOverflow  SQLException    |         |
                                   NullPointer  IndexOutOfBounds
                                   ClassCast    IllegalArgument
                                   Arithmetic   NumberFormat
```

#### Checked vs Unchecked Exception

| 구분 | Checked Exception | Unchecked Exception |
|------|------------------|-------------------|
| 상위 클래스 | Exception | RuntimeException |
| 컴파일 검사 | 반드시 처리해야 함 | 처리 강제 아님 |
| 처리 방법 | try-catch 또는 throws | 선택적 |
| 예시 | IOException, SQLException | NullPointerException, ArithmeticException |
| 발생 시점 | 예측 가능한 외부 요인 | 프로그래밍 실수 |

#### try-catch-finally

```java
public class ExceptionExample {
    public static void main(String[] args) {
        try {
            int result = 10 / 0;
            System.out.println("이 줄은 실행되지 않음");
        } catch (ArithmeticException e) {
            System.out.println("산술 오류: " + e.getMessage());
        } finally {
            System.out.println("항상 실행되는 블록");
            // 리소스 정리 등에 사용
        }
    }
}
```

#### multi-catch (Java 7+)

```java
try {
    // 여러 종류의 예외가 발생할 수 있는 코드
    String str = null;
    str.length();
} catch (NullPointerException | ArrayIndexOutOfBoundsException e) {
    // 파이프(|)로 여러 예외를 한 번에 처리
    System.out.println("예외 발생: " + e.getClass().getSimpleName());
}
```

#### try-with-resources (Java 7+)

AutoCloseable 인터페이스를 구현한 리소스를 자동으로 닫아준다.

```java
// 전통적인 방식 (Java 7 이전)
BufferedReader br = null;
try {
    br = new BufferedReader(new FileReader("data.txt"));
    String line = br.readLine();
    System.out.println(line);
} catch (IOException e) {
    e.printStackTrace();
} finally {
    if (br != null) {
        try { br.close(); } catch (IOException e) { /* ignore */ }
    }
}

// try-with-resources (Java 7+)
try (BufferedReader br = new BufferedReader(new FileReader("data.txt"))) {
    String line = br.readLine();
    System.out.println(line);
} catch (IOException e) {
    e.printStackTrace();
}
// br.close()가 자동 호출됨
```

#### 커스텀 예외 클래스

```java
// Checked 예외
public class InsufficientBalanceException extends Exception {
    private final double amount;

    public InsufficientBalanceException(double amount) {
        super("잔액이 부족합니다. 요청 금액: " + amount);
        this.amount = amount;
    }

    public double getAmount() {
        return amount;
    }
}

// Unchecked 예외
public class InvalidAgeException extends RuntimeException {
    public InvalidAgeException(int age) {
        super("유효하지 않은 나이입니다: " + age);
    }
}

// 사용 예시
public class BankAccount {
    private double balance;

    public void withdraw(double amount) throws InsufficientBalanceException {
        if (amount > balance) {
            throw new InsufficientBalanceException(amount);
        }
        balance -= amount;
    }
}
```

#### 예외 전파와 throws

```java
public class ExceptionPropagation {

    // 메서드에서 예외를 직접 처리하지 않고 호출자에게 전파
    public static void methodC() throws IOException {
        throw new IOException("파일을 찾을 수 없습니다");
    }

    public static void methodB() throws IOException {
        methodC();  // 예외를 다시 상위로 전파
    }

    public static void methodA() {
        try {
            methodB();
        } catch (IOException e) {
            // 최종적으로 여기서 처리
            System.out.println("예외 처리: " + e.getMessage());
        }
    }
}
```

---

## Part 2: 객체지향 프로그래밍 (OOP)

---

### 2.1 클래스와 객체

#### 클래스 구성요소

```java
public class Student {
    // === 필드 (멤버 변수) ===
    private String name;
    private int age;
    private static int studentCount = 0;  // 클래스 변수 (모든 인스턴스 공유)

    // === 생성자 ===
    public Student() {
        this("이름없음", 0);  // 다른 생성자 호출
    }

    public Student(String name, int age) {
        this.name = name;     // this: 현재 인스턴스를 가리킴
        this.age = age;
        studentCount++;
    }

    // === 메서드 ===
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        if (age < 0 || age > 150) {
            throw new IllegalArgumentException("유효하지 않은 나이: " + age);
        }
        this.age = age;
    }

    public static int getStudentCount() {
        return studentCount;
    }

    // === toString, equals, hashCode 오버라이드 ===
    @Override
    public String toString() {
        return "Student{name='" + name + "', age=" + age + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Student student = (Student) o;
        return age == student.age && Objects.equals(name, student.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, age);
    }
}
```

#### 접근 제어자

| 접근 제어자 | 같은 클래스 | 같은 패키지 | 하위 클래스 | 다른 패키지 |
|-----------|:----------:|:----------:|:----------:|:----------:|
| `public` | O | O | O | O |
| `protected` | O | O | O | X |
| `default` (없음) | O | O | X | X |
| `private` | O | X | X | X |

```java
public class AccessModifierExample {
    public int publicField = 1;        // 어디서든 접근 가능
    protected int protectedField = 2;  // 같은 패키지 + 하위 클래스
    int defaultField = 3;              // 같은 패키지에서만
    private int privateField = 4;      // 같은 클래스에서만
}
```

---

### 2.2 캡슐화 (Encapsulation)

#### 정보 은닉의 목적

캡슐화는 객체의 내부 구현을 숨기고, 외부에는 필요한 인터페이스만 공개하는 원칙이다.

- **데이터 보호**: 외부에서 직접 필드를 수정하지 못하게 방지
- **유효성 검증**: setter에서 입력값의 유효성을 검사
- **유연한 변경**: 내부 구현이 변경되어도 외부 인터페이스는 유지

#### Getter/Setter 패턴 (유효성 검증 포함)

```java
public class BankAccount {
    private String accountNumber;
    private double balance;
    private String owner;

    public BankAccount(String accountNumber, String owner) {
        this.accountNumber = accountNumber;
        this.owner = owner;
        this.balance = 0;
    }

    // Getter
    public double getBalance() {
        return balance;
    }

    public String getOwner() {
        return owner;
    }

    // 유효성 검증이 포함된 메서드
    public void deposit(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("입금액은 양수여야 합니다: " + amount);
        }
        this.balance += amount;
    }

    public void withdraw(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("출금액은 양수여야 합니다: " + amount);
        }
        if (amount > balance) {
            throw new IllegalStateException("잔액 부족. 현재 잔액: " + balance);
        }
        this.balance -= amount;
    }
}
```

#### 불변 객체 설계

```java
public final class Money {
    private final int amount;
    private final String currency;

    public Money(int amount, String currency) {
        this.amount = amount;
        this.currency = currency;
    }

    public int getAmount() { return amount; }
    public String getCurrency() { return currency; }

    // 새로운 객체를 반환 (원본 변경 안 됨)
    public Money add(Money other) {
        if (!this.currency.equals(other.currency)) {
            throw new IllegalArgumentException("통화가 다릅니다");
        }
        return new Money(this.amount + other.amount, this.currency);
    }

    @Override
    public String toString() {
        return amount + " " + currency;
    }
}
```

불변 객체의 조건:
1. 클래스를 `final`로 선언 (상속 불가)
2. 모든 필드를 `private final`로 선언
3. setter를 제공하지 않음
4. 상태 변경 메서드는 새 객체를 반환

---

### 2.3 상속 (Inheritance)

#### extends와 super 키워드

```java
// 상위 클래스 (부모)
public class Animal {
    protected String name;
    protected int age;

    public Animal(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public void eat() {
        System.out.println(name + "이(가) 먹이를 먹습니다.");
    }

    public void sleep() {
        System.out.println(name + "이(가) 잠을 잡니다.");
    }
}

// 하위 클래스 (자식)
public class Dog extends Animal {
    private String breed;

    public Dog(String name, int age, String breed) {
        super(name, age);     // 부모 생성자 호출 (반드시 첫 줄)
        this.breed = breed;
    }

    // 메서드 오버라이딩
    @Override
    public void eat() {
        super.eat();          // 부모 메서드 호출
        System.out.println("(사료를 먹습니다)");
    }

    // 자식 클래스만의 메서드
    public void bark() {
        System.out.println(name + "이(가) 짖습니다! 멍멍!");
    }
}
```

#### 생성자 체이닝

```java
public class Person {
    private String name;
    private int age;
    private String address;

    public Person() {
        this("미지정");                              // Person(String) 호출
    }

    public Person(String name) {
        this(name, 0);                               // Person(String, int) 호출
    }

    public Person(String name, int age) {
        this(name, age, "미지정");                    // Person(String, int, String) 호출
    }

    public Person(String name, int age, String address) {
        this.name = name;
        this.age = age;
        this.address = address;
        System.out.println("최종 생성자 호출됨");
    }
}
```

#### Object 클래스

모든 Java 클래스는 암시적으로 `java.lang.Object`를 상속한다.

```java
// 주요 메서드
public class Object {
    public String toString()          // 객체의 문자열 표현
    public boolean equals(Object obj) // 객체 동등성 비교
    public int hashCode()             // 해시코드 반환
    public final Class<?> getClass()  // 런타임 클래스 정보
    protected Object clone()          // 객체 복제
    public final void wait()          // 스레드 대기
    public final void notify()        // 스레드 깨움
}
```

---

### 2.4 다형성 (Polymorphism)

#### 업캐스팅과 다운캐스팅

```java
// 업캐스팅: 자식 → 부모 (자동, 항상 안전)
Animal animal = new Dog("바둑이", 3, "진돗개");
animal.eat();      // Dog의 eat() 실행 (동적 바인딩)
// animal.bark();  // 컴파일 에러! Animal 타입에는 bark() 없음

// 다운캐스팅: 부모 → 자식 (명시적, 위험할 수 있음)
if (animal instanceof Dog) {
    Dog dog = (Dog) animal;
    dog.bark();    // 이제 bark() 호출 가능
}
```

#### instanceof와 패턴 매칭 (Java 16+)

```java
// 전통적인 방식
public void process(Object obj) {
    if (obj instanceof String) {
        String str = (String) obj;
        System.out.println("문자열 길이: " + str.length());
    } else if (obj instanceof Integer) {
        Integer num = (Integer) obj;
        System.out.println("숫자의 2배: " + num * 2);
    }
}

// Java 16+ 패턴 매칭
public void processModern(Object obj) {
    if (obj instanceof String str) {
        System.out.println("문자열 길이: " + str.length());
    } else if (obj instanceof Integer num) {
        System.out.println("숫자의 2배: " + num * 2);
    }
}
```

#### 동적 바인딩 (런타임 다형성)

```java
public class Shape {
    public double area() {
        return 0;
    }
}

public class Circle extends Shape {
    private double radius;
    public Circle(double radius) { this.radius = radius; }

    @Override
    public double area() { return Math.PI * radius * radius; }
}

public class Rectangle extends Shape {
    private double width, height;
    public Rectangle(double w, double h) { width = w; height = h; }

    @Override
    public double area() { return width * height; }
}

// 다형성 활용
public class ShapeApp {
    public static void main(String[] args) {
        Shape[] shapes = {
            new Circle(5),
            new Rectangle(4, 6),
            new Circle(3)
        };

        // 런타임에 실제 타입의 area() 메서드가 호출됨
        for (Shape shape : shapes) {
            System.out.printf("도형: %s, 넓이: %.2f%n",
                shape.getClass().getSimpleName(), shape.area());
        }
    }
}
```

---

### 2.5 추상 클래스와 인터페이스

#### abstract class vs interface 비교

| 특성 | 추상 클래스 (abstract class) | 인터페이스 (interface) |
|------|---------------------------|---------------------|
| 인스턴스화 | 불가 | 불가 |
| 생성자 | 있음 | 없음 |
| 필드 | 모든 종류 가능 | `public static final`만 가능 |
| 메서드 | 추상/구상 메서드 모두 가능 | 추상, default, static, private |
| 상속/구현 | 단일 상속만 가능 | 다중 구현 가능 |
| 접근 제어자 | 모든 접근 제어자 사용 가능 | 메서드는 기본 `public` |
| 관계 | IS-A (A는 B이다) | CAN-DO (A는 B를 할 수 있다) |
| 사용 시점 | 공통 코드와 상태를 공유할 때 | 행동의 규약(계약)을 정의할 때 |

```java
// 추상 클래스
public abstract class Vehicle {
    protected String name;
    protected int speed;

    public Vehicle(String name) {
        this.name = name;
        this.speed = 0;
    }

    // 추상 메서드 (하위 클래스가 반드시 구현)
    public abstract void accelerate();

    // 구상 메서드 (기본 구현 제공)
    public void stop() {
        speed = 0;
        System.out.println(name + " 정지");
    }
}

// 인터페이스
public interface Flyable {
    void fly();                         // 추상 메서드

    default void land() {              // default 메서드 (Java 8+)
        System.out.println("착륙합니다");
    }

    static boolean canFly(Vehicle v) { // static 메서드 (Java 8+)
        return v instanceof Flyable;
    }

    private void logFlight() {         // private 메서드 (Java 9+)
        System.out.println("비행 로그 기록");
    }
}

public interface Swimmable {
    void swim();
}

// 단일 상속 + 다중 구현
public class FlyingCar extends Vehicle implements Flyable, Swimmable {
    public FlyingCar(String name) {
        super(name);
    }

    @Override
    public void accelerate() {
        speed += 10;
        System.out.println(name + " 가속! 현재 속도: " + speed);
    }

    @Override
    public void fly() {
        System.out.println(name + " 하늘을 날고 있습니다!");
    }

    @Override
    public void swim() {
        System.out.println(name + " 수중 주행 중!");
    }
}
```

---

### 2.6 enum (열거형)

#### 기본 사용법

```java
public enum Season {
    SPRING, SUMMER, AUTUMN, WINTER
}

// 사용
Season season = Season.SUMMER;
System.out.println(season);           // SUMMER
System.out.println(season.name());    // "SUMMER" (문자열)
System.out.println(season.ordinal()); // 1 (순서, 0부터)
```

#### 필드, 생성자, 메서드가 있는 enum

```java
public enum Planet {
    MERCURY(3.303e+23, 2.4397e6),
    VENUS(4.869e+24, 6.0518e6),
    EARTH(5.976e+24, 6.37814e6),
    MARS(6.421e+23, 3.3972e6);

    private final double mass;
    private final double radius;

    // enum 생성자는 private (생략 가능)
    Planet(double mass, double radius) {
        this.mass = mass;
        this.radius = radius;
    }

    public double getMass() { return mass; }
    public double getRadius() { return radius; }

    // G는 만유인력 상수
    private static final double G = 6.67300E-11;

    public double surfaceGravity() {
        return G * mass / (radius * radius);
    }

    public double surfaceWeight(double otherMass) {
        return otherMass * surfaceGravity();
    }
}

// 사용
double earthWeight = 75.0;
double mass = earthWeight / Planet.EARTH.surfaceGravity();
for (Planet p : Planet.values()) {
    System.out.printf("%s에서의 몸무게: %.2f%n", p, p.surfaceWeight(mass));
}
```

#### switch에서 enum 사용

```java
public enum Direction {
    NORTH, SOUTH, EAST, WEST
}

Direction dir = Direction.NORTH;

// Java 14+ switch expression
String result = switch (dir) {
    case NORTH -> "위쪽으로 이동";
    case SOUTH -> "아래쪽으로 이동";
    case EAST  -> "오른쪽으로 이동";
    case WEST  -> "왼쪽으로 이동";
};
```

---

### 2.7 record (Java 16+)

#### record 문법과 자동 생성 메서드

record는 불변 데이터를 담기 위한 간결한 클래스 선언이다. 다음 메서드가 자동 생성된다:
- 모든 필드에 대한 접근자 메서드 (getter, 단 get 접두사 없음)
- `toString()`, `equals()`, `hashCode()`
- 생성자 (canonical constructor)

```java
// record 선언 (한 줄로 완전한 불변 데이터 클래스 정의)
public record Point(int x, int y) {}

// 위 한 줄이 아래와 동등한 코드를 자동 생성
/*
public final class Point {
    private final int x;
    private final int y;

    public Point(int x, int y) { this.x = x; this.y = y; }
    public int x() { return x; }
    public int y() { return y; }
    public String toString() { ... }
    public boolean equals(Object o) { ... }
    public int hashCode() { ... }
}
*/

// 사용
Point p = new Point(10, 20);
System.out.println(p.x());       // 10 (getter에 get 접두사 없음)
System.out.println(p.y());       // 20
System.out.println(p);           // Point[x=10, y=20]
```

#### 컴팩트 생성자

```java
public record Email(String address) {
    // 컴팩트 생성자 - 유효성 검증에 사용
    public Email {
        if (address == null || !address.contains("@")) {
            throw new IllegalArgumentException("유효하지 않은 이메일: " + address);
        }
        address = address.toLowerCase();  // 정규화
    }
}
```

#### record의 제약사항

- `final` 클래스이므로 상속 불가
- 다른 클래스를 상속(extends)할 수 없음 (암시적으로 `java.lang.Record` 상속)
- 인터페이스 구현(implements)은 가능
- 모든 필드는 `final` (불변)
- 인스턴스 필드를 추가할 수 없음

#### 활용 사례 (DTO, 값 객체)

```java
// DTO로 활용
public record UserResponse(Long id, String username, String email, LocalDateTime createdAt) {}

// 메서드 추가 가능
public record Range(int start, int end) {
    public Range {
        if (start > end) {
            throw new IllegalArgumentException("start는 end보다 작아야 합니다");
        }
    }

    public int length() {
        return end - start;
    }

    public boolean contains(int value) {
        return value >= start && value <= end;
    }
}

// 인터페이스 구현
public record NamedPoint(String name, int x, int y) implements Comparable<NamedPoint> {
    @Override
    public int compareTo(NamedPoint other) {
        return this.name.compareTo(other.name);
    }
}
```

---

### 2.8 sealed class (Java 17+)

#### sealed, permits, final, non-sealed

sealed 클래스는 어떤 클래스가 자신을 상속할 수 있는지 명시적으로 제한한다.

```java
// sealed 클래스: 허용된 하위 클래스만 상속 가능
public sealed class Shape permits Circle, Rectangle, Triangle {
    public abstract double area();
}

// final: 더 이상 상속 불가
public final class Circle extends Shape {
    private final double radius;

    public Circle(double radius) { this.radius = radius; }

    @Override
    public double area() { return Math.PI * radius * radius; }
}

// final: 더 이상 상속 불가
public final class Rectangle extends Shape {
    private final double width, height;

    public Rectangle(double width, double height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public double area() { return width * height; }
}

// non-sealed: 다시 개방 (누구나 상속 가능)
public non-sealed class Triangle extends Shape {
    private final double base, height;

    public Triangle(double base, double height) {
        this.base = base;
        this.height = height;
    }

    @Override
    public double area() { return 0.5 * base * height; }
}
```

#### 패턴 매칭과 함께 사용 (Java 21+)

```java
// sealed class + switch 패턴 매칭
// 모든 하위 타입을 알고 있으므로 default 불필요 (exhaustive check)
public static String describe(Shape shape) {
    return switch (shape) {
        case Circle c    -> "반지름 " + c.getRadius() + "인 원";
        case Rectangle r -> r.getWidth() + " x " + r.getHeight() + " 직사각형";
        case Triangle t  -> "밑변 " + t.getBase() + "인 삼각형";
    };
}
```

#### 사용 사례

sealed class는 다음과 같은 상황에서 유용하다:
- **도메인 모델링**: 결제 수단(카드, 현금, 포인트) 등 유한한 종류를 표현할 때
- **상태 기계**: 유한한 상태 집합을 안전하게 정의할 때
- **API 설계**: 라이브러리 사용자가 임의로 상속하는 것을 방지할 때

```java
// 결제 수단 모델링 예시
public sealed interface Payment permits CreditCard, Cash, Points {
    double amount();
}

public record CreditCard(double amount, String cardNumber) implements Payment {}
public record Cash(double amount) implements Payment {}
public record Points(double amount, int pointsUsed) implements Payment {}

// 처리 로직 - 모든 경우를 컴파일러가 검증
public static String processPayment(Payment payment) {
    return switch (payment) {
        case CreditCard c -> "카드 결제: " + c.cardNumber();
        case Cash c       -> "현금 결제: " + c.amount() + "원";
        case Points p     -> "포인트 결제: " + p.pointsUsed() + "P 사용";
    };
}
```

---

## Part 3: 컬렉션과 함수형 프로그래밍

---

### 3.1 제네릭 (Generics)

제네릭은 클래스, 인터페이스, 메서드를 정의할 때 타입을 파라미터로 전달하여 타입 안전성을 높이는 기능이다.

#### 제네릭 클래스

```java
// 제네릭 클래스 정의
public class Box<T> {
    private T content;

    public void put(T content) {
        this.content = content;
    }

    public T get() {
        return content;
    }
}

// 사용
Box<String> stringBox = new Box<>();
stringBox.put("Hello");
String value = stringBox.get();    // 캐스팅 불필요

Box<Integer> intBox = new Box<>();
intBox.put(42);
int num = intBox.get();

// 여러 타입 파라미터
public class Pair<K, V> {
    private K key;
    private V value;

    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public K getKey() { return key; }
    public V getValue() { return value; }
}

Pair<String, Integer> pair = new Pair<>("나이", 30);
```

#### 제네릭 메서드

```java
public class GenericMethodExample {

    // 제네릭 메서드: 반환타입 앞에 <T> 선언
    public static <T> void printArray(T[] array) {
        for (T element : array) {
            System.out.print(element + " ");
        }
        System.out.println();
    }

    // Bounded type parameter
    public static <T extends Comparable<T>> T findMax(T[] array) {
        T max = array[0];
        for (T element : array) {
            if (element.compareTo(max) > 0) {
                max = element;
            }
        }
        return max;
    }

    public static void main(String[] args) {
        Integer[] nums = {3, 1, 4, 1, 5, 9};
        String[] words = {"사과", "바나나", "포도"};

        printArray(nums);    // 3 1 4 1 5 9
        printArray(words);   // 사과 바나나 포도

        System.out.println("최대값: " + findMax(nums));    // 9
        System.out.println("최대값: " + findMax(words));   // 포도
    }
}
```

#### 와일드카드 (?, extends, super)

```java
// ?(비한정 와일드카드): 모든 타입
public static void printList(List<?> list) {
    for (Object item : list) {
        System.out.print(item + " ");
    }
}

// ? extends T (상한 와일드카드): T 또는 T의 하위 타입
public static double sumOfList(List<? extends Number> list) {
    double sum = 0;
    for (Number n : list) {
        sum += n.doubleValue();
    }
    return sum;
}

// ? super T (하한 와일드카드): T 또는 T의 상위 타입
public static void addIntegers(List<? super Integer> list) {
    list.add(1);
    list.add(2);
    list.add(3);
}
```

#### PECS 원칙 (Producer-Extends, Consumer-Super)

```
+----------------------------------------------------------+
|  PECS: Producer-Extends, Consumer-Super                   |
|                                                           |
|  데이터를 꺼내는(읽는) 쪽  → ? extends T (Producer)        |
|  데이터를 넣는(쓰는) 쪽    → ? super T   (Consumer)        |
|  둘 다 하는 경우           → 와일드카드 사용하지 않음         |
+----------------------------------------------------------+
```

```java
public class PECSExample {

    // Producer: 컬렉션에서 데이터를 '꺼내서' 처리 → extends
    public static <T> void copy(
            List<? extends T> src,    // 읽기만 함 (Producer)
            List<? super T> dest      // 쓰기만 함 (Consumer)
    ) {
        for (T item : src) {
            dest.add(item);
        }
    }

    public static void main(String[] args) {
        List<Integer> intList = List.of(1, 2, 3);
        List<Number> numList = new ArrayList<>();

        copy(intList, numList);  // Integer를 Number 리스트로 복사
        System.out.println(numList); // [1, 2, 3]
    }
}
```

---

### 3.2 컬렉션 프레임워크

#### Collection 계층 구조

```
                        Iterable<E>
                            |
                       Collection<E>
                      /     |       \
                   List<E>  Set<E>  Queue<E>
                   /  |      |  \       |    \
           ArrayList  |  HashSet TreeSet  |   Deque<E>
           LinkedList |  LinkedHashSet  PriorityQueue  |
                      |                          ArrayDeque
                   Vector
                      |
                    Stack

                       Map<K,V>  (Collection을 상속하지 않음)
                      /    |     \
               HashMap  TreeMap  LinkedHashMap
                  |
              Hashtable
```

#### List: ArrayList vs LinkedList

| 특성 | ArrayList | LinkedList |
|------|-----------|------------|
| 내부 구조 | 동적 배열 | 이중 연결 리스트 |
| 인덱스 접근 (get) | O(1) | O(n) |
| 앞에 삽입/삭제 | O(n) | O(1) |
| 뒤에 삽입 | O(1) amortized | O(1) |
| 중간 삽입/삭제 | O(n) | O(n)* |
| 메모리 | 연속 공간, 적음 | 노드별 포인터, 많음 |
| 권장 용도 | 읽기 위주 | 삽입/삭제 빈번 |

> *LinkedList의 중간 삽입은 위치 탐색에 O(n)이 소요되므로 전체적으로 O(n)이다. Iterator로 이미 위치를 확보한 경우에만 삽입/삭제 자체는 O(1)이다.

```java
// ArrayList
List<String> arrayList = new ArrayList<>();
arrayList.add("Java");
arrayList.add("Python");
arrayList.add("Kotlin");
arrayList.add(1, "C++");              // 인덱스 1에 삽입
arrayList.remove("Python");           // 요소로 삭제
System.out.println(arrayList.get(0)); // "Java"
System.out.println(arrayList.size()); // 3

// 불변 리스트 생성 (Java 9+)
List<String> immutable = List.of("a", "b", "c");
// immutable.add("d");  // UnsupportedOperationException
```

#### Set: HashSet, TreeSet, LinkedHashSet

| 특성 | HashSet | TreeSet | LinkedHashSet |
|------|---------|---------|--------------|
| 순서 | 없음 | 정렬(오름차순) | 삽입 순서 유지 |
| null | 허용 (1개) | 불허 | 허용 (1개) |
| 내부 구조 | HashMap | Red-Black Tree | HashMap + LinkedList |
| add/remove/contains | O(1) | O(log n) | O(1) |
| 정렬 | 안 됨 | 자동 정렬 | 안 됨 |

```java
Set<String> hashSet = new HashSet<>();
hashSet.add("바나나");
hashSet.add("사과");
hashSet.add("포도");
hashSet.add("사과");                     // 중복 무시
System.out.println(hashSet);            // 순서 보장 안 됨

Set<String> treeSet = new TreeSet<>(hashSet);
System.out.println(treeSet);            // [바나나, 사과, 포도] (정렬됨)

Set<String> linkedSet = new LinkedHashSet<>();
linkedSet.add("C");
linkedSet.add("A");
linkedSet.add("B");
System.out.println(linkedSet);          // [C, A, B] (삽입 순서)
```

#### Map: HashMap, TreeMap, LinkedHashMap

| 특성 | HashMap | TreeMap | LinkedHashMap |
|------|---------|---------|--------------|
| 순서 | 없음 | 키 기준 정렬 | 삽입 순서 유지 |
| null 키 | 허용 (1개) | 불허 | 허용 (1개) |
| put/get/remove | O(1) | O(log n) | O(1) |
| 내부 구조 | 해시 테이블 | Red-Black Tree | 해시 + 연결 리스트 |

```java
Map<String, Integer> scores = new HashMap<>();
scores.put("홍길동", 95);
scores.put("김철수", 87);
scores.put("이영희", 92);

// 값 접근
System.out.println(scores.get("홍길동"));         // 95
System.out.println(scores.getOrDefault("박민수", 0)); // 0

// 순회 방법
for (Map.Entry<String, Integer> entry : scores.entrySet()) {
    System.out.println(entry.getKey() + ": " + entry.getValue());
}

// Java 8+ forEach
scores.forEach((name, score) -> System.out.println(name + ": " + score));

// Java 8+ 유용한 메서드
scores.putIfAbsent("박민수", 78);
scores.computeIfAbsent("최지은", name -> name.length() * 10);
scores.merge("홍길동", 5, Integer::sum);  // 기존 95 + 5 = 100

// 불변 맵 (Java 9+)
Map<String, Integer> immutable = Map.of("A", 1, "B", 2, "C", 3);
```

#### Queue: LinkedList, PriorityQueue, ArrayDeque

```java
// Queue (FIFO: First In, First Out)
Queue<String> queue = new LinkedList<>();
queue.offer("첫 번째");  // 삽입
queue.offer("두 번째");
queue.offer("세 번째");
System.out.println(queue.poll());   // "첫 번째" (꺼내기)
System.out.println(queue.peek());   // "두 번째" (확인만)

// PriorityQueue (우선순위 큐 - 최소 힙)
Queue<Integer> pq = new PriorityQueue<>();
pq.offer(30);
pq.offer(10);
pq.offer(20);
System.out.println(pq.poll());  // 10 (가장 작은 값 먼저)

// ArrayDeque (양방향 큐, 스택 대용)
Deque<String> deque = new ArrayDeque<>();
deque.push("A");   // 스택: 맨 앞에 삽입
deque.push("B");
deque.push("C");
System.out.println(deque.pop());  // "C" (LIFO)
```

#### 컬렉션별 시간복잡도 요약

| 컬렉션 | get | add (끝) | add (앞/중간) | remove | contains | 정렬 |
|--------|-----|---------|-------------|--------|---------|------|
| ArrayList | O(1) | O(1)* | O(n) | O(n) | O(n) | - |
| LinkedList | O(n) | O(1) | O(1)** | O(1)** | O(n) | - |
| HashSet | - | O(1) | - | O(1) | O(1) | - |
| TreeSet | - | O(log n) | - | O(log n) | O(log n) | 자동 |
| HashMap | O(1) | O(1) | - | O(1) | O(1) | - |
| TreeMap | O(log n) | O(log n) | - | O(log n) | O(log n) | 자동 |
| PriorityQueue | - | O(log n) | - | O(n) | O(n) | 자동 |

> *amortized, **해당 노드에 대한 참조가 있는 경우

#### Collections 유틸리티 메서드

```java
List<Integer> list = new ArrayList<>(List.of(3, 1, 4, 1, 5, 9));

Collections.sort(list);                     // 오름차순 정렬
Collections.sort(list, Comparator.reverseOrder()); // 내림차순
Collections.shuffle(list);                  // 무작위 섞기
Collections.reverse(list);                  // 뒤집기
System.out.println(Collections.max(list));   // 최대값
System.out.println(Collections.min(list));   // 최소값
System.out.println(Collections.frequency(list, 1)); // 1의 출현 횟수

// 불변 컬렉션으로 래핑
List<Integer> unmodifiable = Collections.unmodifiableList(list);
// unmodifiable.add(10);  // UnsupportedOperationException

// 동기화된 컬렉션
List<Integer> syncList = Collections.synchronizedList(new ArrayList<>());
```

---

### 3.3 Lambda 표현식

#### 함수형 인터페이스 (@FunctionalInterface)

함수형 인터페이스는 추상 메서드가 **정확히 하나**인 인터페이스이다. Lambda 표현식의 타입으로 사용된다.

```java
@FunctionalInterface
public interface Calculator {
    int calculate(int a, int b);
    // 추상 메서드가 하나만 있어야 함
}

// Lambda로 구현
Calculator add = (a, b) -> a + b;
Calculator multiply = (a, b) -> a * b;

System.out.println(add.calculate(3, 5));       // 8
System.out.println(multiply.calculate(3, 5));  // 15
```

#### Lambda 문법

```java
// 기본형
(int a, int b) -> { return a + b; }

// 타입 추론 (매개변수 타입 생략)
(a, b) -> { return a + b; }

// 단일 표현식 (return, 중괄호 생략)
(a, b) -> a + b

// 매개변수 1개 (괄호 생략)
x -> x * x

// 매개변수 없음
() -> System.out.println("Hello")

// 여러 줄
(a, b) -> {
    int result = a + b;
    System.out.println("결과: " + result);
    return result;
}
```

#### 주요 함수형 인터페이스 (java.util.function)

| 인터페이스 | 추상 메서드 | 설명 | 사용 예 |
|-----------|-----------|------|--------|
| `Predicate<T>` | `boolean test(T t)` | 조건 판별 | `x -> x > 0` |
| `Function<T,R>` | `R apply(T t)` | 변환 | `s -> s.length()` |
| `Consumer<T>` | `void accept(T t)` | 소비 (반환 없음) | `s -> System.out.println(s)` |
| `Supplier<T>` | `T get()` | 공급 (매개변수 없음) | `() -> new ArrayList<>()` |
| `UnaryOperator<T>` | `T apply(T t)` | 단항 연산 | `x -> x * 2` |
| `BinaryOperator<T>` | `T apply(T t1, T t2)` | 이항 연산 | `(a, b) -> a + b` |

```java
import java.util.function.*;

// Predicate: 조건 판별
Predicate<Integer> isPositive = x -> x > 0;
Predicate<Integer> isEven = x -> x % 2 == 0;
Predicate<Integer> isPositiveAndEven = isPositive.and(isEven);
System.out.println(isPositiveAndEven.test(4));   // true
System.out.println(isPositiveAndEven.test(-2));  // false

// Function: 변환
Function<String, Integer> strLength = String::length;
Function<Integer, String> intToStr = i -> "값: " + i;
Function<String, String> composed = strLength.andThen(intToStr);
System.out.println(composed.apply("Hello"));     // "값: 5"

// Consumer: 소비
Consumer<String> printer = System.out::println;
Consumer<String> upper = s -> System.out.println(s.toUpperCase());
printer.andThen(upper).accept("hello");
// hello
// HELLO

// Supplier: 공급
Supplier<List<String>> listFactory = ArrayList::new;
List<String> newList = listFactory.get();
```

#### 함수 합성 (compose, andThen)

```java
Function<Integer, Integer> doubleIt = x -> x * 2;
Function<Integer, Integer> addTen = x -> x + 10;

// andThen: doubleIt 먼저, 그 다음 addTen
Function<Integer, Integer> doubleThenAdd = doubleIt.andThen(addTen);
System.out.println(doubleThenAdd.apply(5));  // (5*2) + 10 = 20

// compose: addTen 먼저, 그 다음 doubleIt
Function<Integer, Integer> addThenDouble = doubleIt.compose(addTen);
System.out.println(addThenDouble.apply(5));  // (5+10) * 2 = 30
```

---

### 3.4 메서드 참조 (Method Reference)

Lambda 표현식을 더 간결하게 표현하는 방법이다.

#### 4가지 유형

| 유형 | 문법 | Lambda 대응 | 예시 |
|------|------|-----------|------|
| 정적 메서드 참조 | `클래스::정적메서드` | `x -> Class.method(x)` | `Integer::parseInt` |
| 인스턴스 메서드 참조 | `인스턴스::메서드` | `x -> obj.method(x)` | `System.out::println` |
| 임의 객체의 인스턴스 메서드 | `클래스::인스턴스메서드` | `(x, y) -> x.method(y)` | `String::compareTo` |
| 생성자 참조 | `클래스::new` | `x -> new Class(x)` | `ArrayList::new` |

```java
import java.util.List;
import java.util.stream.Collectors;

List<String> names = List.of("홍길동", "김철수", "이영희");

// 정적 메서드 참조
List<Integer> numbers = List.of("1", "2", "3");
List<Integer> parsed = numbers.stream()
    .map(Integer::parseInt)          // s -> Integer.parseInt(s)
    .collect(Collectors.toList());

// 인스턴스 메서드 참조
names.forEach(System.out::println);  // name -> System.out.println(name)

// 임의 객체의 인스턴스 메서드
List<String> sorted = names.stream()
    .sorted(String::compareTo)       // (s1, s2) -> s1.compareTo(s2)
    .collect(Collectors.toList());

// 생성자 참조
Supplier<List<String>> listFactory = ArrayList::new;  // () -> new ArrayList<>()
Function<String, StringBuilder> sbFactory = StringBuilder::new;  // s -> new StringBuilder(s)
```

---

### 3.5 Stream API

Stream은 컬렉션 데이터를 함수형 스타일로 처리하기 위한 API이다 (Java 8+).
Stream은 **일회용**이며, 원본 데이터를 변경하지 않는다.

#### Stream 생성 방법

```java
// 1. 컬렉션으로부터
List<String> list = List.of("a", "b", "c");
Stream<String> stream1 = list.stream();

// 2. Stream.of()
Stream<String> stream2 = Stream.of("x", "y", "z");

// 3. 배열로부터
int[] arr = {1, 2, 3, 4, 5};
IntStream stream3 = Arrays.stream(arr);

// 4. Stream.iterate() (무한 스트림)
Stream<Integer> stream4 = Stream.iterate(0, n -> n + 2);  // 0, 2, 4, 6, ...

// 5. Stream.generate() (무한 스트림)
Stream<Double> stream5 = Stream.generate(Math::random);

// 6. IntStream.range()
IntStream stream6 = IntStream.range(1, 11);       // 1~10
IntStream stream7 = IntStream.rangeClosed(1, 10);  // 1~10
```

#### 중간 연산 (Intermediate Operations)

중간 연산은 **지연 평가(lazy evaluation)**되며, 최종 연산이 호출될 때 실행된다.

| 메서드 | 설명 | 예시 |
|--------|------|------|
| `filter(Predicate)` | 조건에 맞는 요소만 통과 | `.filter(x -> x > 0)` |
| `map(Function)` | 요소를 변환 | `.map(String::toUpperCase)` |
| `flatMap(Function)` | 중첩 구조를 평탄화 | `.flatMap(List::stream)` |
| `sorted()` | 정렬 (Comparable 기반) | `.sorted()` |
| `sorted(Comparator)` | 비교기 기반 정렬 | `.sorted(Comparator.reverseOrder())` |
| `distinct()` | 중복 제거 | `.distinct()` |
| `peek(Consumer)` | 요소를 들여다봄 (디버깅용) | `.peek(System.out::println)` |
| `limit(long)` | 처음 n개만 취함 | `.limit(5)` |
| `skip(long)` | 처음 n개를 건너뜀 | `.skip(3)` |

#### 최종 연산 (Terminal Operations)

| 메서드 | 반환 타입 | 설명 |
|--------|---------|------|
| `collect(Collector)` | R | 결과를 컬렉션 등으로 수집 |
| `reduce(BinaryOperator)` | Optional\<T\> | 요소를 하나로 축소 |
| `forEach(Consumer)` | void | 각 요소에 대해 동작 수행 |
| `count()` | long | 요소 개수 |
| `anyMatch(Predicate)` | boolean | 하나라도 조건 만족 여부 |
| `allMatch(Predicate)` | boolean | 모두 조건 만족 여부 |
| `noneMatch(Predicate)` | boolean | 조건을 만족하는 요소가 없는지 여부 |
| `findFirst()` | Optional\<T\> | 첫 번째 요소 |
| `findAny()` | Optional\<T\> | 아무 요소 하나 |
| `min(Comparator)` | Optional\<T\> | 최소값 |
| `max(Comparator)` | Optional\<T\> | 최대값 |
| `toArray()` | Object[] | 배열로 변환 |

```java
List<Integer> numbers = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

// filter + map + collect
List<String> result = numbers.stream()
    .filter(n -> n % 2 == 0)              // 짝수만
    .map(n -> n + "번")                    // 문자열로 변환
    .collect(Collectors.toList());
System.out.println(result);  // [2번, 4번, 6번, 8번, 10번]

// reduce
int sum = numbers.stream()
    .reduce(0, Integer::sum);
System.out.println("합계: " + sum);  // 55

Optional<Integer> max = numbers.stream()
    .reduce(Integer::max);
System.out.println("최대값: " + max.orElse(0));  // 10

// flatMap
List<List<Integer>> nested = List.of(
    List.of(1, 2, 3),
    List.of(4, 5),
    List.of(6, 7, 8, 9)
);
List<Integer> flat = nested.stream()
    .flatMap(List::stream)
    .collect(Collectors.toList());
System.out.println(flat);  // [1, 2, 3, 4, 5, 6, 7, 8, 9]
```

#### Collectors

```java
List<Student> students = List.of(
    new Student("홍길동", "수학", 95),
    new Student("김철수", "영어", 87),
    new Student("이영희", "수학", 92),
    new Student("박민수", "영어", 78),
    new Student("최지은", "수학", 88)
);

// toList
List<String> names = students.stream()
    .map(Student::getName)
    .collect(Collectors.toList());

// groupingBy (과목별 그룹화)
Map<String, List<Student>> bySubject = students.stream()
    .collect(Collectors.groupingBy(Student::getSubject));

// partitioningBy (90점 이상 / 미만 분할)
Map<Boolean, List<Student>> partition = students.stream()
    .collect(Collectors.partitioningBy(s -> s.getScore() >= 90));

// joining (이름을 쉼표로 연결)
String nameList = students.stream()
    .map(Student::getName)
    .collect(Collectors.joining(", "));
System.out.println(nameList);  // 홍길동, 김철수, 이영희, 박민수, 최지은

// summarizingInt (통계)
IntSummaryStatistics stats = students.stream()
    .collect(Collectors.summarizingInt(Student::getScore));
System.out.println("평균: " + stats.getAverage());   // 88.0
System.out.println("최고: " + stats.getMax());        // 95
System.out.println("최저: " + stats.getMin());        // 78
System.out.println("합계: " + stats.getSum());        // 440
System.out.println("인원: " + stats.getCount());      // 5

// groupingBy + downstream collector (과목별 평균 점수)
Map<String, Double> avgBySubject = students.stream()
    .collect(Collectors.groupingBy(
        Student::getSubject,
        Collectors.averagingInt(Student::getScore)
    ));
System.out.println(avgBySubject);  // {수학=91.66..., 영어=82.5}
```

#### Parallel Stream

```java
List<Integer> largeList = IntStream.rangeClosed(1, 1_000_000)
    .boxed()
    .collect(Collectors.toList());

// 병렬 스트림
long count = largeList.parallelStream()
    .filter(n -> n % 2 == 0)
    .count();

// 주의사항:
// 1. 데이터가 적을 때는 오히려 오버헤드로 느려질 수 있음
// 2. 순서가 중요한 경우 결과가 달라질 수 있음 (forEachOrdered 사용)
// 3. 공유 자원(mutable state)에 접근하면 스레드 안전 문제 발생
// 4. ArrayList는 분할이 쉬워 효과적, LinkedList는 비효율적
```

#### 실전 예제: 학생 성적 처리

```java
record StudentScore(String name, String subject, int score) {}

public class StreamPractice {
    public static void main(String[] args) {
        List<StudentScore> scores = List.of(
            new StudentScore("홍길동", "국어", 85),
            new StudentScore("홍길동", "수학", 92),
            new StudentScore("홍길동", "영어", 78),
            new StudentScore("김철수", "국어", 90),
            new StudentScore("김철수", "수학", 65),
            new StudentScore("김철수", "영어", 88),
            new StudentScore("이영희", "국어", 95),
            new StudentScore("이영희", "수학", 97),
            new StudentScore("이영희", "영어", 91)
        );

        // 1. 학생별 평균 점수
        Map<String, Double> avgByStudent = scores.stream()
            .collect(Collectors.groupingBy(
                StudentScore::name,
                Collectors.averagingInt(StudentScore::score)
            ));
        System.out.println("=== 학생별 평균 ===");
        avgByStudent.forEach((name, avg) ->
            System.out.printf("  %s: %.1f점%n", name, avg));

        // 2. 과목별 최고 점수 학생
        Map<String, Optional<StudentScore>> topBySubject = scores.stream()
            .collect(Collectors.groupingBy(
                StudentScore::subject,
                Collectors.maxBy(Comparator.comparingInt(StudentScore::score))
            ));
        System.out.println("\n=== 과목별 1등 ===");
        topBySubject.forEach((subject, top) ->
            top.ifPresent(s ->
                System.out.printf("  %s: %s (%d점)%n", subject, s.name(), s.score())));

        // 3. 전체 평균 이상인 점수만 필터링
        double totalAvg = scores.stream()
            .mapToInt(StudentScore::score)
            .average()
            .orElse(0);
        System.out.printf("\n=== 전체 평균(%.1f점) 이상 ===%n", totalAvg);
        scores.stream()
            .filter(s -> s.score() >= totalAvg)
            .forEach(s -> System.out.printf("  %s - %s: %d점%n",
                s.name(), s.subject(), s.score()));
    }
}
```

---

### 3.6 Optional

Optional은 `null`이 될 수 있는 값을 감싸서 NullPointerException을 방지하기 위한 컨테이너 클래스이다 (Java 8+).

#### Optional 생성

```java
// 값이 있는 경우 (null이면 NullPointerException)
Optional<String> opt1 = Optional.of("Hello");

// 빈 Optional
Optional<String> opt2 = Optional.empty();

// null일 수 있는 값 (가장 많이 사용)
String nullable = possiblyNull();
Optional<String> opt3 = Optional.ofNullable(nullable);
```

#### 값 접근

```java
Optional<String> opt = Optional.of("Java");

// isPresent / isEmpty (Java 11+)
if (opt.isPresent()) {
    System.out.println(opt.get());
}

// ifPresent (콜백 방식 - 권장)
opt.ifPresent(value -> System.out.println("값: " + value));

// ifPresentOrElse (Java 9+)
opt.ifPresentOrElse(
    value -> System.out.println("값: " + value),
    () -> System.out.println("값이 없습니다")
);

// orElse: 값이 없으면 기본값
String result1 = opt.orElse("기본값");

// orElseGet: 값이 없으면 Supplier 호출
String result2 = opt.orElseGet(() -> computeDefault());

// orElseThrow: 값이 없으면 예외 발생
String result3 = opt.orElseThrow(
    () -> new IllegalStateException("값이 필수입니다")
);

// or (Java 9+): 값이 없으면 다른 Optional 반환
Optional<String> result4 = opt.or(() -> Optional.of("대체값"));
```

#### 변환 (map, flatMap, filter)

```java
Optional<String> name = Optional.of("홍길동");

// map: 값을 변환
Optional<Integer> nameLength = name.map(String::length);
System.out.println(nameLength.orElse(0));  // 3

// flatMap: 반환값이 Optional인 경우
Optional<String> upper = name
    .flatMap(n -> n.isEmpty() ? Optional.empty() : Optional.of(n.toUpperCase()));

// filter: 조건에 맞지 않으면 빈 Optional
Optional<String> longName = name.filter(n -> n.length() > 5);
System.out.println(longName.isPresent());  // false (길이 3)

// 체이닝 활용
String result = Optional.ofNullable(getUser())
    .map(User::getAddress)
    .map(Address::getCity)
    .map(String::toUpperCase)
    .orElse("알 수 없음");
```

#### Optional 올바른 사용법과 Anti-pattern

```java
// === 올바른 사용 ===

// 1. 메서드 반환 타입으로 사용 (핵심 용도)
public Optional<User> findById(Long id) {
    User user = userMap.get(id);
    return Optional.ofNullable(user);
}

// 2. 체이닝으로 null 안전한 처리
String city = findById(1L)
    .map(User::getAddress)
    .map(Address::getCity)
    .orElse("미등록");

// === Anti-pattern (피해야 할 사용법) ===

// 1. Optional을 필드로 사용하지 말 것 (Serializable 아님)
// private Optional<String> name;  // BAD

// 2. 매개변수로 사용하지 말 것
// public void setName(Optional<String> name) {}  // BAD

// 3. Optional.get()을 검증 없이 호출하지 말 것
// opt.get();  // NoSuchElementException 위험 → orElseThrow() 사용

// 4. orElse()에 비용이 큰 연산을 넣지 말 것
// opt.orElse(heavyComputation());  // 값이 있어도 heavyComputation() 실행됨
// opt.orElseGet(() -> heavyComputation());  // 값이 없을 때만 실행 (권장)

// 5. null 반환 대신 Optional.empty() 반환
// return null;                   // BAD
// return Optional.empty();       // GOOD

// 6. 단순 null 체크에는 사용하지 말 것
// if (Optional.ofNullable(x).isPresent()) { ... }  // BAD
// if (x != null) { ... }                            // GOOD (더 간단)
```

---

## Part 4: 실습 환경 (Docker)

---

### 4.1 Docker로 Java 실행하기

#### Dockerfile 작성법

```dockerfile
# 기본 Dockerfile (단일 파일 실행)
FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY src/ ./src/
RUN javac src/Main.java
CMD ["java", "-cp", "src", "Main"]
```

```dockerfile
# Gradle 프로젝트용 멀티스테이지 빌드
FROM gradle:8.5-jdk21 AS build
WORKDIR /app
COPY build.gradle settings.gradle ./
COPY src ./src
RUN gradle build --no-daemon

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
CMD ["java", "-jar", "app.jar"]
```

#### docker build / docker run 명령어

```bash
# 이미지 빌드
docker build -t java-edu:latest .

# 컨테이너 실행
docker run --rm java-edu:latest

# 대화형 실행 (JShell 등)
docker run -it --rm eclipse-temurin:21-jdk jshell

# 볼륨 마운트 (소스 코드 공유)
docker run --rm -v $(pwd)/src:/app/src eclipse-temurin:21-jdk \
  bash -c "javac /app/src/Main.java && java -cp /app/src Main"
```

#### docker-compose로 실행

```yaml
# docker-compose.yml
services:
  java-app:
    build:
      context: .
      dockerfile: Dockerfile
    volumes:
      - ./src:/app/src
    environment:
      - JAVA_OPTS=-Xmx512m
    command: ["java", "-cp", "src", "Main"]

  # 여러 챕터를 서비스로 분리
  chapter01:
    image: eclipse-temurin:21-jdk
    volumes:
      - ./chapter01/src:/app/src
    working_dir: /app
    command: bash -c "javac src/*.java && java -cp src Main"

  chapter02:
    image: eclipse-temurin:21-jdk
    volumes:
      - ./chapter02/src:/app/src
    working_dir: /app
    command: bash -c "javac src/*.java && java -cp src Main"

  chapter03:
    image: eclipse-temurin:21-jdk
    volumes:
      - ./chapter03/src:/app/src
    working_dir: /app
    command: bash -c "javac src/*.java && java -cp src Main"
```

```bash
# 실행
docker-compose up java-app
docker-compose up chapter01
docker-compose run --rm chapter02
```

---

### 4.2 POC 코드 실행 가이드

#### Chapter 01 (Java 기초) 실행

```bash
# 단일 파일 컴파일 및 실행
cd chapter01/src
javac PrimitiveTypeExample.java
java PrimitiveTypeExample

# Docker로 실행
docker run --rm -v $(pwd)/chapter01/src:/app \
  eclipse-temurin:21-jdk \
  bash -c "cd /app && javac *.java && java PrimitiveTypeExample"
```

#### Chapter 02 (객체지향) 실행

```bash
# 패키지 구조가 있는 경우
cd chapter02/src
javac -d ../out com/edu/**/*.java
java -cp ../out com.edu.Main

# Docker로 실행
docker run --rm -v $(pwd)/chapter02:/app \
  eclipse-temurin:21-jdk \
  bash -c "cd /app && javac -d out src/com/edu/**/*.java && java -cp out com.edu.Main"
```

#### Chapter 03 (컬렉션/함수형) 실행

```bash
# Gradle 프로젝트인 경우
cd chapter03
./gradlew run

# Docker로 실행
docker run --rm -v $(pwd)/chapter03:/app \
  gradle:8.5-jdk21 \
  bash -c "cd /app && gradle run --no-daemon"
```

---

## 부록

---

### A. Java 버전별 주요 기능 요약 표

| 버전 | 주요 기능 |
|------|----------|
| **Java 8** (2014, LTS) | Lambda, Stream API, Optional, default method, java.time API, CompletableFuture |
| **Java 9** (2017) | 모듈 시스템(Jigsaw), JShell, private interface method, of() 팩토리 메서드 |
| **Java 10** (2018) | var (지역 변수 타입 추론), 불변 컬렉션 copyOf(), Optional.orElseThrow() |
| **Java 11** (2018, LTS) | var in lambda, HTTP Client, String 메서드(isBlank, lines, strip, repeat), 파일 readString/writeString |
| **Java 12** (2019) | switch expressions (preview), Compact Number Format |
| **Java 13** (2019) | 텍스트 블록 (preview), switch expressions 개선 (yield) |
| **Java 14** (2020) | switch expressions (정식), record (preview), instanceof 패턴 매칭 (preview), NullPointerException 메시지 개선 |
| **Java 15** (2020) | sealed class (preview), 텍스트 블록 (정식), hidden class |
| **Java 16** (2021) | record (정식), instanceof 패턴 매칭 (정식), Stream.toList() |
| **Java 17** (2021, LTS) | sealed class (정식), switch 패턴 매칭 (preview), 난수 생성기 개선 |
| **Java 18** (2022) | UTF-8 기본 문자셋, Simple Web Server, javadoc 코드 스니펫 |
| **Java 19** (2022) | Virtual Thread (preview), Structured Concurrency (incubator) |
| **Java 20** (2023) | Scoped Values (incubator), Record Patterns (preview) |
| **Java 21** (2023, LTS) | Virtual Thread (정식), switch 패턴 매칭 (정식), Record Patterns (정식), 순차 컬렉션, String Templates (preview) |

---

### B. 자주 사용하는 단축키 (IntelliJ IDEA)

| 기능 | macOS | Windows/Linux |
|------|-------|---------------|
| 실행 | `Ctrl + Shift + R` | `Shift + F10` |
| 디버그 실행 | `Ctrl + Shift + D` | `Shift + F9` |
| 코드 자동완성 | `Ctrl + Space` | `Ctrl + Space` |
| 스마트 자동완성 | `Ctrl + Shift + Space` | `Ctrl + Shift + Space` |
| 빠른 수정 (Quick Fix) | `Option + Enter` | `Alt + Enter` |
| 코드 생성 | `Cmd + N` | `Alt + Insert` |
| 리팩토링 메뉴 | `Ctrl + T` | `Ctrl + Alt + Shift + T` |
| 이름 변경 | `Shift + F6` | `Shift + F6` |
| 파일 검색 | `Cmd + Shift + O` | `Ctrl + Shift + N` |
| 전체 검색 | `Shift + Shift` | `Shift + Shift` |
| 코드 포맷팅 | `Cmd + Option + L` | `Ctrl + Alt + L` |
| import 정리 | `Ctrl + Option + O` | `Ctrl + Alt + O` |
| 라인 복제 | `Cmd + D` | `Ctrl + D` |
| 라인 삭제 | `Cmd + Backspace` | `Ctrl + Y` |
| 주석 토글 | `Cmd + /` | `Ctrl + /` |
| 메서드 추출 | `Cmd + Option + M` | `Ctrl + Alt + M` |
| 변수 추출 | `Cmd + Option + V` | `Ctrl + Alt + V` |
| 선언부로 이동 | `Cmd + B` | `Ctrl + B` |
| 구현체로 이동 | `Cmd + Option + B` | `Ctrl + Alt + B` |
| 사용처 찾기 | `Option + F7` | `Alt + F7` |

---

### C. 추천 학습 리소스

#### 온라인 문서
- [Oracle Java Tutorials](https://docs.oracle.com/javase/tutorial/) - 공식 튜토리얼
- [Baeldung](https://www.baeldung.com/) - Java/Spring 심화 학습
- [Java Language Specification](https://docs.oracle.com/javase/specs/) - 언어 명세서

#### 도서
- **자바의 정석** (남궁성) - 한국어 Java 입문서의 표준
- **Effective Java 3판** (조슈아 블로크) - Java 모범 사례 78가지
- **모던 자바 인 액션** - Lambda, Stream, 함수형 프로그래밍 심화

#### 실습 사이트
- [Programmers](https://programmers.co.kr/) - 코딩 테스트 연습 (한국어)
- [LeetCode](https://leetcode.com/) - 알고리즘 문제 풀이
- [HackerRank Java Track](https://www.hackerrank.com/domains/java) - Java 특화 문제

#### 커뮤니티
- [Stack Overflow](https://stackoverflow.com/questions/tagged/java) - Q&A
- [Oracle Java Community](https://community.oracle.com/tech/developers/categories/java) - 공식 커뮤니티

---

> **문서 버전**: 1.0
> **최종 수정일**: 2026-03-17
> **대상 Java 버전**: Java 21 (LTS)
> **참고**: 본 문서는 Chapter 01(Java 기초), Chapter 02(객체지향), Chapter 03(컬렉션/함수형)을 하나로 통합한 종합 교육자료이다.
