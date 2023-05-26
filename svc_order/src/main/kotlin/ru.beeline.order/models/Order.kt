package ru.beeline.order.models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table
import java.util.UUID
import kotlinx.datetime.LocalDateTime

@Serializable
data class Order(val id: String, val contents: List<OrderItem>, val status: String)

@Serializable
data class OrderDTO(val contents: List<OrderItemDTO>, val status: String)

@Serializable
data class OrderDTOSaga(val contents: List<OrderItemDTO>, val timeDelivery: LocalDateTime)

@Serializable
data class OrderItemDTO(val item: String, val amount: Int, val price: Double)

@Serializable
data class OrderItem(val id: String, val orderId: String, val item: String, val amount: Int, val price: Double)

object Orders : Table("orders") {
    val id = char("id", 36).uniqueIndex()
    val status = varchar("status", 16)

    override val primaryKey = PrimaryKey(id)
}

object OrderItems : Table("order_items") {
    val id = char("id", 36).uniqueIndex()
    val orderId = char("order_id", 36)
    val item = varchar("item", 256)
    val amount = integer("amount")
    val price = double("price")

    override val primaryKey = PrimaryKey(id)
}

fun orderDTOToOrder(orderDTO: OrderDTO, id: String) =
    Order(
        id = id,
        contents = orderDTO.contents.map { orderItemDTO -> orderItemDTOToOrderItem(orderItemDTO, id) },
        status = orderDTO.status
    )

fun orderToOrderDTO(order: Order, newStatus: String): OrderDTO =
    OrderDTO(contents = order.contents.map { orderItemToOrderItemDTO(it) }, status = newStatus)

fun orderItemDTOToOrderItem(orderItemDTO: OrderItemDTO, orderId: String): OrderItem =
    OrderItem(UUID.randomUUID().toString(), orderId, orderItemDTO.item, orderItemDTO.amount, orderItemDTO.price)

fun orderItemToOrderItemDTO(orderItem: OrderItem): OrderItemDTO =
    OrderItemDTO(orderItem.item, orderItem.amount, orderItem.price)

fun orderDTOSagaToOrderDTO(orderDTOSaga: OrderDTOSaga, status: String) =
    OrderDTO(contents = orderDTOSaga.contents, status = status)
