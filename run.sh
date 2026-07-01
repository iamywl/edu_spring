#!/bin/bash
# ============================================================
#  Java & Spring Boot 교육 - 예제 실행기
#  하나의 컨테이너에서 모든 Java 예제를 실행할 수 있습니다.
# ============================================================

BLUE='\033[1;34m'
GREEN='\033[1;32m'
YELLOW='\033[1;33m'
CYAN='\033[1;36m'

# 소스가 있으면 자동 컴파일 (볼륨 마운트 환경 지원)
if [ -d /app/src ] && [ ! -d /app/out ] || [ "$(find /app/src -name '*.java' -newer /app/out 2>/dev/null | head -1)" ]; then
    mkdir -p /app/out
    SOURCES=$(find /app/src -name "*.java" 2>/dev/null)
    if [ -n "$SOURCES" ]; then
        javac -d /app/out $SOURCES 2>/dev/null
    fi
fi
RED='\033[1;31m'
NC='\033[0m' # No Color
BOLD='\033[1m'

show_menu() {
    clear
    echo -e "${BLUE}╔════════════════════════════════════════════════════════════╗${NC}"
    echo -e "${BLUE}║${NC}  ${BOLD}Java & Spring Boot 교육 프로그램${NC}                          ${BLUE}║${NC}"
    echo -e "${BLUE}║${NC}  예제 코드를 선택하여 실행하세요                          ${BLUE}║${NC}"
    echo -e "${BLUE}╠════════════════════════════════════════════════════════════╣${NC}"
    echo -e "${BLUE}║${NC}                                                            ${BLUE}║${NC}"
    echo -e "${BLUE}║${NC}  ${YELLOW}[ Chapter 01: Java 기초 ]${NC}                                 ${BLUE}║${NC}"
    echo -e "${BLUE}║${NC}    ${GREEN}1)${NC} 변수와 데이터 타입, 연산자  (VariablesAndTypes)       ${BLUE}║${NC}"
    echo -e "${BLUE}║${NC}    ${GREEN}2)${NC} 제어문 - if, switch, 반복문 (ControlFlow)             ${BLUE}║${NC}"
    echo -e "${BLUE}║${NC}    ${GREEN}3)${NC} 배열과 메서드, 재귀        (ArraysAndMethods)         ${BLUE}║${NC}"
    echo -e "${BLUE}║${NC}    ${GREEN}4)${NC} 예외처리 기초              (ExceptionBasics)           ${BLUE}║${NC}"
    echo -e "${BLUE}║${NC}   ${GREEN}10)${NC} 래퍼 클래스/박싱            (WrappingAndBoxing)         ${BLUE}║${NC}"
    echo -e "${BLUE}║${NC}   ${GREEN}11)${NC} 날짜와 시간 API            (DateTimeExample)           ${BLUE}║${NC}"
    echo -e "${BLUE}║${NC}   ${GREEN}12)${NC} 파일 입출력 기초            (FileIoExample)             ${BLUE}║${NC}"
    echo -e "${BLUE}║${NC}   ${GREEN}16)${NC} [심화] 부동소수점 비트      (FloatingPointBits)         ${BLUE}║${NC}"
    echo -e "${BLUE}║${NC}   ${GREEN}17)${NC} [심화] 2의 보수/오버플로우  (TwosComplement)            ${BLUE}║${NC}"
    echo -e "${BLUE}║${NC}   ${GREEN}18)${NC} [심화] 값에 의한 전달       (PassByValue)               ${BLUE}║${NC}"
    echo -e "${BLUE}║${NC}                                                            ${BLUE}║${NC}"
    echo -e "${BLUE}║${NC}  ${YELLOW}[ Chapter 02: 객체지향 프로그래밍 (OOP) ]${NC}                  ${BLUE}║${NC}"
    echo -e "${BLUE}║${NC}    ${GREEN}5)${NC} OOP 종합 데모              (OopMain)                   ${BLUE}║${NC}"
    echo -e "${BLUE}║${NC}       클래스/상속/다형성/인터페이스/enum/record/sealed       ${BLUE}║${NC}"
    echo -e "${BLUE}║${NC}   ${GREEN}13)${NC} 중첩 클래스                (NestedClassExample)        ${BLUE}║${NC}"
    echo -e "${BLUE}║${NC}                                                            ${BLUE}║${NC}"
    echo -e "${BLUE}║${NC}  ${YELLOW}[ Chapter 03: 컬렉션과 함수형 프로그래밍 ]${NC}                 ${BLUE}║${NC}"
    echo -e "${BLUE}║${NC}    ${GREEN}6)${NC} 컬렉션 프레임워크          (CollectionExample)         ${BLUE}║${NC}"
    echo -e "${BLUE}║${NC}    ${GREEN}7)${NC} 제네릭                     (GenericExample)             ${BLUE}║${NC}"
    echo -e "${BLUE}║${NC}    ${GREEN}8)${NC} Stream API                 (StreamExample)              ${BLUE}║${NC}"
    echo -e "${BLUE}║${NC}    ${GREEN}9)${NC} 람다와 함수형 인터페이스    (LambdaExample)             ${BLUE}║${NC}"
    echo -e "${BLUE}║${NC}   ${GREEN}14)${NC} Comparable/Comparator      (ComparableComparatorExample)${BLUE}║${NC}"
    echo -e "${BLUE}║${NC}   ${GREEN}15)${NC} equals/hashCode 계약       (EqualsHashCodeExample)     ${BLUE}║${NC}"
    echo -e "${BLUE}║${NC}   ${GREEN}19)${NC} [심화] Big-O 측정           (BigOTiming)                ${BLUE}║${NC}"
    echo -e "${BLUE}║${NC}   ${GREEN}20)${NC} [심화] HashMap 내부 동작    (HashMapInternals)          ${BLUE}║${NC}"
    echo -e "${BLUE}║${NC}   ${GREEN}21)${NC} [심화] 제네릭 타입 소거     (TypeErasureDemo)           ${BLUE}║${NC}"
    echo -e "${BLUE}║${NC}                                                            ${BLUE}║${NC}"
    echo -e "${BLUE}║${NC}  ${YELLOW}[ CS 기반: 자료구조와 알고리즘 ]${NC}                          ${BLUE}║${NC}"
    echo -e "${BLUE}║${NC}   ${GREEN}22)${NC} 탐색: 선형/이진            (SearchAlgorithms)          ${BLUE}║${NC}"
    echo -e "${BLUE}║${NC}   ${GREEN}23)${NC} 정렬 5종 + 측정            (SortingAlgorithms)         ${BLUE}║${NC}"
    echo -e "${BLUE}║${NC}   ${GREEN}24)${NC} 재귀와 동적계획법(DP)      (RecursionAndDP)            ${BLUE}║${NC}"
    echo -e "${BLUE}║${NC}   ${GREEN}25)${NC} 자료구조 직접 구현         (DataStructuresFromScratch) ${BLUE}║${NC}"
    echo -e "${BLUE}║${NC}   ${GREEN}26)${NC} 트리와 힙                  (TreeAndHeap)               ${BLUE}║${NC}"
    echo -e "${BLUE}║${NC}   ${GREEN}27)${NC} 그래프 BFS/DFS/다익스트라  (GraphAlgorithms)           ${BLUE}║${NC}"
    echo -e "${BLUE}║${NC}                                                            ${BLUE}║${NC}"
    echo -e "${BLUE}║${NC}  ${YELLOW}[ CS 기반: 운영체제와 동시성 ]${NC}                            ${BLUE}║${NC}"
    echo -e "${BLUE}║${NC}   ${GREEN}28)${NC} 경쟁 상태(잃어버린 갱신)   (RaceConditionDemo)         ${BLUE}║${NC}"
    echo -e "${BLUE}║${NC}   ${GREEN}29)${NC} 동기화 3종+volatile 반례   (SynchronizationDemo)       ${BLUE}║${NC}"
    echo -e "${BLUE}║${NC}   ${GREEN}30)${NC} 교착 상태 유발/해결        (DeadlockDemo)              ${BLUE}║${NC}"
    echo -e "${BLUE}║${NC}   ${GREEN}31)${NC} 스레드풀과 Future          (ExecutorAndFutures)        ${BLUE}║${NC}"
    echo -e "${BLUE}║${NC}   ${GREEN}32)${NC} 생산자-소비자              (ProducerConsumer)          ${BLUE}║${NC}"
    echo -e "${BLUE}║${NC}                                                            ${BLUE}║${NC}"
    echo -e "${BLUE}║${NC}  ${YELLOW}[ CS 기반: 네트워크 ]${NC}                                     ${BLUE}║${NC}"
    echo -e "${BLUE}║${NC}   ${GREEN}33)${NC} 원시 HTTP 소켓(TCP 위 텍스트) (RawHttpClient)           ${BLUE}║${NC}"
    echo -e "${BLUE}║${NC}   ${CYAN}DB이론=chapter-cs-database/labs/*.sql (SQL 랩)${NC}            ${BLUE}║${NC}"
    echo -e "${BLUE}║${NC}   ${CYAN}네트워크 관찰=chapter-cs-network/labs/observe_http.sh${NC}     ${BLUE}║${NC}"
    echo -e "${BLUE}║${NC}                                                            ${BLUE}║${NC}"
    echo -e "${BLUE}║${NC}  ${CYAN}  a) 전체 실행 (Ch01~03 + CS 알고리즘/동시성)${NC}            ${BLUE}║${NC}"
    echo -e "${BLUE}║${NC}  ${RED}  q) 종료${NC}                                                 ${BLUE}║${NC}"
    echo -e "${BLUE}║${NC}                                                            ${BLUE}║${NC}"
    echo -e "${BLUE}╚════════════════════════════════════════════════════════════╝${NC}"
    echo ""
}

