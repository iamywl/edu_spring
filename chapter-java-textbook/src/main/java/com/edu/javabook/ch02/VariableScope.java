package com.edu.javabook.ch02;

/**
 * 2.11 변수 사용 범위 (스코프, scope)
 *
 * 변수는 자신이 선언된 '중괄호 블록 { }' 안에서만 유효하다.
 * 블록을 벗어나면 그 변수는 사라져(더 이상 접근 불가) 컴파일 에러가 난다.
 * 이 규칙 덕분에 이름이 같아도 서로 다른 블록에서는 충돌하지 않는다.
 */
public class VariableScope {

    // [클래스 영역] main 밖의 static 변수는 메서드 전체에서 사용 가능
    static int classLevel = 999;

    public static void main(String[] args) {

        System.out.println("=== 2.11 변수 사용 범위 ===");

        // [1] 메서드 지역 변수: main 블록 전체에서 유효
        System.out.println("\n[1] 지역 변수 (main 블록)");
        int local = 10;
        System.out.println("local = " + local);
        System.out.println("클래스 변수 classLevel = " + classLevel);

        // [2] 블록 안에서 선언한 변수는 그 블록 밖에서 못 쓴다
        System.out.println("\n[2] 블록 스코프");
        {
            int inner = 20;   // 이 중괄호 블록 안에서만 유효
            System.out.println("블록 안에서 inner = " + inner);
        }
        // System.out.println(inner);  // ← 컴파일 에러: inner 는 위 블록에서만 존재
        System.out.println("블록을 벗어나면 inner 는 접근 불가 (주석 참고)");

        // [3] for 문의 변수도 for 블록 안에서만 유효
        System.out.println("\n[3] for 루프 변수 스코프");
        for (int j = 0; j < 3; j++) {
            System.out.println("루프 안 j = " + j);
        }
        // System.out.println(j);  // ← 컴파일 에러: j 는 for 안에서만 존재
        System.out.println("for 종료 후 j 는 사라짐 (주석 참고)");

        // [4] 안쪽 블록에서는 바깥 변수를 볼 수 있다 (반대는 불가)
        System.out.println("\n[4] 바깥 → 안쪽은 접근 가능");
        int outer = 100;
        if (outer > 0) {
            int deep = outer + 1;   // 바깥의 outer 사용 가능
            System.out.println("안쪽 블록에서 outer 사용: " + deep);
        }

        // [5] 지역 변수는 반드시 초기화 후 사용해야 한다 (기본값 없음)
        System.out.println("\n[5] 지역 변수는 초기화 필수");
        int mustInit = 0;   // 초기화하지 않고 쓰면 컴파일 에러
        System.out.println("초기화한 지역 변수 = " + mustInit);

        // [왜?] 스코프를 좁게 유지하면 변수의 생명주기가 명확해져 버그가 줄고 가독성이 오른다.
        System.out.println("\n[왜?] 변수는 필요한 블록에서만 살게 하면 실수·이름 충돌이 줄어든다.");

        System.out.println("\n프로그램 정상 종료");
    }
}
