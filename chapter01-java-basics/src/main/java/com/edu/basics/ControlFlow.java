package com.edu.basics;

/**
 * Chapter 01 - 제어문 (Control Flow)
 *
 * 조건문(if, switch), 반복문(for, while, do-while),
 * 분기문(break, continue, labeled loops)을 다룹니다.
 */
public class ControlFlow {

    public static void main(String[] args) {
        System.out.println("====================================");
        System.out.println(" Chapter 01: 제어문 (Control Flow)");
        System.out.println("====================================\n");

        ifElseDemo();
        switchDemo();
        switchExpressionDemo();
        forLoopDemo();
        whileLoopDemo();
        breakContinueDemo();
        labeledLoopDemo();
    }

    // ──────────────────────────────────────────────
    // 1. if / else if / else
    // ──────────────────────────────────────────────
    static void ifElseDemo() {
        System.out.println("── 1. if / else if / else ──");

        int score = 85;

        // 기본 if-else 구조
        if (score >= 90) {
            System.out.println(score + "점 → 등급: A (수)");
        } else if (score >= 80) {
            System.out.println(score + "점 → 등급: B (우)");
        } else if (score >= 70) {
            System.out.println(score + "점 → 등급: C (미)");
        } else if (score >= 60) {
            System.out.println(score + "점 → 등급: D (양)");
        } else {
            System.out.println(score + "점 → 등급: F (가)");
        }

        // 중첩 if문
        int age = 25;
        boolean hasLicense = true;

        if (age >= 18) {
            if (hasLicense) {
                System.out.println("운전 가능합니다.");
            } else {
                System.out.println("면허를 먼저 취득하세요.");
            }
        } else {
            System.out.println("미성년자는 운전할 수 없습니다.");
        }

        // 삼항 연산자로 간단한 조건 처리
        String result = (score >= 60) ? "합격" : "불합격";
        System.out.println(score + "점 → " + result);
        System.out.println();
    }

    // ──────────────────────────────────────────────
    // 2. switch 문 (기존 방식)
    // ──────────────────────────────────────────────
    static void switchDemo() {
        System.out.println("── 2. switch 문 (기존 방식) ──");

        int dayNumber = 3;
        String dayName;

        // 기존 switch 문 - break를 빠뜨리면 다음 case로 떨어짐 (fall-through)
        switch (dayNumber) {
            case 1:
                dayName = "월요일";
                break;
            case 2:
                dayName = "화요일";
                break;
            case 3:
                dayName = "수요일";
                break;
            case 4:
                dayName = "목요일";
                break;
            case 5:
                dayName = "금요일";
                break;
            case 6:
                dayName = "토요일";
                break;
            case 7:
                dayName = "일요일";
                break;
            default:
                dayName = "알 수 없음";
                break;
        }
        System.out.println("요일 번호 " + dayNumber + " → " + dayName);

        // fall-through 활용 예시
        int month = 8;
        String season;
        switch (month) {
            case 3: case 4: case 5:    // 여러 case를 묶을 수 있음
                season = "봄";
                break;
            case 6: case 7: case 8:
                season = "여름";
                break;
            case 9: case 10: case 11:
                season = "가을";
                break;
            case 12: case 1: case 2:
                season = "겨울";
                break;
            default:
                season = "알 수 없음";
                break;
        }
        System.out.println(month + "월 → " + season);
        System.out.println();
    }

    // ──────────────────────────────────────────────
    // 3. switch 표현식 (Java 14+)
    // ──────────────────────────────────────────────
    static void switchExpressionDemo() {
        System.out.println("── 3. switch 표현식 (Java 14+) ──");

        // 화살표(Arrow) 구문 - break 불필요, fall-through 없음
        int dayNumber = 5;
        String dayType = switch (dayNumber) {
            case 1, 2, 3, 4, 5 -> "평일";      // 여러 값을 쉼표로 구분
            case 6, 7           -> "주말";
            default             -> "잘못된 값";
        };
        System.out.println("요일 번호 " + dayNumber + " → " + dayType);

        // yield를 사용한 복잡한 로직
        String grade = "B+";
        double gradePoint = switch (grade) {
            case "A+", "A" -> 4.0;
            case "B+"      -> 3.5;
            case "B"       -> 3.0;
            case "C+"      -> 2.5;
            case "C"       -> 2.0;
            default -> {
                System.out.println("  (등급 '" + grade + "'에 대한 특별 처리)");
                yield 0.0;  // 블록 내에서는 yield로 값 반환
            }
        };
        System.out.println("등급 " + grade + " → 학점: " + gradePoint);

        // 문자열 switch
        String command = "start";
        switch (command) {
            case "start"  -> System.out.println("시스템을 시작합니다.");
            case "stop"   -> System.out.println("시스템을 종료합니다.");
            case "restart" -> System.out.println("시스템을 재시작합니다.");
            default       -> System.out.println("알 수 없는 명령: " + command);
        }
        System.out.println();
    }

