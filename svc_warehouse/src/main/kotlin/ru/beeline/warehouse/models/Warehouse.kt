package ru.beeline.warehouse.models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table

@Serializable
data class Item(val id: String, val orderId: String, val item: String, val status: String)

@Serializable
data class ItemDTO(val orderId: String, val item: String, val status: String)

object Warehouse : Table("warehouse") {
    val id = char("id", 36).uniqueIndex()
    val orderId = char("order_id", 36)
    val item = varchar("item", 256)
    val status = varchar("status", 16)

    override val primaryKey = PrimaryKey(id)
}

fun warehouseDTOToWarehouse(warehouseDTO: ItemDTO, id: String) =
    Item(id, warehouseDTO.orderId, warehouseDTO.item, warehouseDTO.status)