## -*- docker-image-name: "rhololkeolke/hadoop-single-node" -*-

FROM ubuntu:12.04

# install the necessary packages
RUN apt-get update && apt-get install -y ssh rsync openjdk-7-jdk

# add the hadoop user
RUN addgroup hadoop && adduser --ingroup hadoop hduser

# download and install hadoop for the previously created user
RUN cd /usr/local && \
	sudo wget http://archive.apache.org/dist/hadoop/core/hadoop-0.20.203.0/hadoop-0.20.203.0rc1.tar.gz && \
	sudo tar xzf hadoop-0.20.203.0rc1.tar.gz && \
	sudo mv hadoop-0.20.203.0 hadoop && \
	sudo chown -R hduser:hadoop hadoop


