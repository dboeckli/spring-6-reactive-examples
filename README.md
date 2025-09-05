# spring-6-reactive-examples

## Kubernetes

To run maven filtering for destination target/k8s and destination target/helm run:
```bash
mvn clean install -DskipTests 
```

### Deployment with Helm

Be aware that we are using a different namespace here (not default)

Go to the directory where the tgz file has been created after 'mvn install'
```powershell
cd target/helm/repo
```

unpack
```powershell
$file = Get-ChildItem -Filter spring-6-reactive-examples-v*.tgz | Select-Object -First 1
tar -xvf $file.Name
```

install
```powershell
$APPLICATION_NAME = Get-ChildItem -Directory | Where-Object { $_.LastWriteTime -ge $file.LastWriteTime } | Select-Object -ExpandProperty Name
helm upgrade --install $APPLICATION_NAME ./$APPLICATION_NAME --namespace spring-6-reactive-examples --create-namespace --wait --timeout 5m --debug --render-subchart-notes
```

show logs and show event
```powershell
kubectl get pods -n spring-6-reactive-examples
```
replace $POD with pods from the command above
```powershell
kubectl logs $POD -n spring-6-reactive-examples --all-containers
```

Show Details and Event

$POD_NAME can be: spring-6-reactive-examples-mongodb, spring-6-reactive-examples
```powershell
kubectl describe pod $POD_NAME -n spring-6-reactive-examples
```

Show Endpoints
```powershell
kubectl get endpoints -n spring-6-reactive-examples
```

test
```powershell
helm test $APPLICATION_NAME --namespace spring-6-reactive-examples --logs
```

status
```powershell
helm status $APPLICATION_NAME --namespace spring-6-reactive-examples
```

uninstall
```powershell
helm uninstall $APPLICATION_NAME --namespace spring-6-reactive-examples
```

delete all
```powershell
kubectl delete all --all -n spring-6-reactive-examples
```

create busybox sidecar
```powershell
kubectl run busybox-test --rm -it --image=busybox:1.36 --namespace=spring-6-reactive-examples --command -- sh
```

You can use the actuator rest call to verify via port 30087

