package ru.beeline.user.dao

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import ru.beeline.user.models.User
import ru.beeline.user.models.Users

class DAOFacadeImpl : DAOFacade {

    private fun resultRowToUser(row: ResultRow) = User(
        id = row[Users.id],
        billingId = row[Users.billingId],
        name = row[Users.name],
        email = row[Users.email],
    )

    override suspend fun user(id: String): User? = dbQuery {
        Users
            .select { Users.id eq id }
            .map(::resultRowToUser)
            .singleOrNull()
    }

    override suspend fun userByBillingId(id: String): User? = dbQuery {
        Users
            .select { Users.billingId eq id }
            .map(::resultRowToUser)
            .singleOrNull()
    }

    override suspend fun newUser(user: User): User? = dbQuery {
        val insertStatementUser = Users.insert {
            it[id] = user.id
            it[billingId] = user.billingId
            it[name] = user.name
            it[email] = user.email
        }
        insertStatementUser.resultedValues?.singleOrNull()?.let(::resultRowToUser)
    }

    override suspend fun editUser(user: User): Boolean = dbQuery {
        Users.update({ Users.id eq user.id }) {
            it[id] = user.id
            it[billingId] = user.billingId
            it[name] = user.name
            it[email] = user.email
        } > 0
    }

    override suspend fun deleteUser(id: String): Boolean = dbQuery {
        Users.deleteWhere { Users.id eq id } > 0
    }
}

val dao: DAOFacade = DAOFacadeImpl()