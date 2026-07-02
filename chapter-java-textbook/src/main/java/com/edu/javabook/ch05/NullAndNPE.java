package com.edu.javabook.ch05;

import java.util.Objects;

/**
 * 5.4 null과 NullPointerException
 *
 * 참조 타입 변수에는 "아무 객체도 가리키지 않는다"는 의미로 null 을 대입할 수 있다.
 *
 * null 인 변수를 통해 필드/메서드에 접근하면 실행 중 NullPointerException(NPE)이 발생한다.
 *
 * 방지법
 *   - 사용 전 null 검사 (if (x != null))
 *   - Objects.requireNonNull() 로 시작 시점에 명확히 검증
 */
public class NullAndNPE {

    public static void main(String[] args) {

        System.out.println("=== 5.4 null과 NullPointerException ===");

        // [1] null 대입 : 아무 객체도 가리키지 않는 상태
        System.out.println("\n[1] null 대입");
        String text = null;
        System.out.println("text = " + text + "  (아직 어떤 객체도 가리키지 않음)");

        // [2] NPE 발생 : null 을 통해 메서드 호출 시도
        System.out.println("\n[2] NPE 발생 시연 (try-catch로 잡음)");
        try {
            int len = text.length();   // null.length() → NullPointerException
            System.out.println("길이 = " + len);
        } catch (NullPointerException e) {
            System.out.println("NPE 발생! null 변수의 메서드를 호출했기 때문");
        }

        // [3] 방지 1 : 사용 전에 null 검사
        System.out.println("\n[3] 방지 - null 검사");
        System.out.println("safeLength(null)   = " + safeLength(null));
        System.out.println("safeLength(\"java\") = " + safeLength("java"));

        // [4] 방지 2 : Objects.requireNonNull() 로 명확히 거부
        System.out.println("\n[4] 방지 - Objects.requireNonNull()");
        try {
            register(null);   // null 이면 즉시 예외로 알림
        } catch (NullPointerException e) {
            System.out.println("requireNonNull이 막음 → " + e.getMessage());
        }
        register("홍길동");    // 정상 값은 통과

        System.out.println("\n프로그램 정상 종료");
    }

    // null 이면 0을 반환하도록 안전하게 처리
    static int safeLength(String s) {
        if (s == null) {
            return 0;
        }
        return s.length();
    }

    // null 이면 즉시 예외를 던져 잘못된 사용을 조기에 드러냄
    static void register(String name) {
        Objects.requireNonNull(name, "name은 null일 수 없습니다");
        System.out.println("등록 완료 : " + name);
    }
}
