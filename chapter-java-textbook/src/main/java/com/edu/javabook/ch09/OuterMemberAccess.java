package com.edu.javabook.ch09;

/**
 * 9.5 바깥 멤버 접근 (Outer.this 와 캡처 규칙)
 *
 * 중첩 클래스는 "바깥의 멤버"에 접근할 수 있는데, 그 방법과 범위는 종류마다 다르다.
 * 이 소절은 특히 "이름이 겹칠 때 어떤 것을 가리키는가"와
 * "바깥 객체 자신을 어떻게 가리키는가(Outer.this)"에 초점을 둔다.
 *
 * 규칙 정리:
 *   - this        : 지금 실행 중인 "내부 클래스 객체" 자신.
 *   - 바깥클래스.this : 그 내부 객체를 소유한 "바깥 객체" 자신.
 *   - 이름이 겹칠 때 우선순위: 로컬 변수/매개변수 → 내부 클래스 필드 → 바깥 필드.
 *     겹친 바깥 필드는 반드시 "바깥클래스.this.필드" 로 명시해야 접근된다.
 *
 * 예제: 바깥과 내부가 같은 이름의 필드 value 를 갖게 하고,
 *       내부에서 각각을 정확히 구분해 출력한다.
 */
public class OuterMemberAccess {

    // 바깥 클래스의 인스턴스 필드
    private int value = 100;
    private String owner = "바깥";

    // ── 인스턴스 멤버 클래스 : 바깥 이름과 겹치는 value 를 갖는다 ──
    class Inner {
        private int value = 20;     // 바깥의 value(100)와 이름이 겹친다

        void show(int value) {      // 매개변수 value 까지 이름이 세 개 겹침
            System.out.println("매개변수 value          = " + value);
            System.out.println("내부 필드 this.value     = " + this.value);
            // 겹친 바깥 필드는 'OuterMemberAccess.this' 로 명시해야 한다.
            System.out.println("바깥 필드 Outer.this.value = "
                    + OuterMemberAccess.this.value);
            // 겹치지 않는 바깥 멤버는 그냥 이름만으로 접근된다.
            System.out.println("바깥 필드 owner          = " + owner);
        }

        // 바깥 객체 자신을 반환하는 예 (Outer.this)
        OuterMemberAccess outer() {
            return OuterMemberAccess.this;
        }
    }

    public static void main(String[] args) {

        System.out.println("=== 9.5 바깥 멤버 접근 (Outer.this) ===");

        OuterMemberAccess outer = new OuterMemberAccess();
        Inner inner = outer.new Inner();

        // [1] 세 겹으로 겹친 value 를 각각 구분해 출력
        System.out.println("\n[1] 이름이 겹칠 때 무엇을 가리키는가 (인자 5 전달)");
        inner.show(5);

        // [2] Outer.this 는 내부 객체를 소유한 바깥 객체 자신
        System.out.println("\n[2] Outer.this 확인");
        boolean same = (inner.outer() == outer);
        System.out.println("inner.outer() == outer ? " + same
                + "  → 내부 객체가 가리키는 바깥 객체는 자신을 만든 그 객체다.");

        System.out.println("\n정리: 겹치면 안쪽이 우선, 바깥은 'Outer.this.필드' 로 명시한다.");
        System.out.println("프로그램 정상 종료");
    }
}
