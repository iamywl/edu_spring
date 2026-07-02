# Chapter 03: 컬렉션, 제네릭, 스트림, 람다 -- 실습 가이드

> 이 문서는 **실습 가이드**입니다. 개념 설명은 [JAVA_개념서](../docs/JAVA_개념서.md)와 [JAVA_교육자료](../docs/JAVA_교육자료.md)를 참고하세요.

---

## 환경 준비

```bash
docker compose up -d
# VS Code: F1 → "Dev Containers: Attach to Running Container" → java-edu
./compile.sh
./run.sh ListExample          # 컬렉션 - List
./run.sh GenericClassExample  # 제네릭
./run.sh StreamCreationExample # Stream API
./run.sh LambdaBasicsExample  # 람다
# 또는 대화형 메뉴: ./run.sh 실행 후 카테고리(Chapter 03) 선택 → 개념 선택
```

> 각 개념은 이제 독립 실행 스크립트로 분리되어 있습니다(**개념당 스크립트 1개**). 소스 파일을 수정한 후 `./compile.sh` → `./run.sh <ClassName>`으로 바로 결과를 확인하세요. 인자 없이 `./run.sh`를 실행하면 대화형 메뉴에서 카테고리(Chapter 03)를 고른 뒤 개념을 선택할 수 있습니다.

---

## 세션 순서

| 세션 | 토픽 | 실행 스크립트 |
|------|------|---------------|
| 1 | 제네릭 | `GenericClassExample`, `GenericMethodExample`, `BoundedTypeExample`, `WildcardExample` |
| 2 | 컬렉션 프레임워크 | `ListExample`, `SetExample`, `MapExample`, `QueueExample`, `CollectionsUtilExample` |
| 3 | Stream API | `StreamCreationExample`, `StreamIntermediateExample`, `StreamTerminalExample`, `CollectorsExample`, `ParallelStreamExample` |
| 4 | 람다와 함수형 인터페이스 | `LambdaBasicsExample`, `FunctionalInterfaceExample`, `MethodReferenceExample`, `OptionalExample`, `LambdaPracticalExample` |
| 5 | Comparable과 Comparator | `ComparableComparatorExample` |
| 6 | equals와 hashCode 계약 | `EqualsHashCodeExample` |
| 7 | [심화] Big-O 측정 | `BigOTiming` |
| 8 | [심화] HashMap 내부 동작 | `HashMapInternals` |
| 9 | [심화] 제네릭 타입 소거 | `TypeErasureDemo` |

---

### 세션 1: 제네릭 (Generics)

**개념 학습**
- 📖 [JAVA_개념서](../docs/JAVA_개념서.md) - Chapter 5: "제네릭" 읽기
- 📝 [JAVA_교육자료](../docs/JAVA_교육자료.md) - Part 3.1: "Generics와 PECS 원칙" 읽기

**예제 코드 분석**
- 파일: `GenericClassExample.java`(제네릭 클래스 `Box<T>`), `GenericMethodExample.java`(제네릭 메서드), `BoundedTypeExample.java`(바운디드 타입 `<T extends Number>`), `WildcardExample.java`(와일드카드/PECS)
- `Box<T>` 클래스가 어떻게 String, Integer, Double 등 다양한 타입을 하나의 클래스로 처리하는지 확인 (`GenericClassExample`)
- `NumberBox<T extends Number>`에서 바운디드 타입이 어떻게 `doubleValue()` 호출을 가능하게 하는지 확인 (`BoundedTypeExample`)
- `copy(List<? extends T> src, List<? super T> dest)` 메서드에서 PECS 원칙이 실제로 어떻게 적용되는지 확인 (`WildcardExample`)

**예제 실행**
- `./run.sh GenericClassExample`, `./run.sh GenericMethodExample`, `./run.sh BoundedTypeExample`, `./run.sh WildcardExample`
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
- 파일: `ListExample.java`, `SetExample.java`, `MapExample.java`, `QueueExample.java`, `CollectionsUtilExample.java`
- `SetExample`에서 HashSet, TreeSet, LinkedHashSet의 출력 순서 차이를 주목 -- 같은 데이터를 넣어도 순서가 다름
- `MapExample`에서 `putIfAbsent`, `computeIfAbsent`, `merge` 같은 Java 8+ 메서드들이 기존 if-null 패턴을 어떻게 대체하는지 확인
- `QueueExample`에서 ArrayDeque가 스택과 큐 두 가지로 모두 사용되는 패턴 확인

