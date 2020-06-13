FROM openjdk:8-jdk-alpine

VOLUME /tmp

COPY target/*.jar app.jar

ENTRYPOINT exec java $JAVA_OPTS -jar /app.jar