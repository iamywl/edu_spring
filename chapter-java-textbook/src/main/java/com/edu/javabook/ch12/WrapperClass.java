package com.edu.javabook.ch12;

/**
 * 12.6 포장 클래스 (Wrapper Class)
 *
 * 기본 타입(int, double, boolean 등)은 객체가 아니다. 하지만 컬렉션에 담거나
 * 객체가 필요한 곳에 쓰려면 "객체 형태"가 필요하다. 이때 기본 타입 값을
 * 객체로 "포장(wrap)" 해 주는 클래스가 포장 클래스다.
 *
 *   기본 타입 → 포장 클래스
 *   int     → Integer      double  → Double
 *   long    → Long         boolean → Boolean
 *   char    → Character     ...
 *
 * 오토박싱 / 언박싱 :
 *   - 오토박싱(auto-boxing)   : 기본 타입 → 포장 객체 자동 변환 (Integer i = 10;)
 *   - 언박싱(unboxing)        : 포장 객체 → 기본 타입 자동 변환 (int n = i;)
 *
 * parseXxx :
 *   - Integer.parseInt("100"), Double.parseDouble("3.14") 처럼
 *     문자열을 기본 타입 숫자로 바꾸는 정적 메서드를 제공한다.
 *
 * Integer 캐시(==) 함정 :
 *   - -128 ~ 127 사이의 Integer 는 미리 만들어 둔 "캐시 객체"를 재사용한다.
 *   - 그래서 이 범위는 == 가 우연히 true 가 되지만, 범위를 벗어나면 false 다.
 *   - 값 비교는 반드시 equals() (또는 언박싱 후 ==) 로 해야 한다.
 *
 * 이 소절에서는 포장/오토박싱/parseXxx/캐시 함정을 코드로 확인한다.
 */
public class WrapperClass {

    public static void main(String[] args) {

        System.out.println("=== 12.6 포장 클래스 ===");

        // [1] 기본 타입 ↔ 포장 클래스
        System.out.println("\n[1] 포장(박싱)과 꺼내기");
        Integer boxed = Integer.valueOf(42);  // 명시적 박싱
        int primitive = boxed.intValue();      // 명시적 언박싱
        System.out.println("  Integer.valueOf(42) = " + boxed);
        System.out.println("  boxed.intValue()    = " + primitive);
        System.out.println("  Integer.MAX_VALUE   = " + Integer.MAX_VALUE);

        // [2] 오토박싱 / 언박싱 : 자동 변환
        System.out.println("\n[2] 오토박싱 / 언박싱");
        Integer auto = 100;      // 오토박싱 : int 100 → Integer
        int back = auto;         // 언박싱   : Integer → int
        Double d = 3.14;         // 오토박싱 : double → Double
        System.out.println("  Integer auto = 100  → " + auto + " (오토박싱)");
        System.out.println("  int back = auto     → " + back + " (언박싱)");
        System.out.println("  Double d = 3.14     → " + d);

        // [3] parseXxx : 문자열 → 숫자
        System.out.println("\n[3] parseXxx (문자열 → 숫자)");
        int i = Integer.parseInt("256");
        double pi = Double.parseDouble("3.14159");
        boolean b = Boolean.parseBoolean("true");
        System.out.println("  Integer.parseInt(\"256\")     = " + (i + 1) + " (+1 계산 가능)");
        System.out.println("  Double.parseDouble(\"3.14159\")= " + pi);
        System.out.println("  Boolean.parseBoolean(\"true\") = " + b);

        // [4] Integer 캐시 == 함정
        System.out.println("\n[4] Integer 캐시 == 함정");
        Integer a1 = 127, a2 = 127;   // 캐시 범위(-128~127) → 같은 객체 재사용
        Integer b1 = 128, b2 = 128;   // 캐시 범위 초과 → 서로 다른 객체
        System.out.println("  127 == 127 : " + (a1 == a2) + "  (캐시 재사용 → 우연히 true)");
        System.out.println("  128 == 128 : " + (b1 == b2) + " (캐시 초과 → false, 함정!)");
        System.out.println("  128.equals(128) : " + b1.equals(b2) + "  (값 비교는 equals 사용)");

        System.out.println("\n프로그램 정상 종료");
    }
}
