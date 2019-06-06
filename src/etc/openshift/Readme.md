# install helm
# see https://docs.confluent.io/current/installation/installing_cp/cp-helm-charts/docs/index.html

oc adm policy add-cluster-role-to-user cluster-admin <user> --as=system:admin

curl -L https://git.io/get_helm.sh | bash
helm repo add confluentinc https://confluentinc.github.io/cp-helm-charts/
helm repo update

oc delete --namespace kube-system svc tiller-deploy
oc delete --namespace kube-system deploy tiller-deploy
oc create serviceaccount --namespace kube-system tiller
oc create clusterrolebinding tiller-cluster-rule --clusterrole=cluster-admin --serviceaccount=kube-system:tiller
oc patch deploy --namespace kube-system tiller-deploy -p '{"spec":{"template":{"spec":{"serviceAccount":"tiller"}}}}'
helm init --service-account tiller --upgrade

# verify tiller installation
helm list
oc get pods --namespace kube-system


# create project
project=${project:-scdf}
oc new-project ${project}

# install kafka
helm install -f src/etc/openshift/helm/cp-kafka/single-kafka-node.yml confluentinc/cp-helm-charts --name scdf-kafka
helm test scdf-kafka

# remove kafka  
#helm delete scdf-kafka && kubectl delete pvc --selector=release=scdf-kafka && helm del --purge scdf-kafka


# verify database template
oc process -n openshift postgresql-persistent -p DATABASE_SERVICE_NAME=scdf-data -p POSTGRESQL_DATABASE=scdf-data -p POSTGRESQL_USER=scdf-data-user -p POSTGRESQL_VERSION=10 -o yaml

# create database
oc process -n openshift postgresql-persistent -p DATABASE_SERVICE_NAME=scdf-data -p POSTGRESQL_DATABASE=scdf-data -p POSTGRESQL_USER=scdf-data-user -p POSTGRESQL_VERSION=10 | oc create -f -

# install scdf template
oc apply -f src/etc/openshift/scdf-2-template.yaml

# "Adding 'edit' role to 'scdf' Service Account..."
oc policy add-role-to-user edit system:serviceaccount:${project}:scdf

# "Adding 'scdf' Service Account to the 'anyuid' SCC..."
oc adm policy add-scc-to-user anyuid system:serviceaccount:${project}:scdf --as system:admin


# Register default apps and tasks
curl -X POST "http://scdf-kafka-${project}.apps.${domain}/apps?force=true&uri=https://repo.spring.io/libs-release-local/org/springframework/cloud/stream/app/spring-cloud-stream-app-descriptor/Einstein.SR3/spring-cloud-stream-app-descriptor-Einstein.SR3.stream-apps-kafka-docker" 
curl -X POST "http://scdf-kafka-${project}.apps.${domain}/apps?force=true&uri=https://repo.spring.io/libs-release-local/org/springframework/cloud/task/app/spring-cloud-task-app-descriptor/Elston.RELEASE/spring-cloud-task-app-descriptor-Elston.RELEASE.task-apps-docker"
