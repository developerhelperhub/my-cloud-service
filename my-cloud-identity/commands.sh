#!/bin/bash

echo "Executing the filebeat...."

exec filebeat -c filebeat.yml -e &

echo "Executing the java...."

echo $JAVA_OPTS

exec java $JAVA_OPTS -jar /app.jar