package ru.beeline.payment.dao

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import ru.beeline.payment.models.Payment
import ru.beeline.payment.models.Payments

class DAOFacadeImpl : DAOFacade {

    private fun resultRowToPayment(row: ResultRow) = Payment(
        id = row[Payments.id],
        orderId = row[Payments.orderId],
        cost = row[Payments.cost],
        status = row[Payments.status],
    )

    override suspend fun payment(id: String): Payment? = dbQuery {
        Payments
        .select { Payments.id eq id }
        .map(::resultRowToPayment)
        .singleOrNull()
    }

    override suspend fun newPayment(payment: Payment): Payment? = dbQuery {
        val insertStatementPayment = Payments.insert {
            it[id] = payment.id
            it[orderId] = payment.orderId
            it[cost] = payment.cost
            it[status] = payment.status
        }
        insertStatementPayment.resultedValues?.singleOrNull()?.let(::resultRowToPayment)
    }

    override suspend fun editPayment(payment: Payment): Boolean = dbQuery {
        Payments.update({ Payments.id eq payment.id }) {
            it[id] = payment.id
            it[orderId] = payment.orderId
            it[cost] = payment.cost
            it[status] = payment.status
        } > 0
    }

    override suspend fun deletePayment(id: String): Boolean = dbQuery {
        Payments.deleteWhere { Payments.id eq id } > 0
    }
}

val dao: DAOFacade = DAOFacadeImpl()