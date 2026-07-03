package com.edu.javabook.ch02;

/**
 * 2.10 문자열을 기본 타입으로 변환
 *
 * 키보드 입력·파일·네트워크로 들어오는 값은 대부분 '문자열(String)'이다.
 * "123" 은 문자열이라 그대로는 계산할 수 없으므로, 숫자 타입으로 변환해야 한다.
 * 각 Wrapper 클래스의 parseXxx() 정적 메서드를 사용한다.
 */
public class StringToPrimitive {

    public static void main(String[] args) {

        System.out.println("=== 2.10 문자열을 기본 타입으로 변환 ===");

        // [1] 문자열 → 각 기본 타입
        System.out.println("\n[1] parseXxx 메서드들");
        byte    b = Byte.parseByte("120");
        short   s = Short.parseShort("30000");
        int     i = Integer.parseInt("12345");
        long    l = Long.parseLong("9000000000");
        float   f = Float.parseFloat("3.14");
        double  d = Double.parseDouble("3.141592");
        boolean bool = Boolean.parseBoolean("true");
        System.out.println("Byte.parseByte(\"120\")        = " + b);
        System.out.println("Short.parseShort(\"30000\")     = " + s);
        System.out.println("Integer.parseInt(\"12345\")     = " + i);
        System.out.println("Long.parseLong(\"9000000000\")  = " + l);
        System.out.println("Float.parseFloat(\"3.14\")      = " + f);
        System.out.println("Double.parseDouble(\"3.141592\")= " + d);
        System.out.println("Boolean.parseBoolean(\"true\")  = " + bool);

        // [2] 변환해야 계산이 된다
        System.out.println("\n[2] 문자열은 그대로 더하면 '연결'된다");
        String n1 = "10", n2 = "20";
        System.out.println("문자열 \"10\" + \"20\" = " + (n1 + n2) + "  (연결!)");
        int calc = Integer.parseInt(n1) + Integer.parseInt(n2);
        System.out.println("숫자로 변환 후 10 + 20 = " + calc + "  (덧셈)");

        // [3] 반대로 기본 타입 → 문자열
        System.out.println("\n[3] 기본 타입 → 문자열");
        int num = 500;
        String str1 = String.valueOf(num);   // 방법 1: String.valueOf
        String str2 = "" + num;               // 방법 2: 빈 문자열과 연결
        String str3 = Integer.toString(num);  // 방법 3: toString
        System.out.println("String.valueOf(500) = " + str1);
        System.out.println("\"\" + 500          = " + str2);
        System.out.println("Integer.toString(500)= " + str3);

        // [4] 잘못된 형식이면 예외 발생 → try-catch 로 안전 처리
        System.out.println("\n[4] 변환 실패 처리");
        String bad = "12a3";
        try {
            int v = Integer.parseInt(bad);
            System.out.println(v);
        } catch (NumberFormatException e) {
            System.out.println("\"" + bad + "\" 는 숫자가 아니라 변환 실패 → 예외 처리됨");
        }

        // [왜?] 외부 입력은 항상 문자열이므로, 계산 전 숫자 변환은 필수 과정이다.
        System.out.println("\n[왜?] 입력은 늘 문자열 → 계산하려면 parseXxx로 숫자 변환이 반드시 필요.");

        System.out.println("\n프로그램 정상 종료");
    }
}
