package com.edu.javabook.ch02;

/**
 * 2.7 자동 타입 변환 (묵시적 형변환)
 *
 * 작은 크기의 타입을 큰 크기의 타입에 대입하면 자동으로 변환된다.
 * 값의 손실이 없기(그릇이 더 커지기) 때문에 별도 표기 없이 안전하게 일어난다.
 *
 * 허용 방향(작은 → 큰):
 *   byte → short → int → long → float → double
 *   char → int   (char도 정수 계열)
 */
public class AutoCasting {

    public static void main(String[] args) {

        System.out.println("=== 2.7 자동 타입 변환 ===");

        // [1] 정수 → 더 큰 정수
        System.out.println("\n[1] 작은 정수 → 큰 정수");
        byte b = 10;
        int  i = b;    // byte(1바이트) → int(4바이트) 자동 변환
        long l = i;    // int → long 자동 변환
        System.out.println("byte 10 → int  : " + i);
        System.out.println("int     → long : " + l);

        // [2] 정수 → 실수 (int → double)
        System.out.println("\n[2] 정수 → 실수");
        int    num = 100;
        double d = num;   // 100 → 100.0
        System.out.println("int 100 → double : " + d);

        // [3] char → int (문자의 유니코드 코드값이 들어간다)
        System.out.println("\n[3] char → int");
        char c = 'A';
        int  code = c;    // 'A' → 65
        System.out.println("char 'A' → int : " + code);

        // [4] 자동 변환은 값 손실이 없다
        System.out.println("\n[4] 값 손실 없음 확인");
        float f = 123456789;      // int → float (넓은 그릇으로)
        System.out.println("int → float : " + f);

        // [주의] float(4바이트)는 정밀도가 낮아 아주 큰 int는 근사될 수 있으나,
        //        '표현 범위' 관점에서는 넘치지 않으므로 자동 변환이 허용된다.
        System.out.println("\n[주의] float은 범위는 넓지만 정밀도가 낮아 큰 int는 근사될 수 있다.");

        // [왜?] 큰 그릇에 작은 물을 붓는 격이라 넘칠 위험이 없어 컴파일러가 알아서 해준다.
        System.out.println("\n[왜?] 큰 타입에 담으니 값이 넘칠 수 없어 안전 → 별도 표기 불필요.");

        System.out.println("\n프로그램 정상 종료");
    }
}
