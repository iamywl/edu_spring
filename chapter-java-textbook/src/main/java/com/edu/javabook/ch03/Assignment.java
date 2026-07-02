package com.edu.javabook.ch03;

/**
 * Chapter 03 연산자 - 3.10 대입 연산자
 *
 * 이 소절 하나만 다룹니다: 기본 대입 =, 그리고 복합 대입 +=, -=, *=, /=, %=, <<=, &= 등.
 *
 * 핵심 결론(먼저 봅니다):
 *   - =는 오른쪽 값을 왼쪽 변수에 '저장'합니다. 결과값은 저장된 값이라 연쇄 대입(a = b = 3)도 됩니다.
 *   - a += b 는 a = a + b 의 축약이지만, 한 가지 숨은 차이가 있습니다: 자동 형변환(암묵적 캐스트)을 포함합니다.
 *   - 그래서 byte b; b += 1; 은 되지만 b = b + 1; 은 컴파일 에러가 날 수 있습니다.
 */
public class Assignment {

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println(" 3.10 대입 연산자 (Assignment)");
        System.out.println("==================================================\n");

        basicAssign();
        chainedAssign();
        compoundAssign();
        hiddenCast();
    }

    private static void basicAssign() {
        System.out.println("[1] 기본 대입 =");
        int a = 10;
        System.out.println("  int a = 10; => a = " + a);
        System.out.println();
    }

    /** 대입식은 값을 가지므로 연쇄 대입이 가능 */
    private static void chainedAssign() {
        System.out.println("[2] 연쇄 대입 (a = b = c = 5)");
        int a, b, c;
        a = b = c = 5;   // 오른쪽부터: c=5, b=5, a=5
        System.out.println("  a=b=c=5 => a=" + a + ", b=" + b + ", c=" + c);
        System.out.println("  (대입식 자체가 대입된 값을 결과로 내기 때문)");
        System.out.println();
    }

    /** 복합 대입 연산자 모음 */
    private static void compoundAssign() {
        System.out.println("[3] 복합 대입 연산자");
        int x = 20;
        System.out.println("  시작 x = " + x);
        x += 5;  System.out.println("  x += 5  => " + x);
        x -= 3;  System.out.println("  x -= 3  => " + x);
        x *= 2;  System.out.println("  x *= 2  => " + x);
        x /= 4;  System.out.println("  x /= 4  => " + x);
        x %= 7;  System.out.println("  x %= 7  => " + x);
        x <<= 2; System.out.println("  x <<= 2 => " + x);
        x &= 6;  System.out.println("  x &= 6  => " + x);
        System.out.println();
    }

    /** 복합 대입에 숨은 자동 형변환 */
    private static void hiddenCast() {
        System.out.println("[4] 복합 대입의 숨은 형변환");
        byte b = 10;
        // b = b + 1;  // 컴파일 에러! (b + 1은 int라서 byte에 담으려면 캐스트 필요)
        b += 1;        // OK: += 는 결과를 byte로 자동 캐스트 해줌
        System.out.println("  byte b=10; b += 1; => " + b + "  (+= 는 암묵적 캐스트 포함)");
        System.out.println("  b = b + 1; 은 int 결과라 그대로면 컴파일 에러가 날 수 있다.");
        System.out.println();
        System.out.println("정상 종료.");
    }
}
