# Chapter 2: IoC/DI - Spring의 심장

> "Spring이 뭐 하는 프레임워크냐"고 물으면, 열에 아홉은 "IoC/DI 컨테이너"라고 답한다.
> 그런데 정작 "그게 왜 필요하냐"고 다시 물으면 대부분 말문이 막힌다.
> 이 장은 바로 그 두 번째 질문 —— **"왜 이렇게 만들어졌는가"** —— 에 답하는 장이다.

앞 장(Chapter 1)에서 우리는 "Spring이 해결하는 문제"를 이야기했다. 객체를 직접 `new`로 만들고, 그 객체가 또 다른 객체를 `new`로 만들고, 그렇게 얽힌 의존성 그물이 프로젝트를 어떻게 굳혀버리는지를 보았다.

이 장에서는 그 문제를 Spring이 **구체적으로 어떤 메커니즘으로** 푸는지를 파고든다. 그리고 그 메커니즘의 밑바닥에는 딱 두 개의 사상이 있다.

- **IoC(제어의 역전, Inversion of Control)** — "누가 객체를 만들고 연결할지"의 주도권을 개발자에게서 프레임워크로 넘긴다.
- **DI(의존성 주입, Dependency Injection)** — 그 주도권을 넘긴 결과, 객체는 자기가 쓸 부품을 "밖에서 받아" 쓴다.

여기까지는 어느 책에나 나온다. 하지만 이 장이 진짜로 다루려는 것은 그다음이다.

> `@Transactional`을 붙이면 왜 트랜잭션이 걸릴까? 애노테이션은 그냥 글자인데?
> 싱글톤 Bean 하나를 수백 명이 동시에 쓰는데, 왜 대부분은 멀쩡할까? 언제 터질까?

이 두 질문에 답하려면 **프록시(Proxy)**, **AOP**, **thread-per-request** 라는, 대부분의 입문서가 "나중에"라며 미뤄두는 개념까지 내려가야 한다. 그 밑바닥을 보고 나면, 여러분은 더 이상 애노테이션을 "마법"으로 여기지 않게 된다. 마법을 이해한 사람에게 그것은 더 이상 마법이 아니다.

자, 심장을 열어보자.

---

## 2.1 제어의 역전(IoC): 주도권을 누가 쥐는가

### 개념: "제어"란 무엇의 제어인가

프로그램을 짜다 보면 수많은 "결정"이 발생한다. 그중 이 장이 주목하는 결정은 딱 하나다.

> **"이 객체가 필요로 하는 다른 객체를, 누가 언제 만들어서 건네줄 것인가?"**

전통적인 방식에서는 이 결정을 **객체 자신**이 내린다. 필요한 게 있으면 자기가 직접 `new` 해서 만든다. 아래 코드를 보자.

```java
// 전통 방식: 제어권이 OrderService 자신에게 있다
public class OrderService {
    private OrderRepository orderRepository;
    private PaymentService paymentService;
    private EmailService emailService;

    public OrderService() {
        // 내가 필요한 부품을 내가 직접 만든다.
        // "언제, 어떤 구현체를" 만들지 전부 내가 결정한다.
        this.orderRepository = new JdbcOrderRepository();
        this.paymentService  = new KakaoPaymentService();
        this.emailService    = new SmtpEmailService();
    }

    public void placeOrder(Order order) {
        orderRepository.save(order);
        paymentService.pay(order);
        emailService.sendConfirmation(order);
    }
}
```

여기서 `OrderService`는 자기가 쓸 부품 세 개를 직접 만들었다. 겉보기엔 아주 자연스럽다. 하지만 이 "자연스러움"이 프로젝트를 굳게 만드는 시멘트다.

IoC(제어의 역전)는 이 결정권을 **거꾸로 뒤집는다**. 객체는 더 이상 자기 부품을 만들지 않는다. 대신 "나는 이러이러한 부품이 필요합니다"라고 **선언만** 하고, 실제로 만들어 건네주는 일은 외부(컨테이너)에게 맡긴다.

```java
// IoC 방식: 제어권이 외부(Spring 컨테이너)로 역전되었다
@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final PaymentService paymentService;
    private final EmailService emailService;

    // "이 세 개가 필요합니다"라고 선언만 한다.
    // 누가, 언제, 어떤 구현체를 만들어 넣어줄지는 나는 모른다. 관심도 없다.
    public OrderService(OrderRepository orderRepository,
                        PaymentService paymentService,
                        EmailService emailService) {
        this.orderRepository = orderRepository;
        this.paymentService  = paymentService;
        this.emailService    = emailService;
    }

    public void placeOrder(Order order) {
        orderRepository.save(order);
        paymentService.pay(order);
        emailService.sendConfirmation(order);
    }
}
```

두 코드의 `placeOrder`는 똑같다. 달라진 건 **생성자**뿐이다. 그런데 이 작은 차이가 프로젝트 전체의 유연성을 결정한다.

### 원리: "제어의 역전"이라는 이름이 가리키는 것

"역전(Inversion)"이라는 단어가 붙은 이유를 정확히 짚자. 무엇이 역전되었는가?

- **전통 방식의 흐름**: 내 코드가 → 라이브러리를 호출한다 (`new`, 메서드 호출). 흐름의 주도권이 내 코드에 있다.
- **IoC 방식의 흐름**: 프레임워크가 → 내 코드를 호출한다. 내 객체를 언제 만들지, 언제 메서드를 부를지를 프레임워크가 결정한다.

이걸 소프트웨어 공학에서는 **할리우드 원칙(Hollywood Principle)** 이라고 부른다.

> **"Don't call us, we'll call you."** (우리한테 전화하지 마세요. 우리가 당신에게 전화하겠습니다.)

오디션을 본 배우가 매일 감독에게 전화하는 게 아니라, 감독이 필요할 때 배우에게 전화한다. 이 관계의 방향이 IoC의 본질이다. **내가 프레임워크를 부리는 게 아니라, 프레임워크가 나를 부린다.**

### 비유: 직접 요리 vs 배달 음식

이해가 애매하다면 이 비유를 기억하자.

- **전통 방식 = 직접 요리**: 재료를 사고, 손질하고, 불을 켜고, 조리한다. 모든 과정을 내가 통제한다. 완전한 통제권을 갖는 대신, 모든 책임과 수고가 나에게 있다. 재료(구현체)를 바꾸려면 장 보는 것부터 다시 해야 한다.
- **IoC 방식 = 배달 음식**: "짜장면 하나요"라고 주문(선언)만 한다. 누가 요리했는지, 어떤 냄비를 썼는지 모른다. 알 필요도 없다. 완성된 결과물만 받는다. 가게(컨테이너)를 바꾸면 같은 주문으로 다른 맛을 받을 수 있다.

### 다이어그램: 전통 방식 vs IoC 컨테이너

```
[ 전통 방식 ] — 객체가 스스로 부품을 만든다
──────────────────────────────────────────────────────────

    ┌─────────────────────────────────────────┐
    │              OrderService               │
    │                                         │
    │   new JdbcOrderRepository()  ───────────┼──▶ 직접 생성
    │   new KakaoPaymentService()  ───────────┼──▶ 직접 생성
    │   new SmtpEmailService()     ───────────┼──▶ 직접 생성
    │                                         │
    └─────────────────────────────────────────┘
          ▲
          │ 제어권(무엇을 만들지 결정)이
          │ 객체 자신에게 있다


[ IoC 방식 ] — 컨테이너가 만들어서 "주입"한다
──────────────────────────────────────────────────────────

    ┌───────────────────── Spring 컨테이너 ─────────────────────┐
    │                                                          │
    │   ① 스캔: @Service, @Repository 붙은 클래스를 찾는다        │
    │   ② 생성: JdbcOrderRepository, KakaoPaymentService ...    │
    │   ③ 조립: OrderService 생성자에 위 객체들을 넣어준다         │
    │                                                          │
    │        만든다      만든다        만든다                     │
    │          │           │             │                     │
    │          ▼           ▼             ▼                     │
    │   [OrderRepo]  [PaymentSvc]   [EmailSvc]                 │
    │          └───────────┼─────────────┘                     │
    │                      │ 주입(inject)                       │
    │                      ▼                                    │
    │              ┌──────────────┐                            │
    │              │ OrderService │  ◀── 부품을 "받아서" 쓴다     │
    │              └──────────────┘                            │
    └──────────────────────────────────────────────────────────┘
          ▲
          │ 제어권이 컨테이너로 "역전"되었다
```

### 왜 IoC가 필요한가: 유연성이라는 대가

여기서 반드시 짚어야 할 질문. **"어차피 객체는 만들어져야 하는데, 누가 만들든 무슨 차이냐?"**

차이는 **변경이 닥쳤을 때** 드러난다. 시나리오를 보자.

프로젝트 초기, `OrderRepository`의 구현으로 `JdbcOrderRepository`를 쓰고 있었다. 이 리포지토리를 10개의 서비스가 각자 `new JdbcOrderRepository()`로 만들어 쓰고 있다. 그런데 팀이 JPA로 갈아타기로 했다. 이제 `JpaOrderRepository`로 바꿔야 한다.

