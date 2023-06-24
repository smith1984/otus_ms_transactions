package ru.beeline.billing.models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table

@Serializable
data class Billing(val id: String, val wallet: Double)

@Serializable
data class BillingDTO(val wallet: Double = 0.0)

object HistoryBillings : Table("history_billings") {
    val id = char("id", 36).uniqueIndex()
    val billingId = char("billing_id", 36)
    val sum = double("sum")
    val lastValueWallet = double("last_value_wallet")
    val currentValueWallet = double("current_value_wallet")
    val typeOperation = varchar("type_operation", 36)
    val status = varchar("status", 36)

    override val primaryKey = PrimaryKey(id)
}

object Billings : Table("billings") {
    val id = char("id", 36).uniqueIndex()
    val wallet = double("wallet")

    override val primaryKey = PrimaryKey(id)
}

fun billingDTOToBilling(billingDTO: BillingDTO, id: String) =
    Billing(id, billingDTO.wallet)