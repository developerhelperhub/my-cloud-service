FROM openjdk:8-jdk-alpine

RUN addgroup -S mycloud
 
RUN adduser -S mycloud -G mycloud

RUN mkdir -pv /var/mycloud/logs
 
RUN chown mycloud:mycloud /var/mycloud/logs

COPY target/*.jar app.jar

ENTRYPOINT exec java $JAVA_OPTS -jar /app.jar