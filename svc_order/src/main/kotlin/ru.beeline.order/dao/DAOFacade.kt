package ru.beeline.order.dao

import ru.beeline.order.models.Order


interface DAOFacade {
    suspend fun order(id: String): Order?
    suspend fun newOrder(order: Order): Order?
    suspend fun editOrder(order: Order): Boolean
    suspend fun deleteOrder(id: String): Boolean
}