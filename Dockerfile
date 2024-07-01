FROM gradle:8.7.0-jdk17 AS build
COPY  . /home/gradle/src
WORKDIR /home/gradle/src

ARG ACTOR
ARG TOKEN

ENV GITHUB_ACTOR ${ACTOR}
ENV GITHUB_TOKEN ${TOKEN}

RUN gradle assemble
FROM openjdk:23-ea-17-jdk-slim
RUN mkdir /app
COPY --from=build /home/gradle/src/build/libs/*.jar /app/spring-boot-application.jar
COPY newrelic/newrelic.jar /app/newrelic.jar
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=production","-javaagent:/app/newrelic.jar","/app/spring-boot-application.jar"]
