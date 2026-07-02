package com.edu.javabook.ch09;

/**
 * 9.6 중첩 인터페이스 (nested interface)
 *
 * 중첩 인터페이스는 클래스 "안"에 선언한 인터페이스다.
 * 특정 클래스와 강하게 연관된 인터페이스를 그 클래스 안에 두어
 * 관계를 분명히 하고, 이름 충돌을 피하며, 응집도를 높인다.
 * (대표 예: 버튼 클래스 안에 클릭 이벤트 리스너 인터페이스를 두는 UI 패턴)
 *
 * 규칙:
 *   - 클래스 안의 인터페이스는 자동으로 static 이다(암묵적 static).
 *     따라서 바깥 객체 없이 "바깥클래스.인터페이스" 로 참조한다.
 *   - 구현은 별도 클래스로도, 익명 클래스로도, 람다로도 할 수 있다.
 *     (인터페이스가 추상 메서드 1개면 함수형 인터페이스 → 람다 가능)
 *
 * 예제: Button(바깥) 안에 OnClick(중첩 인터페이스)을 두고,
 *       버튼을 누르면 등록된 콜백이 실행되도록 한다.
 */
public class NestedInterface {

    // ── 바깥 클래스 : 버튼 ──
    static class Button {
        // 중첩 인터페이스 : 클릭 콜백 (추상 메서드 1개 → 함수형 인터페이스)
        interface OnClick {
            void onClick(String buttonName);
        }

        private String name;
        private OnClick listener;   // 등록된 콜백

        Button(String name) {
            this.name = name;
        }

        // 콜백 등록
        void setOnClick(OnClick listener) {
            this.listener = listener;
        }

        // 버튼 눌림 → 등록된 콜백 실행
        void press() {
            System.out.println(name + " 버튼 눌림");
            if (listener != null) {
                listener.onClick(name);
            }
        }
    }

    public static void main(String[] args) {

        System.out.println("=== 9.6 중첩 인터페이스 ===");

        // [1] 중첩 인터페이스를 익명 클래스로 구현해 등록
        System.out.println("\n[1] 익명 클래스로 콜백 구현");
        Button ok = new Button("확인");
        ok.setOnClick(new NestedInterface.Button.OnClick() {
            @Override
            public void onClick(String buttonName) {
                System.out.println("  → " + buttonName + " 처리: 저장 완료");
            }
        });
        ok.press();

        // [2] 추상 메서드가 1개이므로 람다로도 구현 가능
        System.out.println("\n[2] 람다로 콜백 구현 (함수형 인터페이스)");
        Button cancel = new Button("취소");
        cancel.setOnClick(buttonName ->
                System.out.println("  → " + buttonName + " 처리: 작업 되돌림"));
        cancel.press();

        System.out.println("\n정리: 중첩 인터페이스는 암묵적 static, '바깥.인터페이스'로 참조한다.");
        System.out.println("프로그램 정상 종료");
    }
}
