# Chapter 08: Testing with JUnit 5 and Testcontainers

> **🐳 실습 환경 — 이 장은 `spring-ch08-testing` 컨테이너(+ 전용 DB `spring-ch08-postgres`)로 실습한다**
> ```bash
> cd spring/chapter08-testing && docker compose up --build
> ```
> Testcontainers 통합 테스트(`./gradlew test`)는 앱 컨테이너 없이도 실행되지만 **Docker 데몬은 반드시 떠 있어야 한다**.
> 공통 인프라 `spring-postgres`와 동시에 띄우면 5432 포트 충돌 주의. 컨테이너 상태 확인: `docker ps`

## 학습 목표
- 테스트의 중요성과 종류를 이해한다
- JUnit 5의 핵심 기능을 활용할 수 있다
- Mockito를 사용한 단위 테스트를 작성할 수 있다
- Spring Boot 테스트 슬라이스를 활용할 수 있다
- Testcontainers로 Docker 기반 통합 테스트를 작성할 수 있다

---

## 1. 테스트의 중요성과 종류

### 왜 테스트를 작성해야 하는가?

소프트웨어 개발에서 테스트는 **코드의 품질을 보장**하고 **리팩토링을 안전하게** 만들어주는 핵심 활동이다.
테스트가 없는 코드는 변경할 때마다 기존 기능이 깨질 위험이 있다.

### 테스트의 종류

```
┌─────────────────────────────────────┐
│           E2E 테스트                 │  ← 가장 느리지만 실제 환경과 유사
│     ┌───────────────────────┐       │
│     │    통합 테스트          │       │  ← 여러 컴포넌트의 상호작용 검증
│     │  ┌─────────────────┐  │       │
│     │  │   단위 테스트     │  │       │  ← 가장 빠르고 격리된 테스트
│     │  └─────────────────┘  │       │
│     └───────────────────────┘       │
└─────────────────────────────────────┘
         테스트 피라미드
```

| 종류 | 범위 | 속도 | 도구 |
|------|------|------|------|
| **단위 테스트 (Unit Test)** | 클래스/메서드 하나 | 매우 빠름 | JUnit 5, Mockito |
| **통합 테스트 (Integration Test)** | 여러 컴포넌트 조합 | 보통 | @SpringBootTest, Testcontainers |
| **E2E 테스트 (End-to-End)** | 전체 시스템 | 느림 | Selenium, RestAssured |

---

## 2. JUnit 5 기본

### 2.1 JUnit 5 구조

JUnit 5 = **JUnit Platform** + **JUnit Jupiter** + **JUnit Vintage**

- **JUnit Platform**: 테스트 실행 엔진
- **JUnit Jupiter**: JUnit 5의 새로운 프로그래밍 모델과 확장 모델
- **JUnit Vintage**: JUnit 3/4 호환 지원

### 2.2 핵심 어노테이션

```java
@Test                  // 테스트 메서드 표시
@DisplayName("설명")   // 테스트 이름 지정 (한글 가능)
@BeforeEach            // 각 테스트 전에 실행
@AfterEach             // 각 테스트 후에 실행
@BeforeAll             // 모든 테스트 전에 한 번 실행 (static)
@AfterAll              // 모든 테스트 후에 한 번 실행 (static)
@Nested                // 중첩 테스트 클래스
@Disabled              // 테스트 비활성화
```

### 2.3 Assertions (단언문)

```java
// 기본 단언
assertEquals(expected, actual);          // 같은 값인지
assertNotEquals(unexpected, actual);     // 다른 값인지
assertTrue(condition);                   // true인지
assertFalse(condition);                  // false인지
assertNull(object);                      // null인지
assertNotNull(object);                   // null이 아닌지

// 예외 검증
assertThrows(Exception.class, () -> {
    // 예외가 발생해야 하는 코드
});

// 여러 단언을 그룹화 (하나가 실패해도 나머지 실행)
assertAll(
    () -> assertEquals("이름", product.getName()),
    () -> assertEquals(1000, product.getPrice()),
    () -> assertEquals(10, product.getStock())
);
```

### 2.4 Lifecycle (생명주기)

