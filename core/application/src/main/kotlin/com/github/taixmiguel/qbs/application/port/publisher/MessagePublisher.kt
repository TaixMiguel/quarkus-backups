package com.github.taixmiguel.qbs.application.port.publisher

interface MessagePublisher {
    fun publish(topic: String, payload: String, retain: Boolean = false)
}