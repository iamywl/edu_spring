# Spring Boot 개념서: 왜 이렇게 만들어졌는가?

> "좋은 개발자는 코드를 작성하는 사람이 아니라, 왜 그 코드가 필요한지 설명할 수 있는 사람이다."

---

## 들어가며

### 이 책의 목적

이 책은 Spring Boot의 **"어떻게"가 아니라 "왜"**를 설명하는 책이다.

공식 문서를 읽으면 `@Autowired`를 어떻게 쓰는지는 알 수 있다. 하지만 **왜 의존성 주입이 필요한지**, **왜 직접 new를 하면 안 되는지**, **왜 계층을 나누는지**는 설명해주지 않는다.

수년간 주니어 개발자들을 멘토링하면서 느낀 것이 있다. 대부분의 문제는 **문법을 몰라서가 아니라, 왜 그런 구조가 필요한지 이해하지 못해서** 발생한다. `@Service`를 붙이는 건 알지만, 왜 Service 계층이 필요한지 모르는 것이다.

이 책을 다 읽고 나면 이런 질문에 답할 수 있게 된다:

- "왜 Spring은 객체를 대신 만들어주는가?"
- "왜 Controller에 비즈니스 로직을 넣으면 안 되는가?"
- "왜 비밀번호를 그냥 저장하면 안 되는가?"
- "왜 Docker를 쓰는가?"

**코드보다 개념을 먼저 잡아야 한다.** 개념이 잡히면 코드는 자연스럽게 따라온다.

---

### Spring을 배우기 전에 알아야 할 것들

Spring을 배우기 전에 반드시 이해해야 하는 것들이 있다. 집을 짓기 전에 기초 공사가 필요하듯이, Spring이라는 건물을 세우기 전에 다져야 할 기반이 있다.

**1. Java 기본 문법**
- 클래스와 객체, 인터페이스, 상속, 다형성
- 특히 **인터페이스**를 잘 이해해야 한다. Spring의 핵심인 DI는 인터페이스 없이는 의미가 반감된다.

**2. 객체지향 프로그래밍(OOP)의 원리**
- SOLID 원칙 (특히 Single Responsibility, Dependency Inversion)
- "왜 하나의 클래스가 하나의 책임만 가져야 하는가?"를 이해해야 한다.

**3. HTTP 프로토콜의 기본**
- 요청과 응답이 뭔지, URL이 뭔지, GET과 POST의 차이가 뭔지
- 이것을 모르면 Spring MVC가 무엇을 하는 건지 이해할 수 없다.

**4. SQL 기본**
- SELECT, INSERT, UPDATE, DELETE
- JOIN의 개념
- JPA가 SQL을 대신 만들어주긴 하지만, SQL을 모르면 JPA가 만든 쿼리가 좋은 건지 나쁜 건지 판단할 수 없다.

---

### 웹 애플리케이션의 기본 구조

모든 웹 애플리케이션은 결국 이 구조이다:

```
┌──────────┐         ┌──────────┐         ┌──────────┐
│          │  HTTP   │          │  SQL    │          │
│ 클라이언트 │ ──────→ │   서버    │ ──────→ │   DB     │
│ (브라우저) │ ←────── │ (Spring) │ ←────── │(PostgreSQL)│
│          │  JSON   │          │  결과    │          │
└──────────┘         └──────────┘         └──────────┘
   사용자가              요청을 처리하고         데이터를
   요청을 보냄           비즈니스 로직 실행      저장/조회
```

비유를 들어보자:

- **클라이언트** = 식당의 손님 (메뉴를 주문함)
- **서버** = 주방 (주문을 받아 요리함)
- **데이터베이스** = 식재료 창고 (재료를 보관함)

손님(클라이언트)이 "아메리카노 한 잔 주세요"라고 주문(HTTP 요청)하면, 주방(서버)이 창고(DB)에서 원두를 꺼내 커피를 만들어서 손님에게 전달(HTTP 응답)한다.

Spring Boot는 이 **주방(서버)** 역할을 효율적으로 수행하게 해주는 프레임워크이다.

이제 본격적으로 시작해보자.

---

## Chapter 1: Spring이 해결하는 문제

> "모든 기술은 문제를 해결하기 위해 탄생한다. 문제를 모르면 기술을 이해할 수 없다."

Spring을 이해하려면, Spring이 **없던 시절의 고통**을 먼저 알아야 한다. 고통을 모르면 해결책의 가치를 알 수 없다.

---

### 1.1 Spring 이전의 Java 웹 개발

#### Servlet의 시대: 모든 것을 직접 해야 했다

2000년대 초반, Java로 웹 개발을 하려면 **Servlet**을 직접 작성해야 했다.

```java
// 2000년대 초반의 Java 웹 개발
public class UserServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // 1. HTTP 요청에서 파라미터를 직접 꺼낸다
        String userId = req.getParameter("id");

        // 2. DB 연결을 직접 만든다
        Connection conn = DriverManager.getConnection(
            "jdbc:mysql://localhost:3306/mydb", "root", "password"
        );

        // 3. SQL을 직접 작성한다
        PreparedStatement stmt = conn.prepareStatement(
            "SELECT * FROM users WHERE id = ?"
        );
        stmt.setString(1, userId);
        ResultSet rs = stmt.executeQuery();

        // 4. 결과를 직접 HTML로 만든다
        resp.setContentType("text/html");
        PrintWriter out = resp.getWriter();
        out.println("<html><body>");
        if (rs.next()) {
            out.println("<h1>" + rs.getString("name") + "</h1>");
        }
        out.println("</body></html>");

        // 5. 리소스를 직접 정리한다
        rs.close();
        stmt.close();
        conn.close();
    }
}
```

**하나의 메서드 안에 모든 것**이 들어가 있다:
- HTTP 파라미터 파싱
- DB 연결 관리
- SQL 실행
- HTML 생성
- 리소스 정리

이것은 마치 **식당에서 한 사람이 주문받기, 요리하기, 서빙하기, 설거지하기를 전부 하는 것**과 같다. 손님이 1명일 때는 가능하겠지만, 100명이 되면? 1000명이 되면? 불가능하다.

#### EJB의 시대: 너무 무겁고 복잡했다

이 문제를 해결하기 위해 **EJB(Enterprise JavaBeans)**가 등장했다. EJB는 "엔터프라이즈급" 애플리케이션을 만들기 위한 표준이었다.

하지만 EJB는 **너무 무겁고 복잡했다:**
- 배포 서술자(XML 설정)가 수백 줄
- 원격 호출 인터페이스를 반드시 구현해야 함
- 특정 애플리케이션 서버(WebLogic, JBoss 등)에 종속
- 테스트하려면 반드시 서버를 띄워야 함

비유하면, **작은 동네 카페를 운영하려는데 호텔급 주방 설비를 요구받는 것**과 같았다. 에스프레소 머신 하나면 될 것을 산업용 로스터, 대형 오븐, 업소용 냉장고를 전부 갖추라고 한 것이다.

```
EJB의 문제:
┌─────────────────────────────────────────────┐
│  비즈니스 로직 (우리가 정말 하고 싶은 것)      │  ← 10%
├─────────────────────────────────────────────┤
│  EJB 설정, XML, 인터페이스, 배포 서술자...     │  ← 90%
│  (해야만 하는 의미 없는 반복 작업)             │
└─────────────────────────────────────────────┘
```

개발자들은 90%의 시간을 설정에, 10%의 시간을 비즈니스 로직에 쓰고 있었다.

#### Spring의 등장: "경량 프레임워크"의 혁명

2002년, **Rod Johnson**이 "Expert One-on-One J2EE Design and Development"라는 책을 출간했고, 그 책에서 제시한 코드가 발전하여 2003년에 Spring Framework가 오픈소스로 공개되었다.

Rod Johnson의 핵심 주장은 이것이었다:

> **"EJB 없이도 엔터프라이즈 애플리케이션을 만들 수 있다. 아니, EJB 없이 만드는 것이 더 낫다."**

Spring은 이런 철학을 가지고 있었다:
- **POJO(Plain Old Java Object)** 기반: 특별한 인터페이스를 구현하지 않는 평범한 Java 객체를 사용
- **경량**: 필요한 것만 가져다 쓸 수 있는 모듈화
- **테스트 용이**: 서버 없이도 단위 테스트 가능
- **IoC/DI**: 객체의 생성과 관리를 프레임워크가 담당

```
Spring의 철학:
┌─────────────────────────────────────────────┐
│  비즈니스 로직 (우리가 정말 하고 싶은 것)      │  ← 80%
├─────────────────────────────────────────────┤
│  Spring이 알아서 해주는 것                    │  ← 20%
└─────────────────────────────────────────────┘
```

#### 왜? Rod Johnson의 철학

Rod Johnson은 실용주의자였다. 그의 철학을 한 마디로 정리하면:

> **"개발자가 비즈니스 로직에만 집중할 수 있게 하자."**

DB 연결 관리, 트랜잭션 처리, 보안 같은 **인프라 코드를 프레임워크가 처리**하고, 개발자는 **"사용자가 회원가입하면 이렇게 처리한다"** 같은 핵심 로직에만 집중하게 만드는 것. 이것이 Spring의 존재 이유이다.

---

### 1.2 프레임워크 vs 라이브러리

이 개념을 반드시 이해해야 한다. 많은 사람들이 프레임워크와 라이브러리를 혼동한다.

#### 라이브러리: 내가 호출한다 (도구 상자)

라이브러리는 **도구 상자**이다. 내가 필요할 때 원하는 도구를 꺼내서 사용한다.

```java
// 라이브러리 사용: 내가 주도권을 가짐
String json = objectMapper.writeValueAsString(user);  // 내가 Jackson을 호출
List<String> sorted = list.stream().sorted().toList();  // 내가 Stream API를 호출
```

**내가 코드를 작성하고, 내가 라이브러리를 호출한다.** 흐름의 주도권이 나에게 있다.

#### 프레임워크: 프레임워크가 나를 호출한다 (IoC - 제어의 역전)

프레임워크는 **공장**이다. 공장에는 이미 생산 라인(흐름)이 있고, 나는 그 라인의 특정 구간에서 내 역할만 수행한다.

```java
// 프레임워크 사용: 프레임워크가 주도권을 가짐
@RestController
public class UserController {

    @GetMapping("/users/{id}")    // Spring이 이 메서드를 언제 호출할지 결정
    public UserResponse getUser(@PathVariable Long id) {
        return userService.findById(id);  // 나는 로직만 작성
    }
}
```

**내가 `getUser()`를 직접 호출한 적이 없다.** HTTP 요청이 들어오면 **Spring이 알아서 이 메서드를 호출**해준다. 흐름의 주도권이 프레임워크에 있다.

이것이 바로 **IoC(Inversion of Control, 제어의 역전)**이다.

#### 비유: "라이브러리는 망치, 프레임워크는 공장"

```
라이브러리 (도구 상자):
┌─────────────────────────────────┐
│                                 │
│   개발자: "망치 필요해!"         │
│   도구상자: 🔨 (여기 있어)       │
│   개발자: (직접 못을 박음)       │
│                                 │
│   → 내가 도구를 선택하고 사용    │
└─────────────────────────────────┘

프레임워크 (공장):
┌─────────────────────────────────┐
│                                 │
│   공장: "3번 구간에서 나사 조여!" │
│   개발자: (나사를 조임)          │
│   공장: "다음! 5번 구간으로!"    │
│                                 │
│   → 공장이 흐름을 결정,         │
│     나는 특정 구간만 담당        │
└─────────────────────────────────┘
```

**왜 이 차이가 중요한가?**

프레임워크를 사용하면 **일관된 구조**를 강제할 수 있다. 10명의 개발자가 함께 일할 때, 각자 자기 방식대로 코드를 짜면 혼란스럽지 않겠는가? 프레임워크는 "Controller는 여기, Service는 여기, Repository는 여기"라는 규칙을 정해줌으로써 팀 전체가 같은 구조로 코드를 작성하게 한다.

---

### 1.3 Spring Boot는 왜 만들어졌는가?

#### Spring의 문제: XML 지옥, 설정의 복잡함

Spring은 혁명적이었지만, 시간이 지나면서 **새로운 문제**가 생겼다. 바로 **설정의 복잡함**이다.

Spring으로 웹 프로젝트를 시작하려면:
- `web.xml` 설정 (Servlet 매핑)
- `applicationContext.xml` 설정 (Bean 정의)
- `dispatcher-servlet.xml` 설정 (MVC 설정)
- `pom.xml`에 수십 개의 의존성 추가 (버전 호환성 직접 확인)
- 톰캣 설치 및 설정
- 로깅 프레임워크 설정
- ...

"Hello World" 하나 찍으려면 XML 파일 3개에 수백 줄의 설정이 필요했다. 이것을 **XML 지옥**이라고 불렀다.

```xml
<!-- Spring MVC 시절의 설정 파일 일부 -->
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xmlns:mvc="http://www.springframework.org/schema/mvc"
       xsi:schemaLocation="...긴 URL들...">

    <context:component-scan base-package="com.example"/>
    <mvc:annotation-driven/>

    <bean class="org.springframework.web.servlet.view.InternalResourceViewResolver">
        <property name="prefix" value="/WEB-INF/views/"/>
        <property name="suffix" value=".jsp"/>
    </bean>

    <bean id="dataSource" class="org.apache.commons.dbcp2.BasicDataSource">
        <property name="driverClassName" value="com.mysql.jdbc.Driver"/>
        <property name="url" value="jdbc:mysql://localhost:3306/mydb"/>
        <property name="username" value="root"/>
        <property name="password" value="password"/>
    </bean>

    <!-- ... 수백 줄 더 ... -->
</beans>
```

Spring이 EJB의 복잡함을 해결하려고 했는데, 어느새 **Spring 자체가 복잡해진 것**이다.

#### Spring Boot의 해결: "Convention over Configuration"

2014년, **Spring Boot**가 등장한다. Spring Boot의 핵심 철학은 **"Convention over Configuration (설정보다 규약)"**이다.

> "대부분의 경우 이런 설정을 원하지 않는가? 그냥 우리가 기본으로 해줄 것이다. 바꾸고 싶을 때만 설정하라."

Spring Boot가 해준 것:
- **자동 설정(Auto-configuration)**: classpath에 H2 라이브러리가 있으면? → 자동으로 H2 DataSource 설정
- **내장 서버**: 톰캣을 별도로 설치할 필요 없음. JAR 파일 하나로 실행
- **스타터 의존성**: `spring-boot-starter-web` 하나면 웹 개발에 필요한 모든 라이브러리가 호환되는 버전으로 포함
- **application.yml**: XML 대신 간결한 설정 파일

```yaml
# Spring Boot의 설정: 이게 전부입니다
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/mydb
    username: user
    password: password
  jpa:
    hibernate:
      ddl-auto: update
```

#### 비유: "스타벅스 주문 시스템"

**Spring (Boot 이전) = 개인 카페:**
> "에티오피아 예가체프 원두로, 분쇄도는 중간, 물 온도 93도, 브루잉 시간 25초, 물과 원두 비율 1:15로 해주세요."

**Spring Boot = 스타벅스:**
> "아메리카노 톨 사이즈요."

스타벅스에서 "아메리카노"라고 하면, 원두 종류, 물 온도, 추출 시간 같은 세부 사항은 **이미 정해진 표준(Convention)**을 따른다. 손님은 원하는 것만 말하면 된다. 물론, "얼음 적게" 같은 커스터마이징도 가능하다.

Spring Boot도 마찬가지이다. 기본 설정이 이미 최적화되어 있고, 필요한 부분만 바꾸면 된다.

```
Spring Boot 이전:                    Spring Boot:
┌──────────────────┐                ┌──────────────────┐
│ web.xml          │                │                  │
│ applicationCtx   │                │ application.yml  │
│ dispatcher-xml   │   ──────→      │ (필요한 것만)     │
│ pom.xml (수동)   │                │                  │
│ 톰캣 설치/설정    │                │ 끝!              │
│ 로깅 설정        │                │                  │
│ ...              │                └──────────────────┘
└──────────────────┘
   설정 파일 5-10개                    설정 파일 1개
```

---

## Chapter 2: IoC/DI - Spring의 심장

> "Spring을 이해한다 = IoC/DI를 이해한다. 이것을 모르면 Spring의 나머지 모든 것이 마법처럼 보인다."

IoC/DI는 Spring의 **가장 핵심적인 개념**이다. 이것을 이해하지 못하면 Spring의 모든 것이 "어노테이션 붙이면 마법처럼 동작하는 것"으로 보인다. 마법은 없다. 원리가 있을 뿐이다.

---

### 2.1 제어의 역전 (IoC)이란?

#### 전통적 방식: 내가 필요한 것을 직접 만든다

```java
// 전통적 방식: 내가 모든 것을 제어
public class OrderService {

    // 내가 직접 만든다 (new)
    private final OrderRepository orderRepository = new OrderRepository();
    private final PaymentService paymentService = new PaymentService();
    private final EmailService emailService = new EmailService();

    public void createOrder(OrderRequest request) {
        orderRepository.save(request);
        paymentService.process(request);
        emailService.sendConfirmation(request);
    }
}
```

여기서 `OrderService`는 자신이 필요한 모든 것을 **직접 생성**한다. 제어권이 `OrderService`에 있다.

#### IoC 방식: 필요한 것을 누군가 가져다 준다

```java
// IoC 방식: 누군가 필요한 것을 가져다 준다
@Service
public class OrderService {

    // 누군가 만들어서 가져다 준다 (주입)
    private final OrderRepository orderRepository;
    private final PaymentService paymentService;
    private final EmailService emailService;

    public OrderService(OrderRepository orderRepository,
                        PaymentService paymentService,
                        EmailService emailService) {
        this.orderRepository = orderRepository;
        this.paymentService = paymentService;
        this.emailService = emailService;
    }

    public void createOrder(OrderRequest request) {
        orderRepository.save(request);
        paymentService.process(request);
        emailService.sendConfirmation(request);
    }
}
```

`OrderService`는 더 이상 자신이 필요한 것을 직접 만들지 않는다. **생성자를 통해 외부에서 전달받는다.** 제어권이 **역전(Inversion)**된 것이다.

#### 비유: "직접 요리 vs 배달 음식"

**전통적 방식 (직접 요리):**
> 저녁을 먹으려면 마트에 가서 재료를 사고, 집에 와서 씻고 손질하고, 조리하고, 그릇에 담아야 한다.
> 재료가 없으면? 내가 직접 가서 사와야 한다.

**IoC 방식 (배달 음식):**
> "짜장면 하나요!" 라고 주문하면, 누군가가 알아서 만들어서 가져다준다.
> 재료가 뭔지, 어떻게 만드는지 내가 알 필요 없다.

#### 비유: "할리우드 원칙"

할리우드 오디션에서 배우에게 하는 말이 있다:

> **"Don't call us, we'll call you."** (우리한테 전화하지 마세요. 우리가 전화할게요.)

전통적 방식에서는 `OrderService`가 `OrderRepository`에게 "야, 내가 너 필요해, 생겨나!" 라고 한다.

IoC에서는 Spring 컨테이너가 "걱정 마, 내가 필요한 걸 만들어서 줄게" 라고 한다.

```
전통적 방식:                            IoC 방식:
┌──────────────┐                      ┌──────────────────┐
│ OrderService │                      │  Spring Container │
│              │                      │                  │
│ new Repo()───┼──→ Repository        │  Repository ──┐  │
│ new Pay()────┼──→ PaymentService    │  PayService ──┤  │
│ new Email()──┼──→ EmailService      │  EmailSvc ────┤  │
│              │                      │               │  │
│ (내가 직접   │                      │  OrderService←┘  │
│  만든다)     │                      │  (Spring이 넣어줌)│
└──────────────┘                      └──────────────────┘
```

#### 왜? IoC가 필요한 근본적 이유

**유연성이다.**

