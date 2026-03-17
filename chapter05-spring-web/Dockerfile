# ===========================================
# 1단계: 빌드 스테이지
# JDK를 사용하여 애플리케이션을 빌드한다
# ===========================================
FROM eclipse-temurin:21-jdk AS builder

WORKDIR /app

# Gradle Wrapper와 빌드 설정 파일을 먼저 복사하여 의존성 캐시를 활용한다
COPY gradle/ gradle/
COPY gradlew build.gradle settings.gradle ./

# 의존성만 먼저 다운로드 (소스 코드 변경 시 캐시 재사용)
RUN chmod +x gradlew && ./gradlew dependencies --no-daemon

# 소스 코드 복사 후 빌드
COPY src/ src/
RUN ./gradlew clean bootJar --no-daemon

# ===========================================
# 2단계: 실행 스테이지
# JRE만 포함된 가벼운 이미지로 애플리케이션을 실행한다
# ===========================================
FROM eclipse-temurin:21-jre

WORKDIR /app

# 빌드 결과물(JAR)만 복사한다
COPY --from=builder /app/build/libs/*.jar app.jar

# 애플리케이션 포트 노출
EXPOSE 8080

# 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "app.jar"]
