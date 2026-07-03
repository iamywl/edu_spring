package com.edu.javabook.ch02;

/**
 * 2.1 변수 선언
 *
 * 변수(variable)란 값을 저장할 수 있는 "이름 붙은 메모리 공간"이다.
 * 자바는 정적 타입 언어이므로 변수를 사용하기 전에 반드시 "타입"과 "이름"으로 선언해야 한다.
 *
 * 형식:  타입 변수이름;            (선언만)
 *        타입 변수이름 = 값;       (선언 + 초기화)
 *        변수이름 = 값;            (대입)
 */
public class VariableDeclaration {

    public static void main(String[] args) {

        System.out.println("=== 2.1 변수 선언 ===");

        // [1] 선언과 초기화를 나누어 할 수 있다.
        System.out.println("\n[1] 선언 후 대입");
        int age;          // 선언: int 타입 age 라는 공간을 확보
        age = 25;         // 대입: 그 공간에 값 25 를 넣음
        System.out.println("age = " + age);

        // [2] 선언과 동시에 초기화 (가장 흔한 형태)
        System.out.println("\n[2] 선언과 동시에 초기화");
        int year = 2026;
        System.out.println("year = " + year);

        // [3] 같은 타입의 여러 변수를 콤마로 한 줄에 선언 가능
        System.out.println("\n[3] 여러 변수 한 줄 선언");
        int x = 1, y = 2, z = 3;
        System.out.println("x=" + x + ", y=" + y + ", z=" + z);

        // [4] 변수 값은 언제든 다시 바꿀 수 있다 (그래서 '변수' 이다)
        System.out.println("\n[4] 값 재대입");
        int score = 90;
        System.out.println("처음 score = " + score);
        score = 100;
        System.out.println("바뀐 score = " + score);

        // [5] 다른 변수의 값으로 초기화도 가능 (값이 복사됨)
        System.out.println("\n[5] 변수 값 복사");
        int a = 10;
        int b = a;   // a의 '값' 10이 b에 복사된다. 이후 a가 바뀌어도 b는 그대로.
        a = 99;
        System.out.println("a=" + a + " (바뀜), b=" + b + " (복사된 시점 값 유지)");

        // [6] 변수 이름 규칙(식별자):
        //  - 첫 글자는 문자, $, _ 만 가능 (숫자로 시작 불가)
        //  - 예약어(int, class 등)는 사용 불가
        //  - 관례상 camelCase 사용 (예: userName, totalCount)
        System.out.println("\n[6] 이름 규칙 예시");
        int userName_1 = 7;   // 유효한 이름
        System.out.println("userName_1 = " + userName_1);

        // [왜?] 타입을 명시하면 컴파일러가 "잘못된 값 대입"을 미리 막아준다.
        //       예) int age = "스물다섯"; 은 컴파일 에러 → 실행 전에 버그를 잡는다.
        System.out.println("\n[왜 선언이 필요한가?]");
        System.out.println("타입을 정해두면 컴파일러가 잘못된 값을 실행 전에 걸러준다.");

        System.out.println("\n프로그램 정상 종료");
    }
}