직접 `new`로 생성하면, `OrderRepository`를 `JpaOrderRepository`로 바꾸고 싶을 때 `OrderService`의 코드를 수정해야 한다. 하지만 IoC 방식이라면, Spring 설정만 바꾸면 `OrderService`는 코드 변경 없이 다른 구현체를 사용할 수 있다.

10개의 서비스가 `OrderRepository`를 사용하고 있다면? 전통적 방식에서는 10곳을 다 수정해야 하지만, IoC에서는 **한 곳**만 바꾸면 된다.

---

### 2.2 의존성 주입 (DI)이란?

#### 의존성이란?

**의존성(Dependency)**이란 "A가 B 없이는 동작할 수 없는 관계"를 말한다.

```java
public class UserService {
    private final UserRepository userRepository; // UserService는 UserRepository에 의존

    // UserRepository가 없으면 UserService는 아무것도 할 수 없다
    // → UserRepository는 UserService의 "의존성"
}
```

실생활에서도 마찬가지이다:
- 자동차는 엔진에 **의존**한다 (엔진 없으면 못 움직임)
- 커피머신은 전기에 **의존**한다 (전기 없으면 못 내림)
- 요리사는 식재료에 **의존**한다 (재료 없으면 못 만듦)

#### 왜 직접 new하면 안 되는가? (강한 결합의 문제)

```java
// 강한 결합 (Tight Coupling) - 나쁜 예
public class UserService {
    // 구체 클래스에 직접 의존
    private final MySQLUserRepository userRepository = new MySQLUserRepository();
}
```

이것의 문제는 무엇인가?

**1. 변경이 어렵다**
MySQL에서 PostgreSQL로 바꾸고 싶으면? `UserService` 코드를 수정해야 한다. `UserService`를 사용하는 다른 10개 클래스도? 전부 수정해야 한다.

**2. 테스트가 어렵다**
`UserService`를 테스트하려면 반드시 MySQL이 실행 중이어야 한다. 단위 테스트를 할 수 없다.

**3. 확장이 어렵다**
캐시를 추가한 `CachedUserRepository`를 만들어도, `UserService`를 수정하지 않으면 적용할 수 없다.

```
강한 결합의 문제:

UserService ──────────→ MySQLUserRepository
   (직접 생성)              (구체 클래스)

MySQL을 PostgreSQL로 바꾸려면?
→ UserService 코드를 수정해야 함
→ UserService를 사용하는 모든 곳도 영향 받을 수 있음
→ 테스트도 MySQL 없이는 불가능
```

#### DI: 외부에서 의존성을 주입받는다

```java
// 느슨한 결합 (Loose Coupling) - 좋은 예
public class UserService {
    // 인터페이스에 의존
    private final UserRepository userRepository;

    // 외부에서 구현체를 주입받음
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
}
```

```
느슨한 결합:

UserService ──────────→ UserRepository (인터페이스)
   (주입받음)                  ↑
                     ┌────────┴────────┐
                     │                 │
              MySQLUserRepo    PostgresUserRepo

MySQL을 PostgreSQL로 바꾸려면?
→ 주입하는 구현체만 바꾸면 됨
→ UserService 코드는 전혀 수정할 필요 없음
→ 테스트 시 가짜(Mock) 객체 주입 가능
```

#### 비유: "자동차 엔진"

**강한 결합:**
> 자동차 공장에서 차체와 엔진을 **용접**으로 붙였다. 엔진을 교체하려면? 차체를 잘라내야 한다. 사실상 불가능하다.

**느슨한 결합 (DI):**
> 자동차 공장에서 차체에 **엔진 마운트(인터페이스)**를 만들어놓고, 엔진은 **볼트로 결합**한다. 엔진을 교체하려면? 볼트만 풀고 새 엔진을 끼우면 된다.

DI에서 인터페이스는 이 **엔진 마운트**와 같다. 규격(인터페이스)만 맞으면 어떤 엔진(구현체)이든 장착할 수 있다.

---

### 2.3 왜 생성자 주입이 최선인가?

Spring에서 DI를 하는 방법은 3가지이다:

#### 1. 필드 주입 (Field Injection) - 권장하지 않음

```java
@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;  // Spring이 직접 필드에 주입
}
```

간단해 보이지만, **심각한 문제**가 있다.

#### 2. Setter 주입 (Setter Injection) - 특수한 경우에만

```java
@Service
public class UserService {
    private UserRepository userRepository;

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
}
```

선택적 의존성에는 사용할 수 있지만, 대부분의 경우 불필요하다.

#### 3. 생성자 주입 (Constructor Injection) - 최선

```java
@Service
public class UserService {
    private final UserRepository userRepository;  // final 가능!

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
}
```

#### 왜 생성자 주입이 최선인가?

**1. 불변성 (Immutability)**
```java
// 생성자 주입: final 키워드 사용 가능
private final UserRepository userRepository; // 한번 주입되면 변경 불가

// 필드 주입: final 불가
@Autowired
private UserRepository userRepository; // 언제든 바뀔 수 있음 (불안정)
```

`final`은 **"이 변수는 초기화 후 절대 바뀌지 않는다"**는 계약이다. 의존성이 중간에 바뀌면 안 되지 않는가? `UserService`가 동작하는 도중에 `userRepository`가 갑자기 바뀌면 재앙이다.

**2. 필수 의존성 강제**
```java
// 생성자 주입: 의존성 없이 객체 생성 불가능
UserService service = new UserService(); // 컴파일 에러! Repository를 반드시 넘겨야 함

// 필드 주입: 의존성 없이도 객체 생성 가능 (런타임에 NullPointerException)
UserService service = new UserService(); // 컴파일 성공... 하지만 사용 시 NPE!
```

**3. 테스트 용이성**
```java
// 생성자 주입: Spring 없이도 테스트 가능
@Test
void testFindUser() {
    UserRepository mockRepo = mock(UserRepository.class);
    UserService service = new UserService(mockRepo); // 직접 주입
    // 테스트 실행
}

// 필드 주입: Spring 컨텍스트 없이는 테스트 불가능
// 리플렉션을 써야 함 → 복잡하고 깨지기 쉬움
```

**4. 순환 참조 감지**
```java
// A가 B에 의존, B가 A에 의존 → 순환 참조!
// 생성자 주입: 애플리케이션 시작 시 즉시 에러 → 빠른 발견
// 필드 주입: 에러 없이 시작됨 → 런타임에 문제 발생 → 디버깅 지옥
```

비유하면:
- **필드 주입** = 시한폭탄 (언제 터질지 모름)
- **생성자 주입** = 즉시 경보 (문제가 있으면 바로 알려줌)

> **결론: 특별한 이유가 없다면 항상 생성자 주입을 사용해야 한다.**
> 생성자가 1개뿐이라면 `@Autowired`도 생략할 수 있다 (Spring 4.3+).

---

### 2.4 Bean: Spring이 관리하는 객체

#### Bean이란?

**Bean**은 Spring 컨테이너(IoC Container)가 **생성하고, 관리하고, 소멸시키는 객체**이다.

일반 Java 객체와의 차이는 무엇인가?

```java
// 일반 객체: 내가 만들고, 내가 관리
UserService service = new UserService();

// Bean: Spring이 만들고, Spring이 관리
@Service    // ← 이 어노테이션 덕분에 Spring이 관리
public class UserService { ... }
```

Bean으로 등록되면 Spring이:
- 객체를 **생성**한다
- 의존성을 **주입**한다
- 생명주기를 **관리**한다
- 필요한 곳에 **전달**한다

#### 비유: "호텔의 객실 관리"

```
Spring Container = 호텔
Bean            = 객실 (관리 대상)
개발자          = 투숙객

┌─────────────────────────────────────────┐
│  Spring Container (호텔)                │
│                                         │
│  ┌─────────┐ ┌─────────┐ ┌─────────┐  │
│  │ UserSvc │ │ UserRepo│ │OrderSvc │  │
│  │ (101호) │ │ (102호) │ │ (103호) │  │
│  └─────────┘ └─────────┘ └─────────┘  │
│                                         │
│  호텔이 객실을:                          │
│  - 준비해놓고 (생성)                     │
│  - 청소하고 (초기화)                     │
│  - 체크아웃 시 정리 (소멸)               │
│                                         │
│  투숙객은 방만 사용하면 됨               │
└─────────────────────────────────────────┘
```

개발자는 "UserService가 필요해"라고만 하면, 호텔(Spring Container)이 이미 준비해둔 객실(Bean)을 제공한다. 객실 청소(초기화), 비품 관리(의존성), 체크아웃(소멸)은 호텔이 알아서 한다.

#### Singleton: 왜 기본이 싱글톤인가?

Spring Bean의 기본 스코프는 **Singleton**이다. 즉, **하나의 Bean 정의에 대해 하나의 인스턴스만 생성**된다.

```java
@Service
public class UserService { ... }

// Spring Container 전체에서 UserService 인스턴스는 딱 1개
// A가 요청해도, B가 요청해도 같은 인스턴스를 받음
```

**왜 싱글톤이 기본인가?**

1. **메모리 효율**: 요청이 올 때마다 `UserService`를 새로 만들면? 초당 1000개 요청이면 1000개의 객체가 생성된다. 메모리 낭비이다.

2. **서비스는 상태가 없다**: `UserService`에는 "사용자 A의 로그인 상태" 같은 데이터가 저장되지 않는다. 메서드를 호출하면 결과를 반환하고 끝이다. 상태가 없으니 하나의 인스턴스를 여러 스레드가 공유해도 된다.

```
요청 1 ──→ ┐
요청 2 ──→ ├──→ UserService (인스턴스 1개) ──→ UserRepository
요청 3 ──→ ┘         (상태 없음)
```

**주의! 싱글톤 Bean에 상태를 저장하면 안 된다:**

```java
@Service
public class UserService {
    // 절대 이렇게 하면 안 됨!
    private User currentUser; // 여러 스레드가 이 필드를 공유 → 다른 사람의 데이터가 보임!

    public void login(User user) {
        this.currentUser = user; // Thread A가 설정한 값을 Thread B가 덮어씀!
    }
}
```

이것은 마치 **호텔 로비에 있는 공용 칠판에 개인 메모를 쓰는 것**과 같다. 다른 사람이 지우고 자기 메모를 쓸 수 있다.

#### Bean 생명주기를 이해해야 하는 이유

```
Bean 생명주기:
┌──────────┐    ┌──────────┐    ┌──────────┐    ┌──────────┐
│  생성     │ →  │ 의존성   │ →  │ 초기화   │ →  │  사용    │
│          │    │ 주입     │    │ 콜백     │    │         │
└──────────┘    └──────────┘    └──────────┘    └──────────┘
                                                     │
                                                     ↓
                                              ┌──────────┐
                                              │  소멸     │
                                              │  콜백     │
                                              └──────────┘
```

생명주기를 알면:
- **초기화 시점**에 외부 리소스(커넥션 풀, 캐시)를 설정할 수 있다
- **소멸 시점**에 리소스를 정리(커넥션 반환, 파일 닫기)할 수 있다
- "왜 내 Bean이 아직 null이지?" 같은 문제를 해결할 수 있다

---

### 2.5 Component Scan: 자동 등록의 마법

#### @Component 계열 어노테이션의 역할

Spring은 **특정 어노테이션이 붙은 클래스를 자동으로 Bean으로 등록**한다. 이것을 **Component Scan**이라고 한다.

```java
@Component      // 일반적인 컴포넌트
@Service        // 비즈니스 로직 계층 (내부적으로 @Component)
@Repository     // 데이터 접근 계층 (내부적으로 @Component)
@Controller     // 웹 요청 처리 계층 (내부적으로 @Component)
@RestController // REST API 컨트롤러 (내부적으로 @Controller + @ResponseBody)
```

**왜 다 @Component로 통일하지 않고 나누는가?**

기능적으로는 거의 같다. 하지만 **의도를 명확하게 표현**하기 위함이다:
- `@Service`를 보면 "아, 여기에 비즈니스 로직이 있구나"
- `@Repository`를 보면 "아, 여기서 DB에 접근하는구나"
- 또한 `@Repository`는 **DB 예외를 Spring 예외로 자동 변환**하는 부가 기능이 있다

이것은 마치 **직원들의 유니폼**과 같다. 백화점에서 안내 데스크 직원, 매장 직원, 경비원이 모두 같은 옷을 입으면 누가 누군지 구분할 수 없지 않겠는가?

#### 패키지 스캔 범위와 주의점

```java
@SpringBootApplication  // 이 어노테이션이 있는 패키지부터 하위를 스캔
public class MyApplication { ... }
```

```
com.example.myapp               ← @SpringBootApplication 위치
  ├── controller/               ← 스캔됨
  ├── service/                  ← 스캔됨
  ├── repository/               ← 스캔됨
  └── config/                   ← 스캔됨

com.example.other/              ← 스캔 안 됨! (다른 패키지)
```

**흔한 실수:** `@SpringBootApplication`이 있는 클래스를 너무 깊은 패키지에 두면, 다른 패키지의 Bean이 스캔되지 않는다. 메인 클래스는 **최상위 패키지**에 두어야 한다.

#### @Configuration + @Bean: 언제 수동 등록하는가?

자동 스캔으로 충분하지 않을 때가 있다:

```java
@Configuration
public class AppConfig {

    // 외부 라이브러리의 클래스는 @Component를 붙일 수 없음
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    // 같은 인터페이스의 구현체 중 특정 것을 선택해야 할 때
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

**수동 등록을 하는 경우:**
1. **외부 라이브러리 클래스**: 우리가 소스코드를 수정할 수 없으므로 `@Component`를 붙일 수 없음
2. **복잡한 생성 로직**: Bean을 만들 때 추가적인 설정이 필요한 경우
3. **조건부 등록**: 프로파일이나 환경에 따라 다른 구현체를 등록하고 싶을 때

---

### 2.6 애노테이션은 마법이 아니다: 프록시와 AOP

> "이 책의 부제는 '왜 이렇게 만들어졌는가'이다. 하지만 진짜 중요한 질문은 하나 더 있다: **'어떻게 동작하는가'**. `@Transactional`을 붙이면 트랜잭션이 걸린다. 그런데 **누가, 언제, 어디서** 그 트랜잭션을 여는가? 이걸 모르면 애노테이션은 영원히 마법이다."

지금까지 이 책은 `@Transactional`, `@Service`, `@Repository`를 아무렇지 않게 써왔다. 하지만 한 번도 이 질문에 답하지 않았다: **애노테이션은 그냥 글자일 뿐인데, 어떻게 코드를 실행시키는가?**

문자열 `@Transactional`은 자바 문법상 아무것도 실행하지 않는다. `@Deprecated`처럼 그냥 "표식(marker)"일 뿐이다. 표식은 스스로 트랜잭션을 열지 못한다. **누군가 이 표식을 읽고, 그에 맞는 코드를 대신 실행해줘야 한다.** 그 "누군가"가 바로 **Spring이 만든 프록시(Proxy)**이다.

#### 프록시란: 원본을 감싸는 대리인

당신이 `MemberService`를 작성했다고 하자. 그런데 이 클래스에 `@Transactional`이 붙어 있으면, Spring은 컨테이너에 **당신의 클래스를 그대로 넣지 않는다.** 대신 당신의 클래스를 **상속(또는 인터페이스 구현)한 가짜 클래스**를 런타임에 만들어서, 그 가짜를 Bean으로 등록한다.

```
개발자가 작성한 것:               Spring이 실제로 등록하는 것:
┌──────────────────┐            ┌──────────────────────────────┐
│  MemberService   │            │  MemberService$$SpringCGLIB$$ │  ← 프록시 (자동 생성)
│                  │            │  ┌────────────────────────┐  │
│  save() {        │  ─감싼다→   │  │ save() {               │  │
│    // 내 로직     │            │  │   트랜잭션 열기 ────┐   │  │
│  }               │            │  │   super.save() ────┼─→ │──┼→ 진짜 MemberService.save()
└──────────────────┘            │  │   커밋/롤백 ◀──────┘   │  │
                                │  │ }                      │  │
                                │  └────────────────────────┘  │
                                └──────────────────────────────┘
```

당신이 `memberService.save(...)`를 호출하면, 사실은 이 프록시의 `save()`가 먼저 실행된다. 프록시는 이렇게 동작한다:

```
memberService.save() 호출
        │
        ▼
┌────────────────────────────────────┐
│ 프록시 (대리인)                      │
│  ① 트랜잭션 시작 (BEGIN)             │  ← 부가 기능 (횡단 관심사)
│  ②  ┌──────────────────────────┐   │
│     │ 진짜 MemberService.save()│   │  ← 당신이 짠 비즈니스 로직
│     │  (원래의 핵심 로직)       │   │
│     └──────────────────────────┘   │
│  ③ 성공하면 COMMIT / 예외나면 ROLLBACK │  ← 부가 기능
└────────────────────────────────────┘
```

즉 `@Transactional`의 정체는 이것이다: **"이 메서드를 프록시로 감싸서, 앞뒤에 트랜잭션 열고 닫는 코드를 끼워 넣어라"**는 지시서. `@Cacheable`(앞뒤에 캐시 조회/저장), `@Async`(별도 스레드에서 실행), `@PreAuthorize`(앞에 권한 검사) 모두 **똑같은 프록시 메커니즘**이다. 애노테이션마다 프록시가 끼워 넣는 코드만 다를 뿐이다.

#### 두 종류의 프록시: CGLIB vs JDK 동적 프록시

Spring은 상황에 따라 두 가지 방식으로 프록시를 만든다.

| 방식 | 조건 | 원리 |
|------|------|------|
| **JDK 동적 프록시** | Bean이 **인터페이스를 구현**한 경우 | 같은 인터페이스를 구현한 프록시 객체를 만든다 |
| **CGLIB** | 인터페이스가 없는 **구체 클래스**인 경우 | 클래스를 **상속(subclass)**한 프록시를 만든다 (Spring Boot 기본) |

Spring Boot는 기본적으로 CGLIB을 쓴다. 그래서 프록시 클래스 이름이 `MemberService$$SpringCGLIB$$abc123`처럼 원본을 상속한 형태로 나온다. (CGLIB이 클래스를 상속하기 때문에, 프록시 대상 메서드는 `final`이면 안 된다 — 상속으로 오버라이드할 수 없으니까.)

> **직접 확인해보자 (실행 데모).** `chapter06-spring-data-jpa`에 `ProxyRevealRunner`라는 `CommandLineRunner`를 넣어두었다. 앱을 실행하면 시작 로그에 이렇게 찍힌다:
>
> ```
> [프록시 폭로] 내가 작성한 클래스   : com.edu.jpa.service.MemberService
> [프록시 폭로] 실제 주입된 런타임 클래스: com.edu.jpa.service.MemberService$$SpringCGLIB$$...
> [프록시 폭로] 프록시 여부           : 예 (Spring이 감싼 프록시)
> ```
>
> 분명 내가 만든 클래스는 `MemberService`인데, `memberService.getClass().getName()`을 찍어보면 `$$SpringCGLIB$$`가 붙은 다른 클래스가 나온다. **이것이 프록시가 실재한다는 눈으로 보는 증거**다. `@Transactional`을 지우고 다시 실행하면 프록시가 사라지는 것도 확인해볼 수 있다.

#### 전공자가 반드시 알아야 할 두 가지 함정

프록시라는 사실을 모르면, 다음 두 버그에서 몇 시간을 날린다. **이 두 가지는 면접 단골 질문이기도 하다.**

**함정 1: 자기 호출(self-invocation)은 프록시를 우회한다**

```java
@Service
public class MemberService {

    public void outer() {
        // ...
        this.inner();   // ← 이 호출은 프록시를 거치지 않는다!
    }

