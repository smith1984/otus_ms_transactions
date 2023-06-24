package ru.beeline.billing.routers

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import ru.beeline.billing.dao.dao
import ru.beeline.billing.models.BillingDTO
import ru.beeline.billing.models.billingDTOToBilling
import java.util.UUID

fun Route.billingRouting() {
    route("billing") {
        get("{id}") {
            val id = call.parameters.getOrFail<String>("id")
            val billing = dao.billing(id)?: return@get call.respondText(
                "No billing with id $id",
                status = HttpStatusCode.NotFound
            )
            call.respond(billing)
        }
        post {
            val billingDTO = call.receive<BillingDTO>()
            val uuid = UUID.randomUUID().toString()
            val billing = billingDTOToBilling(billingDTO, uuid)
            val billingR = dao.newBilling(billing)?: return@post call.respondText(
                "Billing not added to DB",
                status = HttpStatusCode.ExpectationFailed
            )
            call.respond(billingR)
        }

        delete("{id}") {
            val id = call.parameters.getOrFail<String>("id")
            val status = dao.deleteBilling(id)
            call.respond(mapOf("status" to status))
        }

        put ("{id}/{sum}") {
            val id = call.parameters.getOrFail<String>("id")
            val sum = call.parameters.getOrFail<Double>("sum")
            val status = dao.buy(id, sum)
            call.respond(mapOf("status" to status))
        }

        put ("refill/{id}/{sum}") {
            val id = call.parameters.getOrFail<String>("id")
            val sum = call.parameters.getOrFail<Double>("sum")
            val status = dao.refill(id, sum)
            call.respond(mapOf("status" to status))
        }

    }
}