
## Run Commands

#### Network creation for my cloud service
#docker network create --gateway 172.16.1.1 --subnet 172.16.1.0/24 mycloud_network

#### Running the services
docker-compose up

#### Stop the services
docker-compose down

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

docker rmi developerhelperhub/my_cloud_mongodb
docker rmi developerhelperhub/my-cloud-discovery
docker rmi developerhelperhub/my-cloud-monitor-scheduler
docker rmi developerhelperhub/my-cloud-api-gateway
docker rmi developerhelperhub/my_cloud_influxdb
docker rmi developerhelperhub/my-cloud-monitor
docker rmi developerhelperhub/my-cloud-identity


#### Remove all unwanted process, contairs
docker system prune
docker rmi -f $(docker images -aq)

docker-compose down
docker rm -fv $(docker ps -aq)

#### Remove all unwanted volume
docker volume prune

#### lsof meaning 'LiSt Open Files' is used to find out which files are open by which process. 
sudo lsof -i -P -n | grep 5432