package ru.beeline.user.models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table

@Serializable
data class User(val id: String, val billingId: String, val name: String, val email: String)

@Serializable
data class UserDTO(val name: String, val email: String)

object Users : Table("users") {
    val id = char("id", 36).uniqueIndex()
    val billingId = char("billing_id", 36)
    val name = varchar("name", 128)
    val email = varchar("email", 128)

    override val primaryKey = PrimaryKey(id)
}

fun userDTOToUser(userDTO: UserDTO, id: String, billingId: String) =
    User(id, billingId, userDTO.name, userDTO.email)