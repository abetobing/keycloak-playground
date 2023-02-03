#!/bin/bash

docker exec -it docker_keycloak_1 /opt/keycloak/bin/kc.sh build
docker-compose restart keycloak
sleep 3
