rootProject.name = "otus_ms_transactions"

pluginManagement {
    val kotlin_version: String by settings
    val ktor_version: String by settings

    plugins {
        kotlin("jvm") version kotlin_version apply false
        id("io.ktor.plugin") version ktor_version apply false
        id("org.jetbrains.kotlin.plugin.serialization") version kotlin_version apply false
    }
}

include("svc_delivery")
include("svc_order")
include("svc_payment")
include("svc_warehouse")
include("svc_user")
include("svc_billing")
include("svc_notification")