```java
class LifecycleTest {
    @BeforeAll
    static void beforeAll() {
        System.out.println("모든 테스트 시작 전 한 번 실행");
    }

    @BeforeEach
    void setUp() {
        System.out.println("각 테스트 시작 전 실행");
    }

    @Test
    void test1() { System.out.println("테스트 1"); }

    @Test
    void test2() { System.out.println("테스트 2"); }

    @AfterEach
    void tearDown() {
        System.out.println("각 테스트 종료 후 실행");
    }

    @AfterAll
    static void afterAll() {
        System.out.println("모든 테스트 종료 후 한 번 실행");
    }
}
```

### 2.5 Parameterized Test (매개변수화 테스트)

같은 로직을 다양한 입력 값으로 반복 테스트할 때 사용한다.

```java
@ParameterizedTest
@ValueSource(ints = {1, 5, 10, 50, 100})
void 재고_감소_테스트(int quantity) {
    product.decreaseStock(quantity);
    assertEquals(100 - quantity, product.getStock());
}

@ParameterizedTest
@CsvSource({
    "'상품A', 1000, 10",
    "'상품B', 2000, 20",
    "'상품C', 3000, 30"
})
void 상품_생성_테스트(String name, int price, int stock) {
    Product p = new Product(name, new BigDecimal(price), stock);
    assertEquals(name, p.getName());
}
```

---

## 3. Mockito 활용

### 3.1 Mockito란?

Mockito는 **가짜 객체(Mock)**를 만들어주는 프레임워크다.
단위 테스트에서 테스트 대상이 의존하는 객체를 Mock으로 대체하여 **격리된 테스트**를 작성할 수 있다.

### 3.2 핵심 개념

```java
@ExtendWith(MockitoExtension.class)   // Mockito 확장 활성화
class ServiceTest {

    @Mock                               // 가짜 객체 생성
    private Repository repository;

    @InjectMocks                        // Mock을 주입받는 테스트 대상
    private Service service;
}
```

### 3.3 BDD 스타일 (Given-When-Then)

```java
@Test
void 상품_조회_테스트() {
    // Given - 테스트 데이터와 Mock 행동 정의
    Product product = new Product("테스트", new BigDecimal("1000"), 10);
    given(repository.findById(1L)).willReturn(Optional.of(product));

    // When - 테스트 대상 실행
    Product result = service.findById(1L);

    // Then - 결과 검증
    assertEquals("테스트", result.getName());
    verify(repository, times(1)).findById(1L);  // 호출 횟수 검증
}
```

### 3.4 주요 Mockito 메서드

```java
// Mock 행동 정의
given(mock.method()).willReturn(value);       // 반환값 지정
given(mock.method()).willThrow(exception);    // 예외 발생
given(mock.method(any())).willReturn(value);  // 임의의 인자

// 호출 검증
verify(mock).method();                        // 1번 호출되었는지
verify(mock, times(2)).method();              // 2번 호출되었는지
verify(mock, never()).method();               // 호출되지 않았는지
verifyNoMoreInteractions(mock);               // 더 이상 호출 없는지
```

---

## 4. Spring Boot 테스트

### 4.1 @SpringBootTest

전체 애플리케이션 컨텍스트를 로드하는 **통합 테스트**용 어노테이션이다.

```java
@SpringBootTest
class ApplicationIntegrationTest {
    @Autowired
    private ProductService productService;

    @Test
    void contextLoads() {
        assertNotNull(productService);
    }
}
```

### 4.2 @WebMvcTest

**Controller 레이어만** 테스트할 때 사용한다. Service는 Mock으로 대체된다.

> **Spring Boot 3.4 변경점**: 기존 `@MockBean`이 deprecated 되었고,
> `org.springframework.test.context.bean.override.mockito.MockitoBean`의 `@MockitoBean`을 사용한다.

```java
@WebMvcTest(ProductController.class)
class ProductControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean                        // Spring Boot 3.4+ : @MockBean 대신 사용
    private ProductService productService;

    @Test
    void 상품_목록_조회() throws Exception {
        given(productService.findAll()).willReturn(List.of(...));

        mockMvc.perform(get("/api/products"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2));
    }
}
```

> 실제 동작하는 예제는 `src/test/.../ProductControllerTest.java`와
> 그 대상인 `src/main/.../controller/ProductController.java`를 참고한다.

### 4.3 @DataJpaTest

**Repository 레이어만** 테스트할 때 사용한다. 내장 DB나 Testcontainers와 함께 사용한다.

