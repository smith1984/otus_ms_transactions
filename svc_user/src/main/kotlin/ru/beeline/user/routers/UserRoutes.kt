package ru.beeline.user.routers

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.java.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.config.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import ru.beeline.billing.models.Billing
import ru.beeline.billing.models.BillingDTO
import ru.beeline.user.config.HostServicesConfig
import ru.beeline.user.dao.dao
import ru.beeline.user.models.User
import ru.beeline.user.models.UserDTO
import ru.beeline.user.models.userDTOToUser
import java.util.UUID

fun Route.userRouting(cfg: ApplicationConfig) {
    route("user") {

        val client = HttpClient(Java) {
            install(ContentNegotiation) {
                json()
            }
        }

        val hsc = HostServicesConfig(cfg)
        val billingStr = "billing"
        val userStr = "user"

        get("{id}") {
            val id = call.parameters.getOrFail<String>("id")
            val user = dao.user(id) ?: return@get call.respondText(
                "No user with id $id",
                status = HttpStatusCode.NotFound
            )
            call.respond(user)
        }

        get("billing_id/{id}") {
            val id = call.parameters.getOrFail<String>("id")
            val user = dao.userByBillingId(id) ?: return@get call.respondText(
                "No user with billing id $id",
                status = HttpStatusCode.NotFound
            )
            call.respond(user)
        }

        post {
            val userDTO = call.receive<UserDTO>()
            val uuid = UUID.randomUUID().toString()
            val billingDTO = BillingDTO()

            val responseCreateBilling = client.post {
                url {
                    host = hsc.cfgs[billingStr]?.host.toString()
                    port = hsc.cfgs[billingStr]?.port.toString().toInt()
                    path(hsc.cfgs[billingStr]?.path.toString())
                }
                contentType(ContentType.Application.Json)
                setBody(billingDTO)
            }

            if (responseCreateBilling.status.value == 200) {
                val billing = responseCreateBilling.body<Billing>()
                val user = userDTOToUser(userDTO, uuid, billing.id)
                val userR = dao.newUser(user) ?: return@post call.respondText(
                    "User not added to DB",
                    status = HttpStatusCode.ExpectationFailed
                )
                call.respond(userR)
            } else {
                call.respondText(
                    "User not added to DB, failed create billing account",
                    status = HttpStatusCode.ExpectationFailed
                )
            }
        }

        delete("{id}") {
            val id = call.parameters.getOrFail<String>("id")
            val status = dao.deleteUser(id)
            call.respond(mapOf("status" to status))
        }

        put("{id}") {
            val id = call.parameters.getOrFail<String>("id")

            val responseGetUser = client.get {
                url {
                    host = hsc.cfgs[userStr]?.host.toString()
                    port = hsc.cfgs[userStr]?.port.toString().toInt()
                    path(hsc.cfgs[userStr]?.path.toString())
                    appendPathSegments(id)
                }
            }

            val billingId = responseGetUser.body<User>().billingId

            val userDTO = call.receive<UserDTO>()
            val user = userDTOToUser(userDTO, id, billingId)
            val status = dao.editUser(user)
            call.respond(mapOf("status" to status))
        }
    }
}