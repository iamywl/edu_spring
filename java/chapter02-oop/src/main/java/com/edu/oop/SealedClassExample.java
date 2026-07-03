package com.edu.oop;

/**
 * [개념 9] 봉인 클래스(sealed class)
 *
 * sealed class란? (Java 17+)
 * - "누가 나를 상속할 수 있는지"를 permits 절로 명시적으로 제한하는 클래스.
 * - 즉, 상속 계층을 닫아(봉인해) 통제한다.
 *   예) public abstract sealed class Shape permits Circle, Rectangle { ... }
 *   -> Shape 는 오직 Circle, Rectangle 만 상속할 수 있다. 그 외에는 컴파일 오류.
 *
 * 허용된 하위 클래스는 반드시 아래 중 하나로 선언해야 한다:
 *   - final       : 더 이상 상속 불가 (예: Circle)
 *   - sealed      : 다시 permits로 제한하며 상속 허용
 *   - non-sealed  : 봉인을 풀어 자유롭게 상속 허용 (예: Rectangle)
 *
 * 이 예제에서 사용하는 것: Shape(봉인) <- Circle(final), Rectangle(non-sealed)
 *
 * 봉인의 의미와 이점:
 *   - 하위 타입의 "전체 목록"을 컴파일러가 알 수 있다.
 *   - 그래서 switch 패턴 매칭에서 모든 경우를 다루면 default 없이도
 *     컴파일러가 완전성(exhaustiveness)을 검사해 준다 -> 실수 방지 & 시너지.
 */
public class SealedClassExample {

    public static void main(String[] args) {

        // 봉인된 계층의 허용된 두 하위 타입만 생성 가능하다.
        Shape circle = new Circle("빨강", 5.0);
        Shape rectangle = new Rectangle("파랑", 4.0, 6.0);
        Shape[] shapes = {circle, rectangle};

        // ------------------------------------------------------------
        // 1) area() 다형성: 각 도형이 자기 방식으로 넓이 계산
        // ------------------------------------------------------------
        printSection("1. area() 다형성");

        for (Shape shape : shapes) {
            // describe()는 내부에서 area()를 호출한다.
            // area()는 추상 메서드라, 실제 타입(Circle/Rectangle)의 구현이 실행된다.
            System.out.println(shape.describe());
        }

        // ------------------------------------------------------------
        // 2) 봉인 + switch 패턴 매칭의 시너지 (완전성 검사)
        // ------------------------------------------------------------
        printSection("2. sealed + switch 패턴 매칭 (default 불필요)");

        for (Shape shape : shapes) {
            // Shape의 하위 타입은 Circle, Rectangle 뿐임을 컴파일러가 안다.
            // 따라서 두 case를 모두 다루면 default 없이도 컴파일된다.
            // (만약 나중에 새 하위 타입이 permits에 추가되면, 여기서 컴파일 오류로
            //  '처리 안 된 케이스'를 알려주므로 누락을 방지할 수 있다.)
            String detail = switch (shape) {
                case Circle c -> "원      - 반지름=" + c.getRadius()
                        + ", 둘레=" + String.format("%.2f", c.circumference());
                case Rectangle r -> "직사각형 - 가로=" + r.getWidth()
                        + ", 세로=" + r.getHeight()
                        + ", 둘레=" + String.format("%.2f", r.perimeter())
                        + ", 정사각형? " + r.isSquare();
            };
            System.out.println(detail);
        }

        // ------------------------------------------------------------
        // 3) instanceof 패턴 매칭으로도 활용 가능
        // ------------------------------------------------------------
        printSection("3. instanceof 패턴 매칭 활용");

        for (Shape shape : shapes) {
            if (shape instanceof Circle c) {
                System.out.println(shape.getColor() + " 원의 넓이 -> "
                        + String.format("%.2f", c.area()));
            } else if (shape instanceof Rectangle r) {
                System.out.println(shape.getColor() + " 직사각형의 넓이 -> "
                        + String.format("%.2f", r.area()));
            }
        }

        // ------------------------------------------------------------
        // 4) 봉인의 의미 설명
        // ------------------------------------------------------------
        printSection("4. 봉인(sealed)의 의미");
        System.out.println("- Shape는 'permits Circle, Rectangle'로 상속을 제한한다.");
        System.out.println("- Circle 은 final     -> 더 이상 상속 불가.");
        System.out.println("- Rectangle 은 non-sealed -> 봉인을 풀어 자유 상속 허용.");
        System.out.println("- 하위 타입 목록이 고정되어, switch 완전성 검사가 가능하다.");

        // ------------------------------------------------------------
        // 정리
        // ------------------------------------------------------------
        printSection("정리");
        System.out.println("- sealed는 상속 가능한 하위 타입을 permits로 통제한다.");
        System.out.println("- 하위는 final / sealed / non-sealed 중 하나여야 한다.");
        System.out.println("- switch 패턴 매칭과 결합하면 모든 경우를 안전하게 처리한다.");
        System.out.println("- area()처럼 다형성은 그대로 활용된다.");
    }

    private static void printSection(String title) {
        System.out.println();
        System.out.println("=".repeat(60));
        System.out.println("  " + title);
        System.out.println("=".repeat(60));
    }
}
