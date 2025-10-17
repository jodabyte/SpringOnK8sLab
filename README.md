# Getting Started

## K8s

Create cluster: `task minikube:create-cluster`

## PostgreSQL

[Docker Hub Image](https://hub.docker.com/_/postgres/)

Deploy PostgreSQL: `task postgres:deploy POSTGRES_PASSWORD=<password>`

## Config Server

1. Create an user in PostgreSQL
2. Create a database in PostgreSQL, with the same name as in [POSTGRES_DB](k8s/Config-Server/ConfigMap.yml)
3. Build-Image: `task config-server:build-image`
4. Create the config with the user credentials from step 1: `task config-server:create-config POSTGRES_USER=<user> POSTGRES_PASSWORD=<password>`
5. Deploy app: `task config-server:deploy-app`

## Product Service

1. Create an user in PostgreSQL
2. Create a database in PostgreSQL, with the same name `odins_oddities`
3. Build-Image: `task product-service:build-image`
4. Create the config with the user credentials from step 1: `task product-service:create-config POSTGRES_USER=<user> POSTGRES_PASSWORD=<password>`
5. Deploy app: `task product-service:deploy-app`

## Event Generator

1. Build-Image: `task event-generator:build-image`
2. Create the config with the user credentials from step 1: `task event-generator:create-config`
3. Deploy app: `task event-generator:deploy-app`

## Kube-Prometheus

Project: [Kube-Prometheus](https://prometheus-operator.dev/kube-prometheus/kube/access-ui/)

1. Install using Helm Chart `helm install kube-prometheus oci://ghcr.io/prometheus-community/charts/kube-prometheus-stack -n monitoring --create-namespace`
2. Create a ServiceMonitor: `kubectl apply -f ./k8s/Prometheus/ServiceMonitor.yml`
3. [Prometheus & Grafana Dashboards](https://prometheus-operator.dev/kube-prometheus/kube/access-ui/) can be accessed with:
    - Prometheus: `kubectl --namespace monitoring port-forward svc/prometheus-operated 9090`
    - Grafana: `kubectl --namespace monitoring port-forward svc/kube-prometheus-grafana 3000:80`

# Local/Dev Environment

## Access Services

Run: `task <service>:port-forward`

## Product Service

### Set-Up Environment

1. Create .env file:
```
CONFIG_SERVER_URI=http://localhost:8888
SPRING_PROFILES_ACTIVE=local
POSTGRES_USER=<user>
POSTGRES_PASSWORD=<password>
```
2. Add `"envFile": "${workspaceFolder}/.env"` to `.vscode/launch.json`:
```
{
    "type": "java",
    "name": "ProductServiceApplication",
    "request": "launch",
    "mainClass": "productservice/de.jodabyte.springonk8slab.productservice.ProductServiceApplication",
    "projectName": "product-service",
    "envFile": "${workspaceFolder}/.env"
}
```
3. Port-Forward PostgreSQL and Config Server

### OpenApi

Open (Swagger UI)[http://localhost:8081/swagger-ui.html]

## Event Generator

1. The Event Generator needs the [`.env` file](#set-up-environment)
2. Add `"envFile": "${workspaceFolder}/.env"` to `.vscode/launch.json`:
```
{
    "type": "java",
    "name": "EventGeneratorApplication",
    "request": "launch",
    "mainClass": "de.jodabyte.springonk8slab.eventgenerator.EventGeneratorApplication",
    "projectName": "event-generator",
    "envFile": "${workspaceFolder}/.env"
}
```
3. Port-Forward Config-Server and Product-Service