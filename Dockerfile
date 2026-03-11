FROM eclipse-temurin:21-jdk-alpine AS build

WORKDIR /app

COPY gradlew ./
COPY gradle ./gradle
COPY settings.gradle* build.gradle* ./
RUN sed -i 's/\r$//' gradlew && chmod +x gradlew
RUN ./gradlew --no-daemon help

COPY . .
RUN ./gradlew --no-daemon bootJar -x test
RUN cp "$(find /app/build/libs -maxdepth 1 -type f -name '*.jar' ! -name '*-plain.jar' | head -n 1)" /app/app.jar

FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY --from=build /app/app.jar /app/app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
