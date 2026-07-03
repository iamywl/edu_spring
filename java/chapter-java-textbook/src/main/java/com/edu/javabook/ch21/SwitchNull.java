package com.edu.javabook.ch21;

/**
 * 21.3 switch의 null 처리
 *
 * [기존 switch의 문제]
 * - 전통적인 switch 문에 null을 넘기면 NullPointerException이 발생했다.
 *   그래서 switch 앞에서 별도의 null 검사 코드를 항상 작성해야 했다.
 *
 * [Java 21의 개선]
 * - switch의 case로 null을 직접 다룰 수 있게 되었다.
 * - case null    : null일 때 실행할 분기를 명시할 수 있다.
 * - case null, default : null과 나머지 모든 경우를 한 분기로 묶을 수 있다.
 * - 이 기능은 화살표(->) 스타일 switch에서 사용한다.
 *
 * [주의]
 * - case null을 두지 않은 switch에 null을 넘기면 여전히 NullPointerException이 난다.
 *   즉 null 처리는 명시적으로 case null을 작성했을 때만 안전하다.
 */
public class SwitchNull {

    public static void main(String[] args) {

        System.out.println("=== 21.3 switch의 null 처리 ===");

        // [1] case null 을 분리해서 처리
        System.out.println("\n[1] case null 로 null 분기");
        System.out.println("  \"MON\" -> " + describeDay("MON"));
        System.out.println("  \"SUN\" -> " + describeDay("SUN"));
        System.out.println("  null  -> " + describeDay(null));   // NPE 없이 처리됨

        // [2] case null, default 로 null과 나머지를 함께 처리
        System.out.println("\n[2] case null, default 로 묶어서 처리");
        System.out.println("  \"RED\"   -> " + colorCode("RED"));
        System.out.println("  \"BLUE\"  -> " + colorCode("BLUE"));
        System.out.println("  \"PINK\"  -> " + colorCode("PINK"));   // default
        System.out.println("  null    -> " + colorCode(null));      // null도 default로

        // [3] case null이 없으면 여전히 NPE가 난다는 점 확인
        System.out.println("\n[3] case null 이 없으면 NPE 발생");
        try {
            unsafeSwitch(null);
        } catch (NullPointerException e) {
            System.out.println("  null 전달 -> NullPointerException 발생(case null 미작성)");
        }

        System.out.println("\n[정리]");
        System.out.println("  Java 21에서는 case null 로 switch가 직접 null을 다룰 수 있다.");
        System.out.println("  null과 기본 처리를 합치려면 case null, default 를 쓴다.");
    }

    // case null 을 별도 분기로 명시
    private static String describeDay(String day) {
        return switch (day) {
            case "MON", "TUE", "WED", "THU", "FRI" -> "평일";
            case "SAT", "SUN" -> "주말";
            case null -> "요일 정보 없음(null)";
            default -> "알 수 없는 요일";
        };
    }

    // null과 default를 하나의 분기로 묶음
    private static String colorCode(String color) {
        return switch (color) {
            case "RED" -> "#FF0000";
            case "GREEN" -> "#00FF00";
            case "BLUE" -> "#0000FF";
            case null, default -> "#000000(기본/알수없음)";
        };
    }

    // case null 이 없어 null 입력 시 NPE가 나는 예
    private static String unsafeSwitch(String s) {
        return switch (s) {
            case "A" -> "에이";
            default -> "기타";
        };
    }
}
