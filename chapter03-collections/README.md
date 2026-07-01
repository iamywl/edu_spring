# Chapter 03: 컬렉션, 제네릭, 스트림, 람다 -- 실습 가이드

> 이 문서는 **실습 가이드**입니다. 개념 설명은 [JAVA_개념서](../docs/JAVA_개념서.md)와 [JAVA_교육자료](../docs/JAVA_교육자료.md)를 참고하세요.

---

## 환경 준비

```bash
docker compose up -d
# VS Code: F1 → "Dev Containers: Attach to Running Container" → java-edu
./compile.sh
./run.sh 6    # 컬렉션
./run.sh 7    # 제네릭
./run.sh 8    # Stream API
./run.sh 9    # 람다
```

> 소스 파일을 수정한 후 `./compile.sh` → `./run.sh N`으로 바로 결과를 확인하세요.

---

## 세션 순서

| 세션 | 토픽 | 실행 명령 | 소스 파일 |
|------|------|-----------|-----------|
| 1 | 제네릭 | `./run.sh 7` | `GenericExample.java` |
| 2 | 컬렉션 프레임워크 | `./run.sh 6` | `CollectionExample.java` |
| 3 | Stream API | `./run.sh 8` | `StreamExample.java` |
| 4 | 람다와 함수형 인터페이스 | `./run.sh 9` | `LambdaExample.java` |
| 5 | Comparable과 Comparator | `./run.sh 14` | `ComparableComparatorExample.java` |
| 6 | equals와 hashCode 계약 | `./run.sh 15` | `EqualsHashCodeExample.java` |

---

### 세션 1: 제네릭 (Generics)

**개념 학습**
- 📖 [JAVA_개념서](../docs/JAVA_개념서.md) - Chapter 5: "제네릭" 읽기
- 📝 [JAVA_교육자료](../docs/JAVA_교육자료.md) - Part 3.1: "Generics와 PECS 원칙" 읽기

**예제 코드 분석**
- 파일: `src/main/java/com/edu/collections/GenericExample.java`
- `Box<T>` 클래스가 어떻게 String, Integer, Double 등 다양한 타입을 하나의 클래스로 처리하는지 확인
- `NumberBox<T extends Number>`에서 바운디드 타입이 어떻게 `doubleValue()` 호출을 가능하게 하는지 확인
- `copy(List<? extends T> src, List<? super T> dest)` 메서드에서 PECS 원칙이 실제로 어떻게 적용되는지 확인

**예제 실행**
- `./run.sh 7`
- 출력에서 확인할 것들:
  - `Box<String>`과 `Box<Integer>`가 같은 클래스인데 다른 타입을 담는 것
  - `NumberBox<String>` 사용 시 컴파일 에러가 나는 이유 (주석 처리된 코드 참고)
  - 와일드카드 섹션에서 `Integer` 리스트와 `Double` 리스트가 모두 `sumWithWildcard`에 전달되는 것

**실습 과제**
1. `Box<T>`에 `boolean isEmpty()` 메서드를 추가하세요. value가 null이면 true를 반환합니다. main에서 테스트 코드도 추가하세요.
2. `Pair<K, V>`를 3개의 타입 파라미터를 받는 `Triple<A, B, C>` 클래스로 확장해보세요.
3. `NumberBox<String> strBox = new NumberBox<>("error");` 주석을 해제하고 컴파일해보세요. 어떤 에러 메시지가 나오나요?

---

### 세션 2: 컬렉션 프레임워크 List/Set/Map

**개념 학습**
- 📖 [JAVA_개념서](../docs/JAVA_개념서.md) - Chapter 5: "컬렉션 - 배열의 한계, List/Set/Map, HashMap 내부 구조" 읽기
- 📝 [JAVA_교육자료](../docs/JAVA_교육자료.md) - Part 3.2: "Collection framework와 시간복잡도" 읽기

