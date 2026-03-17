# 멀티 스테이지 빌드를 사용하여 이미지 크기를 최소화합니다.

# 1단계: 빌드 스테이지
FROM gradle:8.12-jdk21 AS build
WORKDIR /app

# Gradle 빌드 파일 복사 (캐시 활용을 위해 소스보다 먼저 복사)
COPY build.gradle settings.gradle ./
COPY gradle ./gradle

# 의존성 다운로드 (소스 변경 시에도 캐시 활용)
RUN gradle dependencies --no-daemon || true

# 소스 코드 복사 및 빌드
COPY src ./src
RUN gradle bootJar --no-daemon

# 2단계: 실행 스테이지
FROM eclipse-temurin:21-jre
WORKDIR /app

# 빌드 결과물(JAR) 복사
COPY --from=build /app/build/libs/*.jar app.jar

# 8080 포트 노출
EXPOSE 8080

# 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "app.jar"]
