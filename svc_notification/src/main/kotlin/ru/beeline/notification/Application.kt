package ru.beeline.notification

import io.ktor.server.application.*
import ru.beeline.notification.dao.initDB
import ru.beeline.notification.feature.BackgroundJob
import ru.beeline.notification.kafka.createKafkaConsumer
import ru.beeline.notification.plugins.configureRouting
import ru.beeline.notification.plugins.configureSerialization

fun main(args: Array<String>): Unit =
    io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused")
fun Application.module() {
    initDB()
    configureSerialization()
    configureRouting()

    install(BackgroundJob.BackgroundJobFeature) {
        name = "Kafka-Consumer-Job"
        job = createKafkaConsumer<String, String>(environment)
    }
}
