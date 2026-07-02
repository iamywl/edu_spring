# Chapter 01: Java 기초 - 실습 가이드

> 이 문서는 **실습 가이드(LAB GUIDE)**입니다.
> 개념 설명은 `/docs/` 디렉토리의 문서를 참고하고, 여기서는 코드를 직접 실행하고 수정하며 배웁니다.

---

## 환경 설정 및 실행 방법

```bash
# 프로젝트 루트에서
docker compose up -d
# VS Code: F1 → "Dev Containers: Attach to Running Container" → java-edu
# 컨테이너 터미널에서:
./compile.sh
./run.sh      # 대화형 메뉴
```

> 소스 파일을 수정한 후에는 반드시 `./compile.sh` → `./run.sh <ClassName>` 순서로 다시 실행하세요.

---

## 세션 목차

1. [변수와 데이터 타입](#세션-1-변수와-데이터-타입)
2. [연산자](#세션-2-연산자)
3. [제어문](#세션-3-제어문)
4. [배열과 메서드](#세션-4-배열과-메서드)
5. [예외처리](#세션-5-예외처리)
6. [래퍼 클래스와 박싱/언박싱](#세션-6-래퍼-클래스와-박싱언박싱)
7. [날짜와 시간 API](#세션-7-날짜와-시간-api)
8. [파일 입출력 기초](#세션-8-파일-입출력-기초)
9. [[심화] 부동소수점의 비트](#세션-9-심화-부동소수점의-비트)
10. [[심화] 2의 보수와 오버플로우](#세션-10-심화-2의-보수와-오버플로우)
11. [[심화] Java는 항상 값에 의한 전달](#세션-11-심화-java는-항상-값에-의한-전달)

---

### 세션 1: 변수와 데이터 타입

**개념 학습**
- 📖 [JAVA_개념서](../docs/JAVA_교재/심화/README.md) - Chapter 2: "데이터를 담는 그릇 - 변수" 읽기 (기본형 vs 참조형, 메모리 모델, String Pool)
- 📝 [JAVA_교육자료](../docs/JAVA_교육자료.md) - Part 1.2: "변수와 데이터 타입" 읽기 (8가지 기본형, 형변환, var 키워드)

**예제 실행**
- 소스 코드: `src/main/java/com/edu/basics/VariablesAndTypes.java`
- 실행: `./run.sh VariablesAndTypes`
- 주요 출력 확인 포인트:
  - `primitiveTypes()`: 각 기본형의 최대값이 출력됩니다. `int`와 `long`의 범위 차이를 확인하세요.
  - `typeCasting()`: `int(300) → byte`가 **44**가 되는 오버플로우 결과를 확인하세요.
  - `stringOperations()`: `str1 == str3`은 `true`인데 `str1 == str2`는 `false`인 이유를 확인하세요 (String Pool).
  - `varKeyword()`: `var`로 선언해도 타입이 추론되는 것을 확인하세요.

**실습 과제**

1. `VariablesAndTypes.java` 97번 줄의 `int bigInt = 300;`을 `int bigInt = 128;`로 바꿔보세요.
   - `(byte) 128`의 결과는? 왜 `-128`이 되는지 생각해보세요.
   - `./compile.sh` → `./run.sh VariablesAndTypes`로 확인

2. `VariablesAndTypes.java` 41번 줄의 `float floatVar = 3.14f;`에서 `f`를 제거해보세요.
   - 어떤 컴파일 에러가 나는지 확인하세요.
   - `./compile.sh`의 에러 메시지를 읽어보세요.

3. `stringOperations()` 메서드에서 119번 줄의 `new String("Hello")`를 `"Hello"`로 바꿔보세요.
   - `str1 == str2`의 결과가 어떻게 바뀌는지 확인하세요.

---

### 세션 2: 연산자

**개념 학습**
- 📝 [JAVA_교육자료](../docs/JAVA_교육자료.md) - Part 1.3: "연산자" 읽기 (산술, 비교, 논리, 비트, 삼항, instanceof)

**예제 실행**
- 소스 코드: `src/main/java/com/edu/basics/VariablesAndTypes.java` - `operators()` 메서드
- 실행: `./run.sh VariablesAndTypes`
- 주요 출력 확인 포인트:
  - 정수 나눗셈 `10 / 3 = 3`과 실수 나눗셈 `10.0 / 3 = 3.333...`의 차이를 확인하세요.
  - 증감 연산자: `x++`는 5를 출력하고 나서 6이 되지만, `++x`는 7로 먼저 증가한 후 7을 출력합니다.
  - 단축 평가: `str`이 `null`인데도 `NullPointerException`이 발생하지 않는 이유를 확인하세요.

**실습 과제**

1. `operators()` 메서드의 252번 줄 단축 평가 코드에서 조건 순서를 바꿔보세요:
   ```java
   // 기존: (str != null) && (str.length() > 0)
   // 변경: (str.length() > 0) && (str != null)
   ```
   - `NullPointerException`이 발생하는지 확인하세요. 왜 순서가 중요한지 이해합니다.
   - 확인 후 원래대로 되돌리세요.

2. `operators()` 메서드의 216번 줄 부근에 다음 코드를 추가해보세요:
   ```java
   System.out.println("10 / 0 = " + (10 / 0));
   ```
   - `ArithmeticException`이 발생하는 것을 확인하세요. 정수를 0으로 나누면 예외가 됩니다.

---

### 세션 3: 제어문

**개념 학습**
- 📖 [JAVA_개념서](../docs/JAVA_교재/심화/README.md) - Chapter 3: "프로그램의 흐름을 제어하다" 읽기
- 📝 [JAVA_교육자료](../docs/JAVA_교육자료.md) - Part 1.4: "제어문" 읽기 (if/else, switch, for, while, break/continue, labeled loops)

**예제 실행**
- 소스 코드: `src/main/java/com/edu/basics/ControlFlow.java`
- 실행: `./run.sh ControlFlow`
- 주요 출력 확인 포인트:
  - `ifElseDemo()`: score=85일 때 "등급: B"가 출력되는 흐름을 추적하세요.
  - `switchExpressionDemo()`: 화살표(`->`) 구문에서 break가 없어도 fall-through가 발생하지 않는 것을 확인하세요.
  - `whileLoopDemo()`: `while`은 조건이 false이면 실행되지 않지만, `do-while`은 최소 1번 실행되는 차이를 확인하세요.
  - `labeledLoopDemo()`: 레이블 없는 break는 내부 루프만 종료하고, 레이블 break는 외부 루프까지 종료하는 차이를 확인하세요.

**실습 과제**

1. `ControlFlow.java` 76번 줄의 `switchDemo()` 메서드에서 `case 3:`의 `break;` (83번 줄)를 삭제해보세요.
   - `dayNumber = 3`일 때 "수요일"이 아니라 "목요일"이 출력되는 것을 확인하세요 (fall-through).
   - `./compile.sh` → `./run.sh ControlFlow`로 확인

2. `forLoopDemo()`의 177번 줄에서 `i <= 5`를 `i <= 0`으로 바꿔보세요.
   - for 루프가 한 번도 실행되지 않는 것을 확인하세요.

3. `labeledLoopDemo()`의 331번 줄에서 `target = 5`를 `target = 99`로 바꿔보세요.
   - "찾지 못했습니다" 메시지가 출력되는지 확인하세요.

---

### 세션 4: 배열과 메서드

**개념 학습**
- 📝 [JAVA_교육자료](../docs/JAVA_교육자료.md) - Part 1.5: "배열" 읽기 (1차원, 2차원, Arrays 유틸리티)
- 📝 [JAVA_교육자료](../docs/JAVA_교육자료.md) - Part 1.6: "메서드" 읽기 (가변인자, 오버로딩, 재귀)

**예제 실행**
- 소스 코드: `src/main/java/com/edu/basics/ArraysAndMethods.java`
- 실행: `./run.sh ArraysAndMethods`
- 주요 출력 확인 포인트:
  - `oneDimensionalArrays()`: `Arrays.toString()`, `Arrays.sort()`, `Arrays.copyOf()`의 동작을 확인하세요.
  - `twoDimensionalArrays()`: 비정방 배열(jagged array)에서 각 행의 길이가 다른 것을 확인하세요. 학생 성적표에서 평균 계산 로직을 따라가 보세요.
  - `overloadingDemo()`: 같은 이름 `add`이지만 매개변수 타입에 따라 다른 메서드가 호출되는 것을 확인하세요.
  - `recursionDemo()`: 하노이의 탑 출력에서 원반 이동 순서를 따라가 보세요.

**실습 과제**

1. `ArraysAndMethods.java`의 `oneDimensionalArrays()`에서 43번 줄의 `Arrays.toString(numbers1)`을 `numbers1`로만 바꿔보세요.
   - 배열의 주소값(`[I@...`)이 출력되는 것을 확인하세요. 왜 `Arrays.toString()`이 필요한지 이해합니다.

2. `recursionDemo()`의 255번 줄에서 `factorial(5)`를 `factorial(25)`로 바꿔보세요.
   - 결과가 음수가 되는 것을 확인하세요. `long` 타입도 오버플로우가 발생할 수 있습니다.
   - (힌트: `factorial(20)`까지는 정상입니다)

3. `overloadingDemo()`에 새로운 호출을 추가해보세요:
   ```java
   System.out.println("add(3, 5.0) = " + add(3, 5.0));
   ```
   - `int`와 `double`을 섞어서 호출하면 어떤 버전의 `add`가 선택되는지 확인하세요 (자동 형변환 → double 버전).

---

### 세션 5: 예외처리

**개념 학습**
- 📝 [JAVA_교육자료](../docs/JAVA_교육자료.md) - Part 1.7: "예외처리" 읽기 (예외 계층, Checked vs Unchecked, try-with-resources)

**예제 실행**
- 소스 코드: `src/main/java/com/edu/basics/ExceptionBasics.java`
- 실행: `./run.sh ExceptionBasics`
- 주요 출력 확인 포인트:
  - `tryCatchFinallyDemo()`: `10 / 0`에서 예외가 발생해도 finally 블록이 실행되는 것을 확인하세요. 예외가 없는 경우에도 finally가 실행됩니다.
  - `multipleCatchDemo()`: 인덱스 0("100")은 성공, 인덱스 1("abc")은 NumberFormatException, 인덱스 2(null)는 NullPointerException, 인덱스 3은 ArrayIndexOutOfBoundsException -- 각각 다른 catch 블록이 실행됩니다.
  - `customExceptionDemo()`: 3000원 출금은 성공하지만 50000원 출금은 `InsufficientBalanceException`이 발생합니다. 음수 금액은 `IllegalArgumentException`(Unchecked)이 발생합니다.
  - `tryWithResourcesDemo()`: 리소스가 **선언의 역순**으로 닫히는 것을 확인하세요 (리소스2 → 리소스1).

**실습 과제**

1. `ExceptionBasics.java`의 `multipleCatchDemo()` 78번 줄에서 `ArrayIndexOutOfBoundsException`을 `Exception`으로 바꿔보세요.
   - 컴파일 에러가 발생합니다. `Exception`은 더 넓은 타입이므로 아래의 구체적인 catch보다 위에 올 수 없습니다.
   - `./compile.sh`의 에러 메시지를 읽어보세요.

2. `customExceptionDemo()`의 127번 줄에서 출금 금액을 `50000` 대신 `7000`으로 바꿔보세요.
   - 현재 잔액(10000 - 3000 = 7000)과 정확히 같은 금액을 출금하면 성공하는지 확인하세요.
   - 그다음 `7001`로 바꾸면 어떻게 되는지도 확인하세요.

3. `tryWithResourcesDemo()`의 `MyResource` 클래스(329번 줄)에서 `doWork()` 메서드가 예외를 던지도록 수정해보세요:
   ```java
   public void doWork() {
       System.out.println("    " + name + " 작업 수행 중...");
       throw new RuntimeException(name + " 작업 중 오류 발생!");
   }
   ```
   - 예외가 발생해도 `close()`가 자동으로 호출되는지 확인하세요. 이것이 try-with-resources의 핵심입니다.

---

### 세션 6: 래퍼 클래스와 박싱/언박싱

**예제 실행**
- 소스 코드: `src/main/java/com/edu/basics/WrappingAndBoxing.java`
- 실행: `./run.sh WrappingAndBoxing`
- 주요 출력 확인 포인트:
  - 오토박싱/언박싱: `int` ↔ `Integer` 자동 변환과 `List<Integer>`에서의 활용을 확인하세요.
  - **Integer 캐시 함정**: `Integer 100 == 100`은 `true`인데 `Integer 1000 == 1000`은 `false`인 이유를 확인하세요 (-128~127 캐시). 값 비교는 항상 `equals()`를 쓰세요.
  - **null 언박싱**: `null`인 `Integer`를 `int`로 언박싱하면 `NullPointerException`이 발생하는 것을 확인하세요.
  - `parseInt`(기본형 반환) vs `valueOf`(래퍼 반환)의 차이를 확인하세요.

**실습 과제**
1. `integerCachePitfall()`의 `1000`을 `127`로 바꿔보세요. `==` 결과가 어떻게 달라지나요?
2. `nullUnboxingPitfall()`에서 언박싱 전에 null 체크를 추가해 NPE를 방어해보세요.

---

### 세션 7: 날짜와 시간 API

**예제 실행**
- 소스 코드: `src/main/java/com/edu/basics/DateTimeExample.java`
- 실행: `./run.sh DateTimeExample`
- 주요 출력 확인 포인트:
  - `LocalDate`/`LocalTime`/`LocalDateTime`의 생성과 필드 조회를 확인하세요. (Spring 엔티티의 생성일시 필드는 보통 `LocalDateTime`을 사용합니다.)
  - `plusDays`/`minusDays` 등은 불변 객체이므로 원본을 바꾸지 않고 **새 객체**를 반환하는 것을 확인하세요.
  - `isBefore`/`isAfter`로 날짜를 비교하고, `Period`(날짜 간격)와 `Duration`(시간 간격)으로 두 시점의 차이를 계산하는 것을 확인하세요.
  - `DateTimeFormatter`로 객체↔문자열을 포맷팅/파싱하는 것을 확인하세요.

**실습 과제**
1. 오늘로부터 100일 뒤가 무슨 요일인지 출력해보세요.
2. 자신의 생일과 오늘 날짜로 `Period`를 계산해 만 나이를 출력해보세요.

---

### 세션 8: 파일 입출력 기초

**예제 실행**
- 소스 코드: `src/main/java/com/edu/basics/FileIoExample.java`
- 실행: `./run.sh FileIoExample`
- 주요 출력 확인 포인트:
  - `Files.writeString`/`readString`으로 파일 전체를 한 번에 쓰고 읽는 것을 확인하세요. (임시 파일을 사용하므로 비대화형으로 실행됩니다.)
  - `Files.readAllLines`로 줄 단위 읽기, `try-with-resources`로 `BufferedReader`를 자동으로 닫는 패턴을 확인하세요.
  - `Scanner`로 콘솔 입력을 받는 개념을 확인하세요 (실제 stdin 대신 문자열을 소스로 사용).

**실습 과제**
1. CSV 형식(`상품,가격`)을 한 줄 더 추가하고 출력 결과를 확인하세요.
2. `Files.write`로 `List<String>`을 직접 파일에 쓰는 방법을 찾아 적용해보세요.

---

### 세션 9: [심화] 부동소수점의 비트

> CS 전공 심화. "컴퓨터는 왜 소수 계산을 틀리는가"를 IEEE-754 비트 수준에서 직접 봅니다.

**예제 실행**
- 소스 코드: `src/main/java/com/edu/basics/FloatingPointBits.java`
- 실행: `./run.sh FloatingPointBits`
- 주요 출력 확인 포인트:
  - `0.1 + 0.2 == 0.3`이 `false`이고 `%.17f`로 펼치면 오차가 드러나는 것을 확인하세요.
  - `Double.doubleToLongBits(0.1)`의 64비트를 부호(1)/지수(11)/가수(52)로 분해하고, 그 비트로 값을 다시 재구성하는 과정을 따라가 보세요.
  - `new BigDecimal(0.1)`로 double에 "실제로 저장된 값"(0.1000...0555...)을 확인하세요.
  - 규칙: 돈/정밀도 계산은 `BigDecimal`(문자열 생성자) + `compareTo` 비교를 사용하세요.

**실습 과제**
1. `moreSurprises()`에 `0.1 * 10 == 1.0` 같은 등식을 추가해 참/거짓을 예측하고 확인해보세요.
2. `theFix()`에서 `new BigDecimal(0.1)`(double 생성자)로 바꿔 결과가 어떻게 달라지는지 확인하세요.

---

### 세션 10: [심화] 2의 보수와 오버플로우

> CS 전공 심화. 정수가 고정 비트로 저장되기에 생기는 랩어라운드를 비트 연산으로 증명합니다.

**예제 실행**
- 소스 코드: `src/main/java/com/edu/basics/TwosComplement.java`
- 실행: `./run.sh TwosComplement`
- 주요 출력 확인 포인트:
  - `(byte)300 == 44`가 되는 이유를 "하위 8비트만 남긴다"는 비트 관점에서 확인하세요.
  - `-5`의 비트 패턴이 `(~5) + 1`(2의 보수)과 같고, `-1 == 0xFFFFFFFF`인 것을 확인하세요.
  - `Integer.MAX_VALUE + 1`이 `MIN_VALUE`로 점프하는 오버플로우 랩어라운드를 확인하세요.
  - `Math.abs(Integer.MIN_VALUE)`가 여전히 음수인 역설(음수가 1개 더 많음)을 이해하세요.

**실습 과제**
1. `(byte)255`, `(byte)256`, `(byte)-129`의 결과를 예측하고 확인해보세요.
2. `Math.multiplyExact(Integer.MAX_VALUE, 2)`를 호출해 조용한 오버플로우 대신 예외로 감지되는 것을 확인하세요.

---

### 세션 11: [심화] Java는 항상 값에 의한 전달

> CS 전공 심화. "Java는 pass-by-reference다"라는 흔한 오해를 실행으로 반증합니다.

**예제 실행**
- 소스 코드: `src/main/java/com/edu/basics/PassByValue.java`
- 실행: `./run.sh PassByValue`
- 주요 출력 확인 포인트:
  - 매개변수(참조)를 재대입해도 호출자의 참조는 그대로인 것(복사본만 바뀜)을 확인하세요.
  - 반대로 참조가 가리키는 객체를 `append`로 직접 수정하면 호출자에게 반영되는 것을 확인하세요.
  - 메서드 안에서 두 참조를 `swap`해도 교환되지 않는 것 → Java가 pass-by-reference가 아님을 확인하세요.

**실습 과제**
1. `swap`이 성공하려면 무엇을 넘겨야 할지 고민해보세요(예: 배열이나 홀더 객체를 넘겨 내부 원소를 교환).
2. `int[]` 배열을 메서드에 넘겨 원소를 수정하면 호출자에게 보이는 이유를 세션 3의 결과와 연결해 설명해보세요.

---

## 다음 단계

모든 세션의 예제 실행과 실습 과제를 완료했다면 [Chapter 02: 객체지향 프로그래밍(OOP)](../chapter02-oop/)으로 넘어가세요.
여기서 배운 메서드, 메모리 모델, 예외처리가 클래스와 상속을 이해하는 기반이 됩니다.
