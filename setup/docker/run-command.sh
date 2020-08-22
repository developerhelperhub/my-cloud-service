
## Run Commands

#### Network creation for my cloud service
#docker network create --gateway 172.16.1.1 --subnet 172.16.1.0/24 mycloud_network

#### Running the services
docker-compose up

#### Stop the services
docker-compose down

#### Stoping the service
docker stop my-cloud-mongodb
docker stop my-cloud-discovery
docker stop my-cloud-monitor-scheduler
docker stop my-cloud-api-gateway
docker stop my-cloud-influxdb
docker stop my-cloud-monitor
docker stop my-cloud-identity
docker stop kibana
docker stop elasticsearch

#### Remove the process
docker rm my-cloud-mongodb
docker rm my-cloud-discovery
docker rm my-cloud-monitor-scheduler
docker rm my-cloud-api-gateway
docker rm my-cloud-influxdb
docker rm my-cloud-monitor
docker rm my-cloud-identity
docker rm kibana
docker rm elasticsearch

docker rmi developerhelperhub/my_cloud_mongodb
docker rmi developerhelperhub/my-cloud-discovery
docker rmi developerhelperhub/my-cloud-monitor-scheduler
docker rmi developerhelperhub/my-cloud-api-gateway
docker rmi developerhelperhub/my_cloud_influxdb
docker rmi developerhelperhub/my-cloud-monitor
docker rmi developerhelperhub/my-cloud-identity

#### Running db only
docker-compose -f docker-compose-db.yml up

#### Stop db only
docker-compose -f docker-compose-db.yml up


#### Remove all unwanted process, contairs
docker system prune
docker rmi -f $(docker images -aq)

docker-compose down
docker rm -fv $(docker ps -aq)

#### Remove all unwanted volume
docker volume prune

#### lsof meaning 'LiSt Open Files' is used to find out which files are open by which process. 
sudo lsof -i -P -n | grep 5432


####you can run any command in a running container just knowing its ID (or name):
docker exec my-cloud-discovery ls /var/mycloud/logs

####tail contailer logs
docker exec my-cloud-discovery tail -f  /var/mycloud/logs/app.log


docker exec -it my-cloud-discovery /bin/sh

### 
cat /etc/*-release



docker run --name elasticsearch \
    -p 9200:9200 \
    -e "ES_JAVA_OPTS=-Xms1g -Xmx1g" \
    -e "discovery.type=single-node" \
    -v elasticsearch_data:/usr/share/elasticsearch/data \
    docker.elastic.co/elasticsearch/elasticsearch:7.2.0

docker run --name kibana \
    -p 5601:5601 \
    --link elasticsearch \
    docker.elastic.co/kibana/kibana:7.2.0

docker run --name my-cloud-mongodb \
    -p 27017:27017 \
    -e "MONGO_INITDB_DATABASE=myidsrvdb" \
    -e "MONGO_INITDB_ROOT_USERNAME=myidsrvdbuser" \
    -e "MONGO_INITDB_ROOT_PASSWORD=myidsrvdbpwd@1234" \
    -v init-mongo.js:/docker-entrypoint-initdb.d/init-mongo.js:ro \
    -v /var/mycloud/volume/db/mongo:/data/db \
    mongo

docker run --name my-cloud-influxdb \
    -p 8086:8086 \
    -e "INFLUX_USERNAME=todd" \
    -e "INFLUX_PASSWORD=influxdb4ever" \
    -v /var/mycloud/volume/db/influx:/var/lib/influxdb \
    influxdb

my-cloud-influxdb:
    image: influxdb
    container_name: my-cloud-influxdb
    environment:
      - INFLUX_USERNAME=todd
      - INFLUX_PASSWORD=influxdb4ever
    volumes:
      - /var/mycloud/volume/db/influx:/var/lib/influxdb
    ports:
      - '8086:8086'


docker run --name my-cloud-discovery \
    -p 8761:8761 \
    -p 9001:9001 \
    --link elasticsearch \
    -e "ENV_MYC_DISCOVERY_USERNAME=discovery" \
    -e "ENV_MYC_DISCOVERY_PASSWORD=discoverypass" \
    -e "ENV_MYC_DISCOVERY_HOST=my-cloud-discovery" \
    -e "ENV_MYC_DISCOVERY_PORT=8761" \
    -e "ENV_FILEBEAT_ELASTICSEARCH_HOSTS=['elasticsearch:9200']" \
    -e "ENV_FILEBEAT_INDEX='my-cloud-logs-discovery-%{+yyyy.MM.dd}'" \
    -e "ENV_FILEBEAT_LOGGING_LEVEL='info'" \
    -e JAVA_OPTS="-Djava.rmi.server.logCalls=true
        -Djava.rmi.server.hostname=localhost
        -Dcom.sun.management.jmxremote=true
        -Dcom.sun.management.jmxremote.local.only=false
        -Dcom.sun.management.jmxremote.port=7001
        -Dcom.sun.management.jmxremote.rmi.port=7001
        -Dcom.sun.management.jmxremote.ssl=false
        -Dcom.sun.management.jmxremote.authenticate=false" \
    developerhelperhub/my-cloud-discovery

