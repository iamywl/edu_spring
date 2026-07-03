package com.edu.javabook.ch18;

import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * 18.9 프린트 스트림
 *
 * [PrintStream / PrintWriter]
 * - 데이터를 사람이 읽기 좋은 텍스트 형태로 출력하는 편의 스트림이다.
 * - print(), println(), printf(), format() 등 다양한 출력 메서드를 제공한다.
 * - PrintStream : 바이트 기반. System.out, System.err 이 대표적인 PrintStream이다.
 * - PrintWriter : 문자 기반. 텍스트 출력에 더 적합하다.
 *
 * [특징]
 * - 출력 중 예외(IOException)를 던지지 않고 내부에 담아둔다(checkError()로 확인).
 * - autoFlush 옵션으로 println 등에서 자동 flush 되게 할 수 있다.
 *
 * 이 소절에서는 System.out(PrintStream)과 파일 대상 PrintWriter를 시연한다.
 */
public class PrintStreamDemo {

    public static void main(String[] args) throws IOException {

        System.out.println("=== 18.9 프린트 스트림 ===");

        // [1] System.out 은 PrintStream 이다
        System.out.println("\n[1] System.out 은 PrintStream");
        PrintStream out = System.out; // 표준 출력 = PrintStream
        out.println("  println : 한 줄 출력");
        out.printf("  printf  : 이름=%s, 점수=%d, 비율=%.2f%n", "홍길동", 95, 0.87);
        out.print("  print   : 줄바꿈 없음 -> ");
        out.println("이어서 출력");

        // [2] PrintWriter 로 파일에 서식 출력
        System.out.println("\n[2] PrintWriter 로 파일에 서식 출력");
        Path temp = Files.createTempFile("ch18_9_", ".txt");
        temp.toFile().deleteOnExit();
        System.out.println("  임시 파일: " + temp);

        try (PrintWriter pw = new PrintWriter(
                Files.newBufferedWriter(temp, StandardCharsets.UTF_8))) {
            pw.println("리포트 제목");
            pw.printf("합계: %,d 원%n", 1234567);
            pw.printf("정렬: %-8s|%8s|%n", "left", "right");
        }

        // [3] 파일에 쓴 내용 다시 읽어 확인
        System.out.println("\n[3] 파일 내용 확인");
        try (FileReader fr = new FileReader(temp.toFile(), StandardCharsets.UTF_8)) {
            char[] buf = new char[256];
            int len = fr.read(buf);
            for (String line : new String(buf, 0, len).split("\n")) {
                System.out.println("  " + line);
            }
        }

        System.out.println("\n프로그램 정상 종료");
    }
}
