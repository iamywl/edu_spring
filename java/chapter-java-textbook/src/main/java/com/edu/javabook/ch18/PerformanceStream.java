package com.edu.javabook.ch18;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * 18.7 성능 향상 스트림
 *
 * [버퍼링(Buffering)과 성능]
 * - 파일이나 네트워크에 직접 1바이트/1문자씩 접근하면 매번 느린 I/O가 발생한다.
 * - 보조 스트림 BufferedReader/BufferedWriter는 내부 버퍼(메모리)에 데이터를 모았다가
 *   한 번에 처리하여 실제 I/O 호출 횟수를 크게 줄인다. 그래서 성능이 향상된다.
 *
 * [BufferedReader / BufferedWriter의 편의 메서드]
 * - BufferedReader.readLine() : 한 줄을 통째로 읽는다. 줄 끝(EOF)이면 null을 반환한다.
 * - BufferedWriter.newLine()  : 플랫폼에 맞는 줄바꿈 문자를 쓴다.
 *
 * 이 소절에서는 여러 줄을 쓰고 readLine()으로 줄 단위로 읽는다.
 */
public class PerformanceStream {

    public static void main(String[] args) throws IOException {

        System.out.println("=== 18.7 성능 향상 스트림 ===");

        Path temp = Files.createTempFile("ch18_7_", ".txt");
        temp.toFile().deleteOnExit();
        System.out.println("임시 파일: " + temp);

        // [1] BufferedWriter 로 여러 줄 쓰기
        System.out.println("\n[1] BufferedWriter 로 줄 단위 쓰기");
        try (BufferedWriter bw = new BufferedWriter(
                new FileWriter(temp.toFile(), StandardCharsets.UTF_8))) {
            for (int i = 1; i <= 5; i++) {
                bw.write("라인 " + i + "번 데이터");
                bw.newLine(); // 줄바꿈
            }
        }
        System.out.println("  5줄 기록 완료");

        // [2] BufferedReader.readLine() 로 줄 단위 읽기
        System.out.println("\n[2] BufferedReader.readLine() 로 줄 단위 읽기");
        try (BufferedReader br = new BufferedReader(
                new FileReader(temp.toFile(), StandardCharsets.UTF_8))) {
            String line;
            int no = 0;
            while ((line = br.readLine()) != null) { // null이면 EOF
                no++;
                System.out.println("  " + no + ": " + line);
            }
            System.out.println("  총 " + no + "줄 읽음, readLine()이 null 반환하여 종료");
        }

        // [3] 버퍼링 성능 간단 측정(직접 쓰기 vs 버퍼 쓰기)
        System.out.println("\n[3] 버퍼링 성능 비교(문자 10만 개 쓰기)");
        Path plain = Files.createTempFile("ch18_7_plain_", ".txt");
        Path buffered = Files.createTempFile("ch18_7_buf_", ".txt");
        plain.toFile().deleteOnExit();
        buffered.toFile().deleteOnExit();

        long t1 = System.nanoTime();
        try (FileWriter fw = new FileWriter(plain.toFile(), StandardCharsets.UTF_8)) {
            for (int i = 0; i < 100_000; i++) fw.write('x');
        }
        long plainMs = (System.nanoTime() - t1) / 1_000_000;

        long t2 = System.nanoTime();
        try (BufferedWriter bw = new BufferedWriter(
                new FileWriter(buffered.toFile(), StandardCharsets.UTF_8))) {
            for (int i = 0; i < 100_000; i++) bw.write('x');
        }
        long bufMs = (System.nanoTime() - t2) / 1_000_000;

        System.out.println("  버퍼 없음 소요: " + plainMs + " ms");
        System.out.println("  버퍼 있음 소요: " + bufMs + " ms");
        System.out.println("  → 보통 버퍼링이 더 빠르다(환경에 따라 차이는 다를 수 있음).");

        System.out.println("\n프로그램 정상 종료");
    }
}
