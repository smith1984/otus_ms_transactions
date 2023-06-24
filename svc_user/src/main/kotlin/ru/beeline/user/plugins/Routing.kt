package ru.beeline.user.plugins

import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.application.*
import io.ktor.server.config.*
import ru.beeline.user.routers.userRouting

fun Application.configureRouting(cfg: ApplicationConfig) {
    routing {
        get("/health") {
            call.respond(mapOf("status" to "ok"))
        }
        userRouting(cfg)
    }
}
