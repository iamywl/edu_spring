# Chapter 03: Collections, Generics, Streams

Java 컬렉션 프레임워크, 제네릭, 스트림 API를 학습합니다.

---

## 1. 제네릭 (Generic)

제네릭은 클래스나 메서드를 정의할 때 타입을 파라미터로 지정하는 기능입니다.
컴파일 시점에 타입 안전성을 보장하며, 불필요한 캐스팅을 제거합니다.

### 1.1 제네릭 클래스

```java
// T는 타입 파라미터 (Type Parameter)
public class Box<T> {
    private T value;

    public void set(T value) { this.value = value; }
    public T get() { return value; }
}

// 사용
Box<String> stringBox = new Box<>();
stringBox.set("Hello");
String value = stringBox.get(); // 캐스팅 불필요
```

### 1.2 제네릭 메서드

```java
public static <T> T getFirst(List<T> list) {
    return list.isEmpty() ? null : list.get(0);
}
```

### 1.3 바운디드 타입 파라미터 (Bounded Type Parameter)

```java
// T는 반드시 Number의 하위 타입이어야 함
public static <T extends Number> double sum(List<T> list) {
    return list.stream().mapToDouble(Number::doubleValue).sum();
}
```

### 1.4 와일드카드 (Wildcard)

| 와일드카드 | 의미 | 사용 시점 |
|---|---|---|
| `<?>` | 모든 타입 허용 | 읽기 전용으로 사용할 때 |
| `<? extends T>` | T 또는 T의 하위 타입 | 상한 경계 - 데이터를 꺼낼 때 (Producer) |
| `<? super T>` | T 또는 T의 상위 타입 | 하한 경계 - 데이터를 넣을 때 (Consumer) |

> **PECS 원칙**: Producer-Extends, Consumer-Super

---

## 2. List, Set, Map

### 2.1 List - 순서가 있는 컬렉션

| 구현체 | 내부 구조 | 특징 |
|---|---|---|
| `ArrayList` | 배열 기반 | 인덱스 접근 O(1), 삽입/삭제 O(n) |
| `LinkedList` | 이중 연결 리스트 | 삽입/삭제 O(1), 인덱스 접근 O(n) |

```java
List<String> arrayList = new ArrayList<>();
List<String> linkedList = new LinkedList<>();
```

### 2.2 Set - 중복을 허용하지 않는 컬렉션

| 구현체 | 내부 구조 | 특징 |
|---|---|---|
| `HashSet` | 해시 테이블 | 가장 빠름, 순서 없음 |
| `TreeSet` | 레드-블랙 트리 | 정렬된 순서 유지 |
| `LinkedHashSet` | 해시 테이블 + 연결 리스트 | 삽입 순서 유지 |

### 2.3 Map - 키-값 쌍 컬렉션

| 구현체 | 내부 구조 | 특징 |
|---|---|---|
| `HashMap` | 해시 테이블 | 가장 빠름, 순서 없음 |
| `TreeMap` | 레드-블랙 트리 | 키 기준 정렬 |
| `LinkedHashMap` | 해시 테이블 + 연결 리스트 | 삽입 순서 유지 |

---

## 3. Queue, Deque

### 3.1 Queue - FIFO (First In, First Out)

```java
Queue<String> queue = new LinkedList<>();
queue.offer("first");   // 삽입
queue.poll();            // 제거 및 반환
queue.peek();            // 조회만
```

### 3.2 Deque - 양방향 큐

```java
Deque<String> deque = new ArrayDeque<>();
deque.offerFirst("front");  // 앞에 삽입
deque.offerLast("back");    // 뒤에 삽입
deque.pollFirst();           // 앞에서 제거
deque.pollLast();            // 뒤에서 제거
```

### 3.3 PriorityQueue - 우선순위 큐

```java
// 기본: 오름차순 정렬
PriorityQueue<Integer> pq = new PriorityQueue<>();

// 내림차순 정렬
PriorityQueue<Integer> maxHeap = new PriorityQueue<>(Comparator.reverseOrder());
```

---

## 4. Collections 유틸리티

```java
// 정렬
Collections.sort(list);
Collections.sort(list, Comparator.reverseOrder());

// 불변 리스트 (수정 시 UnsupportedOperationException 발생)
List<String> unmodifiable = Collections.unmodifiableList(list);

// 스레드 안전 리스트
List<String> syncList = Collections.synchronizedList(new ArrayList<>());

// 기타 유틸리티
Collections.reverse(list);
Collections.shuffle(list);
Collections.frequency(list, "element");
Collections.min(list);
Collections.max(list);
```

---

## 5. Stream API

Stream은 컬렉션 데이터를 선언적으로 처리하는 API입니다 (Java 8+).

### 5.1 Stream 생성

```java
// 컬렉션에서 생성
list.stream();

// 직접 생성
Stream.of("a", "b", "c");
Stream.iterate(0, n -> n + 2);    // 0, 2, 4, 6, ...
Stream.generate(Math::random);     // 무한 스트림
```

### 5.2 중간 연산 (Intermediate Operations) - Lazy 평가

