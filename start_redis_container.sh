#!/bin/bash

set -e

if docker ps -a | grep redis-graph &> /dev/null ; then
	read -p "redis-graph container already exists. Do you want to delete it?" -n 1 -r
	echo
	if [[ $REPLY =~ ^[Yy]$ ]]; then
		if docker ps -a -f status=exited | grep redis-graph &> /dev/null; then
			docker rm redis-graph
		else
			docker kill redis-graph
			docker rm redis-graph
		fi
	else
		exit
	fi
fi

if [ "$#" -eq 1 ]; then
	docker run --name redis-graph -v $(readlink -f $1):/data -p 6379 -d redis redis-server --appendonly yes
else
	docker run --name redis-graph -p 6379 -d redis
fi

echo "redis-graph running on port $(docker port redis-graph 6379)"

