package com.edu.oop;

/**
 * Chapter 02 - 중첩 클래스 (Nested Classes)
 *
 * 클래스 안에 선언하는 클래스의 4가지 형태를 학습합니다.
 *  1. 정적 중첩 클래스 (static nested class)
 *  2. 내부 클래스 (inner class, 비정적)
 *  3. 지역 클래스 (local class) - 메서드 안에 선언
 *  4. 익명 클래스 (anonymous class) - 이름 없이 즉석에서 구현
 */
public class NestedClassExample {

    // 바깥 클래스의 인스턴스 필드
    private String outerField = "바깥 클래스 필드";

    // ======================================================
    // 1. 정적 중첩 클래스 (static nested class)
    //    - 바깥 인스턴스 없이 독립적으로 생성 가능
    //    - 바깥 클래스의 static 멤버만 접근 가능
    // ======================================================
    static class StaticNested {
        void show() {
            System.out.println("    [정적 중첩 클래스] 바깥 인스턴스 없이 생성됩니다.");
        }
    }

    // ======================================================
    // 2. 내부 클래스 (inner class, 비정적)
    //    - 반드시 바깥 인스턴스가 있어야 생성 가능
    //    - 바깥 클래스의 인스턴스 멤버(private 포함)에 접근 가능
    // ======================================================
    class Inner {
        void show() {
            // 바깥 클래스의 private 필드에 직접 접근 가능
            System.out.println("    [내부 클래스] 바깥 필드 접근 → " + outerField);
        }
    }

    // 함수형 인터페이스: 익명 클래스/람다 예제에 사용
    interface Greeting {
        String greet(String name);
    }

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("  Chapter 02 - 중첩 클래스 (Nested Classes)");
        System.out.println("========================================\n");

        demonstrateStaticNested();
        demonstrateInner();
        demonstrateLocalClass();
        demonstrateAnonymousClass();

        System.out.println("========================================");
        System.out.println("  중첩 클래스 예제 완료!");
        System.out.println("========================================");
    }

    // ------------------------------------------------------
    // 1. 정적 중첩 클래스
    // ------------------------------------------------------
    static void demonstrateStaticNested() {
        System.out.println("--- 1. 정적 중첩 클래스 (static nested) ---");
        // 바깥 인스턴스 없이 바로 생성
        StaticNested nested = new StaticNested();
        nested.show();
        System.out.println();
    }

    // ------------------------------------------------------
    // 2. 내부 클래스
    // ------------------------------------------------------
    static void demonstrateInner() {
        System.out.println("--- 2. 내부 클래스 (inner) ---");
        // 내부 클래스는 바깥 인스턴스를 먼저 만든 뒤 생성합니다.
        NestedClassExample outer = new NestedClassExample();
        NestedClassExample.Inner inner = outer.new Inner();  // 바깥인스턴스.new 문법
        inner.show();
        System.out.println();
    }

    // ------------------------------------------------------
    // 3. 지역 클래스 (메서드 안에 선언)
    // ------------------------------------------------------
    static void demonstrateLocalClass() {
        System.out.println("--- 3. 지역 클래스 (local class) ---");

        // 메서드의 지역 변수 (사실상 final이어야 지역 클래스에서 사용 가능)
        String prefix = "[지역 클래스]";

        // 메서드 내부에서만 사용되는 클래스를 정의
        class LocalCounter {
            int count = 0;
            void increment() {
                count++;
                System.out.println("    " + prefix + " count = " + count);
            }
        }

        LocalCounter counter = new LocalCounter();
        counter.increment();
        counter.increment();
        System.out.println();
    }

    // ------------------------------------------------------
    // 4. 익명 클래스 & 람다 비교
    // ------------------------------------------------------
    static void demonstrateAnonymousClass() {
        System.out.println("--- 4. 익명 클래스 (anonymous class) ---");

        // 익명 클래스: 인터페이스를 즉석에서 구현하며 이름 없이 객체를 생성
        Greeting formal = new Greeting() {
            @Override
            public String greet(String name) {
                return name + "님, 안녕하십니까.";
            }
        };
        System.out.println("    익명 클래스 구현: " + formal.greet("홍길동"));

        // 함수형 인터페이스(추상 메서드 1개)는 람다로 더 간결하게 대체할 수 있습니다.
        // 아래 람다는 위 익명 클래스와 동일한 일을 합니다.
        Greeting casual = name -> name + "야, 안녕!";
        System.out.println("    람다로 대체     : " + casual.greet("철수"));

        System.out.println("\n  💡 정리: 추상 메서드가 1개인 함수형 인터페이스는");
        System.out.println("     장황한 익명 클래스 대신 람다 표현식으로 간결하게 작성하세요.");
        System.out.println();
    }
}
