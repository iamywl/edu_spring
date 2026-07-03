package com.edu.basics;

import java.math.BigDecimal;

/**
 * Chapter 01 (CS 심화) - 부동소수점의 비트 표현: 왜 0.1 + 0.2 != 0.3 인가
 *
 * "컴퓨터는 왜 간단한 소수 계산도 틀리는가?"
 * 이 질문에 답하려면 double이 메모리에서 어떻게 저장되는지(IEEE-754)를 알아야 합니다.
 *
 * 핵심 결론(먼저 봅니다):
 *   - double은 10진 소수를 "정확히" 저장하지 못합니다. 2진 분수로 근사할 뿐입니다.
 *   - 0.1, 0.2, 0.3 모두 2진수로는 무한소수라서 반올림 오차가 섞입니다.
 *   - 따라서 돈/정확도가 중요한 계산은 반드시 BigDecimal(또는 정수 단위)로 하세요.
 */
public class FloatingPointBits {

    public static void main(String[] args) {
        System.out.println("====================================");
        System.out.println(" Chapter 01 심화: 부동소수점의 비트");
        System.out.println("====================================\n");

        theClassicBug();
        bitLayout();
        decomposeDouble(0.1);
        noExactBinary();
        moreSurprises();
        theFix();
    }

    // ──────────────────────────────────────────────
    // 1. 그 유명한 버그: 0.1 + 0.2
    // ──────────────────────────────────────────────
    static void theClassicBug() {
        System.out.println("── 1. 0.1 + 0.2 는 0.3이 아니다 ──");

        double sum = 0.1 + 0.2;
        // println은 "보기 좋게" 반올림해 출력하므로 함정을 숨길 수 있습니다.
        System.out.println("  0.1 + 0.2          = " + sum);
        System.out.println("  0.1 + 0.2 == 0.3   ? " + (sum == 0.3));   // false!

        // %.17f 로 17자리까지 강제로 펼치면 숨어 있던 오차가 드러납니다.
        System.out.printf("  0.1 + 0.2 (17자리) = %.17f%n", sum);
        System.out.printf("  0.3       (17자리) = %.17f%n", 0.3);
        System.out.println("  → 마지막 자리에서 미세하게 다릅니다 (반올림 오차).\n");
    }

    // ──────────────────────────────────────────────
    // 2. double 64비트의 구조 (IEEE-754 배정밀도)
    // ──────────────────────────────────────────────
    static void bitLayout() {
        System.out.println("── 2. double의 64비트 구조 (IEEE-754) ──");

        // double은 64비트를 세 부분으로 나눠 씁니다:
        //   [부호 1비트][지수 11비트][가수(mantissa) 52비트]
        // 값 = (-1)^부호 × 1.가수(2진) × 2^(지수 - 1023)
        // '1.가수' 앞의 1은 항상 생략(암묵적 1)되므로 실제로는 53비트 정밀도를 갖습니다.
        System.out.println("  전체 64비트 = 부호(1) + 지수(11) + 가수(52)");
        System.out.println("  값 = (-1)^S × 1.M(2진) × 2^(E - 1023)");
        System.out.println("  '1.'은 항상 생략되는 암묵적 비트 → 실질 정밀도 53비트");

        // doubleToLongBits: double의 원시 비트를 그대로 64비트 정수로 꺼냅니다.
        long bits = Double.doubleToLongBits(0.1);
        System.out.println("\n  0.1의 원시 비트(16진) : 0x" + Long.toHexString(bits));
        System.out.println("  0.1의 원시 비트(2진)  : " + pad64(Long.toBinaryString(bits)));
        System.out.println();
    }

    // ──────────────────────────────────────────────
    // 3. double을 부호/지수/가수로 분해해 보기
    // ──────────────────────────────────────────────
    static void decomposeDouble(double d) {
        System.out.println("── 3. double 분해: d = " + d + " ──");

        long bits = Double.doubleToLongBits(d);

        // 비트 마스킹/시프트로 세 부분을 잘라냅니다.
        int sign = (int) ((bits >>> 63) & 0x1);          // 최상위 1비트
        int exponent = (int) ((bits >>> 52) & 0x7FF);    // 그다음 11비트
        long mantissa = bits & 0xFFFFFFFFFFFFFL;         // 나머지 52비트

        System.out.println("  부호(1비트)   : " + sign + "  (0=양수, 1=음수)");
        System.out.println("  지수(11비트)  : " + exponent
                + "  → 실제 지수 = " + (exponent - 1023) + " (bias 1023 차감)");
        System.out.println("  가수(52비트)  : " + pad52(Long.toBinaryString(mantissa)));

        // 저장된 비트로부터 값을 직접 재구성해 원본과 같은지 확인합니다.
        // 실제값 = (1 + 가수/2^52) × 2^(지수-1023)
        double reconstructed = (1.0 + mantissa / Math.pow(2, 52)) * Math.pow(2, exponent - 1023);
        if (sign == 1) reconstructed = -reconstructed;
        System.out.println("  비트로 재구성한 값 : " + reconstructed);
        System.out.println("  원본과 일치?       : " + (reconstructed == d));
        System.out.println();
    }

