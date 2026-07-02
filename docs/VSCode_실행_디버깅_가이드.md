# VS Code로 예제 실행·디버깅 (한 줄씩 파고들기)

`run.sh`가 "개념을 골라 빠르게 돌려보는" 도구라면, 이 문서는 **VS Code에서 예제를 열고 ▶ 실행하거나, 중단점을 찍고 한 줄씩 따라가며 이해하는** 방법을 안내합니다. 원리를 깊게 파고들 때는 이 방식이 훨씬 강력합니다.

> **언제 무엇을?**
> - 개념 훑고 결과만 빠르게 확인 → 터미널 `./run.sh <클래스명>`
> - **변수 값·실행 흐름을 한 줄씩 관찰하며 이해 → VS Code 디버그(이 문서)**
> - 둘은 경쟁이 아니라 상호보완입니다. 섞어 쓰세요.

---

## 0. 준비물

- **Docker Desktop** (실행 중)
- **VS Code** + 확장 **Dev Containers**(`ms-vscode-remote.remote-containers`)

> 컨테이너 안에서 쓸 Java 확장(디버거·테스트 포함)은 이 저장소의 `.devcontainer/devcontainer.json`에
> 이미 지정돼 있어 **컨테이너로 열면 자동 설치**됩니다. 로컬에 JDK를 깔 필요가 없습니다.

---

## 1. 컨테이너로 열기 (두 가지 방법)

### 방법 A — Reopen in Container (권장)
1. VS Code로 이 저장소 폴더를 엽니다.
2. `F1` → **Dev Containers: Reopen in Container** 선택.
3. 처음 한 번은 이미지 준비 + 확장 설치로 시간이 걸립니다. 끝나면 창 왼쪽 아래에 `개발 컨테이너: Java & Spring Boot 교육` 이라고 표시됩니다.
4. 워크스페이스는 컨테이너의 `/app` 입니다. 예제 소스는 **`/app/src/com/edu/...`** 아래에 있습니다.
   (컨테이너 시작 시 `compile.sh`가 자동 실행되어 한 번 컴파일해 둡니다.)

### 방법 B — 이미 떠 있는 컨테이너에 붙기 (Attach)
1. 터미널에서 먼저 컨테이너를 띄웁니다: `docker compose up -d`
2. `F1` → **Dev Containers: Attach to Running Container** → **java-edu** 선택.
3. 열린 창에서 폴더 열기: **`/app`**.

> 붙은 뒤 Java 파일을 처음 열면 오른쪽 아래에 "Java 프로젝트를 가져오는 중…" 진행 표시가 잠깐 뜹니다.
> 이 **Java 언어 서버 로딩이 끝나야** 아래의 Run/Debug 버튼이 나타납니다(수십 초 걸릴 수 있음).

---

## 2. 그냥 실행하기 (▶ Run)

1. 실습할 파일을 엽니다. 예: `/app/src/com/edu/basics/TwosComplement.java`
2. `main` 메서드 바로 위에 나타나는 **`Run | Debug`** 링크(CodeLens) 중 **Run** 클릭.
   - 안 보이면: 편집기에서 **우클릭 → Run Java**, 또는 오른쪽 위 **▶** 버튼.
3. 아래 **터미널/디버그 콘솔**에 출력이 찍힙니다.

> `run.sh`처럼 컴파일을 신경 쓸 필요가 없습니다. Java 확장이 알아서 컴파일·실행합니다.

---

## 3. 디버그로 한 줄씩 따라가기 (핵심)

원리를 이해하는 가장 좋은 방법입니다. 예제로 **재귀 호출 스택**을 눈으로 봅시다.

1. `/app/src/com/edu/algorithms/RecursionAndDP.java` 를 엽니다.
2. **중단점(breakpoint)** 을 찍습니다: 실행을 멈추고 싶은 줄 번호 **왼쪽 여백을 클릭**하면 빨간 점이 생깁니다.
   (예: 팩토리얼/피보나치 재귀 메서드의 `return` 줄)
