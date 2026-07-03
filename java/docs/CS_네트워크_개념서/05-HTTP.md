# 5장. 응용 계층 HTTP — 텍스트로 나누는 대화

> "SPRING §3.1은 HTTP '메서드와 상태 코드'를 설명했다. 여기서는 그 요청/응답이 **바이트 수준에서 실제로 어떻게 생겼는지**, 그리고 연결·상태·캐시가 어떻게 얽히는지를 본다."

← [4장. DNS](04-DNS.md) | 다음 장 → [6장. HTTPS/TLS](06-HTTPS-TLS.md)

> **🐳 실습 환경 — 이 장의 Java 데모는 `java-sandbox` 컨테이너에서 실행한다** (인터넷 접속 필요)
> ```bash
> cd java && docker compose up -d              # 컨테이너 켜기 (이미 떠 있으면 생략)
> docker exec -it java-sandbox ./run.sh RawHttpClient
> ```
> 셸 관찰 실습은 호스트에서: `bash java/chapter-cs-network/labs/observe_http.sh`

---

## 도입 — API 위가 아니라 API의 바이트

지금까지 우리는 밑바닥부터 올라왔다. 링크·IP(2장)로 목적지를 찾고, TCP(3장)로 신뢰할 수 있는 관을 뚫고, DNS(4장)로 이름을 IP로 바꿨다. 이제 그 관 위로 흐르는 **실제 대화의 언어**, HTTP를 본다.

SPRING §3.1은 HTTP를 "편지 같다"고, 메서드와 상태 코드를 다뤘다. 이 장은 **그 편지의 실제 글자**를 본다. 놀랍게도 HTTP는 사람이 읽을 수 있는 평범한 텍스트다. 그리고 이 "텍스트 대화"를 여러 파일에 대해 어떻게 효율적으로 나르는지(HTTP/1.1·2·3), "기억 못 하는" HTTP 위에 어떻게 로그인 상태를 얹는지(쿠키/세션), 같은 것을 두 번 안 받게 하는 장치(캐싱)를 다룬다.

---

## 5.1 요청과 응답의 해부 (실제 바이트)

### 개념 — HTTP는 사람이 읽는 텍스트다

SPRING §3.1이 "HTTP는 편지 같다"고 했다. 그 편지의 **실제 글자**는 이렇게 생겼다.

```
  ── HTTP 요청(Request) ──────────────────────────────
  GET /users/1 HTTP/1.1              ← 요청 라인: [메서드] [경로] [버전]
  Host: api.example.com              ┐
  Accept: application/json           │ 헤더(header): "이름: 값" 여러 줄
  Authorization: Bearer eyJ...       │
  Connection: keep-alive             ┘
                                     ← 빈 줄(CRLF) = 헤더 끝 신호 ★
  (GET은 보통 바디 없음)               ← 바디(body): POST/PUT이면 여기에 데이터

  ── HTTP 응답(Response) ─────────────────────────────
  HTTP/1.1 200 OK                    ← 상태 라인: [버전] [상태코드] [설명]
  Content-Type: application/json     ┐
  Content-Length: 34                 │ 헤더
  Cache-Control: max-age=60          ┘
                                     ← 빈 줄(CRLF)
  {"id":1,"name":"김철수","age":28}    ← 바디(실제 데이터)
```

### 원리 — 빈 줄이 헤더와 바디를 가른다

**★ 핵심: 빈 줄(`\r\n\r\n`)이 헤더와 바디를 가른다.** 서버는 이 빈 줄을 만나기 전까지를 헤더로, 이후를 바디로 해석한다. 그리고 각 줄의 끝은 `\r\n`(CRLF)이다. 이것을 알아야 §RawHttpClient에서 왜 `"\r\n\r\n"`을 손으로 써주는지 이해된다.

그러면 "바디가 어디서 끝나는지"는 어떻게 아는가? 두 가지 방법이 있다.

```
  ① Content-Length: 34   →  "바디는 정확히 34바이트다" (길이를 미리 안다)
  ② Transfer-Encoding: chunked  →  길이를 모를 때, 조각(chunk)마다 크기를 앞에 붙여
                                    보내고 크기 0인 조각으로 끝을 알린다(스트리밍)
```

