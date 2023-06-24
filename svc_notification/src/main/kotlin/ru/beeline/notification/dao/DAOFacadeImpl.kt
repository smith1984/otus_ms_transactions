package ru.beeline.notification.dao

import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import ru.beeline.notification.models.Notification
import ru.beeline.notification.models.Notifications

class DAOFacadeImpl : DAOFacade {
    private fun resultRowToNotification(row: ResultRow) = Notification(
        id = row[Notifications.id],
        userId = row[Notifications.userId],
        message = row[Notifications.message],
        email = row[Notifications.email]
    )

    override suspend fun notification(id: String): List<Notification> = dbQuery {
        Notifications
            .select { Notifications.userId eq id }
            .map(::resultRowToNotification)
    }

    override suspend fun allNotification(): List<Notification> = dbQuery {
        Notifications.selectAll().map(::resultRowToNotification)
    }

    override suspend fun newNotification(notification: Notification): Notification? = dbQuery {
        val insertStatementNotification = Notifications.insert {
            it[id] = notification.id
            it[userId] = notification.userId
            it[message] = notification.message
            it[email] = notification.email
        }

        insertStatementNotification.resultedValues?.singleOrNull()?.let(::resultRowToNotification)
    }
}

val dao: DAOFacade = DAOFacadeImpl()