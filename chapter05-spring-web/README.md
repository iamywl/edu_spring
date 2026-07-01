# Chapter 05: Spring Web MVC - REST API

## 학습 목표
- REST API의 개념과 설계 원칙을 이해한다
- Spring Web MVC를 사용하여 REST API를 구현한다
- 요청/응답 처리, 유효성 검증, 예외 처리를 학습한다
- Swagger(OpenAPI)로 API 문서를 자동 생성하고, CORS를 설정한다
- Docker를 사용하여 애플리케이션을 컨테이너로 실행한다

---

## 1. REST API 개념

### REST (Representational State Transfer)란?
REST는 웹 서비스를 설계하기 위한 아키텍처 스타일이다. 리소스(Resource)를 URI로 식별하고, HTTP 메서드를 통해 리소스에 대한 행위를 정의한다.

### REST의 핵심 원칙
| 원칙 | 설명 |
|------|------|
| **클라이언트-서버 분리** | 클라이언트와 서버가 독립적으로 발전할 수 있다 |
| **무상태(Stateless)** | 각 요청은 독립적이며, 서버는 클라이언트의 상태를 저장하지 않는다 |
| **균일한 인터페이스** | URI로 리소스를 식별하고, HTTP 메서드로 행위를 정의한다 |
| **계층 구조** | 클라이언트는 중간 서버의 존재를 알 필요가 없다 |

### RESTful URL 설계 예시
```
GET    /api/todos          → 전체 할일 목록 조회
GET    /api/todos/1        → ID가 1인 할일 조회
POST   /api/todos          → 새로운 할일 생성
PUT    /api/todos/1        → ID가 1인 할일 수정
DELETE /api/todos/1        → ID가 1인 할일 삭제
```

---

## 2. @RestController vs @Controller

### @Controller
전통적인 MVC 패턴에서 **뷰(View)**를 반환한다. 주로 서버 사이드 렌더링(Thymeleaf 등)에 사용된다.

```java
@Controller
public class PageController {
    @GetMapping("/hello")
    public String hello(Model model) {
        model.addAttribute("name", "World");
        return "hello";  // hello.html 뷰를 반환
    }
}
```

### @RestController
`@Controller` + `@ResponseBody`의 조합이다. 메서드의 반환값이 **JSON/XML 등의 데이터**로 직접 응답 본문에 작성된다.

```java
@RestController
@RequestMapping("/api")
public class ApiController {
    @GetMapping("/hello")
    public Map<String, String> hello() {
        return Map.of("message", "Hello World");
        // {"message": "Hello World"} JSON으로 자동 변환
    }
}
```

> **핵심 차이**: `@Controller`는 뷰 이름을 반환하고, `@RestController`는 데이터를 직접 반환한다.

---

## 3. HTTP 메서드 매핑

Spring은 각 HTTP 메서드에 대응하는 애노테이션을 제공한다.

| 애노테이션 | HTTP 메서드 | 용도 |
|------------|-------------|------|
| `@GetMapping` | GET | 리소스 조회 |
| `@PostMapping` | POST | 리소스 생성 |
| `@PutMapping` | PUT | 리소스 전체 수정 |
| `@PatchMapping` | PATCH | 리소스 부분 수정 |
| `@DeleteMapping` | DELETE | 리소스 삭제 |

```java
@RestController
@RequestMapping("/api/todos")  // 공통 경로 지정
public class TodoController {

    @GetMapping           // GET /api/todos
    public List<Todo> list() { ... }

    @GetMapping("/{id}")  // GET /api/todos/1
    public Todo get(@PathVariable Long id) { ... }

    @PostMapping          // POST /api/todos
    public Todo create(@RequestBody TodoRequest request) { ... }

    @PutMapping("/{id}")  // PUT /api/todos/1
    public Todo update(@PathVariable Long id, @RequestBody TodoRequest request) { ... }

    @DeleteMapping("/{id}")  // DELETE /api/todos/1
    public void delete(@PathVariable Long id) { ... }
}
```

---

## 4. 요청/응답 처리

### @RequestBody
HTTP 요청 본문(Body)의 JSON 데이터를 Java 객체로 변환한다.

