plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ktor)
    alias(libs.plugins.kotlin.plugin.serialization)
}
group = "com.omaroid"
version = "0.0.1"

application {
    mainClass.set("com.omaroid.ApplicationKt")
}

dependencies {
    // Ktor
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.postgresql)
    implementation(libs.h2)
    implementation(libs.ktor.server.cio)
    implementation(libs.logback.classic)
    implementation(libs.ktor.server.config.yaml)
    testImplementation(libs.ktor.server.test.host)
    testImplementation(libs.kotlin.test.junit)
    implementation(libs.ktor.server.status.pages)

    // Koin
    implementation(libs.koin.core)
    implementation(libs.koin.ktor)
    implementation(libs.koin.logger.slf4j)

    // Exposed & Database
    implementation(libs.exposed.core)
    implementation(libs.exposed.dao)
    implementation(libs.exposed.jdbc)
    implementation(libs.exposed.kotlin.datetime)
    implementation(libs.postgresql)
    implementation(libs.hikari.cp)

    // Configuration
    implementation(libs.typesafe.config)
    implementation(libs.dotenv.kotlin)

    // DateTime
    implementation(libs.kotlinx.datetime)

    // Testing
    testImplementation(libs.ktor.server.test.host)
    testImplementation(libs.kotlin.test.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.koin.test)
    testImplementation(libs.koin.test.junit4)
    testImplementation(libs.h2.database)
    testImplementation(libs.ktor.client.content.negotiation)
}
ktor {
    fatJar {
        archiveFileName.set("${project.name}-${project.version}-all.jar")
    }
}