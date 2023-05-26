package ru.beeline.payment

import io.ktor.server.application.*
import ru.beeline.payment.dao.initDB
import ru.beeline.payment.plugins.configureRouting
import ru.beeline.payment.plugins.configureSerialization

fun main(args: Array<String>): Unit =
    io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused")
fun Application.module() {
    initDB()
    configureSerialization()
    configureRouting()
}
