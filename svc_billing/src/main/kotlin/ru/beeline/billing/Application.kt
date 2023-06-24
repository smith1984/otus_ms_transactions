package ru.beeline.billing

import io.ktor.server.application.*
import ru.beeline.billing.dao.initDB
import ru.beeline.billing.plugins.configureRouting
import ru.beeline.billing.plugins.configureSerialization

fun main(args: Array<String>): Unit =
    io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused")
fun Application.module() {
    initDB()
    configureSerialization()
    configureRouting()
}
