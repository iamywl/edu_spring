package com.edu.javabook.ch05;

import java.util.Arrays;

/**
 * 5.7 다차원 배열
 *
 * 다차원 배열은 "배열의 배열"이다. 자바는 진짜 2차원 격자가 아니라,
 * 각 행이 다시 배열을 가리키는 구조로 되어 있다.
 *
 * [2차원 배열]  int[][] m = new int[2][3];   // 2행 3열
 * [가변 배열]   행마다 길이가 다를 수 있다 (ragged array).
 *              int[][] r = new int[2][];   // 열은 나중에 각각 생성
 */
public class MultiDimArray {

    public static void main(String[] args) {

        System.out.println("=== 5.7 다차원 배열 ===");

        // [1] 2차원 배열 생성 (2행 3열, 기본값 0)
        System.out.println("\n[1] 2차원 배열 (2행 3열)");
        int[][] matrix = new int[2][3];
        System.out.println("생성 직후 : " + Arrays.deepToString(matrix));

        // [2] 값 대입 후 순회
        System.out.println("\n[2] 값 대입 후 순회");
        int value = 1;
        for (int r = 0; r < matrix.length; r++) {          // 행
            for (int c = 0; c < matrix[r].length; c++) {   // 열
                matrix[r][c] = value++;
            }
        }
        for (int r = 0; r < matrix.length; r++) {
            System.out.println("행 " + r + " : " + Arrays.toString(matrix[r]));
        }

        // [3] 초기화 리터럴로 바로 생성
        System.out.println("\n[3] 초기화 리터럴");
        int[][] grid = { { 1, 2 }, { 3, 4 }, { 5, 6 } };
        System.out.println("grid : " + Arrays.deepToString(grid));

        // [4] 가변(ragged) 배열 : 행마다 길이가 다름
        System.out.println("\n[4] 가변 배열 (행마다 길이 다름)");
        int[][] ragged = new int[3][];
        ragged[0] = new int[] { 1 };
        ragged[1] = new int[] { 1, 2 };
        ragged[2] = new int[] { 1, 2, 3 };
        for (int r = 0; r < ragged.length; r++) {
            System.out.println("행 " + r + " (길이 " + ragged[r].length + ") : "
                    + Arrays.toString(ragged[r]));
        }

        System.out.println("\n프로그램 정상 종료");
    }
}
