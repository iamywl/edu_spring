package com.edu.javabook.ch12;

/**
 * 12.4 System 클래스
 *
 * System 클래스는 운영체제 및 자바 실행 환경과 관련된 기능을 모아 둔
 * "유틸리티 클래스"다. 모든 멤버가 static 이므로 객체를 만들지 않고 바로 쓴다.
 *
 * 주요 기능 :
 *   - System.currentTimeMillis() : 1970-01-01 기준 현재 시각(밀리초). 시각 측정용.
 *   - System.nanoTime()          : 임의 기준점 기준 나노초. "경과 시간 측정"에 적합.
 *   - System.getProperty(key)    : 자바 시스템 속성 조회 (java.version, os.name 등)
 *   - System.getenv(key)         : 운영체제 환경 변수 조회 (PATH 등)
 *   - System.arraycopy(...)      : 배열의 일부를 다른 배열로 빠르게 복사
 *   - System.out / System.err    : 표준 출력 / 표준 에러 출력 스트림
 *
 * 이 소절에서는 위 기능들을 하나씩 호출해 System 클래스의 역할을 확인한다.
 */
public class SystemClass {

    public static void main(String[] args) {

        System.out.println("=== 12.4 System 클래스 ===");

        // [1] currentTimeMillis / nanoTime : 시간 측정
        System.out.println("\n[1] 시간 측정 (currentTimeMillis / nanoTime)");
        long startNano = System.nanoTime();
        long sum = 0;
        for (int i = 1; i <= 1_000_000; i++) sum += i;   // 간단한 작업
        long endNano = System.nanoTime();
        System.out.println("  currentTimeMillis() = " + System.currentTimeMillis() + " (ms, 1970 기준)");
        System.out.println("  1~100만 합 = " + sum);
        System.out.println("  걸린 시간 = " + (endNano - startNano) + " ns (nanoTime 차이)");

        // [2] getProperty : 자바/OS 시스템 속성
        System.out.println("\n[2] getProperty (시스템 속성)");
        System.out.println("  java.version = " + System.getProperty("java.version"));
        System.out.println("  os.name      = " + System.getProperty("os.name"));
        System.out.println("  file.separator = " + System.getProperty("file.separator"));

        // [3] getenv : 운영체제 환경 변수 (없을 수도 있으므로 null 처리)
        System.out.println("\n[3] getenv (환경 변수)");
        String path = System.getenv("PATH");
        System.out.println("  PATH 존재 여부 = " + (path != null));
        String home = System.getenv("HOME");
        System.out.println("  HOME 존재 여부 = " + (home != null));

        // [4] arraycopy : 배열 일부 복사 (원본, 시작, 대상, 시작, 개수)
        System.out.println("\n[4] arraycopy (배열 복사)");
        int[] src = {10, 20, 30, 40, 50};
        int[] dest = new int[5];
        System.arraycopy(src, 1, dest, 0, 3);   // src[1..3] → dest[0..2]
        StringBuilder sb = new StringBuilder();
        for (int v : dest) sb.append(v).append(" ");
        System.out.println("  원본 src  = 10 20 30 40 50");
        System.out.println("  복사 dest = " + sb.toString().trim() + " (src[1..3] 복사)");

        // [5] out / err : 표준 출력과 표준 에러
        System.out.println("\n[5] out / err (표준 출력 / 표준 에러)");
        System.out.println("  System.out : 일반 정보 출력에 사용");
        System.err.println("  System.err : 오류 메시지 출력에 사용 (보통 빨간색)");

        System.out.println("\n프로그램 정상 종료");
    }
}
