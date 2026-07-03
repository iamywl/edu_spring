# Chapter 12. java.base 모듈

> **🐳 실습 환경 — 이 장은 `java-sandbox` 컨테이너에서 실습한다**
> ```bash
> cd java && docker compose up -d              # 컨테이너 켜기 (이미 떠 있으면 생략)
> docker exec -it java-sandbox ./run.sh ApiDocument   # 예제 실행
> ```
> 처음이라면 [java/README.md](../../README.md)의 "빠른 시작"을 먼저 보라.

지금까지 우리는 직접 클래스를 만들고, 상속하고, 예외를 처리하는 법을 배웠다. 그런데 문자열을 대문자로 바꾸는 코드, 오늘 날짜를 구하는 코드, 제곱근을 계산하는 코드를 매번 처음부터 짜야 한다면 어떨까? 다행히 그럴 필요가 없다. 자바는 우리가 자주 쓸 만한 기능을 **표준 API(Application Programming Interface)** 라는 이름으로 미리 만들어 제공한다.

그중에서도 이 장의 주인공은 **`java.base` 모듈**이다. `java.base`는 자바 표준 라이브러리의 "심장"이다. `Object`, `String`, `System`, `Math`, 컬렉션, 날짜/시간, 정규표현식, 리플렉션 — 우리가 매일 손대는 거의 모든 핵심 클래스가 이 안에 들어 있다. 그리고 이 모듈은 우리가 아무 선언도 하지 않아도 **항상 자동으로 포함**된다.

이 장에서는 "표준 API를 어떻게 찾아 쓰는가"부터 시작해서, `java.base`가 제공하는 대표 클래스들을 하나씩 직접 실행하며 익힌다. 각 소절 끝의 `> 💻 실습` 명령을 터미널에서 실행하면 그 개념이 실제로 어떻게 동작하는지 눈으로 확인할 수 있다.

> 이 장은 특정 문법을 새로 배우는 장이 아니라, 이미 배운 문법으로 **자바가 준비해 둔 도구를 꺼내 쓰는** 장이다. 그래서 "이런 게 있다"는 것을 아는 것 자체가 절반이다. 모든 메서드를 외울 필요는 없다. API 도큐먼트에서 찾는 법만 익히면 된다.

---

## 12.1 API 도큐먼트

### 개념: API와 API 도큐먼트

**API(Application Programming Interface)** 는 자바가 미리 만들어 제공하는 "클래스와 인터페이스의 모음"이다. 우리는 이 API를 조합해서 프로그램을 만든다. 자동차를 만들 때 나사와 엔진을 직접 제련하지 않고 규격 부품을 사다 쓰듯이, 프로그램도 표준 부품(API)을 조립해 만든다.

**API 도큐먼트(API Documentation)** 는 이 표준 클래스들의 사용법을 정리한 공식 문서다.

- 위치: `https://docs.oracle.com/en/java/javase/` (버전별 온라인 문서)
- 담긴 내용: 각 클래스의 필드/생성자/메서드, 매개변수, 반환값, 발생 예외, 설명

### 표준 API를 찾아 쓰는 4단계

낯선 기능이 필요할 때, 무작정 코드를 짜기 전에 이 흐름을 따르면 된다.

```
1) 무슨 기능이 필요한지 정한다        →  "문자열을 대문자로 바꾸고 싶다"
2) 관련 클래스를 문서에서 찾는다       →  String 클래스
3) 원하는 메서드를 고른다             →  toUpperCase()
4) 매개변수 / 반환 / 예외를 확인하고 쓴다 →  매개변수 없음, String 반환
```

```java
String word = "java";
String upper = word.toUpperCase();  // 문서에서 찾은 대로 사용 → "JAVA"
```

클래스가 어느 모듈에 속하는지도 리플렉션으로 직접 확인할 수 있다. `String`은 `java.base` 모듈 소속이다.

```java
Module m = String.class.getModule();
System.out.println(m.getName());  // java.base
```

> 💻 실습: `./run.sh ApiDocument`  📁 `chapter-java-textbook/src/main/java/com/edu/javabook/ch12/ApiDocument.java`

---

## 12.2 java.base 모듈

### 개념: 모듈이란

자바 9부터 표준 API는 **모듈(module)** 이라는 큰 단위로 묶여 관리된다. 모듈은 서로 관련 있는 여러 패키지를 하나로 묶은 것이다. 패키지가 "폴더"라면, 모듈은 "여러 폴더를 묶은 상자"에 가깝다.

```
모듈 (java.base)
 ├─ 패키지 java.lang   (Object, String, System, Math, Integer ...)
 ├─ 패키지 java.util   (List, Map, Arrays, Objects ...)
 ├─ 패키지 java.io     (File, InputStream ...)
 ├─ 패키지 java.time   (LocalDate, Duration ...)
 └─ 패키지 java.text   (DecimalFormat, NumberFormat ...)
```

### java.base의 두 가지 특별함

**1) 항상 자동 포함된다.** `java.base`는 표준 모듈 중 가장 기본이 되는 모듈이며, 다른 모듈 대부분이 이 모듈에 의존한다. 그래서 `module-info.java`에 `requires java.base;`를 쓰지 않아도 언제나 포함된다.

**2) java.lang은 자동 import된다.** `java.base` 안의 여러 패키지 중 `java.lang`은 특별하다. `Object`, `String`, `System`, `Math`, `Integer` 같은 클래스는 **import 문 없이** 바로 쓸 수 있다(컴파일러가 자동으로 import). 반면 `java.util`, `java.io` 등은 필요할 때 직접 import 해야 한다.

```java
// 파일 상단에 import가 전혀 없어도 아래는 모두 동작한다 (java.lang 자동 import)
String s = "자동 import";
int n = Integer.parseInt("100");
double r = Math.sqrt(16);
```

> 💻 실습: `./run.sh JavaBaseModule`  📁 `chapter-java-textbook/src/main/java/com/edu/javabook/ch12/JavaBaseModule.java`

---

## 12.3 Object 클래스

### 개념: 모든 클래스의 조상

`Object`는 모든 클래스의 최상위 부모다. 우리가 만든 모든 클래스는 `extends`를 쓰지 않아도 자동으로 `Object`를 상속한다. 따라서 `Object`가 제공하는 메서드는 어떤 객체에서든 사용할 수 있다.

자주 재정의(override)하는 대표 메서드는 다음과 같다.

| 메서드 | 하는 일 | 기본 동작 |
| --- | --- | --- |
| `getClass()` | 객체의 실제 클래스 정보(Class 객체) 반환 | (재정의 대상 아님) |
| `equals(o)` | 두 객체가 "논리적으로 같은가" 정의 | `==` (주소 비교) |
| `hashCode()` | 객체를 대표하는 정수 | 주소 기반 정수 |
| `toString()` | 객체를 문자열로 표현 | `클래스명@16진수해시` |

### equals와 hashCode의 계약

이 둘은 반드시 함께 재정의해야 한다.

> **equals가 true인 두 객체는 hashCode도 반드시 같아야 한다.**

이 규칙을 지켜야 `HashMap`, `HashSet` 등이 올바르게 동작한다. `equals`만 재정의하고 `hashCode`를 빠뜨리면, "논리적으로 같은" 객체가 해시 자료구조에서 서로 다른 것으로 취급되어 버그가 난다.

```java
// "학번(id)이 같으면 같은 학생"으로 취급하고 싶다면
@Override public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    return id == ((Student) o).id;      // 핵심: id만 비교
}
@Override public int hashCode() {
    return Objects.hash(id);            // equals에서 쓴 id로 해시 생성
}
```

