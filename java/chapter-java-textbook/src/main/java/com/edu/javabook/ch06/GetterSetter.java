package com.edu.javabook.ch06;

/**
 * 6.14 Getter와 Setter
 *
 * 캡슐화의 실천 방법: 필드를 private로 숨기고, 값을 읽고 쓰는 통로로 메서드를 제공한다.
 *  - Getter: 필드 값을 읽는 메서드. 관례상 getXxx() (boolean은 isXxx()).
 *  - Setter: 필드 값을 바꾸는 메서드. 관례상 setXxx(값).
 *
 * Setter에서 '유효성 검증'을 하면 잘못된 값이 들어오는 것을 막을 수 있다.
 * (필드를 public으로 열어두면 아무 값이나 들어갈 수 있어 위험하다.)
 */
public class GetterSetter {

    static class Person {
        private String name;
        private int age;   // private: 직접 접근 차단

        // Getter: 읽기 통로
        public String getName() {
            return name;
        }

        public int getAge() {
            return age;
        }

        // Setter: 쓰기 통로 + 유효성 검증
        public void setName(String name) {
            if (name == null || name.isBlank()) {
                System.out.println("  [거부] 이름은 비어 있을 수 없습니다.");
                return;
            }
            this.name = name;
        }

        public void setAge(int age) {
            if (age < 0 || age > 150) {
                System.out.println("  [거부] 나이는 0~150 범위여야 합니다: " + age);
                return;
            }
            this.age = age;
        }
    }

    public static void main(String[] args) {

        System.out.println("=== 6.14 Getter와 Setter ===");
        Person p = new Person();

        // [1] Setter로 값 설정, Getter로 값 읽기
        System.out.println("\n[1] Setter/Getter 기본");
        p.setName("홍길동");
        p.setAge(30);
        System.out.println("이름: " + p.getName() + ", 나이: " + p.getAge());

        // [2] Setter의 유효성 검증: 잘못된 값은 거부됨
        System.out.println("\n[2] 유효성 검증 (잘못된 값)");
        p.setAge(-5);      // 거부됨
        p.setAge(200);     // 거부됨
        System.out.println("검증 실패 후 나이(그대로 유지) = " + p.getAge());

        // [3] 유효한 값은 정상 반영
        System.out.println("\n[3] 유효한 값");
        p.setAge(45);
        System.out.println("setAge(45) → 나이 = " + p.getAge());

        // [4] 빈 이름 거부
        System.out.println("\n[4] 빈 이름 거부");
        p.setName("   ");   // 거부됨
        System.out.println("검증 실패 후 이름(그대로 유지) = " + p.getName());

        // [왜?] Getter/Setter로 접근을 통제하면 객체가 항상 '올바른 상태'를 유지한다.
        System.out.println("\n[왜?] 필드를 감추고 메서드로만 접근시켜, 잘못된 데이터를 원천 차단한다.");

        System.out.println("\n프로그램 정상 종료");
    }
}
