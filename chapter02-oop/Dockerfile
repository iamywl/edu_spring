FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY src/ src/
RUN mkdir -p out && javac -d out src/main/java/com/edu/oop/*.java
CMD ["java", "-cp", "out", "com.edu.oop.OopMain"]
