package ru.beeline.notification.config

import io.ktor.server.config.*


data class KafkaConfig(
    val bootstrap: String = "localhost:9092",
    val groupId: String = "notification",
    val topic: String = "dbserver1.schema_test.history_billings",
    val keyDeserializer: String = "org.apache.kafka.common.serialization.StringDeserializer",
    val valueDeserializer: String = "org.apache.kafka.common.serialization.StringDeserializer",
    val offsetReset: String = "latest"
) {
    constructor(config: ApplicationConfig): this(
        bootstrap = config.property("kafka.bootstrap").getString(),
        groupId = config.property("kafka.groupId").getString(),
        topic = config.property("kafka.topic").getString(),
        keyDeserializer = config.property("kafka.keyDeserializer").getString(),
        valueDeserializer = config.property("kafka.valueDeserializer").getString(),
        offsetReset = config.property("kafka.offsetReset").getString(),
    )
}