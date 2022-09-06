#!make

.PHONY: build-app dockerbuild-app deploy-app

POC_NAMESPACE := dev-dse-poc
IMAGE_NAME := dse-on-k8s-with-java
TAG := latest


install-cass-operator:
	kubectl delete ns ${POC_NAMESPACE} --ignore-not-found
	kubectl create ns ${POC_NAMESPACE}
	helm repo add k8ssandra https://helm.k8ssandra.io/stable --force-update
	helm install cass-operator k8ssandra/cass-operator -n ${POC_NAMESPACE} --create-namespace

create-ebs-storage:
	#kubectl -n ${POC_NAMESPACE} apply -f deployment/ebs-server-storage.yaml
	kubectl -n ${POC_NAMESPACE} apply -f deployment/minikube-server-storage.yaml

create-data-center:
	kubectl -n ${POC_NAMESPACE} apply -f deployment/mini-cass-dc.yaml

clean-app:
	mvn clean

build-app: clean-app
	mvn compile
	mvn package

dockerbuild-app:
	docker build -f Dockerfile -t ${IMAGE_NAME}:${TAG} .


deploy-app:
	minikube image load ${IMAGE_NAME}:${TAG}
	kubectl -n ${POC_NAMESPACE} apply -f deployment/service/deployment.yaml
	kubectl -n ${POC_NAMESPACE} apply -f deployment/service/service.yaml


destroy:
	kubectl delete cassandradatacenters dc1 -n ${POC_NAMESPACE}
	kubectl delete ns ${POC_NAMESPACE} --ignore-not-found