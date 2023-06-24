package ru.beeline.notification.kafka

import io.ktor.server.application.*
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.serialization.StringDeserializer
import ru.beeline.notification.config.HostServicesConfig
import ru.beeline.notification.config.KafkaConfig
import java.util.*

fun <K, V> createKafkaConsumer(environment: ApplicationEnvironment): Consumer<K, V> {

    val kafkaConfig = KafkaConfig(environment.config)

    val props = Properties().apply {
        put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfig.bootstrap)
        put(ConsumerConfig.GROUP_ID_CONFIG, kafkaConfig.groupId)
        put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, kafkaConfig.offsetReset)
        put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, kafkaConfig.keyDeserializer)
        put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, kafkaConfig.valueDeserializer)
    }
    val hsc = HostServicesConfig(environment.config)

    return Consumer(KafkaConsumer(props), kafkaConfig.topic, hsc)
}