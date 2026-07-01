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
    echo -e "${BLUE}║${NC}                                                            ${BLUE}║${NC}"
    echo -e "${BLUE}║${NC}  ${CYAN}  a) 전체 실행 (Chapter 01 ~ 03 순서대로)${NC}                ${BLUE}║${NC}"
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