**예제 실행**
- `./run.sh ListExample`, `./run.sh SetExample`, `./run.sh MapExample`, `./run.sh QueueExample`, `./run.sh CollectionsUtilExample`
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
- 파일: `StreamCreationExample.java`, `StreamIntermediateExample.java`, `StreamTerminalExample.java`, `CollectorsExample.java`, `ParallelStreamExample.java`
- `StreamIntermediateExample`에서 `peek`의 출력 순서를 통해 Lazy 평가가 실제로 어떻게 동작하는지 확인 -- filter에 걸리지 않는 요소도 peek은 실행됨
- `CollectorsExample`의 Student 데이터로 `groupingBy`, `partitioningBy`, `summarizingInt`가 어떻게 데이터를 집계하는지 확인
- `ParallelStreamExample`에서 순차/병렬 스트림의 실행 시간 비교와 스레드 이름 출력 확인

**예제 실행**
- `./run.sh StreamCreationExample`, `./run.sh StreamIntermediateExample`, `./run.sh StreamTerminalExample`, `./run.sh CollectorsExample`, `./run.sh ParallelStreamExample`
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
- 파일: `LambdaBasicsExample.java`, `FunctionalInterfaceExample.java`, `MethodReferenceExample.java`, `OptionalExample.java`, `LambdaPracticalExample.java`
- `FunctionalInterfaceExample`에서 Predicate, Function, Consumer, Supplier 4가지 핵심 인터페이스가 각각 "조건 검사 / 변환 / 소비 / 생성" 역할을 하는 것 확인
- `MethodReferenceExample`에서 람다 `s -> s.toUpperCase()`가 메서드 참조 `String::toUpperCase`로 변환되는 4가지 패턴 확인
- `LambdaPracticalExample`에서 Predicate/Function/Consumer/Supplier가 실제 비즈니스 로직에 어떻게 조합되는지 확인, `OptionalExample`에서 Optional 체이닝 확인

**예제 실행**
- `./run.sh LambdaBasicsExample`, `./run.sh FunctionalInterfaceExample`, `./run.sh MethodReferenceExample`, `./run.sh OptionalExample`, `./run.sh LambdaPracticalExample`
- 출력에서 확인할 것들:
  - Calculator 인터페이스의 4가지 연산이 모두 람다로 구현된 것
  - `isEven.negate()`가 isOdd와 동일하게 동작하는 것
  - `Function.andThen`과 `Function.compose`의 실행 순서 차이
  - Optional 체이닝에서 null이 중간에 있을 때 안전하게 기본값이 반환되는 것

**실습 과제**
1. `FunctionalInterfaceExample`에서 새로운 `Predicate<String>`을 만들어 이름 리스트에서 3글자 이상인 이름만 필터링해보세요.
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
- `./run.sh ComparableComparatorExample`
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
- `./run.sh EqualsHashCodeExample`
- 출력에서 확인할 것들:
  - `BadProduct`: `equals`는 `true`인데 `hashCode`가 달라 HashSet 크기가 2가 되는 것(버그)
  - `GoodProduct`: HashSet 크기가 1이 되고 `contains`로 동일 객체를 찾는 것

**실습 과제**
1. `BadProduct`에 올바른 `hashCode()`를 추가해 버그를 고쳐보세요.
2. 같은 두 필드를 갖는 `record`로 만들면 equals/hashCode가 자동 생성됨을 확인해보세요.

---

### 세션 7: [심화] Big-O 측정

> CS 전공 심화. 추상적으로만 배우던 시간복잡도를 `System.nanoTime`으로 직접 재서 곡선이 갈라지는 것을 봅니다.

**예제 코드 분석**
- 파일: `src/main/java/com/edu/collections/BigOTiming.java`
- 선형 탐색 O(n) vs `Arrays.binarySearch` O(log n), `ArrayList.get` O(1) vs `LinkedList.get` O(n), 맨 앞 삽입 O(n²) vs 맨 뒤 삽입 O(1)을 N을 키워가며 표로 비교
- 주석의 "지배 연산을 센다"는 Big-O 도출 방법을 코드와 함께 읽으세요

