# ams_spring-angular


## set up minikube

```sh 
# Step 1: Start a Minikube Cluster with 3 Nodes
$ minikube start --driver=hyperv --cpus=2 --memory=2048 --nodes=3 -p sip-workshop

# Step 2: Label the Nodes
$ kubectl get nodes
$ kubectl label node sip-workshop-m02 node-role.kubernetes.io/devops-node=devops-node
$ kubectl label node sip-workshop-m03 node-role.kubernetes.io/ams-node=ams-node
$ kubectl get nodes

# Step 3: Create Namespaces
$ kubectl create namespace devops-tools
$ kubectl create namespace ams

# To view all profiles
$ minikube profile list

# Check Cluster Status:
$ minikube status -p sip-workshop

# Pause the Cluster:
$ minikube pause -p sip-workshop

# Unpausing a Cluster with Multiple Nodes
$ minikube unpause -p sip-workshop

# To Delete the Cluster after finishing the workshop:
$ minikube stop -p sip-workshop
$ minikube delete -p sip-workshop

# Note: Minikube does not provide a direct command to delete a specific node within a cluster because it is primarily designed to work with a single-node or multi-node setup where nodes are managed as a single entity. However, you can manage nodes by restarting the cluster with a different configuration or removing and recreating the cluster.
``` 

Next, we need to run another command to enable Ingress addon: ```$ minikube addons enable ingress -p sip-workshop```

## Edit hosts file

- We need to get the cluster ip: 
```sh 
$ minikube ip -p sip-workshop
172.28.118.240
```
- We need to config our hosts file by adding these lines:
```
# Location of hosts file on windows 10 is under c:\Windows\System32\Drivers\etc\hosts
172.28.118.240 jenkins.local.k8s.com
# 172.28.118.240 sonarqube.local.k8s.com
# 172.28.118.240 nexus3.local.k8s.com
172.28.118.240 pma.ams-app.com
172.28.118.240 ams-app.com
``` 

## Install and Configure sonarqube, and Sonatype Nexus 3 Repository Manager OSS using docker: 
 
### Step 1( writing our docker compose file)

```yml
services:

  nexus:
    image: sonatype/nexus3:latest  # Consider specifying a version for reproducibility
    container_name: nexus  # Set the container name to "nexus"
    ports:
      - "8081:8081"  # Map container port 8081 to host port 8081
    volumes:
      - nexus-data:/nexus-data  # Mount a volume named "nexus-data" to the container's "/nexus-data" directory
    restart: unless-stopped  # Restart the container automatically unless manually stopped (optional)
    networks:
      - devops-network  # Optional, uncomment if using a network

  sonarqube:
    image: sonarqube:community
    depends_on:
      - db
    environment:
      SONAR_JDBC_URL: jdbc:postgresql://db:5432/sonar
      SONAR_JDBC_USERNAME: sonar
      SONAR_JDBC_PASSWORD: sonar
    volumes:
      - sonarqube_data:/opt/sonarqube/data
      - sonarqube_extensions:/opt/sonarqube/extensions
      - sonarqube_logs:/opt/sonarqube/logs
    ports:
      - "9000:9000"
    networks:
      - devops-network  # Optional, uncomment if using a network      
      
  db:
    image: postgres:12
    environment:
      POSTGRES_USER: sonar
      POSTGRES_PASSWORD: sonar
    volumes:
      - postgresql:/var/lib/postgresql
      - postgresql_data:/var/lib/postgresql/data
    networks:
      - devops-network  # Optional, uncomment if using a network
 
volumes:
  nexus-data:  # docker volume create jenkins_home; docker volume create nexus-data
  sonarqube_data:          # contains data files, such as Elasticsearch indexes
  sonarqube_extensions:    # will contain any plugins you install and the Oracle JDBC driver if necessary.
  sonarqube_logs:          # contains SonarQube logs about access, web process, CE process, and Elasticsearch
  postgresql:
  postgresql_data:  
  
networks:
  devops-network:  # Define network type (e.g., bridge) if needed for communication
```

###  Step 2( up and run our stack):

```sh 
# Start the Services, Run: 
$ docker-compose up -d # The -d flag runs them in the background. 

#To delete all data run: 
$ docker compose down -v
``` 

### Step 3( compelete our installation) 

