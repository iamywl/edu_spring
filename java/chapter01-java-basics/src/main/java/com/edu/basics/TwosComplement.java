package com.edu.basics;

/**
 * Chapter 01 (CS 심화) - 정수의 비트 표현: 2의 보수(Two's Complement)
 *
 * "왜 (byte)300 이 44가 되고, MAX_VALUE + 1 이 갑자기 최솟값이 되는가?"
 * 컴퓨터는 정수를 고정된 개수의 비트로만 저장하기 때문에, 넘치면(overflow)
 * 시계처럼 한 바퀴 돌아 반대편으로 돌아옵니다(modular wraparound).
 *
 * 핵심:
 *   - 음수는 2의 보수로 저장된다: -x = (~x) + 1
 *   - 최상위 비트(MSB)가 1이면 음수, 0이면 양수 (부호 비트)
 *   - 연산은 2^N 을 법(modulo)으로 하는 원형 산술이다.
 */
public class TwosComplement {

    public static void main(String[] args) {
        System.out.println("====================================");
        System.out.println(" Chapter 01 심화: 2의 보수와 오버플로우");
        System.out.println("====================================\n");

        byteOverflow();
        signBitAndNegatives();
        minusOneIsAllOnes();
        intOverflowWraparound();
        absMinValueParadox();
        modularExplanation();
    }

    // ──────────────────────────────────────────────
    // 1. (byte)300 == 44 인 이유
    // ──────────────────────────────────────────────
    static void byteOverflow() {
        System.out.println("── 1. (byte)300 == 44 ──");

        int original = 300;
        byte narrowed = (byte) original;

        System.out.println("  (byte)300 = " + narrowed + "  → 44!");

        // byte는 8비트. 300을 2진수로 쓰면 9비트가 필요합니다.
        // 300 = 1_0010_1100 (9비트). byte 캐스팅은 하위 8비트만 남깁니다.
        // 남는 8비트 = 0010_1100 = 44.
        System.out.println("\n  300      (2진, 하위 12비트) : " + bin(original, 12));
        System.out.println("  하위 8비트만 남김 (byte)     : " + bin(narrowed & 0xFF, 8));
        System.out.println("  0010_1100(2) = " + Integer.parseInt("00101100", 2) + " = 44");

        // 만약 최상위(8번째) 비트가 1이 되도록 잘리면 음수가 됩니다.
        System.out.println("\n  (byte)200 = " + (byte) 200 + "  (200의 하위 8비트 = 1100_1000, MSB=1 → 음수)");
        System.out.println("  (byte)128 = " + (byte) 128 + "  (1000_0000, MSB=1 → -128)");
        System.out.println();
    }

    // ──────────────────────────────────────────────
    // 2. 부호 비트와 음수 표현
    // ──────────────────────────────────────────────
    static void signBitAndNegatives() {
        System.out.println("── 2. 부호 비트와 2의 보수 ──");

        // 음수는 "2의 보수"로 저장합니다: -x 의 비트 = (~x) + 1
        // 예: 5 → 뒤집기 → +1 = -5 의 비트 패턴
        int five = 5;
        int flipped = ~five;          // 비트 전부 반전
        int negFive = flipped + 1;    // +1 하면 -5

        System.out.println("   5        : " + bin(five, 8) + "  (하위 8비트)");
        System.out.println("  ~5 (반전) : " + bin(flipped, 8));
        System.out.println("  ~5 + 1    : " + bin(negFive, 8) + "  = " + negFive + "  → 이것이 -5의 비트!");
        System.out.println("  확인: (~5)+1 == -5 ? " + ((~five + 1) == -5));

        // 최상위 비트(부호 비트)로 양/음을 판별합니다.
        System.out.println("\n  최상위 비트(MSB)가 1이면 음수, 0이면 양수");
        System.out.println("   7 의 MSB : " + (7 >>> 31) + " (양수)");
        System.out.println("  -7 의 MSB : " + (-7 >>> 31) + " (음수)");
        System.out.println();
    }

    // ──────────────────────────────────────────────
    // 3. -1 은 모든 비트가 1 (0xFFFFFFFF)
    // ──────────────────────────────────────────────
    static void minusOneIsAllOnes() {
        System.out.println("── 3. -1 == 0xFFFFFFFF (모든 비트 1) ──");

        // 2의 보수에서 -1은 모든 비트가 1입니다. (~0 + 1 = 0? 아니라 ~1... 를 생각하지 말고:
        //  0에서 1을 빼면 한 바퀴 돌아 전부 1이 됩니다.)
        System.out.println("  -1 (2진 32비트) : " + bin(-1, 32));
        System.out.println("  -1 (16진)       : 0x" + Integer.toHexString(-1).toUpperCase());
        System.out.println("  -1 == 0xFFFFFFFF ? " + (-1 == 0xFFFFFFFF));

        // 그래서 부호 없는 값처럼 다루려면 별도 처리가 필요합니다.
        System.out.println("  (byte)-1을 부호없이 보면 : " + Integer.toBinaryString((byte) -1 & 0xFF) + " = 255");
        System.out.println();
    }

