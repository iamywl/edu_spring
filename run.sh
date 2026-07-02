#!/bin/bash
# ============================================================
#  Java & CS 교육 - 예제 실행기 (개념당 스크립트 1개)
#  - 대화형: 챕터(카테고리) 선택 → 개념 선택
#  - 비대화형: ./run.sh <클래스이름>  (예: ./run.sh SortingAlgorithms)
#             ./run.sh all            (전체 실행)
# ============================================================

BLUE='\033[1;34m'
GREEN='\033[1;32m'
YELLOW='\033[1;33m'
CYAN='\033[1;36m'
RED='\033[1;31m'
NC='\033[0m'
BOLD='\033[1m'

# 컴파일 출력 위치 (컨테이너: /app/out, 로컬 테스트: OUT_DIR 환경변수로 오버라이드)
OUT="${OUT_DIR:-/app/out}"
SRC="${SRC_DIR:-/app/src}"

# 소스가 있으면 자동 컴파일 (볼륨 마운트 환경 지원)
if [ -d "$SRC" ] && { [ ! -d "$OUT" ] || [ -n "$(find "$SRC" -name '*.java' -newer "$OUT" 2>/dev/null | head -1)" ]; }; then
    mkdir -p "$OUT"
    SOURCES=$(find "$SRC" -name "*.java" 2>/dev/null)
    if [ -n "$SOURCES" ]; then
        javac -d "$OUT" $SOURCES 2>/dev/null
    fi
fi

# ------------------------------------------------------------
#  개념 목록: 카테고리별 "FQCN|한글 설명"
#  "#텍스트" 로 시작하면 실행 항목이 아니라 소제목(구분선)으로 표시된다.
# ------------------------------------------------------------
CH01=(
  "com.edu.basics.VariablesAndTypes|변수와 데이터 타입, 연산자"
  "com.edu.basics.ControlFlow|제어문 (if / switch / 반복문)"
  "com.edu.basics.ArraysAndMethods|배열과 메서드, 재귀"
  "com.edu.basics.ExceptionBasics|예외처리 기초"
  "com.edu.basics.WrappingAndBoxing|래퍼 클래스와 오토박싱"
  "com.edu.basics.DateTimeExample|날짜와 시간 API (java.time)"
  "com.edu.basics.FileIoExample|파일 입출력 기초"
  "#--- 심화: 밑단이 어떻게 동작하는가 ---"
  "com.edu.basics.FloatingPointBits|[심화] 부동소수점 비트 (IEEE-754)"
  "com.edu.basics.TwosComplement|[심화] 2의 보수와 오버플로우"
  "com.edu.basics.PassByValue|[심화] 값에 의한 전달(pass-by-value)"
)

CH02=(
  "com.edu.oop.InheritanceExample|상속 (extends, super, 오버라이딩)"
  "com.edu.oop.PolymorphismExample|다형성 (업캐스팅, 동적 디스패치)"
  "com.edu.oop.EncapsulationExample|캡슐화 (private + getter/setter)"
  "com.edu.oop.ObjectMethodsExample|toString / equals / hashCode"
  "com.edu.oop.PatternMatchingExample|instanceof / switch 패턴 매칭"
  "com.edu.oop.InterfaceExample|인터페이스 (default/static 메서드)"
  "com.edu.oop.EnumExample|enum (값을 가진 열거형)"
  "com.edu.oop.RecordExample|record (불변 데이터 클래스)"
  "com.edu.oop.SealedClassExample|sealed class (봉인 클래스)"
  "com.edu.oop.NestedClassExample|중첩/내부/익명 클래스"
)

