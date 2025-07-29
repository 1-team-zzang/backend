FROM gradle:8.14-jdk17 AS builder
WORKDIR /calpick-backend
RUN gradle wrapper --gradle-version 8.5
RUN ./gradlew clean build -x test

FROM openjdk:17-jdk-slim
COPY build/libs/calpick-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]