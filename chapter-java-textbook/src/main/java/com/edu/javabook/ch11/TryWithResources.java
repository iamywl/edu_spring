package com.edu.javabook.ch11;

/**
 * 11.4 리소스 자동 닫기 (try-with-resources)
 *
 * 파일, 소켓, DB 연결 같은 "리소스"는 사용 후 반드시 close() 로 닫아야 한다.
 * finally 에서 직접 닫으면 코드가 길고 실수하기 쉽다.
 *
 * try-with-resources 문법 :
 *
 *   try (자원선언1; 자원선언2) {
 *       // 자원 사용
 *   }
 *   // → try 블록이 끝나면(정상/예외 무관) 선언한 자원의 close() 가 "자동" 호출된다.
 *
 * 조건 : try 괄호 안에 선언하는 객체는 반드시 AutoCloseable 인터페이스를 구현해야 한다.
 *        (AutoCloseable 의 close() 메소드가 자동으로 불린다)
 *
 * close 순서 : 여러 자원을 선언하면, "선언의 역순"으로 닫힌다(나중에 연 것 먼저 닫음).
 *             자원은 서로 의존할 수 있으므로(예: A 위에 B), 역순 닫기가 안전하다.
 *
 * 이 소절에서는 AutoCloseable 구현, 자동 close, 역순 close 를 확인한다.
 */
public class TryWithResources {

    // AutoCloseable 을 구현한 가상의 리소스 (파일/연결을 흉내낸다)
    static class Resource implements AutoCloseable {
        private final String name;

        Resource(String name) {
            this.name = name;
            System.out.println("  [열림] " + name + " 자원을 연다.");
        }

        void use() {
            System.out.println("  [사용] " + name + " 자원을 사용한다.");
        }

        // try 블록이 끝나면 자동으로 호출되는 메소드
        @Override
        public void close() {
            System.out.println("  [닫힘] " + name + " 자원을 닫는다(close 호출).");
        }
    }

    public static void main(String[] args) {

        System.out.println("=== 11.4 리소스 자동 닫기 ===");

        // [1] 단일 자원 : try 블록 종료 시 close() 자동 호출
        System.out.println("\n[1] 단일 자원 자동 닫기");
        try (Resource r = new Resource("A")) {
            r.use();
        }   // ← 여기서 r.close() 가 자동으로 불린다 (finally 없이도)

        // [2] 다중 자원 : close 는 선언의 "역순"으로 호출됨 (A, B, C 선언 → C, B, A 닫힘)
        System.out.println("\n[2] 다중 자원 - 선언 역순으로 닫힘");
        try (Resource a = new Resource("A");
             Resource b = new Resource("B");
             Resource c = new Resource("C")) {
            a.use();
            b.use();
            c.use();
        }   // ← C.close() → B.close() → A.close() 순으로 자동 호출

        // [3] 예외가 나도 close 는 반드시 호출됨
        System.out.println("\n[3] 사용 중 예외가 발생해도 close 는 호출됨");
        try (Resource r = new Resource("D")) {
            r.use();
            throw new RuntimeException("사용 중 문제 발생!");
        } catch (RuntimeException e) {
            System.out.println("  [catch] 예외 처리: " + e.getMessage());
        }   // ← 예외가 나기 전에 D.close() 가 이미 자동 호출된다

        System.out.println("\n프로그램 정상 종료");
    }
}
