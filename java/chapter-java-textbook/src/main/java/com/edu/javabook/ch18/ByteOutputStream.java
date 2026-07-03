package com.edu.javabook.ch18;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * 18.2 바이트 출력 스트림
 *
 * [FileOutputStream]
 * - OutputStream의 하위 클래스로, 파일에 바이트 단위로 데이터를 쓴다.
 * - write(int b)      : 1바이트를 쓴다.(int의 하위 8비트만 사용)
 * - write(byte[] b)   : 바이트 배열 전체를 쓴다.
 * - write(byte[] b, int off, int len): 배열의 일부를 쓴다.
 * - flush()           : 내부 버퍼에 남은 데이터를 실제 대상으로 내보낸다.
 * - close()           : 스트림을 닫고 자원을 반환한다.
 *
 * [주의] new FileOutputStream(file, true) 처럼 두 번째 인자를 true로 주면
 *        기존 내용에 이어 쓰기(append)가 된다. 기본은 덮어쓰기다.
 *
 * 이 소절에서는 임시 파일에 바이트를 쓰는 방법을 시연한다.
 */
public class ByteOutputStream {

    public static void main(String[] args) throws IOException {

        System.out.println("=== 18.2 바이트 출력 스트림 ===");

        // 임시 파일 생성(실습 후 JVM 종료 시 자동 삭제)
        Path temp = Files.createTempFile("ch18_2_", ".bin");
        temp.toFile().deleteOnExit();
        System.out.println("임시 파일: " + temp);

        // [1] write(int) 로 1바이트씩 쓰기
        System.out.println("\n[1] write(int) 로 한 바이트씩 쓰기");
        try (FileOutputStream fos = new FileOutputStream(temp.toFile())) {
            fos.write(72);  // 'H'
            fos.write(105); // 'i'
            fos.flush();    // 버퍼 비우기
        }
        System.out.println("  72, 105 두 바이트 기록");

        // [2] write(byte[]) 로 배열 전체 쓰기(append 모드로 이어쓰기)
        System.out.println("\n[2] write(byte[]) 로 배열 이어쓰기(append)");
        byte[] data = {33, 10}; // '!' , 개행
        try (FileOutputStream fos = new FileOutputStream(temp.toFile(), true)) {
            fos.write(data);
        }
        System.out.println("  '!' + 개행 이어쓰기");

        // [3] 결과 확인 - 파일 크기
        System.out.println("\n[3] 결과 확인");
        long size = Files.size(temp);
        System.out.println("  최종 파일 크기: " + size + " byte (기대값 4)");
        byte[] read = Files.readAllBytes(temp);
        System.out.print("  기록된 바이트: ");
        for (byte b : read) {
            System.out.print((b & 0xFF) + " ");
        }
        System.out.println();

        System.out.println("\n프로그램 정상 종료");
    }
}
