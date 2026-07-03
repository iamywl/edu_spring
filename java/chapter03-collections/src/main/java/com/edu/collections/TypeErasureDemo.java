package com.edu.collections;

import java.util.ArrayList;
import java.util.List;

/**
 * Chapter 03 (CS 심화) - 제네릭 타입 소거(Type Erasure)
 *
 * Java 제네릭의 핵심 진실: 제네릭 타입 정보는 "컴파일 타임에만" 존재하고,
 * 컴파일이 끝나면 지워집니다(erasure). 런타임의 JVM은 List<String>과
 * List<Integer>를 구분하지 못하고 둘 다 그냥 'List'로 봅니다.
 *
 * 왜 이렇게 만들었나?
 *   - 하위 호환성. 제네릭은 Java 5에서 추가됐는데, 그 이전 코드(raw type)와
 *     같은 바이트코드/클래스 파일에서 함께 동작해야 했습니다.
 *   - 그래서 컴파일러가 타입 검사만 해주고, 실제 바이트코드에는 타입 파라미터를
 *     남기지 않습니다(대신 필요한 곳에 캐스팅을 자동 삽입).
 *
 * 이 데모가 보여주는 것:
 *   - getClass()가 같다(타입이 지워짐)
 *   - new T[], T.class, instanceof List<String> 이 불가능한 이유
 *   - raw type으로 잘못된 원소를 몰래 넣으면 ClassCastException이
 *     "꺼내 쓰는 순간" 엉뚱한 곳에서 터진다
 */
public class TypeErasureDemo {

    public static void main(String[] args) {
        System.out.println("====================================");
        System.out.println(" Chapter 03 심화: 제네릭 타입 소거");
        System.out.println("====================================\n");

        sameRuntimeClass();
        whatYouCannotDo();
        rawTypePoisoning();
        whyErasureExists();
        conclusion();
    }

    // ──────────────────────────────────────────────
    // 1. List<String>과 List<Integer>는 런타임에 같은 클래스
    // ──────────────────────────────────────────────
    static void sameRuntimeClass() {
        System.out.println("── 1. 런타임 클래스는 동일하다 ──");

        List<String> strings = new ArrayList<>();
        List<Integer> integers = new ArrayList<>();

        // 컴파일 타임엔 서로 다른 타입이지만, 런타임엔 타입 인자가 지워져
        // 둘 다 그냥 java.util.ArrayList 입니다.
        Class<?> c1 = strings.getClass();
        Class<?> c2 = integers.getClass();

        System.out.println("  strings.getClass()  = " + c1.getName());
        System.out.println("  integers.getClass() = " + c2.getName());
        System.out.println("  두 클래스가 같은가?  = " + (c1 == c2) + "  ← true! 타입이 소거됨");
        System.out.println();
    }

    // ──────────────────────────────────────────────
    // 2. 소거 때문에 "할 수 없는" 것들
    // ──────────────────────────────────────────────
    static void whatYouCannotDo() {
        System.out.println("── 2. 타입 소거로 인해 불가능한 것들 ──");

        // (아래 코드들은 컴파일 에러가 나므로 주석으로 남기고 이유만 설명합니다.)

        // (a) new T[] 불가:
        //     런타임에 T가 뭔지 모르므로 배열을 만들 수 없습니다.
        //     배열은 런타임에 원소 타입을 알아야 하기 때문(공변성 검사).
        System.out.println("  (a) new T[] 불가 : 런타임에 T의 실제 타입을 몰라 배열 생성 불가");
        System.out.println("      → 우회: (T[]) new Object[n] + @SuppressWarnings, 또는 List<T> 사용");

        // (b) T.class 불가:
        //     T에 해당하는 Class 객체가 런타임에 존재하지 않습니다.
        System.out.println("  (b) T.class 불가 : 타입 리터럴이 런타임에 없음");
        System.out.println("      → 우회: 메서드에 Class<T> clazz 를 파라미터로 넘김");

        // (c) obj instanceof List<String> 불가:
        //     런타임엔 List까지만 알 수 있고 <String>은 지워져 확인할 수 없습니다.
        List<String> list = new ArrayList<>();
        boolean isList = list instanceof List<?>;   // 이건 OK (와일드카드)
        System.out.println("  (c) instanceof List<String> 불가 : <String>은 소거되어 검사 불가");
        System.out.println("      instanceof List<?> 는 가능 → 결과 = " + isList);
        System.out.println();
    }

