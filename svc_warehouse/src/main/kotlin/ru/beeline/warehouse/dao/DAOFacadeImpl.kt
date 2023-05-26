package ru.beeline.warehouse.dao

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import ru.beeline.warehouse.models.Item
import ru.beeline.warehouse.models.Warehouse

class DAOFacadeImpl : DAOFacade {

    private fun resultRowToItem(row: ResultRow) = Item(
        id = row[Warehouse.id],
        orderId = row[Warehouse.orderId],
        item = row[Warehouse.item],
        status = row[Warehouse.status],
    )

    override suspend fun item(id: String): Item? = dbQuery {
        Warehouse
            .select { Warehouse.id eq id }
            .map(::resultRowToItem)
            .singleOrNull()
    }

    override suspend fun newItems(items: List<Item>): List<Item> = dbQuery {
        val insertStatementItems = Warehouse.batchInsert(items) {
            this[Warehouse.id] = it.id
            this[Warehouse.orderId] = it.orderId
            this[Warehouse.item] = it.item
            this[Warehouse.status] = it.status
        }
        insertStatementItems.map(::resultRowToItem)
    }
    override suspend fun editItem(item: Item): Boolean = dbQuery {
        Warehouse.update({ Warehouse.id eq item.id }) {
            it[id] = item.id
            it[orderId] = item.orderId
            it[this.item] = item.item
            it[status] = item.status
        } > 0
    }

    override suspend fun deleteItem(id: String): Boolean = dbQuery {
        Warehouse.deleteWhere { Warehouse.id eq id } > 0
    }
}

val dao: DAOFacade = DAOFacadeImpl()