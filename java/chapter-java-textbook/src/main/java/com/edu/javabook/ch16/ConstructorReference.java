package com.edu.javabook.ch16;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 16.6 생성자 참조
 *
 * 람다식이 "새 객체를 생성"하기만 한다면, 생성자 참조(Constructor Reference)로
 * 간결하게 표현할 수 있다.
 *
 *   람다식                     생성자 참조
 *   () -> new User()                ->  User::new     (기본 생성자)
 *   name -> new User(name)          ->  User::new     (매개변수 1개 생성자)
 *
 * 형태 :  클래스명::new
 *
 * 어떤 생성자가 호출될지는 "대상 함수형 인터페이스의 시그니처"로 결정된다.
 *  - Supplier<User>          : 인자 0개  -> new User()      가 호출된다.
 *  - Function<String, User>  : 인자 1개  -> new User(String) 가 호출된다.
 */
public class ConstructorReference {

    /** 생성자 참조 대상이 될 클래스 */
    static class User {
        private final String name;

        /** 기본 생성자 (인자 0개) */
        User() {
            this.name = "이름없음";
        }

        /** 매개변수 1개 생성자 */
        User(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return "User(name=" + name + ")";
        }
    }

    public static void main(String[] args) {

        System.out.println("=== 16.6 생성자 참조 ===");

        // [1] Supplier 로 기본 생성자 참조
        System.out.println("\n[1] Supplier<User> + User::new (기본 생성자)");
        // 람다:  () -> new User()
        Supplier<User> defaultFactory = User::new;
        User u1 = defaultFactory.get();     // new User() 호출
        System.out.println("생성된 객체: " + u1);

        // [2] Function 으로 매개변수 1개 생성자 참조
        System.out.println("\n[2] Function<String, User> + User::new (인자 1개 생성자)");
        // 람다:  name -> new User(name)
        Function<String, User> namedFactory = User::new;
        User u2 = namedFactory.apply("홍길동");   // new User("홍길동") 호출
        User u3 = namedFactory.apply("김철수");   // new User("김철수") 호출
        System.out.println("생성된 객체: " + u2);
        System.out.println("생성된 객체: " + u3);

        // [3] 같은 팩토리를 재사용해 여러 객체 생성
        System.out.println("\n[3] 생성자 참조를 팩토리처럼 재사용");
        String[] names = {"이영희", "박민수", "최지우"};
        for (String n : names) {
            System.out.println("  -> " + namedFactory.apply(n));
        }

        // [4] 정리
        System.out.println("\n[4] 정리");
        System.out.println("생성자 참조 형태: 클래스명::new");
        System.out.println("어떤 생성자를 호출할지는 대상 함수형 인터페이스가 결정한다.");

        System.out.println("\n프로그램 정상 종료");
    }
}