    // ──────────────────────────────────────────────
    // 3. raw type으로 잘못된 원소 밀어넣기 → 나중에 터짐
    // ──────────────────────────────────────────────
    @SuppressWarnings({"unchecked", "rawtypes"})
    static void rawTypePoisoning() {
        System.out.println("── 3. raw type 오염과 ClassCastException이 터지는 위치 ──");

        List<String> strings = new ArrayList<>();
        strings.add("정상 문자열");

        // raw type(List)으로 취급하면 컴파일러의 타입 검사를 우회할 수 있습니다.
        // 컴파일러는 unchecked 경고만 내고 통과시킵니다.
        List raw = strings;         // 제네릭을 raw로 다운캐스트 (경고)
        raw.add(Integer.valueOf(42));   // String 리스트에 Integer를 몰래 삽입!

        System.out.println("  List<String>에 raw 캐스트로 Integer(42)를 삽입함");
        System.out.println("  삽입 시점엔 아무 예외도 안 남 (타입 검사가 지워졌으니까)");
        System.out.println("  리스트 내용(런타임엔 그냥 Object들) : " + strings);

        // 여기서 중요한 통찰: 예외는 "넣을 때"가 아니라
        // 컴파일러가 자동으로 넣어준 (String) 캐스트가 실행되는 "꺼낼 때" 터집니다.
        try {
            for (String s : strings) {   // 반복문에 숨은 (String) 캐스트가 42에서 실패
                System.out.println("    꺼낸 값: " + s);
            }
        } catch (ClassCastException e) {
            System.out.println("  → ClassCastException 발생! 메시지: " + e.getMessage());
            System.out.println("    핵심: 예외는 '삽입한 코드'가 아니라 '꺼내 쓰는 코드'에서 터진다.");
            System.out.println("    (컴파일러가 get 자리에 숨겨둔 (String) 캐스트가 42를 만나 실패)");
        }
        System.out.println();
    }

    // ──────────────────────────────────────────────
    // 4. 왜 소거를 택했나: 하위 호환성
    // ──────────────────────────────────────────────
    static void whyErasureExists() {
        System.out.println("── 4. 왜 타입 소거인가: 하위 호환성 ──");
        System.out.println("  제네릭은 Java 5(2004)에서 추가됨. 그 이전 코드는 raw type(List)만 씀.");
        System.out.println("  기존 클래스 파일/라이브러리와 섞여 돌아가야 했기에,");
        System.out.println("  런타임 표현을 바꾸지 않고 '컴파일 타임 검사 + 자동 캐스트 삽입'으로 구현.");
        System.out.println("  장점: 기존 코드와 100% 호환. 단점: 런타임에 타입 정보가 사라짐(위 제약들).");
        System.out.println("  (참고: C#은 'reified generics'라 런타임에도 타입을 유지 → 트레이드오프가 다름)");
        System.out.println();
    }

    static void conclusion() {
        System.out.println("── 정리 ──");
        System.out.println("  제네릭 타입은 컴파일 타임 도구다. 컴파일 후 타입 인자는 지워진다.");
        System.out.println("  런타임엔 List<String> == List<Integer> (같은 클래스).");
        System.out.println("  그래서 new T[], T.class, instanceof List<String> 이 불가능하다.");
        System.out.println("  raw type으로 오염시키면 ClassCastException이 '꺼내는 순간' 터진다.");
        System.out.println("  이유는 단 하나: 하위 호환성.");
        System.out.println();
    }
}
