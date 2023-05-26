package ru.beeline.delivery.dao

import kotlinx.datetime.*
import kotlinx.datetime.toJavaLocalDateTime
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import ru.beeline.delivery.models.Deliveries
import ru.beeline.delivery.models.Delivery

class DAOFacadeImpl : DAOFacade {
    private fun resultRowToDelivery(row: ResultRow) =
        Delivery(
        id = row[Deliveries.id],
        orderId = row[Deliveries.orderId],
        timeDelivery = row[Deliveries.timeDelivery].toKotlinLocalDateTime(),
        status = row[Deliveries.status],
    )

    override suspend fun delivery(id: String): Delivery? = dbQuery {
        Deliveries
            .select { Deliveries.id eq id }
            .map(::resultRowToDelivery)
            .singleOrNull()
    }

    override suspend fun newDelivery(delivery: Delivery): Delivery? = dbQuery {
        val insertStatementDelivery = Deliveries.insert {
            it[id] = delivery.id
            it[orderId] = delivery.orderId
            it[timeDelivery] = delivery.timeDelivery.toJavaLocalDateTime()
            it[status] = delivery.status
        }
        insertStatementDelivery.resultedValues?.singleOrNull()?.let(::resultRowToDelivery)
    }

    override suspend fun editDelivery(delivery: Delivery): Boolean = dbQuery {
        Deliveries.update({ Deliveries.id eq delivery.id }) {
            it[id] = delivery.id
            it[orderId] = delivery.orderId
            it[timeDelivery] = delivery.timeDelivery.toJavaLocalDateTime()
            it[status] = delivery.status
        } > 0
    }


    override suspend fun deleteDelivery(id: String): Boolean = dbQuery {
        Deliveries.deleteWhere { Deliveries.id eq id } > 0
    }
}

val dao: DAOFacade = DAOFacadeImpl()