package com.github.taixmiguel.qbs.application.port.event

interface BackupEventPublisher {
    fun publishBackupCreated(event: BackupCreatedEvent)
    fun publishBackupExecuted(event: BackupExecutedEvent)
}