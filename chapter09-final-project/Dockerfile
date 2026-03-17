# ===== 1단계: 빌드 =====
# Gradle을 사용하여 애플리케이션을 빌드합니다.
FROM gradle:8.8-jdk21 AS builder

WORKDIR /app

# Gradle 설정 파일 먼저 복사 (의존성 캐싱 최적화)
COPY build.gradle settings.gradle ./
COPY gradle ./gradle

# 의존성 먼저 다운로드 (소스 변경 시 재다운로드 방지)
RUN gradle dependencies --no-daemon || true

# 소스 코드 복사
COPY src ./src

# 테스트 제외하고 빌드 (테스트는 별도로 실행)
RUN gradle bootJar --no-daemon -x test

# ===== 2단계: 실행 =====
# 경량 JRE 이미지로 실행합니다.
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# 빌드 결과물 복사
COPY --from=builder /app/build/libs/*.jar app.jar

# 포트 노출
EXPOSE 8080

# 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "app.jar"]