### 예시 — 요청 라인과 Host 헤더

- **요청 라인**의 세 조각(메서드/경로/버전)이 SPRING §3.1의 "무엇을/어디로"에 정확히 대응한다.
- **`Host` 헤더가 왜 필수인가?** 한 IP의 한 서버(한 443 포트)가 `a.com`, `b.com` 수백 개 사이트를 호스팅한다(가상 호스팅). 서버는 `Host` 헤더를 보고 "이 요청은 어느 사이트 것"인지 안다. 그래서 HTTP/1.1에서 `Host`는 생략 불가다. (HTTPS에서는 TLS 단계의 SNI가 같은 역할을 암호화 전에 한다 — §6에서 다룸.)

### 실습 연결

> **실습으로 확인**: `RawHttpClient.java`가 바로 이 요청 텍스트를 손으로 조립해 소켓에 쓴다(`"GET / HTTP/1.1\r\nHost: ...\r\nConnection: close\r\n\r\n"`). 그리고 서버가 돌려주는 응답 텍스트(상태 라인 + 헤더 + 바디)를 그대로 출력한다. `./run.sh RawHttpClient`로 실행하면 "HTTP = TCP 위의 텍스트"임을 두 눈으로 확인할 수 있다. 같은 것을 `observe_http.sh`의 STEP 4가 `nc`로 평문 80 포트에 요청 원문을 흘려보내 보여준다(요청 헤더는 `> `, 응답 헤더는 `< `로 표시).

---

## 5.2 연결 재사용 — keep-alive(HTTP/1.1), 멀티플렉싱(HTTP/2), QUIC(HTTP/3)

### 개념 — 한 페이지엔 파일이 수십 개다

한 웹페이지엔 HTML 1개, CSS 몇 개, 이미지 수십 개, JS 여러 개가 있다. 이걸 어떻게 실어 나르는가 — 여기서 HTTP 버전이 갈린다. 각 버전은 앞 버전의 병목을 푼 역사다.

### 원리 — 버전별 진화

**HTTP/1.0**: 요청 하나마다 TCP 연결을 새로 맺고 끊었다. §3.3의 3-way handshake를 **파일마다** 반복 — 끔찍하게 느리다.

**HTTP/1.1 — keep-alive(지속 연결):** 한 번 맺은 TCP 연결을 **끊지 않고 재사용**한다. 악수 비용을 크게 아낀다. 하지만 한계가 있다: 한 연결에서 **요청을 하나씩 순서대로** 처리한다. 앞의 느린 응답이 뒤를 막는 **HOL(Head-of-Line) 블로킹**이 생긴다.

```
  HTTP/1.1 (한 연결, 순차):
    연결 ─[요청A]→ ←[응답A]─ [요청B]→ ←[응답B]─ ...   앞이 막히면 뒤가 대기
    (그래서 브라우저는 연결을 6개쯤 동시에 열어 회피했다)

  HTTP/2 (한 연결, 멀티플렉싱):
    연결 ─[A][B][C] 동시 전송→   ←[C][A][B] 뒤섞여 도착─
    하나의 TCP 연결 안에서 여러 '스트림'이 병렬로 오간다. 앞이 뒤를 안 막음.
```

**HTTP/2 — 멀티플렉싱(multiplexing):** 하나의 TCP 연결 안에서 요청/응답을 **작은 조각(프레임)으로 잘게 나눠 뒤섞어** 병렬로 보낸다. 받는 쪽이 조각을 스트림 번호로 재조립한다. 덕분에 연결 하나로 수십 개 요청을 동시에 처리하고, 헤더도 압축(HPACK)한다. **주소창에 https를 치면 요즘 대부분 HTTP/2로 협상**된다(§6 TLS 핸드셰이크 중 ALPN으로 정해진다).

