package ru.beeline.billing.dao

import ru.beeline.billing.models.Billing

interface DAOFacade {
    suspend fun billing(id: String): Billing?
    suspend fun newBilling(billing: Billing): Billing?
    suspend fun buy(id: String, sum: Double): Boolean
    suspend fun refill(id: String, sum: Double): Boolean
    suspend fun deleteBilling(id: String): Boolean
}