CH03=(
  "#--- 컬렉션 ---"
  "com.edu.collections.ListExample|List (ArrayList/LinkedList)"
  "com.edu.collections.SetExample|Set (Hash/Linked/Tree)"
  "com.edu.collections.MapExample|Map (Hash/Linked/Tree)"
  "com.edu.collections.QueueExample|Queue / Deque / PriorityQueue"
  "com.edu.collections.CollectionsUtilExample|Collections 유틸리티"
  "#--- 제네릭 ---"
  "com.edu.collections.GenericClassExample|제네릭 클래스 / 타입 안전성"
  "com.edu.collections.GenericMethodExample|제네릭 메서드 / 타입 추론"
  "com.edu.collections.BoundedTypeExample|상한 경계 <T extends ...>"
  "com.edu.collections.WildcardExample|와일드카드 / PECS"
  "#--- 람다와 함수형 ---"
  "com.edu.collections.LambdaBasicsExample|람다 기초 (익명클래스→람다)"
  "com.edu.collections.FunctionalInterfaceExample|함수형 인터페이스 (Predicate 등)"
  "com.edu.collections.MethodReferenceExample|메서드 참조 4종"
  "com.edu.collections.OptionalExample|Optional (null 안전 처리)"
  "com.edu.collections.LambdaPracticalExample|람다 실전 (Comparator/조합)"
  "#--- 스트림 ---"
  "com.edu.collections.StreamCreationExample|스트림 생성"
  "com.edu.collections.StreamIntermediateExample|중간 연산 (지연 평가)"
  "com.edu.collections.StreamTerminalExample|종단 연산"
  "com.edu.collections.CollectorsExample|Collectors (groupingBy 등)"
  "com.edu.collections.ParallelStreamExample|병렬 스트림"
  "#--- 심화 ---"
  "com.edu.collections.ComparableComparatorExample|Comparable / Comparator 정렬"
  "com.edu.collections.EqualsHashCodeExample|equals/hashCode 계약"
  "com.edu.collections.BigOTiming|[심화] Big-O 실측"
  "com.edu.collections.HashMapInternals|[심화] HashMap 내부 동작"
  "com.edu.collections.TypeErasureDemo|[심화] 제네릭 타입 소거"
)

ALGO=(
  "com.edu.algorithms.SearchAlgorithms|탐색: 선형/이진 (비교 횟수 실측)"
  "com.edu.algorithms.SortingAlgorithms|정렬 5종 + 성능 측정"
  "com.edu.algorithms.RecursionAndDP|재귀와 동적계획법(DP)"
  "com.edu.algorithms.DataStructuresFromScratch|자료구조 직접 구현"
  "com.edu.algorithms.TreeAndHeap|트리(BST)와 힙"
  "com.edu.algorithms.GraphAlgorithms|그래프 BFS/DFS/다익스트라"
)

CONC=(
  "com.edu.concurrency.RaceConditionDemo|경쟁 상태 (잃어버린 갱신)"
  "com.edu.concurrency.SynchronizationDemo|동기화 3종 + volatile 반례"
  "com.edu.concurrency.DeadlockDemo|교착 상태 유발/해결"
  "com.edu.concurrency.ExecutorAndFutures|스레드풀과 Future"
  "com.edu.concurrency.ProducerConsumer|생산자-소비자 (BlockingQueue)"
)

NET=(
  "com.edu.network.RawHttpClient|원시 HTTP 소켓 (TCP 위의 텍스트, 인터넷 필요)"
)

CATEGORY_KEYS=(CH01 CH02 CH03 ALGO CONC NET)
CATEGORY_TITLES=(
  "Chapter 01 - Java 기초"
  "Chapter 02 - 객체지향 프로그래밍(OOP)"
  "Chapter 03 - 컬렉션·제네릭·함수형·스트림"
  "CS 기반 - 자료구조와 알고리즘"
  "CS 기반 - 운영체제와 동시성"
  "CS 기반 - 컴퓨터 네트워크"
)

run_class() {
    local class_name=$1
    local title=$2
    echo ""
    echo -e "${CYAN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
    echo -e "${BOLD}  실행: ${title}${NC}   ${CYAN}(${class_name})${NC}"
    echo -e "${CYAN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
    echo ""
    java -cp "$OUT" "$class_name"
    echo ""
    echo -e "${CYAN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
}

wait_for_enter() {
    echo ""
    echo -e "${YELLOW}Enter 키를 눌러 메뉴로 돌아가세요...${NC}"
    read -r
}

