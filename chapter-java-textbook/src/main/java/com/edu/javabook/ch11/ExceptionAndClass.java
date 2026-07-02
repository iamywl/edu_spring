package com.edu.javabook.ch11;

/**
 * 11.1 예외와 예외 클래스
 *
 * 프로그램 실행 중 발생하는 비정상 상황은 크게 두 가지로 나뉜다.
 *
 * - 에러(Error)   : JVM 자체의 심각한 문제(메모리 부족 등). 코드로 복구할 수 없다.
 * - 예외(Exception): 사용자의 잘못된 입력, 없는 파일 접근 등 "복구 가능한" 비정상 상황.
 *                   예외는 코드로 처리(try-catch)하여 프로그램을 정상 흐름으로 되돌릴 수 있다.
 *
 * 클래스 계층 구조 :
 *
 *   Object
 *     └─ Throwable            ← 예외/에러의 최상위 조상 (throw 가능한 것)
 *          ├─ Error           ← 시스템 레벨 심각한 오류 (처리 대상 아님)
 *          └─ Exception       ← 예외의 최상위
 *               ├─ (일반 예외) IOException, ClassNotFoundException ...  → checked
 *               └─ RuntimeException                                     → unchecked
 *                    ├─ NullPointerException
 *                    ├─ ArrayIndexOutOfBoundsException
 *                    └─ ArithmeticException ...
 *
 * checked(일반 예외)   : 컴파일러가 처리를 "강제"한다. try-catch 또는 throws 필수.
 * unchecked(실행 예외) : RuntimeException 의 자식. 컴파일러가 강제하지 않는다(프로그래머 부주의).
 *
 * 이 소절에서는 계층 구조와 checked/unchecked 개념만 확인한다.
 */
public class ExceptionAndClass {

    public static void main(String[] args) {

        System.out.println("=== 11.1 예외와 예외 클래스 ===");

        // [1] Throwable 을 최상위로 하는 계층 구조 확인
        System.out.println("\n[1] 예외 클래스 계층 구조");
        Exception e = new ArithmeticException("0으로 나눔");
        System.out.println("ArithmeticException 인스턴스: " + e.getClass().getName());
        System.out.println("Exception 의 자식인가?  " + (e instanceof Exception));
        System.out.println("RuntimeException 의 자식인가? " + (e instanceof RuntimeException));
        System.out.println("Throwable 의 자식인가?   " + (e instanceof Throwable));

        // 상속 사슬을 위로 거슬러 올라가며 출력
        System.out.println("\n상속 사슬(자식 → 부모):");
        Class<?> c = e.getClass();
        while (c != null) {
            System.out.println("  " + c.getSimpleName());
            c = c.getSuperclass();
        }

        // [2] Error 는 예외가 아님(복구 대상이 아님)을 개념적으로 확인
        System.out.println("\n[2] Error vs Exception");
        Throwable err = new StackOverflowError();   // Error 의 한 종류
        System.out.println("StackOverflowError 는 Error 인가?     " + (err instanceof Error));
        System.out.println("StackOverflowError 는 Exception 인가? " + (err instanceof Exception));
        System.out.println("→ Error 는 Exception 이 아니므로 보통 try-catch 대상이 아니다.");

        // [3] checked vs unchecked 구분
        System.out.println("\n[3] checked(일반 예외) vs unchecked(실행 예외)");
        System.out.println("checked   예: java.io.IOException (컴파일러가 처리 강제)");
        System.out.println("unchecked 예: NullPointerException (RuntimeException 자식, 강제 아님)");

        Exception checked = new java.io.IOException("파일 없음");
        Exception unchecked = new NullPointerException("널 참조");
        System.out.println("IOException 이 RuntimeException? " + (checked instanceof RuntimeException)
                + " → false 이면 checked(일반 예외)");
        System.out.println("NPE 가 RuntimeException?         " + (unchecked instanceof RuntimeException)
                + " → true 이면 unchecked(실행 예외)");

        System.out.println("\n프로그램 정상 종료");
    }
}
