
## Run Commands

#### Network creation for my cloud service
docker network create --gateway 172.16.1.1 --subnet 172.16.1.0/24 mycloud_network

#### Running the services
docker-compose up

#### Stoping the service
docker stop my_cloud_mongodb
docker stop my-cloud-discovery
docker stop my-cloud-monitor-scheduler
docker stop my-cloud-api-gateway
docker stop my_cloud_influxdb
docker stop my-cloud-monitor
docker stop my-cloud-identity

#### Remove the process
docker rm my_cloud_mongodb
docker rm my-cloud-discovery
docker rm my-cloud-monitor-scheduler
docker rm my-cloud-api-gateway
docker rm my_cloud_influxdb
docker rm my-cloud-monitor
docker rm my-cloud-identity