run_class() {
    local class_name=$1
    local title=$2
    echo ""
    echo -e "${CYAN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
    echo -e "${BOLD}  실행: ${title}${NC}"
    echo -e "${CYAN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
    echo ""
    java -cp /app/out "$class_name"
    echo ""
    echo -e "${CYAN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
}

wait_for_enter() {
    echo ""
    echo -e "${YELLOW}Enter 키를 눌러 메뉴로 돌아가세요...${NC}"
    read -r
}

run_all() {
    echo -e "\n${BOLD}=== 전체 예제 실행 ===${NC}\n"
    run_class "com.edu.basics.VariablesAndTypes" "Ch01 - 변수와 데이터 타입"
    run_class "com.edu.basics.ControlFlow" "Ch01 - 제어문"
    run_class "com.edu.basics.ArraysAndMethods" "Ch01 - 배열과 메서드"
    run_class "com.edu.basics.ExceptionBasics" "Ch01 - 예외처리"
    run_class "com.edu.oop.OopMain" "Ch02 - OOP 종합"
    run_class "com.edu.collections.CollectionExample" "Ch03 - 컬렉션"
    run_class "com.edu.collections.GenericExample" "Ch03 - 제네릭"
    run_class "com.edu.collections.StreamExample" "Ch03 - Stream API"
    run_class "com.edu.collections.LambdaExample" "Ch03 - 람다"
    run_class "com.edu.basics.WrappingAndBoxing" "Ch01 - 래퍼 클래스/박싱"
    run_class "com.edu.basics.DateTimeExample" "Ch01 - 날짜와 시간 API"
    run_class "com.edu.basics.FileIoExample" "Ch01 - 파일 입출력"
    run_class "com.edu.oop.NestedClassExample" "Ch02 - 중첩 클래스"
    run_class "com.edu.collections.ComparableComparatorExample" "Ch03 - Comparable/Comparator"
    run_class "com.edu.collections.EqualsHashCodeExample" "Ch03 - equals/hashCode"
    run_class "com.edu.basics.FloatingPointBits" "Ch01 심화 - 부동소수점 비트"
    run_class "com.edu.basics.TwosComplement" "Ch01 심화 - 2의 보수/오버플로우"
    run_class "com.edu.basics.PassByValue" "Ch01 심화 - 값에 의한 전달"
    run_class "com.edu.collections.BigOTiming" "Ch03 심화 - Big-O 측정"
    run_class "com.edu.collections.HashMapInternals" "Ch03 심화 - HashMap 내부 동작"
    run_class "com.edu.collections.TypeErasureDemo" "Ch03 심화 - 제네릭 타입 소거"
    # CS 기반: 자료구조와 알고리즘
    run_class "com.edu.algorithms.SearchAlgorithms" "CS 알고리즘 - 탐색"
    run_class "com.edu.algorithms.SortingAlgorithms" "CS 알고리즘 - 정렬"
    run_class "com.edu.algorithms.RecursionAndDP" "CS 알고리즘 - 재귀와 DP"
    run_class "com.edu.algorithms.DataStructuresFromScratch" "CS 알고리즘 - 자료구조"
    run_class "com.edu.algorithms.TreeAndHeap" "CS 알고리즘 - 트리와 힙"
    run_class "com.edu.algorithms.GraphAlgorithms" "CS 알고리즘 - 그래프"
    # CS 기반: 운영체제와 동시성
    run_class "com.edu.concurrency.RaceConditionDemo" "CS 동시성 - 경쟁 상태"
    run_class "com.edu.concurrency.SynchronizationDemo" "CS 동시성 - 동기화"
    run_class "com.edu.concurrency.DeadlockDemo" "CS 동시성 - 교착 상태"
    run_class "com.edu.concurrency.ExecutorAndFutures" "CS 동시성 - 스레드풀"
    run_class "com.edu.concurrency.ProducerConsumer" "CS 동시성 - 생산자-소비자"
    # 네트워크 RawHttpClient는 인터넷 연결이 필요하여 전체 실행에서 제외 (개별 33번으로 실행)
}

