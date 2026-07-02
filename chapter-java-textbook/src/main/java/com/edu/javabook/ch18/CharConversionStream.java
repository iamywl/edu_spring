package com.edu.javabook.ch18;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * 18.6 문자 변환 스트림
 *
 * [문자 변환 보조 스트림]
 * - InputStreamReader  : 바이트 입력 스트림을 문자 입력 스트림으로 변환한다.
 * - OutputStreamWriter : 문자 출력 스트림을 바이트 출력 스트림으로 변환한다.
 * - 바이트 스트림 <-> 문자 스트림 사이의 다리 역할을 한다.
 *
 * [인코딩 지정의 중요성]
 * - 변환 시 어떤 문자 집합(charset)을 쓸지 명시할 수 있다.
 * - 인코딩을 명시하지 않으면 플랫폼 기본 인코딩을 따르므로, 서로 다른 환경에서
 *   한글이 깨질 수 있다. 그래서 UTF-8 등을 명시하는 것이 안전하다.
 *
 * 이 소절에서는 바이트 스트림을 문자 스트림으로 변환하며 인코딩을 지정한다.
 */
public class CharConversionStream {

    public static void main(String[] args) throws IOException {

        System.out.println("=== 18.6 문자 변환 스트림 ===");

        Path temp = Files.createTempFile("ch18_6_", ".txt");
        temp.toFile().deleteOnExit();
        System.out.println("임시 파일: " + temp);

        // [1] OutputStreamWriter: 문자 -> 바이트 변환하여 쓰기(UTF-8 지정)
        System.out.println("\n[1] OutputStreamWriter 로 인코딩 지정하여 쓰기");
        System.out.println("  구조: OutputStreamWriter( FileOutputStream, UTF-8 )");
        try (OutputStreamWriter osw = new OutputStreamWriter(
                new FileOutputStream(temp.toFile()), StandardCharsets.UTF_8)) {
            osw.write("변환 스트림 테스트\n");
            osw.write("encoding = UTF-8");
        }
        System.out.println("  UTF-8로 인코딩하여 기록 완료");

        // [2] InputStreamReader: 바이트 -> 문자 변환하여 읽기(UTF-8 지정)
        System.out.println("\n[2] InputStreamReader 로 인코딩 지정하여 읽기");
        System.out.println("  구조: InputStreamReader( FileInputStream, UTF-8 )");
        try (InputStreamReader isr = new InputStreamReader(
                new FileInputStream(temp.toFile()), StandardCharsets.UTF_8)) {
            char[] buf = new char[64];
            int len = isr.read(buf);
            System.out.println("  읽은 문자 수: " + len);
            System.out.println("  내용: " + new String(buf, 0, len).replace("\n", " / "));
        }

        // [3] 인코딩 불일치 시 문제
        System.out.println("\n[3] 인코딩 정리");
        System.out.println("  - 쓸 때와 읽을 때 인코딩이 다르면 한글이 깨진다.");
        System.out.println("  - 변환 스트림은 인코딩을 명시적으로 지정할 수 있어 유용하다.");

        System.out.println("\n프로그램 정상 종료");
    }
}
