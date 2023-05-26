package ru.beeline.delivery.dao

import ru.beeline.delivery.models.Delivery


interface DAOFacade {
    suspend fun delivery(id: String): Delivery?
    suspend fun newDelivery(delivery: Delivery): Delivery?
    suspend fun editDelivery(delivery: Delivery): Boolean
    suspend fun deleteDelivery(id: String): Boolean
}