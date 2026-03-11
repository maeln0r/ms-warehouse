FROM eclipse-temurin:21-jdk-alpine AS build

WORKDIR /app

COPY gradlew ./
COPY gradle ./gradle
COPY settings.gradle* build.gradle* ./
RUN sed -i 's/\r$//' gradlew && chmod +x gradlew
RUN ./gradlew --no-daemon help

COPY . .
RUN ./gradlew --no-daemon clean bootJar -x test

FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
