package ru.beeline.order.orchestrator

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.encodeToJsonElement
import ru.beeline.delivery.models.Delivery
import ru.beeline.order.config.HostServicesConfig
import ru.beeline.order.models.*
import ru.beeline.delivery.models.DeliveryDTO
import ru.beeline.payment.models.Payment
import ru.beeline.payment.models.PaymentDTO
import ru.beeline.warehouse.models.Item
import ru.beeline.warehouse.models.ItemDTO


class Orchestrator(private val hsc: HostServicesConfig, private val client: HttpClient) {

    private suspend inline fun <reified T> postRequest(service: String, body: T): HttpResponse {
        return client.post {
            url {
                host = hsc.cfgs[service]?.host.toString()
                port = hsc.cfgs[service]?.port.toString().toInt()
                path(hsc.cfgs[service]?.path.toString())
            }
            contentType(ContentType.Application.Json)
            setBody(body)
        }
    }

    private suspend inline fun <reified T> putRequest(
        service: String,
        body: T,
        pathAppend: String? = null,
    ): HttpResponse {
        return client.put {
            url {
                host = hsc.cfgs[service]?.host.toString()
                port = hsc.cfgs[service]?.port.toString().toInt()
                path(hsc.cfgs[service]?.path.toString())
                if (pathAppend != null) appendPathSegments(pathAppend)
            }
            contentType(ContentType.Application.Json)
            setBody(body)
        }
    }

    private suspend fun getRequest(service: String, pathAppend: String? = null): HttpResponse {
        return client.get {
            url {
                host = hsc.cfgs[service]?.host.toString()
                port = hsc.cfgs[service]?.port.toString().toInt()
                path(hsc.cfgs[service]?.path.toString())
                if (pathAppend != null) appendPathSegments(pathAppend)
            }
        }
    }


    suspend fun saga(orderDTOSaga: OrderDTOSaga): Pair<HttpStatusCode, JsonElement> {

        val orderDTO = orderDTOSagaToOrderDTO(orderDTOSaga, "PENDING")

        val responseCreateOrder = postRequest("order", orderDTO)

        val resultSaga = if (responseCreateOrder.status.value == 200) {
            val order = responseCreateOrder.call.body<Order>()

            val deliveryDTO =
                DeliveryDTO(orderId = order.id, timeDelivery = orderDTOSaga.timeDelivery, status = "SCHEDULED")
            val paymentDTO =
                PaymentDTO(orderId = order.id, cost = order.contents.sumOf { it.amount * it.price }, status = "SUCCESS")
            val itemsWarehouse = order.contents.map { ItemDTO(orderId = it.id, item = it.item, status = "RESERVED") }

            val allResponse = runBlocking {
                val responseCreateDelivery = async { postRequest("delivery", deliveryDTO) }
                val responseCreatePayment = async { postRequest("payment", paymentDTO) }
                val responseCreateWarehouse = async { postRequest("warehouse", itemsWarehouse) }
                mapOf(
                    "delivery" to responseCreateDelivery.await(),
                    "payment" to responseCreatePayment.await(),
                    "warehouse" to responseCreateWarehouse.await()
                )
            }

            if (allResponse.values.none { it.status.value != 200 }) {
                //success
                val newOrderDTO = orderToOrderDTO(order = order, newStatus = "SUCCESS")
                val updOrder = putRequest("order", body = newOrderDTO, pathAppend = order.id)

                val newOrder = getRequest("order", order.id).body<Order>()
                val payment = allResponse["payment"]?.body<Payment>()
                val delivery = allResponse["delivery"]?.body<Delivery>()
                val warehouse = allResponse["warehouse"]?.body<List<Item>>()

                val list = mutableListOf(Json.encodeToJsonElement(Order.serializer(), newOrder))
                if (payment != null) list += Json.encodeToJsonElement(Payment.serializer(), payment)
                if (delivery != null) list += Json.encodeToJsonElement(Delivery.serializer(), delivery)
                val lstWarehouseItem =
                    warehouse?.map { item: Item -> Json.encodeToJsonElement(Item.serializer(), item) } ?: listOf()

                list += Json.encodeToJsonElement(lstWarehouseItem)

                Pair(updOrder.status, Json.encodeToJsonElement(list))
            } else {
                //rollback
                val responseCreatePayment = allResponse["payment"]

                if (responseCreatePayment != null && responseCreatePayment.status.value == 200) {
                    val payment = responseCreatePayment.body<Payment>()
                    putRequest(
                        "payment",
                        PaymentDTO(payment.orderId, payment.cost, status = "CANCELED"),
                        pathAppend = payment.id
                    )
                }

                val responseCreateDelivery = allResponse["delivery"]
                if (responseCreateDelivery != null && responseCreateDelivery.status.value == 200) {
                    val delivery = responseCreateDelivery.body<Delivery>()
                    putRequest(
                        "delivery",
                        DeliveryDTO(delivery.orderId, delivery.timeDelivery, status = "CANCELED"),
                        pathAppend = delivery.id
                    )
                }

                val responseCreateWarehouse = allResponse["warehouse"]
                if (responseCreateWarehouse != null && responseCreateWarehouse.status.value == 200) {
                    val warehouse = responseCreateWarehouse.body<List<Item>>()
                    warehouse.forEach {
                        putRequest(
                            "warehouse",
                            ItemDTO(it.orderId, it.item, status = "CANCELED"),
                            pathAppend = it.id
                        )
                    }
                }

                val rollbackOrderDTO = orderToOrderDTO(order = order, newStatus = "CANCELED")
                putRequest("order", body = rollbackOrderDTO, pathAppend = order.id)

                Pair(
                    HttpStatusCode.FailedDependency,
                    Json.encodeToJsonElement(mapOf("status" to "Failed to create order. Rollback all successful steps"))
                )
            }
        } else {
            Pair(responseCreateOrder.status, Json.encodeToJsonElement(mapOf("status" to "Failed to init order")))
        }
        return resultSaga
    }
}