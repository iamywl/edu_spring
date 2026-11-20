@echo off
chcp 65001 >nul
echo ============================================================
echo   MySQL ssafy_sample 샘플 DB 및 더미 데이터 자동 생성 중...
echo ============================================================
"C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe" -ussafy -pssafy --default-character-set=utf8mb4 < "%~dp0Spring_sample\src\main\resources\scripts\ssafy_sample.sql"
if %errorlevel% equ 0 (
    echo [성공] ssafy_sample 데이터베이스와 테이블이 생성되었습니다!
) else (
    echo [오류] MySQL 실행 실패. 아이디/비밀번호(ssafy/ssafy)를 확인하세요.
)
pause