```java
// 전통 방식이라면...
// 10개 파일을 전부 열어서
new JdbcOrderRepository()   →   new JpaOrderRepository()
// 로 일일이 바꿔야 한다. 하나라도 빠뜨리면? 컴파일은 되는데 런타임에 이상 동작.
```

반면 IoC 방식이라면, 서비스들은 전부 `OrderRepository`라는 **인터페이스**에만 의존하고 있고, 실제 구현체는 컨테이너가 주입한다. 그러면 바꿀 곳은 **설정 한 곳**뿐이다.

```java
// 예전엔 이걸 Bean으로 등록했다면
@Bean OrderRepository orderRepository() { return new JdbcOrderRepository(); }

// 이 한 줄만 바꾸면 10개 서비스가 자동으로 새 구현체를 받는다
@Bean OrderRepository orderRepository() { return new JpaOrderRepository(); }
```

(실무에서는 `@Repository`를 구현체에 붙여두면 이 한 줄조차 필요 없이 컨테이너가 알아서 골라준다.)

**핵심**: IoC의 목적은 "객체 생성을 대신 해주는 편리함"이 아니다. 진짜 목적은 **"변경의 파급 범위를 한 곳으로 격리하는 것"** 이다. 이걸 이해하지 못하면 IoC는 그냥 귀찮은 문법 규칙처럼 보인다.

---

## 2.2 의존성 주입(DI): 강한 결합을 끊는 기술

IoC가 "제어권을 넘긴다"는 **사상**이라면, DI(Dependency Injection)는 그 사상을 **구현하는 구체적 기법**이다. 정확히는, IoC를 실현하는 여러 방법 중 가장 대표적인 것이 DI다.

### 개념: 의존성이란 무엇인가

먼저 "의존성(Dependency)"이라는 단어를 정확히 하자.

> **의존성 = A가 B 없이는 제 역할을 못 하는 관계.** A는 B에 "의존한다".

- 자동차는 엔진에 의존한다. 엔진 없이 자동차는 굴러가지 못한다.
- 커피 머신은 전기에 의존한다. 전기 없이 커피 머신은 물도 못 끓인다.
- `OrderService`는 `OrderRepository`에 의존한다. 저장소 없이 주문을 저장할 수 없다.

의존성 자체는 나쁜 게 아니다. 오히려 필수다. 문제는 **"어떻게 의존하느냐"** 다. 여기서 강한 결합과 느슨한 결합이 갈린다.

### 원리: 왜 직접 `new` 하면 안 되는가 (강한 결합의 3대 죄악)

`OrderService`가 저장소를 이렇게 직접 만든다고 하자.

```java
public class UserService {
    private MySQLUserRepository repository = new MySQLUserRepository();
    //                                       ↑ 여기가 "강한 결합"의 발원지
}
```

이 한 줄이 초래하는 세 가지 문제를 보자.

**1) 변경이 어렵다.** `MySQLUserRepository`를 못처럼 박아버렸다. 나중에 PostgreSQL로 바꾸려면? `UserService` 소스를 열어서 고쳐야 한다. `UserService`는 "사용자 로직"을 담당하는 클래스인데, DB를 바꾼다고 이 클래스를 건드려야 한다는 게 이상하지 않은가? **관심사가 섞였다**는 신호다.

**2) 테스트가 어렵다.** `UserService`를 테스트하려면 진짜 `MySQLUserRepository`가 생성된다. 즉, **테스트를 돌리는데 실제 MySQL이 켜져 있어야 한다.** 단위 테스트 하나 돌리자고 DB를 띄우는 건 느리고, 불안정하고, CI 환경을 오염시킨다. 로직만 검증하고 싶은데 인프라가 발목을 잡는다.

**3) 확장이 어렵다.** 성능을 위해 캐시를 앞단에 두고 싶다. `CachedUserRepository`를 만들어서 원래 저장소 앞에 끼우고 싶다. 그런데 `new MySQLUserRepository()`로 박혀 있으니, 끼워 넣을 틈이 없다. 데코레이터 패턴이고 뭐고 적용할 자리가 없다.

이 세 가지 —— 변경/테스트/확장의 어려움 —— 은 모두 **"구체 클래스에 직접 의존했다"** 는 하나의 원인에서 나온다.

### 해결: 인터페이스에 의존하고, 구현체는 주입받는다

DI의 처방은 두 단계다.

1. **인터페이스에 의존하라.** `UserService`는 `MySQLUserRepository`(구체)가 아니라 `UserRepository`(추상)를 안다.
2. **구현체는 생성자로 받아라.** 어떤 구현체가 들어올지는 밖에서 결정한다.

```java
// 1) 추상(인터페이스)에 의존
public interface UserRepository {
    User findById(Long id);
    void save(User user);
}

// 2) 구현체는 여러 개일 수 있다
public class MySQLUserRepository    implements UserRepository { /* ... */ }
public class PostgresUserRepository implements UserRepository { /* ... */ }
public class CachedUserRepository   implements UserRepository { /* ... */ } // 확장!

// 3) 서비스는 인터페이스만 알고, 구현체는 "주입"받는다
@Service
public class UserService {
    private final UserRepository repository; // 구체가 아니라 추상!

    public UserService(UserRepository repository) { // 밖에서 넣어준다
        this.repository = repository;
    }
}
```

이제 앞의 세 문제가 어떻게 풀리는지 보자.

- **변경**: PostgreSQL로 바꾸려면 주입할 구현체만 바꾸면 된다. `UserService`는 손도 안 댄다.
- **테스트**: 테스트에서는 가짜(Mock) `UserRepository`를 만들어 주입한다. MySQL 없이도 로직만 검증할 수 있다.
- **확장**: `CachedUserRepository`를 만들어 주입하면 끝. 서비스 코드 변경 0줄.

### 다이어그램: 강한 결합 vs 느슨한 결합

```
[ 강한 결합 ] — 구체 클래스에 직접 의존 (용접된 상태)
────────────────────────────────────────────────────

    ┌──────────────┐        박아버림(new)
    │ UserService  │──────────────────────▶ [MySQLUserRepository]
    └──────────────┘
        ↑ 다른 구현체로 바꾸려면 UserService를 뜯어야 함


[ 느슨한 결합 ] — 인터페이스에 의존 (볼트로 체결)
────────────────────────────────────────────────────

    ┌──────────────┐   의존    ┌───────────────────┐
    │ UserService  │──────────▶│ «interface»       │
    └──────────────┘           │ UserRepository    │
                               └───────────────────┘
                                        △ 구현(implements)
                       ┌────────────────┼────────────────┐
                       │                │                │
              [MySQLUserRepo]  [PostgresUserRepo]  [CachedUserRepo]
                       ▲
              주입할 구현체만 갈아끼우면 끝 (UserService 무변경)
```

### 비유: 자동차 엔진 —— 용접 vs 엔진 마운트

DI를 자동차에 비유하면 감이 확실히 온다.

- **강한 결합 = 엔진을 차체에 용접한 상태.** 엔진이 고장 나거나 더 좋은 엔진으로 바꾸고 싶어도, 용접을 뜯어야 한다. 차체가 손상된다. 사실상 새 차를 만드는 것과 다름없다.
- **느슨한 결합 = 엔진 마운트 + 볼트로 체결한 상태.** 여기서 **엔진 마운트가 바로 인터페이스**다. 마운트 규격(인터페이스)만 맞으면 어떤 엔진(구현체)이든 볼트만 풀어 갈아 끼울 수 있다. 차체는 그대로다.

현실의 모든 자동차는 엔진을 용접하지 않는다. 볼트로 조인다. 정비와 교체를 위해서다. 좋은 소프트웨어도 마찬가지다. **미래의 교체를 위해 인터페이스라는 마운트를 둔다.** DI는 그 볼트를 조여주는 도구다.

---

## 2.3 왜 생성자 주입이 최선인가

의존성을 주입하는 방법은 문법적으로 세 가지가 있다. Spring은 셋 다 지원한다. 하지만 결론부터 말하면 —— **특별한 이유가 없는 한 언제나 생성자 주입을 써라.** 이 절은 그 "왜"를 끝까지 파고든다.

### 세 가지 주입 방법

```java
// (1) 필드 주입 (Field Injection) — 권장하지 않음
@Service
public class OrderService {
    @Autowired
    private OrderRepository repository; // 필드에 직접 @Autowired
}

// (2) 세터 주입 (Setter Injection) — 특수한 경우에만
@Service
public class OrderService {
    private OrderRepository repository;

    @Autowired
    public void setRepository(OrderRepository repository) {
        this.repository = repository;
    }
}

// (3) 생성자 주입 (Constructor Injection) — 최선, 기본값
@Service
public class OrderService {
    private final OrderRepository repository; // final 가능!

    // 생성자가 하나뿐이면 @Autowired 생략 가능 (Spring 4.3+)
    public OrderService(OrderRepository repository) {
        this.repository = repository;
    }
}
```

세 방법은 "의존성을 넣는다"는 결과는 같다. 하지만 그 과정과 보장이 전혀 다르다. 생성자 주입이 왜 최선인지, 네 가지 이유를 하나씩 보자.

### 이유 1: 불변성(Immutability) —— `final`을 쓸 수 있다