**예제 코드 분석**
- 파일: `src/main/java/com/edu/collections/CollectionExample.java`
- `demonstrateSet()`에서 HashSet, TreeSet, LinkedHashSet의 출력 순서 차이를 주목 -- 같은 데이터를 넣어도 순서가 다름
- `demonstrateMap()`에서 `putIfAbsent`, `computeIfAbsent`, `merge` 같은 Java 8+ 메서드들이 기존 if-null 패턴을 어떻게 대체하는지 확인
- `demonstrateQueue()`에서 ArrayDeque가 스택과 큐 두 가지로 모두 사용되는 패턴 확인

**예제 실행**
- `./run.sh 6`
- 출력에서 확인할 것들:
  - HashSet의 출력 순서가 삽입 순서와 다른 것
  - TreeSet이 한글 기준으로 자동 정렬되는 것
  - 합집합/교집합/차집합 연산 결과
  - HashMap에서 같은 키("김철수")로 두 번 put했을 때 값이 92로 갱신되는 것

**실습 과제**
1. `demonstrateSet()`에서 TreeSet 대신 LinkedHashSet을 사용해보세요. 출력 순서가 어떻게 달라지나요?
2. `demonstrateMap()`에서 `merge`를 사용하여 모든 과목 점수에 5점 보너스를 추가하는 코드를 작성하세요.
3. `demonstrateQueue()`에서 PriorityQueue에 문자열을 넣고, 문자열 길이 기준으로 정렬되도록 Comparator를 지정해보세요.

---

### 세션 3: Stream API

**개념 학습**
- 📖 [JAVA_개념서](../docs/JAVA_개념서.md) - Chapter 6: "함수형 프로그래밍 - Stream (컨베이어 벨트 비유)" 읽기
- 📝 [JAVA_교육자료](../docs/JAVA_교육자료.md) - Part 3.3~3.4: "Lambda/Stream API, 메서드 참조" 읽기

**예제 코드 분석**
- 파일: `src/main/java/com/edu/collections/StreamExample.java`
- `demonstrateIntermediateOperations()`에서 `peek`의 출력 순서를 통해 Lazy 평가가 실제로 어떻게 동작하는지 확인 -- filter에 걸리지 않는 요소도 peek은 실행됨
- `demonstrateCollectors()`의 Student 데이터로 `groupingBy`, `partitioningBy`, `summarizingInt`가 어떻게 데이터를 집계하는지 확인
- `demonstrateParallelStream()`에서 순차/병렬 스트림의 실행 시간 비교와 스레드 이름 출력 확인

**예제 실행**
- `./run.sh 8`
- 출력에서 확인할 것들:
  - `Stream.iterate`와 `Stream.generate`의 차이 (규칙적 vs 무작위)
  - `flatMap`이 중첩 리스트를 어떻게 평탄화하는지
  - `reduce`로 합계, 최댓값, 문자열 결합이 모두 가능한 것
  - `groupingBy`로 도시별 학생 분류 결과
  - 병렬 스트림 실행 시 사용되는 스레드 이름 목록

**실습 과제**
1. `demonstrateIntermediateOperations()`에서 filter 조건을 `name.startsWith("김")` 대신 `name.endsWith("수")`로 바꿔보세요. 결과가 어떻게 달라지나요?
2. `demonstrateCollectors()`에서 Student 데이터를 나이 그룹별(20대 초반/중반/후반)로 groupingBy 해보세요.
3. `demonstrateTerminalOperations()`에서 `reduce`를 사용하여 리스트의 모든 요소를 곱하는 코드를 추가하세요.

---

### 세션 4: 람다와 함수형 인터페이스

**개념 학습**
- 📖 [JAVA_개념서](../docs/JAVA_개념서.md) - Chapter 6: "람다 표현식" 읽기
- 📝 [JAVA_교육자료](../docs/JAVA_교육자료.md) - Part 3.3: "Lambda와 함수형 인터페이스" 읽기

**예제 코드 분석**
- 파일: `src/main/java/com/edu/collections/LambdaExample.java`
- `demonstrateFunctionalInterfaces()`에서 Predicate, Function, Consumer, Supplier 4가지 핵심 인터페이스가 각각 "조건 검사 / 변환 / 소비 / 생성" 역할을 하는 것 확인
- `demonstrateMethodReferences()`에서 람다 `s -> s.toUpperCase()`가 메서드 참조 `String::toUpperCase`로 변환되는 4가지 패턴 확인
- `demonstratePracticalExamples()`에서 Predicate/Function/Consumer/Supplier가 실제 비즈니스 로직에 어떻게 조합되는지 확인

