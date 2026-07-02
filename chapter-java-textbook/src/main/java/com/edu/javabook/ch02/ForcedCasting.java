package com.edu.javabook.ch02;

/**
 * 2.8 강제 타입 변환 (명시적 형변환, 캐스팅)
 *
 * 큰 크기의 타입을 작은 크기의 타입에 넣으려면 반드시 (타입)으로 명시해야 한다.
 * 작은 그릇에 큰 물을 붓는 격이라 넘치는 부분(데이터)이 잘려나갈 수 있다.
 *
 * 형식:  작은타입 변수 = (작은타입) 큰타입값;
 */
public class ForcedCasting {

    public static void main(String[] args) {

        System.out.println("=== 2.8 강제 타입 변환 ===");

        // [1] 큰 정수 → 작은 정수 (캐스팅 필수)
        System.out.println("\n[1] int → byte");
        int i = 65;
        byte b = (byte) i;   // (byte) 를 빼면 컴파일 에러
        System.out.println("int 65 → byte : " + b);

        // [2] 데이터 손실 시연: byte 범위(-128~127)를 넘는 값
        System.out.println("\n[2] 데이터 손실 (범위 초과)");
        int big = 300;
        byte lost = (byte) big;   // 300은 byte에 안 들어감 → 하위 8비트만 남아 44
        System.out.println("int 300 → byte : " + lost + "  (300이 아니다! 상위 비트 잘림)");

        // [3] 실수 → 정수: 소수점 이하가 '버려진다'(반올림 아님)
        System.out.println("\n[3] double → int (소수점 버림)");
        double d = 3.99;
        int cut = (int) d;   // 3.99 → 3 (반올림 X, 버림 O)
        System.out.println("double 3.99 → int : " + cut + "  (반올림 아닌 버림)");

        // [4] char 로의 캐스팅
        System.out.println("\n[4] int → char");
        int code = 66;
        char ch = (char) code;   // 66 → 'B'
        System.out.println("int 66 → char : " + ch);

        // [5] long → int 손실 예
        System.out.println("\n[5] long → int 손실");
        long bigLong = 10_000_000_000L;   // int 범위(약 21억) 초과
        int narrowed = (int) bigLong;
        System.out.println("long 100억 → int : " + narrowed + "  (값이 망가짐)");

        // [6] 올바른 반올림이 필요하면 Math.round 를 쓴다
        System.out.println("\n[6] 반올림이 필요하면 Math.round");
        double price = 3.99;
        long rounded = Math.round(price);
        System.out.println("Math.round(3.99) = " + rounded);

        // [왜?] 컴파일러가 "손실 가능성"을 경고하는 것을, (타입)으로 "알고 있다"고 확인해주는 것.
        System.out.println("\n[왜?] 캐스팅은 '데이터 손실을 감수하겠다'는 프로그래머의 명시적 선언이다.");

        System.out.println("\n프로그램 정상 종료");
    }
}
