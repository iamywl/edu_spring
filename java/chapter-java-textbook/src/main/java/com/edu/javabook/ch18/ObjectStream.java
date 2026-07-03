package com.edu.javabook.ch18;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * 18.10 객체 스트림
 *
 * [객체 직렬화(Serialization)]
 * - 객체를 바이트 형태로 변환하여 저장/전송하는 것을 직렬화라 한다.
 *   반대로 바이트를 객체로 복원하는 것을 역직렬화(Deserialization)라 한다.
 * - ObjectOutputStream.writeObject() 로 직렬화하고,
 *   ObjectInputStream.readObject() 로 역직렬화한다.
 *
 * [필수 조건과 키워드]
 * - Serializable    : 직렬화 대상 클래스는 이 마커 인터페이스를 구현해야 한다.
 * - transient       : 이 키워드가 붙은 필드는 직렬화에서 제외된다(복원 시 기본값).
 * - serialVersionUID: 클래스 버전 식별자. 직렬화/역직렬화 호환성 검증에 쓰인다.
 *
 * 이 소절에서는 객체를 파일에 저장하고 다시 읽으며 transient 동작을 확인한다.
 */
public class ObjectStream {

    // 직렬화 대상 클래스
    static class Member implements Serializable {
        private static final long serialVersionUID = 1L; // 버전 식별자
        private final String name;   // 직렬화 대상
        private final int age;       // 직렬화 대상
        private transient String password; // 직렬화 제외 대상

        Member(String name, int age, String password) {
            this.name = name;
            this.age = age;
            this.password = password;
        }

        @Override
        public String toString() {
            return "Member{name=" + name + ", age=" + age
                    + ", password=" + password + "}";
        }
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {

        System.out.println("=== 18.10 객체 스트림 ===");

        Path temp = Files.createTempFile("ch18_10_", ".ser");
        temp.toFile().deleteOnExit();
        System.out.println("임시 파일: " + temp);

        Member origin = new Member("김자바", 28, "secret1234");
        System.out.println("\n[1] 직렬화 전 객체");
        System.out.println("  " + origin);

        // [2] ObjectOutputStream 으로 객체 직렬화
        System.out.println("\n[2] writeObject() 로 직렬화하여 저장");
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(temp.toFile()))) {
            oos.writeObject(origin);
        }
        System.out.println("  객체를 파일에 저장 완료");

        // [3] ObjectInputStream 으로 객체 역직렬화
        System.out.println("\n[3] readObject() 로 역직렬화하여 복원");
        Member restored;
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(temp.toFile()))) {
            restored = (Member) ois.readObject();
        }
        System.out.println("  복원된 객체: " + restored);

        // [4] transient 동작 확인
        System.out.println("\n[4] transient 확인");
        System.out.println("  password 는 transient 라 복원 시 null 이 된다.");
        System.out.println("  복원된 password = " + restored.password);

        System.out.println("\n프로그램 정상 종료");
    }
}
