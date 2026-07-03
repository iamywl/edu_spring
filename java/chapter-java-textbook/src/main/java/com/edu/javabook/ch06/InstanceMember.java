package com.edu.javabook.ch06;

/**
 * 6.9 인스턴스 멤버
 *
 * 인스턴스 멤버는 객체(인스턴스)마다 따로 존재하는 필드와 메서드다(static이 붙지 않음).
 *  - 인스턴스 필드: 객체를 new 할 때마다 새로 만들어져, 객체마다 독립된 값을 가진다.
 *  - 인스턴스 메서드: 반드시 객체를 통해 호출한다(객체이름.메서드()).
 *
 * this 키워드는 '메서드를 호출한 바로 그 객체 자신'을 가리킨다.
 * 따라서 인스턴스 메서드 안의 this.필드 는 각 객체의 자기 필드를 의미한다.
 */
public class InstanceMember {

    static class Counter {
        int count;   // 인스턴스 필드: 객체마다 별도

        // 인스턴스 메서드: this 를 통해 자기 객체의 필드에 접근
        void increase() {
            this.count++;   // this 는 이 메서드를 호출한 객체
        }

        // this 를 리턴하여 메서드 체이닝도 가능
        Counter reset() {
            this.count = 0;
            return this;
        }
    }

    public static void main(String[] args) {

        System.out.println("=== 6.9 인스턴스 멤버 ===");

        // [1] 인스턴스 필드는 객체마다 독립적
        System.out.println("\n[1] 객체마다 독립된 인스턴스 필드");
        Counter a = new Counter();
        Counter b = new Counter();
        a.increase();
        a.increase();
        b.increase();
        System.out.println("a.count = " + a.count + " (a만 2번 증가)");
        System.out.println("b.count = " + b.count + " (b는 1번 증가)");

        // [2] this: 메서드를 호출한 그 객체를 가리킴
        System.out.println("\n[2] this 는 호출한 객체 자신");
        System.out.println("a.increase() 안의 this.count 는 a의 count");
        System.out.println("b.increase() 안의 this.count 는 b의 count");

        // [3] 인스턴스 메서드는 객체를 통해 호출
        System.out.println("\n[3] 객체를 통한 호출");
        a.reset();
        System.out.println("a.reset() 후 a.count = " + a.count);

        // [4] this 리턴을 이용한 메서드 체이닝
        System.out.println("\n[4] this 리턴 → 체이닝");
        b.reset().increase();     // reset()이 this를 리턴 → 이어서 increase()
        System.out.println("b.reset().increase() → b.count = " + b.count);

        // [왜?] 인스턴스 멤버는 '각 객체의 상태와 행위'를 표현하는 핵심이다.
        System.out.println("\n[왜?] this 덕분에 하나의 메서드 코드가 각 객체에 맞게 동작한다.");

        System.out.println("\n프로그램 정상 종료");
    }
}
