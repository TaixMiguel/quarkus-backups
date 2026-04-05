package com.github.taixmiguel.qbs.driven.mqtt.discovery

import com.github.taixmiguel.qbs.application.port.persistence.BackupRepository
import com.github.taixmiguel.qbs.application.port.publisher.MessagePublisher
import io.quarkus.logging.Log
import io.quarkus.scheduler.Scheduled
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import org.eclipse.microprofile.config.inject.ConfigProperty

@ApplicationScoped
class HomeAssistantDiscoveryService @Inject constructor(
    private val publisher: MessagePublisher,
    private val repository: BackupRepository
) {
    @Inject
    @field:ConfigProperty(name = "app.name")
    private lateinit var appName: String

    @Inject
    @field:ConfigProperty(name = "app.version")
    private lateinit var appVersion: String

    @Scheduled(every = "12h", delayed = "10s")
    fun onSchedule() {
        Log.info("Running Home Assistant discovery schedule")
        try {
            val device = createDevice()
            createBackupsEntities(device)
            Log.info("Home Assistant discovery completed successfully")
        } catch (e: Exception) {
            Log.errorf(e, "Discovery schedule failed: %s", e.message)
        }
    }

    private fun createDevice(): MQTTDevice {
        Log.debugf("Creating MQTT [device='%s' version='%s']", appName, appVersion)
        val device = MQTTDevice.create(identifier = "taixBackupService", name = appName, version = appVersion)

        Log.debug("Creating global sensor: last execution")
        var stateTopic = formatTopic(topicPrefix="stat", topicSubfix="lastExecution")
        var entity = MQTTEntity.create(device, name = "Última ejecución", objectId = "taixBackupsService_global_lastExecution",
            uniqueId = "taixBackupsService_global_lastExecution", retain = true, stateTopic = stateTopic)
        publisher.publish(topic = entity.getConfigTopic(), payload = entity.formatJSON(), retain = true)

        Log.debug("Creating global sensor: last backup")
        stateTopic = formatTopic(topicPrefix="stat", topicSubfix="lastBackup")
        entity = MQTTEntity.create(device, name = "Último backup", objectId = "taixBackupsService_global_lastBackup",
            uniqueId = "taixBackupsService_global_lastBackup", retain = true, stateTopic = stateTopic)
        publisher.publish(topic = entity.getConfigTopic(), payload = entity.formatJSON(), retain = true)
        return device
    }

    private fun createBackupsEntities(device: MQTTDevice) {
        repository.findAll()
            .stream()
            .filter { it.swSensorMQTT }
            .forEach {
                Log.debugf("Creating last execution sensor for backup: %s", it.id.value)
                var stateTopic = formatTopic(topicPrefix="stat", topicSubfix="lastExecution", backupId = it.id.value)
                var entity = MQTTEntity.create(device, name = "Ejecución [${it.name.value}]",
                    objectId = "taixBackupsService_${it.id.value}_lastExecution",
                    uniqueId = "taixBackupsService_${it.id.value}_lastExecution",
                    retain = true, stateTopic = stateTopic)
                publisher.publish(topic = entity.getConfigTopic(), payload = entity.formatJSON(), retain = true)

                Log.debugf("Creating state sensor for backup: %s", it.id.value)
                stateTopic = formatTopic(topicPrefix="stat", topicSubfix="stateBackup", backupId = it.id.value)
                entity = MQTTEntity.create(device, name = "Estado [${it.name.value}]",
                    objectId = "taixBackupsService_${it.id.value}_stateBackup",
                    uniqueId = "taixBackupsService_${it.id.value}_stateBackup",
                    retain = true, stateTopic = stateTopic)
                publisher.publish(topic = entity.getConfigTopic(), payload = entity.formatJSON(), retain = true)
            }
    }

    private fun formatTopic(topicPrefix: String, topicSubfix: String, backupId: String = "global"): String {
        return "$topicPrefix/taixBackupsService/$backupId/$topicSubfix"
    }
}