```java
@DataJpaTest
class ProductRepositoryTest {
    @Autowired
    private ProductRepository productRepository;

    @Test
    void 이름으로_검색() {
        productRepository.save(new Product("노트북", ...));
        List<Product> result = productRepository.findByNameContaining("노트북");
        assertEquals(1, result.size());
    }
}
```

### 4.4 테스트 슬라이스 비교

| 어노테이션 | 로드 범위 | 용도 | 예제 코드 |
|------------|----------|------|-----------|
| `@SpringBootTest` | 전체 컨텍스트 | 통합 테스트 | - |
| `@WebMvcTest` | Controller + MVC 관련 | API 테스트 | `ProductControllerTest` |
| `@DataJpaTest` | JPA + Repository 관련 | DB 테스트 | `ProductRepositoryTestcontainersTest` |
| `@JsonTest` | JSON 직렬화/역직렬화 | JSON 테스트 | - |

---

## 5. Testcontainers로 Docker 기반 통합 테스트

### 5.1 Testcontainers란?

Testcontainers는 **Docker 컨테이너를 테스트에서 자동으로 관리**해주는 라이브러리다.
실제 데이터베이스(PostgreSQL, MySQL 등)를 Docker로 띄워서 테스트하므로 **H2 같은 내장 DB와 달리 실제 환경과 동일한 조건**에서 테스트할 수 있다.

### 5.2 왜 Testcontainers를 사용하는가?

| H2 내장 DB | Testcontainers |
|------------|----------------|
| 실제 DB와 문법 차이 | 실제 DB 사용 |
| DB 고유 기능 미지원 | 모든 기능 지원 |
| 빠르지만 신뢰도 낮음 | 조금 느리지만 신뢰도 높음 |

### 5.3 사용 방법

```java
@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class RepositoryTest {

    // 1. Docker 컨테이너 정의
    @Container
    static PostgreSQLContainer<?> postgres =
        new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("test_db")
            .withUsername("test")
            .withPassword("test");

    // 2. 동적으로 Spring 설정 주입
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    // 3. 실제 PostgreSQL에 대해 테스트 실행
    @Autowired
    private ProductRepository repository;

    @Test
    void 실제_DB_테스트() {
        repository.save(new Product("노트북", ...));
        // 실제 PostgreSQL에서 쿼리 실행
    }
}
```

### 5.4 Testcontainers 동작 흐름

```
1. 테스트 시작
    ↓
2. Docker에서 PostgreSQL 컨테이너 자동 시작
    ↓
3. 컨테이너의 랜덤 포트 할당
    ↓
4. @DynamicPropertySource로 Spring 설정 주입
    ↓
5. 테스트 실행 (실제 PostgreSQL 사용)
    ↓
6. 테스트 종료 → 컨테이너 자동 정리
```

### 5.5 필수 요구사항

- **Docker가 설치되어 있어야 한다** (Docker Desktop 또는 Docker Engine)
- Testcontainers가 Docker daemon과 통신하여 컨테이너를 관리한다

---

## 6. Docker로 테스트 실행하기

### 6.1 로컬 실행

```bash
# 단위 테스트만 실행 (Docker 불필요)
./gradlew test --tests "*EntityTest"
./gradlew test --tests "*ServiceTest"

# Testcontainers 통합 테스트 실행 (Docker 필요)
./gradlew test --tests "*TestcontainersTest"

# 전체 테스트 실행
./gradlew test
```

### 6.2 Docker Compose로 개발 환경 구성

```bash
# PostgreSQL만 실행
docker-compose up -d db

# 애플리케이션 빌드 및 실행
docker-compose up --build
```

### 6.3 테스트 리포트 확인

```bash
# 테스트 실행 후 리포트 확인
./gradlew test

# HTML 리포트 경로
open build/reports/tests/test/index.html
```

---

## 7. 테스트 커버리지 (JaCoCo)

### 7.1 JaCoCo란?

JaCoCo(Java Code Coverage)는 **테스트가 소스 코드의 어느 부분을 실행했는지** 측정해주는 도구다.
"내 테스트가 실제로 얼마나 많은 코드를 검증하고 있는가?"를 수치로 보여준다.

이 챕터의 `build.gradle`에는 JaCoCo 플러그인이 적용되어 있다.

