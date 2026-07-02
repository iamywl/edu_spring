package com.edu.javabook.ch18;

/**
 * 18.1 입출력 스트림
 *
 * [입출력 스트림(I/O Stream)이란]
 * - 데이터가 흐르는 통로다. 프로그램과 데이터 소스(파일, 네트워크, 콘솔 등)를
 *   연결하여 데이터를 한 방향으로 흘려보낸다.
 * - 스트림은 단방향이다. 읽기 전용(입력) 또는 쓰기 전용(출력) 중 하나다.
 *
 * [바이트 스트림 vs 문자 스트림]
 * - 바이트 스트림(InputStream/OutputStream): 1바이트 단위로 처리한다.
 *   이미지, 오디오, 실행 파일 등 모든 종류의 데이터에 사용한다.
 * - 문자 스트림(Reader/Writer): 2바이트 문자(char) 단위로 처리한다.
 *   텍스트 데이터를 다룰 때 문자 인코딩을 자동 처리해주어 편리하다.
 *
 * [입력 방향 vs 출력 방향]
 * - 입력(Input): 소스 → 프로그램 (읽기)
 * - 출력(Output): 프로그램 → 대상(destination) (쓰기)
 *
 * 이 소절에서는 개념만 설명하며 실제 I/O는 다음 소절부터 다룬다.
 */
public class IoStreamIntro {

    public static void main(String[] args) {

        System.out.println("=== 18.1 입출력 스트림 ===");

        // [1] 스트림의 방향 개념
        System.out.println("\n[1] 스트림의 방향");
        System.out.println("  입력 스트림: 데이터 소스 --> 프로그램 (읽기)");
        System.out.println("  출력 스트림: 프로그램 --> 데이터 대상 (쓰기)");
        System.out.println("  스트림은 단방향이므로 읽기용/쓰기용을 따로 연다.");

        // [2] 바이트 스트림과 문자 스트림 비교
        System.out.println("\n[2] 바이트 스트림 vs 문자 스트림");
        System.out.println("  구분        | 처리 단위 | 최상위 클래스              | 용도");
        System.out.println("  ------------|-----------|----------------------------|------");
        System.out.println("  바이트 입력 | 1 byte    | InputStream                | 모든 데이터");
        System.out.println("  바이트 출력 | 1 byte    | OutputStream               | 모든 데이터");
        System.out.println("  문자 입력   | 2 byte    | Reader                     | 텍스트");
        System.out.println("  문자 출력   | 2 byte    | Writer                     | 텍스트");

        // [3] 자바가 실제로 사용하는 값의 크기 확인
        System.out.println("\n[3] 처리 단위 크기(비트)");
        System.out.println("  byte 크기 : " + Byte.SIZE + " bit (바이트 스트림 단위)");
        System.out.println("  char 크기 : " + Character.SIZE + " bit (문자 스트림 단위)");

        // [4] 정리
        System.out.println("\n[4] 정리");
        System.out.println("  - 텍스트는 문자 스트림, 그 외 이진 데이터는 바이트 스트림을 쓴다.");
        System.out.println("  - 스트림은 사용 후 반드시 close() 한다(try-with-resources 권장).");

        System.out.println("\n프로그램 정상 종료");
    }
}
