package com.edu.oop;

/**
 * [개념 7] 열거형(enum)
 *
 * enum이란?
 * - 정해진 상수의 집합을 "타입 안전"하게 표현하는 특별한 클래스.
 * - 각 열거 상수(SPRING, SUMMER ...)는 그 enum 타입의 유일한 인스턴스(싱글턴)이다.
 * - 단순 상수를 넘어서 필드, 생성자, 메서드까지 가질 수 있다.
 *
 * 이 예제에서 사용하는 것: Season enum
 *   - 각 계절은 (한국어 이름, 기간, 평균기온) 필드를 가진다.
 *   - describe(), isHot(), isCold(), fromKoreanName() 등의 메서드를 가진다.
 *
 * enum이 제공하는 기본 기능:
 *   - values()      : 모든 상수를 선언 순서대로 배열로 반환
 *   - valueOf("X")  : 이름 문자열로 상수를 조회
 *   - name()        : 상수의 이름
 *   - ordinal()     : 선언 순서(0부터)
 */
public class EnumExample {

    public static void main(String[] args) {

        // ------------------------------------------------------------
        // 1) values(): 모든 상수 순회 + 필드/메서드 사용
        // ------------------------------------------------------------
        printSection("1. 모든 계절 순회 (values, name, ordinal)");

        for (Season season : Season.values()) {
            // name()=상수이름, ordinal()=선언 순서, describe()=커스텀 메서드
            System.out.println("  " + season.name()
                    + " (ordinal=" + season.ordinal() + ")"
                    + " -> " + season.describe());
        }

        // ------------------------------------------------------------
        // 2) 필드와 메서드를 가진 enum
        // ------------------------------------------------------------
        printSection("2. 필드/메서드를 가진 enum");

        Season summer = Season.SUMMER;
        Season winter = Season.WINTER;

        // 각 상수는 자신만의 필드 값을 갖고, 그 값으로 판단하는 메서드를 제공한다.
        System.out.println("여름 평균 기온: " + summer.getAvgTemperature() + "°C");
        System.out.println("여름은 더운가? isHot() -> " + summer.isHot());
        System.out.println("겨울 평균 기온: " + winter.getAvgTemperature() + "°C");
        System.out.println("겨울은 추운가? isCold() -> " + winter.isCold());

        // ------------------------------------------------------------
        // 3) valueOf / 커스텀 조회 메서드
        // ------------------------------------------------------------
        printSection("3. 이름으로 상수 조회");

        // 정확한 상수 이름으로 조회
        Season autumn = Season.valueOf("AUTUMN");
        System.out.println("valueOf(\"AUTUMN\") -> " + autumn.describe());

        // 한국어 이름으로 조회하는 커스텀 static 메서드
        Season spring = Season.fromKoreanName("봄");
        System.out.println("fromKoreanName(\"봄\") -> " + spring.describe());

        // 잘못된 이름은 예외가 발생한다
        try {
            Season.fromKoreanName("장마철");
        } catch (IllegalArgumentException e) {
            System.out.println("잘못된 조회 -> " + e.getMessage());
        }

        // ------------------------------------------------------------
        // 4) switch에서 enum 사용
        // ------------------------------------------------------------
        printSection("4. switch에서 enum 사용");

        // enum은 switch와 특히 잘 어울린다. (case에 상수 이름만 쓴다: Season. 접두어 불필요)
        for (Season season : Season.values()) {
            String advice = switch (season) {
                case SPRING -> "꽃구경 가기 좋은 계절입니다.";
                case SUMMER -> "수분을 충분히 섭취하세요!";
                case AUTUMN -> "단풍 구경을 추천합니다.";
                case WINTER -> "따뜻하게 입고 다니세요.";
            };
            // 위 switch는 모든 상수를 다뤘으므로 default 없이도 컴파일된다(완전성 검사).
            System.out.println(season.getKoreanName() + " 조언 -> " + advice);
        }

        // ------------------------------------------------------------
        // 정리
        // ------------------------------------------------------------
        printSection("정리");
        System.out.println("- enum은 정해진 상수 집합을 타입 안전하게 표현한다.");
        System.out.println("- 각 상수는 필드/메서드를 가질 수 있는 인스턴스다.");
        System.out.println("- values/valueOf/name/ordinal 기본 기능을 제공한다.");
        System.out.println("- switch와 함께 쓰면 모든 경우를 컴파일 타임에 검증할 수 있다.");
    }

    private static void printSection(String title) {
        System.out.println();
        System.out.println("=".repeat(60));
        System.out.println("  " + title);
        System.out.println("=".repeat(60));
    }
}
