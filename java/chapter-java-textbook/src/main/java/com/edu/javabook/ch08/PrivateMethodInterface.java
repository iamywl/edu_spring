package com.edu.javabook.ch08;

/**
 * 8.7 private 메소드
 *
 * 자바 9부터 인터페이스에 private 메소드를 둘 수 있다.
 * 인터페이스 "내부에서만" 쓰이는 공통 코드를 담는 용도이며,
 * 구현 클래스나 외부에서는 호출할 수 없다.
 *
 * - private 인스턴스 메소드 : default 메소드들이 공유하는 공통 코드
 * - private static 메소드   : static 메소드들이 공유하는 공통 코드
 * → 여러 default 메소드의 "중복 코드"를 한 곳에 모아 재사용한다.
 */
public class PrivateMethodInterface {

    interface Notifier {
        String name();   // 추상 메소드

        // 두 디폴트 메소드가 같은 형식의 머리말을 필요로 한다.
        default void sendInfo(String msg) {
            System.out.println(format("INFO", msg));   // private 메소드 재사용
        }
        default void sendWarn(String msg) {
            System.out.println(format("WARN", msg));   // private 메소드 재사용
        }

        // private 메소드 : 위 두 디폴트 메소드가 공유하는 공통 포맷 로직
        private String format(String level, String msg) {
            return "[" + level + "] (" + name() + ") " + msg;
        }
    }

    static class EmailNotifier implements Notifier {
        @Override
        public String name() {
            return "이메일";
        }
    }

    public static void main(String[] args) {
        System.out.println("=== 8.7 private 메소드 ===");

        // [1] default 메소드 호출 → 내부에서 private 메소드가 공통 포맷 처리
        System.out.println("\n[1] default 메소드 + private 공통 로직");
        Notifier notifier = new EmailNotifier();
        notifier.sendInfo("가입을 환영합니다.");
        notifier.sendWarn("비밀번호가 곧 만료됩니다.");

        // [2] private 메소드는 외부에서 직접 호출 불가
        System.out.println("\n[2] private 메소드는 외부 호출 불가");
        // notifier.format("INFO", "x");   // 컴파일 오류: has private access
        System.out.println("notifier.format(...) 호출은 컴파일 오류가 발생한다.");

        System.out.println("\n프로그램 정상 종료");
    }
}
