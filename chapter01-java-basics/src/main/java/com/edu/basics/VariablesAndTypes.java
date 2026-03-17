package com.edu.basics;

/**
 * Chapter 01 - 변수와 데이터 타입, 연산자
 *
 * 이 클래스는 Java의 기본 데이터 타입, 형변환, 문자열 연산,
 * var 키워드, 그리고 다양한 연산자를 다룹니다.
 */
public class VariablesAndTypes {

    public static void main(String[] args) {
        System.out.println("====================================");
        System.out.println(" Chapter 01: 변수와 데이터 타입, 연산자");
        System.out.println("====================================\n");

        primitiveTypes();
        typeCasting();
        stringOperations();
        varKeyword();
        operators();
    }

    // ──────────────────────────────────────────────
    // 1. 기본형 (Primitive Types)
    // ──────────────────────────────────────────────
    static void primitiveTypes() {
        System.out.println("── 1. 기본형 (Primitive Types) ──");

        // 정수형
        byte byteVar = 127;                    // 1바이트: -128 ~ 127
        short shortVar = 32767;                // 2바이트: -32,768 ~ 32,767
        int intVar = 2_147_483_647;            // 4바이트: 약 -21억 ~ 21억 (언더스코어로 가독성 향상)
        long longVar = 9_223_372_036_854_775_807L; // 8바이트: Long.MAX_VALUE, L 접미사 필수

        System.out.println("byte  : " + byteVar);
        System.out.println("short : " + shortVar);
        System.out.println("int   : " + intVar);
        System.out.println("long  : " + longVar);

        // 실수형
        float floatVar = 3.14f;     // 4바이트: f 접미사 필수
        double doubleVar = 3.14159; // 8바이트: 기본 실수 타입

        System.out.println("float : " + floatVar);
        System.out.println("double: " + doubleVar);

        // 문자형
        char charVar = 'A';          // 2바이트: 유니코드 문자 하나
        char koreanChar = '가';      // 한글도 가능 (유니코드)
        char unicodeChar = '\u0041'; // 유니코드 코드로 지정 ('A')

        System.out.println("char (영문)   : " + charVar);
        System.out.println("char (한글)   : " + koreanChar);
        System.out.println("char (유니코드): " + unicodeChar);

        // 논리형
        boolean boolTrue = true;
        boolean boolFalse = false;

        System.out.println("boolean true : " + boolTrue);
        System.out.println("boolean false: " + boolFalse);

        // 각 타입의 기본값 (클래스 필드일 때)
        // byte=0, short=0, int=0, long=0L, float=0.0f, double=0.0, char='\u0000', boolean=false
        System.out.println();
    }

    // ──────────────────────────────────────────────
    // 2. 형변환 (Type Casting)
    // ──────────────────────────────────────────────
    static void typeCasting() {
        System.out.println("── 2. 형변환 (Type Casting) ──");

        // 자동 형변환 (Widening Casting) - 작은 타입 → 큰 타입
        // byte → short → int → long → float → double
        int intValue = 100;
        long longValue = intValue;       // int → long (자동)
        float floatValue = longValue;    // long → float (자동)
        double doubleValue = floatValue; // float → double (자동)

        System.out.println("[자동 형변환]");
        System.out.println("int    → long  : " + intValue + " → " + longValue);
        System.out.println("long   → float : " + longValue + " → " + floatValue);
        System.out.println("float  → double: " + floatValue + " → " + doubleValue);

        // 강제 형변환 (Narrowing Casting) - 큰 타입 → 작은 타입
        // 데이터 손실이 발생할 수 있으므로 명시적 캐스팅 필요
        double pi = 3.14159;
        int intPi = (int) pi;          // 소수점 이하 손실 → 3
        byte bytePi = (byte) intPi;    // 범위 내이므로 정상

        System.out.println("\n[강제 형변환]");
        System.out.println("double → int : " + pi + " → " + intPi + " (소수점 손실)");
        System.out.println("int    → byte: " + intPi + " → " + bytePi);

        // 오버플로우 예시
        int bigInt = 300;
        byte overflowByte = (byte) bigInt; // 300은 byte 범위 초과 → 44
        System.out.println("int(300) → byte: " + bigInt + " → " + overflowByte + " (오버플로우!)");

        // char와 int 간 변환
        char ch = 'A';
        int asciiValue = ch;            // char → int (자동): 65
        char fromInt = (char) 66;       // int → char (강제): 'B'

        System.out.println("\nchar 'A' → int: " + asciiValue);
        System.out.println("int 66 → char : " + fromInt);
        System.out.println();
    }