    @Transactional
    public void inner() {   // @Transactional이 무시된다!
        // ...
    }
}
```

왜? 프록시는 **바깥에서 `memberService.inner()`로 들어올 때만** 개입한다. `outer()` 안에서 `this.inner()`를 부르면, `this`는 프록시가 아니라 **진짜 원본 객체**다. 원본 객체의 `inner()`를 직접 부르니, 트랜잭션을 여는 프록시 코드를 건너뛴다.

```
정상 (프록시 개입):                  self-invocation (프록시 우회):
외부 → [프록시] → 원본.inner()       외부 → [프록시] → 원본.outer()
        └ 트랜잭션 O                          └ 원본.inner() 직접 호출
                                              └ 트랜잭션 X (this는 원본이니까)
```

해결책: `inner()`를 별도 Bean으로 분리하거나, 자기 자신의 프록시를 주입받아 호출한다. 하지만 **가장 중요한 건 "왜 안 걸리는지"를 아는 것**이다.

**함정 2: Spring이 관리하는 Bean에만 적용된다**

`@Transactional`은 프록시가 감쌀 수 있는 객체, 즉 **Spring 컨테이너가 관리하는 Bean**에만 동작한다. 당신이 `new MemberService()`로 직접 만든 객체는 프록시가 아니므로 `@Transactional`이 전혀 걸리지 않는다. 애노테이션은 프록시를 통해서만 살아난다.

#### 뿌리까지: 리플렉션과 AOP

두 개의 CS 개념이 이 모든 것을 떠받친다.

**① 리플렉션(Reflection) — Spring이 애노테이션을 "읽는" 방법.** 자바는 런타임에 클래스 자신의 정보(메서드, 필드, 붙어 있는 애노테이션)를 코드로 조회할 수 있다. 이것이 리플렉션이다. Spring은 **앱이 시작될 때** 모든 Bean 후보 클래스를 리플렉션으로 훑으면서 "이 메서드에 `@Transactional`이 붙어 있나?"를 검사한다. 붙어 있으면 "이 Bean은 프록시로 감싸야겠다"고 표시한다. 즉 애노테이션은 **컴파일 타임의 표식**이고, 리플렉션은 그 표식을 **런타임에 읽어내는 눈**이다.

**② AOP(관점 지향 프로그래밍) — 프록시가 해결하는 "문제".** 트랜잭션, 로깅, 보안 검사, 캐싱은 특정 비즈니스 로직에 속하지 않고 **여러 메서드에 공통으로 걸쳐 있다**. 이런 것을 **횡단 관심사(cross-cutting concern)**라고 부른다. 만약 프록시가 없다면, 모든 Service 메서드마다 이렇게 써야 한다:

```java
public void save() {
    tx.begin();               // 중복
    try {
        // 진짜 로직
        tx.commit();          // 중복
    } catch (Exception e) {
        tx.rollback();        // 중복
        throw e;
    }
}
```

이 반복이 수백 개 메서드에 퍼진다. AOP는 **"이 반복되는 부가 코드를 한 곳에 모아서, 대상 메서드 앞뒤에 자동으로 끼워 넣자"**는 발상이다. 그 "끼워 넣기"를 구현하는 도구가 프록시다. 정리하면:

```
애노테이션  = 어디에 부가 기능을 넣을지 표시하는 표식
리플렉션    = 시작 시점에 그 표식을 읽어내는 메커니즘
프록시      = 표식이 있는 메서드를 감싸 부가 기능을 끼워 넣는 실체
AOP        = "횡단 관심사를 프록시로 분리한다"는 설계 사상 전체
```

**이 책의 핵심 명제가 여기서 완성된다: "애노테이션은 마법이 아니다"는 곧 "애노테이션은 프록시다"이다.** 이제 `@Transactional`을 볼 때마다, 그 뒤에서 조용히 트랜잭션을 열고 닫는 대리인을 떠올릴 수 있어야 한다.

---

### 2.7 서버는 멀티스레드다 (thread-per-request)

> "§2.4에서 '싱글톤 Bean에 상태를 저장하면 안 된다'고 규칙만 말했다. 이제 그 **이유의 메커니즘**을 밝힌다. 규칙은 잊어버리지만, 메커니즘을 이해하면 평생 안 잊는다."

앞에서 "싱글톤 Bean에 상태를 저장하면 여러 스레드가 공유해서 위험하다"고 했다. 그런데 **애초에 왜 여러 스레드가 하나의 Bean을 동시에 쓰는가?** 이 질문에 답하려면 서버가 요청을 처리하는 구조를 알아야 한다.

#### 스레드 풀: 요청 하나에 스레드 하나

Spring Boot 안에는 **톰캣(Tomcat)**이라는 서블릿 컨테이너가 내장되어 있다. 톰캣은 **스레드 풀(thread pool)**을 갖고 있다. HTTP 요청이 들어올 때마다 톰캣은 풀에서 **놀고 있는 워커 스레드 하나를 꺼내** 그 요청을 처리시킨다. 이것이 **thread-per-request(요청당 스레드)** 모델이다.

```
요청 A ─┐                    ┌─ 워커 스레드 1 ─┐
요청 B ─┤   톰캣 스레드 풀     ├─ 워커 스레드 2 ─┼──→ 하나뿐인 싱글톤 Bean
요청 C ─┤  (예: 최대 200개)   ├─ 워커 스레드 3 ─┤    MemberService
요청 D ─┘                    └─ 워커 스레드 4 ─┘    (모든 스레드가 공유!)
```

핵심은 여기다: **스레드는 요청마다 다르지만, `MemberService` Bean은 딱 하나(싱글톤)다.** 초당 수백 개 요청이 오면, 수백 개 워커 스레드가 **동시에 같은 `MemberService` 인스턴스의 메서드를 실행**한다.

#### 왜 가변 필드가 경쟁 상태(race condition)인가

이제 §2.4의 경고가 왜 위험한지 기계 수준에서 보인다.

```java
@Service
public class MemberService {
    private User currentUser;   // 하나뿐인 필드를 모든 스레드가 공유

    public void process(User u) {
        this.currentUser = u;   // Thread A가 홍길동을 넣는 순간...
        // ... 아주 짧은 틈 ...
        doSomething(currentUser); // Thread B가 이미 김철수로 덮어썼다면?
    }
}
```

Thread A가 `currentUser = 홍길동`을 넣고 다음 줄로 가기 직전, Thread B가 끼어들어 `currentUser = 김철수`로 덮어쓴다. 그러면 A는 자기가 넣은 홍길동이 아니라 **B의 김철수를 처리**한다. 이것이 **경쟁 상태(race condition)**다. 아무 데이터도 안 섞이는 것처럼 보이다가, 트래픽이 몰릴 때만 간헐적으로 남의 데이터가 튀어나온다 — 재현도 어려운 최악의 버그다.

**그래서 Service는 상태를 갖지 않는 것(stateless)이 안전하다.** 메서드의 지역 변수와 매개변수는 **각 스레드의 스택에 따로 존재**하므로 절대 섞이지 않는다. 공유되는 것은 오직 **필드(인스턴스 상태)**뿐이다. 필드에 요청별 데이터를 담지 않으면, 스레드 수백 개가 동시에 들어와도 안전하다.

```
안전 (stateless):                    위험 (stateful):
각 스레드 → process(u) {             각 스레드 → process() {
   User local = u;   ← 스택(독립)       this.field = ...  ← 힙의 공유 필드
}                                    }
```

#### 커넥션 풀: 스레드 풀의 짝

여기서 자연스럽게 또 하나의 CS 개념이 등장한다. 워커 스레드마다 DB 작업을 하려면 **DB 커넥션**이 필요하다. 그런데 DB 커넥션은 만들 때마다 TCP 연결 + 인증을 거쳐야 해서 **비싸다(수십 ms)**. 요청마다 새로 만들면 감당이 안 된다.

그래서 이 프로젝트의 모든 앱은 **HikariCP**라는 **커넥션 풀(connection pool)**을 쓴다. (Spring Boot의 기본 커넥션 풀이라 명시적으로 설정하지 않아도 항상 돌고 있다.) 커넥션 풀은 DB 연결을 미리 여러 개 만들어 놓고, 스레드가 DB가 필요할 때 **빌려 쓰고 반납**하게 한다.

```
톰캣 스레드 풀            HikariCP 커넥션 풀           PostgreSQL
┌─ 스레드 1 ─┐  빌림→   ┌─ 커넥션 1 ─┐   TCP연결    ┌────────┐
├─ 스레드 2 ─┤  ←반납   ├─ 커넥션 2 ─┤  ─────────→  │  DB    │
├─ 스레드 3 ─┤          ├─ 커넥션 3 ─┤              │        │
└─ 스레드 N ─┘          └ (예: 10개) ┘              └────────┘
   (예: 200개)          커넥션은 유한하다!
```

**중요한 실무 감각:** 커넥션 풀 크기는 **유한**하다(HikariCP 기본 10개). 스레드는 200개인데 커넥션이 10개면, 11번째 스레드는 커넥션이 반납될 때까지 **기다린다**. 그래서 트랜잭션을 오래 붙잡고 있으면(예: 트랜잭션 안에서 외부 API를 호출하며 3초 대기) 커넥션이 고갈되어 전체 서버가 멈출 수 있다. "스레드 풀 크기 vs 커넥션 풀 크기 vs DB가 감당할 수 있는 동시 연결 수"의 균형이 성능 튜닝의 핵심이다.

정리하면, **thread-per-request → 싱글톤 공유 → 그래서 stateless여야 함 → DB는 커넥션 풀로 공유**. 이 네 가지가 하나의 이야기로 연결된다.

---

## Chapter 3: 웹 애플리케이션의 구조

> "웹 개발자라면 HTTP를 이해해야 한다. HTTP를 모르고 웹 개발을 하는 것은, 도로교통법을 모르고 운전하는 것과 같다."

---

### 3.1 HTTP 프로토콜 기초

#### 요청(Request)과 응답(Response)

HTTP는 **클라이언트가 요청하고, 서버가 응답하는** 단순한 프로토콜이다.

```
클라이언트 (브라우저)                    서버 (Spring Boot)
     │                                    │
     │  ── HTTP 요청 ──────────────→      │
     │     GET /users/1                   │
     │     Accept: application/json       │
     │                                    │
     │  ←─────────────── HTTP 응답 ──     │
     │     200 OK                         │
     │     {"name": "김철수", "age": 28}   │
     │                                    │
```

#### 비유: "편지 보내기"

HTTP 요청은 편지와 같다:

```
┌─────────────────────────────────────┐
│  받는 사람 주소 (URL): /users/1      │  ← 어디로 보낼 것인가
│  보내는 방법 (Method): GET           │  ← 무엇을 할 것인가
│  우표/등기 (Header):                 │  ← 부가 정보
│    Content-Type: application/json   │
│    Authorization: Bearer xxx        │
│                                     │
│  편지 내용 (Body):                   │  ← 보낼 데이터 (POST/PUT)
│    {"name": "김철수", "age": 28}     │
└─────────────────────────────────────┘
```

#### HTTP 메서드의 의미

HTTP 메서드는 **"이 요청이 무엇을 하려는 것인가"**를 나타낸다:

| 메서드   | 의미    | 비유              | 예시                  |
|---------|--------|-------------------|----------------------|
| GET     | 조회    | 도서관에서 책 빌리기 | GET /users/1         |
| POST    | 생성    | 새 책 기증하기      | POST /users          |
| PUT     | 전체수정 | 책 전체 교체하기    | PUT /users/1         |
| PATCH   | 부분수정 | 책의 특정 페이지 수정| PATCH /users/1       |
| DELETE  | 삭제    | 책 폐기하기        | DELETE /users/1      |

**왜?** 이 구분이 중요한 이유:
- `GET`은 **안전**하다. 아무리 호출해도 데이터가 변하지 않는다.
- `POST`는 호출할 때마다 **새로운 리소스**가 생긴다.
- `PUT`은 **같은 요청을 여러 번 해도 결과가 같다** (멱등성).
- `DELETE`도 **멱등**하다. 이미 삭제된 것을 다시 삭제해도 결과는 같다.

#### 상태 코드가 왜 중요한가

상태 코드는 **서버가 요청을 어떻게 처리했는지** 알려준다:

```
2xx: 성공
  200 OK         → "잘 처리했어!"
  201 Created    → "새로 만들었어!"
  204 No Content → "처리했는데 줄 건 없어" (DELETE 후)

4xx: 클라이언트 잘못
  400 Bad Request  → "너 요청이 잘못됐어" (유효성 검증 실패)
  401 Unauthorized → "너 누군지 모르겠어" (인증 실패)
  403 Forbidden    → "너인 건 알겠는데, 권한이 없어" (인가 실패)
  404 Not Found    → "그런 거 없어"
  409 Conflict     → "이미 있어" (중복 생성)

5xx: 서버 잘못
  500 Internal Server Error → "내(서버) 잘못이야... 미안..."
```

**왜 상태 코드를 제대로 써야 하는가?**

모든 응답을 `200`으로 보내고 body에 `{"success": false, "message": "에러"}`를 넣는 개발자가 있다. 이것은 **안티패턴**이다.

```java
// 나쁜 예: 모든 것을 200으로
@GetMapping("/users/{id}")
public ResponseEntity<?> getUser(@PathVariable Long id) {
    try {
        return ResponseEntity.ok(userService.findById(id));
    } catch (Exception e) {
        return ResponseEntity.ok(Map.of("error", e.getMessage())); // 200인데 에러?
    }
}

// 좋은 예: 상황에 맞는 상태 코드
@GetMapping("/users/{id}")
public ResponseEntity<UserResponse> getUser(@PathVariable Long id) {
    return ResponseEntity.ok(userService.findById(id)); // 200
}
// UserNotFoundException 발생 시 → 404 (전역 예외 처리기에서)
```

클라이언트(프론트엔드)는 상태 코드를 보고 **어떤 일이 일어났는지 즉시 판단**한다. 200이면 성공 로직, 401이면 로그인 페이지로 이동, 500이면 에러 메시지 표시. 모든 게 200이면 이런 분기 처리가 불가능하다.

---

### 3.2 REST API: 왜 이렇게 설계하는가?

#### REST의 핵심 원칙

REST(Representational State Transfer)는 **리소스를 URL로 표현하고, 행위는 HTTP 메서드로 표현**하는 아키텍처 스타일이다.

핵심 원칙:
1. **리소스(Resource)** = 명사로 표현 (`/users`, `/posts`, `/comments`)
2. **행위(Action)** = HTTP 메서드로 표현 (`GET`, `POST`, `PUT`, `DELETE`)
3. **URL에 동사를 넣지 않는다**

#### 나쁜 예 vs 좋은 예

```
나쁜 예 (동사가 URL에 포함):
GET    /getUser?id=1
POST   /createUser
POST   /deleteUser?id=1
GET    /getUserList
POST   /updateUserName

좋은 예 (리소스 + HTTP 메서드):
GET    /users/1          → 1번 사용자 조회
POST   /users            → 새 사용자 생성
DELETE /users/1           → 1번 사용자 삭제
GET    /users             → 사용자 목록 조회
PATCH  /users/1           → 1번 사용자 정보 수정
```

#### 왜 RESTful이 중요한가?

**예측 가능성**이다.

REST 규칙을 따르면, API 문서를 안 봐도 어느 정도 예측이 가능하다:
- "게시글 목록?" → `GET /posts`
- "3번 게시글?" → `GET /posts/3`
- "3번 게시글의 댓글?" → `GET /posts/3/comments`
- "게시글 작성?" → `POST /posts`
- "3번 게시글 삭제?" → `DELETE /posts/3`

이것은 마치 **도로 표지판**과 같다. 많은 나라에서 빨간색은 "금지/위험", 파란색은 "안내/지시"를 의미한다. 표준을 따르면 **처음 보는 API도 직관적으로 이해**할 수 있다.

```java
@RestController
@RequestMapping("/api/posts")
public class PostController {

    @GetMapping                    // GET /api/posts → 목록 조회
    public List<PostResponse> getPosts() { ... }

    @GetMapping("/{id}")           // GET /api/posts/1 → 단건 조회
    public PostResponse getPost(@PathVariable Long id) { ... }

    @PostMapping                   // POST /api/posts → 생성
    public PostResponse createPost(@RequestBody PostRequest request) { ... }

    @PutMapping("/{id}")           // PUT /api/posts/1 → 수정
    public PostResponse updatePost(@PathVariable Long id,
                                   @RequestBody PostRequest request) { ... }

    @DeleteMapping("/{id}")        // DELETE /api/posts/1 → 삭제
    public void deletePost(@PathVariable Long id) { ... }
}
```

---

### 3.3 Spring MVC의 요청 처리 흐름

#### 요청이 처리되는 전체 과정

HTTP 요청이 들어오면 Spring 내부에서는 어떤 일이 일어나는가?

```
HTTP 요청: GET /api/users/1

┌────────────────────────────────────────────────────────────┐
│                     Spring Boot 서버                        │
│                                                            │
│  ① DispatcherServlet (모든 요청의 관문)                     │
│     │                                                      │
│     ↓                                                      │
│  ② HandlerMapping ("이 URL은 어느 Controller가 처리하지?")   │
│     │                                                      │
│     ↓                                                      │
│  ③ Controller (요청 받기, 응답 반환)                         │
│     │                                                      │
│     ↓                                                      │
│  ④ Service (비즈니스 로직 실행)                              │
│     │                                                      │
│     ↓                                                      │
│  ⑤ Repository (DB에서 데이터 조회)                          │
│     │                                                      │
│     ↓                                                      │
│  ⑥ Database (실제 데이터 저장소)                             │
│                                                            │
└────────────────────────────────────────────────────────────┘
```

#### 비유: "레스토랑"

이 흐름을 레스토랑에 비유하면 완벽하게 이해된다:

```
손님(클라이언트): "스테이크 주세요!"

┌─────────────────────────────────────────────┐
│  레스토랑 (Spring Boot)                      │
│                                             │
│  ① 안내원 (DispatcherServlet)               │
│     "어서오세요! 몇 번 테이블이시죠?"          │
│     → 적절한 웨이터에게 연결                  │
│                                             │
│  ② 매니저 (HandlerMapping)                  │
│     "스테이크는 3번 웨이터가 담당이야"         │
│                                             │
│  ③ 웨이터 (Controller)                      │
│     "스테이크 주문 받았습니다!"               │
│     → 주방에 주문 전달                       │
│     → 음식 나오면 손님에게 서빙               │
│                                             │
│  ④ 주방장 (Service)                         │
│     "스테이크를 미디엄으로 굽자"              │
│     → 식재료가 필요하면 냉장고 관리자에게 요청  │
│                                             │
│  ⑤ 냉장고 관리자 (Repository)               │
│     "소고기 등심 1인분 꺼내겠습니다"           │
│     → 냉장고에서 재료 꺼냄                    │
│                                             │
│  ⑥ 냉장고 (Database)                       │
│     실제 식재료가 보관된 곳                    │
└─────────────────────────────────────────────┘
```

**왜 DispatcherServlet이 필요한가?**

안내원 없는 레스토랑을 상상해보자. 손님이 직접 주방에 가서 "스테이크 해주세요"라고 해야 한다. 주방장은 요리도 하면서 주문도 받아야 한다. 혼란스럽지 않겠는가?

DispatcherServlet은 **모든 HTTP 요청을 받아서, 적절한 Controller에게 전달**하는 중앙 관리자이다. 이 덕분에 Controller는 HTTP 요청 파싱 같은 저수준 작업을 신경 쓸 필요 없이 비즈니스 로직에만 집중할 수 있다.

---

### 3.4 레이어드 아키텍처: 왜 계층을 나누는가?

#### 각 계층의 역할

```
┌─────────────────────────────────────────────────────────┐
│  Controller 계층 (Presentation Layer)                    │
│  역할: HTTP 요청/응답 처리                                │
│  비유: 웨이터 - 주문을 받고, 음식을 서빙한다               │
│  알아야 할 것: HTTP, JSON, 요청 파라미터                   │
│  몰라야 할 것: DB 쿼리, 비즈니스 규칙                     │
├─────────────────────────────────────────────────────────┤
│  Service 계층 (Business Layer)                           │
│  역할: 비즈니스 로직 실행                                 │
│  비유: 주방장 - 레시피에 따라 요리한다                     │
│  알아야 할 것: 업무 규칙, 도메인 로직                     │
│  몰라야 할 것: HTTP, JSON, Controller의 존재             │
├─────────────────────────────────────────────────────────┤
│  Repository 계층 (Data Access Layer)                     │
│  역할: 데이터 저장/조회                                   │
│  비유: 냉장고 관리자 - 식재료를 꺼내고 넣는다              │
│  알아야 할 것: SQL, DB 관련 로직                          │
│  몰라야 할 것: 비즈니스 규칙, HTTP                        │
└─────────────────────────────────────────────────────────┘
```

#### 왜 계층을 나누는가? (관심사의 분리)

**1. 변경의 파급 효과를 줄인다**

API 응답 형식을 바꾸고 싶을 때 → Controller만 수정
비즈니스 규칙이 바뀔 때 → Service만 수정
DB를 MySQL에서 PostgreSQL로 바꿀 때 → Repository만 수정

**2. 테스트가 쉬워진다**

Service 로직을 테스트할 때 HTTP 서버를 띄울 필요 없이, Service만 단독으로 테스트할 수 있다.

**3. 역할이 명확해진다**

신입 개발자가 팀에 합류했을 때, "비즈니스 로직은 Service에 있다"라고 하면 바로 찾을 수 있다.

#### 안티패턴: Controller에 비즈니스 로직 넣기

```java
// 안티패턴: Controller에 모든 로직이 있음
@RestController
public class OrderController {

