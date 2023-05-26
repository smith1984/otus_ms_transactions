package ru.beeline.delivery

import io.ktor.server.application.*
import ru.beeline.delivery.dao.initDB
import ru.beeline.delivery.plugins.*

fun main(args: Array<String>): Unit =
    io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused")
fun Application.module() {
    initDB()
    configureSerialization()
   configureRouting()
}
