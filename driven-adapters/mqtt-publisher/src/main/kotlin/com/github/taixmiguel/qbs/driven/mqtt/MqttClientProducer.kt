package com.github.taixmiguel.qbs.driven.mqtt

import io.quarkus.logging.Log
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
    private lateinit var mqttUsername: String

    @Inject
    @field:ConfigProperty(name = "mqtt.password")
    private lateinit var mqttPassword: String

    @Inject
    @field:ConfigProperty(name = "mqtt.clientId")
    private lateinit var mqttClientId: String

    private lateinit var client: MqttClient

    fun onStart(@Observes ev: StartupEvent) {
        Log.infof("MQTT connecting to host='%s' port='%d' clientId='%s' username='%s'",
            host, port, mqttClientId, mqttUsername)
        val options = MqttClientOptions()
            .setClientId(mqttClientId)
            .setUsername(mqttUsername)
            .setPassword(mqttPassword)
        client = MqttClient.create(vertx, options)
        client.connectAndAwait(port, host)
    }

    @jakarta.enterprise.inject.Produces
    @ApplicationScoped
    fun mqttClient(): MqttClient = client
}