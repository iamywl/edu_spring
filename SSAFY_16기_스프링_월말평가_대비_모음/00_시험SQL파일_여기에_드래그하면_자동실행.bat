@echo off
setlocal

if "%~1"=="" (
    echo ====================================================================
    echo   [사용 방법] 이 파일은 그냥 더블클릭하지 마세요!
    echo.
    echo   시험 문제 폴더 안의 .sql 파일
    echo   예: src\main\resources\scripts\xxx.sql
    echo   을 마우스로 끌어다가 이 파일 아이콘 위에 딱 놓으세요.
    echo ====================================================================
    echo.
    pause
    exit /b 1
)

echo ====================================================================
echo   SQL 파일 자동 실행 중: %~nx1
echo   MySQL Workbench 안 켜도 자동으로 DB와 테이블을 만들어 줍니다.
echo ====================================================================
"C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe" -ussafy -pssafy --default-character-set=utf8mb4 < "%~1"

if %errorlevel% equ 0 (
    echo.
    echo [성공] DB와 테이블, 더미 데이터가 모두 만들어졌습니다!
    echo.
    echo 현재 MySQL 안에 있는 데이터베이스 목록:
    "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe" -ussafy -pssafy -e "SHOW DATABASES;"
) else (
    echo.
    echo [오류] ssafy/ssafy 계정으로 실행 실패! root 계정 등으로 확인이 필요합니다.
)

echo.
pause
