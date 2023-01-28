#!/bin/bash

docker exec -it docker_keycloak_1 /bin/bash /opt/keycloak/bin/kc.sh export --users skip --dir /opt/keycloak/imports
