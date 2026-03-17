package com.edu.collections;

import java.util.*;
import java.util.stream.*;

/**
 * Chapter 03 - Stream API 예제
 *
 * Stream은 컬렉션 데이터를 선언적(함수형)으로 처리하는 API입니다.
 * 중간 연산(Lazy)과 최종 연산으로 구성됩니다.
 */
public class StreamExample {

    // 예제용 데이터 클래스
    static class Student {
        String name;
        String city;
        int age;
        int score;

        Student(String name, String city, int age, int score) {
            this.name = name;
            this.city = city;
            this.age = age;
            this.score = score;
        }

        public String getName() { return name; }
        public String getCity() { return city; }
        public int getAge() { return age; }
        public int getScore() { return score; }

        @Override
        public String toString() {
            return name + "(" + city + ", " + age + "세, " + score + "점)";
        }
    }

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("  Chapter 03 - Stream API 예제");
        System.out.println("========================================\n");

        demonstrateStreamCreation();
        demonstrateIntermediateOperations();
        demonstrateTerminalOperations();
        demonstrateCollectors();
        demonstrateParallelStream();

        System.out.println("========================================");
        System.out.println("  Stream API 예제 완료!");
        System.out.println("========================================");
    }

    // ======================================================
    // 1. Stream 생성 방법
    // ======================================================
    static void demonstrateStreamCreation() {
        System.out.println("--- 1. Stream 생성 ---");

        // 컬렉션에서 생성
        List<String> list = List.of("a", "b", "c");
        Stream<String> streamFromList = list.stream();
        System.out.println("  컬렉션에서 생성: " + streamFromList.collect(Collectors.toList()));

        // Stream.of() 로 직접 생성
        Stream<String> streamOf = Stream.of("x", "y", "z");
        System.out.println("  Stream.of(): " + streamOf.collect(Collectors.toList()));

        // Stream.iterate() - 시드값과 함수로 무한 스트림 생성
        List<Integer> iterateResult = Stream.iterate(0, n -> n + 2)
                .limit(5)  // 무한 스트림이므로 제한 필요
                .collect(Collectors.toList());
        System.out.println("  Stream.iterate(0, n->n+2).limit(5): " + iterateResult);

        // Java 9+ iterate with predicate
        List<Integer> iterateWithPredicate = Stream.iterate(1, n -> n <= 100, n -> n * 2)
                .collect(Collectors.toList());
        System.out.println("  Stream.iterate(1, n<=100, n*2): " + iterateWithPredicate);

        // Stream.generate() - Supplier로 무한 스트림 생성
        List<Double> randomNumbers = Stream.generate(Math::random)
                .limit(3)
                .collect(Collectors.toList());
        System.out.println("  Stream.generate(Math::random).limit(3): " + randomNumbers);

        // 배열에서 생성
        int[] intArray = {1, 2, 3, 4, 5};
        int sum = Arrays.stream(intArray).sum();
        System.out.println("  Arrays.stream(intArray).sum(): " + sum);

        // 범위 스트림
        List<Integer> range = IntStream.rangeClosed(1, 5)
                .boxed()
                .collect(Collectors.toList());
        System.out.println("  IntStream.rangeClosed(1, 5): " + range);

        // 문자열에서 생성
        long charCount = "Hello World".chars()
                .filter(c -> c != ' ')
                .count();
        System.out.println("  \"Hello World\" 공백 제외 문자 수: " + charCount);
        System.out.println();
    }

    // ======================================================
    // 2. 중간 연산 (Intermediate Operations) - Lazy 평가
    // ======================================================
    static void demonstrateIntermediateOperations() {
        System.out.println("--- 2. 중간 연산 (Intermediate Operations) ---");

        List<String> names = List.of("김철수", "이영희", "박민수", "김영희", "이철수", "김민수");

        // filter: 조건에 맞는 요소만 통과
        List<String> kims = names.stream()
                .filter(name -> name.startsWith("김"))
                .collect(Collectors.toList());
        System.out.println("  filter (김씨만): " + kims);

        // map: 요소를 변환
        List<Integer> nameLengths = names.stream()
                .map(String::length)
                .collect(Collectors.toList());
        System.out.println("  map (이름 길이): " + nameLengths);

        // flatMap: 중첩 구조를 평탄화
        List<List<Integer>> nested = List.of(
                List.of(1, 2, 3),
                List.of(4, 5),
                List.of(6, 7, 8, 9)
        );
        List<Integer> flattened = nested.stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        System.out.println("  flatMap (중첩 리스트 평탄화): " + flattened);

        // flatMap - 문자열 분리 예시
        List<String> sentences = List.of("Hello World", "Java Stream");
        List<String> words = sentences.stream()
                .flatMap(sentence -> Arrays.stream(sentence.split(" ")))
                .collect(Collectors.toList());
        System.out.println("  flatMap (문장 -> 단어): " + words);

        // sorted: 정렬
        List<Integer> numbers = List.of(5, 3, 8, 1, 9, 2);
        List<Integer> sorted = numbers.stream()
                .sorted()
                .collect(Collectors.toList());
        System.out.println("  sorted (오름차순): " + sorted);

        List<Integer> sortedDesc = numbers.stream()
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());
        System.out.println("  sorted (내림차순): " + sortedDesc);

        // distinct: 중복 제거
        List<Integer> withDups = List.of(1, 2, 2, 3, 3, 3, 4);
        List<Integer> distinct = withDups.stream()
                .distinct()
                .collect(Collectors.toList());
        System.out.println("  distinct (중복 제거): " + distinct);

        // peek: 디버깅용 중간 확인 (스트림 요소를 소비하지 않음)
        System.out.print("  peek (디버깅): ");
        List<Integer> peekResult = List.of(1, 2, 3, 4, 5).stream()
                .peek(n -> System.out.print("[peek:" + n + "] "))
                .filter(n -> n % 2 == 0)
                .collect(Collectors.toList());
        System.out.println("-> 결과: " + peekResult);

        // limit / skip: 개수 제한 및 건너뛰기
        List<Integer> range = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        System.out.println("  skip(3).limit(4): " +
                range.stream().skip(3).limit(4).collect(Collectors.toList()));

        // 연산 체이닝
        System.out.println("  [체이닝 예시: 김씨 이름을 정렬하고 대문자로]");
        List<String> chained = names.stream()
                .filter(name -> name.startsWith("김"))    // 김씨만 필터
                .sorted()                                   // 정렬
                .map(name -> name + " 님")                  // "님" 추가
                .collect(Collectors.toList());
        System.out.println("  결과: " + chained);
        System.out.println();
    }

    // ======================================================
    // 3. 최종 연산 (Terminal Operations)
    // ======================================================
    static void demonstrateTerminalOperations() {
        System.out.println("--- 3. 최종 연산 (Terminal Operations) ---");

        List<Integer> numbers = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        // collect: 결과를 컬렉션으로 수집
        List<Integer> evenList = numbers.stream()
                .filter(n -> n % 2 == 0)
                .collect(Collectors.toList());
        System.out.println("  collect (짝수 리스트): " + evenList);

        // reduce: 요소들을 하나의 값으로 누적
        int sum = numbers.stream()
                .reduce(0, Integer::sum);  // 초기값 0, 누적 함수 sum
        System.out.println("  reduce (합계): " + sum);

        // reduce - 초기값 없이 (Optional 반환)
        Optional<Integer> max = numbers.stream()
                .reduce(Integer::max);
        System.out.println("  reduce (최댓값): " + max.orElse(0));

        // reduce - 문자열 결합
        String joined = List.of("Java", "Stream", "API").stream()
                .reduce("", (a, b) -> a.isEmpty() ? b : a + " " + b);
        System.out.println("  reduce (문자열 결합): " + joined);

        // forEach: 각 요소에 대해 동작 수행
        System.out.print("  forEach: ");
        numbers.stream()
                .filter(n -> n <= 5)
                .forEach(n -> System.out.print(n + " "));
        System.out.println();

        // count: 요소 개수
        long count = numbers.stream()
                .filter(n -> n > 5)
                .count();
        System.out.println("  count (5보다 큰 수): " + count);

        // anyMatch / allMatch / noneMatch
        boolean anyEven = numbers.stream().anyMatch(n -> n % 2 == 0);
        boolean allPositive = numbers.stream().allMatch(n -> n > 0);
        boolean noneNegative = numbers.stream().noneMatch(n -> n < 0);
        System.out.println("  anyMatch (짝수 존재?): " + anyEven);
        System.out.println("  allMatch (모두 양수?): " + allPositive);
        System.out.println("  noneMatch (음수 없음?): " + noneNegative);

        // findFirst / findAny
        Optional<Integer> first = numbers.stream()
                .filter(n -> n > 5)
                .findFirst();
        System.out.println("  findFirst (5보다 큰 첫 번째): " + first.orElse(-1));

        // min / max
        Optional<Integer> minVal = numbers.stream().min(Integer::compareTo);
        Optional<Integer> maxVal = numbers.stream().max(Integer::compareTo);
        System.out.println("  min: " + minVal.orElse(0) + ", max: " + maxVal.orElse(0));

        // toArray: 배열로 변환
        Integer[] array = numbers.stream()
                .filter(n -> n % 2 != 0)
                .toArray(Integer[]::new);
        System.out.println("  toArray (홀수 배열): " + Arrays.toString(array));
        System.out.println();
    }

    // ======================================================
    // 4. Collectors - 다양한 수집 전략
    // ======================================================
    static void demonstrateCollectors() {
        System.out.println("--- 4. Collectors ---");

        List<Student> students = List.of(
                new Student("김철수", "서울", 20, 85),
                new Student("이영희", "부산", 22, 92),
                new Student("박민수", "서울", 21, 78),
                new Student("정수진", "대전", 23, 95),
                new Student("한지민", "부산", 20, 88),
                new Student("최영호", "서울", 22, 91)
        );

        // toList: 리스트로 수집
        List<String> names = students.stream()
                .map(Student::getName)
                .collect(Collectors.toList());
        System.out.println("  toList (이름): " + names);

        // toSet: 셋으로 수집
        Set<String> cities = students.stream()
                .map(Student::getCity)
                .collect(Collectors.toSet());
        System.out.println("  toSet (도시): " + cities);

        // toMap: 맵으로 수집
        Map<String, Integer> nameScoreMap = students.stream()
                .collect(Collectors.toMap(Student::getName, Student::getScore));
        System.out.println("  toMap (이름->점수): " + nameScoreMap);

        // joining: 문자열 결합
        String joinedNames = students.stream()
                .map(Student::getName)
                .collect(Collectors.joining(", ", "[", "]"));
        System.out.println("  joining: " + joinedNames);

        // groupingBy: 그룹핑
        Map<String, List<Student>> byCity = students.stream()
                .collect(Collectors.groupingBy(Student::getCity));
        System.out.println("  groupingBy (도시별):");
        byCity.forEach((city, studs) ->
                System.out.println("    " + city + ": " + studs));

        // groupingBy + counting: 그룹별 개수
        Map<String, Long> countByCity = students.stream()
                .collect(Collectors.groupingBy(Student::getCity, Collectors.counting()));
        System.out.println("  groupingBy + counting: " + countByCity);

        // groupingBy + averagingInt: 그룹별 평균
        Map<String, Double> avgScoreByCity = students.stream()
                .collect(Collectors.groupingBy(
                        Student::getCity,
                        Collectors.averagingInt(Student::getScore)));
        System.out.println("  groupingBy + averaging: " + avgScoreByCity);

        // partitioningBy: 조건에 따라 두 그룹으로 분리
        Map<Boolean, List<Student>> partition = students.stream()
                .collect(Collectors.partitioningBy(s -> s.getScore() >= 90));
        System.out.println("  partitioningBy (90점 이상):");
        System.out.println("    90점 이상: " + partition.get(true));
        System.out.println("    90점 미만: " + partition.get(false));

        // summarizingInt: 통계 정보
        IntSummaryStatistics stats = students.stream()
                .collect(Collectors.summarizingInt(Student::getScore));
        System.out.println("  summarizingInt (점수 통계):");
        System.out.println("    개수: " + stats.getCount());
        System.out.println("    합계: " + stats.getSum());
        System.out.println("    평균: " + String.format("%.2f", stats.getAverage()));
        System.out.println("    최소: " + stats.getMin());
        System.out.println("    최대: " + stats.getMax());

        // collectingAndThen: 수집 후 추가 변환
        List<String> unmodifiableNames = students.stream()
                .map(Student::getName)
                .collect(Collectors.collectingAndThen(
                        Collectors.toList(),
                        Collections::unmodifiableList));
        System.out.println("  collectingAndThen (불변 리스트): " + unmodifiableNames);
        System.out.println();
    }

    // ======================================================
    // 5. Parallel Stream - 병렬 처리
    // ======================================================
    static void demonstrateParallelStream() {
        System.out.println("--- 5. Parallel Stream ---");

        List<Integer> bigList = IntStream.rangeClosed(1, 1_000_000)
                .boxed()
                .collect(Collectors.toList());

        // 순차 스트림 성능 측정
        long startSeq = System.nanoTime();
        long sumSeq = bigList.stream()
                .filter(n -> n % 2 == 0)
                .mapToLong(Integer::longValue)
                .sum();
        long endSeq = System.nanoTime();

        // 병렬 스트림 성능 측정
        long startPar = System.nanoTime();
        long sumPar = bigList.parallelStream()
                .filter(n -> n % 2 == 0)
                .mapToLong(Integer::longValue)
                .sum();
        long endPar = System.nanoTime();

        System.out.println("  [100만 개 짝수 합계]");
        System.out.println("  순차 스트림: " + sumSeq + " (소요: " + (endSeq - startSeq) / 1_000_000 + "ms)");
        System.out.println("  병렬 스트림: " + sumPar + " (소요: " + (endPar - startPar) / 1_000_000 + "ms)");

        // 병렬 스트림에서의 스레드 확인
        System.out.println("  [병렬 스트림 스레드 확인]");
        Set<String> threads = Collections.synchronizedSet(new HashSet<>());
        IntStream.rangeClosed(1, 100)
                .parallel()
                .forEach(n -> threads.add(Thread.currentThread().getName()));
        System.out.println("  사용된 스레드: " + threads);

        // 병렬 스트림 주의사항
        System.out.println("  [주의사항]");
        System.out.println("  - 데이터가 충분히 클 때만 효과적 (작은 데이터에선 오히려 느림)");
        System.out.println("  - 상태 변경(side-effect)이 없는 독립적인 연산에서만 사용");
        System.out.println("  - ArrayList는 병렬 처리에 적합, LinkedList는 부적합");
        System.out.println("  - 순서가 중요한 경우 forEachOrdered() 사용");

        // forEachOrdered: 병렬에서도 순서 보장
        System.out.print("  forEach (순서 보장 안 됨): ");
        List.of(1, 2, 3, 4, 5).parallelStream()
                .forEach(n -> System.out.print(n + " "));
        System.out.println();

        System.out.print("  forEachOrdered (순서 보장): ");
        List.of(1, 2, 3, 4, 5).parallelStream()
                .forEachOrdered(n -> System.out.print(n + " "));
        System.out.println("\n");
    }
}
