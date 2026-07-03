package com.edu.javabook.ch06;

/**
 * 6.15 싱글톤 패턴
 *
 * 싱글톤(Singleton)은 클래스의 인스턴스를 '단 하나만' 생성해 공유하는 디자인 패턴이다.
 * 만드는 방법:
 *  1) 자기 자신 타입의 정적 필드 하나를 private로 둔다.
 *  2) 생성자를 private로 막아 외부에서 new 를 못 하게 한다.
 *  3) 유일한 인스턴스를 돌려주는 public static 메서드(getInstance)를 제공한다.
 *
 * 설정 정보, 로그 관리자처럼 '전체에서 하나만 있으면 되는' 자원에 적합하다.
 */
public class SingletonPattern {

    static class Settings {
        // 1) 유일한 인스턴스를 담는 정적 필드
        private static final Settings INSTANCE = new Settings();

        private String theme = "라이트";

        // 2) private 생성자: 외부 new 차단
        private Settings() {
            System.out.println("  [생성] Settings 인스턴스가 딱 한 번 생성됨");
        }

        // 3) 유일한 인스턴스 반환
        public static Settings getInstance() {
            return INSTANCE;
        }

        public String getTheme() {
            return theme;
        }

        public void setTheme(String theme) {
            this.theme = theme;
        }
    }

    public static void main(String[] args) {

        System.out.println("=== 6.15 싱글톤 패턴 ===");

        // [1] getInstance()로 인스턴스 획득 (new 불가)
        System.out.println("\n[1] 인스턴스 획득");
        Settings s1 = Settings.getInstance();
        Settings s2 = Settings.getInstance();
        System.out.println("s1 획득 완료, s2 획득 완료");

        // [2] 두 참조가 '같은 하나의 객체'를 가리킴
        System.out.println("\n[2] 항상 같은 객체");
        System.out.println("(s1 == s2) → " + (s1 == s2) + " (동일 인스턴스)");

        // [3] 한 곳에서 바꾼 상태가 모든 곳에 반영
        System.out.println("\n[3] 공유 상태");
        System.out.println("초기 테마: " + s1.getTheme());
        s1.setTheme("다크");
        System.out.println("s1.setTheme(\"다크\") 후 s2.getTheme() → " + s2.getTheme());

        // [4] private 생성자로 외부 new 차단됨
        System.out.println("\n[4] new 차단");
        System.out.println("생성자가 private → new Settings() 는 컴파일 오류");

        // [왜?] 싱글톤은 전역에서 공유해야 하는 유일 자원을 안전하게 하나로 관리한다.
        System.out.println("\n[왜?] 인스턴스를 하나로 강제해 상태 공유와 자원 낭비 방지를 동시에 얻는다.");

        System.out.println("\n프로그램 정상 종료");
    }
}
