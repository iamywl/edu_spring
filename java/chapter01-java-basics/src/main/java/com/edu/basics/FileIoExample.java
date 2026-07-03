package com.edu.basics;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;

/**
 * Chapter 01 - 파일 입출력 기초 (java.nio.file)
 *
 * 최신 Java에서는 java.nio.file.Files 유틸리티로 파일을 간편하게 읽고 쓸 수 있습니다.
 * 또한 try-with-resources 구문으로 자원(스트림)을 자동으로 닫는 방법을 배웁니다.
 *
 * ※ 이 예제는 콘솔 입력으로 멈추지 않도록(비대화형) 임시 파일과 문자열을 사용합니다.
 */
public class FileIoExample {

    public static void main(String[] args) {
        System.out.println("====================================");
        System.out.println(" Chapter 01: 파일 입출력 기초 (NIO)");
        System.out.println("====================================\n");

        try {
            writeAndReadFile();
            readLinesAndBufferedReader();
        } catch (IOException e) {
            // 파일 입출력은 검사 예외(IOException)를 던지므로 반드시 처리해야 합니다.
            System.out.println("  파일 입출력 중 오류: " + e.getMessage());
        }

        scannerConcept();
    }

    // ──────────────────────────────────────────────
    // 1. 파일 쓰기/읽기 (Files.writeString / readString)
    // ──────────────────────────────────────────────
    static void writeAndReadFile() throws IOException {
        System.out.println("── 1. 파일 쓰기/읽기 (Files) ──");

        // Files.createTempFile: OS 임시 디렉터리에 임시 파일을 생성합니다.
        // (실습 환경에 영향을 주지 않고 안전하게 테스트할 수 있습니다.)
        Path tempFile = Files.createTempFile("edu-fileio-", ".txt");
        System.out.println("  임시 파일 생성: " + tempFile);

        // 파일 쓰기: 문자열 전체를 한 번에 기록 (Java 11+)
        String content = "첫 번째 줄\n두 번째 줄\n세 번째 줄";
        Files.writeString(tempFile, content);
        System.out.println("  writeString()으로 내용 기록 완료");

        // 파일 읽기: 파일 전체를 하나의 문자열로 읽기 (Java 11+)
        String readBack = Files.readString(tempFile);
        System.out.println("  readString() 결과:");
        readBack.lines().forEach(line -> System.out.println("    | " + line));

        // 정리: 임시 파일 삭제
        Files.deleteIfExists(tempFile);
        System.out.println("  임시 파일 삭제 완료");
        System.out.println();
    }

    // ──────────────────────────────────────────────
    // 2. 줄 단위 읽기 & try-with-resources BufferedReader
    // ──────────────────────────────────────────────
    static void readLinesAndBufferedReader() throws IOException {
        System.out.println("── 2. 줄 단위 읽기 & BufferedReader ──");

        Path tempFile = Files.createTempFile("edu-lines-", ".txt");
        Files.writeString(tempFile, "사과,1000\n바나나,500\n오렌지,1500");

        // Files.readAllLines: 모든 줄을 List<String>으로 읽기
        List<String> lines = Files.readAllLines(tempFile);
        System.out.println("  readAllLines()로 읽은 줄 수: " + lines.size());
        for (String line : lines) {
            String[] parts = line.split(",");
            System.out.println("    상품: " + parts[0] + ", 가격: " + parts[1] + "원");
        }

        // try-with-resources: try() 괄호 안에서 연 자원은 블록이 끝나면 자동으로 close()됩니다.
        // close()를 직접 호출하지 않아도 되어 자원 누수를 방지합니다.
        System.out.println("\n  [try-with-resources로 BufferedReader 사용]");
        try (BufferedReader reader = Files.newBufferedReader(tempFile)) {
            String line;
            int lineNum = 1;
            while ((line = reader.readLine()) != null) {
                System.out.println("    " + lineNum++ + "번 줄: " + line);
            }
        } // 여기서 reader가 자동으로 닫힙니다.

        Files.deleteIfExists(tempFile);
        System.out.println();
    }

    // ──────────────────────────────────────────────
    // 3. Scanner - 콘솔 입력의 개념
    // ──────────────────────────────────────────────
    static void scannerConcept() {
        System.out.println("── 3. Scanner (콘솔 입력 개념) ──");

        // 실제 콘솔 입력은 보통 다음과 같이 작성합니다.
        //   Scanner sc = new Scanner(System.in);
        //   String name = sc.nextLine();   // 사용자가 Enter를 누를 때까지 "대기(블록)"
        //   int age = sc.nextInt();
        // 하지만 위 코드는 입력이 들어올 때까지 프로그램이 멈추므로,
        // 이 예제에서는 비대화형 실행을 위해 "문자열"을 입력 소스로 사용합니다.
        System.out.println("  실제 콘솔 입력 예: new Scanner(System.in)");
        System.out.println("  (사용자 입력을 기다리며 멈추므로 여기선 문자열로 대체)\n");

        // Scanner는 System.in뿐 아니라 문자열도 입력 소스로 받을 수 있습니다.
        String fakeInput = "홍길동 25 서울";
        try (Scanner scanner = new Scanner(fakeInput)) {
            String name = scanner.next();   // 공백 단위로 토큰 읽기
            int age = scanner.nextInt();    // 정수로 읽기
            String city = scanner.next();

            System.out.println("  입력 문자열: \"" + fakeInput + "\"");
            System.out.println("  파싱 결과 → 이름: " + name + ", 나이: " + age + ", 도시: " + city);
        }

        System.out.println("\n  ⚠ Scanner의 주요 메서드:");
        System.out.println("    next()      : 공백 전까지 한 단어");
        System.out.println("    nextLine()  : 한 줄 전체 (개행 전까지)");
        System.out.println("    nextInt()   : 정수, nextDouble(): 실수");
        System.out.println();
    }
}
