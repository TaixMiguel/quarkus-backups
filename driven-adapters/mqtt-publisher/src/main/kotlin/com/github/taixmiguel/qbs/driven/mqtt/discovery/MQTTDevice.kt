package com.github.taixmiguel.qbs.driven.mqtt.discovery

import kotlinx.serialization.json.add
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray

class MQTTDevice private constructor(
    val identifiers: MutableList<String> = mutableListOf(),
    val manufacturer: String,
    val model: String,
    val name: String,
    val version: String) {

    companion object {
        fun create(manufacturer: String = "TaixMiguel", model: String = "TaixBackupsService",
                   identifier: String? = null, name: String, version: String): MQTTDevice {
            val device = MQTTDevice(manufacturer = manufacturer, model = model, name = name, version = version)
            identifier?.let { device.addIdentifier(it) }
            return device
        }
    }

    fun addIdentifier(identifier: String) {
        identifiers.add(identifier)
    }

    fun formatJSON(): String {
        val json = buildJsonObject {
            putJsonArray("identifiers") { identifiers.forEach { identifier -> add(identifier) } }
            put("manufacturer", manufacturer)
            put("sw_version", version)
            put("model", model)
            put("name", name)
        }
        return json.toString()
    }
}