    @PostMapping("/orders")
    public ResponseEntity<?> createOrder(@RequestBody OrderRequest request) {
        // 재고 확인 - 비즈니스 로직
        Product product = productRepository.findById(request.getProductId())
            .orElseThrow();
        if (product.getStock() < request.getQuantity()) {
            throw new RuntimeException("재고 부족");
        }

        // 가격 계산 - 비즈니스 로직
        int totalPrice = product.getPrice() * request.getQuantity();
        if (request.hasCoupon()) {
            totalPrice = totalPrice * 90 / 100; // 10% 할인
        }

        // 주문 저장 - 데이터 접근
        Order order = new Order(product, request.getQuantity(), totalPrice);
        orderRepository.save(order);

        // 재고 차감 - 비즈니스 로직
        product.decreaseStock(request.getQuantity());
        productRepository.save(product);

        // 이메일 발송 - 비즈니스 로직
        emailService.sendOrderConfirmation(order);

        return ResponseEntity.ok(order);
    }
}
```

이것은 마치 **웨이터가 주문도 받고, 요리도 하고, 냉장고 관리도 하는 것**이다. 웨이터가 퇴사하면? 주방도 마비된다.

```java
// 올바른 구조: 각 계층이 자기 역할만
@RestController
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/orders")
    public ResponseEntity<OrderResponse> createOrder(
            @Valid @RequestBody OrderRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(orderService.createOrder(request));  // Service에 위임
    }
}

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final EmailService emailService;

    @Transactional
    public OrderResponse createOrder(OrderRequest request) {
        Product product = productRepository.findById(request.getProductId())
            .orElseThrow(() -> new ProductNotFoundException(request.getProductId()));

        product.validateStock(request.getQuantity());

        int totalPrice = calculatePrice(product, request);
        Order order = Order.create(product, request.getQuantity(), totalPrice);

        product.decreaseStock(request.getQuantity());
        orderRepository.save(order);

        emailService.sendOrderConfirmation(order);

        return OrderResponse.from(order);
    }
}
```

---

### 3.5 DTO 패턴: 왜 Entity를 직접 노출하면 안 되는가?

#### Entity를 직접 노출했을 때의 문제

```java
// Entity
@Entity
public class User {
    @Id @GeneratedValue
    private Long id;
    private String email;
    private String password;   // 비밀번호!
    private String name;
    private String role;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean deleted;   // 소프트 삭제 플래그
}

// 나쁜 예: Entity를 직접 응답으로 반환
@GetMapping("/users/{id}")
public User getUser(@PathVariable Long id) {
    return userRepository.findById(id).orElseThrow();
}
```

이 API의 응답은 다음과 같다:
```json
{
    "id": 1,
    "email": "user@example.com",
    "password": "$2a$10$...",    // 암호화되었지만 해시값 노출!
    "name": "김철수",
    "role": "ADMIN",             // 역할 정보 노출!
    "createdAt": "2024-01-01",
    "updatedAt": "2024-03-15",
    "deleted": false             // 내부 구현 상세 노출!
}
```

#### 3가지 문제

**1. 보안 문제**
`password` 해시값, `role`, `deleted` 같은 민감한 정보가 그대로 노출된다. `@JsonIgnore`로 필드를 숨길 수 있지만, 이는 **Entity에 JSON 관련 로직이 침투**하는 것이다.

**2. 결합 문제**
DB 스키마가 변경되면 API 응답도 변경된다. `email` 컬럼명을 `emailAddress`로 바꾸면? 프론트엔드 코드가 깨진다.

**3. 유연성 문제**
같은 `User` Entity인데, 목록 조회에서는 `id`, `name`만, 상세 조회에서는 `email`까지, 관리자 조회에서는 `role`까지 보여주고 싶다면?

#### DTO로 해결

```java
// 요청 DTO
public record CreateUserRequest(
    @NotBlank String email,
    @NotBlank @Size(min = 8) String password,
    @NotBlank String name
) {}

// 응답 DTO (목록용)
public record UserSummaryResponse(
    Long id,
    String name
) {
    public static UserSummaryResponse from(User user) {
        return new UserSummaryResponse(user.getId(), user.getName());
    }
}

// 응답 DTO (상세용)
public record UserDetailResponse(
    Long id,
    String email,
    String name,
    LocalDateTime createdAt
) {
    public static UserDetailResponse from(User user) {
        return new UserDetailResponse(
            user.getId(), user.getEmail(),
            user.getName(), user.getCreatedAt()
        );
    }
}
```

```
Entity와 DTO의 관계:

      ┌─ CreateUserRequest (요청)
      │
Client ←→ Controller ←→ Service ←→ Repository ←→ DB
      │                     ↕
      ├─ UserSummaryResponse  (Entity ←→ DTO 변환)
      └─ UserDetailResponse

외부(Client)와는 DTO로만 통신
내부(Service/Repository)에서는 Entity 사용
```

비유하면, DTO는 **명함**과 같다. 내 모든 개인정보(주민번호, 계좌번호)를 공개하지 않고, 상대방에게 필요한 정보(이름, 연락처, 직함)만 담아서 전달한다.

---

### 3.6 Validation: 왜 서버에서도 검증해야 하는가?

#### 클라이언트 검증만으로는 부족한 이유

프론트엔드에서 JavaScript로 "이메일 형식 맞는지", "비밀번호 8자 이상인지" 검증하고 있다면, 서버에서 또 검증할 필요가 있는가?

**반드시 있다.**

```
브라우저에서 검증:
  사용자 → [JavaScript 검증] → 서버
  "이메일 형식 맞는지 확인!" → OK

하지만...
  Postman/curl → 서버  (JavaScript 검증을 우회!)
  해커 → 서버           (클라이언트 검증을 완전히 무시!)
```

클라이언트 검증은 **사용자 편의를 위한 것**이지, **보안을 위한 것이 아니다.** 서버 검증이 진짜 검증이다.

비유하면:
- **클라이언트 검증** = 은행 입구의 번호표 기계 (안내 목적)
- **서버 검증** = 은행 창구 직원의 신분증 확인 (보안 목적)

번호표 기계를 우회하고 직접 창구에 가는 사람도 있다. 그래서 창구 직원이 반드시 신분증을 확인해야 한다.

#### @Valid + Bean Validation

```java
// 요청 DTO에 검증 규칙 선언
public record CreateUserRequest(
    @NotBlank(message = "이메일은 필수입니다")
    @Email(message = "올바른 이메일 형식이 아닙니다")
    String email,

    @NotBlank(message = "비밀번호는 필수입니다")
    @Size(min = 8, message = "비밀번호는 8자 이상이어야 합니다")
    String password,

    @NotBlank(message = "이름은 필수입니다")
    @Size(min = 2, max = 20, message = "이름은 2~20자여야 합니다")
    String name
) {}

// Controller에서 @Valid로 검증 활성화
@PostMapping("/users")
public ResponseEntity<UserResponse> createUser(
        @Valid @RequestBody CreateUserRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(userService.createUser(request));
}
```

`@Valid`를 붙이면 Spring이 **자동으로 검증을 수행**하고, 검증에 실패하면 **400 Bad Request**를 반환한다.

#### 전역 예외 처리

검증 실패 시 Spring의 기본 에러 응답은 보기 좋지 않다. 전역 예외 처리기로 일관된 에러 응답을 만들 수 있다:

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .collect(Collectors.joining(", "));

        return ResponseEntity.badRequest()
            .body(new ErrorResponse("VALIDATION_ERROR", message));
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(
            UserNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(new ErrorResponse("USER_NOT_FOUND", e.getMessage()));
    }
}
```

**왜 전역 예외 처리가 필요한가?**

각 Controller마다 try-catch를 쓰면 **중복 코드**가 생기고, **에러 응답 형식이 제각각**이 된다. `@RestControllerAdvice`를 사용하면 애플리케이션 전체에서 일관된 에러 처리가 가능하다.

---

## Chapter 4: 데이터베이스와 JPA

> "JPA를 이해하려면 먼저 JPA가 없을 때의 고통을 알아야 한다."

---

### 4.1 왜 ORM이 필요한가?

#### SQL 직접 작성의 문제

Spring 없이 JDBC로 DB를 다루면 이런 코드를 작성해야 한다:

```java
// JDBC 직접 사용 (JPA 없이)
public User findById(Long id) {
    String sql = "SELECT id, email, name, password, created_at FROM users WHERE id = ?";
    Connection conn = null;
    PreparedStatement stmt = null;
    ResultSet rs = null;

    try {
        conn = dataSource.getConnection();
        stmt = conn.prepareStatement(sql);
        stmt.setLong(1, id);
        rs = stmt.executeQuery();

        if (rs.next()) {
            User user = new User();
            user.setId(rs.getLong("id"));
            user.setEmail(rs.getString("email"));
            user.setName(rs.getString("name"));
            user.setPassword(rs.getString("password"));
            user.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
            return user;
        }
        return null;
    } catch (SQLException e) {
        throw new RuntimeException(e);
    } finally {
        // 리소스 정리 (닫는 순서도 중요!)
        if (rs != null) try { rs.close(); } catch (SQLException e) {}
        if (stmt != null) try { stmt.close(); } catch (SQLException e) {}
        if (conn != null) try { conn.close(); } catch (SQLException e) {}
    }
}
```

**문제점:**
1. **반복적**: 모든 엔티티마다 이런 코드를 작성해야 함 (User, Post, Comment, Order...)
2. **실수 가능**: 컬럼명 오타, 타입 불일치, 리소스 미정리
3. **DB 종속적**: MySQL 함수를 썼다면 PostgreSQL로 바꿀 때 SQL을 전부 수정해야 함
4. **객체-테이블 불일치**: Java 객체의 상속, 연관관계를 SQL로 표현하기 어려움

#### ORM: 객체와 테이블을 자동 매핑

**ORM(Object-Relational Mapping)**은 **Java 객체와 DB 테이블을 자동으로 매핑**해주는 기술이다.

```java
// JPA 사용 (같은 기능)
public User findById(Long id) {
    return entityManager.find(User.class, id); // 끝!
}
```

위의 수십 줄이 **한 줄**이 된다.

#### 비유: "통역사"

```
Java 세계                 JPA (통역사)              DB 세계
┌──────────┐             ┌──────────┐             ┌──────────┐
│ User 객체 │  ←────────→ │   JPA    │ ←────────→ │ users 테이블│
│ .name     │  Java 객체  │ (Hibernate)│  SQL      │ name 컬럼  │
│ .email    │  ←→ SQL    │          │            │ email 컬럼 │
│ .getId()  │  자동 변환  │          │            │ id 컬럼    │
└──────────┘             └──────────┘             └──────────┘
```

Java 개발자가 `user.setName("김철수")`라고 하면, JPA가 `UPDATE users SET name='김철수' WHERE id=1`로 **통역**해준다. 개발자는 SQL을 몰라도(물론 알면 좋지만) Java 객체만 다루면 된다.

#### JPA는 스펙, Hibernate는 구현체

이 관계를 이해하는 것이 중요하다:

```
JPA (Java Persistence API)  = 자동차 표준 규격 (이렇게 만들어야 한다)
Hibernate                    = 현대자동차 (규격에 맞게 만든 실제 제품)
EclipseLink                  = 기아자동차 (또 다른 구현체)

JDBC Driver와 비슷한 관계:
JDBC       = DB 접근 표준 규격
MySQL Driver = MySQL용 JDBC 구현체
PostgreSQL Driver = PostgreSQL용 JDBC 구현체
```

우리가 JPA 인터페이스를 사용하면, 내부적으로는 Hibernate가 실제 작업을 수행한다. 만약 Hibernate에 문제가 생기면 EclipseLink로 교체할 수 있다 (이론적으로는). 이것이 **인터페이스(표준)의 힘**이다.

---

### 4.2 Entity: 데이터베이스 테이블의 Java 버전

#### @Entity가 하는 일

```java
@Entity
@Table(name = "users")    // 테이블명 지정 (생략하면 클래스명 사용)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 50)
    private String name;

    @Enumerated(EnumType.STRING)
    private Role role;

    private LocalDateTime createdAt;

    protected User() {} // JPA용 기본 생성자 (protected로 외부 사용 방지)

    public User(String email, String password, String name, Role role) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.role = role;
        this.createdAt = LocalDateTime.now();
    }
}
```

`@Entity`를 붙이면 JPA는 이 클래스를 **DB 테이블과 매핑**한다:

```
Java 클래스 User          ←→      DB 테이블 users
필드 id (Long)            ←→      컬럼 id (BIGINT, PK)
필드 email (String)       ←→      컬럼 email (VARCHAR(100), UNIQUE)
필드 password (String)    ←→      컬럼 password (VARCHAR(255))
필드 name (String)        ←→      컬럼 name (VARCHAR(50))
필드 role (Role)          ←→      컬럼 role (VARCHAR, enum)
필드 createdAt            ←→      컬럼 created_at (TIMESTAMP)
```

#### 왜 기본 생성자가 필요한가?

많은 초보자가 이 부분에서 혼란스러워한다. "왜 아무 인자 없는 생성자가 필요하지?"

JPA(Hibernate)가 DB에서 데이터를 조회할 때 하는 일:
1. **리플렉션으로 빈 객체를 생성** (기본 생성자 호출)
2. DB에서 가져온 값으로 필드를 설정

```
DB 조회 결과: {id=1, email="user@test.com", name="김철수"}

1단계: User user = new User();        // 기본 생성자로 빈 객체 생성
2단계: user.id = 1;                   // 리플렉션으로 필드 설정
3단계: user.email = "user@test.com";
4단계: user.name = "김철수";
```

기본 생성자가 없으면 1단계에서 실패한다. **JPA의 내부 메커니즘이 필요로 하는 것**이다.

`protected`로 선언하는 이유: 외부에서 `new User()`로 불완전한 객체를 만드는 것을 방지하면서, JPA가 내부적으로 사용할 수 있게 한다.

#### @Id와 @GeneratedValue: 기본 키 전략

```java
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;
```

**IDENTITY 전략:** DB가 자동 증가(AUTO_INCREMENT)로 ID를 생성한다.
- 장점: 간단함, 대부분의 상황에서 충분
- 단점: 배치 INSERT 시 성능 이슈 (JPA가 ID를 미리 알 수 없어 매번 INSERT 후 SELECT)

**SEQUENCE 전략:** DB 시퀀스를 사용하여 ID를 미리 할당받는다.
- 장점: 배치 INSERT 성능 우수
- 단점: 시퀀스를 지원하는 DB만 가능 (PostgreSQL, Oracle)

**실무 가이드:** PostgreSQL을 쓴다면 `SEQUENCE` 전략이 기본이자 권장 전략이다. `IDENTITY`는 MySQL에서 주로 사용한다. 대량 INSERT가 필요한 경우 반드시 `SEQUENCE`를 사용해야 한다.

#### 영속성 컨텍스트: JPA의 핵심 메커니즘

**영속성 컨텍스트(Persistence Context)**는 JPA의 가장 중요한 개념 중 하나이다. 이것을 이해하지 못하면 JPA의 동작이 마법처럼 보인다.

영속성 컨텍스트는 **Entity를 담아두는 1차 캐시**라고 생각하면 된다.

```
영속성 컨텍스트 (1차 캐시):
┌─────────────────────────────────────────┐
│  Key(ID)  │  Value(Entity)              │
│  ─────────┼───────────────────────────  │
│  1        │  User{id=1, name="김철수"}   │
│  2        │  User{id=2, name="이영희"}   │
└─────────────────────────────────────────┘
```

**1차 캐시의 이점:**
```java
// 같은 트랜잭션 내에서 같은 Entity를 두 번 조회하면?
User user1 = userRepository.findById(1L).orElseThrow(); // DB 쿼리 실행
User user2 = userRepository.findById(1L).orElseThrow(); // DB 쿼리 안 함! (캐시에서 가져옴)

System.out.println(user1 == user2); // true! 같은 객체
```

**변경 감지 (Dirty Checking):**

```java
@Transactional
public void updateUserName(Long id, String newName) {
    User user = userRepository.findById(id).orElseThrow();
    user.setName(newName);  // 이게 끝! save()를 호출하지 않아도 됨!
}
// 트랜잭션이 끝나면 JPA가 자동으로 UPDATE 쿼리를 실행
```

**왜?** JPA는 영속성 컨텍스트에 저장된 Entity의 **원본 스냅샷**을 가지고 있다. 트랜잭션이 커밋될 때, 현재 상태와 스냅샷을 비교해서 변경된 부분이 있으면 **자동으로 UPDATE 쿼리를 생성**한다.

비유하면 **Word 문서의 변경 추적 기능**과 같다. 문서를 수정하면 원본과 비교해서 어디가 바뀌었는지 자동으로 감지하고, 저장할 때 변경 사항만 반영한다.

---

### 4.3 JpaRepository: 마법의 인터페이스

#### 인터페이스만 만들면 구현체가 자동 생성되는 원리

```java
// 인터페이스만 선언하면 끝!
public interface UserRepository extends JpaRepository<User, Long> {
    // 아무것도 구현하지 않아도 CRUD 메서드가 이미 있음
}
```

이것만으로 다음 메서드들을 사용할 수 있다:
- `save(User user)` → INSERT 또는 UPDATE
- `findById(Long id)` → SELECT WHERE id = ?
- `findAll()` → SELECT *
- `deleteById(Long id)` → DELETE WHERE id = ?
- `count()` → SELECT COUNT(*)
- ...그 외 수십 개

**왜? 어떻게 가능한가?**

Spring Data JPA가 애플리케이션 시작 시 **프록시(Proxy)**라는 기술을 사용해서 **인터페이스의 구현체를 자동으로 생성**한다. 우리 눈에는 인터페이스만 있지만, 런타임에는 Spring이 만든 구현 클래스가 존재한다.

```
우리가 작성한 것:                  Spring이 런타임에 만든 것:
┌──────────────────┐             ┌──────────────────────────┐
│ UserRepository   │     →      │ SimpleJpaRepository      │
│ (인터페이스)      │             │ (자동 생성된 구현체)       │
│                  │             │                          │
│ findByEmail()    │             │ findByEmail() {          │
│                  │             │   // JPQL 자동 생성       │
│                  │             │   return em.createQuery  │
│                  │             │     ("SELECT u FROM ...") │
│                  │             │     .getResultList();    │
│                  │             │ }                        │
└──────────────────┘             └──────────────────────────┘
```

#### 쿼리 메서드: 메서드 이름이 곧 쿼리

