package com.edu.javabook.ch12;

/**
 * 12.5 문자열 클래스
 *
 * String 은 문자들의 나열(문자열)을 다루는 클래스다. 가장 중요한 특징은
 * "불변(immutable)" 이라는 점이다. 한 번 만든 String 의 내용은 절대 바뀌지 않으며,
 * 문자열을 바꾸는 메서드는 원본을 두고 "새 문자열"을 만들어 반환한다.
 *
 * String 주요 메서드 :
 *   - length()            : 길이
 *   - charAt(i)           : i번째 문자
 *   - substring(a, b)     : a부터 b앞까지 잘라낸 문자열
 *   - indexOf("x")        : 문자열 위치 (없으면 -1)
 *   - replace(a, b)       : a를 b로 바꾼 새 문자열
 *   - toUpperCase()/trim()/split()/concat() 등
 *
 * StringBuilder :
 *   - 문자열을 "자주 바꿔야" 할 때는 String 대신 StringBuilder 를 쓴다.
 *   - "가변(mutable)" 이라 내부 내용을 직접 고칠 수 있어 효율적이다.
 *   - append(추가) / insert(끼워넣기) / reverse(뒤집기) 등을 제공한다.
 *
 * 이 소절에서는 String 의 주요 메서드와 불변성, 그리고 StringBuilder 를 확인한다.
 */
public class StringClass {

    public static void main(String[] args) {

        System.out.println("=== 12.5 문자열 클래스 ===");

        String str = "  Hello, Java World  ";

        // [1] String 주요 메서드
        System.out.println("\n[1] String 주요 메서드");
        System.out.println("  원본            = \"" + str + "\"");
        System.out.println("  length()        = " + str.length());
        System.out.println("  trim()          = \"" + str.trim() + "\"");
        System.out.println("  toUpperCase()   = " + str.trim().toUpperCase());
        System.out.println("  indexOf(\"Java\") = " + str.indexOf("Java"));
        System.out.println("  substring(9,13) = " + str.substring(9, 13));
        System.out.println("  replace('o','0')= " + str.trim().replace('o', '0'));
        System.out.println("  charAt(2)       = " + str.charAt(2));

        // [1-1] split : 구분자로 나누기
        String csv = "사과,바나나,포도";
        String[] fruits = csv.split(",");
        System.out.println("  split(\",\")      = " + fruits.length + "개 → " +
                fruits[0] + " / " + fruits[1] + " / " + fruits[2]);

        // [2] 불변성(immutable) : 문자열 메서드는 원본을 바꾸지 않는다
        System.out.println("\n[2] String 불변성");
        String origin = "java";
        String changed = origin.toUpperCase();  // 새 문자열 반환, origin 은 그대로
        System.out.println("  origin  = " + origin + "  (변하지 않음)");
        System.out.println("  changed = " + changed + "  (새로 만들어진 문자열)");

        // [3] StringBuilder : 가변, append / insert / reverse
        System.out.println("\n[3] StringBuilder (가변 문자열)");
        StringBuilder sb = new StringBuilder();
        sb.append("Hello");           // 뒤에 붙이기
        sb.append(" ").append("Java"); // 연쇄 호출 가능
        System.out.println("  append 후    = " + sb);
        sb.insert(5, "!");            // 5번 위치에 끼워넣기
        System.out.println("  insert(5,\"!\")= " + sb);
        sb.reverse();                 // 뒤집기
        System.out.println("  reverse()    = " + sb);
        System.out.println("  최종 toString= " + sb.toString());

        System.out.println("\n프로그램 정상 종료");
    }
}
