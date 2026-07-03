package com.edu.javabook.ch09;

/**
 * 9.1 중첩 클래스 (개요)
 *
 * 중첩 클래스(nested class)란 "클래스 안에 선언된 클래스"를 말한다.
 * 특정 클래스 안에서만 주로 쓰이는 클래스를 밖으로 빼지 않고 안에 두어
 * 관계를 명확히 하고, 캡슐화를 강화하며, 코드를 가까이 모아둘 수 있다.
 *
 * 중첩 클래스는 "선언 위치"에 따라 다음 네 가지로 분류한다.
 *
 *   1) 인스턴스 멤버 클래스 : 바깥 클래스의 멤버 위치, static 없음.
 *                            → 바깥 "객체"가 있어야 만들 수 있다.        (9.2)
 *   2) 정적 멤버 클래스     : 바깥 클래스의 멤버 위치, static 붙음.
 *                            → 바깥 객체 없이 바로 만들 수 있다.          (9.3)
 *   3) 로컬 클래스          : 메서드(또는 초기화 블록) "안"에 선언.
 *                            → 그 메서드 안에서만 존재한다.               (9.4)
 *   4) 익명 클래스          : 이름 없이 선언과 동시에 객체를 생성.
 *                            → 일회성으로 한 번만 쓸 때 사용한다.         (9.7)
 *
 * 이 파일에서는 네 종류를 아주 단순한 형태로 한자리에 모아 "분류"를 보여준다.
 * 각 종류의 자세한 규칙은 뒤의 소절에서 하나씩 깊게 다룬다.
 */
public class NestedClassIntro {

    // [1] 인스턴스 멤버 클래스 : static 이 없다 → 바깥 객체에 소속
    class InstanceMember {
        String describe() {
            return "인스턴스 멤버 클래스 (바깥 객체 필요)";
        }
    }

    // [2] 정적 멤버 클래스 : static 이 붙는다 → 바깥 객체 불필요
    static class StaticMember {
        String describe() {
            return "정적 멤버 클래스 (바깥 객체 불필요)";
        }
    }

    // [3] 로컬 클래스는 메서드 안에서 선언한다 (아래 메서드 참고)
    String useLocalClass() {
        // 메서드 내부에 선언 → 이 메서드 밖에서는 존재하지 않는다.
        class Local {
            String describe() {
                return "로컬 클래스 (메서드 안에서만 존재)";
            }
        }
        return new Local().describe();
    }

    public static void main(String[] args) {

        System.out.println("=== 9.1 중첩 클래스 (개요) ===");

        System.out.println("\n[분류] 중첩 클래스 네 가지");
        System.out.println("1) 인스턴스 멤버 클래스 (static X)");
        System.out.println("2) 정적 멤버 클래스     (static O)");
        System.out.println("3) 로컬 클래스          (메서드 안)");
        System.out.println("4) 익명 클래스          (이름 없이 즉석 생성)");

        // 개요 확인을 위해 실제로 하나씩 만들어 본다.
        NestedClassIntro outer = new NestedClassIntro();

        System.out.println("\n[1] " + outer.new InstanceMember().describe());
        System.out.println("[2] " + new StaticMember().describe());
        System.out.println("[3] " + outer.useLocalClass());

        // [4] 익명 클래스 : Runnable 인터페이스를 이름 없이 즉석 구현
        Runnable anonymous = new Runnable() {
            @Override
            public void run() {
                System.out.println("[4] 익명 클래스 (이름 없이 즉석 생성)");
            }
        };
        anonymous.run();

        System.out.println("\n정리: 선언 '위치'가 종류를 결정한다.");
        System.out.println("프로그램 정상 종료");
    }
}
