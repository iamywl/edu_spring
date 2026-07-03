# Chapter 04: Spring Boot Introduction

> **🐳 실습 환경 — 이 장은 `spring-ch04-intro` 컨테이너로 실습한다 (DB 불필요)**
> ```bash
> cd spring/chapter04-spring-boot-intro && docker compose up --build
> ```
> 컨테이너 상태 확인: `docker ps` (8080 포트 — 다른 챕터 앱과 동시 실행 시 포트 충돌 주의)

## 목차
1. [Spring Framework란?](#1-spring-framework란)
2. [Spring Boot란?](#2-spring-boot란)
3. [IoC/DI 개념](#3-iocdi-개념)
4. [Bean과 Component Scan](#4-bean과-component-scan)
5. [Spring Boot 자동 설정](#5-spring-boot-자동-설정)
6. [Profile 설정](#6-profile-설정)
7. [@Qualifier와 @Primary (동일 타입 Bean 선택)](#7-qualifier와-primary-동일-타입-bean-선택)
8. [@ConfigurationProperties와 @Value (외부 설정 주입)](#8-configurationproperties와-value-외부-설정-주입)
9. [AOP — 공통 관심사 분리](#9-aop--공통-관심사-분리)
10. [Docker로 실행하기](#10-docker로-실행하기)

---

## 1. Spring Framework란?

### 개요

Spring Framework는 Java 기반의 엔터프라이즈 애플리케이션을 개발하기 위한 오픈소스 프레임워크입니다. 2003년 Rod Johnson이 처음 만들었으며, 현재 Java 생태계에서 가장 널리 사용되는 프레임워크입니다.

### 핵심 철학

- **POJO(Plain Old Java Object) 기반 개발**: 특별한 클래스를 상속받지 않아도 됩니다.
- **제어의 역전(IoC)**: 객체의 생성과 관리를 프레임워크에 위임합니다.
- **관점 지향 프로그래밍(AOP)**: 횡단 관심사를 분리하여 모듈화합니다.
- **느슨한 결합(Loose Coupling)**: 인터페이스 기반 설계로 유연한 구조를 만듭니다.

### Spring의 주요 모듈

```
Spring Framework
├── Spring Core       - IoC/DI 컨테이너
├── Spring MVC        - 웹 애플리케이션 (Controller, View, Model)
├── Spring Data       - 데이터 접근 계층 (JPA, JDBC 등)
├── Spring Security   - 인증/인가
├── Spring AOP        - 관점 지향 프로그래밍
└── Spring Test       - 테스트 지원
```

---

## 2. Spring Boot란?

### Spring vs Spring Boot

Spring Framework는 강력하지만, 초기 설정이 복잡합니다. XML 설정 파일을 작성하고, 의존성 버전을 직접 관리하며, 서버를 별도로 설정해야 했습니다. Spring Boot는 이러한 불편함을 해결하기 위해 등장했습니다.

### Spring Boot의 핵심 특징

| 특징 | 설명 |
|------|------|
| **자동 설정 (Auto Configuration)** | 의존성을 추가하면 관련 설정을 자동으로 해줍니다 |
| **내장 서버** | Tomcat이 내장되어 있어 별도의 서버 설치가 불필요합니다 |
| **스타터 의존성** | `spring-boot-starter-*`로 필요한 의존성을 한 번에 추가합니다 |
| **설정의 간소화** | `application.yml` 또는 `application.properties` 파일로 설정합니다 |

### @SpringBootApplication 어노테이션

```java
// @SpringBootApplication은 아래 3개 어노테이션의 조합입니다
@SpringBootApplication
// = @Configuration       : 설정 클래스로 지정
// + @EnableAutoConfiguration : 자동 설정 활성화
// + @ComponentScan       : 현재 패키지 하위의 컴포넌트를 스캔
public class Chapter04Application {
    public static void main(String[] args) {
        SpringApplication.run(Chapter04Application.class, args);
    }
}
```

### Spring Boot 실행 흐름

```
1. main() 메서드 실행
2. SpringApplication.run() 호출
3. ApplicationContext (IoC 컨테이너) 생성
4. @ComponentScan으로 Bean 스캔 및 등록
5. Auto Configuration 적용
6. 내장 Tomcat 서버 시작
7. 애플리케이션 Ready!
```

---

## 3. IoC/DI 개념

### IoC (Inversion of Control, 제어의 역전)

일반적으로 개발자가 직접 객체를 생성하고 관리합니다. IoC는 이 제어권을 프레임워크(Spring 컨테이너)에 넘기는 것을 말합니다.

#### 기존 방식 (개발자가 직접 제어)

```java
public class OrderService {
    // 개발자가 직접 객체를 생성 (강한 결합)
    private PaymentService paymentService = new KakaoPayService();

    public void order() {
        paymentService.pay();
    }
}
```

이 방식의 문제점:
- `OrderService`가 `KakaoPayService`에 직접 의존합니다.
- 결제 수단을 변경하려면 코드를 수정해야 합니다.
- 테스트 시 Mock 객체로 대체하기 어렵습니다.

#### IoC 방식 (프레임워크가 제어)

```java
public class OrderService {
    // 인터페이스에 의존 (느슨한 결합)
    private final PaymentService paymentService;

    // 생성자를 통해 외부에서 주입받음
    public OrderService(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    public void order() {
        paymentService.pay();
    }
}
```

### DI (Dependency Injection, 의존성 주입)

DI는 IoC를 구현하는 방법 중 하나입니다. 필요한 객체를 외부에서 주입해주는 방식입니다.

#### 의존성 주입 3가지 방법

**1. 생성자 주입 (권장)**
```java
@RestController
public class HelloController {
    private final GreetingService greetingService;

    // 생성자가 하나면 @Autowired 생략 가능
    public HelloController(GreetingService greetingService) {
        this.greetingService = greetingService;
    }
}
```
- `final` 키워드 사용 가능 (불변성 보장)
- 필수 의존성을 강제할 수 있음
- 테스트가 용이함
- **Spring 공식 권장 방식**

**2. 필드 주입 (비권장)**
```java
@RestController
public class HelloController {
    @Autowired
    private GreetingService greetingService;
}
```
- 코드가 간결하지만 테스트가 어려움
- `final` 사용 불가

**3. Setter 주입**
```java
@RestController
public class HelloController {
    private GreetingService greetingService;

    @Autowired
    public void setGreetingService(GreetingService greetingService) {
        this.greetingService = greetingService;
    }
}
```
- 선택적 의존성에 사용

---

## 4. Bean과 Component Scan

### Bean이란?

**Bean**은 Spring IoC 컨테이너가 관리하는 객체입니다. 개발자가 직접 `new`로 생성하는 것이 아니라, Spring이 생성하고 관리합니다.

### Bean 등록 방법

#### 방법 1: @Component 계열 어노테이션 (자동 등록)

```java
@Component          // 일반 컴포넌트
@Service            // 서비스 계층 (비즈니스 로직)
@Repository         // 데이터 접근 계층
@Controller         // MVC 컨트롤러
@RestController     // REST API 컨트롤러 (@Controller + @ResponseBody)
```

이 어노테이션들은 모두 내부적으로 `@Component`를 포함하고 있습니다.

```java
@Service  // Spring이 자동으로 Bean으로 등록
public class KoreanGreetingService implements GreetingService {
    @Override
    public String greet(String name) {
        return "안녕하세요, " + name + "님!";
    }
}
```

#### 방법 2: @Configuration + @Bean (수동 등록)

직접 만들지 않은 외부 라이브러리의 클래스를 Bean으로 등록할 때 사용합니다.

```java
@Configuration
public class AppConfig {

    @Bean  // 반환 객체를 Bean으로 등록 (Bean 이름 = 메서드 이름)
    public DateTimeFormatter dateTimeFormatter() {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    }
}
```

### Component Scan

`@ComponentScan`은 지정된 패키지 하위에서 `@Component` 계열 어노테이션이 붙은 클래스를 찾아 자동으로 Bean으로 등록합니다.

```
com.edu.intro                          <- @SpringBootApplication이 여기에 있으면
├── Chapter04Application.java           <- 이 패키지부터 하위를 모두 스캔
├── HelloController.java                <- @RestController -> Bean 등록
├── KoreanGreetingService.java          <- @Service -> Bean 등록
├── EnglishGreetingService.java         <- @Service -> Bean 등록
├── AppConfig.java                      <- @Configuration -> Bean 등록
└── BeanLifecycleExample.java           <- @Component -> Bean 등록
```

### Bean 생명주기

```
1. 스프링 컨테이너 생성
2. Bean 인스턴스 생성 (생성자 호출)
3. 의존성 주입 (@Autowired 등)
4. 초기화 콜백 (@PostConstruct)
5. Bean 사용
6. 소멸 콜백 (@PreDestroy)
7. 스프링 컨테이너 종료
```

본 프로젝트의 `BeanLifecycleExample.java`에서 이 과정을 직접 확인할 수 있습니다.

### Bean Scope

| Scope | 설명 |
|-------|------|
| **singleton** (기본값) | 스프링 컨테이너에 하나의 인스턴스만 존재 |
| prototype | 요청할 때마다 새 인스턴스 생성 |
| request | HTTP 요청마다 새 인스턴스 (웹) |
| session | HTTP 세션마다 새 인스턴스 (웹) |

---

## 5. Spring Boot 자동 설정

### Auto Configuration이란?

Spring Boot는 클래스패스에 있는 라이브러리를 분석하여 자동으로 설정을 적용합니다.

예를 들어, `spring-boot-starter-web`을 의존성에 추가하면:
- 내장 Tomcat 서버가 자동 설정됩니다.
- Spring MVC가 자동 설정됩니다.
- JSON 변환을 위한 Jackson이 자동 설정됩니다.

### 동작 원리

```
@SpringBootApplication
  └── @EnableAutoConfiguration
        └── META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports
              └── 자동 설정 클래스 목록을 로드
                    └── @Conditional 조건에 따라 설정 적용/미적용
```

### 주요 @Conditional 어노테이션

| 어노테이션 | 조건 |
|------------|------|
| `@ConditionalOnClass` | 특정 클래스가 클래스패스에 있을 때 |
| `@ConditionalOnMissingBean` | 특정 Bean이 없을 때 |
| `@ConditionalOnProperty` | 특정 프로퍼티가 설정되어 있을 때 |

### application.yml 설정

Spring Boot는 `application.yml` (또는 `application.properties`) 파일로 다양한 설정을 관리합니다.

```yaml
spring:
  application:
    name: chapter04-spring-intro    # 애플리케이션 이름
server:
  port: 8080                        # 서버 포트
```

---

## 6. Profile 설정

### Profile이란?

Profile은 환경(개발, 운영 등)에 따라 다른 설정을 적용하는 기능입니다.

### Profile 설정 방법

#### application.yml에서 분리

```yaml
# 기본 설정
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

`---`로 문서를 구분하여 프로필별 설정을 정의합니다.

### Profile 활성화 방법

```bash
# 1. application.yml에서 설정
spring:
  profiles:
    active: dev

# 2. JVM 옵션으로 설정
java -jar app.jar -Dspring.profiles.active=dev

# 3. 환경변수로 설정
export SPRING_PROFILES_ACTIVE=dev

# 4. Gradle 실행 시
./gradlew bootRun --args='--spring.profiles.active=dev'
```

### Profile에 따른 Bean 등록

`@Profile` 어노테이션을 사용하면 특정 프로필에서만 Bean을 활성화할 수 있습니다.

```java
@Service
@Profile("!english")  // english 프로필이 아닐 때 활성화
public class KoreanGreetingService implements GreetingService { ... }

@Service
@Profile("english")   // english 프로필일 때만 활성화
public class EnglishGreetingService implements GreetingService { ... }
```

### 실행 및 테스트

```bash
# 기본 실행 (KoreanGreetingService 활성화)
./gradlew bootRun

# english 프로필로 실행 (EnglishGreetingService 활성화)
./gradlew bootRun --args='--spring.profiles.active=english'
```

```bash
# API 테스트
curl http://localhost:8080/api/hello?name=Spring

# 기본: "안녕하세요, Spring님! (Korean Greeting Service)"
# english: "Hello, Spring! (English Greeting Service)"
```

---

## 7. @Qualifier와 @Primary (동일 타입 Bean 선택)

### 문제 상황

같은 타입(인터페이스)의 Bean이 두 개 이상 등록되면, Spring은 어떤 Bean을 주입해야 할지 알 수 없어 에러가 발생한다.

```java
public interface PaymentService { String pay(int amount); }

@Component class KakaoPayService implements PaymentService { ... }
@Component class NaverPayService implements PaymentService { ... }

// PaymentService 타입 Bean이 2개라 어떤 것을 주입할지 모호함 -> 에러!
public PaymentClient(PaymentService paymentService) { ... }
```

> 참고: 본 프로젝트의 `KoreanGreetingService`/`EnglishGreetingService`는 `@Profile`로 한 번에 하나만 등록되기 때문에 이 문제가 발생하지 않는다. 아래 예제는 동일 타입 Bean을 동시에 여러 개 등록하는 상황을 다룬다.

### 해결 방법 1: @Primary

`@Primary`는 "타입만 명시했을 때 기본으로 주입할 Bean"을 지정한다.

```java
@Component
@Primary   // PaymentService를 타입만으로 주입하면 이 Bean이 선택됨
public class KakaoPayService implements PaymentService { ... }
```

### 해결 방법 2: @Qualifier

`@Qualifier`는 Bean 이름을 콕 집어 주입한다. **`@Primary`보다 우선순위가 높다.**

```java
// @Primary가 KakaoPay에 있어도, @Qualifier로 지정하면 NaverPay가 주입됨
public PaymentClient(
        PaymentService primaryPayment,                          // -> KakaoPay (@Primary)
        @Qualifier("naverPayService") PaymentService selected   // -> NaverPay (@Qualifier)
) { ... }
```

### 우선순위 정리

```
@Qualifier(이름 지정)  >  @Primary(기본 지정)  >  (아무것도 없으면 모호 -> 에러)
```

본 프로젝트의 `QualifierExample.java`에서 두 방식을 모두 확인할 수 있으며, 애플리케이션을 실행하면 어떤 Bean이 주입됐는지 콘솔에 출력된다.

```
===== @Primary / @Qualifier 데모 =====
[@Primary 주입]   10000원을 카카오페이로 결제했습니다. (@Primary 기본 Bean)
[@Qualifier 주입] 5000원을 네이버페이로 결제했습니다. (@Qualifier로 선택된 Bean)
=====================================
```

---

## 8. @ConfigurationProperties와 @Value (외부 설정 주입)

`application.yml`에 작성한 설정값을 코드로 가져오는 두 가지 방법이 있다.

### @Value: 프로퍼티 하나씩 주입

```java
@Value("${app.name:이름 없음}")   // ${키:기본값} - 키가 없으면 기본값 사용
private String appName;
```

- 간단한 값 하나를 주입할 때 편리하다.
- 프로퍼티가 많아지면 `@Value`가 여기저기 흩어져 관리가 어렵다.

### @ConfigurationProperties: 관련 프로퍼티를 객체로 묶기 (권장)

`prefix`로 시작하는 프로퍼티들을 객체의 필드에 한 번에 바인딩한다.

```java
// 1) 설정 클래스 정의 (record로 불변 객체)
@ConfigurationProperties(prefix = "app")
public record AppProperties(
        String name,
        String version,
        int maxUsers       // 케밥케이스(max-users)가 카멜케이스(maxUsers)로 자동 매핑됨
) {}

// 2) 메인 클래스에서 활성화
@SpringBootApplication
@EnableConfigurationProperties(AppProperties.class)
public class Chapter04Application { ... }
```

```yaml
# application.yml
app:
  name: 교육용 Spring Boot 앱
  version: 1.0.0
  max-users: 100
```

### 두 방식 비교

| 구분 | @Value | @ConfigurationProperties |
|------|--------|--------------------------|
| 주입 단위 | 프로퍼티 하나씩 | 관련 프로퍼티를 묶어서 |
| 타입 안전성 | 약함 | 강함 (필드 타입으로 검증) |
| 케밥케이스 매핑 | 직접 키 명시 | 자동 (max-users → maxUsers) |
| 권장 상황 | 단발성 값 1~2개 | 설정 그룹 관리 |

본 프로젝트의 `AppProperties.java`(설정 클래스)와 `AppPropertiesLogger.java`(출력)에서 두 방식을 모두 확인할 수 있다. 실행하면 설정값이 콘솔에 출력된다.

```
===== @ConfigurationProperties 데모 =====
[묶어서 주입] app.name     = 교육용 Spring Boot 앱
[묶어서 주입] app.version  = 1.0.0
[묶어서 주입] app.maxUsers = 100
[@Value 주입] app.name     = 교육용 Spring Boot 앱
=========================================
```

---

## 9. AOP — 공통 관심사 분리

### 왜 필요한가: 어디에나 있는 중복 코드

실행 시간 측정을 모든 서비스 메서드에 넣어야 한다고 해보자.

```java
public String greet(String name) {
    long start = System.nanoTime();               // ← 반복
    try {
        return "안녕하세요, " + name + "님!";       // ← 이 한 줄이 진짜 로직
    } finally {
        log.info("걸린 시간: {}ns", System.nanoTime() - start);  // ← 반복
    }
}
```

메서드가 100개면 이 측정 코드를 100번 복사해야 한다. 측정 방식을 바꾸려면 100곳을 고쳐야 한다. 이렇게 **여러 메서드에 공통으로 걸쳐 있는 관심사**(로깅, 트랜잭션, 보안, 실행 시간 측정 등)를 **횡단 관심사(cross-cutting concern)** 라고 부른다.

**AOP(Aspect-Oriented Programming, 관점 지향 프로그래밍)** 는 이 반복 코드를 한 곳(Aspect)에 모아두고, 대상 메서드의 앞뒤에 자동으로 끼워 넣는 기법이다. 비즈니스 코드에는 진짜 로직 한 줄만 남는다.

### 동작 원리: 프록시(Proxy)

Spring AOP의 실체는 **프록시**다. Spring은 어드바이스를 적용할 Bean을 발견하면, 원본 대신 원본을 상속한 가짜 객체(CGLIB 프록시)를 만들어 컨테이너에 등록한다.

```
 호출자 (HelloController, ProxyCheckRunner ...)
    │
    │  greetingService.greet("AOP") 호출
    ▼
┌───────── 프록시 (KoreanGreetingService$$SpringCGLIB$$...) ─────────┐
│                                                                   │
│   ① 어드바이스 (전): 시작 시각 기록, [서비스 호출 로깅]                │
│           │                                                       │
│           ▼                                                       │
│   ② 원본 KoreanGreetingService.greet() 실행  ← 내가 짠 코드          │
│           │                                                       │
│           ▼                                                       │
│   ③ 어드바이스 (후): [실행 시간 측정] 로그 출력                       │
│                                                                   │
└───────────────────────────────────────────────────────────────────┘
```

`@Transactional`, `@Cacheable`, `@Async` 같은 Spring의 유명한 애너테이션들이 전부 이 구조로 동작한다. 이번 실습에서 그 원리를 직접 만들어보는 것이다.

### 포인트컷 표현식 기초

**포인트컷(Pointcut)** 은 "어디에 끼어들지"를 정하는 조건식이고, **어드바이스(Advice)** 는 "무엇을 끼워 넣을지"인 코드다. 본 프로젝트의 `ExecutionTimeAspect`는 두 가지 포인트컷을 시연한다.

**1) 애너테이션 기반** — 표식을 붙인 메서드에만 적용

```java
@Around("@annotation(com.edu.intro.aop.LogExecutionTime)")
```

**2) execution 패턴 기반** — 이름 규칙에 맞는 메서드에 일괄 적용 (애너테이션 불필요)

```java
@Before("execution(* com.edu.intro..*Service.*(..))")
//                 │       │           │      │  └ (..) 인자: 개수/타입 무관
//                 │       │           │      └ *  메서드 이름: 전부
//                 │       │           └ *Service  클래스: Service로 끝나는 것
//                 │       └ com.edu.intro..  패키지: 하위 포함 전부
//                 └ *  반환 타입: 무엇이든
```

| 어드바이스 | 시점 | 할 수 있는 것 |
|------------|------|---------------|
| `@Before` | 메서드 실행 전 | 호출 사실/인자 로깅 |
| `@After` | 메서드 실행 후 (예외 포함) | 마무리 처리 |
| `@Around` | 앞뒤를 모두 감쌈 (가장 강력) | 시간 측정, 반환값 변경, 실행 차단 |

### ⚠️ 함정: self-invocation (내부 호출)

같은 클래스 안에서 자기 메서드를 `this.`로 호출하면 **어드바이스가 적용되지 않는다.**

```java
@Service
public class SomeService {
    public void outer() {
        this.inner();   // ⚠️ 프록시를 거치지 않는다! @LogExecutionTime 무시됨
    }

    @LogExecutionTime
    public void inner() { ... }
}
```

어드바이스는 **프록시(바깥 껍데기)** 에 들어 있는데, `outer()`가 실행되는 시점에 코드는 이미 원본 객체 안에 들어와 있다. 그 안에서의 `this`는 프록시가 아니라 원본이므로, `this.inner()`는 어드바이스를 건너뛴다. 프록시는 **바깥에서 Bean을 통해 진입할 때만** 개입한다. 해결책은 `inner()`를 별도 Bean으로 분리해 주입받아 호출하는 것이다. (`@Transactional`이 안 걸리는 대표적인 원인이기도 하다 — 개념서 §02 참고)

같은 이유로, 직접 `new`로 만든 객체에도 어드바이스가 적용되지 않는다. 프록시는 컨테이너가 Bean을 만들 때만 씌우기 때문이다.

### 실행 및 확인 방법

```bash
./gradlew bootRun
```

**1) 기동 직후** — `ProxyCheckRunner`가 프록시의 정체를 폭로한다.

```
[프록시 확인] 주입받은 타입(인터페이스) : com.edu.intro.GreetingService
[프록시 확인] 실제 런타임 클래스        : com.edu.intro.KoreanGreetingService$$SpringCGLIB$$0
[프록시 확인] 프록시 여부               : 예 (Spring이 감싼 CGLIB 프록시)
[서비스 호출 로깅] KoreanGreetingService.greet(..) 호출됨 - 인자: [AOP] (execution 포인트컷 어드바이스)
[실행 시간 측정] KoreanGreetingService.greet(..) 실행에 0ms 걸렸습니다 (@LogExecutionTime 어드바이스)
```

클래스 이름의 `$$SpringCGLIB$$` — 컨테이너에 등록된 것은 내가 작성한 원본이 아니라 Spring이 만든 프록시라는 증거다.

**2) API 호출 시** — 요청마다 두 어드바이스의 로그가 함께 찍힌다.

```bash
curl "http://localhost:8080/api/hello?name=Spring"
```

**3) 직접 실험** — `KoreanGreetingService.greet()`의 `@LogExecutionTime`을 지우고 재실행하면 [실행 시간 측정] 로그만 사라지고, execution 패턴에는 여전히 걸리므로 [서비스 호출 로깅]은 남는다. 두 포인트컷이 독립적으로 동작함을 확인할 수 있다.

---

## 10. Docker로 실행하기

### Dockerfile

본 프로젝트는 멀티 스테이지 빌드를 사용합니다.

```dockerfile
# Stage 1: 빌드
FROM eclipse-temurin:21-jdk AS builder
WORKDIR /app
COPY build.gradle settings.gradle ./
COPY gradle gradle
COPY gradlew .
COPY src src
RUN chmod +x gradlew && ./gradlew bootJar --no-daemon

# Stage 2: 실행
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

**멀티 스테이지 빌드의 장점:**
- 빌드 도구(JDK, Gradle)가 최종 이미지에 포함되지 않아 이미지 크기가 작아집니다.
- JDK 대신 JRE만 포함하여 더욱 경량화됩니다.

### Docker Compose

```yaml
services:
  app:
    build: .
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=dev
```

### 실행 명령어

```bash
# Docker Compose로 빌드 및 실행
docker compose up --build

# 백그라운드 실행
docker compose up --build -d

# 로그 확인
docker compose logs -f app

# 중지
docker compose down
```

### 프로필 변경하여 실행

```bash
# 환경변수를 오버라이드하여 prod 프로필로 실행
SPRING_PROFILES_ACTIVE=prod docker compose up --build
```

---

## 프로젝트 구조

```
chapter04-spring-boot-intro/
├── README.md
├── build.gradle
├── settings.gradle
├── Dockerfile
├── docker-compose.yml
├── gradle/
│   └── wrapper/
│       └── gradle-wrapper.properties
└── src/
    └── main/
        ├── java/
        │   └── com/edu/intro/
        │       ├── Chapter04Application.java      # 메인 클래스
        │       ├── GreetingService.java            # 인터페이스
        │       ├── KoreanGreetingService.java      # 한국어 구현체
        │       ├── EnglishGreetingService.java     # 영어 구현체
        │       ├── AppConfig.java                  # @Configuration 예제
        │       ├── HelloController.java            # REST 컨트롤러
        │       ├── BeanLifecycleExample.java       # Bean 생명주기 예제
        │       ├── QualifierExample.java           # @Primary/@Qualifier 예제
        │       ├── AppProperties.java              # @ConfigurationProperties 예제
        │       ├── AppPropertiesLogger.java        # 설정값 출력(@Value 대조)
        │       └── aop/
        │           ├── LogExecutionTime.java       # 커스텀 애너테이션 (표식)
        │           ├── ExecutionTimeAspect.java    # @Aspect (두 가지 포인트컷 시연)
        │           └── ProxyCheckRunner.java       # CGLIB 프록시 확인 러너
        └── resources/
            └── application.yml                     # 설정 파일
```

## 핵심 정리

| 개념 | 설명 |
|------|------|
| **Spring Boot** | Spring Framework의 설정을 자동화한 프레임워크 |
| **IoC** | 객체의 생성/관리를 프레임워크에 위임 |
| **DI** | 필요한 의존성을 외부에서 주입 (생성자 주입 권장) |
| **Bean** | Spring 컨테이너가 관리하는 객체 |
| **Component Scan** | @Component 계열 어노테이션을 탐색하여 Bean 자동 등록 |
| **Auto Configuration** | 클래스패스 기반 자동 설정 |
| **Profile** | 환경별 설정 분리 |
| **@Primary / @Qualifier** | 동일 타입 Bean이 여러 개일 때 주입 대상 선택 (@Qualifier가 우선) |
| **@ConfigurationProperties** | 관련 설정을 객체로 묶어 타입 안전하게 주입 (@Value는 단건 주입) |
| **AOP** | 횡단 관심사(로깅, 시간 측정 등)를 Aspect로 분리, 프록시가 메서드 앞뒤에 끼워 넣음 (self-invocation 주의) |
