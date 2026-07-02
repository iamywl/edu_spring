package com.edu.javabook.ch13;

/**
 * 13.1 제네릭이란?
 *
 * 제네릭(Generics)은 클래스나 메소드에서 사용할 "타입을 미리 정하지 않고"
 * 사용하는 시점에 타입을 지정하는 기능이다. (JDK 5부터 도입)
 *
 * 왜 필요한가?
 *  - 제네릭 이전에는 여러 타입을 담기 위해 Object 를 사용했다.
 *  - Object 방식은 무엇이든 담을 수 있어 "유연"하지만,
 *    꺼낼 때 반드시 형변환(캐스팅)이 필요하고,
 *    엉뚱한 타입을 넣어도 컴파일 시점에는 오류를 잡지 못한다.
 *  - 잘못된 캐스팅은 실행 중 ClassCastException 을 일으킨다.
 *
 * 제네릭의 장점 :
 *  1) 타입 안전성(Type Safety) : 컴파일 시점에 잘못된 타입 사용을 차단한다.
 *  2) 캐스팅 제거 : 꺼낼 때 형변환이 필요 없다(가독성/안정성 향상).
 *
 * 이 소절에서는 Object 방식과 제네릭 방식을 비교한다.
 */
public class GenericIntro {

    /** 제네릭 이전 방식 : 값을 Object 로 저장하는 상자 */
    static class ObjectBox {
        private Object value;               // 무엇이든 담을 수 있다(유연하지만 위험)
        public void set(Object value) { this.value = value; }
        public Object get() { return value; }
    }

    /** 제네릭 방식 : 타입 파라미터 T 를 쓰는 상자 */
    static class GenericBox<T> {
        private T value;                    // 사용 시점에 지정한 타입만 담긴다
        public void set(T value) { this.value = value; }
        public T get() { return value; }
    }

    public static void main(String[] args) {

        System.out.println("=== 13.1 제네릭이란? ===");

        // [1] Object 방식 : 캐스팅이 필요하다
        System.out.println("\n[1] Object 방식 (제네릭 이전)");
        ObjectBox oBox = new ObjectBox();
        oBox.set("Hello");
        // 꺼낼 때 String 으로 형변환(캐스팅)해야 한다
        String s1 = (String) oBox.get();
        System.out.println("저장한 값 꺼냄(캐스팅 필요): " + s1);
        System.out.println("→ 매번 (String) 처럼 명시적 캐스팅을 해야 한다.");

        // [2] Object 방식의 위험 : ClassCastException
        System.out.println("\n[2] Object 방식의 위험성 (실행 중 오류)");
        ObjectBox danger = new ObjectBox();
        danger.set(100);                    // Integer 를 넣었다
        // 컴파일러는 막지 못한다. 실행 중에 String 으로 캐스팅하면 터진다.
        try {
            String wrong = (String) danger.get();   // Integer -> String 캐스팅 시도
            System.out.println(wrong);
        } catch (ClassCastException e) {
            System.out.println("ClassCastException 발생! 메시지: " + e.getMessage());
            System.out.println("→ 컴파일은 통과했지만 실행 중에 예외가 발생했다(타입 안전하지 않음).");
        }

        // [3] 제네릭 방식 : 캐스팅 제거 + 타입 안전
        System.out.println("\n[3] 제네릭 방식");
        GenericBox<String> gBox = new GenericBox<>();
        gBox.set("World");
        String s2 = gBox.get();             // 캐스팅 불필요! 반환 타입이 String 으로 확정
        System.out.println("저장한 값 꺼냄(캐스팅 없음): " + s2);
        System.out.println("문자열 길이: " + s2.length());

        // GenericBox<String> 에는 String 만 넣을 수 있다.
        // gBox.set(100);  // <- 주석을 풀면 컴파일 오류! (실행 전에 잡아준다)
        System.out.println("\n→ gBox.set(100) 은 아예 컴파일되지 않는다(타입 안전성).");

        // [4] 여러 타입으로 재사용
        System.out.println("\n[4] 하나의 제네릭 클래스를 여러 타입으로 재사용");
        GenericBox<Integer> intBox = new GenericBox<>();
        intBox.set(42);
        int n = intBox.get();               // Integer -> int 언박싱, 캐스팅 없음
        System.out.println("Integer 상자: " + n + " (제곱: " + (n * n) + ")");

        System.out.println("\n프로그램 정상 종료");
    }
}
