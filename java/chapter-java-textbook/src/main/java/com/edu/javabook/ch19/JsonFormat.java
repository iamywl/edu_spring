package com.edu.javabook.ch19;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 19.6 JSON 데이터 형식
 *
 * [JSON(JavaScript Object Notation)]
 * - 네트워크로 데이터를 주고받을 때 널리 쓰이는 경량 텍스트 데이터 형식이다.
 * - 객체는 중괄호 { "키": 값, ... }, 배열은 대괄호 [ 값, ... ] 로 표현한다.
 * - 값의 타입: 문자열("..."), 숫자, 불리언(true/false), null, 객체, 배열.
 *
 * [주의]
 * - JDK 표준에는 범용 JSON 라이브러리가 없다.
 *   (실무에서는 Jackson(com.fasterxml.jackson) 또는 Gson(com.google.gson) 을 쓴다.)
 * - 이 예제는 학습용으로, 표준 API만으로 JSON 문자열을 "직접 조립" 하고
 *   아주 단순한 형태를 "수동 파싱" 하는 원리를 보여준다.
 *   (실제 서비스에서는 절대 직접 파싱하지 말고 검증된 라이브러리를 사용할 것.)
 */
public class JsonFormat {

    public static void main(String[] args) {

        System.out.println("=== 19.6 JSON 데이터 형식 ===");

        // [1] 맵 데이터를 JSON 문자열로 직접 조립(직렬화 흉내)
        System.out.println("\n[1] 객체 → JSON 문자열(수동 직렬화)");
        Map<String, String> user = new LinkedHashMap<>();
        user.put("name", "홍길동");
        user.put("age", "20");
        user.put("city", "서울");
        String json = toJson(user);
        System.out.println("  생성된 JSON: " + json);

        // [2] 단순 JSON 문자열을 수동 파싱(역직렬화 흉내)
        System.out.println("\n[2] JSON 문자열 → 객체(수동 파싱)");
        String input = "{\"id\":\"7\",\"title\":\"자바\",\"open\":\"true\"}";
        System.out.println("  입력 JSON: " + input);
        Map<String, String> parsed = parseSimpleJson(input);
        parsed.forEach((k, v) -> System.out.println("    " + k + " = " + v));

        // [3] 실무 안내
        System.out.println("\n[3] 실무 참고");
        System.out.println("  이 코드는 학습용 단순 예시일 뿐이며,");
        System.out.println("  중첩 객체/배열/이스케이프 등은 처리하지 못한다.");
        System.out.println("  실무에서는 Jackson 또는 Gson 라이브러리를 사용한다.");

        System.out.println("\n프로그램 정상 종료");
    }

    // 문자열 맵을 { "k":"v", ... } 형태의 JSON 문자열로 변환
    static String toJson(Map<String, String> map) {
        StringBuilder sb = new StringBuilder("{");
        boolean first = true;
        for (Map.Entry<String, String> e : map.entrySet()) {
            if (!first) sb.append(",");
            sb.append("\"").append(e.getKey()).append("\":\"").append(e.getValue()).append("\"");
            first = false;
        }
        return sb.append("}").toString();
    }

    // 아주 단순한 평면 JSON( {"k":"v",...} )만 파싱 (학습용, 검증 최소)
    static Map<String, String> parseSimpleJson(String json) {
        Map<String, String> map = new LinkedHashMap<>();
        String body = json.trim();
        if (body.startsWith("{")) body = body.substring(1);
        if (body.endsWith("}")) body = body.substring(0, body.length() - 1);
        if (body.isEmpty()) return map;
        for (String pair : body.split(",")) {
            String[] kv = pair.split(":", 2);
            if (kv.length == 2) {
                String key = unquote(kv[0].trim());
                String value = unquote(kv[1].trim());
                map.put(key, value);
            }
        }
        return map;
    }

    // 양쪽 큰따옴표 제거
    static String unquote(String s) {
        if (s.length() >= 2 && s.startsWith("\"") && s.endsWith("\"")) {
            return s.substring(1, s.length() - 1);
        }
        return s;
    }
}
