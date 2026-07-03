#!/bin/bash
# ============================================================
#  소스 컴파일 스크립트
#  코드를 수정한 후 이 스크립트를 실행하면 다시 컴파일됩니다.
# ============================================================

GREEN='\033[1;32m'
RED='\033[1;31m'
YELLOW='\033[1;33m'
NC='\033[0m'

echo -e "${YELLOW}컴파일 중...${NC}"

mkdir -p /app/out

# 모든 Java 소스 찾아서 컴파일 (java/ 전체가 /app에 마운트 → 각 chapter*/src 아래)
SOURCES=$(find /app/chapter* -name "*.java" 2>/dev/null)

if [ -z "$SOURCES" ]; then
    echo -e "${RED}소스 파일을 찾을 수 없습니다. /app/chapter* 디렉토리를 확인하세요.${NC}"
    exit 1
fi

if javac -d /app/out $SOURCES 2>&1; then
    echo -e "${GREEN}컴파일 성공!${NC}"
    echo ""
    echo -e "실행 방법:"
    echo -e "  ${GREEN}./run.sh${NC}                    # 대화형 메뉴 (카테고리 → 개념 선택)"
    echo -e "  ${GREEN}./run.sh SortingAlgorithms${NC}  # 개념 이름으로 직접 실행"
    echo -e "  ${GREEN}./run.sh all${NC}                # 전체 실행"
else
    echo -e "${RED}컴파일 실패! 위의 오류를 확인하세요.${NC}"
    exit 1
fi