이렇게 하면 학번이 같고 이름만 다른 두 학생 `s1`, `s2`에 대해 `s1.equals(s2)`는 `true`, 두 hashCode도 동일해진다.

> 이 개념의 배경은 JAVA_개념서 『04-객체지향이란-무엇인가』(상속과 다형성)와 『05-데이터를-효율적으로-다루다-컬렉션』(equals/hashCode 계약)을 함께 보면 더 깊게 이해된다.

> 💻 실습: `./run.sh ObjectClass`  📁 `chapter-java-textbook/src/main/java/com/edu/javabook/ch12/ObjectClass.java`

---

## 12.4 System 클래스

### 개념: 실행 환경과 통하는 창구

`System` 클래스는 운영체제 및 자바 실행 환경과 관련된 기능을 모아 둔 **유틸리티 클래스**다. 모든 멤버가 `static`이므로 객체를 만들지 않고 `System.메서드()`로 바로 쓴다.

| 기능 | 설명 |
| --- | --- |
| `currentTimeMillis()` | 1970-01-01 기준 현재 시각(밀리초). 시각 측정용 |
| `nanoTime()` | 임의 기준점 기준 나노초. **경과 시간 측정**에 적합 |
| `getProperty(key)` | 자바 시스템 속성 조회 (`java.version`, `os.name` 등) |
| `getenv(key)` | 운영체제 환경 변수 조회 (`PATH` 등) |
| `arraycopy(...)` | 배열의 일부를 다른 배열로 빠르게 복사 |
| `out` / `err` | 표준 출력 / 표준 에러 출력 스트림 |

### 경과 시간은 nanoTime으로

"이 코드가 얼마나 걸렸나"를 재려면 `currentTimeMillis()`가 아니라 `nanoTime()`을 써야 한다. `currentTimeMillis()`는 벽시계 시각이라 도중에 시스템 시간이 바뀌면 음수가 나올 수도 있다. `nanoTime()`은 오직 경과 측정을 위한 단조 증가 값이다.

```java
long start = System.nanoTime();
long sum = 0;
for (int i = 1; i <= 1_000_000; i++) sum += i;
long elapsed = System.nanoTime() - start;   // 걸린 시간 (ns)
```

```java
// arraycopy(원본, 원본시작, 대상, 대상시작, 개수)
int[] src = {10, 20, 30, 40, 50};
int[] dest = new int[5];
System.arraycopy(src, 1, dest, 0, 3);   // src[1..3] → dest[0..2]
```

> 💻 실습: `./run.sh SystemClass`  📁 `chapter-java-textbook/src/main/java/com/edu/javabook/ch12/SystemClass.java`

---

## 12.5 문자열 클래스 (String / StringBuilder)

### 개념: String은 불변이다

`String`은 문자들의 나열을 다루는 클래스다. 가장 중요한 특징은 **불변(immutable)** 이라는 점이다. 한 번 만든 `String`의 내용은 절대 바뀌지 않으며, 문자열을 "바꾸는" 메서드는 원본을 그대로 두고 **새 문자열을 만들어 반환**한다.

```java
String origin = "java";
String changed = origin.toUpperCase();  // 새 문자열 "JAVA" 반환
// origin은 여전히 "java" — 변하지 않았다
```

이 점을 놓치면 `str.trim();`처럼 반환값을 버리고 원본이 바뀌길 기대하는 실수를 하게 된다.

### String 주요 메서드

| 메서드 | 설명 |
| --- | --- |
| `length()` | 길이 |
| `charAt(i)` | i번째 문자 |
| `substring(a, b)` | a부터 b 앞까지 잘라낸 문자열 |
| `indexOf("x")` | 위치 (없으면 -1) |
| `replace(a, b)` | a를 b로 바꾼 **새** 문자열 |
| `trim()` / `toUpperCase()` / `split()` | 공백 제거 / 대문자화 / 분리 |

### 자주 바꿀 땐 StringBuilder

문자열을 반복해서 이어 붙여야 한다면 `String`(불변)은 매번 새 객체를 만들어 비효율적이다. 이럴 땐 **가변(mutable)** 인 `StringBuilder`를 쓴다.

```java
StringBuilder sb = new StringBuilder();
sb.append("Hello").append(" ").append("Java");  // 연쇄 호출
sb.insert(5, "!");   // 5번 위치에 끼워넣기
sb.reverse();        // 뒤집기
String result = sb.toString();
```

> 💻 실습: `./run.sh StringClass`  📁 `chapter-java-textbook/src/main/java/com/edu/javabook/ch12/StringClass.java`

---

## 12.6 포장 클래스 (Wrapper Class)

### 개념: 기본 타입을 객체로 감싸기

기본 타입(`int`, `double`, `boolean` 등)은 객체가 아니다. 하지만 컬렉션(`List<Integer>` 등)에 담거나 객체가 필요한 곳에 쓰려면 "객체 형태"가 필요하다. 이때 기본 타입 값을 객체로 **포장(wrap)** 해 주는 클래스가 포장 클래스다.

```
int  → Integer      double  → Double
long → Long         boolean → Boolean
char → Character     ...
```

### 오토박싱 / 언박싱, 그리고 parseXxx

- **오토박싱(auto-boxing)**: 기본 타입 → 포장 객체 자동 변환 (`Integer i = 10;`)
- **언박싱(unboxing)**: 포장 객체 → 기본 타입 자동 변환 (`int n = i;`)
- **parseXxx**: 문자열을 기본 타입 숫자로 변환 (`Integer.parseInt("100")`, `Double.parseDouble("3.14")`)

### ⚠️ Integer 캐시 == 함정

`-128 ~ 127` 사이의 `Integer`는 미리 만들어 둔 **캐시 객체**를 재사용한다. 그래서 이 범위는 `==`가 우연히 `true`가 되지만, 범위를 벗어나면 `false`다.

```java
Integer a1 = 127, a2 = 127;
Integer b1 = 128, b2 = 128;
System.out.println(a1 == a2);        // true  (캐시 재사용 → 우연)
System.out.println(b1 == b2);        // false (캐시 초과 → 서로 다른 객체!)
System.out.println(b1.equals(b2));   // true  (값 비교는 반드시 equals)
```

값이 같은지는 **반드시 `equals()`**(또는 언박싱 후 `==`)로 비교해야 한다.

> 포장 클래스와 오토박싱은 JAVA_개념서 『05-데이터를-효율적으로-다루다-컬렉션』에서 제네릭 컬렉션과 함께 다룬다.

> 💻 실습: `./run.sh WrapperClass`  📁 `chapter-java-textbook/src/main/java/com/edu/javabook/ch12/WrapperClass.java`

---

## 12.7 수학 클래스

### 개념: Math 유틸리티

`Math` 클래스는 수학 계산에 필요한 상수와 정적 메서드를 모아 둔 유틸리티 클래스다. 모든 멤버가 `static`이라 `Math.메서드()`로 바로 쓴다.

| 메서드 | 설명 |
| --- | --- |
| `abs(x)` | 절댓값 |
| `max(a,b)` / `min(a,b)` | 큰 값 / 작은 값 |
| `pow(a,b)` | a의 b제곱 |
| `sqrt(x)` | 제곱근 |
| `round(x)` | 반올림 |
| `ceil(x)` / `floor(x)` | 올림 / 내림 |
| `random()` | 0.0 이상 1.0 미만 난수 |
| `addExact(a,b)` | 오버플로 시 예외를 던지는 **안전한 덧셈** |
| `PI`, `E` | 원주율, 자연상수 (상수) |

```java
int dice = (int) (Math.random() * 6) + 1;   // 1~6 주사위
```

### addExact: 조용한 오버플로를 막는다

