package com.edu.javabook.ch11;

/**
 * 11.2 예외 처리 코드
 *
 * 예외가 발생해도 프로그램이 갑자기 종료되지 않도록 처리하는 문법이 try-catch-finally 이다.
 *
 *   try {
 *       // 예외가 발생할 수 있는 코드
 *   } catch (예외타입 e) {
 *       // 예외가 발생했을 때 실행 (복구/알림)
 *   } finally {
 *       // 예외 발생 여부와 상관없이 "항상" 실행 (자원 정리 등)
 *   }
 *
 * 흐름 :
 *  - 예외가 없으면 : try 전체 실행 → finally 실행 (catch 는 건너뜀)
 *  - 예외가 있으면 : try 도중 중단 → 맞는 catch 실행 → finally 실행
 *
 * 다중 catch : 발생 가능한 예외가 여러 종류일 때, 종류별로 catch 를 나열한다.
 *             위에서부터 순서대로 검사하므로, "자식 예외 → 부모 예외" 순으로 써야 한다.
 *
 * 이 소절에서는 try-catch-finally 실행 흐름과 다중 catch 를 확인한다.
 */
public class TryCatchFinally {

    // 정수 나눗셈을 수행하되, 배열 접근/0 나눗셈 예외가 날 수 있는 메소드
    static void divide(int[] arr, int index, int divisor) {
        try {
            System.out.println("  [try] 시작 : arr[" + index + "] / " + divisor);
            int value = arr[index];           // ArrayIndexOutOfBoundsException 가능
            int result = value / divisor;     // ArithmeticException 가능
            System.out.println("  [try] 결과 = " + result);
        } catch (ArithmeticException e) {
            // 다중 catch (1) : 0으로 나누기
            System.out.println("  [catch:Arithmetic] 0으로 나눌 수 없음 → " + e.getMessage());
        } catch (ArrayIndexOutOfBoundsException e) {
            // 다중 catch (2) : 배열 범위 초과
            System.out.println("  [catch:ArrayIndex] 잘못된 인덱스 접근 → " + e.getMessage());
        } catch (Exception e) {
            // 다중 catch (3) : 그 외 모든 예외(부모 타입은 반드시 마지막)
            System.out.println("  [catch:Exception] 기타 예외 → " + e.getMessage());
        } finally {
            // 예외 발생 여부와 무관하게 항상 실행
            System.out.println("  [finally] 정리 코드 실행(항상 수행)");
        }
    }

    public static void main(String[] args) {

        System.out.println("=== 11.2 예외 처리 코드 ===");

        int[] arr = {100, 200, 300};

        // [1] 정상 흐름 : 예외 없음 → try 전체 + finally 실행, catch 건너뜀
        System.out.println("\n[1] 예외가 없는 경우");
        divide(arr, 0, 5);

        // [2] ArithmeticException : 0으로 나눔 → 첫 번째 catch
        System.out.println("\n[2] 0으로 나누는 경우 (ArithmeticException)");
        divide(arr, 1, 0);

        // [3] ArrayIndexOutOfBoundsException : 없는 인덱스 → 두 번째 catch
        System.out.println("\n[3] 배열 범위를 벗어나는 경우 (ArrayIndexOutOfBounds)");
        divide(arr, 9, 5);

        // [4] finally 가 항상 실행됨을 강조
        System.out.println("\n[4] 위 세 경우 모두 [finally] 가 실행되었음을 확인");

        System.out.println("\n프로그램 정상 종료");
    }
}
