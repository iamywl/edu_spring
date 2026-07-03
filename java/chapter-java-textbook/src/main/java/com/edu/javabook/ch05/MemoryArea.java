package com.edu.javabook.ch05;

/**
 * 5.2 메모리 사용 영역
 *
 * JVM은 프로그램 실행 시 메모리를 크게 세 영역으로 나눠 사용한다.
 *
 * [메서드 영역(Method Area)]
 *   - 클래스 정보, static 필드/상수, 메서드 코드가 저장된다.
 *   - 프로그램 전체가 공유한다.
 *
 * [스택 영역(Stack)]
 *   - 메서드가 호출될 때마다 프레임이 쌓인다.
 *   - 지역 변수, 매개변수가 저장된다. (기본형은 값, 참조형은 '번지'가 여기 저장)
 *   - 메서드가 끝나면 프레임이 사라진다.
 *
 * [힙 영역(Heap)]
 *   - new 로 생성된 객체(배열, 인스턴스)가 저장된다.
 *   - 참조가 없어지면 가비지 컬렉터가 정리한다.
 */
public class MemoryArea {

    // 메서드 영역에 저장되는 static 상수
    static final String APP_NAME = "MemoryDemo";

    public static void main(String[] args) {

        System.out.println("=== 5.2 메모리 사용 영역 ===");

        // [1] 메서드 영역 : static/상수는 프로그램 전체가 공유
        System.out.println("\n[1] 메서드 영역 (static/상수 저장)");
        System.out.println("APP_NAME(static) = " + APP_NAME);

        // [2] 스택 영역 : 지역 변수(기본형 값, 참조형 번지)가 저장
        System.out.println("\n[2] 스택 영역 (지역 변수 저장)");
        int localValue = 100;               // 값 100이 스택에 저장
        int[] localRef = { 1, 2, 3 };       // 배열 '번지'가 스택에 저장 (실체는 힙)
        System.out.println("localValue = " + localValue + "  → 값이 스택에 저장");
        System.out.println("localRef 변수는 힙의 배열 번지를 스택에서 가리킴");

        // [3] 힙 영역 : new 로 만든 객체가 저장
        System.out.println("\n[3] 힙 영역 (객체 저장)");
        int[] heapArray = new int[3];       // 배열 객체는 힙에 생성됨
        heapArray[0] = 7;
        System.out.println("heapArray는 힙에 생성, heapArray[0] = " + heapArray[0]);

        // [4] 간단 시연 : 메서드로 번지를 넘기면 같은 힙 객체를 공유
        System.out.println("\n[4] 시연 : 메서드에 참조를 넘기면 힙 객체를 공유");
        System.out.println("변경 전 heapArray[0] = " + heapArray[0]);
        modify(heapArray);   // 스택엔 번지가 복사되지만, 가리키는 힙 객체는 동일
        System.out.println("변경 후 heapArray[0] = " + heapArray[0] + " (메서드 안에서 바꾼 값 반영됨)");

        System.out.println("\n프로그램 정상 종료");
    }

    // 매개변수 arr에는 힙 배열의 '번지'가 복사되어 들어온다 → 같은 힙 객체를 수정
    static void modify(int[] arr) {
        arr[0] = 42;
    }
}
