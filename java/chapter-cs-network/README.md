# chapter-cs-network — 컴퓨터 네트워크 트랙 (HTTP 아래를 판다)

> Spring 웹 챕터가 **REST를 API 레벨**에서 가르친다면, 이 트랙은 그 **HTTP 밑에 깔린 네트워크 스택**을 가르친다. 비전공자를 전공자(CS-major) 출발선까지 끌어올리는 것이 목표다.

> **🐳 실습 환경 — 이 장의 Java 데모는 `java-sandbox` 컨테이너에서 실행한다** (인터넷 접속 필요)
> ```bash
> cd java && docker compose up -d              # 컨테이너 켜기 (이미 떠 있으면 생략)
> docker exec -it java-sandbox ./run.sh RawHttpClient
> ```
> 셸 관찰 실습은 호스트에서: `bash java/chapter-cs-network/labs/observe_http.sh`

---

## 무엇을 배우는가 (학습 목표)

이 트랙을 마치면 **"API를 한 번 호출하면 실제로 무슨 일이 벌어지는가"**를 계층별로 설명할 수 있다.

- **계층 모델**: OSI 7계층 / TCP-IP 4계층, 각 층의 역할, 캡슐화(헤더가 페이로드를 감싸는 구조)
- **물리/링크/네트워크**: IP 주소(IPv4/IPv6, 서브넷/CIDR), 라우팅 직관, MAC vs IP
- **전송 계층**: TCP vs UDP, 3-way handshake, 신뢰성(seq/ack/재전송), 흐름·혼잡 제어, 포트와 소켓, 4-way close
- **DNS**: 이름→주소, 재귀/반복 질의, 레코드(A/AAAA/CNAME), 캐싱과 TTL
- **HTTP**: 요청/응답의 바이트 수준 해부, HTTP/1.1 keep-alive vs HTTP/2 멀티플렉싱, 상태 없음과 쿠키/세션, 캐싱 헤더
- **HTTPS/TLS**: 기밀성/무결성/인증, TLS 핸드셰이크(비대칭→대칭 세션키), 인증서와 CA
- **전부 합치기**: URL 입력 → DNS → TCP → TLS → HTTP → 응답까지 7단계를 계층에 매핑

### 개념서와 함께 보기

핵심 개념서는 **[`docs/CS_네트워크_개념서/README.md`](../docs/CS_네트워크_개념서/README.md)** 이다. 먼저 읽고, 아래 실습으로 "정말 그런지" 눈으로 확인하는 흐름을 권장한다.

이 트랙은 기존 문서와 **중복 없이 이어진다**:
- HTTP를 API 레벨에서 다룬 **`docs/SPRING_개념서/README.md` §3(웹 애플리케이션의 구조)** 위에 쌓는다.
- TLS/비대칭 암호는 **`SPRING_개념서/README.md` §5.7(HMAC·대칭/비대칭)**이 "미뤄둔" 이야기를 네트워크 개념서 §6에서 본격적으로 편다.

```
  docs/CS_네트워크_개념서/README.md   (개념: "왜 그런가")
            │
            ▼  읽은 뒤 관찰
  labs/observe_http.sh         (관찰: dig/curl/openssl/nc 로 실제 계층을 봄)
            │
            ▼  직접 구현
  src/.../RawHttpClient.java    (증명: 소켓에 텍스트를 써서 "HTTP=TCP 위 텍스트" 확인)
```

---

## 디렉터리 구조

```
chapter-cs-network/
├── README.md
├── labs/
│   └── observe_http.sh                          # 계층을 눈으로 보는 셸 실습
└── src/main/java/com/edu/network/
    ├── RawHttpClient.java                        # 소켓으로 직접 HTTP 요청을 보내는 데모
    └── HttpClientExample.java                    # Java 11+ 표준 HttpClient (GET/POST/비동기/HTTP2)
```

---

## 실행 방법

> **모든 실습은 인터넷 접속이 필요하다.** 공개 테스트 사이트(`example.com`)만 **읽기 전용**으로 조회하며, 아무것도 설치·변경·삭제하지 않는다. 오프라인이면 각 단계가 실패 메시지를 내고 안전하게 종료된다.

### 1) 셸 실습 — `observe_http.sh`

```bash
# chapter-cs-network 디렉터리에서
bash labs/observe_http.sh
```

무엇을 보여주는가 (macOS/Linux 공용, 도구가 없으면 그 단계만 자동으로 건너뜀):