```java
public interface UserRepository extends JpaRepository<User, Long> {

    // 메서드 이름을 분석해서 쿼리를 자동 생성!
    Optional<User> findByEmail(String email);
    // → SELECT * FROM users WHERE email = ?

    List<User> findByNameContaining(String keyword);
    // → SELECT * FROM users WHERE name LIKE '%keyword%'

    List<User> findByRoleAndDeletedFalse(Role role);
    // → SELECT * FROM users WHERE role = ? AND deleted = false

    boolean existsByEmail(String email);
    // → SELECT COUNT(*) > 0 FROM users WHERE email = ?

    long countByRole(Role role);
    // → SELECT COUNT(*) FROM users WHERE role = ?
}
```

Spring Data JPA가 메서드 이름을 **파싱**한다:
- `findBy` → SELECT 쿼리
- `Email` → WHERE email = ?
- `And` → AND
- `Containing` → LIKE '%...%'
- `OrderBy...Desc` → ORDER BY ... DESC

이것은 마치 **영어 문장을 SQL로 번역**하는 것과 같다.

#### @Query: 복잡한 쿼리가 필요할 때

메서드 이름으로 표현하기 어려운 복잡한 쿼리는 `@Query`를 사용한다:

```java
public interface PostRepository extends JpaRepository<Post, Long> {

    // JPQL (Java Persistence Query Language)
    @Query("SELECT p FROM Post p JOIN FETCH p.author WHERE p.id = :id")
    Optional<Post> findByIdWithAuthor(@Param("id") Long id);

    // Native SQL (정말 필요할 때만)
    @Query(value = "SELECT * FROM posts WHERE MATCH(title, content) AGAINST(:keyword)",
           nativeQuery = true)
    List<Post> fullTextSearch(@Param("keyword") String keyword);
}
```

**규칙:** 가능하면 쿼리 메서드 → 안 되면 JPQL(@Query) → 정말 안 되면 Native SQL 순서로 사용해야 한다.

#### 페이징: 왜 전체 데이터를 한번에 가져오면 안 되는가

게시판에 글이 100만 개 있는데 `findAll()`을 호출하면? 100만 개의 데이터가 DB에서 애플리케이션으로, 애플리케이션에서 클라이언트로 전송된다. **서버 메모리 폭발, 네트워크 과부하, 브라우저 멈춤**.

```java
// 나쁜 예: 전체 데이터를 한번에
@GetMapping("/posts")
public List<PostResponse> getAllPosts() {
    return postRepository.findAll().stream()    // 100만 개를 메모리에 로딩!
        .map(PostResponse::from)
        .toList();
}

// 좋은 예: 페이징
@GetMapping("/posts")
public Page<PostResponse> getPosts(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size) {
    Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
    return postRepository.findAll(pageable).map(PostResponse::from);
}
```

비유하면, 도서관에서 "모든 책 다 달라"고 하면 도서관이 마비된다. "신간 20권만 달라"고 하는 것이 페이징이다.

---

### 4.4 연관관계: 가장 어려운 부분

JPA에서 가장 어렵고, 가장 많은 문제를 일으키는 부분이다. 여기서 시간을 들여 확실하게 이해해야 한다.

#### @ManyToOne vs @OneToMany: 누가 주인인가?

게시글(Post)과 사용자(User)의 관계를 생각해보자:
- 한 사용자(User)는 여러 게시글(Post)을 작성할 수 있음 → **One**ToMany
- 하나의 게시글(Post)은 한 사용자(User)에 속함 → **Many**ToOne

```java
@Entity
public class Post {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)  // 연관관계의 주인
    @JoinColumn(name = "author_id")     // 외래키 컬럼명
    private User author;
}
```

```
DB 테이블:
┌─────────────────────┐        ┌─────────────────────┐
│ users               │        │ posts               │
├─────────────────────┤        ├─────────────────────┤
│ id (PK)             │←───────│ author_id (FK)      │
│ email               │        │ id (PK)             │
│ name                │        │ title               │
│ password            │        │ content             │
└─────────────────────┘        └─────────────────────┘

외래키(FK)는 posts 테이블에 있다 → Post가 연관관계의 주인
```

#### 비유: "외래키는 아파트 열쇠"

```
아파트 건물 (User) ─── 102호 (Post)
                  ─── 203호 (Post)
                  ─── 305호 (Post)

열쇠(외래키)는 누가 가지고 있는가? → 각 호실(Post)이 가지고 있음
건물(User)이 모든 방의 열쇠를 관리하지 않음

따라서: Post가 연관관계의 주인 (외래키를 가지고 있으니까)
```

**연관관계의 주인** = 외래키를 가진 쪽 = `@ManyToOne`이 있는 쪽

주인만이 연관관계를 변경(INSERT, UPDATE)할 수 있다. 주인이 아닌 쪽은 읽기만 가능하다.

#### FetchType.LAZY vs EAGER

```java
@ManyToOne(fetch = FetchType.LAZY)   // 지연 로딩 (권장)
private User author;

@ManyToOne(fetch = FetchType.EAGER)  // 즉시 로딩 (대부분의 경우 비권장)
private User author;
```

**EAGER (즉시 로딩):** Post를 조회하면 author(User)도 **무조건 함께 조회**
```sql
-- Post만 필요한데도 User까지 JOIN!
SELECT p.*, u.* FROM posts p JOIN users u ON p.author_id = u.id WHERE p.id = 1
```

**LAZY (지연 로딩):** Post를 조회할 때 author는 **실제로 접근할 때만 조회**
```sql
-- 1단계: Post만 조회
SELECT * FROM posts WHERE id = 1

-- 2단계: post.getAuthor().getName() 호출 시 그때서야 User 조회
SELECT * FROM users WHERE id = ?
```

**왜 LAZY가 기본이어야 하는가?**

게시글 목록을 조회할 때 작성자 이름이 필요하지 않다면? EAGER로 설정하면 **필요 없는 데이터를 항상 가져온다**. 성능 낭비이다.

비유하면:
- **EAGER** = 책을 빌릴 때 그 작가의 모든 책을 함께 빌리는 것 (필요 없는데!)
- **LAZY** = 책만 빌리고, 작가의 다른 책이 궁금하면 그때 찾아보는 것

> **규칙: 모든 연관관계는 LAZY로 설정하고, 필요한 경우에만 fetch join으로 함께 조회해야 한다.**

#### N+1 문제: 무엇이고, 왜 발생하고, 어떻게 해결하는가?

**N+1 문제는 JPA에서 가장 흔하고 치명적인 성능 문제이다.**

```java
// 게시글 10개를 조회하면서 각 게시글의 작성자 이름을 출력
List<Post> posts = postRepository.findAll(); // 쿼리 1번

for (Post post : posts) {
    System.out.println(post.getAuthor().getName()); // 작성자 조회: 쿼리 10번!
}
// 총 11번의 쿼리 실행! (1 + N)
```

```
실행되는 SQL:
1. SELECT * FROM posts                         (게시글 목록: 1번)
2. SELECT * FROM users WHERE id = 1            (1번 글의 작성자)
3. SELECT * FROM users WHERE id = 2            (2번 글의 작성자)
4. SELECT * FROM users WHERE id = 3            (3번 글의 작성자)
...
11. SELECT * FROM users WHERE id = 10          (10번 글의 작성자)

게시글이 1000개라면? 1001번의 쿼리!
```

**해결 방법: Fetch Join**

```java
public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("SELECT p FROM Post p JOIN FETCH p.author")
    List<Post> findAllWithAuthor();
}
```

```
실행되는 SQL:
1. SELECT p.*, u.* FROM posts p JOIN users u ON p.author_id = u.id

단 1번의 쿼리로 해결!
```

**왜 이 문제가 발생하는가?**

LAZY 로딩은 "필요할 때 조회하겠다"는 전략이다. 하지만 **루프 안에서 하나씩 접근**하면, 결국 N번의 추가 쿼리가 발생한다. Fetch Join은 **"이번에는 한 번에 가져오겠다"**는 명시적 선언이다.

비유하면:
- **N+1 문제** = 편의점에서 물건을 하나씩 계산하는 것 (10번 줄 서기)
- **Fetch Join** = 물건을 바구니에 담아서 한 번에 계산하는 것 (1번 줄 서기)

#### 양방향 매핑의 주의점

```java
@Entity
public class User {
    @OneToMany(mappedBy = "author")  // 주인이 아님 (읽기만 가능)
    private List<Post> posts = new ArrayList<>();
}

@Entity
public class Post {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")  // 주인 (외래키 관리)
    private User author;
}
```

**양방향 매핑 시 흔한 실수:**

```java
// 실수: 주인이 아닌 쪽에만 값을 설정
user.getPosts().add(post); // 이것만으로는 DB에 반영되지 않음!

// 올바른 방법: 주인 쪽에 값을 설정
post.setAuthor(user); // 이것이 DB에 반영됨!

// 더 좋은 방법: 양쪽 모두 설정 (일관성을 위해)
post.setAuthor(user);
user.getPosts().add(post);
```

**실무 팁:** 양방향 매핑은 정말 필요할 때만 사용해야 한다. 대부분의 경우 `@ManyToOne` 단방향만으로 충분하다. 양방향은 복잡성을 증가시키고, 무한 루프(JSON 직렬화 시)나 성능 문제를 일으킬 수 있다.

---

### 4.5 트랜잭션과 ACID: @Transactional이 실제로 보장하는 것

> "이 책은 Service마다 `@Transactional`을 붙여왔다. 그런데 트랜잭션이 **정확히 무엇을 보장하는가?** '함께 실행된다' 정도로 얼버무리면 안 된다. 데이터베이스 이론에는 네 글자로 된 정확한 답이 있다: **ACID**."

**트랜잭션(transaction)**이란 **"전부 성공하거나, 전부 실패하거나" 하는 작업의 묶음**이다. 계좌 이체를 생각하자: A에서 5만원을 빼고, B에 5만원을 더한다. 만약 빼기만 하고 더하기 전에 서버가 죽으면? 돈이 증발한다. 트랜잭션은 이걸 막는다. 두 작업을 하나로 묶어 **둘 다 되거나 둘 다 안 되게** 한다.

데이터베이스가 트랜잭션에게 보장하는 네 가지 성질이 **ACID**다.

```
A - Atomicity  (원자성)  : 전부 성공 or 전부 실패. 중간은 없다.
C - Consistency(일관성)  : 트랜잭션 전후로 규칙(제약조건)이 항상 지켜진다.
I - Isolation  (격리성)  : 동시에 도는 트랜잭션끼리 서로 간섭하지 않는다.
D - Durability (지속성)  : 커밋된 데이터는 서버가 죽어도 살아남는다.
```

각각을 이 프로젝트의 `@Transactional`과 연결하면:

- **원자성(Atomicity)**: `@Transactional` 메서드 안에서 `save()`를 세 번 하다가 두 번째에서 예외가 나면? Spring 프록시(§2.6)가 **롤백**을 호출해 첫 번째 `save()`까지 전부 취소한다. "홍길동은 저장됐는데 그의 게시글은 안 저장된" 어중간한 상태가 생기지 않는다.
- **일관성(Consistency)**: `@Column(unique = true)`, 외래키, `NOT NULL` 같은 **제약조건**이 트랜잭션이 끝난 뒤에도 반드시 지켜진다. 규칙을 깨는 커밋은 DB가 거부한다.
- **격리성(Isolation)**: 두 요청이 같은 행을 동시에 수정하려 할 때, 서로의 절반쯤 진행된 상태가 보이지 않게 한다. 이게 다음 절의 주제다.
- **지속성(Durability)**: 커밋이 성공했다고 응답했으면, 그 직후 정전이 나도 데이터는 디스크에 남아 있다. DB가 **트랜잭션 로그(WAL)**에 먼저 기록한 뒤 커밋을 확정하기 때문이다.

**즉 `@Transactional`은 "이 메서드 전체를 하나의 ACID 트랜잭션으로 실행하라"는 선언**이다. §2.6에서 봤듯 그 실행 주체는 프록시이고, 이제 그 프록시가 여닫는 것이 **ACID를 보장하는 트랜잭션 경계**라는 것까지 이해가 연결된다.

---

### 4.6 격리 수준(Isolation Level)과 이상 현상

ACID의 I, 격리성은 **"완벽히 격리"와 "빠른 동시성" 사이의 트레이드오프**다. 완벽하게 격리하면(트랜잭션을 한 줄로 세우면) 느리다. 그래서 DB는 **격리 수준(isolation level)**을 여러 단계로 제공해, "이 정도 이상 현상은 감수하고 속도를 얻겠다"를 고를 수 있게 한다.

격리가 약할 때 생기는 **세 가지 이상 현상(anomaly)**부터 알아야 한다.

- **Dirty Read (더티 리드)**: 다른 트랜잭션이 **아직 커밋 안 한** 값을 읽어버린다. 그런데 그쪽이 롤백하면? 존재한 적 없는 유령 데이터를 읽은 셈이다.
- **Non-repeatable Read (반복 불가능한 읽기)**: 같은 행을 두 번 읽었는데, 그 사이 다른 트랜잭션이 **수정+커밋**해서 값이 달라진다.
- **Phantom Read (팬텀 리드)**: 같은 조건(`WHERE age > 20`)으로 두 번 조회했는데, 그 사이 다른 트랜잭션이 **새 행을 INSERT**해서 결과 행 개수가 달라진다. (유령처럼 없던 행이 나타난다.)

격리 수준은 이 이상 현상을 **아래로 갈수록 더 많이 막는다**(대신 동시성은 떨어진다).

```
격리 수준             │ Dirty Read │ Non-repeatable │ Phantom
─────────────────────┼────────────┼────────────────┼──────────
READ UNCOMMITTED     │   발생 O    │     발생 O      │  발생 O    ← 가장 약함/빠름
READ COMMITTED       │   막음 X    │     발생 O      │  발생 O    ← PostgreSQL 기본
REPEATABLE READ      │   막음 X    │     막음 X      │  발생 O*   ← MySQL(InnoDB) 기본
SERIALIZABLE         │   막음 X    │     막음 X      │  막음 X    ← 가장 강함/느림
```
(*PostgreSQL의 REPEATABLE READ는 MVCC 덕분에 팬텀까지 대부분 막는다.)

**PostgreSQL의 기본은 READ COMMITTED**다. 즉 "커밋된 값만 읽는다(더티 리드 없음)"까지는 공짜로 보장되지만, 그 이상은 개발자가 필요할 때 올려야 한다. 이 프로젝트도 별도 설정이 없으니 READ COMMITTED로 동작한다.

**MVCC(Multi-Version Concurrency Control) — PostgreSQL의 비결.** 어떻게 "읽는 트랜잭션"과 "쓰는 트랜잭션"이 서로 안 막고 돌 수 있을까? PostgreSQL은 데이터를 덮어쓰지 않고 **새 버전을 추가**한다. 각 트랜잭션은 자기가 시작한 시점의 **스냅샷(버전)**을 본다. 그래서 A가 수정 중이어도 B는 이전 버전을 읽으며 대기하지 않는다 — 락(lock) 대신 버전으로 격리를 구현한 것이다. (이것이 대부분의 읽기에서 락 없이 높은 동시성이 나오는 이유다.)

Spring에서는 `@Transactional(isolation = Isolation.REPEATABLE_READ)`로 메서드별 격리 수준을 지정할 수 있다. 하지만 **대부분은 기본값으로 충분**하고, 필요할 때 "왜 올려야 하는지"를 아는 것이 핵심이다.

---

### 4.7 인덱스와 B-tree: WHERE email = ? 는 왜 빠른가

이 책은 `findByEmail`, `existsByEmail` 같은 쿼리를 당연하게 써왔다. 100만 명의 회원 중에서 특정 이메일을 찾는데, DB는 어떻게 순식간에 찾아낼까? 100만 행을 처음부터 하나씩 비교(**full scan, O(n)**)하면 느릴 텐데?

답은 **인덱스(index)**다. 인덱스는 **책 뒤의 '찾아보기'**와 같다. "트랜잭션"이란 단어를 찾을 때, 500페이지를 처음부터 넘기지 않고 색인에서 "트랜잭션 → 412p"를 보고 바로 간다.

DB 인덱스의 자료구조는 대부분 **B-tree(정확히는 B+tree)**다. B-tree는 **정렬된 값을 계층적으로 저장한 균형 트리**다.

```
                    ┌──────────┐
                    │  M       │          ← 루트: 큰 범위로 분기
                    └────┬─────┘
              ┌──────────┴──────────┐
        ┌─────▼────┐          ┌─────▼────┐
        │  D    H  │          │  R    W  │   ← 중간: 더 좁은 범위
        └──┬──┬──┬─┘          └──┬──┬──┬─┘
      ...  ▼  ▼  ▼  ...          ▼  ▼  ▼
        [실제 행 위치를 가리키는 리프 노드들(정렬됨)]
```

`WHERE email = 'kim@x.com'`을 찾을 때, 루트에서 "kim은 M보다 앞" → 왼쪽, 그 아래에서 다시 범위 비교... 이렇게 **한 단계 내려갈 때마다 후보가 몇 배씩 줄어든다.** 100만 건이라도 트리 높이는 3~4단계면 충분하다. 그래서 조회 비용이 **O(log n)** 이다. full scan의 O(n)과 비교하면 100만 건에서 20번 vs 100만 번 — 5만 배 차이다.

**공짜는 아니다 (쓰기 비용 트레이드오프).** 인덱스는 "정렬된 색인"이므로, 행을 INSERT/UPDATE/DELETE할 때마다 **색인도 함께 갱신**해야 한다. 즉 인덱스는 **읽기를 빠르게 하는 대신 쓰기를 느리게** 한다. 그래서 "모든 컬럼에 인덱스를 걸면 좋다"가 아니라, **자주 조회 조건에 쓰는 컬럼에만** 신중히 건다.

**JPA와의 연결:** `@Column(unique = true)`를 붙이면(이 프로젝트의 이메일 필드처럼), DB는 유일성을 **빠르게 검사하기 위해 자동으로 유니크 인덱스를 만든다.** 그래서 `existsByEmail`이 빠른 것은 우연이 아니라, `unique = true`가 인덱스를 깔아준 결과다. 명시적으로는 `@Table(indexes = @Index(columnList = "email"))`로도 인덱스를 선언할 수 있다.

---

### 4.8 커넥션 풀과 ORM 임피던스 불일치

**커넥션 풀 (§2.7의 재확인).** JPA가 실행하는 모든 쿼리는 결국 DB 커넥션 위에서 돈다. §2.7에서 봤듯, 커넥션은 만들 때마다 비싸므로 **HikariCP**가 미리 만들어 둔 커넥션을 빌려주고 반납받는다. `@Transactional` 메서드는 시작할 때 커넥션 하나를 **빌려서** 트랜잭션 내내 붙잡고, 커밋/롤백 후 반납한다. 그래서 트랜잭션이 길어질수록 커넥션을 오래 점유하고, 풀이 유한하므로 동시 처리량이 떨어진다. **JPA 성능 문제의 상당수는 "쿼리가 느려서"가 아니라 "커넥션을 너무 오래 붙잡아서"**다.

**ORM 임피던스 불일치(impedance mismatch).** 마지막으로 근본적인 이야기 하나. 자바 세계는 **객체 그래프**로 생각한다 — `Post`가 `author`를 참조하고, `author`가 다시 `posts` 리스트를 참조하는 **그물망**. 반면 관계형 DB는 **평평한 테이블과 외래키(숫자 ID)**로만 표현한다 — 참조 대신 `author_id = 5` 같은 값. 이 두 모델의 근본적 차이(객체의 '참조' vs 테이블의 '외래키 값', 객체의 '상속' vs 테이블엔 상속 개념 없음, 객체의 '컬렉션' vs 테이블의 'JOIN')를 **임피던스 불일치**라고 부른다. **JPA/Hibernate의 존재 이유가 바로 이 불일치를 자동으로 메꾸는 것**이다. `post.getAuthor().getName()`(객체 참조)을 JOIN 쿼리로 번역해주는 것 — 그것이 ORM이 하는 일의 본질이다. 그래서 N+1(§4.4)이나 지연 로딩 같은 문제가 생기는 것도, 이 두 세계가 원래 다르기 때문에 번역 과정에서 새는 틈인 셈이다.

