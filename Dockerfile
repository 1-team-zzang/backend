FROM openjdk:17-jdk-slim

ENV TZ=Asia/Seoul
ENV JAVA_TOOL_OPTIONS="-Duser.timezone=Asia/Seoul"

COPY build/libs/calpick-0.0.1-SNAPSHOT.jar app.jar
COPY application.properties application.properties
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]