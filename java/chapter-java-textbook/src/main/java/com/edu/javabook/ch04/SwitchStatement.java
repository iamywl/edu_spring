package com.edu.javabook.ch04;

/**
 * 4.3 switch 문
 *
 * switch 문은 하나의 값을 여러 case와 비교하여 일치하는 곳부터 실행한다.
 * 조건이 "같은지(==)"만 따질 때, else if 사슬보다 읽기 쉽다.
 *
 * 전통 switch 형식:
 *   switch (값) {
 *     case A: ...; break;   // break가 없으면 아래 case로 계속 흘러감(fall-through)
 *     default: ...;         // 어느 case에도 안 맞으면 default
 *   }
 *
 * switch 표현식(JDK 14+): case ... -> 값;  형태로 값을 돌려줄 수 있고 fall-through가 없다.
 */
public class SwitchStatement {

    public static void main(String[] args) {

        System.out.println("=== 4.3 switch 문 ===");

        // [1] 전통 switch : break로 각 case를 끊는다.
        System.out.println("\n[1] 전통 switch");
        int day = 3;
        switch (day) {
            case 1:
                System.out.println("월요일");
                break;
            case 2:
                System.out.println("화요일");
                break;
            case 3:
                System.out.println("수요일");   // day=3 → 여기 실행 후 break
                break;
            default:
                System.out.println("그 외 요일");
        }

        // [2] fall-through : break를 일부러 생략해 여러 case를 묶는다.
        System.out.println("\n[2] fall-through (break 생략)");
        int month = 4;
        switch (month) {
            case 3:
            case 4:
            case 5:
                System.out.println(month + "월은 봄");   // 3,4,5월이 하나로 묶임
                break;
            case 6:
            case 7:
            case 8:
                System.out.println(month + "월은 여름");
                break;
            default:
                System.out.println(month + "월은 가을/겨울");
        }

        // [3] switch 표현식 (->) : 값을 바로 돌려준다. fall-through 없음, break 불필요.
        System.out.println("\n[3] switch 표현식 (화살표)");
        int grade = 2;
        String result = switch (grade) {
            case 1 -> "1학년";
            case 2 -> "2학년";   // grade=2 → 이 값이 result에 들어감
            case 3 -> "3학년";
            default -> "학년 미상";
        };
        System.out.println("result = " + result);

        // [4] switch 표현식 + yield : 블록 안에서 값을 돌려줄 때 yield 사용
        System.out.println("\n[4] switch 표현식 + yield");
        int code = 2;
        String label = switch (code) {
            case 1 -> "낮음";
            case 2 -> {
                String prefix = "중간";   // 여러 문장이 필요하면 { } 블록 + yield
                yield prefix + " 등급";
            }
            default -> "높음";
        };
        System.out.println("label = " + label);

        System.out.println("\n프로그램 정상 종료");
    }
}
