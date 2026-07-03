# Chapter CS: 운영체제 · 동시성(Concurrency) - 실습 가이드

> 이 문서는 **실습 가이드(LAB GUIDE)**입니다.
> 개념 설명은 [`/docs/CS_운영체제_개념서/README.md`](../docs/CS_운영체제_개념서/README.md)를 읽고,
> 여기서는 코드를 직접 실행하며 **동시성 버그를 눈으로 확인**합니다.

> **🐳 실습 환경 — 이 장의 데모는 `java-sandbox` 컨테이너에서 실행한다**
> ```bash
> cd java && docker compose up -d              # 컨테이너 켜기 (이미 떠 있으면 생략)
> docker exec -it java-sandbox ./run.sh RaceConditionDemo
> ```

비전공자에서 전공자(CS-major) 수준으로 가기 위한 **운영체제 & 동시성** 트랙입니다.
Java 21 표준 라이브러리만 사용하며, 각 예제는 **출력이 개념을 증명**하도록 짜여 있습니다.

---

## 학습 목표

이 챕터를 마치면 다음을 **설명하고 코드로 재현**할 수 있습니다.

- 프로세스 vs 스레드: 무엇이 공유(힙/코드)되고 무엇이 전용(스택/레지스터)인가
- 동시성 vs 병렬성, 왜 실행 순서가 비결정적인가(OS 스케줄러, 문맥 교환)
- 경쟁 상태(race condition)와 임계 구역: **왜 `count++`가 원자적이지 않은가**(바이트코드 3단계)
- 동기화: `synchronized`(모니터) · `AtomicInteger`(CAS) · `ReentrantLock`이 각각 무엇을 고치는가
- **가시성 ≠ 원자성**: `volatile`만으로는 왜 `count++`를 못 고치는가
- 교착 상태(deadlock): 코프만 4조건과 락 순서 통일로 예방하기
- 고수준 도구: 스레드 풀(`ExecutorService`)·`Future`·`BlockingQueue`(생산자-소비자)
- 이것이 톰캣/스프링의 요청 처리 방식과 어떻게 연결되는가(`SPRING_개념서 §2.7`)

---

## 파일 목록 — 각 파일이 무엇을 증명하는가

| # | 파일 | 무엇을 증명하는가 |
|---|------|-------------------|
| 1 | `RaceConditionDemo.java` | 스레드 8개가 공유 카운터를 100,000번씩 증가 → 결과가 기대값(800,000)보다 **작게**, 그리고 **매번 다르게** 나온다(잃어버린 갱신 + 비결정성). 단일 스레드 대조군은 항상 정답. |
| 2 | `SynchronizationDemo.java` | 같은 카운터를 `synchronized`·`AtomicInteger`(CAS)·`ReentrantLock` 세 방법으로 고쳐 **셋 다 정답**이 나오는 것, 그리고 `volatile`만 쓰면 **여전히 틀리는 것**(가시성 ≠ 원자성)을 나란히 보여준다. |
| 3 | `DeadlockDemo.java` | 두 스레드가 락을 반대 순서로 잡아 **교착을 실제로 유발**(감시견 타임아웃으로 감지·탈출 → 절대 hang 안 함), 이어서 **락 순서 통일**로 교착이 사라지는 것을 보여준다. |
| 4 | `ExecutorAndFutures.java` | 스레드 3개 풀로 작업 9개를 처리하며 워커 이름이 **3종류만 반복**(=스레드 재사용)되는 것, `Future`로 결과를 회수하는 것, `new Thread()`와의 대비를 보여준다. |
| 5 | `ProducerConsumer.java` | `BlockingQueue`(크기 5) 기반 생산자-소비자. 빠른 생산자가 **버퍼가 찰 때마다 블록**되는 것(배압)과 생산·소비 개수가 **정확히 일치**(유실·중복 없음)하는 것을 보여준다. |
| 6 | `VolatileVisibilityDemo.java` | **가시성 문제를 실제로 재현**: `volatile` 없는 플래그로는 워커가 안 멈추고(타임아웃으로 안전 관찰), `volatile`을 붙이면 항상 즉시 멈춘다. happens-before 로 일반 변수까지 안전 발행되는 것도 보여준다. (환경에 따라 1부 재현이 안 될 수 있음 — 그것도 관찰 포인트) |
| 7 | `AtomicCasDemo.java` | `incrementAndGet()` 속의 **CAS 재시도 루프를 `compareAndSet`으로 직접 구현**하고 재시도 횟수를 센다. 보호 없는 `int++`(틀림) vs Atomic(정답) 대비, `AtomicLong` vs `LongAdder` 처리량 간이 비교까지. |
| 8 | `ReentrantLockDemo.java` | `synchronized`가 못 하는 것들: **재진입 홀드 카운트**, **`tryLock(타임아웃)`으로 반대 순서 락에서도 교착 없이 완주**, **공정성 모드(FIFO 순서)**, **Condition 두 개로 만든 bounded buffer**(BlockingQueue의 속). |
| 9 | `CompletableFutureDemo.java` | 비동기 조합: supplyAsync/thenApply/thenCompose/thenCombine/allOf 를 가짜 API(sleep)로 시연. **순차 ~900ms vs 병렬 ~300ms** 를 시간으로 증명하고, exceptionally/handle 예외 복구를 보여준다. |

