package com.github.taixmiguel.qbs.driven.mqtt

import com.github.taixmiguel.qbs.application.port.publisher.MessagePublisher
import io.netty.handler.codec.mqtt.MqttQoS
import io.quarkus.logging.Log
import io.vertx.mutiny.mqtt.MqttClient
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject

@ApplicationScoped
class MqttMessagePublisher @Inject constructor(
    private val mqttClient: MqttClient
): MessagePublisher {

    override fun publish(topic: String, payload: String, retain: Boolean) {
        if (!mqttClient.isConnected) {
            Log.warnf("MQTT not connected, skipping publish to %s", topic)
            return
        }

        Log.infof("Publishing message to topic: %s", topic)
        mqttClient.publishAndAwait(
            topic,
            io.vertx.mutiny.core.buffer.Buffer.buffer(payload),
            MqttQoS.AT_MOST_ONCE,
            false,
            retain
        )
    }
}