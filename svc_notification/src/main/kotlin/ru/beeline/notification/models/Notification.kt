package ru.beeline.notification.models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table

@Serializable
data class Notification(val id: String, val userId: String, val message: String, val email: String)

object Notifications : Table("notifications") {
    val id = char("id", 36).uniqueIndex()
    val userId = char("user_id", 36)
    val message = varchar("message", 256)
    val email = varchar("email", 128)

    override val primaryKey = PrimaryKey(id)
}

@Serializable
data class KafkaRecordBH(val payload: Payload)

@Serializable
data class Payload(val after: BillingHistory, val op: String)

@Serializable
data class BillingHistory(val id: String, val billing_id: String,
                          val sum: Double,
                          val last_value_wallet: Double,
                          val current_value_wallet: Double,
                          val type_operation: String,
                          val status: String)
