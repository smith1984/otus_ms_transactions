package ru.beeline.order.plugins

import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.application.*
import io.ktor.server.config.*
import ru.beeline.order.routers.orderRouting

fun Application.configureRouting(cfg: ApplicationConfig) {
    routing {
        get("/health") {
            call.respond(mapOf("status" to "ok"))
        }
        orderRouting(cfg)
    }
}
