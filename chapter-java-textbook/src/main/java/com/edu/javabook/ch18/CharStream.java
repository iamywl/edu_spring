package com.edu.javabook.ch18;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * 18.4 문자 입출력 스트림
 *
 * [FileWriter / FileReader]
 * - Writer/Reader의 하위 클래스로, 텍스트 파일을 문자 단위로 읽고 쓴다.
 * - 바이트 스트림과 달리 문자(char)를 처리하므로 텍스트 처리에 적합하다.
 * - write(String), write(char[]) 로 쓰고, read() 로 한 문자씩 읽는다.
 *
 * [한글과 인코딩]
 * - 문자 스트림은 문자를 바이트로 바꿀 때 인코딩(charset)을 사용한다.
 * - JDK 11부터 FileWriter/FileReader에 Charset 인자를 지정할 수 있다.
 *   한글 처리 시 UTF-8을 명시하면 플랫폼 기본 인코딩에 의존하지 않아 안전하다.
 *
 * 이 소절에서는 UTF-8로 한글 텍스트를 쓰고 다시 읽는다.
 */
public class CharStream {

    public static void main(String[] args) throws IOException {

        System.out.println("=== 18.4 문자 입출력 스트림 ===");

        Path temp = Files.createTempFile("ch18_4_", ".txt");
        temp.toFile().deleteOnExit();
        System.out.println("임시 파일: " + temp);

        // [1] FileWriter 로 한글 텍스트 쓰기(UTF-8 지정)
        System.out.println("\n[1] FileWriter 로 한글 쓰기 (UTF-8)");
        try (FileWriter writer = new FileWriter(temp.toFile(), StandardCharsets.UTF_8)) {
            writer.write("안녕하세요\n");     // 문자열 쓰기
            writer.write("자바 입출력\n");
            writer.write('!');               // 단일 문자 쓰기
        }
        System.out.println("  한글 텍스트 기록 완료");

        // [2] FileReader 로 한 문자씩 읽기(UTF-8 지정)
        System.out.println("\n[2] FileReader 로 한 문자씩 읽기 (UTF-8)");
        StringBuilder sb = new StringBuilder();
        try (FileReader reader = new FileReader(temp.toFile(), StandardCharsets.UTF_8)) {
            int c;
            while ((c = reader.read()) != -1) { // -1이면 EOF
                sb.append((char) c);
            }
        }
        System.out.println("  읽은 내용:");
        for (String line : sb.toString().split("\n")) {
            System.out.println("    " + line);
        }

        // [3] 인코딩이 중요한 이유
        System.out.println("\n[3] 인코딩 확인");
        System.out.println("  UTF-8에서 한글 한 글자는 보통 3바이트다.");
        System.out.println("  '가' 의 UTF-8 바이트 수: "
                + "가".getBytes(StandardCharsets.UTF_8).length);

        System.out.println("\n프로그램 정상 종료");
    }
}
