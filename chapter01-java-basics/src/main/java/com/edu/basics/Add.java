package com.edu.basics;

/**
 * 바이트코드 데모 — 아주 단순한 add 메서드가 어떤 바이트코드로 번역되는지 보기 위한 클래스.
 *
 * JAVA 교재 심화편 1장 "바이트코드와 javap"와 연결된다.
 *
 * 이 클래스를 컴파일한 뒤 아래 명령으로 바이트코드를 직접 볼 수 있다:
 *   javap -c -p com.edu.basics.Add
 *
 * add(int a, int b)는 대략 다음 바이트코드로 번역된다:
 *   iload_1   // 1번 지역변수(a)를 operand stack에 push  (0번은 this)
 *   iload_2   // 2번 지역변수(b)를 push
 *   iadd      // 스택 위 두 값을 pop → 더한 값 push
 *   ireturn   // int 결과 반환
 */
public class Add {

    /** 인스턴스 메서드라 0번 지역변수는 this, 파라미터는 1번(a)·2번(b)부터 */
    int add(int a, int b) {
        return a + b;
    }

    public static void main(String[] args) {
        System.out.println("=== 바이트코드 데모: Add.add(int, int) ===\n");

        Add calc = new Add();
        int result = calc.add(3, 4);
        System.out.println("add(3, 4) = " + result);

        System.out.println("""

            이 add 메서드는 JVM에서 아래 바이트코드로 실행됩니다:
              iload_1   // a(=3)를 operand stack에 올림  (0번은 this)
              iload_2   // b(=4)를 올림
              iadd      // 두 값을 더해 7을 스택에 남김
              ireturn   // 7을 반환

            직접 확인하려면 (컴파일된 클래스 대상):
              javap -c -p com.edu.basics.Add
            자바는 '스택 기반 가상 머신'이라 operand stack으로 계산합니다.""");
    }
}
