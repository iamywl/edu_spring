package com.edu.javabook.ch06;

/**
 * 6.13 접근 제한자
 *
 * 접근 제한자(access modifier)는 멤버(또는 클래스)에 대한 '접근 허용 범위'를 정한다.
 *  - public    : 어디서나 접근 가능 (모든 패키지)
 *  - protected : 같은 패키지 + 다른 패키지의 '자식 클래스'에서 접근 가능
 *  - default   : 아무것도 안 붙임. 같은 패키지 안에서만 접근 가능 (package-private)
 *  - private   : 해당 클래스 내부에서만 접근 가능
 *
 * 접근 범위: private < default < protected < public (좁음 → 넓음)
 * 캡슐화를 위해 필드는 보통 private로 숨기고, 필요한 것만 public 메서드로 연다.
 */
public class AccessModifier {

    static class Sample {
        public int pub = 1;         // 어디서나
        protected int pro = 2;      // 같은 패키지 + 자식
        int def = 3;                // default: 같은 패키지
        private int pri = 4;        // 자기 클래스 안에서만

        // private 필드는 외부에서 직접 못 보므로, public 메서드로 열어준다
        public int getPri() {
            return pri;             // 클래스 내부이므로 private 접근 OK
        }
    }

    public static void main(String[] args) {

        System.out.println("=== 6.13 접근 제한자 ===");

        // [1] 네 가지 접근 제한자 개요
        System.out.println("\n[1] 접근 제한자 종류");
        System.out.println("public    → 모든 곳");
        System.out.println("protected → 같은 패키지 + 자식 클래스");
        System.out.println("default   → 같은 패키지 (키워드 없음)");
        System.out.println("private   → 같은 클래스 안");

        // [2] 같은 클래스/패키지에서는 private 외 모두 접근 가능
        System.out.println("\n[2] 같은 패키지에서 접근");
        Sample s = new Sample();
        System.out.println("pub = " + s.pub + " (public)");
        System.out.println("pro = " + s.pro + " (protected, 같은 패키지)");
        System.out.println("def = " + s.def + " (default, 같은 패키지)");

        // [3] private 필드는 직접 접근 불가 → 메서드를 통해서만
        System.out.println("\n[3] private 접근");
        System.out.println("s.pri 직접 접근은 컴파일 오류 → getPri() 사용");
        System.out.println("s.getPri() = " + s.getPri());

        // [4] 접근 범위 넓이 순서
        System.out.println("\n[4] 접근 범위 (좁음 → 넓음)");
        System.out.println("private < default < protected < public");

        // [왜?] 접근 제한자로 내부 구현을 숨기고 꼭 필요한 부분만 공개해 캡슐화를 실현한다.
        System.out.println("\n[왜?] 최소한만 공개하면 결합도가 낮아지고 유지보수가 쉬워진다.");

        System.out.println("\n프로그램 정상 종료");
    }
}
