package ru.beeline.user.dao

import ru.beeline.user.models.User


interface DAOFacade {
    suspend fun user(id: String): User?
    suspend fun newUser(user: User): User?
    suspend fun editUser(user: User): Boolean
    suspend fun deleteUser(id: String): Boolean
    suspend fun userByBillingId(id: String): User?
}