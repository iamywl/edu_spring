FROM eclipse-temurin:21-jdk

WORKDIR /app

# 편의 도구 설치
RUN apt-get update && apt-get install -y --no-install-recommends \
    bash-completion vim less && \
    rm -rf /var/lib/apt/lists/*

# 컴파일 및 실행 스크립트 복사
COPY run.sh /app/run.sh
COPY compile.sh /app/compile.sh
RUN chmod +x /app/run.sh /app/compile.sh

# 기본: 컨테이너를 띄워놓고 VSC로 접속
CMD ["sleep", "infinity"]
