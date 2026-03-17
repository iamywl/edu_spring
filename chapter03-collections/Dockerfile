FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY src/ src/
RUN mkdir -p out && javac -d out src/main/java/com/edu/collections/*.java
CMD ["java", "-cp", "out", "com.edu.collections.StreamExample"]
