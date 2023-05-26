package ru.beeline.delivery.routers

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import ru.beeline.delivery.dao.dao
import ru.beeline.delivery.models.DeliveryDTO
import ru.beeline.delivery.models.deliveryDTOToDelivery
import java.util.*

fun Route.deliveryRouting() {
    route("delivery") {
        get("{id}") {
            val id = call.parameters.getOrFail<String>("id")
            val delivery = dao.delivery(id) ?: return@get call.respondText(
                "No delivery with id $id",
                status = HttpStatusCode.NotFound
            )
            call.respond(delivery)
        }
        post {
            val deliveryDTO = call.receive<DeliveryDTO>()
            if (deliveryDTO.timeDelivery > Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())) {
                val uuid = UUID.randomUUID().toString()
                val delivery = deliveryDTOToDelivery(deliveryDTO, uuid)
                val deliveryR = dao.newDelivery(delivery) ?: return@post call.respondText(
                    "Delivery not added to DB",
                    status = HttpStatusCode.ExpectationFailed
                )
                call.respond(deliveryR)
            } else
                call.respond(status = HttpStatusCode.ExpectationFailed, "Delivery time is incorrect")
        }

        delete("{id}") {
            val id = call.parameters.getOrFail<String>("id")
            val status = dao.deleteDelivery(id)
            call.respond(mapOf("status" to status))
        }

        put("{id}") {
            val id = call.parameters.getOrFail<String>("id")
            val deliveryDTO = call.receive<DeliveryDTO>()
            val delivery = deliveryDTOToDelivery(deliveryDTO, id)
            val status = dao.editDelivery(delivery)
            call.respond(mapOf("status" to status))
        }
    }
}