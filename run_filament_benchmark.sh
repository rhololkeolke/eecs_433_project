#!/bin/bash

set -e

echo "Running small test: 10K nodes"

./filament_postgres_container.sh restart
docker run --name filament-benchmark-10k -e "NUM_EDGES=10000" -it -v $HOME/benchmark_outputs:/output --link filament-postgres:filament_postgres rhololkeolke/filament-benchmark

echo "Running medium test: 100k"

./filament_postgres_container.sh restart
docker run --name filament-benchmark-100k -e "NUM_EDGES=100000" -it -v $HOME/benchmark_outputs:/output --link filament-postgres:filament_postgres rhololkeolke/filament-benchmark

echo "Running full test: 1 million"
./filament_postgres_container.sh restart
docker run --name filament-benchmark-1mil -e "NUM_EDGES=1000000" -it -v $HOME/benchmark_outputs:/output --link filament-postgres:filament_postgres rhololkeolke/filament-benchmark
