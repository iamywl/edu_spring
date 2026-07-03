package com.edu.javabook.ch09;

/**
 * 9.3 정적 멤버 클래스 (static nested class)
 *
 * 정적 멤버 클래스는 바깥 클래스 멤버 자리에 static 을 붙여 선언한 클래스다.
 * 인스턴스 멤버 클래스와 달리 "바깥 객체가 필요 없다".
 *
 * 생성 문법:  바깥클래스.내부클래스()
 *   예)  StaticMemberClass.Point p = new StaticMemberClass.Point(1, 2);
 *
 * 특징:
 *   - 바깥 객체와 무관하므로 바깥의 "인스턴스" 필드/메서드에는 접근 불가.
 *   - 바깥의 "static" 필드/메서드에는 접근 가능.
 *   - 자기 자신도 static 멤버를 자유롭게 가질 수 있다.
 *   - 특정 바깥 객체에 소속되지 않는 "독립적인 도우미 타입"에 적합하다.
 *     (예: 좌표 Point, 노드 Node, 빌더 Builder 등)
 *
 * 예제: 좌표를 나타내는 Point 를 정적 멤버 클래스로 두고, 두 점 사이 거리를 구한다.
 */
public class StaticMemberClass {

    // 바깥 클래스의 static 필드 (정적 멤버 클래스가 접근 가능)
    static int createdCount = 0;

    // ── 정적 멤버 클래스 (static 붙음) ──
    static class Point {
        int x;
        int y;

        Point(int x, int y) {
            this.x = x;
            this.y = y;
            createdCount++;     // 바깥의 static 필드는 접근 가능
        }

        // 정적 멤버 클래스는 자체 static 메서드도 가질 수 있다.
        static double distance(Point a, Point b) {
            int dx = a.x - b.x;
            int dy = a.y - b.y;
            return Math.sqrt(dx * dx + dy * dy);
        }

        @Override
        public String toString() {
            return "(" + x + ", " + y + ")";
        }
    }

    public static void main(String[] args) {

        System.out.println("=== 9.3 정적 멤버 클래스 ===");

        // [1] 바깥 객체 없이 바로 생성: 바깥클래스.내부클래스()
        System.out.println("\n[1] 바깥 객체 없이 생성");
        StaticMemberClass.Point a = new StaticMemberClass.Point(0, 0);
        StaticMemberClass.Point b = new Point(3, 4);   // 같은 파일이라 짧게도 가능
        System.out.println("점 a = " + a);
        System.out.println("점 b = " + b);

        // [2] 정적 멤버 클래스의 static 메서드 사용
        System.out.println("\n[2] 두 점 사이 거리 (static 메서드)");
        double d = Point.distance(a, b);
        System.out.println("distance(a, b) = " + d);

        // [3] 바깥의 static 필드 공유 확인
        System.out.println("\n[3] 생성된 Point 개수 = " + createdCount);

        System.out.println("\n정리: 정적 멤버 클래스는 바깥 객체 없이 쓰는 독립 도우미 타입이다.");
        System.out.println("프로그램 정상 종료");
    }
}
