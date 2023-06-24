package ru.beeline.notification.dao

import ru.beeline.notification.models.Notification

interface DAOFacade {
    suspend fun notification(id: String): List<Notification>
    suspend fun allNotification(): List<Notification>
    suspend fun newNotification(notification: Notification): Notification?

}