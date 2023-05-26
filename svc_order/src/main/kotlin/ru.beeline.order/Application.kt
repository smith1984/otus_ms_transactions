package ru.beeline.order

import io.ktor.server.application.*
import io.ktor.server.config.*
import io.ktor.server.plugins.contentnegotiation.*
import ru.beeline.order.dao.initDB
import ru.beeline.order.plugins.configureRouting
import ru.beeline.order.plugins.configureSerialization

fun main(args: Array<String>): Unit =
    io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused")
fun Application.module() {
    initDB()
    configureSerialization()
    configureRouting(environment.config)
}

