package com.edu.basics;

import java.util.Arrays;

/**
 * Chapter 01 - 배열과 메서드
 *
 * 1차원/2차원 배열, 가변인자(varargs), 메서드 오버로딩,
 * 재귀(recursion)를 다룹니다.
 */
public class ArraysAndMethods {

    public static void main(String[] args) {
        System.out.println("====================================");
        System.out.println(" Chapter 01: 배열과 메서드");
        System.out.println("====================================\n");

        oneDimensionalArrays();
        twoDimensionalArrays();
        varargsDemo();
        overloadingDemo();
        recursionDemo();
    }

    // ══════════════════════════════════════════════
    //  배열 (Arrays)
    // ══════════════════════════════════════════════

    // ──────────────────────────────────────────────
    // 1. 1차원 배열
    // ──────────────────────────────────────────────
    static void oneDimensionalArrays() {
        System.out.println("── 1. 1차원 배열 ──");

        // 배열 선언과 초기화 방법들
        int[] numbers1 = {1, 2, 3, 4, 5};                // 리터럴로 초기화
        int[] numbers2 = new int[5];                       // 크기 지정 (기본값 0)
        int[] numbers3 = new int[]{10, 20, 30, 40, 50};   // new 키워드 + 초기값

        // 배열 접근과 수정
        numbers2[0] = 100;
        numbers2[1] = 200;
        System.out.println("numbers1: " + Arrays.toString(numbers1));
        System.out.println("numbers2: " + Arrays.toString(numbers2));
        System.out.println("numbers3: " + Arrays.toString(numbers3));

        // 배열 길이
        System.out.println("numbers1 길이: " + numbers1.length);

        // 배열 순회
        System.out.print("for-each 순회: ");
        for (int num : numbers1) {
            System.out.print(num + " ");
        }
        System.out.println();

        // 배열 복사
        int[] copied = Arrays.copyOf(numbers1, numbers1.length);
        int[] partial = Arrays.copyOfRange(numbers1, 1, 4); // 인덱스 1~3
        System.out.println("전체 복사: " + Arrays.toString(copied));
        System.out.println("부분 복사 [1,4): " + Arrays.toString(partial));

        // 배열 정렬
        int[] unsorted = {5, 2, 8, 1, 9, 3};
        System.out.println("정렬 전: " + Arrays.toString(unsorted));
        Arrays.sort(unsorted);
        System.out.println("정렬 후: " + Arrays.toString(unsorted));

        // 배열 검색 (정렬된 배열에서 이진 탐색)
        int index = Arrays.binarySearch(unsorted, 5);
        System.out.println("값 5의 인덱스: " + index);

        // 배열 채우기
        int[] filled = new int[5];
        Arrays.fill(filled, 7);
        System.out.println("Arrays.fill(7): " + Arrays.toString(filled));

        // 문자열 배열
        String[] names = {"홍길동", "김철수", "이영희"};
        System.out.println("문자열 배열: " + Arrays.toString(names));
        System.out.println();
    }

