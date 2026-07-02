# Chapter CS: 운영체제 · 동시성(Concurrency) - 실습 가이드

> 이 문서는 **실습 가이드(LAB GUIDE)**입니다.
> 개념 설명은 [`/docs/CS_운영체제_개념서/README.md`](../docs/CS_운영체제_개념서/README.md)를 읽고,
> 여기서는 코드를 직접 실행하며 **동시성 버그를 눈으로 확인**합니다.

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

전부 `package com.edu.concurrency;`, 각각 `main()`을 가진 **독립 실행 프로그램**입니다.
모든 예제는 **래치/타임아웃/awaitTermination**으로 반드시 종료되도록 설계되어 **절대 멈추지(hang) 않습니다.**

---

## 실행 방법

### 방법 A: 통합 Docker Java 환경 (권장)

이 챕터는 프로젝트의 통합 Docker Java 환경에 마운트됩니다.

```bash
# 프로젝트 루트에서
docker compose up -d
# VS Code: F1 → "Dev Containers: Attach to Running Container" → java-edu
# 컨테이너 터미널에서:
./compile.sh
./run.sh      # 대화형 계층 메뉴: 카테고리 선택 → 개념 선택
./run.sh RaceConditionDemo   # 또는 클래스 이름으로 바로 실행
```

> **실행 방법:** `./run.sh <ClassName>`으로 클래스 이름을 직접 지정하거나(예: `./run.sh DeadlockDemo`),
> 인자 없이 `./run.sh`를 실행해 대화형 메뉴에서 "CS 운영체제/동시성" 카테고리를 고른 뒤 각 데모를 선택하세요.
> 사용 가능한 클래스: `RaceConditionDemo`, `SynchronizationDemo`, `DeadlockDemo`, `ExecutorAndFutures`, `ProducerConsumer`.

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

---

## 관찰 포인트 요약

- **`RaceConditionDemo`**: 결과 < 800,000 인가? 실행마다 값이 다른가? → 잃어버린 갱신 + 비결정성.
- **`SynchronizationDemo`**: 세 방법 모두 800,000 인가? `volatile`만 여전히 틀린가? → 가시성 ≠ 원자성.
- **`DeadlockDemo`**: "교착 감지!" 메시지가 뜬 뒤 프로그램이 정상 종료되는가? 순서 통일 후엔 교착이 없는가?
- **`ExecutorAndFutures`**: 워커 이름이 `pool-1-thread-1/2/3` 3종류만 반복되는가? → 스레드 재사용.
- **`ProducerConsumer`**: "버퍼 가득참! 생산자 대기" 메시지가 뜨는가? 생산·소비 개수가 일치하는가?

---

## 연결 지도

이 트랙은 다른 개념서와 하나의 이야기로 이어집니다.

- `JAVA_개념서 §1.4~1.5` (메모리 구조, 바이트코드/operand stack) → `count++`가 왜 3단계인지
- `SPRING_개념서 §2.7` (서버는 멀티스레드다, thread-per-request, 스레드 풀·커넥션 풀) → 왜 실무에서 이게 중요한지
- 본 챕터 `CS_운영체제_개념서/README.md` → 그 밑바닥의 OS·CPU·JMM