| 단계 | 사용 도구 | 관찰 대상 | 개념서 |
|------|-----------|-----------|--------|
| 1 | `dig` / `nslookup` | DNS A/AAAA 레코드와 **TTL** | Ch4 |
| 2 | `curl -v` | TCP 연결 · **TLS 핸드셰이크** · 요청/응답 헤더 | Ch3/5/6 |
| 3 | `openssl s_client` | **인증서 체인** · TLS 버전/암호 스위트 | §6.3 |
| 4 | `nc` + `printf` | 평문 TCP로 보낸 **날것의 HTTP 요청 바이트** | §5.1 |

안전장치: 모든 명령에 타임아웃(`-m`, `-w`)과 입력 차단(`</dev/null`)을 걸어 **절대 멈추지 않는다.**

### 2) Java 데모 — `RawHttpClient.java`

브라우저·`RestTemplate` 없이 `java.net.Socket`을 직접 열어 `"GET / HTTP/1.1\r\nHost: ...\r\nConnection: close\r\n\r\n"`을 써 보내고, 돌아온 응답 원문을 출력한다. **"HTTP는 TCP 위의 텍스트일 뿐"**임을 두 눈으로 확인하는 것이 목적이다.

로컬 JDK로 직접 실행:

```bash
# 컴파일 (임의의 출력 디렉터리에)
javac -d /tmp/netout src/main/java/com/edu/network/RawHttpClient.java

# 실행
java -cp /tmp/netout com.edu.network.RawHttpClient
```

### 3) Java 데모 — `HttpClientExample.java` (RawHttpClient 의 실무 버전)

RawHttpClient 가 손으로 하던 일(소켓 열기·요청 조립·응답 파싱)을 **Java 11+ 표준 `java.net.http.HttpClient`**로 다시 한다. ① GET(타임아웃·헤더, **협상된 HTTP/2 버전 확인**), ② JSON POST, ③ `sendAsync` + `CompletableFuture` 비동기 조합을 보여준다. 대상은 공개 테스트 서비스 `httpbin.org`(비동기 단계는 `example.com`/`example.org`)이며, 오프라인이면 스택트레이스 없이 친절한 안내를 출력하고 정상 종료한다.

```bash
# 컴파일
javac -d /tmp/netout src/main/java/com/edu/network/HttpClientExample.java

# 실행 (인터넷 필요)
java -cp /tmp/netout com.edu.network.HttpClientExample
```

두 데모를 나란히 실행해 보라: **같은 HTTP인데, 하나는 밑바닥(소켓 위의 텍스트), 하나는 실무 도구(TLS·HTTP/2 협상까지 자동)**다. 개념 정리는 [`docs/CS_네트워크_개념서/05-HTTP.md`](../docs/CS_네트워크_개념서/05-HTTP.md) §5.5 참고.

### 4) 통합 Docker Java 실습 환경

이 데모들은 `com.edu.network` 패키지의 표준 Java 클래스이므로, 프로젝트의 **통합 Docker 실습 환경인 `java-sandbox` 컨테이너**로도 실행할 수 있다.

```bash
cd java && docker compose up -d              # 컨테이너 켜기 (이미 떠 있으면 생략)
docker exec -it java-sandbox ./run.sh RawHttpClient
docker exec -it java-sandbox ./run.sh HttpClientExample
```

클래스 이름을 직접 지정하거나 `docker exec -it java-sandbox ./run.sh` 대화형 메뉴에서 카테고리 선택 → 개념 선택으로 실행한다(`run.sh`는 이 트랙에서 직접 수정하지 않는다). Docker 컨테이너에서 실행할 경우에도 **컨테이너에 네트워크 접속이 열려 있어야** `example.com`/`httpbin.org`에 연결된다.

---

## 학습 순서 권장

1. `docs/CS_네트워크_개념서/README.md`를 Ch1→Ch7 순서로 읽는다. (특히 Ch7 "전부 합치기"가 이 트랙의 지불점)
2. `bash labs/observe_http.sh`를 돌려, 읽은 계층(DNS/TCP/TLS/HTTP)을 실제로 관찰한다.
3. `RawHttpClient.java`를 컴파일·실행해, HTTP 요청을 손으로 조립하는 경험을 한다.
4. `HttpClientExample.java`를 실행해, 같은 일을 표준 클라이언트가 어떻게 대신하는지(TLS·HTTP/2 협상 포함) 대비해 본다.
5. 여유가 되면 `Wireshark`/`traceroute`로 캡슐화·라우팅(개념서 §1.3, §2.5)을 더 깊이 본다.

---

## 참고

- 이 트랙은 기존 챕터/`run.sh`/`docker-compose.yml`을 수정하지 않는 **추가 전용** 트랙이다.
- 실습 스크립트는 읽기 전용이며 공개 엔드포인트만 조회한다. 사내망/사설 서버를 대상으로 삼지 말 것.