---

## Chapter 5: 보안 - Spring Security

> "보안은 옵션이 아니다. 보안 없는 웹 애플리케이션은 문 없는 집과 같다."

---

### 5.1 웹 보안의 기본 개념

#### 인증(Authentication): "너 누구야?"

인증은 **사용자가 누구인지 확인**하는 과정이다.

```
로그인 요청:
POST /api/auth/login
{
    "email": "user@example.com",
    "password": "mypassword123"
}

서버: "email이 일치하고, password도 맞네? OK, 너는 '김철수'야."
→ 인증 성공: 토큰 발급
```

#### 인가(Authorization): "너 이거 할 수 있어?"

인가는 **인증된 사용자가 특정 작업을 할 권한이 있는지 확인**하는 과정이다.

```
관리자 API 접근:
GET /api/admin/users
Authorization: Bearer eyJhb...

서버: "토큰 확인... '김철수'네. 근데 김철수는 ROLE_USER잖아. 관리자 API는 못 써."
→ 인가 실패: 403 Forbidden
```

#### 비유: "공항"

```
┌─────────────────────────────────────────────────────────┐
│  공항 보안 과정                                          │
│                                                         │
│  1. 여권 검사 (인증 - Authentication)                    │
│     "여권 보여주세요" → "김철수씨군요, 확인했습니다"        │
│                                                         │
│  2. 탑승권 확인 (인가 - Authorization)                   │
│     "탑승권 보여주세요" → "비즈니스석 탑승권이시네요"       │
│     "라운지에 입장하실 수 있습니다"                       │
│     or                                                  │
│     "이코노미석 탑승권이시네요"                           │
│     "라운지 입장이 불가합니다"                            │
│                                                         │
│  인증 없이 인가 불가 (여권 없이 탑승 불가)                 │
│  인증 성공해도 인가 실패 가능 (여권 있어도 라운지 못 갈 수 있음) │
└─────────────────────────────────────────────────────────┘
```

---

### 5.2 왜 비밀번호를 암호화해야 하는가?

#### 평문 저장의 위험

비밀번호를 그대로 DB에 저장한다면 어떻게 되는가?

```
users 테이블:
┌────┬──────────────────────┬──────────────┐
│ id │ email                │ password     │
├────┼──────────────────────┼──────────────┤
│ 1  │ user1@example.com    │ password123  │  ← 그대로 보임!
│ 2  │ user2@example.com    │ qwerty456   │
│ 3  │ admin@example.com    │ admin!@#$   │
└────┴──────────────────────┴──────────────┘
```

**DB가 유출되면?** (SQL Injection, 내부자 유출, 백업 파일 유출 등)
- 모든 사용자의 비밀번호가 즉시 노출
- 많은 사람들이 여러 사이트에서 같은 비밀번호를 사용 → 다른 사이트도 위험
- 뉴스에 나오는 "개인정보 유출" 사건의 대부분

#### 해시 함수: 단방향 변환

**해시 함수**는 입력값을 **되돌릴 수 없는 고정 길이 문자열**로 변환한다.

```
입력: "password123"
         │
    [해시 함수]
         │
         ↓
출력: "$2a$10$N9qo8uLOickgx2ZMRZoMye..."

특징:
- 같은 입력 → 항상 같은 출력 (결정적)
- 출력에서 입력을 역산할 수 없음 (단방향)
- 입력이 조금만 달라도 출력이 완전히 다름 (눈사태 효과)

"password123" → "$2a$10$N9qo8uLO..."
"password124" → "$2a$10$xK3mPQRv..."  (완전히 다른 결과!)
```

로그인 시 비밀번호 검증:
```
사용자 입력: "password123"
         │
    [같은 해시 함수]
         │
         ↓
해시 결과: "$2a$10$N9qo8uLO..."
         │
         ↓
DB에 저장된 해시와 비교: "$2a$10$N9qo8uLO..." == "$2a$10$N9qo8uLO..."
→ 일치! 로그인 성공
```

#### BCrypt: 왜 MD5, SHA가 아닌가?

MD5, SHA-256 같은 해시 함수는 **빠르다**. 이것이 문제이다.

```
해시 속도 비교:
MD5:     1초에 수십억 번 해시 가능
SHA-256: 1초에 수억 번 해시 가능
BCrypt:  1초에 수천 번만 해시 가능

해커의 무차별 대입 공격(Brute Force):
MD5:     모든 8자리 비밀번호를 몇 시간 만에 시도 가능
BCrypt:  모든 8자리 비밀번호를 시도하려면 수백 년
```

**왜 느린 것이 안전한가?**

정상적인 로그인은 1초에 1번이다. 해시가 0.001초든 0.1초든 사용자는 차이를 모른다. 하지만 해커는 1초에 수억 번을 시도해야 한다. BCrypt가 느리면 해커의 공격도 수억 배 느려진다.

비유하면:
- **MD5** = 일반 자물쇠 (열쇠공이 몇 초면 땀)
- **BCrypt** = 금고 잠금장치 (비밀번호 조합이 너무 많아 수백 년 걸림)

#### 레인보우 테이블 공격과 솔트(Salt)

**레인보우 테이블:** 자주 사용되는 비밀번호의 해시값을 미리 계산한 표

```
레인보우 테이블:
┌──────────────┬────────────────────┐
│ 비밀번호      │ MD5 해시            │
├──────────────┼────────────────────┤
│ password     │ 5f4dcc3b5aa765d6... │
│ 123456       │ e10adc3949ba59ab... │
│ qwerty       │ d8578edf8458ce06... │
│ ...          │ ...                │
└──────────────┴────────────────────┘

해커: "DB에서 가져온 해시값이 'e10adc3949ba...'네?
       테이블에서 찾아보면... 아, 비밀번호가 '123456'이구나!"
```

**솔트(Salt):** 비밀번호에 **랜덤 문자열**을 추가하여 같은 비밀번호도 다른 해시값을 만듦

```
BCrypt 솔트:
"password123" + "abc..." → "$2a$10$abc...XXXYYY..."
"password123" + "def..." → "$2a$10$def...ZZZWWW..."

같은 비밀번호인데 해시값이 다름!
→ 레인보우 테이블 무력화
```

BCrypt는 **솔트를 자동으로 생성하고 해시에 포함**시킨다. 개발자가 별도로 관리할 필요가 없다.

```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(); // 자동으로 솔트 생성 + 느린 해시
}

// 사용
String encoded = passwordEncoder.encode("password123");
// → "$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy"

boolean matches = passwordEncoder.matches("password123", encoded);
// → true
```

---

### 5.3 세션 vs 토큰 (JWT)

HTTP는 **상태를 유지하지 않는(Stateless)** 프로토콜이다. 요청 1과 요청 2를 같은 사용자가 보냈는지 알 수 없다. 그래서 "로그인 상태"를 유지하는 방법이 필요하다.

#### 세션 방식: 서버가 상태를 기억

```
로그인:
1. 사용자: "이메일: user@test.com, 비번: 1234"
2. 서버: "확인! 세션 ID: abc123을 발급할게"
3. 서버: 메모리에 {abc123: {userId: 1, role: USER}} 저장
4. 사용자: 쿠키에 세션 ID 저장

이후 요청:
5. 사용자: "게시글 목록 보여줘 (쿠키: sessionId=abc123)"
6. 서버: "abc123 찾아보니... userId=1이네. OK, 보여줄게"
```

```
┌──────────┐        ┌────────────────────────────────┐
│ 클라이언트 │        │ 서버                            │
│          │        │                                │
│ Cookie:  │ ──→    │ 세션 저장소:                     │
│ JSESSIONID        │ ┌────────┬────────────────┐    │
│ = abc123 │        │ │ abc123 │ userId=1       │    │
│          │        │ │ def456 │ userId=2       │    │
│          │        │ │ ghi789 │ userId=3       │    │
│          │        │ └────────┴────────────────┘    │
└──────────┘        └────────────────────────────────┘
```

**비유: 놀이공원 손목 밴드**
> 입장할 때(로그인) 손목 밴드를 받는다. 놀이기구를 탈 때(요청)마다 밴드를 보여주면, 직원이 시스템에서 확인한다. 밴드에는 아무 정보도 없고, 시스템(서버)에 정보가 저장되어 있다.

**세션 방식의 문제:**
- **서버 확장 어려움**: 서버가 2대면? 서버 A에서 로그인했는데 서버 B로 요청이 가면 세션이 없음
- **메모리 사용**: 사용자가 많아지면 서버 메모리에 세션 데이터가 쌓임
- **서버 재시작 시 세션 소멸**: 세션이 메모리에만 있으면 재시작 시 모든 사용자가 로그아웃됨

#### JWT 방식: 토큰에 정보 포함

```
로그인:
1. 사용자: "이메일: user@test.com, 비번: 1234"
2. 서버: "확인! JWT 토큰을 발급할게"
3. 서버: 사용자 정보를 토큰에 넣고 서명 → JWT 생성
4. 서버: 메모리에 아무것도 저장하지 않음 (Stateless!)
5. 사용자: JWT를 저장 (보통 localStorage 또는 Cookie)
   > ⚠ localStorage에 저장하면 XSS 공격에 취약하다. httpOnly 쿠키에 저장하는 것이 더 안전하지만, 이 경우 CSRF 방어가 필요하다.

이후 요청:
6. 사용자: "게시글 목록 보여줘 (Authorization: Bearer eyJhb...)"
7. 서버: "토큰의 서명이 유효하네. 토큰 안에 userId=1이 있으니 OK"
```

**비유: 여권**
> 여권에는 이름, 국적, 사진 등의 정보가 직접 담겨있다. 공항 직원이 중앙 서버에 조회할 필요 없이, 여권 자체의 정보와 위변조 방지 장치(서명)를 확인하면 된다.

#### JWT 구조 (Header.Payload.Signature)

JWT는 **점(.)으로 구분된 3개의 Base64 인코딩 문자열**이다:

```
eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwicm9sZSI6IlVTRVIiLCJpYXQiOjE3MDk4ODg4ODgsImV4cCI6MTcwOTg5MjQ4OH0.ABC123signature
└──────── Header ────────┘└────────────────── Payload ──────────────────────────┘└──── Signature ────┘
```

```
Header (헤더):
{
  "alg": "HS256",     // 서명 알고리즘
  "typ": "JWT"        // 토큰 타입
}

Payload (내용):
{
  "sub": "1",         // subject (사용자 ID)
  "role": "USER",     // 역할
  "iat": 1709888888,  // 발급 시간 (issued at)
  "exp": 1709892488   // 만료 시간 (expiration)
}

Signature (서명):
HMACSHA256(
  base64(header) + "." + base64(payload),
  서버의_비밀키     // 서버만 알고 있는 키
)
```

```
JWT의 동작 원리:

┌──────────────────────────────────────────────────────┐
│  토큰 생성 (서버):                                     │
│  Header + Payload + 비밀키 → 서명(Signature) 생성      │
│  Header.Payload.Signature → JWT 완성                  │
│                                                      │
│  토큰 검증 (서버):                                     │
│  1. Header와 Payload로 서명을 다시 계산                 │
│  2. 받은 Signature와 비교                              │
│  3. 일치하면 → 변조되지 않음 → 유효!                    │
│  4. 불일치하면 → 누군가 변조함 → 거부!                   │
│                                                      │
│  ※ 비밀키를 모르면 유효한 서명을 만들 수 없음            │
└──────────────────────────────────────────────────────┘
```

**중요:** JWT의 Payload는 **암호화되지 않는다!** Base64 디코딩하면 누구나 내용을 볼 수 있다. 따라서 **비밀번호 같은 민감한 정보를 절대 넣으면 안 된다.** 서명은 "변조 방지"이지 "내용 숨김"이 아니다.

#### 왜 JWT가 유행하는가?

```
세션 방식의 서버 확장:
┌──────────┐        ┌──────────┐
│ 서버 A    │        │ 서버 B    │
│ 세션:abc ✓│        │ 세션:없음 ✗│
└──────────┘        └──────────┘
     ↑                    ↑
     └───── 로드밸런서 ─────┘
               ↑
           사용자 (abc 세션)
           → 서버 B로 가면 로그인 풀림!

JWT 방식의 서버 확장:
┌──────────┐        ┌──────────┐
│ 서버 A    │        │ 서버 B    │
│ 비밀키 ✓  │        │ 비밀키 ✓  │
│ (서명 검증)│        │ (서명 검증)│
└──────────┘        └──────────┘
     ↑                    ↑
     └───── 로드밸런서 ─────┘
               ↑
           사용자 (JWT 토큰)
           → 어느 서버로 가든 검증 가능!
```

JWT의 장점:
- **서버 확장성**: 서버가 상태를 저장하지 않으므로 어떤 서버든 검증 가능
- **MSA 환경**: 여러 마이크로서비스가 같은 비밀키로 토큰 검증
- **모바일 친화**: 쿠키 없이도 HTTP 헤더로 전송 가능

#### JWT의 단점

**1. 토큰 무효화 어려움**
세션은 서버에서 삭제하면 즉시 무효화된다. JWT는 만료 시간까지 유효하다. 사용자가 로그아웃해도 토큰이 살아있을 수 있다.

해결 방법: 블랙리스트(Redis에 무효화된 토큰 저장) 또는 짧은 만료 시간 + Refresh Token

**2. 토큰 크기**
세션 ID는 몇 바이트지만, JWT는 수백 바이트이다. 모든 요청에 포함되므로 네트워크 오버헤드가 있다.

**3. 보안 민감 정보를 담을 수 없음**
Payload는 누구나 볼 수 있다.

---

### 5.4 Spring Security Filter Chain

#### 필터 체인 개념

Spring Security는 **여러 개의 필터를 체인(사슬)처럼 연결**하여 요청을 처리한다.

```
HTTP 요청
    │
    ↓
┌─────────────────────────────────────────────────────┐
│  Spring Security Filter Chain                        │
│                                                     │
│  ① CorsFilter                                       │
│     "이 요청이 허용된 출처(Origin)에서 온 건가?"         │
│     │                                               │
│  ② CsrfFilter                                      │
│     "CSRF 토큰이 유효한가?" (API 서버에서는 보통 비활성화) │
│     │                                               │
│  ③ AuthenticationFilter (JWT Filter)                 │
│     "유효한 JWT 토큰이 있는가?"                        │
│     "있다면 → SecurityContext에 인증 정보 저장"         │
│     "없다면 → 미인증 상태로 통과"                       │
│     │                                               │
│  ④ AuthorizationFilter                              │
│     "이 URL에 접근할 권한이 있는가?"                    │
│     "있다면 → Controller로 전달"                      │
│     "없다면 → 403 Forbidden"                         │
│                                                     │
└─────────────────────────────────────────────────────┘
    │
    ↓
Controller (비즈니스 로직)
```

#### 비유: "공항 보안 검색대"

```
┌─────────────────────────────────────────────────────┐
│  공항 보안 과정 (Security Filter Chain)               │
│                                                     │
│  ① 출발 국가 확인 (CORS Filter)                      │
│     "이 사람이 허용된 나라에서 왔는가?"                  │
│                                                     │
│  ② 여권 검사 (Authentication Filter)                 │
│     "유효한 여권(토큰)을 가지고 있는가?"                │
│                                                     │
│  ③ 짐 검사 (CSRF, Input Validation)                 │
│     "위험한 물건은 없는가?"                            │
│                                                     │
│  ④ 탑승권 확인 (Authorization Filter)                │
│     "이 비행기(API)에 탈 권한이 있는가?"                │
│                                                     │
│  → 모든 검사 통과 → 비행기 탑승 (Controller 도달)      │
│  → 하나라도 실패 → 탑승 거부 (401/403 응답)            │
└─────────────────────────────────────────────────────┘
```

#### SecurityFilterChain 설정의 의미

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
            // CSRF 비활성화 (JWT 기반 Stateless API이므로)
            .csrf(csrf -> csrf.disable())

            // 세션 사용 안 함 (JWT 사용)
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // URL별 접근 권한 설정
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()      // 인증 없이 접근 가능
                .requestMatchers("/api/admin/**").hasRole("ADMIN") // ADMIN만 접근 가능
                .anyRequest().authenticated()                     // 나머지는 인증 필요
            )

            // JWT 인증 필터 추가
            .addFilterBefore(jwtAuthFilter,
                UsernamePasswordAuthenticationFilter.class)

            .build();
    }
}
```

각 설정의 의미를 하나씩 보자:

**CSRF를 비활성화하는 이유:**
CSRF(Cross-Site Request Forgery)는 **쿠키 기반 세션**에서 발생하는 공격이다. JWT를 Authorization 헤더로 전송하고 세션/쿠키를 사용하지 않으면 CSRF 공격의 위험이 없으므로 비활성화한다. 단, JWT를 쿠키에 저장하는 경우에는 CSRF 방어가 여전히 필요하다.

**CORS가 필요한 이유:**
프론트엔드(localhost:3000)와 백엔드(localhost:8080)가 다른 포트(=다른 출처)에서 실행될 때, 브라우저는 **보안 정책(Same-Origin Policy)**으로 요청을 차단한다. CORS 설정으로 "이 출처에서 오는 요청은 허용해"라고 명시해야 한다.

```java
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
            .allowedOrigins("http://localhost:3000")   // 프론트엔드 주소
            .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH")
            .allowedHeaders("*")
            .allowCredentials(true);
    }
}
```

---

### 5.5 Role 기반 접근 제어 (RBAC)

#### 왜 역할이 필요한가?

모든 사용자가 같은 권한을 가진다면? 일반 사용자가 다른 사용자를 삭제할 수 있고, 시스템 설정을 바꿀 수 있다. 이것은 재앙이다.

```
역할(Role) 기반 접근 제어:

ROLE_USER (일반 사용자):
  ✓ 게시글 조회
  ✓ 자기 게시글 작성/수정/삭제
  ✗ 다른 사용자 게시글 삭제
  ✗ 사용자 관리
  ✗ 시스템 설정

ROLE_ADMIN (관리자):
  ✓ 게시글 조회
  ✓ 모든 게시글 작성/수정/삭제
  ✓ 사용자 관리
  ✓ 시스템 설정
```

#### URL 기반 접근 제어

```java
.authorizeHttpRequests(auth -> auth
    .requestMatchers("/api/auth/**").permitAll()       // 누구나
    .requestMatchers("/api/admin/**").hasRole("ADMIN") // 관리자만
    .requestMatchers(HttpMethod.GET, "/api/posts/**").permitAll()  // 조회는 누구나
    .requestMatchers(HttpMethod.POST, "/api/posts/**").authenticated() // 작성은 인증 필요
    .anyRequest().authenticated()                      // 기본: 인증 필요
)
```

#### 메서드 기반 접근 제어

```java
@Service
public class UserService {

    @PreAuthorize("hasRole('ADMIN')")  // ADMIN만 호출 가능
    public List<UserResponse> getAllUsers() { ... }

    @PreAuthorize("#id == authentication.principal.id or hasRole('ADMIN')")
    // 본인이거나 ADMIN만 호출 가능
    public UserDetailResponse getUserDetail(Long id) { ... }
}
```

#### Spring Security의 ROLE_ 접두어 규칙

Spring Security에서 역할(Role)은 내부적으로 `ROLE_` 접두어가 붙는다:

```java
// DB에 저장: "ADMIN"
// Spring Security 내부: "ROLE_ADMIN"