생성자 주입만이 필드를 `final`로 선언할 수 있다.

```java
private final OrderRepository repository; // 생성자에서 딱 한 번 대입, 이후 불변
```

`final`은 컴파일러가 강제하는 **계약**이다. "이 필드는 객체가 생성되는 순간 초기화되고, 그 이후로는 절대 바뀌지 않는다"는 보장이다. 서비스가 쓰는 저장소가 실행 도중에 슬그머니 바뀔 일은 없어야 한다. `final`은 그걸 코드로 못 박는다.

필드 주입이나 세터 주입은 `final`을 못 쓴다(필드가 생성 이후에 대입되므로). 즉, 이론상 누군가 나중에 그 필드를 바꿔치기할 수 있는 여지가 남는다.

### 이유 2: 필수 의존성을 강제한다 —— 컴파일 타임 vs 런타임

이게 아마 가장 중요한 차이다. 시나리오로 보자.

```java
// 생성자 주입: 의존성 없이는 객체를 만들 수조차 없다
new OrderService();
//  ↑ 컴파일 에러! "OrderRepository 인자가 필요합니다"
//    → 문제를 컴파일 시점에 잡는다. 절대 놓칠 수 없다.

// 필드 주입: 객체는 만들어지지만, repository는 null이다
OrderService s = new OrderService(); // 컴파일 성공 (!)
s.placeOrder(order); // 런타임에 NullPointerException 💥
//                     → 문제를 실행하고 나서야 발견한다.
```

생성자 주입은 **"이 객체는 이 부품 없이는 존재할 수 없다"** 를 타입 시스템으로 강제한다. 부품이 없으면 애초에 컴파일이 안 된다. 이건 엄청난 안전장치다. "혹시 의존성을 빠뜨렸나?"를 사람이 확인할 필요가 없다. 컴파일러가 대신 확인해준다.

### 이유 3: 테스트가 쉽다 —— Spring 없이도 만들 수 있다

생성자 주입을 쓰면, 테스트 코드에서 Spring 컨테이너를 띄우지 않고도 객체를 만들 수 있다.

```java
@Test
void 주문을_저장한다() {
    // Spring? 필요 없다. 그냥 순수 Java로 mock을 만들어 넣는다.
    OrderRepository mockRepo = Mockito.mock(OrderRepository.class);
    OrderService service = new OrderService(mockRepo); // 생성자로 주입!

    service.placeOrder(new Order());

    verify(mockRepo).save(any());
}
```

생성자에 인자로 넣기만 하면 되니, 테스트가 명료하고 빠르다. 반면 필드 주입은 `private` 필드라 밖에서 넣을 방법이 없다. 리플렉션을 동원하거나 `@SpringBootTest`로 컨테이너를 통째로 띄워야 한다. 단위 테스트가 무거워진다. **"테스트하기 어렵다"는 건 설계가 나쁘다는 신호**인데, 필드 주입은 그 나쁜 냄새를 숨겨버린다.

### 이유 4: 순환 참조를 시작 시점에 잡아낸다

A가 B를 필요로 하고, B가 다시 A를 필요로 하는 상황을 **순환 참조(circular dependency)** 라고 한다. 이건 대개 설계가 잘못됐다는 신호다.

```java
@Service class A { A(B b){...} }  // A는 B가 필요
@Service class B { B(A a){...} }  // B는 A가 필요
// 닭이 먼저냐 달걀이 먼저냐 — 누굴 먼저 만들지?
```

- **생성자 주입**: 컨테이너가 A를 만들려니 B가 필요하고, B를 만들려니 A가 필요하다. 만들 수가 없다. **애플리케이션 시작 시점에 즉시 에러**를 뿜고 죽는다. "순환 참조입니다"라고 딱 짚어준다. 개발자는 서버 켜자마자 알게 된다. → **빠른 발견.**
- **필드 주입**: 일단 두 객체를 껍데기만 만들어 놓고 나중에 필드를 채우는 방식이라, 시작은 성공한다. 그러다 실제로 서로를 호출하는 특정 상황에서 예상치 못한 동작이나 `NullPointerException`이 터진다. **운영 중에, 특정 조건에서만.** → **디버깅 지옥.**

한마디로:

> **필드 주입 = 시한폭탄** (언제 터질지 모른 채 배포됨)
> **생성자 주입 = 즉시 경보** (문제가 있으면 시작조차 안 됨)

문제는 일찍 발견될수록 싸다. 생성자 주입은 문제를 **가장 이른 시점(컴파일/부팅)** 으로 끌어당긴다.

### 다이어그램: 문제 발견 시점

```
             컴파일 타임        부팅 시점        런타임(운영)
                 │               │               │
 생성자 주입  ────┼───────────────┼───────────────┤
   ✔ 필수 누락   ●(컴파일 에러)                    
   ✔ 순환 참조                   ●(부팅 실패)      
                 │               │               │
 필드 주입   ────┼───────────────┼───────────────┤
   ✘ 필수 누락                                   ●(NPE) 💥
   ✘ 순환 참조                                   ●(간헐 오류) 💥
                 │               │               │
              ◀── 발견이 이를수록 고치기 싸다 ──
```

### 결론

특별한 이유(예: 선택적 의존성, 순환이 불가피한 레거시)가 아니라면 **항상 생성자 주입**이다. 그리고 Spring 4.3부터는 생성자가 하나뿐이면 `@Autowired`조차 생략할 수 있으니, 코드는 이렇게 깔끔해진다.

```java
@Service
public class OrderService {
    private final OrderRepository repository;
    private final PaymentService paymentService;

    // 생성자 1개 → @Autowired 없어도 Spring이 알아서 주입
    public OrderService(OrderRepository repository, PaymentService paymentService) {
        this.repository = repository;
        this.paymentService = paymentService;
    }
}
```

(Lombok의 `@RequiredArgsConstructor`를 쓰면 이 생성자마저 자동 생성된다. 다만 그 편리함 뒤에 위 네 가지 이유가 숨어 있음을 잊지 말자.)

---

## 2.4 Bean: Spring이 관리하는 객체

지금까지 "컨테이너가 객체를 만들어 주입한다"고 했다. 그 "컨테이너가 관리하는 객체"에게는 특별한 이름이 있다. **Bean**이다.

### 개념: 일반 객체 vs Bean

```
[ 일반 객체 ]                        [ Bean ]
─────────────────────────           ─────────────────────────
 내가 new로 만든다                    Spring이 만든다
 내가 참조를 들고 관리한다             Spring이 생명주기를 관리한다
 GC가 알아서 수거                     Spring이 의존성을 주입해준다
 예: new StringBuilder()             필요한 곳에 Spring이 전달한다
                                     예: @Service, @Repository ...
```

`@Service`, `@Repository`, `@Component` 등을 붙인 클래스는 컨테이너가 인스턴스를 만들어 관리한다. 이 인스턴스가 Bean이다. 개발자는 `new`를 쓰지 않는다. "필요하다"고 선언(생성자 매개변수 등)만 하면 컨테이너가 알아서 만들어 넣어준다.

### 비유: 호텔 객실 관리

- **컨테이너 = 호텔**: 모든 객실을 소유하고 관리한다.
- **Bean = 객실**: 호텔이 만들고, 청소하고, 관리한다.
- **개발자 = 투숙객**: 방을 직접 짓지 않는다. "방 하나 주세요"라고 요청(주입 선언)하면 호텔이 배정해준다.

투숙객이 자기 방을 직접 지으려 하지 않듯, 개발자도 Bean을 직접 `new` 하지 않는다.

### 싱글톤(Singleton): 기본 스코프

Spring Bean의 기본 스코프는 **싱글톤(singleton)** 이다. 이 말의 정확한 뜻:

> **Bean 정의(대개 클래스) 하나당, 컨테이너 안에 인스턴스가 딱 하나만 존재한다.**

즉 `OrderService`를 100군데에서 주입받아도, 그 100곳이 받는 건 **전부 같은 인스턴스**다. 여러 이름으로 부르지만 실체는 하나다.

### 왜 싱글톤이 기본인가

**1) 메모리 효율.** 웹 서버는 초당 수백~수천 개의 요청을 받는다. 만약 요청마다 `OrderService`를 새로 만든다면, 초당 1,000요청이면 초당 1,000개의 객체가 만들어졌다 버려진다. 엄청난 낭비다. 게다가 그 객체가 또 다른 객체 5개를 주입받는다면 초당 6,000개다. GC가 비명을 지른다. 싱글톤이면? 인스턴스 하나를 계속 재사용한다. 만들 때 딱 한 번만 만든다.

**2) 서비스는 대개 상태가 없다(stateless).** `OrderService.placeOrder(order)`를 보라. 이 메서드는 **인자로 받은 `order`** 만 가지고 일한다. 서비스 자신은 "특정 주문 정보"를 필드로 들고 있지 않는다. 그저 로직(behavior)의 묶음일 뿐이다. 상태가 없으니, 인스턴스가 하나든 백 개든 결과가 같다. 그러니 하나만 있으면 충분하다.

### ⚠️ 반드시 지켜야 할 규칙: 싱글톤 Bean에 상태를 저장하지 마라

싱글톤이 기본이라는 사실은, 아주 위험한 실수의 문을 연다.

