package com.edu.javabook.ch06;

/**
 * 6.5 클래스의 구성 멤버
 *
 * 클래스는 크게 세 종류의 멤버로 구성된다.
 *  1) 필드(field)       : 객체의 데이터(상태)를 저장한다.
 *  2) 생성자(constructor): 객체가 생성될 때 초기화를 담당한다. 클래스 이름과 같다.
 *  3) 메서드(method)    : 객체의 기능(행위)을 정의한다.
 *
 * 이 소절에서는 세 가지 멤버가 한 클래스 안에서 어떻게 어울리는지 개요를 보인다.
 */
public class ClassMembers {

    // 세 종류 멤버를 모두 가진 클래스
    static class Account {
        // 1) 필드
        String owner;
        int balance;

        // 2) 생성자 (객체 생성 시 필드 초기화)
        Account(String owner, int balance) {
            this.owner = owner;
            this.balance = balance;
        }

        // 3) 메서드
        void deposit(int amount) {
            balance += amount;
        }

        String info() {
            return owner + "님 잔액: " + balance + "원";
        }
    }

    public static void main(String[] args) {

        System.out.println("=== 6.5 클래스의 구성 멤버 ===");

        // [1] 구성 멤버 개요
        System.out.println("\n[1] 클래스의 3대 구성 멤버");
        System.out.println("- 필드      : 데이터(상태) 저장");
        System.out.println("- 생성자    : 객체 생성 시 초기화, 클래스 이름과 동일");
        System.out.println("- 메서드    : 기능(행위) 정의");

        // [2] 생성자로 객체 초기화 (필드 채우기)
        System.out.println("\n[2] 생성자 사용");
        Account acc = new Account("김철수", 1000);
        System.out.println("초기 상태 → " + acc.info());

        // [3] 메서드로 기능 실행 (필드 변경)
        System.out.println("\n[3] 메서드 사용");
        acc.deposit(500);
        System.out.println("deposit(500) 후 → " + acc.info());

        // [4] 필드에 직접 접근
        System.out.println("\n[4] 필드 접근");
        System.out.println("acc.owner   = " + acc.owner);
        System.out.println("acc.balance = " + acc.balance);

        // [왜?] 필드(데이터) + 생성자(초기화) + 메서드(행위)가 모여 하나의 객체를 완성한다.
        System.out.println("\n[왜?] 세 멤버가 협력하여 '상태를 가지고 행동하는' 객체가 된다.");

        System.out.println("\n프로그램 정상 종료");
    }
}