    // ──────────────────────────────────────────────
    // 2. 2차원 배열
    // ──────────────────────────────────────────────
    static void twoDimensionalArrays() {
        System.out.println("── 2. 2차원 배열 ──");

        // 2차원 배열 선언과 초기화
        int[][] matrix = {
            {1, 2, 3},
            {4, 5, 6},
            {7, 8, 9}
        };

        // 2차원 배열 출력
        System.out.println("3x3 행렬:");
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                System.out.printf("%3d", matrix[i][j]);
            }
            System.out.println();
        }

        // 향상된 for문으로 순회
        System.out.println("\nfor-each로 순회:");
        for (int[] row : matrix) {
            System.out.println("  " + Arrays.toString(row));
        }

        // 가변 길이 2차원 배열 (비정방 배열)
        // Java의 2차원 배열은 실제로 "배열의 배열"이므로 각 행의 길이가 다를 수 있음
        int[][] jagged = new int[3][];
        jagged[0] = new int[]{1};
        jagged[1] = new int[]{2, 3};
        jagged[2] = new int[]{4, 5, 6};

        System.out.println("\n가변 길이 2차원 배열 (비정방):");
        for (int[] row : jagged) {
            System.out.println("  " + Arrays.toString(row));
        }

        // Arrays.deepToString - 다차원 배열 출력
        System.out.println("\nArrays.deepToString: " + Arrays.deepToString(matrix));

        // 2차원 배열 활용 - 학생 성적표
        String[] subjects = {"국어", "영어", "수학"};
        int[][] scores = {
            {90, 85, 95},  // 학생 1
            {80, 90, 70},  // 학생 2
            {95, 88, 92}   // 학생 3
        };

        System.out.println("\n학생 성적표:");
        System.out.printf("%-6s", "");
        for (String subject : subjects) {
            System.out.printf("%-6s", subject);
        }
        System.out.println("평균");

        for (int i = 0; i < scores.length; i++) {
            System.out.printf("학생%d ", i + 1);
            int sum = 0;
            for (int j = 0; j < scores[i].length; j++) {
                System.out.printf("%-6d", scores[i][j]);
                sum += scores[i][j];
            }
            System.out.printf("%.1f%n", (double) sum / scores[i].length);
        }
        System.out.println();
    }

    // ══════════════════════════════════════════════
    //  메서드 (Methods)
    // ══════════════════════════════════════════════

    // ──────────────────────────────────────────────
    // 3. 가변인자 (Varargs)
    // ──────────────────────────────────────────────
    static void varargsDemo() {
        System.out.println("── 3. 가변인자 (Varargs) ──");

        // 가변인자: 메서드 호출 시 인자 개수를 자유롭게 전달
        // 내부적으로 배열로 처리됨
        System.out.println("sum()        = " + sum());              // 인자 0개
        System.out.println("sum(1)       = " + sum(1));             // 인자 1개
        System.out.println("sum(1,2,3)   = " + sum(1, 2, 3));      // 인자 3개
        System.out.println("sum(1~5)     = " + sum(1, 2, 3, 4, 5)); // 인자 5개

        // 배열을 직접 전달할 수도 있음
        int[] nums = {10, 20, 30};
        System.out.println("sum(배열)    = " + sum(nums));

        // 가변인자와 일반 매개변수 혼합
        // 가변인자는 반드시 마지막 매개변수여야 함
        System.out.println("joinWithSep: " + joinWithSeparator(", ", "사과", "바나나", "포도"));
        System.out.println("joinWithSep: " + joinWithSeparator(" | ", "Java", "Python", "Go", "Rust"));
        System.out.println();
    }

    // 가변인자 메서드: int 값들의 합을 구함
    static int sum(int... numbers) {
        int total = 0;
        for (int n : numbers) {
            total += n;
        }
        return total;
    }

    // 일반 매개변수 + 가변인자 혼합
    // separator로 문자열들을 연결
    static String joinWithSeparator(String separator, String... items) {
        return String.join(separator, items);
    }

    // ──────────────────────────────────────────────
    // 4. 메서드 오버로딩 (Overloading)
    // ──────────────────────────────────────────────
    static void overloadingDemo() {
        System.out.println("── 4. 메서드 오버로딩 (Overloading) ──");

        // 같은 이름, 다른 매개변수 타입/개수로 여러 메서드 정의
        // 컴파일러가 호출 시 매개변수를 보고 적절한 메서드를 선택
        System.out.println("add(3, 5)         = " + add(3, 5));           // int + int
        System.out.println("add(3.0, 5.0)     = " + add(3.0, 5.0));     // double + double
        System.out.println("add(3, 5, 7)      = " + add(3, 5, 7));       // int + int + int
        System.out.println("add(\"Hello\", \" World\") = " + add("Hello", " World")); // String + String

        // 출력 관련 오버로딩
        printInfo("홍길동");                    // 이름만
        printInfo("홍길동", 25);               // 이름 + 나이
        printInfo("홍길동", 25, "서울");       // 이름 + 나이 + 도시
        System.out.println();
    }

    // 오버로딩된 add 메서드들
    static int add(int a, int b) {
        return a + b;
    }

    static double add(double a, double b) {
        return a + b;
    }

    static int add(int a, int b, int c) {
        return a + b + c;
    }

    static String add(String a, String b) {
        return a + b;
    }

    // 오버로딩된 printInfo 메서드들
    static void printInfo(String name) {
        System.out.println("  이름: " + name);
    }

    static void printInfo(String name, int age) {
        System.out.println("  이름: " + name + ", 나이: " + age);
    }

    static void printInfo(String name, int age, String city) {
        System.out.println("  이름: " + name + ", 나이: " + age + ", 도시: " + city);
    }

    // ──────────────────────────────────────────────
    // 5. 재귀 (Recursion)
    // ──────────────────────────────────────────────
    static void recursionDemo() {
        System.out.println("── 5. 재귀 (Recursion) ──");

        // 팩토리얼 (n! = n * (n-1) * ... * 1)
        System.out.println("5! = " + factorial(5));         // 120
        System.out.println("10! = " + factorial(10));       // 3628800

        // 피보나치 수열 (0, 1, 1, 2, 3, 5, 8, 13, ...)
        System.out.print("피보나치 (0~9번째): ");
        for (int i = 0; i < 10; i++) {
            System.out.print(fibonacci(i) + " ");
        }
        System.out.println();

        // 거듭제곱 (x^n)
        System.out.println("2^10 = " + power(2, 10));      // 1024
        System.out.println("3^4  = " + power(3, 4));       // 81

        // 하노이의 탑
        System.out.println("\n하노이의 탑 (원반 3개):");
        hanoi(3, 'A', 'C', 'B');
        System.out.println();
    }

    /**
     * 팩토리얼 - 재귀의 대표적인 예제
     * 종료 조건: n <= 1일 때 1 반환
     * 재귀 단계: n * factorial(n-1)
     */
    static long factorial(int n) {
        if (n <= 1) return 1;       // 종료 조건 (base case)
        return n * factorial(n - 1); // 재귀 호출
    }

    /**
     * 피보나치 수열
     * F(0) = 0, F(1) = 1
     * F(n) = F(n-1) + F(n-2)
     *
     * 주의: 이 단순 재귀 방식은 O(2^n) 시간 복잡도로 비효율적.
     * 실무에서는 메모이제이션이나 반복문을 사용합니다.
     */
    static int fibonacci(int n) {
        if (n <= 0) return 0;       // 종료 조건 1
        if (n == 1) return 1;       // 종료 조건 2
        return fibonacci(n - 1) + fibonacci(n - 2); // 재귀 호출
    }

    /**
     * 거듭제곱 계산 (분할 정복)
     * x^n = (x^(n/2))^2       (n이 짝수일 때)
     * x^n = x * (x^(n/2))^2   (n이 홀수일 때)
     * 시간 복잡도: O(log n)
     */
    static long power(int base, int exponent) {
        if (exponent == 0) return 1;           // 종료 조건: x^0 = 1
        if (exponent == 1) return base;        // 종료 조건: x^1 = x

        long half = power(base, exponent / 2);
        if (exponent % 2 == 0) {
            return half * half;                // 짝수: (x^(n/2))^2
        } else {
            return base * half * half;         // 홀수: x * (x^(n/2))^2
        }
    }

    /**
     * 하노이의 탑
     * n개의 원반을 from 기둥에서 to 기둥으로 이동 (aux를 보조로 사용)
     * 규칙: 한 번에 하나씩, 큰 원반 위에 작은 원반만 올릴 수 있음
     */
    static void hanoi(int n, char from, char to, char aux) {
        if (n == 1) {
            System.out.println("  원반 1: " + from + " → " + to);
            return;
        }
        hanoi(n - 1, from, aux, to);    // n-1개를 보조 기둥으로 이동
        System.out.println("  원반 " + n + ": " + from + " → " + to); // 가장 큰 원반 이동
        hanoi(n - 1, aux, to, from);    // n-1개를 목적지 기둥으로 이동
    }
}
