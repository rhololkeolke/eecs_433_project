#!/bin/bash

set -e 

java -jar /filament_benchmark_jar/filament_benchmark.jar $FILAMENT_POSTGRES_PORT_5432_TCP_ADDR $FILAMENT_POSTGRES_PORT_5432_TCP_PORT filament /raw_data.nt $NUM_EDGES > /output/benchmark_outputs_$NUM_EDGES.txt 2>&1

psql -h $FILAMENT_POSTGRES_PORT_5432_TCP_ADDR $FILAMENT_POSTGRES_PORT_5432_TCP_PORT filament filament -c "select t1.datname AS db_name,  pg_size_p│Devins-MacBook-Pro:eecs_433_project rhol$ touch ~/boot2docker/benchmark_data.txt
retty(pg_database_size(t1.datname)) as db_size from pg_database as t1 WHERE t1.datname = 'filament';" > /output/db_size_$NUM_EDGES.txt 2>&1