// hasRole("ADMIN") → ROLE_ADMIN 을 확인
// hasAuthority("ROLE_ADMIN") → ROLE_ADMIN 을 직접 확인 (같은 결과)
```

이것은 Spring Security의 관례(Convention)이다. `hasRole()`을 사용하면 자동으로 `ROLE_` 접두어를 붙여서 검사한다.

---

### 5.6 해싱 vs 암호화: 같은 게 아니다

> "§5.2에서 BCrypt로 비밀번호를 안전하게 저장하는 법을 배웠다. 하지만 한 번도 묻지 않았다: **BCrypt는 왜 복호화 함수가 없는가?** 이 질문에 답하면 암호학의 가장 중요한 갈림길이 보인다."

개발자가 뭉뚱그려 "암호화"라고 부르는 것은 사실 **성격이 완전히 다른 두 가지**다.

```
┌─────────────────────────┬──────────────────────────────┐
│ 해싱 (Hashing)          │ 암호화 (Encryption)          │
├─────────────────────────┼──────────────────────────────┤
│ 단방향 (one-way)        │ 양방향 (reversible)          │
│ 원문 → 해시 (되돌리기 X) │ 원문 ⇄ 암호문 (키로 되돌림)   │
│ 예: BCrypt, SHA-256     │ 예: AES                      │
│ 용도: 비밀번호          │ 용도: 나중에 원문이 필요한 데이터 │
│ (원문을 알 필요가 없음)  │ (주민번호, 카드번호 등)       │
└─────────────────────────┴──────────────────────────────┘
```

**해싱은 일부러 되돌릴 수 없게 만든 것**이다. 비밀번호는 시스템조차 원문을 알 필요가 없다 — 사용자가 로그인할 때 입력한 값을 **똑같이 해싱해서 저장된 해시와 비교**하면 되니까(§5.2). 그래서 BCrypt에는 복호화 함수가 아예 없다. DB가 통째로 유출돼도 해시만으로는 원문 비밀번호를 되돌릴 수 없다. **"되돌릴 수 없다"가 결함이 아니라 바로 그것이 목적**이다.

**암호화는 반대로 되돌릴 수 있어야 하는 것**이다. 예를 들어 주민등록번호는 나중에 화면에 다시 보여주거나 관공서에 제출해야 하므로, 원문을 복구할 수 있어야 한다. 이럴 때는 **AES** 같은 양방향 암호화를 쓴다. 대신 **키(key)**를 안전하게 보관하는 책임이 생긴다(키가 유출되면 전부 복호화되니까).

**한 문장 판단 기준:** "이 데이터의 원문을 나중에 다시 볼 일이 있는가?"

- 없다 (비밀번호, 인증) → **해싱** (BCrypt)
- 있다 (개인정보 저장) → **암호화** (AES)

이 프로젝트가 비밀번호에 BCrypt를 쓰는 것은, 비밀번호가 "다시 볼 필요 없는" 데이터이기 때문이다. 만약 여기에 AES를 썼다면, 그것은 **원문을 복구 가능하게 남겨둔 것 자체가 보안 사고**다.

---

### 5.7 HMAC과 대칭/비대칭: JWT 서명의 정체

> "§5.3에서 JWT를 '서명을 다시 계산해서 비교한다'고 설명했다. 이제 그 서명에 **이름**을 붙이고, MSA에서 왜 서명 방식을 바꿔야 하는지까지 밝힌다."

#### HMAC: 서버만이 만들 수 있는 도장

§5.3에서 JWT의 서명 검증을 "서버가 서명을 다시 계산해서 비교한다"고 했다. 그 계산의 정체는 **HMAC-SHA256**이다.

```
signature = HMAC-SHA256( secret , base64(header) + "." + base64(payload) )
```

HMAC은 **"비밀 키(secret)를 섞어서 만든 해시"**다. 핵심 성질은 이것이다: **secret을 아는 사람만이 올바른 서명을 만들 수 있다.** 공격자가 payload의 `role: USER`를 `role: ADMIN`으로 바꾸면, payload가 바뀌었으니 올바른 서명도 바뀌어야 한다. 하지만 공격자는 secret을 모르니 새 서명을 만들 수 없다. 서버는 받은 토큰의 header.payload로 **직접 HMAC을 다시 계산해서** 첨부된 서명과 비교한다 — 다르면 위조다.

이것이 JWT가 보장하는 두 가지다:
- **무결성(Integrity)**: 내용이 중간에 바뀌지 않았다.
- **인증성(Authenticity)**: 이 토큰은 secret을 가진 우리 서버가 발급한 것이 맞다.

(주의: HMAC은 **암호화가 아니다**. payload는 Base64로 인코딩됐을 뿐 누구나 디코딩해 읽을 수 있다. 그래서 JWT에 비밀번호 같은 민감 정보를 담으면 안 된다. HMAC이 막는 것은 '읽기'가 아니라 '위조'다.)

#### 대칭(HS256) vs 비대칭(RS256): 열쇠가 하나냐 둘이냐

HMAC 방식(HS256)은 **대칭키(symmetric)**다. **하나의 secret**으로 서명도 하고 검증도 한다.

```
대칭 (HS256):
  발급 서버 ── secret ──→ 서명
  검증 서버 ── secret ──→ 검증     ← 같은 secret이 양쪽에 필요
```

문제는 §5.3 마지막에서 언급한 **MSA(마이크로서비스)** 상황이다. 인증 서버가 발급한 토큰을 주문 서버, 결제 서버, 배송 서버가 각자 검증해야 한다. 대칭키라면 **모든 서버가 같은 secret을 가져야** 한다. 서버가 10개면 secret 사본이 10개 — 하나만 유출돼도 공격자가 **토큰을 위조**할 수 있다. 검증만 하면 되는 서버들이 위조 능력까지 갖게 되는 것이다.

여기서 **비대칭키(asymmetric, RS256)**가 빛난다. 열쇠가 **한 쌍**이다:

```
비대칭 (RS256):
  인증 서버 ── 개인키(private) ──→ 서명   ← 개인키는 인증 서버만 보관
  나머지 서버 ── 공개키(public) ──→ 검증  ← 공개키는 아무나 가져도 안전
```

**개인키(private key)로 서명하고, 공개키(public key)로 검증**한다. 공개키는 이름 그대로 공개돼도 된다 — 공개키로는 **검증만 가능하고 위조는 불가능**하기 때문이다. 그래서 주문/결제/배송 서버는 공개키만 나눠 가지면 되고, 그중 하나가 뚫려도 **개인키가 없으니 토큰을 위조할 수 없다.** 발급 능력(개인키)과 검증 능력(공개키)이 분리된 것 — 이것이 §5.3에서 말한 "여러 서비스가 같은 토큰을 검증"하는 MSA에서 비대칭이 정답인 이유다.

```
언제 무엇을?
  단일 서버(모놀리식)        → 대칭(HS256). 간단하고 빠르다.
  여러 서버가 검증(MSA)      → 비대칭(RS256). 검증 서버에 위조 능력을 주지 않는다.
```

#### 한 걸음 더: TLS/HTTPS도 같은 원리

같은 대칭/비대칭 아이디어가 **HTTPS**의 심장이다. 브라우저와 서버가 처음 연결할 때, **비대칭키로 안전하게 "세션 키"를 교환**한 뒤(핸드셰이크), 이후 실제 데이터는 빠른 **대칭키로 암호화**해서 주고받는다. 비대칭은 안전하지만 느리고, 대칭은 빠르지만 키 공유가 어렵다 — 그래서 **비대칭으로 대칭키를 안전하게 넘기고, 그다음은 대칭으로 달린다**는 두 방식의 장점을 조합한 것이다. (네트워크/TLS의 본격적인 이야기는 별도의 큰 주제이니 여기서는 "같은 원리가 여기서도 쓰인다"는 것만 짚고 넘어간다.)

---

## Chapter 6: 테스트 - 왜 테스트를 작성하는가?

> "테스트 없는 코드는 레거시다." - Michael Feathers

---

### 6.1 테스트의 가치

#### 왜 테스트를 작성하는가?

"테스트 짜는 시간에 기능 하나 더 만들겠다" - 많은 주니어 개발자가 이렇게 생각한다. 하지만 경험이 쌓이면 이 생각이 얼마나 위험한지 깨닫게 된다.

**1. 리팩토링의 안전망**
코드를 개선하고 싶은데, 고치면 다른 곳이 깨질까봐 무서워서 못 고치는 경험을 해본 적이 있는가? 테스트가 있으면 수정 후 테스트를 돌려서 **"기존 기능이 그대로 동작하는지"** 즉시 확인할 수 있다.

**2. 문서로서의 테스트**
잘 작성된 테스트는 **"이 코드가 어떻게 동작해야 하는지"** 보여주는 살아있는 문서이다.

```java
@Test
void 회원가입_시_비밀번호가_8자_미만이면_실패한다() {
    // 이 테스트 이름만 봐도 비즈니스 규칙을 알 수 있음
}

@Test
void 관리자가_아닌_사용자는_다른_사람의_게시글을_삭제할_수_없다() {
    // 이것도 마찬가지
}
```

**3. 빠른 피드백**
전체 애플리케이션을 실행하고, 브라우저에서 직접 테스트하고, DB를 확인하는 데 5분이 걸린다면? 단위 테스트는 **1초 만에** 같은 검증을 할 수 있다.

#### 비유: "건물의 안전 검사"

```
건물을 짓는 과정:

1. 기초 공사 → 구조 검사 (단위 테스트)
2. 골조 완성 → 하중 검사 (통합 테스트)
3. 내장 완성 → 종합 검사 (E2E 테스트)

"검사하는 시간에 빨리 다 짓자!" → 지진 나면 무너짐
"매 단계마다 검사하자!" → 안전한 건물