**HTTP/3 — QUIC(UDP 기반):** HTTP/2는 응용 계층의 HOL 블로킹은 풀었지만, **TCP 자체의 HOL 블로킹**이 남았다. 한 연결의 어느 한 패킷이 유실되면 TCP가 그것을 재전송해 순서를 맞출 때까지 뒤 스트림 전부가 대기한다(§3.4의 순서 보장이 여기선 걸림돌). HTTP/3는 **TCP 대신 UDP 기반의 QUIC**를 써서, 스트림마다 독립적으로 유실을 처리한다. 즉 UDP 위에서 신뢰성·순서·혼잡 제어를 **응용 계층이 다시 구현**한다(§3.2에서 예고한 그 방식). 게다가 QUIC은 연결 수립과 TLS 핸드셰이크를 합쳐 첫 왕복 비용도 줄인다.

```
  HTTP/1.1  →  TCP,  순차(keep-alive), 응용·전송 HOL 블로킹 모두 있음
  HTTP/2    →  TCP,  멀티플렉싱+헤더압축, 응용 HOL은 해결·TCP HOL 잔존
  HTTP/3    →  QUIC(UDP), 스트림 독립·핸드셰이크 통합, TCP HOL까지 해결
```

### 예시 — "첫 요청 vs 이후"의 큰 그림

첫 요청은 (DNS §4.3) + (TCP handshake §3.3) + (TLS handshake §6.2)를 다 치른다. keep-alive/멀티플렉싱/세션 재사용 덕분에 이후 요청은 이 비싼 준비를 건너뛴다. 그래서 버전 진화의 방향은 한결같다: **비싼 왕복을 줄이고, 한 연결을 최대한 나눠 쓰는 것.**

---

## 5.3 상태 없음(Stateless)과 쿠키/세션

### 개념 — HTTP는 기억하지 않는다

**HTTP는 상태가 없다(stateless).** 서버는 각 요청을 **독립적으로** 처리하고, 방금 누가 왔었는지 **기억하지 않는다.**

### 원리 — 왜 상태가 없게 설계했나, 그리고 그 대가

그래야 서버를 여러 대로 쉽게 늘릴 수 있다(확장성). 요청마다 자기완결적이면 어느 서버가 처리하든 상관없다. 이는 §4.1에서 "이름 하나에 여러 서버"를 두는 것과 같은 확장성 사고다. 하지만 문제가 생긴다 — **로그인하면 그다음 요청부터 "나 아까 로그인한 그 사람이야"를 어떻게 아나?**

해결책이 **쿠키(cookie)**다.

```
  ① 로그인 요청 ──────────────────────────────→ 서버
  ② ←── 응답 헤더:  Set-Cookie: session=abc123   (서버: "이 팔찌 차고 다녀")
  ③ 이후 모든 요청 헤더:  Cookie: session=abc123   (브라우저가 자동 첨부)
  ④ 서버는 abc123을 보고 "아, 그 사용자!" 하고 알아본다
```

### 예시 — 세션 방식 vs 토큰 방식

- **세션(Session) 방식**: 서버가 `abc123 → 사용자 정보`를 **서버 메모리/DB에 저장**하고, 쿠키엔 열쇠(session id)만 담는다. 서버가 상태를 갖는다.
- **토큰(JWT) 방식**: 상태를 서버에 안 두고 **토큰 자체에 정보를 담아** 서명한다. 서버는 서명만 검증하면 되니 stateless를 유지한다.

이 세션 vs 토큰의 **깊은 비교, 그리고 JWT 서명(HMAC/대칭·비대칭)**은 SPRING §5.3과 §5.7에서 이미 다뤘다. 여기서는 "**stateless한 HTTP 위에 상태를 얹는 장치가 쿠키/세션/토큰**"이라는 **네트워크 층위의 위치**만 확인하면 된다. → 자세히는 SPRING §5.3, §5.7 참조. (그리고 그 토큰 서명의 대칭/비대칭 원리는 이 책 [6장 §6.3](06-HTTPS-TLS.md)의 CA 서명과 정확히 같은 논리다.)

---

## 5.4 캐싱 헤더 — 두 번은 안 물어본다

### 개념 — 안 바뀌는 걸 또 받지 말자

같은 자원(로고 이미지, CSS)을 매번 서버에서 다시 받는 건 낭비다. HTTP는 **캐싱 헤더**로 "이건 얼마간 재사용해도 돼"를 표현한다.

### 원리 — 만료 기반 캐시와 재검증

