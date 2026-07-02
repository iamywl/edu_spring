package com.edu.javabook.ch05;

import java.util.Arrays;

/**
 * 5.9 배열 복사
 *
 * 배열은 크기를 바꿀 수 없으므로, 더 큰 배열이 필요하면 새 배열로 복사한다.
 *
 * [System.arraycopy]  원본, 시작위치, 대상, 시작위치, 개수 를 지정해 복사.
 * [Arrays.copyOf]     원하는 길이로 새 배열을 만들며 복사(길면 기본값 채움).
 * [얕은 복사]         참조형 배열을 복사하면 "번지"만 복사된다.
 *                     → 복사본과 원본이 같은 요소 객체를 공유한다.
 */
public class ArrayCopy {

    static class Box {
        int value;
        Box(int value) { this.value = value; }
        @Override public String toString() { return "Box(" + value + ")"; }
    }

    public static void main(String[] args) {

        System.out.println("=== 5.9 배열 복사 ===");

        // [1] System.arraycopy 로 일부 복사
        System.out.println("\n[1] System.arraycopy");
        int[] src = { 1, 2, 3, 4, 5 };
        int[] dest = new int[5];
        System.arraycopy(src, 0, dest, 0, src.length);
        System.out.println("원본 : " + Arrays.toString(src));
        System.out.println("복사 : " + Arrays.toString(dest));

        // [2] Arrays.copyOf 로 길이를 늘려 복사 (남는 칸은 기본값)
        System.out.println("\n[2] Arrays.copyOf (길이 확장)");
        int[] bigger = Arrays.copyOf(src, 7);
        System.out.println("copyOf(src, 7) : " + Arrays.toString(bigger)
                + "  (뒤 2칸은 기본값 0)");

        // [3] 얕은 복사 : 참조형은 번지만 복사 → 요소 객체 공유
        System.out.println("\n[3] 얕은 복사 (요소 객체 공유)");
        Box[] boxes = { new Box(10), new Box(20) };
        Box[] copy = Arrays.copyOf(boxes, boxes.length);
        copy[0].value = 999;   // copy를 통해 바꿔도
        System.out.println("boxes[0] = " + boxes[0]
                + "  (원본도 바뀜 → 같은 객체를 가리킴)");
        System.out.println("boxes == copy : " + (boxes == copy)
                + "  (배열 자체는 다른 객체)");

        System.out.println("\n프로그램 정상 종료");
    }
}