- We need to wait few minutes until all is up and running
```sh
# get the admin password which is auto generated on the initial launch of the container.
$ docker container exec nexus cat /nexus-data/admin.password
# 1f415a6f-6bba-4917-8f2a-b5b63d925442

# To access the Nexus Repository Manager using a web browser 
# http://localhost:8081
# user: admin ; password: 1f415a6f-6bba-4917-8f2a-b5b63d925442

# To access sonarqube using a web browser 
# http://localhost:9000
# user: admin ; password: admin 
```

## Install and Configure Jenkins on Minikube:   

### Step 0( Configure Helm)

- Once Helm is installed and set up properly, add the Jenkins repo as follows:
```sh 
$ helm repo add jenkinsci https://charts.jenkins.io
$ helm repo update 

# The helm charts in the Jenkins repo can be listed with the command:
$ helm search repo jenkinsci
```

### Step 1(Create PersistentVolume, PersistentVolumeClaim, and StorageClass)

- **Create a persistent volume**: We want to create a persistent volume for our Jenkins controller pod. This will prevent us from losing our whole configuration of the Jenkins controller and our jobs when we reboot our minikube.
- We choose to use the **/data** directory. This directory will contain our Jenkins controller configuration.
```yml
# jenkins-volume.yaml
apiVersion: v1
kind: PersistentVolume
metadata:
  name: jenkins-pv
  namespace: devops-tools
spec:
  storageClassName: jenkins-pv
  accessModes:
  - ReadWriteOnce
  capacity:
    storage: 20Gi
  persistentVolumeReclaimPolicy: Retain
  hostPath:
    path: /data/jenkins-volume/

---
apiVersion: storage.k8s.io/v1
kind: StorageClass
metadata:
  name: jenkins-pv
provisioner: kubernetes.io/no-provisioner
volumeBindingMode: WaitForFirstConsumer
```   

```yml
# jenkins-pvc.yaml
apiVersion: v1
kind: PersistentVolumeClaim
metadata:
  name: jenkins-pvc
  namespace: devops-tools
spec:
  storageClassName: jenkins-pv
  accessModes:
    - ReadWriteOnce
  resources:
    requests:
      storage: 1Gi
``` 

```sh
# 
$ kubectl apply -f jenkins-volume.yaml
persistentvolume/jenkins-pv created
storageclass.storage.k8s.io/jenkins-pv created

# Once the volume is created we will need to manually change the permissions to allow the jenkins account to write its data.
# you should run these cmds as admin
# Set the active profile to "sip-workshop":
C:\Windows\system32> minikube profile sip-workshop
# SSH into the Minikube profile:
C:\Windows\system32> minikube ssh

# OR, more better, Combining both steps in one line:
C:\Windows\system32> minikube -p sip-workshop ssh
                         _             _
            _         _ ( )           ( )
  ___ ___  (_)  ___  (_)| |/')  _   _ | |_      __
/' _ ` _ `\| |/' _ `\| || , <  ( ) ( )| '_`\  /'__`\
| ( ) ( ) || || ( ) || || |\`\ | (_) || |_) )(  ___/
(_) (_) (_)(_)(_) (_)(_)(_) (_)`\___/'(_,__/'`\____)
 
$ sudo mkdir -p /data/jenkins-volume/
$ sudo chown -R 1000:1000 /data/jenkins-volume/
$ exit
logout

C:\Windows\system32>  

# Now, 
$ kubectl apply -f jenkins-pvc.yaml

# Troubleshoot PVC Binding Issue: If there are issues with PVC binding, you can describe the PVC to see the detailed events and status:
$ kubectl describe pvc jenkins-pvc -n devops-tools
```

### Step 2( Create a service account)

- **Create a service account**: In Kubernetes, service accounts are used to provide an identity for pods. Pods that want to interact with the API server will authenticate with a particular service account. By default, applications will authenticate as the default service account in the namespace they are running in. This means, for example, that an application running in the test namespace will use the default service account of the test namespace.
```sh
# Create a Service Account
$ kubectl create serviceaccount jenkins-sa -n devops-tools
serviceaccount/jenkins-sa created

# Let's review the service account:
$ kubectl get serviceaccount jenkins-sa -n devops-tools -o yaml
```  
- By default, our service account has no permissions to do anything in our cluster.


### Step 3( Granting Permissions with RBAC)

