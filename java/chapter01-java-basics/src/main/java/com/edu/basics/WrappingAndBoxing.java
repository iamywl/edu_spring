package com.edu.basics;

/**
 * Chapter 01 - 래퍼 클래스와 오토박싱/언박싱
 *
 * 기본형(int, double 등)을 객체로 다루기 위한 래퍼 클래스(Integer, Double 등)와
 * 자동 변환(오토박싱/언박싱)의 동작 원리를 학습합니다.
 *
 * 컬렉션(List<Integer> 등)은 기본형을 담을 수 없어 래퍼 클래스를 사용하므로
 * 이 개념을 이해하는 것이 매우 중요합니다.
 */
public class WrappingAndBoxing {

    public static void main(String[] args) {
        System.out.println("====================================");
        System.out.println(" Chapter 01: 래퍼 클래스와 박싱/언박싱");
        System.out.println("====================================\n");

        wrapperBasics();
        autoboxingUnboxing();
        integerCachePitfall();
        nullUnboxingPitfall();
        parseIntVsValueOf();
    }

    // ──────────────────────────────────────────────
    // 1. 래퍼 클래스 기초 (Wrapper Classes)
    // ──────────────────────────────────────────────
    static void wrapperBasics() {
        System.out.println("── 1. 래퍼 클래스 기초 ──");

        // 각 기본형에 대응하는 래퍼 클래스가 존재합니다.
        // byte→Byte, short→Short, int→Integer, long→Long
        // float→Float, double→Double, char→Character, boolean→Boolean
        System.out.println("기본형 → 래퍼 클래스 대응표:");
        System.out.println("  int     → Integer");
        System.out.println("  double  → Double");
        System.out.println("  char    → Character");
        System.out.println("  boolean → Boolean");

        // 래퍼 클래스가 제공하는 유용한 상수와 메서드
        System.out.println("\n래퍼 클래스의 상수/메서드:");
        System.out.println("  Integer.MAX_VALUE : " + Integer.MAX_VALUE);
        System.out.println("  Integer.MIN_VALUE : " + Integer.MIN_VALUE);
        System.out.println("  Integer.toBinaryString(10) : " + Integer.toBinaryString(10));
        System.out.println("  Double.MAX_VALUE  : " + Double.MAX_VALUE);
        System.out.println("  Character.isDigit('7') : " + Character.isDigit('7'));
        System.out.println("  Character.isLetter('A'): " + Character.isLetter('A'));
        System.out.println();
    }

    // ──────────────────────────────────────────────
    // 2. 오토박싱 / 언박싱 (Autoboxing / Unboxing)
    // ──────────────────────────────────────────────
    static void autoboxingUnboxing() {
        System.out.println("── 2. 오토박싱 / 언박싱 ──");

        // 오토박싱(Autoboxing): 기본형 → 래퍼 클래스 자동 변환
        int primitive = 100;
        Integer boxed = primitive;   // 자동으로 Integer.valueOf(100) 호출됨
        System.out.println("오토박싱  : int 100 → Integer " + boxed);

        // 언박싱(Unboxing): 래퍼 클래스 → 기본형 자동 변환
        Integer wrapper = 200;
        int unboxed = wrapper;       // 자동으로 wrapper.intValue() 호출됨
        System.out.println("언박싱    : Integer 200 → int " + unboxed);

        // 컬렉션에서는 기본형을 담을 수 없으므로 박싱이 필수입니다.
        java.util.List<Integer> list = new java.util.ArrayList<>();
        list.add(1);   // int 1 → Integer (오토박싱)
        list.add(2);
        int sum = list.get(0) + list.get(1);  // Integer → int (언박싱)
        System.out.println("List<Integer>에 int 추가 (오토박싱): " + list);
        System.out.println("list.get(0) + list.get(1) (언박싱): " + sum);

        // 주의: 박싱/언박싱은 편리하지만 반복문 안에서 남발하면 성능 저하가 발생합니다.
        System.out.println();
    }

