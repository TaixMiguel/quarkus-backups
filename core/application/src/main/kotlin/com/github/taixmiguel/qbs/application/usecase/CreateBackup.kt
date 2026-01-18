package com.github.taixmiguel.qbs.application.usecase

import com.github.taixmiguel.qbs.application.port.BackupIdGenerator
import com.github.taixmiguel.qbs.application.port.persistence.BackupRepository
import com.github.taixmiguel.qbs.domain.Backup
import com.github.taixmiguel.qbs.domain.BackupId
import com.github.taixmiguel.qbs.application.port.storage.StorageServiceRegistry
import com.github.taixmiguel.qbs.application.usecase.commands.BackupCommand

class CreateBackup(
    private val idGenerator: BackupIdGenerator,
    private val repository: BackupRepository,
    private val ssRegistry: StorageServiceRegistry
) {
    fun execute(command: BackupCommand): BackupId {
        if (!ssRegistry.isSupported(command.storageService))
            throw IllegalArgumentException("Storage service '${command.storageService}' not supported. Supported " +
                    "services are ${ssRegistry.supportedServices().joinToString(", ")}")

        val backup = Backup(
            id = idGenerator.generate(),
            name = command.name,
            description = command.description,
            storageService = command.storageService,
            sourceDir = command.sourceDir,
            destinationDir = command.destinationDir,
            username = command.username,
            password = command.password,
            nBackupsMax = command.nBackupsMax,
            swSensorMQTT = command.swSensorMQTT
        )

        repository.save(backup)
        return backup.id
    }
}