전부 `package com.edu.concurrency;`, 각각 `main()`을 가진 **독립 실행 프로그램**입니다.
모든 예제는 **래치/타임아웃/awaitTermination**으로 반드시 종료되도록 설계되어 **절대 멈추지(hang) 않습니다.**

---

## 실행 방법

### 방법 A: 통합 Docker Java 환경 (권장)

이 챕터는 프로젝트의 통합 Docker Java 환경에 마운트됩니다.

```bash
# 프로젝트 루트에서
cd java && docker compose up -d
# VS Code: F1 → "Dev Containers: Attach to Running Container" → java-sandbox
# 또는 attach 없이 컨테이너 밖에서 바로 실행:
docker exec -it java-sandbox ./run.sh RaceConditionDemo
# 컨테이너 터미널에서:
./compile.sh
./run.sh      # 대화형 계층 메뉴: 카테고리 선택 → 개념 선택
./run.sh RaceConditionDemo   # 또는 클래스 이름으로 바로 실행
```

> **실행 방법:** `./run.sh <ClassName>`으로 클래스 이름을 직접 지정하거나(예: `./run.sh DeadlockDemo`),
> 인자 없이 `./run.sh`를 실행해 대화형 메뉴에서 "CS 운영체제/동시성" 카테고리를 고른 뒤 각 데모를 선택하세요.
> 사용 가능한 클래스: `RaceConditionDemo`, `SynchronizationDemo`, `DeadlockDemo`, `ExecutorAndFutures`, `ProducerConsumer`, `VolatileVisibilityDemo`, `AtomicCasDemo`, `ReentrantLockDemo`, `CompletableFutureDemo`.

### 방법 B: raw javac / java (Docker 없이 로컬에서)

Java 21 이상이 설치돼 있으면 컴파일러/런타임만으로 바로 실행할 수 있습니다.

```bash
# 1) 컴파일 (out/ 디렉토리에 .class 생성)
cd chapter-cs-concurrency
javac -d out $(find src -name "*.java")

# 2) 각 데모 실행
java -cp out com.edu.concurrency.RaceConditionDemo
java -cp out com.edu.concurrency.SynchronizationDemo
java -cp out com.edu.concurrency.DeadlockDemo
java -cp out com.edu.concurrency.ExecutorAndFutures
java -cp out com.edu.concurrency.ProducerConsumer
java -cp out com.edu.concurrency.VolatileVisibilityDemo
java -cp out com.edu.concurrency.AtomicCasDemo
java -cp out com.edu.concurrency.ReentrantLockDemo
java -cp out com.edu.concurrency.CompletableFutureDemo
```

한 줄로 컴파일 검증만 하려면:

```bash
mkdir -p /tmp/concv && javac -d /tmp/concv $(find "$(pwd)" -name "*.java") && echo OK
```

