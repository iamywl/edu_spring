package com.edu.javabook.ch06;

/**
 * 6.10 정적 멤버
 *
 * static이 붙은 멤버(정적 필드/메서드)는 객체가 아니라 '클래스'에 소속된다.
 *  - 정적 필드: 모든 객체가 '공유'하는 하나의 값. 클래스이름.필드 로 접근.
 *  - 정적 메서드: 객체 없이 클래스이름.메서드() 로 호출. 인스턴스 멤버/this 사용 불가.
 *  - 정적 블록(static { }): 클래스가 메모리에 로딩될 때 딱 한 번 실행되어 정적 필드를 초기화.
 *
 * 정적 멤버는 객체를 생성하기 전에도 사용할 수 있다.
 */
public class StaticMember {

    static class Company {
        static String name;          // 정적 필드: 모든 객체 공유
        static int employeeCount;    // 정적 필드: 직원 수 카운트
        String empName;              // 인스턴스 필드: 객체마다 다름

        // 정적 블록: 클래스 로딩 시 1회 실행
        static {
            name = "에듀컴퍼니";
            employeeCount = 0;
            System.out.println("  [static 블록 실행] 회사 초기화 완료");
        }

        Company(String empName) {
            this.empName = empName;
            employeeCount++;         // 공유 필드를 증가 → 전체 직원 수
        }

        // 정적 메서드: 객체 없이 호출 가능
        static int getEmployeeCount() {
            return employeeCount;
        }
    }

    public static void main(String[] args) {

        System.out.println("=== 6.10 정적 멤버 ===");

        // [1] 객체 생성 전에도 정적 멤버 사용 가능 (static 블록이 먼저 실행됨)
        System.out.println("\n[1] 객체 없이 정적 필드 접근");
        System.out.println("Company.name = " + Company.name);
        System.out.println("초기 직원 수 = " + Company.getEmployeeCount());

        // [2] 정적 필드는 모든 객체가 공유
        System.out.println("\n[2] 정적 필드는 공유됨");
        Company e1 = new Company("홍길동");
        Company e2 = new Company("김철수");
        System.out.println("e1.empName = " + e1.empName + " (인스턴스: 각자)");
        System.out.println("e2.empName = " + e2.empName + " (인스턴스: 각자)");
        System.out.println("직원 수(공유) = " + Company.employeeCount + " (객체 2개 생성됨)");

        // [3] 정적 메서드 호출
        System.out.println("\n[3] 정적 메서드");
        System.out.println("Company.getEmployeeCount() → " + Company.getEmployeeCount());

        // [4] 공유 필드를 바꾸면 모두에게 반영
        System.out.println("\n[4] 공유 값 변경");
        Company.name = "뉴에듀";
        System.out.println("변경 후 Company.name = " + Company.name);

        // [왜?] 정적 멤버는 '객체와 무관한, 클래스 전체의 공통 데이터/기능'에 적합하다.
        System.out.println("\n[왜?] 공유 상태나 유틸 기능은 인스턴스가 아니라 클래스에 두는 것이 자연스럽다.");

        System.out.println("\n프로그램 정상 종료");
    }
}
