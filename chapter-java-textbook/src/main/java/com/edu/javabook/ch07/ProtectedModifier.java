package com.edu.javabook.ch07;

/**
 * 7.6 protected 접근 제한자
 *
 * 접근 제한자는 필드/메소드를 "어디까지 접근 가능하게 할지" 정한다.
 * 상속과 가장 관련 깊은 것이 protected 이다.
 *
 * 접근 범위(좁음 → 넓음) :
 *   private   : 같은 클래스 안에서만
 *   (default) : 같은 패키지 안에서만
 *   protected : 같은 패키지 + "다른 패키지라도 자식(상속) 클래스"에서 접근 가능
 *   public    : 어디서나
 *
 * 즉 protected 는 "외부에는 감추되, 자식에게는 물려주고 싶은" 멤버에 쓴다.
 * private 필드는 자식이 직접 접근할 수 없지만, protected 필드는 자식이 직접 접근할 수 있다.
 *
 * 이 소절에서는 자식이 부모의 protected 멤버에 접근하는 모습을 확인한다.
 */
public class ProtectedModifier {

    static class Employee {
        protected String name;      // 자식이 직접 접근 가능
        protected int baseSalary;   // 자식이 직접 접근 가능
        private String ssn;         // 자식도 직접 접근 불가(외부 완전 차단)

        Employee(String name, int baseSalary, String ssn) {
            this.name = name;
            this.baseSalary = baseSalary;
            this.ssn = ssn;
        }

        protected int calculatePay() {   // 자식이 재사용/재정의하기 좋은 protected 메소드
            return baseSalary;
        }
    }

    static class Manager extends Employee {
        private int bonus;

        Manager(String name, int baseSalary, String ssn, int bonus) {
            super(name, baseSalary, ssn);
            this.bonus = bonus;
        }

        @Override
        protected int calculatePay() {
            // 부모의 protected 필드 baseSalary 에 "직접" 접근 가능
            return baseSalary + bonus;
            // return ssn;  // private 이라 여기서 접근 불가(컴파일 오류)
        }
    }

    public static void main(String[] args) {

        System.out.println("=== 7.6 protected 접근 제한자 ===");

        // [1] 자식이 부모의 protected 필드에 직접 접근
        System.out.println("\n[1] 자식에서 protected 필드에 직접 접근");
        Manager m = new Manager("김부장", 3000, "900101-*", 1500);
        System.out.println("이름(protected 필드) : " + m.name);
        System.out.println("총 급여(protected 메소드 재정의) : " + m.calculatePay());

        // [2] 정리
        System.out.println("\n[2] 정리");
        System.out.println("- protected : 외부에는 숨기고 자식에게는 공개하는 중간 단계.");
        System.out.println("- private 멤버(ssn)는 자식조차 직접 접근할 수 없다.");

        System.out.println("\n프로그램 정상 종료");
    }
}
