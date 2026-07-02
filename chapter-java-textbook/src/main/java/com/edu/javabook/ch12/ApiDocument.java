package com.edu.javabook.ch12;

/**
 * 12.1 API 도큐먼트
 *
 * API(Application Programming Interface)는 자바가 미리 만들어 제공하는
 * "클래스와 인터페이스의 모음"이다. 우리는 이 API를 조합해서 프로그램을 만든다.
 *
 * API 도큐먼트(API Documentation)는 이 표준 클래스들의 사용법을 정리한 공식 문서다.
 *   - 위치 : https://docs.oracle.com/en/java/javase/  (버전별 온라인 문서)
 *   - 담긴 내용 : 각 클래스의 필드/생성자/메서드, 매개변수, 반환값, 예외, 설명
 *
 * 표준 API 찾는 법 :
 *   1) 어떤 기능이 필요한지 정한다.        (예 : "문자열을 대문자로")
 *   2) 관련 클래스를 문서에서 찾는다.       (예 : String 클래스)
 *   3) 원하는 메서드를 고른다.             (예 : toUpperCase())
 *   4) 매개변수/반환/예외를 확인하고 쓴다.
 *
 * java.base 모듈의 위치 :
 *   - 자바 9부터 표준 API는 "모듈" 단위로 나뉜다.
 *   - 가장 기본이 되는 모듈이 java.base 이며, 별도 선언 없이 항상 포함된다.
 *   - java.lang, java.util, java.io, java.time 등 핵심 패키지가 java.base 안에 있다.
 *
 * 이 소절에서는 코드로 "표준 API를 찾아 쓰는 흐름"과
 * 자주 쓰는 핵심 패키지(java.base 소속)를 개념적으로 확인한다.
 */
public class ApiDocument {

    public static void main(String[] args) {

        System.out.println("=== 12.1 API 도큐먼트 ===");

        // [1] API 도큐먼트란 : 표준 클래스 사용법을 정리한 공식 문서
        System.out.println("\n[1] API 도큐먼트");
        System.out.println("  - 표준 클래스(필드/생성자/메서드)의 사용법을 담은 공식 문서");
        System.out.println("  - 온라인 위치 예 : https://docs.oracle.com/en/java/javase/");

        // [2] 표준 API를 찾아 쓰는 흐름을 String 예로 확인
        System.out.println("\n[2] 표준 API 찾아 쓰기 (String.toUpperCase 예)");
        String word = "java";
        // 문서에서 String → toUpperCase() 를 찾음 → 매개변수 없음, String 반환
        String upper = word.toUpperCase();
        System.out.println("  \"" + word + "\".toUpperCase() = " + upper);

        // [3] java.base 모듈에 속한 핵심 패키지들 (개념 정리)
        System.out.println("\n[3] java.base 모듈의 대표 패키지");
        String[] packages = {
            "java.lang  - 언어 핵심(Object, String, System, Math ...) 자동 import",
            "java.util  - 컬렉션/유틸(List, Map, Arrays ...)",
            "java.io    - 입출력(File, InputStream ...)",
            "java.time  - 날짜와 시간(LocalDate, Duration ...)",
            "java.text  - 형식화(DecimalFormat, NumberFormat ...)"
        };
        for (String p : packages) {
            System.out.println("  - " + p);
        }

        // [4] 실제로 어떤 모듈에 있는지 리플렉션으로 확인 (String → java.base)
        System.out.println("\n[4] 클래스가 속한 모듈 확인");
        Module stringModule = String.class.getModule();
        System.out.println("  String 클래스가 속한 모듈 = " + stringModule.getName());

        System.out.println("\n프로그램 정상 종료");
    }
}
