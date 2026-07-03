package com.edu.javabook.ch05;

/**
 * 5.11 main(String[] args) 매개변수
 *
 * 프로그램 시작점인 main 메서드는 String 배열 args 를 매개변수로 받는다.
 * 이는 "명령행 인자(command-line arguments)"를 받기 위한 것이다.
 *
 *   java MainStringArgs 사과 바나나 포도
 *   → args = { "사과", "바나나", "포도" }
 *
 * - 인자가 없으면 args.length 는 0 (null 이 아니라 길이 0인 배열).
 * - 모든 인자는 String 이므로, 숫자로 쓰려면 Integer.parseInt 등으로 변환한다.
 *
 * (이 예제는 비대화형이라, main 안에서 예시 args 배열을 직접 만들어 시연한다.)
 */
public class MainStringArgs {

    public static void main(String[] args) {

        System.out.println("=== 5.11 main(String[] args) 매개변수 ===");

        // [1] 실제 전달된 args 표시 (없으면 길이 0)
        System.out.println("\n[1] 실제 전달된 args");
        System.out.println("args.length = " + args.length);
        for (int i = 0; i < args.length; i++) {
            System.out.println("args[" + i + "] = " + args[i]);
        }
        if (args.length == 0) {
            System.out.println("(전달된 인자 없음 → 아래에서 예시 배열로 시연)");
        }

        // [2] 예시 args 배열을 직접 만들어 처리 방식 시연
        System.out.println("\n[2] 예시 args 로 시연");
        String[] sample = { "사과", "바나나", "포도" };
        for (int i = 0; i < sample.length; i++) {
            System.out.println("sample[" + i + "] = " + sample[i]);
        }

        // [3] 인자는 모두 문자열 → 숫자로 쓰려면 변환 필요
        System.out.println("\n[3] 문자열 인자를 숫자로 변환");
        String[] numberArgs = { "10", "20", "30" };
        int total = 0;
        for (String s : numberArgs) {
            total += Integer.parseInt(s);   // "10" → 10 으로 변환
        }
        System.out.println("문자열 인자 합계 = " + total);

        System.out.println("\n프로그램 정상 종료");
    }
}