# 인자가 있으면 바로 실행 (비대화형 모드)
if [ $# -gt 0 ]; then
    case "$1" in
        1) run_class "com.edu.basics.VariablesAndTypes" "Ch01 - 변수와 데이터 타입" ;;
        2) run_class "com.edu.basics.ControlFlow" "Ch01 - 제어문" ;;
        3) run_class "com.edu.basics.ArraysAndMethods" "Ch01 - 배열과 메서드" ;;
        4) run_class "com.edu.basics.ExceptionBasics" "Ch01 - 예외처리" ;;
        5) run_class "com.edu.oop.OopMain" "Ch02 - OOP 종합" ;;
        6) run_class "com.edu.collections.CollectionExample" "Ch03 - 컬렉션" ;;
        7) run_class "com.edu.collections.GenericExample" "Ch03 - 제네릭" ;;
        8) run_class "com.edu.collections.StreamExample" "Ch03 - Stream API" ;;
        9) run_class "com.edu.collections.LambdaExample" "Ch03 - 람다" ;;
        10) run_class "com.edu.basics.WrappingAndBoxing" "Ch01 - 래퍼 클래스/박싱" ;;
        11) run_class "com.edu.basics.DateTimeExample" "Ch01 - 날짜와 시간 API" ;;
        12) run_class "com.edu.basics.FileIoExample" "Ch01 - 파일 입출력" ;;
        13) run_class "com.edu.oop.NestedClassExample" "Ch02 - 중첩 클래스" ;;
        14) run_class "com.edu.collections.ComparableComparatorExample" "Ch03 - Comparable/Comparator" ;;
        15) run_class "com.edu.collections.EqualsHashCodeExample" "Ch03 - equals/hashCode" ;;
        16) run_class "com.edu.basics.FloatingPointBits" "Ch01 심화 - 부동소수점 비트" ;;
        17) run_class "com.edu.basics.TwosComplement" "Ch01 심화 - 2의 보수/오버플로우" ;;
        18) run_class "com.edu.basics.PassByValue" "Ch01 심화 - 값에 의한 전달" ;;
        19) run_class "com.edu.collections.BigOTiming" "Ch03 심화 - Big-O 측정" ;;
        20) run_class "com.edu.collections.HashMapInternals" "Ch03 심화 - HashMap 내부 동작" ;;
        21) run_class "com.edu.collections.TypeErasureDemo" "Ch03 심화 - 제네릭 타입 소거" ;;
        22) run_class "com.edu.algorithms.SearchAlgorithms" "CS 알고리즘 - 탐색(선형/이진)" ;;
        23) run_class "com.edu.algorithms.SortingAlgorithms" "CS 알고리즘 - 정렬 5종" ;;
        24) run_class "com.edu.algorithms.RecursionAndDP" "CS 알고리즘 - 재귀와 DP" ;;
        25) run_class "com.edu.algorithms.DataStructuresFromScratch" "CS 알고리즘 - 자료구조 직접 구현" ;;
        26) run_class "com.edu.algorithms.TreeAndHeap" "CS 알고리즘 - 트리와 힙" ;;
        27) run_class "com.edu.algorithms.GraphAlgorithms" "CS 알고리즘 - 그래프(BFS/DFS/다익스트라)" ;;
        28) run_class "com.edu.concurrency.RaceConditionDemo" "CS 동시성 - 경쟁 상태" ;;
        29) run_class "com.edu.concurrency.SynchronizationDemo" "CS 동시성 - 동기화 3종" ;;
        30) run_class "com.edu.concurrency.DeadlockDemo" "CS 동시성 - 교착 상태" ;;
        31) run_class "com.edu.concurrency.ExecutorAndFutures" "CS 동시성 - 스레드풀과 Future" ;;
        32) run_class "com.edu.concurrency.ProducerConsumer" "CS 동시성 - 생산자-소비자" ;;
        33) run_class "com.edu.network.RawHttpClient" "CS 네트워크 - 원시 HTTP 소켓" ;;
        all) run_all ;;
        *) java -cp /app/out "$1" ;;
    esac
    exit 0
