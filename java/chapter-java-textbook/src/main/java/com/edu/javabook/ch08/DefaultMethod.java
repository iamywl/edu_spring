package com.edu.javabook.ch08;

/**
 * 8.5 디폴트 메소드
 *
 * default 키워드를 붙이면 인터페이스 안에서 메소드의 "몸통(기본 구현)"을
 * 가질 수 있다. 구현 클래스는 이 메소드를 재정의하지 않아도 그대로 쓸 수 있다.
 *
 * - 목적(하위 호환) : 이미 배포된 인터페이스에 새 기능을 추가할 때,
 *                    기존 구현 클래스를 고치지 않아도 되게 해준다.
 * - 구현 클래스는 디폴트 메소드를 "선택적으로" 재정의(override)할 수 있다.
 */
public class DefaultMethod {

    interface Logger {
        void log(String message);       // 추상 메소드 (반드시 구현)

        // 디폴트 메소드 : 기본 구현을 제공한다.
        default void logError(String message) {
            log("[ERROR] " + message);  // 내부에서 추상 메소드를 활용
        }
    }

    // 디폴트 메소드를 그대로 물려받는 구현
    static class ConsoleLogger implements Logger {
        @Override
        public void log(String message) {
            System.out.println("콘솔 출력: " + message);
        }
        // logError 는 재정의하지 않아도 인터페이스의 기본 구현이 쓰인다.
    }

    // 디폴트 메소드를 재정의(override)하는 구현
    static class LoudLogger implements Logger {
        @Override
        public void log(String message) {
            System.out.println("확성기: " + message);
        }
        @Override
        public void logError(String message) {
            log("!!! 긴급 !!! " + message);   // 기본 동작을 덮어씀
        }
    }

    public static void main(String[] args) {
        System.out.println("=== 8.5 디폴트 메소드 ===");

        // [1] 디폴트 메소드를 그대로 사용
        System.out.println("\n[1] 디폴트 메소드 기본 구현 사용");
        Logger console = new ConsoleLogger();
        console.log("정상 메시지");
        console.logError("디스크 공간 부족");   // 재정의 안 했지만 사용 가능

        // [2] 디폴트 메소드를 재정의한 경우
        System.out.println("\n[2] 디폴트 메소드 재정의");
        Logger loud = new LoudLogger();
        loud.logError("서버 다운");

        System.out.println("\n프로그램 정상 종료");
    }
}