# 단순 클래스 이름(예: SortingAlgorithms) 또는 FQCN으로 실행
run_by_name() {
    local arg="$1"
    if [[ "$arg" == *.* ]]; then
        run_class "$arg" "$arg"
        return
    fi
    local found
    found=$( (cd "$OUT" 2>/dev/null && find . -name "${arg}.class" 2>/dev/null | head -1) )
    if [ -n "$found" ]; then
        found=${found#./}; found=${found%.class}; found=${found//\//.}
        run_class "$found" "$arg"
    else
        echo -e "${RED}클래스를 찾을 수 없습니다: ${arg}${NC}"
        echo -e "예) ./run.sh SortingAlgorithms  또는  ./run.sh com.edu.algorithms.SortingAlgorithms"
    fi
}

run_all() {
    echo -e "\n${BOLD}=== 전체 예제 실행 ===${NC}\n"
    local key entry class desc
    for key in CH01 CH02 CH03 ALGO CONC; do   # NET(RawHttpClient)은 인터넷 필요하여 제외
        eval "local items=(\"\${${key}[@]}\")"
        for entry in "${items[@]}"; do
            [[ "$entry" == \#* ]] && continue
            class="${entry%%|*}"; desc="${entry##*|}"
            run_class "$class" "$desc"
        done
    done
}

# 특정 카테고리의 서브메뉴
run_category() {
    local key="$1" title="$2"
    eval "local items=(\"\${${key}[@]}\")"
    while true; do
        clear
        echo -e "${BLUE}╔════════════════════════════════════════════════════════════╗${NC}"
        echo -e "${BLUE}║${NC} ${BOLD}${title}${NC}"
        echo -e "${BLUE}╚════════════════════════════════════════════════════════════╝${NC}"
        echo ""
        local i=0 entry class desc
        # 표시용 번호 매핑 배열
        local map=()
        for entry in "${items[@]}"; do
            if [[ "$entry" == \#* ]]; then
                echo -e "  ${YELLOW}${entry#\#}${NC}"
            else
                i=$((i+1))
                class="${entry%%|*}"; desc="${entry##*|}"
                map[$i]="$class|$desc"
                printf "   ${GREEN}%2d)${NC} %s\n" "$i" "$desc"
            fi
        done
        echo ""
        echo -e "   ${CYAN}b) 뒤로${NC}    ${RED}q) 종료${NC}"
        echo ""
        echo -ne "${GREEN}선택> ${NC}"
        read -r sel
        case "$sel" in
            b|B) return ;;
            q|Q) echo -e "\n${GREEN}학습을 마칩니다. 수고하셨습니다!${NC}\n"; exit 0 ;;
            ''|*[!0-9]*) echo -e "${RED}숫자를 입력하세요.${NC}"; sleep 1 ;;
            *)
                if [ -n "${map[$sel]}" ]; then
                    class="${map[$sel]%%|*}"; desc="${map[$sel]##*|}"
                    run_class "$class" "$desc"; wait_for_enter
                else
                    echo -e "${RED}잘못된 번호입니다.${NC}"; sleep 1
                fi
                ;;
        esac
    done
}

show_main_menu() {
    clear
    echo -e "${BLUE}╔════════════════════════════════════════════════════════════╗${NC}"
    echo -e "${BLUE}║${NC}  ${BOLD}Java & CS 교육 프로그램${NC}  ─ 개념당 스크립트 1개"
    echo -e "${BLUE}║${NC}  학습할 챕터(카테고리)를 선택하세요"
    echo -e "${BLUE}╠════════════════════════════════════════════════════════════╣${NC}"
    local n=0
    for t in "${CATEGORY_TITLES[@]}"; do
        n=$((n+1))
        printf "${BLUE}║${NC}   ${GREEN}%d)${NC} %s\n" "$n" "$t"
    done
    echo -e "${BLUE}║${NC}"
    echo -e "${BLUE}║${NC}   ${CYAN}a) 전체 실행 (네트워크 예제 제외)${NC}"
    echo -e "${BLUE}║${NC}   ${RED}q) 종료${NC}"
    echo -e "${BLUE}╚════════════════════════════════════════════════════════════╝${NC}"
    echo ""
    echo -e "  ${CYAN}팁:${NC} 터미널에서 바로 실행도 가능 → ${GREEN}./run.sh SortingAlgorithms${NC}"
    echo ""
}

# ------------------------------------------------------------
#  비대화형 모드: 인자가 있으면 바로 실행
# ------------------------------------------------------------
if [ $# -gt 0 ]; then
    case "$1" in
        all|ALL) run_all ;;
        *) run_by_name "$1" ;;
    esac
    exit 0
fi

# ------------------------------------------------------------
#  대화형 모드: 카테고리 → 개념
# ------------------------------------------------------------
while true; do
    show_main_menu
    echo -ne "${GREEN}선택> ${NC}"
    read -r choice
    case "$choice" in
        a|A) run_all; wait_for_enter ;;
        q|Q) echo -e "\n${GREEN}학습을 마칩니다. 수고하셨습니다!${NC}\n"; exit 0 ;;
        ''|*[!0-9]*) echo -e "${RED}숫자를 입력하세요.${NC}"; sleep 1 ;;
        *)
            idx=$((choice))
            if [ "$idx" -ge 1 ] && [ "$idx" -le "${#CATEGORY_KEYS[@]}" ]; then
                run_category "${CATEGORY_KEYS[$((idx-1))]}" "${CATEGORY_TITLES[$((idx-1))]}"
            else
                echo -e "${RED}잘못된 선택입니다.${NC}"; sleep 1
            fi
            ;;
    esac
done
