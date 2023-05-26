package ru.beeline.payment.models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table

@Serializable
data class Payment(val id: String, val orderId: String, val cost: Double, val status: String)

@Serializable
data class PaymentDTO(val orderId: String, val cost: Double, val status: String)

object Payments : Table("payments") {
    val id = char("id", 36).uniqueIndex()
    val orderId = char("order_id", 36)
    val cost = double("cost")
    val status = varchar("status", 16)

    override val primaryKey = PrimaryKey(id)
}

fun paymentDTOToPayment(paymentDTO: PaymentDTO, id: String) =
    Payment(id, paymentDTO.orderId, paymentDTO.cost, paymentDTO.status)