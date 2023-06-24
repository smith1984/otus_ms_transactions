package ru.beeline.notification.plugins

import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.application.*
import ru.beeline.notification.routers.notificationRouting

fun Application.configureRouting() {
    routing {
        get("/health") {
            call.respond(mapOf("status" to "ok"))
        }
        notificationRouting()
    }
}
