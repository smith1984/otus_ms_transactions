package ru.beeline.notification

import io.ktor.server.application.*
import io.ktor.server.metrics.micrometer.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.micrometer.core.instrument.binder.jvm.JvmGcMetrics
import io.micrometer.core.instrument.binder.jvm.JvmMemoryMetrics
import io.micrometer.core.instrument.binder.system.ProcessorMetrics
import io.micrometer.core.instrument.distribution.DistributionStatisticConfig
import io.micrometer.prometheus.PrometheusConfig
import io.micrometer.prometheus.PrometheusMeterRegistry
import org.slf4j.event.Level
import ru.beeline.notification.dao.initDB
import ru.beeline.notification.feature.BackgroundJob
import ru.beeline.notification.kafka.createKafkaConsumer
import ru.beeline.notification.plugins.configureRouting
import ru.beeline.notification.plugins.configureSerialization
import java.time.Duration

fun main(args: Array<String>): Unit =
    io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused")
fun Application.module() {
    val appMicrometerRegistry = PrometheusMeterRegistry(PrometheusConfig.DEFAULT)
    install(MicrometerMetrics) {
        registry = appMicrometerRegistry
        distributionStatisticConfig = DistributionStatisticConfig.Builder()
            .percentilesHistogram(true)
            .maximumExpectedValue(Duration.ofSeconds(20).toNanos().toDouble())
            .serviceLevelObjectives(
                Duration.ofMillis(10).toNanos().toDouble(),
                Duration.ofMillis(50).toNanos().toDouble(),
                Duration.ofMillis(100).toNanos().toDouble(),
                Duration.ofMillis(200).toNanos().toDouble(),
                Duration.ofMillis(300).toNanos().toDouble(),
                Duration.ofMillis(400).toNanos().toDouble(),
                Duration.ofMillis(500).toNanos().toDouble()
            )
            .build()
        meterBinders = listOf(
            JvmMemoryMetrics(),
            JvmGcMetrics(),
            ProcessorMetrics()
        )
    }

    install(CallLogging) {
        level = Level.INFO

        format { call ->
            val status = call.response.status()
            val httpMethod = call.request.httpMethod.value
            val userAgent = call.request.headers["User-Agent"]
            "Status: $status, HTTP method: $httpMethod, User agent: $userAgent"
        }
    }

    routing {
        get("/actuator/prometheus") {
            call.respond(appMicrometerRegistry.scrape())
        }
    }

    initDB()
    configureSerialization()
    configureRouting()

    install(BackgroundJob.BackgroundJobFeature) {
        name = "Kafka-Consumer-Job"
        job = createKafkaConsumer<String, String>(environment)
    }
}
