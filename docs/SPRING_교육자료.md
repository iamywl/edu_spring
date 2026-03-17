# Spring Boot 프로그래밍 교육자료

> **대상**: Spring Boot 입문자 ~ 초급 개발자
> **프로젝트 참조**: `edu_spring` (chapter04 ~ chapter09)
> **Spring Boot 버전**: 3.x / Java 21
> **작성 언어**: 한국어

---

## 목차

- [Part 1: Spring Boot 기초](#part-1-spring-boot-기초)
- [Part 2: Spring Web MVC - REST API](#part-2-spring-web-mvc---rest-api)
- [Part 3: Spring Data JPA](#part-3-spring-data-jpa)
- [Part 4: Spring Security](#part-4-spring-security)
- [Part 5: 테스트](#part-5-테스트)
- [Part 6: 종합 프로젝트 - 게시판 API](#part-6-종합-프로젝트---게시판-api)
- [Part 7: Docker 활용 가이드](#part-7-docker-활용-가이드)
- [부록](#부록)

---

# Part 1: Spring Boot 기초

## 1.1 Spring Framework 소개

### Spring Framework 핵심 철학

Spring Framework는 자바 엔터프라이즈 개발을 단순화하기 위해 탄생한 프레임워크이다. 다음 네 가지 핵심 철학을 기반으로 설계되었다.

| 철학 | 설명 |
|------|------|
| **POJO (Plain Old Java Object)** | 특별한 인터페이스를 구현하지 않는 순수한 자바 객체를 사용한다. 프레임워크에 종속되지 않는 코드를 작성할 수 있다. |
| **IoC (Inversion of Control)** | 객체의 생성과 생명주기를 프레임워크가 관리한다. 개발자는 비즈니스 로직에만 집중할 수 있다. |
| **AOP (Aspect Oriented Programming)** | 로깅, 트랜잭션, 보안 등 횡단 관심사를 비즈니스 로직으로부터 분리한다. |
| **느슨한 결합 (Loose Coupling)** | 인터페이스 기반 프로그래밍과 DI를 통해 모듈 간 의존성을 최소화한다. |

### Spring 주요 모듈

```
┌─────────────────────────────────────────────────────┐
│                  Spring Framework                    │
├───────────┬──────────┬──────────┬───────────────────┤
│   Core    │   Web    │   Data   │     Security      │
│           │          │          │                   │
│ - IoC/DI  │ - MVC    │ - JPA    │ - 인증/인가       │
│ - Bean    │ - REST   │ - JDBC   │ - JWT             │
│ - AOP     │ - Filter │ - Tx     │ - OAuth2          │
├───────────┴──────────┴──────────┴───────────────────┤
│                    Spring Boot                       │
│  (자동 설정 + 내장 서버 + 스타터 + 설정 간소화)       │
└─────────────────────────────────────────────────────┘
```

### Spring vs Spring Boot 비교표

| 항목 | Spring Framework | Spring Boot |
|------|-----------------|-------------|
| 설정 방식 | XML 또는 Java Config (수동 설정) | 자동 설정 (Auto Configuration) |
| 서버 | 외부 Tomcat에 WAR 배포 | 내장 Tomcat (JAR 실행) |
| 의존성 관리 | 개별 라이브러리 버전 직접 관리 | Starter로 호환 버전 자동 관리 |
| 실행 방식 | WAR → WAS 배포 | `java -jar app.jar` |
| 설정 파일 | web.xml, applicationContext.xml | application.yml |
| 초기 세팅 | 수십 개의 설정 파일 필요 | 최소 설정으로 바로 시작 |

### Spring Boot 핵심 특징

1. **자동 설정 (Auto Configuration)**: 클래스패스의 라이브러리를 감지하여 최적의 설정을 자동 적용한다.
2. **내장 서버 (Embedded Server)**: Tomcat, Jetty, Undertow를 내장하여 별도 WAS 설치 없이 실행한다.
3. **스타터 (Starter)**: `spring-boot-starter-web`, `spring-boot-starter-data-jpa` 등 미리 정의된 의존성 묶음을 제공한다.
4. **설정 간소화**: `application.yml` 하나로 대부분의 설정을 관리한다.

---

## 1.2 @SpringBootApplication 분석

> **참조 코드**: `chapter04-spring-boot-intro/src/main/java/com/edu/intro/Chapter04Application.java`

```java
// @SpringBootApplication = @Configuration + @EnableAutoConfiguration + @ComponentScan
@SpringBootApplication
public class Chapter04Application {
    public static void main(String[] args) {
        SpringApplication.run(Chapter04Application.class, args);
    }
}
```

### @SpringBootApplication의 구성

`@SpringBootApplication`은 세 가지 어노테이션의 조합이다.

| 어노테이션 | 역할 |
|-----------|------|
| `@Configuration` | 이 클래스가 설정 클래스임을 선언한다. `@Bean` 메서드를 포함할 수 있다. |
| `@EnableAutoConfiguration` | 클래스패스의 라이브러리를 기반으로 자동 설정을 활성화한다. |
| `@ComponentScan` | 현재 패키지와 하위 패키지에서 `@Component` 계열 어노테이션을 스캔한다. |

### Spring Boot 실행 흐름

```
main() 메서드 호출
    │
    ▼
SpringApplication.run() 실행
    │
    ▼
ApplicationContext (Spring 컨테이너) 생성
    │
    ▼
@ComponentScan → Bean 스캔 및 등록
    │
    ▼
@EnableAutoConfiguration → 자동 설정 적용
    │
    ▼
내장 Tomcat 서버 시작 (포트 8080)
    │
    ▼
애플리케이션 Ready!
```

**중요**: `@SpringBootApplication`이 위치한 패키지가 Component Scan의 시작점이다. 따라서 이 클래스는 항상 **프로젝트 최상위 패키지**에 위치해야 한다.

---

## 1.3 IoC (Inversion of Control) / DI (Dependency Injection)

### 기존 방식 vs IoC 방식 비교

**기존 방식 (직접 생성)**:
```java
// 강한 결합 - 구현 클래스에 직접 의존
public class OrderService {
    private final PaymentService paymentService = new KakaoPayService(); // 직접 생성!

    // KakaoPayService → NaverPayService로 변경하려면?
    // → 이 코드를 직접 수정해야 함 (OCP 원칙 위반)
}
```

**IoC 방식 (Spring DI)**:
```java
// 느슨한 결합 - 인터페이스에 의존
public class OrderService {
    private final PaymentService paymentService; // 인터페이스에 의존

    // Spring이 적절한 구현체를 주입해 줌
    public OrderService(PaymentService paymentService) {
        this.paymentService = paymentService;
    }
}
```

### DI 3가지 방법

| 방법 | 권장 여부 | 특징 |
|------|----------|------|
| **생성자 주입** | **권장** | `final` 선언 가능, 불변 보장, 테스트 용이 |
| 필드 주입 (`@Autowired`) | 비권장 | 간결하지만 테스트 시 주입 어려움, 순환 참조 감지 불가 |
| Setter 주입 | 상황에 따라 | 선택적 의존성에 사용, 변경 가능성 있음 |

**생성자 주입 (권장)**:
> **참조 코드**: `chapter04-spring-boot-intro/src/main/java/com/edu/intro/HelloController.java`

```java
@RestController
@RequestMapping("/api")
public class HelloController {

    // 생성자 주입 (권장 방식)
    private final GreetingService greetingService;        // final 선언
    private final DateTimeFormatter dateTimeFormatter;     // final 선언

    // 생성자가 1개이면 @Autowired 생략 가능
    public HelloController(GreetingService greetingService,
                           DateTimeFormatter dateTimeFormatter) {
        this.greetingService = greetingService;
        this.dateTimeFormatter = dateTimeFormatter;
    }

    @GetMapping("/hello")
    public String hello(@RequestParam(defaultValue = "World") String name) {
        return greetingService.greet(name);
    }
}
```

**필드 주입 (비권장)**:
```java
@RestController
public class ExampleController {

    @Autowired  // 필드에 직접 주입 - 비권장!
    private GreetingService greetingService;
    // final 선언 불가, 테스트 시 리플렉션 필요
}
```

**Setter 주입**:
```java
@RestController
public class ExampleController {

    private GreetingService greetingService;

    @Autowired  // Setter 메서드로 주입
    public void setGreetingService(GreetingService greetingService) {
        this.greetingService = greetingService;
    }
}
```

### @Autowired 동작 원리

1. Spring 컨테이너가 `@Autowired`가 붙은 필드/생성자/메서드를 탐색한다.
2. 해당 타입의 Bean을 컨테이너에서 찾는다.
3. 같은 타입의 Bean이 여러 개이면 `@Primary`, `@Qualifier`로 선택한다.
4. Bean을 찾아 주입한다.

### 왜 생성자 주입이 권장되는가

| 이유 | 설명 |
|------|------|
| **final 선언** | 객체 생성 이후 변경 불가 (불변성 보장) |
| **불변성** | 한 번 주입된 의존성이 변경되지 않아 안전하다 |
| **테스트 용이성** | `new Service(mockRepository)`로 직접 생성 가능 |
| **순환 참조 감지** | 애플리케이션 시작 시 순환 참조를 즉시 감지한다 |
| **NPE 방지** | 생성자 호출 시 모든 필수 의존성이 주입된다 |

---

## 1.4 Bean과 Component Scan

### Bean이란?

**Bean**은 Spring IoC 컨테이너가 생성하고 관리하는 자바 객체이다. 개발자가 `new` 키워드로 직접 생성하는 것이 아니라, Spring이 생성, 의존성 주입, 소멸까지 전체 생명주기를 관리한다.

### @Component 계열 어노테이션

| 어노테이션 | 계층 | 역할 |
|-----------|------|------|
| `@Component` | 공통 | 범용 컴포넌트 등록 |
| `@Service` | Service | 비즈니스 로직 담당 |
| `@Repository` | Repository | 데이터 접근 계층 (예외 변환 기능 포함) |
| `@Controller` | Controller | MVC 컨트롤러 (뷰 반환) |
| `@RestController` | Controller | REST API 컨트롤러 (`@Controller` + `@ResponseBody`) |

> `@Service`, `@Repository`, `@Controller`는 모두 `@Component`를 포함하고 있다. 기능적으로는 동일하지만, **역할을 명확하게 구분**하기 위해 사용한다.

### @Configuration + @Bean (수동 등록)

> **참조 코드**: `chapter04-spring-boot-intro/src/main/java/com/edu/intro/AppConfig.java`

```java
// @Configuration: 설정 클래스임을 선언
// @Bean: 메서드의 반환 객체를 Spring Bean으로 등록
@Configuration
public class AppConfig {

    @Bean
    public DateTimeFormatter dateTimeFormatter() {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    }
}
```

**언제 `@Bean`을 사용하는가?**

| 상황 | 등록 방식 |
|------|----------|
| 직접 작성한 클래스 | `@Component` 계열 어노테이션 |
| 외부 라이브러리 클래스 (수정 불가) | `@Configuration` + `@Bean` |
| 조건부 Bean 등록이 필요할 때 | `@Configuration` + `@Bean` + `@Conditional` |
| 초기화 로직이 복잡한 객체 | `@Configuration` + `@Bean` |

### Component Scan 동작 원리

```
com.edu.intro                    ← @SpringBootApplication 위치 (스캔 시작점)
├── Chapter04Application.java    ← 메인 클래스
├── HelloController.java         ← @RestController → Bean 등록
├── GreetingService.java         ← 인터페이스 (Bean 아님)
├── KoreanGreetingService.java   ← @Service → Bean 등록
├── AppConfig.java               ← @Configuration → Bean 등록
└── BeanLifecycleExample.java    ← @Component → Bean 등록

com.other.package                ← 다른 패키지 → 스캔 범위 밖! (등록 안 됨)
```

### Bean 생명주기

> **참조 코드**: `chapter04-spring-boot-intro/src/main/java/com/edu/intro/BeanLifecycleExample.java`

```java
@Component
public class BeanLifecycleExample {

    public BeanLifecycleExample() {
        System.out.println("1. [생성자] BeanLifecycleExample 생성");
    }

    @PostConstruct
    public void init() {
        System.out.println("2. [@PostConstruct] 초기화 메서드 실행");
    }

    @PreDestroy
    public void destroy() {
        System.out.println("3. [@PreDestroy] 소멸 메서드 실행");
    }
}
```

```
Spring 컨테이너 시작
    │
    ▼
1. Bean 생성 (생성자 호출)
    │
    ▼
2. 의존성 주입 (DI)
    │
    ▼
3. @PostConstruct 초기화 콜백
    │
    ▼
4. Bean 사용 (애플리케이션 동작)
    │
    ▼
5. @PreDestroy 소멸 콜백
    │
    ▼
6. Bean 소멸
```

### Bean Scope

| Scope | 설명 | 생성 시점 |
|-------|------|----------|
| **singleton** (기본) | 컨테이너에 하나의 인스턴스만 존재 | 컨테이너 시작 시 |
| prototype | 요청마다 새로운 인스턴스 생성 | Bean 요청 시 |
| request | HTTP 요청마다 새로운 인스턴스 (웹) | HTTP 요청 시 |
| session | HTTP 세션마다 새로운 인스턴스 (웹) | 세션 생성 시 |

```java
@Component
@Scope("prototype")  // 요청마다 새로운 인스턴스 생성
public class PrototypeBean {
    // ...
}
```

> **실무에서는 99% singleton**을 사용한다. prototype은 매우 특수한 경우에만 사용한다.

---

## 1.5 자동 설정 (Auto Configuration)

### 동작 원리

```
@EnableAutoConfiguration
    │
    ▼
META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports 파일 로드
    │
    ▼
@Conditional 조건 평가
    │
    ▼
조건 충족 시 → 자동 설정 Bean 등록
조건 미충족 시 → 건너뜀
```

### 주요 @Conditional 어노테이션

| 어노테이션 | 조건 |
|-----------|------|
| `@ConditionalOnClass` | 특정 클래스가 클래스패스에 있을 때 |
| `@ConditionalOnMissingBean` | 해당 타입의 Bean이 아직 없을 때 |
| `@ConditionalOnProperty` | 특정 설정 값이 존재할 때 |
| `@ConditionalOnWebApplication` | 웹 애플리케이션일 때 |
| `@ConditionalOnMissingClass` | 특정 클래스가 없을 때 |

**예시**: `spring-boot-starter-web`을 추가하면:
1. 클래스패스에 `DispatcherServlet` 클래스가 감지된다.
2. `@ConditionalOnClass(DispatcherServlet.class)` 조건이 충족된다.
3. `DispatcherServletAutoConfiguration`이 활성화된다.
4. 내장 Tomcat, Jackson, MVC 설정 등이 자동으로 구성된다.

### application.yml 설정 방법

> **참조 코드**: `chapter04-spring-boot-intro/src/main/resources/application.yml`

```yaml
spring:
  application:
    name: chapter04-spring-intro
server:
  port: 8080
```

자동 설정의 기본값을 `application.yml`로 오버라이드할 수 있다.

```yaml
# 서버 포트 변경
server:
  port: 9090

# 데이터소스 설정
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/mydb
    username: myuser
    password: mypass

# JPA 설정
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
```

---

## 1.6 Profile 설정

### 환경별 설정 분리 (dev, prod)

> **참조 코드**: `chapter04-spring-boot-intro/src/main/resources/application.yml`

`application.yml`에서 `---`로 프로필을 구분할 수 있다.

```yaml
# 공통 설정 (모든 프로필에 적용)
spring:
  application:
    name: chapter04-spring-intro
server:
  port: 8080
---
# dev 프로필
spring:
  config:
    activate:
      on-profile: dev
  application:
    name: chapter04-spring-intro-dev
---
# prod 프로필
spring:
  config:
    activate:
      on-profile: prod
  application:
    name: chapter04-spring-intro-prod
```

### @Profile 어노테이션으로 Bean 활성화

> **참조 코드**: `chapter04-spring-boot-intro/src/main/java/com/edu/intro/KoreanGreetingService.java`

```java
// english 프로필이 아닐 때 활성화
@Service
@Profile("!english")
public class KoreanGreetingService implements GreetingService {
    @Override
    public String greet(String name) {
        return "안녕하세요, " + name + "님! (Korean Greeting Service)";
    }
}

// english 프로필일 때만 활성화
@Service
@Profile("english")
public class EnglishGreetingService implements GreetingService {
    @Override
    public String greet(String name) {
        return "Hello, " + name + "! (English Greeting Service)";
    }
}
```

### Profile 활성화 방법 4가지

| 방법 | 설정 |
|------|------|
| **application.yml** | `spring.profiles.active: dev` |
| **JVM 옵션** | `-Dspring.profiles.active=prod` |
| **환경변수** | `SPRING_PROFILES_ACTIVE=prod` |
| **Gradle** | `bootRun { systemProperty 'spring.profiles.active', 'dev' }` |

```bash
# JVM 옵션으로 프로필 활성화
java -Dspring.profiles.active=prod -jar app.jar

# 환경변수로 프로필 활성화
SPRING_PROFILES_ACTIVE=prod java -jar app.jar
```

---

# Part 2: Spring Web MVC - REST API

## 2.1 REST API 개념

### REST 핵심 원칙

| 원칙 | 설명 |
|------|------|
| **Stateless** | 서버는 클라이언트의 상태를 저장하지 않는다. 모든 요청은 독립적이다. |
| **Uniform Interface** | 일관된 인터페이스 (URL + HTTP 메서드)로 리소스에 접근한다. |
| **Client-Server** | 클라이언트와 서버는 독립적으로 개발된다. |
| **Layered System** | 계층화된 시스템 구조를 허용한다. |

### RESTful URL 설계 규칙

| 규칙 | 좋은 예 | 나쁜 예 |
|------|--------|--------|
| 명사 사용 | `/api/posts` | `/api/getPost` |
| 복수형 사용 | `/api/users` | `/api/user` |
| 소문자 사용 | `/api/user-profiles` | `/api/UserProfiles` |
| 행위는 HTTP 메서드로 | `DELETE /api/posts/1` | `/api/deletePost/1` |
| 계층 관계 표현 | `/api/posts/1/comments` | `/api/post-comments?postId=1` |

### HTTP 메서드

| 메서드 | 용도 | 멱등성 | 요청 본문 |
|--------|------|--------|----------|
| **GET** | 리소스 조회 | O | X |
| **POST** | 리소스 생성 | X | O |
| **PUT** | 리소스 전체 수정 | O | O |
| **PATCH** | 리소스 부분 수정 | X | O |
| **DELETE** | 리소스 삭제 | O | X |

---

## 2.2 @RestController vs @Controller

| 항목 | `@Controller` | `@RestController` |
|------|--------------|-------------------|
| 반환 타입 | **뷰 이름** (HTML 템플릿) | **데이터** (JSON/XML) |
| `@ResponseBody` | 필요 | 자동 포함 |
| 사용 용도 | SSR (서버 사이드 렌더링) | REST API |

```java
// @Controller - 뷰 반환 (Thymeleaf 등)
@Controller
public class ViewController {
    @GetMapping("/home")
    public String home(Model model) {
        model.addAttribute("message", "Hello");
        return "home";  // templates/home.html 반환
    }
}

// @RestController - 데이터 반환 (JSON)
@RestController
public class ApiController {
    @GetMapping("/api/data")
    public Map<String, String> data() {
        return Map.of("message", "Hello");  // {"message": "Hello"} JSON 반환
    }
}
```

### @ResponseBody 동작 원리

```
클라이언트 요청
    │
    ▼
DispatcherServlet → 컨트롤러 메서드 실행
    │
    ▼
반환값을 HttpMessageConverter가 JSON으로 변환
(Jackson 라이브러리 사용: 객체 → JSON)
    │
    ▼
HTTP 응답 본문에 JSON 데이터 담아 반환
```

---

## 2.3 HTTP 메서드 매핑

### 전체 CRUD 컨트롤러 예제

> **참조 코드**: `chapter05-spring-web/src/main/java/com/edu/web/controller/TodoController.java`

```java
@RestController
@RequestMapping("/api/todos")   // 공통 경로 지정
public class TodoController {

    private final TodoService todoService;

    // 생성자 주입 - 단일 생성자이므로 @Autowired 생략 가능
    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    // GET /api/todos → 전체 조회 (200 OK)
    @GetMapping
    public ResponseEntity<List<TodoResponse>> getAllTodos() {
        List<TodoResponse> todos = todoService.getAllTodos();
        return ResponseEntity.ok(todos);
    }

    // GET /api/todos/{id} → 단건 조회 (200 OK / 404 Not Found)
    @GetMapping("/{id}")
    public ResponseEntity<TodoResponse> getTodoById(@PathVariable Long id) {
        TodoResponse todo = todoService.getTodoById(id);
        return ResponseEntity.ok(todo);
    }

    // POST /api/todos → 생성 (201 Created / 400 Bad Request)
    @PostMapping
    public ResponseEntity<TodoResponse> createTodo(
            @Valid @RequestBody TodoRequest request) {
        TodoResponse created = todoService.createTodo(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // PUT /api/todos/{id} → 수정 (200 OK / 404 Not Found)
    @PutMapping("/{id}")
    public ResponseEntity<TodoResponse> updateTodo(
            @PathVariable Long id,
            @Valid @RequestBody TodoRequest request) {
        TodoResponse updated = todoService.updateTodo(id, request);
        return ResponseEntity.ok(updated);
    }

    // DELETE /api/todos/{id} → 삭제 (204 No Content / 404 Not Found)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTodo(@PathVariable Long id) {
        todoService.deleteTodo(id);
        return ResponseEntity.noContent().build();
    }
}
```

---

## 2.4 요청/응답 처리

### @RequestBody (JSON -> Java 객체)

HTTP 요청 본문의 JSON을 자바 객체로 변환한다.

```java
// 요청 JSON: {"title": "공부하기", "description": "Spring 공부"}
@PostMapping
public ResponseEntity<TodoResponse> createTodo(
        @RequestBody TodoRequest request) {  // JSON → TodoRequest 자동 변환
    // request.title() == "공부하기"
    // request.description() == "Spring 공부"
}
```

### @PathVariable (경로 변수)

URL 경로의 일부를 변수로 바인딩한다.

```java
// GET /api/todos/42
@GetMapping("/{id}")
public ResponseEntity<TodoResponse> getTodoById(
        @PathVariable Long id) {   // id == 42
    return ResponseEntity.ok(todoService.getTodoById(id));
}

// 변수명이 다른 경우
@GetMapping("/{todoId}")
public ResponseEntity<TodoResponse> getTodo(
        @PathVariable("todoId") Long id) {   // URL의 {todoId}를 id에 바인딩
    return ResponseEntity.ok(todoService.getTodoById(id));
}
```

### @RequestParam (쿼리 파라미터)

URL 쿼리 스트링의 파라미터를 바인딩한다.

```java
// GET /api/todos?completed=true&page=0&size=10
@GetMapping
public ResponseEntity<List<TodoResponse>> search(
        @RequestParam(required = false) Boolean completed,  // 선택적 파라미터
        @RequestParam(defaultValue = "0") int page,         // 기본값 지정
        @RequestParam(defaultValue = "10") int size) {
    // ...
}
```

### @RequestHeader

HTTP 요청 헤더 값을 바인딩한다.

```java
@GetMapping("/info")
public String info(
        @RequestHeader("Authorization") String token,
        @RequestHeader(value = "Accept-Language", defaultValue = "ko") String lang) {
    return "Token: " + token + ", Lang: " + lang;
}
```

---

## 2.5 DTO 패턴

### 왜 DTO를 사용하는가

| 이유 | 설명 |
|------|------|
| **보안** | Entity의 민감한 필드(비밀번호 등)가 API 응답에 노출되는 것을 방지한다 |
| **유연성** | API 스펙과 DB 스키마를 독립적으로 변경할 수 있다 |
| **검증** | 요청 DTO에 검증 어노테이션을 적용하여 입력을 검증한다 |
| **관심사 분리** | 프레젠테이션 계층과 도메인 계층을 분리한다 |

### Java record로 DTO 정의하기

> **참조 코드**: `chapter05-spring-web/src/main/java/com/edu/web/dto/`

```java
// 요청 DTO - 클라이언트가 보내는 데이터
public record TodoRequest(
    @NotBlank(message = "제목은 필수입니다")
    @Size(max = 100, message = "제목은 100자 이하입니다")
    String title,

    String description,

    boolean completed
) {}

// 응답 DTO - 클라이언트에게 반환할 데이터
public record TodoResponse(
    Long id,
    String title,
    String description,
    boolean completed,
    LocalDateTime createdAt
) {}
```

> **record**는 Java 16부터 도입된 불변 데이터 클래스이다. `getter`, `equals()`, `hashCode()`, `toString()`이 자동 생성된다. DTO에 매우 적합하다.

### 요청 DTO vs 응답 DTO 분리

```
클라이언트 → [TodoRequest] → Controller → Service → Repository
                                                      │
클라이언트 ← [TodoResponse] ← Controller ← Service ←──┘
```

요청/응답 DTO를 분리하면:
- 요청에는 `id`, `createdAt` 등을 포함하지 않는다 (서버가 생성하는 값).
- 응답에는 비밀번호 등 민감한 정보를 포함하지 않는다.

---

## 2.6 ResponseEntity

### HTTP 상태 코드

| 코드 | 상수 | 의미 | 사용 예시 |
|------|------|------|----------|
| 200 | `OK` | 성공 | 조회, 수정 성공 |
| 201 | `CREATED` | 생성됨 | 리소스 생성 성공 |
| 204 | `NO_CONTENT` | 본문 없음 | 삭제 성공 |
| 400 | `BAD_REQUEST` | 잘못된 요청 | 유효성 검증 실패 |
| 401 | `UNAUTHORIZED` | 인증 실패 | 로그인 필요 |
| 403 | `FORBIDDEN` | 권한 부족 | 접근 권한 없음 |
| 404 | `NOT_FOUND` | 찾을 수 없음 | 리소스 없음 |
| 500 | `INTERNAL_SERVER_ERROR` | 서버 오류 | 예상치 못한 에러 |

### ResponseEntity 사용법

```java
// 200 OK + 본문
return ResponseEntity.ok(todoResponse);

// 201 Created + 본문
return ResponseEntity.status(HttpStatus.CREATED).body(created);

// 204 No Content (본문 없음)
return ResponseEntity.noContent().build();

// 400 Bad Request + 에러 메시지
return ResponseEntity.badRequest().body(errorResponse);

// 404 Not Found
return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);

// 커스텀 헤더 포함
return ResponseEntity.ok()
        .header("X-Custom-Header", "value")
        .body(data);
```

### ResponseEntity vs 직접 반환 비교

```java
// 직접 반환 - 항상 200 OK
@GetMapping("/{id}")
public TodoResponse getTodo(@PathVariable Long id) {
    return todoService.getTodoById(id);  // 상태 코드 제어 불가
}

// ResponseEntity - 상태 코드 + 헤더 제어 가능
@GetMapping("/{id}")
public ResponseEntity<TodoResponse> getTodo(@PathVariable Long id) {
    TodoResponse todo = todoService.getTodoById(id);
    return ResponseEntity.ok(todo);  // 200 OK + 본문
}
```

---

## 2.7 전역 예외 처리

> **참조 코드**: `chapter05-spring-web/src/main/java/com/edu/web/controller/GlobalExceptionHandler.java`

### @RestControllerAdvice + @ExceptionHandler

```java
@RestControllerAdvice  // 모든 컨트롤러에 적용되는 전역 예외 처리기
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // 커스텀 예외 처리 (404 Not Found)
    @ExceptionHandler(TodoNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleTodoNotFound(TodoNotFoundException ex) {
        log.warn("할일을 찾을 수 없음: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse("NOT_FOUND", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    // 유효성 검증 실패 (400 Bad Request)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .collect(Collectors.joining(", "));
        log.warn("유효성 검증 실패: {}", message);
        ErrorResponse error = new ErrorResponse("VALIDATION_ERROR", message);
        return ResponseEntity.badRequest().body(error);
    }

    // 그 외 모든 예외 (500 Internal Server Error)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex) {
        log.error("예상치 못한 서버 오류 발생", ex);
        ErrorResponse error = new ErrorResponse(
                "INTERNAL_SERVER_ERROR", "서버 내부 오류가 발생했습니다");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
```

### 커스텀 예외 클래스 만들기

> **참조 코드**: `chapter05-spring-web/src/main/java/com/edu/web/exception/TodoNotFoundException.java`

```java
// RuntimeException 상속 → 체크 예외 처리(try-catch) 생략 가능
public class TodoNotFoundException extends RuntimeException {

    public TodoNotFoundException(Long id) {
        super("할일을 찾을 수 없습니다. ID: " + id);
    }
}
```

### 일관된 에러 응답 형식 설계

> **참조 코드**: `chapter05-spring-web/src/main/java/com/edu/web/dto/ErrorResponse.java`

```java
// 표준화된 에러 응답 DTO
public record ErrorResponse(
    String code,
    String message,
    LocalDateTime timestamp
) {
    // 간편 생성자 - 타임스탬프를 자동으로 현재 시각으로 설정
    public ErrorResponse(String code, String message) {
        this(code, message, LocalDateTime.now());
    }
}
```

**응답 예시**:
```json
{
  "code": "NOT_FOUND",
  "message": "할일을 찾을 수 없습니다. ID: 999",
  "timestamp": "2026-03-17T14:30:00"
}
```

---

## 2.8 Validation (@Valid)

### spring-boot-starter-validation

```groovy
// build.gradle
dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-validation'
}
```

### 주요 검증 어노테이션

| 어노테이션 | 설명 | 예시 |
|-----------|------|------|
| `@NotBlank` | null, 빈 문자열, 공백만 있는 문자열 불가 | `@NotBlank String title` |
| `@NotNull` | null 불가 | `@NotNull Long id` |
| `@NotEmpty` | null, 빈 문자열 불가 (공백은 허용) | `@NotEmpty String name` |
| `@Size` | 문자열/컬렉션 크기 제한 | `@Size(min=2, max=100)` |
| `@Min` | 숫자 최솟값 | `@Min(0) int quantity` |
| `@Max` | 숫자 최댓값 | `@Max(999) int stock` |
| `@Email` | 이메일 형식 검증 | `@Email String email` |
| `@Pattern` | 정규 표현식 패턴 | `@Pattern(regexp="[0-9]+")` |
| `@Positive` | 양수만 허용 | `@Positive int price` |
| `@Past` | 과거 날짜만 허용 | `@Past LocalDate birthDate` |
| `@Future` | 미래 날짜만 허용 | `@Future LocalDate dueDate` |

### DTO에 검증 규칙 적용하기

```java
public record TodoRequest(
    @NotBlank(message = "제목은 필수입니다")
    @Size(max = 100, message = "제목은 100자 이하입니다")
    String title,

    String description,

    boolean completed
) {}
```

컨트롤러에서 `@Valid`를 붙여야 검증이 실행된다:

```java
@PostMapping
public ResponseEntity<TodoResponse> createTodo(
        @Valid @RequestBody TodoRequest request) {  // @Valid가 있어야 검증 실행!
    // ...
}
```

### 커스텀 Validator 만들기

```java
// 1. 커스텀 어노테이션 정의
@Documented
@Constraint(validatedBy = NoSpecialCharValidator.class)
@Target({FIELD})
@Retention(RUNTIME)
public @interface NoSpecialChar {
    String message() default "특수문자를 포함할 수 없습니다";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

// 2. Validator 구현
public class NoSpecialCharValidator
        implements ConstraintValidator<NoSpecialChar, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) return true;
        return value.matches("^[a-zA-Z0-9가-힣\\s]+$");
    }
}

// 3. DTO에 적용
public record PostRequest(
    @NoSpecialChar
    @NotBlank
    String title,
    String content
) {}
```

---

# Part 3: Spring Data JPA

## 3.1 JPA와 ORM 개념

### ORM이란?

**ORM (Object-Relational Mapping)** 은 자바 객체와 데이터베이스 테이블을 자동으로 매핑해주는 기술이다.

```
┌──────────────────┐          ┌──────────────────┐
│   Java 객체       │  ← ORM →  │   DB 테이블       │
│                  │          │                  │
│  class Member {  │          │  CREATE TABLE    │
│    Long id;      │  ──────  │    member (      │
│    String name;  │          │      id BIGINT,  │
│    String email; │          │      name VARCHAR,│
│  }               │          │      email VARCHAR│
│                  │          │    );            │
└──────────────────┘          └──────────────────┘
```

### JPA 계층 구조

```
┌─────────────────────────────┐
│      Spring Data JPA         │  ← Repository 인터페이스 제공
├─────────────────────────────┤
│       JPA (스펙)             │  ← 표준 인터페이스 (EntityManager 등)
├─────────────────────────────┤
│   Hibernate (구현체)         │  ← JPA 스펙의 실제 구현
├─────────────────────────────┤
│         JDBC                 │  ← DB 연결, SQL 실행
├─────────────────────────────┤
│     PostgreSQL / MySQL       │  ← 실제 데이터베이스
└─────────────────────────────┘
```

### JPA를 사용하는 이유

| 이유 | 설명 |
|------|------|
| **생산성** | SQL을 직접 작성하지 않아도 CRUD가 자동으로 생성된다 |
| **유지보수** | 필드 추가/삭제 시 엔티티만 수정하면 된다 (SQL 변경 불필요) |
| **DB 독립성** | 방언(Dialect)만 바꾸면 DB를 교체할 수 있다 |
| **패러다임 불일치 해결** | 상속, 연관관계 등 객체지향 개념을 DB에 매핑한다 |

---

## 3.2 Entity 매핑

> **참조 코드**: `chapter06-spring-data-jpa/src/main/java/com/edu/jpa/entity/Member.java`

### 주요 어노테이션

| 어노테이션 | 설명 | 예시 |
|-----------|------|------|
| `@Entity` | JPA 엔티티 클래스 선언 | `@Entity public class Member` |
| `@Table` | 매핑할 테이블명 지정 | `@Table(name = "member")` |
| `@Id` | 기본 키(PK) 지정 | `@Id private Long id` |
| `@GeneratedValue` | PK 자동 생성 전략 | `@GeneratedValue(strategy = IDENTITY)` |
| `@Column` | 컬럼 매핑 옵션 | `@Column(nullable = false, length = 50)` |
| `@CreatedDate` | 생성일 자동 기록 | `@CreatedDate private LocalDateTime createdAt` |
| `@Enumerated` | Enum 타입 매핑 | `@Enumerated(EnumType.STRING)` |

### @GeneratedValue 전략

| 전략 | 설명 | 사용 DB |
|------|------|---------|
| `IDENTITY` | DB의 자동 증가 기능 사용 (MySQL: AUTO_INCREMENT, PostgreSQL: SERIAL) | MySQL, PostgreSQL |
| `SEQUENCE` | DB 시퀀스 사용 | PostgreSQL, Oracle |
| `TABLE` | 키 생성 전용 테이블 사용 | 모든 DB |
| `AUTO` | JPA가 전략 자동 선택 | 모든 DB |

### 엔티티 예제

```java
@Entity
@Table(name = "member")
@EntityListeners(AuditingEntityListener.class)  // @CreatedDate 사용을 위해 필요
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    // 기본 생성자 (JPA 스펙에서 필수 - protected로 외부 직접 호출 방지)
    protected Member() {
    }

    public Member(String name, String email) {
        this.name = name;
        this.email = email;
    }

    // Getter / Setter ...
}
```

### 엔티티 설계 규칙

1. **기본 생성자 필수**: JPA가 리플렉션으로 객체를 생성하기 위해 필요하다. `protected`로 선언하면 외부 직접 호출을 방지할 수 있다.
2. **final 클래스 금지**: JPA가 프록시를 생성해야 하므로 `final` 클래스로 선언하면 안 된다.
3. **`@Id` 필수**: 모든 엔티티는 기본 키를 가져야 한다.
4. **Enum은 `@Enumerated(EnumType.STRING)`**: 기본값 `ORDINAL`은 순서 변경 시 데이터가 꼬일 수 있으므로 `STRING`을 권장한다.

---

## 3.3 JpaRepository

### JpaRepository 계층 구조

```
┌──────────────────────────────────────┐
│         JpaRepository<T, ID>          │ ← flush(), saveAllAndFlush()
├──────────────────────────────────────┤
│     ListCrudRepository<T, ID>         │ ← findAll() returns List
├──────────────────────────────────────┤
│     PagingAndSortingRepository<T, ID> │ ← findAll(Pageable), findAll(Sort)
├──────────────────────────────────────┤
│         CrudRepository<T, ID>         │ ← save(), findById(), delete()
├──────────────────────────────────────┤
│           Repository<T, ID>           │ ← 마커 인터페이스
└──────────────────────────────────────┘
```

### 기본 제공 메서드

| 메서드 | 반환 타입 | 설명 |
|--------|----------|------|
| `save(entity)` | `T` | 저장 (INSERT 또는 UPDATE) |
| `findById(id)` | `Optional<T>` | PK로 단건 조회 |
| `findAll()` | `List<T>` | 전체 조회 |
| `findAll(Pageable)` | `Page<T>` | 페이징 조회 |
| `deleteById(id)` | `void` | PK로 삭제 |
| `delete(entity)` | `void` | 엔티티로 삭제 |
| `count()` | `long` | 전체 건수 |
| `existsById(id)` | `boolean` | 존재 여부 확인 |

```java
// JpaRepository를 상속받으면 위 메서드를 모두 사용할 수 있습니다
public interface MemberRepository extends JpaRepository<Member, Long> {
    // 기본 CRUD 메서드는 자동 제공!
}
```

---

## 3.4 쿼리 메서드 (Query Methods)

> **참조 코드**: `chapter06-spring-data-jpa/src/main/java/com/edu/jpa/repository/MemberRepository.java`

### 메서드 이름 규칙으로 쿼리 자동 생성

Spring Data JPA는 메서드 이름을 분석하여 자동으로 JPQL 쿼리를 생성한다.

### 주요 키워드와 생성되는 SQL

| 키워드 | 메서드 예시 | 생성되는 SQL (WHERE 절) |
|--------|-----------|----------------------|
| `And` | `findByNameAndEmail(name, email)` | `WHERE name = ? AND email = ?` |
| `Or` | `findByNameOrEmail(name, email)` | `WHERE name = ? OR email = ?` |
| `Between` | `findByAgeBetween(min, max)` | `WHERE age BETWEEN ? AND ?` |
| `LessThan` | `findByAgeLessThan(age)` | `WHERE age < ?` |
| `LessThanEqual` | `findByAgeLessThanEqual(age)` | `WHERE age <= ?` |
| `GreaterThan` | `findByAgeGreaterThan(age)` | `WHERE age > ?` |
| `GreaterThanEqual` | `findByAgeGreaterThanEqual(age)` | `WHERE age >= ?` |
| `Like` | `findByNameLike(pattern)` | `WHERE name LIKE ?` |
| `Containing` | `findByNameContaining(keyword)` | `WHERE name LIKE '%keyword%'` |
| `StartingWith` | `findByNameStartingWith(prefix)` | `WHERE name LIKE 'prefix%'` |
| `EndingWith` | `findByNameEndingWith(suffix)` | `WHERE name LIKE '%suffix'` |
| `OrderBy` | `findByTeamOrderByNameAsc()` | `ORDER BY name ASC` |
| `IsNull` | `findByTeamIsNull()` | `WHERE team IS NULL` |
| `IsNotNull` | `findByTeamIsNotNull()` | `WHERE team IS NOT NULL` |
| `In` | `findByNameIn(names)` | `WHERE name IN (?, ?, ?)` |
| `countBy` | `countByTeamName(teamName)` | `SELECT COUNT(*) WHERE team_name = ?` |

### 실제 사용 예시

```java
public interface MemberRepository extends JpaRepository<Member, Long> {

    // 이메일로 회원 조회
    Optional<Member> findByEmail(String email);

    // 이름에 특정 문자열이 포함된 회원 조회
    List<Member> findByNameContaining(String name);

    // 팀 이름으로 회원 조회 (연관 엔티티의 필드로 조회)
    List<Member> findByTeamName(String teamName);

    // 이름으로 검색 + 페이징
    Page<Member> findByNameContaining(String name, Pageable pageable);
}
```

---

## 3.5 @Query (JPQL, Native Query)

> **참조 코드**: `chapter06-spring-data-jpa/src/main/java/com/edu/jpa/repository/MemberRepository.java`

### JPQL vs Native Query 비교

| 항목 | JPQL | Native Query |
|------|------|-------------|
| 문법 | 엔티티명, 필드명 사용 | 테이블명, 컬럼명 사용 |
| DB 독립성 | O (DB에 관계없이 동작) | X (특정 DB SQL 사용) |
| 설정 | `@Query("SELECT m FROM Member m ...")` | `@Query(value = "SELECT * FROM member ...", nativeQuery = true)` |
| 사용 시기 | 대부분의 경우 | DB 전용 함수, 복잡한 SQL |

### 사용 예시

```java
public interface MemberRepository extends JpaRepository<Member, Long> {

    // JPQL - 엔티티명(Member), 필드명(name) 사용
    @Query("SELECT m FROM Member m WHERE m.name LIKE %:keyword%")
    List<Member> searchByName(@Param("keyword") String keyword);

    // Native Query - 테이블명(member), 컬럼명(email) 사용
    @Query(value = "SELECT * FROM member WHERE email LIKE %:domain",
           nativeQuery = true)
    List<Member> findByEmailDomain(@Param("domain") String domain);

    // JPQL - JOIN FETCH (N+1 문제 해결)
    @Query("SELECT m FROM Member m JOIN FETCH m.team WHERE m.id = :id")
    Optional<Member> findByIdWithTeam(@Param("id") Long id);
}
```

### 사용 시기 가이드

| 상황 | 권장 방법 |
|------|----------|
| 단순 조건 조회 | 쿼리 메서드 (`findByName`) |
| 조건이 2~3개 이상 | `@Query` + JPQL |
| DB 전용 함수 사용 | `@Query` + Native Query |
| 매우 복잡한 쿼리 | QueryDSL 또는 Native Query |

---

## 3.6 페이징과 정렬

> **참조 코드**: `chapter06-spring-data-jpa/src/main/java/com/edu/jpa/controller/MemberController.java`

### Pageable, PageRequest, Sort

```java
// Controller에서 Pageable 자동 바인딩
// GET /api/members?page=0&size=10&sort=createdAt,desc
@GetMapping("/members")
public ResponseEntity<Page<MemberResponse>> getMembers(
        @PageableDefault(size = 10, sort = "createdAt",
                         direction = Sort.Direction.DESC)
        Pageable pageable) {
    return ResponseEntity.ok(memberService.getMembers(pageable));
}

// Service에서 Pageable 전달
public Page<MemberResponse> getMembers(Pageable pageable) {
    return memberRepository.findAll(pageable)
            .map(MemberResponse::from);  // Page의 map 메서드로 DTO 변환
}
```

### Page 객체 주요 메서드

| 메서드 | 반환 타입 | 설명 |
|--------|----------|------|
| `getContent()` | `List<T>` | 현재 페이지의 데이터 목록 |
| `getTotalElements()` | `long` | 전체 데이터 건수 |
| `getTotalPages()` | `int` | 전체 페이지 수 |
| `getNumber()` | `int` | 현재 페이지 번호 (0부터 시작) |
| `getSize()` | `int` | 페이지 크기 |
| `hasNext()` | `boolean` | 다음 페이지 존재 여부 |
| `hasPrevious()` | `boolean` | 이전 페이지 존재 여부 |
| `isFirst()` | `boolean` | 첫 번째 페이지 여부 |
| `isLast()` | `boolean` | 마지막 페이지 여부 |

### REST API에서 페이징 파라미터 처리

```
GET /api/members?page=0&size=10&sort=createdAt,desc

page  = 페이지 번호 (0부터 시작)
size  = 페이지당 항목 수
sort  = 정렬 필드,방향 (여러 개 가능: sort=name,asc&sort=id,desc)
```

### @PageableDefault 사용법

```java
// 기본값 설정: 한 페이지에 10개, createdAt 내림차순
@PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
Pageable pageable

// 정렬 여러 개 설정할 때는 PageRequest를 직접 생성
Pageable pageable = PageRequest.of(0, 10,
        Sort.by(Sort.Direction.DESC, "createdAt")
             .and(Sort.by(Sort.Direction.ASC, "name")));
```

---

## 3.7 연관관계 매핑

> **참조 코드**: `chapter06-spring-data-jpa/src/main/java/com/edu/jpa/entity/Team.java`, `Member.java`

### @ManyToOne / @OneToMany

```
┌──────────────┐     N:1      ┌──────────────┐
│    Member     │ ──────────── │     Team      │
│              │              │              │
│  id          │              │  id          │
│  name        │              │  name        │
│  email       │              │  members     │
│  team_id (FK)│              │              │
└──────────────┘              └──────────────┘
       N                              1
```

**Member (N쪽 - 연관관계의 주인)**:
```java
@Entity
public class Member {
    // ...

    // 다대일(N:1) 관계 - 여러 회원이 하나의 팀에 소속
    @ManyToOne(fetch = FetchType.LAZY)      // 지연 로딩
    @JoinColumn(name = "team_id")           // FK 컬럼명
    private Team team;
}
```

**Team (1쪽 - 연관관계의 주인이 아님)**:
```java
@Entity
public class Team {
    // ...

    // 일대다(1:N) 관계 매핑
    @OneToMany(mappedBy = "team",               // 연관관계의 주인은 Member.team
               cascade = CascadeType.ALL,        // 영속성 전이
               orphanRemoval = true)             // 고아 객체 자동 삭제
    private List<Member> members = new ArrayList<>();
}
```

### 연관관계의 주인 개념

> **외래 키(FK)를 가진 쪽이 연관관계의 주인이다.**

- **주인 (Member.team)**: 데이터를 읽고 쓸 수 있다. `team_id` FK를 관리한다.
- **비주인 (Team.members)**: 읽기만 가능하다. `mappedBy`로 주인을 지정한다.

### FetchType.LAZY vs EAGER 비교

| 항목 | LAZY (지연 로딩) | EAGER (즉시 로딩) |
|------|----------------|------------------|
| SQL 실행 시점 | 연관 데이터 접근 시 | 엔티티 조회 시 즉시 |
| 쿼리 수 | 필요할 때 추가 쿼리 | 엔티티 조회 시 즉시 추가 쿼리 또는 JOIN |
| 성능 | 필요한 데이터만 로딩 | 불필요한 데이터도 로딩 |
| **권장 여부** | **권장** | 비권장 |

> **실무 규칙**: 모든 연관관계를 `FetchType.LAZY`로 설정하고, 필요할 때 `fetch join`으로 조회한다.

### cascade 옵션

| 옵션 | 설명 |
|------|------|
| `CascadeType.PERSIST` | 부모 저장 시 자식도 함께 저장 |
| `CascadeType.REMOVE` | 부모 삭제 시 자식도 함께 삭제 |
| `CascadeType.ALL` | 모든 영속성 전이 |
| `orphanRemoval = true` | 부모 컬렉션에서 제거된 자식을 DB에서도 삭제 |

### 연관관계 편의 메서드

```java
@Entity
public class Team {
    // ...

    // 양방향 관계에서 양쪽 모두에 값을 설정해주는 편의 메서드
    public void addMember(Member member) {
        members.add(member);
        member.setTeam(this);   // 양쪽 모두 설정!
    }

    public void removeMember(Member member) {
        members.remove(member);
        member.setTeam(null);   // 양쪽 모두 설정!
    }
}
```

> 양방향 연관관계에서는 반드시 **양쪽 모두에 값을 설정**해야 한다. 편의 메서드를 사용하면 실수를 방지할 수 있다.

### N+1 문제 소개와 해결 방법

**N+1 문제란?**

```java
// 팀 전체 조회 (쿼리 1번)
List<Team> teams = teamRepository.findAll();

// 각 팀의 멤버에 접근할 때마다 추가 쿼리 (N번)
for (Team team : teams) {
    System.out.println(team.getMembers().size());  // LAZY → 추가 쿼리!
}
// 총 쿼리 수: 1 + N (팀이 10개면 11번!)
```

**해결 방법: fetch join**

```java
// JPQL fetch join - 한 번의 쿼리로 팀과 멤버를 함께 조회
@Query("SELECT t FROM Team t JOIN FETCH t.members")
List<Team> findAllWithMembers();
// 총 쿼리 수: 1번!
```

---

# Part 4: Spring Security

## 4.1 Spring Security 아키텍처

### SecurityFilterChain 동작 흐름

```
HTTP 요청
    │
    ▼
┌─────────────────────────────────────────┐
│          SecurityFilterChain             │
│                                         │
│  ┌─────────────────────────────────┐    │
│  │ CorsFilter                      │    │
│  └──────────┬──────────────────────┘    │
│             │                           │
│  ┌──────────▼──────────────────────┐    │
│  │ CsrfFilter (비활성화)            │    │
│  └──────────┬──────────────────────┘    │
│             │                           │
│  ┌──────────▼──────────────────────┐    │
│  │ JwtAuthenticationFilter ★       │    │  ← 우리가 구현하는 필터
│  │ (토큰 추출 → 검증 → 인증 설정)   │    │
│  └──────────┬──────────────────────┘    │
│             │                           │
│  ┌──────────▼──────────────────────┐    │
│  │ UsernamePasswordAuthFilter      │    │
│  └──────────┬──────────────────────┘    │
│             │                           │
│  ┌──────────▼──────────────────────┐    │
│  │ AuthorizationFilter             │    │  ← URL 접근 권한 확인
│  └──────────┬──────────────────────┘    │
│             │                           │
└─────────────┼───────────────────────────┘
              │
              ▼
         DispatcherServlet → Controller
```

### 핵심 구성 요소

| 구성 요소 | 역할 |
|----------|------|
| **SecurityFilterChain** | 보안 필터 체인을 정의한다. HTTP 요청에 대한 보안 규칙을 설정한다. |
| **AuthenticationManager** | 인증 처리를 총괄한다. 내부적으로 AuthenticationProvider에 위임한다. |
| **AuthenticationProvider** | 실제 인증 로직을 수행한다 (비밀번호 검증 등). |
| **UserDetailsService** | DB에서 사용자 정보를 로드한다. |
| **SecurityContext** | 현재 인증된 사용자 정보를 저장한다. ThreadLocal로 관리된다. |
| **PasswordEncoder** | 비밀번호 해싱 및 검증을 담당한다 (BCrypt). |

---

## 4.2 인증(Authentication) vs 인가(Authorization)

| 항목 | 인증 (Authentication) | 인가 (Authorization) |
|------|---------------------|---------------------|
| **목적** | "누구인가?" 확인 | "무엇을 할 수 있는가?" 확인 |
| **시점** | 먼저 실행 | 인증 이후 실행 |
| **실패 코드** | 401 Unauthorized | 403 Forbidden |
| **예시** | 로그인 (ID/PW 확인) | 관리자 페이지 접근 제어 |

### URL 기반 인가 설정

```java
.authorizeHttpRequests(auth -> auth
    // 인증 없이 접근 가능한 URL
    .requestMatchers("/api/auth/**").permitAll()
    // ADMIN 역할만 접근 가능
    .requestMatchers("/api/admin/**").hasRole("ADMIN")
    // USER 또는 ADMIN 접근 가능
    .requestMatchers("/api/user/**").hasAnyRole("USER", "ADMIN")
    // 그 외 모든 요청은 인증 필요
    .anyRequest().authenticated()
)
```

### 메서드 레벨 보안 (@PreAuthorize)

```java
@EnableMethodSecurity  // SecurityConfig에 추가

@RestController
public class AdminController {

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/api/admin/users")
    public List<UserResponse> getAllUsers() {
        // ADMIN만 접근 가능
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/api/user/profile")
    public UserResponse getProfile(@AuthenticationPrincipal User user) {
        // USER 이상만 접근 가능
    }

    @PreAuthorize("#userId == authentication.principal.id")
    @PutMapping("/api/users/{userId}")
    public UserResponse updateUser(@PathVariable Long userId) {
        // 본인만 수정 가능
    }
}
```

---

## 4.3 Password Encoding (BCrypt)

### 왜 암호화가 필요한가

비밀번호를 평문으로 저장하면 DB가 유출되었을 때 모든 사용자의 비밀번호가 노출된다. BCrypt로 해싱하면 원본 비밀번호를 복원할 수 없다.

### BCrypt 특징

| 특징 | 설명 |
|------|------|
| **솔트 (Salt)** | 매번 다른 랜덤 솔트를 생성하여 같은 비밀번호도 다른 해시값 생성 |
| **적응형 해싱** | 반복 횟수(strength)를 조절하여 하드웨어 발전에 대응 |
| **단방향** | 해시값에서 원본 비밀번호를 역추적할 수 없음 |
| **느림** | 의도적으로 느리게 설계하여 무차별 대입 공격을 어렵게 함 |

### BCrypt 해시 구조 분석

```
$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy
 │   │  │                     │
 │   │  │                     └─ 해시값 (31자)
 │   │  └─ 솔트 (22자, Base64)
 │   └─ 비용 인자 (10 → 2^10 = 1,024번 반복)
 └─ 알고리즘 버전 (2a = BCrypt)
```

### PasswordEncoder 사용법

```java
// SecurityConfig에서 Bean 등록
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();  // 기본 strength: 10
}

// 회원가입 시 - 비밀번호 암호화
String rawPassword = "myPassword123";
String encodedPassword = passwordEncoder.encode(rawPassword);
// $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy

// 로그인 시 - 비밀번호 검증
boolean matches = passwordEncoder.matches(rawPassword, encodedPassword);
// true
```

---

## 4.4 JWT (JSON Web Token)

### JWT 구조

```
eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyMSIsImlhdCI6MTcxMDAwMDAwMH0.abc123signature
│                     │                                              │
│                     │                                              └─ Signature (서명)
│                     └─ Payload (데이터)
└─ Header (알고리즘)

┌───────────┬─────────────────────────────────────────────────┐
│  Header   │ {"alg": "HS256"}                                │
├───────────┼─────────────────────────────────────────────────┤
│  Payload  │ {"sub": "user1", "iat": 1710000000,            │
│           │  "exp": 1710086400}                              │
├───────────┼─────────────────────────────────────────────────┤
│ Signature │ HMACSHA256(base64(header) + "." +               │
│           │   base64(payload), secret)                       │
└───────────┴─────────────────────────────────────────────────┘
```

### JWT 인증 흐름

```
1. 로그인 요청
   Client ──POST /api/auth/login──▶ Server
           { "username": "user1",
             "password": "1234" }

2. 토큰 발급
   Client ◀── 200 OK ──────────── Server
           { "token": "eyJhbGci..." }
              (비밀번호 검증 후 JWT 생성)

3. 인증된 요청
   Client ──GET /api/posts─────── Server
           Authorization: Bearer eyJhbGci...
              (모든 요청에 토큰 포함)

4. 토큰 검증
   Server: JWT 서명 검증 → 만료 확인 → 사용자 확인 → 인증 성공
```

### JwtService 핵심 메서드

> **참조 코드**: `chapter07-spring-security/src/main/java/com/edu/security/service/JwtService.java`

```java
@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long expiration;

    // 토큰 생성
    public String generateToken(UserDetails userDetails) {
        return Jwts.builder()
                .subject(userDetails.getUsername())       // 토큰 주체 (사용자명)
                .issuedAt(new Date())                     // 발행 시간
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey())                // HMAC-SHA 서명
                .compact();
    }

    // 토큰에서 사용자명 추출
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // 토큰 유효성 검증
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    // 서명 키 생성
    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
```

### JwtAuthenticationFilter 구현

> **참조 코드**: `chapter07-spring-security/src/main/java/com/edu/security/config/JwtAuthenticationFilter.java`

```java
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        // 1. Authorization 헤더에서 JWT 토큰 추출
        final String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 2. "Bearer " 이후의 토큰 문자열 추출
        final String jwt = authHeader.substring(7);

        // 3. 토큰에서 사용자명 추출
        final String username = jwtService.extractUsername(jwt);

        // 4. 아직 인증되지 않은 경우에만 처리
        if (username != null &&
                SecurityContextHolder.getContext().getAuthentication() == null) {

            UserDetails userDetails =
                    userDetailsService.loadUserByUsername(username);

            // 5. 토큰 유효성 검증
            if (jwtService.isTokenValid(jwt, userDetails)) {

                // 6. SecurityContext에 인증 정보 설정
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails, null,
                                userDetails.getAuthorities());

                authToken.setDetails(
                        new WebAuthenticationDetailsSource()
                                .buildDetails(request));

                SecurityContextHolder.getContext()
                        .setAuthentication(authToken);
            }
        }

        // 7. 다음 필터로 요청 전달
        filterChain.doFilter(request, response);
    }
}
```

---

## 4.5 SecurityFilterChain 설정

> **참조 코드**: `chapter07-spring-security/src/main/java/com/edu/security/config/SecurityConfig.java`

```java
@Configuration
@EnableWebSecurity       // Spring Security 활성화
@EnableMethodSecurity    // @PreAuthorize 등 메서드 레벨 보안 활성화
@RequiredArgsConstructor
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           JwtAuthenticationFilter jwtFilter)
            throws Exception {
        return http
                // CORS 설정 적용
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                // CSRF 비활성화 (JWT 사용 시 불필요)
                .csrf(csrf -> csrf.disable())
                // 세션 STATELESS (JWT로 인증하므로 서버 세션 불필요)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS))
                // URL별 접근 제어
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                // JWT 필터를 UsernamePasswordAuthenticationFilter 앞에 추가
                .addFilterBefore(jwtFilter,
                        UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public UserDetailsService userDetailsService(
            UserRepository userRepository) {
        return username -> userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "사용자를 찾을 수 없습니다: " + username));
    }
}
```

### 설정 요약

| 설정 | 이유 |
|------|------|
| **CSRF 비활성화** | JWT는 쿠키를 사용하지 않으므로 CSRF 공격에 안전하다 |
| **STATELESS 세션** | JWT 자체에 인증 정보가 포함되므로 서버 세션이 불필요하다 |
| **permitAll()** | 회원가입, 로그인 등 인증 없이 접근해야 하는 API |
| **hasRole("ADMIN")** | 관리자 전용 API |
| **authenticated()** | 로그인한 사용자만 접근 가능 |
| **addFilterBefore** | 모든 요청에서 JWT 토큰을 먼저 확인한다 |

---

## 4.6 Role 기반 접근 제어 (RBAC)

### Role enum 설계

> **참조 코드**: `chapter07-spring-security/src/main/java/com/edu/security/entity/Role.java`

```java
public enum Role {
    USER,   // 일반 사용자
    ADMIN   // 관리자
}
```

### User 엔티티에 Role 적용

> **참조 코드**: `chapter07-spring-security/src/main/java/com/edu/security/entity/User.java`

```java
@Entity
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    // Spring Security에서 hasRole("ADMIN")은 "ROLE_ADMIN" 권한을 확인
    // 반드시 "ROLE_" 접두어를 붙여야 함!
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    // UserDetails의 나머지 메서드들은 모두 true 반환
    // ...
}
```

### ROLE_ 접두어 규칙

```java
// Spring Security 내부에서 hasRole("ADMIN") 호출 시:
// → "ROLE_ADMIN" 권한을 가지고 있는지 확인

// 따라서 getAuthorities()에서 반드시 "ROLE_" 접두어를 붙여야 한다:
new SimpleGrantedAuthority("ROLE_" + role.name())
// Role.ADMIN → "ROLE_ADMIN"
// Role.USER  → "ROLE_USER"
```

### @PreAuthorize, @EnableMethodSecurity

```java
// SecurityConfig에서 활성화
@EnableMethodSecurity

// 컨트롤러에서 사용
@PreAuthorize("hasRole('ADMIN')")           // ADMIN만
@PreAuthorize("hasAnyRole('USER', 'ADMIN')") // USER 또는 ADMIN
@PreAuthorize("isAuthenticated()")           // 로그인한 사용자
@PreAuthorize("#username == authentication.name") // 본인만
```

---

## 4.7 CORS 설정

### CORS란?

**CORS (Cross-Origin Resource Sharing)** 는 브라우저가 다른 출처(origin)의 리소스에 접근할 수 있도록 허용하는 메커니즘이다. 프론트엔드와 백엔드가 다른 포트에서 실행될 때 필수이다.

```
프론트엔드 (http://localhost:3000)
    │
    │ API 요청 (다른 출처!)
    ▼
백엔드 (http://localhost:8080)
    │
    │ CORS 헤더가 없으면 → 브라우저가 응답 차단!
    │ CORS 헤더가 있으면 → 정상 응답
    ▼
```

### Spring Security에서 CORS 설정 방법

```java
@Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration config = new CorsConfiguration();
    // 허용할 출처 (프론트엔드 주소)
    config.setAllowedOrigins(List.of("http://localhost:3000"));
    // 허용할 HTTP 메서드
    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    // 허용할 헤더 (Authorization 헤더 포함)
    config.setAllowedHeaders(List.of("*"));
    // 자격 증명(쿠키 등) 포함 허용
    config.setAllowCredentials(true);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config);  // 모든 경로에 적용
    return source;
}
```

---

# Part 5: 테스트

## 5.1 테스트의 종류

### 테스트 피라미드

```
          /\
         /  \        E2E 테스트
        / E2E\       (느림, 적게)
       /──────\
      /        \     통합 테스트
     / 통합 테스트\   (중간)
    /────────────\
   /              \  단위 테스트
  /  단위 테스트    \ (빠름, 많이)
 /──────────────────\
```

### 각 테스트 유형별 비교

| 항목 | 단위 테스트 | 통합 테스트 | E2E 테스트 |
|------|-----------|-----------|-----------|
| **범위** | 클래스/메서드 | 여러 계층 | 전체 시스템 |
| **속도** | 매우 빠름 (ms) | 보통 (초) | 느림 (분) |
| **도구** | JUnit + Mockito | @SpringBootTest | Selenium, Postman |
| **DB** | Mock | H2 / Testcontainers | 실제 DB |
| **비용** | 낮음 | 중간 | 높음 |
| **목적** | 로직 정확성 | 계층 간 연동 | 사용자 시나리오 |

---

## 5.2 JUnit 5

### JUnit 5 구조

```
JUnit 5
├── JUnit Platform   ← 테스트 실행 엔진 (IDE, 빌드 도구와 통합)
├── JUnit Jupiter    ← 테스트 작성 API (우리가 사용하는 부분)
└── JUnit Vintage    ← JUnit 3/4 호환성 지원
```

### 핵심 어노테이션

| 어노테이션 | 설명 |
|-----------|------|
| `@Test` | 테스트 메서드 선언 |
| `@DisplayName` | 테스트 이름 지정 (한글 가능) |
| `@BeforeEach` | 각 테스트 실행 전에 실행 |
| `@AfterEach` | 각 테스트 실행 후에 실행 |
| `@BeforeAll` | 클래스의 모든 테스트 전에 한 번 실행 (static) |
| `@AfterAll` | 클래스의 모든 테스트 후에 한 번 실행 (static) |
| `@Nested` | 테스트를 그룹화 (내부 클래스) |
| `@Disabled` | 테스트 비활성화 (건너뜀) |
| `@ParameterizedTest` | 파라미터화된 테스트 |

### Assertions (검증)

```java
import static org.junit.jupiter.api.Assertions.*;

@Test
@DisplayName("기본 Assertions 예제")
void assertions() {
    // 값 동등 비교
    assertEquals(expected, actual);
    assertEquals(expected, actual, "실패 시 메시지");

    // 참/거짓 비교
    assertTrue(condition);
    assertFalse(condition);

    // null 비교
    assertNull(value);
    assertNotNull(value);

    // 예외 검증
    assertThrows(IllegalArgumentException.class, () -> {
        service.findById(999L);
    });

    // 여러 검증을 한 번에 (하나 실패해도 나머지 계속 실행)
    assertAll(
        () -> assertEquals("이름", result.getName()),
        () -> assertEquals("email@test.com", result.getEmail()),
        () -> assertNotNull(result.getId())
    );
}
```

### Parameterized Test

```java
// @ValueSource - 단일 값 파라미터
@ParameterizedTest
@ValueSource(strings = {"", " ", "  "})
@DisplayName("빈 문자열은 유효하지 않다")
void validateBlank(String input) {
    assertFalse(isValid(input));
}

// @CsvSource - CSV 형태의 여러 값
@ParameterizedTest
@CsvSource({
    "1, 2, 3",      // a=1, b=2, expected=3
    "10, 20, 30",
    "-1, 1, 0"
})
@DisplayName("덧셈 테스트")
void add(int a, int b, int expected) {
    assertEquals(expected, calculator.add(a, b));
}

// @MethodSource - 메서드에서 파라미터 제공
@ParameterizedTest
@MethodSource("provideProducts")
@DisplayName("상품 가격 검증")
void validatePrice(Product product, boolean expected) {
    assertEquals(expected, product.getPrice().compareTo(BigDecimal.ZERO) > 0);
}

static Stream<Arguments> provideProducts() {
    return Stream.of(
        Arguments.of(new Product("A", new BigDecimal("1000"), 1), true),
        Arguments.of(new Product("B", new BigDecimal("-100"), 1), false)
    );
}
```

---

## 5.3 Mockito

> **참조 코드**: `chapter08-testing/src/test/java/com/edu/testing/ProductServiceTest.java`

### Mock 객체 개념

**Mock 객체**는 실제 객체의 행동을 흉내 내는 가짜 객체이다. 테스트 대상이 의존하는 외부 객체(DB, 네트워크 등)를 대체하여 **격리된 테스트**를 가능하게 한다.

```
┌──────────────┐      ┌──────────────────┐
│ ProductService│─────▶│ ProductRepository │  ← 실제 객체 (DB 필요)
│ (테스트 대상) │      │   (의존 객체)      │
└──────────────┘      └──────────────────┘

         ↓ Mock으로 대체

┌──────────────┐      ┌──────────────────┐
│ ProductService│─────▶│ Mock Repository   │  ← 가짜 객체 (DB 불필요)
│ (테스트 대상) │      │ (행동을 정의)      │
└──────────────┘      └──────────────────┘
```

### @Mock, @InjectMocks, @ExtendWith

```java
@ExtendWith(MockitoExtension.class)  // Mockito 확장 활성화
@DisplayName("ProductService 단위 테스트 (Mockito)")
class ProductServiceTest {

    @Mock                               // Mock 객체 생성
    private ProductRepository productRepository;

    @InjectMocks                        // Mock을 주입받는 테스트 대상
    private ProductService productService;
}
```

### BDD 스타일 (Given-When-Then)

```java
@Test
@DisplayName("전체 상품 조회")
void findAll() {
    // Given - 테스트 데이터 준비 및 Mock 행동 정의
    List<Product> products = List.of(
            new Product("상품1", new BigDecimal("1000"), 10),
            new Product("상품2", new BigDecimal("2000"), 20)
    );
    given(productRepository.findAll()).willReturn(products);

    // When - 테스트 대상 실행
    List<Product> result = productService.findAll();

    // Then - 결과 검증
    assertEquals(2, result.size());
    verify(productRepository, times(1)).findAll();
}

@Test
@DisplayName("ID로 상품 조회 - 존재하지 않는 경우")
void findById_notFound() {
    // Given
    given(productRepository.findById(999L)).willReturn(Optional.empty());

    // When & Then
    IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> productService.findById(999L)
    );
    assertTrue(exception.getMessage().contains("999"));
}
```

### 주요 메서드

| 메서드 | 설명 |
|--------|------|
| `given(mock.method()).willReturn(value)` | Mock 메서드 호출 시 반환값 정의 |
| `given(mock.method()).willThrow(exception)` | Mock 메서드 호출 시 예외 발생 |
| `verify(mock).method()` | 해당 메서드가 호출되었는지 검증 |
| `verify(mock, times(n)).method()` | 정확히 n번 호출되었는지 검증 |
| `verify(mock, never()).method()` | 한 번도 호출되지 않았는지 검증 |
| `any(Class.class)` | 어떤 값이든 매칭 |

---

## 5.4 Spring Boot 테스트 슬라이스

### 비교표

| 어노테이션 | 범위 | 로드하는 Bean | 속도 | 용도 |
|-----------|------|-------------|------|------|
| `@SpringBootTest` | 전체 | 모든 Bean | 느림 | 통합 테스트 |
| `@WebMvcTest` | Controller | Controller + Security | 빠름 | API 테스트 |
| `@DataJpaTest` | Repository | JPA 관련 Bean | 보통 | DB 테스트 |
| `@JsonTest` | JSON | Jackson 관련 Bean | 매우 빠름 | 직렬화 테스트 |

### MockMvc 사용법

```java
@WebMvcTest(TodoController.class)
class TodoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean   // Spring Context의 Bean을 Mock으로 대체 (Spring Boot 3.4부터 @MockitoBean으로 대체됨)
    private TodoService todoService;

    @Test
    @DisplayName("할일 전체 조회 API")
    void getAllTodos() throws Exception {
        // Given
        given(todoService.getAllTodos()).willReturn(List.of(
                new TodoResponse(1L, "할일1", "설명1", false, LocalDateTime.now())
        ));

        // When & Then
        mockMvc.perform(get("/api/todos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("할일1"))
                .andDo(print());
    }

    @Test
    @DisplayName("할일 생성 API - 유효성 검증 실패")
    void createTodo_validationFail() throws Exception {
        mockMvc.perform(post("/api/todos")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\": \"\"}"))   // 빈 제목 (NotBlank 위반)
                .andExpect(status().isBadRequest());
    }
}
```

### @MockBean vs @Mock 차이

| 항목 | `@MockBean` | `@Mock` |
|------|------------|---------|
| 소속 | Spring Boot Test | Mockito |
| 컨텍스트 | Spring ApplicationContext의 Bean을 대체 | 독립적 Mock 생성 |
| 사용 위치 | `@WebMvcTest`, `@SpringBootTest` | `@ExtendWith(MockitoExtension.class)` |
| 용도 | Spring 컨테이너와 함께 테스트할 때 | 순수 단위 테스트 |

---

## 5.5 Testcontainers

> **참조 코드**: `chapter08-testing/src/test/java/com/edu/testing/ProductRepositoryTestcontainersTest.java`

### Testcontainers란?

**Testcontainers**는 Docker 컨테이너를 사용하여 실제 데이터베이스, 메시지 큐 등을 테스트 환경에서 자동으로 실행하고 관리하는 라이브러리이다.

### H2 vs Testcontainers 비교

| 항목 | H2 (인메모리 DB) | Testcontainers |
|------|----------------|---------------|
| 환경 | JVM 내부 | Docker 컨테이너 |
| DB 호환성 | 운영 DB와 차이 있음 | 운영 DB와 동일 |
| 속도 | 매우 빠름 | 컨테이너 시작 시간 필요 |
| 사전 요구사항 | 없음 | Docker 설치 필요 |
| SQL 호환성 | 일부 DB 전용 문법 미지원 | 완벽 호환 |
| 권장 사용 | 간단한 테스트 | 실제 환경에 가까운 테스트 |

### 동작 흐름

```
테스트 실행
    │
    ▼
@Container: Docker에서 PostgreSQL 컨테이너 시작
    │              (랜덤 포트 할당)
    ▼
@DynamicPropertySource: DataSource URL을 컨테이너 주소로 설정
    │
    ▼
@DataJpaTest: JPA Repository 테스트 실행
    │              (실제 PostgreSQL에서 쿼리 실행)
    ▼
테스트 완료 → 컨테이너 자동 종료 및 정리
```

### PostgreSQLContainer 예제

```java
@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DisplayName("ProductRepository 통합 테스트 (Testcontainers)")
class ProductRepositoryTestcontainersTest {

    // Docker로 PostgreSQL 컨테이너를 자동 실행
    // static: 모든 테스트가 하나의 컨테이너를 공유 (성능 최적화)
    @Container
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16-alpine")
                .withDatabaseName("test_db")
                .withUsername("test")
                .withPassword("test");

    // 동적으로 데이터소스 설정을 Testcontainers의 PostgreSQL로 변경
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();
        productRepository.save(new Product("노트북", new BigDecimal("1500000"), 10));
        productRepository.save(new Product("마우스", new BigDecimal("50000"), 100));
    }

    @Test
    @DisplayName("PostgreSQL 컨테이너가 실행 중인지 확인")
    void containerIsRunning() {
        assertTrue(postgres.isRunning());
    }

    @Test
    @DisplayName("이름으로 검색")
    void findByNameContaining() {
        List<Product> result = productRepository.findByNameContaining("노트북");
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("상품 저장 및 조회")
    void saveAndFind() {
        Product saved = productRepository.save(
                new Product("모니터", new BigDecimal("500000"), 20));

        assertNotNull(saved.getId());

        Product found = productRepository.findById(saved.getId()).orElseThrow();
        assertEquals("모니터", found.getName());
    }
}
```

---

# Part 6: 종합 프로젝트 - 게시판 API

## 6.1 프로젝트 아키텍처

> **참조 코드**: `chapter09-final-project/`

### 레이어드 아키텍처

```
┌─────────────────────────────────────────────┐
│              Client (브라우저/Postman)         │
└──────────────────┬──────────────────────────┘
                   │ HTTP 요청/응답
┌──────────────────▼──────────────────────────┐
│ Controller 계층                              │
│ (AuthController, PostController,             │
│  CommentController)                          │
│ - HTTP 요청 수신, 응답 반환                    │
│ - @Valid 검증                                │
│ - @AuthenticationPrincipal 사용자 확인         │
├──────────────────┬──────────────────────────┤
│ Service 계층     │                           │
│ (AuthService, PostService, CommentService,   │
│  JwtService)                                 │
│ - 비즈니스 로직 처리                           │
│ - 트랜잭션 관리 (@Transactional)              │
│ - 권한 검증                                   │
├──────────────────┬──────────────────────────┤
│ Repository 계층  │                           │
│ (UserRepository, PostRepository,             │
│  CommentRepository)                          │
│ - DB CRUD 처리                               │
│ - JpaRepository 상속                         │
├──────────────────┬──────────────────────────┤
│ Database (PostgreSQL)                        │
└──────────────────────────────────────────────┘
```

### 패키지 구조

```
com.edu.board
├── Chapter09Application.java    ← 메인 클래스
├── config/
│   ├── SecurityConfig.java      ← Spring Security 설정
│   └── JwtAuthenticationFilter.java ← JWT 필터
├── controller/
│   ├── AuthController.java      ← 인증 API (회원가입, 로그인)
│   ├── PostController.java      ← 게시글 API
│   └── CommentController.java   ← 댓글 API
├── service/
│   ├── JwtService.java          ← JWT 토큰 생성/검증
│   ├── AuthService.java         ← 인증 비즈니스 로직
│   ├── PostService.java         ← 게시글 비즈니스 로직
│   └── CommentService.java      ← 댓글 비즈니스 로직
├── repository/
│   ├── UserRepository.java
│   ├── PostRepository.java
│   └── CommentRepository.java
├── entity/
│   ├── User.java                ← 사용자 엔티티 (UserDetails 구현)
│   ├── Role.java                ← 역할 enum (USER, ADMIN)
│   ├── Post.java                ← 게시글 엔티티
│   └── Comment.java             ← 댓글 엔티티
├── dto/
│   ├── SignUpRequest.java       ← 회원가입 요청
│   ├── LoginRequest.java        ← 로그인 요청
│   ├── AuthResponse.java        ← 인증 응답 (JWT 토큰)
│   ├── PostRequest.java         ← 게시글 작성/수정 요청
│   ├── PostResponse.java        ← 게시글 상세 응답
│   ├── PostListResponse.java    ← 게시글 목록 응답
│   ├── CommentRequest.java      ← 댓글 작성 요청
│   ├── CommentResponse.java     ← 댓글 응답
│   └── PageResponse.java        ← 페이징 응답
└── exception/
    ├── ResourceNotFoundException.java
    ├── UnauthorizedException.java
    └── GlobalExceptionHandler.java
```

---

## 6.2 기능 명세

### Auth API 명세

| 메서드 | URL | 설명 | 인증 |
|--------|-----|------|------|
| POST | `/api/auth/signup` | 회원가입 | 불필요 |
| POST | `/api/auth/login` | 로그인 | 불필요 |

### Post API 명세

| 메서드 | URL | 설명 | 인증 |
|--------|-----|------|------|
| GET | `/api/posts` | 게시글 목록 (페이징) | 필요 |
| GET | `/api/posts/{id}` | 게시글 상세 | 필요 |
| POST | `/api/posts` | 게시글 작성 | 필요 |
| PUT | `/api/posts/{id}` | 게시글 수정 (작성자만) | 필요 |
| DELETE | `/api/posts/{id}` | 게시글 삭제 (작성자/관리자) | 필요 |

### Comment API 명세

| 메서드 | URL | 설명 | 인증 |
|--------|-----|------|------|
| GET | `/api/posts/{postId}/comments` | 댓글 목록 | 필요 |
| POST | `/api/posts/{postId}/comments` | 댓글 작성 | 필요 |
| PUT | `/api/comments/{id}` | 댓글 수정 (작성자만) | 필요 |
| DELETE | `/api/comments/{id}` | 댓글 삭제 (작성자/관리자) | 필요 |

### 요청/응답 JSON 예시

**회원가입 요청**:
```json
POST /api/auth/signup
{
  "username": "testuser",
  "password": "password123",
  "role": "USER"
}
```

**회원가입/로그인 응답**:
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

**게시글 작성 요청**:
```json
POST /api/posts
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
{
  "title": "첫 번째 게시글",
  "content": "안녕하세요, 게시판입니다!"
}
```

**게시글 상세 응답**:
```json
{
  "id": 1,
  "title": "첫 번째 게시글",
  "content": "안녕하세요, 게시판입니다!",
  "author": "testuser",
  "viewCount": 5,
  "comments": [
    {
      "id": 1,
      "content": "좋은 글이네요!",
      "author": "user2",
      "createdAt": "2026-03-17T10:30:00"
    }
  ],
  "createdAt": "2026-03-17T10:00:00",
  "updatedAt": "2026-03-17T10:00:00"
}
```

---

## 6.3 Docker Compose 구성

> **참조 코드**: `chapter09-final-project/docker-compose.yml`

### 전체 docker-compose.yml

```yaml
services:
  # PostgreSQL 데이터베이스
  postgres:
    image: postgres:16-alpine
    environment:
      POSTGRES_DB: edu_spring
      POSTGRES_USER: edu
      POSTGRES_PASSWORD: edu1234
    ports:
      - "5432:5432"
    volumes:
      - pgdata:/var/lib/postgresql/data
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U edu"]
      interval: 5s
      timeout: 5s
      retries: 5

  # Redis (추후 캐시/세션 확장용)
  redis:
    image: redis:7-alpine
    ports:
      - "6379:6379"

  # Spring Boot 애플리케이션
  app:
    build: .
    ports:
      - "8080:8080"
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/edu_spring
      SPRING_DATASOURCE_USERNAME: edu
      SPRING_DATASOURCE_PASSWORD: edu1234
      JWT_SECRET: mySecretKeyForBoardProjectJwtTokenGeneration1234567890abcdef
    depends_on:
      postgres:
        condition: service_healthy

  # Adminer - DB 관리 웹 UI (http://localhost:8081)
  adminer:
    image: adminer
    ports:
      - "8081:8080"
    depends_on:
      - postgres

volumes:
  pgdata:
```

### 서비스별 역할

| 서비스 | 이미지 | 포트 | 역할 |
|--------|--------|------|------|
| **postgres** | postgres:16-alpine | 5432 | 메인 데이터베이스 |
| **redis** | redis:7-alpine | 6379 | 캐시/세션 저장소 (확장용) |
| **app** | 빌드 이미지 | 8080 | Spring Boot 애플리케이션 |
| **adminer** | adminer | 8081 | DB 관리 웹 UI |

### 실행/중지 명령어

```bash
# 전체 환경 실행 (빌드 포함)
docker compose up --build

# 백그라운드 실행
docker compose up -d --build

# 종료
docker compose down

# DB 데이터 포함 종료 (볼륨 삭제)
docker compose down -v

# 로그 확인
docker compose logs -f app

# 특정 서비스만 재시작
docker compose restart app
```

### 환경변수 설정

Docker Compose에서 환경변수를 통해 `application.yml`의 설정을 오버라이드한다.

```yaml
environment:
  # Spring Boot는 환경변수를 자동으로 프로퍼티로 변환
  # SPRING_DATASOURCE_URL → spring.datasource.url
  SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/edu_spring
  SPRING_DATASOURCE_USERNAME: edu
  SPRING_DATASOURCE_PASSWORD: edu1234
  JWT_SECRET: mySecretKey...
```

---

## 6.4 curl로 API 테스트하기

### 전체 흐름 스크립트

```bash
# ========================================
# 1. 회원가입
# ========================================
curl -X POST http://localhost:8080/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password123",
    "role": "USER"
  }'
# 응답: {"token": "eyJhbGciOiJIUzI1NiJ9..."}

# 관리자 계정 생성
curl -X POST http://localhost:8080/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin123",
    "role": "ADMIN"
  }'

# ========================================
# 2. 로그인 → 토큰 저장
# ========================================
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password123"
  }' | jq -r '.token')

echo "TOKEN: $TOKEN"

# ========================================
# 3. 게시글 CRUD
# ========================================

# 게시글 작성
curl -X POST http://localhost:8080/api/posts \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "title": "첫 번째 게시글",
    "content": "Spring Boot로 만든 게시판입니다!"
  }'

# 게시글 목록 조회 (페이징)
curl -X GET "http://localhost:8080/api/posts?page=0&size=10" \
  -H "Authorization: Bearer $TOKEN"

# 게시글 상세 조회
curl -X GET http://localhost:8080/api/posts/1 \
  -H "Authorization: Bearer $TOKEN"

# 게시글 수정
curl -X PUT http://localhost:8080/api/posts/1 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "title": "수정된 제목",
    "content": "수정된 내용입니다."
  }'

# 게시글 삭제
curl -X DELETE http://localhost:8080/api/posts/1 \
  -H "Authorization: Bearer $TOKEN"

# ========================================
# 4. 댓글 CRUD
# ========================================

# 댓글 작성
curl -X POST http://localhost:8080/api/posts/1/comments \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "content": "좋은 글이네요!"
  }'

# 댓글 목록 조회
curl -X GET http://localhost:8080/api/posts/1/comments \
  -H "Authorization: Bearer $TOKEN"

# 댓글 수정
curl -X PUT http://localhost:8080/api/comments/1 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "content": "수정된 댓글입니다."
  }'

# 댓글 삭제
curl -X DELETE http://localhost:8080/api/comments/1 \
  -H "Authorization: Bearer $TOKEN"
```

---

# Part 7: Docker 활용 가이드

## 7.1 Docker 기초

### Docker란?

Docker는 애플리케이션을 **컨테이너**라는 격리된 환경에서 실행하는 플랫폼이다. "내 컴퓨터에서는 되는데, 서버에서는 안 된다" 문제를 해결한다.

### 이미지 vs 컨테이너

| 항목 | 이미지 (Image) | 컨테이너 (Container) |
|------|--------------|---------------------|
| 비유 | 클래스 (설계도) | 인스턴스 (실행체) |
| 상태 | 읽기 전용 (불변) | 읽기/쓰기 가능 |
| 생명주기 | 빌드 시 생성 | 실행/중지/삭제 가능 |
| 예시 | `postgres:16-alpine` | 실행 중인 PostgreSQL |

### Dockerfile 작성법

```dockerfile
# 베이스 이미지 지정
FROM eclipse-temurin:21-jre-alpine

# 작업 디렉토리 설정
WORKDIR /app

# 파일 복사
COPY build/libs/*.jar app.jar

# 포트 노출 (문서화 목적)
EXPOSE 8080

# 실행 명령
ENTRYPOINT ["java", "-jar", "app.jar"]
```

---

## 7.2 멀티 스테이지 빌드

> **참조 코드**: `chapter09-final-project/Dockerfile`

### 빌드 스테이지와 실행 스테이지 분리

```dockerfile
# ===== 1단계: 빌드 =====
FROM gradle:8.8-jdk21 AS builder

WORKDIR /app

# Gradle 설정 파일 먼저 복사 (의존성 캐싱 최적화)
COPY build.gradle settings.gradle ./
COPY gradle ./gradle

# 의존성 먼저 다운로드 (소스 변경 시 재다운로드 방지)
RUN gradle dependencies --no-daemon || true

# 소스 코드 복사
COPY src ./src

# 테스트 제외하고 빌드
RUN gradle bootJar --no-daemon -x test

# ===== 2단계: 실행 =====
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# 빌드 결과물만 복사 (소스 코드, 빌드 도구는 제외)
COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
```

### 이미지 크기 최적화

| 항목 | 단일 스테이지 | 멀티 스테이지 |
|------|-------------|-------------|
| 빌드 이미지 | `gradle:8.8-jdk21` (약 800MB) | 빌드 후 버림 |
| 실행 이미지 | 동일 (800MB) | `eclipse-temurin:21-jre-alpine` (약 100MB) |
| 포함 내용 | JDK + Gradle + 소스 + JAR | JRE + JAR만 |

> **핵심**: 빌드에는 JDK가 필요하지만, 실행에는 JRE만 있으면 충분하다. 멀티 스테이지 빌드로 이미지 크기를 대폭 줄일 수 있다.

---

## 7.3 Docker Compose

### docker-compose.yml 구조

```yaml
services:           # 서비스 정의
  service-name:     # 서비스 이름
    image: ...      # 사용할 이미지
    build: .        # 또는 Dockerfile로 빌드
    ports:          # 포트 매핑 (호스트:컨테이너)
    environment:    # 환경변수
    volumes:        # 볼륨 마운트
    depends_on:     # 의존 관계
    healthcheck:    # 상태 확인

volumes:            # 볼륨 정의
networks:           # 네트워크 정의
```

### depends_on, healthcheck

```yaml
services:
  postgres:
    image: postgres:16-alpine
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U edu"]  # 상태 확인 명령
      interval: 5s    # 확인 간격
      timeout: 5s     # 타임아웃
      retries: 5      # 재시도 횟수

  app:
    build: .
    depends_on:
      postgres:
        condition: service_healthy  # PostgreSQL이 healthy가 된 후 실행
```

### volumes, networks

```yaml
services:
  postgres:
    volumes:
      # Named Volume - 데이터 영속화
      - pgdata:/var/lib/postgresql/data
      # Bind Mount - 초기화 SQL 파일 마운트
      - ./init-db.sql:/docker-entrypoint-initdb.d/init.sql

volumes:
  pgdata:  # Named Volume 선언
```

### 주요 명령어

| 명령어 | 설명 |
|--------|------|
| `docker compose up` | 모든 서비스 시작 |
| `docker compose up -d` | 백그라운드 실행 |
| `docker compose up --build` | 이미지 재빌드 후 실행 |
| `docker compose down` | 모든 서비스 종료 |
| `docker compose down -v` | 종료 + 볼륨 삭제 |
| `docker compose logs` | 전체 로그 확인 |
| `docker compose logs -f app` | 특정 서비스 로그 실시간 확인 |
| `docker compose exec postgres psql -U edu` | 컨테이너 내부 명령 실행 |
| `docker compose ps` | 실행 중인 서비스 확인 |
| `docker compose restart app` | 특정 서비스 재시작 |

---

## 7.4 개발환경 구성

> **참조 코드**: `docker/docker-compose-infra.yml`

### PostgreSQL, Redis, Adminer 실행

인프라만 Docker로 실행하고, 애플리케이션은 IDE에서 직접 실행하는 방식:

```yaml
# docker/docker-compose-infra.yml
services:
  postgres:
    image: postgres:16-alpine
    environment:
      POSTGRES_DB: edu_spring
      POSTGRES_USER: edu
      POSTGRES_PASSWORD: edu1234
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data
      - ./init-db.sql:/docker-entrypoint-initdb.d/init.sql
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U edu"]
      interval: 5s
      timeout: 5s
      retries: 5

  redis:
    image: redis:7-alpine
    ports:
      - "6379:6379"

  adminer:
    image: adminer
    ports:
      - "8081:8080"
    depends_on:
      - postgres

volumes:
  postgres_data:
```

```bash
# 인프라만 실행
cd docker
docker compose -f docker-compose-infra.yml up -d

# 애플리케이션은 IDE에서 실행 (application.yml 사용)
```

### 포트 매핑 정리표

| 서비스 | 호스트 포트 | 컨테이너 포트 | 접근 URL |
|--------|-----------|-------------|----------|
| Spring Boot | 8080 | 8080 | http://localhost:8080 |
| PostgreSQL | 5432 | 5432 | `jdbc:postgresql://localhost:5432/edu_spring` |
| Redis | 6379 | 6379 | `localhost:6379` |
| Adminer | 8081 | 8080 | http://localhost:8081 |

---

# 부록

## Spring Boot 주요 Starter 목록

| Starter | 용도 |
|---------|------|
| `spring-boot-starter-web` | Web MVC, REST API, 내장 Tomcat |
| `spring-boot-starter-data-jpa` | JPA, Hibernate |
| `spring-boot-starter-security` | Spring Security |
| `spring-boot-starter-validation` | Bean Validation (JSR-380) |
| `spring-boot-starter-test` | JUnit 5, Mockito, MockMvc |
| `spring-boot-starter-actuator` | 모니터링, 헬스 체크 |
| `spring-boot-starter-mail` | 이메일 발송 |
| `spring-boot-starter-cache` | 캐시 추상화 |
| `spring-boot-starter-data-redis` | Redis |
| `spring-boot-starter-oauth2-client` | OAuth2 클라이언트 |
| `spring-boot-starter-thymeleaf` | Thymeleaf 템플릿 엔진 |
| `spring-boot-starter-websocket` | WebSocket |
| `spring-boot-starter-batch` | Spring Batch |
| `spring-boot-starter-amqp` | RabbitMQ |

## HTTP 상태 코드 전체 정리표

### 2xx 성공

| 코드 | 이름 | 설명 | 사용 예시 |
|------|------|------|----------|
| 200 | OK | 요청 성공 | GET 조회, PUT 수정 성공 |
| 201 | Created | 리소스 생성 성공 | POST 생성 성공 |
| 202 | Accepted | 요청 접수 (비동기 처리) | 비동기 작업 요청 |
| 204 | No Content | 성공, 응답 본문 없음 | DELETE 삭제 성공 |

### 3xx 리다이렉션

| 코드 | 이름 | 설명 |
|------|------|------|
| 301 | Moved Permanently | 영구 이동 |
| 302 | Found | 임시 이동 |
| 304 | Not Modified | 캐시 사용 |

### 4xx 클라이언트 에러

| 코드 | 이름 | 설명 | 사용 예시 |
|------|------|------|----------|
| 400 | Bad Request | 잘못된 요청 | 유효성 검증 실패 |
| 401 | Unauthorized | 인증 실패 | 토큰 없음/만료 |
| 403 | Forbidden | 권한 부족 | ADMIN만 접근 가능 |
| 404 | Not Found | 리소스 없음 | 존재하지 않는 ID |
| 405 | Method Not Allowed | 허용되지 않은 메서드 | GET 전용 URL에 POST |
| 409 | Conflict | 충돌 | 중복 데이터 |
| 422 | Unprocessable Entity | 처리 불가 | 비즈니스 규칙 위반 |
| 429 | Too Many Requests | 요청 초과 | Rate Limit |

### 5xx 서버 에러

| 코드 | 이름 | 설명 |
|------|------|------|
| 500 | Internal Server Error | 서버 내부 오류 |
| 502 | Bad Gateway | 게이트웨이 오류 |
| 503 | Service Unavailable | 서비스 불가 |
| 504 | Gateway Timeout | 게이트웨이 타임아웃 |

## application.yml 주요 설정 레퍼런스

```yaml
# ========================================
# 서버 설정
# ========================================
server:
  port: 8080                          # 서버 포트
  servlet:
    context-path: /api                # 컨텍스트 경로

# ========================================
# Spring 기본 설정
# ========================================
spring:
  application:
    name: my-app                      # 애플리케이션 이름

  profiles:
    active: dev                       # 활성 프로필

# ========================================
# 데이터소스 설정
# ========================================
  datasource:
    url: jdbc:postgresql://localhost:5432/mydb
    username: myuser
    password: mypassword
    driver-class-name: org.postgresql.Driver

# ========================================
# JPA 설정
# ========================================
  jpa:
    hibernate:
      ddl-auto: update                # 스키마 자동 관리
      # none: 아무것도 안 함 (운영)
      # update: 변경사항만 반영 (개발)
      # create: 매번 테이블 새로 생성
      # create-drop: 생성 후 종료 시 삭제
      # validate: 스키마 검증만
    show-sql: true                    # SQL 로그 출력
    properties:
      hibernate:
        format_sql: true              # SQL 포맷팅
        default_batch_fetch_size: 100 # N+1 문제 완화
    open-in-view: false               # OSIV 비활성화 (권장)

# ========================================
# 로깅 설정
# ========================================
logging:
  level:
    root: INFO
    com.edu: DEBUG                    # 패키지별 로그 레벨
    org.hibernate.SQL: DEBUG          # SQL 로그
    org.hibernate.orm.jdbc.bind: TRACE       # 바인딩 파라미터 로그 (Hibernate 6+)

# ========================================
# JWT 설정
# ========================================
jwt:
  secret: mySecretKeyBase64Encoded...
  expiration: 86400000                # 24시간 (밀리초)
```

## 추천 학습 리소스

### 공식 문서

| 리소스 | URL |
|--------|-----|
| Spring Boot Reference | https://docs.spring.io/spring-boot/reference/ |
| Spring Data JPA Reference | https://docs.spring.io/spring-data/jpa/reference/ |
| Spring Security Reference | https://docs.spring.io/spring-security/reference/ |
| Baeldung (튜토리얼) | https://www.baeldung.com/ |

### 추천 학습 순서

```
1. Java 기초 (chapter01-03)
   ├── 변수, 제어문, 배열, 메서드
   ├── OOP (상속, 다형성, 인터페이스)
   └── 컬렉션, 제네릭, 람다, Stream
        │
2. Spring Boot 기초 (chapter04)         ← Part 1
   ├── IoC/DI 이해
   ├── Bean 등록 및 생명주기
   └── Profile 설정
        │
3. REST API 개발 (chapter05)             ← Part 2
   ├── @RestController, HTTP 메서드
   ├── DTO, Validation
   └── 전역 예외 처리
        │
4. JPA / DB 연동 (chapter06)             ← Part 3
   ├── Entity 매핑
   ├── JpaRepository
   └── 연관관계, 페이징
        │
5. Spring Security (chapter07)           ← Part 4
   ├── 인증/인가 개념
   ├── JWT 구현
   └── RBAC
        │
6. 테스트 (chapter08)                    ← Part 5
   ├── JUnit 5 + Mockito
   ├── Spring Boot 테스트 슬라이스
   └── Testcontainers
        │
7. 종합 프로젝트 (chapter09)             ← Part 6
   └── 게시판 API (전체 통합)
```

---

> 이 교육자료는 `edu_spring` 프로젝트의 chapter04 ~ chapter09 코드를 기반으로 작성되었다.
> 각 장의 소스 코드를 직접 실행하면서 학습하면 더욱 효과적이다.
