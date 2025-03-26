FROM gradle:jdk17-alpine AS build

WORKDIR /home/gradle/src
COPY . .

RUN gradle clean build -x test

FROM openjdk:17-jdk-slim

WORKDIR /app

COPY --from=build /home/gradle/src/build/libs/professionals-language-api-1.0.jar app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]
