# 부록 04. NIO — 채널·버퍼 기반 입출력

전통적인 자바 I/O(`java.io`의 `InputStream`/`OutputStream`)는 데이터를 한 바이트씩 흘려보내는 **스트림 방식**이라 직관적이지만, 대용량 파일이나 수천 개의 네트워크 연결을 다룰 때는 성능이 아쉽다. **NIO(New I/O, `java.nio`)**는 **채널(Channel)**과 **버퍼(Buffer)**를 중심으로 데이터를 블록 단위로 옮기고, 하나의 스레드로 여러 연결을 감시하는 **셀렉터(Selector)**를 제공해 이 한계를 넘는다.

이 부록은 NIO의 핵심(채널·버퍼·셀렉터, 블로킹/논블로킹, `Path`/`Files`, `ByteBuffer`, `FileChannel`, 비동기 I/O, TCP/UDP 소켓 채널)을 코드 예시 위주로 정리한다.

> ✅ **실행 환경**: NIO는 GUI가 아니므로 **CLI/컨테이너에서 정상 동작한다.** 파일·디렉토리 조작은 [Chapter 18 (File·Files)](../JAVA_교재/) 내용과 이어지며, `./run.sh`나 일반 `java` 실행으로 그대로 테스트할 수 있다. (네트워크 예제는 방화벽·포트 상황에 따라 로컬에서 확인하는 것을 권장한다.)

---

## 1. NIO 소개 — 채널·버퍼·셀렉터

세 가지 핵심 개념을 먼저 잡자.

- **채널(Channel)** — 데이터가 오가는 양방향 통로. 파일·소켓 등과 연결된다(`FileChannel`, `SocketChannel`). 스트림이 한 방향(입력 또는 출력)인 것과 달리 채널은 읽기·쓰기 모두 가능하다.
- **버퍼(Buffer)** — 데이터를 담는 고정 크기 메모리 블록(`ByteBuffer` 등). 채널은 **항상 버퍼를 통해** 데이터를 읽고 쓴다. "채널 ↔ 버퍼" 사이에서만 데이터가 이동한다.
- **셀렉터(Selector)** — 여러 채널을 하나의 스레드로 감시하는 감시자. "읽을 준비가 된 채널"을 골라내 알려 준다. 다중 접속 서버의 핵심.

**블로킹 vs 논블로킹**도 NIO의 중요한 축이다.

- **블로킹(blocking)** — 데이터가 준비될 때까지 스레드가 멈춰 기다린다(전통 I/O 기본).
- **논블로킹(non-blocking)** — 준비된 데이터만큼만 즉시 처리하고 바로 반환한다. 스레드가 멈추지 않아 셀렉터와 함께 쓰면 스레드 하나로 수많은 연결을 다룰 수 있다.

---

## 2. 파일과 디렉토리 — Path와 Files

NIO.2(`java.nio.file`)는 파일 경로를 `Path`로, 파일 조작을 `Files` 유틸리티로 다룬다. 낡은 `java.io.File`보다 훨씬 강력하고 직관적이다.

```java
import java.nio.file.*;

Path path = Paths.get("data", "report.txt");   // data/report.txt (OS 독립적)

// 존재 확인 · 정보
boolean exists = Files.exists(path);
long    size   = Files.size(path);

// 디렉토리 생성
Files.createDirectories(Paths.get("data/logs"));

// 파일 전체를 문자열로 읽기 / 쓰기 (작은 파일용, 아주 간편)
Files.writeString(path, "안녕하세요");
String content = Files.readString(path);

// 한 줄씩 스트림으로 처리 (대용량 안전)
try (var lines = Files.lines(path)) {
    lines.forEach(System.out::println);
}

// 복사 · 이동 · 삭제
Files.copy(path, Paths.get("backup.txt"), StandardCopyOption.REPLACE_EXISTING);
Files.deleteIfExists(Paths.get("backup.txt"));
```

`Path`는 경로를 나타내는 값이고(실제 파일 존재와 무관), `Files`는 그 경로에 실제 작업을 수행하는 정적 메서드 모음이다.

---