일반 덧셈은 오버플로가 나도 조용히 잘못된 값을 반환한다. `addExact`는 오버플로를 감지하면 `ArithmeticException`을 던져 문제를 즉시 드러낸다.

```java
Math.addExact(100, 200);                 // 300
Math.addExact(Integer.MAX_VALUE, 1);     // ArithmeticException: integer overflow
```

> 오버플로와 2의 보수의 원리는 JAVA_개념서 『02-데이터를-담는-그릇-변수』, 예외 처리는 『07-예외처리-실패에-대비하다』를 참고하라.

> 💻 실습: `./run.sh MathClass`  📁 `chapter-java-textbook/src/main/java/com/edu/javabook/ch12/MathClass.java`

---

## 12.8 날짜와 시간 클래스

### 개념: java.time (자바 8+)

자바 8부터 `java.time` 패키지가 도입되어 날짜/시간을 훨씬 안전하고 편하게 다룬다. 예전 `Date`/`Calendar`의 불편함(가변, 헷갈리는 월 인덱스 등)을 개선한 **불변** 클래스들이다.

| 클래스 | 담는 것 |
| --- | --- |
| `LocalDate` | 날짜만 (년-월-일) |
| `LocalTime` | 시간만 (시:분:초) |
| `LocalDateTime` | 날짜 + 시간 |
| `Period` | 두 **날짜** 사이 간격 (년/월/일) |
| `Duration` | 두 **시간** 사이 간격 (시/분/초) |
| `DateTimeFormatter` | 날짜/시간 ↔ 문자열 형식 변환 |

### 불변이라 계산이 안전하다

`plusDays`, `minusMonths` 같은 메서드는 원본을 바꾸지 않고 **새 객체를 반환**한다. `String`과 같은 불변 철학이다.

```java
LocalDate date = LocalDate.of(2026, 6, 30);
LocalDate nextWeek = date.plusDays(7);      // 새 객체, date는 그대로

Period p = Period.between(LocalDate.of(2026,1,1), date);   // 5개월 29일
Duration d = Duration.between(LocalTime.of(9,0), LocalTime.of(17,30)); // 510분

DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH:mm");
String text = LocalDateTime.of(2026,6,30,14,30).format(fmt);
LocalDate parsed = LocalDate.parse("2026-12-25");   // 문자열 → 날짜
```

> 💻 실습: `./run.sh DateTimeClass`  📁 `chapter-java-textbook/src/main/java/com/edu/javabook/ch12/DateTimeClass.java`

---

## 12.9 형식 클래스

### 개념: 숫자와 값을 "보기 좋게"

숫자나 문자열을 보기 좋은 형식으로 출력하는 세 가지 도구를 다룬다.

**1) 서식 문자열 (printf / String.format)**

`%d`(정수), `%f`(실수), `%s`(문자열), `%x`(16진수) 등 **변환 문자**로 값을 끼워 넣는다. 폭과 정밀도도 지정할 수 있다.

```java
System.out.printf("[%5d]%n", 42);       // [   42]  폭 5칸 오른쪽 정렬
System.out.printf("[%.2f]%n", 3.14159); // [3.14]   소수 2자리
System.out.printf("[%,d]%n", 1234567);  // [1,234,567]  천 단위 콤마
System.out.printf("[%-10s]%n", "hi");   // [hi        ]  왼쪽 정렬 10칸
```

**2) DecimalFormat** — 패턴으로 숫자를 문자열화한다. `0`은 자리 채움, `#`은 필요할 때만 표시.

```java
new DecimalFormat("#,###").format(1234567);   // "1,234,567"
new DecimalFormat("000").format(7);           // "007"  (0은 자리 채움)
```

**3) NumberFormat** — 통화/백분율 등 **지역(Locale)에 맞는** 형식을 제공한다.

```java
NumberFormat.getCurrencyInstance(Locale.KOREA).format(50000); // "₩50,000"
NumberFormat.getPercentInstance().format(0.875);              // "88%"
```

> 💻 실습: `./run.sh FormatClass`  📁 `chapter-java-textbook/src/main/java/com/edu/javabook/ch12/FormatClass.java`

---

## 12.10 정규 표현식 클래스

### 개념: 문자열 패턴 규칙

**정규 표현식(Regular Expression)** 은 "문자열의 패턴"을 표현하는 규칙 문자열이다. 이메일, 전화번호처럼 일정한 형식을 검사하거나 원하는 부분을 찾을 때 쓴다.

| 기호 | 의미 | 기호 | 의미 |
| --- | --- | --- | --- |
| `\d` | 숫자 | `*` | 0회 이상 |
| `\w` | 단어 문자 | `+` | 1회 이상 |
| `.` | 임의 문자 | `?` | 0 또는 1회 |
| `{n}` | n회 | `[ ]` | 문자 집합 |
| `( )` | 그룹 | `^` `$` | 시작 / 끝 |

> 자바 문자열 안에서는 `\`를 `\\`로 이스케이프해야 한다. 즉 정규식 `\d`는 코드에서 `"\\d"`로 쓴다.

### Pattern과 Matcher

- `Pattern`: 정규식을 컴파일한 객체 (`Pattern.compile("...")`)
- `Matcher`: 대상 문자열에 패턴을 적용한 객체 (`pattern.matcher(text)`)

| 동작 | 설명 |
| --- | --- |
| `matches()` | 문자열 **전체**가 패턴과 일치하는가 |
| `find()` | 일치하는 부분을 찾아가며 탐색 |
| `group()` | 찾은 부분(또는 그룹)을 꺼냄 |
| `replaceAll()` | 맞는 부분을 모두 치환 |
| `split()` | 패턴 기준으로 분리 |

```java
// 이메일 전체 일치 검사
Pattern.matches("\\w+@\\w+\\.\\w+", "user@test.com");   // true

// 전화번호를 찾아가며 그룹으로 추출
Pattern phone = Pattern.compile("(\\d{2,3})-(\\d{3,4})-(\\d{4})");
Matcher m = phone.matcher("연락처 010-1234-5678");
while (m.find()) {
    System.out.println(m.group() + " / 국번=" + m.group(1));
}
```

> 💻 실습: `./run.sh RegexClass`  📁 `chapter-java-textbook/src/main/java/com/edu/javabook/ch12/RegexClass.java`

---

## 12.11 리플렉션 (Reflection)

### 개념: 실행 중에 클래스를 들여다보기

**리플렉션(Reflection)** 은 실행 중(runtime)에 클래스의 구조(필드/메서드/생성자)를 들여다보고, 심지어 객체를 동적으로 생성하거나 메서드를 호출하는 기능이다. 컴파일 시점이 아니라 **실행 시점**에 클래스 정보를 다룬다.

이 기능이 없으면 Spring 같은 프레임워크가 존재할 수 없다. 의존성 주입, JSON 라이브러리, 테스트 도구 등이 모두 리플렉션 위에서 돌아간다.

### Class 객체 얻는 3가지 방법

```java
Class<?> c1 = Person.class;                          // (1) 클래스.class
Class<?> c2 = person.getClass();                     // (2) 객체.getClass()
Class<?> c3 = Class.forName("com.edu...Person");     // (3) 완전 이름으로 동적 로딩
// 셋 다 동일한 Class 객체를 가리킨다 (c1 == c2 == c3)
```

### 조회하고 동적으로 호출하기

```java
Class<?> c = Person.class;
c.getDeclaredFields();    // 선언된 필드 목록
c.getDeclaredMethods();   // 선언된 메서드 목록