```
  Cache-Control: max-age=3600    → "3600초(1시간)간 다시 묻지 말고 재사용해"
  ETag: "v3-abc"                 → 자원의 지문(버전표). 바뀌면 지문이 바뀜.
  Last-Modified: (날짜)           → 마지막 수정 시각

  재검증(revalidation):
    브라우저 → If-None-Match: "v3-abc"  ("나 v3 갖고 있는데 아직 유효해?")
    서버 →  304 Not Modified            ("안 바뀌었어. 갖고 있는 거 써")  ← 바디 없음 = 절약!
```

두 층위가 있다. `max-age`가 살아 있으면 **아예 서버에 묻지 않고** 캐시를 쓴다(가장 빠름). 만료됐으면 `ETag`/`Last-Modified`로 **"바뀌었는지만" 가볍게 확인**하고, 안 바뀌었으면 서버가 `304 Not Modified`(바디 없이)로 답해 큰 바디 재전송을 아낀다.

### 예시 — DNS TTL과 같은 발상

**왜 중요한가?** DNS의 TTL(§4.3)과 발상이 같다 — **"바뀌지 않는 것을 매번 다시 가져오지 말자."** DNS TTL, HTTP `max-age`, TCP 재전송 타이머 모두 "동적으로 조정되는 유효기간"이라는 같은 사고의 변주다. 이것이 웹이 빠른 큰 이유 중 하나다.

### 실습 연결

> **실습으로 확인**: `observe_http.sh`의 STEP 2(`curl -v`) 응답 헤더 블록("[라) 서버가 보낸 응답 헤더]", `< `로 시작하는 줄들)에서 `Cache-Control`, `ETag`, `Content-Type` 등 이 절의 헤더들이 실제로 찍히는 것을 볼 수 있다.

---

## 5.5 자바 표준 `HttpClient` — 소켓 직접 조립 vs 표준 클라이언트

### 개념 — RawHttpClient 다음 단계

`RawHttpClient`로 우리는 소켓에 `"GET / HTTP/1.1\r\n..."`을 손으로 써 봤다 — HTTP의 정체를 아는 데는
최고의 방법이지만, 실무에서 매번 그렇게 하지는 않는다. Java 11부터 표준 라이브러리에 들어온
**`java.net.http.HttpClient`**가 그 모든 밑작업을 대신한다.

| | `RawHttpClient` (5.1절, 교육용) | `HttpClient` (실무용, Java 11+) |
|---|---|---|
| 연결 | `new Socket(host, 80)` 직접 | 커넥션 관리(keep-alive/풀) 자동 |
| 요청 | CRLF 텍스트 손으로 조립 | `HttpRequest` 빌더로 선언 |
| 응답 | 상태 라인/헤더/바디 직접 파싱 | `HttpResponse`가 상태·헤더·바디 제공 |
| 암호화 | 평문 80 포트만 | HTTPS(TLS, §6) 자동 |
| 버전 | HTTP/1.1 고정 | **HTTP/2 협상(ALPN, §5.2)** 자동 |
| 방식 | 동기만 | 동기 `send` + 비동기 `sendAsync`(`CompletableFuture`) |

둘을 **같은 층위의 대체재로 보면 안 된다.** RawHttpClient는 "밑에서 무슨 일이 벌어지는지"를 보여주고,
HttpClient는 그 일을 "안 보이게 잘" 해 준다. 순서대로 배우면 HttpClient의 옵션 하나하나
(`connectTimeout`은 §3.3 handshake의 한도, `version(HTTP_2)`는 §5.2의 협상 희망)가 무엇인지 보인다.

### 자바 코드 — 골격

```java
HttpClient client = HttpClient.newBuilder()
        .version(HttpClient.Version.HTTP_2)      // HTTP/2 희망 (안 되면 1.1로 자동 강등)
        .connectTimeout(Duration.ofSeconds(5))   // TCP 연결 수립 한도 (§3.3)
        .build();                                // 한 번 만들어 재사용 (내부에 커넥션 풀)

HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create("https://httpbin.org/get"))
        .timeout(Duration.ofSeconds(8))          // 이 요청의 응답 대기 한도
        .header("Accept", "application/json")
        .GET()                                    // POST(BodyPublishers.ofString(json)) 도 가능
        .build();

HttpResponse<String> res = client.send(request, HttpResponse.BodyHandlers.ofString());
res.statusCode();   // 200
res.version();      // HTTP_2  ← ALPN 협상 결과를 여기서 확인!
```

