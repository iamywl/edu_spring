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

> 소스 파일을 수정한 후에는 반드시 `./compile.sh` → `./run.sh N` 순서로 다시 실행하세요.

---

## 세션 목차

1. [변수와 데이터 타입](#세션-1-변수와-데이터-타입)
2. [연산자](#세션-2-연산자)
3. [제어문](#세션-3-제어문)
4. [배열과 메서드](#세션-4-배열과-메서드)
5. [예외처리](#세션-5-예외처리)

---

### 세션 1: 변수와 데이터 타입

**개념 학습**
- 📖 [JAVA_개념서](../docs/JAVA_개념서.md) - Chapter 2: "데이터를 담는 그릇 - 변수" 읽기 (기본형 vs 참조형, 메모리 모델, String Pool)
- 📝 [JAVA_교육자료](../docs/JAVA_교육자료.md) - Part 1.2: "변수와 데이터 타입" 읽기 (8가지 기본형, 형변환, var 키워드)

**예제 실행**
- 소스 코드: `src/main/java/com/edu/basics/VariablesAndTypes.java`
- 실행: `./run.sh 1`
- 주요 출력 확인 포인트:
  - `primitiveTypes()`: 각 기본형의 최대값이 출력됩니다. `int`와 `long`의 범위 차이를 확인하세요.
  - `typeCasting()`: `int(300) → byte`가 **44**가 되는 오버플로우 결과를 확인하세요.
  - `stringOperations()`: `str1 == str3`은 `true`인데 `str1 == str2`는 `false`인 이유를 확인하세요 (String Pool).
  - `varKeyword()`: `var`로 선언해도 타입이 추론되는 것을 확인하세요.

**실습 과제**

1. `VariablesAndTypes.java` 97번 줄의 `int bigInt = 300;`을 `int bigInt = 128;`로 바꿔보세요.
   - `(byte) 128`의 결과는? 왜 `-128`이 되는지 생각해보세요.
   - `./compile.sh` → `./run.sh 1`로 확인

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
- 실행: `./run.sh 1`
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
- 📖 [JAVA_개념서](../docs/JAVA_개념서.md) - Chapter 3: "프로그램의 흐름을 제어하다" 읽기
- 📝 [JAVA_교육자료](../docs/JAVA_교육자료.md) - Part 1.4: "제어문" 읽기 (if/else, switch, for, while, break/continue, labeled loops)

**예제 실행**
- 소스 코드: `src/main/java/com/edu/basics/ControlFlow.java`
- 실행: `./run.sh 2`
- 주요 출력 확인 포인트:
  - `ifElseDemo()`: score=85일 때 "등급: B"가 출력되는 흐름을 추적하세요.
  - `switchExpressionDemo()`: 화살표(`->`) 구문에서 break가 없어도 fall-through가 발생하지 않는 것을 확인하세요.
  - `whileLoopDemo()`: `while`은 조건이 false이면 실행되지 않지만, `do-while`은 최소 1번 실행되는 차이를 확인하세요.
  - `labeledLoopDemo()`: 레이블 없는 break는 내부 루프만 종료하고, 레이블 break는 외부 루프까지 종료하는 차이를 확인하세요.

**실습 과제**

1. `ControlFlow.java` 76번 줄의 `switchDemo()` 메서드에서 `case 3:`의 `break;` (83번 줄)를 삭제해보세요.
   - `dayNumber = 3`일 때 "수요일"이 아니라 "목요일"이 출력되는 것을 확인하세요 (fall-through).
   - `./compile.sh` → `./run.sh 2`로 확인

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
- 실행: `./run.sh 3`
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
- 실행: `./run.sh 4`
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

## 다음 단계

모든 세션의 예제 실행과 실습 과제를 완료했다면 [Chapter 02: 객체지향 프로그래밍(OOP)](../chapter02-oop/)으로 넘어가세요.
여기서 배운 메서드, 메모리 모델, 예외처리가 클래스와 상속을 이해하는 기반이 됩니다.