```java
@Service
public class OrderService {
    private User currentUser; // ⚠️⚠️⚠️ 절대 하면 안 되는 것

    public void process(User user) {
        this.currentUser = user; // 필드에 상태 저장
        // ... currentUser로 작업 ...
    }
}
```

이 `OrderService`는 싱글톤 —— 즉 인스턴스가 하나 —— 다. 그런데 이 하나의 인스턴스를 **여러 요청(여러 스레드)이 동시에 공유**한다. `currentUser` 필드도 공유된다. 그러면 A 사용자의 요청이 `currentUser = 홍길동`을 넣은 직후, B 사용자의 요청이 `currentUser = 김철수`로 덮어쓸 수 있다. 그 결과 A는 자기 데이터가 아니라 B의 데이터를 처리하게 된다. **다른 사람의 정보가 내 화면에 보이는** 최악의 보안 사고다.

비유하자면, **호텔 로비의 공용 칠판에 개인 메모를 써두는 것**과 같다. 누구나 볼 수 있고, 누구나 지워버릴 수 있다. 개인 정보는 자기 방(지역 변수, 메서드 인자)에 두어야지, 공용 공간(싱글톤 필드)에 두면 안 된다.

> **원칙: 싱글톤 Bean은 stateless(무상태)로 유지하라. 요청별 데이터는 필드가 아니라 메서드 인자/지역 변수로 다뤄라.**

이 규칙이 "왜" 그런지의 정확한 메커니즘은 §2.7 "서버는 멀티스레드다"에서 끝까지 파헤친다. 지금은 규칙만 확실히 새겨두자.

### Bean 생명주기(Lifecycle)

Bean은 태어나서 죽을 때까지 정해진 단계를 거친다. 컨테이너가 이 과정을 전부 관리한다.

```
 ①생성          ②의존성 주입      ③초기화 콜백       ④사용        ⑤소멸 콜백
──────────▶──────────────▶──────────────▶─────────▶──────────────
 인스턴스화    생성자/필드로       @PostConstruct    비즈니스     @PreDestroy
 (new)         부품을 넣음        여기서 리소스      로직 수행    여기서 리소스
                                 초기화                          정리(close)
```

- **③ 초기화 콜백** (`@PostConstruct`): 의존성이 다 채워진 뒤, 본격 사용 전에 한 번 실행된다. 커넥션을 열거나, 캐시를 예열하거나, 설정을 검증하는 등 "준비 작업"을 여기서 한다.
- **⑤ 소멸 콜백** (`@PreDestroy`): 컨테이너가 종료될 때 실행된다. 열어둔 리소스(파일, 커넥션, 스레드풀)를 안전하게 닫는 "뒷정리"를 여기서 한다.

```java
@Service
public class ConnectionManager {
    private Connection connection;

    @PostConstruct
    public void init() {          // ③ 사용 전 준비
        this.connection = openConnection();
    }

    @PreDestroy
    public void cleanup() {        // ⑤ 종료 시 정리
        connection.close();
    }
}
```

일반 객체라면 이 준비/정리를 개발자가 직접 챙겨야 한다. Bean은 컨테이너가 정해진 시점에 자동으로 불러준다. 이것도 앞서 말한 할리우드 원칙("우리가 당신에게 전화하겠습니다")의 한 사례다.

---

## 2.5 Component Scan: Bean을 어떻게 찾아내는가

컨테이너가 Bean을 만든다는 건 알겠다. 그런데 **컨테이너는 "어떤 클래스가 Bean인지" 어떻게 알까?** 답은 **Component Scan(컴포넌트 스캔)** 이다.

### 개념: 애노테이션이라는 "표식"을 스캔한다

Spring은 애플리케이션 시작 시 지정된 패키지들을 훑으며(scan), 특정 애노테이션이 붙은 클래스를 찾아 Bean으로 등록한다. 그 애노테이션들은 다음과 같다.

```
@Component          — 가장 기본. "이건 Bean이다"라는 최소 표식
  ├─ @Service       — 비즈니스 로직 계층
  ├─ @Repository    — 데이터 접근 계층 (+ DB 예외 변환 부가기능)
  ├─ @Controller    — 웹 요청 처리 (뷰 반환)
  └─ @RestController — @Controller + @ResponseBody (JSON/데이터 반환)
```

`@Service`, `@Repository`, `@Controller`는 사실 **속을 보면 전부 `@Component`** 다. 즉 기능적으로는 "Bean으로 등록된다"는 점에서 거의 같다.

### 왜 굳이 나눠 놓았을까

기능이 거의 같은데 왜 이름을 넷으로 나눴을까? **의도(intention)를 코드로 표현하기 위해서**다.

비유하자면 **직원 유니폼**이다. 병원에서 흰 가운은 의사, 분홍 유니폼은 간호사, 파란 옷은 청소 담당이라고 색으로 역할을 구분한다. 다 같은 "직원"이지만, 옷 색만 봐도 무슨 일을 하는지 안다. 마찬가지로 `@Service`가 붙어 있으면 "아, 여긴 비즈니스 로직이구나", `@Repository`면 "여긴 DB를 다루는구나"를 한눈에 안다. 계층 구조가 코드에 드러난다.

게다가 몇몇은 **실제 부가 기능**도 다르다. 대표적으로 `@Repository`는 데이터 접근 중 발생하는 벤더별 DB 예외(JDBC `SQLException` 등)를 Spring의 통일된 `DataAccessException` 계층으로 **자동 변환**해준다. 그래서 서비스 계층은 "MySQL 예외냐 Oracle 예외냐"를 신경 쓸 필요 없이 Spring 예외 하나로 다룰 수 있다.

### 어디부터 스캔하는가: `@SpringBootApplication`의 위치

`@SpringBootApplication`이 붙은 메인 클래스는 그 안에 `@ComponentScan`을 품고 있다. **스캔은 이 메인 클래스가 위치한 패키지와 그 하위 패키지 전체**를 대상으로 한다.

```
com.edu.myapp                        ← 메인 클래스 위치 (스캔 기준점)
├─ MyAppApplication.java   @SpringBootApplication
├─ controller/             ← 스캔됨 ✔
├─ service/                ← 스캔됨 ✔
└─ repository/             ← 스캔됨 ✔
```

### 흔한 실수: 메인 클래스를 깊은 패키지에 둔다

```
com.edu.myapp
├─ controller/                       ← 스캔 안 됨 ✘ (기준점보다 위!)
├─ service/                          ← 스캔 안 됨 ✘
└─ config/
   └─ MyAppApplication.java  ← 여기 두면 config/ 하위만 스캔된다
```

메인 클래스를 `config`처럼 깊은 하위 패키지에 두면, 형제/상위 패키지의 Bean들이 스캔되지 않는다. 그러면 "분명히 `@Service`를 붙였는데 주입이 안 된다"는 미궁의 에러가 난다.

> **원칙: `@SpringBootApplication` 메인 클래스는 프로젝트의 최상위 패키지(루트)에 두어라.** 그래야 모든 하위 패키지가 자연스럽게 스캔 범위에 들어온다.

### `@Configuration` + `@Bean`: 수동 등록

컴포넌트 스캔은 "내가 만든 클래스에 애노테이션을 붙이는" 방식이다. 하지만 그럴 수 없는 경우가 있다. 이때는 `@Configuration` 클래스 안에서 `@Bean` 메서드로 **수동 등록**한다.

수동 등록이 필요한 경우는 크게 셋이다.

**1) 외부 라이브러리 클래스** —— 소스를 내가 소유하지 않아 `@Component`를 붙일 수 없다.

```java
@Configuration
public class AppConfig {

    // Jackson의 ObjectMapper는 남의 라이브러리 클래스라 @Component를 못 붙인다.
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return mapper; // 반환된 객체가 Bean으로 등록된다
    }
}
```

**2) 생성 로직이 복잡할 때** —— 단순 `new`가 아니라 여러 설정을 거쳐 만들어야 하는 객체.

```java
@Bean
public PasswordEncoder passwordEncoder() {
    // 인터페이스 PasswordEncoder의 구현체를 어떤 걸로, 어떤 강도로 쓸지 여기서 결정
    return new BCryptPasswordEncoder();
}
```

**3) 조건부/프로파일별 등록** —— 환경(개발/운영)에 따라 다른 Bean을 넣고 싶을 때 (`@Profile`, `@ConditionalOnProperty` 등).

정리하면, **컴포넌트 스캔은 "내 클래스를 자동 등록", `@Bean`은 "임의의 객체를 수동 등록"** 하는 두 갈래의 Bean 등록 방식이다. 둘은 상호 보완적이다.

---

## 2.6 애노테이션은 마법이 아니다: 프록시와 AOP

여기서부터가 이 장의 진짜 핵심이다. 대부분의 개발자가 "그냥 되니까" 쓰고 넘어가는 것을, 우리는 밑바닥까지 열어본다.

### 핵심 명제

> **"애노테이션은 마법이 아니다"** === **"애노테이션은 프록시다"**

`@Transactional`을 메서드에 붙이면 트랜잭션이 걸린다. `@Cacheable`을 붙이면 결과가 캐시된다. `@Async`를 붙이면 비동기로 실행된다. 이게 다 어떻게 되는 걸까? 애노테이션은 그냥 **글자 몇 개**인데?

