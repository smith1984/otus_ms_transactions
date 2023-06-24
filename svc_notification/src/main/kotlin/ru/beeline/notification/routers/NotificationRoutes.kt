package ru.beeline.notification.routers

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import ru.beeline.notification.dao.dao

fun Route.notificationRouting() {
    route("notification") {
        get("{id}") {
            val id = call.parameters.getOrFail<String>("id")
            val notification = dao.notification(id)?: return@get call.respondText(
                "No notifications for user $id",
                status = HttpStatusCode.NotFound
            )
            call.respond(notification)
        }
        get {
            val notifications = dao.allNotification()
            call.respond(notifications)
        }
    }
}