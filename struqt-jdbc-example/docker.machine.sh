#!/usr/bin/env bash

machine_name="struqt"

docker-machine create --driver virtualbox ${machine_name}

docker-machine stop ${machine_name}

VBoxManage modifyvm ${machine_name} \
    --natpf1 "MySQL-PF, tcp, 127.0.0.1, 33061, , 33061"

docker-machine start ${machine_name}

eval $(docker-machine env ${machine_name})
