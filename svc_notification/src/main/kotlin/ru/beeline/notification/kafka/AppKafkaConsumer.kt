package ru.beeline.notification.kafka

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.java.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.errors.WakeupException
import ru.beeline.notification.config.HostServicesConfig
import ru.beeline.notification.dao.dao
import ru.beeline.notification.feature.ClosableJob
import ru.beeline.notification.models.KafkaRecordBH
import ru.beeline.notification.models.Notification
import ru.beeline.user.models.User
import java.time.Duration
import java.time.temporal.ChronoUnit
import java.util.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class Consumer<K, V>(private val consumer: KafkaConsumer<K, V>, topic: String, hsc: HostServicesConfig) : ClosableJob {
    private val notClosed = atomic(true)
    private var finished = CountDownLatch(1)
    private val hscLocal = hsc

    init {
        consumer.subscribe(listOf(topic))

    }

    override fun run() = runBlocking {
        try {
            val mapper = jacksonObjectMapper()
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

            val client = HttpClient(Java) {
                install(ContentNegotiation) {
                    json()
                }
            }
            val userStr = "user"

            while (notClosed.value) {

                val records = withContext(Dispatchers.IO) { consumer.poll(Duration.of(1000, ChronoUnit.MILLIS)) }

                for (record in records) {

                    val rec = mapper.readValue<KafkaRecordBH>(record.value().toString())

                    val billingId = rec.payload.after.billing_id

                    val responseGetUserByBillingId = client.get {
                        url {
                            host = hscLocal.cfgs[userStr]?.host.toString()
                            port = hscLocal.cfgs[userStr]?.port.toString().toInt()
                            path(hscLocal.cfgs[userStr]?.path.toString())
                            appendPathSegments("billing_id", billingId)
                        }
                    }

                    val user = responseGetUserByBillingId.body<User>()

                    val notification = Notification(
                        id = UUID.randomUUID().toString(),
                        userId = user.id,
                        message = "${rec.payload.after.status}: sum ${rec.payload.after.sum}, lastValue ${rec.payload.after.last_value_wallet} currentValue ${rec.payload.after.current_value_wallet}",
                        email = user.email
                    )

                    dao.newNotification(notification)
                }

                if (!records.isEmpty) {
                    consumer.commitAsync()
                }
            }
        } catch (ex: WakeupException) {
            // ignore for shutdown
        } catch (ex: RuntimeException) {
            // exception handling
            withContext(NonCancellable) {
                throw ex
            }
        } finally {
            consumer.commitSync()
            consumer.close()
            finished.countDown()
        }
    }

    override fun close() {
        notClosed.value = false
        consumer.wakeup()
        finished.await(3000, TimeUnit.MILLISECONDS)
    }
}