    // ──────────────────────────────────────────────
    // 4. 0.1은 2진수로 무한소수라서 "정확히" 저장 불가
    // ──────────────────────────────────────────────
    static void noExactBinary() {
        System.out.println("── 4. 0.1은 2진수로 무한 반복소수 ──");

        // 10진수 0.1을 2진수로 바꾸면 0.0001100110011001100...(1100 무한 반복)입니다.
        // 마치 10진수에서 1/3 = 0.3333...이 끝나지 않는 것과 같은 원리입니다.
        // 52비트에서 잘리므로 double은 0.1과 "가장 가까운" 2진 분수를 저장합니다.
        System.out.println("  10진 0.1 → 2진 0.0001100110011001100...(1100 무한반복)");
        System.out.println("  비유: 10진수에서 1/3 = 0.3333...이 끝나지 않는 것과 동일");

        // BigDecimal(double) 생성자는 double에 "실제로 저장된 값"을 그대로 보여줍니다.
        // (반면 BigDecimal("0.1")은 우리가 입력한 그대로의 정확한 0.1입니다.)
        System.out.println("\n  double 0.1에 실제로 저장된 값(BigDecimal로 확인):");
        System.out.println("    " + new BigDecimal(0.1));
        System.out.println("  → 0.1이 아니라 0.1000000000000000055511151231257827021181583404541015625");
        System.out.println();
    }

    // ──────────────────────────────────────────────
    // 5. 더 놀라운 등식들
    // ──────────────────────────────────────────────
    static void moreSurprises() {
        System.out.println("── 5. 더 놀라운 등식들 ──");

        // 정수처럼 보이는 계산도 틀릴 수 있습니다.
        System.out.println("  0.1 + 0.2 == 0.3           ? " + (0.1 + 0.2 == 0.3));       // false
        System.out.println("  0.1 * 3   == 0.3           ? " + (0.1 * 3 == 0.3));         // false
        System.out.printf ("  0.1 * 3    (17자리)        = %.17f%n", 0.1 * 3);

        // 반대로 우연히 딱 맞아떨어지는 경우도 있습니다(오차가 서로 상쇄).
        System.out.println("  0.1 + 0.1 == 0.2           ? " + (0.1 + 0.1 == 0.2));       // true (우연)
        System.out.println("  0.5 + 0.25 == 0.75         ? " + (0.5 + 0.25 == 0.75));     // true (2의 거듭제곱은 정확)

        // 매우 큰 수 + 매우 작은 수: 작은 값이 정밀도 밖으로 밀려나 사라집니다.
        double big = 1e16;
        System.out.println("\n  1e16 + 1   == 1e16         ? " + (big + 1 == big));        // true! 1이 흡수됨
        System.out.println("  → 유효자리(약 15~16자리)를 넘으면 작은 값은 무시됩니다.");
        System.out.println();
    }

    // ──────────────────────────────────────────────
    // 6. 해결책: 돈 계산은 BigDecimal
    // ──────────────────────────────────────────────
    static void theFix() {
        System.out.println("── 6. 해결책: 돈 계산은 BigDecimal ★규칙★ ──");

        // 반드시 "문자열" 생성자를 쓰세요. new BigDecimal(0.1)은 오차가 이미 섞인 double을 받습니다.
        BigDecimal a = new BigDecimal("0.1");
        BigDecimal b = new BigDecimal("0.2");
        BigDecimal sum = a.add(b);

        System.out.println("  new BigDecimal(\"0.1\").add(\"0.2\") = " + sum);
        System.out.println("  == new BigDecimal(\"0.3\")        ? "
                + (sum.compareTo(new BigDecimal("0.3")) == 0));   // true!

        System.out.println("\n  ⚠ 규칙 정리:");
        System.out.println("    1) 금액/정밀도가 중요하면 double 금지 → BigDecimal(문자열 생성자) 사용");
        System.out.println("    2) BigDecimal 값 비교는 equals가 아니라 compareTo 사용 (스케일 무시)");
        System.out.println("    3) 또는 '원' 단위 정수(long)로 저장해 소수점 자체를 없애기");
        System.out.println();
    }

    // ── 출력 보조: 64비트/52비트 자리수 맞춰 0 채우기 ──
    static String pad64(String bin) {
        return String.format("%64s", bin).replace(' ', '0');
    }
    static String pad52(String bin) {
        return String.format("%52s", bin).replace(' ', '0');
    }
}
