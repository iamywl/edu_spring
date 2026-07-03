package com.edu.javabook.ch15;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 15.7 동기화된 컬렉션
 *
 * ArrayList, HashMap 같은 기본 컬렉션은 "동기화되지 않았다(not thread-safe)".
 * 즉 여러 스레드가 "동시에" 수정하면 데이터가 깨지거나 예외가 날 수 있다.
 *
 * 왜 필요한가?
 *  - 단일 스레드에서는 문제없지만, 여러 스레드가 같은 컬렉션을 동시에 add/remove 하면
 *    내부 상태가 꼬여 잘못된 결과나 ConcurrentModificationException 등이 발생할 수 있다.
 *
 * 해결책 :
 *  1) Collections.synchronizedList/Set/Map(...) : 기존 컬렉션을 "동기화 래퍼"로 감싼다.
 *     - 모든 메소드가 하나의 락으로 보호된다.
 *     - 단, 순회(for-each) 시에는 직접 synchronized 블록으로 감싸야 안전하다.
 *  2) java.util.concurrent 패키지 : 애초에 동시성을 위해 설계된 컬렉션.
 *     - ConcurrentHashMap : 부분 잠금으로 동기화 래퍼보다 성능이 우수하다.
 *     - CopyOnWriteArrayList 등.
 *
 * 이 소절에서는 동기화 래퍼와 ConcurrentHashMap 을 여러 스레드로 실습한다.
 */
public class SynchronizedCollection {

    public static void main(String[] args) throws InterruptedException {

        System.out.println("=== 15.7 동기화된 컬렉션 ===");

        // [1] 동기화되지 않은 컬렉션의 위험
        System.out.println("\n[1] 기본 컬렉션은 thread-safe 하지 않다");
        System.out.println("ArrayList, HashMap 은 여러 스레드가 동시에 수정하면 깨질 수 있다.");

        // [2] Collections.synchronizedList
        System.out.println("\n[2] Collections.synchronizedList 로 동기화");
        List<Integer> syncList = Collections.synchronizedList(new ArrayList<>());

        Runnable addTask = () -> {
            for (int i = 0; i < 1000; i++) syncList.add(i);
        };
        Thread t1 = new Thread(addTask);
        Thread t2 = new Thread(addTask);
        t1.start(); t2.start();
        t1.join();  t2.join();               // 두 스레드 종료 대기
        System.out.println("두 스레드가 각 1000개 add → 총 크기: " + syncList.size()
                + " (동기화 덕분에 2000 정확)");

        // 순회는 직접 락으로 감싸야 안전하다
        long sum = 0;
        synchronized (syncList) {            // 순회 중 다른 수정 차단
            for (int v : syncList) sum += v;
        }
        System.out.println("동기화 블록으로 순회한 합계: " + sum);

        // [3] ConcurrentHashMap
        System.out.println("\n[3] ConcurrentHashMap (java.util.concurrent)");
        Map<Integer, Integer> concMap = new ConcurrentHashMap<>();
        Runnable putTask = () -> {
            for (int i = 0; i < 500; i++) concMap.put(i, i * i);
        };
        Thread m1 = new Thread(putTask);
        Thread m2 = new Thread(putTask);
        m1.start(); m2.start();
        m1.join();  m2.join();
        System.out.println("두 스레드가 같은 키로 put → 키 개수: " + concMap.size()
                + " (같은 키라 500)");
        System.out.println("concMap.get(10) = " + concMap.get(10));
        System.out.println("→ ConcurrentHashMap 은 부분 잠금으로 동기화 래퍼보다 빠르다.");

        System.out.println("\n프로그램 정상 종료");
    }
}