- RBAC in K8s controls the level of access a service account has.
```yml
# jenkins-sa-rbac.yaml
apiVersion: rbac.authorization.k8s.io/v1
kind: ClusterRole
metadata:
  name: jenkins-sa-manager
rules:
- apiGroups: [""]
  resources: ["serviceaccounts"]
  verbs: ["get", "list", "watch", "create", "update", "patch", "delete"]
- apiGroups: ["networking.k8s.io"]
  resources: ["ingresses"]
  verbs: ["get", "list", "watch", "create", "update", "patch", "delete"]
---
apiVersion: rbac.authorization.k8s.io/v1
kind: ClusterRoleBinding
metadata:
  name: jenkins-sa-binding
roleRef:
  apiGroup: rbac.authorization.k8s.io
  kind: ClusterRole
  name: jenkins-sa-manager
subjects:
- kind: ServiceAccount
  name: jenkins-sa
  namespace: devops-tools
```   

- Apply it using: 
```sh
$ kubectl apply -f jenkins-sa-rbac.yaml
clusterrole.rbac.authorization.k8s.io/jenkins-sa-manager created
clusterrolebinding.rbac.authorization.k8s.io/jenkins-sa-binding created
```
 
### Step 4( Install Jenkins)
   
- i have used other approach here
```sh
$ helm create jenkinschart
$ helm install jenkins ./jenkinschart

# Check the deployment status: 
$ kubectl get deployments -n devops-tools

# Now, you can get the deployment details using the following command:
$ kubectl describe deployments --namespace=devops-tools

# Find the Node IP Address:
$ kubectl get nodes -o wide

# To access Jenkins at http://<node-ip>:32000
# http://172.28.118.23:32000

$ kubectl get pods --namespace=devops-tools
NAME                       READY   STATUS    RESTARTS   AGE
jenkins-568dcbffb5-8dhts   1/1     Running   0          5m9s

$ kubectl logs jenkins-568dcbffb5-8dhts --namespace=devops-tools
*************************************************************
*************************************************************
*************************************************************

Jenkins initial setup is required. An admin user has been created and a password generated.
Please use the following password to proceed to installation:

008125eea59e4ddba345134b3f27d92e

This may also be found at: /var/jenkins_home/secrets/initialAdminPassword

*************************************************************
*************************************************************
*************************************************************

# Jenkins URL:: http://172.28.118.23:32000/
# The Jenkins URL is used to provide the root URL for absolute links to various Jenkins resources. That means this value is required for proper operation of many Jenkins features including email notifications, PR status updates, and the BUILD_URL environment variable provided to build steps.
# The proposed default value shown is not saved yet and is generated from the current request, if possible. The best practice is to set this value to the URL that users are expected to use. This will avoid confusion when sharing or viewing links.

   
# Uninstall Chart
$ helm uninstall jenkins -n devops-tools
```

## 

- 
- 
```sh 
# 
$ 
# 
$ 
# 
$ 
# 
$ 
# 
$ 
# 
$ 
# 
$ 
``` 

##

- 
- 
```sh 
# 
$ 
# 
$ 
# 
$ 
# 
$ 
# 
$ 
# 
$ 
# 
$ 
``` 

##

- 
- 
```sh 
# 
$ 
# 
$ 
# 
$ 
# 
$ 
# 
$ 
# 
$ 
# 
$ 
``` 

##

- 
- 
```sh 
# 
$ 
# 
$ 
# 
$ 
# 
$ 
# 
$ 
# 
$ 
# 
$ 
``` 


##

- 
- 
```sh 
# 
$ 
# 
$ 
# 
$ 
# 
$ 
# 
$ 
# 
$ 
# 
$ 
``` 


##

- 
- 
```sh 
# 
$ 
# 
$ 
# 
$ 
# 
$ 
# 
$ 
# 
$ 
# 
$ 
``` 






############################################################################

# used ressources:

- **jenkins on k8s**:
- [How To Setup Jenkins On Kubernetes Cluster – Beginners Guide](https://devopscube.com/setup-jenkins-on-kubernetes-cluster/)
- [Docs: Installing Jenkins/Kubernetes](https://www.jenkins.io/doc/book/installing/kubernetes/)
- []()
- []()
- []()
- []()
- []()
- []()
- []()
- []()
- []()
- []()