소프트웨어도 마찬가지:
"테스트 없이 빨리 개발하자!" → 장애 나면 무너짐
"테스트와 함께 개발하자!" → 안정적인 서비스
```

---

### 6.2 단위 테스트 vs 통합 테스트

#### 단위 테스트 (Unit Test)

**하나의 클래스(또는 메서드)를 고립시켜서 테스트**한다. 의존하는 객체는 가짜(Mock)로 대체한다.

```java
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;  // 가짜 Repository

    @Mock
    private PasswordEncoder passwordEncoder;  // 가짜 인코더

    @InjectMocks
    private UserService userService;  // 테스트 대상

    @Test
    void 회원가입_성공() {
        // Given: 준비
        CreateUserRequest request = new CreateUserRequest(
            "test@example.com", "password123", "테스트"
        );
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encoded_pw");
        when(userRepository.save(any(User.class))).thenReturn(
            new User("test@example.com", "encoded_pw", "테스트", Role.USER)
        );

        // When: 실행
        UserResponse result = userService.createUser(request);

        // Then: 검증
        assertThat(result.email()).isEqualTo("test@example.com");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void 이미_존재하는_이메일로_가입하면_예외() {
        // Given
        CreateUserRequest request = new CreateUserRequest(
            "existing@example.com", "password123", "테스트"
        );
        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> userService.createUser(request))
            .isInstanceOf(DuplicateEmailException.class);
    }
}
```

**특징:**
- 실행 속도: **매우 빠름** (DB, 네트워크 없음)
- 검증 범위: **작음** (하나의 클래스만)
- Spring 컨텍스트: **필요 없음**

#### 통합 테스트 (Integration Test)

**여러 계층이 함께 동작하는 것을 테스트**한다. 실제 DB를 사용한다.

```java
@SpringBootTest
@AutoConfigureMockMvc
@Transactional  // 테스트 후 DB 롤백
class UserIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void 회원가입_후_로그인_성공() throws Exception {
        // 1. 회원가입
        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "email": "test@example.com",
                        "password": "password123",
                        "name": "테스트"
                    }
                    """))
            .andExpect(status().isCreated());

        // 2. 로그인
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "email": "test@example.com",
                        "password": "password123"
                    }
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").exists());
    }
}
```

**특징:**
- 실행 속도: **느림** (Spring 컨텍스트 로딩, DB 접근)
- 검증 범위: **넓음** (Controller → Service → Repository → DB)
- Spring 컨텍스트: **필요**

#### 테스트 피라미드

```
          /\
         /  \          E2E 테스트 (적게)
        / E2E \        - 전체 시스템 테스트
       /──────\        - 가장 느리고 비쌈
      /        \
     / 통합 테스트 \     통합 테스트 (중간)
    / Integration \    - 여러 계층 함께 테스트
   /──────────────\    - 중간 속도
  /                \
 /   단위 테스트     \   단위 테스트 (많이)
/    Unit Tests     \  - 개별 클래스 테스트
/────────────────────\  - 가장 빠르고 저렴
```

**왜 단위 테스트가 가장 많아야 하는가?**
- 가장 빠르게 실행됨 → 자주 돌릴 수 있음
- 문제의 원인을 정확하게 짚어줌 → "UserService의 가격 계산 로직이 잘못됨"
- 작성과 유지보수가 쉬움

---

### 6.3 Mock: 왜 가짜 객체가 필요한가?

#### 문제 상황

`UserService`의 `createUser()` 메서드를 테스트하고 싶다고 하자. 이 메서드는:
1. `UserRepository`로 DB에서 이메일 중복 확인
2. `PasswordEncoder`로 비밀번호 암호화
3. `UserRepository`로 DB에 저장
4. `EmailService`로 환영 이메일 발송

진짜 객체를 사용하면?
- PostgreSQL이 실행 중이어야 함
- 이메일 서버가 동작해야 함 → 테스트할 때마다 진짜 이메일 발송?!
- DB 데이터가 테스트 간에 영향을 줌 → 불안정한 테스트

#### Mock으로 해결

```java
@Mock
private UserRepository userRepository;  // 가짜 Repository (DB 없이)

@Mock
private EmailService emailService;  // 가짜 이메일 서비스 (이메일 안 보냄)

// Mock의 동작 정의
when(userRepository.existsByEmail("test@test.com")).thenReturn(false);
// → "test@test.com이 있는지 물어보면, false를 반환해"

verify(emailService).sendWelcomeEmail(any());
// → "환영 이메일 발송 메서드가 호출되었는지 확인"
```

#### 비유: "영화 촬영의 스턴트맨"

```
실제 배우 (진짜 객체)           스턴트맨 (Mock 객체)
┌──────────────────┐          ┌──────────────────┐
│ 위험한 장면을      │          │ 위험한 장면을      │
│ 직접 촬영하면      │          │ 대역이 수행        │
│ 다칠 수 있음       │          │ 안전하게 촬영      │
│                  │          │                  │
│ 비용이 많이 듦     │          │ 비용 절감          │
│ 시간이 오래 걸림   │          │ 빠르게 진행        │
└──────────────────┘          └──────────────────┘

진짜 DB (진짜 객체)             Mock Repository (Mock 객체)
┌──────────────────┐          ┌──────────────────┐
│ DB 설치/실행 필요  │          │ DB 없이 테스트     │
│ 느림              │          │ 빠름              │
│ 데이터 정리 필요   │          │ 정리 불필요        │
│ 환경에 의존적      │          │ 어디서든 실행      │
└──────────────────┘          └──────────────────┘
```

---

### 6.4 Testcontainers: Docker를 활용한 진짜 테스트

#### H2 내장 DB의 한계

단위 테스트에서는 Mock으로 충분하지만, 통합 테스트에서는 **실제 DB와 비슷한 환경**이 필요하다. 많은 프로젝트가 H2 내장 DB를 사용하는데, 문제가 있다.

```
실제 운영: PostgreSQL
테스트:    H2 (내장 DB)

차이점:
- PostgreSQL의 JSONB 타입 → H2에 없음
- PostgreSQL의 전문 검색(Full-text search) → H2에 없음
- SQL 문법 차이 → H2에서 되는데 PostgreSQL에서 안 됨 (또는 그 반대)

결과: "테스트는 통과했는데 운영에서 에러!"
```

#### Testcontainers: 실제 DB를 Docker로

**Testcontainers**는 **테스트 실행 시 Docker 컨테이너를 자동으로 띄우고, 테스트가 끝나면 자동으로 정리**한다.

```java
@SpringBootTest
@Testcontainers
class UserRepositoryTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16")
        .withDatabaseName("testdb")
        .withUsername("test")
        .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Test
    void PostgreSQL_고유_기능_테스트() {
        // 진짜 PostgreSQL에서 테스트!
    }
}
```

```
테스트 실행 흐름:
1. Docker로 PostgreSQL 컨테이너 시작 (자동)
2. 테스트용 DB 생성 (자동)
3. Spring Boot 연결 설정 (자동)
4. 테스트 실행
5. 컨테이너 정리 및 삭제 (자동)
```

#### 비유: "모형 자동차 vs 실제 도로 테스트"

- **H2 테스트** = 모형 자동차로 미니어처 트랙에서 테스트 (한계 있음)
- **Testcontainers** = 실제 자동차로 실제 도로에서 테스트 (신뢰도 높음)
- **운영 환경** = 실제 도로에서 고객을 태우고 운행

---

### 6.5 Given-When-Then 패턴

테스트 코드도 **읽기 쉬워야** 한다. Given-When-Then 패턴은 테스트를 3단계로 구조화한다:

```java
@Test
void 쿠폰_적용_시_10퍼센트_할인() {
    // Given: 테스트 환경 준비 (이런 상황이 주어졌을 때)
    Product product = new Product("노트북", 1_000_000);
    Coupon coupon = Coupon.percentage(10); // 10% 할인 쿠폰

    // When: 테스트 대상 실행 (이것을 실행하면)
    int discountedPrice = product.applyDiscount(coupon);

    // Then: 결과 검증 (이런 결과가 나와야 한다)
    assertThat(discountedPrice).isEqualTo(900_000);
}
```

**왜 이 패턴이 좋은가?**

1. **일관성**: 모든 테스트가 같은 구조 → 읽기 쉬움
2. **명확성**: "무엇을 준비하고, 무엇을 실행하고, 무엇을 검증하는지" 한눈에 보임
3. **유지보수성**: 테스트가 실패하면 어느 단계에 문제가 있는지 빠르게 파악

비유하면 **과학 실험 보고서**와 같다:
- Given = 실험 조건 (온도 25도, 습도 60%)
- When = 실험 수행 (시약 A를 넣는다)
- Then = 예상 결과 (색이 파란색으로 변한다)

---

## Chapter 7: Docker - 왜 컨테이너인가?

> "내 컴퓨터에서는 되는데..." - 모든 개발자가 한 번쯤 해본 말

---

### 7.1 Docker 이전의 문제

#### "내 컴퓨터에서는 되는데..." 문제

```
개발자 A의 컴퓨터:              서버 (운영 환경):
┌──────────────────┐           ┌──────────────────┐
│ macOS            │           │ Ubuntu 22.04     │
│ Java 21          │           │ Java 17 (?!)     │
│ PostgreSQL 16    │           │ PostgreSQL 14    │
│ Node.js 20       │           │ Node.js 18       │
│ Python 3.12      │           │ Python 3.9       │
│                  │           │                  │
│ "내 컴퓨터에서는   │           │ "여기서는         │
│  잘 되는데..."    │           │  안 되는데..."     │
└──────────────────┘           └──────────────────┘
```

Java 21의 새 기능을 사용했는데 서버에는 Java 17이 설치되어 있다. PostgreSQL 16의 새 문법을 썼는데 서버에는 14가 있다. **환경이 다르면 같은 코드도 다르게 동작한다.**

#### 비유: "이사할 때 짐을 하나하나 옮기기 vs 컨테이너째 옮기기"

```
Docker 없이 배포 (짐을 하나하나 옮기기):
1. 서버에 Java 설치
2. 서버에 PostgreSQL 설치 (버전 맞춰서!)
3. 환경 변수 설정
4. 의존성 설치
5. 애플리케이션 배포
6. "어? 이 라이브러리 버전이 안 맞네..."
7. (삽질 3시간)

Docker로 배포 (컨테이너째 옮기기):
1. docker compose up
2. 끝!
```

실제 이사에 비유하면:
- **Docker 없이** = 가구를 분해해서 트럭에 싣고, 새 집에서 다시 조립. 나사가 없으면? 맞는 나사를 찾아야 함
- **Docker로** = 방째로 컨테이너에 넣어서 옮김. 컨테이너를 열면 그대로!

---

### 7.2 Docker의 핵심 개념

#### 이미지, 컨테이너, Dockerfile

```
Dockerfile (레시피)
   │
   │  docker build
   ↓
Image (이미지 = 설계도, Read-only)
   │
   │  docker run
   ↓
Container (컨테이너 = 실행 중인 인스턴스)
```

**비유: "붕어빵"**

```
Dockerfile = 붕어빵 레시피 (재료: 밀가루, 설탕, 팥...)
Image      = 붕어빵 틀 (틀 자체는 변하지 않음)
Container  = 실제 붕어빵 (틀에서 찍어낸 결과물)

하나의 틀(Image)에서 여러 개의 붕어빵(Container)을 찍어낼 수 있음!
```

#### Dockerfile 예시

```dockerfile
# Dockerfile: Spring Boot 애플리케이션 빌드 레시피
FROM eclipse-temurin:21-jdk AS builder    # 1. 기본 재료: Java 21
WORKDIR /app                              # 2. 작업 공간 설정
COPY . .                                  # 3. 소스코드 복사
RUN ./gradlew bootJar                     # 4. 빌드 (JAR 생성)

FROM eclipse-temurin:21-jre               # 5. 실행용 경량 이미지
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar  # 6. 빌드 결과물만 복사
ENTRYPOINT ["java", "-jar", "app.jar"]    # 7. 실행 명령
```

**각 줄의 의미:**
- `FROM`: 기반 이미지 (요리의 기본 재료)
- `WORKDIR`: 작업 디렉토리 (주방)
- `COPY`: 파일 복사 (재료를 주방에 가져다 놓기)
- `RUN`: 명령 실행 (요리하기)
- `ENTRYPOINT`: 컨테이너 시작 시 실행할 명령 (완성된 요리 서빙)

---

### 7.3 Docker Compose: 여러 컨테이너 오케스트레이션

#### 왜 Docker Compose가 필요한가?

실제 애플리케이션은 혼자 동작하지 않는다:

```
┌──────────┐    ┌──────────┐    ┌──────────┐
│  Spring   │ →  │PostgreSQL│    │  Redis   │
│  Boot     │ →  │ (DB)     │    │ (캐시)   │
│  (App)    │ →  │          │    │          │
└──────────┘    └──────────┘    └──────────┘
      ↑
      │
┌──────────┐
│  Nginx   │
│ (리버스  │
│  프록시)  │
└──────────┘
```

이 4개를 각각 `docker run`으로 실행하면? 네트워크 연결, 포트 매핑, 실행 순서, 환경 변수... 복잡해진다.

**Docker Compose는 이 모든 것을 하나의 파일로 정의한다.**

```yaml
# docker-compose.yml
services:
  app:                              # Spring Boot 애플리케이션
    build: .
    ports:
      - "8080:8080"
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://db:5432/mydb
      SPRING_DATASOURCE_USERNAME: user
      SPRING_DATASOURCE_PASSWORD: password
    depends_on:
      db:
        condition: service_healthy  # DB가 준비된 후 시작

  db:                               # PostgreSQL
    image: postgres:16
    environment:
      POSTGRES_DB: mydb
      POSTGRES_USER: user
      POSTGRES_PASSWORD: password
    volumes:
      - postgres_data:/var/lib/postgresql/data  # 데이터 영속화
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U user"]
      interval: 5s
      timeout: 5s
      retries: 5

  redis:                            # Redis 캐시
    image: redis:7-alpine
    ports:
      - "6379:6379"

volumes:
  postgres_data:                    # 볼륨 정의
```

```bash
# 이 한 줄로 전체 환경이 구성됨!
docker compose up -d
```

#### 서비스 간 통신

Docker Compose 안에서는 **서비스 이름이 곧 호스트명**이다:

```
app에서 DB 접근: jdbc:postgresql://db:5432/mydb
                                   ↑
                           서비스 이름 = 호스트명!

app에서 Redis 접근: redis://redis:6379
                          ↑
                   서비스 이름 = 호스트명!
```

localhost가 아니라 **서비스 이름**으로 접근한다. Docker가 내부 네트워크에서 이름을 자동으로 해석(DNS)해준다.

#### depends_on과 healthcheck

```yaml
depends_on:
  db:
    condition: service_healthy  # 단순히 시작만이 아니라, 건강한 상태까지 기다림
```

**왜 중요한가?**

PostgreSQL 컨테이너가 "시작"되는 것과 "쿼리를 받을 준비가 되는 것"은 다르다. 컨테이너는 시작했지만, PostgreSQL이 아직 초기화 중이면? Spring Boot가 DB 연결을 시도하다 실패한다.

`healthcheck`는 **"이 서비스가 진짜로 준비됐는지"** 확인하는 장치이다. `pg_isready`가 성공해야 비로소 app 서비스가 시작된다.

---

### 7.4 멀티 스테이지 빌드

#### 왜 빌드와 실행을 분리하는가?

```dockerfile
# 나쁜 예: 단일 스테이지
FROM eclipse-temurin:21-jdk
COPY . .
RUN ./gradlew bootJar
ENTRYPOINT ["java", "-jar", "build/libs/app.jar"]

# 문제: 이미지에 소스코드, Gradle, 빌드 도구, JDK가 모두 포함!
# 이미지 크기: ~800MB
```

```dockerfile
# 좋은 예: 멀티 스테이지
FROM eclipse-temurin:21-jdk AS builder      # 1단계: 빌드
COPY . .
RUN ./gradlew bootJar

FROM eclipse-temurin:21-jre                 # 2단계: 실행
COPY --from=builder /app/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]

# 결과: 이미지에는 JRE + JAR만 포함
# 이미지 크기: ~300MB
```

```
크기 비교:
┌────────────────────────────────────────────────┐
│ 단일 스테이지 (~800MB)                          │
│ ┌──────┐ ┌──────┐ ┌──────┐ ┌──────┐ ┌──────┐ │
│ │ JDK  │ │소스코드│ │Gradle│ │ 빌드  │ │ JAR  │ │
│ │300MB │ │ 50MB │ │200MB │ │200MB │ │ 50MB │ │
│ └──────┘ └──────┘ └──────┘ └──────┘ └──────┘ │
└────────────────────────────────────────────────┘

┌──────────────────────────┐
│ 멀티 스테이지 (~300MB)    │
│ ┌──────┐ ┌──────┐       │
│ │ JRE  │ │ JAR  │       │
│ │250MB │ │ 50MB │       │
│ └──────┘ └──────┘       │
└──────────────────────────┘
```

비유하면:
- **단일 스테이지** = 빵을 구운 후 오븐, 밀가루, 반죽 그릇까지 같이 배달
- **멀티 스테이지** = 빵만 포장해서 배달 (오븐은 빵집에 두고)

JDK(빌드 도구 포함)는 소스를 컴파일할 때만 필요하다. 실행할 때는 JRE(실행 환경)만 있으면 된다. 불필요한 것을 제거하면 이미지가 작아지고, 배포가 빨라지고, 보안 공격 표면(attack surface)도 줄어든다.

---

## Chapter 8: 전체 그림 - 모든 것이 어떻게 연결되는가?

> "나무를 보다가 숲을 잊지 말아야 한다. 이제 숲 전체를 조망해보자."

---

### 8.1 게시판 프로젝트로 보는 전체 아키텍처

지금까지 배운 모든 개념이 하나의 프로젝트에서 어떻게 연결되는지 보자.

#### 전체 아키텍처

```
┌──────────────────────────────────────────────────────────────┐
│  Docker Compose                                              │
│                                                              │
│  ┌──────────┐     ┌──────────────────────────────────────┐   │
│  │  Nginx   │     │  Spring Boot Application              │   │
│  │ (Port 80)│────→│                                      │   │
│  │          │     │  ┌────────────────────────────────┐  │   │
│  └──────────┘     │  │ Security Filter Chain           │  │   │
│                   │  │ (JWT 검증, 권한 확인)             │  │   │
│                   │  └──────────┬─────────────────────┘  │   │
│                   │             │                         │   │
│                   │  ┌──────────↓─────────────────────┐  │   │
│                   │  │ Controller (REST API)           │  │   │
│                   │  │ @RestController                 │  │   │
│                   │  └──────────┬─────────────────────┘  │   │
│                   │             │                         │   │
│                   │  ┌──────────↓─────────────────────┐  │   │
│                   │  │ Service (비즈니스 로직)           │  │   │
│                   │  │ @Service @Transactional          │  │   │
│                   │  └──────────┬─────────────────────┘  │   │
│                   │             │                         │   │
│                   │  ┌──────────↓─────────────────────┐  │   │
│                   │  │ Repository (데이터 접근)         │  │   │
│                   │  │ JpaRepository                   │  │   │
│                   │  └──────────┬─────────────────────┘  │   │
│                   │             │                         │   │
│                   └─────────────┼─────────────────────────┘   │
│                                 │                              │
│                   ┌─────────────↓──────────────────┐          │
│                   │  PostgreSQL (Port 5432)         │          │
│                   │  데이터 영구 저장                 │          │
│                   └────────────────────────────────┘          │
│                                                              │
└──────────────────────────────────────────────────────────────┘
```

#### 요청 하나의 여정: 회원가입부터 게시글 작성까지

**1단계: 회원가입**

```
클라이언트 → POST /api/auth/signup
{
    "email": "hong@example.com",
    "password": "secure123!",
    "name": "홍길동"
}

요청의 여정:
┌─────────────────────────────────────────────────────────┐
│                                                         │
│  [Nginx] 요청 수신, /api/** → Spring Boot으로 전달       │
│     │                                                   │
│  [Security Filter] /api/auth/** → permitAll → 통과      │
│     │                                                   │
│  [AuthController] @PostMapping("/api/auth/signup")       │
│     │  @Valid로 요청 검증 (이메일 형식, 비번 8자 이상)     │
│     │                                                   │
│  [AuthService] createUser()                             │
│     │  1. 이메일 중복 확인 → UserRepository.existsByEmail │
│     │  2. 비밀번호 암호화 → BCryptPasswordEncoder.encode  │
│     │  3. User 엔티티 생성                               │
│     │  4. DB에 저장 → UserRepository.save                │
│     │                                                   │
│  [UserRepository] JPA → INSERT INTO users ...            │
│     │                                                   │
│  [PostgreSQL] 데이터 저장 완료                            │
│                                                         │
│  응답: 201 Created                                      │
│  { "id": 1, "email": "hong@example.com", "name": "홍길동" } │
└─────────────────────────────────────────────────────────┘
```

**2단계: 로그인**

```
클라이언트 → POST /api/auth/login
{
    "email": "hong@example.com",
    "password": "secure123!"
}

요청의 여정:
┌─────────────────────────────────────────────────────────┐
│                                                         │
│  [AuthController] @PostMapping("/api/auth/login")        │
│     │                                                   │
│  [AuthService] login()                                  │
│     │  1. 이메일로 사용자 조회                             │
│     │  2. BCrypt로 비밀번호 검증                          │
│     │  3. JWT 토큰 생성 (userId, role, 만료시간 포함)     │
│     │                                                   │
│  응답: 200 OK                                           │
│  { "token": "eyJhbGciOiJIUzI1NiJ9..." }                │
└─────────────────────────────────────────────────────────┘
```

**3단계: 게시글 작성 (인증 필요)**

```
클라이언트 → POST /api/posts
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
{
    "title": "첫 번째 게시글",
    "content": "안녕하세요!"
}

요청의 여정:
┌─────────────────────────────────────────────────────────┐
│                                                         │
│  [JwtAuthFilter] Authorization 헤더에서 JWT 추출         │
│     │  1. 토큰 파싱 및 서명 검증                         │
│     │  2. 토큰에서 userId 추출                           │
│     │  3. SecurityContext에 인증 정보 저장               │
│     │                                                   │
│  [AuthorizationFilter] /api/posts → authenticated → OK  │
│     │                                                   │
│  [PostController] @PostMapping("/api/posts")             │
│     │  @Valid로 요청 검증                                │
│     │  @AuthenticationPrincipal로 현재 사용자 ID 추출     │
│     │                                                   │
│  [PostService] createPost()                             │
│     │  @Transactional                                   │
│     │  1. 작성자(User) 조회                              │
│     │  2. Post 엔티티 생성 (작성자 연관관계 설정)          │
│     │  3. DB에 저장                                     │
│     │                                                   │
│  [PostRepository] JPA → INSERT INTO posts ...            │
│     │                                                   │
│  [PostgreSQL] 데이터 저장 완료                            │
│                                                         │
│  응답: 201 Created                                      │
│  { "id": 1, "title": "첫 번째 게시글", "author": "홍길동" } │
└─────────────────────────────────────────────────────────┘
```

이것이 **지금까지 배운 모든 것의 조합**이다:
- **IoC/DI**: Controller → Service → Repository가 생성자 주입으로 연결
- **레이어드 아키텍처**: 각 계층이 자기 역할만 수행
- **DTO**: Entity가 아닌 Request/Response DTO로 통신
- **Validation**: @Valid로 입력 검증
- **JPA**: Entity → DB 자동 매핑, Repository의 쿼리 메서드
- **Spring Security**: JWT 인증, URL 기반 인가
- **Docker Compose**: 전체 환경을 하나의 명령으로 구성

---

### 8.2 실무에서의 Spring Boot

#### 추가로 배워야 할 것들

이 책에서 다룬 것은 **기초**이다. 실무에서는 더 많은 것을 다루게 된다:

```
현재 위치 (이 책):
┌─────────────────────────────────────────────────┐
│  Spring Boot + JPA + Security + Docker           │
│  ✓ REST API 개발                                 │
│  ✓ DB CRUD                                       │
│  ✓ JWT 인증                                      │
│  ✓ 기본 테스트                                    │
│  ✓ Docker 배포                                   │
└─────────────────────────────────────────────────┘

다음 단계:
┌─────────────────────────────────────────────────┐
│  캐싱                                            │
│  └ Redis: 자주 조회하는 데이터 캐싱 (성능 향상)    │
│                                                  │
│  메시지 큐                                       │
│  └ Kafka/RabbitMQ: 비동기 이벤트 처리             │
│    (이메일 발송, 알림, 로그 수집)                   │
│                                                  │
│  마이크로서비스 (MSA)                             │
│  └ 하나의 큰 서비스를 작은 서비스들로 분리          │
│  └ Spring Cloud, Service Discovery, API Gateway  │
│                                                  │
│  CI/CD                                           │
│  └ GitHub Actions: 코드 푸시 → 자동 테스트 → 배포  │
│  └ ArgoCD, Jenkins                               │
│                                                  │
│  모니터링                                        │
│  └ Prometheus + Grafana: 서버 상태 모니터링       │
│  └ ELK Stack: 로그 수집 및 분석                   │
│                                                  │
│  클라우드                                        │
│  └ AWS, GCP, Azure 배포                          │
│  └ Kubernetes (K8s): 컨테이너 오케스트레이션       │
└─────────────────────────────────────────────────┘
```

#### 학습 로드맵

```
Phase 1: 기초 (현재)
─────────────────────────────────────────────
Java 기초 → Spring Boot → JPA → Security → Docker
→ 게시판 프로젝트 완성

Phase 2: 심화
─────────────────────────────────────────────
Redis 캐싱 → 성능 최적화 (쿼리 튜닝, 인덱스 심화 — 기초는 §4.7)
→ 테스트 코드 강화 → CI/CD 구축
→ 실서비스 배포 경험

Phase 3: 아키텍처
─────────────────────────────────────────────
디자인 패턴 → DDD (도메인 주도 설계) → MSA
→ Kafka/RabbitMQ → Kubernetes
→ 대용량 트래픽 처리

Phase 4: 전문성
─────────────────────────────────────────────
특정 도메인 전문가 (결제, 검색, 추천, 데이터...)
→ 오픈소스 기여 → 기술 블로그 운영
→ 멘토링/강의
```

---

## 마무리: 좋은 개발자가 되려면

### 코드를 많이 읽어라

**좋은 글을 쓰려면 좋은 글을 많이 읽어야 하듯이, 좋은 코드를 짜려면 좋은 코드를 많이 읽어야 한다.**

추천하는 방법:
1. **GitHub에서 잘 만들어진 Spring Boot 프로젝트**를 찾아서 구조를 분석해야 한다
2. **Spring Framework 소스코드**를 읽어보아야 한다. `@Transactional`이 어떻게 동작하는지 내부를 들여다보면 이해가 깊어진다. (§2.6에서 이미 그 정체가 **프록시**임을 밝혔다 — 소스에서는 `TransactionInterceptor`가 그 프록시의 심장이다. 아는 만큼 보인다.)
3. **코드 리뷰**를 적극적으로 참여해야 한다. 다른 사람의 코드를 읽는 것만으로도 배울 점이 많다

### "왜?"를 계속 물어라

이 책에서 가장 강조한 것이다. **"어떻게"보다 "왜"가 중요하다.**

```
"어떻게" 아는 개발자:
  "생성자 주입을 사용해야 해" (외운 것)
  → 이유를 모르니, 상황에 따른 판단이 불가능

"왜"를 아는 개발자:
  "생성자 주입이 불변성을 보장하고, 테스트가 쉽고, 순환 참조를 즉시 발견하니까 사용해"
  → 이유를 아니, 예외 상황도 판단 가능
```

항상 이 질문을 던져야 한다:
- "왜 이 어노테이션을 붙이는 거지?"
- "왜 이 패턴을 사용하는 거지?"
- "이렇게 안 하면 어떤 문제가 생기지?"
- "이것의 대안은 뭐가 있지?"

### 완벽하지 않아도 괜찮다

처음부터 완벽한 코드를 짤 수 있는 사람은 없다. 주니어 시절에는 Controller에 모든 로직을 넣고, 필드 주입을 남발하고, 테스트를 작성하지 않는 것이 흔하다.

중요한 것은 **방향**이다. 매일 조금씩 더 나은 코드를 작성하려고 노력하면 된다.

```
좋은 개발자의 성장 과정:

1년차: "돌아가기만 하면 됐다!"
2년차: "코드를 좀 더 깔끔하게 짜야겠다"
3년차: "테스트도 짜야 하고, 구조도 잘 잡아야겠다"
5년차: "유지보수하기 좋고, 확장 가능한 설계를 해야겠다"
7년차: "팀 전체가 좋은 코드를 짤 수 있는 환경을 만들어야겠다"
```

### 추천 학습 리소스

**서적:**
- "토비의 스프링" (이일민) - Spring의 원리를 가장 깊이 설명하는 국내 최고의 Spring 서적
- "자바 ORM 표준 JPA 프로그래밍" (김영한) - JPA의 바이블
- "Clean Code" (Robert C. Martin) - 깨끗한 코드 작성법
- "객체지향의 사실과 오해" (조영호) - 객체지향을 제대로 이해하기 위한 책

**온라인:**
- Spring 공식 문서 (spring.io/docs) - 가장 정확한 정보의 원천
- Baeldung (baeldung.com) - Spring 관련 튜토리얼의 보고
- 인프런의 Spring 강의들 - 한국어로 된 양질의 강의

**실습:**
- 직접 프로젝트를 만들어보아야 한다. 게시판, To-Do 리스트, 간단한 쇼핑몰 등
- 코드를 작성한 후 **리팩토링**해보아야 한다. 처음에는 동작하게 만들고, 그 다음에 좋게 만든다
- 오픈소스 프로젝트에 기여해보아야 한다. 작은 버그 수정이나 문서 개선부터 시작하면 된다

---

> **"시작이 반이다. 하지만 나머지 반은 계속하는 것이다."**
>
> 이 책을 읽은 독자는 이미 시작한 것이다. 이제 계속해야 한다.
> 매일 조금씩, 꾸준히. 그것이 좋은 개발자가 되는 유일한 방법이다.
>
> 화이팅!

---

*이 개념서는 Spring Boot의 "왜?"를 이해하기 위해 작성되었다.*
*코드는 변하지만, 원리는 오래간다.*
