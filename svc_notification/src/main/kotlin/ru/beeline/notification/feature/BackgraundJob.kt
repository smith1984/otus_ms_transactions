package ru.beeline.notification.feature

import io.ktor.server.application.*
import io.ktor.util.AttributeKey
import java.io.Closeable
import kotlin.concurrent.thread

class BackgroundJob(configuration: JobConfiguration) : Closeable {
    private val job = configuration.job
    private val name = configuration.name

    class JobConfiguration {
        var name: String? = null
        var job: ClosableJob? = null
    }

    object BackgroundJobFeature : BaseApplicationPlugin<Application, JobConfiguration, BackgroundJob> {
        override val key: AttributeKey<BackgroundJob> = AttributeKey("BackgroundJob")

        override fun install(pipeline: Application, configure: JobConfiguration.() -> Unit): BackgroundJob {
            val configuration = JobConfiguration().apply(configure)
            val backgroundJob = BackgroundJob(configuration)
            configuration.job?.let { thread(name = configuration.name) { it.run() } }
            return backgroundJob
        }
    }

    override fun close() {
        job?.close()
    }
}