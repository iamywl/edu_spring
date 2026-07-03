package com.edu.javabook.ch18;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * 18.8 기본 타입 스트림
 *
 * [DataOutputStream / DataInputStream]
 * - 자바의 기본 타입(int, double, boolean, char 등)과 문자열을 이진(binary)
 *   형태 그대로 읽고 쓰는 보조 스트림이다.
 * - writeInt/readInt, writeDouble/readDouble, writeBoolean/readBoolean,
 *   writeUTF/readUTF(문자열, 수정된 UTF-8) 등의 메서드를 제공한다.
 *
 * [주의] 쓴 순서와 읽는 순서가 정확히 일치해야 한다.
 *        int로 썼으면 int로 읽어야 하며, 순서가 어긋나면 값이 깨진다.
 *
 * 이 소절에서는 여러 기본 타입을 순서대로 쓰고 같은 순서로 읽는다.
 */
public class DataStream {

    public static void main(String[] args) throws IOException {

        System.out.println("=== 18.8 기본 타입 스트림 ===");

        Path temp = Files.createTempFile("ch18_8_", ".dat");
        temp.toFile().deleteOnExit();
        System.out.println("임시 파일: " + temp);

        // [1] DataOutputStream 으로 기본 타입 쓰기
        System.out.println("\n[1] DataOutputStream 으로 기본 타입 쓰기");
        try (DataOutputStream dos = new DataOutputStream(
                new FileOutputStream(temp.toFile()))) {
            dos.writeUTF("상품A");   // 문자열
            dos.writeInt(30000);      // int
            dos.writeDouble(4.5);     // double
            dos.writeBoolean(true);   // boolean
        }
        System.out.println("  writeUTF, writeInt, writeDouble, writeBoolean 순서로 기록");

        // [2] DataInputStream 으로 같은 순서로 읽기
        System.out.println("\n[2] DataInputStream 으로 같은 순서로 읽기");
        try (DataInputStream dis = new DataInputStream(
                new FileInputStream(temp.toFile()))) {
            String name = dis.readUTF();       // 쓴 순서와 동일하게
            int price = dis.readInt();
            double rating = dis.readDouble();
            boolean inStock = dis.readBoolean();

            System.out.println("  상품명 : " + name);
            System.out.println("  가격   : " + price);
            System.out.println("  평점   : " + rating);
            System.out.println("  재고   : " + inStock);
        }

        // [3] 순서의 중요성
        System.out.println("\n[3] 정리");
        System.out.println("  - 기본 타입을 이진 형태로 저장하므로 사람이 읽기는 어렵다.");
        System.out.println("  - 쓴 타입/순서 그대로 읽어야 데이터가 올바르게 복원된다.");

        System.out.println("\n프로그램 정상 종료");
    }
}
