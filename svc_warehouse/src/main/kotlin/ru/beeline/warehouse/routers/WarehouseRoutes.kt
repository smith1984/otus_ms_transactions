package ru.beeline.warehouse.routers

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import ru.beeline.warehouse.dao.dao
import ru.beeline.warehouse.models.ItemDTO
import ru.beeline.warehouse.models.warehouseDTOToWarehouse
import java.util.*
import kotlin.collections.List

fun Route.warehouseRouting() {
    route("warehouse") {
        get("{id}") {
            val id = call.parameters.getOrFail<String>("id")
            val warehouse = dao.item(id)?: return@get call.respondText(
                "No warehouse with id $id",
                status = HttpStatusCode.NotFound
            )
            call.respond(warehouse)
        }

        post {
            val lstItemDTO = call.receive<List<ItemDTO>>()
            val lstItem = lstItemDTO.map { warehouseDTOToWarehouse(it, UUID.randomUUID().toString())}
            val warehouseR = dao.newItems(lstItem)?: return@post call.respondText(
                "Items not added to DB",
                status = HttpStatusCode.ExpectationFailed
            )
            call.respond(warehouseR)
        }

        delete("{id}") {
            val id = call.parameters.getOrFail<String>("id")
            val status = dao.deleteItem(id)
            call.respond(mapOf("status" to status))
        }

        put ("{id}") {
            val id = call.parameters.getOrFail<String>("id")
            val warehouseDTO = call.receive<ItemDTO>()
            val warehouse = warehouseDTOToWarehouse(warehouseDTO, id)
            val status = dao.editItem(warehouse)
            call.respond(mapOf("status" to status))
        }
    }
}