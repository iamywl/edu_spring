# Java 코딩 표준 (Java Coding Standards)

> **대상**: Java 21 / Spring Boot 3.x 기반 프로젝트
> **참고**: Google Java Style Guide, Oracle Code Conventions, Effective Java 3rd Edition
> **최종 수정**: 2026-03-17

---

## 목차

1. [개요](#1-개요)
2. [소스 파일 구조](#2-소스-파일-구조)
3. [명명 규칙 (Naming Conventions)](#3-명명-규칙-naming-conventions)
4. [포맷팅 (Formatting)](#4-포맷팅-formatting)
5. [import 규칙](#5-import-규칙)
6. [클래스 구성 순서](#6-클래스-구성-순서)
7. [주석과 Javadoc](#7-주석과-javadoc)
8. [프로그래밍 관행 (Programming Practices)](#8-프로그래밍-관행-programming-practices)
9. [Spring Boot 코딩 규칙](#9-spring-boot-코딩-규칙)
10. [테스트 코딩 규칙](#10-테스트-코딩-규칙)
11. [안티패턴과 피해야 할 것들](#11-안티패턴과-피해야-할-것들)
12. [도구와 자동화](#12-도구와-자동화)

---

## 1. 개요

### 1.1 코딩 표준이 필요한 이유

코딩 표준은 단순한 "코드를 예쁘게 만드는 규칙"이 아니다. 소프트웨어 개발에서 코딩 표준이 중요한 이유는 다음과 같다.

- **가독성 향상**: 일관된 스타일은 코드를 읽는 시간을 크게 줄여준다. 개발자는 코드를 작성하는 시간보다 읽는 시간이 10배 이상 많다.
- **유지보수 비용 절감**: 통일된 코드 스타일은 새로운 팀원이 프로젝트에 적응하는 시간을 단축한다.
- **버그 예방**: 좋은 코딩 습관은 흔한 실수와 버그를 사전에 방지한다.
- **코드 리뷰 효율화**: 스타일 논쟁 대신 로직과 설계에 집중할 수 있다.
- **팀 협업 강화**: "내 코드"가 아닌 "우리 코드"라는 인식을 만든다.

> "코드는 쓰는 것보다 읽는 것이 훨씬 더 많다." -- Robert C. Martin, Clean Code

### 1.2 참고 표준

이 문서는 다음 표준들을 기반으로 작성되었다.

| 표준 | 설명 |
|------|------|
| [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html) | 업계에서 가장 널리 참조되는 Java 스타일 가이드 |
| [Oracle Code Conventions](https://www.oracle.com/java/technologies/javase/codeconventions-introduction.html) | Java 공식 코딩 규약 (클래식) |
| [Effective Java 3rd Edition](https://www.oreilly.com/library/view/effective-java/9780134686097/) | Joshua Bloch의 Java 모범 사례 |
| [Spring Framework 코딩 스타일](https://github.com/spring-projects/spring-framework/wiki/Code-Style) | Spring 프로젝트 자체의 코딩 규칙 |

### 1.3 이 문서의 규칙 수준

각 규칙은 다음 수준으로 분류된다.

- **필수(MUST)**: 반드시 준수해야 한다. 코드 리뷰 시 위반 사항은 수정 필요.
- **권장(SHOULD)**: 가능한 한 준수한다. 합리적인 이유가 있으면 예외 허용.
- **선택(MAY)**: 팀 합의에 따라 결정한다.

---

## 2. 소스 파일 구조

### 2.1 파일 인코딩

**[필수]** 모든 소스 파일은 **UTF-8**로 인코딩한다.

```
# .editorconfig
[*.java]
charset = utf-8
```

### 2.2 파일 이름

**[필수]** 소스 파일 이름은 파일 안에 포함된 최상위(top-level) 클래스의 이름과 정확히 일치해야 하며, `.java` 확장자를 사용한다.

```
// 파일명: OrderService.java
public class OrderService { ... }
```

### 2.3 파일 구조 순서

**[필수]** 하나의 소스 파일은 다음 순서로 구성한다.

```java
// 1. 라이선스/저작권 헤더 (있는 경우)
/*
 * Copyright 2026 MyCompany. All rights reserved.
 */

// 2. package 선언
package com.example.myapp.order.service;

// 3. import 선언 (섹션 5 참조)
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.myapp.order.domain.Order;

// 4. 정확히 하나의 최상위 클래스
public class OrderService {
    // ...
}
```

### 2.4 하나의 파일에 하나의 최상위 클래스

**[필수]** 각 소스 파일에는 정확히 하나의 최상위 클래스만 포함한다.

```java
// ❌ 잘못된 예: 하나의 파일에 여러 최상위 클래스
// 파일명: Order.java
public class Order { ... }
class OrderItem { ... }  // 별도 파일로 분리해야 함
```

```java
// ✅ 올바른 예: 각각 별도 파일
// Order.java
public class Order { ... }

// OrderItem.java
public class OrderItem { ... }
```

**예외**: 파일 내부에 정의하는 중첩 클래스(nested class), sealed class의 permit된 하위 클래스는 허용된다.

```java
// ✅ 허용: sealed class와 permits 대상을 같은 파일에 정의
public sealed interface Shape
        permits Circle, Rectangle, Triangle {
    double area();
}

record Circle(double radius) implements Shape {
    public double area() { return Math.PI * radius * radius; }
}

record Rectangle(double width, double height) implements Shape {
    public double area() { return width * height; }
}

record Triangle(double base, double height) implements Shape {
    public double area() { return 0.5 * base * height; }
}
```

### 2.5 소스 파일 길이

**[권장]** 하나의 소스 파일은 **500줄**을 넘지 않도록 한다. 파일이 지나치게 길어지면 클래스의 책임이 과도하다는 신호일 수 있다.

---

## 3. 명명 규칙 (Naming Conventions)

명명 규칙은 코드의 가독성에 가장 직접적인 영향을 미친다. 좋은 이름은 주석 없이도 의도를 전달한다.

### 3.1 패키지명

**[필수]** 패키지명은 모두 소문자로 작성하며, 역 도메인명(reverse domain name) 규칙을 따른다.

```java
// ✅ 올바른 예
package com.example.myapp;
package com.example.myapp.order.service;
package com.example.myapp.common.util;

// ❌ 잘못된 예
package com.example.myApp;        // 대문자 사용
package com.example.my_app;       // 언더스코어 사용
package com.example.myapp.Order;  // 대문자 사용
```

**[권장]** 패키지 구조는 기능(feature) 기반으로 구성한다.

```
// ✅ 기능 기반 패키지 구조 (권장)
com.example.myapp
├── order
│   ├── controller
│   ├── service
│   ├── repository
│   ├── domain
│   └── dto
├── member
│   ├── controller
│   ├── service
│   ├── repository
│   ├── domain
│   └── dto
└── common
    ├── config
    ├── exception
    └── util
```

### 3.2 클래스 / 인터페이스명

**[필수]** 클래스와 인터페이스 이름은 **UpperCamelCase(파스칼 케이스)**를 사용한다.

```java
// ✅ 올바른 예
public class OrderService { }
public class HttpClient { }
public class XmlParser { }
public interface Serializable { }
public record OrderResponse(Long id, String name) { }

// ❌ 잘못된 예
public class orderService { }    // 소문자 시작
public class Order_Service { }   // 언더스코어 사용
public class ORDERSERVICE { }    // 전부 대문자
```

**[권장]** 클래스 이름에는 역할을 나타내는 접미사를 사용한다.

| 접미사 | 용도 | 예시 |
|--------|------|------|
| `Controller` | REST 컨트롤러 | `OrderController` |
| `Service` | 비즈니스 로직 | `OrderService` |
| `Repository` | 데이터 접근 | `OrderRepository` |
| `Entity` | JPA 엔티티 (또는 접미사 없음) | `Order`, `OrderEntity` |
| `Dto` / `Request` / `Response` | 데이터 전송 객체 | `OrderCreateRequest` |
| `Exception` | 예외 클래스 | `OrderNotFoundException` |
| `Config` / `Configuration` | 설정 클래스 | `SecurityConfig` |
| `Mapper` | 객체 변환기 | `OrderMapper` |
| `Validator` | 검증기 | `OrderValidator` |
| `Factory` | 팩토리 | `ConnectionFactory` |
| `Handler` | 핸들러 | `GlobalExceptionHandler` |

### 3.3 메서드명

**[필수]** 메서드 이름은 **lowerCamelCase**를 사용하며, 동사 또는 동사구로 시작한다.

```java
// ✅ 올바른 예
public void sendEmail() { }
public Order findById(Long id) { }
public List<Order> findAllByStatus(OrderStatus status) { }
public boolean isValid() { }
public OrderResponse toResponse() { }

// ❌ 잘못된 예
public void SendEmail() { }       // 대문자 시작
public void send_email() { }      // 언더스코어 사용
public Order order(Long id) { }   // 동사가 아닌 명사
```

**[권장]** 메서드 이름 규칙표

| 접두사 | 용도 | 예시 |
|--------|------|------|
| `find` / `get` | 조회 | `findById()`, `getOrderCount()` |
| `create` / `save` | 생성 | `createOrder()`, `save()` |
| `update` / `modify` | 수정 | `updateStatus()`, `modifyAddress()` |
| `delete` / `remove` | 삭제 | `deleteOrder()`, `removeItem()` |
| `is` / `has` / `can` | boolean 반환 | `isActive()`, `hasPermission()` |
| `to` | 변환 | `toString()`, `toResponse()` |
| `from` | 정적 팩토리 | `from()`, `fromEntity()` |
| `of` | 정적 팩토리 | `of()`, `ofNullable()` |
| `validate` / `check` | 검증 | `validateInput()`, `checkPermission()` |
| `calculate` / `compute` | 계산 | `calculateTotal()`, `computeHash()` |

### 3.4 변수명

**[필수]** 변수 이름은 **lowerCamelCase**를 사용한다.

```java
// ✅ 올바른 예
int orderCount;
String customerName;
List<Order> pendingOrders;
Map<String, Integer> itemQuantityMap;

// ❌ 잘못된 예
int OrderCount;       // 대문자 시작
int order_count;      // 언더스코어
int oc;               // 의미 없는 축약
int n;                // 루프 인덱스 외에는 단일 문자 지양
```

**[필수]** 컬렉션 변수에는 복수형을 사용한다.

```java
// ✅ 올바른 예
List<Order> orders;
Set<String> tags;
Map<Long, User> usersById;

// ❌ 잘못된 예
List<Order> orderList;   // 타입에 이미 List라는 정보가 있음
Set<String> tagSet;      // 불필요한 접미사
```

**예외**: `Map`의 경우 키 정보를 포함하는 이름이 유용할 수 있다.

```java
// ✅ Map은 키 기준 접미사 허용
Map<Long, User> usersById;
Map<String, List<Order>> ordersByStatus;
```

### 3.5 상수명

**[필수]** 상수(`static final` 필드)는 **UPPER_SNAKE_CASE**를 사용한다.

```java
// ✅ 올바른 예
public static final int MAX_RETRY_COUNT = 3;
public static final String DEFAULT_ENCODING = "UTF-8";
public static final Duration CACHE_TTL = Duration.ofMinutes(30);
private static final Logger log = LoggerFactory.getLogger(OrderService.class);

// ❌ 잘못된 예
public static final int maxRetryCount = 3;     // camelCase
public static final int MAX_RETRY = 3;          // 모호한 이름
public static final int MAXRETRYCOUNT = 3;      // 언더스코어 누락
```

**참고**: `Logger`는 관례상 `log` (소문자)로 선언하는 것도 널리 통용된다. Lombok의 `@Slf4j`를 사용하면 자동으로 `log` 필드가 생성된다.

### 3.6 제네릭 타입 파라미터

**[필수]** 제네릭 타입 파라미터는 대문자 한 글자를 사용한다.

| 타입 | 의미 | 사용 예 |
|------|------|---------|
| `T` | Type (일반 타입) | `List<T>`, `Optional<T>` |
| `E` | Element (컬렉션 요소) | `Collection<E>` |
| `K` | Key (맵 키) | `Map<K, V>` |
| `V` | Value (맵 값) | `Map<K, V>` |
| `N` | Number (숫자) | `Comparable<N>` |
| `R` | Return/Result (반환) | `Function<T, R>` |
| `S`, `U` | 두 번째, 세 번째 타입 | `BiFunction<T, U, R>` |

```java
// ✅ 올바른 예
public interface Repository<T, ID> {
    Optional<T> findById(ID id);
    List<T> findAll();
}

public class ApiResponse<T> {
    private final T data;
    private final String message;
    // ...
}

// ❌ 잘못된 예
public interface Repository<Entity, Identifier> {  // 너무 장황함
    Optional<Entity> findById(Identifier id);
}
```

### 3.7 Boolean 변수 및 메서드

**[권장]** Boolean 변수는 `is`, `has`, `can`, `should`, `was` 등의 접두사를 사용한다.

```java
// ✅ 올바른 예 - 변수
boolean isActive;
boolean hasPermission;
boolean canAccess;
boolean shouldRetry;

// ✅ 올바른 예 - 메서드
public boolean isValid() { ... }
public boolean hasRole(String role) { ... }
public boolean canExecute() { ... }

// ❌ 잘못된 예
boolean active;           // is 접두사 없음 (필드로는 사용 가능하나 메서드명에서 혼동)
boolean checkPermission;  // 동사형이지만 boolean 의미 불명확
public boolean valid() { ... }  // is 접두사 없으면 의미가 모호
```

**주의**: JPA Entity나 DTO의 필드에서는 `is` 접두사 없이 사용하는 것이 관례이다. 직렬화/역직렬화 시 `is` 접두사가 문제를 일으킬 수 있다.

```java
// ✅ Entity 필드에서는 is 없이
@Entity
public class Member {
    private boolean active;     // getter: isActive()
    private boolean deleted;    // getter: isDeleted()
}
```

### 3.8 Builder 패턴 명명

**[권장]** Builder 패턴 사용 시 다음 규칙을 따른다.

```java
// ✅ 올바른 예: 내부 Builder 클래스
public class Order {
    private final Long id;
    private final String orderNumber;
    private final OrderStatus status;

    private Order(Builder builder) {
        this.id = builder.id;
        this.orderNumber = builder.orderNumber;
        this.status = builder.status;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String orderNumber;
        private OrderStatus status;

        // 메서드명은 필드명과 동일하게
        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder orderNumber(String orderNumber) {
            this.orderNumber = orderNumber;
            return this;
        }

        public Builder status(OrderStatus status) {
            this.status = status;
            return this;
        }

        public Order build() {
            return new Order(this);
        }
    }
}

// 사용
Order order = Order.builder()
        .id(1L)
        .orderNumber("ORD-001")
        .status(OrderStatus.PENDING)
        .build();
```

### 3.9 열거형(Enum) 명명

**[필수]** 열거형 이름은 UpperCamelCase, 열거 상수는 UPPER_SNAKE_CASE를 사용한다.

```java
// ✅ 올바른 예
public enum OrderStatus {
    PENDING,
    CONFIRMED,
    SHIPPED,
    DELIVERED,
    CANCELLED;
}

public enum HttpMethod {
    GET, POST, PUT, DELETE, PATCH;
}

// ❌ 잘못된 예
public enum orderStatus { ... }        // 소문자 시작
public enum ORDER_STATUS { ... }       // UPPER_SNAKE_CASE
public enum OrderStatus {
    pending, confirmed, shipped;       // 상수에 소문자
}
```

---

## 4. 포맷팅 (Formatting)

### 4.1 들여쓰기

**[필수]** 들여쓰기는 **공백 4칸**을 사용한다. 탭(tab) 문자는 사용하지 않는다.

```java
// ✅ 올바른 예 (공백 4칸)
public class OrderService {
    private final OrderRepository orderRepository;

    public Order findById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
    }
}
```

### 4.2 줄 길이

**[권장]** 한 줄의 최대 길이는 **120자**로 제한한다.

- 120자를 넘는 줄은 적절한 위치에서 줄바꿈한다.
- 줄바꿈 시 다음 줄은 원래 줄보다 **8칸** 들여쓰거나, 인자 정렬에 맞춘다.

```java
// ✅ 줄바꿈 예시 - 메서드 호출
List<OrderResponse> responses = orders.stream()
        .filter(order -> order.getStatus() == OrderStatus.CONFIRMED)
        .map(OrderMapper::toResponse)
        .sorted(Comparator.comparing(OrderResponse::createdAt).reversed())
        .toList();

// ✅ 줄바꿈 예시 - 메서드 선언
public ResponseEntity<ApiResponse<OrderResponse>> createOrder(
        @Valid @RequestBody OrderCreateRequest request,
        @AuthenticationPrincipal UserDetails userDetails) {
    // ...
}

// ✅ 줄바꿈 예시 - 조건문
if (order.getStatus() == OrderStatus.CONFIRMED
        && order.getPayment().isCompleted()
        && order.getDelivery().isReady()) {
    processShipment(order);
}
```

### 4.3 중괄호 (Braces)

**[필수]** 중괄호는 **K&R 스타일(Kernighan and Ritchie style)**을 사용한다. 여는 중괄호는 같은 줄에, 닫는 중괄호는 새 줄에 위치한다.

```java
// ✅ K&R 스타일 (올바른 예)
public class OrderService {

    public void processOrder(Order order) {
        if (order.isValid()) {
            execute(order);
        } else {
            reject(order);
        }

        for (OrderItem item : order.getItems()) {
            validateItem(item);
        }

        try {
            save(order);
        } catch (DataAccessException e) {
            log.error("주문 저장 실패: {}", order.getId(), e);
            throw new OrderProcessingException("주문 저장에 실패했습니다.", e);
        }
    }
}

// ❌ Allman 스타일 (사용하지 않음)
public class OrderService
{
    public void processOrder(Order order)
    {
        if (order.isValid())
        {
            execute(order);
        }
    }
}
```

**[필수]** 한 줄짜리 if/for/while 문에도 중괄호를 사용한다.

```java
// ✅ 올바른 예
if (order == null) {
    throw new IllegalArgumentException("order must not be null");
}

// ❌ 잘못된 예 - 중괄호 생략
if (order == null)
    throw new IllegalArgumentException("order must not be null");

// ❌ 잘못된 예 - 한 줄에 작성
if (order == null) throw new IllegalArgumentException("order must not be null");
```

### 4.4 공백 규칙

**[필수]** 연산자 양쪽에 공백을 넣는다.

```java
// ✅ 올바른 예
int total = price * quantity + shippingFee;
boolean isValid = age >= 18 && hasConsent;
String message = "Hello, " + name + "!";

// ❌ 잘못된 예
int total=price*quantity+shippingFee;
boolean isValid=age>=18&&hasConsent;
```

**[필수]** 제어문 키워드 뒤에 공백을 넣는다.

```java
// ✅ 올바른 예
if (condition) { ... }
for (int i = 0; i < size; i++) { ... }
while (hasNext()) { ... }
switch (status) { ... }
try { ... } catch (Exception e) { ... }

// ❌ 잘못된 예
if(condition) { ... }
for(int i=0; i<size; i++) { ... }
while(hasNext()) { ... }
```

**[필수]** 메서드명과 여는 괄호 사이에는 공백을 넣지 않는다.

```java
// ✅ 올바른 예
processOrder(order);
findById(id);

// ❌ 잘못된 예
processOrder (order);
findById (id);
```

**[필수]** 쉼표(,) 뒤에 공백을 넣는다.

```java
// ✅ 올바른 예
public Order create(String name, int quantity, BigDecimal price) { ... }
List.of("apple", "banana", "cherry");

// ❌ 잘못된 예
public Order create(String name,int quantity,BigDecimal price) { ... }
List.of("apple","banana","cherry");
```

**[필수]** 캐스팅 후 공백을 넣는다.

```java
// ✅ 올바른 예
OrderService service = (OrderService) bean;

// ❌ 잘못된 예
OrderService service = (OrderService)bean;
```

### 4.5 빈 줄 사용 규칙

**[필수]** 다음 위치에 빈 줄을 하나 삽입한다.

```java
package com.example.myapp.order.service;
                                          // <- package 선언 후 빈 줄
import java.util.List;
import java.util.Optional;
                                          // <- import 그룹 사이 빈 줄
import org.springframework.stereotype.Service;
                                          // <- import 선언 후 빈 줄
@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
                                          // <- 필드와 생성자 사이 빈 줄
    public OrderService(OrderRepository orderRepository, OrderMapper orderMapper) {
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
    }
                                          // <- 메서드 사이 빈 줄
    public OrderResponse findById(Long id) {
        return orderRepository.findById(id)
                .map(orderMapper::toResponse)
                .orElseThrow(() -> new OrderNotFoundException(id));
    }
                                          // <- 메서드 사이 빈 줄
    public List<OrderResponse> findAll() {
        return orderRepository.findAll().stream()
                .map(orderMapper::toResponse)
                .toList();
    }
}
```

**[필수]** 연속으로 2개 이상의 빈 줄을 사용하지 않는다.

### 4.6 어노테이션 포맷팅

**[필수]** 클래스, 메서드, 필드에 붙는 어노테이션은 한 줄에 하나씩 작성한다.

```java
// ✅ 올바른 예
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderService {

    @Override
    public String toString() { ... }

    @GetMapping("/orders/{id}")
    @ResponseStatus(HttpStatus.OK)
    public OrderResponse findById(@PathVariable Long id) { ... }
}

// ❌ 잘못된 예
@Service @Transactional(readOnly = true) @RequiredArgsConstructor
public class OrderService { ... }
```

**[선택]** 파라미터에 붙는 어노테이션은 같은 줄에 작성할 수 있다.

```java
// ✅ 허용
public OrderResponse findById(@PathVariable Long id) { ... }
public void createOrder(@Valid @RequestBody OrderCreateRequest request) { ... }
```

### 4.7 람다 표현식 포맷팅

```java
// ✅ 짧은 람다: 한 줄로
orders.forEach(order -> process(order));

// ✅ 메서드 참조가 가능하면 메서드 참조 사용
orders.forEach(this::process);

// ✅ 긴 람다: 블록 사용
orders.stream()
        .filter(order -> {
            boolean isConfirmed = order.getStatus() == OrderStatus.CONFIRMED;
            boolean isPaid = order.getPayment().isCompleted();
            return isConfirmed && isPaid;
        })
        .toList();

// ❌ 잘못된 예 - 너무 긴 람다를 한 줄에
orders.stream().filter(order -> order.getStatus() == OrderStatus.CONFIRMED && order.getPayment().isCompleted() && order.getDelivery().isReady()).toList();
```

---

## 5. import 규칙

### 5.1 와일드카드 import 금지

**[필수]** 와일드카드(`*`) import를 사용하지 않는다.

```java
// ✅ 올바른 예 - 명시적 import
import java.util.List;
import java.util.Map;
import java.util.Optional;

// ❌ 잘못된 예 - 와일드카드 import
import java.util.*;
```

이유:
- 어떤 클래스를 사용하는지 명확하지 않다.
- 이름 충돌(name collision) 가능성이 높아진다.
- IDE가 자동으로 관리하므로 번거롭지 않다.

### 5.2 import 순서

**[필수]** import는 다음 순서로 그룹화하며, 그룹 사이에 빈 줄을 하나 넣는다.

```java
// 1. 정적(static) import
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

// 2. java.* 패키지
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

// 3. javax.* / jakarta.* 패키지
import jakarta.persistence.Entity;
import jakarta.validation.Valid;

// 4. 서드파티 라이브러리
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// 5. 프로젝트 내부 패키지
import com.example.myapp.order.domain.Order;
import com.example.myapp.order.dto.OrderResponse;
```

**[필수]** 각 그룹 내에서는 알파벳 순으로 정렬한다.

### 5.3 사용하지 않는 import 제거

**[필수]** 사용하지 않는 import 문은 반드시 제거한다.

IntelliJ IDEA 설정:
- `Settings > Editor > General > Auto Import > Optimize imports on the fly` 체크
- 저장 시 자동 정리: `Settings > Tools > Actions on Save > Optimize imports` 체크

### 5.4 정적 import 사용 기준

**[권장]** 정적 import는 다음 경우에만 사용한다.

```java
// ✅ 테스트 코드에서 AssertJ, Mockito
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

// ✅ 상수 참조가 빈번할 때
import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableList;

// ❌ 지나친 정적 import - 출처가 불분명해짐
import static com.example.myapp.common.util.StringUtils.*;
import static com.example.myapp.order.domain.OrderStatus.*;
```

---

## 6. 클래스 구성 순서

### 6.1 멤버 순서

**[권장]** 클래스 내부 멤버는 다음 순서로 배치한다.

```java
public class OrderService {

    // 1. 정적 상수 (static final)
    private static final Logger log = LoggerFactory.getLogger(OrderService.class);
    private static final int MAX_RETRY = 3;

    // 2. 정적 변수 (static)
    private static int instanceCount;

    // 3. 인스턴스 필드
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final EventPublisher eventPublisher;

    // 4. 생성자
    public OrderService(OrderRepository orderRepository,
                        OrderMapper orderMapper,
                        EventPublisher eventPublisher) {
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
        this.eventPublisher = eventPublisher;
    }

    // 5. 정적 팩토리 메서드
    public static OrderService createDefault() { ... }

    // 6. public 메서드 (비즈니스 로직)
    public OrderResponse createOrder(OrderCreateRequest request) { ... }
    public OrderResponse findById(Long id) { ... }
    public List<OrderResponse> findAll() { ... }

    // 7. protected 메서드
    protected void validate(Order order) { ... }

    // 8. package-private 메서드
    void publishEvent(OrderEvent event) { ... }

    // 9. private 메서드
    private Order toEntity(OrderCreateRequest request) { ... }
    private void notifyAdmin(Order order) { ... }

    // 10. equals, hashCode, toString
    @Override
    public boolean equals(Object o) { ... }

    @Override
    public int hashCode() { ... }

    @Override
    public String toString() { ... }

    // 11. 내부 클래스 / 내부 열거형
    private enum InternalStatus {
        PROCESSING, COMPLETED
    }
}
```

### 6.2 접근 제한자 순서

**[권장]** 같은 카테고리의 멤버 내에서는 접근 제한자에 따라 정렬한다.

```
public -> protected -> package-private(default) -> private
```

### 6.3 관련 메서드 그룹화

**[권장]** 호출하는 메서드와 호출되는 메서드를 가까이 배치한다.

```java
// ✅ 올바른 예 - 호출 순서대로 배치
public OrderResponse createOrder(OrderCreateRequest request) {
    validate(request);
    Order order = toEntity(request);
    Order saved = save(order);
    publishEvent(saved);
    return toResponse(saved);
}

private void validate(OrderCreateRequest request) { ... }

private Order toEntity(OrderCreateRequest request) { ... }

private Order save(Order order) { ... }

private void publishEvent(Order order) { ... }

private OrderResponse toResponse(Order order) { ... }
```

### 6.4 필드 선언 시 접근 제한자와 수정자 순서

**[필수]** 접근 제한자와 수정자(modifier)는 다음 순서로 작성한다.

```java
// 순서: 접근제한자 -> static -> final -> transient -> volatile
public static final int MAX_SIZE = 100;
private static int count;
private final String name;
private transient int cachedHash;
private volatile boolean running;
```

---

## 7. 주석과 Javadoc

### 7.1 Javadoc 작성 대상

**[필수]** 다음 대상에는 Javadoc을 작성한다.

- 모든 `public` 클래스와 인터페이스
- 모든 `public` 및 `protected` 메서드
- 의미가 자명하지 않은 `public` 필드

**[선택]** 다음 경우에는 Javadoc을 생략할 수 있다.

- `getter`, `setter` (의미가 자명한 경우)
- `@Override` 메서드 (상위 타입의 Javadoc으로 충분한 경우)
- 테스트 메서드 (`@DisplayName`으로 대체)

### 7.2 Javadoc 형식

```java
// ✅ 올바른 Javadoc 예시
/**
 * 주문을 생성하고 결제를 처리한다.
 *
 * <p>주문 생성 후 재고 확인, 결제 처리, 이벤트 발행 순으로 진행된다.
 * 재고가 부족하거나 결제가 실패하면 주문이 취소된다.</p>
 *
 * @param request 주문 생성 요청 DTO (null 불가)
 * @return 생성된 주문 응답 DTO
 * @throws InsufficientStockException 재고가 부족한 경우
 * @throws PaymentFailedException 결제 처리에 실패한 경우
 * @since 1.0
 * @see OrderCreateRequest
 */
public OrderResponse createOrder(OrderCreateRequest request) {
    // ...
}
```

```java
// ✅ 클래스 Javadoc 예시
/**
 * 주문 관련 비즈니스 로직을 처리하는 서비스.
 *
 * <p>주문의 생성, 조회, 수정, 삭제 기능을 제공하며,
 * 결제 서비스 및 재고 서비스와 연동한다.</p>
 *
 * @author 개발팀
 * @since 1.0
 */
@Service
@Transactional(readOnly = true)
public class OrderService {
    // ...
}
```

### 7.3 Javadoc 태그 순서

**[권장]** Javadoc 태그는 다음 순서로 작성한다.

```
@param      - 파라미터 설명 (선언 순서대로)
@return     - 반환값 설명
@throws     - 예외 설명 (알파벳순 또는 발생 가능성순)
@see        - 참조
@since      - 도입 버전
@deprecated - 폐기 안내
```

### 7.4 인라인 주석

**[권장]** 인라인 주석은 "무엇(what)"이 아닌 **"왜(why)"**를 설명한다.

```java
// ✅ 올바른 예 - "왜"를 설명
// 결제 실패 시 즉시 재시도하면 동일 에러가 발생하므로 3초 대기
Thread.sleep(3000);

// 배송지 주소가 변경되면 기존 배송 추적 정보를 무효화해야 함
delivery.invalidateTracking();

// DB 인덱스가 (status, created_at) 순이므로 status 조건을 먼저 걸어야 함
List<Order> orders = orderRepository.findByStatusAndCreatedAtAfter(status, since);
```

```java
// ❌ 잘못된 예 - "무엇"을 설명 (코드를 읽으면 알 수 있음)
// 주문 목록을 가져온다
List<Order> orders = orderRepository.findAll();

// i를 1 증가시킨다
i++;

// null 체크
if (order != null) { ... }
```

### 7.5 TODO / FIXME 규칙

**[권장]** `TODO`와 `FIXME` 주석에는 담당자와 이슈 번호를 명시한다.

```java
// ✅ 올바른 예
// TODO(ywlee): 대량 주문 시 배치 처리 구현 필요 (#234)
// FIXME(ywlee): 동시성 이슈로 간헐적 데이터 불일치 발생 (#567)

// ❌ 잘못된 예
// TODO: 나중에 수정
// FIXME: 이거 고쳐야 함
```

### 7.6 주석 달지 말아야 할 것

**[필수]** 다음에는 주석을 달지 않는다.

```java
// ❌ 주석 처리된 코드 - 삭제하라. 버전 관리 도구가 있다.
// public void oldMethod() {
//     // 이전 로직
// }

// ❌ 닫는 중괄호에 주석 - 메서드가 너무 길다는 신호
public void process() {
    // 수백 줄의 코드...
} // end of process   <- 이런 주석이 필요하면 메서드를 분리하라

// ❌ 변경 이력 주석 - Git 로그를 사용하라
// 2026-03-01 ywlee: 할인 로직 추가
// 2026-02-15 ywlee: 초기 구현
```

---

## 8. 프로그래밍 관행 (Programming Practices)

### 8.1 @Override 항상 사용

**[필수]** 상위 타입의 메서드를 재정의할 때 `@Override`를 반드시 붙인다.

```java
// ✅ 올바른 예
@Override
public boolean equals(Object o) { ... }

@Override
public int hashCode() { ... }

@Override
public String toString() { ... }

// ✅ 인터페이스 구현에도 붙인다
@Override
public OrderResponse findById(Long id) { ... }

// ❌ 잘못된 예 - @Override 누락
public boolean equals(Object o) { ... }  // 오타 시 새 메서드가 됨
```

### 8.2 예외 무시하지 않기

**[필수]** catch 블록을 비워두지 않는다.

```java
// ✅ 올바른 예 - 예외 처리
try {
    orderRepository.save(order);
} catch (DataAccessException e) {
    log.error("주문 저장 실패: orderId={}", order.getId(), e);
    throw new OrderSaveException("주문 저장에 실패했습니다.", e);
}

// ✅ 정말로 무시해야 하는 경우 - 이유를 명시
try {
    Thread.sleep(100);
} catch (InterruptedException e) {
    // 인터럽트 상태를 복원하고 현재 작업을 종료한다
    Thread.currentThread().interrupt();
}

// ❌ 잘못된 예 - 빈 catch 블록
try {
    orderRepository.save(order);
} catch (DataAccessException e) {
    // 아무것도 하지 않음 - 버그의 원인이 됨
}

// ❌ 잘못된 예 - 단순히 출력만
try {
    orderRepository.save(order);
} catch (DataAccessException e) {
    e.printStackTrace();  // 프로덕션 코드에서 사용 금지
}
```

### 8.3 switch default case

**[필수]** `switch` 문에는 `default` case를 포함한다. (sealed class 패턴 매칭 제외)

```java
// ✅ 올바른 예
switch (order.getStatus()) {
    case PENDING -> processNewOrder(order);
    case CONFIRMED -> processConfirmedOrder(order);
    case SHIPPED -> trackShipment(order);
    case DELIVERED -> completeOrder(order);
    case CANCELLED -> handleCancellation(order);
    default -> throw new IllegalStateException(
            "지원하지 않는 주문 상태: " + order.getStatus());
}

// ✅ Java 21 패턴 매칭 - sealed class는 default 불필요
sealed interface Shape permits Circle, Rectangle {}
record Circle(double radius) implements Shape {}
record Rectangle(double w, double h) implements Shape {}

String describe(Shape shape) {
    return switch (shape) {
        case Circle c -> "원: 반지름=" + c.radius();
        case Rectangle r -> "사각형: " + r.w() + "x" + r.h();
        // default 불필요 - 컴파일러가 완전성(exhaustiveness) 검사
    };
}
```

### 8.4 equals / hashCode 함께 오버라이드

**[필수]** `equals()`를 재정의하면 반드시 `hashCode()`도 함께 재정의한다.

```java
// ✅ 올바른 예
public class OrderId {
    private final String value;

    public OrderId(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OrderId orderId)) return false;
        return Objects.equals(value, orderId.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}

// ✅ 더 나은 방법: record 사용 (equals, hashCode, toString 자동 생성)
public record OrderId(String value) {}
```

```java
// ❌ 잘못된 예 - hashCode 없이 equals만 재정의
public class OrderId {
    private final String value;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OrderId orderId)) return false;
        return Objects.equals(value, orderId.value);
    }
    // hashCode() 누락 -> HashMap, HashSet에서 오동작
}
```

### 8.5 불변 객체 선호 (record 활용)

**[권장]** 데이터 전달 객체는 가능한 한 불변(immutable)으로 만든다. Java 16+에서는 `record`를 활용한다.

```java
// ✅ 올바른 예 - record로 DTO 정의
public record OrderCreateRequest(
        @NotBlank String productName,
        @Positive int quantity,
        @NotNull BigDecimal price
) {}

public record OrderResponse(
        Long id,
        String orderNumber,
        String productName,
        int quantity,
        BigDecimal totalPrice,
        OrderStatus status,
        LocalDateTime createdAt
) {
    // 정적 팩토리 메서드
    public static OrderResponse from(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getOrderNumber(),
                order.getProductName(),
                order.getQuantity(),
                order.getTotalPrice(),
                order.getStatus(),
                order.getCreatedAt()
        );
    }
}
```

```java
// ✅ 불변 클래스 (record를 사용할 수 없는 경우)
public final class Money {
    private final BigDecimal amount;
    private final Currency currency;

    public Money(BigDecimal amount, Currency currency) {
        this.amount = Objects.requireNonNull(amount, "amount must not be null");
        this.currency = Objects.requireNonNull(currency, "currency must not be null");
    }

    public Money add(Money other) {
        if (!this.currency.equals(other.currency)) {
            throw new IllegalArgumentException("통화가 다릅니다.");
        }
        return new Money(this.amount.add(other.amount), this.currency);
    }

    // getter만 제공, setter 없음
    public BigDecimal amount() { return amount; }
    public Currency currency() { return currency; }
}
```

```java
// ❌ 잘못된 예 - 가변 DTO
public class OrderCreateRequest {
    private String productName;
    private int quantity;

    public void setProductName(String productName) {
        this.productName = productName;  // 어디서든 변경 가능 - 위험
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
```

### 8.6 Optional 올바르게 사용하기

**[필수]** `Optional`은 메서드의 반환 타입으로만 사용한다. 필드, 파라미터, 컬렉션 원소로 사용하지 않는다.

```java
// ✅ 올바른 예 - 반환 타입으로 사용
public Optional<Order> findById(Long id) {
    return orderRepository.findById(id);
}

// ✅ 올바른 예 - Optional 처리
// 방법 1: orElseThrow
Order order = orderRepository.findById(id)
        .orElseThrow(() -> new OrderNotFoundException(id));

// 방법 2: orElse (기본값 제공)
String name = findUserName(id).orElse("Unknown");

// 방법 3: ifPresent
findById(id).ifPresent(order -> {
    order.cancel();
    orderRepository.save(order);
});

// 방법 4: map으로 변환
Optional<String> orderNumber = findById(id)
        .map(Order::getOrderNumber);
```

```java
// ❌ 잘못된 예
// 필드에 Optional 사용
private Optional<String> middleName;  // 그냥 null 허용 필드로

// 파라미터에 Optional 사용
public void process(Optional<String> name) { ... }  // null을 전달하면 NPE

// Optional.get() 직접 호출
Order order = findById(id).get();  // NoSuchElementException 위험

// isPresent() + get() 조합
Optional<Order> optional = findById(id);
if (optional.isPresent()) {
    Order order = optional.get();  // orElseThrow로 대체
}
```

### 8.7 Stream vs for-loop 선택 기준

**[권장]** 다음 기준에 따라 Stream과 for-loop을 선택한다.

```java
// ✅ Stream이 적합한 경우: 변환, 필터링, 집계

// 변환 (map)
List<OrderResponse> responses = orders.stream()
        .map(OrderResponse::from)
        .toList();

// 필터링 (filter)
List<Order> confirmedOrders = orders.stream()
        .filter(order -> order.getStatus() == OrderStatus.CONFIRMED)
        .toList();

// 집계 (reduce, collect)
BigDecimal totalPrice = orders.stream()
        .map(Order::getTotalPrice)
        .reduce(BigDecimal.ZERO, BigDecimal::add);

// 그룹화
Map<OrderStatus, List<Order>> ordersByStatus = orders.stream()
        .collect(Collectors.groupingBy(Order::getStatus));
```

```java
// ✅ for-loop이 적합한 경우: 부수 효과(side effect), 복잡한 제어 흐름

// 부수 효과가 있는 경우
for (Order order : orders) {
    order.updateStatus(OrderStatus.CANCELLED);
    orderRepository.save(order);
    eventPublisher.publish(new OrderCancelledEvent(order));
}

// break, continue 등 제어 흐름이 필요한 경우
for (Order order : orders) {
    if (order.isExpired()) {
        continue;
    }
    if (order.getTotalPrice().compareTo(limit) > 0) {
        break;
    }
    process(order);
}

// 인덱스가 필요한 경우
for (int i = 0; i < items.size(); i++) {
    items.get(i).setSequence(i + 1);
}
```

```java
// ❌ 잘못된 예 - Stream 남용
orders.stream().forEach(order -> {         // stream() 불필요
    order.cancel();
    orderRepository.save(order);
});
// -> orders.forEach(...) 또는 for-loop 사용
```

### 8.8 var 사용 가이드라인 (Java 10+)

**[권장]** `var`는 타입이 명확한 경우에만 사용한다.

```java
// ✅ 올바른 예 - 우변에서 타입이 명확
var orders = new ArrayList<Order>();                        // ArrayList<Order>
var response = new OrderResponse(1L, "ORD-001", ...);      // OrderResponse
var entry = Map.entry("key", "value");                     // Map.Entry<String, String>
var path = Path.of("/tmp/data.txt");                       // Path

// ✅ for 루프에서 사용
for (var order : orders) {
    process(order);
}

// ✅ try-with-resources에서 사용
try (var reader = new BufferedReader(new FileReader(file))) {
    // ...
}
```

```java
// ❌ 잘못된 예 - 타입이 불분명
var result = process(data);           // result의 타입이 뭔지 알 수 없음
var x = calculateSomething();         // 모호한 변수명 + var = 최악
var data = service.getData();         // 반환 타입을 파악하려면 메서드를 봐야 함

// ❌ 잘못된 예 - 리터럴과 함께
var count = 0;                        // int? long? -> 명시적으로 작성
var price = 19.99;                    // double? float? BigDecimal?
```

### 8.9 sealed class 활용 (Java 17+)

**[권장]** 한정된 계층 구조를 표현할 때 `sealed` 클래스를 사용한다.

```java
// ✅ 올바른 예 - 결제 수단 모델링
public sealed interface PaymentMethod
        permits CreditCard, BankTransfer, VirtualAccount {

    String getDescription();
    BigDecimal getFee();
}

public record CreditCard(
        String cardNumber,
        String expiryDate,
        BigDecimal fee
) implements PaymentMethod {

    @Override
    public String getDescription() {
        return "신용카드 (" + cardNumber.substring(cardNumber.length() - 4) + ")";
    }

    @Override
    public BigDecimal getFee() { return fee; }
}

public record BankTransfer(
        String bankName,
        String accountNumber
) implements PaymentMethod {

    @Override
    public String getDescription() {
        return bankName + " 계좌이체";
    }

    @Override
    public BigDecimal getFee() { return BigDecimal.ZERO; }
}

public record VirtualAccount(
        String bankName,
        String accountNumber,
        LocalDateTime expiresAt
) implements PaymentMethod {

    @Override
    public String getDescription() {
        return bankName + " 가상계좌";
    }

    @Override
    public BigDecimal getFee() { return BigDecimal.ZERO; }
}
```

```java
// ✅ sealed class와 패턴 매칭 조합 (Java 21)
public String processPayment(PaymentMethod method) {
    return switch (method) {
        case CreditCard card ->
                "카드 결제: " + card.cardNumber();
        case BankTransfer transfer ->
                "계좌이체: " + transfer.bankName();
        case VirtualAccount account ->
                "가상계좌: " + account.accountNumber() +
                " (만료: " + account.expiresAt() + ")";
    };
}
```

### 8.10 패턴 매칭 (Java 21)

**[권장]** instanceof 검사와 캐스팅을 패턴 매칭으로 대체한다.

```java
// ✅ 올바른 예 - instanceof 패턴 매칭 (Java 16+)
if (obj instanceof String s) {
    System.out.println(s.length());
}

if (obj instanceof Order order && order.isValid()) {
    process(order);
}

// ❌ 잘못된 예 - 전통적인 instanceof + 캐스팅
if (obj instanceof String) {
    String s = (String) obj;
    System.out.println(s.length());
}
```

```java
// ✅ 올바른 예 - switch 패턴 매칭 (Java 21)
public String format(Object obj) {
    return switch (obj) {
        case Integer i -> "정수: %d".formatted(i);
        case Long l -> "큰 정수: %d".formatted(l);
        case Double d -> "실수: %.2f".formatted(d);
        case String s -> "문자열: %s".formatted(s);
        case null -> "null";
        default -> "기타: " + obj.toString();
    };
}

// ✅ guard 패턴 (when 절)
public String classifyAge(Object obj) {
    return switch (obj) {
        case Integer age when age < 0 -> "잘못된 나이";
        case Integer age when age < 13 -> "어린이";
        case Integer age when age < 20 -> "청소년";
        case Integer age -> "성인 (" + age + "세)";
        default -> "나이 정보 없음";
    };
}
```

```java
// ✅ record 패턴 (Java 21)
record Point(int x, int y) {}
record Line(Point start, Point end) {}

public double length(Object shape) {
    return switch (shape) {
        case Line(Point(var x1, var y1), Point(var x2, var y2)) ->
                Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
        default -> 0.0;
    };
}
```

### 8.11 문자열 처리

**[필수]** 문자열 비교는 `equals()`를 사용한다. `==`를 사용하지 않는다.

```java
// ✅ 올바른 예
if ("CONFIRMED".equals(status)) { ... }
if (name.equals(otherName)) { ... }
if (name.equalsIgnoreCase(otherName)) { ... }

// ❌ 잘못된 예
if (status == "CONFIRMED") { ... }     // 참조 비교, 내용 비교 아님
```

**[권장]** 문자열 연결이 많은 경우 Text Block이나 `formatted()`를 사용한다.

```java
// ✅ Text Block (Java 15+)
String query = """
        SELECT o.id, o.order_number, o.status
        FROM orders o
        WHERE o.status = :status
          AND o.created_at > :since
        ORDER BY o.created_at DESC
        """;

// ✅ String.formatted() (Java 15+)
String message = "주문 #%s이(가) %s 상태로 변경되었습니다."
        .formatted(orderNumber, newStatus);

// ❌ 잘못된 예 - 복잡한 문자열 연결
String query = "SELECT o.id, o.order_number, o.status " +
               "FROM orders o " +
               "WHERE o.status = :status " +
               "AND o.created_at > :since " +
               "ORDER BY o.created_at DESC";
```

### 8.12 Null 처리

**[권장]** null을 방어적으로 처리한다.

```java
// ✅ 올바른 예 - Objects.requireNonNull (생성자, 메서드 초입)
public OrderService(OrderRepository orderRepository) {
    this.orderRepository = Objects.requireNonNull(orderRepository,
            "orderRepository must not be null");
}

// ✅ 올바른 예 - Optional 반환
public Optional<Order> findByOrderNumber(String orderNumber) {
    return orderRepository.findByOrderNumber(orderNumber);
}

// ✅ 올바른 예 - 빈 컬렉션 반환 (null 대신)
public List<Order> findByStatus(OrderStatus status) {
    List<Order> orders = orderRepository.findByStatus(status);
    return orders != null ? orders : Collections.emptyList();
}

// ❌ 잘못된 예 - null 반환
public List<Order> findByStatus(OrderStatus status) {
    // ...
    return null;  // 호출자가 NPE에 노출됨
}
```

### 8.13 리소스 관리

**[필수]** `AutoCloseable` 리소스는 try-with-resources로 관리한다.

```java
// ✅ 올바른 예
try (var connection = dataSource.getConnection();
     var statement = connection.prepareStatement(sql);
     var resultSet = statement.executeQuery()) {

    while (resultSet.next()) {
        // ...
    }
}

// ❌ 잘못된 예 - 수동 close
Connection connection = null;
try {
    connection = dataSource.getConnection();
    // ...
} finally {
    if (connection != null) {
        connection.close();  // 예외 발생 가능
    }
}
```

### 8.14 숫자와 날짜 처리

```java
// ✅ 금액은 반드시 BigDecimal 사용
BigDecimal price = new BigDecimal("19900");
BigDecimal tax = price.multiply(new BigDecimal("0.1"));
// BigDecimal 비교는 compareTo 사용
if (price.compareTo(BigDecimal.ZERO) > 0) { ... }

// ❌ float/double로 금액 처리
double price = 19900.0;  // 부동소수점 오차 발생

// ✅ 날짜/시간은 java.time API 사용
LocalDate today = LocalDate.now();
LocalDateTime now = LocalDateTime.now();
Instant timestamp = Instant.now();
Duration timeout = Duration.ofSeconds(30);

// ❌ java.util.Date 사용
Date now = new Date();  // 레거시, 가변 객체
```

---

## 9. Spring Boot 코딩 규칙

### 9.1 스테레오타입 어노테이션 사용 기준

**[필수]** 각 계층에 맞는 스테레오타입 어노테이션을 사용한다.

| 어노테이션 | 용도 | 설명 |
|-----------|------|------|
| `@Controller` | 웹 컨트롤러 (뷰 반환) | 템플릿 엔진과 함께 사용 |
| `@RestController` | REST API 컨트롤러 | `@Controller` + `@ResponseBody` |
| `@Service` | 비즈니스 로직 | 트랜잭션 경계 설정 |
| `@Repository` | 데이터 접근 | 예외 변환(DataAccessException) |
| `@Component` | 기타 빈 | 위 세 가지에 해당하지 않는 경우 |
| `@Configuration` | 설정 클래스 | `@Bean` 메서드 포함 |

```java
// ❌ 잘못된 예 - 모든 곳에 @Component 사용
@Component  // @Service를 써야 함
public class OrderService { ... }

@Component  // @Repository를 써야 함
public class OrderRepositoryImpl { ... }
```

### 9.2 생성자 주입 권장

**[필수]** 의존성 주입은 **생성자 주입(Constructor Injection)**을 사용한다.

```java
// ✅ 올바른 예 - 생성자 주입 (Lombok 사용)
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final PaymentService paymentService;

    // Lombok이 생성자를 자동 생성
}

// ✅ 올바른 예 - 생성자 주입 (Lombok 미사용)
@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    // 생성자가 하나일 때는 @Autowired 생략 가능
    public OrderService(OrderRepository orderRepository,
                        OrderMapper orderMapper) {
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
    }
}
```

```java
// ❌ 잘못된 예 - 필드 주입
@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;  // 테스트하기 어려움

    @Autowired
    private OrderMapper orderMapper;
}

// ❌ 잘못된 예 - setter 주입
@Service
public class OrderService {

    private OrderRepository orderRepository;

    @Autowired
    public void setOrderRepository(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }
}
```

생성자 주입의 장점:
- **불변성 보장**: `final` 필드로 선언 가능
- **필수 의존성 명시**: 생성자 파라미터에 누락 시 컴파일 에러
- **테스트 용이**: `new`로 직접 생성하여 테스트 가능
- **순환 의존성 감지**: 애플리케이션 시작 시 즉시 발견

### 9.3 DTO vs Entity 분리

**[필수]** API 요청/응답에는 Entity를 직접 노출하지 않고, 별도 DTO를 사용한다.

```java
// ✅ 올바른 예 - Entity
@Entity
@Table(name = "orders")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String orderNumber;

    @Column(nullable = false)
    private String productName;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    private LocalDateTime createdAt;

    // 비즈니스 메서드
    public void confirm() {
        if (this.status != OrderStatus.PENDING) {
            throw new IllegalStateException("PENDING 상태에서만 확인 가능합니다.");
        }
        this.status = OrderStatus.CONFIRMED;
    }
}

// ✅ 요청 DTO
public record OrderCreateRequest(
        @NotBlank(message = "상품명은 필수입니다")
        String productName,

        @Min(value = 1, message = "수량은 1 이상이어야 합니다")
        int quantity,

        @NotNull(message = "가격은 필수입니다")
        @DecimalMin(value = "0.01", message = "가격은 0보다 커야 합니다")
        BigDecimal price
) {}

// ✅ 응답 DTO
public record OrderResponse(
        Long id,
        String orderNumber,
        String productName,
        int quantity,
        BigDecimal totalPrice,
        String status,
        LocalDateTime createdAt
) {
    public static OrderResponse from(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getOrderNumber(),
                order.getProductName(),
                order.getQuantity(),
                order.getTotalPrice(),
                order.getStatus().name(),
                order.getCreatedAt()
        );
    }
}
```

```java
// ❌ 잘못된 예 - Entity를 직접 API 응답으로 사용
@GetMapping("/orders/{id}")
public Order findById(@PathVariable Long id) {
    return orderRepository.findById(id).orElseThrow();
    // 문제점:
    // 1. 불필요한 내부 필드 노출
    // 2. 지연 로딩 시 LazyInitializationException
    // 3. 순환 참조 시 무한 재귀 (Jackson)
    // 4. Entity 변경이 API 스펙에 직접 영향
}
```

### 9.4 @Transactional 사용 규칙

**[필수]** 트랜잭션 관리는 다음 규칙을 따른다.

```java
// ✅ 올바른 예
@Service
@Transactional(readOnly = true)  // 클래스 레벨: 기본 읽기 전용
public class OrderService {

    private final OrderRepository orderRepository;

    // 조회 메서드: 클래스 레벨 readOnly=true 상속
    public OrderResponse findById(Long id) {
        return orderRepository.findById(id)
                .map(OrderResponse::from)
                .orElseThrow(() -> new OrderNotFoundException(id));
    }

    public List<OrderResponse> findAll() {
        return orderRepository.findAll().stream()
                .map(OrderResponse::from)
                .toList();
    }

    // 변경 메서드: 메서드 레벨에서 readOnly 해제
    @Transactional
    public OrderResponse createOrder(OrderCreateRequest request) {
        Order order = Order.create(request);
        Order saved = orderRepository.save(order);
        return OrderResponse.from(saved);
    }

    @Transactional
    public void cancelOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
        order.cancel();
        // 더티 체킹으로 자동 업데이트
    }
}
```

**@Transactional 사용 시 주의사항**:

```java
// ❌ 잘못된 예 - 같은 클래스 내부 호출 (프록시 우회)
@Service
public class OrderService {

    public void processAll() {
        List<Order> orders = findPendingOrders();
        for (Order order : orders) {
            processOrder(order);  // 내부 호출이므로 @Transactional 무시됨!
        }
    }

    @Transactional
    public void processOrder(Order order) {
        order.confirm();
        orderRepository.save(order);
    }
}

// ✅ 해결방법: 별도 서비스로 분리
@Service
@RequiredArgsConstructor
public class OrderBatchService {

    private final OrderProcessService orderProcessService;

    public void processAll() {
        List<Order> orders = findPendingOrders();
        for (Order order : orders) {
            orderProcessService.processOrder(order);  // 외부 호출로 트랜잭션 적용
        }
    }
}

@Service
public class OrderProcessService {

    @Transactional
    public void processOrder(Order order) {
        order.confirm();
        orderRepository.save(order);
    }
}
```

```java
// ❌ 잘못된 예 - 트랜잭션 안에서 외부 API 호출
@Transactional
public void createOrderAndNotify(OrderCreateRequest request) {
    Order order = orderRepository.save(Order.create(request));
    externalApiClient.sendNotification(order);  // 외부 호출 중 타임아웃 시 DB 커넥션 점유
}

// ✅ 해결방법: 이벤트 기반으로 분리
@Transactional
public Order createOrder(OrderCreateRequest request) {
    Order order = orderRepository.save(Order.create(request));
    applicationEventPublisher.publishEvent(new OrderCreatedEvent(order));
    return order;
}

@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
public void handleOrderCreated(OrderCreatedEvent event) {
    externalApiClient.sendNotification(event.order());
}
```

### 9.5 REST API 명명 규칙

**[필수]** REST API 설계 시 다음 규칙을 따른다.

```java
// ✅ 올바른 예 - RESTful API
@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // GET /api/v1/orders - 목록 조회
    @GetMapping
    public ResponseEntity<List<OrderResponse>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(orderService.findAll(page, size));
    }

    // GET /api/v1/orders/{id} - 단건 조회
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.findById(id));
    }

    // POST /api/v1/orders - 생성
    @PostMapping
    public ResponseEntity<OrderResponse> create(
            @Valid @RequestBody OrderCreateRequest request) {
        OrderResponse response = orderService.createOrder(request);
        URI location = URI.create("/api/v1/orders/" + response.id());
        return ResponseEntity.created(location).body(response);
    }

    // PUT /api/v1/orders/{id} - 전체 수정
    @PutMapping("/{id}")
    public ResponseEntity<OrderResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody OrderUpdateRequest request) {
        return ResponseEntity.ok(orderService.updateOrder(id, request));
    }

    // PATCH /api/v1/orders/{id}/status - 부분 수정
    @PatchMapping("/{id}/status")
    public ResponseEntity<OrderResponse> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody OrderStatusUpdateRequest request) {
        return ResponseEntity.ok(orderService.updateStatus(id, request));
    }

    // DELETE /api/v1/orders/{id} - 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }

    // GET /api/v1/orders/{id}/items - 하위 리소스 조회
    @GetMapping("/{id}/items")
    public ResponseEntity<List<OrderItemResponse>> findItems(
            @PathVariable Long id) {
        return ResponseEntity.ok(orderService.findItemsByOrderId(id));
    }
}
```

**REST API URL 규칙**:

| 규칙 | 올바른 예 | 잘못된 예 |
|------|----------|----------|
| 소문자 사용 | `/api/v1/orders` | `/api/v1/Orders` |
| 복수형 명사 | `/orders` | `/order` |
| 하이픈 사용 (필요 시) | `/order-items` | `/orderItems`, `/order_items` |
| 동사 사용 금지 | `POST /orders` | `POST /createOrder` |
| 버전 포함 | `/api/v1/orders` | `/orders` |

### 9.6 예외 처리 구조

**[권장]** 전역 예외 처리기와 커스텀 예외를 활용한다.

```java
// ✅ 비즈니스 예외 기본 클래스
public abstract class BusinessException extends RuntimeException {

    private final ErrorCode errorCode;

    protected BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    protected BusinessException(ErrorCode errorCode, String detail) {
        super(detail);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}

// ✅ 구체적인 비즈니스 예외
public class OrderNotFoundException extends BusinessException {

    public OrderNotFoundException(Long id) {
        super(ErrorCode.ORDER_NOT_FOUND,
              "주문을 찾을 수 없습니다. id=" + id);
    }
}

// ✅ 에러 코드 정의
public enum ErrorCode {

    // 공통 에러
    INVALID_INPUT("C001", "잘못된 입력입니다."),
    INTERNAL_ERROR("C002", "서버 내부 오류가 발생했습니다."),

    // 주문 관련 에러
    ORDER_NOT_FOUND("O001", "주문을 찾을 수 없습니다."),
    INSUFFICIENT_STOCK("O002", "재고가 부족합니다."),
    INVALID_ORDER_STATUS("O003", "유효하지 않은 주문 상태입니다.");

    private final String code;
    private final String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() { return code; }
    public String getMessage() { return message; }
}

// ✅ 에러 응답 DTO
public record ErrorResponse(
        String code,
        String message,
        LocalDateTime timestamp
) {
    public static ErrorResponse of(ErrorCode errorCode, String message) {
        return new ErrorResponse(errorCode.getCode(), message, LocalDateTime.now());
    }
}

// ✅ 전역 예외 처리기
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException e) {
        log.warn("비즈니스 예외 발생: {}", e.getMessage());
        ErrorCode errorCode = e.getErrorCode();
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.of(errorCode, e.getMessage()));
    }

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleOrderNotFound(OrderNotFoundException e) {
        log.warn("주문 조회 실패: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ErrorResponse.of(e.getErrorCode(), e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        log.warn("입력값 검증 실패: {}", message);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.of(ErrorCode.INVALID_INPUT, message));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        log.error("예기치 않은 오류 발생", e);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.of(ErrorCode.INTERNAL_ERROR, "서버 오류가 발생했습니다."));
    }
}
```

### 9.7 application.yml 구성 가이드

**[권장]** 설정 파일은 다음과 같이 구성한다.

```yaml
# ✅ 올바른 예 - application.yml
spring:
  application:
    name: my-order-service

  # 데이터베이스 설정
  datasource:
    url: jdbc:postgresql://localhost:5432/mydb
    username: ${DB_USERNAME:myuser}          # 환경변수 + 기본값
    password: ${DB_PASSWORD:mypassword}
    hikari:
      maximum-pool-size: 10
      minimum-idle: 5
      connection-timeout: 30000

  # JPA 설정
  jpa:
    hibernate:
      ddl-auto: validate                     # 프로덕션에서는 validate 또는 none
    open-in-view: false                      # OSIV 비활성화 권장
    properties:
      hibernate:
        format_sql: true
        default_batch_fetch_size: 100

  # Jackson 설정
  jackson:
    property-naming-strategy: SNAKE_CASE
    default-property-inclusion: non_null
    serialization:
      write-dates-as-timestamps: false       # ISO-8601 형식

# 서버 설정
server:
  port: 8080
  shutdown: graceful

# 로깅 설정
logging:
  level:
    root: INFO
    com.example.myapp: DEBUG
    org.hibernate.SQL: DEBUG

# 커스텀 설정 (타입 안전 프로퍼티)
app:
  order:
    max-items-per-order: 50
    default-currency: KRW
  notification:
    enabled: true
    retry-count: 3
    retry-delay: PT5S                        # ISO-8601 Duration
```

```java
// ✅ 타입 안전 설정 바인딩
@ConfigurationProperties(prefix = "app.order")
public record OrderProperties(
        int maxItemsPerOrder,
        String defaultCurrency
) {}

@ConfigurationProperties(prefix = "app.notification")
public record NotificationProperties(
        boolean enabled,
        int retryCount,
        Duration retryDelay
) {}

// Application 클래스에서 활성화
@SpringBootApplication
@ConfigurationPropertiesScan
public class MyApplication { ... }
```

**설정 파일 분리**:

```
resources/
├── application.yml              # 공통 설정
├── application-local.yml        # 로컬 개발 설정
├── application-dev.yml          # 개발 서버 설정
├── application-staging.yml      # 스테이징 설정
└── application-prod.yml         # 프로덕션 설정
```

---

## 10. 테스트 코딩 규칙

### 10.1 테스트 클래스 / 메서드 명명

**[필수]** 테스트 클래스는 `{대상클래스}Test` 형태로 명명한다.

```java
// ✅ 단위 테스트 클래스
class OrderServiceTest { ... }
class OrderMapperTest { ... }

// ✅ 통합 테스트 클래스
class OrderControllerIntegrationTest { ... }
class OrderRepositoryTest { ... }  // @DataJpaTest
```

**[권장]** 테스트 메서드는 `should_결과_when_조건` 패턴으로 명명한다.

```java
// ✅ 올바른 예 - should_xxx_when_xxx 패턴
@Test
void should_create_order_when_valid_request() { ... }

@Test
void should_throw_exception_when_order_not_found() { ... }

@Test
void should_return_empty_list_when_no_orders_exist() { ... }

// ✅ 올바른 예 - 한국어 DisplayName과 함께
@Test
@DisplayName("유효한 요청으로 주문을 생성한다")
void should_create_order_when_valid_request() { ... }

@Test
@DisplayName("존재하지 않는 주문 ID로 조회하면 예외가 발생한다")
void should_throw_exception_when_order_not_found() { ... }
```

```java
// ❌ 잘못된 예
@Test
void test1() { ... }                    // 의미 없는 이름

@Test
void testCreateOrder() { ... }          // test 접두사 불필요

@Test
void createOrderSuccess() { ... }       // 조건이 명확하지 않음
```

### 10.2 Given-When-Then 패턴

**[필수]** 테스트 메서드 본문은 Given-When-Then 패턴으로 구성한다.

```java
// ✅ 올바른 예
@Test
@DisplayName("유효한 요청으로 주문을 생성한다")
void should_create_order_when_valid_request() {
    // given - 테스트 데이터와 사전 조건 설정
    var request = new OrderCreateRequest("노트북", 2, new BigDecimal("1500000"));
    var expectedOrder = Order.create(request);
    given(orderRepository.save(any(Order.class))).willReturn(expectedOrder);

    // when - 테스트 대상 실행
    OrderResponse response = orderService.createOrder(request);

    // then - 결과 검증
    assertThat(response).isNotNull();
    assertThat(response.productName()).isEqualTo("노트북");
    assertThat(response.quantity()).isEqualTo(2);
    assertThat(response.status()).isEqualTo("PENDING");

    verify(orderRepository).save(any(Order.class));
}

@Test
@DisplayName("존재하지 않는 주문 ID로 조회하면 OrderNotFoundException이 발생한다")
void should_throw_exception_when_order_not_found() {
    // given
    Long nonExistentId = 999L;
    given(orderRepository.findById(nonExistentId)).willReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> orderService.findById(nonExistentId))
            .isInstanceOf(OrderNotFoundException.class)
            .hasMessageContaining("999");
}
```

### 10.3 @DisplayName 활용

**[필수]** 모든 테스트 메서드에 `@DisplayName`을 작성한다. 한국어로 작성하여 비즈니스 요구사항과 연결한다.

```java
// ✅ 올바른 예 - @Nested로 그룹화
@Nested
@DisplayName("주문 생성")
class CreateOrder {

    @Test
    @DisplayName("유효한 요청으로 주문을 생성하면 PENDING 상태로 저장된다")
    void should_save_with_pending_status() { ... }

    @Test
    @DisplayName("상품명이 비어있으면 검증 예외가 발생한다")
    void should_throw_when_product_name_is_blank() { ... }

    @Test
    @DisplayName("수량이 0 이하이면 검증 예외가 발생한다")
    void should_throw_when_quantity_is_not_positive() { ... }
}

@Nested
@DisplayName("주문 취소")
class CancelOrder {

    @Test
    @DisplayName("PENDING 상태의 주문을 취소할 수 있다")
    void should_cancel_when_pending() { ... }

    @Test
    @DisplayName("이미 배송 중인 주문은 취소할 수 없다")
    void should_not_cancel_when_shipped() { ... }
}
```

### 10.4 테스트 격리 원칙

**[필수]** 각 테스트는 독립적으로 실행 가능해야 한다.

```java
// ❌ 잘못된 예 - 테스트 간 상태 공유
class OrderServiceTest {

    private static Order sharedOrder;  // static 상태 공유

    @Test
    void createOrder() {
        sharedOrder = orderService.createOrder(request);  // 다른 테스트가 의존
    }

    @Test
    void findCreatedOrder() {
        Order found = orderService.findById(sharedOrder.getId());  // createOrder에 의존
    }
}
```

```java
// ✅ 올바른 예 - 각 테스트가 독립적
class OrderServiceTest {

    @BeforeEach
    void setUp() {
        // 각 테스트 전에 깨끗한 상태 설정
    }

    @Test
    void should_find_order_by_id() {
        // given - 이 테스트에 필요한 데이터를 직접 생성
        Order order = createTestOrder("ORD-001");
        given(orderRepository.findById(1L)).willReturn(Optional.of(order));

        // when
        OrderResponse response = orderService.findById(1L);

        // then
        assertThat(response.orderNumber()).isEqualTo("ORD-001");
    }

    private Order createTestOrder(String orderNumber) {
        return Order.builder()
                .orderNumber(orderNumber)
                .productName("테스트 상품")
                .quantity(1)
                .totalPrice(new BigDecimal("10000"))
                .status(OrderStatus.PENDING)
                .build();
    }
}
```

### 10.5 테스트 종류별 구성

```java
// ✅ 단위 테스트 (Unit Test)
@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @InjectMocks
    private OrderService orderService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderMapper orderMapper;

    @Test
    @DisplayName("주문을 ID로 조회한다")
    void should_find_order_by_id() { ... }
}
```

```java
// ✅ 슬라이스 테스트 - @WebMvcTest (컨트롤러)
@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean  // Spring Boot 3.4부터는 @MockitoBean을 사용한다
    private OrderService orderService;

    @Test
    @DisplayName("GET /api/v1/orders/{id} - 주문 조회 성공")
    void should_return_order() throws Exception {
        // given
        var response = new OrderResponse(1L, "ORD-001", "노트북",
                2, new BigDecimal("3000000"), "CONFIRMED", LocalDateTime.now());
        given(orderService.findById(1L)).willReturn(response);

        // when & then
        mockMvc.perform(get("/api/v1/orders/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderNumber").value("ORD-001"))
                .andExpect(jsonPath("$.productName").value("노트북"));
    }
}
```

```java
// ✅ 슬라이스 테스트 - @DataJpaTest (리포지토리)
@DataJpaTest
class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("주문 상태로 주문 목록을 조회한다")
    void should_find_orders_by_status() {
        // given
        Order order1 = createOrder("ORD-001", OrderStatus.CONFIRMED);
        Order order2 = createOrder("ORD-002", OrderStatus.CONFIRMED);
        Order order3 = createOrder("ORD-003", OrderStatus.PENDING);
        entityManager.persist(order1);
        entityManager.persist(order2);
        entityManager.persist(order3);
        entityManager.flush();

        // when
        List<Order> confirmedOrders =
                orderRepository.findByStatus(OrderStatus.CONFIRMED);

        // then
        assertThat(confirmedOrders).hasSize(2);
        assertThat(confirmedOrders)
                .extracting(Order::getOrderNumber)
                .containsExactlyInAnyOrder("ORD-001", "ORD-002");
    }
}
```

```java
// ✅ 통합 테스트 - @SpringBootTest
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OrderIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    @DisplayName("주문 생성부터 조회까지 전체 흐름을 테스트한다")
    void should_create_and_find_order() {
        // given
        var request = new OrderCreateRequest("노트북", 1, new BigDecimal("1500000"));

        // when - 주문 생성
        ResponseEntity<OrderResponse> createResponse = restTemplate.postForEntity(
                "/api/v1/orders", request, OrderResponse.class);

        // then
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(createResponse.getBody()).isNotNull();

        Long orderId = createResponse.getBody().id();

        // when - 주문 조회
        ResponseEntity<OrderResponse> findResponse = restTemplate.getForEntity(
                "/api/v1/orders/" + orderId, OrderResponse.class);

        // then
        assertThat(findResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(findResponse.getBody().productName()).isEqualTo("노트북");
    }
}
```

### 10.6 AssertJ 사용 권장

**[권장]** 단언(assertion)에는 AssertJ를 사용한다.

```java
// ✅ AssertJ (권장)
assertThat(order.getStatus()).isEqualTo(OrderStatus.CONFIRMED);
assertThat(orders).hasSize(3);
assertThat(orders).extracting(Order::getStatus)
        .containsOnly(OrderStatus.CONFIRMED);
assertThat(response.totalPrice())
        .isEqualByComparingTo(new BigDecimal("3000000"));
assertThatThrownBy(() -> service.findById(999L))
        .isInstanceOf(OrderNotFoundException.class)
        .hasMessageContaining("999");

// ❌ JUnit의 기본 assertion (가독성 낮음)
assertEquals(OrderStatus.CONFIRMED, order.getStatus());
assertEquals(3, orders.size());
assertThrows(OrderNotFoundException.class, () -> service.findById(999L));
```

---

## 11. 안티패턴과 피해야 할 것들

### 11.1 God Class (신 클래스)

하나의 클래스가 너무 많은 책임을 가지는 안티패턴이다.

```java
// ❌ God Class - 너무 많은 책임
public class OrderManager {
    public Order createOrder() { ... }
    public Order findOrder() { ... }
    public void processPayment() { ... }
    public void refundPayment() { ... }
    public void checkStock() { ... }
    public void deductStock() { ... }
    public void sendEmail() { ... }
    public void sendSms() { ... }
    public byte[] generateReport() { ... }
    // 수백 줄의 코드...
}
```

```java
// ✅ 단일 책임 원칙(SRP)에 따라 분리
@Service
public class OrderService { ... }       // 주문 비즈니스 로직만

@Service
public class PaymentService { ... }     // 결제 처리만

@Service
public class StockService { ... }       // 재고 관리만

@Service
public class NotificationService { ... } // 알림 발송만

@Service
public class ReportService { ... }      // 리포트 생성만
```

### 11.2 Feature Envy (기능 편향)

다른 클래스의 데이터를 과도하게 사용하는 안티패턴이다.

```java
// ❌ Feature Envy - Order의 데이터를 외부에서 조합
public class OrderPriceCalculator {

    public BigDecimal calculateTotal(Order order) {
        BigDecimal subtotal = order.getPrice()
                .multiply(BigDecimal.valueOf(order.getQuantity()));
        BigDecimal discount = order.getCoupon() != null
                ? order.getCoupon().getDiscountAmount()
                : BigDecimal.ZERO;
        BigDecimal shipping = order.getAddress().isRemote()
                ? new BigDecimal("5000")
                : new BigDecimal("2500");
        return subtotal.subtract(discount).add(shipping);
    }
}
```

```java
// ✅ 로직을 데이터가 있는 곳으로 이동
public class Order {

    public BigDecimal calculateTotal() {
        BigDecimal subtotal = this.price.multiply(BigDecimal.valueOf(this.quantity));
        BigDecimal discount = calculateDiscount();
        BigDecimal shipping = this.address.calculateShippingFee();
        return subtotal.subtract(discount).add(shipping);
    }

    private BigDecimal calculateDiscount() {
        return this.coupon != null
                ? this.coupon.getDiscountAmount()
                : BigDecimal.ZERO;
    }
}

public class Address {
    public BigDecimal calculateShippingFee() {
        return this.isRemote()
                ? new BigDecimal("5000")
                : new BigDecimal("2500");
    }
}
```

### 11.3 Primitive Obsession (원시 타입 집착)

의미 있는 도메인 개념을 원시 타입으로만 표현하는 안티패턴이다.

```java
// ❌ Primitive Obsession
public class Order {
    private String orderNumber;       // "ORD-20260317-001" 형식 검증 없음
    private String phoneNumber;       // "010-1234-5678" 형식 검증 없음
    private String email;             // 이메일 형식 검증 없음
    private int quantity;             // 음수도 가능
    private double price;             // 금액에 double 사용
}
```

```java
// ✅ Value Object로 감싸기
public record OrderNumber(String value) {
    public OrderNumber {
        if (value == null || !value.matches("ORD-\\d{8}-\\d{3}")) {
            throw new IllegalArgumentException("잘못된 주문번호 형식: " + value);
        }
    }
}

public record PhoneNumber(String value) {
    public PhoneNumber {
        if (value == null || !value.matches("\\d{3}-\\d{4}-\\d{4}")) {
            throw new IllegalArgumentException("잘못된 전화번호 형식: " + value);
        }
    }
}

public record Email(String value) {
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    public Email {
        if (value == null || !EMAIL_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException("잘못된 이메일 형식: " + value);
        }
    }
}

public record Money(BigDecimal amount, Currency currency) {
    public Money {
        Objects.requireNonNull(amount, "금액은 null일 수 없습니다");
        Objects.requireNonNull(currency, "통화는 null일 수 없습니다");
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("금액은 음수일 수 없습니다: " + amount);
        }
    }
}
```

### 11.4 Magic Numbers (매직 넘버)

의미가 불분명한 리터럴 값을 코드에 직접 사용하는 안티패턴이다.

```java
// ❌ Magic Numbers
public void processOrder(Order order) {
    if (order.getItems().size() > 50) {       // 50이 뭔가?
        throw new TooManyItemsException();
    }
    if (order.getTotalPrice().compareTo(new BigDecimal("1000000")) > 0) {
        requireApproval(order);                // 100만이 뭔가?
    }
    Thread.sleep(3000);                       // 왜 3초?
}
```

```java
// ✅ 상수 또는 설정으로 추출
public class OrderPolicy {
    public static final int MAX_ITEMS_PER_ORDER = 50;
    public static final BigDecimal APPROVAL_THRESHOLD = new BigDecimal("1000000");
    public static final Duration PROCESSING_DELAY = Duration.ofSeconds(3);
}

public void processOrder(Order order) {
    if (order.getItems().size() > OrderPolicy.MAX_ITEMS_PER_ORDER) {
        throw new TooManyItemsException();
    }
    if (order.getTotalPrice().compareTo(OrderPolicy.APPROVAL_THRESHOLD) > 0) {
        requireApproval(order);
    }
    Thread.sleep(OrderPolicy.PROCESSING_DELAY.toMillis());
}
```

### 11.5 Deep Nesting (깊은 중첩)

조건문이 깊게 중첩되어 가독성을 해치는 안티패턴이다.

```java
// ❌ Deep Nesting - 화살표 코드(Arrow Anti-pattern)
public void processOrder(Order order) {
    if (order != null) {
        if (order.isValid()) {
            if (order.getItems() != null && !order.getItems().isEmpty()) {
                for (OrderItem item : order.getItems()) {
                    if (item.getQuantity() > 0) {
                        if (stockService.isAvailable(item)) {
                            processItem(item);
                        }
                    }
                }
            }
        }
    }
}
```

```java
// ✅ Guard Clause (보호 구문)으로 중첩 제거
public void processOrder(Order order) {
    Objects.requireNonNull(order, "order must not be null");

    if (!order.isValid()) {
        throw new InvalidOrderException();
    }

    if (order.getItems() == null || order.getItems().isEmpty()) {
        throw new EmptyOrderException();
    }

    for (OrderItem item : order.getItems()) {
        validateItem(item);
        processItem(item);
    }
}

private void validateItem(OrderItem item) {
    if (item.getQuantity() <= 0) {
        throw new InvalidQuantityException();
    }
    if (!stockService.isAvailable(item)) {
        throw new OutOfStockException();
    }
}
```

### 11.6 Checked Exception 남용

불필요한 Checked Exception이 호출자에게 부담을 주는 안티패턴이다.

```java
// ❌ Checked Exception 남용
public class OrderService {

    public Order createOrder(OrderRequest request)
            throws OrderValidationException, StockException, PaymentException {
        validate(request);   // throws OrderValidationException
        checkStock(request); // throws StockException
        pay(request);        // throws PaymentException
        return save(request);
    }
}

// 호출자 코드가 복잡해짐
try {
    orderService.createOrder(request);
} catch (OrderValidationException e) {
    // ...
} catch (StockException e) {
    // ...
} catch (PaymentException e) {
    // ...
}
```

```java
// ✅ RuntimeException 기반 비즈니스 예외 사용
public abstract class BusinessException extends RuntimeException {
    private final ErrorCode errorCode;

    protected BusinessException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}

// 서비스 코드가 깔끔해짐
public class OrderService {
    public Order createOrder(OrderRequest request) {
        validate(request);    // 실패 시 OrderValidationException (RuntimeException)
        checkStock(request);  // 실패 시 InsufficientStockException
        pay(request);         // 실패 시 PaymentFailedException
        return save(request);
    }
}

// 전역 예외 처리기가 일괄 처리
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handle(BusinessException e) {
        return ResponseEntity.badRequest()
                .body(ErrorResponse.of(e.getErrorCode(), e.getMessage()));
    }
}
```

### 11.7 기타 피해야 할 패턴

```java
// ❌ 유틸리티 클래스의 public 생성자
public class StringUtils {
    public static String capitalize(String s) { ... }
    // 인스턴스 생성 가능하지만 의미 없음
}

// ✅ 유틸리티 클래스는 인스턴스 생성 방지
public final class StringUtils {
    private StringUtils() {
        throw new AssertionError("인스턴스 생성 불가");
    }

    public static String capitalize(String s) { ... }
}

// ❌ System.out.println 사용
System.out.println("주문 처리 완료");

// ✅ Logger 사용
log.info("주문 처리 완료: orderId={}", orderId);

// ❌ 과도한 상속 - 상속보다 조합(composition)을 선호
// ElectronicsOrder → DiscountableOrder → PricedOrder → Order (깊은 상속 계층)
public class ElectronicsOrder extends DiscountableOrder { ... }

// ✅ 조합 사용
public class Order {
    private final DiscountPolicy discountPolicy;
    private final ShippingPolicy shippingPolicy;
}
```

---

## 12. 도구와 자동화

### 12.1 Checkstyle

Checkstyle은 Java 코드의 스타일 규칙 준수 여부를 자동으로 검사하는 도구이다.

**Gradle 설정**:

```groovy
// build.gradle
plugins {
    id 'checkstyle'
}

checkstyle {
    toolVersion = '10.14.0'
    configFile = file("${rootDir}/config/checkstyle/checkstyle.xml")
    maxWarnings = 0
}

tasks.withType(Checkstyle) {
    reports {
        xml.required = false
        html.required = true
    }
}
```

**Checkstyle 설정 파일 예시** (`config/checkstyle/checkstyle.xml`):

```xml
<?xml version="1.0"?>
<!DOCTYPE module PUBLIC
    "-//Checkstyle//DTD Checkstyle Configuration 1.3//EN"
    "https://checkstyle.org/dtds/configuration_1_3.dtd">

<module name="Checker">
    <property name="charset" value="UTF-8"/>
    <property name="severity" value="warning"/>

    <module name="FileTabCharacter"/>

    <module name="TreeWalker">
        <!-- 명명 규칙 -->
        <module name="PackageName">
            <property name="format" value="^[a-z]+(\.[a-z][a-z0-9]*)*$"/>
        </module>
        <module name="TypeName"/>
        <module name="MethodName"/>
        <module name="ConstantName"/>
        <module name="MemberName"/>
        <module name="ParameterName"/>

        <!-- import 규칙 -->
        <module name="AvoidStarImport"/>
        <module name="UnusedImports"/>
        <module name="RedundantImport"/>

        <!-- 줄 길이 -->
        <module name="LineLength">
            <property name="max" value="120"/>
            <property name="ignorePattern"
                      value="^package.*|^import.*|a href|href|http://|https://"/>
        </module>

        <!-- 중괄호 규칙 -->
        <module name="NeedBraces"/>
        <module name="LeftCurly"/>
        <module name="RightCurly"/>

        <!-- 공백 규칙 -->
        <module name="WhitespaceAround"/>
        <module name="GenericWhitespace"/>

        <!-- @Override 필수 -->
        <module name="MissingOverride"/>

        <!-- equals와 hashCode 함께 -->
        <module name="EqualsHashCode"/>
    </module>
</module>
```

### 12.2 SpotBugs

SpotBugs는 바이트코드를 분석하여 잠재적 버그를 찾아내는 정적 분석 도구이다.

```groovy
// build.gradle
plugins {
    id 'com.github.spotbugs' version '6.0.7'
}

spotbugs {
    toolVersion = '4.8.3'
    effort = 'max'
    reportLevel = 'medium'
    excludeFilter = file("${rootDir}/config/spotbugs/exclude.xml")
}

tasks.withType(com.github.spotbugs.snom.SpotBugsTask) {
    reports {
        xml.required = false
        html.required = true
    }
}
```

**SpotBugs가 탐지하는 주요 문제**:

| 카테고리 | 설명 | 예시 |
|---------|------|------|
| NP (Null Pointer) | null 참조 위험 | null 가능 값 역참조 |
| EQ (Equals) | equals/hashCode 문제 | hashCode 없이 equals만 재정의 |
| SE (Serialization) | 직렬화 문제 | Serializable 구현 누락 |
| DMI (Dubious Method Invocation) | 의심스러운 메서드 호출 | Random 재생성 등 |

### 12.3 SonarLint

SonarLint는 IDE 플러그인으로, 코딩하는 중 실시간으로 코드 품질 이슈를 알려준다.

**IntelliJ에서 설치**:
1. `Settings > Plugins > Marketplace` 에서 "SonarLint" 검색
2. 설치 후 IDE 재시작
3. `Settings > Tools > SonarLint` 에서 규칙 커스터마이징

**SonarLint가 잡아주는 주요 이슈**:
- 코드 스멜 (Code Smell): 유지보수 어려운 코드
- 버그 (Bug): 잘못된 동작을 초래하는 코드
- 보안 취약점 (Vulnerability): 보안 문제가 될 수 있는 코드
- 보안 핫스팟 (Security Hotspot): 수동 검토가 필요한 코드

### 12.4 IntelliJ IDEA Formatter 설정

**[권장]** 팀 전체가 동일한 포맷터 설정을 공유한다.

**Code Style 내보내기/가져오기**:
1. `Settings > Editor > Code Style > Java`
2. 상단 톱니바퀴 > `Export` > IntelliJ IDEA code style XML
3. 프로젝트 루트의 `.idea/codeStyles/` 디렉토리에 저장
4. Git에 커밋하여 팀과 공유

**주요 설정 항목**:

```
[Tabs and Indents]
- Use tab character: false (체크 해제)
- Tab size: 4
- Indent: 4
- Continuation indent: 8

[Wrapping and Braces]
- Hard wrap at: 120
- Braces placement > Class/Method declaration: End of line
- Braces placement > Other: End of line
- Force braces > if/for/while/do...while: Always

[Imports]
- Class count to use import with '*': 999 (사실상 비활성화)
- Names count to use static import with '*': 999
- Import Layout:
  - import static all other imports
  - <blank line>
  - import java.*
  - import javax.*
  - <blank line>
  - import all other imports
  - <blank line>
  - import com.example.*
```

**저장 시 자동 포맷팅**:
- `Settings > Tools > Actions on Save`
  - `Reformat code` 체크
  - `Optimize imports` 체크

### 12.5 EditorConfig

**[필수]** 프로젝트 루트에 `.editorconfig` 파일을 설정한다.

```ini
# .editorconfig
root = true

# 전체 파일 기본 설정
[*]
charset = utf-8
end_of_line = lf
indent_style = space
indent_size = 4
insert_final_newline = true
trim_trailing_whitespace = true

# Java 파일
[*.java]
indent_size = 4
max_line_length = 120

# Gradle 파일
[*.gradle]
indent_size = 4

# Kotlin 파일 (Gradle Kotlin DSL)
[*.kts]
indent_size = 4

# YAML 파일
[*.{yml,yaml}]
indent_size = 2

# JSON 파일
[*.json]
indent_size = 2

# XML 파일
[*.xml]
indent_size = 4

# Properties 파일
[*.properties]
charset = utf-8

# Markdown 파일 (후행 공백 유지 - 줄바꿈 의미)
[*.md]
trim_trailing_whitespace = false

# Makefile (탭 필수)
[Makefile]
indent_style = tab
```

### 12.6 Git Hooks를 활용한 자동 검사

```bash
#!/bin/sh
# .git/hooks/pre-commit

echo "Running Checkstyle..."
./gradlew checkstyleMain --daemon --quiet
if [ $? -ne 0 ]; then
    echo "Checkstyle 검사에 실패했습니다. 코드 스타일을 확인하세요."
    exit 1
fi

echo "Running SpotBugs..."
./gradlew spotbugsMain --daemon --quiet
if [ $? -ne 0 ]; then
    echo "SpotBugs 검사에 실패했습니다. 잠재적 버그를 확인하세요."
    exit 1
fi

echo "Running tests..."
./gradlew test --daemon --quiet
if [ $? -ne 0 ]; then
    echo "테스트에 실패했습니다."
    exit 1
fi

echo "모든 검사를 통과했습니다."
```

### 12.7 CI/CD 파이프라인 통합

```yaml
# .github/workflows/code-quality.yml
name: Code Quality

on:
  pull_request:
    branches: [ main, develop ]

jobs:
  quality-check:
    runs-on: ubuntu-latest

    steps:
      - uses: actions/checkout@v4

      - name: Set up JDK 21
        uses: actions/setup-java@v4
        with:
          java-version: '21'
          distribution: 'temurin'

      - name: Grant execute permission for gradlew
        run: chmod +x gradlew

      - name: Run Checkstyle
        run: ./gradlew checkstyleMain checkstyleTest

      - name: Run SpotBugs
        run: ./gradlew spotbugsMain

      - name: Run Tests
        run: ./gradlew test

      - name: Upload Test Results
        if: always()
        uses: actions/upload-artifact@v4
        with:
          name: test-results
          path: build/reports/tests/
```

---

## 부록

### A. 빠른 참조 요약표

| 대상 | 명명 규칙 | 예시 |
|------|----------|------|
| 패키지 | all lowercase, 역 도메인 | `com.example.myapp.order` |
| 클래스/인터페이스 | UpperCamelCase | `OrderService` |
| 메서드 | lowerCamelCase, 동사 시작 | `findById()` |
| 변수 | lowerCamelCase | `orderCount` |
| 상수 | UPPER_SNAKE_CASE | `MAX_RETRY_COUNT` |
| 제네릭 타입 | 대문자 한 글자 | `T`, `E`, `K`, `V` |
| 열거 상수 | UPPER_SNAKE_CASE | `CONFIRMED` |

### B. IntelliJ 유용한 단축키

| 기능 | macOS | Windows/Linux |
|------|-------|---------------|
| 코드 포맷팅 | `Cmd + Option + L` | `Ctrl + Alt + L` |
| import 정리 | `Ctrl + Option + O` | `Ctrl + Alt + O` |
| Javadoc 생성 | `/** + Enter` | `/** + Enter` |
| 변수 추출 | `Cmd + Option + V` | `Ctrl + Alt + V` |
| 메서드 추출 | `Cmd + Option + M` | `Ctrl + Alt + M` |
| 상수 추출 | `Cmd + Option + C` | `Ctrl + Alt + C` |
| 리팩토링 메뉴 | `Ctrl + T` | `Ctrl + Alt + Shift + T` |

### C. Lombok 사용 가이드

**[권장]** Lombok은 보일러플레이트를 줄이되, 과도하게 사용하지 않는다.

```java
// ✅ 권장하는 Lombok 사용
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Order { ... }

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService { ... }

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderResponse { ... }
```

```java
// ❌ 주의가 필요한 Lombok 사용
@Data                     // equals/hashCode 자동 생성 - JPA Entity에서 위험
@Entity
public class Order { ... }

@Setter                   // 무분별한 setter - 불변성 위반
@Entity
public class Order { ... }

@ToString                 // 연관 엔티티 포함 시 LazyInitializationException
@Entity
public class Order {
    @ManyToOne
    private Member member;   // toString에 포함되면 문제
}

// ✅ @ToString 사용 시 연관 엔티티 제외
@ToString(exclude = {"member", "items"})
@Entity
public class Order { ... }
```

---

> **이 문서는 팀의 합의에 따라 언제든 수정될 수 있다.**
> 새로운 규칙을 추가하거나 기존 규칙을 변경할 때는 팀 전체의 동의를 거친다.
> 규칙에 대한 질문이나 개선 제안은 코드 리뷰나 팀 회의에서 논의한다.
