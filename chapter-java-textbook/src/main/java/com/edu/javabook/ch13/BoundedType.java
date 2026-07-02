package com.edu.javabook.ch13;

/**
 * 13.4 제한된 타입 파라미터 (Bounded Type Parameter)
 *
 * 타입 파라미터에 아무 타입이나 오면 곤란한 경우가 있다.
 * 예를 들어 "숫자만" 받고 싶거나, "서로 비교 가능한 타입만" 받고 싶을 때다.
 * 이때 extends 키워드로 상한 경계(upper bound)를 지정한다.
 *
 *   <T extends 상위타입>
 *     → T 는 상위타입(또는 그 자손)만 될 수 있다.
 *     → 메소드 안에서 T 를 상위타입의 멤버처럼 사용할 수 있다.
 *
 * 대표 예 :
 *   <T extends Number>          → 숫자 타입만. intValue(), doubleValue() 등 사용 가능
 *   <T extends Comparable<T>>   → 자기 자신과 비교 가능한 타입만. compareTo() 사용 가능
 *
 * 주의 : 인터페이스를 경계로 지정할 때도 extends 를 쓴다(implements 아님).
 *
 * 이 소절에서는 두 대표 형태를 확인한다.
 */
public class BoundedType {

    /** <T extends Number> : 숫자 타입만 받아 double 합계를 구한다 */
    static <T extends Number> double sum(T[] numbers) {
        double total = 0.0;
        for (T n : numbers) {
            total += n.doubleValue();       // Number 의 메소드 사용 가능(경계 덕분)
        }
        return total;
    }

    /** <T extends Comparable<T>> : 비교 가능한 타입만 받아 최댓값을 구한다 */
    static <T extends Comparable<T>> T max(T[] arr) {
        T best = arr[0];
        for (T cur : arr) {
            if (cur.compareTo(best) > 0) {  // Comparable 의 compareTo 사용 가능
                best = cur;
            }
        }
        return best;
    }

    /** 사용자 정의 타입도 Comparable 을 구현하면 max 에 넣을 수 있다 */
    static class Student implements Comparable<Student> {
        final String name;
        final int score;
        Student(String name, int score) { this.name = name; this.score = score; }
        @Override public int compareTo(Student o) { return Integer.compare(this.score, o.score); }
        @Override public String toString() { return name + "(" + score + ")"; }
    }

    public static void main(String[] args) {

        System.out.println("=== 13.4 제한된 타입 파라미터 ===");

        // [1] <T extends Number> : 숫자 타입만 허용
        System.out.println("\n[1] <T extends Number>");
        Integer[] ints    = { 1, 2, 3, 4, 5 };
        Double[]  doubles = { 1.5, 2.5, 3.0 };
        System.out.println("정수 배열 합계 : " + sum(ints));
        System.out.println("실수 배열 합계 : " + sum(doubles));
        System.out.println("→ String[] 은 Number 가 아니므로 sum(...) 에 넣으면 컴파일 오류.");

        // [2] <T extends Comparable<T>> : 비교 가능한 타입만 허용
        System.out.println("\n[2] <T extends Comparable<T>>");
        Integer[] scores = { 88, 95, 70, 100, 63 };
        String[]  words  = { "banana", "apple", "cherry" };
        System.out.println("정수 최댓값   : " + max(scores));
        System.out.println("문자열 최댓값 : " + max(words) + " (사전순 가장 뒤)");

        // [3] 사용자 정의 타입 + Comparable
        System.out.println("\n[3] 사용자 정의 타입에 경계 적용");
        Student[] students = {
                new Student("김철수", 82),
                new Student("이영희", 91),
                new Student("박민수", 77)
        };
        System.out.println("최고 점수 학생: " + max(students));

        System.out.println("\n프로그램 정상 종료");
    }
}