---

## 권장 학습 순서

1. **개념서 먼저**: [`/docs/CS_운영체제_개념서/README.md`](../docs/CS_운영체제_개념서/README.md)를 Chapter 1~7 순서로 읽습니다.
2. **`RaceConditionDemo` 실행** — "문제"를 눈으로 봅니다. 여러 번 실행해 결과가 매번 다른 것을 확인하세요.
   > 참고: JIT 최적화로 스레드가 아주 빨라지면 어떤 실행은 우연히 정답이 나올 수도 있습니다.
   > 그것도 관찰 포인트입니다 — **동시성 버그는 "가끔만" 난다**는 것이 바로 무서운 점이니까요.
3. **`SynchronizationDemo` 실행** — 세 가지 해법과 `volatile`의 함정을 봅니다.
4. **`DeadlockDemo` 실행** — 락이 만드는 새 문제(교착)와 예방법을 봅니다.
5. **`ExecutorAndFutures` / `ProducerConsumer` 실행** — 실무에서 쓰는 고수준 도구를 봅니다.
6. **(심화) `VolatileVisibilityDemo` 실행** — "가시성 ≠ 원자성"의 가시성 쪽을 실제로 재현합니다 (개념서 5장).
7. **(심화) `AtomicCasDemo` / `ReentrantLockDemo` 실행** — CAS의 속과 명시적 락의 기능을 파고듭니다 (개념서 4장 §4.6).
8. **(심화) `CompletableFutureDemo` 실행** — Future 다음 단계, 비동기 조합을 봅니다 (개념서 7장 §7.3).

---

## 관찰 포인트 요약

- **`RaceConditionDemo`**: 결과 < 800,000 인가? 실행마다 값이 다른가? → 잃어버린 갱신 + 비결정성.
- **`SynchronizationDemo`**: 세 방법 모두 800,000 인가? `volatile`만 여전히 틀린가? → 가시성 ≠ 원자성.
- **`DeadlockDemo`**: "교착 감지!" 메시지가 뜬 뒤 프로그램이 정상 종료되는가? 순서 통일 후엔 교착이 없는가?
- **`ExecutorAndFutures`**: 워커 이름이 `pool-1-thread-1/2/3` 3종류만 반복되는가? → 스레드 재사용.
- **`ProducerConsumer`**: "버퍼 가득참! 생산자 대기" 메시지가 뜨는가? 생산·소비 개수가 일치하는가?
- **`VolatileVisibilityDemo`**: 1부의 워커가 1.5초를 기다려도 안 멈추는가(가시성 문제 재현)? 2부의 volatile 워커는 0ms에 멈추는가? 3부에서 payload=42가 정확히 보이는가? (1부가 우연히 멈춰도 정상 — 출력의 해석 안내 참고)
- **`AtomicCasDemo`**: 보호 없는 int++만 틀리는가? CAS 재시도 횟수가 0이 아닌가(실제 충돌의 증거)? LongAdder가 AtomicLong보다 빠른 경향인가?
- **`ReentrantLockDemo`**: 반대 순서 락인데 두 스레드 모두 완주하는가? 공정 락이 워커 1→2→3→4 순서를 지키는가? "버퍼 가득 → await" 후 signal로 깨어나는가?
- **`CompletableFutureDemo`**: 순차 ~900ms vs 병렬 ~300ms 차이가 찍히는가? 예외 발생 시 thenApply를 건너뛰고 exceptionally로 복구되는가?

---

## 연결 지도

이 트랙은 다른 개념서와 하나의 이야기로 이어집니다.

- `JAVA_개념서 §1.4~1.5` (메모리 구조, 바이트코드/operand stack) → `count++`가 왜 3단계인지
- `SPRING_개념서 §2.7` (서버는 멀티스레드다, thread-per-request, 스레드 풀·커넥션 풀) → 왜 실무에서 이게 중요한지
- 본 챕터 `CS_운영체제_개념서/README.md` → 그 밑바닥의 OS·CPU·JMM
