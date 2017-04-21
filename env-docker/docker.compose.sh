#!/usr/bin/env bash

machine_name="struqt"

docker-machine start ${machine_name}
eval $(docker-machine env ${machine_name})

docker-compose --version
docker-compose up -d --build
