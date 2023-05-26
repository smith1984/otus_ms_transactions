package ru.beeline.delivery.plugins

import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.application.*
import ru.beeline.delivery.routers.deliveryRouting

fun Application.configureRouting() {
    routing {
        get("/health") {
            call.respond(mapOf("status" to "ok"))
        }
        deliveryRouting()
    }
}
