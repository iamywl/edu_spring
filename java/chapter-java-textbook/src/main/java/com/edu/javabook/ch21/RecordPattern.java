package com.edu.javabook.ch21;

/**
 * 21.5 레코드 패턴 (Record Pattern)
 *
 * [레코드 패턴이란]
 * - Java 21에서 정식화된 기능으로, 레코드(record) 인스턴스를
 *   그 구성요소(component)로 분해(deconstruct)하면서 매칭하는 문법이다.
 * - case Point(int x, int y) 처럼 쓰면, 값이 Point이면 매칭되면서
 *   동시에 x, y 변수에 각 구성요소가 바로 추출된다.
 *
 * [중첩 분해]
 * - 레코드가 레코드를 품고 있으면 중첩해서 분해할 수 있다.
 *   예: case Line(Point(var x1, var y1), Point(var x2, var y2))
 *
 * [장점]
 * - instanceof + getter 호출을 반복하지 않고 한 번에 값을 꺼낼 수 있다.
 * - switch 패턴 매칭과 결합해 데이터 구조 기반 분기를 간결하게 만든다.
 */
public class RecordPattern {

    // 실습용 레코드 정의
    record Point(int x, int y) {}
    record Line(Point start, Point end) {}

    public static void main(String[] args) {

        System.out.println("=== 21.5 레코드 패턴 ===");

        // [1] instanceof에서의 레코드 패턴 분해
        System.out.println("\n[1] instanceof 로 분해");
        Object obj = new Point(3, 4);
        if (obj instanceof Point(int x, int y)) {   // x, y로 즉시 분해
            System.out.println("  Point로 분해됨 -> x=" + x + ", y=" + y);
            System.out.println("  원점과의 거리 제곱 = " + (x * x + y * y));
        }

        // [2] switch에서의 레코드 패턴
        System.out.println("\n[2] switch 로 도형 분해");
        System.out.println("  " + describe(new Point(0, 0)));
        System.out.println("  " + describe(new Point(5, 2)));

        // [3] 중첩 레코드 분해
        System.out.println("\n[3] 중첩 레코드 분해");
        Line line = new Line(new Point(1, 1), new Point(4, 5));
        System.out.println("  " + describeLine(line));

        // [4] var를 이용한 구성요소 타입 추론 분해
        System.out.println("\n[4] var 로 분해");
        Object something = new Line(new Point(0, 0), new Point(3, 4));
        if (something instanceof Line(Point(var x1, var y1), Point(var x2, var y2))) {
            int dx = x2 - x1;
            int dy = y2 - y1;
            double len = Math.sqrt(dx * dx + dy * dy);
            System.out.printf("  선분 (%d,%d)->(%d,%d) 길이 = %.1f%n", x1, y1, x2, y2, len);
        }

        System.out.println("\n[정리]");
        System.out.println("  레코드 패턴은 레코드를 구성요소로 즉시 분해하며,");
        System.out.println("  중첩 분해로 복잡한 데이터 구조도 간결하게 다룰 수 있다.");
    }

    // switch + 레코드 패턴 + when 가드
    private static String describe(Object obj) {
        return switch (obj) {
            case Point(int x, int y) when x == 0 && y == 0 -> "원점(0,0)";
            case Point(int x, int y) -> "점 (" + x + ", " + y + ")";
            default -> "알 수 없는 도형";
        };
    }

    // 중첩 레코드 패턴으로 Line을 두 Point로 분해
    private static String describeLine(Object obj) {
        return switch (obj) {
            case Line(Point(var sx, var sy), Point(var ex, var ey)) ->
                "선분: (" + sx + "," + sy + ") -> (" + ex + "," + ey + ")";
            default -> "선분 아님";
        };
    }
}
