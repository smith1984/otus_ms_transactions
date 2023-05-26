package ru.beeline.warehouse.plugins

import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.application.*
import ru.beeline.warehouse.routers.warehouseRouting

fun Application.configureRouting() {
    routing {
        get("/health") {
            call.respond(mapOf("status" to "ok"))
        }
        warehouseRouting()
    }
}
