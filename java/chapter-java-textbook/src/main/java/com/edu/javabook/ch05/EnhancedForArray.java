package com.edu.javabook.ch05;

/**
 * 5.10 향상된 for 문 (enhanced for / for-each)
 *
 * 배열이나 컬렉션의 요소를 처음부터 끝까지 순서대로 꺼낼 때 쓰는 간결한 반복문.
 *
 *   for (타입 변수 : 배열) { ... }
 *
 * - 인덱스 변수, 조건, 증감식이 없어 코드가 짧고 실수가 적다.
 * - 순회 중 요소를 "읽기"에 적합하다.
 * - 반복 변수는 요소의 '복사본'이라, 기본형 배열은 반복 변수를 바꿔도 원본은 안 바뀐다.
 */
public class EnhancedForArray {

    public static void main(String[] args) {

        System.out.println("=== 5.10 향상된 for 문 ===");

        // [1] 기본형 배열 순회
        System.out.println("\n[1] 기본형 배열 순회");
        int[] scores = { 90, 85, 100 };
        int sum = 0;
        for (int s : scores) {   // scores의 각 요소가 순서대로 s에 담김
            System.out.println("점수 = " + s);
            sum += s;
        }
        System.out.println("합계 = " + sum);

        // [2] 문자열 배열 순회
        System.out.println("\n[2] 문자열 배열 순회");
        String[] names = { "홍길동", "김철수", "이영희" };
        for (String name : names) {
            System.out.println("이름 = " + name);
        }

        // [3] 반복 변수는 복사본 → 원본 배열은 바뀌지 않는다.
        System.out.println("\n[3] 반복 변수 수정은 원본에 영향 없음");
        for (int s : scores) {
            s = 0;   // 반복 변수만 0이 됨 (배열 요소는 그대로)
        }
        System.out.print("순회 후 원본 : ");
        for (int s : scores) {
            System.out.print(s + " ");
        }
        System.out.println("(변하지 않음)");

        // [4] 2차원 배열도 향상된 for 로 순회 가능
        System.out.println("\n[4] 2차원 배열 순회");
        int[][] grid = { { 1, 2 }, { 3, 4 } };
        for (int[] row : grid) {        // 바깥: 각 행(배열)
            for (int cell : row) {      // 안쪽: 행의 각 값
                System.out.print(cell + " ");
            }
        }
        System.out.println();

        System.out.println("\n프로그램 정상 종료");
    }
}