정답: **애노테이션 자신은 아무 일도 하지 않는다.** 애노테이션은 그저 **표식(marker)** 일 뿐이다. "여기에 트랜잭션이 필요합니다"라는 팻말이다. 팻말이 스스로 트랜잭션을 열지는 못한다. 그 팻말을 **읽고 대신 일을 해주는 누군가**가 있다. 그 누군가가 바로 **프록시(Proxy)** 다.

### 프록시란: 원본을 감싸는 대리인

프록시(proxy)는 "대리인"이라는 뜻이다. 원본 객체를 감싸고, 원본인 척 행세하면서, 원본에게 요청이 전달되기 **전후로** 부가 작업을 끼워 넣는 객체다.

`@Transactional`이 붙은 `MemberService`가 있다고 하자. 이때 컨테이너에 등록되는 것은 우리가 작성한 **원본 `MemberService`가 아니다.** Spring이 원본을 상속(또는 인터페이스 구현)해서 만든 **가짜 —— 프록시 ——** 다. 클래스 이름을 찍어보면 `MemberService$$SpringCGLIB$$...` 같은 괴상한 이름이 나온다.

우리가 `memberService.save()`를 호출하면, 실제로는 이 프록시의 `save()`가 먼저 실행되고, 프록시가 이렇게 일한다.

```
memberService.save(member)  호출
        │
        ▼
┌──────────────── 프록시(MemberService$$SpringCGLIB$$) ────────────────┐
│                                                                     │
│   ① 트랜잭션 시작 (BEGIN)          ← 부가 기능 / 횡단 관심사            │
│           │                                                         │
│           ▼                                                         │
│   ② 진짜 MemberService.save() 호출  ← 내가 짠 비즈니스 로직            │
│           │                                                         │
│           ▼                                                         │
│   ③ 성공하면 COMMIT / 예외 나면 ROLLBACK   ← 다시 부가 기능           │
│                                                                     │
└─────────────────────────────────────────────────────────────────────┘
```

즉 트랜잭션의 시작과 커밋/롤백은 **내 코드 어디에도 없다.** 프록시가 내 메서드 앞뒤에 몰래 끼워 넣는다. 내 `save()` 안에는 오직 "회원을 저장한다"는 비즈니스 로직만 있다. **관심사가 깔끔하게 분리**된 것이다.

그리고 `@Cacheable`, `@Async`, `@PreAuthorize`, `@Retryable`... 이 모든 애노테이션이 **정확히 같은 프록시 메커니즘**으로 동작한다. 앞뒤에 끼워 넣는 작업이 트랜잭션이냐, 캐시냐, 비동기냐, 권한 검사냐만 다를 뿐, 구조는 동일하다. 하나를 이해하면 전부를 이해한 것이다.

### 🔬 실행으로 확인하기: `ProxyRevealRunner`

말로만 하면 안 믿긴다. 그래서 실제로 "프록시가 주입되었다"는 것을 눈으로 볼 수 있는 코드가 프로젝트에 들어 있다. **chapter06-spring-data-jpa 의 `ProxyRevealRunner` (CommandLineRunner)** 다.

이 러너는 애플리케이션이 부팅될 때 실행되어, 주입받은 `MemberService` Bean의 **실제 런타임 클래스 이름**을 로그로 출력한다. 애플리케이션을 실행하면 콘솔에 다음과 같이 찍힌다.

```
[프록시 폭로] 내가 작성한 클래스   : com.edu.jpa.service.MemberService
[프록시 폭로] 실제 주입된 런타임 클래스: com.edu.jpa.service.MemberService$$SpringCGLIB$$...
[프록시 폭로] 프록시 여부           : 예 (Spring이 감싼 프록시)
```

핵심을 보자.

- 내가 작성한 클래스는 분명 `com.edu.jpa.service.MemberService`다.
- 그런데 **실제 주입된 객체의 클래스는 `MemberService$$SpringCGLIB$$...`** —— Spring이 만든 CGLIB 프록시다.
- 러너는 클래스 이름에 `$$`가 들어 있는지로 프록시 여부를 판정한다.

이것이 `@Transactional`이 "마법"이 아니라 "프록시"라는 **살아 있는 증거**다. 코드 원리는 간단하다.

```java
String runtimeClass = memberService.getClass().getName();
boolean isProxy = runtimeClass.contains("$$"); // CGLIB 프록시는 이름에 $$가 들어간다
```

> **직접 해볼 것:** `MemberService`에서 `@Transactional`을 **지우고** 다시 실행해보라.
> 그러면 프록시가 사라진다 —— 로그에 `$$SpringCGLIB$$`가 없는 **원본 클래스 이름**이 찍히고,
> "프록시 여부: 아니오 (원본)"으로 바뀐다.
> 붙일 부가 기능이 없으면 Spring은 프록시를 만들 이유가 없기 때문이다.
> 애노테이션 하나가 런타임 객체의 정체를 바꾼다는 것을 직접 확인할 수 있다.

### 두 종류의 프록시: JDK 동적 프록시 vs CGLIB

Spring이 프록시를 만드는 방법은 두 가지다. 대상 클래스의 형태에 따라 자동으로 골라진다.

```
[ JDK 동적 프록시 ]                   [ CGLIB 프록시 ]
──────────────────────────           ──────────────────────────
 대상이 인터페이스를 구현할 때          대상이 구체 클래스일 때
 같은 인터페이스를 구현한 프록시 생성    대상 클래스를 상속(subclass)한 프록시
                                       클래스 이름에 $$SpringCGLIB$$

 «interface» Service                   class MemberService
      △                                     △
      │ implements                          │ extends
 [$Proxy Service]                      [MemberService$$CGLIB]

 제약: 인터페이스가 있어야 함           제약: final 클래스/메서드는 불가
                                              (상속을 못 하므로)
```

역사적으로 Spring은 "인터페이스가 있으면 JDK 동적 프록시, 없으면 CGLIB"를 썼다. 하지만 **Spring Boot는 기본적으로 CGLIB를 사용**한다(`proxyTargetClass=true`). 인터페이스가 있어도 구체 클래스를 상속한 프록시를 만든다. 그래서 위 `ProxyRevealRunner`에서도 `$$SpringCGLIB$$`가 찍힌 것이다.

CGLIB의 유일한 제약은 **상속을 이용하므로 `final`이면 안 된다**는 것이다. `final` 클래스는 상속할 수 없고, `final` 메서드는 오버라이드할 수 없어 프록시가 감쌀 수 없다. 그래서 "`@Transactional`을 붙였는데 왜 안 걸리지?" 하는 함정 중 하나가 대상 클래스/메서드가 `final`인 경우다.

### 함정 1: Self-invocation (자기 호출) —— 프록시를 우회한다

가장 악명 높은 함정이다. 같은 클래스 안에서 한 메서드가 다른 메서드를 `this.`로 호출하면, **애노테이션이 무시된다.**

```java
@Service
public class MemberService {

    public void outer() {
        // ⚠️ this.inner()는 프록시를 거치지 않는다!
        this.inner(); // 사실상 그냥 inner()
    }

    @Transactional
    public void inner() {
        // 여기 걸린 @Transactional은... outer()를 통해 들어오면 무시된다!
    }
}
```

왜 그럴까? 다시 그림을 보자. 프록시는 원본을 **감싼 바깥 껍데기**다. 트랜잭션 처리는 이 껍데기(프록시)에 있다.

```
[ 바깥에서 호출: 프록시 개입 O ]

  외부 호출자 ──▶ [프록시.inner()] ──▶ 트랜잭션 BEGIN ──▶ 원본.inner() ──▶ COMMIT
                     ↑ 프록시를 통과하므로 @Transactional 적용됨 ✔


[ self-invocation: 프록시 개입 X ]

  외부 호출자 ──▶ [프록시.outer()] ──▶ 트랜잭션 처리(outer엔 @Tx 없음)
                          │
                          ▼ 원본.outer() 안에서
                     this.inner()  ← this는 "원본"이지 "프록시"가 아니다!
                          │
                          ▼
                     원본.inner()  ← 프록시를 안 거치므로 @Transactional 무시 ✘
```

핵심은 `this`의 정체다. `outer()`가 실행되는 시점, 코드는 이미 **원본 객체 안**으로 들어와 있다. 그 안에서의 `this`는 프록시가 아니라 원본이다. 원본의 `inner()`를 직접 부르니 프록시의 트랜잭션 로직을 건너뛴다.

**프록시는 오직 바깥에서 `memberService.inner()` 형태로 진입할 때만 개입한다.** 안에서 자기 자신을 부르는 순간, 대리인은 잠들어 있다.

**해결책:**

1. **메서드를 별도 Bean으로 분리한다.** `inner()`를 다른 서비스로 빼면, `outer()`에서 그 Bean을 주입받아 호출한다. 그러면 프록시를 통과한다. (가장 권장.)
2. **자기 자신의 프록시를 주입받아 호출한다.** `AopContext.currentProxy()`나 자기 참조 주입으로 프록시를 통해 호출. (편법이며 가독성이 떨어진다.)

