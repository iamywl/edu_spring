# 자바 교재 (이것이 자바다 목차 기반)

이 교재는 널리 쓰이는 자바 입문서 목차를 따라 **각 소절마다 [개념 설명 + 실습 코드]** 로 구성됩니다.
개념을 읽고 곧바로 **Docker 환경에서 실행**해 확인할 수 있습니다.

## 실행 방법

각 소절 끝의 `💻 실습: ./run.sh <클래스명>` 을 그대로 실행하면 됩니다.

```bash
# 1) Docker 컨테이너 기동 (최초 1회)
docker compose up -d

# 2) 대화형 메뉴 — 'j) 자바 교재' 선택 → 챕터 → 소절
docker exec -it java-edu ./run.sh

# 3) 또는 클래스 이름으로 바로 실행
docker exec -it java-edu ./run.sh VariableDeclaration
docker exec -it java-edu ./run.sh SortingAlgorithms
```

> 실습 코드는 `chapter-java-textbook/src/main/java/com/edu/javabook/chNN/` 에 소절별로 1개씩 있습니다.
> "왜 그렇게 동작하는가"의 깊은 설명은 [JAVA_개념서](../JAVA_개념서/README.md)를 함께 보세요.

## 목차

### PART 01 자바 언어의 기초
- [Chapter 01 자바 시작하기](01-자바-시작하기.md) — 개발 환경(Docker), 바이트코드/JVM, 실행 흐름, 주석
- [Chapter 02 변수와 타입](02-변수와-타입.md)
- [Chapter 03 연산자](03-연산자.md)
- [Chapter 04 조건문과 반복문](04-조건문과-반복문.md)

### PART 02 객체지향 프로그래밍
- [Chapter 05 참조 타입](05-참조-타입.md)
- [Chapter 06 클래스](06-클래스.md)
- [Chapter 07 상속](07-상속.md)
- [Chapter 08 인터페이스](08-인터페이스.md)
- [Chapter 09 중첩 선언과 익명 객체](09-중첩-선언과-익명-객체.md)
- [Chapter 10 라이브러리와 모듈](10-라이브러리와-모듈.md)
- [Chapter 11 예외 처리](11-예외-처리.md)

### PART 03 라이브러리 활용
- [Chapter 12 java.base 모듈](12-java.base-모듈.md)
- [Chapter 13 제네릭](13-제네릭.md)
- [Chapter 14 멀티 스레드](14-멀티-스레드.md)
- [Chapter 15 컬렉션 자료구조](15-컬렉션-자료구조.md)
- [Chapter 16 람다식](16-람다식.md)
- [Chapter 17 스트림 요소 처리](17-스트림-요소-처리.md)

### PART 04 데이터 입출력
- [Chapter 18 데이터 입출력](18-데이터-입출력.md)
- [Chapter 19 네트워크 입출력](19-네트워크-입출력.md)
- [Chapter 20 데이터베이스 입출력 (JDBC)](20-데이터베이스-입출력.md)

### PART 05 최신 자바
- [Chapter 21 자바 21에서 강화된 언어 및 라이브러리](21-자바-21-강화-기능.md)

### 부록
- [부록 01 Java UI — Swing](부록-01-Swing.md) *(GUI: 로컬 데스크톱 필요)*
- [부록 02 Java UI — JavaFX](부록-02-JavaFX.md) *(별도 SDK + GUI 필요)*
- [부록 04 NIO 기반 입출력·네트워킹](부록-04-NIO.md)

## 참고

- 실습 코드 176개(소절별)는 전부 컴파일·실행 검증되었습니다.
- 모듈(10장)·JDBC(20장)·GUI 부록은 실행 모델이 달라(모듈 시스템/외부 DB/디스플레이) 개념+예시 중심이며, 실행 방법을 각 장에 안내합니다.