```java
@PostMapping("/api/todos")
public TodoResponse create(@RequestBody TodoRequest request) {
    // JSON → TodoRequest 객체로 자동 역직렬화
    return todoService.createTodo(request);
}
```

### @PathVariable
URL 경로에 포함된 변수를 추출한다.

```java
@GetMapping("/api/todos/{id}")
public TodoResponse get(@PathVariable Long id) {
    // /api/todos/42 → id = 42
    return todoService.getTodoById(id);
}
```

### @RequestParam
쿼리 파라미터(URL의 `?key=value`)를 추출한다.

```java
@GetMapping("/api/todos")
public List<TodoResponse> getTodos(
    @RequestParam(required = false) Boolean completed,  // ?completed=true
    @RequestParam(required = false) String keyword      // ?keyword=spring
) {
    // /api/todos?completed=true&keyword=spring
    return todoService.search(completed, keyword);
}
```

### @PathVariable vs @RequestParam

| 구분 | @PathVariable | @RequestParam |
|------|---------------|---------------|
| 위치 | 경로 일부 `/api/todos/{id}` | 쿼리 문자열 `?completed=true` |
| 용도 | 특정 리소스 식별 | 필터/검색/정렬/페이징 조건 |
| 예시 | `GET /api/todos/1` | `GET /api/todos?completed=true` |

본 프로젝트의 `TodoController.getTodos()`에서 `@RequestParam` 기반 검색/필터를, `getTodoById()`에서 `@PathVariable` 기반 단건 조회를 직접 비교할 수 있다.

```bash
# 완료된 할일만 조회
curl "http://localhost:8080/api/todos?completed=true"

# 제목에 "spring"이 포함된 할일만 조회
curl "http://localhost:8080/api/todos?keyword=spring"
```

---

## 5. DTO 패턴

DTO(Data Transfer Object)는 계층 간 데이터 전송을 위한 객체이다. Java 16+의 `record`를 사용하면 간결하게 정의할 수 있다.

### 왜 DTO를 사용하는가?
1. **보안**: 내부 엔티티를 직접 노출하지 않는다
2. **유연성**: API 스펙과 내부 모델을 독립적으로 변경할 수 있다
3. **검증**: 요청 DTO에 유효성 검증 규칙을 적용한다

```java
// 요청 DTO - 클라이언트가 보내는 데이터
public record TodoRequest(
    @NotBlank String title,
    String description,
    boolean completed
) {}

// 응답 DTO - 클라이언트에게 보내는 데이터
public record TodoResponse(
    Long id,
    String title,
    String description,
    boolean completed,
    LocalDateTime createdAt
) {}
```

---

## 6. 응답 코드와 ResponseEntity

### HTTP 상태 코드
| 코드 | 의미 | 사용 예 |
|------|------|---------|
| 200 OK | 요청 성공 | 조회 성공 |
| 201 Created | 리소스 생성 완료 | 새 항목 등록 |
| 204 No Content | 성공, 응답 본문 없음 | 삭제 완료 |
| 400 Bad Request | 잘못된 요청 | 유효성 검증 실패 |
| 404 Not Found | 리소스 없음 | 존재하지 않는 ID 조회 |
| 500 Internal Server Error | 서버 내부 오류 | 예상치 못한 에러 |

### ResponseEntity 사용법
`ResponseEntity`를 사용하면 HTTP 상태 코드, 헤더, 본문을 세밀하게 제어할 수 있다.

```java
// 201 Created와 함께 생성된 리소스 반환
@PostMapping
public ResponseEntity<TodoResponse> create(@RequestBody TodoRequest request) {
    TodoResponse created = todoService.createTodo(request);
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(created);
}

// 204 No Content (삭제 시)
@DeleteMapping("/{id}")
public ResponseEntity<Void> delete(@PathVariable Long id) {
    todoService.deleteTodo(id);
    return ResponseEntity.noContent().build();
}
```

---

## 7. 전역 예외 처리 (@ControllerAdvice)

