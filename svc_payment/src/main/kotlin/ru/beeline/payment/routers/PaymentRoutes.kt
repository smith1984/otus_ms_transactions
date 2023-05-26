package ru.beeline.payment.routers

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import ru.beeline.payment.dao.dao
import ru.beeline.payment.models.PaymentDTO
import ru.beeline.payment.models.paymentDTOToPayment
import java.util.UUID

fun Route.paymentRouting() {
    route("payment") {
        get("{id}") {
            val id = call.parameters.getOrFail<String>("id")
            val payment = dao.payment(id)?: return@get call.respondText(
                "No payment with id $id",
                status = HttpStatusCode.NotFound
            )
            call.respond(payment)
        }
        post {
            val paymentDTO = call.receive<PaymentDTO>()
            val uuid = UUID.randomUUID().toString()
            val payment = paymentDTOToPayment(paymentDTO, uuid)
            val paymentR = dao.newPayment(payment)?: return@post call.respondText(
                "Payment not added to DB",
                status = HttpStatusCode.ExpectationFailed
            )
            call.respond(paymentR)
        }

        delete("{id}") {
            val id = call.parameters.getOrFail<String>("id")
            val status = dao.deletePayment(id)
            call.respond(mapOf("status" to status))
        }

        put ("{id}") {
            val id = call.parameters.getOrFail<String>("id")
            val paymentDTO = call.receive<PaymentDTO>()
            val payment = paymentDTOToPayment(paymentDTO, id)
            val status = dao.editPayment(payment)
            call.respond(mapOf("status" to status))
        }
    }
}