### 함정 2: Spring이 관리하는 Bean에만 적용된다

프록시는 컨테이너가 Bean을 만들 때 씌운다. 그러니 **내가 직접 `new`로 만든 객체에는 프록시가 없다.**

```java
MemberService s = new MemberService(); // 컨테이너가 안 만들었다 → 프록시 없음
s.save(member); // @Transactional 붙어 있어도 트랜잭션 안 걸림 ✘
```

`new`로 만든 순간 그것은 그냥 순수한 원본 객체다. 애노테이션은 붙어 있지만, 그걸 읽고 감싸줄 프록시가 없으니 아무 일도 일어나지 않는다. **반드시 컨테이너로부터 주입받은 Bean을 통해 호출**해야 애노테이션이 살아난다.

### 뿌리로 내려가기: 이 모든 걸 떠받치는 두 기둥

프록시라는 실체는 두 개의 더 근본적인 개념 위에 서 있다.

#### 기둥 1: 리플렉션(Reflection) —— 런타임에 애노테이션을 읽는 눈

애노테이션은 **컴파일 타임에 코드에 박아두는 표식**이다. 그런데 이 표식을 실행 중에 누가 읽는가? **리플렉션**이다.

리플렉션은 "실행 중인 프로그램이 자기 자신의 클래스 구조(필드, 메서드, 애노테이션)를 조회하는 기능"이다. Spring은 시작 시점에 모든 Bean 후보 클래스를 리플렉션으로 훑으며 이렇게 묻는다.

> "이 클래스, 혹은 이 메서드에 `@Transactional`이 붙어 있나?"

붙어 있으면 "이 Bean은 프록시로 감싸야겠군" 하고 표시한다. 정리하면:

- **애노테이션** = 컴파일 타임에 심어두는 정적 표식
- **리플렉션** = 런타임에 그 표식을 읽어내는 눈

이 둘이 만나야 비로소 "선언(애노테이션)"이 "동작(프록시 생성)"으로 이어진다.

#### 기둥 2: AOP(관점 지향 프로그래밍) —— 왜 이런 설계가 필요했나

프록시는 "어떻게(how)"이고, AOP는 "왜(why)"다. AOP가 풀려는 문제는 **횡단 관심사(cross-cutting concern)** 다.

횡단 관심사란, 특정 계층이나 모듈에 국한되지 않고 **애플리케이션 곳곳의 여러 메서드에 공통으로 걸쳐 있는 관심사**다. 대표적으로 트랜잭션, 로깅, 보안(인증/인가), 캐싱, 성능 측정이 있다. 이들은 비즈니스 로직의 "본질"은 아니지만, 거의 모든 메서드에 필요하다.

프록시(=AOP) 없이 트랜잭션을 직접 짠다면 이렇게 된다.

```java
// AOP가 없다면... 모든 메서드가 이 지옥을 반복한다
public void save(Member m) {
    tx.begin();                    // ← 반복
    try {
        repository.save(m);        // ← 이 한 줄이 진짜 로직
        tx.commit();               // ← 반복
    } catch (Exception e) {
        tx.rollback();             // ← 반복
        throw e;
    }
}
public void update(Member m) {
    tx.begin();                    // ← 또 반복
    try { /* ... */ tx.commit(); }
    catch (Exception e) { tx.rollback(); throw e; } // ← 또 반복
}
// ... 수십, 수백 개 메서드마다 이 begin/commit/rollback을 복붙 ...
```

진짜 로직은 한 줄인데, 그 주변을 트랜잭션 보일러플레이트가 감싸고 있다. 메서드가 수백 개면 이 코드가 수백 번 복사된다. 트랜잭션 처리 방식을 바꾸려면 수백 곳을 다 고쳐야 한다. **관심사가 흩어져(scattered) 있고, 로직과 뒤섞여(tangled) 있다.**

AOP는 이렇게 선언한다.

> **"반복되는 부가 코드를, 한 곳에 모아서, 대상 메서드의 앞뒤에 자동으로 끼워 넣겠다."**

- **한 곳에 모은다** = 트랜잭션 로직을 프록시(Advice) 한 군데에만 둔다.
- **대상 메서드 앞뒤에 자동 삽입** = 프록시가 `@Transactional` 붙은 메서드마다 begin/commit/rollback을 대신 실행한다.

그러면 개발자의 `save()`에는 `repository.save(m)` 한 줄만 남는다. 트랜잭션은 사라진 게 아니라, 프록시로 **옮겨졌다.** 그리고 그 프록시를 만드는 구현 도구가 앞서 본 CGLIB/JDK 동적 프록시다.

### 최종 정리: 네 단어의 관계

```
 애노테이션 ── "여기에 트랜잭션이 필요하다"는 표식 (선언)
     │
     │ 리플렉션이 읽는다
     ▼
 리플렉션 ──── 런타임에 표식을 발견하는 메커니즘 (읽는 눈)
     │
     │ 발견하면 프록시로 감싼다
     ▼
 프록시 ────── 원본을 감싸 앞뒤에 부가 코드를 끼워 넣는 실체 (실행 주체)
     │
     │ 이 모든 걸 관통하는 설계 사상
     ▼
 AOP ──────── 횡단 관심사를 한 곳에 모아 자동 적용하는 설계 사상 (why)
```

- **애노테이션** = 표식 (선언)
- **리플렉션** = 표식을 읽는 메커니즘 (눈)
- **프록시** = 실제로 코드를 끼워 넣는 실체 (손)
- **AOP** = 이렇게 하자는 설계 사상 (뇌)

이제 `@Transactional`을 보면 "마법"이 아니라 이 네 단어의 협주가 보여야 한다.

---

## 2.7 서버는 멀티스레드다 (thread-per-request)

§2.4에서 "싱글톤 Bean에 상태를 저장하지 마라"고 못 박았다. 이 절은 그 규칙의 **정확한 메커니즘**을 파헤친다. 왜 하필 위험한가? 언제 터지는가? 그 답은 "서버는 여러 스레드로 동작한다"는 사실에 있다.

### 질문: 왜 여러 스레드가 하나의 Bean을 동시에 쓰는가

싱글톤 Bean은 인스턴스가 하나뿐이라고 했다. 그런데 웹 서버에는 수백 명이 동시에 접속한다. 이 수백 개의 요청이 그 하나뿐인 `MemberService` 인스턴스를 어떻게 나눠 쓸까? 답은 **스레드풀(thread pool)** 이다.

### 원리: thread-per-request 모델

Spring Boot에 내장된 톰캣(Tomcat)은 **워커 스레드(worker thread) 풀**을 갖고 있다. HTTP 요청이 하나 들어오면, 톰캣은 풀에서 놀고 있는 워커 스레드 하나를 꺼내 그 요청 처리를 통째로 맡긴다. 요청 처리가 끝나면 스레드는 풀로 반납되어 다음 요청을 기다린다. 이 모델을 **thread-per-request(요청당 스레드)** 라고 부른다.

```
                       [ 톰캣 워커 스레드 풀 ]
                       ┌──────────────────────┐
   요청 A ────────────▶│  Thread-1 (요청 A 처리)│──┐
   요청 B ────────────▶│  Thread-2 (요청 B 처리)│──┤   전부 같은
   요청 C ────────────▶│  Thread-3 (요청 C 처리)│──┼──▶ ┌──────────────┐
   요청 D ────────────▶│  Thread-4 (요청 D 처리)│──┤    │ MemberService│
   ...    ────────────▶│  ...  (기본 최대 200개) │──┘    │  (싱글톤 1개) │
                       └──────────────────────┘        └──────────────┘
                          ↑ 스레드는 요청마다 다르다        ↑ Bean은 딱 하나
```

**핵심 대비:**

- **스레드는 요청마다 다르다.** 요청 A는 Thread-1이, 요청 B는 Thread-2가 처리한다.
- **하지만 `MemberService` Bean은 딱 하나(싱글톤)다.** 모든 워커 스레드가 이 하나의 인스턴스를 공유한다.

즉 초당 수백 요청이 몰리면, 수백 개의 워커 스레드가 **동시에 같은 인스턴스의 메서드를 실행**한다. 이것이 "싱글톤을 여러 스레드가 공유한다"는 말의 실체다.

### 왜 가변 필드가 Race Condition을 부르는가

이제 §2.4의 나쁜 코드로 돌아가자.

```java
@Service
public class MemberService {
    private User currentUser; // 싱글톤 인스턴스의 필드 = 모든 스레드가 공유!

    public void process(User user) {
        this.currentUser = user;         // (1) 필드에 저장
        doSomethingSlow();               // (2) 시간이 걸리는 작업
        report(this.currentUser);        // (3) 필드를 다시 읽음
    }
}
```

이 필드 `currentUser`는 인스턴스가 하나뿐이므로 **모든 스레드가 같은 변수를 본다.** 두 요청이 겹치면 이런 시나리오가 벌어진다.

```
시간 ▼      Thread-A (홍길동 요청)              Thread-B (김철수 요청)
─────────────────────────────────────────────────────────────────────
 t1    currentUser = 홍길동   (1)
 t2    doSomethingSlow() 실행 중... (2)
 t3                                       currentUser = 김철수   (1)  ← 덮어씀!
 t4    report(currentUser) (3)
       → currentUser는 이미 "김철수"다 💥
       → 홍길동이 김철수의 데이터를 처리/조회하게 됨
─────────────────────────────────────────────────────────────────────
```

