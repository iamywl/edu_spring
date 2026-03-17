package com.edu.oop;

/**
 * 날 수 있는 능력을 나타내는 인터페이스
 * - 인터페이스: 클래스가 구현해야 할 계약(contract)을 정의
 * - 다중 구현 가능 (클래스는 여러 인터페이스를 implements 할 수 있음)
 * - Java 8+: default 메서드, static 메서드 지원
 */
public interface Flyable {

    // === 추상 메서드: 구현 클래스에서 반드시 구현해야 함 ===

    /** 날기 동작 */
    String fly();

    /** 최대 비행 고도(미터) */
    int getMaxAltitude();

    // === default 메서드 (Java 8+): 기본 구현을 제공, 필요시 오버라이드 가능 ===

    /** 착륙 동작 - 기본 구현 제공 */
    default String land() {
        return "안전하게 착륙합니다.";
    }

    /** 비행 상태 출력 */
    default String flightStatus() {
        return "최대 비행 고도: " + getMaxAltitude() + "m";
    }

    // === static 메서드 (Java 8+): 인터페이스 자체에서 호출 가능 ===

    /** 인터페이스 설명 */
    static String description() {
        return "Flyable: 날 수 있는 능력을 나타내는 인터페이스입니다.";
    }

    /** 고도 안전성 확인 유틸리티 */
    static boolean isSafeAltitude(int altitude) {
        return altitude >= 0 && altitude <= 12000;  // 12,000m 이하를 안전 고도로 판단
    }
}
