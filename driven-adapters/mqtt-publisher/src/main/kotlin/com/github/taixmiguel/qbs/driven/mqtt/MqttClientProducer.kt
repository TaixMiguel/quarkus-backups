package com.github.taixmiguel.qbs.driven.mqtt

import io.quarkus.runtime.StartupEvent
import io.vertx.mutiny.core.Vertx
import io.vertx.mutiny.mqtt.MqttClient
import io.vertx.mqtt.MqttClientOptions
import jakarta.enterprise.context.ApplicationScoped
import jakarta.enterprise.event.Observes
import jakarta.inject.Inject
import org.eclipse.microprofile.config.inject.ConfigProperty

@ApplicationScoped
class MqttClientProducer @Inject constructor(
    private val vertx: Vertx
) {
    @Inject
    @field:ConfigProperty(name = "mqtt.host")
    private lateinit var host: String

    @Inject
    @field:ConfigProperty(name = "mqtt.port")
    private var port: Int = 1883

    @Inject
    @field:ConfigProperty(name = "mqtt.username")
    private lateinit var username: String

    @Inject
    @field:ConfigProperty(name = "mqtt.password")
    private lateinit var password: String

    private lateinit var client: MqttClient

    fun onStart(@Observes ev: StartupEvent) {
        val options = MqttClientOptions().apply {
            this.username = username
            this.password = password
        }
        client = MqttClient.create(vertx, options)
        client.connectAndAwait(port, host)
    }

    @jakarta.enterprise.inject.Produces
    @ApplicationScoped
    fun mqttClient(): MqttClient = client
}