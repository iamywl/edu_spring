package com.edu.javabook.ch21;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * 21.8 기본 문자셋 변경 (Default Charset -> UTF-8)
 *
 * [무엇이 바뀌었나]
 * - Java 18(JEP 400)부터 표준 자바 API의 "기본 문자셋"이 UTF-8로 통일되었다.
 *   이 변경은 Java 21에도 그대로 적용되어 있다.
 * - 즉 Charset.defaultCharset()이 실행 환경(OS 로케일)과 무관하게 UTF-8을 반환한다.
 *
 * [왜 중요한가]
 * - 예전에는 기본 문자셋이 OS/로케일에 따라 달랐다.
 *   (윈도우 한글 환경은 MS949/CP949, 리눅스는 UTF-8 등)
 * - 그래서 같은 코드가 개발 PC와 서버에서 다르게 동작하며,
 *   파일을 읽고 쓸 때 한글이 깨지는 문제가 흔했다.
 * - 기본이 UTF-8로 통일되면서 "어디서 실행하든 동일하게 동작"하는
 *   이식성과 예측 가능성이 크게 향상되었다.
 *
 * [주의]
 * - file.encoding 시스템 속성으로 옛 동작을 흉내낼 수도 있지만,
 *   새 코드에서는 문자셋을 다룰 때 StandardCharsets.UTF_8을 명시하는 것이 가장 안전하다.
 */
public class DefaultCharset {

    public static void main(String[] args) {

        System.out.println("=== 21.8 기본 문자셋 변경 ===");

        // [1] 현재 기본 문자셋 확인 (Java 18+ 에서는 UTF-8)
        System.out.println("\n[1] 현재 기본 문자셋");
        Charset def = Charset.defaultCharset();
        System.out.println("  Charset.defaultCharset() = " + def);
        System.out.println("  file.encoding 속성       = " + System.getProperty("file.encoding"));
        System.out.println("  UTF-8 인가?              = " + def.equals(StandardCharsets.UTF_8));

        // [2] UTF-8로 한글 인코딩/디코딩이 일관되게 동작하는지 확인
        System.out.println("\n[2] 한글 인코딩/디코딩 (UTF-8)");
        String korean = "한글 テスト émoji";
        byte[] bytes = korean.getBytes(StandardCharsets.UTF_8);   // 문자셋 명시가 가장 안전
        String restored = new String(bytes, StandardCharsets.UTF_8);
        System.out.println("  원본 문자열   = " + korean);
        System.out.println("  UTF-8 바이트수 = " + bytes.length);
        System.out.println("  복원 문자열   = " + restored);
        System.out.println("  원본==복원    = " + korean.equals(restored));

        // [3] 왜 중요한가: 환경에 따라 달라지던 과거와의 대비
        System.out.println("\n[3] 왜 중요한가");
        System.out.println("  과거: 기본 문자셋이 OS/로케일에 의존(윈도우 MS949, 리눅스 UTF-8 등)");
        System.out.println("       -> 같은 코드가 환경마다 다르게 동작, 한글 깨짐 빈번");
        System.out.println("  현재: 어디서든 기본이 UTF-8 -> 이식성/예측 가능성 향상");

        // [4] 실무 권장: 문자셋을 명시하라
        System.out.println("\n[4] 실무 권장");
        System.out.println("  기본값에 의존하지 말고 StandardCharsets.UTF_8을 명시하면");
        System.out.println("  JDK 버전/환경과 무관하게 항상 동일하게 동작한다.");

        System.out.println("\n[정리]");
        System.out.println("  Java 18부터 기본 문자셋이 UTF-8로 통일되어(Java 21 포함)");
        System.out.println("  플랫폼 간 문자 처리 결과가 일관되게 되었다.");
    }
}
