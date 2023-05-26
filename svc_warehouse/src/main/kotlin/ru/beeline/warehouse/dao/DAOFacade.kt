package ru.beeline.warehouse.dao

import ru.beeline.warehouse.models.Item


interface DAOFacade {
    suspend fun item(id: String): Item?
    suspend fun newItems(items: List<Item>): List<Item>?
    suspend fun editItem(item: Item): Boolean
    suspend fun deleteItem(id: String): Boolean
}