    // ──────────────────────────────────────────────
    // 4. MAX_VALUE + 1 → MIN_VALUE (오버플로우 랩어라운드)
    // ──────────────────────────────────────────────
    static void intOverflowWraparound() {
        System.out.println("── 4. MAX_VALUE + 1 → MIN_VALUE ──");

        int max = Integer.MAX_VALUE;   //  2147483647 = 0x7FFFFFFF
        int over = max + 1;            // 오버플로우!

        System.out.println("  Integer.MAX_VALUE     : " + max + " = 0x" + Integer.toHexString(max).toUpperCase());
        System.out.println("  MAX_VALUE + 1         : " + over + " = 0x" + Integer.toHexString(over).toUpperCase());
        System.out.println("  == Integer.MIN_VALUE ? " + (over == Integer.MIN_VALUE));

        // 0x7FFFFFFF 에 1을 더하면 0x80000000. 최상위 비트가 1이 되어 가장 큰 음수가 됩니다.
        System.out.println("\n  0x7FFF...F + 1 = 0x8000...0 → 부호비트가 1로 켜지며 최솟값으로 점프");
        System.out.println("  MIN_VALUE - 1         : " + (Integer.MIN_VALUE - 1) + "  → 다시 MAX_VALUE (반대로 랩)");
        System.out.println();
    }

    // ──────────────────────────────────────────────
    // 5. Math.abs(Integer.MIN_VALUE) 가 음수인 역설
    // ──────────────────────────────────────────────
    static void absMinValueParadox() {
        System.out.println("── 5. Math.abs(MIN_VALUE)가 음수인 역설 ──");

        // int 범위: -2147483648 ~ +2147483647
        // 음수 쪽이 1개 더 많습니다. 즉 MIN_VALUE의 절댓값(+2147483648)은
        // int로 표현할 수 없습니다! 그래서 abs가 오버플로우해 자기 자신을 반환합니다.
        System.out.println("  Integer.MIN_VALUE      : " + Integer.MIN_VALUE);
        System.out.println("  Integer.MAX_VALUE      : " + Integer.MAX_VALUE);
        System.out.println("  Math.abs(MIN_VALUE)    : " + Math.abs(Integer.MIN_VALUE) + "  ← 여전히 음수!");
        System.out.println("  이유: |MIN_VALUE| = 2147483648 은 MAX_VALUE(2147483647)보다 커서 표현 불가");
        System.out.println("  음수가 양수보다 정확히 1개 더 많기 때문 (0을 양수 쪽으로 셈)");
        System.out.println();
    }

    // ──────────────────────────────────────────────
    // 6. 결국은 modular(원형) 산술
    // ──────────────────────────────────────────────
    static void modularExplanation() {
        System.out.println("── 6. 정수 연산 = 2^N 을 법으로 하는 원형 산술 ──");

        // N비트 정수 연산은 결과를 2^N 으로 나눈 나머지로 취급합니다(시계 산술).
        // byte(8비트)는 mod 256, int(32비트)는 mod 2^32 로 순환합니다.
        System.out.println("  N비트 정수는 시계처럼 2^N 을 한 바퀴로 순환합니다.");
        System.out.println("  byte(8비트): 값들이 0~255(부호없이) 또는 -128~127(부호있게) 원형 배치");
        System.out.println("  300 mod 256 = " + (300 % 256) + "  → (byte)300 의 부호없는 결과와 일치(44)");
        System.out.println("  큰 수 곱셈에서 소리 없이 오버플로우가 나면 결과가 완전히 틀립니다.");
        System.out.println("  → 안전이 필요하면 Math.addExact / Math.multiplyExact 로 예외 감지, 또는 long/BigInteger 사용");
        System.out.println();
    }

    // ── 출력 보조: 값을 지정한 비트 수의 2진 문자열로 (4자리마다 _) ──
    static String bin(int value, int bits) {
        String raw = Integer.toBinaryString(value);
        if (raw.length() > bits) raw = raw.substring(raw.length() - bits);
        raw = String.format("%" + bits + "s", raw).replace(' ', '0');
        // 4자리마다 언더스코어로 가독성 향상
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < raw.length(); i++) {
            if (i > 0 && (raw.length() - i) % 4 == 0) sb.append('_');
            sb.append(raw.charAt(i));
        }
        return sb.toString();
    }
}
