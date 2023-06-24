package ru.beeline.user

import io.ktor.server.application.*
import ru.beeline.user.dao.initDB
import ru.beeline.user.plugins.configureRouting
import ru.beeline.user.plugins.configureSerialization

fun main(args: Array<String>): Unit =
    io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused")
fun Application.module() {
    initDB()
    configureSerialization()
    configureRouting(environment.config)
}