## 3. 버퍼 — ByteBuffer의 상태값

버퍼는 NIO에서 가장 헷갈리는 부분이다. `ByteBuffer`는 내부에 세 가지 위치 표시자를 갖는다.

- **capacity** — 버퍼가 담을 수 있는 최대 크기(고정).
- **position** — 다음에 읽거나 쓸 위치(현재 커서).
- **limit** — 읽거나 쓸 수 있는 경계(여기까지만).

핵심은 **`flip()`**이다. 데이터를 버퍼에 **쓴 뒤** 그것을 **읽으려면** 반드시 `flip()`으로 모드를 전환해야 한다. `flip()`은 `limit = position; position = 0`으로 바꿔, "방금 쓴 만큼을 처음부터 읽도록" 준비한다.

```java
import java.nio.ByteBuffer;

ByteBuffer buffer = ByteBuffer.allocate(16);   // capacity=16

buffer.put((byte) 'H');                         // 쓰기 → position 증가
buffer.put("i!".getBytes());
// 지금은 '쓰기 모드': position은 방금 쓴 곳, limit은 capacity

buffer.flip();                                  // 읽기 모드로 전환 (필수!)
// 이제 position=0, limit=방금 쓴 지점

while (buffer.hasRemaining()) {                 // position < limit 인 동안
    System.out.print((char) buffer.get());      // 읽기 → position 증가
}                                               // 출력: Hi!

buffer.clear();                                 // position=0, limit=capacity (다시 쓰기 준비)
```

패턴을 기억하자: **`put`(쓰기) → `flip` → `get`(읽기) → `clear`(초기화)**.

---

## 4. 파일 입출력 — FileChannel

`FileChannel`은 파일과 연결된 채널이다. 버퍼를 통해 파일을 읽고 쓴다. 위의 버퍼 개념이 그대로 적용된다.

```java
import java.nio.channels.FileChannel;
import java.nio.file.*;

// 쓰기
try (FileChannel ch = FileChannel.open(Paths.get("out.txt"),
        StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {
    ByteBuffer buf = ByteBuffer.wrap("NIO 채널 쓰기".getBytes());
    ch.write(buf);
}

// 읽기
try (FileChannel ch = FileChannel.open(Paths.get("out.txt"), StandardOpenOption.READ)) {
    ByteBuffer buf = ByteBuffer.allocate(1024);
    int bytesRead = ch.read(buf);          // 채널 → 버퍼 (쓰기 모드로 채워짐)
    buf.flip();                            // 읽기 모드로 전환
    byte[] data = new byte[buf.remaining()];
    buf.get(data);
    System.out.println(new String(data));
}
```

채널이 버퍼를 "채우면" 버퍼는 쓰기 모드가 되므로, 내용을 꺼내기 전에 `flip()`을 잊지 말자.

---

## 5. 비동기 파일 I/O — AsynchronousFileChannel

`AsynchronousFileChannel`은 파일 읽기/쓰기를 **백그라운드에서 처리하고** 완료 시 콜백이나 `Future`로 결과를 받는다. 스레드가 I/O 완료를 기다리며 놀지 않아도 된다.

```java
import java.nio.channels.AsynchronousFileChannel;
import java.util.concurrent.Future;

try (AsynchronousFileChannel ch = AsynchronousFileChannel.open(
        Paths.get("out.txt"), StandardOpenOption.READ)) {

    ByteBuffer buf = ByteBuffer.allocate(1024);
    Future<Integer> future = ch.read(buf, 0);   // position 0부터 비동기 읽기

    // ... 이 사이에 다른 작업 수행 가능 ...

    int bytesRead = future.get();               // 필요할 때 결과 대기
    buf.flip();
    System.out.println("읽은 바이트: " + bytesRead);
}
```

`CompletionHandler`를 넘기면 완료 시점에 콜백으로 결과를 받는 방식도 가능하다.

---

## 6. TCP — SocketChannel / ServerSocketChannel + Selector

NIO의 진가는 네트워크에서 드러난다. **논블로킹 소켓 채널 + 셀렉터**를 조합하면 **스레드 하나로 수많은 클라이언트**를 처리하는 서버를 만들 수 있다.

