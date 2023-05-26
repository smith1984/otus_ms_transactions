package ru.beeline.payment.dao

import ru.beeline.payment.models.Payment


interface DAOFacade {
    suspend fun payment(id: String): Payment?
    suspend fun newPayment(payment: Payment): Payment?
    suspend fun editPayment(payment: Payment): Boolean
    suspend fun deletePayment(id: String): Boolean
}