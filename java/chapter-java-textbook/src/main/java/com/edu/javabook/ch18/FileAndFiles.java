package com.edu.javabook.ch18;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * 18.11 File과 Files 클래스
 *
 * [java.io.File]
 * - 파일/디렉터리의 경로를 표현하는 전통적인 클래스다.
 * - exists(), length(), isDirectory(), list(), mkdir(), delete() 등을 제공한다.
 * - 파일의 존재 여부나 크기 같은 메타 정보를 다루며, 실제 입출력은 스트림이 담당한다.
 *
 * [java.nio.file.Files]
 * - NIO.2에서 도입된 유틸리티 클래스로, Path와 함께 사용한다.
 * - 파일 읽기/쓰기를 한 줄로 처리하는 정적 메서드를 제공하여 편리하다.
 *   Files.write(path, bytes), Files.readAllLines(path), Files.size(path) 등.
 * - 현대 자바에서는 File보다 Path/Files 사용이 권장된다.
 *
 * 이 소절에서는 File과 Files를 비교하고 Files의 간편 입출력을 시연한다.
 */
public class FileAndFiles {

    public static void main(String[] args) throws IOException {

        System.out.println("=== 18.11 File과 Files 클래스 ===");

        Path temp = Files.createTempFile("ch18_11_", ".txt");
        temp.toFile().deleteOnExit();
        System.out.println("임시 파일: " + temp);

        // [1] Files.write / readAllLines 로 간편 입출력
        System.out.println("\n[1] Files 로 간편하게 쓰고 읽기");
        List<String> lines = List.of("첫째 줄", "둘째 줄", "셋째 줄");
        Files.write(temp, lines, StandardCharsets.UTF_8); // 리스트를 한 번에 파일로 저장
        System.out.println("  Files.write() 로 3줄 저장");

        List<String> read = Files.readAllLines(temp, StandardCharsets.UTF_8);
        System.out.println("  Files.readAllLines() 로 읽음:");
        for (String line : read) {
            System.out.println("    " + line);
        }

        // [2] File 클래스로 메타 정보 조회
        System.out.println("\n[2] File 클래스로 메타 정보 조회");
        File file = temp.toFile();
        System.out.println("  파일 이름   : " + file.getName());
        System.out.println("  존재 여부   : " + file.exists());
        System.out.println("  디렉터리?   : " + file.isDirectory());
        System.out.println("  파일 크기   : " + file.length() + " byte");

        // [3] Files 로 동일 정보 조회 (File vs Files 비교)
        System.out.println("\n[3] Files 로 동일 정보 조회");
        System.out.println("  Files.exists  : " + Files.exists(temp));
        System.out.println("  Files.size    : " + Files.size(temp) + " byte");
        System.out.println("  Files.isDir   : " + Files.isDirectory(temp));

        // [4] 디렉터리 목록 조회 (임시 파일이 위치한 디렉터리)
        System.out.println("\n[4] 상위 디렉터리 목록(File.list, 최대 3개)");
        File parent = file.getParentFile();
        String[] children = parent.list();
        if (children != null) {
            int limit = Math.min(3, children.length);
            for (int i = 0; i < limit; i++) {
                System.out.println("    - " + children[i]);
            }
            System.out.println("  ... 총 " + children.length + "개 항목");
        }

        // [5] 정리
        System.out.println("\n[5] 정리");
        System.out.println("  - File: 전통적 경로/메타 정보 표현.");
        System.out.println("  - Files: Path 기반의 간편한 입출력 유틸리티(권장).");

        System.out.println("\n프로그램 정상 종료");
    }
}
