package com.github.taixmiguel.qbs.application.usecase

import com.github.taixmiguel.qbs.application.port.BackupRepository
import com.github.taixmiguel.qbs.application.port.StorageServiceRegistry
import com.github.taixmiguel.qbs.application.usecase.commands.BackupCommand
import com.github.taixmiguel.qbs.domain.BackupId

class UpdateBackup(
    private val repository: BackupRepository,
    private val ssRegistry: StorageServiceRegistry
) {
    fun execute(backupId: BackupId, command: BackupCommand) {
        if (!ssRegistry.isSupported(command.storageService))
            throw IllegalArgumentException("Storage service '${command.storageService}' not supported. Supported " +
                    "services are ${ssRegistry.supportedServices().joinToString(", ")}")

        if (command.nBackupsMax <= 0) throw IllegalArgumentException("nBackupsMax must be greater than 0")

        val backup = repository.findById(backupId)
            ?: throw NoSuchElementException("Backup with id '$backupId' not found")

        val updatedBackup = backup.copy(
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
        repository.save(updatedBackup)
    }
}