fi

# 대화형 메뉴 모드
while true; do
    show_menu
    echo -ne "${GREEN}선택> ${NC}"
    read -r choice

    case "$choice" in
        1) run_class "com.edu.basics.VariablesAndTypes" "Ch01 - 변수와 데이터 타입"; wait_for_enter ;;
        2) run_class "com.edu.basics.ControlFlow" "Ch01 - 제어문"; wait_for_enter ;;
        3) run_class "com.edu.basics.ArraysAndMethods" "Ch01 - 배열과 메서드"; wait_for_enter ;;
        4) run_class "com.edu.basics.ExceptionBasics" "Ch01 - 예외처리"; wait_for_enter ;;
        5) run_class "com.edu.oop.OopMain" "Ch02 - OOP 종합"; wait_for_enter ;;
        6) run_class "com.edu.collections.CollectionExample" "Ch03 - 컬렉션"; wait_for_enter ;;
        7) run_class "com.edu.collections.GenericExample" "Ch03 - 제네릭"; wait_for_enter ;;
        8) run_class "com.edu.collections.StreamExample" "Ch03 - Stream API"; wait_for_enter ;;
        9) run_class "com.edu.collections.LambdaExample" "Ch03 - 람다"; wait_for_enter ;;
        10) run_class "com.edu.basics.WrappingAndBoxing" "Ch01 - 래퍼 클래스/박싱"; wait_for_enter ;;
        11) run_class "com.edu.basics.DateTimeExample" "Ch01 - 날짜와 시간 API"; wait_for_enter ;;
        12) run_class "com.edu.basics.FileIoExample" "Ch01 - 파일 입출력"; wait_for_enter ;;
        13) run_class "com.edu.oop.NestedClassExample" "Ch02 - 중첩 클래스"; wait_for_enter ;;
        14) run_class "com.edu.collections.ComparableComparatorExample" "Ch03 - Comparable/Comparator"; wait_for_enter ;;
        15) run_class "com.edu.collections.EqualsHashCodeExample" "Ch03 - equals/hashCode"; wait_for_enter ;;
        16) run_class "com.edu.basics.FloatingPointBits" "Ch01 심화 - 부동소수점 비트"; wait_for_enter ;;
        17) run_class "com.edu.basics.TwosComplement" "Ch01 심화 - 2의 보수/오버플로우"; wait_for_enter ;;
        18) run_class "com.edu.basics.PassByValue" "Ch01 심화 - 값에 의한 전달"; wait_for_enter ;;
        19) run_class "com.edu.collections.BigOTiming" "Ch03 심화 - Big-O 측정"; wait_for_enter ;;
        20) run_class "com.edu.collections.HashMapInternals" "Ch03 심화 - HashMap 내부 동작"; wait_for_enter ;;
        21) run_class "com.edu.collections.TypeErasureDemo" "Ch03 심화 - 제네릭 타입 소거"; wait_for_enter ;;
        22) run_class "com.edu.algorithms.SearchAlgorithms" "CS 알고리즘 - 탐색(선형/이진)"; wait_for_enter ;;
        23) run_class "com.edu.algorithms.SortingAlgorithms" "CS 알고리즘 - 정렬 5종"; wait_for_enter ;;
        24) run_class "com.edu.algorithms.RecursionAndDP" "CS 알고리즘 - 재귀와 DP"; wait_for_enter ;;
        25) run_class "com.edu.algorithms.DataStructuresFromScratch" "CS 알고리즘 - 자료구조 직접 구현"; wait_for_enter ;;
        26) run_class "com.edu.algorithms.TreeAndHeap" "CS 알고리즘 - 트리와 힙"; wait_for_enter ;;
        27) run_class "com.edu.algorithms.GraphAlgorithms" "CS 알고리즘 - 그래프(BFS/DFS/다익스트라)"; wait_for_enter ;;
        28) run_class "com.edu.concurrency.RaceConditionDemo" "CS 동시성 - 경쟁 상태"; wait_for_enter ;;
        29) run_class "com.edu.concurrency.SynchronizationDemo" "CS 동시성 - 동기화 3종"; wait_for_enter ;;
        30) run_class "com.edu.concurrency.DeadlockDemo" "CS 동시성 - 교착 상태"; wait_for_enter ;;
        31) run_class "com.edu.concurrency.ExecutorAndFutures" "CS 동시성 - 스레드풀과 Future"; wait_for_enter ;;
        32) run_class "com.edu.concurrency.ProducerConsumer" "CS 동시성 - 생산자-소비자"; wait_for_enter ;;
        33) run_class "com.edu.network.RawHttpClient" "CS 네트워크 - 원시 HTTP 소켓"; wait_for_enter ;;
        a|A) run_all; wait_for_enter ;;
        q|Q)
            echo -e "\n${GREEN}학습을 마칩니다. 수고하셨습니다!${NC}\n"
            exit 0
            ;;
        *)
            echo -e "\n${RED}잘못된 선택입니다. 다시 선택해주세요.${NC}"
            sleep 1
            ;;
    esac
done
