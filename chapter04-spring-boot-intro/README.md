# Chapter 04: Spring Boot Introduction

## 목차
1. [Spring Framework란?](#1-spring-framework란)
2. [Spring Boot란?](#2-spring-boot란)
3. [IoC/DI 개념](#3-iocdi-개념)
4. [Bean과 Component Scan](#4-bean과-component-scan)
5. [Spring Boot 자동 설정](#5-spring-boot-자동-설정)
6. [Profile 설정](#6-profile-설정)
7. [Docker로 실행하기](#7-docker로-실행하기)

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

## 7. Docker로 실행하기

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
        │       └── BeanLifecycleExample.java       # Bean 생명주기 예제
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
