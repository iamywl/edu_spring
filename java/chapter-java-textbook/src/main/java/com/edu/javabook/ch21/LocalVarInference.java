package com.edu.javabook.ch21;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * 21.2 지역 변수 타입 추론 (var)
 *
 * [var란]
 * - Java 10부터 도입된 지역 변수 타입 추론(Local Variable Type Inference) 키워드다.
 * - 컴파일러가 우변(초기화 식)을 보고 변수의 타입을 자동으로 결정한다.
 *
 * [핵심 성질]
 * - 컴파일 타임에 타입이 확정된다. 런타임 동적 타입이 아니다.
 *   즉 var는 자바스크립트의 var와 전혀 다르며, 타입 안전성은 그대로 유지된다.
 * - 지역 변수에서만 사용할 수 있다.
 *   (필드, 메서드 매개변수, 반환 타입에는 사용할 수 없다.)
 * - 반드시 선언과 동시에 초기화해야 한다. (타입을 추론할 근거가 필요하기 때문)
 *
 * [가독성과 남용]
 * - 우변에서 타입이 명확할 때는 코드를 간결하게 만들어 가독성을 높인다.
 * - 반대로 우변만으로 타입을 알기 어려울 때는 오히려 가독성을 떨어뜨린다.
 *   이런 경우는 명시적 타입 선언이 낫다.
 */
public class LocalVarInference {

    public static void main(String[] args) {

        System.out.println("=== 21.2 지역 변수 타입 추론 (var) ===");

        // [1] 기본 사용: 우변으로 타입 추론
        System.out.println("\n[1] 기본 사용");
        var message = "안녕하세요";          // String으로 추론
        var count = 42;                       // int로 추론
        var pi = 3.14;                        // double로 추론
        System.out.println("  message 실제 타입 = " + message.getClass().getSimpleName());
        System.out.println("  count  = " + count + " (int로 추론)");
        System.out.println("  pi     = " + pi + " (double로 추론)");

        // [2] 제네릭/컬렉션에서 중복 타입을 줄여 가독성 향상
        System.out.println("\n[2] 가독성 향상 예");
        // 명시적: Map<String, List<Integer>> scores = new HashMap<>();
        var scores = new HashMap<String, List<Integer>>();
        scores.put("kim", new ArrayList<>(List.of(90, 85)));
        System.out.println("  scores 타입 = " + scores.getClass().getSimpleName()
                + ", 값 = " + scores);

        // [3] 컴파일 타임 확정 증명: 타입이 고정되어 형 검사가 동작한다
        System.out.println("\n[3] 컴파일 타임 타입 확정");
        var number = 10;                      // int로 확정됨
        // number = "문자열";   // <- 이 줄은 컴파일 오류(타입 불일치). var는 정적 타입이다.
        number = number + 5;                  // int 연산은 정상
        System.out.println("  number = " + number + " (int로 확정되어 int 연산만 허용)");

        // [4] for 루프에서의 var 활용
        System.out.println("\n[4] 반복문에서의 var");
        var fruits = List.of("사과", "바나나", "포도");
        for (var fruit : fruits) {            // fruit은 String으로 추론
            System.out.println("  과일: " + fruit);
        }

        // [5] 남용 주의: 우변만으로 타입을 알기 어려운 경우
        System.out.println("\n[5] 남용 주의");
        var good = new HashMap<String, Integer>();   // 우변이 명확 -> 좋은 사용
        var unclear = getValue();                    // 우변이 메서드 -> 타입 불명확
        System.out.println("  good    : new HashMap<>() -> 타입 명확, 권장");
        System.out.println("  unclear : getValue() 반환 -> 타입 감춰짐, 명시 타입 권장");
        System.out.println("  (참고) unclear 실제 값 = " + unclear);

        System.out.println("\n[정리]");
        System.out.println("  var는 컴파일 타임에 타입이 확정되는 정적 타입이며,");
        System.out.println("  우변이 명확할 때만 사용해 가독성을 높이는 것이 좋다.");
    }

    // 반환 타입이 코드에서 바로 안 보이므로 var 사용 시 타입이 감춰지는 예
    private static long getValue() {
        return 123L;
    }
}
