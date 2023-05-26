package ru.beeline.delivery.models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.javatime.datetime
import kotlinx.datetime.LocalDateTime

@Serializable
data class Delivery(val id: String, val orderId: String, val timeDelivery: LocalDateTime, val status: String)

@Serializable
data class DeliveryDTO(val orderId: String, val timeDelivery: LocalDateTime, val status: String)

object Deliveries : Table("deliveries") {
    val id = char("id", 36).uniqueIndex()
    val orderId = char("order_id", 36)
    val timeDelivery = datetime("time_delivery")
    val status = varchar("status", 16)

    override val primaryKey = PrimaryKey(id)
}

fun deliveryDTOToDelivery(deliveryDTO: DeliveryDTO, id: String) =
    Delivery(id, deliveryDTO.orderId, deliveryDTO.timeDelivery, deliveryDTO.status)