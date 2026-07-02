package com.edu.javabook.ch17;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 17.12 수집
 *
 * [수집(collect)]
 * - collect(Collector) : 스트림 요소를 컬렉션/맵/문자열 등으로 모은다(최종 연산).
 *
 * [자주 쓰는 Collectors]
 * - toList()          : List 로 수집.
 * - toMap(k, v)       : Map 으로 수집(키/값 함수 지정).
 * - groupingBy(k)     : 키로 그룹화하여 Map<K, List<T>> 생성.
 * - joining(구분자)   : 문자열을 이어붙임.
 * - partitioningBy(p) : 조건 true/false 두 그룹으로 분할.
 *
 * 이 소절에서는 각 Collectors 사용법을 시연한다.
 */
public class Collecting {

    record Product(String name, String category, int price) {}

    public static void main(String[] args) {

        System.out.println("=== 17.12 수집 ===");

        List<Product> products = List.of(
                new Product("사과", "과일", 1000),
                new Product("바나나", "과일", 1500),
                new Product("당근", "채소", 800),
                new Product("배추", "채소", 2000)
        );

        // [1] toList: 이름만 리스트로 수집
        System.out.println("\n[1] Collectors.toList (이름 목록)");
        List<String> names = products.stream()
                .map(Product::name)
                .collect(Collectors.toList());
        System.out.println("이름: " + names);

        // [2] toMap: 이름 → 가격 맵으로 수집
        System.out.println("\n[2] Collectors.toMap (이름→가격)");
        Map<String, Integer> priceMap = products.stream()
                .collect(Collectors.toMap(Product::name, Product::price));
        System.out.println("가격 맵: " + priceMap);

        // [3] groupingBy: 카테고리별 그룹화
        System.out.println("\n[3] Collectors.groupingBy (카테고리별 그룹화)");
        Map<String, List<Product>> byCategory = products.stream()
                .collect(Collectors.groupingBy(Product::category));
        byCategory.forEach((category, list) ->
                System.out.println("  " + category + ": " + list.stream().map(Product::name).toList()));

        // [4] joining: 이름을 하나의 문자열로 연결
        System.out.println("\n[4] Collectors.joining (이름 문자열 연결)");
        String joined = products.stream()
                .map(Product::name)
                .collect(Collectors.joining(", ", "[", "]"));
        System.out.println("연결: " + joined);

        // [5] partitioningBy: 1000원 이상 여부로 두 그룹 분할
        System.out.println("\n[5] Collectors.partitioningBy (1000원 이상 여부로 분할)");
        Map<Boolean, List<Product>> partitioned = products.stream()
                .collect(Collectors.partitioningBy(p -> p.price() >= 1000));
        System.out.println("  1000원 이상: " + partitioned.get(true).stream().map(Product::name).toList());
        System.out.println("  1000원 미만: " + partitioned.get(false).stream().map(Product::name).toList());

        System.out.println("\n프로그램 정상 종료");
    }
}
