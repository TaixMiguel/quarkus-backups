package com.github.taixmiguel.qbs.driven.mqtt.discovery

import com.github.taixmiguel.qbs.application.port.persistence.BackupRepository
import com.github.taixmiguel.qbs.application.port.publisher.MessagePublisher
import io.quarkus.runtime.StartupEvent
import io.quarkus.scheduler.Scheduled
import jakarta.enterprise.context.ApplicationScoped
import jakarta.enterprise.event.Observes
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

    fun onStart(@Observes ev: StartupEvent) {
        onSchedule()
    }

    @Scheduled(every = "12h")
    fun onSchedule() {
        val device = createDevice()
        createBackupsEntities(device)
    }

    private fun createDevice(): MQTTDevice {
        // log: Creación del dispositivo MQTT
        val device = MQTTDevice.create(identifier = "taixBackupService", name = appName, version = appVersion)

        // log: Creación del sensor global última ejecución
        var stateTopic = formatTopic(topicPrefix="stat", topicSubfix="lastExecution")
        var entity = MQTTEntity.create(device, name = "Última ejecución", objectId = "taixBackupsService_global_lastExecution",
            retain = true, stateTopic = stateTopic)
        publisher.publish(topic = entity.getConfigTopic(), payload = entity.formatJSON(), retain = true)

        // log: Creación del sensor global último backup
        stateTopic = formatTopic(topicPrefix="stat", topicSubfix="lastBackup")
        entity = MQTTEntity.create(device, name = "Último backup", objectId = "taixBackupsService_global_lastBackup",
            retain = true, stateTopic = stateTopic)
        publisher.publish(topic = entity.getConfigTopic(), payload = entity.formatJSON(), retain = true)
        return device
    }

    private fun createBackupsEntities(device: MQTTDevice) {
        repository.findAll()
            .stream()
            .filter { it.swSensorMQTT }
            .forEach {
                // log: Creación del sensor de última ejecución para el backup {backup}
                var stateTopic = formatTopic(topicPrefix="stat", topicSubfix="lastExecution")
                var entity = MQTTEntity.create(device, name = "Ejecución [${it.name.value}]",
                    objectId = "taixBackupsService_${it.name.value}_lastExecution",
                    retain = true, stateTopic = stateTopic)
                publisher.publish(topic = entity.getConfigTopic(), payload = entity.formatJSON(), retain = true)

                // log: Creación del sensor de estado para el backup {backup}
                stateTopic = formatTopic(topicPrefix="stat", topicSubfix="stateBackup")
                entity = MQTTEntity.create(device, name = "Estado [${it.name.value}]",
                    objectId = "taixBackupsService_${it.name.value}_stateBackup",
                    retain = true, stateTopic = stateTopic)
                publisher.publish(topic = entity.getConfigTopic(), payload = entity.formatJSON(), retain = true)
            }

    }

    private fun formatTopic(topicPrefix: String, topicSubfix: String, backupId: String = "global"): String {
        return "$topicPrefix/taixBackupsService/$backupId/$topicSubfix"
    }
}