    // ──────────────────────────────────────────────
    // 3. 문자열 연산 (String Operations)
    // ──────────────────────────────────────────────
    static void stringOperations() {
        System.out.println("── 3. 문자열 연산 (String Operations) ──");

        // 문자열 생성
        String str1 = "Hello";               // 문자열 리터럴 (String Pool 사용)
        String str2 = new String("Hello");    // new 키워드 (힙에 새 객체 생성)
        String str3 = "Hello";               // str1과 같은 String Pool 객체 참조

        // == vs equals()
        // == 는 참조(주소) 비교, equals()는 내용 비교
        System.out.println("str1 == str3       : " + (str1 == str3));       // true (같은 풀 객체)
        System.out.println("str1 == str2       : " + (str1 == str2));       // false (다른 객체)
        System.out.println("str1.equals(str2)  : " + str1.equals(str2));    // true (내용 동일)

        // 주요 String 메서드
        String text = "  Java Programming  ";
        System.out.println("\n원본 문자열: \"" + text + "\"");
        System.out.println("length()    : " + text.length());           // 길이
        System.out.println("trim()      : \"" + text.trim() + "\"");   // 앞뒤 공백 제거
        System.out.println("toUpperCase(): " + text.trim().toUpperCase()); // 대문자 변환
        System.out.println("toLowerCase(): " + text.trim().toLowerCase()); // 소문자 변환
        System.out.println("charAt(2)   : " + text.charAt(2));            // 인덱스 위치 문자
        System.out.println("substring(0,4): " + text.trim().substring(0, 4)); // 부분 문자열
        System.out.println("contains(\"Java\"): " + text.contains("Java")); // 포함 여부
        System.out.println("replace(\"Java\",\"Kotlin\"): " + text.trim().replace("Java", "Kotlin"));

        // 문자열 연결
        String firstName = "홍";
        String lastName = "길동";
        String fullName = firstName + lastName;        // + 연산자
        String greeting = String.format("안녕하세요, %s님!", fullName); // format

        System.out.println("\n문자열 연결: " + fullName);
        System.out.println("String.format: " + greeting);

        // 텍스트 블록 (Java 15+) - 여러 줄 문자열
        String json = """
                {
                    "name": "홍길동",
                    "age": 25
                }
                """;
        System.out.println("텍스트 블록:\n" + json);

        // StringBuilder - 문자열을 자주 변경할 때 사용 (가변 객체)
        StringBuilder sb = new StringBuilder();
        sb.append("Java");
        sb.append(" ");
        sb.append("기초");
        sb.insert(0, "[");
        sb.append("]");
        System.out.println("StringBuilder: " + sb.toString());
        System.out.println();
    }

    // ──────────────────────────────────────────────
    // 4. var 키워드 (Java 10+)
    // ──────────────────────────────────────────────
    static void varKeyword() {
        System.out.println("── 4. var 키워드 (Java 10+) ──");

        // var는 지역 변수에서만 사용 가능
        // 컴파일 시점에 타입이 결정됨 (타입 추론)
        var message = "안녕하세요";        // String으로 추론
        var count = 42;                    // int로 추론
        var price = 19.99;                 // double로 추론
        var flag = true;                   // boolean으로 추론
        var numbers = new int[]{1, 2, 3};  // int[]로 추론

        System.out.println("var message (String) : " + message);
        System.out.println("var count   (int)    : " + count);
        System.out.println("var price   (double) : " + price);
        System.out.println("var flag    (boolean): " + flag);
        System.out.println("var numbers (int[])  : 길이=" + numbers.length);

        // var 사용 시 주의사항:
        // 1. 클래스 필드(멤버 변수)에는 사용 불가
        // 2. 메서드 매개변수에는 사용 불가
        // 3. 반환 타입에는 사용 불가
        // 4. null로 초기화 불가: var x = null; (X)
        // 5. 람다 표현식에 직접 사용 불가: var f = () -> {}; (X)

        // for 루프에서의 var 사용
        System.out.print("var in for loop: ");
        for (var i = 0; i < 5; i++) {
            System.out.print(i + " ");
        }
        System.out.println("\n");
    }

