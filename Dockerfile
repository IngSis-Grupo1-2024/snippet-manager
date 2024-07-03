FROM gradle:8.7.0-jdk17 AS build

ARG ACTOR
ARG TOKEN

ENV GITHUB_ACTOR=${ACTOR}
ENV GITHUB_TOKEN=${TOKEN}

COPY  . /app
WORKDIR /app
RUN ./gradlew bootJar

FROM eclipse-temurin:17-jre-jammy
EXPOSE 8080
RUN mkdir /app
COPY --from=build /app/build/libs/snippet-manager.jar /app/snippet-manager.jar
COPY newrelic/newrelic.jar /app/newrelic.jar
COPY newrelic/newrelic.yml /app/newrelic.yml
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=production","-javaagent:/app/newrelic.jar","/app/snippet-manager.jar"]
