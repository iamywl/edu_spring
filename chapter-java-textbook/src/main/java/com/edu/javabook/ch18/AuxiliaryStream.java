package com.edu.javabook.ch18;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * 18.5 보조 스트림
 *
 * [보조 스트림(Filter Stream)이란]
 * - 자체적으로 데이터를 읽거나 쓰지 못한다. 대신 다른 스트림에 연결되어
 *   부가 기능(버퍼링, 문자 변환, 기본 타입 처리 등)을 제공한다.
 * - "스트림을 감싼다(wrapping)"고 표현한다. 데코레이터(Decorator) 패턴이다.
 *
 * [연결 구조]
 * - 대상 스트림(FileOutputStream) 위에 보조 스트림(BufferedOutputStream)을 씌운다.
 *   new BufferedOutputStream(new FileOutputStream(...))
 * - 여러 보조 스트림을 겹겹이 연결할 수도 있다.
 * - 바깥쪽 스트림만 close() 하면 연결된 안쪽 스트림까지 함께 닫힌다.
 *
 * 이 소절에서는 BufferedXxx 보조 스트림으로 기반 스트림을 감싸는 방법을 보인다.
 */
public class AuxiliaryStream {

    public static void main(String[] args) throws IOException {

        System.out.println("=== 18.5 보조 스트림 ===");

        Path temp = Files.createTempFile("ch18_5_", ".bin");
        temp.toFile().deleteOnExit();
        System.out.println("임시 파일: " + temp);

        // [1] 보조 스트림으로 기반 스트림 감싸기(쓰기)
        System.out.println("\n[1] BufferedOutputStream 으로 감싸서 쓰기");
        System.out.println("  구조: BufferedOutputStream( FileOutputStream )");
        try (BufferedOutputStream bos =
                     new BufferedOutputStream(new FileOutputStream(temp.toFile()))) {
            for (int i = 0; i < 5; i++) {
                bos.write(i); // 일단 내부 버퍼에 쌓임
            }
            bos.flush();      // 버퍼 내용을 실제 파일로 내보냄
            System.out.println("  0~4를 버퍼를 거쳐 기록");
        } // 바깥 스트림 close() → 안쪽 FileOutputStream 도 함께 닫힘

        // [2] 보조 스트림으로 기반 스트림 감싸기(읽기)
        System.out.println("\n[2] BufferedInputStream 으로 감싸서 읽기");
        System.out.println("  구조: BufferedInputStream( FileInputStream )");
        try (BufferedInputStream bis =
                     new BufferedInputStream(new FileInputStream(temp.toFile()))) {
            int b;
            System.out.print("  읽은 값: ");
            while ((b = bis.read()) != -1) {
                System.out.print(b + " ");
            }
            System.out.println();
        }

        // [3] 보조 스트림의 이점 정리
        System.out.println("\n[3] 보조 스트림 정리");
        System.out.println("  - 스스로 입출력하지 못하고 기반 스트림에 기능을 덧붙인다.");
        System.out.println("  - 바깥 스트림만 닫으면 연결된 스트림이 연쇄적으로 닫힌다.");

        System.out.println("\n프로그램 정상 종료");
    }
}