    // ──────────────────────────────────────────────
    // 5. 연산자 (Operators)
    // ──────────────────────────────────────────────
    static void operators() {
        System.out.println("── 5. 연산자 (Operators) ──");

        // 5.1 산술 연산자
        System.out.println("[산술 연산자]");
        int a = 10, b = 3;
        System.out.println("10 + 3 = " + (a + b));   // 덧셈: 13
        System.out.println("10 - 3 = " + (a - b));   // 뺄셈: 7
        System.out.println("10 * 3 = " + (a * b));   // 곱셈: 30
        System.out.println("10 / 3 = " + (a / b));   // 나눗셈: 3 (정수 나눗셈)
        System.out.println("10 % 3 = " + (a % b));   // 나머지: 1

        // 정수 나눗셈 vs 실수 나눗셈
        System.out.println("10 / 3   (정수) = " + (10 / 3));       // 3
        System.out.println("10.0 / 3 (실수) = " + (10.0 / 3));     // 3.3333...

        // 5.2 증감 연산자
        System.out.println("\n[증감 연산자]");
        int x = 5;
        System.out.println("x = " + x);
        System.out.println("x++ = " + (x++)); // 후위: 현재 값(5) 반환 후 증가
        System.out.println("x   = " + x);     // 6
        System.out.println("++x = " + (++x)); // 전위: 증가 후 값(7) 반환
        System.out.println("x   = " + x);     // 7

        // 5.3 비교 연산자
        System.out.println("\n[비교 연산자]");
        System.out.println("10 == 3 : " + (10 == 3));  // false
        System.out.println("10 != 3 : " + (10 != 3));  // true
        System.out.println("10 > 3  : " + (10 > 3));   // true
        System.out.println("10 < 3  : " + (10 < 3));   // false
        System.out.println("10 >= 10: " + (10 >= 10)); // true
        System.out.println("10 <= 3 : " + (10 <= 3));  // false

        // 5.4 논리 연산자
        System.out.println("\n[논리 연산자]");
        boolean p = true, q = false;
        System.out.println("true && false : " + (p && q));  // AND: false
        System.out.println("true || false : " + (p || q));  // OR: true
        System.out.println("!true         : " + (!p));       // NOT: false

        // 단축 평가 (Short-circuit Evaluation)
        // && : 왼쪽이 false이면 오른쪽 평가 안 함
        // || : 왼쪽이 true이면 오른쪽 평가 안 함
        String str = null;
        boolean safe = (str != null) && (str.length() > 0); // str이 null이면 length() 호출 안 함
        System.out.println("단축 평가 (null 안전): " + safe);

        // 5.5 비트 연산자
        System.out.println("\n[비트 연산자]");
        int m = 0b1010; // 10 (2진수)
        int n = 0b1100; // 12 (2진수)
        System.out.println("1010 & 1100 (AND) : " + Integer.toBinaryString(m & n));  // 1000
        System.out.println("1010 | 1100 (OR)  : " + Integer.toBinaryString(m | n));  // 1110
        System.out.println("1010 ^ 1100 (XOR) : " + Integer.toBinaryString(m ^ n));  // 0110
        System.out.println("~1010       (NOT) : " + Integer.toBinaryString(~m));
        System.out.println("1010 << 2 (좌시프트): " + (m << 2));  // 40
        System.out.println("1010 >> 1 (우시프트): " + (m >> 1));  // 5

        // 5.6 삼항 연산자
        System.out.println("\n[삼항 연산자]");
        int age = 20;
        String status = (age >= 18) ? "성인" : "미성년자";
        System.out.println("나이 " + age + " → " + status);

        // 5.7 instanceof 연산자
        System.out.println("\n[instanceof 연산자]");
        Object obj = "Hello";
        System.out.println("\"Hello\" instanceof String: " + (obj instanceof String)); // true
        System.out.println("\"Hello\" instanceof Integer: " + (obj instanceof Integer)); // false

        // Java 16+ 패턴 매칭 instanceof
        if (obj instanceof String s) {
            // s는 이미 String 타입으로 캐스팅됨
            System.out.println("패턴 매칭: 문자열 길이 = " + s.length());
        }
        System.out.println();
    }
}
