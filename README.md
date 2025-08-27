# Getting Started

## K8s

Create cluster: `task minikube:create-cluster`

## PostgreSQL

[Docker Hub Image](https://hub.docker.com/_/postgres/)

Deploy PostgreSQL: `task postgres:deploy POSTGRES_PASSWORD=<password>`

## Config Server

1. Create an user in PostgreSQL
2. Create a database in PostgreSQL, with the same name as in [POSTGRES_DB](k8s/Config-Server/ConfigMap.yml)
3. Build-Image: `task minikube:link-docker-env && task confifg-server:build-image`
4. Create the config with the user credentials from step 1: `task confifg-server:create-config POSTGRES_USER=<user> POSTGRES_PASSWORD=<password>`
5. Deploy app: `task confifg-server:deploy-app`

## Product Service

1. Create an user in PostgreSQL
2. Create a database in PostgreSQL, with the same name `odins_oddities`
3. Build-Image: `task minikube:link-docker-env && task product-service:build-image`
4. Create the config with the user credentials from step 1: `task product-service:create-config POSTGRES_USER=<user> POSTGRES_PASSWORD=<password>`
5. Deploy app: `task product-service:deploy-app`

# Local/Dev Environment

## Access Services

Run: `task <service>:port-forward`