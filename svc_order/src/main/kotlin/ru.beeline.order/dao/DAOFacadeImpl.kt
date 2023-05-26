package ru.beeline.order.dao

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import ru.beeline.order.models.*

class DAOFacadeImpl : DAOFacade {
    private fun resultRowToOrderItem(row: ResultRow) = OrderItem(
        id = row[OrderItems.id],
        orderId = row[OrderItems.orderId],
        item = row[OrderItems.item],
        amount = row[OrderItems.amount],
        price = row[OrderItems.price]
    )

    private fun resultRowToOrder(rowOrder: ResultRow, lstItem: List<ResultRow>) = Order(
        id = rowOrder[Orders.id],
        contents = lstItem.map(::resultRowToOrderItem),
        status = rowOrder[Orders.status],
    )

    private suspend fun batchInsertItems(order: Order): List<ResultRow> = dbQuery {
        OrderItems.batchInsert(order.contents) {
            this[OrderItems.id] = it.id
            this[OrderItems.orderId] = order.id
            this[OrderItems.item] = it.item
            this[OrderItems.amount] = it.amount
            this[OrderItems.price] = it.price
        }
    }

    override suspend fun order(id: String): Order? = dbQuery {
        val orderRow = Orders.select { Orders.id eq id }.singleOrNull()
        val items = OrderItems.select { OrderItems.orderId eq id }.toList()
        if (orderRow != null) resultRowToOrder(orderRow, items) else null
    }

    override suspend fun newOrder(order: Order): Order? = dbQuery {
        val insertStatementOrder = Orders.insert {
            it[id] = order.id
            it[status] = order.status
        }
        val insertStatementItems = batchInsertItems(order)

        insertStatementOrder.resultedValues?.singleOrNull()?.let { row ->
            resultRowToOrder(row, insertStatementItems)
        }
    }

    override suspend fun editOrder(order: Order): Boolean = dbQuery {
        val isUpd = Orders.update({ Orders.id eq order.id }) {
            it[id] = order.id
            it[status] = order.status
        }
        OrderItems.deleteWhere { orderId eq order.id }

        val insertStatementItems = batchInsertItems(order)

        isUpd > 0 && insertStatementItems.size == order.contents.size
    }

    override suspend fun deleteOrder(id: String): Boolean = dbQuery {
        val isDelOrder = Orders.deleteWhere { Orders.id eq id }
        val isDelItems = OrderItems.deleteWhere { orderId eq id }
        isDelOrder > 0 && isDelItems >= 0
    }
}

val dao: DAOFacade = DAOFacadeImpl()