FROM openjdk:21-slim as base
FROM base as build
RUN mkdir /app
RUN mkdir /appdata
COPY build/libs/*.jar /app/epic-sounds.jar
WORKDIR /appdata
ENTRYPOINT ["java", "-jar", "/app/epic-sounds.jar"]
