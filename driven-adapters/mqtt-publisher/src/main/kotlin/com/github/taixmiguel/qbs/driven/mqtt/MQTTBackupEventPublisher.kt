package com.github.taixmiguel.qbs.driven.mqtt

import com.github.taixmiguel.qbs.application.port.event.BackupCreatedEvent
import com.github.taixmiguel.qbs.application.port.event.BackupEventPublisher
import com.github.taixmiguel.qbs.application.port.event.BackupExecutedEvent
import jakarta.enterprise.context.ApplicationScoped
import jakarta.enterprise.event.Event
import jakarta.inject.Inject

@ApplicationScoped
class MQTTBackupEventPublisher @Inject constructor(
    private val backupCreatedEvent: Event<BackupCreatedEvent>,
    private val backupExecutedEvent: Event<BackupExecutedEvent>
): BackupEventPublisher {

    override fun publishBackupCreated(event: BackupCreatedEvent) =
        backupCreatedEvent.fire(event)

    override fun publishBackupExecuted(event: BackupExecutedEvent) =
        backupExecutedEvent.fire(event)
}