- **`ServerSocketChannel`** — 서버 소켓. 클라이언트 연결을 수락(`accept`).
- **`SocketChannel`** — 연결된 소켓. 실제 데이터 송수신.
- **`Selector`** — 여러 채널을 등록해 두고, 준비된 채널(연결 도착·읽기 가능 등)만 골라 처리.

```java
import java.nio.channels.*;
import java.net.InetSocketAddress;
import java.util.Iterator;

Selector selector = Selector.open();
ServerSocketChannel server = ServerSocketChannel.open();
server.bind(new InetSocketAddress(8080));
server.configureBlocking(false);                       // 논블로킹 필수
server.register(selector, SelectionKey.OP_ACCEPT);     // "연결 도착"에 관심 등록

while (true) {
    selector.select();                                 // 준비된 채널이 생길 때까지 대기
    Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
    while (keys.hasNext()) {
        SelectionKey key = keys.next();
        keys.remove();

        if (key.isAcceptable()) {                      // 새 연결
            SocketChannel client = server.accept();
            client.configureBlocking(false);
            client.register(selector, SelectionKey.OP_READ); // 이 연결의 "읽기"에 관심
        } else if (key.isReadable()) {                 // 읽을 데이터 도착
            SocketChannel client = (SocketChannel) key.channel();
            ByteBuffer buf = ByteBuffer.allocate(256);
            int n = client.read(buf);
            if (n == -1) { client.close(); continue; }
            buf.flip();
            client.write(buf);                         // 받은 내용 그대로 돌려주기(에코)
        }
    }
}
```

이 한 루프가 셀렉터가 알려 주는 "준비된 채널"만 처리하며 여러 접속을 동시에 감당한다. 스레드를 접속마다 하나씩 만드는 전통 방식보다 훨씬 효율적이다.

---

## 7. UDP — DatagramChannel

TCP가 연결을 맺고 스트림을 주고받는다면, **UDP는 연결 없이 데이터그램(패킷)을 던지는** 방식이다. NIO에서는 `DatagramChannel`로 다룬다.

```java
import java.nio.channels.DatagramChannel;
import java.net.InetSocketAddress;

// 수신 측
try (DatagramChannel channel = DatagramChannel.open()) {
    channel.bind(new InetSocketAddress(9090));
    ByteBuffer buf = ByteBuffer.allocate(256);
    channel.receive(buf);                              // 패킷 수신 (보낸 주소 반환)
    buf.flip();
    System.out.println("수신: " + new String(buf.array(), 0, buf.limit()));
}

// 송신 측
try (DatagramChannel channel = DatagramChannel.open()) {
    ByteBuffer buf = ByteBuffer.wrap("hello UDP".getBytes());
    channel.send(buf, new InetSocketAddress("localhost", 9090));
}
```

UDP는 전달·순서를 보장하지 않는 대신 가볍고 빠르다. 실시간 스트리밍·게임처럼 약간의 손실을 감수하고 속도가 중요한 곳에 쓰인다.

---

## 요약

- **NIO**는 **채널(통로)·버퍼(데이터 블록)·셀렉터(다중 채널 감시)**를 축으로 하는 입출력 API로, GUI가 아니라 **CLI/컨테이너에서 정상 동작**한다.
- **블로킹**은 준비될 때까지 대기, **논블로킹**은 즉시 반환. 논블로킹 + 셀렉터로 스레드 하나가 다수 연결을 처리한다.
- 파일은 **`Path`**(경로 값)와 **`Files`**(정적 조작 메서드)로 다룬다 — Chapter 18과 연계.
- 버퍼는 **capacity/position/limit** 상태를 가지며, 쓰기 후 읽기 전에 **`flip()`**이 필수다: `put → flip → get → clear`.
- 파일 채널 입출력은 **`FileChannel`**, 완료 콜백/`Future` 기반 비동기는 **`AsynchronousFileChannel`**.
- 네트워크는 TCP **`ServerSocketChannel`/`SocketChannel` + `Selector`**, UDP **`DatagramChannel`**로 처리한다.