**예제 실행**
- `./run.sh 9`
- 출력에서 확인할 것들:
  - Calculator 인터페이스의 4가지 연산이 모두 람다로 구현된 것
  - `isEven.negate()`가 isOdd와 동일하게 동작하는 것
  - `Function.andThen`과 `Function.compose`의 실행 순서 차이
  - Optional 체이닝에서 null이 중간에 있을 때 안전하게 기본값이 반환되는 것

**실습 과제**
1. `LambdaExample`에서 새로운 `Predicate<String>`을 만들어 이름 리스트에서 3글자 이상인 이름만 필터링해보세요.
2. `Calculator` 인터페이스를 사용하여 거듭제곱(`Math.pow`) 연산을 구현해보세요. (힌트: 반환 타입이 int이므로 캐스팅 필요)
3. `demonstratePracticalExamples()`에서 `Function` 조합을 사용하여 Person의 이름을 대문자로 변환 후 "님" 접미사를 붙이는 파이프라인을 만들어보세요.

---

### 세션 5: Comparable과 Comparator

**개념 학습**
- 📝 [JAVA_교육자료](../docs/JAVA_교육자료.md) - "객체 정렬: Comparable / Comparator" 참고

**예제 코드 분석**
- 파일: `src/main/java/com/edu/collections/ComparableComparatorExample.java`
- `Student implements Comparable<Student>`로 자연 정렬(점수 오름차순)을 정의하는 방법 확인
- `Comparator.comparing(...).thenComparing(...)`로 다중 키 정렬, `reversed()`로 역순 정렬 확인
- `TreeSet`이 `compareTo`(또는 생성자에 넘긴 Comparator)로 자동 정렬하는 것 확인

**예제 실행**
- `./run.sh 14`
- 출력에서 확인할 것들:
  - `sort(null)`로 자연 순서 정렬이 동작하는 것
  - 점수 내림차순 + 동점 시 나이 오름차순의 다중 키 정렬 결과
  - TreeSet이 `compareTo == 0`인 요소를 중복으로 보고 저장하지 않는 것

**실습 과제**
1. 이름 기준 정렬 후 동명이인은 점수 높은 순으로 정렬하는 Comparator를 만들어보세요.
2. `Comparator.comparingInt`로 점수 정렬을 작성해 `comparing`과 비교해보세요.

---

### 세션 6: equals와 hashCode 계약

**개념 학습**
- 📝 [JAVA_교육자료](../docs/JAVA_교육자료.md) - "equals/hashCode 계약" 참고

**예제 코드 분석**
- 파일: `src/main/java/com/edu/collections/EqualsHashCodeExample.java`
- `equals`만 재정의하고 `hashCode`를 재정의하지 않은 `BadProduct`가 `HashSet`에서 중복 제거에 실패하는 버그 확인
- `equals`와 `hashCode`를 함께 재정의한 `GoodProduct`로 정상 동작하는 것 확인
- equals/hashCode 계약 규칙(주석/출력) 확인

**예제 실행**
- `./run.sh 15`
- 출력에서 확인할 것들:
  - `BadProduct`: `equals`는 `true`인데 `hashCode`가 달라 HashSet 크기가 2가 되는 것(버그)
  - `GoodProduct`: HashSet 크기가 1이 되고 `contains`로 동일 객체를 찾는 것

**실습 과제**
1. `BadProduct`에 올바른 `hashCode()`를 추가해 버그를 고쳐보세요.
2. 같은 두 필드를 갖는 `record`로 만들면 equals/hashCode가 자동 생성됨을 확인해보세요.

---

## 학습 팁

- 각 세션의 **개념 학습**을 먼저 읽고 코드를 분석하면 이해가 빠릅니다.
- 실습 과제를 수행한 후 반드시 `./compile.sh` → `./run.sh N`으로 결과를 확인하세요.
- 컴파일 에러가 나면 에러 메시지를 읽어보세요 -- 제네릭 관련 에러 메시지를 해석하는 것도 중요한 학습입니다.
