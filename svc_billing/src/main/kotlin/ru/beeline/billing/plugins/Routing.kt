package ru.beeline.billing.plugins

import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.application.*
import ru.beeline.billing.routers.billingRouting

fun Application.configureRouting() {
    routing {
        get("/health") {
            call.respond(mapOf("status" to "ok"))
        }
        billingRouting()
    }
}
