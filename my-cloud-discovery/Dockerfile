FROM openjdk:8-jdk-alpine

RUN addgroup -S mycloud
RUN adduser -S mycloud -G mycloud


RUN mkdir -pv /var/mycloud/logs
RUN mkdir -pv /var/mycloud/filebeat/logs

RUN chown mycloud:mycloud /var/mycloud/logs
RUN chown mycloud:mycloud /var/mycloud/filebeat/logs


RUN echo "http://dl-cdn.alpinelinux.org/alpine/edge/testing" >> /etc/apk/repositories
RUN apk update
RUN apk add filebeat


COPY filebeat.yml filebeat.yml
COPY target/*.jar app.jar
COPY commands.sh commands.sh

RUN apk --update add bash && \
    apk add dos2unix
RUN dos2unix /commands.sh

RUN ["chmod", "+x", "commands.sh"] 
ENTRYPOINT ["/commands.sh"]