| 연산 | 설명 | 예시 |
|---|---|---|
| `filter` | 조건에 맞는 요소만 통과 | `.filter(x -> x > 5)` |
| `map` | 요소 변환 | `.map(String::toUpperCase)` |
| `flatMap` | 중첩 구조 평탄화 | `.flatMap(Collection::stream)` |
| `sorted` | 정렬 | `.sorted(Comparator.reverseOrder())` |
| `distinct` | 중복 제거 | `.distinct()` |
| `peek` | 디버깅용 확인 | `.peek(System.out::println)` |
| `limit` | 개수 제한 | `.limit(5)` |
| `skip` | 앞에서 건너뛰기 | `.skip(3)` |

### 5.3 최종 연산 (Terminal Operations) - 실행 트리거

| 연산 | 설명 | 반환 타입 |
|---|---|---|
| `collect` | 결과 수집 | 컬렉션 등 |
| `reduce` | 누적 연산 | Optional/T |
| `forEach` | 각 요소 소비 | void |
| `count` | 개수 | long |
| `anyMatch` | 하나라도 일치? | boolean |
| `allMatch` | 모두 일치? | boolean |
| `findFirst` | 첫 번째 요소 | Optional |

### 5.4 Collectors

```java
// 리스트로 수집
.collect(Collectors.toList());

// 그룹핑
.collect(Collectors.groupingBy(Person::getCity));

// 문자열 결합
.collect(Collectors.joining(", "));

// 통계
.collect(Collectors.summarizingInt(Person::getAge));
```

### 5.5 Parallel Stream

```java
list.parallelStream()
    .filter(x -> x > 10)
    .collect(Collectors.toList());
```

> 주의: 항상 빠른 것은 아닙니다. 데이터가 충분히 크고, 독립적인 연산일 때 효과적입니다.

---

## 6. Optional

`null` 대신 값의 존재/부재를 명시적으로 표현하는 컨테이너입니다.

```java
// 생성
Optional<String> opt = Optional.of("value");
Optional<String> empty = Optional.empty();
Optional<String> nullable = Optional.ofNullable(possiblyNull);

// 값 접근
opt.isPresent();                    // true/false
opt.ifPresent(System.out::println); // 값이 있을 때만 실행
opt.orElse("default");              // 없으면 기본값
opt.orElseThrow();                  // 없으면 예외

// 변환
opt.map(String::toUpperCase);
opt.flatMap(this::findById);
opt.filter(s -> s.length() > 3);
```

---

## 7. Lambda와 함수형 인터페이스

### 7.1 주요 함수형 인터페이스

| 인터페이스 | 메서드 | 입력 | 출력 | 용도 |
|---|---|---|---|---|
| `Predicate<T>` | `test(T)` | T | boolean | 조건 검사 |
| `Function<T,R>` | `apply(T)` | T | R | 변환 |
| `Consumer<T>` | `accept(T)` | T | void | 소비 |
| `Supplier<T>` | `get()` | - | T | 생성 |
| `UnaryOperator<T>` | `apply(T)` | T | T | 단항 연산 |
| `BinaryOperator<T>` | `apply(T,T)` | T, T | T | 이항 연산 |

### 7.2 Lambda 표현식

```java
// 기본 형태
(parameters) -> expression
(parameters) -> { statements; }

// 예시
Predicate<String> isEmpty = s -> s.isEmpty();
Function<String, Integer> toLength = String::length;  // 메서드 참조
Consumer<String> printer = System.out::println;
Supplier<List<String>> listFactory = ArrayList::new;   // 생성자 참조
```

### 7.3 메서드 참조 (Method Reference)

| 유형 | 문법 | Lambda 동등 표현 |
|---|---|---|
| 정적 메서드 | `Class::staticMethod` | `x -> Class.staticMethod(x)` |
| 인스턴스 메서드 | `obj::method` | `x -> obj.method(x)` |
| 임의 객체 메서드 | `Class::method` | `(obj, x) -> obj.method(x)` |
| 생성자 | `Class::new` | `x -> new Class(x)` |

---

## 실행 방법

### javac 직접 실행
```bash
cd chapter03-collections
mkdir -p out
javac -d out src/main/java/com/edu/collections/*.java
java -cp out com.edu.collections.GenericExample
java -cp out com.edu.collections.CollectionExample
java -cp out com.edu.collections.StreamExample
java -cp out com.edu.collections.LambdaExample
```

### Docker 실행
```bash
cd chapter03-collections
docker build -t chapter03 .
docker run --rm chapter03
```

---

## 핵심 정리

| 주제 | 핵심 포인트 |
|---|---|
| 제네릭 | 타입 안전성, PECS 원칙, 와일드카드 |
| 컬렉션 | List(순서), Set(중복X), Map(키-값), Queue(FIFO) |
| Stream | 선언적 처리, Lazy 평가, 중간/최종 연산 구분 |
| Optional | null 안전성, map/flatMap 체이닝 |
| Lambda | 함수형 인터페이스, 메서드 참조, 간결한 코드 |