Thread-A가 `currentUser=홍길동`을 넣고 느린 작업을 하는 사이, Thread-B가 `currentUser=김철수`로 **덮어써버렸다.** 그러다 Thread-A가 필드를 다시 읽으니 "김철수"가 나온다. 홍길동 사용자가 김철수의 정보를 보게 되는, 앞서 말한 그 개인정보 유출 사고다. 이것이 **레이스 컨디션(race condition)** —— 여러 스레드가 공유 자원에 경쟁적으로 접근하다 순서가 꼬여 나는 버그 —— 다.

**이 버그가 최악인 이유:** 트래픽이 몰려 두 요청이 정확히 겹치는 그 짧은 순간에만 발생한다. 개발자 PC에서 혼자 테스트할 땐 절대 안 나타난다. 요청이 하나뿐이니까. **운영 서버에서, 사용자가 많을 때만, 간헐적으로** 터진다. 재현조차 안 되니 디버깅이 지옥이다. 로그를 봐도 "가끔 데이터가 뒤바뀐다"는 유령 같은 현상만 남는다.

### 그래서: Service는 Stateless여야 한다

해답은 명확하다. **싱글톤 Bean(특히 Service)은 상태를 필드로 들지 않는다.** 요청별 데이터는 필드가 아니라 **메서드 인자와 지역 변수**로 다룬다.

```java
@Service
public class MemberService {
    // 필드에 요청 데이터를 두지 않는다 (currentUser 같은 것 금지)

    public void process(User user) {   // user는 매개변수 → 각 스레드의 스택에 따로 존재
        User localUser = user;         // 지역 변수 → 스레드마다 독립적
        doSomethingSlow();
        report(localUser);             // 절대 다른 스레드와 안 섞인다 ✔
    }
}
```

**왜 지역 변수/매개변수는 안전한가?** 이건 JVM 메모리 구조의 문제다.

```
[ 힙(Heap) — 모든 스레드 공유 ]         [ 스택(Stack) — 스레드마다 따로 ]
────────────────────────────           ──────────────────────────────────
  MemberService 인스턴스 (1개)            Thread-A 스택: user=홍길동, localUser=홍길동
   └─ 필드 currentUser ← 공유! 위험      Thread-B 스택: user=김철수, localUser=김철수
                                              ↑ 서로 완전히 격리됨. 안 섞임.
```

- **인스턴스 필드**는 힙에 있는 하나의 객체에 속하므로, 그 객체를 공유하는 모든 스레드가 함께 본다. → 공유됨 → 위험.
- **지역 변수와 매개변수**는 각 스레드의 **스택(stack)** 에 따로 저장된다. 스레드마다 자기 스택이 있으므로, Thread-A의 `localUser`와 Thread-B의 `localUser`는 이름만 같을 뿐 완전히 별개의 메모리다. → 절대 안 섞임 → 안전.

> **원칙: 공유되는 것은 오직 필드(인스턴스 상태)뿐이다. 요청마다 다른 데이터는 반드시 지역 변수/매개변수로 흐르게 하라.** 이것이 "Service는 stateless여야 한다"의 진짜 의미다.

### 또 하나의 공유 자원: 커넥션 풀 (HikariCP)

thread-per-request는 또 다른 병목을 드러낸다. **DB 커넥션**이다.

워커 스레드마다 DB 작업을 하려면 DB 커넥션이 필요하다. 그런데 커넥션은 **비싸다.** 새 커넥션 하나를 여는 데는 TCP 연결 수립 + 인증 협상이 필요해 수십 밀리초가 걸린다. 요청마다 커넥션을 새로 열고 닫으면, 그 오버헤드만으로 서버가 느려진다.

그래서 커넥션도 Bean처럼 "미리 만들어 두고 재사용"한다. 이것이 **커넥션 풀(connection pool)** 이고, Spring Boot의 기본 구현이 **HikariCP**다.

```
   워커 스레드들                    [ HikariCP 커넥션 풀 (기본 10개) ]
   ┌──────────┐   빌린다(borrow)    ┌────────────────────────────┐
   │ Thread-1 │──────────────────▶  │ [Conn-1] 사용 중            │
   │ Thread-2 │──────────────────▶  │ [Conn-2] 사용 중            │
   │ Thread-3 │──────────────────▶  │ [Conn-3] 사용 중            │──▶ [ DB ]
   │   ...    │                     │ [Conn-4] 유휴(idle)         │
   │ Thread-11│── 대기(wait) ⏳     │ ...                        │
   └──────────┘                     └────────────────────────────┘
       ↑ 반납(return)하면 다음 스레드가 빌려간다
```

HikariCP는 커넥션을 미리 여러 개 열어두고, 스레드가 필요할 때 **빌려주고(borrow)**, 작업이 끝나면 **반납(return)** 받는다. 열고 닫는 비용이 사라지니 훨씬 빠르다.

### 커넥션 풀 고갈: 서버가 통째로 멈추는 시나리오

하지만 커넥션 풀의 크기는 **유한**하다(기본 10개). 여기서 무서운 문제가 생긴다.

워커 스레드는 200개인데 커넥션은 10개뿐이라면, 11번째로 DB가 필요한 스레드는 **커넥션이 반납될 때까지 대기**한다. 대기가 짧으면 괜찮다. 하지만 이런 코드가 있다면?

```java
@Transactional
public void process() {
    repository.save(data);        // 커넥션 빌림 (트랜잭션 시작 시점)
    externalApi.call();           // ⚠️ 외부 API 호출 — 3초 걸림!
    repository.update(data);      // 그동안 커넥션을 계속 붙잡고 있다
}                                 // 트랜잭션 끝나야 커넥션 반납
```

`@Transactional` 안에서 외부 API를 3초간 기다린다. 그동안 이 트랜잭션은 커넥션을 붙잡고 놓지 않는다. 요청 10개가 동시에 이 코드를 타면 커넥션 10개가 전부 "외부 API 대기" 상태로 묶인다. 그러면 **11번째 요청부터는 DB 작업이 필요한 순간 무한정 대기**한다. 사용자 눈에는 "서버가 응답을 안 한다"로 보인다. 사실 CPU는 놀고 있는데, 커넥션이 고갈되어 아무도 DB에 못 가는 것이다. **커넥션 풀 고갈로 인한 전면 장애**다.

**교훈:**

- 트랜잭션은 짧게 유지하라. 트랜잭션 안에서 외부 API 호출, 파일 IO, 사용자 입력 대기 같은 "느린 작업"을 하지 마라.
- 성능 튜닝의 핵심은 **"톰캣 스레드풀 크기 ↔ HikariCP 커넥션 풀 크기 ↔ DB의 최대 동시 연결 수"의 균형**이다. 스레드가 커넥션보다 지나치게 많으면 대기가 쌓이고, 커넥션이 DB 허용치를 넘으면 DB가 거부한다. 이 세 숫자를 함께 맞춰야 한다.

### 이 절의 흐름 정리

```
 thread-per-request       →   싱글톤 Bean을        →   그래서 Service는     →   DB도 커넥션 풀을
 (요청마다 워커 스레드)         여러 스레드가 공유         stateless여야 함          여러 스레드가 공유
                                    │                       │                        │
                              공유되는 건 필드뿐      상태는 지역변수/인자로      풀 고갈 주의 (짧은 tx)
```

§2.4의 "싱글톤에 상태 저장 금지"라는 규칙이, 여기까지 와서야 완전한 그림으로 이어진다. 규칙을 외우는 것과 그 메커니즘을 이해하는 것은 다르다. 이제 여러분은 이해했다.

---

## ⚠️ 흔한 오해와 함정

**1. "`@Autowired`를 필드에 붙이는 게 제일 간단하니 그게 정석이다."**
아니다. 간단해 보이는 필드 주입은 §2.3의 네 가지 위험(불변성 불가, 필수 의존성 미강제, 테스트 곤란, 순환 참조 은폐)을 전부 안고 있다. 간단함이 곧 올바름은 아니다. 생성자 주입이 정석이다.

**2. "`new`로 만든 객체에도 `@Transactional`이 걸린다."**
안 걸린다(§2.6). 애노테이션은 표식일 뿐, 프록시가 감싸야 동작한다. 프록시는 컨테이너가 관리하는 Bean에만 씌운다. `new`로 만든 순간 그건 그냥 원본 객체다.

**3. "같은 클래스 안에서 `@Transactional` 메서드를 호출하면 당연히 트랜잭션이 걸린다."**
안 걸린다. self-invocation 함정(§2.6)이다. `this.method()`는 프록시를 우회한다. 프록시는 바깥에서 진입할 때만 개입한다.

**4. "싱글톤이니까 상태를 필드에 저장해도 하나뿐이라 안전하다."**
정반대다. **하나뿐이라서 위험**하다(§2.7). 그 하나를 수백 스레드가 공유하므로, 가변 필드는 레이스 컨디션의 온상이다.

