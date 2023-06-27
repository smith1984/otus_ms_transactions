FROM quay.io/strimzi/kafka:latest-kafka-3.5.0
USER root:root
RUN mkdir -p /opt/kafka/plugins/debezium
ADD https://repo1.maven.org/maven2/io/debezium/debezium-connector-postgres/2.3.0.Final/debezium-connector-postgres-2.3.0.Final-plugin.tar.gz ./
RUN tar -xzf debezium-connector-postgres-2.3.0.Final-plugin.tar.gz && rm debezium-connector-postgres-2.3.0.Final-plugin.tar.gz && mv ./debezium-connector-postgres/ /opt/kafka/plugins/debezium/
USER 1001