package com.edu.javabook.ch11;

/**
 * 11.3 예외 종류에 따른 처리
 *
 * 예외는 크게 두 종류이며, 처리 방식(강제성)이 다르다.
 *
 * (1) 일반 예외 = checked 예외
 *     - Exception 을 상속하되 RuntimeException 은 상속하지 않는 예외.
 *     - 예) IOException, ClassNotFoundException, ParseException ...
 *     - 컴파일러가 처리를 "강제"한다.
 *       → 반드시 try-catch 로 처리하거나, throws 로 떠넘겨야 컴파일이 된다.
 *     - 개발자가 어쩔 수 없는 외부 요인(파일 없음, 네트워크 끊김 등)에 주로 사용.
 *
 * (2) 실행 예외 = unchecked 예외
 *     - RuntimeException 을 상속하는 예외.
 *     - 예) NullPointerException, ArithmeticException, NumberFormatException ...
 *     - 컴파일러가 처리를 "강제하지 않는다".
 *       → try-catch 없이도 컴파일된다(단, 발생하면 실행 중 종료됨).
 *     - 프로그래머의 부주의(널 참조, 잘못된 형변환 등)로 발생 → 근본은 코드 수정으로 예방.
 *
 * 이 소절에서는 두 종류의 처리 차이(컴파일 강제 여부, 처리 방법)를 확인한다.
 */
public class ExceptionTypes {

    // [일반 예외(checked)] IOException 은 반드시 처리해야 함 → 여기서는 throws 로 떠넘김
    static void readFileLike() throws java.io.IOException {
        // 실제 파일 입출력 대신, checked 예외를 강제로 발생시켜 시연
        throw new java.io.IOException("파일을 찾을 수 없습니다 (checked 예외)");
    }

    // [실행 예외(unchecked)] throws 를 선언하지 않아도 컴파일된다
    static int parseNumber(String text) {
        // 잘못된 문자열이면 NumberFormatException(RuntimeException 자식) 발생
        return Integer.parseInt(text);
    }

    public static void main(String[] args) {

        System.out.println("=== 11.3 예외 종류에 따른 처리 ===");

        // [1] 일반 예외(checked) : 컴파일러가 처리를 강제 → 반드시 try-catch
        System.out.println("\n[1] 일반 예외(checked) 처리 - try-catch 필수");
        try {
            readFileLike();
        } catch (java.io.IOException e) {
            System.out.println("  처리됨: " + e.getMessage());
            System.out.println("  → checked 예외는 컴파일러가 처리를 강제한다.");
        }

        // [2] 실행 예외(unchecked) : 처리하지 않아도 컴파일되지만, 발생하면 종료됨 → 원하면 처리 가능
        System.out.println("\n[2] 실행 예외(unchecked) 처리 - try-catch 는 선택");
        System.out.println("  \"123\" 변환 시도:");
        System.out.println("  결과 = " + parseNumber("123") + " (정상)");

        System.out.println("  \"abc\" 변환 시도(예외 발생 예상):");
        try {
            int n = parseNumber("abc");   // NumberFormatException
            System.out.println("  결과 = " + n);
        } catch (NumberFormatException e) {
            System.out.println("  처리됨: " + e.getMessage());
            System.out.println("  → unchecked 예외는 강제는 아니지만, 안전을 위해 처리할 수 있다.");
        }

        // [3] 두 종류 요약
        System.out.println("\n[3] 요약");
        System.out.println("  checked  (일반) : 컴파일 강제 O, try-catch 또는 throws 필수");
        System.out.println("  unchecked(실행) : 컴파일 강제 X, 근본은 코드 수정으로 예방");

        System.out.println("\n프로그램 정상 종료");
    }
}
