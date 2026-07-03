package com.edu.oop;

/**
 * [개념 4] Object의 toString / equals / hashCode 재정의
 *
 * 모든 Java 클래스는 java.lang.Object 를 상속한다.
 * Object 에는 toString(), equals(), hashCode() 가 기본 구현되어 있지만,
 * 기본 동작은 "객체의 메모리 주소" 기준이라 대부분 원하는 결과가 아니다.
 * 그래서 의미 있는 동작을 위해 재정의(override)한다.
 *
 * 이 예제는 Animal(및 자식 Dog)에서 재정의된 세 메서드를 사용한다.
 *
 * === equals - hashCode 계약(contract) 요약 ===
 *   1. a.equals(b) 가 true 이면, a.hashCode() == b.hashCode() 여야 한다.
 *   2. 반대는 성립하지 않아도 된다(해시 충돌 허용):
 *      hashCode 가 같아도 equals 가 false 일 수 있다.
 *   3. equals 를 재정의하면 hashCode 도 반드시 함께 재정의해야 한다.
 *      -> 그렇지 않으면 HashMap/HashSet 같은 해시 기반 컬렉션이 오작동한다.
 */
public class ObjectMethodsExample {

    public static void main(String[] args) {

        // 내용이 동일한 두 객체(이름/나이가 같음)와, 다른 객체 하나 준비
        Dog dog1 = new Dog("바둑이", 3, "진돗개");
        Dog dog2 = new Dog("바둑이", 3, "진돗개");  // dog1과 내용은 같지만 다른 인스턴스
        Dog dog3 = new Dog("초코", 5, "푸들");       // 내용이 다른 객체

        // ------------------------------------------------------------
        // 1) toString: 객체를 사람이 읽기 좋은 문자열로
        // ------------------------------------------------------------
        printSection("1. toString()");

        // println에 객체를 넘기면 toString()이 자동 호출된다.
        // Object 기본 toString은 'Dog@1b6d3586' 같은 형태지만,
        // Dog가 재정의했기 때문에 필드 값이 보인다.
        System.out.println("dog1.toString() -> " + dog1);
        System.out.println("dog3.toString() -> " + dog3);

        // ------------------------------------------------------------
        // 2) == 과 equals 의 차이
        // ------------------------------------------------------------
        printSection("2. == (참조 비교) vs equals (내용 비교)");

        // == 는 두 변수가 "같은 객체"를 가리키는지(주소)를 본다.
        System.out.println("dog1 == dog2         -> " + (dog1 == dog2));  // false: 서로 다른 인스턴스
        // equals 는 재정의된 내용 비교(name, age 기준)를 본다.
        System.out.println("dog1.equals(dog2)    -> " + dog1.equals(dog2)); // true: 내용이 같음
        System.out.println("dog1.equals(dog3)    -> " + dog1.equals(dog3)); // false: 내용이 다름

        // ------------------------------------------------------------
        // 3) hashCode: equals와 함께 재정의 (계약 확인)
        // ------------------------------------------------------------
        printSection("3. hashCode() 와 equals-hashCode 계약");

        System.out.println("dog1.hashCode() = " + dog1.hashCode());
        System.out.println("dog2.hashCode() = " + dog2.hashCode());
        System.out.println("dog3.hashCode() = " + dog3.hashCode());

        System.out.println();
        // 계약 1 검증: equals가 true인 dog1, dog2는 hashCode도 같아야 한다.
        boolean equalsTrue = dog1.equals(dog2);
        boolean hashSame = dog1.hashCode() == dog2.hashCode();
        System.out.println("dog1.equals(dog2) = " + equalsTrue
                + " 이고 hashCode 동일 = " + hashSame);
        System.out.println("=> equals가 true면 hashCode도 같아야 한다는 계약을 지킴: "
                + (!equalsTrue || hashSame));

        // ------------------------------------------------------------
        // 4) 계약을 지켜야 하는 실제 이유: 해시 컬렉션
        // ------------------------------------------------------------
        printSection("4. 왜 계약이 중요한가 (HashSet 예시)");

        java.util.Set<Dog> set = new java.util.HashSet<>();
        set.add(dog1);
        set.add(dog2);  // dog1과 equals/hashCode가 같으므로 중복으로 간주 -> 추가되지 않음
        set.add(dog3);

        System.out.println("HashSet에 dog1, dog2, dog3 추가");
        System.out.println("실제 저장된 개수: " + set.size()
                + " (dog1==dog2 로 취급되어 중복 제거됨)");
        System.out.println("set.contains(new Dog(\"바둑이\",3,\"진돗개\")) -> "
                + set.contains(new Dog("바둑이", 3, "진돗개")));

        // ------------------------------------------------------------
        // 정리
        // ------------------------------------------------------------
        printSection("정리");
        System.out.println("- toString: 객체를 읽기 좋은 문자열로 (로그/디버깅에 유용).");
        System.out.println("- equals: 내용 동등성 비교 (== 는 참조 비교).");
        System.out.println("- hashCode: equals와 반드시 함께 재정의.");
        System.out.println("  (equals가 true면 hashCode도 같아야 해시 컬렉션이 올바르게 동작).");
    }

    private static void printSection(String title) {
        System.out.println();
        System.out.println("=".repeat(60));
        System.out.println("  " + title);
        System.out.println("=".repeat(60));
    }
}
