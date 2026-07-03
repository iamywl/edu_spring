package com.edu.javabook.ch21;

/**
 * 21.4 switch 패턴 매칭
 *
 * [switch 패턴 매칭이란]
 * - Java 21에서 정식화된 기능으로, switch의 case에 값 상수뿐 아니라
 *   "타입 패턴"을 쓸 수 있게 되었다.
 * - case 타입 변수  형태로 작성하면, 값이 그 타입이면 매칭되고
 *   동시에 그 타입으로 캐스팅된 변수를 바로 사용할 수 있다.
 *
 * [when 가드]
 * - case 타입 변수 when 조건  형태로 추가 조건을 붙일 수 있다.
 *   타입이 맞더라도 when 조건이 참일 때만 그 분기가 선택된다.
 *
 * [장점]
 * - 예전의 if - else if + instanceof + 명시적 캐스팅의 사슬을
 *   간결하고 안전한 switch 하나로 표현할 수 있다.
 * - 컴파일러가 분기 완전성(exhaustiveness)을 검사해준다.
 */
public class SwitchPattern {

    public static void main(String[] args) {

        System.out.println("=== 21.4 switch 패턴 매칭 ===");

        // [1] 타입 패턴: 값의 타입에 따라 분기
        System.out.println("\n[1] 타입 패턴으로 분기");
        System.out.println("  정수 100      -> " + describe(100));
        System.out.println("  실수 3.14     -> " + describe(3.14));
        System.out.println("  문자열 hello  -> " + describe("hello"));
        System.out.println("  null          -> " + describe(null));

        // [2] when 가드: 같은 타입이라도 조건으로 세분화
        System.out.println("\n[2] when 가드로 조건 추가");
        System.out.println("  -5   -> " + classifyNumber(-5));
        System.out.println("  0    -> " + classifyNumber(0));
        System.out.println("  7    -> " + classifyNumber(7));
        System.out.println("  1000 -> " + classifyNumber(1000));

        // [3] 패턴 매칭으로 별도 캐스팅 없이 메서드 호출
        System.out.println("\n[3] 캐스팅 없이 값 사용");
        System.out.println("  \"Java\"의 길이 정보 -> " + lengthInfo("Java"));
        System.out.println("  42의 길이 정보      -> " + lengthInfo(42));

        System.out.println("\n[정리]");
        System.out.println("  switch 패턴 매칭은 instanceof 사슬을 대체하며,");
        System.out.println("  when 가드로 타입 + 조건을 함께 표현할 수 있다.");
    }

    // 타입 패턴 + null 처리
    private static String describe(Object obj) {
        return switch (obj) {
            case null -> "널 값";
            case Integer i -> "정수: " + i;
            case Double d -> "실수: " + d;
            case String s -> "문자열: \"" + s + "\" (길이 " + s.length() + ")";
            default -> "기타 타입: " + obj.getClass().getSimpleName();
        };
    }

    // when 가드로 같은 Integer 타입을 값에 따라 세분화 (위에서부터 순서대로 검사)
    private static String classifyNumber(Object obj) {
        return switch (obj) {
            case Integer i when i < 0   -> "음수";
            case Integer i when i == 0  -> "영(0)";
            case Integer i when i < 100 -> "작은 양수";
            case Integer i             -> "큰 양수(100 이상)";
            default -> "정수가 아님";
        };
    }

    // 타입 패턴으로 바인딩된 변수를 캐스팅 없이 바로 사용
    private static String lengthInfo(Object obj) {
        return switch (obj) {
            case String s -> "문자열 길이는 " + s.length();     // s는 String으로 바인딩됨
            case Integer i -> "정수 자릿수는 " + String.valueOf(i).length();
            default -> "길이 정보 없음";
        };
    }
}