`@ControllerAdvice`와 `@ExceptionHandler`를 사용하면 컨트롤러 전체에서 발생하는 예외를 한 곳에서 처리할 수 있다.

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 커스텀 예외 처리
    @ExceptionHandler(TodoNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(TodoNotFoundException e) {
        ErrorResponse error = new ErrorResponse("NOT_FOUND", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    // 유효성 검증 실패 처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
            .map(err -> err.getField() + ": " + err.getDefaultMessage())
            .collect(Collectors.joining(", "));
        ErrorResponse error = new ErrorResponse("VALIDATION_ERROR", message);
        return ResponseEntity.badRequest().body(error);
    }
}
```

### 장점
- 컨트롤러 코드가 깔끔해진다 (try-catch 불필요)
- 일관된 에러 응답 형식을 보장한다
- 예외 처리 로직을 한 곳에서 관리한다

---

## 8. Validation (@Valid)

`spring-boot-starter-validation` 의존성을 추가하면 Bean Validation을 사용할 수 있다.

### 주요 검증 애노테이션
| 애노테이션 | 설명 |
|------------|------|
| `@NotBlank` | null이 아니고, 공백이 아닌 문자열 |
| `@NotNull` | null이 아닌 값 |
| `@Size(min, max)` | 문자열 길이 또는 컬렉션 크기 |
| `@Min`, `@Max` | 숫자 최솟값, 최댓값 |
| `@Email` | 이메일 형식 |
| `@Pattern(regexp)` | 정규표현식 매칭 |

### 적용 방법
```java
// DTO에 검증 규칙 정의
public record TodoRequest(
    @NotBlank(message = "제목은 필수입니다")
    @Size(max = 100, message = "제목은 100자 이하입니다")
    String title,
    String description,
    boolean completed
) {}

// 컨트롤러에서 @Valid 적용
@PostMapping
public ResponseEntity<TodoResponse> create(@Valid @RequestBody TodoRequest request) {
    // 유효성 검증 실패 시 MethodArgumentNotValidException 발생
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(todoService.createTodo(request));
}
```

---

## 9. Swagger / OpenAPI (API 문서 자동화)

API 명세를 코드와 따로 문서로 관리하면 금방 불일치가 생긴다. Swagger(OpenAPI)는 코드의 애노테이션을 읽어 **API 문서와 테스트 UI를 자동 생성**한다.

### 의존성 추가

```gradle
implementation 'org.springdoc:springdoc-openapi-starter-webmvc-ui:2.7.0'
```

의존성만 추가하면 별도 설정 없이 다음 엔드포인트가 자동 생성된다.

| 엔드포인트 | 설명 |
|------------|------|
| `/swagger-ui.html` | 브라우저에서 API를 보고 직접 호출해보는 UI |
| `/v3/api-docs` | OpenAPI 명세(JSON) |

### 문서화 애노테이션

| 애노테이션 | 대상 | 설명 |
|------------|------|------|
| `@Tag` | 컨트롤러 클래스 | API 그룹 이름/설명 |
| `@Operation` | 메서드 | 각 API의 요약/설명 |
| `@ApiResponse` | 메서드 | 응답 코드별 의미 (200, 404 등) |
| `@Parameter` | 파라미터 | 파라미터 설명 |

```java
@Tag(name = "Todo API", description = "할일 관리 REST API")
@RestController
@RequestMapping("/api/todos")
public class TodoController {

    @Operation(summary = "할일 단건 조회", description = "ID로 특정 할일을 조회한다")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "404", description = "해당 ID의 할일이 없음")
    })
    @GetMapping("/{id}")
    public ResponseEntity<TodoResponse> getTodoById(@PathVariable Long id) { ... }
}
```

### 접속 방법

애플리케이션 실행 후 브라우저에서 아래 주소로 접속한다.

```
http://localhost:8080/swagger-ui.html
```

화면에서 각 API를 펼쳐 "Try it out" 버튼으로 직접 요청을 보내볼 수 있다.

---

## 10. CORS 설정

### CORS(Cross-Origin Resource Sharing)란?
브라우저는 보안을 위해 **다른 출처(origin)** 로의 요청을 기본적으로 차단한다. 출처는 `프로토콜 + 호스트 + 포트`로 구성된다.

```
프론트엔드 http://localhost:3000  →  백엔드 API http://localhost:8080
                                     (포트가 달라 "다른 출처" → 브라우저가 차단)
