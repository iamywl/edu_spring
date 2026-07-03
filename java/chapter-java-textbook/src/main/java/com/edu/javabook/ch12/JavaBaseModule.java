package com.edu.javabook.ch12;

/**
 * 12.2 java.base 모듈
 *
 * 자바 9부터 표준 API는 "모듈(module)" 이라는 큰 단위로 묶여 관리된다.
 * 모듈은 서로 관련 있는 여러 패키지를 하나로 묶은 것이다.
 *
 * java.base 모듈의 특징 :
 *   - 자바 표준 모듈 중 "가장 기본"이 되는 모듈이다.
 *   - 다른 모듈들은 대부분 java.base 에 의존한다.
 *   - 우리가 module-info.java 에 아무것도 적지 않아도 "항상 자동으로 포함"된다.
 *     (requires java.base; 를 쓰지 않아도 됨)
 *
 * java.lang 자동 import :
 *   - java.base 안의 여러 패키지 중 java.lang 은 특별하다.
 *   - java.lang 의 클래스(Object, String, System, Math, Integer 등)는
 *     import 문 없이 바로 쓸 수 있다. (컴파일러가 자동으로 import 해줌)
 *   - 반면 java.util, java.io 등은 필요할 때 직접 import 해야 한다.
 *
 * 이 소절에서는 java.base 소속 클래스들이 실제로 어떤 모듈에 속하는지,
 * 그리고 java.lang 이 import 없이 쓰이는 점을 코드로 확인한다.
 */
public class JavaBaseModule {

    public static void main(String[] args) {

        System.out.println("=== 12.2 java.base 모듈 ===");

        // [1] java.base 는 기본 모듈 : 대표 클래스들의 소속 모듈 확인
        System.out.println("\n[1] 대표 클래스가 속한 모듈");
        // import 없이 쓴 Object, String, System, Math 는 모두 java.lang → java.base 소속
        System.out.println("  Object → " + Object.class.getModule().getName());
        System.out.println("  String → " + String.class.getModule().getName());
        System.out.println("  System → " + System.class.getModule().getName());
        System.out.println("  Math   → " + Math.class.getModule().getName());

        // [2] java.util 클래스도 java.base 안에 있음 (단, import 는 필요)
        System.out.println("\n[2] java.util 도 java.base 모듈 소속");
        // java.util.List 를 완전 이름으로 사용 (개념 확인)
        System.out.println("  List(java.util) → " + java.util.List.class.getModule().getName());

        // [3] java.lang 자동 import 확인 : import 문 없이 바로 사용됨
        System.out.println("\n[3] java.lang 자동 import 확인");
        // 아래 String, Integer, Math 는 파일 상단에 import 가 전혀 없는데도 사용 가능하다
        String s = "자동 import";                 // java.lang.String
        int n = Integer.parseInt("100");           // java.lang.Integer
        double r = Math.sqrt(16);                  // java.lang.Math
        System.out.println("  String  : " + s);
        System.out.println("  Integer : parseInt(\"100\") = " + n);
        System.out.println("  Math    : sqrt(16) = " + r);
        System.out.println("  → 위 세 클래스는 import 없이 사용됨 (java.lang 자동 import)");

        // [4] 현재 실행 중인 이 클래스의 모듈 (이름 없는 모듈일 수 있음)
        System.out.println("\n[4] 현재 클래스의 모듈");
        Module myModule = JavaBaseModule.class.getModule();
        String name = myModule.getName();
        System.out.println("  이 클래스의 모듈 이름 = " + (name == null ? "(unnamed module)" : name));

        System.out.println("\n프로그램 정상 종료");
    }
}
