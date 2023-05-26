package ru.beeline.order.config

import io.ktor.server.config.*

data class ServiceCfg(val host: String, val port: Int, val path: String)

data class HostServicesConfig(
    val cfgs: Map<String, ServiceCfg> = mapOf(
        "order" to ServiceCfg("localhost", 8080, "order"),
        "payment" to ServiceCfg("localhost", 8081, "payment"),
        "warehouse" to ServiceCfg("localhost", 8082, "warehouse"),
        "delivery" to ServiceCfg("localhost", 8083, "delivery"),
        )
) {
    constructor(config: ApplicationConfig) : this(
        cfgs = mapOf(
        "order" to getCfg(config, "order"),
        "payment" to getCfg(config, "payment"),
        "warehouse" to getCfg(config, "warehouse"),
        "delivery" to getCfg(config, "delivery")
    ))
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


