package ru.beeline.order.routers

import io.ktor.client.*
import io.ktor.client.engine.java.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.config.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import ru.beeline.order.config.HostServicesConfig
import ru.beeline.order.dao.dao
import ru.beeline.order.models.OrderDTO
import ru.beeline.order.models.OrderDTOSaga
import ru.beeline.order.models.orderDTOToOrder
import ru.beeline.order.orchestrator.Orchestrator
import java.net.http.HttpClient
import java.util.UUID

fun Route.orderRouting(cfg: ApplicationConfig) {
    route("order") {

        val client = HttpClient(Java) {
            install(ContentNegotiation) {
                json()
            }

            engine {
                threadsCount = 8
                pipelining = true
                protocolVersion = HttpClient.Version.HTTP_2
            }
        }

        val hsc = HostServicesConfig(cfg)

        val orchestrator = Orchestrator(hsc, client)

        get("{id}") {
            val id = call.parameters.getOrFail<String>("id")
            val order = dao.order(id)?: return@get call.respondText(
                "No order with id $id",
                status = HttpStatusCode.NotFound
            )
            call.respond(order)
        }

        post {
            val uuid = UUID.randomUUID().toString()
            val orderDTO = call.receive<OrderDTO>()
            val order = orderDTOToOrder(orderDTO, uuid)
            val orderR = dao.newOrder(order)?: return@post call.respondText(
                "Order not added to DB",
                status = HttpStatusCode.ExpectationFailed
            )
            call.respond(orderR)
        }

        post("saga") {
            val orderDTOSaga = call.receive<OrderDTOSaga>()
            val result = orchestrator.saga(orderDTOSaga)
            println(result.second)
            call.respond(status = result.first, message = result.second)
        }

        delete("{id}") {
            val id = call.parameters.getOrFail<String>("id")
            val status = dao.deleteOrder(id)
            call.respond(mapOf("status" to status))
        }

        put ("{id}") {
            val id = call.parameters.getOrFail<String>("id")
            val orderDTO = call.receive<OrderDTO>()
            val order = orderDTOToOrder(orderDTO, id)
            val status = dao.editOrder(order)
            call.respond(mapOf("status" to status))
        }
    }
}