**5. "인터페이스가 없으면 프록시가 안 만들어진다."**
과거 이야기다. Spring Boot는 기본으로 CGLIB를 써서 구체 클래스도 상속으로 프록시를 만든다. 단, `final` 클래스/메서드는 상속/오버라이드가 불가능해 프록시가 안 된다.

**6. "메인 클래스는 아무 데나 둬도 스캔된다."**
아니다(§2.5). 스캔은 메인 클래스가 있는 패키지와 그 하위만 대상이다. 메인 클래스는 최상위 패키지에 둬야 한다.

**7. "커넥션 풀만 크게 잡으면 성능이 좋아진다."**
아니다(§2.7). 커넥션이 DB의 최대 동시 연결 수를 넘으면 DB가 거부하고, 커넥션 컨텍스트 스위칭 비용도 커진다. 스레드/커넥션/DB 세 숫자의 균형이 중요하다.

---

## 연습문제

**문제 1.** 아래 코드는 필드 주입을 쓰고 있다. 생성자 주입으로 리팩터링하고, 왜 그게 더 나은지 두 가지 이유를 대라.

```java
@Service
public class ReviewService {
    @Autowired private ReviewRepository repository;
    @Autowired private MemberService memberService;
}
```

<details><summary>힌트</summary>필드를 <code>final</code>로 바꾸고 생성자를 추가하라. 생성자가 하나면 <code>@Autowired</code>는 생략할 수 있다.</details>

<details><summary>해설</summary>

```java
@Service
public class ReviewService {
    private final ReviewRepository repository;
    private final MemberService memberService;

    public ReviewService(ReviewRepository repository, MemberService memberService) {
        this.repository = repository;
        this.memberService = memberService;
    }
}
```
더 나은 이유(택 2): ① `final`로 불변성을 보장한다. ② 의존성 없이는 컴파일이 안 되므로 필수 의존성이 강제된다. ③ 테스트에서 Spring 없이 mock을 생성자로 주입할 수 있다. ④ 순환 참조가 있으면 부팅 시점에 즉시 발견된다.
</details>

---

**문제 2.** 다음 코드에서 `saveWithLog()`를 통해 호출하면 `save()`의 `@Transactional`이 적용되지 않는다. 이유를 설명하고 해결 방법을 하나 제시하라.

```java
@Service
public class MemberService {
    public void saveWithLog(Member m) {
        log.info("saving...");
        this.save(m);
    }
    @Transactional
    public void save(Member m) { repository.save(m); }
}
```

<details><summary>힌트</summary><code>this.save(m)</code>에서 <code>this</code>는 무엇인가? 프록시인가, 원본인가?</details>

<details><summary>해설</summary>
self-invocation 함정이다. `saveWithLog()`가 실행되는 시점 코드는 이미 원본 객체 안에 있고, `this`는 프록시가 아니라 원본이다. 따라서 `this.save()`는 프록시의 트랜잭션 로직을 우회한다. 해결책: (1) `save()`를 별도 Bean(예: `MemberWriter`)으로 분리하고 주입받아 호출한다. (2) `saveWithLog()`에 `@Transactional`을 붙이고 로깅은 AOP나 별도 방식으로 처리한다. (3) 자기 프록시를 주입받아 호출한다(비권장).
</details>

---

**문제 3.** 아래 서비스는 트래픽이 몰릴 때 "다른 사용자의 이름이 응답에 섞여 나온다"는 버그 리포트를 받았다. 원인을 §2.7의 용어로 설명하고 수정하라.

```java
@Service
public class GreetingService {
    private String name;
    public String greet(String name) {
        this.name = name;
        return "안녕하세요, " + this.name + "님";
    }
}
```

<details><summary>힌트</summary>싱글톤 Bean의 필드는 누가 공유하는가?</details>

<details><summary>해설</summary>
`GreetingService`는 싱글톤이고 `name`은 인스턴스 필드라 모든 워커 스레드가 공유한다. 스레드 A가 `this.name`을 세팅한 직후 스레드 B가 덮어쓰면, A가 B의 이름을 반환하는 레이스 컨디션이 발생한다. 트래픽이 몰려 요청이 겹칠 때만 간헐적으로 터진다. 수정: 필드를 없애고 매개변수/지역 변수만 사용한다.

```java
@Service
public class GreetingService {
    public String greet(String name) {          // name은 스레드별 스택에 독립 존재
        return "안녕하세요, " + name + "님";
    }
}
```
</details>

---

**문제 4.** `chapter06-spring-data-jpa`의 애플리케이션을 실행하면 `ProxyRevealRunner`가 로그를 찍는다. `MemberService`에서 `@Transactional`을 모두 제거하고 다시 실행하면 로그가 어떻게 바뀔지 예측하고, 그 이유를 설명하라.

<details><summary>힌트</summary>Spring은 언제 프록시를 만드는가? 감쌀 부가 기능이 없으면?</details>

<details><summary>해설</summary>
`@Transactional`이 사라지면 Spring이 이 Bean을 프록시로 감쌀 이유가 없다. 따라서 런타임 클래스가 `MemberService$$SpringCGLIB$$...`가 아니라 원본 `com.edu.jpa.service.MemberService`로 찍히고, "프록시 여부: 아니오 (원본)"이 출력된다. 프록시는 마법이 아니라, 적용할 부가 기능(애노테이션)이 있을 때만 생성되는 실체임을 보여준다.
</details>

---

**문제 5.** 외부 라이브러리인 `com.fasterxml.jackson.databind.ObjectMapper`를 Bean으로 등록하고 싶다. `@Component`를 붙일 수 없는 이유와 올바른 등록 방법을 코드로 보여라.

<details><summary>힌트</summary>내가 소스를 소유하지 않은 클래스는 어떻게 Bean으로 만드는가?(§2.5)</details>

<details><summary>해설</summary>
`ObjectMapper`는 남의 라이브러리 클래스라 소스를 수정해 `@Component`를 붙일 수 없다. 이럴 때는 `@Configuration` 클래스에서 `@Bean` 메서드로 수동 등록한다.

```java
@Configuration
public class JacksonConfig {
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }
}
```
</details>

---

## 요약

- **IoC(제어의 역전)**: "무엇을 언제 만들고 연결할지"의 주도권을 개발자에서 컨테이너로 넘긴다. 목적은 편리함이 아니라 **변경의 파급 범위를 한 곳으로 격리**하는 것이다. 할리우드 원칙 —— "우리가 당신에게 전화하겠습니다".
- **DI(의존성 주입)**: IoC를 실현하는 기법. 구체 클래스가 아니라 **인터페이스에 의존**하고 구현체는 밖에서 주입받아, 강한 결합(용접)을 느슨한 결합(엔진 마운트+볼트)으로 바꾼다. 변경/테스트/확장이 쉬워진다.
- **생성자 주입이 최선**: 불변성(`final`), 필수 의존성 강제(컴파일 에러), 테스트 용이(Spring 없이 mock 주입), 순환 참조 조기 발견(부팅 실패). 필드 주입은 시한폭탄, 생성자 주입은 즉시 경보.
- **Bean**: 컨테이너가 생성/주입/생명주기를 관리하는 객체. 기본 스코프는 **싱글톤**(인스턴스 1개, 메모리 효율 + stateless). 싱글톤 Bean에 상태(가변 필드)를 저장하면 안 된다.
- **Component Scan**: `@Component` 계열 애노테이션을 스캔해 Bean 등록. 메인 클래스는 최상위 패키지에. 외부 라이브러리/복잡한 생성/조건부 등록은 `@Configuration`+`@Bean` 수동 등록.
- **애노테이션은 마법이 아니라 프록시다**: `@Transactional` 등은 표식일 뿐, **프록시**가 원본을 감싸 앞뒤에 부가 기능을 끼워 넣는다. 리플렉션이 표식을 읽고, 프록시가 실행하며, AOP가 그 설계 사상이다. Spring Boot 기본은 CGLIB(상속 프록시, `final` 불가). 함정: self-invocation(`this.method()`는 프록시 우회), `new` 객체엔 미적용. 실행 증거는 **chapter06-spring-data-jpa 의 `ProxyRevealRunner`** 가 찍는 `$$SpringCGLIB$$` 로그.
- **서버는 멀티스레드다(thread-per-request)**: 톰캣 워커 스레드풀이 요청마다 스레드를 배정하지만 싱글톤 Bean은 하나라 **여러 스레드가 공유**한다. 가변 필드는 레이스 컨디션을 부르니 Service는 **stateless**여야 한다(요청 데이터는 지역 변수/인자로). DB 커넥션도 **HikariCP 풀**로 공유하며, 긴 트랜잭션은 커넥션 풀을 고갈시켜 서버를 멈출 수 있다.

이 장을 관통하는 한 문장:

> **Spring은 "당신이 로직에만 집중하도록, 나머지 지루하고 위험한 일(생성, 연결, 트랜잭션, 동시성)을 대신 떠맡는" 프레임워크다. 그리고 그 대신 떠맡는 방식이 IoC/DI이고, 프록시이고, thread-per-request다.**

---

[← 이전: Spring이 해결하는 문제](01-spring이-해결하는-문제.md) | [목차](README.md) | [다음: 웹 애플리케이션의 구조 →](03-웹-애플리케이션-구조.md)