    // ──────────────────────────────────────────────
    // 4. for 반복문
    // ──────────────────────────────────────────────
    static void forLoopDemo() {
        System.out.println("── 4. for 반복문 ──");

        // 기본 for 문
        System.out.print("기본 for: ");
        for (int i = 1; i <= 5; i++) {
            System.out.print(i + " ");
        }
        System.out.println();

        // 향상된 for 문 (enhanced for / for-each)
        // 배열이나 컬렉션을 순회할 때 사용
        String[] fruits = {"사과", "바나나", "포도", "딸기"};
        System.out.print("향상된 for: ");
        for (String fruit : fruits) {
            System.out.print(fruit + " ");
        }
        System.out.println();

        // 역순 for 문
        System.out.print("역순 for: ");
        for (int i = 5; i >= 1; i--) {
            System.out.print(i + " ");
        }
        System.out.println();

        // 중첩 for 문 - 구구단 (2단만)
        System.out.println("구구단 2단:");
        for (int j = 1; j <= 9; j++) {
            System.out.printf("  2 x %d = %2d%n", j, 2 * j);
        }

        // 무한 for 문 (조건부 탈출)
        System.out.print("무한 for + break: ");
        int count = 0;
        for (;;) {  // for(;;)는 무한 루프
            if (count >= 3) break;
            System.out.print(count + " ");
            count++;
        }
        System.out.println("\n");
    }

    // ──────────────────────────────────────────────
    // 5. while / do-while 반복문
    // ──────────────────────────────────────────────
    static void whileLoopDemo() {
        System.out.println("── 5. while / do-while ──");

        // while - 조건을 먼저 검사한 후 실행
        System.out.print("while: ");
        int i = 1;
        while (i <= 5) {
            System.out.print(i + " ");
            i++;
        }
        System.out.println();

        // do-while - 먼저 실행한 후 조건 검사 (최소 1번 실행 보장)
        System.out.print("do-while: ");
        int j = 1;
        do {
            System.out.print(j + " ");
            j++;
        } while (j <= 5);
        System.out.println();

        // while vs do-while 차이점
        // 조건이 처음부터 false일 때
        System.out.print("while (조건 false): ");
        int k = 10;
        while (k < 5) {  // 조건 false → 한 번도 실행 안 됨
            System.out.print(k + " ");
            k++;
        }
        System.out.println("(실행 안 됨)");

        System.out.print("do-while (조건 false): ");
        int m = 10;
        do {  // 조건과 무관하게 최소 1번 실행
            System.out.print(m + " ");
            m++;
        } while (m < 5);
        System.out.println("(1번 실행됨)");
        System.out.println();
    }

    // ──────────────────────────────────────────────
    // 6. break / continue
    // ──────────────────────────────────────────────
    static void breakContinueDemo() {
        System.out.println("── 6. break / continue ──");

        // break - 반복문을 즉시 종료
        System.out.print("break 예시: ");
        for (int i = 1; i <= 10; i++) {
            if (i == 6) break;  // i가 6이면 반복 종료
            System.out.print(i + " ");
        }
        System.out.println(" (6에서 종료)");

        // continue - 현재 반복을 건너뛰고 다음 반복으로
        System.out.print("continue 예시: ");
        for (int i = 1; i <= 10; i++) {
            if (i % 2 == 0) continue;  // 짝수는 건너뜀
            System.out.print(i + " ");
        }
        System.out.println(" (짝수 건너뜀)");
        System.out.println();
    }

    // ──────────────────────────────────────────────
    // 7. 레이블 반복문 (Labeled Loops)
    // ──────────────────────────────────────────────
    static void labeledLoopDemo() {
        System.out.println("── 7. 레이블 반복문 (Labeled Loops) ──");

        // 레이블 없이 중첩 루프에서 break → 내부 루프만 종료
        System.out.println("[레이블 없는 break]");
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (j == 2) break;  // 내부 루프만 종료
                System.out.println("  i=" + i + ", j=" + j);
            }
        }

        // 레이블을 사용하면 외부 루프도 제어 가능
        System.out.println("\n[레이블 break - 외부 루프 종료]");
        outer:  // 레이블 선언
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (i == 1 && j == 1) {
                    System.out.println("  i=1, j=1에서 외부 루프 종료!");
                    break outer;  // 외부 루프까지 종료
                }
                System.out.println("  i=" + i + ", j=" + j);
            }
        }

        // 레이블 continue - 외부 루프의 다음 반복으로
        System.out.println("\n[레이블 continue - 외부 루프 다음 반복]");
        outerLoop:
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (j == 1) {
                    continue outerLoop;  // 외부 루프의 다음 반복으로 이동
                }
                System.out.println("  i=" + i + ", j=" + j);
            }
        }

        // 실용 예시: 2차원 배열에서 특정 값 찾기
        System.out.println("\n[실용 예시: 2차원 배열에서 값 찾기]");
        int[][] matrix = {
            {1, 2, 3},
            {4, 5, 6},
            {7, 8, 9}
        };
        int target = 5;
        boolean found = false;

        search:
        for (int row = 0; row < matrix.length; row++) {
            for (int col = 0; col < matrix[row].length; col++) {
                if (matrix[row][col] == target) {
                    System.out.println("  " + target + "을(를) 찾았습니다! 위치: [" + row + "][" + col + "]");
                    found = true;
                    break search;  // 찾으면 전체 탐색 종료
                }
            }
        }
        if (!found) {
            System.out.println("  " + target + "을(를) 찾지 못했습니다.");
        }
        System.out.println();
    }
}
