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


#### Deploy the mongodb
```
kubectl apply -f mongodb.yml

kubectl get pvc

kubectl get pvc pvc-mongodb -n my-cloud-local

kubectl get deployment mongodb -n my-cloud-local

kubectl get pods -n my-cloud-local

kubectl get services -n my-cloud-local
```

Enable the mongo search access outside of the cluster.
```
kubectl port-forward service/mongodb 9200:9200 -n my-cloud-local
```

```
kubectl describe pvc pvc-mongodb -n my-cloud-local
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

```
kubectl get storageclasses.storage.k8s.io

minikube addons list

```

You can also access the Init Container statuses programmatically by reading the status.initContainerStatuses field on the Pod Spec:
```
kubectl get pod/mongodb-7864f954b8-j2n2t -n my-cloud-local --template '{{.status.initContainerStatuses}}'
```

Execute the bash command for specific container
```
kubectl run busybox --rm -ti --image=busybox /bin/sh

kubectl exec -it -v=6 mongodb-6d586d7b5b-rsf2k -n my-cloud-local  -- /bin/sh
```