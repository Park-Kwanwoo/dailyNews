FROM eclipse-temurin:17-jdk
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar
ENV PROFILE=prod
ENTRYPOINT ["java", "-Dspring.active.profiles=${PROFILE}", "-jar", "app.jar"]