package com.edu.collections;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

/**
 * Chapter 03 - Comparable과 Comparator 예제
 *
 * 객체를 정렬하는 두 가지 방법을 학습합니다.
 *  - Comparable : 클래스 "자신"의 기본(자연) 정렬 순서를 정의 (compareTo)
 *  - Comparator : 정렬 기준을 "외부"에서 다양하게 정의 (comparing/thenComparing)
 */
public class ComparableComparatorExample {

    // ======================================================
    // Comparable을 구현한 학생 클래스
    // ======================================================

    /**
     * Comparable<Student>를 구현하여 "자연 정렬 순서"를 점수 기준으로 정의합니다.
     * compareTo가 음수면 앞, 0이면 같음, 양수면 뒤로 정렬됩니다.
     */
    static class Student implements Comparable<Student> {
        String name;
        int score;
        int age;

        Student(String name, int score, int age) {
            this.name = name;
            this.score = score;
            this.age = age;
        }

        public String getName() { return name; }
        public int getScore() { return score; }
        public int getAge() { return age; }

        // 자연 정렬: 점수 오름차순
        // Integer.compare(a, b)를 쓰면 오버플로우 걱정 없이 안전하게 비교할 수 있습니다.
        @Override
        public int compareTo(Student other) {
            return Integer.compare(this.score, other.score);
        }

        @Override
        public String toString() {
            return name + "(" + score + "점, " + age + "세)";
        }
    }

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("  Chapter 03 - Comparable & Comparator");
        System.out.println("========================================\n");

        demonstrateComparable();
        demonstrateComparator();
        demonstrateTreeSetNaturalOrdering();

        System.out.println("========================================");
        System.out.println("  Comparable & Comparator 예제 완료!");
        System.out.println("========================================");
    }

    // ======================================================
    // 1. Comparable - 자연 정렬 순서
    // ======================================================
    static void demonstrateComparable() {
        System.out.println("--- 1. Comparable (자연 정렬: 점수 오름차순) ---");

        List<Student> students = new ArrayList<>(List.of(
                new Student("김철수", 85, 20),
                new Student("이영희", 92, 22),
                new Student("박민수", 78, 21),
                new Student("정수진", 92, 19)
        ));

        System.out.println("  정렬 전: " + students);

        // Comparable을 구현했으므로 인자 없는 sort()로 자연 순서 정렬이 가능합니다.
        students.sort(null);  // 또는 Collections.sort(students)
        System.out.println("  정렬 후 (점수 오름차순): " + students);
        System.out.println();
    }

    // ======================================================
    // 2. Comparator - 다양한 정렬 기준
    // ======================================================
    static void demonstrateComparator() {
        System.out.println("--- 2. Comparator (comparing / thenComparing / reversed) ---");

        List<Student> students = new ArrayList<>(List.of(
                new Student("김철수", 85, 20),
                new Student("이영희", 92, 22),
                new Student("박민수", 78, 21),
                new Student("정수진", 92, 19),
                new Student("한지민", 85, 23)
        ));

        // comparing: 점수 기준 정렬
        students.sort(Comparator.comparing(Student::getScore));
        System.out.println("  점수 오름차순: " + students);

        // reversed: 점수 내림차순
        students.sort(Comparator.comparing(Student::getScore).reversed());
        System.out.println("  점수 내림차순: " + students);

        // thenComparing: 다중 키 정렬
        // 1차로 점수 내림차순, 점수가 같으면 2차로 나이 오름차순
        students.sort(
                Comparator.comparing(Student::getScore).reversed()
                        .thenComparing(Student::getAge)
        );
        System.out.println("  점수↓, 같으면 나이↑: " + students);

        // 이름(문자열) 기준 정렬
        students.sort(Comparator.comparing(Student::getName));
        System.out.println("  이름 가나다순: " + students);
        System.out.println();
    }

    // ======================================================
    // 3. TreeSet - 자연 정렬을 이용한 자동 정렬 저장
    // ======================================================
    static void demonstrateTreeSetNaturalOrdering() {
        System.out.println("--- 3. TreeSet (자연 정렬 자동 적용) ---");

        // TreeSet은 요소를 추가할 때 Comparable의 compareTo를 사용해 자동 정렬합니다.
        // 별도 정렬 호출 없이도 항상 정렬된 상태를 유지합니다.
        TreeSet<Student> set = new TreeSet<>();
        set.add(new Student("김철수", 85, 20));
        set.add(new Student("이영희", 92, 22));
        set.add(new Student("박민수", 78, 21));

        System.out.println("  TreeSet (점수순 자동 정렬): " + set);
        System.out.println("  first() 가장 낮은 점수: " + set.first());
        System.out.println("  last()  가장 높은 점수: " + set.last());

        // 생성자에 Comparator를 넘기면 자연 순서 대신 다른 기준으로 정렬할 수도 있습니다.
        TreeSet<Student> byName = new TreeSet<>(Comparator.comparing(Student::getName));
        byName.add(new Student("김철수", 85, 20));
        byName.add(new Student("이영희", 92, 22));
        byName.add(new Student("박민수", 78, 21));
        System.out.println("  TreeSet (이름순 - Comparator 지정): " + byName);

        System.out.println("\n  ⚠ 주의: TreeSet은 compareTo 결과가 0이면 '같은 요소'로 보고 중복 저장하지 않습니다.");
        System.out.println();
    }
}