// 동적 생성 + 동적 호출
Constructor<?> cons = c.getConstructor(String.class, int.class);
Object obj = cons.newInstance("김철수", 25);
Method greet = c.getMethod("greet");
Object result = greet.invoke(obj);   // obj.greet() 를 동적으로 호출
```

> 리플렉션은 강력하지만 컴파일러의 타입 검증을 우회하므로, 일반 코드에서는 정적 호출을 쓰고 꼭 필요한 프레임워크 수준에서만 사용한다.

> 💻 실습: `./run.sh ReflectionClass`  📁 `chapter-java-textbook/src/main/java/com/edu/javabook/ch12/ReflectionClass.java`

---

## 12.12 어노테이션 (Annotation)

### 개념: 코드에 붙이는 메타데이터

**어노테이션(Annotation)** 은 코드에 붙이는 "메타데이터(추가 정보)"다. `@`로 시작하며, 컴파일러에게 지시하거나 프레임워크가 실행 중 읽어 동작을 바꾸는 데 쓴다.

**내장(표준) 어노테이션 예:**

- `@Override`: 부모 메서드를 재정의함을 표시 (오타 방지)
- `@Deprecated`: 더 이상 쓰지 말 것을 표시
- `@SuppressWarnings`: 특정 경고를 숨김

### 커스텀 어노테이션 만들기

`@interface`로 정의한다. 실행 중에 읽으려면 `@Retention(RUNTIME)`이 필수다.

```java
@Retention(RetentionPolicy.RUNTIME)   // 실행 시점까지 유지 → 리플렉션으로 읽기 가능
@Target(ElementType.METHOD)           // 메서드에만 붙일 수 있음
@interface TestCase {
    String name();               // 필수 요소
    int order() default 0;       // 기본값이 있는 요소
}
```

`@Retention`의 세 단계: `SOURCE`(컴파일 후 사라짐), `CLASS`(클래스파일까지만), `RUNTIME`(실행까지 유지).

### 어노테이션 + 리플렉션 = 프레임워크의 원리

어노테이션은 그 자체로는 아무 일도 하지 않는다. **리플렉션으로 읽어서** 동작을 만들어야 의미가 생긴다. 아래는 JUnit 같은 테스트 러너의 축소판이다.

```java
for (Method m : Calculator.class.getDeclaredMethods()) {
    if (m.isAnnotationPresent(TestCase.class)) {        // @TestCase 붙었나?
        TestCase tc = m.getAnnotation(TestCase.class);  // 정보 획득
        Object result = m.invoke(calc, 6, 4);           // 동적 호출
        System.out.println(tc.name() + " → " + result);
    }
}
```

> 어노테이션과 리플렉션의 조합은 SPRING_개념서에서 `@Component`, `@Autowired`가 어떻게 동작하는지 이해하는 기반이 된다.

> 💻 실습: `./run.sh AnnotationClass`  📁 `chapter-java-textbook/src/main/java/com/edu/javabook/ch12/AnnotationClass.java`

---

## ⚠️ 흔한 오해와 함정

- **"String의 메서드가 원본을 바꾼다."** → 아니다. `String`은 불변이라 `str.toUpperCase()`, `str.trim()`은 모두 **새 문자열을 반환**할 뿐 원본은 그대로다. 반환값을 반드시 변수에 받아야 한다.

- **"Integer는 == 로 값을 비교하면 된다."** → -128~127은 캐시 때문에 우연히 `true`가 되지만, 범위를 벗어나면 `false`다. 포장 객체의 값 비교는 항상 `equals()`.

- **"equals만 재정의하면 충분하다."** → `HashMap`/`HashSet`은 `hashCode`로 먼저 버킷을 찾는다. `hashCode`를 함께 재정의하지 않으면 "같은 객체"를 찾지 못한다. equals와 hashCode는 **세트**다.

- **"경과 시간은 currentTimeMillis로 잰다."** → 벽시계 시각이라 시스템 시간이 조정되면 음수/오차가 난다. 경과 측정은 단조 증가하는 `nanoTime()`.

- **"a + b 오버플로는 예외가 난다."** → 일반 산술 연산은 오버플로가 나도 조용히 잘못된 값을 반환한다. 감지가 필요하면 `Math.addExact` 계열을 써야 한다.

- **"정규식 `\d`를 그대로 문자열에 쓴다."** → 자바 문자열에서 `\`는 이스케이프 문자다. 정규식 `\d`는 코드에서 `"\\d"`로 써야 한다.

- **"java.util도 자동 import된다."** → 자동 import되는 것은 `java.lang`뿐이다. `List`, `Map`, `Objects` 등은 직접 `import java.util.*;`가 필요하다.

- **"리플렉션을 평소 코드에도 쓰면 좋다."** → 리플렉션은 컴파일러의 타입 검증을 우회하고 느리다. 일반 코드는 정적 호출을, 리플렉션은 프레임워크 수준에서만 쓴다.

---

## ✍️ 직접 작성해보기

지금까지는 완성된 실습 코드를 **읽고 실행**하며 `java.base`의 표준 API를 익혔다. 이제는 직접 **키보드로 타이핑**해 볼 차례다. API는 눈으로 훑을 때와 손으로 호출해 볼 때 남는 것이 완전히 다르다. 메서드 이름을 외우는 게 목적이 아니라, "필요한 기능 → 클래스 → 메서드"를 스스로 찾아 조립하는 감각을 기르는 것이 목적이다.

**푸는 방법**

1. 각 과제의 **스펙(무엇을 만들지)**과 **힌트**를 읽는다.
2. 여러분만의 클래스를 직접 작성하고 실행해 결과가 스펙과 맞는지 확인한다. **정답을 먼저 열어 보지 말고, 최소 한 번은 스스로 작성해 실행까지** 해 보자.
3. 막히거나 다 풀었다면, 각 과제 끝의 `> 답안 비교` 명령으로 **모범 답안 코드를 실행**해 내 결과와 비교한다. 답안 클래스는 이 장의 실습 코드와 같은 위치에 있다.

> 순서를 지키자: **답 보지 말고 → 직접 작성 → 실행 → 그다음에 답안과 비교.** 답안 실행 명령은 "내가 짠 결과가 맞는지" 확인하는 채점 도구일 뿐, 먼저 열어 보는 커닝 페이퍼가 아니다. 메서드 사용법이 헷갈리면 코드를 훔쳐보는 대신 12장 본문 표와 API 도큐먼트에서 먼저 찾아보자.

### 과제 1. 문장 다듬기 (String / StringBuilder)

앞뒤에 공백이 섞인 문장 `"  hello java world  "`를 변수에 담아 두고, 아래 세 가지를 순서대로 출력하는 프로그램을 작성하라.

1. 앞뒤 공백을 제거하고 **모두 대문자로** 바꾼 문자열 → `HELLO JAVA WORLD`
2. 그 문장에서 `"java"`가 **몇 번째 위치**에서 시작하는지(0부터 세는 인덱스)
3. `StringBuilder`로 다듬은 문장을 **거꾸로 뒤집은** 문자열

- 힌트: `trim()`으로 공백을 없앤 뒤 `toUpperCase()`를 이어서 호출한다(연쇄 호출). 위치 찾기는 `indexOf("...")`이며, 없으면 `-1`을 돌려준다.
- 힌트: `String`은 불변이라 `trim()`·`toUpperCase()`는 원본을 바꾸지 않고 **새 문자열을 반환**한다. 반환값을 반드시 변수에 받아야 한다. 뒤집기는 `new StringBuilder(문자열).reverse().toString()`.

> 답안 비교: `./run.sh StringClass`  📁 `chapter-java-textbook/src/main/java/com/edu/javabook/ch12/StringClass.java`

### 과제 2. 문자열을 숫자로 (Wrapper / 파싱)

사람이 입력한 형태의 문자열 세 개 `"256"`, `"3.14159"`, `"true"`를 각각 변수에 담아 두고, 이를 **기본 타입 값으로 변환**해 다음을 출력하는 프로그램을 작성하라.

1. `"256"`을 `int`로 바꾼 뒤 **1을 더한 값** → `257`
2. `"3.14159"`를 `double`로 바꾼 값
3. `"true"`를 `boolean`으로 바꾼 값
4. `Integer` 두 개에 각각 `1000`을 넣고 `==`와 `equals()`로 비교한 결과를 함께 출력해, 왜 값 비교에는 `equals()`를 써야 하는지 눈으로 확인한다.

- 힌트: 문자열 → 숫자 변환은 `Integer.parseInt(...)`, `Double.parseDouble(...)`, `Boolean.parseBoolean(...)`이다. 변환한 값은 진짜 숫자라 `+ 1` 같은 계산이 된다(문자열 이어 붙이기와 다르다).
- 힌트: `Integer 1000`은 캐시 범위(`-128~127`)를 벗어나므로 `==`는 `false`, `equals()`는 `true`가 나온다. 이게 12.6에서 다룬 캐시 함정이다.

> 답안 비교: `./run.sh WrapperClass`  📁 `chapter-java-textbook/src/main/java/com/edu/javabook/ch12/WrapperClass.java`

### 과제 3. 간단 계산기 (Math)

반지름이 `5`인 원과, 밑변·높이가 각각 `6`, `8`인 직각삼각형이 있다고 하자. `Math` 클래스만 사용해 다음을 계산해 출력하는 프로그램을 작성하라.

1. 원의 넓이(`반지름 × 반지름 × 원주율`)
2. 직각삼각형의 **빗변 길이**(두 변을 각각 제곱해 더한 값의 제곱근)
3. `2`의 `10`제곱
4. 실수 `3.6`을 반올림한 값과 올림·내림한 값

- 힌트: 원주율 상수는 `Math.PI`, 제곱은 `Math.pow(a, b)`, 제곱근은 `Math.sqrt(x)`이다. 빗변은 `Math.sqrt(6*6 + 8*8)`로 구하면 `10.0`이 나온다(피타고라스 정리).
- 힌트: 반올림은 `Math.round`, 올림은 `Math.ceil`, 내림은 `Math.floor`다. `ceil`/`floor`는 `double`을 돌려준다는 점에 유의.
- 응용: 큰 두 수를 `Math.addExact(a, b)`로 더해 보고, `Integer.MAX_VALUE`에 `1`을 더하면 어떤 예외가 나는지 `try-catch`로 확인해 보자.

> 답안 비교: `./run.sh MathClass`  📁 `chapter-java-textbook/src/main/java/com/edu/javabook/ch12/MathClass.java`

### 과제 4. D-day 계산기 (LocalDate / Duration)

특정한 날짜와 시각들을 고정값으로 두고(재현 가능하도록 오늘 날짜 대신 정해진 값 사용), `java.time`으로 다음을 계산해 출력하는 프로그램을 작성하라.

1. `2026-06-30`을 만들고, 그날의 **요일**과 **1주일 뒤 날짜**를 출력
2. `2026-01-01`부터 `2026-06-30`까지의 간격을 `Period`로 구해 "N개월 N일" 형태로 출력
3. `09:00`부터 `17:30`까지의 간격을 `Duration`으로 구해 **총 몇 분**인지 출력
4. `2026-06-30 14:30`을 `"yyyy년 MM월 dd일 HH:mm"` 형식의 문자열로 변환해 출력

- 힌트: 날짜 생성은 `LocalDate.of(년, 월, 일)`, 날짜+시각은 `LocalDateTime.of(...)`. 요일은 `getDayOfWeek()`, 1주일 뒤는 `plusDays(7)`(불변이라 새 객체를 반환한다).
- 힌트: 날짜 간격은 `Period.between(시작, 끝)` → `getMonths()`, `getDays()`. 시간 간격은 `Duration.between(시작, 끝)` → `toMinutes()`. 형식화는 `DateTimeFormatter.ofPattern("...")`을 만들어 `dateTime.format(fmt)`.

> 답안 비교: `./run.sh DateTimeClass`  📁 `chapter-java-textbook/src/main/java/com/edu/javabook/ch12/DateTimeClass.java`

### 과제 5. 영수증 서식 맞추기 (String.format / printf)

상품명 `"아메리카노"`, 수량 `3`, 단가 `4500`이 있다고 하자. 서식 문자열을 이용해 아래처럼 **자리를 맞춘** 출력을 만드는 프로그램을 작성하라.

1. `printf`로 `[   42]`처럼 정수를 **폭 5칸 오른쪽 정렬**로 출력해 감을 잡기
2. 실수 `3.14159`를 **소수 둘째 자리**까지 출력 → `3.14`
3. 총액(`수량 × 단가` = `13500`)을 **천 단위 콤마**를 넣어 출력 → `13,500`
4. `String.format`으로 `"상품=아메리카노, 수량=3, 금액=13,500원"` 같은 한 줄 문자열을 만들어 출력

- 힌트: 변환 문자는 `%d`(정수), `%f`(실수), `%s`(문자열)이다. 폭은 `%5d`, 정밀도는 `%.2f`, 천 단위 콤마는 `%,d`로 지정한다. 줄바꿈은 `%n`.
- 힌트: `printf`는 바로 출력하고, `String.format`은 **문자열을 반환**한다(변수에 받아 두었다가 나중에 출력 가능). `%` 자체를 출력하려면 `%%`로 쓴다.
- 응용: `%-10s`로 왼쪽 정렬을 시험해 보고, `NumberFormat.getCurrencyInstance(Locale.KOREA)`로 `₩13,500`처럼 통화 형식을 만들어 비교해 보자.

> 답안 비교: `./run.sh FormatClass`  📁 `chapter-java-textbook/src/main/java/com/edu/javabook/ch12/FormatClass.java`

### 과제 6. 형식 검사기 (정규 표현식)

문자열 안에서 일정한 형식을 검사·추출하는 프로그램을 작성하라. 대상 문장은 `"연락처는 010-1234-5678 또는 02-9876-5432 입니다."`로 둔다.

1. `"user@test.com"`이 **이메일 형식**(`단어+@단어+.단어+`)에 **전체 일치**하는지, `"bad-email"`은 어떤지 각각 `true`/`false`로 출력
2. 위 문장에서 **전화번호를 모두 찾아** 각각을 출력하고, 그중 **국번(첫 그룹)** 도 함께 뽑아 출력
3. 문장의 전화번호 뒷 4자리를 `****`로 **마스킹**한 결과 출력(도전 과제, 어려우면 건너뛰어도 좋다)

- 힌트: 전체 일치는 `Pattern.matches("정규식", 문자열)` 또는 `"문자열".matches("정규식")`. 이메일 패턴은 `"\\w+@\\w+\\.\\w+"`이다. 자바 문자열 안에서는 `\d`·`\w`를 `"\\d"`·`"\\w"`처럼 백슬래시를 **두 번** 써야 한다.
- 힌트: 부분 추출은 `Pattern.compile("(\\d{2,3})-(\\d{3,4})-(\\d{4})")`로 컴파일한 뒤 `matcher(text)`를 만들고 `while (m.find())` 안에서 `m.group()`(전체), `m.group(1)`(첫 그룹)을 꺼낸다. 괄호 `( )`가 그룹을 만든다.
- 힌트: 치환은 `text.replaceAll("정규식", "대체문자열")`을 쓴다.

> 답안 비교: `./run.sh RegexClass`  📁 `chapter-java-textbook/src/main/java/com/edu/javabook/ch12/RegexClass.java`

### 과제 7. 같은 학번은 같은 학생 (Object / equals / hashCode)

"학번(id)이 같으면 같은 학생"으로 취급되는 `Student` 클래스를 직접 만들고, 아래를 확인하는 프로그램을 작성하라. 학번 `1`·이름 `"홍길동"`인 `s1`, 학번 `1`·이름 `"홍길순"`인 `s2`, 학번 `2`·이름 `"김철수"`인 `s3`를 준비한다.

1. `s1.equals(s2)`(학번 같음 → `true`)와 `s1.equals(s3)`(학번 다름 → `false`), 그리고 `s1 == s2`(주소는 다름 → `false`)를 출력
2. `s1.hashCode()`와 `s2.hashCode()`가 **같은 값**임을 출력해 계약이 지켜졌는지 눈으로 확인
3. `HashSet<Student>`에 `s1`을 넣은 뒤 `set.contains(s2)`가 `true`가 나오는지 출력(계약을 지켰다면 true여야 한다)
4. `toString()`을 재정의해 `Student{id=1, name='홍길동'}`처럼 사람이 읽기 좋게 출력

- 힌트: `equals`는 `if (this == o) return true;` → `if (o == null || getClass() != o.getClass()) return false;` → `id`만 비교의 순서로 작성한다. `hashCode`는 `Objects.hash(id)`로, **equals에서 쓴 필드와 같은 필드(id)** 로 만들어야 한다.
- 힌트: `equals`만 재정의하고 `hashCode`를 빠뜨리면 3번의 `contains`가 `false`로 나올 수 있다(12.3의 계약 위반). 두 메서드는 반드시 세트로 재정의한다.

> 답안 비교: `./run.sh ObjectClass`  📁 `chapter-java-textbook/src/main/java/com/edu/javabook/ch12/ObjectClass.java`

### 과제 8. 실행 환경 들여다보기 (System)

`System` 클래스만 사용해 실행 환경 정보를 조회하고 간단한 성능 측정을 하는 프로그램을 작성하라.

1. `getProperty`로 자바 버전(`"java.version"`)과 운영체제 이름(`"os.name"`)을 조회해 출력
2. `nanoTime()`으로 시작 시각을 재고, `1`부터 `1_000_000`까지 더하는 반복문을 돈 뒤, 다시 `nanoTime()`을 재서 **경과 시간(나노초)**을 출력
3. `arraycopy`로 배열 `{10, 20, 30, 40, 50}`의 인덱스 `1`부터 `3`개를 새 배열의 앞부분으로 복사해 결과를 출력 → `[20, 30, 40, 0, 0]`

- 힌트: 시스템 속성은 `System.getProperty("java.version")`처럼 문자열 키로 조회한다. 환경 변수는 `System.getenv("PATH")`로 조회한다(속성과 다르다).
- 힌트: 경과 시간은 `long start = System.nanoTime();` … `long elapsed = System.nanoTime() - start;`로 구한다. 벽시계 시각인 `currentTimeMillis()`가 아니라 단조 증가하는 `nanoTime()`을 써야 하는 이유를 12.4에서 확인하자.
- 힌트: `System.arraycopy(원본, 원본시작, 대상, 대상시작, 개수)` 순서다. 배열 출력은 `Arrays.toString(배열)`을 쓰면 편하다(`import java.util.Arrays;` 필요).

> 답안 비교: `./run.sh SystemClass`  📁 `chapter-java-textbook/src/main/java/com/edu/javabook/ch12/SystemClass.java`

---

## 확인문제

> 객관식·출력 예측·오류 찾기·개념 서술·빈칸 채우기·참·거짓까지 유형을 섞어 15문항을 준비했다. 먼저 스스로 답을 적어 본 뒤 `<details>`를 펼쳐 정답과 해설을 확인하자. 출력 예측 문제는 **머릿속으로 실행**한 결과를 종이에 적고 나서 답을 여는 습관을 들이면 좋다.

### 유형 A. 객관식

**1.** `java.base` 모듈에 대한 설명으로 옳지 않은 것은?

① 자바 표준 모듈 중 가장 기본이 되는 모듈이다.
② `module-info.java`에 `requires java.base;`를 반드시 명시해야 포함된다.
③ `Object`, `String`, `System` 등 핵심 클래스가 들어 있다.
④ `java.util`, `java.io`, `java.time` 패키지도 이 모듈에 속한다.

<details><summary>힌트</summary>java.base가 "항상 자동으로" 포함되는지 떠올려 보자.</details>
<details><summary>정답 및 해설</summary>정답 ②. java.base는 선언하지 않아도 항상 자동 포함된다. requires를 쓰지 않아도 된다. 참고로 java.lang은 import까지 자동이지만, java.util·java.io는 같은 java.base 소속이어도 import는 직접 해야 한다. (12.2 참고)</details>

---

**2.** `Integer`의 `==` 비교에 대한 설명으로 옳은 것은?

① 모든 값에서 `==`는 항상 `true`를 반환한다.
② `-128 ~ 127` 범위는 캐시 재사용으로 `==`가 `true`가 될 수 있다.
③ 값 비교에는 `==`가 `equals()`보다 안전하다.
④ 포장 클래스는 `==` 연산을 지원하지 않는다.

<details><summary>힌트</summary>Integer 캐시 범위와, 값 비교의 올바른 방법을 떠올려 보자.</details>
<details><summary>정답 및 해설</summary>정답 ②. -128~127은 캐시된 같은 객체를 재사용해 ==가 true가 되지만, 범위를 벗어나면 false다. 값 비교는 항상 equals()를 써야 한다. (12.6 참고)</details>

---

**3.** `equals()`를 재정의할 때 함께 재정의해야 하는 메서드와 그 이유로 옳은 것은?

① `toString()` — 출력 형식을 맞추기 위해
② `getClass()` — 타입을 확인하기 위해
③ `hashCode()` — HashMap/HashSet에서 올바르게 동작하게 하기 위해
④ `clone()` — 복제 시 값을 유지하기 위해

<details><summary>힌트</summary>"equals가 true인 두 객체는 hashCode도 같아야 한다"는 계약을 떠올려 보자.</details>
<details><summary>정답 및 해설</summary>정답 ③. equals가 true면 hashCode도 같아야 한다는 계약이 있다. 이를 지키지 않으면 해시 기반 자료구조가 "같은 객체"를 찾지 못한다. (12.3 참고)</details>

---

**4.** 커스텀 어노테이션을 리플렉션으로 **실행 중에** 읽으려면 반드시 지정해야 하는 `@Retention` 정책은?

① `RetentionPolicy.SOURCE`
② `RetentionPolicy.CLASS`
③ `RetentionPolicy.RUNTIME`
④ 지정하지 않아도 항상 읽을 수 있다

<details><summary>힌트</summary>SOURCE는 컴파일 후 사라지고, CLASS는 클래스파일까지만 남는다. 실행 중까지 남기려면?</details>
<details><summary>정답 및 해설</summary>정답 ③. RUNTIME으로 지정해야 실행 시점까지 정보가 유지되어 리플렉션(isAnnotationPresent, getAnnotation)으로 읽을 수 있다. 지정하지 않으면 기본값은 CLASS라 실행 중에는 읽을 수 없다. (12.12 참고)</details>

---

**5.** 경과 시간(코드 실행에 걸린 시간)을 측정할 때 가장 적절한 것은?

① `System.currentTimeMillis()` — 벽시계 시각이라 가장 정확하다
② `System.nanoTime()` — 단조 증가하는 값이라 경과 측정에 적합하다
③ `Math.random()`으로 근사한다
④ `LocalDateTime.now()`의 차이를 밀리초로 환산한다

<details><summary>힌트</summary>시스템 시각이 도중에 조정되면 currentTimeMillis는 음수가 나올 수도 있다.</details>
<details><summary>정답 및 해설</summary>정답 ②. nanoTime()은 임의 기준점 기준으로 단조 증가하므로 경과 시간 측정에 적합하다. currentTimeMillis()는 벽시계 시각이라 시스템 시간이 조정되면 오차·음수가 발생할 수 있다. (12.4 참고)</details>

---

### 유형 B. 출력 예측

**6.** 다음 코드의 출력 결과는? (String 불변성)

```java
String origin = "java";
origin.toUpperCase();
System.out.println(origin);
```

① `JAVA`  ② `java`  ③ 컴파일 오류  ④ 빈 문자열

<details><summary>힌트</summary>String은 불변(immutable)이다. toUpperCase()의 반환값을 어디에도 받지 않았다.</details>
<details><summary>정답 및 해설</summary>정답 ②. String은 불변이므로 toUpperCase()는 새 문자열("JAVA")을 반환할 뿐 origin은 바뀌지 않는다. 반환값을 버렸으므로 origin은 여전히 "java". (12.5 참고)</details>

---

**7.** 다음 코드의 세 줄 출력 결과를 순서대로 예측하라. (Integer 캐시)

```java
Integer a1 = 127, a2 = 127;
Integer b1 = 128, b2 = 128;
System.out.println(a1 == a2);
System.out.println(b1 == b2);
System.out.println(b1.equals(b2));
```

<details><summary>힌트</summary>-128~127은 캐시 객체를 재사용한다. 128은 캐시 범위 밖이다. equals는 항상 값을 비교한다.</details>
<details><summary>정답 및 해설</summary>정답: `true` / `false` / `true`. 127은 캐시된 같은 객체라 ==가 true, 128은 범위를 벗어나 서로 다른 객체이므로 ==가 false, 그러나 equals는 값을 비교하므로 true다. 값 비교는 항상 equals를 써야 한다는 근거가 여기에 있다. (12.6 참고)</details>

---

**8.** 다음 `StringBuilder` 코드의 최종 출력은? (가변 문자열)

```java
StringBuilder sb = new StringBuilder();
sb.append("Hello");
sb.insert(5, "!");
sb.reverse();
System.out.println(sb);
```

<details><summary>힌트</summary>append 후 "Hello"(길이 5), insert(5,"!")는 인덱스 5(맨 끝)에 "!"를 끼운다. 그다음 전체를 뒤집는다.</details>
<details><summary>정답 및 해설</summary>정답: `!olleH`. `append("Hello")`로 "Hello"가 되고, `insert(5, "!")`는 인덱스 5(문자열 맨 끝)에 "!"를 넣어 "Hello!"가 된다. `reverse()`로 뒤집으면 "!olleH". StringBuilder는 가변이라 원본 자체가 바뀐다(String과 다르다). (12.5 참고)</details>

---

**9.** 다음 정규식 코드의 출력은? (그룹 추출)

```java
Pattern phone = Pattern.compile("(\\d{2,3})-(\\d{3,4})-(\\d{4})");
Matcher m = phone.matcher("전화 02-9876-5432 끝");
if (m.find()) {
    System.out.println(m.group() + " / " + m.group(1) + " / " + m.group(2));
}
```

<details><summary>힌트</summary>group()은 전체 매칭, group(1)은 첫 번째 괄호 그룹, group(2)는 두 번째 괄호 그룹이다. "02"는 \d{2,3}에, "9876"은 \d{3,4}에 걸린다.</details>
<details><summary>정답 및 해설</summary>정답: `02-9876-5432 / 02 / 9876`. group()은 매칭된 전체 문자열, group(1)은 첫 그룹 `(\d{2,3})` → "02", group(2)는 둘째 그룹 `(\d{3,4})` → "9876". 그룹 번호는 여는 괄호 순서대로 1부터 매겨진다(group(0)은 전체와 같다). (12.10 참고)</details>

---

**10.** 다음 날짜 연산 코드의 출력은? 여기에는 초심자가 자주 빠지는 함정이 있다. (Period)

```java
LocalDate start = LocalDate.of(2026, 1, 1);
LocalDate end   = LocalDate.of(2026, 6, 30);
Period p = Period.between(start, end);
System.out.println(p.getMonths() + "개월 " + p.getDays() + "일");
System.out.println("총 일수처럼 보이는 값: " + p.getDays());
```

<details><summary>힌트</summary>Period는 "년/월/일" 단위로 나눠 저장한다. getDays()는 "총 일수"가 아니라 "월을 뺀 나머지 일수"다. 총 일수는 ChronoUnit.DAYS.between(...)으로 구한다.</details>
<details><summary>정답 및 해설</summary>정답: 첫 줄 `5개월 29일`, 둘째 줄 `29`. 함정은 `getDays()`가 **총 일수가 아니라 개월을 뺀 나머지 일수(29)**라는 점이다. 1월 1일부터 6월 30일까지는 5개월 29일이며, 실제 총 일수(180일)를 구하려면 `ChronoUnit.DAYS.between(start, end)`를 써야 한다. Period의 get 메서드를 총량으로 오해하면 안 된다. (12.8 참고)</details>

---

**11.** 다음 형식화 코드의 출력을 예측하라. (printf / NumberFormat)

```java
System.out.printf("[%,d]%n", 13500);
System.out.printf("[%.2f]%n", 3.14159);
System.out.println(NumberFormat.getPercentInstance().format(0.875));
```

<details><summary>힌트</summary>%,d는 천 단위 콤마, %.2f는 소수 둘째 자리까지. 백분율은 0.875를 87.5%로 만든 뒤 반올림해 소수 없이 표시한다.</details>
<details><summary>정답 및 해설</summary>정답: `[13,500]` / `[3.14]` / `88%`. `%,d`는 천 단위 콤마를 넣어 13,500, `%.2f`는 반올림해 3.14. `getPercentInstance()`는 0.875를 87.5%로 환산한 뒤 기본적으로 소수 없이 반올림해 "88%"로 출력한다(87%가 아님에 주의). (12.9 참고)</details>

---

### 유형 C. 오류 찾기

**12.** 다음 코드는 "학번이 같으면 같은 학생"으로 취급하려 한다. 컴파일은 되지만 `HashSet`에 넣었을 때 **의도대로 동작하지 않는 버그**가 있다. 어디가 문제이고 어떻게 고쳐야 하는가?

```java
static class Student {
    int id;
    Student(int id) { this.id = id; }
    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return id == ((Student) o).id;
    }
    // hashCode 재정의 없음
}
// 사용:
Set<Student> set = new HashSet<>();
set.add(new Student(1));
System.out.println(set.contains(new Student(1)));  // 기대: true
```

<details><summary>힌트</summary>equals는 재정의했는데 hashCode는 재정의하지 않았다. HashSet은 먼저 무엇으로 버킷을 찾는가?</details>
<details><summary>정답 및 해설</summary>버그: `equals()`만 재정의하고 `hashCode()`를 재정의하지 않았다. HashSet은 먼저 hashCode로 버킷을 찾은 뒤 그 안에서 equals로 비교한다. 두 Student의 hashCode(기본은 주소 기반)가 다르면 서로 다른 버킷으로 가서 equals까지 도달하지 못하므로 `contains`가 `false`가 될 수 있다. 해결: `@Override public int hashCode() { return Objects.hash(id); }`를 추가해 equals와 같은 필드(id)로 해시를 만든다. (12.3 참고)</details>

---

**13.** 다음 코드는 이메일 형식을 검사하려는 의도지만 항상 컴파일 오류가 나거나 원하는 대로 매칭되지 않는다. 무엇이 문제인가?

```java
boolean ok = Pattern.matches("\w+@\w+\.\w+", "user@test.com");
```

<details><summary>힌트</summary>자바 문자열 리터럴 안에서 백슬래시(`\`)는 특별한 의미를 갖는다. `\w`, `\.`을 문자열에 그대로 쓸 수 있을까?</details>
<details><summary>정답 및 해설</summary>문제: 자바 문자열 안에서 `\`는 이스케이프 문자다. `"\w"`, `"\."`는 유효한 이스케이프 시퀀스가 아니므로 컴파일 오류(illegal escape character)가 난다. 정규식의 `\d`·`\w`·`\.`을 문자열에 담으려면 백슬래시를 **두 번** 써야 한다: `Pattern.matches("\\w+@\\w+\\.\\w+", "user@test.com")`. 즉 정규식 엔진이 볼 때는 `\w`, 자바 문자열로 쓸 때는 `\\w`. (12.10 참고)</details>

---

### 유형 D. 개념 서술

**14.** 다음 두 개념을 각각 서술하라.

(a) `String`과 `StringBuilder`의 차이를 "불변/가변"과 "성능" 관점에서 설명하고, 언제 어느 것을 써야 하는지 밝혀라.

(b) `equals()`와 `hashCode()`의 "계약(contract)"이 무엇인지 한 문장으로 쓰고, 이 계약을 어겼을 때 `HashMap`/`HashSet`에서 어떤 문제가 생기는지 설명하라.

<details><summary>정답 및 해설</summary>

(a) `String`은 **불변(immutable)**이라 내용을 바꾸는 메서드가 매번 새 객체를 만든다. 그래서 문자열을 반복해서 이어 붙이면(예: 루프 안 `s += x`) 그때마다 새 객체가 생겨 비효율적이다. `StringBuilder`는 **가변(mutable)**이라 내부 버퍼를 직접 수정하므로 반복 수정에 효율적이다. 정리: 한두 번의 단순 조합·상수 문자열은 `String`, 루프 등에서 문자열을 여러 번 조립·수정할 때는 `StringBuilder`.

(b) 계약: **"equals가 true인 두 객체는 hashCode도 반드시 같아야 한다."** 이를 어기면(예: equals만 재정의) 논리적으로 같은 두 객체가 서로 다른 hashCode를 가질 수 있고, HashMap/HashSet은 hashCode로 버킷을 먼저 찾으므로 "같은 객체"를 다른 버킷에서 찾게 되어 `get`/`contains`가 실패하거나 중복 저장이 발생한다. (12.3, 12.5 참고)

</details>

---

### 유형 E. 빈칸 채우기

**15.** 다음 문장의 빈칸 (가)~(마)를 채워라.

- 자바 표준 API에서 `java.base` 모듈 안의 `(가)` 패키지는 import 문 없이 자동으로 사용할 수 있다.
- `-128 ~ 127` 범위의 `Integer`는 미리 만들어 둔 `(나)` 객체를 재사용하므로 `==`가 우연히 `true`가 된다.
- 두 **날짜** 사이의 간격은 `(다)` 클래스로, 두 **시간** 사이의 간격은 `(라)` 클래스로 구한다.
- 커스텀 어노테이션을 리플렉션으로 실행 중에 읽으려면 `@Retention(RetentionPolicy.`(마)`)`를 지정해야 한다.

<details><summary>정답 및 해설</summary>(가) `java.lang` / (나) 캐시(cache) / (다) `Period` / (라) `Duration` / (마) `RUNTIME`. (각각 12.2, 12.6, 12.8, 12.12 참고)</details>

---

### 유형 F. 참·거짓 (이유 포함)

**16.** 다음 각 명제가 참인지 거짓인지 판단하고 **이유**를 한 줄로 쓰라.

(a) `str.trim()`을 호출하면 원본 문자열 `str`의 앞뒤 공백이 제거된다.

(b) `java.util`도 `java.lang`처럼 import 없이 자동으로 사용할 수 있다.

(c) `a + b`가 오버플로되면 자동으로 `ArithmeticException`이 발생한다.

(d) `Class<?>` 객체는 `클래스.class`, `객체.getClass()`, `Class.forName("...")` 세 방법으로 얻을 수 있고, 같은 클래스라면 셋 다 동일한 Class 객체를 가리킨다.

<details><summary>정답 및 해설</summary>

(a) **거짓.** String은 불변이라 trim()은 원본을 바꾸지 않고 새 문자열을 반환한다. 원본을 바꾸려면 `str = str.trim();`처럼 반환값을 다시 받아야 한다.

(b) **거짓.** 자동 import되는 것은 `java.lang`뿐이다. `List`, `Map`, `Objects` 등 `java.util`은 직접 `import java.util.*;`가 필요하다(같은 java.base 소속이어도 import는 별개).

(c) **거짓.** 일반 산술 연산은 오버플로가 나도 조용히 잘못된 값(2의 보수 순환 값)을 반환한다. 예외로 감지하려면 `Math.addExact` 계열을 써야 한다.

(d) **참.** 세 방법 모두 같은 클래스에 대해서는 JVM이 클래스당 하나만 유지하는 동일한 Class 객체를 반환한다(`c1 == c2 == c3`).

(12.5, 12.2, 12.7, 12.11 참고)

</details>

---

## 요약

- **API와 API 도큐먼트**: 자바가 미리 제공하는 클래스 모음이 API이고, 그 사용법을 정리한 공식 문서가 API 도큐먼트다. "기능 정의 → 클래스 찾기 → 메서드 고르기 → 매개변수/반환/예외 확인"의 4단계로 찾아 쓴다. (12.1)
- **java.base 모듈**: 항상 자동 포함되는 기본 모듈. 그 안의 `java.lang`은 import 없이 자동으로 쓸 수 있고, `java.util`·`java.io` 등은 직접 import한다. (12.2)
- **Object**: 모든 클래스의 부모. `equals`와 `hashCode`는 함께 재정의해야 하며, equals가 true면 hashCode도 같아야 한다. (12.3)
- **System**: 실행 환경 유틸리티. 경과 시간은 `nanoTime()`, 시스템 정보는 `getProperty`, 배열 복사는 `arraycopy`. (12.4)
- **String / StringBuilder**: String은 불변이라 변경 메서드가 새 문자열을 반환한다. 반복 수정은 가변인 StringBuilder로. (12.5)
- **포장 클래스**: 기본 타입을 객체로 감싼다. 오토박싱/언박싱이 자동으로 일어나지만, 값 비교는 `==`가 아니라 `equals()`로. (12.6)
- **Math**: 정적 수학 유틸리티. 오버플로 감지가 필요하면 `addExact` 계열을 쓴다. (12.7)
- **java.time**: 불변 날짜/시간 클래스. `LocalDate/Time/DateTime`, 간격은 `Period`(날짜)·`Duration`(시간), 형식은 `DateTimeFormatter`. (12.8)
- **형식**: `printf`/`String.format` 서식, `DecimalFormat`(패턴), `NumberFormat`(지역별 통화·백분율). (12.9)
- **정규 표현식**: `Pattern`으로 컴파일하고 `Matcher`로 적용한다. `matches`(전체)·`find`(부분)·`group`·`replaceAll`·`split`. 자바 문자열에서는 `\`를 `\\`로 쓴다. (12.10)
- **리플렉션**: 실행 중에 클래스 구조를 조회하고 동적으로 객체 생성·메서드 호출을 한다. 프레임워크의 기반 기술. (12.11)
- **어노테이션**: 코드에 붙이는 메타데이터. `@interface`로 정의하고 `@Retention(RUNTIME)` + 리플렉션으로 읽어 동작을 만든다. (12.12)
