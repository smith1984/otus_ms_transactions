package ru.beeline.user.config

import io.ktor.server.config.*

data class ServiceCfg(val host: String, val port: Int, val path: String)

data class HostServicesConfig(
    val cfgs: Map<String, ServiceCfg> = mapOf(
        "billing" to ServiceCfg("localhost", 8085, "billing"),
        "user" to ServiceCfg("localhost", 8084, "user")
    ),
) {
    constructor(config: ApplicationConfig) : this(
        cfgs = mapOf(
            "billing" to getCfg(config, "billing"),
            "user" to getCfg(config, "user")
        )
    )
}

fun getCfg(config: ApplicationConfig, name: String): ServiceCfg {
    val lstConfiguration: List<ApplicationConfig> = config.configList("svcs")

    val cfg = lstConfiguration.first { it.propertyOrNull(name) != null }

    return ServiceCfg(
        cfg.property("${name}.host").getString(),
        cfg.property("${name}.port").getString().toInt(),
        cfg.property("${name}.path").getString()
    )
}


