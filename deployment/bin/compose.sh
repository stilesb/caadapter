#!/bin/bash

set -e
set -x

export PROJECT_DIR=$(PWD)
export COMPOSE_FILE=$PROJECT_DIR/deployment/docker-compose.yml
export COMPOSE_PROJECT_NAME=caadapter

docker_build() {
    docker-compose -f $COMPOSE_FILE build
}

docker_up() {
    docker-compose -f $COMPOSE_FILE up -d
}

docker_rm() {
    docker-compose -f $COMPOSE_FILE stop
    docker-compose -f $COMPOSE_FILE rm
}

case "$1" in
    "build")
	docker_build
	;;
    "up")
	docker_up
	;;
    "bounce")
	docker_build
	docker_up
	;;
    "rm")
	docker_rm
	;;
    *)
	echo "Usage: ./deployment/bin/compose.sh [build | up | rm]"
	;; 
esac
