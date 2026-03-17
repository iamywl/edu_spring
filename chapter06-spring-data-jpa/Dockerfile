# === 1단계: 빌드 스테이지 ===
# Gradle로 애플리케이션을 빌드하는 단계
FROM gradle:8.8-jdk21 AS build

WORKDIR /app

# Gradle 설정 파일 먼저 복사 (의존성 캐싱 활용)
COPY build.gradle settings.gradle ./
COPY gradle ./gradle

# 의존성 다운로드 (소스 변경 시에도 이 레이어는 캐시됨)
RUN gradle dependencies --no-daemon || true

# 소스 코드 복사
COPY src ./src

# 애플리케이션 빌드 (테스트 제외 - CI에서 별도 실행)
RUN gradle bootJar --no-daemon -x test

# === 2단계: 실행 스테이지 ===
# 빌드 결과물(JAR)만 가져와서 실행하는 경량 이미지
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# 빌드 스테이지에서 JAR 파일만 복사
COPY --from=build /app/build/libs/*.jar app.jar

# 컨테이너 포트 노출
EXPOSE 8080

# 애플리케이션 실행
# - JVM 메모리 설정: 컨테이너 환경에 적합하게 조정
ENTRYPOINT ["java", "-jar", "-Djava.security.egd=file:/dev/./urandom", "app.jar"]
