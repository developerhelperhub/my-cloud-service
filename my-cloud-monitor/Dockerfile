FROM openjdk:8-jdk-alpine

VOLUME /tmp

COPY target/*.jar app.jar

ENTRYPOINT java -Djava.rmi.server.hostname=$ENV_MYC_MONITOR_RMI_HOST -Dcom.sun.management.jmxremote.port=$ENV_MYC_MONITOR_JMX_PORT -Dcom.sun.management.jmxremote.rmi.port=$ENV_MYC_MONITOR_JMX_RMI_PORT -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.authenticate=false -jar /app.jar