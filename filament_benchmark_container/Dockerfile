## -*- docker-image-name: "rhololkeolke/filament-benchmark" -*-

FROM ubuntu
RUN apt-get update && apt-get install -y openjdk-7-*
ADD filament_benchmark_jar /filament_benchmark_jar
ADD dump.nt /raw_data.nt

CMD /bin/bash -c "java -jar /filament_benchmark_jar/filament_benchmark.jar $FILAMENT_POSTGRES_PORT_5432_TCP_ADDR $FILAMENT_POSTGRES_PORT_5432_TCP_PORT filament /raw_data.nt $NUM_EDGES > /output/benchmark_outputs.txt 2>&1"