    // ──────────────────────────────────────────────
    // 3. Integer 캐시와 == 함정 ★중요★
    // ──────────────────────────────────────────────
    static void integerCachePitfall() {
        System.out.println("── 3. Integer 캐시와 == 함정 ★중요★ ──");

        // JVM은 -128 ~ 127 범위의 Integer 객체를 미리 캐싱해 둡니다.
        // 따라서 이 범위 안의 값은 valueOf()가 항상 "같은 객체"를 반환합니다.
        Integer a1 = 100;   // 캐시된 객체
        Integer a2 = 100;   // 같은 캐시 객체를 참조
        System.out.println("[캐시 범위 안: 100]");
        System.out.println("  a1 == a2        : " + (a1 == a2));        // true (같은 객체!)
        System.out.println("  a1.equals(a2)   : " + a1.equals(a2));     // true (내용 비교)

        // 하지만 -128 ~ 127 범위를 벗어나면 매번 새로운 객체가 생성됩니다.
        Integer b1 = 1000;  // 새 객체
        Integer b2 = 1000;  // 또 다른 새 객체
        System.out.println("[캐시 범위 밖: 1000]");
        System.out.println("  b1 == b2        : " + (b1 == b2));        // false (서로 다른 객체!) ← 함정!
        System.out.println("  b1.equals(b2)   : " + b1.equals(b2));     // true (내용은 동일)

        System.out.println("\n  ⚠ 교훈: 래퍼 객체 값 비교는 반드시 equals()를 사용하세요.");
        System.out.println("    == 는 참조(주소)를 비교하므로 값이 같아도 false가 나올 수 있습니다.");
        System.out.println();
    }

    // ──────────────────────────────────────────────
    // 4. null 언박싱과 NullPointerException ★중요★
    // ──────────────────────────────────────────────
    static void nullUnboxingPitfall() {
        System.out.println("── 4. null 언박싱과 NullPointerException ★중요★ ──");

        // 래퍼 클래스는 객체이므로 null이 될 수 있습니다.
        // null인 래퍼를 기본형으로 언박싱하려 하면 NullPointerException이 발생합니다.
        Integer nullable = null;   // 래퍼는 null 가능 (기본형 int는 불가능)

        try {
            // 아래 줄에서 nullable.intValue()가 호출되며 NPE 발생!
            int value = nullable;  // 언박싱 시도
            System.out.println("  value: " + value);  // 도달하지 못함
        } catch (NullPointerException e) {
            System.out.println("  null Integer를 int로 언박싱 → NullPointerException 발생!");
            System.out.println("  (내부적으로 null.intValue()를 호출하기 때문)");
        }

        // 실전 사례: DB 조회 결과가 null일 수 있는데 기본형으로 받으면 위험합니다.
        // 예) int count = repository.findCount();  // 결과가 null이면 NPE!
        System.out.println("\n  ⚠ 교훈: null 가능성이 있는 값은 래퍼 타입으로 받고,");
        System.out.println("    언박싱 전에 null 체크를 하세요.");
        System.out.println();
    }

    // ──────────────────────────────────────────────
    // 5. parseInt vs valueOf
    // ──────────────────────────────────────────────
    static void parseIntVsValueOf() {
        System.out.println("── 5. Integer.parseInt vs Integer.valueOf ──");

        String numStr = "42";

        // parseInt: 문자열 → 기본형 int 반환
        int primitive = Integer.parseInt(numStr);
        System.out.println("  Integer.parseInt(\"42\") → int     : " + primitive);

        // valueOf: 문자열 → 래퍼 Integer 반환 (캐시 활용 가능)
        Integer wrapper = Integer.valueOf(numStr);
        System.out.println("  Integer.valueOf(\"42\") → Integer  : " + wrapper);

        // 차이 정리:
        // - parseInt: 기본형이 필요할 때 (예: 산술 연산)
        // - valueOf : 래퍼 객체가 필요할 때 (예: 컬렉션에 담기). 캐시를 사용해 효율적
        System.out.println("\n  반환 타입 차이:");
        System.out.println("    parseInt → 기본형 int (산술 연산에 적합)");
        System.out.println("    valueOf  → 래퍼 Integer (캐시 사용, 컬렉션에 적합)");

        // 잘못된 형식의 문자열은 NumberFormatException을 던집니다.
        try {
            Integer.parseInt("abc");
        } catch (NumberFormatException e) {
            System.out.println("\n  Integer.parseInt(\"abc\") → NumberFormatException 발생!");
        }
        System.out.println();
    }
}
