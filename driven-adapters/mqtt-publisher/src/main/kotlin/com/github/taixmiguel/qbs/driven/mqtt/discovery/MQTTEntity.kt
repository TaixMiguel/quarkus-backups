package com.github.taixmiguel.qbs.driven.mqtt.discovery

import kotlinx.serialization.json.*

class MQTTEntity private constructor(
    val unitOfMeasurement: String?,
    val commandTopic: String?,
    val stateTopic: String?,
    val device: MQTTDevice,
    val component: String,
    val objectId: String?,
    val uniqueId: String?,
    val retain: Boolean,
    val icon: String?,
    val name: String?,
) {
    companion object {
        fun create(device: MQTTDevice, unitOfMeasurement: String? = null, component: String = "sensor",
                   commandTopic: String? = null, stateTopic: String? = null, objectId: String? = null,
                   uniqueId: String? = null, retain: Boolean = false, icon: String? = null,
                   name: String? = null): MQTTEntity {
            return MQTTEntity(device = device, unitOfMeasurement = unitOfMeasurement, component = component,
                commandTopic = commandTopic, stateTopic = stateTopic, objectId = objectId, uniqueId = uniqueId,
                retain = retain, icon = icon, name = name)
        }
    }

    fun getConfigTopic(discoveryPrefix: String = "homeassistant"): String {
        var topic = "$discoveryPrefix/$component/"
        if (device.identifiers.isNotEmpty()) {
            val firstIdentifier = device.identifiers.first()
            topic += "$firstIdentifier/$objectId/config"
        }
        return topic
    }

    fun formatJSON(): String {
        val json = buildJsonObject {
            unitOfMeasurement?.let { put("unit_of_measurement", unitOfMeasurement) }
            commandTopic?.let { put("command_topic", commandTopic) }
            stateTopic?.let { put("state_topic", stateTopic) }
            unitOfMeasurement?.let { put("unit_of_measurement", unitOfMeasurement) }
            put("device", device.formatJSON())
            objectId?.let { put("object_id", objectId) }
            uniqueId?.let { put("unique_id", uniqueId) }
            put("retain", retain)
            icon?.let { put("icon", icon) }
            name?.let { put("name", name) }
        }
        return json.toString()
    }
}
