package com.edu.javabook.ch08;

/**
 * 8.9 인터페이스 상속
 *
 * 인터페이스도 다른 인터페이스를 extends 로 상속할 수 있다.
 * 클래스와 달리 인터페이스는 "여러 인터페이스를 동시에" 상속할 수 있다(다중 상속).
 *
 *   interface 자식 extends 부모1, 부모2 { ... }
 *
 * → 하위(자식) 인터페이스를 구현하는 클래스는
 *   부모 인터페이스들의 추상 메소드까지 모두 구현해야 한다.
 */
public class InterfaceInheritance {

    interface Reader {
        void read();
    }

    interface Writer {
        void write();
    }

    // 인터페이스가 두 인터페이스를 동시에 상속 (다중 상속)
    interface ReadWriter extends Reader, Writer {
        void flush();   // 자신만의 추상 메소드 추가
    }

    // ReadWriter 구현 → 부모(Reader, Writer)의 메소드까지 모두 구현해야 함
    static class FileStream implements ReadWriter {
        @Override public void read()  { System.out.println("파일에서 읽습니다."); }
        @Override public void write() { System.out.println("파일에 씁니다."); }
        @Override public void flush() { System.out.println("버퍼를 비웁니다."); }
    }

    public static void main(String[] args) {
        System.out.println("=== 8.9 인터페이스 상속 ===");

        FileStream stream = new FileStream();

        // [1] 자식 인터페이스가 부모의 기능까지 모두 포함
        System.out.println("\n[1] 상속으로 모든 규격 통합");
        stream.read();
        stream.write();
        stream.flush();

        // [2] 부모 인터페이스 타입으로도 취급 가능
        System.out.println("\n[2] 부모 인터페이스 타입으로 취급");
        Reader r = stream;   // ReadWriter 는 Reader 를 상속하므로 가능
        r.read();

        System.out.println("\n프로그램 정상 종료");
    }
}
