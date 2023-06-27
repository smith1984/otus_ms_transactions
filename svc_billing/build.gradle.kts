import io.ktor.plugin.features.JreVersion
import io.ktor.plugin.features.DockerImageRegistry

plugins {
    kotlin("jvm")
    id("io.ktor.plugin")
    id("org.jetbrains.kotlin.plugin.serialization")
}

application {
    mainClass.set("ru.beeline.billing.ApplicationKt")
}

ktor {

    docker {
        val java_version: String by project

        localImageName.set(project.name)
        imageTag.set(project.version.toString())
        jreVersion.set(JreVersion.valueOf("JRE_$java_version"))
        externalRegistry.set(
            DockerImageRegistry.dockerHub(
                appName = provider { "svc_billing" },
                username = providers.environmentVariable("DOCKER_HUB_USERNAME"),
                password = providers.environmentVariable("DOCKER_HUB_PASSWORD")
            )
        )
    }
}

dependencies {
    val ktor_version: String by project
    val logback_version: String by project
    val exposed_version: String by project
    val postgres_version: String by project

    implementation("io.ktor:ktor-server-core-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-content-negotiation-jvm:$ktor_version")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-netty-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-config-yaml:$ktor_version")

    implementation("ch.qos.logback:logback-classic:$logback_version")

    implementation("org.postgresql:postgresql:$postgres_version")
    implementation("org.jetbrains.exposed:exposed-core:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-dao:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposed_version")

    val prometheus_version: String by project
    val logback_appenders_version: String by project
    val fluent_logger_version: String by project

    implementation("io.ktor:ktor-server-call-logging:$ktor_version")
    implementation("io.ktor:ktor-server-metrics-micrometer:$ktor_version")
    implementation("com.sndyuk:logback-more-appenders:$logback_appenders_version")
    implementation("org.fluentd:fluent-logger:$fluent_logger_version")
    implementation("io.micrometer:micrometer-registry-prometheus:$prometheus_version")
}
