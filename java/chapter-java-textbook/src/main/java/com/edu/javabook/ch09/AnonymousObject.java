package com.edu.javabook.ch09;

/**
 * 9.7 익명 객체 (익명 클래스)
 *
 * 익명 객체는 "이름이 없는 클래스"를 선언과 동시에 곧바로 하나 생성한 객체다.
 * 어떤 인터페이스나 (추상)클래스를 딱 한 번만 구현해서 쓸 때,
 * 별도의 이름 있는 클래스를 만들지 않고 그 자리에서 즉석으로 정의한다.
 *
 * 문법:  new 부모타입() { ...재정의/추가... }
 *   - 부모타입이 인터페이스면 그 인터페이스를 "구현"한다.
 *   - 부모타입이 (추상)클래스면 그 클래스를 "상속"한다.
 *   - 중괄호 { } 안에서 메서드를 재정의하거나 필드를 추가한다.
 *
 * 특징과 대비:
 *   - 익명 클래스는 effectively final 지역 변수를 캡처할 수 있다(로컬 클래스와 동일).
 *   - 추상 메서드가 여러 개거나 필드/추가 메서드가 필요하면 익명 클래스를 쓴다.
 *   - 추상 메서드가 "정확히 1개"인 함수형 인터페이스라면
 *     익명 클래스 대신 "람다식"으로 훨씬 짧게 쓸 수 있다.
 *
 * 예제: 인터페이스/추상클래스를 익명 클래스로 즉석 구현하고,
 *       마지막에 같은 인터페이스를 람다로 다시 써서 길이를 비교한다.
 */
public class AnonymousObject {

    // 익명 클래스로 구현할 인터페이스 (추상 메서드 1개 → 함수형 인터페이스)
    interface Greeting {
        String greet(String name);
    }

    // 익명 클래스로 상속할 추상 클래스
    static abstract class Animal {
        abstract String sound();

        void describe() {                // 상속받아 그대로 쓰는 일반 메서드
            System.out.println("이 동물의 소리: " + sound());
        }
    }

    public static void main(String[] args) {

        System.out.println("=== 9.7 익명 객체 ===");

        // [1] 인터페이스를 익명 클래스로 즉석 구현
        System.out.println("\n[1] 인터페이스를 익명 클래스로 구현");
        Greeting korean = new Greeting() {
            @Override
            public String greet(String name) {
                return "안녕하세요, " + name + "님";
            }
        };
        System.out.println(korean.greet("홍길동"));

        // [2] 추상 클래스를 익명 클래스로 즉석 상속 (abstract 메서드 구현)
        System.out.println("\n[2] 추상 클래스를 익명 클래스로 상속");
        Animal cat = new Animal() {
            @Override
            String sound() {
                return "야옹";
            }
        };
        cat.describe();

        // [3] 익명 클래스의 지역 변수 캡처 (effectively final)
        System.out.println("\n[3] 지역 변수 캡처 (effectively final)");
        String suffix = "!!!";       // 재대입 없음 → 캡처 가능
        Greeting excited = new Greeting() {
            @Override
            public String greet(String name) {
                return "야호 " + name + suffix;   // 바깥 지역변수 suffix 캡처
            }
        };
        System.out.println(excited.greet("친구"));

        // [4] 람다와 대비 : 함수형 인터페이스는 람다가 더 간결
        System.out.println("\n[4] 같은 인터페이스를 람다로 (더 짧다)");
        Greeting english = name -> "Hello, " + name;
        System.out.println(english.greet("Tom"));

        System.out.println("\n정리: 일회성 구현은 익명 클래스, 메서드 1개면 람다가 간결하다.");
        System.out.println("프로그램 정상 종료");
    }
}
