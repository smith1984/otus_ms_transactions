package ru.beeline.warehouse

import io.ktor.server.application.*
import ru.beeline.warehouse.dao.initDB
import ru.beeline.warehouse.plugins.*

fun main(args: Array<String>): Unit =
    io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused")
fun Application.module() {
    initDB()
    configureSerialization()
    configureRouting()
}