### 실습 연결

> **실습으로 확인**: `./run.sh HttpClientExample` (인터넷 필요; 오프라인이면 친절한 안내 후 정상 종료)
> ① GET — 상태 코드와 함께 **협상된 버전이 `HTTP_2`로 찍히는 것**을 확인한다(§5.2·§6의 ALPN이
> 실제로 일어났다는 증거). ② POST — JSON 바디와 `Content-Type` 헤더를 싣고, httpbin.org 가 받은 것을
> 그대로 메아리쳐 주는 응답을 본다. ③ `sendAsync` — 두 요청을 동시에 던지고 `CompletableFuture`로
> 취합해, 총 소요가 순차(2배)가 아님을 확인한다(운영체제 개념서 7장의 비동기 조합이 실전 HTTP에
> 그대로 쓰인다). RawHttpClient를 먼저 실행해 두 출력을 비교해 보라 — 같은 프로토콜, 다른 추상화 수준.

### 확인문제

1. `RawHttpClient`와 `HttpClientExample`은 같은 HTTP 프로토콜을 쓴다. 그런데 전자는 응답이 항상
   HTTP/1.1이고 후자는 대개 HTTP/2다. 이 차이는 어디서 오는가?
   <details><summary>정답</summary>
   HTTP/2 협상은 TLS 핸드셰이크의 ALPN 확장에서 일어난다(§5.2, §6). RawHttpClient는 평문 80 포트에
   HTTP/1.1 텍스트를 직접 쓰므로 협상 자체가 없다. HttpClient는 HTTPS(443)로 TLS를 맺으며 ALPN으로
   "h2 가능?"을 묻고, 서버가 동의하면 HTTP/2 바이너리 프레이밍으로 통신한다.</details>

2. `HttpClient.newBuilder().connectTimeout(...)`과 `HttpRequest.newBuilder().timeout(...)`은 각각
   네트워크의 어느 단계에 대한 한도인가?
   <details><summary>정답</summary>
   connectTimeout은 TCP 연결 수립(3-way handshake, §3.3)이 끝날 때까지의 한도 — 클라이언트 전체
   설정이다. request의 timeout은 연결 이후 "이 요청의 응답이 도착할 때까지"의 한도 — 요청별 설정이다.
   둘 다 없으면 상대가 침묵할 때 프로그램이 hang 할 수 있다.</details>

3. `sendAsync`가 돌려주는 것은 무엇이며, 독립적인 API 두 개를 호출할 때 `send` 두 번 대신
   `sendAsync` 두 개 + `allOf`를 쓰면 시간이 어떻게 달라지는가?
   <details><summary>정답</summary>
   CompletableFuture&lt;HttpResponse&gt;를 즉시 돌려주고 요청은 백그라운드에서 진행된다. send 두 번은
   순차라 t1+t2 걸리지만, sendAsync 두 개를 먼저 던지고 allOf로 취합하면 max(t1, t2)만 걸린다.
   → 운영체제 개념서 7장 §7.3(CompletableFuture)의 조합 기법 그대로.</details>

---

## ⚠️ 흔한 오해와 함정

- **"HTTP는 바이너리라 사람이 못 읽는다."** HTTP/1.x는 평문 텍스트라 사람이 읽는다. HTTP/2·3부터 바이너리 프레이밍을 쓰지만, 의미(메서드·헤더·바디)는 동일하다.
- **"헤더와 바디는 어떤 특수 문자로 구분된다."** 특수 문자가 아니라 **빈 줄(`\r\n\r\n`)**이다. 그리고 각 줄 끝은 CRLF(`\r\n`)다.
- **"keep-alive면 요청을 병렬로 보낸다."** 아니다. HTTP/1.1 keep-alive는 **연결 재사용**일 뿐 여전히 순차(HOL 블로킹)다. 병렬은 HTTP/2 멀티플렉싱부터다.
- **"HTTP/2면 무조건 HTTP/1.1보다 빠르다."** 대개 그렇지만, 패킷 유실이 잦은 회선에선 TCP HOL 블로킹 때문에 체감이 나쁠 수 있다. 그래서 HTTP/3(QUIC)가 나왔다.
- **"쿠키는 서버가 사용자를 기억하는 것이다."** HTTP 자체는 여전히 stateless다. 쿠키는 "상태를 매 요청에 실어 나르는" 우회 장치다. 상태를 서버에 둘지(세션) 토큰에 둘지(JWT)는 설계 선택이다.
- **"304 응답에도 바디가 온다."** 아니다. `304 Not Modified`는 바디가 없다. 바로 그 점이 절약의 핵심이다.