**예제 실행**
- `./run.sh BigOTiming`
- 출력에서 확인할 것들:
  - N이 10배 커질 때 선형 탐색 시간은 약 10배, 이진 탐색은 거의 일정한 것
  - `LinkedList.get(mid)`가 N에 비례해 급격히 느려지는 배율
  - 맨 앞 삽입이 삽입 횟수 2배당 약 4배로 폭발(n²)하는 것

**실습 과제**
1. `ArrayList` 대신 `ArrayDeque`의 `addFirst`로 맨 앞 삽입을 측정해 O(1)로 개선되는지 확인해보세요.
2. N 값을 더 키워(예: 200만) 곡선이 더 뚜렷하게 갈라지는지 확인해보세요.

---

### 세션 8: [심화] HashMap 내부 동작

> CS 전공 심화. HashMap이 왜 O(1)인지, 언제 O(n)으로 퇴화하는지를 직접 측정합니다.

**예제 코드 분석**
- 파일: `src/main/java/com/edu/collections/HashMapInternals.java`
- 버킷 배열, 해시로 인덱스 계산, 충돌 체이닝, treeify(8), 로드팩터 0.75 resize를 주석으로 설명
- 좋은 hashCode와 "무조건 42를 반환하는" 나쁜 hashCode의 조회 시간을 비교

**예제 실행**
- `./run.sh HashMapInternals`
- 출력에서 확인할 것들:
  - key 개수를 100배 늘려도 조회 시간이 거의 그대로인 것(O(1))
  - 로드팩터 임계값(12, 24, 48...)을 넘을 때마다 용량이 2배로 resize되는 지점
  - 나쁜 hashCode가 좋은 hashCode보다 수백 배 느린 것(모두 한 버킷으로 충돌)
  - put 이후 key의 필드를 바꾸면(계약 위반) 같은 객체인데도 조회가 `null`이 되는 것

**실습 과제**
1. `BadKey`의 `hashCode`를 `id % 100`으로 바꿔 버킷이 100개로 분산되면 속도가 얼마나 회복되는지 확인해보세요.
2. `MutableKey`를 `record`로 바꿔 필드를 불변으로 만들면 세션 4의 버그가 사라지는 것을 확인해보세요.

---

### 세션 9: [심화] 제네릭 타입 소거

> CS 전공 심화. 제네릭 타입 정보가 런타임에 지워진다는 것을 실행으로 증명합니다.

**예제 코드 분석**
- 파일: `src/main/java/com/edu/collections/TypeErasureDemo.java`
- `List<String>`과 `List<Integer>`의 `getClass()`가 같은 것, `new T[]`·`T.class`·`instanceof List<String>`이 불가능한 이유를 주석으로 설명
- raw type으로 잘못된 원소를 몰래 넣었을 때 `ClassCastException`이 "꺼내는 순간" 터지는 위치 확인

**예제 실행**
- `./run.sh TypeErasureDemo`
- 출력에서 확인할 것들:
  - `strings.getClass() == integers.getClass()`가 `true`인 것(타입 소거)
  - `List<String>`에 raw 캐스트로 넣은 `Integer(42)`가 삽입 시엔 조용하다가, 향상된 for문의 숨은 `(String)` 캐스트에서 예외가 터지는 것
  - 소거를 택한 이유가 "하위 호환성"인 것

**실습 과제**
1. `strings.get(1)`을 `Object`로 받아 출력해 예외 없이 42가 나오는 것을 확인하고, 왜 for문에서만 터지는지 설명해보세요.
2. 제네릭 배열 `new T[]`가 필요한 상황을 `List<T>`로 대체하는 코드를 작성해보세요.

---

## 학습 팁

- 각 세션의 **개념 학습**을 먼저 읽고 코드를 분석하면 이해가 빠릅니다.
- 실습 과제를 수행한 후 반드시 `./compile.sh` → `./run.sh <ClassName>`으로 결과를 확인하세요.
- 컴파일 에러가 나면 에러 메시지를 읽어보세요 -- 제네릭 관련 에러 메시지를 해석하는 것도 중요한 학습입니다.