3. `main` 위의 **Debug** 링크 클릭(또는 `F5`).
4. 실행이 중단점에서 멈추면, 왼쪽 **실행 및 디버그** 패널에서:
   - **변수(Variables)** — 지금 이 순간 각 지역 변수의 값
   - **호출 스택(Call Stack)** — 어떤 메서드가 어떤 순서로 쌓여 있는지 (재귀가 쌓이는 게 보임!)
   - **조사식(Watch)** — 보고 싶은 식(예: `n * result`)을 등록해 추적
5. 위쪽 디버그 툴바로 한 걸음씩 진행합니다:

| 단축키 | 동작 | 뜻 |
|--------|------|----|
| `F10` | **Step Over** | 다음 줄로 (메서드 호출은 통째로 실행) |
| `F11` | **Step Into** | 호출하는 메서드 **안으로** 들어가기 (재귀 따라 들어감) |
| `Shift+F11` | **Step Out** | 지금 메서드를 끝내고 **호출한 곳으로** |
| `F5` | **Continue** | 다음 중단점까지 계속 |

> `F11`로 재귀 메서드를 계속 파고들면 **Call Stack이 쌓였다가**, `Step Out`/`Continue`로 **하나씩 빠지는** 걸 직접 볼 수 있습니다. 개념서 심화편 1장의 "호출 스택은 LIFO"가 눈앞에서 재현됩니다.

---

## 4. 디버깅이 특히 유익한 예제

| 파일 | 무엇을 관찰하나 |
|------|----------------|
| `com.edu.basics.TwosComplement` | 정수 변수의 비트가 어떻게 바뀌는지, 오버플로 순간 |
| `com.edu.basics.FloatingPointBits` | `0.1+0.2` 계산 중간값, 실수 변수의 실제 저장값 |
| `com.edu.algorithms.RecursionAndDP` | 재귀 **Call Stack**이 쌓였다 빠지는 흐름 |
| `com.edu.algorithms.SortingAlgorithms` | 정렬 도중 배열이 한 단계씩 바뀌는 모습(Watch에 배열 등록) |
| `com.edu.concurrency.RaceConditionDemo` | 여러 스레드가 공유 변수를 밟는 순간(스레드별 스택 전환) |
| `com.edu.collections.HashMapInternals` | put/get 시 내부 동작 추적 |

> 교재의 각 소절 `💻 실습: ./run.sh <클래스명>` 에서 `<클래스명>` 이 곧 파일 이름입니다.
> 예: `./run.sh IntegerType` → `/app/src/com/edu/javabook/ch02/IntegerType.java` 를 열어 실행/디버그.

---

## 5. 자주 겪는 문제

- **Run/Debug 링크가 안 보여요** → Java 언어 서버가 아직 로딩 중입니다. 오른쪽 아래 진행 표시가 사라질 때까지 기다리세요. 그래도 없으면 `F1` → **Java: Clean Java Language Server Workspace** 후 재시작.
- **"클래스를 찾을 수 없음"** → 파일의 `package` 선언과 폴더 경로가 맞는지 확인(예: `com.edu.basics` ↔ `.../com/edu/basics/`). 이 저장소 예제는 이미 맞춰져 있습니다.
- **다른 파일(도우미 클래스)을 못 찾음** → 대부분 예제는 한 파일에 자체 완결(내부 nested 클래스)이라 문제없습니다. 만약 같은 패키지의 다른 파일이 필요하면 그 파일도 워크스페이스에 열려 있어야 언어 서버가 인식합니다.
- **터미널에서 직접 하고 싶다** → 언제든 `./run.sh <클래스명>` 이 대안입니다.

---

## 요약

- **Reopen in Container**(또는 Attach) → 파일 열기 → **▶ Run** 이면 실행 끝.
- 깊게 이해하려면 **중단점 + F5(디버그) + F10/F11로 한 줄씩**, 그리고 **변수·호출 스택 패널**을 보세요.
- `run.sh`는 빠른 확인용, VS Code 디버그는 원리 파고들기용 — **둘을 섞어** 쓰는 게 가장 좋습니다.
