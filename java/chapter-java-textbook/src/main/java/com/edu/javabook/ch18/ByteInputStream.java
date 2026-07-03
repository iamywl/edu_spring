package com.edu.javabook.ch18;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * 18.3 바이트 입력 스트림
 *
 * [FileInputStream]
 * - InputStream의 하위 클래스로, 파일에서 바이트 단위로 데이터를 읽는다.
 * - read()            : 1바이트를 읽어 0~255의 int로 반환한다.
 *                       더 이상 읽을 데이터가 없으면 -1(EOF)을 반환한다.
 * - read(byte[] b)    : 배열 크기만큼 읽고 실제 읽은 바이트 수를 반환한다.
 *
 * [EOF(End Of File) 처리]
 * - read()가 -1을 반환할 때까지 반복하는 것이 표준 패턴이다.
 *   int b;  while ((b = in.read()) != -1) { ... }
 * - 반환 타입이 int인 이유: 유효한 값(0~255)과 EOF(-1)를 구분하기 위해서다.
 *
 * 이 소절에서는 파일을 만들고 바이트 단위로 읽어 EOF까지 처리한다.
 */
public class ByteInputStream {

    public static void main(String[] args) throws IOException {

        System.out.println("=== 18.3 바이트 입력 스트림 ===");

        // 읽을 대상 임시 파일 준비
        Path temp = Files.createTempFile("ch18_3_", ".bin");
        temp.toFile().deleteOnExit();
        Files.write(temp, new byte[]{74, 97, 118, 97}); // "Java"
        System.out.println("임시 파일: " + temp + " (내용: Java)");

        // [1] read() 로 한 바이트씩 EOF(-1)까지 읽기
        System.out.println("\n[1] read() 반복으로 EOF까지 읽기");
        try (FileInputStream fis = new FileInputStream(temp.toFile())) {
            int b;
            int count = 0;
            while ((b = fis.read()) != -1) { // -1이면 파일 끝
                System.out.println("  읽은 값: " + b + " ('" + (char) b + "')");
                count++;
            }
            System.out.println("  총 " + count + " 바이트 읽음, 마지막 read()가 -1 반환");
        }

        // [2] read(byte[]) 로 한 번에 여러 바이트 읽기
        System.out.println("\n[2] read(byte[]) 로 블록 단위 읽기");
        try (FileInputStream fis = new FileInputStream(temp.toFile())) {
            byte[] buf = new byte[10];
            int len = fis.read(buf); // 실제 읽은 바이트 수
            System.out.println("  요청 10바이트 중 실제 읽음: " + len);
            System.out.println("  내용: " + new String(buf, 0, len));
        }

        System.out.println("\n프로그램 정상 종료");
    }
}
