### Running the My Cloud Service on Minikube

### Install Minikube on Mac
https://kubernetes.io/docs/tasks/tools/install-minikube/

#### Verify whether VM enabled or not
```
sysctl -a | grep -E --color 'machdep.cpu.features|VMX'
```

#### Install and start Minikube
Minikube running on the docker driver
```
brew install minikube

minikube start --driver=docker --memory=6g --cpus=4 
```
Note: cleaning the kubernets cluster
```
minikube delete
```

#### Create the main objects
```main.yml``` contains the main objects of my cloud services. It contains namespace, volume, etc. which are sharing multiple deployment.

```
kubectl apply -f main.yml

kubectl get namespace

kubectl get pv

kubectl describe pv pv-volume -n my-cloud-local

kubectl delete pv pv-volume -n my-cloud-local
```

#### Deploy the elastic search
```
kubectl apply -f elasticsearch.yml

kubectl get pvc

kubectl get pvc pvc-elasticsearch -n my-cloud-local

kubectl get deployment elasticsearch -n my-cloud-local

kubectl get pods -n my-cloud-local

kubectl get services -n my-cloud-local
```

Enable the elastic search access outside of the cluster.
```
kubectl port-forward service/elasticsearch 9200:9200 -n my-cloud-local
```


#### Other commands
Get the cluster information
```
kubectl cluster-info
```

Now to test that our cluster was made correctly, enter:
```
kubectl get svc
```

Checking the minikube status
```
minikube status
```