```groovy
plugins {
    id 'jacoco'
}

tasks.named('test') {
    useJUnitPlatform()
    finalizedBy jacocoTestReport   // 테스트가 끝나면 자동으로 리포트 생성
}

jacocoTestReport {
    dependsOn test                 // 리포트는 항상 test 실행 결과 기반
}
```

### 7.2 실행 방법

```bash
# 테스트 실행 후 커버리지 리포트 생성
./gradlew test jacocoTestReport

# (test 태스크에 finalizedBy가 걸려 있어 ./gradlew test 만 실행해도 리포트가 생성된다)
```

### 7.3 리포트 확인

HTML 리포트는 아래 경로에 생성된다.

```
build/reports/jacoco/test/html/index.html
```

```bash
# macOS 기준
open build/reports/jacoco/test/html/index.html
```

패키지/클래스/메서드 단위로 다음과 같은 지표를 보여준다.

- **라인 커버리지(Line Coverage)**: 실행된 코드 라인의 비율
- **브랜치 커버리지(Branch Coverage)**: 실행된 분기(if/else 등)의 비율

### 7.4 커버리지의 의미와 한계

| 구분 | 설명 |
|------|------|
| **의미** | 테스트가 코드를 얼마나 "실행"했는지 보여주는 지표 |
| **유용성** | 테스트가 아예 닿지 않은 코드(사각지대)를 발견하는 데 효과적 |

> **주의: 높은 커버리지 != 좋은 테스트**
>
> 커버리지는 코드를 **실행했는지**만 측정할 뿐, 그 결과를 **제대로 검증(assert)했는지**는 알지 못한다.
> 단언문 없이 메서드를 호출하기만 해도 커버리지는 올라간다.
> 따라서 커버리지는 "테스트가 부족한 곳"을 찾는 보조 지표로 활용하고,
> 100% 커버리지 자체를 목표로 삼지 않는 것이 좋다. 중요한 것은 **의미 있는 검증**이다.

---

## 8. 프로젝트 구조

```
chapter08-testing/
├── build.gradle
├── settings.gradle
├── Dockerfile
├── docker-compose.yml
├── src/
│   ├── main/
│   │   ├── java/com/edu/testing/
│   │   │   ├── Chapter08Application.java
│   │   │   ├── controller/
│   │   │   │   └── ProductController.java
│   │   │   ├── entity/
│   │   │   │   └── Product.java
│   │   │   ├── repository/
│   │   │   │   └── ProductRepository.java
│   │   │   └── service/
│   │   │       └── ProductService.java
│   │   └── resources/
│   │       └── application.yml
│   └── test/
│       ├── java/com/edu/testing/
│       │   ├── ProductEntityTest.java          ← 단위 테스트 (JUnit 5)
│       │   ├── ProductServiceTest.java         ← 단위 테스트 (Mockito)
│       │   ├── ProductControllerTest.java      ← 슬라이스 테스트 (@WebMvcTest + MockMvc)
│       │   └── ProductRepositoryTestcontainersTest.java ← 통합 테스트 (Testcontainers)
│       └── resources/
│           └── application.yml
└── README.md
```

---

## 9. 핵심 정리

| 개념 | 설명 |
|------|------|
| **단위 테스트** | 하나의 클래스/메서드를 격리하여 테스트. 빠르고 간단 |
| **Mockito** | 의존 객체를 가짜(Mock)로 대체하여 격리된 테스트 작성 |
| **Spring Boot Test Slice** | 필요한 레이어만 로드하여 테스트 속도 향상 |
| **@WebMvcTest + MockMvc** | Controller 레이어만 로드해 HTTP API를 빠르게 검증 (`@MockitoBean`으로 Service Mock) |
| **Testcontainers** | Docker로 실제 DB를 띄워서 신뢰도 높은 통합 테스트 |
| **Given-When-Then** | 테스트의 가독성을 높이는 BDD 스타일 패턴 |
| **JaCoCo 커버리지** | 테스트가 실행한 코드 비율 측정 (높은 수치 != 좋은 테스트) |

---

## 참고 자료
- [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/)
- [Mockito Documentation](https://javadoc.io/doc/org.mockito/mockito-core/latest/org.mockito/org/mockito/Mockito.html)
- [Testcontainers Documentation](https://testcontainers.com/)
- [Spring Boot Testing](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.testing)
