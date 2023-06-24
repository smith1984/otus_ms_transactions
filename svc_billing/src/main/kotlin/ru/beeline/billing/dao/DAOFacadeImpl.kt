package ru.beeline.billing.dao

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import ru.beeline.billing.models.Billing
import ru.beeline.billing.models.Billings
import ru.beeline.billing.models.HistoryBillings
import java.util.*

class DAOFacadeImpl : DAOFacade {
    private fun resultRowToBilling(row: ResultRow) = Billing(
        id = row[Billings.id],
        wallet = row[Billings.wallet]
    )

    override suspend fun billing(id: String): Billing? = dbQuery {
        Billings
            .select { Billings.id eq id }
            .map(::resultRowToBilling)
            .singleOrNull()
    }

    override suspend fun newBilling(billing: Billing): Billing? = dbQuery {
        val insertStatementBilling = Billings.insert {
            it[id] = billing.id
            it[wallet] = billing.wallet

        }

        HistoryBillings.insert {
            it[HistoryBillings.id] = UUID.randomUUID().toString()
            it[billingId] = billing.id
            it[currentValueWallet] = billing.wallet
            it[typeOperation] = "CREATE"
            it[status] = "SUCCESS"
        }

        insertStatementBilling.resultedValues?.singleOrNull()?.let(::resultRowToBilling)
    }

    override suspend fun refill(id: String, sum: Double): Boolean = dbQuery {
        val billing: Billing? = Billings
            .select { Billings.id eq id }
            .map(::resultRowToBilling)
            .singleOrNull()

        if (billing != null) {
                HistoryBillings.insert {
                    it[HistoryBillings.id] = UUID.randomUUID().toString()
                    it[billingId] = billing.id
                    it[HistoryBillings.sum] = sum
                    it[lastValueWallet] = billing.wallet
                    it[currentValueWallet] = billing.wallet + sum
                    it[typeOperation] = "REFILL"
                    it[status] = "SUCCESS"
                }

                Billings.update({ Billings.id eq billing.id }) {
                    it[wallet] = billing.wallet + sum
                } > 0
        } else
            false
    }

    override suspend fun buy(id: String, sum: Double): Boolean = dbQuery {
        val billing: Billing? = Billings
            .select { Billings.id eq id }
            .map(::resultRowToBilling)
            .singleOrNull()

        if (billing != null) {
            if (billing.wallet >= sum) {
                HistoryBillings.insert {
                    it[HistoryBillings.id] = UUID.randomUUID().toString()
                    it[billingId] = billing.id
                    it[HistoryBillings.sum] = sum
                    it[lastValueWallet] = billing.wallet
                    it[currentValueWallet] = billing.wallet - sum
                    it[typeOperation] = "BUY"
                    it[status] = "SUCCESS"
                }

                Billings.update({ Billings.id eq billing.id }) {
                    it[wallet] = billing.wallet - sum
                } > 0
            } else {
                HistoryBillings.insert {
                    it[HistoryBillings.id] = UUID.randomUUID().toString()
                    it[billingId] = billing.id
                    it[HistoryBillings.sum] = sum
                    it[lastValueWallet] = billing.wallet
                    it[currentValueWallet] = billing.wallet
                    it[typeOperation] = "BUY"
                    it[status] = "FAILED"
                }
                false
            }

        } else
            false
    }

    override suspend fun deleteBilling(id: String): Boolean = dbQuery {
        Billings.deleteWhere { Billings.id eq id } > 0
    }
}

val dao: DAOFacade = DAOFacadeImpl()
