package com.edu.javabook.ch05;

/**
 * 5.12 열거(enum) 타입
 *
 * 열거 타입은 "정해진 몇 개의 상수"만 값으로 가질 수 있는 참조 타입이다.
 * 예: 요일, 방향, 신호등 색처럼 값의 범위가 한정된 경우에 쓴다.
 *
 *   - 정의   : enum Week { MONDAY, TUESDAY, ... }
 *   - values(): 모든 상수를 배열로 반환
 *   - valueOf(): 문자열로부터 해당 상수를 얻음
 *   - switch 에서 case 라벨로 바로 사용 가능
 *
 * enum 상수는 각각 하나의 객체이며, == 로 안전하게 비교할 수 있다.
 */
public class EnumType {

    // 신호등 색을 나타내는 열거 타입 정의
    enum TrafficLight {
        RED, YELLOW, GREEN
    }

    public static void main(String[] args) {

        System.out.println("=== 5.12 열거(enum) 타입 ===");

        // [1] enum 상수 사용 및 == 비교
        System.out.println("\n[1] enum 상수 사용");
        TrafficLight light = TrafficLight.RED;
        System.out.println("현재 신호 = " + light);
        System.out.println("light == RED : " + (light == TrafficLight.RED));

        // [2] values() : 모든 상수를 순회
        System.out.println("\n[2] values() 로 전체 순회");
        for (TrafficLight t : TrafficLight.values()) {
            // name(): 상수 이름,  ordinal(): 선언 순서(0부터)
            System.out.println("순번 " + t.ordinal() + " : " + t.name());
        }

        // [3] valueOf() : 문자열 → 상수
        System.out.println("\n[3] valueOf() 로 문자열에서 상수 얻기");
        TrafficLight fromText = TrafficLight.valueOf("GREEN");
        System.out.println("valueOf(\"GREEN\") = " + fromText);

        // [4] switch 에서 enum 사용
        System.out.println("\n[4] switch 로 분기");
        for (TrafficLight t : TrafficLight.values()) {
            String action;
            switch (t) {
                case RED:    action = "정지"; break;
                case YELLOW: action = "주의"; break;
                case GREEN:  action = "진행"; break;
                default:     action = "알 수 없음";
            }
            System.out.println(t + " → " + action);
        }

        System.out.println("\n프로그램 정상 종료");
    }
}
