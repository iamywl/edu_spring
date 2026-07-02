package com.edu.javabook.ch09;

/**
 * 9.4 로컬 클래스 (local class)
 *
 * 로컬 클래스는 "메서드 안"에 선언한 클래스다.
 * 지역 변수처럼 그 메서드 안에서만 존재하며, 메서드가 끝나면 이름은 사라진다.
 *
 * 핵심 규칙: effectively final 캡처
 *   - 로컬 클래스는 자신을 감싼 메서드의 "지역 변수/매개변수"를 사용할 수 있다.
 *   - 단, 그 변수는 값이 변하지 않아야 한다. 즉 final 이거나,
 *     final 을 붙이지 않았더라도 "한 번 대입 후 바뀌지 않는(effectively final)"
 *     변수여야 한다.
 *   - 이유: 로컬 클래스 객체는 메서드가 끝난 뒤에도 살아남을 수 있어서,
 *     변하는 지역 변수를 그대로 참조하면 값이 어긋날 수 있기 때문이다.
 *     그래서 자바는 "변하지 않는 값만" 캡처하도록 허용한다.
 *
 * 예제: 인사말 접두사(prefix)를 캡처해서 이름을 붙여 주는 Greeter 를 만든다.
 */
public class LocalClass {

    // 로컬 클래스를 만들어서 반환하는(사용하는) 메서드
    static void makeGreeter(String prefix) {

        // prefix 는 재대입이 없으므로 effectively final → 캡처 가능
        int mark = prefix.length();   // 지역 변수도 이후 변경 없으면 캡처 가능

        // ── 로컬 클래스 : 이 메서드 안에서만 존재 ──
        class Greeter {
            void greet(String name) {
                // 감싼 메서드의 매개변수 prefix, 지역변수 mark 를 캡처해서 사용
                System.out.println(prefix + name + " (prefix 길이=" + mark + ")");
            }
        }

        // 로컬 클래스는 선언한 메서드 안에서 바로 사용한다.
        Greeter g = new Greeter();
        g.greet("홍길동");
        g.greet("이몽룡");
    }

    public static void main(String[] args) {

        System.out.println("=== 9.4 로컬 클래스 ===");

        // [1] 서로 다른 인자로 로컬 클래스가 서로 다른 값을 캡처한다.
        System.out.println("\n[1] prefix \"안녕하세요, \" 캡처");
        makeGreeter("안녕하세요, ");

        System.out.println("\n[2] prefix \"Hello, \" 캡처");
        makeGreeter("Hello, ");

        // [3] effectively final 설명 (주석으로만 제시 — 컴파일 오류 예시)
        System.out.println("\n[3] effectively final 규칙");
        System.out.println("메서드의 지역 변수를 로컬 클래스가 쓰려면 값이 변하면 안 된다.");
        System.out.println("아래처럼 캡처한 변수를 재대입하면 컴파일 오류가 난다:");
        System.out.println("    int n = 1;");
        System.out.println("    class Inner { void f(){ System.out.println(n); } }");
        System.out.println("    n = 2;   // 오류! n 은 effectively final 이 아니게 됨");

        System.out.println("\n정리: 로컬 클래스는 메서드 지역이며, 변하지 않는 값만 캡처한다.");
        System.out.println("프로그램 정상 종료");
    }
}
