package com.edu.basics;

/**
 * Chapter 01 (CS 심화) - Java는 언제나 "값에 의한 전달(pass-by-value)"이다
 *
 * 가장 흔한 오해: "Java는 객체를 참조로 전달한다(pass-by-reference)".
 * → 틀렸습니다. Java는 예외 없이 "값에 의한 전달"만 합니다.
 *   다만 객체의 경우 전달되는 "값"이 곧 "참조(주소)의 복사본"일 뿐입니다.
 *
 * 정확한 규칙:
 *   - 메서드 호출 시 인자는 항상 "복사"되어 전달된다.
 *   - 기본형이면 값 자체를 복사한다.
 *   - 참조형이면 "참조(주소)"를 복사한다. (객체를 복사하는 게 아님!)
 *   - 그래서: 매개변수 재대입은 호출자에게 안 보이지만,
 *            복사된 참조로 "가리키는 객체"를 수정하면 호출자에게 보인다.
 */
public class PassByValue {

    public static void main(String[] args) {
        System.out.println("====================================");
        System.out.println(" Chapter 01 심화: Java는 항상 pass-by-value");
        System.out.println("====================================\n");

        primitiveIsCopied();
        reassignReferenceDoesNothing();
        mutatingObjectWorks();
        swapFails();
        conclusion();
    }

    // ──────────────────────────────────────────────
    // 1. 기본형은 값이 복사된다
    // ──────────────────────────────────────────────
    static void primitiveIsCopied() {
        System.out.println("── 1. 기본형: 값 자체가 복사됨 ──");

        int x = 10;
        System.out.println("  호출 전 x = " + x);
        addOneToParam(x);   // x의 "값 10"이 복사되어 전달됨
        System.out.println("  호출 후 x = " + x + "  → 그대로 10 (복사본만 바뀜)");
        System.out.println();
    }

    static void addOneToParam(int n) {
        n = n + 1;   // 이 n은 호출자의 x와 무관한 복사본
    }

    // ──────────────────────────────────────────────
    // 2. 매개변수(참조)를 재대입해도 호출자에겐 영향 없음
    // ──────────────────────────────────────────────
    static void reassignReferenceDoesNothing() {
        System.out.println("── 2. 참조 매개변수 재대입은 호출자에 영향 없음 ──");

        StringBuilder caller = new StringBuilder("원본");
        System.out.println("  호출 전 : " + caller);
        reassign(caller);   // "참조의 복사본"이 전달됨
        System.out.println("  호출 후 : " + caller + "  → 여전히 '원본'");
        System.out.println("  이유: 메서드 안에서 새 객체를 가리켜도, 그건 복사된 참조변수만 바뀐 것");
        System.out.println();
    }

    static void reassign(StringBuilder sb) {
        sb = new StringBuilder("바뀐객체");  // 복사된 참조가 다른 객체를 가리킬 뿐
        // 호출자의 참조는 여전히 원래 객체를 가리킴
    }

    // ──────────────────────────────────────────────
    // 3. 참조가 가리키는 "객체"를 수정하면 반영됨
    // ──────────────────────────────────────────────
    static void mutatingObjectWorks() {
        System.out.println("── 3. 가리키는 객체를 직접 수정하면 반영됨 ──");

        StringBuilder caller = new StringBuilder("원본");
        System.out.println("  호출 전 : " + caller);
        mutate(caller);   // 복사된 참조지만 "같은 객체"를 가리킴
        System.out.println("  호출 후 : " + caller + "  → '원본추가됨' (같은 객체를 수정!)");
        System.out.println("  핵심: 참조는 복사됐지만 두 참조가 '동일한 힙 객체'를 가리킴");
        System.out.println();
    }

    static void mutate(StringBuilder sb) {
        sb.append("추가됨");   // 참조가 가리키는 바로 그 객체를 변경 → 호출자에게 보임
    }

    // ──────────────────────────────────────────────
    // 4. 메서드 안에서 두 참조 교환(swap)은 실패한다
    // ──────────────────────────────────────────────
    static void swapFails() {
        System.out.println("── 4. swap이 실패하는 이유 ──");

        // 만약 Java가 진짜 pass-by-reference라면 아래 swap이 성공해야 합니다.
        // 하지만 전달된 것은 참조의 "복사본"이라, 복사본끼리 교환해도 원본은 그대로입니다.
        String[] a = {"A"};   // 배열로 감싸 참조를 관찰
        String[] b = {"B"};
        System.out.println("  호출 전 : a=" + a[0] + ", b=" + b[0]);
        swap(a[0], b[0]);
        System.out.println("  호출 후 : a=" + a[0] + ", b=" + b[0] + "  → 교환 실패!");
        System.out.println("  swap 내부의 지역 변수만 서로 바뀌었을 뿐, 호출자 참조는 불변");
        System.out.println("  (진짜 pass-by-reference였다면 A/B가 교환됐어야 함 → Java는 아님을 증명)");
        System.out.println();
    }

    static void swap(String x, String y) {
        String tmp = x;   // 복사된 참조끼리 교환 → 메서드 밖에는 아무 영향 없음
        x = y;
        y = tmp;
    }

    // ──────────────────────────────────────────────
    // 5. 결론
    // ──────────────────────────────────────────────
    static void conclusion() {
        System.out.println("── 5. 정확한 규칙 정리 ──");
        System.out.println("  1) Java는 예외 없이 pass-by-value (값에 의한 전달)");
        System.out.println("  2) 기본형은 '값'을, 참조형은 '참조(주소)'를 복사해 전달");
        System.out.println("  3) 매개변수 재대입 → 호출자에게 안 보임 (복사본만 바뀜)");
        System.out.println("  4) 가리키는 객체 상태 변경 → 호출자에게 보임 (같은 객체)");
        System.out.println("  5) 그래서 swap은 불가능 → 'Java는 pass-by-reference'는 잘못된 표현");
        System.out.println();
    }
}
