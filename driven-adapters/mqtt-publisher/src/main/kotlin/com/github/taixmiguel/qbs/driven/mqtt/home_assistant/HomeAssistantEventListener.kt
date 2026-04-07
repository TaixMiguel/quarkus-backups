package com.github.taixmiguel.qbs.driven.mqtt.home_assistant

import com.github.taixmiguel.qbs.application.port.event.BackupCreatedEvent
import com.github.taixmiguel.qbs.application.port.event.BackupExecutedEvent
import com.github.taixmiguel.qbs.application.port.publisher.MessagePublisher
import io.quarkus.logging.Log
import jakarta.enterprise.context.ApplicationScoped
import jakarta.enterprise.event.Observes
import jakarta.inject.Inject
import java.time.ZoneOffset

@ApplicationScoped
class HomeAssistantEventListener @Inject constructor(
    private val msgPublisher: MessagePublisher,
    private val discoveryService: HomeAssistantDiscoveryService
) {
    fun onBackupCreated(@Observes event: BackupCreatedEvent) {
        try {
            if (event.swSensorMQTT) discoveryService.registerBackup(backupId = event.backupId, backupName = event.backupName)
        } catch (e: Exception) {
            Log.error("Failed to register backup in HA discovery: ${e.message}", e)
        }
    }

    fun onBackupExecuted(@Observes event: BackupExecutedEvent) {
        val createdAt = event.executedAt.toEpochSecond(ZoneOffset.UTC)

        try {
            msgPublisher.publish("stat/taixBackupsService/lastBackup", event.backupName.value, true)
            msgPublisher.publish("stat/taixBackupsService/lastExecution", "$createdAt", true)

            if (event.swSensorMQTT) {
                val id = event.backupId.value
                msgPublisher.publish("stat/taixBackupsService/$id/stateBackup", event.state.name, true)
                msgPublisher.publish("stat/taixBackupsService/$id/lastExecution", "$createdAt", true)
            }
        } catch (e: Exception) {
            Log.error(e.message, e)
        }
    }
}