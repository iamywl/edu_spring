# Chapter CS: 자료구조 & 알고리즘 -- 실습 가이드

> 이 문서는 **실습 가이드**입니다. 개념 설명은 [CS_알고리즘_개념서](../docs/CS_알고리즘_개념서.md)를 먼저 읽으세요.
> Java 자체가 처음이라면 [JAVA_개념서](../docs/JAVA_개념서.md)를 선행하세요 (특히 §5 컬렉션).

---

## 이 챕터의 목표

당신은 이미 `ArrayList`, `HashMap`, `TreeMap`, `PriorityQueue`를 **써봤습니다**.
이 챕터는 그 안이 어떻게 생겼는지, 왜 그런 성능(Big-O)이 나오는지를
**직접 구현하고 측정**하며 배웁니다. 비전공자에서 전공자(CS major) 수준으로 가는 다리입니다.

학습 목표:

1. **Big-O를 추론**할 수 있다 — 코드를 보고 O(n²)/O(log n)을 유도한다.
2. **탐색·정렬**의 여러 알고리즘과 그 트레이드오프(안정성/제자리/속도)를 안다.
3. **재귀와 DP**로 지수 시간 문제를 다항 시간으로 낮춘다.
4. 자료구조를 **밑바닥부터 구현**한다 — 동적 배열, 연결 리스트, 스택, 큐, BST, 힙, 그래프.
5. 위 구현이 Java 표준 라이브러리(`ArrayList`/`TreeMap`/`PriorityQueue`)의 **원리임**을 안다.
6. 그래프 탐색(BFS/DFS)과 최단 경로(다익스트라)를 이해한다.

---

## 파일 구성

경로: `src/main/java/com/edu/algorithms/`

| 파일 | 무엇을 가르치는가 | 개념서 연결 |
|------|------------------|------------|
| `SearchAlgorithms.java` | 선형 탐색 O(n) vs 이진 탐색 O(log n). 비교 횟수를 세서 표로 출력. 정렬 전제를 깨면 이진 탐색이 실패하는 것을 시연. | 개념서 Ch.1~2 |
| `SortingAlgorithms.java` | 버블/선택/삽입(O(n²)) + 병합/퀵(O(n log n)) 5종 구현. 크기별 시간 측정 표. 안정성(stable) 차이 시연. | 개념서 Ch.3 |
| `RecursionAndDP.java` | 팩토리얼, 피보나치(순진 O(2ⁿ) vs 메모이제이션 vs 타뷸레이션), 콜 스택 깊이 측정, 0/1 배낭 DP, 그리디 실패 사례. | 개념서 Ch.4 |
| `DataStructuresFromScratch.java` | 동적 배열·단일 연결 리스트·스택(LIFO)·큐(FIFO)를 라이브러리 없이 구현. 각 연산의 O()를 출력으로 관찰. | 개념서 Ch.5 |
| `TreeAndHeap.java` | 이진 탐색 트리(삽입/탐색/중위순회, 퇴화 시연)와 최소 힙/우선순위 큐를 구현. 균형 O(log n) vs 퇴화 O(n), 힙 정렬. | 개념서 Ch.6 |
| `GraphAlgorithms.java` | 인접 리스트 그래프. BFS/DFS 방문 순서, 다익스트라 최단 경로(가중치 그래프)를 거리와 함께 출력. | 개념서 Ch.7 |

각 파일은 **독립 실행 가능한** `main()`을 가지며, 실행하면
"말"이 아니라 **측정값과 다이어그램**으로 결과를 보여줍니다.

---

## 실행 방법

### 방법 A: 통합 Docker Java 환경 (권장)

이 챕터는 통합 Docker Java 환경에 마운트됩니다.

```bash
docker compose up -d
# VS Code: F1 → "Dev Containers: Attach to Running Container" → java-edu
./compile.sh
./run.sh <ClassName>    # 클래스 이름으로 직접 실행 (예: SearchAlgorithms), 또는 ./run.sh로 대화형 메뉴
```

> **실행 안내:** `./run.sh <ClassName>`으로 클래스 이름을 직접 지정해 실행하거나,
> 인자 없이 `./run.sh`를 실행해 대화형 메뉴에서 카테고리 선택 → 개념 선택으로 실행할 수 있습니다.
> 예: `./run.sh SearchAlgorithms` 처럼 실행하세요. (아래 "방법 B"의 raw 명령으로도 바로 실행 가능)

