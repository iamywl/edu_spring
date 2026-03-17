package com.edu.oop;

/**
 * 계절 열거형 (enum)
 * - enum: 상수의 집합을 타입 안전하게 정의
 * - 각 상수는 enum의 인스턴스
 * - 필드, 생성자, 메서드를 가질 수 있음
 * - values(): 모든 상수 배열 반환
 * - valueOf("SPRING"): 이름으로 상수 조회
 * - name(): 상수 이름, ordinal(): 선언 순서(0부터)
 */
public enum Season {

    // === 열거 상수 정의 (각각이 Season의 인스턴스) ===
    SPRING("봄", "3월~5월", 15),
    SUMMER("여름", "6월~8월", 30),
    AUTUMN("가을", "9월~11월", 15),
    WINTER("겨울", "12월~2월", -5);

    // === 필드 ===
    private final String koreanName;    // 한국어 이름
    private final String period;        // 기간
    private final int avgTemperature;   // 평균 기온 (섭씨)

    // === 생성자 (private만 허용, 외부에서 new 불가) ===
    Season(String koreanName, String period, int avgTemperature) {
        this.koreanName = koreanName;
        this.period = period;
        this.avgTemperature = avgTemperature;
    }

    // === Getter ===
    public String getKoreanName() {
        return koreanName;
    }

    public String getPeriod() {
        return period;
    }

    public int getAvgTemperature() {
        return avgTemperature;
    }

    // === 커스텀 메서드 ===

    /** 계절 설명 */
    public String describe() {
        return koreanName + " (" + period + "), 평균 기온: " + avgTemperature + "°C";
    }

    /** 더운 계절인지 판별 */
    public boolean isHot() {
        return avgTemperature >= 25;
    }

    /** 추운 계절인지 판별 */
    public boolean isCold() {
        return avgTemperature <= 0;
    }

    /** 한국어 이름으로 계절 검색 */
    public static Season fromKoreanName(String koreanName) {
        for (Season season : values()) {
            if (season.koreanName.equals(koreanName)) {
                return season;
            }
        }
        throw new IllegalArgumentException("해당하는 계절이 없습니다: " + koreanName);
    }
}