---

## 연습문제

1. **HTTP 요청 메시지에서 헤더와 바디를 나누는 것은 무엇인가? 각 줄의 끝 문자는?**
   - *힌트*: RawHttpClient가 손으로 써주는 그것.
   - *해설*: 빈 줄(`\r\n\r\n`)이 헤더 끝 신호. 각 줄 끝은 CRLF(`\r\n`).

2. **HTTP/1.1에서 `Host` 헤더가 필수인 이유는?**
   - *힌트*: 한 IP·한 포트에 여러 사이트.
   - *해설*: 한 서버(IP:443)가 여러 도메인을 가상 호스팅하므로, 서버는 `Host` 헤더로 어느 사이트에 대한 요청인지 판단한다.

3. **HTTP/1.1 keep-alive와 HTTP/2 멀티플렉싱의 차이는 무엇인가?**
   - *힌트*: 재사용 vs 병렬.
   - *해설*: keep-alive는 한 TCP 연결을 재사용하지만 요청은 순차(HOL 블로킹). 멀티플렉싱은 한 연결 안에서 여러 스트림을 병렬로 처리해 HOL 블로킹을 없앤다.

4. **HTTP/2에도 남아 있던 병목을 HTTP/3가 어떻게 해결했는가?**
   - *힌트*: TCP HOL, QUIC.
   - *해설*: HTTP/2는 TCP 자체의 HOL 블로킹(한 패킷 유실 시 뒤 스트림 대기)이 남았다. HTTP/3는 UDP 기반 QUIC로 스트림을 독립 처리해 이를 해결한다.

5. **`Cache-Control: max-age`가 만료됐을 때 브라우저와 서버는 바디 재전송을 어떻게 아끼는가?**
   - *힌트*: ETag, 304.
   - *해설*: 브라우저가 `If-None-Match: "ETag값"`으로 재검증하고, 안 바뀌었으면 서버가 바디 없는 `304 Not Modified`로 답해 큰 바디 재전송을 생략한다.

---

## 요약

- **HTTP는 (1.x에서) 사람이 읽는 텍스트**다. 요청은 [요청 라인 + 헤더 + 빈 줄 + 바디], 응답은 [상태 라인 + 헤더 + 빈 줄 + 바디]. **빈 줄(`\r\n\r\n`)**이 헤더와 바디를 가른다.
- **연결 재사용의 진화**: HTTP/1.0(매번 새 연결) → 1.1(keep-alive, 순차·HOL) → 2(멀티플렉싱·헤더압축, TCP HOL 잔존) → 3(QUIC/UDP, TCP HOL 해결). 방향은 "비싼 왕복 줄이기".
- **HTTP는 stateless**. 확장성을 얻는 대신 로그인 상태를 기억 못 한다. **쿠키/세션/토큰**이 stateless 위에 상태를 얹는다(자세한 세션 vs JWT·서명은 SPRING §5.3·§5.7).
- **캐싱 헤더**(`Cache-Control`·`ETag`·`304`)로 "안 바뀐 것을 또 받지 않는다". DNS TTL(§4.3)과 같은 발상.
- **자바 표준 `HttpClient`(§5.5)**는 RawHttpClient가 손으로 하던 일(연결·조립·파싱)에 TLS와 HTTP/2 협상까지 자동으로 해 주는 실무 도구다. ▶ `./run.sh HttpClientExample`

---

← [4장. DNS](04-DNS.md) | 다음 장 → [6장. HTTPS/TLS](06-HTTPS-TLS.md)