```

서버가 "이 출처는 허용한다"고 응답 헤더로 알려주면 브라우저가 요청을 통과시킨다.

### WebMvcConfigurer로 전역 설정
`WebMvcConfigurer`를 구현하고 `addCorsMappings()`를 오버라이드하여 전역 CORS 정책을 정의한다.

```java
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")                   // 적용 경로
                .allowedOrigins("http://localhost:3000")  // 허용 출처(개발용 프론트엔드)
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .allowedHeaders("*")
                .allowCredentials(true)                   // 쿠키 등 인증 정보 허용
                .maxAge(3600);                            // preflight 캐시(초)
    }
}
```

> 운영 환경에서는 `allowedOrigins("*")`처럼 모든 출처를 허용하지 말고, 신뢰할 수 있는 출처만 명시하는 것이 안전하다.

---

## 11. Docker로 실행하기

### 프로젝트 빌드 및 Docker 실행

```bash
# 1. Gradle로 JAR 빌드
./gradlew clean bootJar

# 2. Docker Compose로 실행
docker compose up --build

# 3. API 테스트
# 할일 생성
curl -X POST http://localhost:8080/api/todos \
  -H "Content-Type: application/json" \
  -d '{"title": "Spring 학습", "description": "Chapter 05 실습", "completed": false}'

# 전체 조회
curl http://localhost:8080/api/todos

# 단건 조회
curl http://localhost:8080/api/todos/1

# 수정
curl -X PUT http://localhost:8080/api/todos/1 \
  -H "Content-Type: application/json" \
  -d '{"title": "Spring 학습 완료", "description": "Chapter 05 실습 완료", "completed": true}'

# 삭제
curl -X DELETE http://localhost:8080/api/todos/1
```

### Dockerfile (멀티 스테이지 빌드)
멀티 스테이지 빌드를 사용하면 빌드 도구 없이 최종 이미지에 실행 파일만 포함시킬 수 있다.

```dockerfile
# 1단계: 빌드
FROM eclipse-temurin:21-jdk AS builder
COPY . /app
WORKDIR /app
RUN ./gradlew clean bootJar

# 2단계: 실행
FROM eclipse-temurin:21-jre
COPY --from=builder /app/build/libs/*.jar /app/app.jar
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
```

---

## 프로젝트 구조

```
chapter05-spring-web/
├── build.gradle
├── settings.gradle
├── Dockerfile
├── docker-compose.yml
├── gradle/wrapper/
│   └── gradle-wrapper.properties
└── src/main/
    ├── java/com/edu/web/
    │   ├── Chapter05Application.java          ← 메인 클래스
    │   ├── config/
    │   │   └── WebConfig.java                 ← 전역 CORS 설정
    │   ├── controller/
    │   │   ├── TodoController.java            ← REST 컨트롤러 (+ Swagger 문서화)
    │   │   └── GlobalExceptionHandler.java    ← 전역 예외 처리
    │   ├── dto/
    │   │   ├── TodoRequest.java               ← 요청 DTO
    │   │   ├── TodoResponse.java              ← 응답 DTO
    │   │   └── ErrorResponse.java             ← 에러 응답 DTO
    │   ├── exception/
    │   │   └── TodoNotFoundException.java     ← 커스텀 예외
    │   └── service/
    │       └── TodoService.java               ← 비즈니스 로직
    └── resources/
        └── application.yml                    ← 설정 파일
```

---

## 핵심 정리

1. **@RestController**는 JSON 데이터를 직접 반환하는 컨트롤러이다
2. **HTTP 메서드**에 맞는 매핑 애노테이션을 사용하여 RESTful API를 설계한다
3. **DTO 패턴**으로 요청과 응답 데이터를 분리한다
4. **ResponseEntity**로 HTTP 상태 코드를 명시적으로 제어한다
5. **@ControllerAdvice**로 예외를 전역적으로 처리하여 일관된 에러 응답을 제공한다
6. **@Valid**와 Bean Validation으로 입력 데이터의 유효성을 검증한다
7. **@RequestParam**은 검색/필터 조건에, **@PathVariable**은 리소스 식별에 사용한다
8. **Swagger/OpenAPI**로 API 문서와 테스트 UI를 자동 생성한다 (`/swagger-ui.html`)
9. **CORS** 설정으로 다른 출처의 프론트엔드가 API를 호출할 수 있게 허용한다
10. **Docker 멀티 스테이지 빌드**로 최적화된 컨테이너 이미지를 생성한다
