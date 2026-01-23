package com.github.taixmiguel.qbs.driven.mqtt

import com.github.taixmiguel.qbs.application.port.publisher.MessagePublisher
import org.eclipse.microprofile.reactive.messaging.Channel
import org.eclipse.microprofile.reactive.messaging.Emitter
import io.smallrye.reactive.messaging.mqtt.MqttMessage
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject

@ApplicationScoped
class MqttMessagePublisher @Inject constructor(
    @Channel("backup-status") private val emitter: Emitter<String>
): MessagePublisher {
    override fun publish(topic: String, payload: String, retain: Boolean) {
        val message: MqttMessage<String> = MqttMessage.of(topic, payload)
        emitter.send(message)
    }
}