### 방법 B: 순수 javac / java (독립 실행)

Docker 없이, JDK 21 이상만 있으면 어디서든 실행됩니다.

```bash
# 1) 컴파일 (프로젝트 루트에서)
mkdir -p out
javac -d out $(find chapter-cs-algorithms/src/main/java -name "*.java")

# 2) 실행 (원하는 클래스 하나씩)
java -cp out com.edu.algorithms.SearchAlgorithms
java -cp out com.edu.algorithms.SortingAlgorithms
java -cp out com.edu.algorithms.RecursionAndDP
java -cp out com.edu.algorithms.DataStructuresFromScratch
java -cp out com.edu.algorithms.TreeAndHeap
java -cp out com.edu.algorithms.GraphAlgorithms
```

한 줄로 컴파일+실행 (예: 탐색):

```bash
javac -d out $(find chapter-cs-algorithms/src/main/java -name "*.java") \
  && java -cp out com.edu.algorithms.SearchAlgorithms
```

> 이 챕터는 **표준 라이브러리만** 사용합니다. 외부 의존성이 없어 어떤 JDK 21+ 에서도 그대로 돕니다.

---

## 권장 학습 순서

| 순서 | 파일 | 실행 후 확인할 것 |
|------|------|------------------|
| 1 | `SearchAlgorithms` | 비교 횟수 표에서 N이 10배 커질 때 선형은 10배, 이진은 +3~4번만 느는 것 |
| 2 | `SortingAlgorithms` | 벤치마크 표에서 O(n²)이 O(n log n)보다 급격히 폭증하는 것, 안정/불안정 정렬의 순서 차이 |
| 3 | `RecursionAndDP` | 피보나치 호출 수가 순진 재귀에서 3억 번(n=40)까지 폭발 vs 메모는 79번, 그리디가 DP보다 작은 값을 내는 것 |
| 4 | `DataStructuresFromScratch` | 동적 배열 용량이 2→4→8로 "복사 이사"하는 순간, 연결 리스트 get이 O(n)인 것 |
| 5 | `TreeAndHeap` | 균형 BST 높이(log n) vs 퇴화 BST 높이(n) 표, 힙에서 최솟값이 오름차순으로 쏟아지는 것 |
| 6 | `GraphAlgorithms` | BFS와 DFS의 방문 순서 차이, 다익스트라가 0→2→1(=3) 우회 최단경로를 찾는 것 |

---

## 실습 과제 (직접 손으로 짜보기)

이론은 읽는 것이 아니라 **구현하는 것**입니다. 아래를 직접 고쳐보세요.

1. **`SearchAlgorithms`**: 이진 탐색을 재귀 버전으로 다시 작성하고, 비교 횟수가 반복 버전과 같은지 확인하세요.
2. **`SortingAlgorithms`**: 퀵 정렬의 피벗을 "맨 끝" 대신 "가운데"로 바꿔, 이미 정렬된 입력에서 최악 O(n²)을 피하는지 실험하세요.
3. **`RecursionAndDP`**: 0/1 배낭 DP에서 "실제로 어떤 물건을 담았는지" 목록을 역추적(backtracking)해 출력하세요.
4. **`DataStructuresFromScratch`**: 단일 연결 리스트에 `reverse()`(뒤집기)와 `contains(int)`를 추가하세요.
5. **`TreeAndHeap`**: BST에 `min()`, `max()`, `delete(int)`를 추가하세요(삭제는 세 가지 경우를 다뤄야 합니다).
6. **`GraphAlgorithms`**: DFS를 재귀 대신 **명시적 스택**(`java.util.Deque`)으로 다시 구현하세요.

---

## 참고

- 개념 설명: [../docs/CS_알고리즘_개념서.md](../docs/CS_알고리즘_개념서.md)
- Java 기초/컬렉션: [../docs/JAVA_개념서.md](../docs/JAVA_개념서.md)
- 관련 심화(Big-O 측정): `../chapter03-collections/src/main/java/com/edu/collections/BigOTiming.java`
